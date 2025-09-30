package data

import (
	"context"
	"fmt"

	"github.com/google/uuid"
)

// TransferParams holds all the necessary information to perform a transfer.
type TransferParams struct {
	FromAccountID  uuid.UUID
	ToAccountID    uuid.UUID
	Amount         int64
	IdempotencyKey string
}

// --- ADD THIS VALIDATION FUNCTION ---
func ValidateTransferParams(v *Validator, params *TransferParams) {
	v.Check(params.FromAccountID != uuid.Nil, "from_account_id", "must be a valid UUID")
	v.Check(params.ToAccountID != uuid.Nil, "to_account_id", "must be a valid UUID")
	v.Check(params.FromAccountID != params.ToAccountID, "accounts", "from and to accounts cannot be the same")
	v.Check(params.Amount > 0, "amount", "must be a positive integer")
	// We could add more validation for the idempotency key here if needed.
}

// CreateTransferTx performs the wallet-to-wallet transfer within a single database transaction.
func (m Models) CreateTransferTx(params TransferParams) (*Transaction, error) {
	// Begin a new database transaction.
	tx, err := m.Accounts.DB.BeginTx(context.Background(), nil)
	if err != nil {
		return nil, err
	}
	// Defer a rollback. If the function returns without a successful commit,
	// the transaction will be automatically rolled back.
	defer tx.Rollback()

	// 1. Create the main transaction record.
	transaction := &Transaction{
		IdempotencyKey:  params.IdempotencyKey,
		TransactionType: "P2P_TRANSFER",
		Status:          "COMPLETED",
	}
	err = m.Transactions.Insert(tx, transaction)
	if err != nil {
		return nil, fmt.Errorf("failed to insert transaction: %w", err)
	}

	// 2. Create the debit entry (money leaving the sender's account).
	debitEntry := &Entry{
		TransactionID: transaction.ID,
		AccountID:     params.FromAccountID,
		Amount:        params.Amount,
		Direction:     "DEBIT",
	}
	err = m.Entries.Insert(tx, debitEntry)
	if err != nil {
		return nil, fmt.Errorf("failed to insert debit entry: %w", err)
	}

	// 3. Create the credit entry (money entering the receiver's account).
	creditEntry := &Entry{
		TransactionID: transaction.ID,
		AccountID:     params.ToAccountID,
		Amount:        params.Amount,
		Direction:     "CREDIT",
	}
	err = m.Entries.Insert(tx, creditEntry)
	if err != nil {
		return nil, fmt.Errorf("failed to insert credit entry: %w", err)
	}

	// If all steps were successful, commit the transaction.
	if err = tx.Commit(); err != nil {
		return nil, err
	}

	return transaction, nil
}


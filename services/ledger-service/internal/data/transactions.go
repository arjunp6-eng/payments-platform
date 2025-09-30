package data

import (
	"context"
	"database/sql"
	"time"
	"errors"

	"github.com/google/uuid"
)

// Transaction struct maps directly to the transactions table.
type Transaction struct {
	ID              uuid.UUID `json:"id"`
	IdempotencyKey  string    `json:"idempotency_key,omitempty"`
	TransactionType string    `json:"transaction_type"`
	Status          string    `json:"status"`
	CreatedAt       time.Time `json:"created_at"`
}

// TransactionModel wraps the database connection pool.
type TransactionModel struct {
	DB *sql.DB
}

// Insert adds a new record to the transactions table.
// It accepts a transaction object (tx) so it can be part of a larger DB transaction.
func (m TransactionModel) Insert(tx *sql.Tx, transaction *Transaction) error {
	query := `
        INSERT INTO transactions (id, idempotency_key, transaction_type, status)
        VALUES ($1, $2, $3, $4)
        RETURNING created_at`

	transaction.ID = uuid.New()
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	return tx.QueryRowContext(ctx, query,
		transaction.ID,
		transaction.IdempotencyKey,
		transaction.TransactionType,
		transaction.Status,
	).Scan(&transaction.CreatedAt)
}

// Get retrieves a single transaction from the database by its ID.
func (m TransactionModel) Get(id uuid.UUID) (*Transaction, error) {
	query := `
        SELECT id, idempotency_key, transaction_type, status, created_at
        FROM transactions
        WHERE id = $1`

	var transaction Transaction
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	err := m.DB.QueryRowContext(ctx, query, id).Scan(
		&transaction.ID,
		&transaction.IdempotencyKey,
		&transaction.TransactionType,
		&transaction.Status,
		&transaction.CreatedAt,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.New("record not found")
		}
		return nil, err
	}

	return &transaction, nil
}

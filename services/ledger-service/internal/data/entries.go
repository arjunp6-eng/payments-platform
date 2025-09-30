package data

import (
	"context"
	"database/sql"
	"time"

	"github.com/google/uuid"
)

// Entry struct maps directly to the entries table.
type Entry struct {
	ID            uuid.UUID `json:"id"`
	TransactionID uuid.UUID `json:"transaction_id"`
	AccountID     uuid.UUID `json:"account_id"`
	Amount        int64     `json:"amount"` // Stored as the smallest currency unit (e.g., cents)
	Direction     string    `json:"direction"` // 'DEBIT' or 'CREDIT'
	CreatedAt     time.Time `json:"created_at"`
}

// EntryModel wraps the database connection pool.
type EntryModel struct {
	DB *sql.DB
}

// Insert adds a new record to the entries table.
// It accepts a transaction object (tx) so it can be part of a larger DB transaction.
func (m EntryModel) Insert(tx *sql.Tx, entry *Entry) error {
	query := `
        INSERT INTO entries (id, transaction_id, account_id, amount, direction)
        VALUES ($1, $2, $3, $4, $5)
        RETURNING created_at`

	entry.ID = uuid.New()
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	return tx.QueryRowContext(ctx, query,
		entry.ID,
		entry.TransactionID,
		entry.AccountID,
		entry.Amount,
		entry.Direction,
	).Scan(&entry.CreatedAt)
}

// GetAllForTransaction retrieves all entries for a given transaction ID.
func (m EntryModel) GetAllForTransaction(transactionID uuid.UUID) ([]*Entry, error) {
	query := `
        SELECT id, transaction_id, account_id, amount, direction, created_at
        FROM entries
        WHERE transaction_id = $1
        ORDER BY created_at`

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	rows, err := m.DB.QueryContext(ctx, query, transactionID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var entries []*Entry

	for rows.Next() {
		var entry Entry
		err := rows.Scan(
			&entry.ID,
			&entry.TransactionID,
			&entry.AccountID,
			&entry.Amount,
			&entry.Direction,
			&entry.CreatedAt,
		)
		if err != nil {
			return nil, err
		}
		entries = append(entries, &entry)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return entries, nil
}
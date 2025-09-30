package main

import (
	"errors"
	"net/http"

	"github.com/dinesh-suryanand/payments-platform/ledger-service/internal/data"
	"github.com/google/uuid"
)

// healthcheckHandler writes a plain-text response with information about the
// application status and environment.
func (app *application) healthcheckHandler(w http.ResponseWriter, r *http.Request) {
	data := envelope{
		"status": "available",
		"system_info": map[string]string{
			"environment": "development",
			"version":     "1.0.0",
		},
	}

	err := app.writeJSON(w, http.StatusOK, data, nil)
	if err != nil {
		app.logger.Println(err)
		http.Error(w, "The server encountered a problem and could not process your request", http.StatusInternalServerError)
	}
}

// createAccountHandler handles the creation of new accounts.
func (app *application) createAccountHandler(w http.ResponseWriter, r *http.Request) {
	var input struct {
		UserID      uuid.UUID `json:"user_id"`
		AccountType string    `json:"account_type"`
	}

	err := app.readJSON(w, r, &input)
	if err != nil {
		app.badRequestResponse(w, r, err)
		return
	}

	account := &data.Account{
		UserID:      input.UserID,
		AccountType: input.AccountType,
		Status:      "ACTIVE",
	}

	v := data.NewValidator()

	if data.ValidateAccount(v, account); !v.Valid() {
		app.failedValidationResponse(w, r, v.Errors)
		return
	}

	err = app.models.Accounts.Insert(account)
	if err != nil {
		app.logger.Printf("could not insert account: %v", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	headers := make(http.Header)
	headers.Set("Location", "/v1/accounts/"+account.ID.String())
	app.writeJSON(w, http.StatusCreated, envelope{"account": account}, headers)
}

// getAccountHandler handles fetching a single account by its ID.
func (app *application) getAccountHandler(w http.ResponseWriter, r *http.Request) {
	id, err := uuid.Parse(r.PathValue("id"))
	if err != nil {
		app.badRequestResponse(w, r, errors.New("invalid account ID"))
		return
	}

	account, err := app.models.Accounts.Get(id)
	if err != nil {
		if errors.Is(err, errors.New("record not found")) {
			http.Error(w, "Not Found", http.StatusNotFound)
		} else {
			app.logger.Printf("could not get account: %v", err)
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		}
		return
	}

	app.writeJSON(w, http.StatusOK, envelope{"account": account}, nil)
}

// createTransferHandler handles a new P2P transfer.
func (app *application) createTransferHandler(w http.ResponseWriter, r *http.Request) {
	var input struct {
		FromAccountID  uuid.UUID `json:"from_account_id"`
		ToAccountID    uuid.UUID `json:"to_account_id"`
		Amount         int64     `json:"amount"`
		IdempotencyKey string    `json:"idempotency_key"`
	}

	err := app.readJSON(w, r, &input)
	if err != nil {
		app.badRequestResponse(w, r, err)
		return
	}

	params := data.TransferParams{
		FromAccountID:  input.FromAccountID,
		ToAccountID:    input.ToAccountID,
		Amount:         input.Amount,
		IdempotencyKey: input.IdempotencyKey,
	}

	v := data.NewValidator()
	if data.ValidateTransferParams(v, &params); !v.Valid() {
		app.failedValidationResponse(w, r, v.Errors)
		return
	}

	transaction, err := app.models.CreateTransferTx(params)
	if err != nil {
		app.logger.Printf("could not create transfer: %v", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	app.writeJSON(w, http.StatusCreated, envelope{"transaction": transaction}, nil)
}

// getTransactionHandler handles fetching a single transaction and its entries.
func (app *application) getTransactionHandler(w http.ResponseWriter, r *http.Request) {
	id, err := uuid.Parse(r.PathValue("id"))
	if err != nil {
		app.badRequestResponse(w, r, errors.New("invalid transaction ID"))
		return
	}

	// Fetch the main transaction record.
	transaction, err := app.models.Transactions.Get(id)
	if err != nil {
		if errors.Is(err, errors.New("record not found")) {
			http.Error(w, "Not Found", http.StatusNotFound)
		} else {
			app.logger.Printf("could not get transaction: %v", err)
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		}
		return
	}

	// Fetch all the entries associated with this transaction.
	entries, err := app.models.Entries.GetAllForTransaction(id)
	if err != nil {
		app.logger.Printf("could not get entries for transaction: %v", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	// Combine the data into a single response.
	app.writeJSON(w, http.StatusOK, envelope{
		"transaction": transaction,
		"entries":     entries,
	}, nil)
}

// getAccountBalanceHandler calculates and returns the balance for a specific account.
func (app *application) getAccountBalanceHandler(w http.ResponseWriter, r *http.Request) {
	id, err := uuid.Parse(r.PathValue("id"))
	if err != nil {
		app.badRequestResponse(w, r, errors.New("invalid account ID"))
		return
	}

	// First, check if the account exists to provide a better error message.
	_, err = app.models.Accounts.Get(id)
	if err != nil {
		if errors.Is(err, errors.New("record not found")) {
			http.Error(w, "Not Found", http.StatusNotFound)
		} else {
			app.logger.Printf("could not get account for balance check: %v", err)
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		}
		return
	}

	// If the account exists, calculate its balance.
	balance, err := app.models.Accounts.GetBalance(id)
	if err != nil {
		app.logger.Printf("could not calculate balance: %v", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	app.writeJSON(w, http.StatusOK, envelope{"balance": balance}, nil)
}

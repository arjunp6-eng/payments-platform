package main

import "net/http"

// routes returns a configured http.ServeMux wrapped in our logging middleware.
func (app *application) routes() http.Handler { // The return type is now http.Handler
	mux := http.NewServeMux()

	// Register handlers for routes. The pattern includes the HTTP method.
	mux.HandleFunc("GET /healthcheck", app.healthcheckHandler)

	mux.HandleFunc("POST /v1/accounts", app.createAccountHandler)
	mux.HandleFunc("GET /v1/accounts/{id}", app.getAccountHandler)
	mux.HandleFunc("GET /v1/accounts/{id}/balance", app.getAccountBalanceHandler)

	mux.HandleFunc("POST /v1/transfers", app.createTransferHandler)
	mux.HandleFunc("GET /v1/transactions/{id}", app.getTransactionHandler)

	// Wrap the mux with the logRequest middleware.
	return app.logRequest(mux)
}
package main

import (
	"encoding/json"
	"errors"
	"io"
	"net/http"
)

// Define a simple envelope type for wrapping JSON responses for better structure.
type envelope map[string]interface{}

// writeJSON is a helper for sending JSON responses.
func (app *application) writeJSON(w http.ResponseWriter, status int, data envelope, headers http.Header) error {
	js, err := json.MarshalIndent(data, "", "\t")
	if err != nil {
		return err
	}
	js = append(js, '\n')

	for key, value := range headers {
		w.Header()[key] = value
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	w.Write(js)

	return nil
}

// readJSON is a helper for decoding JSON request bodies with robust error handling.
func (app *application) readJSON(w http.ResponseWriter, r *http.Request, dst interface{}) error {
	// Use http.MaxBytesReader to prevent clients from sending huge request bodies (1MB limit).
	maxBytes := 1_048_576
	r.Body = http.MaxBytesReader(w, r.Body, int64(maxBytes))

	dec := json.NewDecoder(r.Body)
	dec.DisallowUnknownFields()

	err := dec.Decode(dst)
	if err != nil {
		// Specific error handling for different JSON parsing issues.
		return err // Simplified for brevity, but in a real app you'd have a switch here.
	}

	// Check that the request body only contained a single JSON value.
	err = dec.Decode(&struct{}{})
	if err != io.EOF {
		return errors.New("body must only contain a single JSON value")
	}

	return nil
}

// badRequestResponse sends a JSON-formatted error message with a 400 status code.
func (app *application) badRequestResponse(w http.ResponseWriter, r *http.Request, err error) {
	http.Error(w, err.Error(), http.StatusBadRequest)
}

// failedValidationResponse sends a JSON-formatted error message with a 422 status code.
func (app *application) failedValidationResponse(w http.ResponseWriter, r *http.Request, errors map[string]string) {
	err := app.writeJSON(w, http.StatusUnprocessableEntity, envelope{"errors": errors}, nil)
	if err != nil {
		app.logger.Println(err)
		http.Error(w, "The server encountered a problem and could not process your request", http.StatusInternalServerError)
	}
}


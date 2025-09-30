package main

import (
	"context"
	"database/sql"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/dinesh-suryanand/payments-platform/ledger-service/internal/data"
	_ "github.com/jackc/pgx/v5/stdlib"
)

// application struct holds dependencies for our service.
type application struct {
	config config
	models data.Models
	logger *log.Logger
}

type config struct {
	Port string
	DSN  string
}

func main() {
	cfg := config{
		Port: os.Getenv("LEDGER_SERVICE_PORT"),
		DSN:  os.Getenv("LEDGER_DB_DSN"),
	}
	if cfg.Port == "" {
		cfg.Port = "8082"
	}

	logger := log.New(os.Stdout, "", log.Ldate|log.Ltime)

	db, err := openDB(cfg.DSN)
	if err != nil {
		logger.Fatalf("could not connect to database: %v", err)
	}
	defer db.Close()
	logger.Println("database connection pool established")

	app := &application{
		config: cfg,
		models: data.NewModels(db),
		logger: logger,
	}

	// Use our new router to create a configurable server.
	srv := &http.Server{
		Addr:    ":" + cfg.Port,
		Handler: app.routes(), // This now calls the routes() function
	}

	logger.Printf("starting ledger-service on port %s", cfg.Port)
	// Start the new server.
	err = srv.ListenAndServe()
	if err != nil {
		logger.Fatal(err)
	}
}

// openDB creates and verifies a database connection pool.
func openDB(dsn string) (*sql.DB, error) {
	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	err = db.PingContext(ctx)
	if err != nil {
		return nil, err
	}
	return db, nil
}


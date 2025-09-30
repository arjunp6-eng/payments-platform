# Use the official Go image as a base
FROM golang:1.25-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the go.mod and go.sum files to download dependencies first
# This leverages Docker's layer caching
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the application source code
COPY . .

# Command to run the application
CMD ["go", "run", "./cmd/api"]
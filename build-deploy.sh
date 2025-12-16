#!/bin/bash

# Usage: ./build-deploy.sh [dev|prod]
# Builds the latest transaction-service image, runs MySQL as a background service, and Liquibase migration as a background job for the specified environment.

set -e

ENV="$1"
if [[ "$ENV" != "dev" && "$ENV" != "prod" ]]; then
  echo "Usage: $0 [dev|prod]"
  exit 1
fi

./gradlew clean build

if [ "$ENV" == "dev" ]; then
  COMPOSE_FILE="transaction-system/docker-compose-dev.yml"
  BUILD_ARG="dev"
else
  COMPOSE_FILE="transaction-system/docker-compose.yml"
  BUILD_ARG="prod"
fi

echo "Built latest transaction-service image with ENV=$BUILD_ARG."
# Always build the latest transaction-service image before deployment
DOCKER_BUILDKIT=1 docker build --build-arg ENV=$BUILD_ARG -t transaction-service .
echo "Latest docker image for transaction-service built successfully."

# Start MySQL service in background (only once)
echo "Starting MySQL service in background for $ENV..."
docker compose -f "$COMPOSE_FILE" up -d mysql
echo "MySQL service started in the background."

# Run Liquibase migration in background (can be repeatable with more liquibase change logs)
echo "Running Liquibase migration in background for $ENV..."
docker compose -f "$COMPOSE_FILE" up -d liquibase
echo "Liquibase migration started in the background."

if docker compose -f "$COMPOSE_FILE" ps -q transaction-service | grep -q .; then
    echo "Stopping and removing transaction-service container..."
    docker compose -f "$COMPOSE_FILE" rm -sf transaction-service
fi
echo "Running transactional service in background for $ENV..."
docker compose -f "$COMPOSE_FILE" up -d --force-recreate  transaction-service
echo "Transactional service started in the background."

echo "Deployment completed for $ENV. For more info check the container logs."

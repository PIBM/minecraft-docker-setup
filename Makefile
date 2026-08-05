.PHONY: all build up down logs restart clean help

all: build up

## Build the plugin inside Docker and extract usersettings.jar to root and build/libs folder
build:
	docker build -t paper-plugin-builder .
	docker create --name temp-builder paper-plugin-builder
	mkdir -p build/libs
	docker cp temp-builder:/output/usersettings.jar ./usersettings.jar
	docker cp temp-builder:/output/usersettings.jar ./build/libs/usersettings.jar
	docker rm temp-builder

## Start the Paper 26.2 Minecraft test server in background
up:
	docker compose up -d

## Stop the Paper 26.2 Minecraft test server
down:
	docker compose down

## View logs of the Paper 26.2 Minecraft server
logs:
	docker compose logs -f mc-server

## Restart the test server
restart: down up

## Clean build artifacts and server data
clean:
	docker compose down -v
	rm -rf build .gradle server-data usersettings.jar

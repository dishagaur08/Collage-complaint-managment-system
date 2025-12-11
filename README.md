# College Complaint Management System

A minimal Java + SQLite web backend with a static HTML/JS front-end for registering and tracking complaints. The backend uses `com.sun.net.httpserver` to expose simple JSON endpoints and persists data to a local SQLite database via JDBC.

## Features
- Register a complaint with name, contact, and issue details.
- Look up a complaint by numeric ID.
- View all complaints.
- Lightweight front-end served directly by the same HTTP server.

## Project Structure
- `MainServer.java` – HTTP server, routes (`/register`, `/search`, `/all`) and static file serving.
- `Complaint.java` – Simple complaint model.
- `ComplaintService.java` – Database CRUD helpers.
- `DBUtil.java` – SQLite connection and schema bootstrap (creates `complaints` table).
- `index.html`, `style.css`, `script.js` – Front-end UI and API calls.
- `lib/` – Place the SQLite JDBC driver JAR here (e.g., `sqlite-jdbc-<version>.jar`).

## Prerequisites
- JDK 8+ (tested with Java SE on Windows/PowerShell).
- SQLite JDBC driver JAR (download from https://github.com/xerial/sqlite-jdbc) placed inside `lib/`.

## Build and Run
From the project root (`d:\Complaint Managemt System`), in PowerShell:

```powershell
# Compile
javac -cp ".;lib/*" *.java

# Run the server on http://localhost:8080
java -cp ".;lib/*" MainServer
```

When the server starts, it also serves the front-end:
- Open `http://localhost:8080/` to use the UI.

## API Endpoints
- `POST /register` – Body JSON: `{ "name": "...", "contact": "...", "issue": "..." }`.
  - Responses: `{"message": "Complaint Registered! ID: <id>"}` or `{"error": "..."}`.
- `GET /search?id=<id>` – Fetch a specific complaint.
- `GET /all` – Return all complaints as a JSON array.

All endpoints set `Access-Control-Allow-Origin: *` for easy local testing.

## Data Storage
- SQLite database file `complaints.db` is created in the project root on first run.
- Table: `complaints(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, contact TEXT, issue TEXT, status TEXT DEFAULT 'Pending')`.

## Notes
- Error handling is minimal; inputs are validated for non-empty fields server-side.
- The JSON parsing in `MainServer` is a simple string extractor; avoid nested quotes in payload values.
- To reset data, delete `complaints.db` (the schema will be recreated automatically on next start).

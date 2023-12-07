# Quests Backend

The backend for Quests consists of a database and a web API to interact with
the database.

## About
Quests is an Android application for managing simple to-do lists. One of Quests'
features is a backup service where a user can create an account and upload / load
their data through the internet. This backend serves to fulfill that feature.

## Getting Started
### Prerequisites
* Python 3.11 or higher
* PostgreSQL 15

### Installation
After cloning the repository, set up and start a [Python virtual environment](https://docs.python.org/3.11/library/venv.html),
then run
```bash
pip install -r requirements.txt
```

Create a `.env` file in the root directory with these values
```
DATABASE_HOSTNAME=
DATABASE_USER=
DATABASE_PASSWORD=
DATABASE_NAME=
DATABASE_PORT=
SECRET_KEY=
```

Here is an example of a `.env` file used during development
```
DATABASE_HOSTNAME=localhost
DATABASE_USER=postgres
DATABASE_PASSWORD=admin
DATABASE_NAME=quests
DATABASE_PORT=5432
SECRET_KEY=dev
```

### Usage
Start the Flask application in development:
```bash
flask run --debug
```
`flask run` uses Flask’s built-in development server, but this should not be used
when running the application publicly. Instead, use a production WSGI server.

You can open your browser to interact with the API documentation at
http://localhost:5000/swagger-ui/

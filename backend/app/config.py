import os
from dotenv import load_dotenv

from sqlalchemy import URL

load_dotenv()
SECRET_KEY=os.getenv('SECRET_KEY')
DATABASE_HOSTNAME = os.getenv('DATABASE_HOSTNAME')
DATABASE_USER = os.getenv('DATABASE_USER')
DATABASE_PASSWORD = os.getenv('DATABASE_PASSWORD')
DATABASE_NAME = os.getenv('DATABASE_NAME')
DATABASE_PORT = os.getenv('DATABASE_PORT')
SQLALCHEMY_DATABASE_URI = URL.create(
    'postgresql+psycopg',
    username=DATABASE_USER,
    password=DATABASE_PASSWORD,
    host=DATABASE_HOSTNAME,
    port=DATABASE_PORT,
    database=DATABASE_NAME
)

from flask import Blueprint, abort, make_response
from flask_apispec import doc, use_kwargs, marshal_with
from webargs import fields
from sqlalchemy import exc
from marshmallow import validate
from flask_jwt_extended import create_access_token, current_user, jwt_required, JWTManager

from app.database import Session

from .service import (
    get,
)
from .. import docs


bp = Blueprint('user', __name__, url_prefix='/users')


# TODO: endpoints for uploading / retrieving data

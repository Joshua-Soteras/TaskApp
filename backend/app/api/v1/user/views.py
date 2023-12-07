from flask import Blueprint, abort, jsonify
from flask_apispec import doc, use_kwargs, marshal_with
from webargs import fields
from sqlalchemy import exc
from marshmallow import validate
from flask_jwt_extended import current_user, jwt_required

from app.database import Session
from app.schemas import UserSchema

from .service import (
    get,
    update_data,
)
from .. import docs


bp = Blueprint('user', __name__, url_prefix='/users')


@doc(
    summary='Get task data',
    description='Returns the task data associated with the signed in user.',
    tags=['data']
)
@bp.route('/data', methods=['GET'])
@jwt_required()
@marshal_with(UserSchema, code=200)
def get_data():
    return current_user
docs.register(get_data, blueprint='api.v1.user')


@doc(
    summary='Upload task data',
    description='Updates the task data associated with the signed in user'
        ' with the contents of the request body.',
    tags=['data']
)
@bp.route('/data', methods=['POST'])
@jwt_required()
@use_kwargs({
    'data': fields.Str(required=True)
})
@marshal_with(UserSchema, code=200)
def upload_data(data):
    try:
        update_data(Session, current_user.username, data)
    except Exception:
        # Not sure what could cause this
        abort(400, description='Unexpected error occurred when updating data.')
    return current_user
docs.register(upload_data, blueprint='api.v1.user')

# test command
# curl -X POST "http://localhost:5000/api/v1/users/data" -H "accept: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcwMDgwNDkxMywianRpIjoiNTMwOTlkZTItMThjOS00ZmIzLWJjMzUtMmRiNmE1ODFmZTVkIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6InN0cmluZyIsIm5iZiI6MTcwMDgwNDkxMywiZXhwIjoxNzAwODA0OTczfQ.F7XcmAnz25soOk9Edggm0YRJUZGGln91qr0cqrIEv84" -H "Content-Type: application/json" -d "{ \"data\": \"string\"}"

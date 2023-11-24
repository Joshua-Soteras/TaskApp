from flask import Blueprint, abort, make_response, jsonify
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


@doc(
    summary='Get task data',
    description='Returns the task data associated with the signed in user.',
    tags=['data']
)
@bp.route('/data', methods=['GET'])
@jwt_required()
@marshal_with(None, code=200)
def get_data():
    return jsonify(data=current_user.data)
docs.register(get_data, blueprint='api.v1.user')


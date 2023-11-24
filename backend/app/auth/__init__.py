from flask import Blueprint, jsonify, make_response, abort
from flask_jwt_extended import create_access_token, create_refresh_token, JWTManager, jwt_required, current_user
from werkzeug.security import check_password_hash, generate_password_hash
from flask_apispec import doc, use_kwargs, marshal_with
from webargs import fields
from sqlalchemy import exc

from app.database import Session
from app.api.v1.user.service import (
    get as get_user,
    create as create_user,
)
from app.api.v1 import docs


bp = Blueprint('auth', __name__, url_prefix='/auth')

jwt = JWTManager()

# Register a callback function that takes whatever object is passed in as the
# identity when creating JWTs and converts it to a JSON serializable format.
@jwt.user_identity_loader
def user_identity_lookup(user):
    return user.username

# Register a callback function that loads a user from your database whenever
# a protected route is accessed. This should return any python object on a
# successful lookup, or None if the lookup failed for any reason (for example
# if the user has been deleted from the database).
@jwt.user_lookup_loader
def user_lookup_callback(_jwt_header, jwt_data):
    identity = jwt_data["sub"]
    return get_user(Session, identity)


@doc(
    summary='Register',
    description='Create a new user with the passed username and password.',
    tags=['auth']
)
@bp.route('/register', methods=['POST'])
@use_kwargs({
    'username': fields.Str(required=True),
    'password': fields.Str(required=True),
})
@marshal_with(None, code=200)
def register(username, password):
    try:
        create_user(Session, username, generate_password_hash(password))
    except exc.IntegrityError as e:
        abort(400, description='Username is already used')
    return make_response('', 200)
docs.register(register, blueprint='auth')


@doc(
    summary='Login',
    description='Logs in with the passed username and password, returns'
        ' an access token.',
    tags=['auth']
)
@bp.route('/login', methods=['POST'])
@use_kwargs({
    'username': fields.Str(required=True),
    'password': fields.Str(required=True),
})
@marshal_with(None, code=200)
def login(username, password):
    user = get_user(Session, username)
    if not user or not check_password_hash(user.password, password):
        abort(401, 'Wrong username or password')

    # Notice that we are passing in the actual sqlalchemy user object here
    access_token = create_access_token(identity=user)
    refresh_token = create_refresh_token(identity=user)
    return jsonify(access_token=access_token, refresh_token=refresh_token)
docs.register(login, blueprint='auth')


# We are using the `refresh=True` options in jwt_required to only allow
# refresh tokens to access this route.
@doc(
    summary='Refresh access token',
    description='Refreshes the access token associated with the passed'
        ' refresh token.',
    tags=['auth']
)
@bp.route('/refresh', methods=['POST'])
@jwt_required(refresh=True)
@marshal_with(None, code=200)
def refresh():
    access_token = create_access_token(identity=current_user)
    return jsonify(access_token=access_token)
docs.register(refresh, blueprint='auth')

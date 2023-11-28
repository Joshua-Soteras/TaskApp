from flask import Blueprint

bp = Blueprint('api', __name__, url_prefix='/api')

from . import v1

bp.register_blueprint(v1.bp)

from app.api import errors

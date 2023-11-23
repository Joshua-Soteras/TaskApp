from flask import Blueprint
from apispec import APISpec
from apispec.ext.marshmallow import MarshmallowPlugin
from flask_apispec import FlaskApiSpec


bp = Blueprint('v1', __name__, url_prefix='/v1')

ma_plugin = MarshmallowPlugin()

api_spec_mapping = {
    'APISPEC_SPEC': APISpec(
        title='Test project',
        version='v1',
        plugins=[ma_plugin],
        openapi_version='2.0',
    ),
    'APISPEC_SWAGGER_URL': '/api/v1/swagger/',
}

docs = FlaskApiSpec()

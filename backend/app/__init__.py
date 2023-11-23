from flask import Flask
from flask_cors import CORS
from flask_jwt_extended import JWTManager

from .config import SECRET_KEY

def create_app(test_config=None):
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)

    CORS(app)
    jwt = JWTManager(app)

    app.config['CORS_HEADERS'] = 'Content-Type'
    app.config["JWT_SECRET_KEY"] = SECRET_KEY
    if test_config is None:
        # load the instance config, if it exists, when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # load the test config if passed in
        app.config.from_mapping(test_config)

    from . import database
    database.init_db()

    @app.teardown_appcontext
    def shutdown_session(exception=None):
        database.Session.remove()

    # from . import api
    # app.register_blueprint(api.bp)

    # from .api.v1 import api_spec_mapping, docs
    # app.config.update(api_spec_mapping)
    # docs.init_app(app)

    # from .auth import auth
    # app.register_blueprint(auth.bp)

    # print(app.url_map)

    return app
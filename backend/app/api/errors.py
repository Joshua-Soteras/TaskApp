from flask import jsonify
from werkzeug.http import HTTP_STATUS_CODES

from . import bp


def error_response(status_code, e=None):
    payload = {
        'status': str(status_code),
        'title': HTTP_STATUS_CODES.get(status_code, 'Unknown error')
    }
    if e.description:
        payload['detail'] = e.description
    return jsonify(error=payload), status_code


@bp.app_errorhandler(400)
def bad_request(e):
    return error_response(400, e)


@bp.app_errorhandler(401)
def bad_request(e):
    return error_response(400, e)


@bp.app_errorhandler(404)
def resource_not_found(e):
    return error_response(404, e)


@bp.app_errorhandler(405)
def method_not_allowed(e):
    return error_response(405, e)


@bp.app_errorhandler(422)
def handle_error(e):
    # e is an werkzeug.exceptions.UnprocessableEntity, but webargs'
    # has a .data attribute for some reason
    if hasattr(e, 'data'):
        e.description = e.data.get('messages', ['Invalid request.'])
        return error_response(422, e)
    else:
        return error_response(422, e)


@bp.app_errorhandler(500)
def internal_server_error(e):
    return error_response(500, e)

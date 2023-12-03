from marshmallow_sqlalchemy import SQLAlchemySchema, auto_field

from .models import (
    User
)


class UserSchema(SQLAlchemySchema):
    class Meta:
        model = User
        load_instance = True

    data = auto_field()

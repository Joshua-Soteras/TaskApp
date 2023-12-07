from sqlalchemy import select, exc
from sqlalchemy.orm import scoped_session

from app.models import User


def get(db_session: scoped_session, username: str):
    """Get a user by username."""
    return db_session.scalar(
        select(User).where(User.username == username)
    )


def create(db_session: scoped_session, username: str, password: str):
    """Create a user with the passed username and password."""
    try:
        user = User(
            username=username,
            password=password,
        )
        db_session.add(user)
        db_session.commit()
    except exc.IntegrityError as e:
        print(e)
        raise e
    return user


def update_data(db_session: scoped_session, username: str, data: str):
    """Update the `data` field associated with the `username`"""
    user = get(db_session, username)
    user.data = data
    try:
        db_session.commit()
    except Exception as e:
        # No idea what could cause this
        print(e)
        raise e
    return user

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


def update(db_session: scoped_session, username: str, password):
    pass
    # TODO: should be able to update data from here?

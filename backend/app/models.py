from typing import Optional

from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.types import Integer, String

from .database import Base


class User(Base):
    """Table to store users and their data."""
    __tablename__ = 'user'

    username: Mapped[str] = mapped_column(String, primary_key=True)
    password: Mapped[str] = mapped_column(String)
    api_key: Mapped[Optional[str]] = mapped_column(String)
    data: Mapped[Optional[str]] = mapped_column(String)

    def __repr__(self) -> str:
        return (
            f'User(username={self.username!r},'
            f' password={self.password!r},'
            f' api_key={self.api_key!r},'
            f' data={self.data!r})'
        )

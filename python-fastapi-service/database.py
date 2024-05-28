from sqlalchemy import create_engine, Column, Integer, String, Text, func
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

DATABASE_URL = "postgresql://postgres:1999@localhost:5433/summarydb"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class SummaryRecord(Base):
    __tablename__ = "summary_record"

    id = Column(Integer, primary_key=True, index=True)
    url = Column(String, nullable=False)
    summary = Column(Text, nullable=False)

Base.metadata.create_all(bind=engine)

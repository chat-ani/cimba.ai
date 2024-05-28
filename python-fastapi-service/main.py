from fastapi import Depends, FastAPI, HTTPException
from pydantic import BaseModel, HttpUrl
import httpx
import logging
import openai
from fastapi.middleware.cors import CORSMiddleware
from bs4 import BeautifulSoup
from sqlalchemy.orm import Session
from database import SessionLocal, SummaryRecord

# Initialize FastAPI app
app = FastAPI()

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Enhanced logging configuration
logging.basicConfig(
    level=logging.DEBUG,  # Set to DEBUG to capture all levels of logs
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler()]
)

logger = logging.getLogger(__name__)

# In-memory history store
history = []

#include
# Set your OpenAI API key
# openai.api_key = ''

class UrlInput(BaseModel):
    url: HttpUrl

def extract_text_from_html(html_content: str) -> str:
    soup = BeautifulSoup(html_content, 'html.parser')
    text = soup.get_text(separator=' ', strip=True)
    return text

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/api/summarize")
async def summarize(input: UrlInput, db: Session = Depends(get_db)):
    try:
        url_str = str(input.url)  # Convert HttpUrl to string
        logger.info(f"Called URL for summary: {url_str}")

        # Fetch the URL content with redirects enabled
        async with httpx.AsyncClient(follow_redirects=True) as client:
            response = await client.get(url_str)
            response.raise_for_status()  # Ensure we notice bad responses
            html_content = response.text
            logger.info(f"Fetched HTML content: {html_content[:500]}...")  

        # Extract text from HTML content
        extracted_text = extract_text_from_html(html_content)
        logger.info(f"Extracted text content: {extracted_text[:500]}...") 

        openai_response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": f"Summarize the following text: {extracted_text}"}
            ],
            max_tokens=150
        )
        summary = openai_response.choices[0].message["content"].strip()

        logger.info(f"Generated summary: {summary}")

        # Store in database
        db_summary = SummaryRecord(url=url_str, summary=summary)
        db.add(db_summary)
        db.commit()
        db.refresh(db_summary)

        # Store in history
        history_entry = {"url": url_str, "summary": summary}
        history.append(history_entry)

        return {"summary": summary}
    except httpx.HTTPStatusError as e:
        logger.error(f"HTTP error occurred: {e.response.status_code} - {e.response.text}")
        raise HTTPException(status_code=e.response.status_code, detail=f"Error fetching the URL content: {e.response.text}")
    except openai.error.OpenAIError as e:
        logger.error(f"OpenAI API error occurred: {str(e)}")
        raise HTTPException(status_code=500, detail=f"OpenAI API error: {str(e)}")
    except Exception as e:
        logger.error(f"An unexpected error occurred: {str(e)}")
        raise HTTPException(status_code=500, detail=f"An unexpected error occurred: {str(e)}")

@app.get("/api/history")
async def get_history(db: Session = Depends(get_db)):
    return db.query(SummaryRecord).all()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)

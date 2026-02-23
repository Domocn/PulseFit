FROM python:3.12-slim

WORKDIR /app

# Install dependencies first for better caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Railway sets PORT env var automatically
CMD uvicorn server:app --host 0.0.0.0 --port ${PORT:-8001}

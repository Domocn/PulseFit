FROM python:3.12-slim

# MALLOC_ARENA_MAX caps glibc's per-thread arenas, which otherwise inflate a
# threaded Python process's RSS well beyond its real heap. Single biggest
# free win on resident memory.
ENV MALLOC_ARENA_MAX=2 \
    PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

WORKDIR /app

# Dependencies first for layer caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Drop privileges — the app never needs to write to its own image
RUN useradd --create-home --uid 10001 app \
    && chown -R app:app /app
USER app

EXPOSE 8001

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD python -c "import urllib.request,os,sys; \
sys.exit(0 if urllib.request.urlopen('http://127.0.0.1:'+os.environ.get('PORT','8001')+'/api/health', timeout=4).status==200 else 1)"

# Single worker on purpose: each additional worker is a full copy of the
# interpreter and its imports. Scale with replicas, not workers, until the
# per-process footprint is known.
CMD uvicorn server:app --host 0.0.0.0 --port ${PORT:-8001} --workers 1

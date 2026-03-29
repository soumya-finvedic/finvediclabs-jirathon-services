from fastapi import FastAPI

from app.api import router
from app.config import settings
from app.database import init_indexes

app = FastAPI(title=settings.service_name)
app.include_router(router, prefix=settings.api_prefix)


@app.on_event("startup")
async def startup_event() -> None:
    await init_indexes()


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok"}

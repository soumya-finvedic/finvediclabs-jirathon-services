from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorCollection

from app.config import settings

_client: AsyncIOMotorClient | None = None


def get_client() -> AsyncIOMotorClient:
    global _client
    if _client is None:
        _client = AsyncIOMotorClient(settings.mongo_uri)
    return _client


def get_collection() -> AsyncIOMotorCollection:
    db = get_client()[settings.mongo_db]
    return db[settings.mongo_collection]


async def init_indexes() -> None:
    collection = get_collection()
    await collection.create_index("status")
    await collection.create_index("evaluation_type")
    await collection.create_index("created_at")
    await collection.create_index("requested_by")

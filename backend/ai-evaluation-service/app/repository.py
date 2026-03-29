from datetime import datetime, timezone

from app.database import get_collection
from app.models import EvaluationDocument, EvaluationStatus


class EvaluationRepository:
    def __init__(self) -> None:
        self.collection = get_collection()

    async def create(self, document: EvaluationDocument) -> None:
        payload = document.model_dump(mode="json")
        payload["_id"] = payload["id"]
        await self.collection.insert_one(payload)

    async def get(self, evaluation_id: str) -> dict | None:
        doc = await self.collection.find_one({"_id": evaluation_id})
        return self._normalize(doc)

    async def set_processing(self, evaluation_id: str) -> None:
        await self.collection.update_one(
            {"_id": evaluation_id},
            {
                "$set": {
                    "status": EvaluationStatus.PROCESSING.value,
                    "updated_at": datetime.now(timezone.utc).isoformat(),
                }
            },
        )

    async def set_completed(self, evaluation_id: str, result: dict, llm_raw_response: str) -> None:
        now = datetime.now(timezone.utc).isoformat()
        await self.collection.update_one(
            {"_id": evaluation_id},
            {
                "$set": {
                    "status": EvaluationStatus.COMPLETED.value,
                    "result": result,
                    "llm_raw_response": llm_raw_response,
                    "updated_at": now,
                }
            },
        )

    async def set_failed(self, evaluation_id: str, error_message: str) -> None:
        now = datetime.now(timezone.utc).isoformat()
        await self.collection.update_one(
            {"_id": evaluation_id},
            {
                "$set": {
                    "status": EvaluationStatus.FAILED.value,
                    "error_message": error_message,
                    "updated_at": now,
                }
            },
        )

    async def list_recent(self, limit: int = 50) -> list[dict]:
        cursor = self.collection.find().sort("created_at", -1).limit(limit)
        docs = await cursor.to_list(length=limit)
        return [self._normalize(d) for d in docs]

    def _normalize(self, doc: dict | None) -> dict | None:
        if doc is None:
            return None
        doc["id"] = doc.get("_id", doc.get("id"))
        doc.pop("_id", None)
        return doc

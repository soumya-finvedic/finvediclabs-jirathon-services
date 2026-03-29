from fastapi import BackgroundTasks, HTTPException

from app.config import settings
from app.llm_client import LLMClient
from app.models import (
    DescriptiveEvaluationRequest,
    EvaluationDocument,
    EvaluationResult,
    EvaluationStatus,
    EvaluationType,
    Principal,
    TranscriptEvaluationRequest,
)
from app.repository import EvaluationRepository


class EvaluationService:
    def __init__(self) -> None:
        self.repo = EvaluationRepository()
        self.llm = LLMClient()

    async def submit_descriptive(
        self,
        request: DescriptiveEvaluationRequest,
        principal: Principal,
        background_tasks: BackgroundTasks,
    ) -> str:
        return await self._submit(
            evaluation_type=EvaluationType.DESCRIPTIVE,
            prompt=request.prompt,
            rubric=request.rubric,
            max_score=request.max_score,
            content=request.answer_text,
            principal=principal,
            background_tasks=background_tasks,
        )

    async def submit_transcript(
        self,
        request: TranscriptEvaluationRequest,
        principal: Principal,
        background_tasks: BackgroundTasks,
    ) -> str:
        return await self._submit(
            evaluation_type=EvaluationType.TRANSCRIPT,
            prompt=request.prompt,
            rubric=request.rubric,
            max_score=request.max_score,
            content=request.transcript_text,
            principal=principal,
            background_tasks=background_tasks,
        )

    async def submit_file(
        self,
        *,
        prompt: str,
        rubric: str,
        max_score: float,
        file_content: str,
        file_name: str | None,
        mime_type: str | None,
        principal: Principal,
        background_tasks: BackgroundTasks,
    ) -> str:
        return await self._submit(
            evaluation_type=EvaluationType.FILE,
            prompt=prompt,
            rubric=rubric,
            max_score=max_score,
            content=file_content,
            principal=principal,
            background_tasks=background_tasks,
            file_name=file_name,
            mime_type=mime_type,
        )

    async def trigger_admin(self, evaluation_id: str, background_tasks: BackgroundTasks) -> None:
        existing = await self.repo.get(evaluation_id)
        if not existing:
            raise HTTPException(status_code=404, detail="Evaluation not found")
        background_tasks.add_task(self.process_evaluation, evaluation_id)

    async def process_evaluation(self, evaluation_id: str) -> None:
        doc = await self.repo.get(evaluation_id)
        if not doc:
            return

        await self.repo.set_processing(evaluation_id)

        try:
            parsed, raw = await self.llm.evaluate(
                prompt=doc["prompt"],
                rubric=doc["rubric"],
                max_score=float(doc["max_score"]),
                content=doc["content"],
                evaluation_type=doc["evaluation_type"],
            )
            result = EvaluationResult(
                score=float(parsed["score"]),
                feedback=str(parsed["feedback"]),
                strengths=[str(x) for x in parsed.get("strengths", [])],
                improvements=[str(x) for x in parsed.get("improvements", [])],
            )
            await self.repo.set_completed(evaluation_id, result.model_dump(mode="json"), raw)
        except Exception as ex:
            await self.repo.set_failed(evaluation_id, str(ex))

    async def get_evaluation(self, evaluation_id: str) -> dict:
        doc = await self.repo.get(evaluation_id)
        if not doc:
            raise HTTPException(status_code=404, detail="Evaluation not found")
        return doc

    async def list_evaluations(self, limit: int) -> list[dict]:
        return await self.repo.list_recent(limit=limit)

    async def _submit(
        self,
        *,
        evaluation_type: EvaluationType,
        prompt: str,
        rubric: str,
        max_score: float,
        content: str,
        principal: Principal,
        background_tasks: BackgroundTasks,
        file_name: str | None = None,
        mime_type: str | None = None,
    ) -> str:
        self._enforce_content_limit(evaluation_type, content)

        document = EvaluationDocument(
            evaluation_type=evaluation_type,
            status=EvaluationStatus.PENDING,
            prompt=prompt,
            rubric=rubric,
            max_score=max_score,
            content=content,
            file_name=file_name,
            mime_type=mime_type,
            requested_by=principal.user_id,
            requested_by_email=principal.email,
        )

        await self.repo.create(document)
        background_tasks.add_task(self.process_evaluation, document.id)
        return document.id

    def _enforce_content_limit(self, evaluation_type: EvaluationType, content: str) -> None:
        size = len(content.encode("utf-8"))
        if evaluation_type == EvaluationType.DESCRIPTIVE and size > settings.max_answer_size_bytes:
            raise HTTPException(status_code=400, detail="Descriptive answer exceeds max size")
        if evaluation_type == EvaluationType.TRANSCRIPT and size > settings.max_transcript_size_bytes:
            raise HTTPException(status_code=400, detail="Transcript exceeds max size")
        if evaluation_type == EvaluationType.FILE and size > settings.max_file_size_bytes:
            raise HTTPException(status_code=400, detail="File content exceeds max size")

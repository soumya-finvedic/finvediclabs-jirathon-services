from datetime import datetime, timezone
from enum import Enum
from uuid import uuid4

from pydantic import BaseModel, Field


class EvaluationType(str, Enum):
    DESCRIPTIVE = "descriptive"
    FILE = "file"
    TRANSCRIPT = "transcript"


class EvaluationStatus(str, Enum):
    PENDING = "PENDING"
    PROCESSING = "PROCESSING"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"


class BaseEvaluationRequest(BaseModel):
    prompt: str = Field(min_length=3, max_length=4000)
    rubric: str = Field(min_length=3, max_length=4000)
    max_score: float = Field(gt=0, le=1000)


class DescriptiveEvaluationRequest(BaseEvaluationRequest):
    answer_text: str = Field(min_length=1, max_length=50000)


class TranscriptEvaluationRequest(BaseEvaluationRequest):
    transcript_text: str = Field(min_length=1, max_length=300000)


class EvaluationResult(BaseModel):
    score: float
    feedback: str
    strengths: list[str] = Field(default_factory=list)
    improvements: list[str] = Field(default_factory=list)


class EvaluationDocument(BaseModel):
    id: str = Field(default_factory=lambda: str(uuid4()))
    evaluation_type: EvaluationType
    status: EvaluationStatus = EvaluationStatus.PENDING

    prompt: str
    rubric: str
    max_score: float
    content: str
    file_name: str | None = None
    mime_type: str | None = None

    requested_by: str
    requested_by_email: str | None = None

    result: EvaluationResult | None = None
    error_message: str | None = None
    llm_raw_response: str | None = None

    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))


class TriggerResponse(BaseModel):
    evaluation_id: str
    status: EvaluationStatus


class ApiResponse(BaseModel):
    success: bool
    message: str | None = None
    data: dict | None = None
    error: str | None = None


class Principal(BaseModel):
    user_id: str
    email: str | None = None
    roles: set[str] = Field(default_factory=set)

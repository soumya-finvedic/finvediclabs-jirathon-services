from fastapi import APIRouter, BackgroundTasks, Depends, File, Form, Header, HTTPException, UploadFile

from app.config import settings
from app.models import (
    ApiResponse,
    DescriptiveEvaluationRequest,
    Principal,
    TranscriptEvaluationRequest,
)
from app.service import EvaluationService

router = APIRouter()
service = EvaluationService()


def get_principal(
    x_user_id: str = Header(..., alias="X-User-Id"),
    x_user_email: str | None = Header(default=None, alias="X-User-Email"),
    x_user_roles: str | None = Header(default=None, alias="X-User-Roles"),
) -> Principal:
    roles = set()
    if x_user_roles:
        roles = {r.strip().upper() for r in x_user_roles.split(",") if r.strip()}
    return Principal(user_id=x_user_id, email=x_user_email, roles=roles)


def require_admin_token(x_admin_token: str = Header(..., alias="X-Admin-Token")) -> None:
    if not settings.admin_token or x_admin_token != settings.admin_token:
        raise HTTPException(status_code=403, detail="Invalid admin token")


@router.post("/evaluations/answers", response_model=ApiResponse)
async def evaluate_descriptive_answer(
    request: DescriptiveEvaluationRequest,
    background_tasks: BackgroundTasks,
    principal: Principal = Depends(get_principal),
) -> ApiResponse:
    evaluation_id = await service.submit_descriptive(request, principal, background_tasks)
    return ApiResponse(success=True, message="Evaluation queued", data={"evaluationId": evaluation_id})


@router.post("/evaluations/transcripts", response_model=ApiResponse)
async def evaluate_transcript(
    request: TranscriptEvaluationRequest,
    background_tasks: BackgroundTasks,
    principal: Principal = Depends(get_principal),
) -> ApiResponse:
    evaluation_id = await service.submit_transcript(request, principal, background_tasks)
    return ApiResponse(success=True, message="Evaluation queued", data={"evaluationId": evaluation_id})


@router.post("/evaluations/files", response_model=ApiResponse)
async def evaluate_file_upload(
    background_tasks: BackgroundTasks,
    prompt: str = Form(...),
    rubric: str = Form(...),
    max_score: float = Form(...),
    file: UploadFile = File(...),
    principal: Principal = Depends(get_principal),
) -> ApiResponse:
    raw = await file.read()
    if len(raw) > settings.max_file_size_bytes:
        raise HTTPException(status_code=400, detail="Uploaded file exceeds max allowed size")

    content = raw.decode("utf-8", errors="ignore")
    evaluation_id = await service.submit_file(
        prompt=prompt,
        rubric=rubric,
        max_score=max_score,
        file_content=content,
        file_name=file.filename,
        mime_type=file.content_type,
        principal=principal,
        background_tasks=background_tasks,
    )
    return ApiResponse(success=True, message="Evaluation queued", data={"evaluationId": evaluation_id})


@router.post("/admin/evaluations/{evaluation_id}/trigger", response_model=ApiResponse)
async def trigger_admin_evaluation(
    evaluation_id: str,
    background_tasks: BackgroundTasks,
    _: None = Depends(require_admin_token),
) -> ApiResponse:
    await service.trigger_admin(evaluation_id, background_tasks)
    return ApiResponse(success=True, message="Admin trigger accepted", data={"evaluationId": evaluation_id})


@router.get("/evaluations/{evaluation_id}", response_model=ApiResponse)
async def get_evaluation(
    evaluation_id: str,
    _: Principal = Depends(get_principal),
) -> ApiResponse:
    data = await service.get_evaluation(evaluation_id)
    return ApiResponse(success=True, data=data)


@router.get("/evaluations", response_model=ApiResponse)
async def list_evaluations(
    limit: int = 20,
    _: Principal = Depends(get_principal),
) -> ApiResponse:
    safe_limit = max(1, min(limit, 100))
    data = await service.list_evaluations(limit=safe_limit)
    return ApiResponse(success=True, data={"items": data})

import json
import re
from typing import Any

import httpx

from app.config import settings


class LLMClient:
    async def evaluate(
        self,
        *,
        prompt: str,
        rubric: str,
        max_score: float,
        content: str,
        evaluation_type: str,
    ) -> tuple[dict[str, Any], str]:
        if not settings.openai_api_key:
            return self._heuristic_response(content, rubric, max_score), "heuristic"

        system_prompt = (
            "You are an objective evaluator. Score the response and provide concise educational feedback. "
            "Return valid JSON with keys: score (number), feedback (string), strengths (string array), improvements (string array)."
        )

        user_prompt = (
            f"Evaluation type: {evaluation_type}\n"
            f"Prompt:\n{prompt}\n\n"
            f"Rubric:\n{rubric}\n\n"
            f"Maximum score: {max_score}\n\n"
            f"Submission content:\n{content}\n"
        )

        payload = {
            "model": settings.openai_model,
            "temperature": 0.2,
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            "response_format": {"type": "json_object"},
        }

        headers = {
            "Authorization": f"Bearer {settings.openai_api_key}",
            "Content-Type": "application/json",
        }

        timeout = httpx.Timeout(settings.worker_timeout_seconds)
        async with httpx.AsyncClient(timeout=timeout) as client:
            response = await client.post(
                f"{settings.openai_base_url}/chat/completions",
                headers=headers,
                json=payload,
            )
            response.raise_for_status()
            raw = response.text
            parsed = self._extract_json(response.json())
            parsed["score"] = float(max(0.0, min(max_score, float(parsed.get("score", 0.0)))))
            parsed.setdefault("feedback", "")
            parsed.setdefault("strengths", [])
            parsed.setdefault("improvements", [])
            return parsed, raw

    def _extract_json(self, data: dict[str, Any]) -> dict[str, Any]:
        text = data["choices"][0]["message"]["content"]
        try:
            return json.loads(text)
        except json.JSONDecodeError:
            match = re.search(r"\{.*\}", text, flags=re.DOTALL)
            if not match:
                raise ValueError("Model response is not valid JSON")
            return json.loads(match.group(0))

    def _heuristic_response(self, content: str, rubric: str, max_score: float) -> dict[str, Any]:
        content_words = set(re.findall(r"[a-zA-Z]{3,}", content.lower()))
        rubric_words = set(re.findall(r"[a-zA-Z]{3,}", rubric.lower()))
        overlap = len(content_words & rubric_words)
        baseline = min(1.0, len(content) / 2500)
        rubric_factor = min(1.0, overlap / 20) if rubric_words else 0.5
        score = round(max_score * (0.55 * baseline + 0.45 * rubric_factor), 2)

        return {
            "score": max(0.0, min(max_score, score)),
            "feedback": "Heuristic evaluation used because LLM credentials are not configured.",
            "strengths": [
                "Submission provided enough material for evaluation" if len(content) > 120 else "Submission is concise"
            ],
            "improvements": [
                "Add clearer structure and explicit references to rubric criteria",
                "Expand key points with concrete examples",
            ],
        }

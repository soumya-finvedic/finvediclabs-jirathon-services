from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    service_name: str = "ai-evaluation-service"
    api_prefix: str = "/api/v1"

    mongo_uri: str = "mongodb://localhost:27017"
    mongo_db: str = "jirathon_ai_evaluation"
    mongo_collection: str = "evaluations"

    openai_api_key: str | None = None
    openai_base_url: str = "https://api.openai.com/v1"
    openai_model: str = "gpt-4o-mini"

    max_file_size_bytes: int = 2_000_000
    max_answer_size_bytes: int = 50_000
    max_transcript_size_bytes: int = 300_000

    worker_timeout_seconds: int = 45
    admin_token: str

    model_config = SettingsConfigDict(env_file=".env", env_prefix="AIEVAL_")


settings = Settings()

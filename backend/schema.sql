CREATE TABLE IF NOT EXISTS lahjat (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPZ NOT NULL DEFAULT now(),
    lahja TEXT NOT NULL,
    hinta NUMERIC,
    valmistaja TEXT NOT NULL
);
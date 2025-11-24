-- H2 database schema

CREATE TABLE IF NOT EXISTS work_items (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT
  , code            CHAR(5) NOT NULL
  , name            VARCHAR(60) NOT NULL
  , price           NUMERIC(10,2) NOT NULL
  , quantity        SMALLINT
  , purchase_date   DATE
  , last_updated_at TIMESTAMP WITH TIME ZONE
  , clob_data       CLOB
  , blob_data       BLOB
);


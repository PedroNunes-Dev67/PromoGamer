-- Tabela DEAL
CREATE TABLE deal (
                      id_deal               VARCHAR(100)  NOT NULL,
                      title                 VARCHAR(500)  NOT NULL,
                      steam_app_id          VARCHAR(50)   NOT NULL,
                      steam_rating_percent  VARCHAR(10)   NOT NULL,
                      status                VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
                      creation_date         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT pk_deal PRIMARY KEY (id_deal)
);

-- Tabela MESSAGE
CREATE TABLE message (
                         id_message      BIGINT        GENERATED ALWAYS AS IDENTITY,
                         id_deal         VARCHAR(100)  NOT NULL,
                         send_date       TIMESTAMP,
                         creation_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         message_status  VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
                         source_type     VARCHAR(20)   NOT NULL,
                         number          VARCHAR(50)   NOT NULL,
                         mediatype       VARCHAR(20)   NOT NULL,
                         mimetype        VARCHAR(50)   NOT NULL,
                         media           VARCHAR(500)  NOT NULL,
                         caption         TEXT          NOT NULL,
                         CONSTRAINT pk_message PRIMARY KEY (id_message),
                         CONSTRAINT fk_message_deal FOREIGN KEY (id_deal) REFERENCES deal (id_deal),
                         CONSTRAINT uq_message_deal UNIQUE (id_deal)
);
CREATE TABLE deal (
                      deal_id               VARCHAR(100)  NOT NULL,
                      title                 VARCHAR(500)  NOT NULL,
                      steam_app_id          VARCHAR(50)   NOT NULL,
                      steam_rating_percent  VARCHAR(10)   NOT NULL ,
                      status                VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
                      creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT pk_deal PRIMARY KEY (deal_id)
);
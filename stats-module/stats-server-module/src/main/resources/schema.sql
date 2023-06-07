DROP TABLE IF EXISTS endpoint_hit CASCADE;
DROP INDEX IF EXISTS endpoint_hit_stats_uri;
DROP INDEX IF EXISTS endpoint_hit_stats_app_uri;
DROP INDEX IF EXISTS endpoint_hit_stats_app_uri_ip;

CREATE TABLE IF NOT EXISTS endpoint_hit
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(300)                            NOT NULL,
    uri       VARCHAR(500)                            NOT NULL,
    ip        VARCHAR(50)                             NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (id)
);

CREATE INDEX endpoint_hit_stats_uri ON endpoint_hit (uri);
CREATE INDEX endpoint_hit_stats_app_uri ON endpoint_hit (app, uri);
CREATE INDEX endpoint_hit_stats_app_uri_ip ON endpoint_hit (app, uri, ip);






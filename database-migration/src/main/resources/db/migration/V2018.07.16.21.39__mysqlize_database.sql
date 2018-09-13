CREATE TABLE user_details (
  user_id      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_ref     VARCHAR(12)  NOT NULL UNIQUE,
  email        VARCHAR(128) NOT NULL UNIQUE,
  display_name varchar(128) NOT NULL
)
  ENGINE = innodb;

CREATE TABLE user_tokens (
  user_token_id     BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  public_user_token VARCHAR(64)  NOT NULL UNIQUE,
  created_at        TIMESTAMP(6) NOT NULL,
  expires_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  valid             BOOL                  DEFAULT 1,
  user_id           BIGINT       NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user_details (user_id)
    ON DELETE CASCADE
)
  ENGINE = innodb;

CREATE TABLE target (
  target_id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  target_name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE target_access_rule (
  rule_id       BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  target_id     BIGINT       NOT NULL,
  resource_name VARCHAR(128) NOT NULL,
  action_name   VARCHAR(128) NOT NULL,
  CONSTRAINT rule_to_target FOREIGN KEY (target_id) REFERENCES target (target_id)
    ON DELETE CASCADE,
  UNIQUE (target_id, resource_name, action_name)
);

CREATE TABLE rule_grant (
  grant_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rule_id  BIGINT NOT NULL,
  user_id  BIGINT NOT NULL,
  CONSTRAINT grant_to_rule FOREIGN KEY (rule_id) REFERENCES target_access_rule (rule_id)
    ON DELETE CASCADE,
  CONSTRAINT grant_to_user FOREIGN KEY (user_id) REFERENCES user_details (user_id)
    ON DELETE CASCADE,
  UNIQUE (rule_id, user_id)
);

-- spring social stuff

CREATE TABLE ss_UserConnection (
  userId         VARCHAR(255) NOT NULL,
  providerId     VARCHAR(255) NOT NULL,
  providerUserId VARCHAR(255),
  `rank`         INT          NOT NULL,
  displayName    VARCHAR(255),
  profileUrl     VARCHAR(512),
  imageUrl       VARCHAR(512),
  accessToken    VARCHAR(512) NOT NULL,
  secret         VARCHAR(512),
  refreshToken   VARCHAR(512),
  expireTime     BIGINT,
  PRIMARY KEY (userId, providerId, providerUserId)
);

CREATE UNIQUE INDEX UserConnectionRank
  ON ss_UserConnection (userId, providerId, `rank`);

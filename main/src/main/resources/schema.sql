create TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
create TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);
create TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) NOT NULL UNIQUE
);
create TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(50) NOT NULL
);
create TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    paid               BOOLEAN       NOT NULL,
    participant_limit  BIGINT       NOT NULL,
    request_moderation BOOLEAN       NOT NULL,
    title              VARCHAR(120)  NOT NULL,
    state              VARCHAR(10)       NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    initiator_id       BIGINT REFERENCES users (id) ON delete CASCADE,
    category_id        BIGINT REFERENCES categories (id) ON delete CASCADE,
    location_id        BIGINT REFERENCES locations (id) ON delete CASCADE
);
create TABLE IF NOT EXISTS events_compilations
(
    events_id       BIGINT REFERENCES events (id) ON delete CASCADE,
    compilations_id BIGINT REFERENCES compilations (id) ON delete CASCADE
);
create TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(255) NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON delete CASCADE,
    event_id     BIGINT REFERENCES events (id) ON delete CASCADE
);
create TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     BIGINT REFERENCES users (id) ON delete CASCADE,
    event_id    BIGINT REFERENCES events (id) ON delete CASCADE,
    description VARCHAR(255) NOT NULL,
    timestamp   TIMESTAMP WITHOUT TIME ZONE
);
CREATE TABLE IF NOT EXISTS dungeons (
    id        varchar(20) NOT NULL,
    world     varchar(20) NOT NULL,
    weather   varchar(10) NOT NULL,
    life      int(11)     NOT NULL,
    nbPlayers int(11)     NOT NULL,
    time      int(11)     NOT NULL,
    timeLimit int(11)     NOT NULL,
    timeStop  tinyint(1)  NOT NULL,
    hunger    tinyint(1)  NOT NULL,
    magic     tinyint(1)  NOT NULL,
    godmode   tinyint(1)  NOT NULL,
    gem       int(11)     NOT NULL,
    name      varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    dungeon_id varchar(20)  NOT NULL,
    id         int(11)      NOT NULL,
    message    varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS portals (
    id         varchar(20) NOT NULL,
    dungeon_id varchar(20) NOT NULL,
    world      varchar(20) NOT NULL,
    pos1_x     int(11)     NOT NULL,
    pos2_x     int(11)     NOT NULL,
    pos1_y     int(11)     NOT NULL,
    pos2_y     int(11)     NOT NULL,
    pos1_z     int(11)     NOT NULL,
    pos2_z     int(11)     NOT NULL,
    lobby_x    float       NOT NULL,
    lobby_y    float       NOT NULL,
    lobby_z    float       NOT NULL
);

CREATE TABLE IF NOT EXISTS sign (
    id varchar(20) NOT NULL,
    x  int(11)     NOT NULL,
    y  int(11)     NOT NULL,
    z  int(11)     NOT NULL
);
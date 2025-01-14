-- Set the time zone to Asia/Seoul
SET GLOBAL time_zone = 'Asia/Seoul';
SET time_zone = 'Asia/Seoul';

-- DB 생성
CREATE DATABASE IF NOT EXISTS company;
USE company;

-- artist(작가) 테이블 생성
CREATE TABLE artist (
    aid BIGINT NOT NULL AUTO_INCREMENT,
    artist_name VARCHAR(255),
    created_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (aid)
);

-- member(소비자) 테이블 생성
CREATE TABLE members (
    mid BIGINT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (mid)
);

-- comics(작품) 테이블 생성
CREATE TABLE comics (
    cid BIGINT NOT NULL AUTO_INCREMENT,
    aid BIGINT NOT NULL,
    comic_name VARCHAR(255) NOT NULL,
    state VARCHAR(4) NOT NULL,
    created_dmt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dtm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (cid),
    FOREIGN KEY (aid) REFERENCES artist(aid)
);

-- stars(소비자가 좋아요한 작품) 테이블 생성
CREATE TABLE stars (
    mid BIGINT NOT NULL,
    cid BIGINT NOT NULL,
    created_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (mid, cid),
    FOREIGN KEY (mid) REFERENCES members(mid),
    FOREIGN KEY (cid) REFERENCES comics(cid)
);

-- order(작품구매내역) 테이블 생성
CREATE TABLE orders (
    oid BIGINT NOT NULL AUTO_INCREMENT,
    cid BIGINT NOT NULL,
    mid BIGINT NOT NULL,
    episod INT NOT NULL, -- 0은 작품 자체, 그 이상의 숫자는 회차(1화, 2화, ...)
    created_dmt DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_dtm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (oid),
    FOREIGN KEY (cid) REFERENCES comics(cid),
    FOREIGN KEY (mid) REFERENCES members(mid)
);

-- reward(리워드) 테이블 생성
CREATE TABLE reward (
    rid BIGINT NOT NULL AUTO_INCREMENT,
    reward_type VARCHAR(4),
    state VARCHAR(4),
    created_dmt DATETIME,
    update_dtm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (rid)
);

-- reward_history(리워드) 테이블 생성
CREATE TABLE reward_history (
    rid BIGINT NOT NULL,
    cid BIGINT NOT NULL,
    reward_type VARCHAR(4),
    created_dmt DATETIME,
    PRIMARY KEY (rid, cid),
    FOREIGN KEY (rid) REFERENCES reward(rid),
    FOREIGN KEY (cid) REFERENCES comics(cid)
);

-- DB 사용자 생성 & company DB에 모든 권한 부여
DROP USER IF EXISTS 'prdadmin'@'%';
CREATE USER 'prdadmin'@'%' IDENTIFIED BY 'prdpass';
GRANT ALL PRIVILEGES ON company.* TO 'prdadmin'@'%';
FLUSH PRIVILEGES;

-- 작가 생성
INSERT INTO artist (artist_name)
VALUES ('artist-a');
INSERT INTO artist (artist_name)
VALUES ('artist-b');

-- 작품 생성
INSERT INTO comics (aid, comic_name, state)
VALUES ((SELECT aid FROM artist WHERE artist_name = 'artist-a'), 'comic-1', '02');
INSERT INTO comics (aid, comic_name, state)
VALUES ((SELECT aid FROM artist WHERE artist_name = 'artist-a'), 'comic-2', '01');
INSERT INTO comics (aid, comic_name, state)
VALUES ((SELECT aid FROM artist WHERE artist_name = 'artist-b'), 'comic-3', '01');


-- 소비자 생성
INSERT INTO members (user_name)
VALUES ('user-a');
INSERT INTO members (user_name)
VALUES ('user-b');
INSERT INTO members (user_name)
VALUES ('user-c');
INSERT INTO members (user_name)
VALUES ('user-d');

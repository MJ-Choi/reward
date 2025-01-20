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
    episod INT,
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
    episod INT NOT NULL, -- 회차(1화, 2화, ...)
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
    reward_dmt DATETIME,
    collect_dmt DATETIME,
    created_dmt DATETIME,
    update_dtm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (rid)
);

-- reward_history(리워드) 테이블 생성
CREATE TABLE reward_history (
    rid BIGINT NOT NULL,
    cid BIGINT NOT NULL,
    user_type VARCHAR(2) NOT NULL,
    id BIGINT NOT NULL,
    rank VARCHAR(6) NOT NULL,
    point INT NOT NULL,
    created_dmt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rid, cid),
    FOREIGN KEY (rid) REFERENCES reward(rid),
    FOREIGN KEY (cid) REFERENCES comics(cid)
);

-- DB 사용자 생성 & company DB에 모든 권한 부여
DROP USER IF EXISTS 'prdadmin'@'%';
CREATE USER 'prdadmin'@'%' IDENTIFIED BY 'prdpass';
GRANT ALL PRIVILEGES ON company.* TO 'prdadmin'@'%';
FLUSH PRIVILEGES;

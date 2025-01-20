# 리워드 시스템
작품의 인기 순위를 기반으로 작가와 소비자에게 보상을 제공하는 시스템

# 1. 구조
## 1.1. 아키텍처
- `USER`: API를 이용하는 사용자로, 소비자, 작가, 관리자가 있다.
- `WEB`: API를 이용하는 사용자가 사용하는 WEB 서비스. 이 시스템에서는 구현하지 않으며 있다고 가정한다.
- `LoadBalancer`: 어플리케이션이 여러 대인 경우, 로드밸런서를 추가하여 요청을 분산. 서버가 한 대인 경우는 구조에서 제외한다.
- `Application(s)`: 이 프로젝트에서 구현하는 어플리케이션으로 아래 [API](#3-api) 기능을 지원한다.
- `Redis` : 어플리케이션에서 사용하는 저장소로 집계시 사용한다.
- `DB` : 어플리케이션에서 사용하는 저장소로 이용자가 실행하는 내역을 기록한다

```agsl
[USER] -> [WEB] -> [LoadBalancer] -> [Application]s -> [Redis]/[DB]
```

## 1.2. 어플리케이션 패키지 구조
어플리케이션 실행을 위한 관리영역과 서비스 영영으로 구분한다.

### 1.2.1. 관리 영역
- api: api에서 사용하는 에러와 응답 포맷 정의
- config: 어플리케이션에서 사용하는 인프라 환경정보와 어플리케이션 설정 정보 정의
- util: 인프라(레디스 등)를 사용하기 위한 기능이나 어플리케이션에서 공통적으로 사용하는 기능 모음
- entity: 어플리케이션 내에서 사용하는 DB 테이블 정보

### 1.2.2. 서비스 영역
도메인 별로 서비스를 구분한다. 각 서비스는 추후 다른 서비스로 분리될 수 있다.

#### (1) artist: 작가
- 작가가 작품을 관리하기 위한 서비스
- 작가는 웹서비스에서 생성되어 `artist` 테이블에 작가 정보가 정의된 것을 전제로 한다.
- 작가가 등록한 작품정보는 `comics` 테이블에서 관리
- 작품 등록 시, DB에 데이터를 저장한다. 이 때 레디스 사용 시 데이터를 입력 받거나 수정할 때 레디스를 같이 갱신한다.
- 작품 정보 수정은 빈번하지 않을 것으로 고려하여 배치로 동기화 하지 않고 데이터 인입시마다 동기화한다.

#### (2) comic: 작품
- 소비자가 작품을 이용하기 위한 서비스
- 소비자는 웹서비스에서 생성되어 `members` 테이블에 소비자 정보가 정의된 것을 전제로 한다.
- 소비자는 작품을 조회하거나 좋아요를 설정할 수 있다.
- 집계방식
  - 집계는 회원만을 집계하며, 비회원은 집계하지 않는다.
  - 조회수: 웹서비스에 작품 데이터를 제공하면서 조회된 작품의 조회수를 레디스(`RedisCountCollector.java`)와 `orders` 테이블 저장한다.
  - 좋아요수: 좋아요를 누르면 `star` 테이블에 좋아요를 누른 회원과 작품을 저장하고, 좋아요 클릭수는 레디스에 저장한다. (좋아요는 먼저 조회한 후에 사용함을 전제로 개발)

#### (3) reward: 리워드
- 관리자가 리워드를 정의하고, 작가와 소비자가 지급받은 보상을 조회
- 관리자가 리워드를 요청하면 `reward` 테이블에 데이터가 생성
- 관리자가 리워드를 지급하면 `reward_history` 테이블에 지급받은 작가와 소비자를 저장
- 집계기준
  - 일간: 선택된 날짜에 대한 집계 (예시: 2025-01-10 선택 시, 2025-01-10 에 대한 데이터)
  - 주간: 선택된 날짜에서 그 주 월요일부터의 데이터 합계 (예시: 2025-01-10 선택 시, 2025-01-06(월) ~ 2025-01-10(금) 에 대한 데이터)
  - 월간: 선택된 날짜의에서 1일부터 현재까지의 데이터 (예시: 2025-01-10 선택 시, 2025-01-01 ~ 2025-01-10 에 대한 데이터)
  - 연간: 선택된 날짜에서 1월1일부터 현재까지의 데이터 (예시: 2025-01-10 선택 시, 2025-01-01 ~ 2025-02-10 에 대한 데이터)
  - 상위 10위권만을 조회할 때, 하위권에서도 동점이라면 하위권도 리워드를 지급 (예시: 10위 ~ 12위의 점수가 같다면 12위까지도 리워드를 지급)

## 1.3. 데이터 구조
### 1.3.1. 작가
- 테이블 명: artist
- 역할: 작가 정보. 이 환경에서 작가는 웹서비스에서 생성되었다고 가정하며, 작가 정보를 조회하는 용도로만 사용.

|colum| data type | desc       |
|--|-----------|------------|
|aid|bigint| (PK) 작가ID  |
|artist_name|varchar| 작가명        |
|created_dtm|timestamp| 작가 등록일     |
|updated_dtm|timestamp| 작가 데이터 변경일 |

### 1.3.2. 작품
- 테이블 명: comics
- 역할: 작품 정보

| colum       | data type | desc                                              |
|-------------|-----------|---------------------------------------------------|
| cid         |bigint| (PK) 작품ID                                         |
| aid         |bigint| (FK) 작가ID (artist)                                |
| comic_name  |varchar| 작품명                                               |
| state       |varchar| 작품 상태(01: 오픈 예약/02: 연재중/03: 휴재/04: 완결/05: 서비스 종료) |
| created_dtm |timestamp| 작품 등록일                                            |
| updated_dtm |timestamp| 작품 데이터 변경일                                        |

### 1.3.3. 소비자(회원)
- 테이블 명: members
- 역할: 회원 정보. 이 환경에서 회원은 웹서비스에서 생성되었다고 가정하며, 회원정보를 조회하는 용도로만 사용.

| colum     | data type | desc      |
|-----------|-----------|-----------|
| mid       |bigint| (PK) 회원ID |
| user_name |varchar| 회원명       |

### 1.3.4. 주문
- 테이블 명: orders
- 역할: 회원의 작품 구매 내역. 이 환경에서 가격은 고려하지 않으며 작품조회 적재용으로 사용

| colum       | data type | desc                        |
|-------------|-----------|-----------------------------|
| oid         | bigint    | (PK) 주문ID                   |
| cid         | bigint    | (FK) 작품ID                   |
| mid         | bigint    | (FK) 회원ID                   |
| episod      | int       | 회차 (0은 작품 자체, 그 이상의 숫자는 회차) |
| created_dtm | timestamp | 주문 생성일                      |
| updated_dtm | timestamp | 주문 데이터 변경일                  |
### 1.3.5. 리워드
- 테이블 명: reward
- 역할: 리워드 정보

| colum       | data type | desc              |
|-------------|-----------|-------------------|
| rid         |bigint| (PK) 리워드ID        |
| reward_type |varchar| 리워드 유형(D: 일간/W: 주간/M: 월간/Y: 연간) |
| state       |varchar| 리워드 상태(01: 요청 생성/02: 처리 완료/10: 실패/11: 취소) |
| reward_dtm  |timestamp| 리워드 지급 대상 날짜      |
| collect_dtm |timestamp| 요청 시점(집계 시작일)     |
| created_dtm |timestamp| 리워드 등록일           |
| updated_dtm |timestamp| 리워드 데이터 변경일       |

### 1.3.6. 리워드 내역
- 테이블 명: reward_history
- 역할: 리워드 지급 내역

| colum       | data type | desc                |
|-------------|-----------|---------------------|
| rid         |bigint| (PK) 작가ID           |
| cid         |bigint| (PK) 작품ID           |
| user_type   |varchar| 회원 타입(a: 작가/m: 소비자) |
| id          |bigint| 작가 혹은 소비자 id        |
| created_dtm |timestamp| 리워드 지급일             |
### 1.3.7. 좋아요
- 테이블 명: star
- 역할: 회원이 좋아요를 선택한 작품 내역

| colum       | data type | desc       |
|-------------|-----------|------------|
| mid         |bigint| (PK) 회원ID  |
| cid         |bigint| (PK) 작품ID  |
| created_dtm |timestamp| 작가등록일      |

# 2. 개발환경
- jdk 17
- spring 3.4.1
- redis (컨테이너를 이용)
- mariadb (컨테이너를 이용)
ㅋ
## 2.1. 환경설정
이 프로젝트 어플리케이션을 실행하고 테스트 하기 위한 컨테이너 구조
```agsl
container/
├── prd-compose.yml
├── init.sql
└── test-init.sql
```
- `prd-compose.yml`: 리워드 시스템을 구동하기 위한 환경을 컨테이너로 설정
- `init.yml`: 리워드 시스템에서 사용하는 DB의 초기 정보
- `test-init.yml`: 테스트 코드를 실행 시 사용하는 DB의 초기 정보


### 2.1.1. 컨테이너 생성
어플리케이션 테스트를 위한 client ui 컨테이너의 베이스 이미지 생성.
streamlit 라이브러리를 포함하는 파이썬 컨테이너 이미지를 로컬에 생성.

> ** 한 번 생성 후, 이후에는 실행하지 않아도 된다.

```agsl
$ cd container/streamlit_img/
$ docker build -t streamlit/streamlit:latest .
# docker images 
REPOSITORY            TAG        IMAGE ID       CREATED              SIZE
streamlit/streamlit   latest     e7319ac6d6b2   About a minute ago   621MB

```
### 2.1.2. 컨테이너 실행
어플리케이션에서 사용하는 DB와 테스트를 위한 client ui 컨테이너를 실행

```agsl
$ cd container/
$ docker-compose -f prd-compose.yml up -d
```

레디스 모니터링을 위한 GUI
웹 브라우저에서 아래와 같이 입력 후 실행

> http://localhost:5540

## 2.2. 프로젝트 환경 설정
`/resources/application.yml` 에서 설정을 변경하여 테스트 가능
```yaml
spring:
  datasource: #database 연동 정보
    ...
redis: #redis 연동정보
  ...
# 어플리케이션 설정 정보
app:
  init-artist: true # 작가 초기 정보 등록 여부(true: 등록, false: 미등록)
  init-comics: true # 작품 초기 정보 등록 여부(true: 등록, false: 미등록)
  init-member: true # 소비자(회원) 초기 정보 등록 여부(true: 등록, false: 미등록)
```

## 2.3. 테스트
1) 컨테이너 실행
2) 소스 코드 빌드
3) bootRun

# 3. API
이 프로젝트에서 개발하는 구현 범위 

집계까지 구현완료. 집계된 내용은 작품 조회 시 확인 가능하나 랭킹은 구현하지 못함.
회원에게 지급은 구현하지 못함.

| 구현 및 테스트   | user | method | uri                          | description        |
|------------|------|--------|------------------------------|--------------------|
| 구현, 테스트 완료 | user      | GET    | /comic/{comics}              | 소비자가 작품 조회         |
| 구현, 테스트 완료 | user       | POST   | /comic/{comics}              | 소비자가 작품에 '좋아요'를 설정 |
| 구현, 테스트 완료 | artist     | GET    | /artist/{comics}             | 작가가 작품별 데이터 확인     |
| 구현, 테스트 완료 | artist     | POST   | /artist                      | 작가가 작품 등록          |
| 테스트 미완     | artist     | GET    | /reward/history?t=a&id={aid} | 리워드 지급 내역 조회       |
| 테스트 미완     | user       | GET    | /reward?/historyt=m&id={mid} | 리워드 지급 내역 조회       |
| 테스트 미완     | admin      | POST   | /reward                      | 운영자가 리워드 요청 생성     |
| 구현 미완      | admin      | POST   | /reward/{rid}                | 운영자가 리워드 지급 요청     |
| 테스트 미완     | admin      | GET    | /reward/history              | 리워드 지급 내역 조회       |

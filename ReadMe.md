# 리워드 시스템
작품의 인기 순위를 기반으로 작가와 소비자에게 보상을 제공하는 시스템

# 1. 구조
- `USER`: API를 이용하는 사용자로, 소비자, 작가, 관리자가 있다.
- `WEB`: API를 이용하는 사용자가 사용하는 WEB 서비스. 이 시스템에서는 구현하지 않으며 있다고 가정한다.
- `LoadBalancer`: 어플리케이션이 여러 대인 경우, 로드밸런서를 추가하여 요청을 분산. 서버가 한 대인 경우는 구조에서 제외한다.
- `Application(s)`: 이 프로젝트에서 구현하는 어플리케이션으로 아래 [API](#3-api) 기능을 지원한다.
- `Redis` : 어플리케이션에서 사용하는 저장소로 집계시 사용한다.
- `DB` : 어플리케이션에서 사용하는 저장소로 이용자가 실행하는 내역을 기록한다
```agsl
[USER] -> [WEB] -> [LoadBalancer] -> [Application]s -> [Redis]/[DB]
```

# 2. 개발환경
- jdk 17
- spring 3.4.1
- redis (컨테이너를 이용)
- mariadb (컨테이너를 이용)

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

## 2.3. 테스트
`postman` 폴더의 json을 포스트맨에 import하여 테스트

# 3. API
이 프로젝트에서 개발하는 구현 범위 

| user | method | uri                  | description       |
|------|--------|----------------------|-------------------|
| user | GET    | /comic/{comics}      | 소비자가 작품 조회        |
| user | POST   | /comic/{comics}      | 소비자가 작품에 '좋아요'를 설정 |
| artist | GET    | /artist/{comics}     | 작가가 작품별 데이터 확인    |
| artist | POST   | /artist              | 작가가 작품 등록         |
| artist | GET    | /reward?t=a&id={aid} | 리워드 지급 내역 조회       |
| user | GET    | /reward?t=m&id={mid} | 리워드 지급 내역 조회       |
| admin | POST   | /reward              | 운영자가 리워드 지급 요청    |
| admin | GET    | /reward/history      | 리워드 지급 내역 조회      |

# Chronicle

## About
Chronicle은 MSA 환경에서 발생하는 로그를 안정적으로 수집하여, 서비스 성능에 영향을 최소화하면서도 대량 로그를 효율적으로 검색할 수 있도록 설계된 백엔드 시스템입니다.  
SDK를 통해 각 마이크로서비스에서 발생하는 로그를 손쉽게 중앙 시스템으로 수집하고, 대시보드를 통해 빠르게 조회하는 것을 목표로 합니다.

## Skills
- Language: Java 21
- Framework: Spring Boot 4.0
- Database: MySQL 8.0
- Test & Monitoring: k6, Docker, Prometheus, Grafana

## Architecture
<img width="1132" height="758" alt="chronicle drawio" src="https://github.com/user-attachments/assets/2bf7a208-9227-42fe-9ba3-02c618acd696" />

## Component

### 1. SDK(chronicle-sdk)
- Java 애플리케이션용 로그 수집 클라이언트 라이브러리
- 비동기 배치 전송으로 애플리케이션 성능 영향 최소화
- 자동 재시도 및 로컬 버퍼링 지원
- Maven/Gradle 의존성으로 간편 연동

### 2. API Server(chronicle-server)
- Spring Boot 기반 로그 수신 REST API
- 로그 유효성 검증 및 정규화
- MySQL 저장 및 조회 API 제공

### 3. Database(MySQL)
- 로그 데이터 저장
- 인덱싱을 통한 검색 최적화
- 파티셔닝을 통한 대용량 데이터 관리
- 보존 기간 기반 데이터 정리

### 4. Dashboard
- 실시간 로그 모니터링
- 검색 및 필터링
- 시각화 및 알림

## 주요 기능 요구사항

### 수집(Ingestion)
- 초당 10,000건 이상의 로그 수집 처리
- 다양한 로그 포맷 지원(JSON, Plain Text)
- SDK를 통한 구조화된 로그 전송
- 네트워크 장애 시 로컬 버퍼링

### 저장(Storage)
- 로그 데이터 압축 저장
- 보존 기간 기반 자동 삭제/아카이빙
- 샤딩 전략을 통한 수평 확장

### 검색(Search)
- 전문 검색(Full-text Search)
- 시간 범위 기반 필터링
- 필드 기반 쿼리
- 검색 응답 시간 1초 이내

### 모니터링(Monitoring)
- 실시간 로그 스트리밍
- 커스텀 대시보드 구성
- 알림 규칙 설정

## 프로젝트 구조
```
chronicle/
├── chronicle-sdk/           # 클라이언트 SDK
├── chronicle-server/        # API 서버
├── chronicle-dashboard/     # 웹 대시보드
├── chronicle-common/        # 공통 모듈
└── docs/                    # 문서
```

## 마일스톤

### Phase 1: MVP
- SDK 기본 구현 (로그 전송, 배치 처리)
- API Server 기본 구현 (로그 수신 및 MySQL 저장)
- 로그 조회 API

### Phase 2: 검색 및 Dashboard
- 다양한 조건의 로그 검색 API
- 기본 Dashboard 구현
- 로그 레벨, 시간 범위 필터링

### Phase 3: 운영 기능
- 로그 보존 정책 및 자동 삭제
- 알림 기능
- 성능 최적화 (인덱스, 파티셔닝)

### Phase 4: 확장 (필요시)
- 메시지 큐 도입 (Kafka)
- 검색엔진 도입 (Elasticsearch)
- 다른 언어 SDK 지원

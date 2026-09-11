# Carrot Backend

당근의 중고거래 기능을 참고하여 Java와 Spring Boot를 복습하는
학습용 백엔드 프로젝트입니다.

## 학습 목표

- HTTP 요청과 JSON 응답 흐름 이해
- Controller와 Service의 역할 분리
- 생성자 주입을 통한 의존성 주입(DI) 이해
- 상품 API 구현과 입력값 검증
- Git과 GitHub를 활용한 변경 이력 관리

## 개발 환경

- Java 21
- Spring Boot — 정확한 버전은 build.gradle 참고
- Gradle / Groovy DSL
- Spring Web
- IntelliJ IDEA
- Git Bash
- Spring Data JPA / Hibernate
- H2 — 개발 시 파일 DB, 테스트 시 메모리 DB
- Bean Validation
- JUnit 5 / Mockito / MockMvc

## 현재 구현 기능

- 상품 목록·상세 조회
- 상품 등록·수정·삭제
- H2 파일 DB 저장 및 서버 재시작 후 데이터 유지
- Bean Validation을 통한 등록·수정 입력 검증
- 입력 오류·요청 형식 오류·상품 없음에 대한 공통 오류 응답
- Controller 테스트 및 상품 CRUD 통합 테스트

## API

| Method | Path | 기능 | 성공 응답 |
|---|---|---|---|
| GET | /api/products | 상품 목록 조회 | 200 |
| GET | /api/products/{id} | 상품 상세 조회 | 200 |
| POST | /api/products | 상품 등록 | 201 |
| PUT | /api/products/{id} | 상품 수정 | 200 |
| DELETE | /api/products/{id} | 상품 삭제 | 204 |

### 상품 등록 요청 예시

```json
{
  "title": "무선 키보드",
  "price": 15000,
  "location": "잠실동"
}
```

상품 ID는 서버에서 생성합니다.

### 상품 수정

제목, 가격, 지역을 모두 전달합니다.
가격은 0 이상이어야 하며, 생략하거나 null로 보내면 400을 반환합니다.

### 상품 삭제

DB에서 상품을 실제로 삭제합니다.
성공 시 응답 본문 없이 204를 반환합니다.
존재하지 않는 상품을 수정하거나 삭제하면 404를 반환합니다.

## 입력 검증

등록·수정 요청 DTO에 Bean Validation을 적용합니다.
Controller의 @Valid를 통해 요청을 검증합니다.

- 제목·지역: @NotBlank
- 가격: @NotNull, @PositiveOrZero
- 가격 0은 허용합니다.
- 검증 실패 시 400과 필드별 오류 메시지를 반환합니다.

Service를 직접 호출하는 경우에는 Controller의 검증이 실행되지 않습니다.

## 코드 구조

- Product: 상품 API 응답용 record
- CreateProductRequest: 상품 등록 요청 DTO와 입력 검증 규칙
- UpdateProductRequest: 상품 수정 요청 DTO와 입력 검증 규칙
- ProductEntity: products 테이블과 연결되는 JPA Entity
- ProductRepository: 상품 데이터 저장·조회
- ProductService: 상품 CRUD 처리와 트랜잭션 관리
- ProductController: HTTP 요청 매핑과 요청 DTO 검증 실행
- ApiErrorResponse / FieldViolation: 공통 오류 응답 DTO
- GlobalExceptionHandler: 예외를 공통 HTTP 오류 응답으로 변환

요청 처리 흐름:

Controller → Service → Repository → H2 DB

의존성은 생성자로 주입합니다.
Service는 상품이 없을 때 ResponseStatusException을 발생시키며,
GlobalExceptionHandler가 이를 404 오류 응답으로 변환합니다.

## 오류 응답

다음 오류를 GlobalExceptionHandler에서 공통 형식으로 처리합니다.

| 오류 | HTTP 상태 |
|---|---|
| 입력값 검증 실패 | 400 |
| JSON 문법·본문 타입 변환 오류 | 400 |
| URL 파라미터 타입 변환 오류 | 400 |
| 존재하지 않는 상품 | 404 |

입력 검증 실패 예시:

```json
{
  "status": 400,
  "message": "입력값을 확인해주세요.",
  "errors": [
    {
      "field": "price",
      "message": "가격은 필수입니다."
    }
  ]
}
```

현재 모든 예외 종류를 공통 형식으로 처리하는 것은 아닙니다.

## 테스트

### 실행

프로젝트 루트의 Git Bash에서 실행합니다.

전체 테스트와 코드 스타일 검사:

```bash
./gradlew test checkstyleMain checkstyleTest
```

Controller 테스트만 실행:

```bash
./gradlew test --tests "com.example.carrot.product.ProductControllerTest"
```

Service 통합 테스트만 실행:

```bash
./gradlew test --tests "com.example.carrot.product.ProductServiceIntegrationTest"
```

### 테스트 범위

- ProductControllerTest
    - 입력 검증과 요청 형식 오류
    - 상세 조회 성공 및 404 응답
    - 잘못된 요청의 Service 미호출 확인
    - 실제 Service 대신 mock 사용

- ProductServiceIntegrationTest
    - 실제 Service·Repository·H2를 사용한 상품 CRUD
    - flush 후 clear하여 DB 재조회 결과 확인
    - 테스트 트랜잭션은 종료 시 롤백

- CarrotBackendApplicationTests
    - 애플리케이션 컨텍스트 시작 확인

### 테스트 DB

DB를 사용하는 위 테스트들은 @ActiveProfiles("test")를 적용합니다.
설정 파일은 src/test/resources/application-test.properties입니다.

개발용 파일 DB와 분리된 H2 메모리 DB를 사용합니다.
메모리 DB 설정만으로 테스트 메서드마다 데이터가 초기화되는 것은 아닙니다.

### 보고서

- 테스트: build/reports/tests/test/index.html
- Checkstyle: build/reports/checkstyle/

## 실행 방법

JDK 21을 설치하고 프로젝트 루트에서 실행합니다.

Windows Git Bash:

```bash
./gradlew bootRun
```

또는 IntelliJ에서 CarrotBackendApplication의 main 메서드를 실행합니다.

서버 실행 후 접속:

http://localhost:8080/api/products

### 개발용 DB

H2 파일 DB를 사용합니다.
프로젝트 루트를 작업 폴더로 실행하면 data/ 아래에 DB 파일이 생성됩니다.
서버를 재시작해도 상품은 유지되며, data/는 Git에서 제외합니다.

H2 Console: http://localhost:8080/h2-console

- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:file:./data/carrot;DB_CLOSE_ON_EXIT=FALSE
- User Name: sa
- Password: 비워두기

위 접속 정보와 H2 Console은 로컬 개발용 설정입니다.

## 코드 스타일

네이버 캠퍼스 핵데이 Java 코딩 컨벤션을 기준으로 합니다.

### 편집기 설정

- 기본 편집 규칙: `.editorconfig`
- IntelliJ 포매터: `config/style/naver-intellij-formatter.xml`
- IntelliJ의 Code Style → Import Scheme에서 해당 XML을 불러옵니다.
- 저장 시 Reformat code와 Optimize imports를 사용합니다.
- Java 파일의 줄바꿈은 LF로 통일합니다.

### Checkstyle 검사

- Checkstyle 버전: `10.26.1`
- 규칙 파일: `config/checkstyle/naver-checkstyle-rules.xml`
- 검사 제외 설정: `config/checkstyle/naver-checkstyle-suppressions.xml`
- 경고가 남아 있으면 검사에 실패합니다.

프로젝트 루트의 Git Bash에서 실행합니다.

```bash
./gradlew checkstyleMain checkstyleTest
```

운영 코드와 테스트 코드의 스타일을 검사합니다.
테스트 자체를 실행하는 명령은 아닙니다.

HTML 보고서:
- `build/reports/checkstyle/main.html`
- `build/reports/checkstyle/test.html`

공식 가이드: [네이버 Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)

## 현재 제한 사항

- 운영 DB 연결과 배포는 아직 진행하지 않았습니다.
- 로그인, 이미지 업로드, 채팅, 프론트엔드는 아직 구현하지 않았습니다.
- 개발용 테이블 변경에는 ddl-auto=update를 사용합니다.
- 공통 오류 응답은 현재 등록된 예외 처리 범위에만 적용됩니다.

## 다음 단계

- 테스트 실행 환경 정리
- 프론트엔드 연결

## 학습 진행 규칙

- 시작 시각을 기록하고 하루 약 2시간 동안 학습
- 기능 구현 후 동작을 확인하고 단계별 커밋
- 매일 종료 시 기록 작성
- 프로젝트 완료 시 Velog 회고와 README 최종 정리
- 다음 프로젝트는 기존 학습 내용을 확장하는 방향으로 선정

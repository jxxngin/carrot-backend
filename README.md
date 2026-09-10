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

## 현재 구현 기능

- 상품 목록 조회
- 상품 상세 조회
- 상품 등록
- 제목·지역의 빈 문자열 및 음수 가격 검사
- 존재하지 않는 상품에 대한 404 응답

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

## 코드 구조

- Product: 상품 데이터를 표현하는 record
- CreateProductRequest: 등록 요청 데이터를 받는 DTO
- ProductController: HTTP 요청 매핑과 응답 구성
- ProductService: 상품 조회·등록 및 입력 검사
- UpdateProductRequest: 상품 수정 요청 DTO

Controller는 생성자를 통해 ProductService를 주입받습니다.
현재 예외 처리는 ResponseStatusException을 사용합니다.

## 실행 방법

JDK 21을 설치하고 프로젝트 루트에서 실행합니다.

Windows Git Bash:

```bash
./gradlew.bat bootRun
```

또는 IntelliJ에서 CarrotBackendApplication의 main 메서드를 실행합니다.

서버 실행 후 접속:

http://localhost:8080/api/products

## 현재 제한 사항

- 데이터는 메모리에 저장되며 서버 재시작 시 초기화됩니다.
- DB, 로그인, 이미지 업로드, 채팅은 아직 구현하지 않았습니다.
- price는 int 타입이므로 입력 생략과 0을 구분하지 못합니다.
  필수값 검증은 추후 보완할 예정입니다.
- 프론트엔드 연결과 배포는 아직 진행하지 않았습니다.

## 다음 단계

- 등록 요청의 가격 누락 검증 개선
- 입력 검증 및 예외 응답 형식 정리
- 프론트엔드 연결

## 학습 진행 규칙

- 하루 약 2시간 단위로 목표와 완료 기준 설정
- 기능 구현 후 동작을 확인하고 커밋
- 매일 학습 종료 시 Velog 기록 작성
- 프로젝트 완료 시 README 정리 및 다음 확장 프로젝트 선정
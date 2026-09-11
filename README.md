# 붉은 달의 성채

Spring Boot로 게임 생성, 진행 저장, 목록·상세 조회를 제공하는 프로젝트입니다.

## 개발 환경

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA
- MySQL
- Gradle Wrapper

## 실행 준비

1. Java 21을 설치합니다.
2. MySQL 서버를 실행합니다.
3. 사용할 데이터베이스를 생성합니다.

```sql
CREATE DATABASE IF NOT EXISTS gameBasic;
```

## 환경 변수

`src/main/resources/application.properties`는 다음 환경 변수를 사용합니다.

| 변수 | 설명 | 예시 |
|---|---|---|
| DB_URL | MySQL JDBC 접속 주소 | jdbc:mysql://localhost:3306/gameBasic |
| DB_USERNAME | MySQL 사용자 이름 | root |
| DB_PASSWORD | MySQL 사용자 비밀번호 | 본인 환경에서 설정 |

실제 비밀번호는 저장소에 작성하지 않습니다.

## 실행 방법

프로젝트 폴더의 PowerShell에서 실행합니다.
아래 사용자 이름과 비밀번호는 본인 MySQL 계정에 맞게 입력합니다.

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/gameBasic'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = '본인_MySQL_비밀번호'

.\gradlew.bat bootRun
```

서버 실행 후 브라우저에서 http://localhost:8080 에 접속합니다.

IntelliJ 실행 버튼을 사용하는 경우에는 해당 실행 구성의
Environment variables에 위 세 환경 변수를 등록합니다.




Controller, Service, Repository는 각각 어떤 역할을 맡나요?
요청 처리·업무 처리·DB 접근을 나눠 맡습니다

@Service를 붙이지 않으면 서버가 뜨지 않는 이유는 무엇인가요?
@Service는 컴포넌트 스캔 대상에 있는 클래스를 **스프링이 관리하는 객체인 빈(Bean)**으로 자동 등록하게 합니다. 컨트롤러는 이렇게 등록된 GameService를 주입받습니다.

@Transactional(readOnly = true)는 무슨 뜻이며, 저장하는 메서드에 붙이면 왜 안 되나요?
조회 전용 트랜잭션으로 처리하겠다는 설정입니다.

@NotBlank, @NotNull, @NotEmpty는 각각 어떤 값을 걸러내나요?
@NotNull : 	값의 존재 여부만 확인
@NotEmpty : 길이(size)가 0보다 커야 함
@NotBlank : 앞뒤 공백을 제거(trim)한 후 길이가 0보다 커야 함

엔티티를 그대로 응답하지 않고 DTO로 바꿔서 응답하는 이유는 무엇인가요?
필요한 정보만 공개 할 수 있으며 DB 구조와 API 응답 구조를 독립적으로 관리할 수 있습니다.

이름 변경에서 save()를 호출하지 않았는데 DB에 반영되는 이유는 무엇인가요?
JPA의 더티 체킹 때문입니다.
JPA는 관리 중인 객체의 변경을 감지하고, 보통 트랜잭션 커밋 전에 변경 내용을 DB와 동기화하는 플러시(flush) 과정에서 UPDATE SQL을 실행합니다. 그래서 명시적인 save() 호출 없이도 반영됩니다.

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
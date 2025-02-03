# Week4 일정 관리 앱 서버 구현 과제

## About Project

| 구분    | 내용                                                   |
|-------|------------------------------------------------------|
| 목적    | SpringMVC와 JDBC를 통해 DB에 데이터를 저장하는 Web API를 구현할 수 있다. |
| 기간    | 2025.1.27 ~ 2025.2.3                                 |
| 설명    | User와 Schedule Entity로 구성된 일정 관리 앱의 백엔드 구성.          |
| 사용 기술 | Java, SpringMVC, JDBC template                       |
| JDK   | Amazon Corretto 17.0.13 - aarch64                    |

<br/><br/>

## ERD & API 명세

### ERD

<img src="https://github.com/yeonic/nbc-w4-assignment/blob/dev/resources/0_erd.png" alt="erd">

<br/>

### API 명세

- Swagger
- 서버 실행 후 Localhost로 실행(http://localhost:8080/swagger-ui/index.html#/)

<br/><br/>

## Features

### CRUD

| Entity   | 작업     | Method 이름          | 설명                                                                  |
|----------|--------|--------------------|---------------------------------------------------------------------|
| User     | Create | addUser            | 이름, 이메일을 입력받아 User 생성                                               |
| Schedule | Create | addSchedule        | 작성자 ID, 할일, 비밀번호를 입력받아 Schedule 생성                                  |
| Schedule | Read   | schedulesByOptions | Query Parameter 날짜, 작성자명, 페이지 크기(필수), 인덱스(필수)를 받아 Schedule 검색       |
| Schedule | Read   | schedule           | Schedule ID(path variable)로 Schedule 1개 검색                          |                                               
| Schedule | Update | update             | Schedule ID(path variable)와 할일, 작성자명, 비밀번호(필수)를 입력받아, 입력받은 필드를 업데이트 |                                               
| Schedule | Delete | delete             | Schedule ID(path variable)와 비밀번호(필수)를 입력받아 Schedule 삭제              |                                                                  |                                               

<br/>

### Pagination

- Offset Pagination으로 구현
- 현재로서는 자료의 개수가 많아질 일이 없어, 구현의 복잡도를 낮추는 것을 선택함
- Schedule의 updated_at을 기준으로 내림차순으로 정렬, offset과 limit 구문을 이용해 pagination 구현

```java
// ScheduleRepository
public List<ScheduleWithUsername> findByOptions(
    Timestamp updatedAt, String writerName, Paging pagingReq
) {
  List<Object> params = new ArrayList<>();
  StringBuilder sb = new StringBuilder(
      "select s.*, u.name from schedule as s inner join user as u on s.user_id = u.user_id where 1=1");

  // Some Logics

  sb.append(" order by s.updated_at desc");

  sb.append(" limit ? offset ?");
  params.add(pagingReq.getPageSize());
  params.add(pagingReq.getOffset());

  String sql = sb.toString();
  return template.query(sql, (rs, rowNum) -> new ScheduleWithUsername(
      rs.getLong("schedule_id"),
      rs.getLong("user_id"),
      rs.getString("name"),
      rs.getString("todo"),
      rs.getTimestamp("created_at").toLocalDateTime(),
      rs.getTimestamp("updated_at").toLocalDateTime()
  ), params.toArray(new Object[params.size()]));
}
```

<br/>

- Pagination에 필요한 값들을 편하게 관리할 Paging 객체
- 음수 범위의 페이지 요청이 들어오지 못하도록 생성자에서 처리
- Offset 계산 메서드 제공

```java
// Paging 객체
@Getter
public class Paging {

  private int pageSize;
  private int pageNum;

  public Paging(int pageSize, int pageNum) {
    this.pageSize = Math.max(pageSize, 1);
    this.pageNum = Math.max(pageNum, 1);
  }

  public int getOffset() {
    return (pageNum - 1) * pageSize;
  }
}

```

<br/>

### Exception Handling

- Spring AOP의 @RestControllerAdvice를 이용한 에러 공통 처리
- @ExceptionHandler의 value에 에러 타입 객체(Class<? extends Throwable>)를 넘겨주어, 에러별로 처리
- 처리되지 않은 에러는 Exception handler로 처리
- @ResponseStatue를 통한, 에러 Http Status 명시

```java

@RestControllerAdvice
public class GlobalExceptionHandler {

  // ...

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalStateException(IllegalStateException e) {
    log.error("handleIllegalStateException", e);
    return ErrorResponse.of(ErrorCode.ILLEGAL_STATE, e.getMessage());
  }

  // ...

  // default handling Exception
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleException(Exception e) {
    log.error("handleException");
    return ErrorResponse.of(ErrorCode.EXCEPTION, e.getMessage());
  }
}
```

<br/>

- ErrorResponse에서 활용할 ErrorCode Enum 구현
- 에러 코드와 에러 메시지에 접근 가능

```java

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public enum ErrorCode {
  // Common
  ARGUMENT_NOT_VALID("COMMON_001", "method argument not valid"),

  // Standard
  ILLEGAL_STATE("STANDARD_001", "illegal state"),
  ILLEGAL_ARGUMENT("STANDARD_002", "illegal argument"),

  // Data Access
  DATA_ACCESS_EXCEPTION("DATA_001", "error accessing data"),

  // Finally
  EXCEPTION("EXCEPTION", "exception");

  private final String code;
  private final String message;
}
```

<br/>

### Validation

- DTO에 Validation 규칙을 어노테이션으로 규정
- Controller에서 검증할 Parameter에 @Valid 어노테이션 추가

```java
// dto
@Getter
@NoArgsConstructor
public class AddScheduleRequest {

  @NotBlank
  private Long userId;

  @NotBlank(message = "할일을 입력해주세요.")
  @Size(max = 200, message = "할일은 최대 200자까지 입력 가능힙니다.")
  private String todo;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  private String password;
}
```

- Validation 규칙을 어긴 입력이 들어오면 MethodArgumentNotValidException 발생
- ExceptionHandler에서 에러 객체의 getBindingResult를 이용해 설정한 메시지를 불러올 수 있음

```java

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("handleMethodArgumentNotValidException", e);
    BindingResult bindingResult = e.getBindingResult();
    return ErrorResponse.of(ErrorCode.ARGUMENT_NOT_VALID, bindingResult);
  }

  // ...

}
```

<br/><br/>

## Structure

### SRP

이번 프로젝트에서 각 Layer간의 책임을 정리하고 명확하게 함

- Controller
    - Request Param, Request Body, Path Variable 등을 처리하여 Service 객체에 전달
    - Service에서 처리된 데이터를 Response로 포맷하여 제공
- Service
    - 데이터 검증, 비즈니스 로직을 수행
    - Repository를 통한 데이터 접근, 가공
    - DTO를 통해 Controller와 소통
- Repository
    - Query를 생성하고 DB에 접근, 데이터 관련 작업 수행
    - Repository는 DTO 없이 Entity로 Service와 소통

### DTO - Entity간 Mapper

- Mapper 라이브러리들은 많지만, 프로젝트 규모를 봤을 때, 직접 구현하는 것이 낫다고 판단
- Lombok의 @Builder 어노테이션을 DTO에 추가하고, builder pattern을 활용해서 Entity를 DTO로 변환

```java
public class ScheduleMapper {

  public static GetScheduleResponse toGetResponseDto(Schedule schedule) {
    return GetScheduleResponse.builder()
        .scheduleId(schedule.getScheduleId())
        .userId(schedule.getUserId())
        .todo(schedule.getTodo())
        .build();
  }

  // ...
}
```

### ErrorResponse

- Factory 패턴으로 Response를 생성할 수 있도록 구현
- Nested Class FieldErrorMapper를 활용해서, 필요한 정보만 넘겨줄 수 있도록 함
- Validation 에러 / 그 외의 RuntimeError를 모두 지원하도록 오버라이딩 된 of 메서드 구현

```java

@Getter
public class ErrorResponse {

  private String code;
  private String message;
  private List<FieldErrorWrapper> errors;

  private ErrorResponse(ErrorCode code, List<FieldError> errors) {
    this.code = code.getCode();
    this.message = code.getMessage();
    this.errors = errors.stream().map(FieldErrorWrapper::new).toList();
  }

  private ErrorResponse(ErrorCode code, String exceptionMessage) {
    this.code = code.getCode();
    this.message = code.getMessage();
    this.errors = List.of(new FieldErrorWrapper("", "", exceptionMessage));
  }

  public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
    return new ErrorResponse(errorCode, bindingResult.getFieldErrors());
  }

  public static ErrorResponse of(ErrorCode errorCode, String exceptionMessage) {
    return new ErrorResponse(errorCode, exceptionMessage);
  }

  @Getter
  static class FieldErrorWrapper {

    private final String field;
    private final String rejectedValue;
    private final String reason;

    private FieldErrorWrapper(String field, String rejectedValue, String reason) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.reason = reason;
    }

    private FieldErrorWrapper(FieldError error) {
      field = error.getField();
      reason = error.getDefaultMessage();

      Object rejectedValueObject = error.getRejectedValue();
      rejectedValue = rejectedValueObject == null ? "" : rejectedValueObject.toString();
    }
  }
}
```
## 예외
예외표는 노션 참조, 더 필요할 경우 비슷하게 만들어서 사용하면 됨
## 팩토리
각자 필요한 예외를 사용할 때는 팩토리를 불러서 사용하면 됨. final에 static으로 만들었음
따라서 사용할 때는 AccountFactory.accountException(String, HttpStatus) 형식으로 사용하면 됨


//url이 없을 때
@ExceptionHandler(NoHandlerFoundException.class)
public ResponseEntity<ExceptionResponse> noHandlerFoundExceptionHandler()
        throws Exception {
    CustomException ex = new NotFound("url not found");
    return loggingCustomException(ex);
}

//redis가 안켜져 있음
@ExceptionHandler(RedisConnectionFailureException.class)
public ResponseEntity<ExceptionResponse> RedisConnectionExceptionHandler(
RedisConnectionFailureException e
) throws Exception {
    CustomException ex = new ServiceUnavailable(e);
    return loggingCustomException(ex);
}

//pathFinder는 이런 느낌으로
public CustomException findPath(HttpServletRequest request, String message, HttpStatus status) {
    String s = request.getRequestURI().split("/")[1];
    return switch (s) {
        case "accounts" -> new AccountAccountException(message, status);
        case "company" -> new AccountCompanyException(message, status);
        case "auth" -> new AccountAuthException(message, status);
        default -> null;
    };
}
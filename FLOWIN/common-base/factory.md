@Component
public class {팩토리 이름} {

    private final ServerPortHolder portHolder;

    public AccountServerExceptionFactory(ServerPortHolder portHolder) {
        this.portHolder = portHolder;
    }

    
    public {예외이름} {함수이름}(String message, HttpStatus status) {
        return new AccountServerException(
            message,
            status <- 늘 쓰는 그거
            portHolder.getPort()
        );
    }
}

이거는 실재 포트를 뽑아주는 로직
@Component
public class PortConfig implements ApplicationListener<WebServerInitializedEvent> {

    private int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();
    }

    public int getPort() {
        return port;
    }
}
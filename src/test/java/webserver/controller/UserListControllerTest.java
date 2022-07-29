package webserver.controller;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exception.HttpMethodNotSupportedException;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpSession;
import webserver.http.HttpSessions;
import webserver.http.HttpStatus;

class UserListControllerTest {

    @DisplayName("사용자 목록 조회 요청 시, HTTP 메서드가 POST인 경우 HttpMethodNotSupportedException 예외가 발생한다.")
    @Test
    void invalidHttpMethod() {
        String loginRequest = "POST /user/list HTTP/1.1";
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(loginRequest.getBytes(UTF_8)));
        HttpResponse response = new HttpResponse(new ByteArrayOutputStream());

        UserListController controller = new UserListController();

        assertThatThrownBy(() -> controller.handle(request, response))
            .isInstanceOf(HttpMethodNotSupportedException.class);
    }

    @DisplayName("사용자 목록 조회 요청 시, 로그인 상태인 경우 200 OK 응답을 내려준다.")
    @Test
    void userListWithLoggedIn() throws IOException, URISyntaxException {
        HttpSession session = HttpSessions.createSession();
        session.setAttribute("user", new User("user", "1234", "유저", "user@example.com"));

        String userListRequest = userListRequest(session.getId());
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(userListRequest.getBytes(UTF_8)));
        HttpResponse response = new HttpResponse(new ByteArrayOutputStream());

        UserListController controller = new UserListController();
        controller.handle(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("사용자 목록 조회 요청 시, 비로그인 상태인 경우 302 Found 응답과 함께 login.html로 리다이렉트한다.")
    @Test
    void userListWithoutLoggedIn() throws IOException, URISyntaxException {
        String userListRequest = userListRequest(UUID.randomUUID().toString());
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(userListRequest.getBytes(UTF_8)));
        HttpResponse response = new HttpResponse(new ByteArrayOutputStream());

        UserListController controller = new UserListController();
        controller.handle(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/user/login.html");
    }

    private String userListRequest(String sessionId) {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder("GET /user/list HTTP/1.1").append(lineSeparator);
        builder.append(String.format("Cookie: JSESSIONID=%s", sessionId)).append(lineSeparator);
        return builder.toString();
    }
}

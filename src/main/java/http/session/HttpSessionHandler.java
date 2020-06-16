package http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionHandler {
    private static Map<String, HttpSession> sessions;

    public static void run() {
        new HttpSessionHandler();
    }

    private HttpSessionHandler() {
        sessions = new HashMap<>();
    }

    public static HttpSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public static void applySession(HttpSession httpSession) {
        sessions.put(httpSession.getId(), httpSession);
    }
}

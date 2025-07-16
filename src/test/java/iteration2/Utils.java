package iteration2;

import org.example.models.LoginUserRequest;

public class Utils {

    public static final LoginUserRequest USER_ONE = initializeUser("zimeni", "Zimeni33$");
    public static final LoginUserRequest ADMIN = initializeUser("admin", "admin");

    private static LoginUserRequest initializeUser(String username, String password) {
        return LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}

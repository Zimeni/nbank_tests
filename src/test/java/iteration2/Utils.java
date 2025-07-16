package iteration2;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.models.*;
import org.example.requesters.AdminCreateUserRequester;
import org.example.requesters.UserCreateAccountRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;

public class Utils {
    public static final LoginUserRequest ADMIN = initializeUser("admin", "admin");

    private static LoginUserRequest initializeUser(String username, String password) {
        return LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    public static LoginUserRequest getUser() {
        String username = getUsername();
        String password = getPassword();
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.name())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.authorizedUserSpec("admin", "admin"),
                ResponseSpecs.returnsCreatedAndBody()
        ).post(createUserRequest)
                .extract()
                .as(CreateUserResponse.class);

        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        return loginUserRequest;
    }

    public static AccountReponse getAccount(LoginUserRequest user) {
        var response = new UserCreateAccountRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsCreatedAndBody()
        ).post(null)
                .extract()
                .as(AccountReponse.class);

        return response;
    }

    private static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }
    private static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomNumeric(3).toUpperCase() + "$";
    }
}

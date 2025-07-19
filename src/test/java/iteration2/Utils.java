package iteration2;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.example.models.*;
import org.example.requesters.*;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    public static CreateAccountReponse getAccount(LoginUserRequest user) {
        var response = new UserCreateAccountRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsCreatedAndBody()
        ).post(null)
                .extract()
                .as(CreateAccountReponse.class);

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


    public static void isTransactionDoesntExist(LoginUserRequest user, int accountId, int relatedAccountId, String type, float amount) {

        List<Map<String, Object>> transactions = new UserGetTransactionsRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody())
                .get(accountId)
                .extract()
                .jsonPath().getList("");

        boolean invalidTransactionExists =
                transactions.stream()
                        .anyMatch(txn ->
                                ( txn.get("relatedAccountId")).equals(relatedAccountId) &&
                                        type.equals(txn.get("type")) &&
                                        ((Number) txn.get("amount")).floatValue() == amount
                        );

        assertFalse(invalidTransactionExists, "Invalid transaction was found in history!");
    }

    public static void isTransactionExists(LoginUserRequest user, int accountId, int relatedAccountId, String type, float amount) {
        new UserGetTransactionsRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody())
                .get(accountId)
                .body("find { it.relatedAccountId == "+ relatedAccountId + " && it.type == '"+ type +"' && it.amount == " + amount + "}",
                        notNullValue()
                );
    }

    public static void nameEqualsTo(LoginUserRequest user, int profileBeforeChangeId, String name, SoftAssertions soflty) {

        GetProfileResponse response = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        soflty.assertThat(profileBeforeChangeId == response.getId());
        soflty.assertThat(response.getName()).isEqualTo(name);
    }

    public static void nameNotEqualTo(LoginUserRequest user, int profileBeforeChangeId, String name, SoftAssertions soflty) {

        GetProfileResponse response = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        soflty.assertThat(profileBeforeChangeId == response.getId());
        soflty.assertThat(response.getName()).isNotEqualTo(name);
    }
}

package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Utils {

    public static final String USER_ZIMENI_TOKEN = "Basic emltZW5pMTIzOlppbWVuaTMzJA==";
    public static final String ADMIN_TOKEN = "Basic YWRtaW46YWRtaW4=";


    public static void isTransactionExists(int accountId, int relatedAccountId, String type, float amount) {
        // к сожалению не предоставлено апи для проверки текущего баланса, поэтому проверяю по транзакциям
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .get("http://localhost:4111/api/v1/accounts/"+ accountId+ "/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("find { it.relatedAccountId == "+ relatedAccountId + " && it.type == '"+ type +"' && it.amount == " + amount + "}",
                        notNullValue()
                );
    }


    public static void isTransactionDoesntExist(int accountId, int relatedAccountId, String type, float amount) {

        // к сожалению не предоставлено апи для проверки текущего баланса, поэтому проверяю по транзакциям
        List<Map<String, Object>> transactions =
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .header("Authorization", USER_ZIMENI_TOKEN)
                        .when()
                        .get("http://localhost:4111/api/v1/accounts/" + accountId + "/transactions")
                        .then()
                        .statusCode(HttpStatus.SC_OK)
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


    public static void nameEqualsTo(int profileId, String name) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(profileId))
                .body("name", Matchers.equalTo(name));
    }

    public static void nameNotEqualTo(int profileId, String name) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", Matchers.equalTo(profileId))
                .body("name", Matchers.not(Matchers.equalTo(name)));
    }


}

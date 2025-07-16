package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TransferMoneyTest {

    private final String USER_ZIMENI_TOKEN = "Basic emltZW5pOlppbWVuaTMzJA==";
    private final String USER_NOT_ZIMENI_TOKEN = "Basic bm90X3ppbWVuaTpaaW1lbmkzMyQ=";

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                )
        );
    }


    @Test
    public void userCanTransferValidSumBetweenHisAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 50
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("receiverAccountId", Matchers.equalTo(2))
                .body("senderAccountId", Matchers.equalTo(1))
                .body("amount", Matchers.equalTo(50.0F))
                .body("message", Matchers.equalTo("Transfer successful"));
    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 3,
                          "amount": 50
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("receiverAccountId", Matchers.equalTo(3))
                .body("senderAccountId", Matchers.equalTo(1))
                .body("amount", Matchers.equalTo(50.0F))
                .body("message", Matchers.equalTo("Transfer successful"));
    }


    @CsvSource({
            "-10, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        String requestBody = String.format("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 3,
                          "amount": %s
                        }
                        """, sum);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));
    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 3,
                          "amount": 1000
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 5,
                          "amount": 10
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 5,
                          "amount": 10
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }


}

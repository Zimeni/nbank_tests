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
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 82,
                          "amount": 57
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("receiverAccountId", Matchers.equalTo(82))
                .body("senderAccountId", Matchers.equalTo(81))
                .body("amount", Matchers.equalTo(57.0F))
                .body("message", Matchers.equalTo("Transfer successful"));


        Utils.isTransactionExists(81, 82, "TRANSFER_OUT", 57.0f);
    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 79,
                          "amount": 5
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("receiverAccountId", Matchers.equalTo(79))
                .body("senderAccountId", Matchers.equalTo(81))
                .body("amount", Matchers.equalTo(5.0F))
                .body("message", Matchers.equalTo("Transfer successful"));

        Utils.isTransactionExists(81, 79, "TRANSFER_OUT", 5.0F);
    }


    @CsvSource({
            "-10.0, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        String requestBody = String.format("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 82,
                          "amount": %s
                        }
                        """, sum);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));

        Utils.isTransactionDoesntExist(81, 82, "TRANSFER_OUT", sum);
    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 82,
                          "amount": 10000
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        Utils.isTransactionDoesntExist(81, 82, "TRANSFER_OUT", 10000.0f);
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 5666,
                          "amount": 10
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        Utils.isTransactionDoesntExist(81, 5666, "TRANSFER_OUT", 10.0f);
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 81,
                          "receiverAccountId": 5,
                          "amount": 10
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

        Utils.isTransactionDoesntExist(81, 5, "TRANSFER_OUT", 10.0f);
    }


}

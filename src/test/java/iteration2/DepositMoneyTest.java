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

public class DepositMoneyTest {

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
    public void userCanDepositValidSumTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                    {
                      "id": 81,
                      "balance": 4
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo(1508.0f))
                .body("id", Matchers.equalTo(81));


        Utils.isTransactionExists(81, 81, "DEPOSIT", 4.0f);
    }


    @CsvSource({
            "81, -10.0, Invalid account or amount",
            "81, 0, Invalid account or amount"
    })
    @ParameterizedTest
    public void userCannotDepositInvalidSumTest(Integer accountId, Float balance, String error) {
        String requestBody = String.format(
                """
                      {
                        "id": %s,
                        "balance": %s
                      }
                  """, accountId, balance
        );
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));



    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", Utils.USER_ZIMENI_TOKEN)
                .body("""
                    {
                      "id": 80,
                      "balance": 998
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.equalTo("Unauthorized access to account"));


        Utils.isTransactionDoesntExist(81, 80, "DEPOSIT", 998.0f);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                    {
                      "id": 81,
                      "balance": 8989
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

        Utils.isTransactionDoesntExist(81, 81, "DEPOSIT", 8989.0f);
    }


}

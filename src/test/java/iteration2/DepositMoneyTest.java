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

    private final String ADMIN_TOKEN = "Basic YWRtaW46YWRtaW4=";
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
    public void userCanDepositValidSumTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                    {
                      "id": 1,
                      "balance": 100
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.equalTo(500.0F))
                .body("id", Matchers.equalTo(1));


    }


    @CsvSource({
            "1, -10, Invalid account or amount",
            "1, 0, Invalid account or amount"
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
                .header("Authorization", USER_ZIMENI_TOKEN)
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
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                    {
                      "id": 3,
                      "balance": 100
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.equalTo("Unauthorized access to account"));
    }

    @Test
    public void unauthorizedUserCannotDepositTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                    {
                      "id": 3,
                      "balance": 100
                    }
                """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }


}

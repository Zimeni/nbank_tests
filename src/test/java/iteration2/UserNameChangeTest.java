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

public class UserNameChangeTest {
    private final String USER_ZIMENI_TOKEN = "Basic emltZW5pOlppbWVuaTMzJA==";

    private final String ADMIN_TOKEN = "Basic YWRtaW46YWRtaW4=";

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
    public void userCanChangeNameOnHisProfile() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body("""
                    {
                      "name": "zimeni Updated"
                    }
                """)
                .put("http://localhost:4111/api/v1/customer/profile")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.id", Matchers.equalTo(1))
                .body("customer.name", Matchers.equalTo("zimeni Updated"));

    }

    @Test
    public void unauthorizedUserCannotChangeName() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                    {
                      "name": "zimeni Updated"
                    }
                """)
                .put("http://localhost:4111/api/v1/customer/profile")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @CsvSource({
            "zimeniUpdated, Name must contain two words with letters only",
            "zimeni0 Updated, Name must contain two words with letters only",
            "0zimeni Updated, Name must contain two words with letters only",
            "zimeni 0Updated, Name must contain two words with letters only",
            "zimeni Updated0, Name must contain two words with letters only"
    })
    @ParameterizedTest
    public void userCannotChangeNameWithInvalidValues(String name, String error) {
        String requestBody = String.format("""
                    {
                      "name": "%s"
                    }
                """, name);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", USER_ZIMENI_TOKEN)
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));
    }

    @Test
    public void userWithRoleAdminCannotChangeName() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", ADMIN_TOKEN)
                .body("""
                    {
                      "name": "zimeni Updated"
                    }
                """)
                .put("http://localhost:4111/api/v1/customer/profile")
        .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

}

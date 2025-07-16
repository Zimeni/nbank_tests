package org.example.requests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.example.models.TransferMoneyRequest;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class UserTransferRequest extends Request<TransferMoneyRequest> {
    public UserTransferRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(TransferMoneyRequest requestBody) {
        return given()
                    .spec(requestSpecification)
                    .body(requestBody)
                    .post("/api/v1/accounts/transfer")
                .then()
                    .assertThat()
                    .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(TransferMoneyRequest requestBody) {
        return null;
    }

    @Override
    public Response get() {
        return null;
    }
}

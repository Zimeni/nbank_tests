package org.example.requesters;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.TransferMoneyRequest;

import static io.restassured.RestAssured.given;

public class UserTransferRequester extends Request<TransferMoneyRequest> {
    public UserTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
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
    public ValidatableResponse get(Integer id) {
        return null;
    }
}

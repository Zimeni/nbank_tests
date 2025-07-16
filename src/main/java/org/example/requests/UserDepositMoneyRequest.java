package org.example.requests;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.BaseModel;
import org.example.models.DepositMoneyRequest;

import static io.restassured.RestAssured.given;

public class UserDepositMoneyRequest extends Request<DepositMoneyRequest>{
    public UserDepositMoneyRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(DepositMoneyRequest requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .post("/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(DepositMoneyRequest requestBody) {
        return null;
    }

    @Override
    public Response get() {
        return null;
    }
}

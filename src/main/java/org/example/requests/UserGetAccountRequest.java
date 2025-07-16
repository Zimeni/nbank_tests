package org.example.requests;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.BaseModel;

import static io.restassured.RestAssured.given;

public class UserGetAccountRequest extends Request{
    public UserGetAccountRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel requestBody) {
        return null;
    }

    @Override
    public ValidatableResponse put(BaseModel requestBody) {
        return null;
    }

    @Override
    public Response get() {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/customer/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .response();
    }
}

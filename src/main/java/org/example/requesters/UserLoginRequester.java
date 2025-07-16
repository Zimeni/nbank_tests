package org.example.requesters;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.LoginUserRequest;

import static io.restassured.RestAssured.given;

public class UserLoginRequester extends Request<LoginUserRequest>{
    public UserLoginRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(LoginUserRequest requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .post("/api/v1/auth/login")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(LoginUserRequest requestBody) {
        return null;
    }

    @Override
    public Response get() {
        return null;
    }
}

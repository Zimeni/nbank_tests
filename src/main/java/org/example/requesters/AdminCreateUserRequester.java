package org.example.requesters;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.CreateUserRequest;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester extends Request<CreateUserRequest>{
    public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(CreateUserRequest requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .post("/api/v1/admin/users")
                .then()
                .assertThat()
                .spec(responseSpecification);

    }

    @Override
    public ValidatableResponse put(CreateUserRequest requestBody) {
        return null;
    }

    @Override
    public ValidatableResponse get(Integer id) {
        return null;
    }
}

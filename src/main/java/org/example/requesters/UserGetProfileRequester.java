package org.example.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.BaseModel;

import static io.restassured.RestAssured.given;

public class UserGetProfileRequester extends Request{
    public UserGetProfileRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
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
    public ValidatableResponse get(Integer id) {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}

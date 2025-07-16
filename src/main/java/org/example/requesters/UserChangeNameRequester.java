package org.example.requesters;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.NameChangeRequest;

import static io.restassured.RestAssured.given;

public class UserChangeNameRequester extends Request<NameChangeRequest>{
    public UserChangeNameRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(NameChangeRequest requestBody) {
       return null;
    }

    @Override
    public ValidatableResponse put(NameChangeRequest requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .put("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Response get() {
        return null;
    }
}

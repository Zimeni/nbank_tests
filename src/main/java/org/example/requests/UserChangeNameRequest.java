package org.example.requests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.example.models.NameChangeRequest;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class UserChangeNameRequest extends Request<NameChangeRequest>{
    public UserChangeNameRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
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

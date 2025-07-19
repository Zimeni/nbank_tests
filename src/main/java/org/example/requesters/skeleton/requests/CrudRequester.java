package org.example.requesters.skeleton.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.BaseModel;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.HttpRequest;
import org.example.requesters.skeleton.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel requestBody) {
        var body = requestBody == null ? "" : requestBody;
        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get(Integer id) {
        String url = (id != null) ? endpoint.getUrl().replace("$id", id.toString()) : endpoint.getUrl();
        return  given()
                .spec(requestSpecification)
                .get(url)
                .then()
                .assertThat()
                .spec(responseSpecification);

    }

    @Override
    public ValidatableResponse update(Integer id, BaseModel requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .put(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(Integer id) {
        return given()
                .spec(requestSpecification)
                .put(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}

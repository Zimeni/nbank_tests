package org.example.requesters.skeleton.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.models.BaseModel;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.HttpRequest;
import org.example.requesters.skeleton.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class ValidatedRequester<M extends BaseModel> extends HttpRequest implements CrudEndpointInterface {

    private CrudRequester crudRequester;
    public ValidatedRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }


    @Override
    public M post(BaseModel requestBody) {
        return (M)crudRequester.post(requestBody)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public Object get(Long id) {
        return null;
    }

    @Override
    public M update(Long id, BaseModel requestBody) {
        return (M) given()
                .spec(requestSpecification)
                .body(requestBody)
                .put(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(Long id) {
        return null;
    }
}

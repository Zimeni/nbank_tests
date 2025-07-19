package org.example.requesters;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;
import org.example.models.BaseModel;


@AllArgsConstructor
public abstract class Request<T extends BaseModel> {
    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;


    public abstract ValidatableResponse post(T requestBody);
    public abstract ValidatableResponse put(T requestBody);
    public abstract ValidatableResponse get(Integer id);

}

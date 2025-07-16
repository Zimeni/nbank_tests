package org.example.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecs {

    private ResponseSpecs () {}

    private static ResponseSpecBuilder defaultBuilder() {
        return new ResponseSpecBuilder();

    }



    public static ResponseSpecification returnsOkAndBody() {
        return defaultBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody( Matchers.notNullValue())
                .build();
    }


    public static ResponseSpecification returnsBadRequestWithError(String error) {
        return defaultBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.equalTo(error))
                .build();
    }

    public static ResponseSpecification returnsForbiddenWithError(String error) {
        return defaultBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.equalTo(error))
                .build();
    }
    public static ResponseSpecification returnsForbiddenWithoutError() {
        return defaultBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    public static ResponseSpecification returnsUnauthorized() {
        return defaultBuilder()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .build();
    }



}

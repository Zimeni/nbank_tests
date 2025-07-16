package org.example.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.models.LoginUserRequest;
import org.example.requesters.UserLoginRequester;

import java.util.List;

public class RequestSpecs {

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri("http://localhost:4111");

    }

    public static RequestSpecification unauthorizedSpec() {
        return defaultRequestBuilder()
                .build();
    }
    public static RequestSpecification authorizedUserSpec(String username, String password) {

        return defaultRequestBuilder()
                .addHeader("Authorization", getAuthToken(username, password))
                .build();
    }

    private static String getAuthToken(String username, String password) {
        var userLoginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        var authToken = new UserLoginRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsOkAndBody()
        ).post(userLoginRequest)
                .extract()
                .header("Authorization");

        return authToken;
    }
}

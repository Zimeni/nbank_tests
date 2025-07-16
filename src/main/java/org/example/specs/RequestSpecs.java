package org.example.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.models.LoginUserRequest;
import org.example.requests.UserLoginRequest;

import java.util.List;

public class RequestSpecs {

    private RequestSpecs() {}

    private static final String ADMIN_TOKEN = "Basic YWRtaW46YWRtaW4=";
    private static final String USER_ZIMENI_TOKEN = "Basic emltZW5pOlppbWVuaTMzJA==";
    private static final String USER_NOT_ZIMENI_TOKEN = "Basic bm90X3ppbWVuaTpaaW1lbmkzMyQ=";

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

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", ADMIN_TOKEN)
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

        var authToken = new UserLoginRequest(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsOkAndBody()
        ).post(userLoginRequest)
                .extract()
                .header("Authorization");

        return authToken;
    }
}

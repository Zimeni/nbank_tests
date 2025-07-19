package org.example.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.configs.Config;
import org.example.models.LoginUserRequest;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>();

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(Config.getProperty("server") + ":" + Config.getProperty("port") + Config.getProperty("apiVersion"));

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

        if(!authHeaders.containsKey(username)) {
            var userLoginRequest = LoginUserRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            var authToken = new CrudRequester(
                    RequestSpecs.unauthorizedSpec(),
                    Endpoint.LOGIN,
                    ResponseSpecs.returnsOkAndBody()
            ).post(userLoginRequest)
                    .extract()
                    .header("Authorization");

            authHeaders.put(username, authToken);

            return authToken;
        }

        return authHeaders.get(username);

    }
}

package org.example.requesters.steps;

import org.example.configs.Config;
import org.example.generators.RandomModelGenerator;
import org.example.models.CreateUserRequest;
import org.example.models.CreateUserResponse;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.ValidatedRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;

public class AdminSteps {

    public static CreateUserResponse createUser() {

        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        var createUserResponse = new ValidatedRequester<CreateUserResponse>(
                RequestSpecs.authorizedUserSpec(Config.getProperty("adminLogin"), Config.getProperty("adminPassword")),
                Endpoint.ADMIN_USER,
                ResponseSpecs.returnsCreatedAndBody()
        ).post(createUserRequest);

        CreateUserResponse user = CreateUserResponse.builder()
                .username(createUserResponse.getUsername())
                .password(createUserRequest.getPassword())
                .id(createUserResponse.getId())
                .build();

        return user;
    }
}

package org.example.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.generators.GeneratingRule;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel{

    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private String role;
}
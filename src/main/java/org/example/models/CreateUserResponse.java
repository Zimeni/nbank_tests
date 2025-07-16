package org.example.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserResponse extends BaseModel{
    private int id;
    private String username;
    private String password;
    private String role;
    private String name;

    private List<AccountReponse> accounts;
}
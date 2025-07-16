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
public class Customer {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<Account> accounts;
}

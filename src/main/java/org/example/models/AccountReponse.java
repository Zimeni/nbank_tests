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
public class AccountReponse extends BaseModel{
    private int id;
    private String accountNumber;
    private float balance;
    private List<Transaction> transactions;
}
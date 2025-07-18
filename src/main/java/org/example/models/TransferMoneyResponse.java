package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoneyResponse extends BaseModel{
    private int senderAccountId;
    private int receiverAccountId;
    private float amount;
    private String message;
}
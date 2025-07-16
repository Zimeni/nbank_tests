package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.requests.Request;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoneyRequest extends BaseModel {
    private int senderAccountId;
    private int receiverAccountId;
    private float amount;
}
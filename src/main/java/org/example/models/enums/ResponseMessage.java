package org.example.models.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessage {
    TRANSFER_SUCCESSFUL("Transfer successful"),
    TRANSFER_INVALID_INSUFFICIENT_FUNDS_INVALID_ACCOUNT("Invalid transfer: insufficient funds or invalid accounts"),

    ACCOUNT_UNAUTHORIZED_ACCESS("Unauthorized access to account")
    ;

    private final String message;
}

package org.example.requesters.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.models.*;


@AllArgsConstructor
@Getter
public enum Endpoint {
    ADMIN_USER("/admin/users", CreateUserRequest.class, CreateUserResponse.class),
    ACCOUNT("/accounts", BaseModel.class, AccountReponse.class),
    DEPOSIT("/accounts/deposit", DepositMoneyRequest.class, DepositMoneyResponse.class),
    PROFILE("/customer/profile", NameChangeRequest.class, NameChangeResponse.class),
    LOGIN("/auth/login", LoginUserRequest.class, LoginUserResponse.class),
    TRANSFER("/accounts/transfer", TransferMoneyRequest.class, TransferMoneyResponse.class);



    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;


}

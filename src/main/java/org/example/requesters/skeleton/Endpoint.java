package org.example.requesters.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.models.*;

import java.util.List;


@AllArgsConstructor
@Getter
public enum Endpoint {
    ADMIN_USER("/admin/users", CreateUserRequest.class, CreateUserResponse.class),
    ACCOUNT("/accounts", BaseModel.class, CreateAccountReponse.class),
    DEPOSIT("/accounts/deposit", DepositMoneyRequest.class, DepositMoneyResponse.class),
    PROFILE_CHANGE("/customer/profile", NameChangeRequest.class, NameChangeResponse.class),
    PROFILE_GET("/customer/profile", BaseModel.class, GetProfileResponse.class),
    LOGIN("/auth/login", BaseModel.class, GetProfileResponse.class),
    TRANSFER("/accounts/transfer", TransferMoneyRequest.class, TransferMoneyResponse.class),
    TRANSACTIONS("/accounts/$id/transactions", BaseModel.class, BaseModel.class);



    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;


}

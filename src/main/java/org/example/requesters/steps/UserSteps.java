package org.example.requesters.steps;

import org.example.models.AccountReponse;
import org.example.models.CreateUserResponse;
import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.ValidatedRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;

public class UserSteps {
    public static AccountReponse createAccount(CreateUserResponse user) {
        var response = new ValidatedRequester<AccountReponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNT,
                ResponseSpecs.returnsCreatedAndBody()
        ).post(null);

        return response;
    }

    public static void depositMoney(AccountReponse account, CreateUserResponse user){
        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(100F)
                .build();

        new ValidatedRequester<DepositMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsOkAndBody()
        ).post(depositMoneyRequest);
    }
}

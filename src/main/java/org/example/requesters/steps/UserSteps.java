package org.example.requesters.steps;

import org.assertj.core.api.SoftAssertions;
import org.example.models.*;
import org.example.models.comparison.ModelAssertions;
import org.example.models.enums.ResponseMessage;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.CrudRequester;
import org.example.requesters.skeleton.requests.ValidatedRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserSteps {
    public static CreateAccountReponse createAccount(CreateUserResponse user) {
        var response = new ValidatedRequester<CreateAccountReponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNT,
                ResponseSpecs.returnsCreatedAndBody()
        ).post(null);

        return response;
    }

    public static DepositMoneyResponse depositMoney(CreateAccountReponse account, CreateUserResponse user, float amount){
        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        DepositMoneyResponse response = new ValidatedRequester<DepositMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsOkAndBody()
        ).post(depositMoneyRequest);

        return response;
    }

    public static void depositMoneyWithError(CreateAccountReponse account, CreateUserResponse user, float amount, String error){
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }

    public static void depositToForeignAccout(CreateAccountReponse account, CreateUserResponse user, float amount){
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsForbiddenWithError(ResponseMessage.ACCOUNT_UNAUTHORIZED_ACCESS.getMessage())
        ).post(request);
    }
    public static void depositAsUnauthorized(CreateAccountReponse account, float amount){
        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(amount)
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }

    public static GetProfileResponse getProfile(CreateUserResponse user){
        return  new ValidatedRequester<GetProfileResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                Endpoint.PROFILE_GET,
                ResponseSpecs.returnsOkAndBody()
        ).get(null);
    }

    public static void checkIfTransactionExist(CreateUserResponse user, Integer accountId, Integer relatedAccountId, String type, float amount) {
        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.returnsOkAndBody())
                .get(accountId)
                .body("find { it.relatedAccountId == "+ relatedAccountId + " && it.type == '"+ type +"' && it.amount == " + amount + "}",
                        notNullValue()
                );
    }

    public static void checkIfTransactionDoesntExist(CreateUserResponse user, Integer accountId, Integer relatedAccountId, String type, float amount) {

        List<Map<String, Object>> transactions = new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.returnsOkAndBody())
                .get(accountId)
                .extract()
                .jsonPath().getList("");

        boolean invalidTransactionExists =
                transactions.stream()
                        .anyMatch(txn ->
                                ( txn.get("relatedAccountId")).equals(relatedAccountId) &&
                                        type.equals(txn.get("type")) &&
                                        ((Number) txn.get("amount")).floatValue() == amount
                        );

        assertFalse(invalidTransactionExists, "Invalid transaction was found in history!");
    }

    public static void transferMoneyWithBadRequest(CreateUserResponse user, int accountOneId, int accountTwoId, float amount, String error) {
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOneId)
                .receiverAccountId(accountTwoId)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }

    public static void checkIfNameEquals(CreateUserResponse user, int profileBeforeChangeId, String name, SoftAssertions soflty) {

        GetProfileResponse response = new ValidatedRequester<GetProfileResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                Endpoint.PROFILE_GET,
                ResponseSpecs.returnsOkAndBody()
        ).get(null);

        soflty.assertThat(profileBeforeChangeId == response.getId());
        soflty.assertThat(response.getName()).isEqualTo(name);
    }

    public static void checkNameNotEquals(CreateUserResponse user, int profileBeforeChangeId, String name, SoftAssertions soflty) {

        GetProfileResponse response = new ValidatedRequester<GetProfileResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                Endpoint.PROFILE_GET,
                ResponseSpecs.returnsOkAndBody()
        ).get(null);

        soflty.assertThat(profileBeforeChangeId == response.getId());
        soflty.assertThat(response.getName()).isNotEqualTo(name);
    }


}

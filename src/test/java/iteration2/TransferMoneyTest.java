package iteration2;

import org.example.models.*;
import org.example.models.comparison.ModelAssertions;
import org.example.models.enums.ResponseMessage;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.CrudRequester;
import org.example.requesters.skeleton.requests.ValidatedRequester;
import org.example.requesters.steps.AdminSteps;
import org.example.requesters.steps.UserSteps;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


public class TransferMoneyTest extends BaseTest {

    private CreateUserResponse user;

    @BeforeEach
    public void setupUser() {
        this.user = AdminSteps.createUser();
    }

    @Test
    public void userCanTransferValidSumBetweenHisAccount() {

        var accountOne = UserSteps.createAccount(user);
        var accountTwo = UserSteps.createAccount(user);

        UserSteps.depositMoney(accountOne, user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(50)
                .build();

        var response = new ValidatedRequester<TransferMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsOkAndBody()
        ).post(request);

        ModelAssertions.assertThatModels(request, response).match();

    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {

        var accountOne = UserSteps.createAccount(user);
        var anotherUser = AdminSteps.createUser();
        var anotherUserAccount = UserSteps.createAccount(anotherUser);

        UserSteps.depositMoney(accountOne, user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(anotherUserAccount.getId())
                .amount(50)
                .build();

        var response = new ValidatedRequester<TransferMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsOkAndBody()
        ).post(request);

        ModelAssertions.assertThatModels(request, response).match();
    }


    @CsvSource({
            "-10, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        var accountOne = UserSteps.createAccount(user);
        var accountTwo = UserSteps.createAccount(user);

        UserSteps.depositMoney(accountOne, user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(sum)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {

        var accountOne = UserSteps.createAccount(user);
        var accountTwo = UserSteps.createAccount(user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(10000)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsBadRequestWithError(ResponseMessage.TRANSFER_INVALID_INSUFFICIENT_FUNDS_INVALID_ACCOUNT.getMessage())
        ).post(request);
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {
        var accountOne = UserSteps.createAccount(user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(Integer.MAX_VALUE)
                .amount(10000)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsBadRequestWithError(ResponseMessage.TRANSFER_INVALID_INSUFFICIENT_FUNDS_INVALID_ACCOUNT.getMessage())
        ).post(request);
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {
        var accountOne = UserSteps.createAccount(user);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountOne.getId())
                .amount(10)
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }


}

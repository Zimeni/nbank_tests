package iteration2;

import org.example.models.*;
import org.example.models.comparison.ModelAssertions;
import org.example.models.enums.ResponseMessage;
import org.example.models.enums.TransactionType;
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

        UserSteps.depositMoney(accountOne, user, 100.0f);

        float transferAmount = 50.0f;
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(transferAmount)
                .build();

        var response = new ValidatedRequester<TransferMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsOkAndBody()
        ).post(request);

        ModelAssertions.assertThatModels(request, response).match();

        UserSteps.checkIfTransactionExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), transferAmount);

    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {

        var accountOne = UserSteps.createAccount(user);
        var anotherUser = AdminSteps.createUser();
        var anotherUserAccount = UserSteps.createAccount(anotherUser);

        float amount = 100.0f;
        UserSteps.depositMoney(accountOne, user, amount);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(anotherUserAccount.getId())
                .amount(amount)
                .build();

        var response = new ValidatedRequester<TransferMoneyResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsOkAndBody()
        ).post(request);

        ModelAssertions.assertThatModels(request, response).match();

        UserSteps.checkIfTransactionExist(user, accountOne.getId(), anotherUserAccount.getId(), TransactionType.TRANSFER_OUT.name(), amount);
    }


    @CsvSource({
            "-10, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        var accountOne = UserSteps.createAccount(user);
        var accountTwo = UserSteps.createAccount(user);

        float amount = 100.0f;
        UserSteps.depositMoney(accountOne, user, 100.0f);

        UserSteps.transferMoneyWithBadRequest(user, accountOne.getId(), accountTwo.getId(), sum, error);

        UserSteps.checkIfTransactionDoesntExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), sum);
    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {

        var accountOne = UserSteps.createAccount(user);
        var accountTwo = UserSteps.createAccount(user);

        float amount = 10000.0f;

        UserSteps.transferMoneyWithBadRequest(user, accountOne.getId(), accountTwo.getId(), amount, ResponseMessage.TRANSFER_INVALID_INSUFFICIENT_FUNDS_INVALID_ACCOUNT.getMessage());

        UserSteps.checkIfTransactionDoesntExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), amount);
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {
        var accountOne = UserSteps.createAccount(user);

        float amount = 10000.0f;

        UserSteps.transferMoneyWithBadRequest(user, accountOne.getId(), Integer.MAX_VALUE, amount, ResponseMessage.TRANSFER_INVALID_INSUFFICIENT_FUNDS_INVALID_ACCOUNT.getMessage());

        UserSteps.checkIfTransactionDoesntExist(user, accountOne.getId(), accountOne.getId(), TransactionType.TRANSFER_OUT.name(), amount);
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {
        var accountOne = UserSteps.createAccount(user);

        float amount = 10.0f;
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountOne.getId())
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.TRANSFER,
                ResponseSpecs.returnsUnauthorized()
        ).post(request);

        UserSteps.checkIfTransactionDoesntExist(user, accountOne.getId(), accountOne.getId(), TransactionType.TRANSFER_OUT.name(), amount);
    }


}

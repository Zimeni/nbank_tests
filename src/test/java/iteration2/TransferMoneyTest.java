package iteration2;

import org.example.models.*;
import org.example.requesters.UserDepositMoneyRequester;
import org.example.requesters.UserTransferRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;

public class TransferMoneyTest extends BaseTest {

    private LoginUserRequest user;

    @BeforeEach
    public void setupUser() {
        this.user = Utils.getUser();
    }

    @Test
    public void userCanTransferValidSumBetweenHisAccount() {

        var accountOne = Utils.getAccount(user);
        var accountTwo = Utils.getAccount(user);

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder()
                .id(accountOne.getId())
                .balance(100F)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(depositMoneyRequest)
                .extract()
                .as(DepositMoneyResponse.class);


        var transferAmount = 50.0f;

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(transferAmount)
                .build();

        var response = new UserTransferRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(request)
                .extract()
                .as(TransferMoneyResponse.class);

        String error = "Transfer successful";

        soflty.assertThat(request.getReceiverAccountId() == response.getReceiverAccountId());
        soflty.assertThat(request.getSenderAccountId() == response.getSenderAccountId());
        soflty.assertThat(request.getAmount()).isEqualTo(response.getAmount());
        soflty.assertThat(error.equals(response.getMessage()));


        Utils.isTransactionExists(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), transferAmount);

    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {

        var accountOne = Utils.getAccount(user);
        var anotherUser = Utils.getUser();
        var anotherUserAccount = Utils.getAccount(anotherUser);

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder()
                .id(accountOne.getId())
                .balance(100F)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(depositMoneyRequest)
                .extract()
                .as(DepositMoneyResponse.class);

        var transferAmount = 50.0f;

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(anotherUserAccount.getId())
                .amount(transferAmount)
                .build();

        var response = new UserTransferRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(request)
                .extract()
                .as(TransferMoneyResponse.class);

        String error = "Transfer successful";

        soflty.assertThat(request.getReceiverAccountId() == response.getReceiverAccountId());
        soflty.assertThat(request.getSenderAccountId() == response.getSenderAccountId());
        soflty.assertThat(request.getAmount()).isEqualTo(response.getAmount());
        soflty.assertThat(error.equals(response.getMessage()));

        Utils.isTransactionExists(user, accountOne.getId(), anotherUserAccount.getId(), TransactionType.TRANSFER_OUT.name(), transferAmount);
    }


    @CsvSource({
            "-10, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        var accountOne = Utils.getAccount(user);
        var accountTwo = Utils.getAccount(user);

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder()
                .id(accountOne.getId())
                .balance(100F)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(depositMoneyRequest)
                .extract()
                .as(DepositMoneyResponse.class);

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(sum)
                .build();

        new UserTransferRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);

        Utils.isTransactionDoesntExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), sum);

    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {

        var accountOne = Utils.getAccount(user);
        var accountTwo = Utils.getAccount(user);

        var transferAmount = 10000.0f;

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(transferAmount)
                .build();

        new UserTransferRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError("Invalid transfer: insufficient funds or invalid accounts")
        ).post(request);

        Utils.isTransactionDoesntExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), transferAmount);
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {
        var accountOne = Utils.getAccount(user);

        var transferAmount = 10000.0f;
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(Integer.MAX_VALUE)
                .amount(10000)
                .build();

        new UserTransferRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError("Invalid transfer: insufficient funds or invalid accounts")
        ).post(request);

        Utils.isTransactionDoesntExist(user, accountOne.getId(), Integer.MAX_VALUE, TransactionType.TRANSFER_OUT.name(), transferAmount);
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {


        var accountOne = Utils.getAccount(user);
        var accountTwo = Utils.getAccount(user);


        var transferAmount = 10.0f;

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(accountOne.getId())
                .receiverAccountId(accountTwo.getId())
                .amount(transferAmount)
                .build();

        new UserTransferRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).post(request);

        Utils.isTransactionDoesntExist(user, accountOne.getId(), accountTwo.getId(), TransactionType.TRANSFER_OUT.name(), transferAmount);
    }


}

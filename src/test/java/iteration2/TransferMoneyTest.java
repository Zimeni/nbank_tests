package iteration2;

import org.example.models.TransferMoneyRequest;
import org.example.models.TransferMoneyResponse;
import org.example.requests.UserTransferRequest;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;

public class TransferMoneyTest extends BaseTest {

    @Test
    public void userCanTransferValidSumBetweenHisAccount() {
        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(2)
                .amount(50)
                .build();

        var response = new UserTransferRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(request)
                .extract()
                .as(TransferMoneyResponse.class);

        String error = "Transfer successful";

        soflty.assertThat(request.getReceiverAccountId() == response.getReceiverAccountId());
        soflty.assertThat(request.getSenderAccountId() == response.getSenderAccountId());
        soflty.assertThat(request.getAmount()).isEqualTo(response.getAmount());
        soflty.assertThat(error.equals(response.getMessage()));

    }

    @Test
    public void userCanTransferValidSumBetweenHisAndNotHisAccount() {

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(3)
                .amount(50)
                .build();

        var response = new UserTransferRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).post(request)
                .extract()
                .as(TransferMoneyResponse.class);

        String error = "Transfer successful";

        soflty.assertThat(request.getReceiverAccountId() == response.getReceiverAccountId());
        soflty.assertThat(request.getSenderAccountId() == response.getSenderAccountId());
        soflty.assertThat(request.getAmount()).isEqualTo(response.getAmount());
        soflty.assertThat(error.equals(response.getMessage()));
    }


    @CsvSource({
            "-10, Invalid transfer: insufficient funds or invalid accounts",
            "0, Invalid transfer: insufficient funds or invalid accounts"
    })
    @ParameterizedTest
    public void userCannotTransferInvalidSumBetweenAccounts(Float sum, String error) {

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(3)
                .amount(sum)
                .build();

        new UserTransferRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }


    @Test
    public void userCannotTransferSumFromInsufficientBalance() {

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(3)
                .amount(10000)
                .build();

        new UserTransferRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsBadRequestWithError("Invalid transfer: insufficient funds or invalid accounts")
        ).post(request);
    }

    @Test
    public void userCannotTransferToNonexistingAccount() {

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(13)
                .amount(10000)
                .build();

        new UserTransferRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsBadRequestWithError("Invalid transfer: insufficient funds or invalid accounts")
        ).post(request);
    }

    @Test
    public void unauthorizedUserCannotTransferBetweenAccounts() {

        TransferMoneyRequest request = TransferMoneyRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(5)
                .amount(10)
                .build();

        new UserTransferRequest(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }


}

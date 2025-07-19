package iteration2;

import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
import org.example.models.LoginUserRequest;
import org.example.models.TransactionType;
import org.example.requesters.UserDepositMoneyRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DepositMoneyTest extends BaseTest{

    private LoginUserRequest user;

    @BeforeEach
    public void setupUser() {
        this.user = Utils.getUser();
    }

    @Test
    public void userCanDepositValidSumTest() {

        var account = Utils.getAccount(user);

        Float currentBalance = account.getBalance();
        float expectedBalance = currentBalance + 100;
        float depositAmount = 100F;

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        DepositMoneyResponse response = new UserDepositMoneyRequester(
                    RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                    ResponseSpecs.returnsOkAndBody()
                ).post(request)
                .extract()
                .as(DepositMoneyResponse.class);

        soflty.assertThat(request.getId() == response.getId());
        soflty.assertThat(response.getBalance()).isEqualTo(expectedBalance);

        Utils.isTransactionExists(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), depositAmount);

    }


    @CsvSource({
            "-10, Invalid account or amount",
            "0, Invalid account or amount"
    })
    @ParameterizedTest
    public void userCannotDepositInvalidSumTest(Float balance, String error) {

        var account = Utils.getAccount(user);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(balance)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);

        Utils.isTransactionDoesntExist(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), balance);
    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {

        var account = Utils.getAccount(user);

        var anotherUser = Utils.getUser();
        var anotherUserAccount = Utils.getAccount(anotherUser);

        var depositAmount = 100.0f;

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(anotherUserAccount.getId())
                .balance(depositAmount)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsForbiddenWithError("Unauthorized access to account")
        ).post(request);

        Utils.isTransactionDoesntExist(user, account.getId(), anotherUserAccount.getId(), TransactionType.DEPOSIT.name(), depositAmount);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {

        var account = Utils.getAccount(user);
        var depositAmount = 100.0f;

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(depositAmount)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).post(request);

        Utils.isTransactionDoesntExist(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), depositAmount);
    }
}

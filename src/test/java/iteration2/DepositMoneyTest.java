package iteration2;

import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
import org.example.models.LoginUserRequest;
import org.example.requesters.UserDepositMoneyRequester;
import org.example.requesters.UserGetAccountRequester;
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

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(100F)
                .build();

        DepositMoneyResponse response = new UserDepositMoneyRequester(
                    RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                    ResponseSpecs.returnsOkAndBody()
                ).post(request)
                .extract()
                .as(DepositMoneyResponse.class);

        soflty.assertThat(request.getId() == response.getId());
        soflty.assertThat(response.getBalance()).isEqualTo(expectedBalance);

    }


    @CsvSource({
            "1, -10, Invalid account or amount",
            "1, 0, Invalid account or amount"
    })
    @ParameterizedTest
    public void userCannotDepositInvalidSumTest(Integer accountId, Float balance, String error) {

        Utils.getAccount(user);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(accountId)
                .balance(balance)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {

        var anotherUser = Utils.getUser();
        var anotherUserAccount = Utils.getAccount(anotherUser);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(anotherUserAccount.getId())
                .balance(100)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsForbiddenWithError("Unauthorized access to account")
        ).post(request);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {

        var account = Utils.getAccount(user);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(100)
                .build();

        new UserDepositMoneyRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }
}

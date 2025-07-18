package iteration2;

import org.example.models.CreateUserResponse;
import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
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

public class DepositMoneyTest extends BaseTest{

    private CreateUserResponse user;

    @BeforeEach
    public void setupUser() {
        this.user = AdminSteps.createUser();
    }

    @Test
    public void userCanDepositValidSumTest() {

        var account = UserSteps.createAccount(user);

        Float currentBalance = account.getBalance();
        float expectedBalance = currentBalance + 100;

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(100F)
                .build();

        DepositMoneyResponse response = new ValidatedRequester<DepositMoneyResponse>(
                    RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                    Endpoint.DEPOSIT,
                    ResponseSpecs.returnsOkAndBody()
                ).post(request);

        soflty.assertThat(request.getId() == response.getId());
        soflty.assertThat(response.getBalance()).isEqualTo(expectedBalance);
    }


    @CsvSource({
            "-10, Invalid account or amount",
            "0, Invalid account or amount"
    })
    @ParameterizedTest
    public void userCannotDepositInvalidSumTest(Float balance, String error) {

        var account = UserSteps.createAccount(user);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(balance)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {

        UserSteps.createAccount(user);

        var anotherUser = AdminSteps.createUser();
        var anotherUserAccount = UserSteps.createAccount(anotherUser);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(anotherUserAccount.getId())
                .balance(100)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsForbiddenWithError(ResponseMessage.ACCOUNT_UNAUTHORIZED_ACCESS.getMessage())
        ).post(request);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {

        var account = UserSteps.createAccount(user);

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(account.getId())
                .balance(100)
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.DEPOSIT,
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }
}

package iteration2;

import org.example.models.CreateUserResponse;
import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
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

        float depositAmount = 100.0f;
        DepositMoneyResponse response = UserSteps.depositMoney(account, user, 100.0f);

        soflty.assertThat(account.getId() == response.getId());
        soflty.assertThat(response.getBalance()).isEqualTo(expectedBalance);

        UserSteps.checkIfTransactionExist(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), depositAmount);

    }


    @CsvSource({
            "-10, Invalid account or amount",
            "0, Invalid account or amount"
    })
    @ParameterizedTest
    public void userCannotDepositInvalidSumTest(Float amount, String error) {

        var account = UserSteps.createAccount(user);

        UserSteps.depositMoneyWithError(account, user, amount, error);

        UserSteps.checkIfTransactionDoesntExist(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), amount);
    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {

        var currentUserAccount = UserSteps.createAccount(user);

        var anotherUser = AdminSteps.createUser();
        var anotherUserAccount = UserSteps.createAccount(anotherUser);

        float amount = 100.0f;
        UserSteps.depositToForeignAccout(anotherUserAccount, user, amount);

        UserSteps.checkIfTransactionDoesntExist(user, currentUserAccount.getId(), anotherUserAccount.getId(), TransactionType.DEPOSIT.name(), amount);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {

        var account = UserSteps.createAccount(user);
        float amount = 100.0f;
        UserSteps.depositAsUnauthorized(account, amount);

        UserSteps.checkIfTransactionDoesntExist(user, account.getId(), account.getId(), TransactionType.DEPOSIT.name(), amount);
    }
}

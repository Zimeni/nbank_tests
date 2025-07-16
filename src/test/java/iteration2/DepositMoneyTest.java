package iteration2;

import org.example.models.DepositMoneyRequest;
import org.example.models.DepositMoneyResponse;
import org.example.requests.UserDepositMoneyRequest;
import org.example.requests.UserGetAccountRequest;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;

public class DepositMoneyTest extends BaseTest{
    @Test
    public void userCanDepositValidSumTest() {

        var getAccountResponse = new UserGetAccountRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(),Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get();

        Float currentBalance = getAccountResponse.jsonPath().getFloat("find { it.id == 1 }.balance");
        float expectedBalance = currentBalance + 100;

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(1)
                .balance(100F)
                .build();

        DepositMoneyResponse response = new UserDepositMoneyRequest(
                    RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(),Utils.USER_ONE.getPassword()),
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

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(accountId)
                .balance(balance)
                .build();

        new UserDepositMoneyRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).post(request);
    }

    @Test
    public void userCannotDepositSumToNotHisAccountTest() {

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(3)
                .balance(100)
                .build();

        new UserDepositMoneyRequest(
                RequestSpecs.authorizedUserSpec(Utils.USER_ONE.getUsername(), Utils.USER_ONE.getPassword()),
                ResponseSpecs.returnsForbiddenWithError("Unauthorized access to account")
        ).post(request);

    }

    @Test
    public void unauthorizedUserCannotDepositTest() {

        DepositMoneyRequest request = DepositMoneyRequest.builder()
                .id(3)
                .balance(100)
                .build();

        new UserDepositMoneyRequest(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).post(request);
    }
}

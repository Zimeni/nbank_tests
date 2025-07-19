package iteration2;

import org.example.configs.Config;
import org.example.models.CreateUserResponse;
import org.example.models.NameChangeRequest;
import org.example.models.NameChangeResponse;
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

public class UserNameChangeTest extends BaseTest {

    private CreateUserResponse user;

    @BeforeEach
    public void setupUser() {
        this.user = AdminSteps.createUser();
    }


    @Test
    public void userCanChangeNameOnHisProfile() {

        var profileBeforeChange = UserSteps.getProfile(user);

        String changedName = "zimeni Updated";
        NameChangeRequest request = NameChangeRequest.builder()
                .name(changedName)
                .build();

        var response = new ValidatedRequester<NameChangeResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.PROFILE_CHANGE,
                ResponseSpecs.returnsOkAndBody()
        ).update(null, request);


        // оставлены soflty асерты, так как пока нет модели запроса для сравнения с моделью ответа
        soflty.assertThat(response.getCustomer().getId() == user.getId());
        soflty.assertThat(response.getCustomer().getName().equals(response.getCustomer().getName()));


        UserSteps.checkIfNameEquals(user, profileBeforeChange.getId(), changedName, soflty);

    }

    @Test
    public void unauthorizedUserCannotChangeName() {
        var profileBeforeChange = UserSteps.getProfile(user);

        String changedName = "zimeni Updated";

        NameChangeRequest request = NameChangeRequest.builder()
                .name(changedName)
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.PROFILE_CHANGE,
                ResponseSpecs.returnsUnauthorized()
        ).update(null, request);

        UserSteps.checkNameNotEquals(user, profileBeforeChange.getId(), changedName, soflty);
    }

    @CsvSource({
            "zimeniUpdated, Name must contain two words with letters only",
            "zimeni0 Updated, Name must contain two words with letters only",
            "0zimeni Updated, Name must contain two words with letters only",
            "zimeni 0Updated, Name must contain two words with letters only",
            "zimeni Updated0, Name must contain two words with letters only"
    })
    @ParameterizedTest
    public void userCannotChangeNameWithInvalidValues(String name, String error) {
        var profileBeforeChange = UserSteps.getProfile(user);

        NameChangeRequest request = NameChangeRequest.builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.PROFILE_CHANGE,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).update(null, request);

        UserSteps.checkNameNotEquals(user, profileBeforeChange.getId(), name, soflty);
    }

    @Test
    public void userWithRoleAdminCannotChangeName() {
        var profileBeforeChange = UserSteps.getProfile(user);

        String changedName = "zimeni Updated";
        NameChangeRequest request = NameChangeRequest.builder()
                .name(changedName)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(Config.getProperty("adminLogin"), Config.getProperty("adminPassword")),
                Endpoint.PROFILE_CHANGE,
                ResponseSpecs.returnsForbiddenWithoutError()
        ).update(null, request);

        UserSteps.checkNameNotEquals(user, profileBeforeChange.getId(), changedName, soflty);
    }

}

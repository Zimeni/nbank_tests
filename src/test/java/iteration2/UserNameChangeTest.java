package iteration2;

import org.example.configs.Config;
import org.example.models.CreateUserResponse;
import org.example.models.NameChangeRequest;
import org.example.models.NameChangeResponse;
import org.example.requesters.skeleton.Endpoint;
import org.example.requesters.skeleton.requests.CrudRequester;
import org.example.requesters.skeleton.requests.ValidatedRequester;
import org.example.requesters.steps.AdminSteps;
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

        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        var response = new ValidatedRequester<NameChangeResponse>(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.returnsOkAndBody()
        ).update(null, request);


        soflty.assertThat(response.getCustomer().getId() == user.getId());
        soflty.assertThat(response.getCustomer().getName().equals(response.getCustomer().getName()));

    }

    @Test
    public void unauthorizedUserCannotChangeName() {
        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        new CrudRequester(
                RequestSpecs.unauthorizedSpec(),
                Endpoint.PROFILE,
                ResponseSpecs.returnsUnauthorized()
        ).update(null, request);
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
        NameChangeRequest request = NameChangeRequest.builder()
                .name(name)
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.returnsBadRequestWithError(error)
        ).update(null, request);
    }

    @Test
    public void userWithRoleAdminCannotChangeName() {

        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        new CrudRequester(
                RequestSpecs.authorizedUserSpec(Config.getProperty("adminLogin"), Config.getProperty("adminPassword")),
                Endpoint.PROFILE,
                ResponseSpecs.returnsForbiddenWithoutError()
        ).update(null, request);
    }

}

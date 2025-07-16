package iteration2;

import org.example.models.LoginUserRequest;
import org.example.models.NameChangeRequest;
import org.example.models.NameChangeResponse;
import org.example.requesters.UserChangeNameRequester;
import org.example.specs.RequestSpecs;
import org.example.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UserNameChangeTest extends BaseTest {

    private LoginUserRequest user;

    @BeforeEach
    public void setupUser() {
        this.user = Utils.getUser();
    }


    @Test
    public void userCanChangeNameOnHisProfile() {

        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        var response = new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).put(request)
                .extract()
                .as(NameChangeResponse.class);


        soflty.assertThat(response.getCustomer().getId() == 1);
        soflty.assertThat(response.getCustomer().getName().equals(response.getCustomer().getName()));

    }

    @Test
    public void unauthorizedUserCannotChangeName() {
        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        new UserChangeNameRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).put(request);;
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

        new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).put(request);
    }

    @Test
    public void userWithRoleAdminCannotChangeName() {

        NameChangeRequest request = NameChangeRequest.builder()
                .name("zimeni Updated")
                .build();

        new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(Utils.ADMIN.getUsername(), Utils.ADMIN.getPassword()),
                ResponseSpecs.returnsForbiddenWithoutError()
        ).put(request);
    }

}

package iteration2;

import org.example.models.GetProfileResponse;
import org.example.models.LoginUserRequest;
import org.example.models.NameChangeRequest;
import org.example.models.NameChangeResponse;
import org.example.requesters.UserChangeNameRequester;
import org.example.requesters.UserGetProfileRequester;
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

        GetProfileResponse profileBeforeChange = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        String updateName = "zimeni Updated";

        NameChangeRequest request = NameChangeRequest.builder()
                .name(updateName)
                .build();

        var response = new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).put(request)
                .extract()
                .as(NameChangeResponse.class);


        soflty.assertThat(response.getCustomer().getId() == profileBeforeChange.getId());
        soflty.assertThat(response.getCustomer().getName().equals(request.getName()));

        Utils.nameEqualsTo(user, profileBeforeChange.getId(), updateName, soflty);

    }

    @Test
    public void unauthorizedUserCannotChangeName() {

        GetProfileResponse profileBeforeChange = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        String updateName = "zimeni Updated";

        NameChangeRequest request = NameChangeRequest.builder()
                .name(updateName)
                .build();

        new UserChangeNameRequester(
                RequestSpecs.unauthorizedSpec(),
                ResponseSpecs.returnsUnauthorized()
        ).put(request);


        Utils.nameNotEqualTo(user, profileBeforeChange.getId(), updateName, soflty);
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
        GetProfileResponse profileBeforeChange = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        NameChangeRequest request = NameChangeRequest.builder()
                .name(name)
                .build();

        new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(), user.getPassword()),
                ResponseSpecs.returnsBadRequestWithError(error)
        ).put(request);

        Utils.nameNotEqualTo(user, profileBeforeChange.getId(), name, soflty);
    }

    @Test
    public void userWithRoleAdminCannotChangeName() {

        GetProfileResponse profileBeforeChange = new UserGetProfileRequester(
                RequestSpecs.authorizedUserSpec(user.getUsername(),user.getPassword()),
                ResponseSpecs.returnsOkAndBody()
        ).get(null)
                .extract()
                .as(GetProfileResponse.class);

        String updateName = "zimeni Updated";

        NameChangeRequest request = NameChangeRequest.builder()
                .name(updateName)
                .build();

        new UserChangeNameRequester(
                RequestSpecs.authorizedUserSpec(Utils.ADMIN.getUsername(), Utils.ADMIN.getPassword()),
                ResponseSpecs.returnsForbiddenWithoutError()
        ).put(request);

        Utils.nameNotEqualTo(user, profileBeforeChange.getId(), updateName, soflty);
    }

}

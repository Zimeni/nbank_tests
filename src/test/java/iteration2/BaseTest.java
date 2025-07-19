package iteration2;

import org.assertj.core.api.SoftAssertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions soflty;

    @BeforeEach
    public void setUpTest() {
        this.soflty = new SoftAssertions();
    }
    @AfterEach
    public void afterTest() {
        this.soflty.assertAll();
    }
}

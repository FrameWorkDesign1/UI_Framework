package steps;

import Core.UI.TestContext;
import Core.UI.utils.PK_UI_Framework;
import Page.HRM_Admin;
import Page.LoginHRMPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class UI_Steps {
    private TestContext context;

    static final Logger LOGGER = PK_UI_Framework.getLogger(UI_Steps.class);

    public UI_Steps(TestContext context) {
        this.context = context;
    }

    @Given("User enter Username {string} and Password {string}")
    public void user_enter_Username_and_Password(String username, String password)
    {
        LoginHRMPage loginPage =this.context.getPageObject(LoginHRMPage.class);
        loginPage.Login(this.context.resolve(username),this.context.resolve(password));

    }

    @And("Validate Admin tab \\(System Users) and filled detailed {string}")
    public void validateAdminTabSystemUsersAndFilledDetailed(String uname)
    {
        HRM_Admin admin = this.context.getPageObject(HRM_Admin.class);
        admin.adminDataentry(this.context.resolve(uname));
    }

    @Then("^I get all the options from the dropdown$")
    public void i_get_all_the_options_from_the_dropdown() throws Throwable {
        HRM_Admin admin = this.context.getPageObject(HRM_Admin.class);

        Select dropdown = new Select((WebElement) admin.scltct(String.valueOf(0)));
        List<WebElement> options = dropdown.getOptions();
        for (WebElement option : options) {
            System.out.println(option.getText());
        }
    }
}

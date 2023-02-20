package Page;

import Core.UI.BaseWdPage;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class LoginHRMPage extends BaseWdPage
{

    static final Logger LOGGER = PK_UI_Framework.getLogger(LoginHRMPage.class);


    public static final String UserNameXpath="/html/body/div/div[1]/div/div[1]/div/div[2]/div[2]/form/div[1]/div/div[2]/input";
    public static final String UserPassXpath="/html/body/div/div[1]/div/div[1]/div/div[2]/div[2]/form/div[2]/div/div[2]/input";
    public static final String LoginButton="//*[@id='app']/div[1]/div/div[1]/div/div[2]/div[2]/form/div[3]/button";


    //    @FindBy (xpath ="(//*[@class='oxd-input oxd-input--active'])[1]")
    @FindBy (xpath =UserNameXpath)
    @CacheLookup
    WebElement username;

    //    @FindBy (xpath = "(//*[@class='oxd-input oxd-input--active'])[2]")
    @FindBy (xpath = UserPassXpath)
    @CacheLookup
    WebElement password;

    @FindBy (xpath = LoginButton)
    @CacheLookup
    WebElement Login;

    public LoginHRMPage(WebDriver driver) {
        super(driver);
    }



    public void Login(String ID, String Pass)
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver().type(username,ID);
        driver().type(password,Pass);
        driver().click(Login);

    }

    @Override
    public void waitForLoad() {

    }

    @Override
    public void pageIsValid() {

    }
}

package Page;

import Core.UI.BaseWdPage;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HRM_Admin extends BaseWdPage
{
    static final Logger LOGGER = PK_UI_Framework.getLogger(HRM_Admin.class);

    @FindBy(xpath = "//*[@class='oxd-text oxd-text--h5 oxd-table-filter-title']")
    WebElement Adminsystittle;

    @FindBy(xpath ="(//*[@class='oxd-input oxd-input--active'])[2]")
    WebElement Admin_Username;

    @FindBy (xpath = "//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div[1]/div[2]/form/div[1]/div/div[2]/div/div[2]/div/div")
    WebElement selctclass;

//    @FindBy(xpath = )
//    WebDriver ;
//
//    @FindBy(xpath = )
//    WebDriver ;
//
//    @FindBy(xpath = )
//    WebDriver ;
//
//    @FindBy(xpath = )
//    WebDriver ;



    public HRM_Admin(WebDriver driver) {
        super(driver);
    }

    public void adminDataentry(String a_username)
    {
        String systtl = Adminsystittle.getText().trim();
        LOGGER.info("Admin Tab :- " +systtl);
        String trdd = (String) driver().type(Admin_Username, a_username);
    }

    public Object scltct(String yxy)
    {
        Object SAd = driver().selectByIndex(selctclass, 0);
        return SAd;
    }


    @Override
    public void waitForLoad() {

    }

    @Override
    public void pageIsValid() {

    }
}

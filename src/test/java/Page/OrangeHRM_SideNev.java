package Page;

import Core.UI.BaseWdPage;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class OrangeHRM_SideNev extends BaseWdPage
{
    static final Logger LOGGER = PK_UI_Framework.getLogger(OrangeHRM_SideNev.class);

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[1]")
    WebElement Admin;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[2]")
    WebElement PIM;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[3]")
    WebElement Leaves;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[4]")
    WebElement Time;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[5]")
    WebElement Recruitment;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[6]")
    WebElement My_Info;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[7]")
    WebElement Performance;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[8]")
    WebElement Dashboard;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[9]")
    WebElement Directory;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[10]")
    WebElement Maintenance;

    @FindBy (xpath = "(//*[@class='oxd-text oxd-text--span oxd-main-menu-item--name'])[11]")
    WebElement Buzz;

    @FindBy(xpath = "(//*[@class='oxd-input oxd-input--active'])[2]")
    WebElement MaintenanceAdministrator;

    @FindBy(xpath = "oxd-button oxd-button--large oxd-button--secondary orangehrm-admin-access-button")
    WebElement AdministratorBtn;

    public OrangeHRM_SideNev(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
    }

    @Override
    public void pageIsValid()
    {

    }

    public String clickOnAdmin()
    {
        driver().jsClick(Admin);
        LOGGER.info("Clicked on Admin Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnPIM()
    {
        driver().jsClick(PIM);
        LOGGER.info("Clicked on PIM Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnLeaves()
    {
        driver().jsClick(Leaves);
        LOGGER.info("Clicked on Leaves Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnTime()
    {
        driver().jsClick(Time);
        LOGGER.info("Clicked on Time Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnRecruitment()
    {
        driver().jsClick(Recruitment);
        LOGGER.info("Clicked on Recruitment Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnMy_Info()
    {
        driver().jsClick(My_Info);
        LOGGER.info("Clicked on My_Info Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnPerformance()
    {
        driver().jsClick(Performance);
        LOGGER.info("Clicked on Performance Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnDashboard()
    {
        driver().jsClick(Dashboard);
        LOGGER.info("Clicked on Dashboard Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnDirectory()
    {
        driver().jsClick(Directory);
        LOGGER.info("Clicked on Directory Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnMaintenance() throws InterruptedException {
        driver().jsClick(Maintenance);
        Thread.sleep(1000);
        driver().typePassword(MaintenanceAdministrator,"admin123");
        driver().click(AdministratorBtn);
        LOGGER.info("Clicked on Maintenance Button" + driver().getTitle());
        return driver().getTitle();
    }

    public String clickOnBuzz()
    {
        driver().jsClick(Buzz);
        LOGGER.info("Clicked on clickOnBuzz Button" + driver().getTitle());
        return driver().getTitle();
    }


}

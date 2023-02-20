package Core.UI.hooks;


import Core.UI.Const;
import Core.UI.ContextVars;
import Core.UI.TestContext;
import Core.UI.utils.PK_UI_Framework;
import Page.OrangeHRM_SideNev;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.logging.LogEntry;
import org.testng.Assert;
import utils.CommonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UiSteps {
    private TestContext context;
    private static final Logger LOGGER = PK_UI_Framework.getLogger(UiSteps.class);
    public UiSteps(TestContext context) {
        this.context = context;
    }
    CommonUtil commonUtil = new CommonUtil(context);
    @Given("Open browser with url")
    @Given("Open browser with url {string}")
    public void userOpensTheBrowserWithUrl()
    {
        Object url = context.resolve(Const.ENV_URL);
        this.context.initContext();
        this.context.driver().openUrl((String) url);
    }

    @When("Type {string} to element {string}")
    public void typeToElement(String text, String elementLocator) {
        this.context.driver().type(this.context.resolve(elementLocator),this.context.resolve(text));
    }

    @And("Submit element {string}")
    public void submitElement(String elementLocator) {
        this.context.driver().submit(this.context.resolve(elementLocator));
    }
    @And("Wait for element {string} to be clickable")
    public void waitForElementToBeClickAble(String elementLocator) {
        this.context.waitUtils().waitForElementToBeClickable(this.context.resolve(elementLocator));
    }
    @And("Wait for element {string} value to be {string}")
    public void waitForElementValueToContainsText(String elementLocator,String expectedValue) {
        this.context.waitUtils().waitForElementValueToContainsText(this.context.resolve(elementLocator),this.context.resolve(expectedValue));
    }
    @And("Wait for element {string} text to match regex {string}")
    public void waitForElementTextToBe(String elementLocator,String regex) {
        this.context.waitUtils().waitForElementTextMatches(this.context.resolve(elementLocator),this.context.resolve(regex));
    }
    @And("Wait for element {string} text to contain {string}")
    public void waitForElementTextToContainsText(String elementLocator,String subText) {
        this.context.waitUtils().waitForElementTextToContainsText(this.context.resolve(elementLocator),this.context.resolve(subText));
    }
    @And("Click element {string}")
    public void clickElement(String elementLocator) {
        this.context.driver().click(this.context.resolve(elementLocator));
    }
    @And("JsClick element {string}")
    public void jsClickElement(String elementLocator) {
        this.context.driver().jsClick(this.context.resolve(elementLocator));
    }
    @And("Read text of element {string} store as variable {string}")
    public void readTextOfElement(String elementLocator,String var) {
        String text = this.context.driver().getText(this.context.resolve(elementLocator));
        this.context.setVar(var, text);
    }

    @And("Validate/Assert/Check/Verify url starts with {string}")
    public void assertUrlStartsWith(String expUrlStartsWith) {
        String actualUrl = this.context.driver().getUrl();
        Assert.assertTrue(actualUrl.startsWith(this.context.resolve(expUrlStartsWith)),"Browser url ["+actualUrl+"] did not start with expected text ["+expUrlStartsWith+"]");
    }

    @Given("Read page url to variable {string}")
    public void readPageUrlToVariable(String var) {
        this.context.setVar(var, this.context.driver().getUrl());
    }

    @And("Set browser {string}")
    public void setBrowser(String browserName) {
        browserName=this.context.resolve(browserName);
        this.context.setVar(Const.BROWSER,browserName.toUpperCase());
    }
    @Given("Open {string} browser with url {string}")
    public void userOpensTheBrowserWithUrl(String browser, String url) {
        browser=this.context.resolve(browser);
        setBrowser(browser);
        url= this.context.resolve(url);
        this.context.initContext();
        this.context.driver().openUrl(url);
    }

    @Then("Reload/Refresh (the )browser")
    public void reloadBrowser() {
        this.context.driver().refresh();
    }

    @And("Close the browser")
    public void closeTheBrowser(){
        this.context.driver().closeBrowser();
        this.context.removeVar(ContextVars.WAIT_UTILS);
        this.context.removeVar(ContextVars.DRIVER);
    }
    @And("Close all the browser windows")
    public void closeTheBrowsers(){
        this.context.driver().quitSession();
        this.context.removeVar(ContextVars.WAIT_UTILS);
        this.context.removeVar(ContextVars.DRIVER);
    }
    @And("Switch to recently opened window")
    public void switchToLatestWindow(){
        int count = this.context.driver().getWindowsCount();
        LOGGER.debug("Windows count: "+count+", Switching to last window");
        this.context.driver().switchWindowByIndex(count-1);
        LOGGER.debug("Window url after switch: "+this.context.driver().getTitle());
    }
    @And("Read page title to variable {string}")
    public void readPageTitle(String varName){
        this.context.setVar(varName,this.context.driver().getTitle());
    }

    @Then("Print driver {string} log")
    public void printDriverLog(String logType) {
        System.out.println("---------------- "+ logType +" log --------------------");
        for (LogEntry entry : this.context.driver().getDriverLog(logType)) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }
        System.out.println("___________________________________________________");
    }
    @And("Read browser cookies and save as map variable {string}")
    public void readBrowserCookiesAndSaveAsMap(String mapVar) {
        Map<String, String> map = new HashMap<String,String>();
        for (Cookie cookie : this.context.driver().getCookies()) {
            map.put(cookie.getName(), cookie.getValue());
        }
        this.context.setVar(mapVar, map);
    }
    @And("Delete cookie name {string}")
    public void copyVariableAs(String name) {
        name = this.context.resolve(name);
        this.context.driver().deleteCookies(name);
    }

    @And("Read cookie value by name {string} save as {string}")
    public void readCookieByName(String cookieName, String varName) {
        cookieName = this.context.resolve(cookieName);
        varName = this.context.resolve(varName);
        String value = this.context.driver().getCookie(cookieName).getValue();
        this.context.setVar(varName, value);
    }

    @And("Print all cookies")
    public void printAllCookies() {
        Set<Cookie> cookies = this.context.driver().getCookies();
        cookies.forEach(c -> {
            LOGGER.info("Cookie Name: " + c.getName() + ", Value: " + c.getValue());
        });
    }

    @And("Set cookie {string} with value {string}")
    public void setCookieByNameValue(String name, String value) {
        Cookie c = new Cookie(this.context.resolve(name), this.context.resolve(value));
        this.context.driver().setCookie(c);
    }

    @And("Delete/Clear all cookies")
    public void setCookieByNameValue() {
        this.context.driver().clearCookies();
    }

    @And("User opens the browser")
    public void userOpensTheBrowser() {
        this.context.initContext();
    }

    @Then("Switch to frame {string}")
    public void switchToFrame(String locator) {
        this.context.driver().switchFrameByLocatorLookUp(locator);
    }

    @And("Scroll to element {string}")
    public void scrollToElement(String locator) {
        this.context.driver().scrollToElement(locator);
    }

    @And("Scroll to {word} of web page")
    public void scrollToTopBottomOfWebPage(String where) {
        where = this.context.resolve(where);
        where = where.toUpperCase();
        if("TOP".equals(where)){
            this.context.driver().scrollTop();
        }
        else if("BOTTOM".equals(where)){
            this.context.driver().scrollBottom();
        }
        else{
            throw new RuntimeException("Invalid parameter: "+where+", valid options [top/bottom]");
        }
    }

    @And("Click on sidebar navigation {string} and save {string}")
    public void clickOnSidebarNavigationandsave(String btnname,String var) throws InterruptedException {
        OrangeHRM_SideNev orangeHRMSideNev = this.context.getPageObject(OrangeHRM_SideNev.class);
        String Var;
        switch (btnname)
        {
            case "Admin":
                 Var = orangeHRMSideNev.clickOnAdmin();
                break;

            case "PIM":
                Var = orangeHRMSideNev.clickOnPIM();
                break;

            case "Leaves":
                Var = orangeHRMSideNev.clickOnLeaves();
                break;

            case "Time":
                Var = orangeHRMSideNev.clickOnTime();
                break;

            case "Recruitment":
                Var = orangeHRMSideNev.clickOnRecruitment();
                break;

            case "Performance":
                Var = orangeHRMSideNev.clickOnPerformance();
                break;

            case "MyInfo":
                Var = orangeHRMSideNev.clickOnMy_Info();
                break;

            case "Dashboard":
                Var = orangeHRMSideNev.clickOnDashboard();
                break;

            case "Directory":
                Var = orangeHRMSideNev.clickOnDirectory();
                break;

            case "Maintenance":
                Var = orangeHRMSideNev.clickOnMaintenance();
                break;

            case "Buzz":
                Var = orangeHRMSideNev.clickOnBuzz();
                break;


            default:
                throw new IllegalStateException("Filed are not matching "+ btnname);
        }
        this.context.setVar(var,Var);
    }
}

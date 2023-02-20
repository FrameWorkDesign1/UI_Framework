package Core.UI.utils;


import Core.UI.Const;
import Core.UI.grid.IDriver;
import Core.UI.grid.wd.LocatorLookup;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.regex.Pattern;

public class WaitUtils {
    private IDriver driver;
    static final Logger LOGGER = PK_UI_Framework.getLogger(WaitUtils.class);
    public WaitUtils(IDriver driver){
        this.driver=driver;
    }
    public static void wait(int seconds){
        try{
            Thread.sleep(seconds*1000);
        }
        catch (InterruptedException e){
            LOGGER.error("Exception while calling thread.sleep() ",e);
        }
    }
    public WebElement waitForElementToBeVisible(WebElement e, int timeoutInSec){
        return driver.waitInstance(timeoutInSec).until(ExpectedConditions.visibilityOf(e));
    }
    public WebElement waitForElementToBeVisible(String locatorStrategy, int timeoutInSec){
        return waitForElementToBeVisible(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementToBeVisible(By locator, int timeoutInSec){
        return driver.waitInstance(timeoutInSec).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    public WebElement waitForElementPresence(String locatorStrategy, int timeoutInSec){
        return waitForElementPresence(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementPresence(By locator, int timeoutInSec){
        return driver.waitInstance(timeoutInSec).until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    public WebElement waitForElementToBeClickable(WebElement locator, int timeoutInSec){
        return driver.waitInstance(timeoutInSec).until(ExpectedConditions.elementToBeClickable(locator));
    }
    public WebElement waitForElementToBeClickable(String locatorStrategy, int timeoutInSec){
        return waitForElementToBeClickable(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementToBeClickable(By locator, int timeoutInSec){
        return driver.waitInstance(timeoutInSec).until(ExpectedConditions.elementToBeClickable(locator));
    }


    //region With Default Explicit timeout
    public WebElement waitForElementToBeVisible(WebElement e){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.visibilityOf(e));
    }
    public WebElement waitForElementToBeVisible(String locatorStrategy){
        return waitForElementToBeVisible(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementToBeVisible(By locator){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    public WebElement waitForElementPresence(String locatorStrategy){
        return waitForElementPresence(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementPresence(By locator){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    public WebElement waitForElementToBeClickable(WebElement locator){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.elementToBeClickable(locator));
    }
    public WebElement waitForElementToBeClickable(String locatorStrategy){
        return waitForElementToBeClickable(LocatorLookup.getLocator(locatorStrategy));
    }
    public WebElement waitForElementToBeClickable(By locator){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.elementToBeClickable(locator));
    }

    /* Wait for text box to contain given value */
    public boolean waitForElementValueToContainsText(WebElement element,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElementValue(element,expectedText));
    }
    public boolean waitForElementValueToContainsText(String locatorStrategy,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElementValue(LocatorLookup.getLocator(locatorStrategy),expectedText));
    }
    public boolean waitForElementValueToContainsText(By locator,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElementValue(locator,expectedText));
    }

    /* Wait for element to contain given value */
    public boolean waitForElementTextToContainsText(WebElement element,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElement(element,expectedText));
    }
    public boolean waitForElementTextToContainsText(String locatorStrategy,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElementLocated(LocatorLookup.getLocator(locatorStrategy),expectedText));
    }
    public boolean waitForElementTextToContainsText(By locator,String expectedText){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textToBePresentInElementLocated(locator,expectedText));
    }

    /* Wait for element to contain given value */
    public boolean waitForElementTextMatches(WebElement element,String regex){
        return driver.waitInstance(getExplicitTimeOut()).until(textMatches(element,Pattern.compile(regex)));
    }
    public boolean waitForElementTextMatches(String locatorStrategy,String regex){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textMatches(LocatorLookup.getLocator(locatorStrategy),Pattern.compile(regex)));
    }
    public boolean waitForElementTextMatches(By locator,String regex){
        return driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.textMatches(locator,Pattern.compile(regex)));
    }
    public void frameToBeAvailableAndSwitchToIt(String name){
        driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(name));
    }
    public void frameToBeAvailableAndSwitchToIt(WebElement element){
        driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
    }
    public void frameToBeAvailableAndSwitchToIt(int index){
        driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
    }
    public void frameToBeAvailableAndSwitchToIt(By locator){
        driver.waitInstance(getExplicitTimeOut()).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }



    private int getExplicitTimeOut(){
        Object value = PropMgr.get(Const.DEFAULT_EXPLICIT_WAIT_TIMEOUT);
        if(value==null){
            throw new RuntimeException("Property ["+Const.DEFAULT_EXPLICIT_WAIT_TIMEOUT+"] was not set");
        }
        return Integer.parseInt(PropMgr.get(Const.DEFAULT_EXPLICIT_WAIT_TIMEOUT));
    }
    public void waitForCondition(ExpectedCondition condition){
        driver.waitInstance(getExplicitTimeOut()).until(condition);
    }
    //endregion With Default Explicit timeout


    /**
     * An expectation for checking WebElement with given locator has text with a value as a part of
     * it
     *
     * @param element element to compare
     * @param pattern used as expected text matcher pattern
     * @return Boolean true when element has text value containing @value
     */
    private static ExpectedCondition<Boolean> textMatches(final WebElement element, final Pattern pattern) {
        return new ExpectedCondition<Boolean>() {
            private String currentValue = null;

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    currentValue = element.getText();
                    return pattern.matcher(currentValue).find();
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return String
                        .format("text found by %s to match pattern \"%s\". Current text: \"%s\"",
                                element, pattern.pattern(), currentValue);
            }
        };
    }
}

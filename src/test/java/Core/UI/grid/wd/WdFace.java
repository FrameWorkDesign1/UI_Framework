package Core.UI.grid.wd;


import Core.UI.TestContext;
import Core.UI.grid.Browser;
import Core.UI.grid.IDriver;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WdFace implements IDriver {

    private WebDriver driver; // DO not make public
    private TestContext context;
    static final Logger LOGGER = PK_UI_Framework.getLogger(WdFace.class);
    @Deprecated
    public WdFace(Browser browser){
        this.context= new TestContext();
        this.driver = DriverFactory.getInstance(browser, context);
        try {
            this.driver.manage().window().maximize();
        }catch (Exception e){
            LOGGER.warn("Ignoring exception while maximizing, Exception:"+e.getMessage());
        }

    }
    public WdFace(Browser browser, TestContext context){
        this.context = context;
        this.driver = DriverFactory.getInstance(browser, context);
        try {
            this.driver.manage().window().maximize();
        }catch (Exception e){
            LOGGER.warn("Ignoring exception while maximizing, Exception:"+e.getMessage());
        }

    }


    public WebDriver driver(){ // DO not make public
        return driver;
    }

    @Override
    public <T> T getPageObject(Class<T> pageClassToProxy) {
        return PageFactory.initElements(driver(), pageClassToProxy);
    }

    public void openUrl(String url){
        //TODO: Init driver
        driver().get(url);
        LOGGER.info("Done Opening Page Url ["+url+"]");
    }
    public void closeBrowser(){
        driver().close();
        LOGGER.info("DOne closing browser..");
    }
    public void quitSession(){
        driver().quit();
        LOGGER.info("Done quiting WebDriver session..");
    }
    public String getUrl(){
        String url = driver().getCurrentUrl();
        LOGGER.info("Page Url ["+url+"] ");
        return url;
    }
    public String getTitle(){
        String title =  driver().getTitle();
        LOGGER.info("Page title ["+title+"] ");
        return title;
    }
    public void click(String locator ){
        try{
            getElement(locator).click();
            LOGGER.info("Done clicking on element ["+locator + "]");
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed Clicking on element ["+locator +"]",ex);
            throw ex;
        }
    }
    public void click(WebElement element){
        try{
            element.click();
            LOGGER.info("Done clicking on element ["+element + "]");
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed Clicking on element ["+element +"]",ex);
            throw ex;
        }
    }
    public void click(String locator,boolean tryJsAsFallback){
        try{
            getElement(locator).click();
            LOGGER.info("Done clicking on element ["+locator +"]");
        }
        catch(WebDriverException ex){
            LOGGER.info("Skipping WebDriver clicking on element ["+locator+"], Retrying with JS Click");
            jsClick(getElement(locator));
        }
    }
    public void click(WebElement element,boolean tryJsAsFallback){
        try{
            element.click();
            LOGGER.info("Done clicking on element ["+element +"]");
        }
        catch(WebDriverException ex){
            LOGGER.info("Skipping WebDriver clicking on element ["+element+"], Retrying with JS Click");
            jsClick(element);
        }
    }
    public void jsClick(String locator){
        try {
            WebElement element = getElement(locator);
            performScript("arguments[0].click()", element);
            LOGGER.info("Done JS Click on element["+element+"]");
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed executing JavaScript Click on element["+locator+"], Error ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void jsClick(WebElement element){
        try {
            performScript("arguments[0].click()", element);
            LOGGER.info("Done JS Click on element["+element+"]");
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed executing JavaScript Click on element["+element+"], Error ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public Object readDom(String script){
        try {
            if (!script.toLowerCase().startsWith("return "))
                script = "return " + script;
            Object retValue =  executeScript(script);
            LOGGER.info("Done reading value ["+retValue+"] from DOM ["+ retValue+"]");
            return retValue;
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to read DOM, Error["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public Object executeScript(String script){
        try {
            Object retValue = ((JavascriptExecutor) driver()).executeScript(script);
            LOGGER.info("Done script execution, Result ["+retValue+"]  from JS ["+script+"]");
            return retValue;
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to execute JS ["+script+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }

    }
    public void executeScript(String script,Object o){
        try {
            ((JavascriptExecutor) driver()).executeScript(script,o);
            LOGGER.info("Done script execution. JS ["+script+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to execute JS ["+script+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }

    }
    public Object performScript(String script, WebElement element){
        try{
            Object retValue =  ((JavascriptExecutor) driver()).executeScript(script,element);
            LOGGER.info("Done executing JS script ["+script+"], Result["+retValue+"] on element ["+element+"]");
            return retValue;
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed executing JS ["+script+"] on Element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void hoverOnElement(WebElement element){
        try {
            Actions action = new Actions(driver());
            action.moveToElement(element).build().perform();
            LOGGER.info("Done hovering element["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to hover element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void hoverOnElement(String locator){
        try {
            Actions action = new Actions(driver());
            action.moveToElement(getElement(locator)).build().perform();
            LOGGER.info("Done hovering element["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to hover element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void clear(String locator){
        try {
            getElement(locator).clear();
            LOGGER.info("Done clearing  element["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to clear element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void clear(WebElement element){
        try {
            element.clear();
            LOGGER.info("Done clearing  element["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to clear element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public Object type(WebElement element, CharSequence text){
        try {
            element.sendKeys(text);
            LOGGER.info("Done typing value ["+text+"] in  element["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to type value ["+ text +"] in element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
        return null;
    }
    public void type(String locator,CharSequence text){

        try {
            type(getElement(locator),text);
            LOGGER.info("Done typing value ["+text+"] in  element["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed to type value ["+ text +"] in element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }

    private WebElement getElement(String locator) {
        return driver().findElement(LocatorLookup.getLocator(locator));
    }



    public void submit(WebElement element){
        try{
        element.submit();
        LOGGER.info("Done submitting form element ["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed submitting element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void submit(String locator){
        try{
            getElement(locator).submit();
            LOGGER.info("Done submitting form element ["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed submitting element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }

    public void typePassword(WebElement element,String text){
        try{
//            element.sendKeys(EncryptDecrypt.decryptPassword(text));
            LOGGER.info("Done typing ****** in element ["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed typing password in element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }

    }
    public void typePassword(String locator,String text){
        try{
//            getElement(locator).sendKeys(EncryptDecrypt.decryptPassword(text));
            LOGGER.info("Done typing ****** in element ["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed typing password in element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }

    }
    public void selectByValue(WebElement element,String value){
        try {
            new Select(element).selectByValue(value);
            LOGGER.info("Done selecting option by value ["+value+"], Element ["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed selecting option by value ["+value+"], element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void selectByValue(String locator,String value){
        try {
            new Select(getElement(locator)).selectByValue(value);
            LOGGER.info("Done selecting option by value ["+value+"], Element ["+locator+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed selecting option by value ["+value+"], element ["+locator+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void selectByVisibleTextContains(String locator,String subText){
        selectByVisibleTextContains(getElement(locator),subText);
    }
    public void selectByVisibleTextContains(WebElement element,String subText){
        Select s = new Select(element);
        List<WebElement> options = s.getOptions();
        List<String> actualVisibleText= new ArrayList<String>();
        for(WebElement option:options){
            if(option.getText().contains(subText)){
                actualVisibleText.add(option.getText());
            }
        }
        if(actualVisibleText.size()==0){
            LOGGER.info("Failed selecting option, No options present in list containing sub text ["+subText+"] element["+element+"]");
            throw new AssertionError("Failed to find dropdown list with visible text containing ["+subText+"] ");
        }
        if(actualVisibleText.size()>2){
            LOGGER.info("Multiple matching options found: "+actualVisibleText);
            LOGGER.info("Selecting first match..");
        }
        s.selectByVisibleText(actualVisibleText.get(0));
        LOGGER.info("Done selecting option ["+actualVisibleText.get(0)+"] from list element["+element+"]");
    }
    public void selectByVisibleText(String locator,String visibleText){
        selectByVisibleText(getElement(locator),visibleText);
    }

    public void selectByVisibleText(WebElement element,String visibleText){
        try {
            new Select(element).selectByVisibleText(visibleText);
            LOGGER.info("Done selecting option by visible text ["+visibleText+"], Element ["+element+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed selecting option by visible text ["+visibleText+"], element ["+element+"], Error: ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void selectByIndex(String locator,int index){

        new Select(getElement(locator)).selectByIndex(index);
    }
    public Object selectByIndex(WebElement element, int index){
        new Select(element).selectByIndex(index);
        return null;
    }
    public boolean isDisplayed(String locator){
        try{
            return getElement(locator).isDisplayed();
        }
        catch (WebDriverException ex){
            return false;
        }
    }
    public boolean isDisplayed(WebElement element){
        try{
            return element.isDisplayed();
        }
        catch (WebDriverException ex){
            return false;
        }
    }

    @Override
    public void doubleClick(WebElement element) {
        try{
            Actions actions = new Actions(driver);
            actions.doubleClick().build().perform();
            LOGGER.info("Done double clicking on element ["+element + "]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed double clicking on element ["+element +"]",ex);
            throw ex;
        }
    }

    @Override
    public void doubleClick(WebElement element, int xOffset, int yOffset) {
        try{
            Actions actions = new Actions(driver);
            actions.moveToElement(element,xOffset,yOffset).doubleClick().build().perform();
            LOGGER.info("Done double clicking on element ["+element + "] with xOffset ["+xOffset+"] and yOffset ["+yOffset+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed double clicking on element ["+element + "] with xOffset ["+xOffset+"] and yOffset ["+yOffset+"]",ex);
            throw ex;
        }
    }

    @Override
    public void contextClick(WebElement element) {
        try{
            Actions actions = new Actions(driver);
            actions.contextClick(element).build().perform();
            LOGGER.info("Done context clicking on element ["+element + "]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed context clicking on element ["+element +"]",ex);
            throw ex;
        }
    }

    @Override
    public void doubleClick(String locator) {
        try{
            doubleClick(getElement(locator));
            LOGGER.info("Done double clicking on locator ["+locator + "]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed double clicking on locator ["+locator +"]",ex);
            throw ex;
        }
    }

    @Override
    public void doubleClick(String locator, int xOffset, int yOffset) {
        try{
            doubleClick(getElement(locator),xOffset,yOffset);
            LOGGER.info("Done double clicking on locator ["+locator + "] with xOffset ["+xOffset+"] and yOffset ["+yOffset+"]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed double clicking on locator ["+locator + "] with xOffset ["+xOffset+"] and yOffset ["+yOffset+"]",ex);
            throw ex;
        }
    }

    @Override
    public void contextClick(String locator) {
        try{
            contextClick(getElement(locator));
            LOGGER.info("Done context clicking on locator ["+locator + "]");
        }
        catch (WebDriverException ex){
            LOGGER.error("Failed context clicking on locator ["+locator +"]",ex);
            throw ex;
        }
    }

    public String getText(WebElement elem){
        String text = null;
        try{

            if(elem.getTagName().equalsIgnoreCase("input") && elem.getAttribute("type").equalsIgnoreCase("text")){
                text = elem.getAttribute("value");
            }
            else {
                text = elem.getText();
            }
            LOGGER.info("Done reading text ["+text+"] from element ["+elem+"]");
        }
        catch (WebDriverException ex){
            LOGGER.info("Failed reading text from element ["+elem+"], Error ["+ex.getMessage()+"]",ex);
            throw ex;
        }
        return text;
    }
    public String getText(String locator){
        try{
            return getText(getElement(locator));
        }
        catch (WebDriverException ex){
            LOGGER.info("Failed reading text from element ["+locator+"], Error ["+ex.getMessage()+"]",ex);
            throw ex;
        }
    }
    public void acceptAlert(){
        LOGGER.info("Accepting Alert message");
        driver.switchTo().alert().accept();
    }
    public void dismissAlert(){
        LOGGER.info("Dismissing Alert message");
        driver.switchTo().alert().dismiss();
    }
    public String getAlertText(){
        LOGGER.info("Reading Alert message");
        return driver.switchTo().alert().getText();
    }
    public int getWindowsCount(){
        return driver.getWindowHandles().size();
    }
    public void switchWindows(String nameOrHandle){
        LOGGER.info("Switching windows by name/handle id ["+nameOrHandle+"]");
         driver.switchTo().window(nameOrHandle);
    }
    public void switchWindowByIndex(int index){
        Set<String> handles = driver.getWindowHandles();
        String handle = new ArrayList<String>(handles).get(index);
        this.driver.switchTo().window(handle);
    }
    public void switchFrame(WebElement element){
        LOGGER.info("Switching Frame by element ["+element+"]");
        this.context.waitUtils().frameToBeAvailableAndSwitchToIt(element);
    }
    public void switchFrame(String idOrName){
        LOGGER.info("Switching frame by name/id ["+idOrName+"]");
        this.context.waitUtils().frameToBeAvailableAndSwitchToIt(idOrName);
    }
    public void switchFrame(int index){
        LOGGER.info("Switching frame by index ["+index+"]");
        this.context.waitUtils().frameToBeAvailableAndSwitchToIt(index);
    }

    public void switchFrameByLocatorLookUp(String locator){
        LOGGER.info("Switching frame by locator ["+locator+"]");
        this.context.waitUtils().frameToBeAvailableAndSwitchToIt(getElement(locator));
    }
    public void switchToDefaultContent(){
        LOGGER.info("Switching to default content");
        driver.switchTo().defaultContent();
    }
    public void switchToParentFrame(){
        LOGGER.info("Switching to parent frame");
        driver.switchTo().parentFrame();
    }

    public String getAttribute(WebElement element, String attribName){
        LOGGER.info("Reading attribute ["+attribName+"] from element ["+element+"]");
        return element.getAttribute(attribName);
    }
    public String getCssAttribute(WebElement element, String attribName){
        LOGGER.info("Reading CSS attribute ["+attribName+"] from element ["+element+"]");
       return element.getCssValue(attribName);
    }
    public String getAttribute(String locatorLookup, String attribName){
        return getAttribute(getElement(locatorLookup),attribName);
    }
    public String getCssAttribute(String locatorLookup, String attribName){
        return getCssAttribute(getElement(locatorLookup),attribName);
    }

    public void closeWindowByTitle(String title){
        //TODO:
        throw new RuntimeException("Not implemented yet!");
    }
    public void ZoomOut(){
        LOGGER.info("Zooming out inside the page");
        ((JavascriptExecutor)driver()).executeScript( "document.body.style.zoom = '0.5'");
    }
    public void ZoomIn(){
        LOGGER.info("Zooming in inside the page");
        ((JavascriptExecutor)driver()).executeScript( "document.body.style.zoom = '1'");
    }
    public byte[] getScreenshotAsByte(){
        return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
    }
    public String getScreenshotAsString(){
        return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
    }
    public File getScreenshotAsFile(){
        return ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
    }

    @Override
    public void refresh() {
        LOGGER.info("Refreshing page");
        driver().navigate().refresh();
    }

    @Override
    public WebDriverWait waitInstance(int timeInSeconds) {
        return new WebDriverWait(driver(), timeInSeconds);
    }

    @Override
    public void clearCookies() {
        driver().manage().deleteAllCookies();
    }
    //region cookies
    @Override
    public void deleteCookies(String name) {
        driver().manage().deleteCookieNamed(name);
    }
    @Override
    public void setCookie(Cookie c) {
        driver().manage().addCookie( c);
    }
    @Override
    public Cookie getCookie(String name) {
        return driver().manage().getCookieNamed(name);
    }
    @Override
    public Set<Cookie> getCookies() {
        return driver().manage().getCookies();
    }
    //endregion cookies
    @Override
    public Actions getActions() {
        return new Actions(this.driver);
    }

    @Override
    public List<LogEntry> getDriverLog(String logType) {
        return driver.manage().logs().get(logType).getAll();
    }

    @Override
    public void scrollToElement(String locatorLookup) {
        try{
            WebElement element = getElement(locatorLookup);
            performScript("arguments[0].scrollIntoView();",element);
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed scroll into view for element ["+locatorLookup +"]",ex);
            throw ex;
        }
    }
    @Override
    public void scrollToElement(WebElement element) {
        try{
            performScript("arguments[0].scrollIntoView();",element);
        }
        catch(WebDriverException ex){
            LOGGER.error("Failed scroll into view for element ["+element +"]",ex);
            throw ex;
        }
    }
    @Override
    public void scrollBy(int xOffset,int yOffset){
        executeScript("window.scrollBy("+xOffset+","+yOffset+")");
    }
    @Override
    public void scrollTo(int x,int y){
        executeScript("window.scrollTo("+x+","+y+")");
    }
    @Override
    public void scrollTop(){
        executeScript("window.scrollTo(0,0)");
    }
    @Override
    public void scrollBottom(){
        executeScript("window.scrollTo(0,document.body.scrollHeight)");
    }

}

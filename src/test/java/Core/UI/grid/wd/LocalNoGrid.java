package Core.UI.grid.wd;

import Core.UI.TestContext;
import Core.UI.grid.Browser;
import Core.UI.grid.IGrid;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import java.net.URL;

public class LocalNoGrid extends GridBaseClass implements IGrid {
    private static final Logger LOGGER = PK_UI_Framework.getLogger(LocalNoGrid.class);
    @Override
    public DesiredCapabilities getCapabilities(Browser browser, TestContext context) {
        DesiredCapabilities caps = null;
        switch(browser){

            case INTERNET_EXPLORER:
                caps = DesiredCapabilities.internetExplorer();
                break;
            case MS_EDGE:
                caps = DesiredCapabilities.edge();
                break;
            case CHROME:
                caps = getChromeCaps();
                break;
            case FIREFOX:
                caps = getFirefoxCaps();
                break;
            case SAFARI:
                caps = DesiredCapabilities.safari();
                break;
            case ANDROID:
            case IOS:
                throw new IllegalStateException("Android/IOS browser not supported for local ["+browser+"]");
            default :
                throw new IllegalStateException("Unsupported browser for local ["+browser+"]");
        }
        caps = getCommonCaps(caps);
        LOGGER.info("Browser: "+browser+", Capabilities : "+ caps);
        return  caps;
    }



    @Override
    public URL getHubUrl() {
        // no grid hub url
        return null;
    }

    @Override
    public Class<? extends WebDriver> getDriverClass(Browser browser) {
        switch (browser){
            case INTERNET_EXPLORER:
                return InternetExplorerDriver.class;
            case MS_EDGE:
                return EdgeDriver.class;
            case CHROME:
                return ChromeDriver.class;
            case FIREFOX:
                return FirefoxDriver.class;
            case SAFARI:
                return SafariDriver.class;
            case ANDROID:
            case IOS:
                 throw new IllegalStateException("Android/IOS browser not supported for local ["+browser+"]");
            default :
                throw new IllegalStateException("Unsupported browser for local ["+browser+"]");
        }
    }

    @Override
    public void onDriverInit(WebDriver driver) {
        LOGGER.info("Maximizing window size..");
        try{driver.manage().window().maximize();}catch (Exception e){ }
    }
}

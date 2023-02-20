package Core.UI.grid.wd;


import Core.UI.Const;
import Core.UI.ContextVars;
import Core.UI.TestContext;
import Core.UI.grid.Browser;
import Core.UI.grid.IGrid;
import Core.UI.utils.PK_UI_Framework;
import Core.UI.utils.PropMgr;
import Core.UI.utils.Reflection;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Constructor;
import java.net.URL;

public class DriverFactory {
    private static final Logger LOGGER = PK_UI_Framework.getLogger(DriverFactory.class);


    public static WebDriver getInstance(Browser b, TestContext context){

        IGrid grid =  Reflection.newInstance(PropMgr.get(Const.TEST_GRID));
        DesiredCapabilities caps = grid.getCapabilities(b,context);
        WebDriver driver = generateDriver(grid,caps,b);
        if(grid.getHubUrl()!=null){
            context.setVar(ContextVars.DRIVER_SESSION_ID,(((RemoteWebDriver) driver).getSessionId()));
        }
        return driver;
    }

    private static WebDriver generateDriver(IGrid grid, DesiredCapabilities caps, Browser b) {

        WebDriver driver;
        URL hubUrl = grid.getHubUrl();

        try {
            if(hubUrl==null) {
                Constructor<? extends WebDriver> con = grid.getDriverClass(b).getConstructor(Capabilities.class);
                driver = con.newInstance(caps);
            }
            else {
                LOGGER.info("Grid: "+ grid.getClass().getName());
                LOGGER.info("Capabilities: "+ caps);
                Constructor<? extends WebDriver> con = grid.getDriverClass(b).getConstructor(URL.class,Capabilities.class);
                driver = newGridDriver(con,hubUrl,caps,15*60);
            }
            grid.onDriverInit(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Browser, Error: "+ e.getMessage(),e);
        }
        return driver;
    }

    private static WebDriver newGridDriver(Constructor<? extends WebDriver> con, URL hubUrl, DesiredCapabilities caps, int timeoutSeconds) {
        int attemptCount=1;
        int maxAttempts = PropMgr.getInt(Const.GRID_MAX_ATTEMPTS, 5);
        Exception lastException;
        do {
            try {
                Thread.sleep(2000);
                LOGGER.info("Attempt #" + attemptCount + " ;  Launching browser on grid.. ");
                WebDriver driver = con.newInstance(hubUrl, caps);
                LOGGER.info("Attempt #" + attemptCount + " ;  Done, Launching browser on grid!");
                return driver;
            } catch (Exception e) {
                LOGGER.error("Failed Attempt #" + attemptCount + " : Failed to launch browser on grid, Error: " + e.getMessage(), e);
                lastException = e;
            }
            attemptCount++;
        } while(attemptCount<=maxAttempts);
        throw new RuntimeException("Failed to launch browser on grid after "+maxAttempts+" attempts, Last Error: "+ lastException.getClass()+" : "+lastException.getMessage(), lastException);
    }
}

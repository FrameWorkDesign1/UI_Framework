package Core.UI.grid;


import Core.UI.TestContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public interface IGrid {
    DesiredCapabilities getCapabilities(Browser browser, TestContext context);
    URL getHubUrl();
    Class<? extends WebDriver> getDriverClass(Browser browser);
    void onDriverInit(WebDriver driver);

}

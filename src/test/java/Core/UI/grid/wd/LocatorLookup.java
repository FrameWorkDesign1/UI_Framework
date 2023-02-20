package Core.UI.grid.wd;

import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Locale;

public class LocatorLookup {
    private static final Logger LOGGER = PK_UI_Framework.getLogger(LocatorLookup.class);
    public static By getLocator(String txtLocatorWithStrategy){
        if(!txtLocatorWithStrategy.toUpperCase(Locale.ROOT).matches("(CSS=.*)|(XPATH=.*)|(LINKTEXT=.*)|(PARTIAL_LINKTEXT=.*)|(ID=.*)|(CLASSNAME=.*)|(TAG_NAME=.*)|(NAME=.*)")){
            LOGGER.info("Locator ["+txtLocatorWithStrategy+"] , using default locating strategy [CSS]");
            txtLocatorWithStrategy="css="+txtLocatorWithStrategy;
        }
        int index = txtLocatorWithStrategy.indexOf("=");

        String type = txtLocatorWithStrategy.substring(0, index).trim();
        String locatorText = txtLocatorWithStrategy.substring(index+1).trim();
        By by = null;
        switch(type.toUpperCase()){
            case "CSS":
                by = By.cssSelector(locatorText);
                break;
            case "XPATH":
                by = By.xpath(locatorText);
                break;
            case "LINKTEXT":
                by = By.linkText(locatorText);
                break;
            case "PARTIAL_LINKTEXT":
                by = By.partialLinkText(locatorText);
                break;
            case "ID":
                by = By.id(locatorText);
                break;
            case "CLASSNAME":
                by = By.className(locatorText);
                break;
            case "TAG_NAME":
                by = By.tagName(locatorText);
                break;
            case "NAME":
                by = By.name(locatorText);
                break;
            default:
                throw new RuntimeException("Invalid locating strategy ["+type+"], Please refer documentation");
        }
        return  by;
    }
}

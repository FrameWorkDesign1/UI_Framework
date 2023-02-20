package Core.UI.grid.wd;

import Core.UI.Const;
import Core.UI.utils.PK_UI_Framework;
import Core.UI.utils.PropMgr;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GridBaseClass {

    private static final Logger LOGGER = PK_UI_Framework.getLogger(GridBaseClass.class);
    protected DesiredCapabilities getChromeCaps() {
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        ChromeOptions options = new ChromeOptions();

        caps = getCommonCaps(caps);

        Map<String,Object> chromePref = PropMgr.getPropertiesByPrefix(Const.PREFIX_CHROME_PREFS);
        if(!chromePref.isEmpty()) {
            options.setExperimentalOption("prefs", chromePref);
        }
        Map<String,Object> capExperimentalOptions = PropMgr.getPropertiesByPrefix(Const.PREFIX_CHROME_OPTION);
        for(String optionName:capExperimentalOptions.keySet()){
            Object value = capExperimentalOptions.get(optionName);

            if(value.toString().toLowerCase().equals("false")|| value.toString().toLowerCase().equals("true")){
                value=Boolean.parseBoolean(value.toString());
            }
            else if(value.toString().startsWith("{")){
                try {
                    Map<String,Object> result = new ObjectMapper().readValue(value.toString(), HashMap.class);
                    value=result;
                } catch (IOException e) {
                    LOGGER.warn("Skipping error:"+e.getClass()+" while setting chrome option: "+optionName+" -> "+value.toString(),e);
                }

            }
            options.setExperimentalOption(optionName, value);
        }

        String [] chromeArgs = PropMgr.getArray(Const.CHROME_ARGUMENT," ");
        if(chromeArgs!=null)
            options.addArguments(chromeArgs);
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        return caps;
    }
    protected DesiredCapabilities getFirefoxCaps(){
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps = DesiredCapabilities.firefox();
        caps = getCommonCaps(caps);
        FirefoxProfile p = new FirefoxProfile();
        Map<String,Object> firefoxPreference = PropMgr.getPropertiesByPrefix(Const.PREFIX_FIREFOX_PREF);
        if(firefoxPreference!=null && firefoxPreference.size()>0){
            for(String key: firefoxPreference.keySet()) {
                p.setPreference(key, firefoxPreference.get(key).toString());
            }
        }
        caps.setCapability(FirefoxDriver.PROFILE, p);
        return caps;
    }
    protected DesiredCapabilities getCommonCaps(DesiredCapabilities caps)  {
        Map<String,Object> capMap = PropMgr.getPropertiesByPrefix(Const.PREFIX_CAPABILITY);
        for(String capName:capMap.keySet()){
            if(capMap.get(capName).toString().startsWith("{")){
                try {
                    caps.setCapability(capName, new ObjectMapper().readValue(capMap.get(capName).toString(), HashMap.class));
                }
                catch (IOException e){
                    throw new RuntimeException("Invalid json map syntax for capability: "+capName,e);
                }
            }
            else {
                caps.setCapability(capName, capMap.get(capName));
            }
        }
        return caps;
    }
}

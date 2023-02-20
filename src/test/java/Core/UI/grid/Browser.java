package Core.UI.grid;

import java.util.Arrays;
import java.util.Optional;

public enum Browser {
    INTERNET_EXPLORER("internet explorer"),
    MS_EDGE("MicrosoftEdge"),
    CHROME("chrome"),
    FIREFOX("firefox"),
    SAFARI("safari"),
    ANDROID("android"),
    IOS("iPhone");


    String selBrowserName;
    private Browser(String selBrowserName){
        this.selBrowserName = selBrowserName;
    }
    public String getSeleniumName(){
        return this.selBrowserName;
    }
    public static Browser findByName(String selBrowserName){
        Optional<Browser> browser = Arrays.stream(Browser.values()).filter(b -> b.getSeleniumName().equals(selBrowserName)).findFirst();
        if(!browser.isPresent()){
            throw new IllegalStateException("Unkown browser ["+selBrowserName+"]");
        }
        return browser.get();
    }
}

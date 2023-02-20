package Core.UI;


import Core.UI.grid.IDriver;
import Core.UI.grid.wd.WdFace;
import Core.UI.utils.PK_UI_Framework;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public abstract class BaseWdPage  {

    protected TestContext context;
    private WebDriver driver ;
    static final Logger LOGGER = PK_UI_Framework.getLogger(BaseWdPage.class);
    protected IDriver driver(){
        return this.context.driver();
    }
    public BaseWdPage(WebDriver driver) {
        this.driver = this.driver;
    }
    abstract public void waitForLoad();
    abstract public void pageIsValid();
    public String getTitle(){
        return driver.getTitle();
    }
    public void refresh(){
        context.driver().refresh();
    }

    public static <T extends BaseWdPage> T getPageObject(TestContext context, Class<? extends BaseWdPage> pageClassToProxy) {
        WebDriver d =  ((WdFace)context.driver()).driver();
        T object = (T) PageFactory.initElements(d, pageClassToProxy);
        object.context=context;
        object.waitForLoad();
        object.pageIsValid();
        return object;
    }
    public  <T extends BaseWdPage> T getPageObject(Class<? extends BaseWdPage> pageClassToProxy) {
        WebDriver d =  ((WdFace)context.driver()).driver();
        T object = (T) PageFactory.initElements(d, pageClassToProxy);
        object.context=context;
        object.waitForLoad();
        object.pageIsValid();
        return object;
    }
    public void reloadPageObject() {
        WebDriver d =  ((WdFace)this.context.driver()).driver();
        PageFactory.initElements(d,this);
    }
    public void wait(int seconds){
        this.context.waitUtils().wait(seconds);
    }

}

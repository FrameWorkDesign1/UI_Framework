package Core.UI;

import Core.UI.grid.Browser;
import Core.UI.grid.IDriver;
import Core.UI.grid.wd.WdFace;
import Core.UI.utils.*;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class TestContext {

    static final Logger LOGGER = PK_UI_Framework.getLogger(TestContext.class);
    private static Map global = new HashMap<Object, Object>();
    private Map<Object, Object> vars = new HashMap<Object, Object>();
    public TestContext(){
        TestThread.setContext(this);

    }
    public Scenario scenario(){
        return (Scenario) vars.get(ContextVars.SCENARIO);
    }
    public IDriver driver(){
        IDriver driver = (IDriver) vars.get(ContextVars.DRIVER);
        if(driver==null){
            LOGGER.debug("Driver has not initialized yet");
        }
        return driver;
    }
    public  void initContext() {
        IDriver driver = (IDriver) this.vars.get(ContextVars.DRIVER);
        if (driver == null){
            Object txtBrowser = vars.get(Const.BROWSER);
            LOGGER.info(Const.BROWSER+" : "+ txtBrowser);
            Browser b = null;
            if (txtBrowser != null) {
                b = Browser.valueOf(txtBrowser.toString().toUpperCase());
            } else {
                b = Browser.valueOf(PropMgr.get(Const.BROWSER).toUpperCase());
            }
            driver = new WdFace(b,this);
            vars.put(ContextVars.DRIVER, driver);
//            setTestType(TestType.UI);
        }
//        vars.put(ContextVars.WAIT_UTILS,new WaitUtils(driver));
    }
    public void setScenario(Scenario scenario) {
        vars.put(ContextVars.SCENARIO,scenario);
    }

    public void setVars(Map vars){
        for (Object key : vars.keySet()){
            this.vars.put(key, vars.get(key));
        }
    }
    public void setVar(Object name, Object value){
        vars.put(name, value);
    }
    public void removeVar(Object var){
        vars.remove(var);
    }
    public Map vars(){
        return vars;
    }
    public Object var(Object name){
        if(name.toString().contains("#($")){
            return this.resolve(name.toString());
        }
        else {
            Object value = vars.get(name);
            if (value instanceof String) {
                if(value.toString().contains("#($")) {
                    return this.resolve(value.toString());
                }
                else{
                    return value;
                }
            } else {
                return vars.get(name);
            }
        }
    }
    public WaitUtils waitUtils(){
        return (WaitUtils) vars.get(ContextVars.WAIT_UTILS);
    }

    public  <T extends BaseWdPage> T getPageObject( Class<? extends BaseWdPage> pageClassToProxy) {
        return BaseWdPage.getPageObject(this,pageClassToProxy);
    }

    public void cleanUp() {
        try{
            driver().quitSession();
        }
        catch (Exception e){//ignore
        }
        vars.remove(ContextVars.WAIT_UTILS);
        vars.remove(ContextVars.DRIVER);
    }

    public String resolve(String text) {

        if(text.startsWith("!")){
            text = text.substring(1);
            Object v = vars.get(text);
            if(v==null){
                return text;
            }
            if(v!=null && v instanceof String){
               text = (String) v;
            }

        }
        return PropMgr.resolveWithProperties(text, this.vars());
    }

    public Map<String, String> resolveMapValues(Map<String, String> source) {
        Map<String,String> target = new HashMap<String,String>();
        for(String key: source.keySet()){
            target.put(key, resolve(source.get(key)));
        }
        return target;
    }
}

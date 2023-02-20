package Core.UI.utils;


import Core.UI.TestContext;

import java.nio.charset.StandardCharsets;

public class TestThread {
    private static ThreadLocal<TestContext> context= new ThreadLocal<TestContext>();

    public static boolean isScenarioInitialized(){
        if(context.get()==null){
            return context.get().scenario()!=null;
        }
        return false;
    }
    public static void setContext(TestContext context){
        TestThread.context.set(context);
    }
    public static TestContext getContext(){
        return TestThread.context.get();
    }
    public static boolean isContextSet(){
        return TestThread.context.get()!=null;
    }

    public static boolean logMessage(byte[] bytes) {
        if(context.get().scenario()!=null){
            context.get().scenario().write(new String(bytes, StandardCharsets.UTF_8));
            return true;
        }
        return false;
    }
}

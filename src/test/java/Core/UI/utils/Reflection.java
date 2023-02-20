package Core.UI.utils;

import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;

public class Reflection {

    static final Logger LOGGER = PK_UI_Framework.getLogger(Reflection.class);
    public static <T> T newInstance(String fullyQualifyedClass){
        if(fullyQualifyedClass==null){
            return null;
        }
        try{
            Class<T> clazz = (Class<T>) Class.forName(fullyQualifyedClass);
            return clazz.getDeclaredConstructor().newInstance();
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
            LOGGER.error("Error creating instance via reflection ["+fullyQualifyedClass+"]",e);
        }
        return null;
    }
    public static void changeAnnotation(Class<?> clazz,Class annotationClazz, String key, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
        Annotation options = clazz.getAnnotation(annotationClazz);
        InvocationHandler proxyHandler = Proxy.getInvocationHandler(options);
        Field f = proxyHandler.getClass().getDeclaredField("memberValues");
        f.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) f.get(proxyHandler);
        memberValues.remove(key);
        memberValues.put(key,newValue);
    }
}

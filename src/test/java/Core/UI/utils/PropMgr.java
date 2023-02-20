package Core.UI.utils;


import Core.UI.Const;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropMgr {

    private static final Logger LOGGER;
    private static boolean propertiesLoaded=false;


    static {
        LOGGER= PK_UI_Framework.getLogger(PropMgr.class);
        initProperties();
    }
    private static Logger logger(){
        return LOGGER;
    }
    public static void initProperties(){
        if(!PropMgr.propertiesLoaded) {
            loadConfigsToSystemProp(Const.PROP_PATH);
            String envSpecifConfig = PropMgr.get(Const.ENV_CONFIG_PATH);
//            String envSpecifConfig = PropMgr.resolve(Const.ENV_CONFIG_PATH);
            logger().info("Loading properties file: " + envSpecifConfig);
            if (envSpecifConfig != null) {
                String[] loadProperties = envSpecifConfig.split(";");
                for (String envSpecificProp : loadProperties) {
                    loadConfigsToSystemProp(envSpecificProp.trim());
                }
            }
        }
    }

    public static void loadConfigsToSystemProp(String file) {
        try {
            setSystemProperties(FilesUtil.getInputStream(file));
        } catch (IOException e) {
            logger().error("Ignoring loading system properties from file: " + file, e);
        }
    }

    public static void setSystemProperty(String propName, String value) {
        System.setProperty(propName, value);
    }

    public static void setSystemProperties(InputStream inputStream) throws IOException {
        Properties props = new Properties();
        props.load(inputStream);
        for (Object key : props.keySet()) {
            if (!System.getProperties().containsKey(key.toString())) {
                System.setProperty(key.toString(), props.get(key).toString());
            } else {
                logger().debug("System property: " + key + " overriding value from config file");
            }
        }

    }

    public static String get(String key) {

        String value = System.getenv(key);
        if (value != null) {
            logger().debug("Env Property [" + key + "] : " + value);
            return resolve(value);
        }
        value = System.getProperty(key);
        if (value != null) {
            logger().debug("System Property [" + key + "] : " + value);
            return resolve(value);
        }
        logger().debug("Property value not found for : " + key + ", returning null");
        return null;
    }

    public static String[] getArray(String key, String delimiter) {

        String value = resolve(get(key));
        if (value != null) {
            return Arrays.stream(value.split(delimiter)).map(String::trim).toArray(String[]::new);
        }
        return null;
    }

    public static String[] getArray(String key) {
        return getArray(key, ";");
    }

    public static Map<String, Object> getPropertiesByPrefix(String prefix) {
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            if (key.toString().startsWith(prefix)) {
                map.put(key.replace(prefix, ""), resolve(env.get(key.toString())));
            }
        }

        Properties sysProp = System.getProperties();
        for (Object key : sysProp.keySet()) {
            if (key.toString().startsWith(prefix)) {
                map.put(key.toString().replace(prefix, ""), resolve(sysProp.get(key.toString()).toString()));
            }
        }
        return map;
    }

    public static String resolve(String text) {
        // #($var)
        if (text == null) {
            return null;
        }
        if (!text.contains("#($")) {
            logger().debug("Resolved Value: " + text);
            return text;
        }
        Matcher m = Pattern.compile(Const.VARIABLE_REGEX).matcher(text);
        if (m.find()) {
            logger().debug("Match Found Now Resolving: " + (m.group(3)));
            return resolve(text.replace(m.group(1), get(m.group(3))));
        } else {
            logger().debug("Resolved Value: " + text);
            return text;
        }
    }

    private static String get(String key, Map prop) {
        if (key == null) {
            return null;
        }
        if (key.isEmpty()) {
            return "";
        }
        String value = null;

        if (prop.get(key) != null) {
            value = prop.get(key).toString();
            logger().debug("Local Property [" + key + "] : " + value);
            return value;
        }
        value = System.getenv(key);
        if (value != null) {
            logger().debug("Env Property [" + key + "] : " + value);
            return value;
        }
        value = System.getProperty(key);
        if (value != null) {
            logger().debug("System Property [" + key + "] : " + value);
            return value;
        }
        logger().debug("Property value not found for : " + key + ", returning key");
        return key;
    }

    public static String resolveWithProperties(String text, Map prop) {
        // #($var)
        if (text == null) {
            return null;
        }
        // Pull from Context first
        if (!text.contains("#($")) {
            String value = get(text, prop);
            if (value == null) {
                logger().debug("Resolved Value: " + text);
                return text;
            } else {
                logger().debug("Resolved Value: " + value);
                text = value;
            }
        }
        // Check do we still need parsing?
        if (text.contains("#($")) {
            Matcher m = Pattern.compile(Const.VARIABLE_REGEX).matcher(text);
            if (m.find()) {
                logger().debug("Match Found Now Resolving: " + (m.group(3)));
                String value = get(m.group(3), prop);
                if (value == null) {
                    logger().warn("Ignoring Error : Variable [" + m.group(3) + "] not found, substituting with empty text");
                    value = "";
                }
                text = resolveWithProperties(text.replace(m.group(1), value), prop);
            }

        }

        // Done with variable replacements , no more variables now
        //final check
        String value = get(text, prop);
        if (value == null) {
            logger().debug("Resolved Value: " + text);
            return text;
        } else {
            logger().debug("Resolved Value: " + value);
            return value;
        }
    }

    public static int getInt(String propName, int defaultValue) {
        Object value = PropMgr.get(propName);
        return value == null || value.toString().isEmpty() ? defaultValue : Integer.parseInt(value.toString());
    }

    public static String getString(String propName, String defaultValue) {
        Object value = PropMgr.get(propName);
        return value == null || value.toString().isEmpty() ? defaultValue : value.toString();
    }

    public static boolean getBoolean(String propName, boolean defaultValue) {
        Object value = PropMgr.get(propName);
        return value == null || value.toString().isEmpty() ? defaultValue : Boolean.parseBoolean(value.toString());
    }

}

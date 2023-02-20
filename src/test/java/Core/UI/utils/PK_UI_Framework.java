package Core.UI.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PK_UI_Framework {
    public static Logger getLogger(Class clz) {
        return LogManager.getLogger(clz);
    }
}

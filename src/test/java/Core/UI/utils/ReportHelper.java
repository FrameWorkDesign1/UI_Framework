package Core.UI.utils;


import Core.UI.Const;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ReportHelper
{
    static final Logger LOGGER = PK_UI_Framework.getLogger(ReportHelper.class);

    public static void generateCucumberHTMLReport(String jsonReportPath)
    {
        List<String> report = new LinkedList<String>();
        report.add(jsonReportPath);
        Reporting.generateCucumberReport(report);
    }

    public static void aftersuit()
    {
        ReportHelper.generateCucumberHTMLReport(PropMgr.get(Const.OUTPUT_FOLDER));
    }
}

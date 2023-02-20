package Core.UI.utils;


import Core.UI.Const;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.sorting.SortingMethod;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class Reporting {
    static final Logger LOGGER = PK_UI_Framework.getLogger(Reporting.class);

    public static void generateCucumberReport(List<String>  jsonFiles){
        try {
            String outPutFolderPath = PropMgr.get(Const.HTML_OUTPUT_FOLDER);
            outPutFolderPath = outPutFolderPath==null?Const.DEFAULT_HTML_OUTPUT_FOLDER:outPutFolderPath;
            File reportOutputDirectory = new File(outPutFolderPath);


            String buildNumber = PropMgr.get("BUILD_NUMBER") == null ? "--" : PropMgr.get("BUILD_NUMBER");
            String projectName = PropMgr.get(Const.PROJECT_NAME);
            Configuration configuration = new Configuration(reportOutputDirectory, projectName);
            configuration.setBuildNumber(buildNumber);

            configuration.addClassifications("Browser", PropMgr.get(Const.BROWSER));

            configuration.setSortingMethod(SortingMethod.NATURAL);
            configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
            configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
            // points to the demo trends which is not used for other tests
            configuration.setTrendsStatsFile(new File(outPutFolderPath+File.separator+"trends.json"));

            ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
            reportBuilder.generateReports();
        }
        catch (Exception e){
            LOGGER.error("Failed to generate HTML report, Error: "+ e.getMessage(),e);
            throw new RuntimeException(e);
        }

    }
}

package runner;

import Core.UI.utils.PK_UI_Framework;
import Core.UI.utils.ReportHelper;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        plugin = {
                "pretty",
                "html:target/cucumberHtmlReport/Report.html",
                "json:target/json/cucumber.json",
                "junit:target/xml/junit_report.xml",
                "rerun:target/rerun.txt",
                "junit:target/xml/junit_report.xml",
        },
        tags =  {"@UI"},
        features = {"src/test/resources/API_Features"},
        glue = {"steps","Core.UI.hooks"},
        strict = true,
        dryRun = false
)

public class RunTestNgRunnerTest extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = PK_UI_Framework.getLogger(RunTestNgRunnerTest.class);

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @AfterSuite
    public void afterSuite(){
        ReportHelper.aftersuit();
    }
}

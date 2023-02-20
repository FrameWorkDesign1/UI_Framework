package Core.UI;


public class Const {

	public static final String VARIABLE_REGEX = ".*(#(\\(\\$(\\S+)\\))).*"; // #($var)
	public static final String PROP_PATH = "config.properties";


	// ECuke  Options //
	/* Must be the path from plugin={json:target/json/cucumber.json}*/
	public static final String OUTPUT_FOLDER="json_output_file";
	public static final String HTML_OUTPUT_FOLDER="html_output_folder";
	public static final String DEFAULT_HTML_OUTPUT_FOLDER="target/cucumber-html-full-report";

	// Browser Arguments / Capabilities
	public static final String PREFIX_FIREFOX_PREF = "ff_opt_";
	public static final String PREFIX_CHROME_PREFS = "chrome_prefs_";
	public static final String PREFIX_CHROME_OPTION = "chrome_options_";
	public static final String CHROME_ARGUMENT = "chrome_arg";

	public static final String PREFIX_CAPABILITY= "caps_";

	public static final String BROWSER = "browser";


	public static final String PROJECT_NAME="project_name";

	public static final String ENV_CONFIG_PATH= "env_properties_file";

	public static final String ENV_URL="env_url";

	public static final String TEST_GRID = "test_grid";
	public static final String GRID_MAX_ATTEMPTS="grid_max_attempts";

    public static final String SNAP_SCALE = "scale";
	public static final String SNAP_DISABLED = "disable_snap";
	public static final String API_LOG = "api_log";

    public static final String DEFAULT_EXPLICIT_WAIT_TIMEOUT = "default_explicit_wait_timeout";
}

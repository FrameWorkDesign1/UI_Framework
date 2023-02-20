package Core.UI.hooks;

import Core.UI.TestContext;
import Core.UI.utils.FilesUtil;
import Core.UI.utils.PK_UI_Framework;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FwSteps {

    private TestContext context;
    private static final Logger LOGGER = PK_UI_Framework.getLogger(FwSteps.class);

    public FwSteps(TestContext context) {
        this.context = context;
    }

    @Given("Load properties from file {string}")
    public void loadPropertiesFileToVars(String filePath) {
        Map<String, String> map = FilesUtil.readFileAsMap(this.context.resolve(filePath));
        map = this.context.resolveMapValues(map);
        this.context.setVars(map);
    }

    @Given("Load properties from csv file {string} with index {int} with delimiter {word}")
    public void loadPropertiesCsvFileByIndexToVars(String filePath, int rowNumber,String delimiter) {
        delimiter=this.context.resolve(delimiter).trim();
        if(!(delimiter.trim().equals("|") || delimiter.trim().equals(","))){
            throw new RuntimeException("Invalid delimiter["+delimiter+"], valid options are[| ,]");
        }
        Map<String, String> map = FilesUtil.readCsvFileAsMapByIndex(this.context.resolve(filePath), rowNumber,delimiter.charAt(0));
        this.context.setVars(map);
    }

    @Given("Load properties from csv file {string} with index {int}")
    public void loadPropertiesCsvFileByIndexToVars(String filePath, int rowNumber) {
        Map<String, String> map = FilesUtil.readCsvFileAsMapByIndex(this.context.resolve(filePath), rowNumber,',');
        this.context.setVars(map);
    }

    @Given("Load properties from csv file {string} with value at column index {int} having value {string} with delimiter {word}")
    public void loadPropertiesCsvFileWithValueAtIndexHavingValueWithDelimiter(String filePath, int colIndex, String value,String delimiter) {
        delimiter=this.context.resolve(delimiter).trim();
        if(!(delimiter.trim().equals("|") || delimiter.trim().equals(","))){
            throw new RuntimeException("Invalid delimiter["+delimiter+"], valid options are[| ,]");
        }
        Map<String, String> map = FilesUtil.readCsvFileAsMapByKeyValue(this.context.resolve(filePath),colIndex , this.context.resolve(value),delimiter.charAt(0));
        this.context.setVars(map);
    }
    @Given("Load properties from csv file {string} with value at column index {int} having value {string}")
    public void loadPropertiesCsvFileWithValueAtIndexHavingValue(String filePath, int colIndex, String value,String delimiter) {
        delimiter=this.context.resolve(delimiter).trim();
        if(!(delimiter.trim().equals("|") || delimiter.trim().equals(","))){
            throw new RuntimeException("Invalid delimiter["+delimiter+"], valid options are[| ,]");
        }
        Map<String, String> map = FilesUtil.readCsvFileAsMapByKeyValue(this.context.resolve(filePath),colIndex , this.context.resolve(value),',');
        this.context.setVars(map);
    }

    @Given("Load properties from csv file {string} with column {string} having value {string} with delimiter {word}")
    public void loadPropertiesCsvFileByConditionToVars(String filePath, String key, String value,String delimiter) {
        delimiter=this.context.resolve(delimiter).trim();
        if(!(delimiter.trim().equals("|") || delimiter.trim().equals(","))){
            throw new RuntimeException("Invalid delimiter["+delimiter+"], valid options are[| ,]");
        }
        Map<String, String> map = FilesUtil.readCsvFileAsMapByKeyValue(this.context.resolve(filePath), this.context.resolve(key), this.context.resolve(value),delimiter.charAt(0));
        this.context.setVars(map);
    }
    @Given("Load properties from csv file {string} with column {string} having value {string}")
    public void loadPropertiesCsvFileByConditionToVars(String filePath, String key, String value) {
        Map<String, String> map = FilesUtil.readCsvFileAsMapByKeyValue(this.context.resolve(filePath), this.context.resolve(key), this.context.resolve(value),',');
        this.context.setVars(map);

    }

    /**
     * This step should be avoided. Use only for debugging, It is not recommended to print vars/text in real test cases.
     * Note: Never use this step for printing data containing sensitive data (passwords etc.). this step will not mask sensitive data and it will print as it is.
     * @param propName
     */
    @And("Print property value for {string}")
    public void printPropertyValue(String propName) {
        String value = this.context.resolve(propName);
        System.out.println("---------------- " + propName + " --------------------");
        System.out.println(value);
        System.out.println("___________________________________________________");

    }


    @And("Remove variable from context by name {string}")
    public void removeVariableByName(String varName) {
        this.context.removeVar(varName);
    }

    @And("Save variable with name {string} and value {string}")
    public void saveVariableWithName(String varName, String varValue) {
        String uCaseVarValue = varValue.toUpperCase();
        if (uCaseVarValue.startsWith("INT::")) {
            this.context.setVar(varName, Integer.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("FLOAT::")) {
            this.context.setVar(varName, Float.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("LONG::")) {
            this.context.setVar(varName, Long.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("SHORT::")) {
            this.context.setVar(varName, Short.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("BYTE::")) {
            this.context.setVar(varName, Byte.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("CHAR::")) {
            this.context.setVar(varName, (char) varValue.split("::")[1].charAt(0));
        } else if (uCaseVarValue.startsWith("DOUBLE::")) {
            this.context.setVar(varName, Double.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("BOOLEAN::")) {
            this.context.setVar(varName, Boolean.valueOf(varValue.split("::")[1]));
        } else if (uCaseVarValue.startsWith("STRING::")) {
            this.context.setVar(varName, varValue.split("::")[1]);
        } else {
            this.context.setVar(varName, varValue);
        }
    }

    //Save list variable with name "expectedList" and value "[\"iPhone\",\"home\"]"
    @And("Save list variable with name {string} and value {string}")
    public void saveListVariableWithName(String varName, String values) throws IOException {
        List<Object> listValues = new ObjectMapper().readValue(this.context.resolve(values), new TypeReference<List<Object>>() {
        });
        this.context.setVar(varName, listValues);
    }

    @And("Copy variable {string} as {string}")
    public void copyVariableAs(String existingVariable, String newVariable) {
        Object value = this.context.var(existingVariable);
        if (value == null) {
            throw new RuntimeException("Variable [] not found in context");
        }
        this.context.setVar(newVariable, value);
    }

    @Given("Set variable {string} with value {string}")
    public void setVariableWithValue(String varName, String varValue) {
        saveVariableWithName(varName, varValue);
    }
    @Given("Set variable {string}")
    public void setVariableEqValue(String varNameAndValue) {
        Assert.assertFalse(varNameAndValue.indexOf("=")==-1,"Invalid syntax for input, expected[name=value] instead received ["+varNameAndValue+"]");
        String varName=varNameAndValue.substring(0,varNameAndValue.indexOf("="));
        String value=varNameAndValue.substring(varNameAndValue.indexOf("=")+1);
        saveVariableWithName(varName, value);
    }
    @Given("Set variable {string} with resolve option")
    public void setVariableEqValueWithResolveOption(String varNameAndValue) {
        Assert.assertFalse(varNameAndValue.indexOf("=")==-1,"Invalid syntax for input, expected[name=value] instead received ["+varNameAndValue+"]");
        String varName=varNameAndValue.substring(0,varNameAndValue.indexOf("="));
        String value=varNameAndValue.substring(varNameAndValue.indexOf("=")+1);
        value = context.resolve(value);
        saveVariableWithName(varName, value);
    }

    @Given("Load variables without header")
    public void setVariablesWithoutHeader(DataTable dt) {
        List<List<String>> list = dt.asLists(String.class);
        if (list.get(0).size() != 2) {
            throw new RuntimeException("Failed to load variables, expected data column in data table [2] but found [" + list.size() + "]");
        }
        for (int i = 0; i < list.size(); i++) {
            LOGGER.info("Variable: " + list.get(i).get(0) + " , Value: " + list.get(i).get(1));
            saveVariableWithName(list.get(i).get(0), list.get(i).get(1));
        }
    }

    @Given("Load variables with header")
    public void setVariablesWithHeader(DataTable dt) {
        List<Map<String, String>> list = dt.asMaps(String.class, String.class); // with header
        if (list.size() != 1) {
            throw new RuntimeException("Failed to load variables, expected data rows in data table [1] but found [" + list.size() + "]");
        }
        for (String key : list.get(0).keySet()) {
            LOGGER.info("Variable: " + key + " , Value: " + list.get(0).get(key));
            saveVariableWithName(key, list.get(0).get(key));
        }
    }

    @Then("Validate/Assert/Check/Verify variable {string} with expected value {string}")
    public void assertVariableWithExpectedValue(String varName, String expected) {
        String actual = this.context.resolve(varName);
        expected = this.context.resolve(expected);
        Assert.assertEquals(actual, expected, "Variable [" + varName + "] value failed.");
    }

    @And("Convert Base64 text {string} to byte and save as {string}")
    public void convertBase64ToText(String textToConvert, String saveAsVariable) {
        textToConvert = this.context.resolve(textToConvert);
        this.context.setVar(saveAsVariable, Base64.decodeBase64(textToConvert));
    }

    @And("Write variable {string} to file {string}")
    public void saveVariableToFile(String variable, String filePath) {
        FilesUtil.saveToFile(this.context.resolve(variable), this.context.resolve(filePath));
    }

    @And("Write Base64 variable {string} to file {string}")
    public void saveBase64VariableToFile(String variable, String filePath) {
        FilesUtil.saveToFile((byte[]) this.context.var(variable), this.context.resolve(filePath));
    }

    @And("Convert text {string} to Base64 string and save as {string}")
    public void convertTextToBase64String(String textToConvert, String saveAsVariable) {
        textToConvert = this.context.resolve(textToConvert);
        this.context.setVar(saveAsVariable, Base64.encodeBase64String(textToConvert.getBytes()));
    }

//    @And("Decrypt text {string} and save as {string}")
//    public void decryptTextAndSaveAs(String textToDecrypt, String saveAsVar) {
//        textToDecrypt = this.context.resolve(textToDecrypt);
//        this.context.setVar(saveAsVar, EncryptDecrypt.decryptPassword(textToDecrypt));
//    }

    @And("Wait for {int} seconds")
    public void waitForSeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    @And("Validate/Assert/Check/Verify context variable {string} not null")
    public void assertVariableWithExpectedNotNull(Object obj) {
        Assert.assertNotNull(this.context.var(obj), "Variable value is null");
    }

    @And("Replace {string} with {string} from {string} and save as {string}")
    public void replace(String txtToReplace, String replaceWith, String txt, String varName) {
        txt = this.context.resolve(txt);
        txtToReplace = this.context.resolve(txtToReplace);
        replaceWith = this.context.resolve(replaceWith);
        txt = txt.replace(txtToReplace, replaceWith);
        this.context.setVar(varName, txt);
    }
    @And("Convert map variable {string} to json text and store as variable {string}")
    public void  convertMapToJsonText(String mapVar,String var) throws IOException {
        Map map = (Map) this.context.var(mapVar);
        this.context.setVar(var,new ObjectMapper().writeValueAsString(map));
    }
    @And("Convert json text {string} to map variable {string}")
    public void  convertJsonTextToMap(String jsonText,String mapVar) throws IOException {
        jsonText = this.context.resolve(jsonText);
        this.context.setVar(mapVar,new ObjectMapper().readValue(jsonText, Map.class));
    }

    @Then("Load file {string} content as variable {string}")
    public void loadFileContentAsVariable(String filePath, String varName) throws IOException {
        filePath = this.context.resolve(filePath);
        String content = FilesUtil.getFileContentAsString(filePath);
        this.context.setVar(varName,content);
    }
}
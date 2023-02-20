package steps;

import Core.UI.Const;
import Core.UI.TestContext;
import Core.UI.utils.FilesUtil;
import Core.UI.utils.PK_UI_Framework;
import Core.UI.utils.api.ApiContextVars;
import Core.UI.utils.api.ApiHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.custommonkey.xmlunit.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.CommonUtil;
import utils.MapUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static config.ProjectConst.PDF_FILE_PATH;


public class APISteps {

    private TestContext context;

    static final Logger LOGGER = PK_UI_Framework.getLogger(APISteps.class);
    private StringBuffer Name;
    public static String res = null;
    public APISteps(TestContext context) {
        this.context = context;
    }

    @And("Expected/Actual result is stored in variable {string}")
    public void expectedResultIsStoredInVariable(String varName, DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> dataRow : data) {
            String fieldValue = dataRow.get("Field Value") == null ? "(none)" : this.context.resolve(dataRow.get("Field Value")).trim();
            Map<String, String> nameValueMap = CommonUtil.convertFieldsAndValuesToMap(this.context.resolve(dataRow.get("Field Name")).split(","), fieldValue.split(","));
            this.context.setVar(varName, nameValueMap);
        }
    }
    @Given("Set Api testing environment {string}")
    public void setApiTestingEnvironment(String envUrl) {
        envUrl = this.context.resolve(envUrl);
        this.context.setVar(Const.ENV_URL, envUrl);
    }

    //For FNMA response decoding
    @And("Save Base64 from each variable from list {string} as {string}, {string}, {string} and {string}")
    public void saveDecodedResponseVariables(String responseList, String repositoryResponse, String dotdatResponse, String textResponse, String mismoResponse)
    {
        byte[] value;
        List<String> responseValues = (List<String>) this.context.var(responseList);

        for(int i =0; i <= responseValues.size() ; i++)
            switch(i)
            {
                case 0 :
                    value = Base64.decodeBase64(responseValues.get(i));
                    this.context.setVar(repositoryResponse,new String(value));
                    System.out.println(this.context.var(repositoryResponse));
                    break;

                case 1 :
                    value = Base64.decodeBase64(responseValues.get(i));
                    this.context.setVar(dotdatResponse,new String(value));
                    System.out.println(this.context.var(dotdatResponse));
                    break;

                case 2 :
                    value = Base64.decodeBase64(responseValues.get(i));
                    this.context.setVar(textResponse,new String(value));
                    System.out.println(this.context.var(textResponse));
                    break;

                case 3 :
                    value = Base64.decodeBase64(responseValues.get(i));
                    this.context.setVar(mismoResponse,new String(value));
                    System.out.println(this.context.var(mismoResponse));
                    break;
            }
    }

    @And("Save string {string} to variable {string} with file path {string}")
    public void saveBaseStringToVariableWithFilePath(String response, String fileName, String varFilePath)
    {
        response = this.context.resolve(response);
        String filePath = PDF_FILE_PATH + File.separator + fileName + "temp.txt";
        this.context.setVar(varFilePath,filePath);
        FilesUtil.saveToFIle(response, filePath);
    }

    @And("Read variable with tag {string} and attribute {string} from file {string} and store as variable {string}")
    public void readTagAttributeFromFile(String tag, String attribute, String filePath,String variable) throws ParserConfigurationException, SAXException, IOException
    {
        String val;
        filePath = this.context.resolve(filePath);
        Document doc = documentObjectCreation(filePath);
        if(attribute.equalsIgnoreCase("CreditReportResponseType"))
        {
            val = doc.getDocumentElement().getAttributeNode("CreditReportResponseType").getValue();
            this.context.setVar(variable, val);
            return;
        }
        Node nNode = doc.getDocumentElement().getElementsByTagName(tag).item(0);
        Element eElement = (Element) nNode;
        val = eElement.getAttribute(attribute);
        this.context.setVar(variable, val);
    }

    @And("Get length of tag variable {string} from response {string} and store as variable {string}")
    public void getTagLength(String tag,String filePath,String variable) throws ParserConfigurationException, SAXException, IOException {
        int count;
        filePath = this.context.resolve(filePath);
        Document doc = documentObjectCreation(filePath);
        count = doc.getDocumentElement().getElementsByTagName(tag).getLength();
        this.context.setVar(variable,count);
    }

    @And("Read value for tag {string} from file {string} and store as variable {string}")
    public void readTagValueFile(String tag, String filepath, String variable) throws ParserConfigurationException, IOException, SAXException {
        String val;
        filepath = this.context.resolve(filepath);
        Document doc = documentObjectCreation(filepath);
        Node nNode = doc.getDocumentElement().getElementsByTagName(tag).item(0);
        Element eElement = (Element) nNode;
        val = eElement.getTextContent();
        this.context.setVar(variable,val.trim());
    }

    public Document documentObjectCreation(String filePath) throws ParserConfigurationException, IOException, SAXException
    {
        String text = FilesUtil.getFileContentAsString(filePath);
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = docBuilderFactory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes());
        Document doc = docbuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        return doc;
    }

    @And("Read list for tag {string} from file {string} for bureau {string} and store as variable {string}")
    public void readListForTagFromFile(String tag, String filePath, String bureau, String variable) throws IOException, SAXException, ParserConfigurationException {
        String val = "";
        filePath = this.context.resolve(filePath);
        Document doc = documentObjectCreation(filePath);
        NodeList nodeList = doc.getElementsByTagName(tag);

        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);

            if(node.getAttributes().item(0).getTextContent().equalsIgnoreCase(bureau)) {
                if(bureau.equalsIgnoreCase("TransUnion"))
                    val = val + node.getTextContent().trim().replaceAll("null","").replaceAll("\n","");

                else if(bureau.equalsIgnoreCase("Equifax") && node.getTextContent().contains("ECIS") )
                    val = node.getTextContent().trim().replaceAll("null","").replaceAll("\n","");

                else if(bureau.equalsIgnoreCase("Experian"))
                    val = node.getTextContent().trim().replaceAll("null","").replaceAll("\n","");
            }
        }this.context.setVar(variable,val);
    }


    @And("User compares map variables for expected {string} with actual {string}")
    public void userComparesMapVariablesForExpectedWithActual(String expectedVar, String actualVar) {
        Map<String, String> expectedMap = (Map<String, String>) this.context.var(expectedVar);
        Map<String, String> actualMap = (Map<String, String>) this.context.var(actualVar);
        Assert.assertEquals(actualMap,expectedMap);
        LOGGER.info("Compare successful for - " + expectedVar + " with " + actualVar);
    }

    @And("Extract value from the recent 1X response by {string} path {string} and set as variable {string}")
    public void extractValueFromMISMO1XResponseByPathAndSetAsVariable(String xmlOrJson, String path, String varName) {
        path = this.context.resolve(path);
        xmlOrJson = this.context.resolve(xmlOrJson);
        LOGGER.debug("Searching for " + xmlOrJson + " Path: [" + path + "]");
        Assert.assertNotNull(this.context.var(ApiContextVars.RESPONSE), "Response not found. Please make sure you finished making call");
        Response response = (Response)this.context.var(ApiContextVars.RESPONSE);
        String value = ApiHelper.extractValueResponse(response.body().asString().replace("<!DOCTYPE MORTGAGEDATA SYSTEM \"CreditReporting1_0_1.DTD\">",""), xmlOrJson, path);
        this.context.setVar(varName, value);
    }

    @Then("Assert variable {string} is correct")
    public void assertVariableIsCorrect(String varName) {
        varName = (String) this.context.var(varName);
        Assert.assertFalse(varName.contains("CreditReportIdentifier"), "Order number is not generated." );
        LOGGER.info("Value found - " + varName);
    }


    public static Map<String, String> convertResultSetToMapForFirstRow(ResultSet resultSet) throws SQLException {
        Map<String, String> resultSetMap = new HashMap<>();
        String rs_value = "";
        String colName = "";
        ResultSetMetaData md = resultSet.getMetaData();
        int db_column = md.getColumnCount();
        try {
            resultSet.next();
            for (int i=1; i<=db_column; i++) {
                colName = md.getColumnName(i);
                rs_value = resultSet.getString(colName);
                resultSetMap.put(colName,rs_value);
            }
        } catch (Exception e) {
            throw new RuntimeException("convertResultSetToMapForFirstRow - exception : " + e.toString());
        }
        return resultSetMap;
    }

    @And("Get below fields from response and prefix {string} and store in map variable {string}")
    public void getFieldsFromResponseAndStoreInMapVariable(String prefix,String varName,DataTable dataTable) {
        Map<String,String> plainText =new HashMap<>();
        StringBuffer mapValue = new StringBuffer();
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> rowData : data) {
            for (Map.Entry<String, String> pair : rowData.entrySet()) {
               if(pair.getKey().equalsIgnoreCase("dob"))
               {
                   String dob_val= extractValueFromResponseByPath("Json", MapUtil.getMappingValue(prefix,pair.getValue()));
                   mapValue.append(dob_val.substring(0,10)).append("_");
               }
               else
               {
                   mapValue.append(extractValueFromResponseByPath("Json", MapUtil.getMappingValue(prefix, pair.getValue()))).append("_");
               }
            }
            mapValue.deleteCharAt(mapValue.length()-1);
            plainText.put(this.context.resolve("#($orderNo)"),mapValue.toString());
        }
        this.context.setVar(varName,plainText);
    }

    public String extractValueFromResponseByPath(String xmlOrJson, String path) {
        path = this.context.resolve(path);
        xmlOrJson = this.context.resolve(xmlOrJson);
        LOGGER.debug("Searching for " + xmlOrJson + " Path: [" + path + "]");
        Assert.assertNotNull(this.context.var(ApiContextVars.RESPONSE), "Response not found. Please make sure you finished making call");
        Response response = (Response)this.context.var(ApiContextVars.RESPONSE);
        String value = ApiHelper.extractValueResponse(response.body().asString(), xmlOrJson, path);
        LOGGER.info("Value from response for given path ["+path+"] is ["+value+"]");
        return value;
    }

    @And("Remove hyphen from variable {string}")
    public void test(String varName) {
        String modified_value = this.context.resolve(varName).replaceAll("-", "");
        this.context.setVar(varName, modified_value);
    }

    @And("Save Base64 from response {string} as {string}")
    public void saveBase64BlobDataFromResponse(String varName,String decodedBlobData) throws SQLException {
        String responseValues = this.context.resolve(varName);
        byte[] value = Base64.decodeBase64(responseValues);
        this.context.setVar(decodedBlobData,new String(value));
        System.out.println(this.context.var(decodedBlobData));
        LOGGER.info("Decoded Blob Data: " + decodedBlobData);
    }

    @Then("Convert byte code {byte} to string data type {string}")
        public void byteCodeToStringConverstion(byte[] varNameByte,String varNameString){
            String plainText= new String(varNameByte);
            LOGGER.info("Plain Text is converted in String form byte: " + plainText);
            this.context.setVar(varNameString,plainText);
        }

    @And("Compare {string} result with {string} xml")
    public void compareResultWithXml(String actualString, String expectedString) throws IOException, SAXException {
        String actual = this.context.resolve(actualString);
        String expected = this.context.resolve(expectedString);
        Reader sourceReader = new StringReader(expected);
        Reader targetReader = new StringReader(actual);
        Diff diff = compareXML(sourceReader, targetReader);
        List xmlDifferences = new DetailedDiff(diff).getAllDifferences();
        Iterator<Difference> differenceIterator = xmlDifferences.listIterator();
        while (differenceIterator.hasNext()) {
            Difference difference = differenceIterator.next();
            if (difference.toString().contains("Expected sequence of child nodes")) {
                LOGGER.info("Order sequence ignored. " + difference);
                differenceIterator.remove();
            } else if (difference.toString().contains("DOCUMENT")) {
                LOGGER.info("Encoded PDF Document ignored. " + difference);
                differenceIterator.remove();
            }
        }
        Assert.assertTrue(xmlDifferences.size() == 0, "Response not matched. " + printDifferences(xmlDifferences));
    }

    public static Diff compareXML(Reader source, Reader target) throws SAXException, IOException {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff xmlDiff = new Diff(source, target);
        xmlDiff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
        return xmlDiff;
    }

    public static String printDifferences(List<Difference> differences) {
        int totalDifferences = differences.size();
        StringBuilder sb = new StringBuilder();
        sb.append("Total differences : " + totalDifferences);
        sb.append("\n");
        for (Difference difference : differences) {
            sb.append(difference);
            sb.append("\n");
        }
        return sb.toString();
    }



    @And("Randomize the request FirstName and stored in {string}")
    public void randomize_the_request_FirstName_and_stored_in(String string) {
        String RANDOCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder stringbuild = new StringBuilder();
        Random rnd = new Random();
        while (stringbuild.length() < 8)
        {
            int index = (int) (rnd.nextFloat() * RANDOCHARS.length());
            stringbuild.append(RANDOCHARS.charAt(index));
        }
        String saltStr = stringbuild.toString();
        this.context.setVar(string,saltStr);
        this.context.setVar("BFName",saltStr);
    }

    @And("Compare {string} result with {string}")
    public void compareResultWith(String actualString, String expectedString)
    {
        String actual = this.context.resolve(actualString);
        String expected = this.context.resolve(expectedString);
        Assert.assertTrue(expected.equalsIgnoreCase(actual));
    }

    @Then("Split {string} and {string}and stored in {string}")
    public void splitAndAndStoredIn(String ln_no, String splitvalue, String loannumber) throws Exception {
        String loanno = this.context.resolve(ln_no);
        System.out.println(loanno);
        String loanno1 = this.context.resolve(ln_no);
        System.out.println(loanno1);
        String firstfourstring = "";
        boolean validX = false;
        int value= Integer.parseInt(splitvalue);
        System.out.println(splitvalue);
        switch (value)
        {
            case 1:
                if (ln_no.length() > 4) {
                    firstfourstring = loanno.substring(0,4);

                }
                else {
                    LOGGER.info("-------------------" + ln_no + "-----------------------");
                    LOGGER.info("Invalid " + ln_no);
                    LOGGER.info("___________________________________________________");
                }
                this.context.resolve("LenderCaseIdentifier");
                System.out.println(firstfourstring);
                this.context.setVar("LenderCaseIdentifier", firstfourstring);
                break;
            case 2:
                if (ln_no.length() > 3) {
                    firstfourstring = loanno.substring(0,3);
                }
                else {
                    LOGGER.info("-------------------" + ln_no + "-----------------------");
                    LOGGER.info("Invalid " + ln_no);
                    LOGGER.info("___________________________________________________");
                }
                this.context.resolve("LenderCaseIdentifier");
                System.out.println(firstfourstring);
                this.context.setVar("LenderCaseIdentifier", firstfourstring);
                break;
            case 3:
                if (ln_no.length() > 4) {
                    firstfourstring = loanno.substring(loanno.length()-4);
                }
                else {
                    LOGGER.info("-------------------" + ln_no + "-----------------------");
                    LOGGER.info("Invalid " + ln_no);
                    LOGGER.info("___________________________________________________");
                }
                this.context.resolve("LenderCaseIdentifier");
                this.context.setVar("LenderCaseIdentifier", firstfourstring);
                break;
            case 4:
                String [] txtsp =  loanno1.split("P         ");
                String p1=txtsp[1];
                validX = (p1.contains("GIKSX") || p1.contains("GIKPS") || p1.contains("GIKOSX"));
                System.out.println(validX);
                this.context.setVar(loannumber,validX);
                break;

            case 5:
                String [] txtsp1 =  loanno1.split("P         ");
                String p2=txtsp1[2];
                boolean validXS = (p2.contains("GIKSX") || p2.contains("GIKPS"));
                this.context.setVar(loannumber, validXS);
                break;
            case 6:
                if (loanno1.contains("DA16")==true)
                {
                    String keys = null ;
                    String Valuess = null ;
                    String NewMapValue =null;
                    Map<String,String> map=new HashMap<String,String>();
                    int indexno = loanno1.indexOf("DA16");
                    System.out.println("Index value of DA16 segment is  :- "+indexno);
                    String Da16segemnt = loanno1.substring(indexno, (indexno+243));
                    String[] segment = Da16segemnt.split("DA161000=8349293154");
                    String[] seg = segment[1].split(" ");

                    for (int i = 0; i < seg.length; i++)
                    {
                        String[] da16 = seg[i].split("=");
                        for (int j = 0; j < da16.length; j++)
                        {
                            keys = da16[0];
                            Valuess  = da16[1];
                            NewMapValue = CommonUtil.leadiginZero(Valuess);
                            map.put(keys,NewMapValue);
                        }
                    }
                    System.out.println("Map Value :- "+map);
                    LOGGER.info("Map Value is  :- "+NewMapValue);
                    this.context.setVar(loannumber, map);
                }
                else
                {
                    throw new Exception("Case :- "+value+"----- DA segemnt not found");

                }
                break;
            default:
                throw new Exception("Case :- "+value+" = Not found");
        }

    }

    @And("Generate Reports for Stylesheet values from Request {string}")
    public void generateReportsForStylesheetValues(String filePath, DataTable table) {
        SoftAssert softAssert = new SoftAssert();
        List<Map<String, String>> list = table.asMaps(String.class, String.class);
        String variableName = "Payload";
        if (list.size() < 1) {
            throw new RuntimeException("Expected Data Table values, please provide | path | expected value| table, Actual Rows:" + list.size());
        }
        for (int i = 0; i < list.size(); i++) {
            this.context.setVar("XSL_IDENTIFIER", list.get(i).get("XSL_IDENTIFIER"));
            this.context.setVar("OUTPUT_TYPE", list.get(i).get("OUTPUT_TYPE"));

            String txt = FilesUtil.getFileContentAsString(this.context.resolve(filePath));
            txt = this.context.resolve(txt);
            this.context.setVar(variableName, txt);
            LOGGER.info(variableName + " : " + txt);

            Object value = this.context.var(variableName);
            if (value == null) {
                throw new RuntimeException("Variable [" + variableName + "] not found");
            }
            this.context.setVar(ApiContextVars.BODY, value);

            this.context.setVar(ApiContextVars.METHOD, "POST");
            Response response = ApiHelper.makeCall(this.context);
            this.context.setVar(ApiContextVars.RESPONSE, response);
            String actResponse = this.context.var("_RESPONSE_BODY_TEXT").toString();

            res = actResponse;
            softAssert.assertTrue(actResponse.contains(this.context.resolve("CreditReportIdentifier"))
                            || actResponse.contains("PDF") || actResponse.contains("xml") || actResponse.contains("HOME VALUE") || actResponse.contains("compare"),
                    "\n" +
                            "XSL_IDENTIFIER : " + this.context.resolve("XSL_IDENTIFIER") + "\n" +
                            "OUTPUT_TYPE : " + this.context.resolve("OUTPUT_TYPE") + "\n" +
                            "Stylesheet verification failed");
        }
        softAssert.assertAll();
    }

    @And("Verify RG Response {string}")
    public void containText(String text) {
        if (text.isEmpty()) {
            LOGGER.info("Passed : " + " No text For verification: ");
        } else {
            Assert.assertTrue(res.contains(text), "text found in RG response");
            LOGGER.info("Passed :" + text + " In Response ");
        }
    }

    @And("Trim the spces for {string} and store in {string}")
    public void trimTheSpcesForAndStoreIn(String DATA_SET, String attribute) {
        String Datasets = this.context.resolve(DATA_SET);
        String newdata = Datasets.replace(" ", "");
        LOGGER.info(newdata);
        this.context.setVar(attribute, newdata);
    }

    @And("Read  PDF from pdf file {string} and save as {string}")
    public void readPDFFromPdfFileAndSaveAs(String pdfFile, String saveVariable) {
        String Filess = this.context.resolve(pdfFile);
        LOGGER.info("Path ===============================" + Filess);

        PDDocument document = null;
        try {
            document = PDDocument.load(new File(Filess));
            PDFTextStripper pdftext = new PDFTextStripper();
            String pdftextdata = pdftext.getText(document);
            LOGGER.info("============================== PDF DATA =========================================");
            LOGGER.info(pdftextdata);
            LOGGER.info("============================== PDF DATA =========================================");
            this.context.setVar(saveVariable, pdftextdata);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    @Then("Cooper Split {string} and {string}and stored in {string}")
    public void cooperSplitAndAndStoredIn(String segment, String splitvalue, String segValue, DataTable dataTable ) throws Exception {
        String rawsegment = this.context.resolve(segment);

        String varible = this.context.resolve(segValue);
        int value= Integer.parseInt(splitvalue);
        switch (value)
        {
            case 1:
                List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
                for (Map<String, String> dataRow : data) {
                    switch (dataRow.get("ActionType")) {
                        case " ":
                            if (rawsegment.contains("DA16") == true) {
                                String keys = null;
                                String Valuess = null;
                                String NewMapValue = null;

                                Map<String, String> map = new HashMap<String, String>();

                                int indexno = rawsegment.indexOf("2001=");
                                System.out.println("Index value of 2001 segment is  :- " + indexno);
                                String Da16segemnt = rawsegment.substring(indexno, (indexno + 224));

                                String[] seg = Da16segemnt.split(" ");
                                for (int i = 0; i < seg.length; i++) {
                                    String[] da16 = seg[i].split("=");
                                    for (int j = 0; j < da16.length; j++) {
                                        keys = da16[0];
                                        Valuess = da16[1];
                                        NewMapValue = CommonUtil.leadiginZero(Valuess);
                                        map.put("attribute_" + keys, NewMapValue);
                                    }
                                }
                                LOGGER.info("Map Value is  :- " + CommonUtil.sortByKeys(map));
                                this.context.setVar(varible, map = CommonUtil.sortByKeys(map));
                            }
                            else
                            {
                                throw new Exception("Case :- " + value + "----- DA segemnt not found");
                            }
                            break;

                        case "Joint":
                            if (rawsegment.contains("N& ") == true)
                            {
                                String keys = null;
                                String Valuess = null;
                                String NewMapValue = null;

                                Map<String, String> map = new HashMap<String, String>();
                                String[] coborrower = rawsegment.split("N& ");
                                String cbr = coborrower[1];
                                int indexnumber= cbr.indexOf("2001=");
                                System.out.println("Index value of 2001 segment is  :- " + indexnumber);
                                String Da16segemnt1 = cbr.substring(indexnumber, (indexnumber + 224));

                                String[] seg = Da16segemnt1.split(" ");
                                for (int i = 0; i < seg.length; i++) {
                                    String[] da16 = seg[i].split("=");
                                    for (int j = 0; j < da16.length; j++) {
                                        keys = da16[0];
                                        Valuess = da16[1];
                                        NewMapValue = CommonUtil.leadiginZero(Valuess);
                                        map.put("attribute_" + keys, NewMapValue);
                                    }
                                }
                                LOGGER.info("Map Value is  :- " + CommonUtil.sortByKeys(map));
                                this.context.setVar(varible, map = CommonUtil.sortByKeys(map));
                            }
                            else
                            {
                                throw new Exception("Case :- " + value + "----- DA segemnt not found");
                            }

                            break;
                        default:
                            throw new Exception("Case :- "+dataRow.get("ActionType")+" = Not found");
                    }
                }
                break;
            default:
                throw new Exception("Case :- "+value+" = Not found");
        }
    }

    @And("Split segment {string} and where raw data is {string} store in variable {string}")
    public void splitSegmentAndWhereRawDataIsStoreInVariable(String SegName, String rawdata, String variables) throws Exception {
        int startIndex;
        String segment = null;
        String data = this.context.resolve(rawdata);
        SegName= this.context.resolve(SegName);
        boolean truefalse;
        boolean seg = SegName.contains("CP00") || SegName.contains("MM04") || SegName.contains("MM03") ||SegName.contains("DA16");
        if (seg)
        {
            truefalse = seg;
            startIndex = data.indexOf(SegName);
            segment = data.substring(startIndex,(startIndex+40));
            System.out.println(SegName +"Segemnent :-"+segment);
            System.out.println(SegName +"Segemnent :-"+ truefalse);
        }
        else
        {
            throw new Exception("Segment :- " + segment + "  Not found");
        }

        this.context.setVar(variables,truefalse);
    }
    @Given("Load XMl from {string} with code {string} and having description {string}")
    public String loadXMlFromWithCodeAndHavingDescription(String XMLPath, String Codevalue, String Description) throws IOException {
        String xmlpath = this.context.resolve(XMLPath);
        String codevalue=this.context.resolve(Codevalue);
        String values = null;
        Map <Object ,Object> map = new HashedMap();
        Properties AttributeCode = new Properties();
        InputStream AttributeMapping  = APISteps.class.getClassLoader().getResourceAsStream(xmlpath);
        AttributeCode.loadFromXML(AttributeMapping);
        if ((AttributeCode != null) && (codevalue != null))
        {
             values = AttributeCode.getProperty(codevalue);
        }
        map.put(codevalue,values);
        this.context.setVar(Description,map);
        return values;
        }

    @And("Mapping two value {string} and {string} store in {string}")
    public void mappingTwoValueAndStoreIn(String code, String description, String arg2)
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put(this.context.resolve(code),this.context.resolve(description));
        this.context.setVar(arg2,data);

    }

    @Then("Asserts variable {string} with expected value {string} and {string}")
    public void assertsVariableWithExpectedValueAnd(String varName, String expected ,String expected2) {
        String actual = this.context.resolve(varName);
        expected = this.context.resolve(expected);
        expected2 = this.context.resolve(expected2);
        System.out.println("Actual :- "+actual +"\nExpected :- "+expected2 +"\nExpected2 :- "+expected2);
        Assert.assertTrue(actual.contains(expected),actual.contains(expected2)+ expected + " : string not found in document");
    }

    @And("Randomize the request SSN and stored in {string}")
    public void randomizeTheRequestSSNAndStoredIn(String ssnrandom)
    {
        String numbers = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 10;
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            char randomChar = numbers.charAt(index);
            sb.append(randomChar);
        }

        String randomString = sb.toString();
        System.out.println("Random String is: " + randomString.substring(0,9));
        this.context.setVar("CoBSSN",randomString.substring(0,9));
    }

    @And("Mapping two ErrorCode {string} and {string} store in {string}")
    public void mappingTwoErrorCodeAndStoreIn(String code1, String code2, String code)
    {
        String Code = this.context.resolve(code1) + "," + this.context.resolve(code2);
        System.out.println(Code);
        this.context.setVar(code,Code);

    }
    @And("Remove the tag from {string} where tag is {string} and save to {string}")
    public void removeTheTagFromWhereTagIsAndSaveTo(String payload, String Tag, String newPayload)
    {
        String Payload = this.context.resolve(payload);
        String NewPayload = Payload.trim().replaceAll(this.context.resolve("<" + Tag + ">"), "");
        this.context.setVar(newPayload,NewPayload);
    }

    @And("Map the ssn {string} and {string} with value csv\"")
    public void mapTheSsnAndB_SSNWithValueCsv(String b1ssn, String b2ssn)
    {
        b1ssn = this.context.resolve(b1ssn);
        b2ssn = this.context.resolve(b2ssn);
        this.context.setVar("B1_SSN", b1ssn);
        this.context.setVar("B2_SSN", b2ssn);
    }

    @And("Mapping value {string} with csv {string}")
    public void mappingValueWithCsv(String value, String value2)
    {
        String saltStr = value;
        this.context.setVar(value2,saltStr);
    }

    @And("Mapping value {string} and {string} with csv {string} and {string}")
    public void mappingValueAndWithCsvAnd(String Id, String Pass, String LoginAccountIdentifier, String LoginAccountPassword)
    {
        String UID = Id;
        String UPass = Pass;

        this.context.setVar(LoginAccountIdentifier, UID);
        this.context.setVar(LoginAccountPassword, UPass);
    }

    @Then("Make folder to save raw data at {string}")
    public void makeFolderToSaveRawDataAt(String path) {
//        String newCreatedFolderName = new SimpleDateFormat("MM-dd-YYYY_HH-mm-ss").format(new GregorianCalendar().getTime());
        String newCreatedFolderName = System.getProperty("user.dir") + "/" + path;
        File dir = new File(newCreatedFolderName);
        if (dir.mkdirs()) {
            System.out.println("Folder [" + newCreatedFolderName + "] is created successfully.");
        } else
            System.out.println("Error::Folder [" + newCreatedFolderName + "] is not created.");
    }
    @Then("Get latest directory from {string} and store in variable {string}")
    public void getChildDirectory(String parentDirectory, String varName) {
        File file = new File(parentDirectory);
        String[] folderNames = file.list();
        assert folderNames != null;
        for (String name : folderNames) {
            if (new File(parentDirectory + name).isDirectory()) {
                String rollingRawDataFolder = System.getProperty("user.dir") + "/" + parentDirectory + name;
                this.context.setVar(varName, rollingRawDataFolder);
                System.out.println("Folder to save raw data: [" + rollingRawDataFolder + "]");
            }
        }
    }
    @And("Compare raw data {string} result with {string} xml")
    public void compareRawDataResultWithXml(String actualString, String expectedString) throws IOException {

        BufferedReader reader1 = new BufferedReader(new FileReader(this.context.resolve(actualString)));

        BufferedReader reader2 = new BufferedReader(new FileReader(this.context.resolve(expectedString)));
        BufferedWriter bw;
        String line1 = reader1.readLine();

        String line2 = reader2.readLine();

        boolean areEqual = true;

        int lineNum = 1;

        while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
                areEqual = false;

                break;
            } else if (!line1.equalsIgnoreCase(line2)) {
                areEqual = false;

                break;
            }

            line1 = reader1.readLine();

            line2 = reader2.readLine();

            lineNum++;
        }

        if (areEqual) {
            System.out.println("___________________________________________________");
            System.out.println("Two files have same content.");
            System.out.println("___________________________________________________");

        } else {

            System.out.println("Two files have different content. They differ at line " + lineNum);

            System.out.println("File has " + line1 + " and \nFile2 has " + line2 + " at line " + lineNum);

            Assert.assertEquals(line1, line2, "Differences found");
        }

        reader1.close();
        reader2.close();
    }

}


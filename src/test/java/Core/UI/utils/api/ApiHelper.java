package Core.UI.utils.api;

import Core.UI.Const;
import Core.UI.TestContext;
import Core.UI.utils.FilesUtil;
import Core.UI.utils.PK_UI_Framework;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.Method;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class ApiHelper {
    private static final Logger LOGGER = PK_UI_Framework.getLogger(ApiHelper.class);
    public static Map<String,String> queryStringToMap(String text){
        Map<String,String> map = new LinkedHashMap<String,String>();
        if(text != null){
            String[] pairs = text.split("&");
            for(String pair: pairs){
                int idx = pair.indexOf("=");
                try{
                    map.put(URLDecoder.decode(pair.substring(0,idx), "UTF-8"), URLDecoder.decode(pair.substring(idx+1), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    map.put(pair.substring(0,idx),pair.substring(idx+1));
                }
            }
        }
        return map;
    }
    public static Map<String,String> parseJSONObjectToMap(String jsonText){
        Map<String,String> mapData = new HashMap<>();
        JSONObject jsonObject = null;
        if(jsonText.trim().startsWith("[")){
            jsonObject = new JSONArray(jsonText).getJSONObject(0);
        }
        else{
            jsonObject = new JSONObject(jsonText);
        }
        Iterator<String> keysIterator = jsonObject.keys();
        while(keysIterator.hasNext()){
            String key = keysIterator.next();
            String value = jsonObject.get(key).toString();
            mapData.put(key, jsonObject.get(key).toString());
        }
        return mapData;
    }
    public static void addMapVarToContextFromFile(TestContext context, String fileName, ApiContextVars apiVar) {
        Map<String,String> newMap = FilesUtil.readFileAsMap(fileName);
        newMap = context.resolveMapValues(newMap);
        addMapToContext(context,newMap,apiVar);
    }

    public static void addMapKeyValueToContext(TestContext context, String key,String value, ApiContextVars apiVar) {
        Map<String,String> newMap = new HashMap<>();
        newMap.put(key,value);
        addMapToContext(context,newMap,apiVar);
    }
    public static void addMapToContext(TestContext context, Map<String,String> newMapValues, ApiContextVars apiVar){
        Map<String,String> existingMap= new HashMap<>();
        Object var = context.var(apiVar);
        if(var!=null){
            existingMap = (Map<String, String>) var;
        }
        existingMap.putAll(newMapValues);
        context.setVar(apiVar, existingMap);
    }
    public static Response makeCall(TestContext context){

        RequestSpecBuilder request = new RequestSpecBuilder();

        // Header
        if(context.var(ApiContextVars.HEADERS)!=null) {
            Map<String, String> map = (Map<String, String>) context.var(ApiContextVars.HEADERS);
            if(!map.isEmpty()){
                request.addHeaders(map);
            }
        }
        //URL
        Object url = context.resolve(Const.ENV_URL);
        LOGGER.info(Const.ENV_URL + " : "+url);
        if(url!=null){
            request.setBaseUri(url.toString());
        }
        Object path = context.var(ApiContextVars.PATH);
        LOGGER.info("Path : "+path);
        if(path!=null){
            request.setBasePath(path.toString());
        }
        Map<String,String> queryParamFromPath = new HashMap<String,String>();
//        if(path.toString().contains("&")){
//            queryParamFromPath = queryStringToMap(path.toString());
//        }
        //Query_param
        Object queryParamFromVar = context.var(ApiContextVars.QUERY_STRING_PARAMETER);
        if(queryParamFromVar!=null){
            queryParamFromPath.putAll((Map<String,String>)queryParamFromVar);
        }
        if(!queryParamFromPath.isEmpty()){
            request.addQueryParams(queryParamFromPath);
        }
        // BODY
        Object body = context.var(ApiContextVars.BODY);
        if(body!=null){
            if(body instanceof byte[]){
                request.setBody((byte[])body);
            }
            else if(body instanceof String) {
                request.setBody((String)body);
            }
            else if(body instanceof File) {
                request.setBody((File)body);
            }
            else {
                request.setBody(body);
            }
        }
        //Auth
        Object auth = context.var(ApiContextVars.AUTH);
        if(auth!=null){
            request.setAuth((AuthenticationScheme)auth);
        }
        //Cookies
        Object cookies = context.var(ApiContextVars.COOKIES);
        if(cookies!=null){
            request.addCookies((Map<String,String>)cookies);
        }
        //URL_PARAM
        Object urlPathParams = context.var(ApiContextVars.URL_PARAM);
        if(urlPathParams!=null){
            HashMap<String,String> map = (HashMap<String, String>) urlPathParams;
            request.addPathParams(map);
        }
        //Form_data
        Object formData = context.var(ApiContextVars.FORM_DATA);
        if(formData!=null){
            HashMap<String,String> map = (HashMap<String, String>) formData;
            request.addFormParams(map);
        }
        // FILES
        Object files = context.var(ApiContextVars.FILES);
        if(files!=null){
            HashMap<String,MultiPartFile> map = (HashMap<String, MultiPartFile>) files;
            for(String name: map.keySet()){
                MultiPartFile filePart = map.get(name);
                request.addMultiPart(name, filePart.getFile(), filePart.getMimeType());

            }
        }
        if(context.resolve(Const.API_LOG)!=null) {
            String[] logs = context.resolve(Const.API_LOG).split(",");
            for(String log:logs) {
                request = setLog(request,log.trim());
            }
        }
        //RestAssuredConfig config = RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("application/text", ContentType.TEXT));

        // Build Request Specification
//      RequestSpecification requestSpec =  RestAssured.given(request.build()).relaxedHTTPSValidation().config(config);
        RequestSpecification requestSpec =  RestAssured.given(request.build()).relaxedHTTPSValidation();

        Object config = context.var(ApiContextVars.REST_ASSURED_CONFIG);
        if(config!=null){
            requestSpec = requestSpec.config((RestAssuredConfig)config);
        }
        Object object =  context.var(ApiContextVars.URL_ENCODING);
        if(object!=null){
            requestSpec.urlEncodingEnabled((Boolean)object);
        }
        Object method = context.var(ApiContextVars.METHOD);
        if(method==null){
            LOGGER.info("Request method was not specified, defaulting to GET");
            method = Method.GET;
        }
        else{
            method = Method.valueOf(method.toString().trim().toUpperCase());
        }
        Response response = requestSpec.request((Method) method).andReturn();

        LOGGER.info("Response Status: "+response.getStatusCode());
        LOGGER.info("Response Content Type: "+response.getContentType());
        LOGGER.info("Response Headers: "+response.getHeaders());
        LOGGER.info("Response Body: "+response.body().asString());

        context.setVar("_"+ApiContextVars.RESPONSE_STATUS.name(), response.getStatusCode());
        context.setVar("_"+ApiContextVars.RESPONSE_CONTENT_TYPE.name(), response.getContentType());
        context.setVar("_"+ApiContextVars.RESPONSE_HEADERS.name(), response.getHeaders());
        context.setVar("_"+ApiContextVars.RESPONSE_BODY_TEXT.name(), response.body().asString());

        context.setVar(ApiContextVars.RESPONSE, response);
        context.setVar(ApiContextVars.RESPONSE_STATUS, response.getStatusCode());
        context.setVar(ApiContextVars.RESPONSE_CONTENT_TYPE, response.getContentType());
        context.setVar(ApiContextVars.RESPONSE_HEADERS, response.getHeaders());
        context.setVar(ApiContextVars.RESPONSE_BODY_TEXT, response.body().asString());

        return response;
    }


    public static RequestSpecBuilder setLog(RequestSpecBuilder requestSpec, String log) {
        //disabled,all,params,body,headers,cookies,method,path,ifError,statusLine
        if(log.equalsIgnoreCase("disabled")){
            return requestSpec;
        }
        requestSpec.log(LogDetail.valueOf(log.toUpperCase()));
        return requestSpec;
    }
    public static void validateJsonSchemaText(String txt, String expectedSchemaText) {
        try {
            if(expectedSchemaText.trim().startsWith("<")) {
                MatcherAssert.assertThat(txt, RestAssuredMatchers.matchesXsd(expectedSchemaText));
            }
            else {
                JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchema(expectedSchemaText);
                MatcherAssert.assertThat(txt, validator);
            }
        }catch (Exception e){
            LOGGER.info("Failed to match schema, error: "+ e.toString());
        }
    }
    public static void validateJsonSchemaFile(String responseBodytext, String schemaFile) {
        try {
            if(schemaFile.toLowerCase().endsWith(".json")) {
                JsonSchemaValidator validator = JsonSchemaValidator.matchesJsonSchema(FilesUtil.getInputStream(schemaFile));
                MatcherAssert.assertThat(responseBodytext, validator);
            }
            else if(schemaFile.toLowerCase().endsWith(".xsd")){
                MatcherAssert.assertThat(responseBodytext, RestAssuredMatchers.matchesXsd(FilesUtil.getInputStream(schemaFile)));
            } else {
                Assert.fail("Invalid schema file, Please use .xsd (for SOAP Api) or .json (for REST api)");
            }
        }catch (IOException e){
            LOGGER.info("Failed to read schema fie ["+schemaFile+"]");
        }
    }
    public static boolean isJsonPathValid(String jsonString,String path){
        try {
            String value = JsonPath.read(jsonString, path).toString();
            if(value.equals("[]")){
                return false;
            }
        } catch(PathNotFoundException e) {
            return false;
        }
        return true;
    }
    public static boolean isXmlPathValid(String xmlString,String path){
        try {
            //TODO: VT: Fix invalid path not throwing exception
            String value = new XmlPath(xmlString).getString(path);
            if(value.isEmpty()){
                return false;
            }
        } catch(PathNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String extractValueResponse(String responseText, String xmlOrJson, String path) {
        switch(xmlOrJson.trim().toLowerCase()) {
            case "json":
                return JsonPath.read(responseText, path).toString();
            case "xml":
                return XmlPath.from(responseText).getString(path);
                //return XmlPath.from(responseText).get(path);

            default:
                throw new RuntimeException("Invalid type of path ["+xmlOrJson+"], Possible valid types [json|xml]");
        }
    }

    public static Map<String,String> getListOfNameValuePairsAsMap(List<Map<String,String>> mapList, String keyName, String valueName){
        Map<String,String> finalMap = new HashMap<>();
        if(mapList!=null) {
            for (Map<String, String> map : mapList) {
                finalMap.put(map.get(keyName), map.get(valueName));
            }
        }
        return finalMap;

    }


}

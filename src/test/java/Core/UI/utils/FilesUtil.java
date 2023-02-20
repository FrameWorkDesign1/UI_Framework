package Core.UI.utils;


import Core.UI.utils.api.ApiHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FilesUtil {

    final static Logger LOGGER = PK_UI_Framework.getLogger(FilesUtil.class);

    public static InputStream getInputStream(String file) throws IOException {

        InputStream stream = null;
        try {
            Path f = Paths.get(file);
            if (Files.exists(f)) {
                // try directly from path
                stream = Files.newInputStream(f);
            } else {
                // Load from classloader
                URL url = ClassLoader.getSystemResource(file);
                if (url != null) {
                    stream = Files.newInputStream(Paths.get(url.toURI()));
                } else {
                    return new URL(file).openStream();
                }
            }
            return stream;

        } catch (IOException e) {
            throw new IOException("Exception while finding resource file: " + file, e);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid url path for resource: " + file, e);
        }
    }

    public static Path getPath(String filePath) {
        Path f = Paths.get(filePath);
        if (Files.exists(f)) {
            return f;
        }
        URL url = ClassLoader.getSystemResource(filePath);
        if (url != null) {
            try {
                return Paths.get(url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.toString(), e);
            }
        } else {
            throw new RuntimeException("Failed to find resource file : " + filePath);
        }

    }

    public static boolean fileLineStartsWith(String filePath, String textStartsWith) throws IOException {
        boolean found = false;
        List<String> allLines = Files.readAllLines(Paths.get(filePath));
        LOGGER.info("File Path: " + filePath + ", Expected line to starts with: " + textStartsWith);
        LOGGER.info("File context:" + allLines);
        for (String line : allLines) {
            if (line.startsWith(textStartsWith)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static List<String> getFileContent(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public static List<String> getFileContent(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }


    public static String getFileName(String path) {
        return new File(path.replace("classpath:", "")).getName();
    }

    public static String getAbsoluteFilePath(String path) {
        path = path.replace("classpath:", "");
        Path p = getPath(path);
        //return FilesUtil.class.getClassLoader().getResource(path).getFile();
        return p.toAbsolutePath().toString();

    }

    public static String processFileName(String fileName) {

        if (fileName.contains("YYYYMMDD")) {
            String date = DateTimeUtil.getDate("yyyy/MM/dd");
            fileName = fileName.replace("YYYYMMDD", date);
        }
        return fileName;
    }

    public static Map<String, Object> getJsonAsMap(String filename){
        String json = getFileContentAsString(filename);
        Map<String, Object> retMap = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Can't parse string to Map ", e);
        }
    }

    public static Map<String, String> getPropertiesMap(String filePath) {
        Properties properties = new Properties();
        Map<String, String> myMap = new HashMap<String, String>();
        try {
            properties.load(getInputStream(filePath));
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                myMap.put(key, value);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load properties to vars, Error: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load properties to vars, Error: " + e.getMessage(), e);
        }
        return myMap;
    }

    public static void cleanDirectory(String path) {
        try {
            FileUtils.cleanDirectory(new File(path));
            LOGGER.info("Done cleaning folder: " + path);
        } catch (IOException e) {
            // Ignore
            e.printStackTrace();
            LOGGER.info("Skipping error : Failed to clean folder:" + path + ", Error: " + e.getMessage());
        }
    }

    public static Map<String, String> readCsvFileAsMapByKeyValue(String filePath, int colIndex, String value) {
        return readCsvFileAsMapByKeyValue(filePath, colIndex, value,',');
    }
    public static Map<String, String> readCsvFileAsMapByKeyValue(String filePath, int colIndex, String value, char delimiter) {
        InputStream is = null;
        Map<String, String> map = null;
        if (!filePath.toLowerCase().contains(".csv")) {
            throw new RuntimeException("Error while loading properties from csv with index, [" + filePath + "] Not a .csv file to load properties.");
        }
        try {
            is = FilesUtil.getInputStream(filePath);
            Object[][] csvData = CSVReader.getCsvDataIncludeHeaders(is, delimiter);

            // with headers 2 Rows X Multi Col
            map = new HashMap<>();
            for (int i = 0; i < csvData.length; i++) {
                String act = csvData[i][colIndex].toString();
                if (act.equals(value)) {
                    for (int j = 0; j < csvData[i].length; j++) {
                        map.put("column_index_" + j, csvData[i][j].toString());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file [" + filePath + "], Error [" + e.getClass() + " : " + e.getMessage() + "]", e);
        }
        if (map == null) {
            throw new RuntimeException("Failed to read file content [" + filePath + "]");
        }
        return map;
    }
    public static List<Map<String, String>> readCsvFileAsListOfMap(String filePath,char delimiter) {
        InputStream is = null;
        List<Map<String, String>> list = new ArrayList<Map<String,String>>();
        if (!filePath.toLowerCase().contains(".csv")) {
            throw new RuntimeException("Error while loading properties from csv with index, [" + filePath + "] Not a .csv file to load properties.");
        }
        try {
            is = FilesUtil.getInputStream(filePath);
            List<String> headers = CSVReader.getCsvHeaders(is, delimiter);
            is.close();is=FilesUtil.getInputStream(filePath);
            Object[][] csvData = CSVReader.getCsvData(is, delimiter);
            is.close();
            for(int i=0;i<csvData.length;i++){
                Map row = new HashMap<String,String>();
                for(int j=0;j<headers.size();j++){
                    row.put(headers.get(j),csvData[i][j]);
                }
                list.add(row);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file [" + filePath + "], Error [" + e.getClass() + " : " + e.getMessage() + "]", e);
        }
        if (list == null) {
            throw new RuntimeException("Failed to read file content [" + filePath + "]");
        }
        return list;
    }
    public static Map<String, String> readCsvFileAsMapByKeyValue(String filePath, String colName, String value){
        return readCsvFileAsMapByKeyValue( filePath, colName, value,',');
    }
    public static Map<String,String> readCsvFileAsMapByKeyValue(String filePath,String colName,String value,char delimiter){
        InputStream is =null;
        Map<String,String> map=null;
        if(!filePath.toLowerCase().contains(".csv")){
            throw new RuntimeException("Error while loading properties from csv with index, ["+filePath+"] Not a .csv file to load properties.");
        }
        try {
            is = FilesUtil.getInputStream(filePath);
            Object[][] csvData = CSVReader.getCsvData(is,delimiter);
            is = FilesUtil.getInputStream(filePath);
            List<String> headers = CSVReader.getCsvHeaders(is,delimiter);
            if(!headers.contains(colName)){
                throw new RuntimeException("Failed to read csv file, Csv column header["+colName+"] not found in actual headers"+headers);
            }
            // with headers 2 Rows X Multi Col
            map = new HashMap<>();
            for(int i=0;i<csvData.length;i++){
                String act = csvData[i][headers.indexOf(colName)].toString();
                if(act.equals(value)){
                    for(int j=0;j<headers.size();j++){
                        map.put(headers.get(j),csvData[i][j].toString());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file ["+filePath+"], Error ["+e.getClass()+" : "+e.getMessage()+"]",e);
        }
        if(map==null){
            throw new RuntimeException("Failed to read file content ["+filePath+"]");
        }
        return map;
    };
    public static Map<String,String> readCsvFileAsMapByIndex(String filePath,int rowNumber,char delimiter){
        InputStream is =null;
        Map<String,String> map=null;
        if(!filePath.toLowerCase().contains(".csv")){
            throw new RuntimeException("Error while loading properties from csv with index, ["+filePath+"] Not a .csv file to load properties.");
        }
        try {
            is = FilesUtil.getInputStream(filePath);
            Object[][] csvData = CSVReader.getCsvData(is,delimiter);
            is = FilesUtil.getInputStream(filePath);
            List<String> headers = CSVReader.getCsvHeaders(is,delimiter);
            // with headers 2 Rows X Multi Col
            map = new HashMap<>();
            for(int i=0;i<headers.size();i++){
                map.put(headers.get(i),csvData[rowNumber-1][i].toString());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file ["+filePath+"], Error ["+e.getClass()+" : "+e.getMessage()+"]",e);
        }
        if(map==null){
            throw new RuntimeException("Failed to read file content ["+filePath+"]");
        }
        return map;

    }
    public static Map<String,String> readFileAsMap(String filePath){
        return readFileAsMap(filePath,',');
    }
    public static Map<String,String> readFileAsMap(String filePath,char separator){

        InputStream is =null;

        Map<String,String> map=null;
        try{
            is = FilesUtil.getInputStream(filePath);
            if(filePath.toLowerCase().contains(".csv")){
                Object[][] csvData = CSVReader.getCsvData(is,separator);
                map = new HashMap<>();
                if(csvData.length==2 && csvData[0].length==2  ){
                    // without header 2 cols X Multi Row
                    for (Object[] objects : csvData) {
                        map.put(objects[0].toString(), objects[1].toString());
                    }

                }
                else {
                    is = FilesUtil.getInputStream(filePath);
                    List<String> headers = CSVReader.getCsvHeaders(is,separator);
                    // with headers 2 Rows X Multi Col
                    for(int i=0;i<headers.size();i++){
                        map.put(headers.get(i),csvData[0][i].toString());
                    }
                }

            }
            else if(filePath.toLowerCase().contains(".properties")){
                map = new HashMap<>();
                Properties prop = new Properties();
                prop.load(is);
                for(Object key : prop.keySet()){
                    map.put(key.toString(), prop.getProperty(key.toString()));
                }

            } else if(filePath.toLowerCase().contains(".json")){
                String txt = IOUtils.toString(is,StandardCharsets.UTF_8);
                map = ApiHelper.parseJSONObjectToMap(txt);
            } else{
                LOGGER.debug("Considering as a plain text json file ..");
                String txt = IOUtils.toString(is,StandardCharsets.UTF_8);
                map = ApiHelper.parseJSONObjectToMap(txt);
            }
        }
        catch (Exception e){
            LOGGER.error("Failed to read file ["+filePath+"], Error ["+e.getClass()+" : "+e.getMessage()+"]",e);
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.debug("Ignoring error while closing input stream, Error:["+e.getClass()+": "+e.getMessage()+"]",e);
                }
            }
        }
        if(map==null){
            throw new RuntimeException("Failed to read file content ["+filePath+"]");
        }
        return map;
    }
    public static byte[] getFileContentAsByteArray(String filePath) throws IOException {
        return Files.readAllBytes(getPath(filePath));
    }
    public static String getFileContentAsString(String filePath){
        Map<String,String> map=null;
        InputStream is =null;
        try {
            is = FilesUtil.getInputStream(filePath);
            return IOUtils.toString(is,StandardCharsets.UTF_8);
        }
        catch (Exception e){
            LOGGER.error("Failed to read file ["+filePath+"], Error ["+e.getClass()+" : "+e.getMessage()+"]",e);
            throw new RuntimeException("Failed to read file ["+filePath+"], Error ["+e.getClass()+" : "+e.getMessage()+"]",e);
        }
        finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.debug("Ignoring error while closing input stream, Error:["+e.getClass()+": "+e.getMessage()+"]",e);
                }
            }
        }
    }
    public static void saveToFile(String text,String file){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, false);
            IOUtils.write(text,os,StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to File ["+file+"]",e);
        }
    }
    @Deprecated
    public static void saveToFIle(String text,String file){
        saveToFile(text, file);
    }

    public static void saveToFile(InputStream inputStream, String file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, false);
            IOUtils.copy(inputStream,os);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to File ["+file+"]",e);
        }
        finally{
            if(os!=null){
                try{os.close();}catch (IOException e){};
            }
        }
    }

    public static void saveToFIle(byte[] bytes,String file){
        saveToFile(bytes,file);
    }
    public static void saveToFile(byte[] bytes,String file){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, false);
            IOUtils.write(bytes,os);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to File ["+file+"]",e);
        } finally {
            if(os!=null){
                try {
                    os.close();
                }catch (Exception e){//ignore
                }
            }
        }
    }

    public synchronized static boolean appendDataToCsv(String fileName, Map<String, Object> allConfigs) {
        synchronized (LOGGER) {
            StringBuilder body = new StringBuilder();
            StringBuilder headers = new StringBuilder();
            String prefix = "";
            for (String key : allConfigs.keySet()) {
                headers.append(prefix);
                body.append(prefix);
                headers.append(escapeSpecialCharactersForCsv(key));
                body.append(escapeSpecialCharactersForCsv(allConfigs.get(key)));
                prefix = ",";
            }

            LOGGER.debug("HEADERS: " + headers);
            LOGGER.debug("BODY: " + body);

            File file = new File(fileName);
            boolean addHeader = !file.exists();
            try (FileWriter f = new FileWriter(fileName, true);
                 BufferedWriter b = new BufferedWriter(f);
                 PrintWriter p = new PrintWriter(b);) {
                if (addHeader) {
                    p.println(headers);
                }
                p.println(body);

            } catch (IOException e) {
                LOGGER.error("Ignoring error : Failed to write to csv file: " + fileName, e);
                return false;
            }
            return true;
        }
    }
    public static String escapeSpecialCharactersForCsv(Object o) {
        if(o==null){
            return "null";
        }
        String data = o.toString();
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    public static boolean deleteFile(String path) {
        try{Path fileToDeletePath = Paths.get(path);
        Files.delete(fileToDeletePath);}
        catch (Exception e){
            return false;
        }
        return true;
    }


}

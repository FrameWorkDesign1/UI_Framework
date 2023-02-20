package Core.UI.utils;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CSVReader{

    private static final Logger LOGGER = PK_UI_Framework.getLogger(CSVReader.class);
    public static List<String> getCsvHeaders(InputStream is,char delimiter) {
        List<String> headerList = new ArrayList<>();
        String[] headerArr = null;

        CSVFormat fmt = CSVFormat.DEFAULT.withHeader().withDelimiter(delimiter).withIgnoreEmptyLines();
//        if(isCsvTrimEnabled()) {
//            fmt = fmt.withIgnoreSurroundingSpaces();
//        }
        //try (CSVParser csvParser = CSVParser.parse(path.toFile(), StandardCharsets.UTF_8, fmt)) {
        try (CSVParser csvParser = CSVParser.parse(is, StandardCharsets.UTF_8, fmt)) {
            headerArr = csvParser.getHeaderMap().keySet().toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException("Parse CSV error", e);
        }

        headerList.addAll(Arrays.asList(headerArr));
        for(int i = 0; i < headerList.size(); i++) {
            if(headerList.get(i).isEmpty()) {
                throw new RuntimeException("Header missing in stream");
            }
        }
        return headerList;
    }

    public static Object[][] getCsvData(Path p){
        return getCsvData(p,',');
    }
    public static Object[][] getCsvData(Path p,char separator){
        try {
            return getCsvData(Files.newInputStream(p),separator);
        } catch (IOException e) {
            throw new RuntimeException(e.getClass()+" : "+ e.getMessage(), e);
        }
    }
    public static Object[][] getCsvData(InputStream is) {
        return getCsvData(is,',');
    }
    public static Object[][] getCsvDataIncludeHeaders(InputStream is,char separator) {
        CSVFormat fmt = CSVFormat.DEFAULT.withDelimiter(separator).withIgnoreEmptyLines();
//        if(isCsvTrimEnabled()) {
//            fmt = fmt.withIgnoreSurroundingSpaces();
//        }
        List<CSVRecord> records = null;

        try (CSVParser csvParser = CSVParser.parse(is, StandardCharsets.UTF_8, fmt)) {
            records = csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException("Parse CSV error", e);
        }

        Object[][] params = new Object[records.size()][];

        for(int i = 0; i < records.size(); i++) {

            String[] line = new String[records.get(i).size()];
            for (int j = 0; j < records.get(i).size(); j++) {
                line[j] = records.get(i).get(j);
            }
            params[i] = line;
        }

        return params;
    }
    public static Object[][] getCsvData(InputStream is,char separator) {

        CSVFormat fmt = CSVFormat.DEFAULT.withHeader().withDelimiter(separator).withIgnoreEmptyLines();
//        if(isCsvTrimEnabled()) {
//            fmt = fmt.withIgnoreSurroundingSpaces();
//        }
        List<CSVRecord> records = null;
        try (CSVParser csvParser = CSVParser.parse(is, StandardCharsets.UTF_8, fmt)) {
            records = csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException("Parse CSV error", e);
        }
        Object[][] params = new Object[records.size()][];
        for(int i = 0; i < records.size(); i++) {
            String[] line = new String[records.get(i).size()];
            for (int j = 0; j < records.get(i).size(); j++) {
                    line[j] = records.get(i).get(j);
            }
            params[i] = line;
        }
        return params;
    }


    public static Object[][] mergeCsvHeadersAndData(List<String> headers, Object[][] data) {

        String[] headerArr = headers.toArray(new String[0]);
        Object[][] all = new Object[data.length][];

        // add header to beginning of each row
        for(int i = 0; i < data.length; i++) {
            Object[] line = new Object[data[i].length + 1];
            line[0] = headerArr;
//            if(isCsvTrimEnabled()) {
                for(int j = 0; j < data[i].length; j++) {
                    line[j + 1] = ((String) data[i][j]).trim();
                }
//            }
//            else{
                for(int j = 0; j < data[i].length; j++) {
                    line[j + 1] = ((String) data[i][j]);
//                }
            }
            // nest whole array into object[] to be consumed by setDataVariables
            all[i] = new Object[] { line };
        }

        return all;
    }


    public static Object[][] mergeParams(Object[][] original, Object[][] toAdd) {

        if(original.length == 0) {
            return toAdd;
        }

        int numRows = toAdd.length;
        if(numRows == 0) {
            LOGGER.info("No test data found");
            return new Object[][] {};
        }
        int numCols = toAdd[0].length;

        int lengthToAdd = original[0].length;

        Object[][] newParams = new Object[numRows * original.length][numCols + lengthToAdd];

        for(int k = 0; k < original.length; k++) {
            Object[] array = original[k];
            int offset = k * numRows;
            for(int i = 0; i < numRows; i++) {
                for(int l = 0; l < lengthToAdd; l++) {
                    newParams[i + offset][l] = array[l];
                }
                for(int j = 0; j < numCols; j++) {
                    newParams[i + offset][j + lengthToAdd] = toAdd[i][j];
                }
            }
        }
        return newParams;
    }
    /*
     * This method is verify the duplicate Key in the file, If duplicate key is find in the file then it will return message with duplicate key name and location.
     */


    public static String duplicateData(Path path,char separator) throws FileNotFoundException {
        List<Object> dataSet = new ArrayList<Object>();
        String duplicateEntrymsg = null;
        String verifyFile[] = (path.toString()).split("\\.");
        String fileType = verifyFile[(verifyFile.length) - 1];
        switch (fileType) {
            case "csv":

                Object[][] csvData = getCsvData(path,separator);
                for(int i = 0; i < csvData.length; i++) {
                    if(dataSet.contains(csvData[i][0]) && csvData[i][0].toString().length() > 1) {
                        return duplicateEntrymsg = "Variable name is duplicate, Rename the variable " + csvData[i][0] + " On Line number " + (i + 2);
                    }
                    dataSet.add(csvData[i][0]);
                }
                break;
            case "properties":
                Scanner scan = new Scanner(new File((path.toAbsolutePath()).toString()));
                while (scan.hasNextLine()) {
                    String line = scan.nextLine();
                    if(line.length() > 1) {
                        String split[] = line.trim().split(" ");
                        if(dataSet.contains(split[0])) {
                            return duplicateEntrymsg = "Variable name is duplicate, Rename the variable " + " '  " + split[0] + "  '" + " On Line number " + (dataSet.size() + 1);
                        }
                        dataSet.add(split[0]);
                    }
                }

                break;
            default:
                LOGGER.info("Invalid file format " + fileType);
        }
        return duplicateEntrymsg;

    }


    private static Object[][] getIterationData(String iteration, Object[][] data) {
        List<Object> rangeValue = new ArrayList<>();
        int startRange;
        int endRange;
        if(!(iteration.contains(",")) && !(iteration.contains("-"))) {
            endRange = Integer.valueOf(iteration);
            endRange = endRange>=data.length?data.length:endRange;
            for(int i = 0; i < endRange; i++) {
                rangeValue.add(data[i]);
            }
            return rangeData(rangeValue);
        }
        String rangeCount[] = iteration.split(",");
        for(int i = 0; i < rangeCount.length; i++) {
            if(rangeCount[i].contains("-")) {
                String range[] = rangeCount[i].split("-");
                startRange = Integer.valueOf(range[0]);
                endRange = Integer.valueOf(range[1]);
                for(int j = startRange - 1; j < endRange; j++) {
                    rangeValue.add(data[j]);
                }
            } else {
                rangeValue.add(data[Integer.valueOf(rangeCount[i]) - 1]);
            }
        }
        return rangeData(rangeValue);
    }
    private static Object[][] rangeData(List<Object> rangeValue) {
        LinkedHashSet<Object> uniqueElements = new LinkedHashSet<Object>(rangeValue);
        rangeValue.clear();
        rangeValue.addAll(uniqueElements);
        Object[][] rangeData = new Object[rangeValue.size()][];
        for(int i = 0; i < rangeValue.size(); i++) {
            List<Object> row = new ArrayList<>();
            row.add(rangeValue.get(i));
            rangeData[i] = (Object[]) row.get(0);
            row.clear();
        }
        return rangeData;
    }
}


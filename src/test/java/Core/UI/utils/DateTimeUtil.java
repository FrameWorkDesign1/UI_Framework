package Core.UI.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;


public class DateTimeUtil {

    ///////////////// Date Time Operations ////////////////////

    private static LocalDateTime toLocalDataTime(String date, String sourceFormat) {
        LocalDateTime inputDate;
        if(date.equalsIgnoreCase("today") || date.equalsIgnoreCase("now")){
            inputDate =LocalDateTime.now();
        }
        else{
            try{
                inputDate=LocalDateTime.parse(date, DateTimeFormatter.ofPattern(sourceFormat));
            }
            catch (DateTimeParseException e){
                inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(sourceFormat)).atStartOfDay();
            }
        }
        return inputDate;
    }
    private static LocalDateTime performDateArithmetic(LocalDateTime d,String diff){

        String[] token = diff.trim().split(",");
        int numDiff = Integer.parseInt(token[0].trim());
        return d.plus(numDiff, ChronoUnit.valueOf(token[1].trim().toUpperCase()));
    }
    /* Method to return today's date in given format **/
    public static String getDate(String format){
        LocalDateTime now =LocalDateTime.now();
        return DateTimeFormatter.ofPattern(format).format(now);
    }
    /* Convert data from one format to other */
    public static String getDate(String sourceDate,String sourceFormat, String targetFormat){
        LocalDateTime inputDate = toLocalDataTime(sourceDate, sourceFormat);

        return DateTimeFormatter.ofPattern(targetFormat).format(inputDate);
    }
    /* perform date arithmetic on give date and date in target format */
    public static String getDate(String sourceDate,String sourceFormat, String diff,String targetFormat){
        LocalDateTime inputDate= toLocalDataTime(sourceDate, sourceFormat);
        LocalDateTime dateTime2 = performDateArithmetic(inputDate, diff);
        return DateTimeFormatter.ofPattern(targetFormat).format(dateTime2);
    }
}

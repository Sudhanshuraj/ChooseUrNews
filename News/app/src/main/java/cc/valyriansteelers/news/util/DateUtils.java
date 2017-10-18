package cc.valyriansteelers.news.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ankit on 12/10/17.
 */

public class DateUtils {

    public static String formatNewsApiDAte(String inputdate){

        String aa=inputdate+"asas";
        if(aa.equals("nullasas"))return "Time Not Available";
        else
        try{
            String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            String outputDateFormat= "EEE, d MMM yyyy KK:mm";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

            Date date = inputFormat.parse(inputdate);
            return outputFormat.format(date);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return inputdate;

    }
}

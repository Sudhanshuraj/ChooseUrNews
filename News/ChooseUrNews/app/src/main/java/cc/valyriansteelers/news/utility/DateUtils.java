package cc.valyriansteelers.news.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ankit on 12/10/17.
 */

/**Class for correcting time format of news articles*/
public class DateUtils {

    /**
     * It takes the input date from the site, and change it (date) to simple format.
     * if the date is not given, then it simply change it to Time not available
     * If given, then it check the format of date and change into the simple format for users and return this simple format.
     */

    public static String formatNewsApiDAte(String inputdate){


        if(inputdate==null)return "Time Not Available";
        else
            try{
                if(inputdate.length()>20){
                    inputdate = inputdate.substring(0,19)+"Z";
                }
                String inputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                String outputDateFormat= "EEE, d MMM HH:mm";
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


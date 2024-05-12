package org.doancnpm.Ultilities;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class DayFormat {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static String GetDayStringFormatted(Date date){
        return sdf.format(date);
    }
}

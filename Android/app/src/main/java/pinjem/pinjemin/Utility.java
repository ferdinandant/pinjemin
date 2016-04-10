package pinjem.pinjemin;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Utility
{
    /** ==============================================================================
     * Mengembalikkan nama bulan yang disingkat
     * -------------------------------------------------------------------------------
     * @param: monthNum - nomor bulan (Jan = 0, Des = 11)
     * @return: string Jan, Feb, Mar, ..., Des
     * ============================================================================== */
    public static String monthNameAbbr(int monthNum) {
        switch (monthNum) {
            case 0: return "Jan";
            case 1: return "Feb";
            case 2: return "Mar";
            case 3: return "Apr";
            case 4: return "Mei";
            case 5: return "Jun";
            case 6: return "Jul";
            case 7: return "Agu";
            case 8: return "Sep";
            case 9: return "Okt";
            case 10: return "Nov";
            default: return "Des";
        }
    }

    /** ==============================================================================
     * Mengembalikkan bentuk timestamp yang dicetak untuk post
     * -------------------------------------------------------------------------------
     * @param: timestamp - bentuk timestamp sql, e.g. "2017-01-20 14:12:56"
     * @return: bentuk timestamp yang sudah di-format, e.g. "1 menit"
     * ============================================================================== */
    public static String formatPostTimestamp(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar parsedTime = new GregorianCalendar();
            parsedTime.setTime(format.parse(timestamp));

            // sudah ganti tahun
            long yearDiff = currentTime.get(Calendar.YEAR) - parsedTime.get(Calendar.YEAR);
            if (yearDiff >= 1) {
                return "" + parsedTime.get(Calendar.YEAR);
            }

            // lebih dari sehari
            long msDiff = currentTime.getTime().getTime() - parsedTime.getTime().getTime();
            if (msDiff >= 1000 * 60 * 60 * 24) {
                return "" + parsedTime.get(Calendar.DATE) + " "
                        + monthNameAbbr(parsedTime.get(Calendar.MONTH));
            }

            // lebih dari sejam
            if (msDiff >= 1000 * 60 * 60) {
                return "" + (msDiff / (1000*60*60)) + " jam";
            }

            // sisanya, kembalikan dalam menit
            long minDiff = msDiff / (1000 * 60);
            minDiff = Math.max(minDiff, 1);
            return "" + minDiff + " menit";
        }
        catch (ParseException e) {
            return "n/a";
        }
    }
}

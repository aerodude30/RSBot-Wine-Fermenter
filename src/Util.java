import java.text.DecimalFormat;

/**
 * Created by cjb on 6/29/2016.
 * These methods are used for converting, formatting, and calculating data to be displayed for the graphics
 */
public class Util {

    /**
     * Formats experience gained to be in an hourly format
     * @param gained the experience gained
     * @param startTime the start time of the script
     * @return
     */
     String perHour(int gained, int startTime) {
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
    }

    /**
     * Formats numbers for thousands and millions
     * @param start the start time.
     * @return String value of the formatted number
     */
     String formatNumber(int start) {

        DecimalFormat nf = new DecimalFormat("0.0");
         if((double) start >= 1000000) {
            return nf.format(((double) start / 1000000)) + "m";
        }
        if((double) start >= 1000) {
            return nf.format(((double) start / 1000)) + "k";
        }
        return String.valueOf(start);
    }

    /**
     * Formats a millisecond time into hours/minutes/seconds
     * @param start Start time of the script in milliseconds
     * @return formatted String for runtime in the format (h:m:s)
     */
     String runtime(long start) {
        DecimalFormat nf = new DecimalFormat("00");
        long millis = System.currentTimeMillis() - start;

        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;

        return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
    }
}

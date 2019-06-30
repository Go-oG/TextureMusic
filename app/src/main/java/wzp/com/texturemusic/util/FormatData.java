package wzp.com.texturemusic.util;


import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by wang on 2017/2/17.
 * 该类用于一些常用数据之间的转换
 */

public class FormatData {
    private static final String TAG = "FormatData";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    /**
     * 将时间封装为mm:ss格式
     *
     * @param times 212123123 格式毫秒
     * @return
     */
    public static String timeValueToString(long times) {
        Date date = new Date(times);
        return sdf.format(date);
    }

    /**
     * 将形如3459749格式的文件大小 字符串封装为
     * 形如5.31MB格式的字符串
     *
     * @param fileSize 单位 B
     * @return
     */
    public static String fileSizeToString(long fileSize) {
        if (fileSize > 1024 * 1024) {
            long mm = fileSize / (1024 * 1024);
            long ss = fileSize % (1024 * 1024);
            if (ss == 0) {
                return mm + "MB";
            } else {
                float ysize = ss / (1024 * 1024);
                mm += ysize;
                return mm + "MB";
            }
        } else {
            //小于1M
            return fileSize / 1024 + "KB";
        }
    }

    /**
     * 把数值转为字符串
     * 如1000000->100万   100000—>10万  1200000->120万
     * 如10000000->1000万
     *
     * @param values
     * @return
     */
    public static String longValueToString(long values) {
        if (values >= 100000000) {
            long a = (values / 100000000);//亿
            long q = (values % 100000000 / 10000000);//千万位
            if (q <= 0) {
                return a + "亿";
            } else {
                return a + "." + q + "亿";
            }
        }
        if (values >= 10000000) {
            long q = (values / 10000000);
            long b = ((values % 10000000) / 1000000);
            long s = (((values % 10000000) % 1000000) / 100000);
            long w = (((values % 10000000) % 1000000) % 100000) / 10000;
            return q + "" + b + "" + s + "" + w + "万";
        }
        if (values >= 1000000) {
            long b = (values / 1000000);
            long s = (values % 1000000) / 100000;
            long w = ((values % 1000000) % 100000) / 10000;
            return b + "" + s + "" + w + "万";
        }
        if (values >= 100000) {
            long s = (values / 100000);
            long w = (values % 100000) / 10000;
            return s + "" + w + "万";
        }
        return values + "";
    }

    /**
     * unix时间戳转换
     *
     * @param unix
     * @return
     */
    public static String unixTimeTostring(Long unix) {
        String re;
        String time = String.valueOf(unix);
        time = time.substring(0, time.length() - 3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        re = sdf.format(new Date(Long.valueOf(time) * 1000L));
        return re;
    }

    /**
     * 将歌词时间解析为int
     * 格式00:00.00;
     * 或者 00:00.000
     */
    public static int lyricsTimeToString(String time) {
        int relust;
        if (time.length() >= 7) {
            int one = Integer.valueOf(time.substring(0, 1));
            int two = Integer.valueOf(time.substring(1, 2));
            int three = Integer.valueOf(time.substring(3, 4));
            int four = Integer.valueOf(time.substring(4, 5));
            int five = Integer.valueOf(time.substring(6, 7));
            relust = (one * 10 * 60 * 1000) + (two * 60 * 1000) + (three * 10 * 1000) + (four * 1000) + (five * 100);
        } else {
            relust = -1;
        }
        return relust;
    }


}

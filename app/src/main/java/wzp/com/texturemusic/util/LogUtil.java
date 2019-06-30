package wzp.com.texturemusic.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import wzp.com.texturemusic.core.config.AppFileConstant;

/**
 * Created by wang on 2017/2/17.
 * log日志类
 */

public class LogUtil {
    public static final String TEST_TAG = "TEST_TAG";
    public static boolean IS_DEBUG = true;

    public static void d(String tag, String msg) {
        if (IS_DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (IS_DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void showBigLog(String tag, String logContent) {
        if (logContent.length() > 3800) {
            String show = logContent.substring(0, 3800);
            e(tag, show);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length() - 3800) > 3800) {
                String partLog = logContent.substring(3800, logContent.length());
                showBigLog(tag, partLog);
            } else {
                String printLog = logContent.substring(3800, logContent.length());
                e(tag, printLog);
            }
        } else {
            e(tag, logContent);
        }

    }

    public static void test(String msg) {
        if (IS_DEBUG) {
            Log.e(TEST_TAG, msg);
        }
    }

    public static void saveLog(String log, boolean clearnOldData) {
        File file = new File(AppFileConstant.FILE_DRESS + "log");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter;
            if (clearnOldData) {
                fileWriter = new FileWriter(file, false);
            } else {
                fileWriter = new FileWriter(file, true);
                fileWriter.write("\n" + FormatData.unixTimeTostring(System.currentTimeMillis()) + "\n" + log);
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {

        }
    }


}

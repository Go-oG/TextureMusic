package wzp.com.texturemusic.api;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.util.FileUtil;
import wzp.com.texturemusic.util.LogUtil;
import wzp.com.texturemusic.util.StringUtil;

/**
 * author:Go_oG
 * date: on 2018/5/20
 * packageName: wzp.com.texturemusic.api
 */
public class CacheUtil {

    public static void cacheData(String url, String data) {
        String key = StringUtil.MD5(url);
        File file = new File(AppFileConstant.CACHE_DATA_INFO + key);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file,false);
            if (StringUtil.isEmpty(data)) {
                fileWriter.write("");
            } else {
                fileWriter.write(data);
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            LogUtil.test("文件缓存错误");
        }

    }

    public static String getCacheData(String url) {
        String key = StringUtil.MD5(url);
        File file = new File(AppFileConstant.CACHE_DATA_INFO + key);
        if (file.exists() && file.isFile()) {
            return FileUtil.fileToString(file);
        } else {
            return "";
        }
    }


}

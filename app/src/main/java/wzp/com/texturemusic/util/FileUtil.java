package wzp.com.texturemusic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


/**
 * Created by Go_oG
 * Description:
 * on 2018/1/7.
 */

public class FileUtil {

    /**
     * 获取文件或者目录的大小
     *
     * @return KB
     */
    public static long getFileSize(File file) {
        //字节
        long size = 0;
        if (file.exists()) {
            if (file.isFile()) {
                size = file.getUsableSpace();
            } else {
                //是目录
                size = getSize(file, 0L);
            }
        }
        return size / 1024;
    }

    /**
     * 清空某一个目录下的文件
     * @param file
     */
    public static void clearCache(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                boolean result = file.delete();
            } else {
                File[] files = file.listFiles();
                int size = files.length;
                for (int i = 0; i < size; i++) {
                    clearCache(files[i]);
                }
            }
        }
    }

    private static long getSize(File file, long size) {
        if (file != null) {
            if (file.isDirectory()) {//如果是文件夹
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (File fil : listFile) {
                        getSize(fil, size);//递归，直到把所有文件遍历完
                    }
                }
            } else {
                //是文件
                //返回的为byte 字节
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 将文本文件转化为String
     * @param file
     * @return
     */
    public static String fileToString(File file) {
        String result = "";
        if (file.exists() && file.isFile() && file.length() > 0) {
            FileInputStream inputStream = null;
            InputStreamReader streamReader = null;
            BufferedReader reader = null;
            try {
                inputStream = new FileInputStream(file);
                streamReader = new InputStreamReader(inputStream, "utf-8");
                reader = new BufferedReader(streamReader);
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                result = builder.toString();
                reader.close();
                streamReader.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将字符串保存为文件
     *
     * @param string
     * @param filePath
     * @param replace  true表示将 原来的文本内容覆盖掉
     */
    public static void stringToFile(String string, String filePath, boolean replace) {
        File file = new File(filePath);
        if (file.exists()) {
            if (replace) {
                //可以替换
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write("");
                    fileWriter.flush();
                    fileWriter.write(string);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                boolean re = file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(string);
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
            }
        }
    }


}

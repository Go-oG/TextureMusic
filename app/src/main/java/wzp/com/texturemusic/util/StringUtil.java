package wzp.com.texturemusic.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.core.config.AppConstant;

/**
 * Created by Go_oG
 * Description:字符串工具类
 * on 2018/2/6.
 */

public class StringUtil {

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || str.equals("null") || str.equals("NULL")
                || TextUtils.isEmpty(str.trim());
    }

    public static boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str) || str.equals("null") || str.equals("NULL");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static SpannableStringBuilder buildStringColor(String content, int color, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        builder.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder buildStringColor(SpannableStringBuilder builder, int color, int startIndex, int endIndex) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        builder.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * @param content
     * @param fontSize   单位dip
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static SpannableStringBuilder builderStringSize(CharSequence content, int fontSize, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(fontSize, true);
        builder.setSpan(sizeSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder builderStringSize(SpannableStringBuilder builder, int fontSize, int startIndex, int endIndex) {
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(fontSize, true);
        builder.setSpan(sizeSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 基线对齐
     *
     * @param resouceId
     */
    public static SpannableStringBuilder buildStringImage(CharSequence content, int resouceId, int startIndex, int endIndex, boolean alignBottom) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ImageSpan imageSpan;
        if (alignBottom) {
            imageSpan = new ImageSpan(MyApplication.getInstace(), resouceId, ImageSpan.ALIGN_BOTTOM);
        } else {
            imageSpan = new ImageSpan(MyApplication.getInstace(), resouceId, ImageSpan.ALIGN_BASELINE);
        }
        builder.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder buildStringImage(CharSequence content, Bitmap bitmap, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ImageSpan imageSpan = new ImageSpan(MyApplication.getInstace(), bitmap, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder buildStringImage(CharSequence content, Drawable drawable, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        builder.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder buildStringURL(CharSequence content, String url, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        URLSpan urlSpan = new URLSpan(url);
        builder.setSpan(urlSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder buildStringUnderline(CharSequence content, int startIndex, int endIndex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        builder.setSpan(underlineSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }


    /**
     * 处理歌曲的字符串
     */
    public static String dealMusicName(MusicBean bean) {
        String str = bean.getMusicName();
        if (!isEmpty(str)) {
            int size = AppConstant.MUSIC_FILE_TYPE.length;
            for (int i = 0; i < size; i++) {
                if (str.endsWith(AppConstant.MUSIC_FILE_TYPE[i])) {
                    str = str.substring(0, str.length() - AppConstant.MUSIC_FILE_TYPE[i].length());
                    break;
                }
            }
            str = str.trim();
            if (str.endsWith("-")) {
                str = str.substring(0, str.length() - 1);
            }
            if (str.startsWith("-")) {
                str = str.substring(1, str.length());
            }
            str = str.trim();
            return str;
        }
        return str;
    }

    public static String dealMusicName(String musicName, String albumName, String artistName) {
        if (!isEmpty(musicName)) {
            int size = AppConstant.MUSIC_FILE_TYPE.length;
            for (int i = 0; i < size; i++) {
                if (musicName.endsWith(AppConstant.MUSIC_FILE_TYPE[i])) {
                    musicName = musicName.substring(0, AppConstant.MUSIC_FILE_TYPE[i].length());
                    break;
                }
            }
            musicName = musicName.trim();
            if (musicName.endsWith("-")) {
                musicName = musicName.substring(0, musicName.length() - 1);
            }
            if (musicName.startsWith("-")) {
                musicName = musicName.substring(1, musicName.length());
            }
            musicName = musicName.trim();
            return musicName;
        }
        return musicName;
    }

    public static void removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
    }

    public static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String p_Url) {
            super(p_Url);
        }

        public void updateDrawState(TextPaint p_DrawState) {
            super.updateDrawState(p_DrawState);
            p_DrawState.setUnderlineText(false);
        }
    }


    public static String checkStringNull(String s) {
        if (isEmpty(s)) {
            return "";
        }
        return s;
    }

    public static String MD5(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(string.getBytes());
            return bytesToHexString(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            return string;
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

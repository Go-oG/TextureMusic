package wzp.com.texturemusic.util;

import android.net.Uri;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

/**
 * Created by Wang on 2018/2/21.
 * 用于替换Glide Url解析的
 */

public class GlideUrlUtil extends GlideUrl {
    private String mUrl;

    public GlideUrlUtil(URL url) {
        super(url);
        mUrl = url.toString();
    }

    public GlideUrlUtil(String url) {
        super(url);
        mUrl = url;
    }

    public GlideUrlUtil(URL url, Headers headers) {
        super(url, headers);
        mUrl = url.toString();
    }

    public GlideUrlUtil(String url, Headers headers) {
        super(url, headers);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return resetUrl(mUrl);
    }

    private String resetUrl(String s) {
        if (!StringUtil.isEmpty(s)) {
            String result;
            if (s.startsWith("http")) {
                Uri uri = Uri.parse(s);
                String size = "_" + uri.getQueryParameter("param");
                String s1 = uri.getLastPathSegment();
                if (!StringUtil.isEmpty(s1) && (s1.endsWith(".jpg") || s1.endsWith(".png"))) {
                    result = s1.substring(0, s1.length() - 4) + size;
                } else {
                    result = s;
                }
            } else {
                result = s;
            }
            return result;
        } else {
            return "url_empty";
        }
    }

}

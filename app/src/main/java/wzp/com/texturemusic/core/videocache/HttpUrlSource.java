package wzp.com.texturemusic.core.videocache;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.core.videocache.headers.EmptyHeadersInjector;
import wzp.com.texturemusic.core.videocache.headers.HeaderInjector;
import wzp.com.texturemusic.core.videocache.sourcestorage.SourceInfoStorage;
import wzp.com.texturemusic.core.videocache.sourcestorage.SourceInfoStorageFactory;
import wzp.com.texturemusic.util.LogUtil;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static wzp.com.texturemusic.core.videocache.Preconditions.checkNotNull;
import static wzp.com.texturemusic.core.videocache.ProxyCacheUtils.DEFAULT_BUFFER_SIZE;


/**
 * {@link Source} that uses http resource as source for {@link ProxyCache}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class HttpUrlSource implements Source {
    private static final String TAG = "HttpUrlSource";
    private static final int MAX_REDIRECTS = 5;
    public final SourceInfoStorage sourceInfoStorage;
    public final HeaderInjector headerInjector;
    public SourceInfo sourceInfo;
    private InputStream inputStream;
    ////////////////////////////
    private Response response;

    public HttpUrlSource(String url) {
        this(url, SourceInfoStorageFactory.newEmptySourceInfoStorage());
    }

    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage) {
        this(url, sourceInfoStorage, new EmptyHeadersInjector());
    }

    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        this.sourceInfoStorage = checkNotNull(sourceInfoStorage);
        this.headerInjector = checkNotNull(headerInjector);
        SourceInfo sourceInfo = sourceInfoStorage.get(url);
        this.sourceInfo = sourceInfo != null ? sourceInfo :
                new SourceInfo(url, Integer.MIN_VALUE, ProxyCacheUtils.getSupposablyMime(url));
    }

    public HttpUrlSource(HttpUrlSource source) {
        this.sourceInfo = source.sourceInfo;
        this.sourceInfoStorage = source.sourceInfoStorage;
        this.headerInjector = source.headerInjector;
    }

    @Override
    public synchronized long length() {
        if (sourceInfo.length == Integer.MIN_VALUE) {
            fetchContentInfo();
        }
        return sourceInfo.length;
    }

    @Override
    public void open(long offset) throws ProxyCacheException {
        try {
            response = openConnection(offset);
            String mime = response.header("Content-Type");
            if (response != null && response.body() != null) {
                InputStream is = response.body().byteStream();
                if (is == null) {
                    LogUtil.e(TAG, "输入流为空");
                } else {
                    inputStream = new BufferedInputStream(is, DEFAULT_BUFFER_SIZE);
                    long length = readSourceAvailableBytes(response, offset, response.code());
                    this.sourceInfo = new SourceInfo(sourceInfo.url, length, mime);
                    this.sourceInfoStorage.put(sourceInfo.url, sourceInfo);
                }
            }
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening connection for " + sourceInfo.url + " with offset " + offset, e);
        } catch (NullPointerException e) {
            throw new ProxyCacheException("Error opening connection  is maybe NUll");
        }
    }

    private long readSourceAvailableBytes(Response response, long offset, int responseCode) throws IOException {
        long contentLength = getContentLength(response);
        return responseCode == HTTP_OK ? contentLength
                : responseCode == HTTP_PARTIAL ? contentLength + offset : sourceInfo.length;
    }

    private long getContentLength(Response connection) {
        String contentLengthValue = connection.header("Content-Length");
        return contentLengthValue == null ? -1 : Long.parseLong(contentLengthValue);
    }

    @Override
    public void close() {
        ProxyCacheUtils.close(inputStream);
        if (response != null) {
            try {
                response.close();
            } catch (NullPointerException | IllegalArgumentException e) {
                String message = "Wait... but why? WTF!? " +
                        "Really shouldn't happen any more after fixing https://github.com/danikula/AndroidVideoCache/issues/43. " +
                        "If you read it on your device log, please, notify me danikula@gmail.com or create issue here " +
                        "https://github.com/danikula/AndroidVideoCache/issues.";
                throw new RuntimeException(message, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                LogUtil.e(TAG, "Error closing connection correctly. Should happen only on Android L. ");
            }
        }
    }

    @Override
    public int read(byte[] buffer) throws ProxyCacheException {
        if (inputStream == null) {
            throw new ProxyCacheException("Error reading data from " + sourceInfo.url + ": connection is absent!");
        }
        try {
            return inputStream.read(buffer, 0, buffer.length);
        } catch (InterruptedIOException e) {
            throw new InterruptedProxyCacheException("Reading source " + sourceInfo.url + " is interrupted", e);
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading data from " + sourceInfo.url, e);
        }
    }

    /**
     * 获取头部信息
     */
    private void fetchContentInfo() {
        Response response = null;
        Request request;
        Request.Builder builder = new Request.Builder();
        builder.url(this.sourceInfo.url);
        builder.head();
        request = builder.build();
        //不能添加任何的拦截器
        Call requestCall = MyApplication.getOkHttpClient2().newCall(request);
        try {
            response = requestCall.execute();
            String length = response.header("Content-Length", "-1");
            if (!TextUtils.isEmpty(length)) {
                this.sourceInfo.length = Long.valueOf(length);
            } else {
                this.sourceInfo.length = -1L;
            }
            this.sourceInfo.mime = response.header("Content-Type");
        } catch (IOException e) {
            this.sourceInfo.length = -1L;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private Response openConnection(long offset) throws IOException, ProxyCacheException {
        Response connection;
        boolean redirected;
        int redirectCount = 0;
        String url = this.sourceInfo.url;
        Request request;
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder = injectCustomHeaders(builder, url);
        if (offset > 0) {
            builder.addHeader("Range", "bytes=" + offset + "-");
        }
        request = builder.build();
        //不能添加任何的拦截器
        Call requestCall = MyApplication.getOkHttpClient2().newCall(request);
        connection = requestCall.execute();
        int code = connection.code();
        redirected = code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP || code == HTTP_SEE_OTHER;
        if (redirected) {
            url = connection.header("Location");
            redirectCount++;
            connection.close();
        }
        if (redirectCount > MAX_REDIRECTS) {
            throw new ProxyCacheException("Too many redirects: " + redirectCount);
        }
        return connection;
    }

    private Request.Builder injectCustomHeaders(Request.Builder builder, String url) {
        Map<String, String> extraHeaders = headerInjector.addHeaders(url);
        for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
            builder.addHeader(header.getKey(), header.getValue());
        }
        return builder;
    }

    public synchronized String getMime()  {
        if (TextUtils.isEmpty(sourceInfo.mime)) {
            fetchContentInfo();
        }
        return sourceInfo.mime;
    }

    public String getUrl() {
        return sourceInfo.url;
    }

    @Override
    public String toString() {
        return "HttpUrlSource{sourceInfo='" + sourceInfo + "}";
    }
}

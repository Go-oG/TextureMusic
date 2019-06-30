package wzp.com.texturemusic.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Go_oG
 * Description:用于下载文件
 * on 2018/1/25.
 */

public interface WYApiForDownload {
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileNetUrl);

}

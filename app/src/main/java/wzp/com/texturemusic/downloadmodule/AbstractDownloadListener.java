package wzp.com.texturemusic.downloadmodule;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;

/**
 * Created by Wang on 2018/3/12.
 */

public abstract class AbstractDownloadListener extends FileDownloadLargeFileListener {

    @Override
    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

    }

    @Override
    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {

    }

    @Override
    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

    }

    @Override
    protected void completed(BaseDownloadTask task) {

    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {

    }

    @Override
    protected void warn(BaseDownloadTask task) {

    }
}

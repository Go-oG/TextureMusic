package wzp.com.texturemusic.core.videocache;

/**
 * Stores source's info.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class SourceInfo {

    public String url;
    public long length;
    public String mime;

    public SourceInfo(String url, long length, String mime) {
        this.url = url;
        this.length = length;
        this.mime = mime;
    }

    @Override
    public String toString() {
        return "SourceInfo{" +
                "url='" + url + '\'' +
                ", length=" + length +
                ", mime='" + mime + '\'' +
                '}';
    }
}

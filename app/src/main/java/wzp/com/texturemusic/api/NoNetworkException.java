package wzp.com.texturemusic.api;

/**
 * Created by Go_oG
 * Description:自定义网络错误类
 * on 2017/10/4.
 */


public class NoNetworkException extends RuntimeException {
    private String msg;
    private int errorCode;

    public NoNetworkException() {
        super();
    }

    public NoNetworkException(String message) {
        super(message);
        msg = message;
    }

    public NoNetworkException(int errorCode, String msgDes) {
        super();
        this.errorCode = errorCode;
        this.msg = msgDes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}

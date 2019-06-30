package wzp.com.texturemusic.bmobModule.util;

import org.junit.Test;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.Scheduler;
import rx.functions.Action1;
import wzp.com.texturemusic.MyApplication;
import wzp.com.texturemusic.bmobModule.bean.BmobUser;

import static org.junit.Assert.*;

/**
 * Created by Go_oG
 * Description:
 * on 2018/2/6.
 */
public class BmobUtilTest {
    @Test
    public void addUser() throws Exception {
        BmobUser user = new BmobUser();
        //user.increment("userId");
        user.setEmail("123");
        user.setUserName("admin");
        user.setUserPsw("psw");
        user.setQqNumber(14032738850L);
        BmobUtil.addUser(user);


    }

}
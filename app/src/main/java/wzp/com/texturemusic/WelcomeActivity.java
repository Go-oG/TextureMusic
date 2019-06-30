package wzp.com.texturemusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import wzp.com.texturemusic.core.config.AppFileConstant;
import wzp.com.texturemusic.indexmodule.IndexActivity;
import wzp.com.texturemusic.interf.OnDialogListener;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * 欢迎页面即首屏页面
 */

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions permissions = new RxPermissions(this);
            permissions.request(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (!aBoolean) {
                                showPermissionFail();
                            } else {
                                initFileDirect();
                                Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        } else {
            initFileDirect();
            Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initFileDirect() {
        File file = new File(AppFileConstant.IMAGE_CACHE_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }


        file = new File(AppFileConstant.MEDIA_CACHE_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }

        file = new File(AppFileConstant.DOWNLOAD_MUSIC_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(AppFileConstant.DOWNLOAD_LYRICS_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(AppFileConstant.DOWNLOAD_MV_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(AppFileConstant.ARTIST_IMAGE_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(AppFileConstant.ALBUM_IMAGE_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File(AppFileConstant.CACHE_DATA_INFO);
        if (!file.exists()){
            file.mkdirs();
        }
        file=new File(AppFileConstant.FILE_DRESS);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    @SuppressLint("CheckResult")
    private void showPermissionFail() {
        ToastUtil.showCustomDialog(this, "权限请求", "为了保证APP的运行，请赋予APP部分权限；若没有权限,则部分功能会失效。"
                , new OnDialogListener() {
                    @Override
                    public void onResult(final boolean success) {
                        if (success) {
                            RxPermissions permissions = new RxPermissions(WelcomeActivity.this);
                            permissions.request(Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean aBoolean) throws Exception {
                                            if (aBoolean) {
                                                initFileDirect();
                                            } else {
                                                ToastUtil.showNormalMsg("权限请求失败");
                                            }
                                            Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        } else {
                            ToastUtil.showNormalMsg("权限请求失败");
                            Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }

}

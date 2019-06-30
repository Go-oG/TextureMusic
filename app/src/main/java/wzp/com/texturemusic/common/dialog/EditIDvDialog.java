package wzp.com.texturemusic.common.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.bean.MusicBean;
import wzp.com.texturemusic.util.BaseUtil;
import wzp.com.texturemusic.util.MusicUtil;
import wzp.com.texturemusic.util.ToastUtil;

/**
 * Created by Wang on 2018/2/24.
 * 修改歌曲IDv3标签信息的dialog
 */

public class EditIDvDialog extends DialogFragment {
    @BindView(R.id.edit_music_name_tv)
    EditText mMusicNameEdit;
    @BindView(R.id.edit_artist_edit)
    EditText mArtistNameEdit;
    @BindView(R.id.edit_album_edit)
    EditText mAlbumEdit;
    Unbinder unbinder;
    private MusicBean musicBean;
    private View view;
    private Context context;

    public void setMusicBean(MusicBean musicBean) {
        this.musicBean = musicBean;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_edit_idv3_info, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        if (musicBean != null) {
            mMusicNameEdit.setText(musicBean.getMusicName() == null ? "" : musicBean.getMusicName());
            mAlbumEdit.setText(musicBean.getAlbumName() == null ? "" : musicBean.getAlbumName());
            mArtistNameEdit.setText(musicBean.getArtistName() == null ? "" : musicBean.getArtistName());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = BaseUtil.dp2px(350);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.edit_music_name_close_img, R.id.edit_artist_close_img, R.id.edit_album_close_img,
            R.id.edit_info_commit, R.id.edit_info_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_music_name_close_img:
                break;
            case R.id.edit_artist_close_img:
                break;
            case R.id.edit_album_close_img:
                break;
            case R.id.edit_info_commit:
                editINfo(mMusicNameEdit.getText().toString(), mArtistNameEdit.getText().toString(), mAlbumEdit.getText().toString(), null);
                break;
            case R.id.edit_info_cancle:
                dismiss();
                break;
        }
    }

    private void editINfo(String newMusicNAme, String newArtist, String newAlbum, String newImgPath) {
        if (TextUtils.isEmpty(newMusicNAme)) {
            newMusicNAme = "";
        }
        if (TextUtils.isEmpty(newArtist)) {
            newArtist = "";
        }
        if (TextUtils.isEmpty(newAlbum)) {
            newAlbum = "";
        }
        MusicBean bean = new MusicBean();
        bean.setMusicName(newMusicNAme);
        bean.setArtistName(newArtist);
        bean.setAlbumName(newAlbum);
        try {
            MusicUtil.editMusicInfo(context, bean, musicBean.getPlayPath());
            ToastUtil.showNormalMsg("编辑成功");
        } catch (Exception e) {
            ToastUtil.showNormalMsg("编辑失败");
        }
        dismiss();
    }


}

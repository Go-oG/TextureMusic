package wzp.com.texturemusic.common.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wzp.com.texturemusic.R;
import wzp.com.texturemusic.interf.OnDialogInputListener;
import wzp.com.texturemusic.util.BaseUtil;

/**
 * Created by Go_oG
 * Description:带有输入控件的Dialog
 * on 2018/1/23.
 */

public class InputDialog extends DialogFragment {
    @BindView(R.id.dialog_title_tv)
    TextView dialogTitleTv;
    @BindView(R.id.dialog_input_edit)
    AppCompatEditText dialogInputEdit;
    @BindView(R.id.dialog_sub_title)
    TextView dialogSubTitle;
    @BindView(R.id.dialog_tips_tv)
    TextView dialogTipsTv;
    Unbinder unbinder;
    private String title = "";
    private String subTitle = "";
    private String tips = "";
    private View view;
    private OnDialogInputListener inputListener;
    private String result = "";
    private String defaultValue = "";

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setInputListener(OnDialogInputListener inputListener) {
        this.inputListener = inputListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_input, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        dialogInputEdit.setText(defaultValue);
        dialogSubTitle.setText(subTitle);
        dialogTitleTv.setText(title);
        dialogTipsTv.setText(tips);
        dialogInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                result = s.toString();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int dialogWidth = BaseUtil.dp2px(350);
        int dialogHeight = BaseUtil.dp2px(200);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dialog_sure_button, R.id.dialog_cancle_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_sure_button:
                if (inputListener!=null){
                    inputListener.onResult(result,true);
                }
                dismiss();
                break;
            case R.id.dialog_cancle_button:
                dismiss();
                break;
        }
    }

}

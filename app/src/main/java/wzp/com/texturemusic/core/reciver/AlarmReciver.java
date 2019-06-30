package wzp.com.texturemusic.core.reciver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import wzp.com.texturemusic.bean.TimeBean;
import wzp.com.texturemusic.core.config.AppConstant;


/**
 * Created by wang on 2017/3/10.
 * 定时广播的接收器
 */

public class AlarmReciver extends BroadcastReceiver {
    public static final int START_ALARM = 1;
    public static final int CANCLE_ALARM = 0;
    public static final String ACTION_TIME = "com.wang.AlarmReciver";
    private AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (!intent.getAction().equals(ACTION_TIME)) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        TimeBean bean = intent.getExtras().getParcelable(AppConstant.UI_BUNDLE_KEY_TIME);
        if (bean == null) {
            return;
        }
        switch (bean.getRequestCode()) {
            case START_ALARM:
                startTiming(context, bean.getTimes());
                break;
            case CANCLE_ALARM:
                cancelTiming();
                break;
        }
    }

    private void startTiming(Context context, long timeing) {
        long nowtime = SystemClock.elapsedRealtime();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent();
        i.setAction(AppConstant.RECIVER_ACTION_PAUSE);
        pendingIntent = PendingIntent.getBroadcast(context, 2, i, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, nowtime + timeing, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, nowtime + timeing, pendingIntent);
        }
    }

    private void cancelTiming() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}

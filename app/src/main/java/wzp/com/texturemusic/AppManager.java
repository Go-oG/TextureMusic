package wzp.com.texturemusic;

import android.app.Activity;

import java.util.Stack;

import wzp.com.texturemusic.indexmodule.IndexActivity;

/**
 * Created by Go_oG
 * Description: 用于管理所有的Activity
 * on 2017/9/16.
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {

    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        boolean s = activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null || activityStack.empty()) {
            return;
        }
        for (int i = 0; i < activityStack.size(); i++) {
            Activity activity = activityStack.get(i);
            if (activity != null && activity.getClass().equals(cls) && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity,但不包含传入进来的
     */
    public void finishAllActivityExclude(Class<?> cls) {
        if (activityStack != null && activityStack.size() > 0) {
            for (int i = 0; i < activityStack.size(); i++) {
                Activity activity = activityStack.get(i);
                if (activity != null && (!activity.getClass().equals(cls)) && (!activity.isFinishing())) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null || activityStack.empty()) {
            return;
        }
        for (Activity activity : activityStack) {
            activity.finish();
        }
        activityStack.clear();

    }

    public void finishAllActivityNotIndex() {
        if (activityStack == null || activityStack.empty()) {
            return;
        }
        for (Activity activity : activityStack) {
            if (!(activity instanceof IndexActivity)) {
                activity.finish();
                activityStack.remove(activity);
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        } catch (Exception e) {
        }
    }

}

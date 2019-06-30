package wzp.com.texturemusic.dbmodule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import wzp.com.texturemusic.dbmodule.bean.DaoMaster;

/**
 * Created by Go_oG
 * Description:数据库帮助类
 * on 2018/2/7.
 */

public class DbHelper extends DaoMaster.OpenHelper{
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

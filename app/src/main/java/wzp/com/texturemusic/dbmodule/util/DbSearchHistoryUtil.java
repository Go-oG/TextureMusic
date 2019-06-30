package wzp.com.texturemusic.dbmodule.util;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import wzp.com.texturemusic.dbmodule.DbUtil;
import wzp.com.texturemusic.dbmodule.bean.DbSearchHistoryBean;
import wzp.com.texturemusic.dbmodule.bean.DbSearchHistoryBeanDao;
import wzp.com.texturemusic.util.StringUtil;

/**
 * Created by Wang on 2018/2/21.
 * 用于操作搜索历史数据库的工具类
 */

public class DbSearchHistoryUtil {


    public static void deleteHistory(String history) {
        DbSearchHistoryBeanDao dao = DbUtil.getSearchDao();
//        QueryBuilder queryBuilder = dao.queryBuilder();
//        queryBuilder.where(DbSearchHistoryBeanDao.Properties.SearchStr.eq(history));
//        List<DbSearchHistoryBean> list = queryBuilder.list();
//        if (list != null && !list.isEmpty()) {
//            int q = 0;
//            for (DbSearchHistoryBean bean : list) {
//                if (bean.getSearchStr().equals(history)) {
//                    q++;
//                    break;
//                }
//            }
//            if (q!=0){
//                QueryBuilder builder=dao.queryBuilder();
//                builder.where(DbSearchHistoryBeanDao.Properties.SearchStr.eq(history));
//                DeleteQuery deleteQuery=builder.buildDelete();
//                deleteQuery.executeDeleteWithoutDetachingEntities();
//            }
//        }

        QueryBuilder builder = dao.queryBuilder();
        builder.where(DbSearchHistoryBeanDao.Properties.SearchStr.eq(history));
        DeleteQuery deleteQuery = builder.buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public static void addHistoy(String str) {
        if (!StringUtil.isEmpty(str)){
            DbSearchHistoryBeanDao dao = DbUtil.getSearchDao();
            QueryBuilder builder = dao.queryBuilder();
            builder.where(DbSearchHistoryBeanDao.Properties.SearchStr.eq(str));
            List<DbSearchHistoryBean> list = builder.list();
            if (list == null || list.isEmpty()) {
                DbSearchHistoryBean bean = new DbSearchHistoryBean();
                bean.setSearchTime(System.currentTimeMillis());
                bean.setSearchStr(str);
                dao.insert(bean);
            } else {
                DbSearchHistoryBean bean = list.get(0);
                bean.setSearchTime(System.currentTimeMillis());
                dao.update(bean);
            }
        }
    }

    public static void deleteAllHistory() {
        DbUtil.getSearchDao().deleteAll();
    }

    public static List<String> getAllHistory() {
        List<String> list = new ArrayList<>();
        DbSearchHistoryBeanDao dao = DbUtil.getSearchDao();
        QueryBuilder builder = dao.queryBuilder();
        builder.orderDesc(DbSearchHistoryBeanDao.Properties.SearchTime);
        List<DbSearchHistoryBean> beanList = builder.list();
        for (DbSearchHistoryBean bean : beanList) {
            list.add(bean.getSearchStr());
        }
        return list;
    }
}

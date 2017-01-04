package com.sibozn.peo.application;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.sibozn.peo.gen.DaoMaster;
import com.sibozn.peo.gen.DaoSession;
import com.sibozn.peo.utils.MySQLiteOpenHelper;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.cache.DBCacheStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class MyApplication extends Application {

    public static List<Activity> activiys;
    public static MyApplication instance;
    private MySQLiteOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
        activiys = new ArrayList<>();

        // Application 中执行
        // DevOpenHelper 每次数据库升级会清空数据，一般用于开发
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
//        Database db = helper.getWritableDb();
//        DaoSession daoSession = new DaoMaster(db).newSession();
        //GreenDao的初始化
        //GreenDaoManager.getInstance();
        setDatabase();
        //Logger.setDebug(true); // 开启NoHttp调试模式。
        //Logger.setTag("NoHttpSample"); // 设置NoHttp打印Log的TAG。
        NoHttp.initialize(this, new NoHttp.Config()
                .setNetworkExecutor(new OkHttpNetworkExecutor())  // 使用OkHttp做网络层。
                .setConnectTimeout(30 * 1000) // 全局连接超时时间，单位毫秒。
                .setReadTimeout(30 * 1000) // 全局服务器响应超时时间，单位毫秒。
                //.new DiskCacheStore(this) // 配置缓存到SD卡。
                .setCacheStore(
                        new DBCacheStore(this) // 配置缓存到数据库。
                                .setEnable(true) // true启用缓存，fasle禁用缓存。
                )
        );
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        //通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new MySQLiteOpenHelper(this, "greenDao.db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}

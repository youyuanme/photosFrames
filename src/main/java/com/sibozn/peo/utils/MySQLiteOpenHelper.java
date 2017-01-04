package com.sibozn.peo.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.sibozn.peo.gen.BeansDao;
import com.sibozn.peo.gen.DaoMaster;
import com.sibozn.peo.gen.FeatureBeanDao;

/**
 * Created by Administrator on 2016/11/21.
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, FeatureBeanDao.class);
        MigrationHelper.migrate(db, BeansDao.class);
    }
}

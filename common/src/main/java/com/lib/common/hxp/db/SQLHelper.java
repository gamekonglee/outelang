package com.lib.common.hxp.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

    public SQLHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //版本1
        db.execSQL(CartSQL.CREATE_TABLE);//创建购物车表
        db.execSQL(CartSQL.CREATE_UNIQUE_INDEX);//创建购物车表唯一索引
        db.execSQL(CollectSQL.CREATE_TABLE);//创建收藏表
        db.execSQL(CollectSQL.CREATE_UNIQUE_INDEX);//创建收藏表唯一索引
        db.execSQL(LogisticSQL.CREATE_TABLE);//创建地址表
        db.execSQL(LogisticSQL.CREATE_UNIQUE_INDEX);//创建地址表唯一索引
    }

    /**
     * 当系统在构造SQLiteOpenHelper类的对象时，如果发现版本号不一样，就会自动调用onUpgrade函数，让你在这里对数据库进行升级。
     * 升级完成后，数据库会自动存储最新的版本号为当前数据库版本号。
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                //版本2

        }
    }
}

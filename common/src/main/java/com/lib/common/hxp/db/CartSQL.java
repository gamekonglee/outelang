package com.lib.common.hxp.db;

/**
 * Created by xpHuang on 2016/9/29.
 */
public class CartSQL {
    public static final String TABLE_NAME = "t_cart";
    public static final String ID = "id";//主键
    public static final String PID = "pid";//产品的ID
    public static final String NAME = "name";//产品的名称
    public static final String PRICE = "price";//产品的app售价
    public static final String URL = "url";//产品的URL

    //创建表
    public static final String CREATE_TABLE = "CREATE TABLE if not exists "
            + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY, "
            + PID + " INTEGER, "
            + NAME + " TEXT, "
            + PRICE + " REAL, "
            + URL + " TEXT);";

    private static final String UNIQUE_INDEX_NAME = TABLE_NAME + "_unique_index_pid";//唯一索引名称

    //创建PID字段的唯一索引
    public static final String CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX "
            + UNIQUE_INDEX_NAME + " ON "
            + TABLE_NAME + " ("
            + PID + ");";

}

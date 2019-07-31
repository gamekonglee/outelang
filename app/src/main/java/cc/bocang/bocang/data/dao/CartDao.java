package cc.bocang.bocang.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lib.common.hxp.db.CartSQL;
import com.lib.common.hxp.db.DatabaseManager;
import com.lib.common.hxp.db.SQLHelper;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.global.Constant;

/**
 * Created by xpHuang on 2016/9/29.
 */

public class CartDao {
    private final String TAG = CartDao.class.getSimpleName();

    public CartDao(Context context) {

        SQLHelper helper = new SQLHelper(context, Constant.DB_NAME, null, Constant.DB_VERSION);
        DatabaseManager.initializeInstance(helper);
    }

    /**
     * 添加(或更新)一条记录
     *
     * @param
     * @return -1:添加（更新）失败；否则添加成功
     */
    public long replaceOne(Goods bean) {
        long result = -1;
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

            ContentValues values = new ContentValues();
            values.put(CartSQL.PID, bean.getId());
            values.put(CartSQL.NAME, bean.getName());
            values.put(CartSQL.PRICE, bean.getShop_price());
            values.put(CartSQL.URL, bean.getImg_url());

            if (db.isOpen()) {
                result = db.replace(CartSQL.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.getInstance().closeDatabase();
        }
        return result;
    }

    /**
     * 获取所有记录
     *
     * @return
     */
    public List<Goods> getAll() {
        Cursor cursor = null;
        List<Goods> beans = new ArrayList<>();
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
            if (db.isOpen()) {
                cursor = db.query(CartSQL.TABLE_NAME, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    int pId = cursor.getInt(cursor.getColumnIndex(CartSQL.PID));
                    float price = cursor.getFloat(cursor.getColumnIndex(CartSQL.PRICE));
                    String name = cursor.getString(cursor.getColumnIndex(CartSQL.NAME));
                    String url = cursor.getString(cursor.getColumnIndex(CartSQL.URL));
                    Goods bean = new Goods();
                    bean.setId(pId);
                    bean.setName(name);
                    bean.setShop_price(price);
                    bean.setImg_url(url);

                    beans.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
            DatabaseManager.getInstance().closeDatabase();
        }
        return beans;
    }

    /**
     * 删除一个记录
     *
     * @param pId 产品id
     * @return 删除成功记录数
     */
    public int deleteOne(int pId) {
        int result = 0;
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
            if (db.isOpen()) {
                result = db.delete(CartSQL.TABLE_NAME, CartSQL.PID + "=?", new String[]{String.valueOf(pId)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.getInstance().closeDatabase();
        }
        return result;
    }

    /**
     * 删除所有记录
     *
     * @return 删除成功记录数
     */
    public int deleteAll() {
        int result = 0;
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
            if (db.isOpen()) {
                result = db.delete(CartSQL.TABLE_NAME, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.getInstance().closeDatabase();
        }
        return result;
    }
}

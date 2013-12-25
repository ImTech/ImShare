/**
 * douzifly @2013-12-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 数据库操作封装
 * @author douzifly
 *
 */
public abstract class DbTableBase <T> extends SQLiteOpenHelper implements IDbTable{
    
    public DbTableBase(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    SQLiteDatabase mDb;
    
    /**
     * 打开数据库
     */
    synchronized void openDb() {
        if (mDb == null) {
            mDb = getWritableDatabase();
        }
    }
    
    public synchronized void close() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }
    
    /**
     * 保存
     */
    public boolean save(T t) {
        openDb();
        return false;
    }
    
    /**
     * 获取
     */
    public List<T> get() {
        return null;
    }
    
    /**
     * 删除
     */
    public boolean delete(int id) {
        
        return false;
    }
    
    /**
     * 更新
     */
    public boolean update(T t) {
        return false;
    } 
    
    /**
     * 是否存在
     */
    public boolean isExists(int id) {
        return false;
    }
    
    /**
     * 开启批量模式
     */
    public void beginTransaction() {
        openDb();
        if (mDb.inTransaction()) {
            return;
        }
        mDb.beginTransaction();
    }
    
    /**
     * 关闭批量模式
     */
    public void endTransaction() {
       if (mDb != null && mDb.inTransaction()) {
           mDb.endTransaction();
       }
      
    }

}

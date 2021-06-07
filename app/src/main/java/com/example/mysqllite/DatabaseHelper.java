package com.example.mysqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="Mhs.db";
    private static final String TABLE_NAME="Mahasiswa";
    private static final String COLUMN_NIM="Nim";
    private static final String COLUMN_NAMA="Nama";
    private static final String COLUMN_UMUR="Umur";
    private static final String COLUMN_PATH="PathFoto";

    SQLiteDatabase db;

    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("
            +COLUMN_NIM+" TEXT PRIMARY KEY, "
            +COLUMN_NAMA+" TEXT, "
            +COLUMN_UMUR+" INTEGER, "
            +COLUMN_PATH+" TEXT "
            +")";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db=db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = " DROP TABLE IF EXISTS "+TABLE_NAME;
        this.db.execSQL(query);
    }

    public int getCountData(){
        int result = 0;
        this.db = this.getReadableDatabase();
        Cursor cursor = this.db.query(TABLE_NAME, new String[]{COLUMN_NIM}, null,
                null, null, null, COLUMN_NIM+" ASC");
        result = cursor.getCount();
        this.db.close();
        return result;

    }

    public Mahasiswa getDataExist(String key){
        Mahasiswa mhs=null;
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE UPPER ("+COLUMN_NIM+") = '"+key.toUpperCase()+"' ";
        this.db=this.getReadableDatabase();
        Cursor cursor=this.db.rawQuery(query, null);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            mhs=new Mahasiswa(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NIM)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PATH)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_UMUR))
            );
        }
        this.db.close();
        return mhs;
    }

    public ArrayList<Mahasiswa> TransferDataArrayList(){
        this.db = getReadableDatabase();
        ArrayList<Mahasiswa> mymhs=null;
        Cursor cursor = this.db.query(TABLE_NAME, new String[]{COLUMN_NIM, COLUMN_NAMA, COLUMN_PATH, COLUMN_UMUR},
                null, null, null , null,COLUMN_NIM+" ASC");

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            mymhs = new ArrayList<Mahasiswa>();
            do{
                mymhs.add(new Mahasiswa(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NIM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAMA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PATH)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_UMUR))
                ));
            }while(cursor.moveToNext());
        }
        this.db.close();
        return mymhs;
    }

    public int deleteData(String key){
        int jmldelete=1;
        try{
            this.db = this.getWritableDatabase();
            String where = "nim=?";
            jmldelete=this.db.delete(TABLE_NAME,where, new String[]{key} );
            this.db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return jmldelete;
    }

    public boolean updateData(String Nim, String newNama, String newPath, int newUmur){
        boolean benar=false;
        try{
            ContentValues updateValues=new ContentValues();
            updateValues.put(COLUMN_NAMA, newNama);
            updateValues.put(COLUMN_PATH, newPath);
            updateValues.put(COLUMN_UMUR, newUmur);

            String where = "nim=?";
            this.db = this.getWritableDatabase();
            this.db.update(TABLE_NAME, updateValues,where, new String[]{Nim});
            this.db.close();
            benar=true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return benar;
    }

    public boolean insertData(Mahasiswa mhs){
        boolean benar = false;
        try{
            ContentValues newValues = new ContentValues();
            newValues.put(COLUMN_NIM, mhs.getNim());
            newValues.put(COLUMN_NAMA, mhs.getNama());
            newValues.put(COLUMN_PATH, mhs.getPath());
            newValues.put(COLUMN_UMUR, mhs.getUmur());

            this.db = this.getWritableDatabase();
            long result = db.insert(TABLE_NAME, null, newValues);
            benar = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return benar;
    }


}

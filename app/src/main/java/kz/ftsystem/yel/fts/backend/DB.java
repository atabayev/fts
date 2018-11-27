package kz.ftsystem.yel.fts.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String table = MyConstants.MY_DB_TABLE_NAME;
    private final Context context;


    public DB(Context _context) {
        context = _context;
    }

    public void open(){
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) dbHelper.close();
    }


    public void setVariable(String name, String value) {
        ContentValues cv = new ContentValues();
        Cursor c = db.query(table, new String[]{"name"}, "name = ?", new String[]{name}, null, null, null);
        cv.put("name", name);
        cv.put("value", value);
        if (!c.moveToFirst()) {
            db.insert(table, null, cv);
        } else {
            db.update(table, cv, "name = ?", new String[]{name});
        }
        c.close();
    }


    public String getVariable(String name) {
        Cursor c = db.query(table, new String[]{"name", "value"}, "name = ?", new String[]{name}, null, null, null);
        String result;
        if (c.moveToFirst()) {
            result = c.getString(1);
        } else {
            result = "0";
        }
        c.close();
        return result;
    }


    class DBHelper extends SQLiteOpenHelper {

        private DBHelper(Context context) {
            // конструктор суперкласса
            super(context, MyConstants.MY_DB, null, MyConstants.MY_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE = "create table " + table + " ("
                    + "name text,"
                    + "value text);";
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO: Реализовать Upgrade базы.
            db.execSQL("DROP TABLE IF EXISTS " + table);
            onCreate(db);
        }
    }

}

package com.example.oneconfirm2;


import static com.example.oneconfirm2.DBContract.DBEntry.COLUMN_NAME_CONTENTS;
import static com.example.oneconfirm2.DBContract.DBEntry.COLUMN_NAME_TITLE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oneconfirm2.DBContract.DBEntry;


// データベースをアプリから使用するために、 SQLiteOpenHelperを継承する
// SQLiteOpenHelperは、データベースやテーブルが存在する場合はそれを開き、存在しない場合は作成してくれる
public class ForgetDatabaseHelper extends SQLiteOpenHelper {

    // データベースのバージョン
    // テーブルの内容などを変更したら、この数字を変更する
    static final private int VERSION = 2;

    // データベース名
    static final private String DBNAME = "forget.db";

    // コンストラクタは必ず必要
    public ForgetDatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    // データベース作成時にテーブルを作成
    public void onCreate(SQLiteDatabase db) {

        // テーブルを作成
        db.execSQL(
                "CREATE TABLE "+ DBEntry.TABLE_NAME + " (" +
                        DBEntry._ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NAME_TITLE + " TEXT ," +
                        COLUMN_NAME_CONTENTS + " TEXT); "
        );

        db.execSQL(
                "INSERT INTO "+ DBEntry.TABLE_NAME + " (" +
                        COLUMN_NAME_TITLE + "," + COLUMN_NAME_CONTENTS
                + ")" + " VALUES" + "(" +"'鍵を締めましたか？', '一昨日も忘れていた。');"
        );

        db.execSQL(
                "INSERT INTO "+ DBEntry.TABLE_NAME + " (" +
                        COLUMN_NAME_TITLE + "," + COLUMN_NAME_CONTENTS
                        + ")" + " VALUES" + "(" +"'部屋の電気を消しましたか？', '省エネを心がけたい。');"
        );

        // トリガーを作成
        db.execSQL(
                "CREATE TRIGGER trigger_samp_tbl_update AFTER UPDATE ON " + DBEntry.TABLE_NAME +
                        " BEGIN "+
                        " UPDATE " + DBEntry.TABLE_NAME + " SET up_date = DATETIME('now', 'localtime') WHERE rowid == NEW.rowid; "+
                        " END;");
    }

    // データベースをバージョンアップした時、テーブルを削除してから再作成
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + DBEntry.TABLE_NAME);
        onCreate(db);
    }
}

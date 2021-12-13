package com.example.oneconfirm2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.oneconfirm2.DBContract.DBEntry;


public class ChecklistActivity extends AppCompatActivity {

    private ForgetDatabaseHelper helper = null;
    MainListAdapter sc_adapter;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        Button m_btn_return = findViewById(R.id.m_btn_return);
        Button m_btn_new = findViewById(R.id.m_btn_new);

        m_btn_return.setOnClickListener((View v) -> startActivity(new Intent(this, MainActivity.class)));

        m_btn_new.setOnClickListener((View v) -> startActivity(new Intent(this, TextActivity.class)));


        // データベースヘルパーを準備
        helper = new ForgetDatabaseHelper(this);

        // データベースを検索する項目を定義
        String[] cols = {DBEntry._ID, DBEntry.COLUMN_NAME_TITLE, DBEntry.COLUMN_NAME_CONTENTS };

        // 読み込みモードでデータベースをオープン
        SQLiteDatabase db = helper.getReadableDatabase();

        // データベースを検索
        Cursor cursor = db.query(DBEntry.TABLE_NAME, cols, null,
                null, null, null, null, null);



        Log.v("DB_TEST", "Count: " + cursor.getCount());
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBEntry._ID));
            name = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.COLUMN_NAME_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(DBEntry.COLUMN_NAME_CONTENTS));
            Log.v("DB_TEST", "id: " + id + " name: " + name + " content: " + content);
        }


        // 検索結果から取得する項目を定義
        String[] from = {DBEntry.COLUMN_NAME_TITLE};

        // データを設定するレイアウトのフィールドを定義
        int[] to = {R.id.title};

        // ListViewの1行分のレイアウト(row_main.xml)と検索結果を関連付け
        sc_adapter = new MainListAdapter(
                this, R.layout.row_checklist, cursor, from, to,0);

        // activity_main.xmlに定義したListViewオブジェクトを取得
        ListView list = findViewById(R.id.c_listview);

        // ListViewにアダプターを設定
        list.setAdapter(sc_adapter);

        // リストの項目をクリックしたときの処理
        list.setOnItemClickListener((av, view, position, id) -> {

            //　クリックされた行のデータを取得
            Cursor cursor1 = (Cursor)av.getItemAtPosition(position);

            // テキスト登録画面 Activity へのインテントを作成
            Intent intent  = new Intent(ChecklistActivity.this, TextActivity.class);

            intent.putExtra(DBEntry._ID, cursor1.getInt(0));
            intent.putExtra(DBEntry.COLUMN_NAME_TITLE, cursor1.getString(1));
            intent.putExtra(DBEntry.COLUMN_NAME_CONTENTS, cursor1.getString(2));

            // アクティビティを起動
            startActivity(intent);
        });

    }

    // アクティビティの再開処理
    @Override
    protected void onResume() {
        super.onResume();

        // データを一覧表示
        onShow();
    }

    // データを一覧表示
    protected void onShow() {

        // データベースヘルパーを準備
        helper = new ForgetDatabaseHelper(this);

        // データベースを検索する項目を定義
        String[] cols = {DBEntry._ID, DBEntry.COLUMN_NAME_TITLE, DBEntry.COLUMN_NAME_CONTENTS };

        // 読み込みモードでデータベースをオープン
        SQLiteDatabase db = helper.getReadableDatabase();

        // データベースを検索
        Cursor cursor = db.query(DBEntry.TABLE_NAME, cols, null,
                null, null, null, null, null);

        // 検索結果から取得する項目を定義
        String[] from = {DBEntry.COLUMN_NAME_TITLE};

        // データを設定するレイアウトのフィールドを定義
        int[] to = {R.id.title};

        // ListViewの1行分のレイアウト(row_main.xml)と検索結果を関連付け
        sc_adapter = new MainListAdapter(
                this, R.layout.row_checklist, cursor, from, to,0);

        // activity_main.xmlに定義したListViewオブジェクトを取得
        ListView list = findViewById(R.id.c_listview);

        // ListViewにアダプターを設定
        list.setAdapter(sc_adapter);

        // リストの項目をクリックしたときの処理
        list.setOnItemClickListener((av, view, position, id) -> {

            //　クリックされた行のデータを取得
            Cursor cursor1 = (Cursor)av.getItemAtPosition(position);

            // テキスト登録画面 Activity へのインテントを作成
            Intent intent  = new Intent(ChecklistActivity.this, TextActivity.class);

            intent.putExtra(DBEntry._ID, cursor1.getInt(0));
            intent.putExtra(DBEntry.COLUMN_NAME_TITLE, cursor1.getString(1));
            intent.putExtra(DBEntry.COLUMN_NAME_CONTENTS, cursor1.getString(2));

            // アクティビティを起動
            startActivity(intent);
        });
    }

    // 削除ボタン　タップ時に呼び出されるメソッド
    public void btnDel_onClick(View view){

        // MainListAdapterで設定されたリスト内の位置を取得
        int pos = (Integer)view.getTag();

        // アダプターから、_idの値を取得
        int id = ((Cursor) sc_adapter.getItem(pos)).getInt(0);

        // データを削除
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete(DBEntry.TABLE_NAME, DBEntry._ID+" = ?", new String[] {String.valueOf(id)});
        }

        // データを一覧表示
        onShow();
    }
}

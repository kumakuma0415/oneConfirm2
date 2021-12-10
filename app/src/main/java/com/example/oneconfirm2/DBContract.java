package com.example.oneconfirm2;


import android.provider.BaseColumns;

// データベースのテーブル名・項目名を定義
public final class DBContract extends android.app.Activity {

    // 誤ってインスタンス化しないようにコンストラクタをプライベート宣言
    public DBContract() {}

    // テーブルの内容を定義
    public static class DBEntry implements BaseColumns {
        // BaseColumns インターフェースを実装することで、内部クラスは_IDを継承できる
        public static final String TABLE_NAME           = "forget_tbl";
        public static final String COLUMN_NAME_TITLE    = "title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
    }
}
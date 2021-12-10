package com.example.oneconfirm2;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;

public class MainListAdapter extends SimpleCursorAdapter {

    // コンストラクタ
    public MainListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }


    // 指定データのビューを取得
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        // 削除ボタン オブジェクトを取得
        ImageButton btnDel = (ImageButton) view.findViewById(R.id.button_delete);

        // ボタンにリスト内の位置を設定
        btnDel.setTag(position);

        return view;
    }

}
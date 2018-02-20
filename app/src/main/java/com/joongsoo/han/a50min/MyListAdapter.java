package com.joongsoo.han.a50min;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by joongsoo on 2016-12-05.
 */
public class MyListAdapter extends BaseAdapter {

    Boolean D = false;
    String TAG = "50_ACTIVITY_MyListAdapter";
    private final int VERSION = 50;
    private int CUR_TOTAL = 0;

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<mITEM> item = new ArrayList<mITEM>();

    private final String[] HINT = {"5분 국제뉴스 메인", "5분 전공뉴스", "5분 웃는연습", "5분 스트레칭", "5분 방정리",
            "5분 복식호흡", "5분 운동","5분 악기","5분 자기전 청결유지", "5분 감사한 일", "5분 잘못한 점, 잘한 점",
            "5분 통화", "5분 새로운 노래", "5분 거울보기", "5분 일기장", "5분 디버깅", "5분 디버깅", "5분 디버깅",
            "5분 디버깅", "5분 디버깅", "5분 디버깅", "5분 디버깅","5분 디버깅", "5분 디버깅", "5분 디버깅", "5분 디버깅"};

    public MyListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void updateItemDone(int position, int sec) {
        item.get(position).DONE = sec;
        notifyDataSetChanged();
    }

    public void updateItemTitle(int position, String title) {
        item.get(position).TITLE = title;
        MainActivity.getInstance().updateTitleOnSheet(position, title);
        notifyDataSetChanged();
    }

    public void addItem(int mode, String title, int done, boolean is_type_disable) {
        Log.d(TAG, "addItem(mode"+","+title+", "+done+", "+is_type_disable+")");
        item.add(new mITEM(mode, title, done, is_type_disable));
        notifyDataSetChanged();
    }

    public void removeAllItem() {
        item.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public mITEM getItem(int position) {
        if(D)Log.d(TAG, "getItem("+position+")");
        return item.get(position);
    }

    public void setNewProfile(int mode) {
        CUR_TOTAL = 60 * (VERSION / mode);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        final ViewHolder viewholder;

        View v = convertView;

        String title = item.get(position).TITLE;
        int done = item.get(position).DONE;


        if (v == null) {
            v = mInflater.inflate(R.layout.item_menu, parent, false);

            // SAVE HOLDER
            viewholder = new ViewHolder();
            viewholder.iv = (ImageView) v.findViewById(R.id.iv_item_menu);
            viewholder.et = (EditText) v.findViewById(R.id.et_item_menu);
            viewholder.iv2 = (ImageView)v.findViewById(R.id.iv_item_timer);

            v.setTag(viewholder);
        }
        else {
            viewholder = (ViewHolder) v.getTag();
        }

        // LOAD INITIAL VIEW INFO
        // ALREADY DONE
        if(done <= 0)
            viewholder.iv2.setImageResource(R.drawable.item_menu_checked);
        // DONE OVER 7/8
        else if (done * 8 < CUR_TOTAL)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_7octa);
            // DONE OVER 3/4
        else if (done * 4 < CUR_TOTAL)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_3quarter);
            // DONE OVER 5/8
        else if (done * 8 < CUR_TOTAL * 3)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_5octa);
        // DONE OVER 1/2
        else if (done * 2 < CUR_TOTAL)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_2quarter);
            // DONE OVER 3/8
        else if (done * 8 < CUR_TOTAL * 5)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_3octa);
            // DONE OVER 1/4
        else if (done * 4 < CUR_TOTAL * 3)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_1quarter);
            // DONE OVER 1/8
        else if (done * 8 < CUR_TOTAL * 7)
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer_1octa);
            // LESS THAN 1/8 ALMOST 0
        else
            viewholder.iv2.setImageResource(R.drawable.item_menu_timer);

        // DISABLE TYPE
        if(item.get(position).IS_TYPE_DISABLED) {
            viewholder.iv.setImageResource(R.drawable.item_menu_button_on);
            viewholder.et.setKeyListener(null);
            viewholder.et.setEnabled(false);
            MainActivity.getInstance().hideSoftKeyboard(viewholder.et);
        }
        // ENABLE TYPE
        else {
            viewholder.iv.setImageResource(R.drawable.item_menu_button_off);
            viewholder.et.setKeyListener(new EditText(context).getKeyListener());
            viewholder.et.setEnabled(true);
        }

        viewholder.et.setHint(HINT[position]);
         viewholder.et.setText(title);

        viewholder.iv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (item.get(position).IS_TYPE_DISABLED) {
                        if(D)Log.d(TAG, "TYPE MAKE ENABLE");
                        viewholder.iv.setImageResource(R.drawable.item_menu_button_off);
                        viewholder.et.setKeyListener(new EditText(context).getKeyListener());
                        viewholder.et.setEnabled(true);
                    }
                    else {
                        // save edittext
                        if(D)Log.d(TAG, "TYPE MAKE DISABLE");
                        viewholder.iv.setImageResource(R.drawable.item_menu_button_on);
                        viewholder.et.setKeyListener(null);
                        viewholder.et.setEnabled(false);
                        MainActivity.getInstance().hideSoftKeyboard(viewholder.et);

                        // if user wrote edittext we update this and will save
                        // we need only position and name
                        // other case will just get hint
                        if(viewholder.et.getText().length() != 0)
                            updateItemTitle(position, viewholder.et.getText().toString());

                    }
                    item.get(position).IS_TYPE_DISABLED = !item.get(position).IS_TYPE_DISABLED;
                    notifyDataSetChanged();
                }
            });

        // timer handler
        viewholder.iv2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(item.get(position).DONE <= 0 || !item.get(position).IS_TYPE_DISABLED || (viewholder.et.getText().length() == 0)) {
                        return;
                    }
                    if(D)Log.d(TAG, "onClick: position: "+position);
                    MainActivity.getInstance().setTimer(position);
                }
            });
        return v;
    }
}
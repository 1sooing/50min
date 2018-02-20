package com.joongsoo.han.a50min;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.joongsoo.han.a50min.Database.DatabaseOpener;

import java.util.ArrayList;

public class MySpinnerModify extends DialogFragment {

    private final String TAG = "50_MySpinnerModify";
    private final boolean D = false ;

    // may be 30-series have ID 1000 times 50-series
    private final String[] MODE50 = {"5MIN x 10", "10MIN x 5", "25MIN x 2", "50MIN x 1","CUSTOM" };
    private final String[] MODE30 = {"3MIN x 10", "5MIN x 6", "10MIN x 3", "15MIN x 2","30MIN x 1", "CUSTOM"};

    private final int VERSION = 50;

    private static ArrayList<String> SHEETS_NAME = new ArrayList<String>();
    private static ArrayList<Integer> SHEETS_ID = new ArrayList<Integer>();
    //private static ArrayList<Integer> SHEETS_MODE = new ArrayList<Integer>();
    //private static ArrayList<String> SHEETS_TODAY = new ArrayList<String>();

    private static int CUR_ID;
    private static int CUR_MODE;
    private static String CUR_NAME;
    private static String CUR_TODAY;
    private static String CUR_EXTRA;
    private static int selected = 0;

    public static DatabaseOpener dbOpenner;
    public static SQLiteDatabase dbSQLite;

    /*
    MODE50 SELECT
     */
    private final int MODE_5MIN_10ITEM = 10;
    private final int MODE_10MIN_5ITEM = 5;
    private final int MODE_25MIN_2ITEM = 2;
    private final int MODE_50MIN_1ITEM = 1;
    private final int MODE_CUSTOM = 25;

    private MainActivity Main;

    public static MySpinnerModify newInstance(int title) {
        MySpinnerModify frag = new MySpinnerModify();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        if(D)Log.d(TAG, "onCreateModifyDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        dbOpenner = new DatabaseOpener(this.getActivity());
        dbSQLite = dbOpenner.getWritableDatabase();

        String strQuery = "SELECT * " + "FROM " + DatabaseOpener.TABLENAME + " ORDER BY " + "_id";
        Cursor cur = dbSQLite.rawQuery(strQuery, null);

        SHEETS_NAME.clear();
        SHEETS_ID.clear();

        while (cur.moveToNext())
        {
            CUR_ID = cur.getInt(0);
            CUR_NAME = cur.getString(3);
            //CUR_EXTRA = cur.getString(4);

            SHEETS_ID.add(CUR_ID);
            SHEETS_NAME.add(CUR_NAME);
            // EXTRA = cur.getString(CUR_EXTRA);
            if(D)Log.d(TAG, "loadOneSheet: (" + CUR_ID + "," + CUR_NAME+")");
        }
        cur.close();
        dbSQLite.close();
        dbOpenner.close();

        String[] arr = SHEETS_NAME.toArray(new String[SHEETS_NAME.size()]);

        builder.setTitle("SELECT SHEET");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(arr, selected, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selected = 0;
                selected = which;
            }
        });

        builder.setPositiveButton("LOAD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MainActivity.getInstance().loadOneSheet(SHEETS_ID.get(selected));
                        } catch (IndexOutOfBoundsException e) {
                            return;
                        }

                    }
                });

        builder.setNeutralButton("DELETE", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Wait!");
                alert.setMessage("sure to delete sheet?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(D)Log.d(TAG, "request deleteOneSheet ID: SHEETS_ID.get("+selected+")");

                        try {
                            MainActivity.getInstance().deleteOneSheet(SHEETS_ID.get(selected));
                        } catch (IndexOutOfBoundsException e) {
                            if(D)Log.d(TAG, "error detection deleteOneSheet ID: SHEETS_ID.get("+selected+")");
                            return;
                        }

                        SHEETS_ID.remove(selected);
                        SHEETS_NAME.remove(selected);
                        if(SHEETS_ID.size() == 0)
                            MainActivity.getInstance().deleteOneSheet(-92);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                if(SHEETS_ID.size() != 0)
                    alert.show();
                else {
                    MainActivity.getInstance().deleteOneSheet(-92);
                }
                return;
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
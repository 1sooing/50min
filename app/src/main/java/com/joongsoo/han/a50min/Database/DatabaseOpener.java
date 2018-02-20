package com.joongsoo.han.a50min.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joongsoo on 2016-12-05.
 */
public class DatabaseOpener extends SQLiteOpenHelper {

    private final String TAG = "50_DatabaseOpener";
    private final boolean D = false;

    private final static String DBNAME = "min_sheet.db";
    private final static int VERSION = 1;
    private final static SQLiteDatabase.CursorFactory NULLFACTORY = null;

    public final static String TABLENAME = "WORKSHEET";
    public final static String[] COLUMNS = {
            "_id", 	"_date", "mode", "name", "extra",
            "W0", "W1", "W2", "W3", "W4", "W5", "W6", "W7","W8","W9",
            "W10","W11","W12","W13","W14","W15","W16","W17","W18","W19",
            "W20","W21","W22","W23","W24","W25","W26","W27","W28","W29",
            "W30","W31","W32","W33","W34","W35","W36","W37","W38","W39",
            "W40","W41","W42","W43","W44","W45","W46","W47","W48","W49",
            "W50", "W51"};

    public final static int NUM_COLS = COLUMNS.length;

    public DatabaseOpener(Context context) {
        // args : Context context, String name, CursorFactory factory, int version
        super(context, DBNAME, NULLFACTORY, VERSION);
        if(D)Log.d(TAG, "Constructor(DatabaseOpener)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(D) Log.d(TAG, "onCreate()");
        // Create Table and PK
		/* CREATE TABLE [TABLENAME]
		 * 	(COLUMN0 INTEGER PRIMARY KEY AUTOINCREMENT,
		 *	 COLUMN1 TEXT
		 *	 COLUMN2 TEXT
		 *	 COLUMN3 TEXT) */

        /*
        ID / DATE / MODE / NAME / [EXTRA]
        25 [text-int] pairs
        */
        String strQuery = "CREATE TABLE " + TABLENAME + " (" +
                COLUMNS[0] + " INTEGER, " +	COLUMNS[1] + " TEXT, " +	COLUMNS[2]	+ " INTEGER, " + COLUMNS[3] + " TEXT, " +  COLUMNS[4] + " TEXT, " +
                COLUMNS[5] + " TEXT, " + COLUMNS[6]	+ " INTEGER, " +	COLUMNS[7] + " TEXT, " +  COLUMNS[8] + " INTEGER, " +
                COLUMNS[9] + " TEXT, " +	COLUMNS[10] + " INTEGER, " + COLUMNS[11] + " TEXT, " +	 COLUMNS[12] + " INTEGER, " +
                COLUMNS[13] + " TEXT, " +  COLUMNS[14] + " INTEGER, " + COLUMNS[15] + " TEXT, " + COLUMNS[16] + " INTEGER, " +
                COLUMNS[17] + " TEXT, " + COLUMNS[18] + " INTEGER, " + COLUMNS[19] + " TEXT, " + COLUMNS[20] + " INTEGER, " +
                COLUMNS[21] + " TEXT, " +	 COLUMNS[22] + " INTEGER, " + COLUMNS[23] + " TEXT, " + COLUMNS[24] + " INTEGER, " +
                COLUMNS[25] + " TEXT, " + COLUMNS[26] + " INTEGER, " + COLUMNS[27] + " TEXT, " + COLUMNS[28] + " INTEGER, " +
                COLUMNS[29] + " TEXT, " + COLUMNS[30] + " INTEGER, " + COLUMNS[31] + " TEXT, " + COLUMNS[32] + " INTEGER, " +
                COLUMNS[33] + " TEXT, " + COLUMNS[34] + " INTEGER, " + COLUMNS[35] + " TEXT, " + COLUMNS[36] + " INTEGER, " +
                COLUMNS[37] + " TEXT, " +	COLUMNS[38] + " INTEGER, " + COLUMNS[39] + " TEXT, " + COLUMNS[40] + " INTEGER, " +
                COLUMNS[41] + " TEXT, " + COLUMNS[42] + " INTEGER, " + COLUMNS[43] + " TEXT, " + COLUMNS[44] + " INTEGER, " +
                COLUMNS[45] + " TEXT, " + COLUMNS[46] + " INTEGER, " + COLUMNS[47] + " TEXT, " + COLUMNS[48] + " INTEGER, " +
                COLUMNS[49] + " TEXT, " +	COLUMNS[50] + " INTEGER, " + COLUMNS[51] + " TEXT, " + COLUMNS[52] + " INTEGER, " +
                COLUMNS[53] + " TEXT, " + COLUMNS[54] + " INTEGER, " + COLUMNS[55] + " TEXT, " + COLUMNS[56] + " INTEGER )";
        db.execSQL(strQuery);
        //onInsertItemTest(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version up
        onCreate(db);
        // return;
    }

    public void dropDatabase(Context context) {
        context.deleteDatabase(DBNAME);
    }

    private void onInsertItemTest(SQLiteDatabase db)
    {

    }
}

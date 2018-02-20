package com.joongsoo.han.a50min;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MySpinnerNEW extends DialogFragment {

    private final String TAG = "50_MySpinnerNew";
    private final boolean D = false;

    /*
    MODE50 SELECT
     */
    private final int VERSION = 50;

    private final int MODE_5MIN_10ITEM = 10;
    private final int MODE_10MIN_5ITEM = 5;
    private final int MODE_25MIN_2ITEM = 2;
    private final int MODE_50MIN_1ITEM = 1;
    private final int MODE_CUSTOM = 25;

    private MainActivity Main;

    public static MySpinnerNEW newInstance(int title) {
        MySpinnerNEW frag = new MySpinnerNEW();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        if(D)Log.d(TAG, "onCreateNewDialog");

        final Context ctx = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_newsheet, null, false);

        final EditText et_newsheet_title = (EditText) rootView.findViewById(R.id.et_new_sheet);

        final ImageButton ib_item0= (ImageButton) rootView.findViewById(R.id.ib_newsheet_item0);
        final ImageButton ib_item1= (ImageButton) rootView.findViewById(R.id.ib_newsheet_item1);
        final ImageButton ib_item2= (ImageButton) rootView.findViewById(R.id.ib_newsheet_item2);
        final ImageButton ib_item3= (ImageButton) rootView.findViewById(R.id.ib_newsheet_item3);
        final ImageButton ib_item4= (ImageButton) rootView.findViewById(R.id.ib_newsheet_item4);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setView(rootView);
        builder.setNegativeButton("Cancel", null);

        ib_item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "create new sheet!, 5 x 10");

                        String title = et_newsheet_title.getText().toString();

                        if (title.isEmpty())
                        {
                            Toast.makeText(getActivity(), "Please ENTER the title", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        /* Create sheet of that name */
                        makeNewSheet(title, 0);
                getDialog().dismiss();
            }
        });

        ib_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "create new sheet!, 10 x 5");

                String title = et_newsheet_title.getText().toString();

                if (title.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please ENTER the title", Toast.LENGTH_SHORT).show();
                    return;
                }
                        /* Create sheet of that name */
                makeNewSheet(title, 1);
                getDialog().dismiss();
            }
        });

        ib_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "create new sheet!, 25 x 2");

                String title = et_newsheet_title.getText().toString();

                if (title.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please ENTER the title", Toast.LENGTH_SHORT).show();
                    return;
                }
                        /* Create sheet of that name */
                makeNewSheet(title, 2);
                getDialog().dismiss();
            }
        });

        ib_item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "create new sheet!, 50 x 1");

                String title = et_newsheet_title.getText().toString();

                if (title.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please ENTER the title", Toast.LENGTH_SHORT).show();
                    return;
                }
                        /* Create sheet of that name */
                makeNewSheet(title, 3);
                getDialog().dismiss();
            }
        });

        ib_item4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "create new sheet!, 2 x 25");

                String title = et_newsheet_title.getText().toString();

                if (title.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please ENTER the title", Toast.LENGTH_SHORT).show();
                    return;
                }
                        /* Create sheet of that name */
                makeNewSheet(title, 4);
                getDialog().dismiss();
            }
        });

        return builder.create();
    }

    private void makeNewSheet(String name, int position) {

        Main = MainActivity.getInstance();

        // initialize
        String NAME = name;
        int MODE = -1;

        // MODE_50
        switch(position) {
            case 0:
                MODE = MODE_5MIN_10ITEM;
                break;
            case 1:
                MODE = MODE_10MIN_5ITEM;
                break;
            case 2:
                MODE = MODE_25MIN_2ITEM;
                break;
            case 3:
                MODE = MODE_50MIN_1ITEM;
                break;
            case 4:
                MODE = MODE_CUSTOM;
                break;
        }
        Main.setNewProfile(NAME, MODE);
        Toast.makeText(getActivity(), "New Sheet " + NAME, Toast.LENGTH_LONG).show();
    }
}
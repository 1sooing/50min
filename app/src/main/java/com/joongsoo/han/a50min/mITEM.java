package com.joongsoo.han.a50min;

/**
 * Created by joongsoo on 2016-12-08.
 */
public class mITEM {

    int MODE;
    String TITLE;
    int DONE;
    boolean IS_TYPE_DISABLED;

    public mITEM(int mode, String title, int done, boolean is_type_diabled) {
        this.MODE = mode;
        this.TITLE = title;
        this.DONE = done;
        this.IS_TYPE_DISABLED = is_type_diabled;
    }

    public mITEM(int mode, String title, int done) {
        this.MODE = mode;
        this.TITLE = title;
        this.DONE = done;
    }
}

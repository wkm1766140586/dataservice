package com.abc.newsserversec.common;

/**
 * Created by admin on 2017/5/23.
 */
public class LogonResult {
    boolean success;
    String msg;

    public LogonResult(boolean success,String msg){
        this.success = success;
        this.msg = msg;
    }
}

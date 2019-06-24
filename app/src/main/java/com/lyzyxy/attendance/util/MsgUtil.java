package com.lyzyxy.attendance.util;

import android.content.Context;
import android.widget.Toast;

public class MsgUtil {
    public static void msg(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void msg(Context context, int msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}

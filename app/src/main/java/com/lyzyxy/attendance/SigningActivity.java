package com.lyzyxy.attendance;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.lyzyxy.attendance.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SigningActivity extends BaseActivity {
    private Socket mSocket;

    {
        IO.Options opts = new IO.Options();
        opts.query = "client=" + CourseActivity.course.getId();
        try {
            mSocket = IO.socket(Constant.URL_SOCKET, opts);
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icon_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SigningActivity", "连接成功！");
            }
        });
        mSocket.on("info", onInfoMessage);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SigningActivity", "连接出错！");
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SigningActivity", "连接超时！");
            }
        });
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SigningActivity", "断开连接！");
            }

        });
        mSocket.connect();
    }

    private void cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SigningActivity.this)
                .setTitle("提示")
                .setMessage("是否放弃这次签到？")
                .setPositiveButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("不放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private Emitter.Listener onInfoMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            SigningActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String clientId;
                    int type;
                    String message;
                    try {
                        clientId = data.getString("clientId");
                        type = data.getInt("type");
                        message = data.getString("content");
                    } catch (JSONException e) {
                        return;
                    }

                    if(type == 0){
                        //TODO add the message to view
                        // addMessage(username, message);
                        Log.d("SigningActivity", "签到："+message);
                    }else if(type == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SigningActivity.this)
                                .setTitle("提示")
                                .setMessage(message)
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        builder.show();
                    }

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT);
        mSocket.off("info", onInfoMessage);
        mSocket.off(Socket.EVENT_CONNECT_ERROR);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT);
        mSocket.off(Socket.EVENT_DISCONNECT);
    }
}

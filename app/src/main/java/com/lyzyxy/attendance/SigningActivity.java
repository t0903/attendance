package com.lyzyxy.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lyzyxy.attendance.network.RetrofitRequest;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AcFunDanmakuParser;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.MsgUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.ui.widget.DanmakuView;

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

    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private AcFunDanmakuParser mParser;

    private TextView tv_num,tv_sum;
    private int num = 0;
    private int recordId;

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

        mDanmakuView = findViewById(R.id.dmk_show_danmu);

        recordId = getIntent().getIntExtra("recordId",-1);

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        findViewById(R.id.tv_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSign();
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

        tv_num = findViewById(R.id.tv_num);
        tv_sum = findViewById(R.id.tv_sum);

        loadData();

        init();
    }

    private void loadData(){
        if(recordId == -1)
            finish();

        String url = Constant.URL_BASE + "user/studentNum";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordId", recordId);

        RetrofitRequest.sendPostRequest(url, params, Integer.class, false,
                new RetrofitRequest.ResultHandler<Integer>(SigningActivity.this) {
                    @Override
                    public void onBeforeResult() {
                        // 这里可以放关闭loading
                    }

                    @Override
                    public void onResult(RequestResult<Integer> r) {
                        if(r.getCode() == Constant.SUCCESS){
                            int n = r.getData();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_sum.setText(" / "+ n);
                                }
                            });
                        }
                    }

                    @Override
                    public void onAfterFailure() {
                        // 这里可以放关闭loading
                    }
                });
    }

    private void init() {
        mContext = DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 8); // 滚动弹幕最大显示5行

        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 10) //描边的厚度
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //弹幕的速度。注意！此值越小，速度越快！值越大，速度越慢。// by phil
                .setScaleTextSize(1.2f)  //缩放的值
                //.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        mParser = new AcFunDanmakuParser();
        mDanmakuView.prepare(mParser, mContext);

        //mDanmakuView.showFPS(true);
        mDanmakuView.enableDanmakuDrawingCache(true);

        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.d("弹幕文本", "danmakuShown text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
        }
    }

    public void sendTextMessage(String msg) {
        num++;
        tv_num.setText(""+num);
        addDanmaku(msg);
    }

    private void addDanmaku(String msg) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = msg;
        danmaku.padding = 6;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 30f * (mParser.getDisplayer().getDensity() - 0.6f); //文本弹幕字体大小
        danmaku.textColor = getRandomColor(); //文本的颜色
        //danmaku.textShadowColor = getRandomColor(); //文本弹幕描边的颜色
        //danmaku.underlineColor = Color.DKGRAY; //文本弹幕下划线的颜色
        //danmaku.borderColor = getRandomColor(); //边框的颜色

        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 结束签到
     */
    private void endSign(){
        if(recordId == -1)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(SigningActivity.this)
                .setTitle("提示")
                .setMessage("是否结束这次签到？")
                .setPositiveButton("结束", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Constant.URL_BASE + "user/endSign";
                        Map<String,Object> params = new HashMap<String, Object>();
                        params.put("recordId",recordId);
                        RetrofitRequest.sendPostRequest(url, params, null, false,
                                new RetrofitRequest.ResultHandler(SigningActivity.this) {
                                    @Override
                                    public void onBeforeResult() {
                                        // 这里可以放关闭loading
                                    }

                                    @Override
                                    public void onResult(RequestResult r) {
                                        if(r.getCode() == Constant.SUCCESS){
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onAfterFailure() {
                                        // 这里可以放关闭loading
                                    }
                                });
                    }
                })
                .setNegativeButton("不结束", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    /**
     * 取消签到
     */
    private void cancel() {
        if(recordId == -1)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(SigningActivity.this)
                .setTitle("提示")
                .setMessage("是否放弃这次签到？")
                .setPositiveButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = Constant.URL_BASE + "user/cancelSign";
                        Map<String,Object> params = new HashMap<String, Object>();
                        params.put("recordId",recordId);
                        RetrofitRequest.sendPostRequest(url, params, null, false,
                                new RetrofitRequest.ResultHandler(SigningActivity.this) {
                                    @Override
                                    public void onBeforeResult() {
                                        // 这里可以放关闭loading
                                    }

                                    @Override
                                    public void onResult(RequestResult r) {
                                        if(r.getCode() == Constant.SUCCESS){
                                            finish();
                                        }else{
                                            MsgUtil.msg(SigningActivity.this,"取消失败！");
                                        }
                                    }

                                    @Override
                                    public void onAfterFailure() {
                                        // 这里可以放关闭loading
                                    }
                                });
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
                        sendTextMessage(message+"已签到");
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

    public static void startActivity(Context context,int n, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("recordId",n);
        context.startActivity(intent);
    }

    /**
     * 从一系列颜色中随机选择一种颜色
     *
     * @return
     */
    private int getRandomColor() {
        int[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.CYAN, Color.BLACK, Color.DKGRAY};
        int i = ((int) (Math.random() * 10)) % colors.length;
        return colors[i];
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT);
        mSocket.off("info", onInfoMessage);
        mSocket.off(Socket.EVENT_CONNECT_ERROR);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT);
        mSocket.off(Socket.EVENT_DISCONNECT);

        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
}

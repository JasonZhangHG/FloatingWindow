package cool.floatingwindow.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ChatActivity extends AppCompatActivity {

    private ImageView mScaleView;
    private ImageView mCloseView;
    private boolean mIsClose = false;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mScaleView = findViewById(R.id.iv_scale);
        mCloseView = findViewById(R.id.iv_close);
        mScaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Android 6.0 以下无需获取权限，可直接展示悬浮窗
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
                    if (Settings.canDrawOverlays(ChatActivity.this)) {
                        showFloatingView();
                        finish();
                    } else {
                        //跳转悬浮窗权限授权页面\
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }
                } else {
                    showFloatingView();
                    finish();
                }
            }
        });

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsClose = true;
                ChatFloatingService.stopService();
                finish();
            }
        });
    }

    //隐藏悬浮窗
    private void dismissFloatingView() {
        if (ChatFloatingService.isStart) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ChatFloatingService.ACTION_DISMISS_FLOATING));
        }
    }

    /**
     * 显示悬浮窗
     */
    private void showFloatingView() {
        if (ChatFloatingService.isStart) {
            //通知显示悬浮窗
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ChatFloatingService.ACTION_SHOW_FLOATING));
        } else {
            //启动悬浮窗管理服务
            startService(new Intent(this, ChatFloatingService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(ChatActivity.this)) {
                    showFloatingView();
                    finish();
                }
            }
        }
    }
}
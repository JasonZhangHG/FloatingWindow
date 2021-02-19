package cool.floatingwindow.android;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChatFloatingService extends Service {

    public static String ACTION_SHOW_FLOATING = "action_show_floating";
    public static String ACTION_DISMISS_FLOATING = "action_dismiss_floating";
    public static ChatFloatingService mChatFloatingService;
    public static boolean isStart = false;
    private FloatingView mFloatingView;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SHOW_FLOATING.equals(intent.getAction())) {
                if (mFloatingView != null) {
                    mFloatingView.show();
                }
            } else if (ACTION_DISMISS_FLOATING.equals(intent.getAction())) {
                if (mFloatingView != null) {
                    mFloatingView.dismiss();
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mChatFloatingService = this;
        isStart = true;
        mFloatingView = new FloatingView(this);

        IntentFilter intentFilter = new IntentFilter(ACTION_SHOW_FLOATING);
        intentFilter.addAction(ACTION_DISMISS_FLOATING);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatingView != null) {
            mFloatingView.show();
            mFloatingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatActivityIntent = new Intent(ChatFloatingService.this, ChatActivity.class);
                    chatActivityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chatActivityIntent);
                    mFloatingView.dismiss();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mFloatingView != null) {
            mFloatingView.dismiss();
            mFloatingView = null;
        }
        isStart = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public static void stopService() {
        if (mChatFloatingService != null) {
            mChatFloatingService.stopService();
        }
    }
}

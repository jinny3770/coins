package com.example.sora.coins;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sora.coins.Chat.SendBirdChannelListActivity;
import com.example.sora.coins.Chat.SendBirdMessagingChannelListActivity;
import com.example.sora.coins.Main.MainActivity;

import com.example.sora.coins.etc.MyInfo;
import com.google.android.gcm.GCMBaseIntentService;
import com.sendbird.android.SendBird;

/**
 * Created by Administrator on 2016-04-23.
 */

public class GCMIntentService extends GCMBaseIntentService
{
    private static final String tag = "GCMIntentService";
    private static final String projectID = "massive-dynamo-127407";

    MyInfo myInfo = MyInfo.getInstance();

    public static void generateNotification(Context context, String message)
    {
        int icon = R.drawable.ic_drawer;
        long time = System.currentTimeMillis();
        String title = "WAY";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(icon);
        builder.setTicker("메시지가 도착했습니다.");
        builder.setWhen(time);
        builder.setNumber(1);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public GCMIntentService()
    {
        this(projectID);
    }

    public GCMIntentService(String projectID)
    {
        super(projectID);
    }

    /* 푸시로 받은 메시지 */
    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.d(tag, " 메시지 수신.");
        generateNotification(context, myInfo.getName() + "님께서 도착지에 도착하지 못하였습니다.");
    }

    /* 에러 발생시 */
    @Override
    protected void onError(Context context, String errorId)
    {
        Log.d(tag, " 에러 발생. 에러 ID : " + errorId);
    }

    /* 단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다 */
    @Override
    protected void onRegistered(Context context, String regId)
    {
        Log.d(tag, "기기 등록. 등록 ID : " + regId);
    }

    /* 단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다 */
    @Override
    protected void onUnregistered(Context context, String regId)
    {
        Log.d(tag, "기기 해지. 해지 ID : " + regId);
    }
}
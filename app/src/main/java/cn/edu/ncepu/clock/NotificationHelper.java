package cn.edu.ncepu.clock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper
{
	private Context mContext;
	private static final String CHANNEL_ID = "your_channel_id"; // 请替换为你的channel id
	private static final String DEFAULT_SOUND_URI = "android.resource://cn.edu.ncepu.clock/" + R.raw.m1;
	
	public NotificationHelper(Context context)
	{
		mContext = context;
	}
	public void showNotification(String title, String message)
	{
		createNotificationChannel();
		
		Intent stopIntent = new Intent(mContext, AlarmReceiver.class);
		stopIntent.setAction("ACTION_STOP");
		PendingIntent stopPendingIntent = PendingIntent.getBroadcast(mContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
		
		Uri soundUri = Uri.parse(DEFAULT_SOUND_URI);
		AudioAttributes audioAttributes = new AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.setUsage(AudioAttributes.USAGE_ALARM)
				.build();
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notifications_black_24dp)
				.setContentTitle(title)
				.setContentText(message)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setSound(soundUri,audioAttributes.getContentType())
				.addAction(R.drawable.ic_home_black_24dp, "停止", stopPendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0 /* ID of notification */, builder.build());
	}
	
	private void createNotificationChannel() {
		// 创建通知渠道，针对Android 8.0及以上版本
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "Your Channel Name"; // 渠道名称
			String description = "Your Channel Description"; // 渠道描述
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			
			NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.createNotificationChannel(channel);
		}
	}
}
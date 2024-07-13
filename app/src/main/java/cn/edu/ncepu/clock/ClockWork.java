package cn.edu.ncepu.clock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import cn.edu.ncepu.clock.MainActivity;
import cn.edu.ncepu.clock.R;

public class ClockWork extends Worker
{
	private Handler mainHandler;
	private Context mContext;
	private static final String CHANNEL_ID = "alarm_channel";
	public ClockWork(@NonNull Context context, @NonNull WorkerParameters workerParams)
	{
		super(context, workerParams);
		mContext=context;
		mainHandler = new Handler(Looper.getMainLooper());
	}
	@NonNull
	@Override
	public Result doWork()
	{
		mainHandler.post(new Runnable()
		{
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), "接下来你要去算竟了", Toast.LENGTH_LONG).show();
			}
		});
		createNotificationChannel();
		showNotification();
		sounding();
		return Result.success();
	}
	private void sounding()
	{
		MediaPlayer mediaPlayer = AlarmReceiver.createMediaPlayer(mContext);
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared(MediaPlayer mp)
			{
				mp.start();
			}
		});
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				mp.release();
			}
		});
	}
	private void createNotificationChannel()
	{
		NotificationChannel serviceChannel = new NotificationChannel(
				CHANNEL_ID,
				"Alarm Channel",
				NotificationManager.IMPORTANCE_DEFAULT
		);
		NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
		manager.createNotificationChannel(serviceChannel);
	}
	
	private void showNotification()
	{
		Data input=getInputData();
		Intent stopIntent = new Intent(mContext, AlarmReceiver.class);
		stopIntent.setAction("ACTION_STOP");
		PendingIntent stopPendingIntent = PendingIntent.getBroadcast(mContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notifications_black_24dp)
				.setContentTitle(input.getString("theme"))
				.setContentText(String.format("共%.2f小时",input.getDouble("timeLength",-1)))
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.addAction(R.drawable.ic_home_black_24dp, "停止", stopPendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0 /* ID of notification */, builder.build());
	}
}
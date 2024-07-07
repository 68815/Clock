package cn.edu.ncepu.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
	private static MediaPlayer mediaPlayer;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if ("ACTION_STOP".equals(intent.getAction()))
		{
			if (null != mediaPlayer)
			{
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}
	public static MediaPlayer createMediaPlayer(Context context)
	{
		if(null == mediaPlayer)
		{
			mediaPlayer=MediaPlayer.create(context, R.raw.m1);
		}
		return mediaPlayer;
	}
}
package cn.edu.ncepu.clock.model;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class ClockDate
{
	private ArrayList<SingleClockDate> dates;
	private static ClockDate clockDate;
	private DataDBHelper clockDBHelper;
	private Dao<SingleClockDate,Integer> clockDao;
	private ClockDate(Context context)
	{
		dates=new ArrayList<SingleClockDate>();
		clockDBHelper=getClockDBHelper(context);
		clockDao=clockDBHelper.getDataDao();
	}
	public DataDBHelper getClockDBHelper(Context context)
	{
		if(null == clockDBHelper)
		{
			clockDBHelper= OpenHelperManager.getHelper(context.getApplicationContext(),DataDBHelper.class);
		}
		return clockDBHelper;
	}
	public static ClockDate getClockDate(Context context)
	{
		if(null == clockDate)
		{
			clockDate=new ClockDate(context);
		}
		return clockDate;
	}
	public ArrayList<SingleClockDate> getDates()
	{
		if(null == clockDao)
		{
			clockDao=clockDBHelper.getDataDao();
		}
		try
		{
			dates=(ArrayList<SingleClockDate>) clockDao.queryForAll();
			dates.sort(new Comparator<SingleClockDate>()
			{
				@Override
				public int compare(SingleClockDate s1,SingleClockDate s2)
				{
					long mirrorSeconds = Instant.now().toEpochMilli();
					if(s1.getDate().getTime() > mirrorSeconds && s2.getDate().getTime() < mirrorSeconds)
					{
						return -1;
					}
					else if(s1.getDate().getTime() < mirrorSeconds && s2.getDate().getTime() > mirrorSeconds)
					{
						return 1;
					}
					else if(s1.getDate().getTime() < mirrorSeconds && s2.getDate().getTime() < mirrorSeconds)
					{
						return -1;
					}
					else
					{
						return s1.getDate().compareTo(s2.getDate());
					}
				}
			});
		} catch (SQLException e)
		{
			Log.e("111","getCrimes");
		}
		return dates;
	}
	public int getNearbyDateClockCounts()
	{
		long currentMills = new Date().getTime();
		for(int i=0;!dates.isEmpty() && i<dates.size();i++)
		{
			if(dates.get(i).getDate().getTime() + 24 * 60 * 60 * 1000 < currentMills)
			{
				return i;
			}
		}
		return dates.size();
	}
	public void setDates(ArrayList<SingleClockDate> dates)
	{
		this.dates=dates;
	}
	public void addDate(SingleClockDate date)
	{
		dates.add(date);
		if(null == clockDao)
		{
			clockDao=clockDBHelper.getDataDao();
		}
		try
		{
			clockDao.create(date);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	public void deleteDate(int position)
	{
		if(null == clockDao)
		{
			clockDao=clockDBHelper.getDataDao();
		}
		try
		{
			clockDao.delete(dates.get(position));
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		dates.remove(position);
	}
}
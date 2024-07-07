package cn.edu.ncepu.clock.model;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class HistoryDate
{
	private ArrayList<SingleClockDate> dates;
	private static HistoryDate historyDate;
	private DataDBHelper historyDBHelper;
	private Dao<SingleClockDate,Integer> historyDao;
	private HistoryDate(Context context)
	{
		dates=new ArrayList<SingleClockDate>();
		historyDBHelper=getHistoryDBHelper(context);
		historyDao=historyDBHelper.getDataDao();
	}
	public DataDBHelper getHistoryDBHelper(Context context)
	{
		if(null == historyDBHelper)
		{
			historyDBHelper= OpenHelperManager.getHelper(context.getApplicationContext(),DataDBHelper.class);
		}
		return historyDBHelper;
	}
	public static HistoryDate getHistoryDate(Context context)
	{
		if(null == historyDate)
		{
			historyDate=new HistoryDate(context);
		}
		return historyDate;
	}
	public ArrayList<SingleClockDate> getDates()
	{
		if(null == historyDao)
		{
			historyDao=historyDBHelper.getDataDao();
		}
		try
		{
			dates=(ArrayList<SingleClockDate>) historyDao.queryForAll();
			dates.sort(new Comparator<SingleClockDate>()
			{
				@Override
				public int compare(SingleClockDate s1,SingleClockDate s2)
				{
					return s1.getDate().compareTo(s2.getDate());
				}
			});
		} catch (SQLException e)
		{
			Log.e("111","getCrimes");
		}
		return dates;
	}
	public void setDates(ArrayList<SingleClockDate> dates)
	{
		this.dates=dates;
	}
	public void addDate(SingleClockDate date)
	{
		dates.add(date);
		if(null == historyDao)
		{
			historyDao=historyDBHelper.getDataDao();
		}
		try
		{
			historyDao.create(date);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	public void deleteDate(int position)
	{
		if(null == historyDao)
		{
			historyDao=historyDBHelper.getDataDao();
		}
		try
		{
			historyDao.delete(dates.get(position));
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		dates.remove(position);
	}
}
package cn.edu.ncepu.clock.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataDBHelper extends OrmLiteSqliteOpenHelper
{
	private static final String TAG="ClockDBHelper";
	public static final String databaseName="clock.db";
	static int databaseVersion=3;
	private Dao<SingleClockDate,Integer> DataDao;
	public DataDBHelper(Context context)
	{
		super(context, databaseName,null,databaseVersion);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
	{
		try
		{
			TableUtils.createTable(connectionSource, SingleClockDate.class);
		} catch (SQLException e)
		{
			Log.e(TAG,e.toString());
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try
		{
			TableUtils.dropTable(connectionSource,SingleClockDate.class,true);
			onCreate(database,connectionSource);
		} catch (SQLException e)
		{
			Log.e(TAG,e.toString());
		}
	}
	public Dao<SingleClockDate,Integer> getDataDao()
	{
		if(null == DataDao)
		{
			try
			{
				DataDao=getDao(SingleClockDate.class);
			} catch (SQLException e)
			{
				Log.e(TAG,e.toString());
				return null;
			}
		}
		return DataDao;
	}
}

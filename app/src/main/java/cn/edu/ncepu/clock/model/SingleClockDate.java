package cn.edu.ncepu.clock.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@DatabaseTable
public class SingleClockDate
{
	@DatabaseField(generatedId = true, useGetSet = true)
	private UUID id;
	@DatabaseField
	private Date date;
	@DatabaseField
	private double length;
	@DatabaseField
	private String theme;
	public SingleClockDate()
	{
		id=UUID.randomUUID();
		date = new Date();
		length = 0.0;
		theme="本主题由你来设置";
	}
	public SingleClockDate(Date date,double length,String theme)
	{
		id=UUID.randomUUID();
		this.date=date;
		this.length=length;
		this.theme=theme;
	}
	public int getStartHour()
	{
		int minute=date.getMinutes()+30;
		return date.getHours()+minute/60;
	}
	public int getStartMinute()
	{
		return (date.getMinutes()+30)%60;
	}
	
	public int getEndMinute()
	{
		int endMinte=getStartMinute()+(int)((length-(int)length)*60);
		return endMinte%60;
	}
	public int getEndHour()
	{
		int minute=date.getMinutes()+30;
		int hour=date.getHours()+minute/60;
		minute%=60;
		int endMinte=minute+(int)((length-(int)length)*60);
		int endHour=hour+(int)length+endMinte/60;
		return endHour;
	}
	public long getStartTime()
	{
		return date.getTime()+1800000L;
	}
	public long getEndTime()
	{
		return getStartTime()+(long)((int)length*3600000L)+(long)((length-(int)length)*60000L);
	}
	public UUID getId()
	{
		return id;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	public double getLength()
	{
		return length;
	}
	
	public void setLength(double length)
	{
		this.length = length;
	}
	
	public String getTheme()
	{
		return theme;
	}
	
	public void setTheme(String theme)
	{
		this.theme = theme;
	}
}
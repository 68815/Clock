package cn.edu.ncepu.clock;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
	private String theme;
	public SingleClockDate()
	{
		id=UUID.randomUUID();
		date = new Date();
		theme="本主题由你来设置";
	}
	public SingleClockDate(Date date,String theme)
	{
		id=UUID.randomUUID();
		this.date=date;
		this.theme=theme;
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
	
	public String getTheme()
	{
		return theme;
	}
	
	public void setTheme(String theme)
	{
		this.theme = theme;
	}
}
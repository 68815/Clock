package cn.edu.ncepu.clock.ui.notifications;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.edu.ncepu.clock.ClockWork;
import cn.edu.ncepu.clock.model.ClockDate;
import cn.edu.ncepu.clock.R;
import cn.edu.ncepu.clock.model.SingleClockDate;
import cn.edu.ncepu.clock.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment
{
	private ArrayList<SingleClockDate> dates;
	private Date seclectDate;
	private TextView tvDate,tvTime,tvTimeLength;
	private EditText etTheme;
	private Button btnDate,btnTime;
	private FloatingActionButton faBtnAdd;
	private ClockDate clockDate;
	private FragmentNotificationsBinding binding;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		dates=ClockDate.getClockDate(getContext()).getDates();
	}
	
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		NotificationsViewModel notificationsViewModel =
				new ViewModelProvider(this).get(NotificationsViewModel.class);
		
		binding = FragmentNotificationsBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		etTheme=root.findViewById(R.id.et_set_theme);
		tvTimeLength=root.findViewById(R.id.etv_time_length);
		tvDate=root.findViewById(R.id.editTextDate);
		tvTime=root.findViewById(R.id.editTextTime);
		btnDate=root.findViewById(R.id.btn_set_date);
		btnTime=root.findViewById(R.id.btn_set_time);
		faBtnAdd=root.findViewById(R.id.fa_btn_add);
		clockDate= ClockDate.getClockDate(getContext());
		seclectDate=new Date();
		tvDate.setText(String.format("%d.%d.%d", seclectDate.getYear()+1900, seclectDate.getMonth()+1, seclectDate.getDate()));
		tvTime.setText(String.format("%d.%d", seclectDate.getHours(), seclectDate.getMinutes()));
		btnDate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Dialog dialog = new Dialog(requireContext());
				dialog.setContentView(R.layout.dialog_calendar);
				final CalendarView calendarView=dialog.findViewById(R.id.calendarView);
				calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
				{
					@Override
					public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
					{
						seclectDate.setYear(year-1900);seclectDate.setMonth(month);seclectDate.setDate(dayOfMonth);
						tvDate.setText(String.format("%d.%d.%d", year, month + 1, dayOfMonth));
					}
				});
				dialog.show();
			}
		});
		btnTime.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Dialog dialog = new Dialog(requireContext());
				dialog.setContentView(R.layout.dialog_timepicker);
				final TimePicker timePicker = dialog.findViewById(R.id.timePicker);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override
					public void onCancel(DialogInterface dialog)
					{
						seclectDate.setHours(timePicker.getHour());
						seclectDate.setMinutes(timePicker.getMinute());
						tvTime.setText(String.format("%d.%d", timePicker.getHour(), timePicker.getMinute()));
					}
				});
				dialog.show();
			}
		});
		faBtnAdd.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				long currentTime=new Date().getTime();
				if(seclectDate.getTime() < currentTime - 1000)
				{
					Toast.makeText(getContext(),"为什么要设置过去的时间\n想穿越吗",Toast.LENGTH_LONG).show();
					return;
				}
				double timeLength=Double.parseDouble(tvTimeLength.getText().toString());
				long startTime=seclectDate.getTime()+1800000L;
				long endTime=startTime+(long)((int)timeLength*3600000L)+(long)((timeLength-(int)timeLength)*60000L);
				for(int i=0;i<dates.size();i++)
				{
					if(dates.get(i).getDate().getTime() <= currentTime)
					{
						break;
					}
					else if(!(dates.get(i).getStartTime()>endTime||dates.get(i).getEndTime()<startTime))
					{
						Toast.makeText(getContext(),String.format("这个时间段你应该在%s\n而不是%s",dates.get(i).getTheme(),etTheme.getText().toString()),Toast.LENGTH_LONG).show();
						return;
					}
				}
				SingleClockDate newClock=new SingleClockDate(seclectDate,timeLength,etTheme.getText().toString());
				clockDate.addDate(newClock);
				Data inputData = new Data.Builder()
						.putString("theme", etTheme.getText().toString())
						.putDouble("timeLength",timeLength)
						.build();
				long triggerAtMillis = seclectDate.getTime()-currentTime;
				Constraints constraints = new Constraints.Builder()
						.setRequiresStorageNotLow(false)
						.build();
				OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ClockWork.class)
						.setInitialDelay(triggerAtMillis, TimeUnit.MILLISECONDS)
						.setId(newClock.getId())
						.setInputData(inputData)
						.setBackoffCriteria(BackoffPolicy.LINEAR, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
						.setConstraints(constraints)
						.build();
				
				WorkManager.getInstance(requireContext())
						.enqueue(workRequest);
				dates=clockDate.getDates();
				/*ZoneId zoneId = ZoneId.systemDefault();
				Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)seclectDate.getTime(), alarmIntent, PendingIntent.FLAG_IMMUTABLE);
				AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
				alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, seclectDate.getTime(), pendingIntent);*/
				Toast.makeText(getContext(),"successfully added",Toast.LENGTH_LONG).show();
				//Snackbar.make(root, "successfully added", Snackbar.LENGTH_LONG).show();
			}
		});
		//notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
}
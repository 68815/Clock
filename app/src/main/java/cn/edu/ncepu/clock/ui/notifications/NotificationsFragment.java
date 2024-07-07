package cn.edu.ncepu.clock.ui.notifications;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import cn.edu.ncepu.clock.AlarmReceiver;
import cn.edu.ncepu.clock.ClockDate;
import cn.edu.ncepu.clock.R;
import cn.edu.ncepu.clock.SingleClockDate;
import cn.edu.ncepu.clock.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment
{
	private Date seclectDate;
	private TextView tvDate,tvTime;
	private EditText etTheme;
	private Button btnDate,btnTime;
	private FloatingActionButton faBtnAdd;
	private ClockDate clockDate;
	private FragmentNotificationsBinding binding;
	
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		NotificationsViewModel notificationsViewModel =
				new ViewModelProvider(this).get(NotificationsViewModel.class);
		
		binding = FragmentNotificationsBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		tvDate=root.findViewById(R.id.editTextDate);
		tvTime=root.findViewById(R.id.editTextTime);
		etTheme=root.findViewById(R.id.et_set_theme);
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
				final Dialog dialog = new Dialog(getContext());
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
				clockDate.addDate(new SingleClockDate(seclectDate,etTheme.getText().toString()));
				ZoneId zoneId = ZoneId.systemDefault();
				Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)seclectDate.getTime(), alarmIntent, PendingIntent.FLAG_IMMUTABLE);
				AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
				alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, seclectDate.getTime(), pendingIntent);
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
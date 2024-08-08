package cn.edu.ncepu.clock.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Date;

import cn.edu.ncepu.clock.model.ClockDate;
import cn.edu.ncepu.clock.R;
import cn.edu.ncepu.clock.model.SingleClockDate;
import cn.edu.ncepu.clock.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment
{
	private SeekBar seekBar;
	private RecyclerView recyclerView;
	private ArrayList<SingleClockDate> dates;
	private FragmentHomeBinding binding;
	private ClockAdapter adapter;
	private int nearbyDateClockCounts;//时间久远的闹钟就不显示了
	public void updateUI()
	{
		dates = ClockDate.getClockDate(getContext()).getDates();
		if(null == adapter)
		{
			adapter = new ClockAdapter();
		}
		recyclerView.setAdapter(adapter);
		nearbyDateClockCounts = ClockDate.getClockDate(getContext()).getNearbyDateClockCounts();
	}
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		recyclerView = root.findViewById(R.id.rv_clocks);
		seekBar = root.findViewById(R.id.seekBar5);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setLongClickable(true);
		registerForContextMenu(recyclerView);
		updateUI();
		if(!dates.isEmpty())
		{
			seekBar.setMax((int)(dates.get(0).getDate().getTime() - new Date().getTime()));
		}
		int[] count = {0};
		Handler handler = new Handler();
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				if (!dates.isEmpty())
				{
					long seconds = dates.get(0).getDate().getTime() - new Date().getTime();
					if (seconds >= 0)
					{
						count[0] = 1;
						seekBar.setProgress(seekBar.getMax() - (int) (seconds));
						handler.postDelayed(this, 100);
					}
					else
					{
						if(count[0] == 1)
						{
							int pp = ClockDate.getClockDate(getContext()).getNearbyDateClockCounts() - 1;
							if (pp != 0)
							{
								long currentTime = new Date().getTime();
								dates = ClockDate.getClockDate(getContext()).getDates();
								adapter.notifyItemMoved(0, pp);
								if (dates.get(0).getDate().getTime() > currentTime)
								{
									seekBar.setMax((int) (dates.get(0).getDate().getTime() - currentTime));
									count[0] = 0;
									handler.postDelayed(this, 100);
								}
							}
						}
					}
				}
			}
		};
		handler.postDelayed(runnable, 0);
		//homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
	}
	private class ViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView tvClock,tvTime,tvTheme,tvTimeRange;
		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			tvClock=itemView.findViewById(R.id.tv_clock);
			tvTime=itemView.findViewById(R.id.tv_time);
			tvTheme=itemView.findViewById(R.id.tv_theme);
			tvTimeRange=itemView.findViewById(R.id.tv_time_range);
		}
		public void bind(SingleClockDate date)
		{
			tvTheme.setText(date.getTheme());
			tvClock.setText(String.format("%d年%d月%d日%d时%d分 响铃", date.getDate().getYear()+1900, date.getDate().getMonth()+1, date.getDate().getDate(), date.getDate().getHours(), date.getDate().getMinutes()));
			tvTimeRange.setText(String.format("%d:%d~%d:%d",date.getStartHour(),date.getStartMinute(),date.getEndHour(),date.getEndMinute()));
			Handler handler=new Handler();
			Runnable runnable=new Runnable()
			{
				@Override
				public void run()
				{
					long seconds = date.getDate().getTime()/1000-new Date().getTime()/1000;
					if(seconds < 0)
					{
						tvTime.setText("时间已过");
					}
					else
					{
						long minutes = seconds / 60;
						long hours = minutes / 60;
						long days = hours / 24;
						String timeStr = "还有" +
								(days > 0 ? days + "天" : "") +
								(hours % 24 > 0 ? hours % 24 + "时" : "") +
								(minutes % 60 > 0 ? minutes % 60 + "分" : "") +
								(seconds % 60 + "秒");
						tvTime.setText(timeStr.trim());
						handler.postDelayed(this,1000);
					}
				}
			};
			handler.postDelayed(runnable,0);
		}
	}
	private class ClockAdapter extends RecyclerView.Adapter<ViewHolder>
	{
		private int position=0;
		
		public int getPosition()
		{
			return position;
		}
		public void setPosition(int position)
		{
			this.position = position;
		}
		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
		{
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_view, parent, false);
			return new ViewHolder(itemView);
		}
		
		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position)
		{
			holder.bind(dates.get(position));
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					setPosition(holder.getLayoutPosition());
					return false;
				}
			});
		}
		@Override
		public void onViewRecycled(@NonNull ViewHolder holder)
		{
			holder.itemView.setOnLongClickListener(null);
			super.onViewRecycled(holder);
		}
		@Override
		public int getItemCount()
		{
			return nearbyDateClockCounts;
		}
	}
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		binding = null;
	}
	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo)
	{
		requireActivity().getMenuInflater().inflate(R.menu.menu_delete,menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item)
	{
		if(item.getItemId() == R.id.menu_delete)
		{
			WorkManager workManager = WorkManager.getInstance(requireContext());
			workManager.cancelWorkById(dates.get(adapter.getPosition()).getId());
			/*Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)(dates.get(adapter.getPosition()).getDate().getTime()), alarmIntent, PendingIntent.FLAG_IMMUTABLE);
			AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
			if (null != alarmManager)
			{
				alarmManager.cancel(pendingIntent);
			}*/
			ClockDate.getClockDate(getContext()).deleteDate(adapter.getPosition());
			adapter.notifyItemRemoved(adapter.getPosition());
		}
		return super.onContextItemSelected(item);
	}
}
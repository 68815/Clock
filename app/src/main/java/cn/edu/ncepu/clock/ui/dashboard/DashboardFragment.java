package cn.edu.ncepu.clock.ui.dashboard;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.edu.ncepu.clock.R;
import cn.edu.ncepu.clock.model.ClockDate;
import cn.edu.ncepu.clock.model.SingleClockDate;
import cn.edu.ncepu.clock.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment
{
	private RecyclerView recyclerView;
	private ArrayList<SingleClockDate> dates;
	private FragmentDashboardBinding binding;
	private DashboardFragment.HistoryAdapter adapter;
	public void updateUI()
	{
		dates= ClockDate.getClockDate(getContext()).getDates();
		if(null == adapter)
		{
			adapter = new HistoryAdapter();
		}
		recyclerView.setAdapter(adapter);
	}
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState)
	{
		DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
		
		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		recyclerView=root.findViewById(R.id.rv_histories);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setLongClickable(true);
		registerForContextMenu(recyclerView);
		updateUI();
		//dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
	}
	
	private class ViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView tvDate,tvTheme;
		public ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			tvDate=itemView.findViewById(R.id.tv_history_date);
			tvTheme=itemView.findViewById(R.id.tv_history_theme);
		}
		public void bind(SingleClockDate date)
		{
			tvDate.setText(String.format("%d年%d月%d日%d时%d分", date.getDate().getYear()+1900, date.getDate().getMonth()+1, date.getDate().getDate(), date.getDate().getHours(), date.getDate().getMinutes()));
			tvTheme.setText(date.getTheme());
		}
	}
	private class HistoryAdapter extends RecyclerView.Adapter<DashboardFragment.ViewHolder>
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
		public DashboardFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
		{
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_view, parent, false);
			return new DashboardFragment.ViewHolder(itemView);
		}
		
		@Override
		public void onBindViewHolder(@NonNull DashboardFragment.ViewHolder holder, int position)
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
		public void onViewRecycled(@NonNull DashboardFragment.ViewHolder holder)
		{
			holder.itemView.setOnLongClickListener(null);
			super.onViewRecycled(holder);
		}
		@Override
		public int getItemCount()
		{
			return dates.size();
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
			ClockDate.getClockDate(getContext()).deleteDate(adapter.getPosition());
			adapter.notifyItemRemoved(adapter.getPosition());
		}
		return super.onContextItemSelected(item);
	}
}
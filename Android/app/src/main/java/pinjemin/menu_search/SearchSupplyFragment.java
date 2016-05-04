package pinjemin.menu_search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.SearchTask;

public class SearchSupplyFragment extends Fragment {

	private String query;
	private TreeMap<String, String> searchQuery = new TreeMap<>();

	public SearchSupplyFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SearchTask searchTask = new SearchTask(getActivity(), SearchTask.SUPPLY_POST, searchQuery);
		searchTask.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search_supply, container, false);

		if (getArguments().getString("query") != null) {
			query = getArguments().getString("query");
			searchQuery.put("query", query);
		} else {
			searchQuery.put("query", null);
		}

		return view;
	}

	public interface ClickListener {
		void onClick(View view, int position);

		void onLongClick(View view, int position);
	}

	public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

		private GestureDetector gestureDetector;
		private SearchSupplyFragment.ClickListener clickListener;

		public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SearchSupplyFragment.ClickListener clickListener) {
			this.clickListener = clickListener;
			gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
					if (child != null && clickListener != null) {
						clickListener.onLongClick(child, recyclerView.getChildPosition(child));
					}
				}
			});
		}

		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

			View child = rv.findChildViewUnder(e.getX(), e.getY());
			if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
				clickListener.onClick(child, rv.getChildPosition(child));
			}
			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		}
	}
}

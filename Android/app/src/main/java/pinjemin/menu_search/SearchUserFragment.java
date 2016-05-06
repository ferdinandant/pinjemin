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


public class SearchUserFragment extends Fragment
{

	private String query;
	private TreeMap<String,String> searchQuery = new TreeMap<>();

	public SearchUserFragment() {
		// Required empty public constructor
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SearchTask searchTask = new SearchTask(getActivity(), SearchTask.USER_POST, searchQuery);
		searchTask.execute();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search_user, container, false);

		if (getArguments().getString("query") != null) {
			query = getArguments().getString("query");
			searchQuery.put("query", query);
		}
		else {
			searchQuery.put("query", null);
		}

		return view;
	}
}

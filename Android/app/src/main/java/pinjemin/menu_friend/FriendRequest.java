package pinjemin.menu_friend;

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

import pinjemin.R;
import pinjemin.backgroundTask.PopulateFriendTask;
import pinjemin.session.SessionManager;


public class FriendRequest extends Fragment
{

	private SessionManager session;

	public FriendRequest() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		session = new SessionManager(getActivity());
		String currentUid = session.getUserDetails().get(SessionManager.KEY_UID);

		PopulateFriendTask populateFriendTask = new PopulateFriendTask(getActivity(), PopulateFriendTask.FRIEND_REQUEST, currentUid);
		populateFriendTask.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
		return view;
	}

	public interface ClickListener
	{
		void onClick(View view, int position);

		void onLongClick(View view, int position);
	}

	public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
	{

		private GestureDetector gestureDetector;
		private FriendRequest.ClickListener clickListener;

		public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendRequest.ClickListener clickListener) {
			this.clickListener = clickListener;
			gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
			{
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

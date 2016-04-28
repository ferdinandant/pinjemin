package pinjemin.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import pinjemin.backgroundTask.PopulateTimelineTask;
import pinjemin.R;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineDemandFragment extends Fragment {

    private Calendar lastRequest = null;
    private SessionManager session;

    public TimelineDemandFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (lastRequest == null || UtilityDate.isToRefreshAgain(lastRequest)) {
            String[] inputReceive = {"PID", "UID", "Timestamp", "NamaBarang", "Deskripsi", "LastNeed", "AccountName"};

            PopulateTimelineTask populateTimelineTask = new PopulateTimelineTask(getActivity(), "postDemand", inputReceive);
            populateTimelineTask.execute();

            //BackgroundTaskDatabase backgroundTaskDatabase = new BackgroundTaskDatabase(getActivity(), "getpermintaantimeline.php", "receive", "postDemand", inputReceive);
            //backgroundTaskDatabase.execute();

            lastRequest = Calendar.getInstance();
        }

        lastRequest = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline_demand, container, false);

        return view;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TimelineDemandFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TimelineDemandFragment.ClickListener clickListener) {
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

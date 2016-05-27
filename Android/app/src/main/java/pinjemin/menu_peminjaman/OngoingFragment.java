package pinjemin.menu_peminjaman;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.backgroundTask.PopulatePeminjamanTask;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;
import pinjemin.menu_friend.FriendRequest;
import pinjemin.menu_friend.FriendTemanAnda;
import pinjemin.session.SessionManager;

public class OngoingFragment extends Fragment {

    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    public OngoingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_ongoing, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(
              getChildFragmentManager());

        adapter.addFragment(new OngoingDipinjamFragment(), "Dipinjam");
        adapter.addFragment(new OngoingDipinjamkanFragment(), "Dipinjamkan");

        Log.d("debug", "bikin tab");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}

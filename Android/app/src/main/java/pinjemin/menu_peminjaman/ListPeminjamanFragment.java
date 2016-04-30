package pinjemin.menu_peminjaman;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;


public class ListPeminjamanFragment extends Fragment {

    private TabLayout tabLayout;
    private CustomViewPager viewPager;

    public ListPeminjamanFragment() {
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
        View view = inflater.inflate(R.layout.fragment_list_peminjaman, container, false);

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

        adapter.addFragment(new WaitingPeminjamanFragment(), "Waiting");
        adapter.addFragment(new OngoingPeminjamanFragment(), "Ongoing");
        adapter.addFragment(new ExpiredPeminjamanFragment(), "Expired");

        viewPager.setAdapter(adapter);
    }
}

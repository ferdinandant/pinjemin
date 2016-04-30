/**
 * ===================================================================================
 * [TIMELINE FRAGMENT]
 * Binding data di timeline dengan RecyclerView yang terkait
 * ------------------------------------------------------------------------------------
 * Author: Ferdinand Antonius, Kemal Amru Ramadhan
 * Refactoring & Documentation: Ferdinand Antonius
 * ===================================================================================
 */

package pinjemin.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.behavior.CustomViewPager;
import pinjemin.behavior.CustomViewPagerAdapter;


public class TimelineFragment extends Fragment {
    public static final int DEMAND_MODE = 0;
    public static final int SUPPLY_MODE = 1;

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private int currentTimelineMode = 0;

    public TimelineFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState
    ) {
        // Inflate (render) layout pada fragment ini:
        // Syntax: inflate(xmlLayoutFile, parentViewGroup, attachToRoot)
        // (attachToRoot == false, berarti dia stand-alone view (tidak jadi child))
        final View view = inflater.inflate(R.layout.fragment_timeline, null, false);

        // initialize components:
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewpager);

        // populate tab items dan set adapter ke viewPager
        configureViewPager(viewPager);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        // initialize floating action button
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) view.findViewById(R.id.btn_post);

        // setting action listeners:
        // (1) pada viewPager: saat tab item dipencet (permintaan/penawaran)
        // (2) pada floatingActionButton: saat dipencet
        viewPager.addOnPageChangeListener(new ViewPagerListener());
        floatingActionButton.setOnClickListener(new floatingActionButtonListener());

        return view;
    }

    /**
     * ==============================================================================
     * Mengatur viewPager: mengatur adapter (penampung fragment) dan action listener-nya
     * ==============================================================================
     */
    private void configureViewPager(ViewPager viewPager) {
        // NOTE: CustomViewPagerAdapter dideklarasikan di kelas terpisah
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getChildFragmentManager());

        // tambahkan tab "Permintaan" dan "Penawaran"
        // menambahkan adapter untuk viewPager
        // Syntax: addFragment(fragment, title)
        adapter.addFragment(new TimelineDemandFragment(), "Permintaan");
        adapter.addFragment(new TimelineSupplyFragment(), "Penawaran");
        viewPager.setAdapter(adapter);
    }

    /**
     * ==============================================================================
     * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
     * ==============================================================================
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // --- inner class declaration ---

    /**
     * ==============================================================================
     * Action Listener untuk viewPager, berguna untuk handling saat tab item ditekan
     * (saat berpindah tab peminjaman <-> penawaran)
     * ==============================================================================
     */
    private class ViewPagerListener implements CustomViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    // dipencet tab item index 0: permintaan
                    currentTimelineMode = DEMAND_MODE;
                    break;
                case 1:
                    // dipencet tab item index 0: penawaran
                    currentTimelineMode = SUPPLY_MODE;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * ==============================================================================
     * Action Listener untuk floatingActionButtonListener, berguna untuk handling
     * saat tombol float action button ditekan
     * ==============================================================================
     */
    private class floatingActionButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (currentTimelineMode == 0) {
                // mulai activity CreatePostDemand (permintaan baru)
                startActivity(new Intent(getActivity(), CreatePostDemand.class));
            } else {
                // mulai activity CreatePostSupply (penawaran baru)
                startActivity(new Intent(getActivity(), CreatePostSupply.class));
            }
        }
    }
}

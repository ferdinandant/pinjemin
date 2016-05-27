package pinjemin.menu_peminjaman;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.backgroundTask.PopulatePeminjamanTask;
import pinjemin.session.SessionManager;

public class ExpiredFragment extends Fragment {

    PopulatePeminjamanTask task;

    public ExpiredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SessionManager session = new SessionManager(getActivity());
        session.checkLogin();

        String ownUid = session.getUserDetails().get(SessionManager.KEY_UID);

        task = new PopulatePeminjamanTask(getActivity(), PopulatePeminjamanTask.PEMINJAMAN_EXPIRED, ownUid);
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        task.update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_expired, container, false);
    }
}

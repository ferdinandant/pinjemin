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

public class WaitingFragment extends Fragment {

    PopulatePeminjamanTask task;
    public WaitingFragment() {
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
        return inflater.inflate(R.layout.fragment_log_waiting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SessionManager session = new SessionManager(getActivity());

        String ownUid = session.getUserDetails().get(SessionManager.KEY_UID);

        task = new PopulatePeminjamanTask(getActivity(), PopulatePeminjamanTask.PEMINJAMAN_WAITING, ownUid);
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        task.update();
    }
}

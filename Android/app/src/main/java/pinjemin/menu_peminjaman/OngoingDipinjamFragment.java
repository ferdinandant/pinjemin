package pinjemin.menu_peminjaman;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.backgroundTask.PopulatePeminjamanTask;
import pinjemin.session.SessionManager;


public class OngoingDipinjamFragment extends Fragment
{
	boolean hasBeenInflated = false;
	PopulatePeminjamanTask task;

	public OngoingDipinjamFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		hasBeenInflated = true;
		return inflater.inflate(R.layout.fragment_ongoing_dipinjam, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SessionManager session = new SessionManager(getActivity());
		String ownUid = session.getUserDetails().get(SessionManager.KEY_UID);

		task = new PopulatePeminjamanTask(getActivity(), PopulatePeminjamanTask.PEMINJAMAN_ONGOING_DIPINJAM, ownUid);

		task.execute();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/** ==============================================================================
	 * Handler saat fragment ini muncul ke user
	 * ============================================================================== */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// refresh status visible atau tidak
		super.setUserVisibleHint(isVisibleToUser);

		// kalau visible, refresh
		if (isVisibleToUser && hasBeenInflated) {

		}
	}

}

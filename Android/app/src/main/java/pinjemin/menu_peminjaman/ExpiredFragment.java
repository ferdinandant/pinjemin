package pinjemin.menu_peminjaman;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pinjemin.R;
import pinjemin.backgroundTask.PopulatePeminjamanTask;
import pinjemin.session.SessionManager;


public class ExpiredFragment extends Fragment
{
	private static boolean isFragmentReady = false;
	private static String currentUID;
	private static Activity activity;

	public ExpiredFragment() {
		// Required empty public constructor
	}

	/** ==============================================================================
	 *Initial creation of fragment, dipanggil sebelum pemanggilan onCreateView()
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Dipanggil saat activity yang mengandung fragment ini sudah di-create dan view
	 * hierarchy dari fragment ini sudah diinstansiasi.
	 * ============================================================================== */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SessionManager session = new SessionManager(getActivity());
		currentUID = session.getUserDetails().get(SessionManager.KEY_UID);
		activity = getActivity();

		isFragmentReady = true;
		performRefresh();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/** ==============================================================================
	 * Untuk instansiasi GUI
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState
	) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_log_expired, container, false);
	}

	/** ==============================================================================
	 * Handler saat fragment ini muncul ke user
	 * ============================================================================== */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// refresh status visible atau tidak
		super.setUserVisibleHint(isVisibleToUser);
	}

	/** ==============================================================================
	 * Mmeperbarui semua item yang ditampilkan pada fragment ini
	 * ============================================================================== */
	public static void performRefresh() {
		if (isFragmentReady) {
			Log.d("DEBUG", "performRefresh di Expired");
			PopulatePeminjamanTask task = new PopulatePeminjamanTask(activity,
				PopulatePeminjamanTask.PEMINJAMAN_EXPIRED, currentUID);
			task.execute();
		}
	}
}

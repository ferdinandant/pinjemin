package pinjemin.menu_friend;

import android.app.Activity;
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


public class FriendTemanAndaFragment extends Fragment
{
	private SessionManager session;
	private static String currentUID;
	private static Activity activity;
	private static boolean isFragmentReady = false;


	public FriendTemanAndaFragment() {
		// Required empty public constructor
	}

	/** ==============================================================================
	 * Dipanggil saat Fragment dibentuk, sebelum pemanggilan onCreateView()
	 * NOTE: Hanya dipanggil kalau ada setHasOptionsMenu(true) di onCreateView
	 * ============================================================================== */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** ==============================================================================
	 * Inisialisasi fragment GUI
	 * ============================================================================== */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_friend_teman_anda, container, false);
		return view;
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

	/** ==============================================================================
	 * Mmeperbarui semua item yang ditampilkan pada fragment ini
	 * ============================================================================== */
	public static void performRefresh() {
		if (isFragmentReady) {
			PopulateFriendTask populateFriendTask = new PopulateFriendTask(
				activity, PopulateFriendTask.FRIEND_TEMAN_ANDA, currentUID);
			populateFriendTask.execute();
		}
	}
}

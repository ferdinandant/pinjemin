/** ===================================================================================
 * [LOGIN ACTIVITY]
 * Kelas yang menampilkan halaman login
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.TreeMap;

import pinjemin.backgroundTask.CommentTask;
import pinjemin.R;
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityGUI;


public class CommentActivity extends AppCompatActivity
{
	private Toolbar toolbar;
	private EditText inputKomentar;
	private TextInputLayout inputLayoutKomentar;
	private Button buttonSubmit;
	private String type;
	private String PID, ownUID, parentUID, content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		PID = intent.getStringExtra("pid");
		parentUID = intent.getStringExtra("parentUid");

		SessionManager session = new SessionManager(getBaseContext());
		ownUID = session.getUserDetails().get(SessionManager.KEY_UID);

		// initialize toolbar
		// set toolbar sebagai action bar (main toolbar) untuk activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (type.equalsIgnoreCase("create")) {
			toolbar.setTitle("Beri Tanggapan");
		}
		else {
			toolbar.setTitle("Balas Komentar");
		}

		setSupportActionBar(toolbar);

		// initialize layouts
		inputLayoutKomentar = (TextInputLayout) findViewById(R.id.input_layout_komentar);

		// initialize components
		inputKomentar = (EditText) findViewById(R.id.input_komentar);
		buttonSubmit = (Button) findViewById(R.id.btn_submit);

		// NOTE: inner class MyTextWatcher diimplementasikan  di bawah
		inputKomentar.addTextChangedListener(new EditTextTextWatcher(
			this, inputKomentar, inputLayoutKomentar, "Masukkan Komentar Anda"));

		Log.d("Masuk Comment", "");

		// set action listener (submit form)
		buttonSubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				submitForm();
			}
		});
	}


	/** ==============================================================================
	 * Submit data login
	 * ============================================================================== */
	private void submitForm() {
		// kalau form tidak valid, jangan lakukan apa-apa lagi
		if (!UtilityGUI.assureNotEmpty(this, inputKomentar, inputLayoutKomentar,
			"Masukkan komentar Anda")) return;

		// ambil userkomentar dan password dari text field
		content = inputKomentar.getText().toString();

		// susun informasi login yang akan dikirim ke server
		TreeMap<String,String> inputData = new TreeMap<>();
		inputData.put("PID", PID);
		inputData.put("ownUID", ownUID);
		inputData.put("content", content);

		Log.d("DEBUG", "hai" + PID);
		Log.d("Comment", "fasdf");

		if (type.equalsIgnoreCase("create")) {
			CommentTask task = new CommentTask(this, CommentTask.CREATE_THREAD, inputData);
			task.execute();
			finish();
		}
		else {
			inputData.put("parentUID", parentUID);
			CommentTask task = new CommentTask(this, CommentTask.REPLY_THREAD, inputData);
			task.execute();
			finish();
		}
	}
}
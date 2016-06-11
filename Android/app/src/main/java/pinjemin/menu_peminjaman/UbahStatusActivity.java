/** ===================================================================================
 * [LOGIN ACTIVITY]
 * Kelas yang menampilkan halaman login
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.menu_peminjaman;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pinjemin.R;
import pinjemin.backgroundTask.PeminjamanTask;
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.utility.UtilityGUI;


public class UbahStatusActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
	private Toolbar toolbar;
	private EditText inputreview;
	private Spinner spinnerRating, spinnerStatus;
	private TextInputLayout inputLayoutreview;
	private Button buttonSubmit;
	private String pid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubah_status);

		Intent intent = getIntent();
		pid = intent.getStringExtra("PID");

		// initialize toolbar
		// set toolbar sebagai action bar (main toolbar) untuk activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Ubah Status Peminaman");
		setSupportActionBar(toolbar);

		// initialize layouts
		inputLayoutreview = (TextInputLayout) findViewById(R.id.input_layout_review);

		// initialize components
		inputreview = (EditText) findViewById(R.id.input_review);
		spinnerRating = (Spinner) findViewById(R.id.spinner_rating);
		spinnerStatus = (Spinner) findViewById(R.id.spinner_status);
		buttonSubmit = (Button) findViewById(R.id.btn_submit);

		// spinner rating
		spinnerRating.setOnItemSelectedListener(this);
		spinnerRating.setPrompt("Pilih Rating");

		// spinner drop down elements
		List<String> categoriesRating = new ArrayList<>();
		categoriesRating.add("5 bintang");
		categoriesRating.add("4 bintang");
		categoriesRating.add("3 bintang");
		categoriesRating.add("2 bintang");
		categoriesRating.add("1 bintang");

		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesRating);

		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerRating.setAdapter(dataAdapter1);

		// spinner status
		spinnerStatus.setOnItemSelectedListener(this);
		spinnerStatus.setPrompt("Pilih Status");

		// spinner drop down elements
		List<String> categoriesStatus = new ArrayList<>();
		categoriesStatus.add("MASIH DIPINJAM");
		categoriesStatus.add("DIKEMBALIKAN");
		categoriesStatus.add("HILANG");

		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesStatus);

		// Drop down layout style - list view with radio button
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerStatus.setAdapter(dataAdapter2);

		// NOTE: inner class MyTextWatcher diimplementasikan  di bawah
		inputreview.addTextChangedListener(new EditTextTextWatcher(
			this, inputreview, inputLayoutreview, "Masukkan Review Anda"));

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
		// ambil review dan password dari text field
		String review = inputreview.getText().toString();
		String rating = spinnerRating.getSelectedItem().toString();
		String status = spinnerStatus.getSelectedItem().toString();

		rating = "" + rating.charAt(0);

		// susun informasi login yang akan dikirim ke server
		TreeMap<String,String> inputData = new TreeMap<String,String>();
		inputData.put("PID", pid);
		inputData.put("status", status);
		inputData.put("rating", rating);
		inputData.put("review", review);

		//Toast.makeText(this, status + " - " + rating, Toast.LENGTH_LONG).show();
		PeminjamanTask task = new PeminjamanTask(this, PeminjamanTask.CHANGE_STATUS, inputData);
		task.execute();
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// On selecting a spinner item
		//String item = parent.getItemAtPosition(position).toString();

		// Showing selected spinner item
		//Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
}
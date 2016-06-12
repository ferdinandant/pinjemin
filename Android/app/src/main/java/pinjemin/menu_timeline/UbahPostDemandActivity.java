/** ===================================================================================
 * [CREATE POST DEMAND ACTIVITY]
 * Activity untuk membuat post permintaan baru
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.menu_timeline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TreeMap;

import pinjemin.backgroundTask.CreatePostTask;
import pinjemin.R;
import pinjemin.backgroundTask.UbahPostTask;
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.session.SessionManager;
import pinjemin.utility.UtilityDate;
import pinjemin.utility.UtilityGUI;


public class UbahPostDemandActivity extends AppCompatActivity
{
	private static final int DATEPICKER_DIALOG = 0;

	private EditText inputNamaBarang, inputDeskripsi;
	private TextInputLayout inputLayoutNamaBarang, inputLayoutDeskripsi;
	private TextView dateView;
	private View garisBawah;
	private Toolbar toolbar;
	private Button btnSubmit;
	private ImageButton btnBatas;
	private int tahun, bulan, hari;
	private SessionManager sessionManager;
	private String intentPid, intentNamaBarang, intentDeskripsi, intentLastNeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubah_post_demand);

		// initialize session manager
		sessionManager = new SessionManager(this);

		// saat melakukan ubah post
		Intent intent = getIntent();
		intentPid = intent.getStringExtra("pid");
		intentNamaBarang = intent.getStringExtra("namabarang");
		intentDeskripsi = intent.getStringExtra("deskripsi");
		intentLastNeed = intent.getStringExtra("lastneed");

		// initialize toolbar:
		// set title dan jadikan toolbar ini Action Bar (main toolbar) di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Ubah Post Permintaan");
		setSupportActionBar(toolbar);

		// initialize layouts:
		inputLayoutNamaBarang = (TextInputLayout) findViewById(R.id.input_layout_namaBarang);
		inputLayoutDeskripsi = (TextInputLayout) findViewById(R.id.input_layout_deskripsi);

		// initialize components:
		inputNamaBarang = (EditText) findViewById(R.id.input_namaBarang);
		inputDeskripsi = (EditText) findViewById(R.id.input_deskripsi);
		dateView = (TextView) findViewById(R.id.dateView);
		btnBatas = (ImageButton) findViewById(R.id.btn_batas);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		garisBawah = findViewById(R.id.garisBawah);

		// set action listener (text watchers)
		inputNamaBarang.addTextChangedListener(new EditTextTextWatcher(
			this, inputNamaBarang, inputLayoutNamaBarang, "Masukkan nama barang"));
		inputDeskripsi.addTextChangedListener(new EditTextTextWatcher(
			this, inputDeskripsi, inputLayoutDeskripsi, "Masukkan deskripsi"));

		// set tulisan pada dateView:
		// Default date: currentDate + 7 hari
		tahun = Integer.parseInt(intentLastNeed.substring(0, 4));
		bulan = Integer.parseInt(intentLastNeed.substring(5, 7));
		hari = Integer.parseInt(intentLastNeed.substring(8, 10));
		dateView.setText((bulan + 1) + "/" + hari + "/" + tahun);

		// set action listener (submit form)
		btnSubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				submitForm();
			}
		});

		// set action listener (buka dialog date picker):
		btnBatas.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				showDialog(DATEPICKER_DIALOG);
			}
		});

		dateView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				showDialog(DATEPICKER_DIALOG);
			}
		});

		inputNamaBarang.setText(intentNamaBarang);
		inputDeskripsi.setText(intentDeskripsi);
	}

	/** ==============================================================================
	 * Handler untuk menampilkan dialog pada activity. (In this case, satu-satunya
	 * dialog yang mungkin dimunculkan hanya datepicker dialog).
	 * ============================================================================== */
	@Override
	public Dialog onCreateDialog(int id) {
		if (id == DATEPICKER_DIALOG) {
			// tampilkan dialog untuk memilih tanggal (date picker)
			// set nilainya berdasarkan apa yang di-set pada variabel {tahun, bulan, hari}
			// NOTE: inner class MyOnDateSetListener dideklarasikan di bawah
			// Syntax: DatePickerDialog(context, postEditListener, varYear, varMonth, varDate)
			return new DatePickerDialog(this, new MyOnDateSetListener(), tahun, bulan, hari);
		}
		return null;
	}

	/** ==============================================================================
	 * Submit data pembuatan post permintaan
	 * ============================================================================== */
	private void submitForm() {
		// cek apakah setiap field sudah diisi dengan benar
		if (!UtilityGUI.assureNotEmpty(this, inputNamaBarang, inputLayoutNamaBarang,
			"Masukkan nama barang")) return;
		if (!UtilityGUI.assureNotEmpty(this, inputDeskripsi, inputLayoutDeskripsi,
			"Masukkan deskripsi")) return;

		// ambil data dari sessionManager dan EditText fields
		// NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
		String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);
		String namaBarang = inputNamaBarang.getText().toString();
		String deskripsi = inputDeskripsi.getText().toString();
		String batas = tahun + "-" + (bulan + 1) + "-" + hari;

		// susun data untuk dikirim ke server
		TreeMap<String,String> inputSend = new TreeMap<>();
		inputSend.put("PID", intentPid);
		inputSend.put("NamaBarang", namaBarang);
		inputSend.put("Deskripsi", deskripsi);
		inputSend.put("LastNeed", batas);

		// kirim data ke server
		UbahPostTask sendTask = new UbahPostTask(this, UbahPostTask.DEMAND_POST, inputSend);
		sendTask.execute();

		finish();
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Handler ketika user memilih tanggal pada pada date picker
	 * ============================================================================== */
	private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// set variabel sesuai nilai yang diberikan
			tahun = year;
			bulan = monthOfYear;
			hari = dayOfMonth;

			// update text pada dateView
			// NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
			dateView.setText((bulan + 1) + "/" + hari + "/" + tahun);
			garisBawah.setMinimumWidth(dateView.getWidth());
		}
	}


}

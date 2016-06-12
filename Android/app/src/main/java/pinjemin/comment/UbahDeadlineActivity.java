/** ===================================================================================
 * [CREATE POST DEMAND ACTIVITY]
 * Activity untuk membuat post permintaan baru
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.comment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TreeMap;

import pinjemin.backgroundTask.CommentActionTask;
import pinjemin.R;
import pinjemin.backgroundTask.PeminjamanTask;
import pinjemin.session.SessionManager;


public class UbahDeadlineActivity extends AppCompatActivity
{
	private static final int DATEPICKER_DIALOG = 0;
	private static final int TIMEPICKER_DIALOG = 1;

	private Toolbar toolbar;s
	private Button btnSubmit;

	private ImageButton btnDatePicker, btnTimePicker;
	private TextView dateView, timeView;
	private View garisBawahDate, garisBawahTime;

	private int tahun, bulan, hari;
	private int jam, menit;

	private SessionManager sessionManager;
	private String targetUID, postPID, updatePeminjaman;

	private static int inPx_1dp = 0;
	private static int inPx_neg6dp = 0;


	/** ==============================================================================
	 * Inisialisasi fragments dan loaders, dipanggil sebelum activity di-start
	 * ============================================================================== */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ubah_deadline);

		Intent intent = getIntent();
		targetUID = intent.getStringExtra("targetUID");
		postPID = intent.getStringExtra("pid");
		updatePeminjaman = intent.getStringExtra("updatePeminjaman");

		// initialize session manager
		sessionManager = new SessionManager(this);

		// initialize toolbar:
		// set title dan jadikan toolbar ini Action Bar (main toolbar) di activity ini
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Mengubah Deadline");
		setSupportActionBar(toolbar);

		dateView = (TextView) findViewById(R.id.dateView);
		timeView = (TextView) findViewById(R.id.timeView);
		btnDatePicker = (ImageButton) findViewById(R.id.btn_datePicker);
		btnTimePicker = (ImageButton) findViewById(R.id.btn_timePicker);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		garisBawahDate = findViewById(R.id.garisBawahDate);
		garisBawahTime = findViewById(R.id.garisBawahTime);

		// set tulisan pada dateView:
		// Default date: currentDate + 7 hari
		// NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 7);
		tahun = calendar.get(Calendar.YEAR);
		bulan = calendar.get(Calendar.MONTH);
		hari = calendar.get(Calendar.DAY_OF_MONTH);
		dateView.setText((bulan + 1) + "/" + hari + "/" + tahun);

		// set tulisan pada timeView:
		// Default time: 23:55
		jam = 23;
		menit = 55;
		timeView.setText(jam + ":" + menit);

		// resize underline
		double scale = getBaseContext().getResources().getDisplayMetrics().density;
		inPx_1dp = (int) (1 * scale + 0.5f);
		inPx_neg6dp = (int) (-6 * scale + 0.5f);
		dateView.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		timeView.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		LinearLayout.LayoutParams paramDate = new LinearLayout.LayoutParams(dateView.getMeasuredWidth(), inPx_1dp);
		paramDate.topMargin = inPx_neg6dp;
		garisBawahDate.setLayoutParams(paramDate);

		LinearLayout.LayoutParams paramTime = new LinearLayout.LayoutParams(timeView.getMeasuredWidth(), inPx_1dp);
		paramTime.topMargin = inPx_neg6dp;
		garisBawahTime.setLayoutParams(paramTime);

		// set action listener (submit form)
		btnSubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				submitForm();
			}
		});

		// set action listeners (buka dialog date/time picker):
		btnDatePicker.setOnClickListener(new View.OnClickListener()
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
		btnTimePicker.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				showDialog(TIMEPICKER_DIALOG);
			}
		});
		timeView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				showDialog(TIMEPICKER_DIALOG);
			}
		});
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
		if (id == TIMEPICKER_DIALOG) {
			// NOTE: inner class MyOnTimeSetListener dideklarasikan di bawah
			// Syntax: TimePickerDialog(context, postEditListener, varHour, varMinute, is24H)
			return new TimePickerDialog(this, new MyOnTimeSetListener(), jam, menit, true);
		}
		return null;
	}

	/** ==============================================================================
	 * Submit data pembuatan post permintaan
	 * ============================================================================== */
	private void submitForm() {
		// ambil data dari sessionManager dan EditText fields
		// NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
		String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);
		String deadline = tahun + "-" + (bulan + 1) + "-" + hari;
		deadline += " " + (jam % 24) + ":" + (menit % 60) + ":00";

		// susun data untuk dikirim ke server
		TreeMap<String,String> inputData = new TreeMap<>();

		if (updatePeminjaman != null) {
			// ini untuk mengubah deadline pada peminjaman
			inputData.put("PID", "" + postPID);
			inputData.put("deadline", deadline);

			PeminjamanTask task = new PeminjamanTask(
				this, PeminjamanTask.CHANGE_DEADLINE, inputData);
			task.execute();
			finish();
		}
		else {
			// ini confirm transfer barang
			inputData.put("PID", "" + postPID);
			inputData.put("ownUID", "" + uid);
			inputData.put("targetUID", "" + targetUID);
			inputData.put("deadline", deadline);

			CommentActionTask task = new CommentActionTask(
				this, CommentActionTask.INITIATE_TRANSFER, inputData);
			task.execute();
			finish();
		}
	}


	// --- inner class declaration ---

	/** ==============================================================================
	 * Handler ketika user memilih tanggal pada date picker
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

			LinearLayout.LayoutParams paramDate = new LinearLayout.LayoutParams(dateView.getMeasuredWidth(), inPx_1dp);
			paramDate.topMargin = inPx_neg6dp;
			garisBawahDate.setLayoutParams(paramDate);
		}
	}

	/** ==============================================================================
	 * Handler ketika user memilih waktu pada time picker
	 * ============================================================================== */
	private class MyOnTimeSetListener implements TimePickerDialog.OnTimeSetListener
	{
		@Override
		public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
			// set variabel sesuai nilai yang diberikan
			jam = hourOfDay;
			menit = minute;

			timeView.setText(jam + ":" + menit);

			LinearLayout.LayoutParams paramTime = new LinearLayout.LayoutParams(timeView.getMeasuredWidth(), inPx_1dp);
			paramTime.topMargin = inPx_neg6dp;
			garisBawahTime.setLayoutParams(paramTime);
		}
	}


}

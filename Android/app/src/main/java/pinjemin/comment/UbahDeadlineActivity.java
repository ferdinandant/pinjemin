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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TreeMap;

import pinjemin.backgroundTask.CommentActionTask;
import pinjemin.R;
import pinjemin.session.SessionManager;


public class UbahDeadlineActivity extends AppCompatActivity
{
    private static final int DATEPICKER_DIALOG = 0;

    private TextView dateView;
    private View garisBawah;
    private Toolbar toolbar;
    private Button btnSubmit;
    private ImageButton btnBatas;
    private int tahun, bulan, hari;
    private SessionManager sessionManager;
    private String targetUID, postPID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_deadline);

        // initialize session manager
        sessionManager = new SessionManager(this);

        // initialize toolbar:
        // set title dan jadikan toolbar ini Action Bar (main toolbar) di activity ini
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Mengubah Deadline");
        setSupportActionBar(toolbar);

        dateView = (TextView) findViewById(R.id.dateView);
        btnBatas = (ImageButton) findViewById(R.id.btn_batas);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        garisBawah = findViewById(R.id.garisBawah);

        // set action listener (text watchers)
        // set tulisan pada dateView:
        // Default date: currentDate + 7 hari
        // NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        tahun = calendar.get(Calendar.YEAR);
        bulan = calendar.get(Calendar.MONTH);
        hari = calendar.get(Calendar.DAY_OF_MONTH);
        dateView.setText((bulan + 1) + "/" + hari + "/" + tahun);

        Intent intent = getIntent();
        targetUID = intent.getStringExtra("targetUID");
        postPID = intent.getStringExtra("pid");

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

        // ambil data dari sessionManager dan EditText fields
        // NOTE: pada Java, indeks bulan Januari = 0 (jadi perlu ditambah 1)
        String uid = sessionManager.getUserDetails().get(SessionManager.KEY_UID);
        String batas = tahun + "-" + (bulan + 1) + "-" + hari;

        // susun data untuk dikirim ke server

        TreeMap<String, String> inputData = new TreeMap<>();
        inputData.put("PID", ""+postPID);
        inputData.put("ownUID", ""+uid);
        inputData.put("targetUID", ""+targetUID);
        inputData.put("deadline", batas);

        CommentActionTask task = new CommentActionTask(this, CommentActionTask.INITIATE_TRANSFER, inputData);
        task.execute();
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

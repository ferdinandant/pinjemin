/** ===================================================================================
 * [LOGIN ACTIVITY]
 * Kelas yang menampilkan halaman login
 * ------------------------------------------------------------------------------------
 * Author: Kemal Amru Ramadhan
 * Refactoring & Doumentation: Ferdinand Antonius
 * =================================================================================== */

package pinjemin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.util.TreeMap;

import pinjemin.backgroundTask.LoginGoogleTask;
import pinjemin.R;
import pinjemin.backgroundTask.LoginNormalTask;
import pinjemin.behavior.EditTextTextWatcher;
import pinjemin.utility.UtilityGUI;


public class LoginActivity extends AppCompatActivity implements
	GoogleApiClient.OnConnectionFailedListener
{
	private Toolbar toolbar;
	private EditText inputName, inputPassword;
	private TextInputLayout inputLayoutName, inputLayoutPassword;
	private Button buttonSignIn;

	// Sign In Google
	private static final int RC_SIGN_IN = 9001;
	private SignInButton signInButton;
	private GoogleApiClient mGoogleApiClient;
	private ProgressDialog mProgressDialog;


	/** ==============================================================================
	 * Inisialisasi fragments dan loaders, dipanggil sebelum activity di-start
	 * ============================================================================== */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// initialize layouts
		inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
		inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

		// initialize components
		inputName = (EditText) findViewById(R.id.input_name);
		inputPassword = (EditText) findViewById(R.id.input_password);
		buttonSignIn = (Button) findViewById(R.id.btn_signup);

		// NOTE: inner class MyTextWatcher diimplementasikan di bawah
		inputName.addTextChangedListener(new EditTextTextWatcher(
			this, inputName, inputLayoutName, "Masukkan username Anda"));
		inputPassword.addTextChangedListener(new EditTextTextWatcher(
			this, inputPassword, inputLayoutPassword, "Masukkan password Anda"));

		//--------------------------------
		// INI UNTUK LOGIN PINJEMIN BIASA
		//--------------------------------

		// set action listener (submit form)
		buttonSignIn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				Log.d("DEBUG", "Login biasa...");
				normalSignIn();
			}
		});

		//------------------------
		// INI UNTUK LOGIN GOOGLE
		//------------------------

		// Google Sign In
		signInButton = (SignInButton) findViewById(R.id.sign_in_button);

		// konstruksi objek GoogleSignInOptions untuk me-request user ID dan basic profile
		// ID dan basic profile dapat diminta dengan GoogleSignInOptions.DEFAULT_SIGN_IN
		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
			GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

		// tombol login google
		SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
		signInButton.setSize(SignInButton.SIZE_WIDE);
		signInButton.setScopes(googleSignInOptions.getScopeArray());
		signInButton.setColorScheme(SignInButton.COLOR_DARK);

		// bentuk GoogleApiClient dengan akses ke Google Sign-In API,
		// menggunakan options yang di-specify oleh googleSignInOptions.
		// NOTE: Parameter enableAutoManage(FragmentActivity, OnConnectionFailedListener)
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.enableAutoManage(this, this)
			.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
			.build();

		// attach handler untuk tombol login google
		signInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Log.d("DEBUG", "Login google...");
				googleSignIn();
			}
		});
	}

	/** ==============================================================================
	 * Dispatch onStart() ke semua fragments. Memastikan setiap loaders di-start.
	 * ============================================================================== */
	@Override
	public void onStart() {
		super.onStart();

		OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
		if (opr.isDone()) {
			// If the user's cached credentials are valid, the OptionalPendingResult will be "done"
			// and the GoogleSignInResult will be available instantly.
			Log.d("DEBUG", "Got cached sign-in");
			GoogleSignInResult result = opr.get();
			handleSignInResult(result);
		}
		else {
			// If the user has not previously signed in on this device or the sign-in has expired,
			// this asynchronous branch will attempt to sign in the user silently. Cross-device
			// single sign-on will occur in this branch.
			showProgressDialog();
			opr.setResultCallback(new ResultCallback<GoogleSignInResult>()
			{
				@Override
				public void onResult(GoogleSignInResult googleSignInResult) {
					hideProgressDialog();
					handleSignInResult(googleSignInResult);
				}
			});
		}
	}

	/** ==============================================================================
	 * Dispatch incoming result to the correct fragment.
	 * ============================================================================== */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}

	/** ==============================================================================
	 * Menangani hasil sign in
	 * ============================================================================== */
	private void handleSignInResult(GoogleSignInResult result) {
		// kalau berhasil, coba cek di database apakah sudah ada account-nya
		// kalau belum ada, akan dibuatkan akun di server dan user diminta untuk register
		Log.d("DEBUG", "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			String email = acct.getEmail();

			TreeMap<String,String> inputData = new TreeMap<>();
			inputData.put("username", email);

			LoginGoogleTask task = new LoginGoogleTask(this, inputData);
			task.execute();
			finish();
		}
	}

	/** ==============================================================================
	 * Manangani action sign in (kirim intent ke google sign in API)
	 * ============================================================================== */
	private void googleSignIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	/** ==============================================================================
	 * Submit data login untuk login normal (menggunakan akun pinjemin)
	 * ============================================================================== */
	private void normalSignIn() {
		// kalau form tidak valid, jangan lakukan apa-apa lagi
		if (!UtilityGUI.assureNotEmpty(this, inputName, inputLayoutName,
			"Masukkan username Anda")) return;
		if (!UtilityGUI.assureNotEmpty(this, inputPassword, inputLayoutPassword,
			"Masukkan password Anda")) return;

		// ambil username dan password dari text field
		String username = inputName.getText().toString();
		String password = inputPassword.getText().toString();

		// susun informasi login yang akan dikirim ke server
		TreeMap<String,String> loginData = new TreeMap<String,String>();
		loginData.put("username", username);
		loginData.put("password", password);

		// kirimkan data login ke server pada background
		// LoginGoogleTask loginGoogleTask = new LoginGoogleTask(LoginActivity.this, "login.php", username, password);
		Log.d("DEBUG", "Handling untuk login biasa!!!");
		LoginNormalTask loginNormalTask = new LoginNormalTask(this, loginData);
		loginNormalTask.execute();
	}

	/** ==============================================================================
	 * Menangani kasus tidak bisa mengubungi server google
	 * ============================================================================== */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d("DEBUG", "onConnectionFailed:" + connectionResult);
		Toast.makeText(getBaseContext(), "Tidak bisa menghubungi server.", Toast.LENGTH_LONG).show();
	}

	/** ==============================================================================
	 * Dipanggil di onStart(), untuk menampilkan progress dialog saat sistem mencoba
	 * men-sign-in user (silently) saat user tidak dalam keadaan login
	 * ============================================================================== */
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(true);
		}

		mProgressDialog.show();
	}

	/** ==============================================================================
	 * Dipanggil di onStart(), untuk menyembunyikan progress dialog saat sistem
	 * sudah selesai mencoba login user (silently, menggunakan akun google)
	 * ============================================================================== */
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.hide();
		}
	}

	/** ==============================================================================
	 * Handler saat tombol back ditekan
	 * ==============================================================================*/
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.TreeMap;

import pinjemin.backgroundTask.LoginTask;
import pinjemin.R;
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
	private SignInButton signInButton;
	private static final String TAG = "SignInActivity";
	private static final int RC_SIGN_IN = 9001;

	private GoogleApiClient mGoogleApiClient;
	private ProgressDialog mProgressDialog;

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

		// NOTE: inner class MyTextWatcher diimplementasikan  di bawah
		inputName.addTextChangedListener(new EditTextTextWatcher(
			this, inputName, inputLayoutName, "Masukkan username Anda"));
		inputPassword.addTextChangedListener(new EditTextTextWatcher(
			this, inputPassword, inputLayoutPassword, "Masukkan password Anda"));

		// set action listener (submit form)
		buttonSignIn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				submitForm();
			}
		});

		// Google Sign In

		signInButton = (SignInButton) findViewById(R.id.sign_in_button);

		// [START configure_signin]
		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		// [END configure_signin]

		// [START build_client]
		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();
		// [END build_client]

		SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
		signInButton.setSize(SignInButton.SIZE_WIDE);
		signInButton.setScopes(gso.getScopeArray());
		signInButton.setColorScheme(SignInButton.COLOR_DARK);

		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signIn();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
		if (opr.isDone()) {
			// If the user's cached credentials are valid, the OptionalPendingResult will be "done"
			// and the GoogleSignInResult will be available instantly.
			Log.d(TAG, "Got cached sign-in");
			GoogleSignInResult result = opr.get();
			handleSignInResult(result);
		} else {
			// If the user has not previously signed in on this device or the sign-in has expired,
			// this asynchronous branch will attempt to sign in the user silently.  Cross-device
			// single sign-on will occur in this branch.
			showProgressDialog();
			opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
				@Override
				public void onResult(GoogleSignInResult googleSignInResult) {
					hideProgressDialog();
					handleSignInResult(googleSignInResult);
				}
			});
		}
	}

	// [START onActivityResult]
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}
	// [END onActivityResult]

	// [START handleSignInResult]
	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			String email = acct.getEmail();

			TreeMap<String, String> inputData = new TreeMap<>();
			inputData.put("username", email);

			LoginTask task = new LoginTask(this, inputData);
			task.execute();
			finish();
		} else {
			// Signed out, show unauthenticated UI.

		}
	}
	// [END handleSignInResult]

	// [START signIn]
	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	// [END signIn]

	// [START signOut]
	private void signOut() {
		Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
						// [START_EXCLUDE]

						// [END_EXCLUDE]
					}
				});
	}
	// [END signOut]

	// [START revokeAccess]
	private void revokeAccess() {
		Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
				new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
						// [START_EXCLUDE]

						// [END_EXCLUDE]
					}
				});
	}
	// [END revokeAccess]

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Loading....");
			mProgressDialog.setIndeterminate(true);
		}

		mProgressDialog.show();
	}

	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.hide();
		}
	}


	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}


	/** ==============================================================================
	 * Submit data login
	 * ============================================================================== */
	private void submitForm() {
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
		// LoginTask loginTask = new LoginTask(LoginActivity.this, "login.php", username, password);
		LoginTask loginTask = new LoginTask(this, loginData);
		loginTask.execute();
		finish();
	}
}
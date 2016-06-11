package coins.hansung.way.Intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Id to identity READ_CONTACTS permission request.
     */

    Intent intent;
    String gcmCode;

    MyInfo myInfo;

    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefEditor;

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intent = getIntent();
        gcmCode = intent.getStringExtra("gcmCode");

        Log.w("gcmCode", gcmCode);

        mEmailView = (EditText) findViewById(R.id.id);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        myInfo = MyInfo.getInstance();

        loginPref = getSharedPreferences("Login", 0);
        loginPrefEditor = loginPref.edit();
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.


            mAuthTask = new UserLoginTask(email, password, gcmCode);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 2;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }


    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mGcmCode;

        String data;
        BufferedReader reader = null;

        UserLoginTask(String email, String password, String gcmCode) {
            mEmail = email;
            mPassword = password;
            mGcmCode = gcmCode;

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                URL url = new URL(Links.login2URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(mEmail, "UTF-8")
                        + "&" + URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(mPassword, "UTF-8")
                        + "&" + URLEncoder.encode("GCM", "UTF-8") + "=" + URLEncoder.encode(mGcmCode, "UTF-8");

                Log.d("Login", data);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                return e.getMessage();
            }
            // TODO: register the new account here.
        }

        @Override
        protected void onPostExecute(final String str) {

            JSONObject jsonObj;
            JSONArray jsonArray;

            mAuthTask = null;

            if (str.equals("fail")) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else if (str.equals("not exist")) {
                mEmailView.setError(getString(R.string.error_incorrect_email));
                mEmailView.requestFocus();
            } else {
                myInfo.setID(mEmail);

                try {
                    jsonArray = new JSONArray(str);
                    jsonObj = jsonArray.getJSONObject(0);

                    String name = jsonObj.getString("name");
                    String groupCode = jsonObj.getString("group_code");
                    myInfo.setName(name);
                    myInfo.setGroupCode(groupCode);

                    //id groupcode name point
                    loginPrefEditor.putString("ID", mEmail);
                    loginPrefEditor.putString("PW", mPassword);
                    loginPrefEditor.putString("Name", name);
                    loginPrefEditor.putString("Code", groupCode);
                    loginPrefEditor.putBoolean("AutoLogin", true);
                    loginPrefEditor.commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}


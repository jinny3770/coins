package coins.hansung.way.SideMenu;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener{

    SharedPreferences pref;

    MyInfo myInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        myInfo = MyInfo.getInstance();

        setOnPreferenceChange(findPreference("gps"));

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d("gpsSettings", Boolean.toString(pref.getBoolean("gps", true)));

        Preference pAppName = (Preference)findPreference("warning");

        pAppName.setOnPreferenceClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UpdateGpsSignal updateGpsSignal= new UpdateGpsSignal();
        updateGpsSignal.execute(pref.getBoolean("gps", true));
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return true;
        }

    };

    private void setOnPreferenceChange(Preference mPreference) {
        mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        onPreferenceChangeListener.onPreferenceChange(mPreference,
                PreferenceManager.getDefaultSharedPreferences(mPreference.getContext()).getBoolean(mPreference.getKey(), true));
    }


    class UpdateGpsSignal extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... params) {

            try {
                URL url = new URL(Links.updateGpsSigURL);

                Boolean bool = params[0];
                String boolStr;

                if(bool) boolStr="true";
                else boolStr="false";

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                String data = URLEncoder.encode("signal", "UTF-8") + "=" + URLEncoder.encode(boolStr, "UTF-8")
                        + "&" + URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(myInfo.getID(), "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public boolean onPreferenceClick(Preference preference)
    {
        // 도움말 선택시
        if(preference.getKey().equals("warning"))
        {
            Intent intent = new Intent(this, WarningActivity.class);
            startActivityForResult(intent, 0);
        }
        return false;
    }
}
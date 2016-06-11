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

import coins.hansung.way.Main.LoadFamilyList;
import coins.hansung.way.Main.MainActivity;
import coins.hansung.way.R;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;
import coins.hansung.way.etc.RegID;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import org.json.JSONArray;
import org.json.JSONObject;


public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener{
    /*RegID regID;
    private Result result;
    private Message message;
    private final Sender sender = new Sender("AIzaSyB2XTCAUUYfCMNCSyd4Xwvr9gqYr65e1no");*/
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
        //Preference pAppName1 = (Preference)findPreference("testgcm");
        pAppName.setOnPreferenceClickListener(this);
        //pAppName1.setOnPreferenceClickListener(this);

        Log.e("lock", String.valueOf(pref.getBoolean("lock", true)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        UpdateGpsSignal updateGpsSignal= new UpdateGpsSignal();
        updateGpsSignal.execute(pref.getBoolean("gps", true));
        Log.e("lock", String.valueOf(pref.getBoolean("lock", true)));
        //pref.edit().putBoolean();
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
            Log.d("saldkfjlew", "warningstart");
            Intent intent = new Intent(this, WarningActivity.class);
            startActivityForResult(intent, 0);
        }/*
        if (preference.getKey().equals("testgcm")) {
            Log.e("Send", sender.toString());

            regID = RegID.getInstance();
            myInfo = MyInfo.getInstance();
            try {
                String returnString = new LoadFamilyList().execute(myInfo.getGroupCode()).get();
                JSONArray jsonArray = new JSONArray(returnString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    message = new Message.Builder().addData("message", myInfo.getName()).build();
                    String code = jsonObject.getString("GCMCode");
                    Log.e("-------------","---------------");
                    Log.e("groupcode", myInfo.getGroupCode());
                    Log.e("jsonobject", jsonObject.getString("GCMCode"));
                    Log.e("who?", jsonObject.getString("id"));
                    Log.e("returnstring", returnString);
                    Log.e("gcmcode", code);
                    Log.e("message", message.toString());
                    Log.e("regID", GCMRegistrar.getRegistrationId(this));
                    Log.e("myInfo", myInfo.toString());
                    Log.e("sender", sender.send(message, code, 5).toString());


                    if (code != null) {
                        Log.e("result", "213123123");
                        result = sender.send(message, code, 5);

                    }
                }
            } catch (Exception e) {
                result = null;
                e.printStackTrace();
            }

            if (result.getMessageId() != null) {
                try {
                    System.out.println("푸쉬 테스트");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String error = result.getErrorCodeName();
                System.out.println(error);

                if (Constants.ERROR_INTERNAL_SERVER_ERROR.equals(error)) {
                    System.out.println("구글 서버 에러");
                }
            }
        }*/
        return false;
    }
}
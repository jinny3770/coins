package coins.hansung.way.SideMenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.regex.Pattern;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-03-21.
 */

public class WarningActivity extends AppCompatActivity
{
    ListView warningListView;
    GroupListAdapter adapter;

    EditText inputName, inputPhone;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        inputName = (EditText) findViewById(R.id.name);
        inputPhone = (EditText) findViewById(R.id.phone);
        register = (Button) findViewById(R.id.register);

        warningListView = (ListView) findViewById(R.id.warningList);
        adapter = new GroupListAdapter();
        warningListView.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        String jsonName = pref.getString("name", null);
        String jsonPhone = pref.getString("phone", null);
        Log.e("Qewr", jsonName + jsonPhone);

        if (jsonName != null && jsonPhone != null)
        {
            try
            {
                JSONArray jsonNameArray = new JSONArray(jsonName);
                JSONArray jsonPhoneArray = new JSONArray(jsonPhone);

                for (int i = 0; i < jsonNameArray.length(); i++)
                {
                    adapter.addItem(jsonNameArray.optString(i), jsonPhoneArray.optString(i));
                }
            }

            catch (JSONException jsone)
            {
                jsone.printStackTrace();
            }
        }

        warningListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // 롱클릭시 팝업창 호출 => 삭제여부 물어보고 그에 따라 조치
                AlertDialog.Builder builder = new AlertDialog.Builder(WarningActivity.this);
                builder.setTitle("전화번호 삭제");
                builder.setMessage("삭제하시겠습니까?");

                builder.setNegativeButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeItem(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

                return true;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String phone = inputPhone.getText().toString();

                if (name.equals("") || phone.equals("")) // 공백 체크
                {
                    inputName.setText(null);
                    inputPhone.setText(null);
                    Toast.makeText(getApplicationContext(), "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (makePhoneNumber(phone) == null) // 전화번호 정규식 체크
                {
                    inputName.setText(null);
                    inputPhone.setText(null);
                    Toast.makeText(getApplicationContext(), "전화번호 방식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (adapter.checkDuplication(makePhoneNumber(phone))) // 등록여부 체크
                {
                    inputName.setText(null);
                    inputPhone.setText(null);
                    Toast.makeText(getApplicationContext(), "이미 등록된 전화번호입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    inputName.setText(null);
                    inputPhone.setText(null);
                    phone = makePhoneNumber(phone);
                    adapter.addItem(name, phone);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        JSONArray jsonNameArray = new JSONArray();
        JSONArray jsonPhoneArray = new JSONArray();

        for (int i = 0; i < adapter.getCount(); i++)
        {
            jsonNameArray.put(adapter.getItem(i).name);
            jsonPhoneArray.put(adapter.getItem(i).number);
        }

        if (!adapter.isEmpty())
        {
            editor.putString("name", jsonNameArray.toString());
            editor.putString("phone", jsonPhoneArray.toString());

            Log.e("Qewr", jsonNameArray.toString() + jsonPhoneArray.toString());
        }

        else
        {
            editor.putString("name", null);
            editor.putString("phone", null);
        }

        editor.commit();
    }

    public static String makePhoneNumber(String phone) // 전화번호 정규식
    {
        String regExample = "(\\d{3})(\\d{3,4})(\\d{4})";
        String regExample2 = "(\\d{3})-(\\d{3,4})-(\\d{4})";

        if (!Pattern.matches(regExample, phone) && !Pattern.matches(regExample2, phone))
        {
            return null;
        }

        return phone.replaceAll(regExample, "$1-$2-$3");
    }
}
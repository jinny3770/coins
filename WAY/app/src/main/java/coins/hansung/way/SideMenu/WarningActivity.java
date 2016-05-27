package coins.hansung.way.SideMenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    ListView alertListview;
    EditText input, input2;
    Button register;
    ArrayList <String> items;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        alertListview = (ListView) findViewById(R.id.alertList);
        input = (EditText) findViewById(R.id.phone);
        input2 = (EditText) findViewById(R.id.name);
        register = (Button) findViewById(R.id.register);
        items = new ArrayList <String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, R.layout.warning_row, R.id.text1, items);

        alertListview.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("pref", 0);
        String json = pref.getString("phone", null);

        if (json != null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String url = jsonArray.optString(i);
                    items.add(url);
                }
            }

            catch (JSONException jsone)
            {
                jsone.printStackTrace();
            }
        }

        alertListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                // 롱클릭시 팝업창 호출 => 삭제여부 물어보고 그에 따라 조치
                AlertDialog.Builder builder = new AlertDialog.Builder(WarningActivity.this);
                builder.setTitle("연락처 삭제");
                builder.setMessage("삭제하시겠습니까?");

                builder.setNegativeButton("네", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("아니오", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                builder.show();

                return true;
            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String number = input.getText().toString();
                String phone = makePhoneNumber(number);

                if (number.equals("")) // 연락처 공백 체크
                {
                    input.setText(null);
                    Toast.makeText(getApplicationContext(), "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (phone == null) // 연락처 방식 체크 aaa-bbbb-cccc or aaa-bbb-cccc
                {
                    input.setText(null);
                    Toast.makeText(getApplicationContext(), "연락처 방식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (checkDuplication(items, phone)) {
                    input.setText(null);
                    Toast.makeText(getApplicationContext(), "이미 등록된 연락처입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    items.add(phone);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();

        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < items.size(); i++)
        {
            jsonArray.put(items.get(i));
        }

        if (!items.isEmpty())
        {
            editor.putString("phone", jsonArray.toString());
            editor.putString("name", jsonArray.toString());
        }

        else
        {
            editor.putString("phone", null);
            editor.putString("name", null);
        }

        editor.commit();
    }

    public static String makePhoneNumber(String phone)
    {
        String regExample = "(\\d{3})(\\d{3,4})(\\d{4})";
        String regExample2 = "(\\d{3})-(\\d{3,4})-(\\d{4})";

        if (!Pattern.matches(regExample, phone) && !Pattern.matches(regExample2, phone))
        {
            return null;
        }

        return phone.replaceAll(regExample, "$1-$2-$3");
    }

    public boolean checkDuplication(ArrayList <String> arrayList, String str)
    {
        for (int i = 0; i < arrayList.size(); i++)
        {
            if (arrayList.get(i).equals(str))
            {
                return true;
            }
        }

        return false;
    }
}
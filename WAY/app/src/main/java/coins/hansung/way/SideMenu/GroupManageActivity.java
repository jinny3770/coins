package coins.hansung.way.SideMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import coins.hansung.way.Main.LoadFamilyList;
import coins.hansung.way.R;
import coins.hansung.way.etc.Family;
import coins.hansung.way.etc.MyInfo;
import coins.hansung.way.etc.PersonInfo;

public class GroupManageActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView groupCode;
    Button btnInvite, btncheckInvite, btnCreateGroup;
    MyInfo myinfo = MyInfo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupCode = (TextView) findViewById(R.id.groupcode);
        btnInvite = (Button) findViewById(R.id.inviteUser); // 있
        btncheckInvite = (Button) findViewById(R.id.checkInvite); // 없
        btnCreateGroup = (Button) findViewById(R.id.createGroup); // 없

        ListView alertView = (ListView) findViewById(R.id.alertList);
        GroupListAdapter adapter = new GroupListAdapter();
        alertView.setAdapter(adapter);

        if (!myinfo.getGroupCode().equals("000000")) // 그룹코드가 있을 때
        {
            groupCode.setText(myinfo.getGroupCode());
            btncheckInvite.setVisibility(View.GONE);
            btnCreateGroup.setVisibility(View.GONE);
        }

        else // 그룹코드가 없을 때
        {
            groupCode.setText("000000");
            btnInvite.setVisibility(View.GONE);
        }

        try
        {
            Family family = Family.getInstance();
            Log.e("size", String.valueOf(family.getFamilyArray().size()));


            for (int i = 0; i < family.getFamilyArray().size(); i++) {
                PersonInfo p = (PersonInfo) family.getFamilyArray().get(i);
                adapter.addItem(p.getName(), p.getPhoneNumber());
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("그룹관리");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnInvite.setOnClickListener(this);
        btncheckInvite.setOnClickListener(this);
        btnCreateGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.inviteUser :
                intent = new Intent(getApplicationContext(), InviteActivity.class);
                intent.putExtra("code", myinfo.getGroupCode());
                startActivity(intent);
                break;

            case R.id.checkInvite :
                intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
                break;

            case R.id.createGroup :
                String group = makeRandomString();
                myinfo.setGroupCode(group);
                groupCode.setText(myinfo.getGroupCode());

                btnInvite.setVisibility(View.VISIBLE);
                btncheckInvite.setVisibility(View.GONE);
                btnCreateGroup.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("group", groupCode.getText().toString());
        editor.commit();
    }

    private String makeRandomString()
    {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9".split(",");

        for (int i = 0 ; i <  6 ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }

        return buffer.toString();
    }
}
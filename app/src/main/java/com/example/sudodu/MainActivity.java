package com.example.sudodu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

public  class MainActivity extends AppCompatActivity {

    int count = 0;
    private UnlockView mUnlockView;
    private String pwd;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private SimpleDraweeView img;
    private TextView time;
    private TextView forget;
    private TextView type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forget = (TextView) findViewById(R.id.forget);
        type = (TextView) findViewById(R.id.type);


        time = (TextView) findViewById(R.id.time);
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;
        time.setText(date+"");

        img = (SimpleDraweeView) findViewById(R.id.img);
        mUnlockView = (UnlockView) findViewById(R.id.unlock);

        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit = sharedPreferences.edit();
                edit.putBoolean("key",false);
                edit.commit();
                onResume();
                count=0;
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean key = sharedPreferences.getBoolean("key", false);
        pwd = sharedPreferences.getString("pwd", "pwd");
        if (key) {

            mUnlockView.setMode(UnlockView.CHECK_MODE);
            type.setText("请登录你的密码");
            mUnlockView.setOnUnlockListener(new UnlockView.OnUnlockListener() {
                @Override
                public boolean isUnlockSuccess(String result) {

                    if (result.equals(pwd)) {
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();


                }

                @Override
                public void onFailure() {
                    count++;
                    if (count == 3) {
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "密码错误，还有" + (3 - count) + "次机会", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            mUnlockView.setMode(UnlockView.CREATE_MODE);

            type.setText("请设置您的手势密码!");
            mUnlockView.setGestureListener(new UnlockView.CreateGestureListener()

            {
                @Override
                public void onGestureCreated (String result){

                    Log.i("zzz", "onGestureCreated: " + result);
                    pwd = result;
                    edit = sharedPreferences.edit();
                    edit.putBoolean("key",true);
                    edit.putString("pwd",pwd);
                    edit.commit();
                    Toast.makeText(MainActivity.this, "密码设置成功！", Toast.LENGTH_SHORT).show();
                    mUnlockView.setMode(UnlockView.CHECK_MODE);
                    onResume();

                }
            });

        }
    }
}

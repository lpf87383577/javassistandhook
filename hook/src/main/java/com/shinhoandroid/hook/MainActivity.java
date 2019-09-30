package com.shinhoandroid.hook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.shinhoandroid.hook.demo.A;
import com.shinhoandroid.hook.demo.B;
import com.shinhoandroid.hook.demo.HookA;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "bt1被点击了", Toast.LENGTH_SHORT).show();
            }
        });

        HookSetOnClickListenerHelper.hook(this,findViewById(R.id.bt1));

        final A a =  new A();

        a.setB(new B());

        HookA.hook(a);

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                a.testA();
            }
        });



    }
}

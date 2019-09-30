package com.shinhoandroid.testjavassist;

import android.util.Log;
import android.view.View;

public class MyListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        Log.e("MyListener", "myListener clicked");
    }
}

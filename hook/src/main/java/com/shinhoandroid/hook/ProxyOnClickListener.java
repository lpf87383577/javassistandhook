package com.shinhoandroid.hook;

import android.util.Log;
import android.view.View;

/**
 * @author Liupengfei
 * @describe TODO
 * @date on 2019/9/23 14:09
 */
public class ProxyOnClickListener implements View.OnClickListener {

    View.OnClickListener oriLis;


    public ProxyOnClickListener(View.OnClickListener oriLis) {

        this.oriLis = oriLis;
    }


    @Override
    public void onClick(View v) {

        Log.e("lpf", "点击事件被hook到了");

        if (oriLis != null) {
            oriLis.onClick(v);
        }

    }


}

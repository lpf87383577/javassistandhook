package com.shinhoandroid.hook.demo;

import android.view.View;

import com.shinhoandroid.hook.L;

/**
 * @author Liupengfei
 * @describe b的代理
 * @date on 2019/9/26 13:57
 */
public class ProxyB extends B{

    B b;

    public ProxyB(B b) {

        this.b = b;
    }

    @Override
    public void testB() {

        L.e("我是B的代理类，代理执行B的方法");

        if (b != null){
            b.testB();
        }
    }
}

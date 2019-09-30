package com.shinhoandroid.hook.demo;

import com.shinhoandroid.hook.L;

/**
 * @author Liupengfei
 * @describe 持有B的类
 * @date on 2019/9/26 13:52
 */

public class A {

    private B b;

    public void setB(B b){

        this.b = b;
    }

    public void testA(){

        L.e("我是A类的testA方法");

        if (b!=null){

            b.testB();
        }
    }

}

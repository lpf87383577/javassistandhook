package com.shinhoandroid.hook.demo;

import com.shinhoandroid.hook.L;

import java.lang.reflect.Field;

/**
 * @author Liupengfei
 * @describe hook 的核心代码
 * @date on 2019/9/26 14:00
 */
public class HookA {

    /**
     * hook的核心代码
     */
    public static void hook(A a) {

        try {
            //获取到B的属性
            Field fieldB = A.class.getDeclaredField("b");

            //设置B可以被外部访问
            fieldB.setAccessible(true);

            //从a里面获取到B的值
            B b = (B) fieldB.get(a);

            //将B的值设置给代理
            ProxyB proxyB = new ProxyB(b);

            //将a中B的值用proxyB替换
            fieldB.set(a, proxyB);

        } catch (Exception e) {

            L.e("Exception-------"+e.getMessage());
            e.printStackTrace();
        }
    }

}

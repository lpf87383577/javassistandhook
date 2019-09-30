### hook
==hook 表示一段代码我们无法修改或者不想修改，通过反射从外部对代码进行修改。==


###### 需要hook的类
``` 
/**
 * @describe 被代理的类
 */
public class B {
    
    public void testB(){
        L.e("我是B类的testB方法");
    }
}


/**
 * @describe 持有B的类
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

```
###### B的代理类
``` 
/**
 * @describe b的代理
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
```

###### hook类
``` 
/**
 * @describe hook 的核心代码
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
```

###### 代码执行
``` 
final A a =  new A();

        a.setB(new B());

        HookA.hook(a);

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                a.testA();
            }
        });
        
        
//运行结果
//----lpf----: 我是A类的testA方法
//----lpf----: 我是B的代理类，代理执行B的方法
//----lpf----: 我是B类的testB方法        

```

### Javassist
Javassist作用是在编译器间修改class文件，与之相似的ASM（热修复框架女娲）也有这个功能，可以让我们直接修改编译后的class二进制代码，首先我们得知道什么时候编译完成，并且我们要赶在class文件被转化为dex文件之前去修改。在Transfrom这个api出来之前，想要在项目被打包成dex之前对class进行操作，必须自定义一个Task，然后插入到predex或者dex之前，在自定义的Task中可以使用javassist或者asm对class进行操作。而Transform则更为方便，Transfrom会有他自己的执行时机，不需要我们插入到某个Task前面。Tranfrom一经注册便会自动添加到Task执行序列中，并且正好是项目被打包成dex之前。

##### Tranfrom 使用

流程：拿到输入路径->取出要处理的文件->处理文件->移动文件到输出路径

API：

**getName**：用于指明本Transform的名字，这个 name 并不是最终的名字，在TransformManager 中会对名字再处理

**getInputTypes**：用于指明Transform的输入类型，可以作为输入过滤的手段

    –CLASSES 表示要处理编译后的字节码，可能是 jar 包也可能是目录

    –RESOURCES 表示处理标准的 java 资源

**getScopes**：用于指明Transform的作用域

    –PROJECT                         只处理当前项目

    –SUB_PROJECTS  只处理子项目

    –PROJECT_LOCAL_DEPS  只处理当前项目的本地依赖,例如jar, aar

    –EXTERNAL_LIBRARIES  只处理外部的依赖库

    –PROVIDED_ONLY  只处理本地或远程以provided形式引入的依赖库

    –TESTED_CODE                         只处理测试代码

**isIncremental**：用于指明是否是增量构建。

**transform**：核心方法，用于自定义处理,在这个方法中我们可以拿到要处理的.class文件路径、jar包路径、输出文件路径等，拿到文件之后就可以对他们进行操作

```
@Override
    public String getName() {
        return "ModifyTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
    }
```

### javassist介绍
 **介绍**：Javassist是一个动态类库，可以用来检查、”动态”修改以及创建 Java类。其功能与jdk自带的反射功能类似，但比反射功能更强大。
 
 **常用类**
 
 ```
    ClassPool：javassist的类池，使用ClassPool类可以跟踪和控制所操作的类,它的工作方式与 JVM类装载器非常相似，

    CtClass： CtClass提供了检查类数据（如字段和方法）以及在类中添加新字段、方法和构造函数、以及改变类、父类和接口的方法。不
        过Javassist并未提供删除类中字段、方法或者构造函数的任何方法。

    CtField：用来访问属性

    CtMethod ：用来访问方法 

    CtConstructor：用来访问构造器
   
```

**基本用法**

```
   1、添加类搜索路径

          ClassPool pool =ClassPool.getDefault();

            pool.insertClassPath("/usr/local/javalib");

        2、添加方法

         CtClass point =ClassPool.getDefault().get("Point");

         CtMethod m =CtNewMethod.make( "public int xmove(int dx) { x += dx; }", point);point.addMethod(m);

        3、修改方法

         CtClass point =ClassPool.getDefault().get("Point"); 

         CtMethod m= point.getDeclaredMethod(“show", null)

         m.insertAfter(“System.out.prinln(“x:” + x + “,y:) + y”))

        4、添加字段

         CtClass point =ClassPool.getDefault().get("Point");

          CtField f = newCtField(CtClass.intType, "z", point);

          point.addField(f);
 ```  

在每次编译之后我们可以到文件夹app\build\intermediates\transforms\ModifyTransform\debug下查看.class文件是否修改成功   




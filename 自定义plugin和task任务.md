## task 任务

一个 project 包含多个 task,一个 task 包含连续的多个Action

```
println "hello world1111"

task hello{

    doFirst{
        println("doFirst")
    }

    //这段代码不包含在action里面，写在task里面和外面没有区别
    println "hello world2222"

    doLast{
        println("doLast")
    }

}

```
执行task
```
> Configure project :testplugin
hello world1111
hello world2222

> Task :testplugin:hello
doFirst
doLast
```
hello world11111，hello world2222  执行在testplugin这个module里

doFirst，doLast 才是执行在testplugin:hello这个task里面


task 定义好后，可以在后面添加action，doFirst表示往前添加action，doLast表示往后添加action。
```
/**先添加的doFirst动作后执行，先添加的doLast动作先执行*/
hello.doFirst { println "Second action" }
hello.doFirst { println "First action" }
hello.doLast { println "Not Last action" }
hello.doLast { println "Last action" }

```
执行task
```
> Task :testplugin:hello
First action
Second action
doFirst
doLast
Not Last action
Last action
```
task 写好后可以在gradle-->Tasks-->other 中看到这个自己写的task


## 自定义plugin

Gradle是采用Groovy语言，编写Gradle插件主要有三种方法：

1.build.gradle脚本中直接使用。这种方式就是直接在Android Studioapp moudle的build.gradle 中进行插件的编写，优点就是不用再上传插件到maven或者其它地方，项目就可以直接使用；缺点也是很明显，就是只能在自己的项目中使用，不能复用，这个不是我们今天要说的。

2.buildSrc中使用。这种方式需要在项目中新建一个model命名为buildSrc，这个目录就用来存放自定义插件。然后在src/main中建立两个目录，一个就是存放代码的groovy目录，一个是存放自定义插件名称的resources目录。这种定义方式也是只能在我们项目中进行使用，不好复用。我们今天要分享的是最灵活的方式，也就是第三点。

3.独立Module中使用。这种方式就是完全独立开发一个Module，可以随便用。


### 1.build.gradle脚本中使用

直接在module的build.gradle中编写plugin。
```

class Myplugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        //将自定义参数类和用户设置的参数进行绑定（将hencoder里面的参数映射到ExtensionDemo类的属性里面）
        //返回的值是一个ExtensionDemo类的实例，里面属性就是用户定义的hencoder的参数
        def extension = target.extensions.create('hencoder',ExtensionDemo)

        target.afterEvaluate {
            println "hellow ${extension.name}"
        }
    }
}

//自定义参数类
class ExtensionDemo{
    //设置默认值
    def name = 'Author'
}

//引入plugin
apply plugin: Myplugin

//自定义参数
hencoder{
    name '------liupengfei-------'
}
```
输出结果：

```
hellow ------liupengfei-------

```
### 2.buildSrc中使用

##### 步骤：
1.新建一个module，取名buildSrc

2.在main文件夹下新建resources --> META-INF --> gradle-plugins --> 新建文件 com.lpf.plugin.properties (文件名com.lpf.plugin于后面module中引入plugin所对应)

properties 文件里指明Gradle插件的具体实现类：
```
implementation-class=com.liupf.plugin.LpfPlugin

```
3.plugin的build.gradle

```
apply plugin: 'groovy'

dependencies {
    compile gradleApi()
    compile localGroovy()
}
```
4.plugin编写

Gradle插件本身用的是groovy语言，但是我们在开发过程中可以在groovy的文件中直接写Java语言，他们是可以自动转换的。

```
package com.liupf.plugin
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LpfPlugin implements Plugin<Project>{

    @Override
    void apply(Project target) {
        def extension = target.extensions.create('hencoder2',ExtensionDemo)
        target.afterEvaluate {
            println "hellow2 ${extension.name}"
        }
    }
}

//自定义参数类
package com.liupf.plugin

class ExtensionDemo{

    def name = '---刘鹏飞---'
}

```
5.将Java文件夹改成groovy文件夹，将Java文件改成groovy文件，无关文件删除

6.主module引用
```
apply plugin: 'com.lpf.plugin'
```
### 3.独立Module中使用

和buildSrc相比更加方便灵活以及不会有导包问题。


##### 步骤：

1.和buildSrc类似，创建一个module，名字可以随便取,写好plugin。

build.gradle文件中添加上传仓库的task

```
apply plugin: 'groovy'

//引入Maven仓库，为后面plugin上传做准备
apply plugin: 'maven'

dependencies {
    compile gradleApi() //gradle sdk
    compile localGroovy() //groovy sdk
    compile 'com.android.tools.build:gradle:3.4.1'
    compile 'org.javassist:javassist:3.20.0-GA'
}

repositories {
    jcenter()
}

//plugin上传本地仓库
uploadArchives {
    repositories.mavenDeployer {

//提交到远程服务器：
//        repository(url: "http://www.xxx.com/repos") {
//            authentication(userName: "admin", password: "admin")
//        }
        //提交本地仓库
        repository(url: uri('../repo')) //仓库的路径，此处是项目根目录下的 repo 的文件夹
        pom.groupId = 'com.modify.plugin'  //groupId ，自行定义，一般是包名
        pom.artifactId = 'modify' //artifactId ，自行定义
        pom.version = '1.0.0' //version 版本号
    }
}

```
2.在gradle中执行uploadArchives任务，在主目录生成repo文件夹，里面就是我们编写的plugin。


3.在项目build.gradle中引入仓库
```
buildscript {

    repositories {
        google()
        jcenter()
        //引入本地仓库
        maven {
            url uri('repo')
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.4.1'
        //引入自定义仓库
        classpath 'com.modify.plugin:modify:1.0.0'
    }
}

```
4.主module引用
```
apply plugin: 'com.modify.plugin'
```

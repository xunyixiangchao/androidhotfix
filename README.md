# androidhotfix  安卓热修复实现

## 项目结构

- app
  - 测试Demo
- anbuildSrc
  - 插件源码
- patch-hook
  - 用于插桩进入所有工程代码的类
- patch-lib
  - 执行热修复的依赖库

**生成hook.jar包方式：**

使用gradle build，进入build-classes-java-main目录下，使用命令 dx(需要配置全局变量里）  dx --dex --output=hook.jar com\lis\patch\
hack\AntiLazyLoad.class

**切记一定使用dx命令生成，而不是编译自己生成的jar包，因为编译的jar包里是.class文件，dx命令生成的是.dex文件**

## 使用方法

先直接运行一次app，然后打开权限。修改app中的`Utils`类，然后执行：**Build-make Module 'app'**生成补丁。

> 没用代码加入读写权限，请自行到设置中加入权限。真实项目中，补丁可以从服务器下载后放入程序私有目录，如: `context.getFilesDir()`，私有目录无需权限。

![生成补丁](https://github.com/xunyixiangchao/androidhotfix/blob/master/img/%E7%94%9F%E6%88%90%E8%A1%A5%E4%B8%81.png)

执行完成后，默认在`app/build/patch/`下生成**patch.jar**，这个jar包就是我们的补丁包。测试时候，可以使用`adb push patch.jar /sdcard/` 命令把补丁放入sdcard目录下。然后重启app，即可看到执行修改之后的效果。

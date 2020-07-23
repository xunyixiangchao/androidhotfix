# androidhotfix  安卓热修复实现

## 项目结构

- app
  - 测试Demo
- anbuildSrc
  - 插件源码
- patch-hook
  - 用于插桩进入所有工程代码的类
- patch-lib
  - todo:执行热修复的依赖库


## 使用方法

先直接运行一次app，然后打开权限。修改app中的`Utils`类，然后执行：**Build-make Module 'app'**生成补丁。

> 没用代码加入读写权限，请自行到设置中加入权限。真实项目中，补丁可以从服务器下载后放入程序私有目录，如: `context.getFilesDir()`，私有目录无需权限。

![生成补丁](img\生成补丁.png)

执行完成后，默认在`app/build/patch/`下生成**patch.jar**，这个jar包就是我们的补丁包。测试时候，可以使用`adb push patch.jar /sdcard/` 命令把补丁放入sdcard目录下。然后重启app，即可看到执行修改之后的效果。

package com.lis.hotfix;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class NewClassLoaderInjector {
    public static ClassLoader inject(Application app, ClassLoader oldClassLoader) throws Throwable {
        ClassLoader newClassLoader = createNewClassLoader(app, oldClassLoader, oldClassLoader.getParent());
        doInject(app, newClassLoader);
        return newClassLoader;
    }

    private static ClassLoader createNewClassLoader(Context context, ClassLoader oldClassLoader,
                                                    ClassLoader oldParentClassLoader) throws Throwable {
        //获得pathList
        Field pathListField = ShareReflectUtil.findField(oldClassLoader, "pathList");
        Object oldPathList = pathListField.get(oldClassLoader);

        //dexElements
        Field dexElementsField = ShareReflectUtil.findField(oldPathList, "dexElements");
        Object[] oldDexElements = (Object[]) dexElementsField.get(oldPathList);

        //从Element上得到 dexFile
        Field dexFileField = ShareReflectUtil.findField(oldDexElements[0], "dexFile");

        // 获得原始的dexPath用于构造classloader
        StringBuilder dexPathBuilder = new StringBuilder();
        String packageName = context.getPackageName();
        boolean isFirstItem = true;
        for (Object oldDexElement : oldDexElements) {
            String dexPath = null;
            DexFile dexFile = (DexFile) dexFileField.get(oldDexElement);
            if (dexFile != null) {
                dexPath = dexFile.getName();
            }
            if (dexPath == null || dexPath.isEmpty()) {
                continue;
            }
            if (!dexPath.contains("/" + packageName)) {
                continue;
            }
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                dexPathBuilder.append(File.pathSeparator);
            }
            dexPathBuilder.append(dexPath);
        }

        final String combinedDexPath = dexPathBuilder.toString();

        //app的native库（so）文件目录，用于构造classloader
        Field nativeLibraryDirectories = ShareReflectUtil.findField(oldPathList, "nativeLibraryDirectories");
        List<File> oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectories.get(oldPathList);

        StringBuilder libraryPathBuilder = new StringBuilder();
        isFirstItem = true;
        for (File libDir : oldNativeLibraryDirectories) {
            if (libDir == null) {
                continue;
            }
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                libraryPathBuilder.append(File.pathSeparator);
            }
            libraryPathBuilder.append(libDir.getAbsolutePath());
        }
        String combinedLibraryPath = libraryPathBuilder.toString();
        //父类加载器不能是 oldClassLoader
        ClassLoader result = new PathClassLoader(combinedDexPath, combinedLibraryPath, oldParentClassLoader);
        return result;

    }

    /**
     * 将系统的ClassLoader替换，主线程，Resources和DrawableInflater中的ClassLoader
     *
     * @param app
     * @param newClassLoader
     * @throws Throwable
     */
    private static void doInject(Application app, ClassLoader newClassLoader) throws Throwable {
        Thread.currentThread().setContextClassLoader(newClassLoader);

        Context baseContext = (Context) ShareReflectUtil.findField(app, "mBase").get(app);
        Object basePackageInfo = ShareReflectUtil.findField(baseContext, "mPackageInfo").get(baseContext);
        ShareReflectUtil.findField(basePackageInfo, "mClassLoader").set(basePackageInfo, newClassLoader);

        if (Build.VERSION.SDK_INT < 27) {
            Resources res = app.getResources();
            try {
                ShareReflectUtil.findField(res, "mClassLoader").set(res, newClassLoader);

                final Object drawableInflater = ShareReflectUtil.findField(res, "mDrawableInflater").get(res);
                if (drawableInflater != null) {
                    ShareReflectUtil.findField(drawableInflater, "mClassLoader").set(drawableInflater, newClassLoader);
                }
            } catch (Throwable ignored) {
                // Ignored.
            }
        }
    }
}

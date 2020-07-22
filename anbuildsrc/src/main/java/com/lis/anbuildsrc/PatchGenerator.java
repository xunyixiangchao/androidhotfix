package com.lis.anbuildsrc;


import com.android.build.gradle.AppExtension;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author Lance
 * @date 2019-09-23
 */
public class PatchGenerator {

    private File patchFile;
    private String buildToolsVersion;
    private Project project;
    private File jarFile;
    private Map<String, String> oldHexs;
    private JarOutputStream jarOutputStream;

    public PatchGenerator(Project project, File patchFile, File jarFile,
                          File hexFile) {
        this.project = project;
        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        buildToolsVersion = android.getBuildToolsVersion();
        this.patchFile = patchFile;
        this.jarFile = jarFile;
        if (hexFile.exists()) {
            oldHexs = Utils.readHex(hexFile);
        }

    }

    public void checkClass(String className, String hex, byte[] byteCode) {
        if (Utils.isEmpty(oldHexs)) {
            return;
        }
        String oldHex = oldHexs.get(className);
        if (oldHex == null || !oldHex.equals(hex)) {
            JarOutputStream output = getOutput();
            try {
                output.putNextEntry(new JarEntry(className));
                output.write(byteCode);
                output.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private JarOutputStream getOutput() {
        if (jarOutputStream == null) {
            try {
                jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jarOutputStream;
    }

    public void generate() throws Exception {
        if (!jarFile.exists()) {
            return;
        }
        JarOutputStream output = getOutput();
        output.close();
        Properties properties = new Properties();
        File localProps = project.getRootProject().file("local.properties");
        String sdkDir;
        if (localProps.exists()) {
            properties.load(new FileInputStream(localProps));
            sdkDir = properties.getProperty("sdk.dir");
        } else {
            sdkDir = System.getenv("ANDROID_HOME");
        }
        String cmdExt = Os.isFamily(Os.FAMILY_WINDOWS) ? ".bat" : "";
        String dxPath = sdkDir + "/build-tools/" + buildToolsVersion +
                "/dx" + cmdExt;
        String patch = "--output=" + patchFile.getAbsolutePath();
        String cmd = dxPath + " --dex " + patch + " " + jarFile.getAbsolutePath();
        Process process =
                Runtime.getRuntime().exec(cmd);
        process.waitFor();
        jarFile.delete();
        if (process.exitValue() != 0) {
            throw new IOException("generate patch error:" + cmd);
        }

        project.getLogger().error("patch generated in : " + patchFile);
    }
}

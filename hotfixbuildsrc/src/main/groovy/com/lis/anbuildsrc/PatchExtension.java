package com.lis.anbuildsrc;

/**
 * Created by lis on 2020/7/22.
 */
public class PatchExtension {

    public boolean isDebugOn() {
        return debugOn;
    }

    public String getOutput() {
        return output;
    }

    public String getApplicationName() {
        return applicationName;
    }

    boolean debugOn;
    String output;
    String applicationName;


    public PatchExtension() {
        debugOn = false;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}

package com.smart32.sonycamstereoenabler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SonyCameraHook implements IXposedHookLoadPackage {

    private static final String TAG = "SonyCameraHook";
    // Set to true to enable Xposed logs
    private static final boolean DEBUG = false;

    private static final String MEDIA_RECORDER_CLASS = "com.sonymobile.android.media.MediaRecorder";
    private static final String AUDIO_RECORD_CLASS = "android.media.AudioRecord";

    private static class TargetConfiguration {
        String pkgBase;
        String micKeySource;
        String proSettingClass;
        String[] validCallSources;

        public TargetConfiguration(String pkgBase, String micKeySource, String proSettingClass, String[] validCallSources) {
            this.pkgBase = pkgBase;
            this.micKeySource = micKeySource;
            this.proSettingClass = proSettingClass;
            this.validCallSources = validCallSources;
        }
    }

    private static final TargetConfiguration[] targets = {
            new TargetConfiguration(
                    "jp.co.sony.mc.videopro",
                    ".setting.CommonSettings",
                    ".setting.CameraProSetting",
                    new String[]{
                            ".view.widget.AudioLevelWidget",
                            ".recorder"
                    }
            ),
            new TargetConfiguration(
                    "jp.co.sony.mc.camera",
                    ".setting.CameraSettings",
                    ".setting.CameraProSetting",
                    new String[]{
                            ".recorder.AudioLevelMonitor"
                    }
            ),
            new TargetConfiguration(
                    "com.sonymobile.cinemapro",
                    "=LR",
                    "",
                    new String[]{
                            ".view.widget.AudioLevelWidget",
                            ".recorder"
                    }
            ),
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Use startsWith to support modded package names
        TargetConfiguration targetConfig = Arrays.stream(targets)
                .filter(config -> lpparam.packageName.startsWith(config.pkgBase))
                .findFirst()
                .orElse(null);

        if (targetConfig == null) {
            return;
        }

        if (DEBUG) XposedBridge.log(TAG + ": Loaded target package: " + lpparam.packageName);

        // 1. Hook setAudioSource
        XposedHelpers.findAndHookMethod(MEDIA_RECORDER_CLASS, lpparam.classLoader, "setAudioSource", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (DEBUG) XposedBridge.log(TAG + ": setAudioSource original: " + param.args[0]);

                String micName = getCurrentMicSetting(lpparam.classLoader, targetConfig);

                if ("LR".equals(micName)) {
                    param.args[0] = 1; // Stereo
                    if (DEBUG) XposedBridge.log(TAG + ": Force Stereo (1) for LR");
                } else {
                    param.args[0] = 5; // Mono/rear
                    if (DEBUG) XposedBridge.log(TAG + ": Force Mono (5) for " + micName);
                }
            }
        });

        // 2. Hook AudioRecord constructor (level indicator)
        XposedHelpers.findAndHookConstructor(AUDIO_RECORD_CLASS, lpparam.classLoader,
                int.class, int.class, int.class, int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // Check call stack to ensure we only hook inside camera context
                        if (!isCalledFromCamera(Thread.currentThread().getStackTrace(), targetConfig)) {
                            return;
                        }

                        if (DEBUG)
                            XposedBridge.log(TAG + ": AudioRecord hook. Original: " + param.args[0]);

                        String micName = getCurrentMicSetting(lpparam.classLoader, targetConfig);
                        if ("LR".equals(micName)) {
                            param.args[0] = 1;
                            if (DEBUG)
                                XposedBridge.log(TAG + ": Preview forced to Stereo (1) for LR");
                        } else {
                            param.args[0] = 5;
                            if (DEBUG)
                                XposedBridge.log(TAG + ": Preview forced to Mono (5) for " + micName);
                        }
                    }
                });
    }

    // Helper method to get the current MIC setting value from the app via reflection.
    private String getCurrentMicSetting(ClassLoader classLoader, TargetConfiguration config) {
        if (config.micKeySource.startsWith("=")) {
            return config.micKeySource.substring(1);
        }

        try {
            String micSettingsClass = config.pkgBase + config.micKeySource;
            Class<?> micSettingsClazz = XposedHelpers.findClass(micSettingsClass, classLoader);
            Object micKey = XposedHelpers.getStaticObjectField(micSettingsClazz, "MIC");
            Class<?> proSettingClazz = XposedHelpers.findClass(config.pkgBase + config.proSettingClass, classLoader);
            Object settingInstance = XposedHelpers.callStaticMethod(proSettingClazz, "getInstance");
            Object micValue = XposedHelpers.callMethod(settingInstance, "get", micKey);
            return (micValue != null) ? micValue.toString() : "UNKNOWN";
        } catch (Throwable t) {
            if (DEBUG) XposedBridge.log(TAG + ": Error getting mic setting: " + t.getMessage());
            return "ERROR";
        }
    }

    // Helper method to check if the execution flow comes from Camera/VideoPro specific classes.
    private boolean isCalledFromCamera(StackTraceElement[] stack, TargetConfiguration config) {
        List<String> validCallSources = Arrays.stream(config.validCallSources)
                .map(source -> config.pkgBase + source)
                .collect(Collectors.toList());

        // Check if any element in the stack trace comes from a valid class.
        return Arrays.stream(stack)
                .anyMatch(element -> validCallSources.stream()
                        .anyMatch(element.getClassName()::contains));
    }
}


# Sony Camera Stereo Enabler for Xperia Devices

**Sony Camera Stereo Enabler** is an Xposed module designed to restore stereo audio recording capabilities in official Sony Camera apps when running on custom ROMs (such as LineageOS, crDroid, EvolutionX, etc.) on Sony Xperia devices.

## Features
* **Restores Stereo Recording:** Enables true stereo audio capture in "All Directions" mode.
* **UI Fixes:** Ensures input level (VU) meters display correctly where applicable.
* **Smart Switching:** Maintains the ability to toggle between "Voice Priority" (Mono/Rear) and "All Directions" (Stereo) within the camera settings.
* **Universal Compatibility:** Supports **Camera**, **Video Pro** and **Cinema Pro** apps including various modded versions and clones (package names starting with `jp.co.sony.mc.camera`, `jp.co.sony.mc.videopro` and `com.sonymobile.cinemapro`).

## When to Use This Module
You should use this module only if you are experiencing a bug where the camera app records mono sound from the rear microphone regardless of the settings chosen in the UI. This is a common issue on AOSP-based ROMs where the Sony hardware abstraction layer (HAL) and AOSP audio routing are not handled correctly by the app's standard logic.

## Common Unsolved Issues
* **External Microphones:** Audio recording via the 3.5mm jack is currently not working. This is a common limitation of custom ROMs for Xperia devices and is not caused by this module.
* **4K @ 60 FPS in Camera "Basic" Mode:** Video recording at 4K 60 FPS is not functional in the **Camera** app when using the **Basic video mode**. This is an app/ROM compatibility issue and is not caused by this module either. However, 4K 60 FPS works perfectly fine in the **Video Pro** mode (both in the standalone Video Pro app and the Video Pro mode integrated within the Camera app) and in the **Cinema Pro** app.
* **Rear Mic Noise:** In "Voice Priority" mode, the recording might contain audible noise from the Optical Image Stabilization (OIS) system. This is a hardware design characteristic of Xperia devices.

**Note:** While I cannot fix the hardware-induced OIS noise, I may investigate the first two issues (External Mic and 4K60 Basic Mode) in the future if there is community interest.

## Installation
1. Ensure you have **Magisk** (or KernelSU/APatch) and the **LSPosed Framework** installed.
2. Download and install the module's APK.
3. Open the LSPosed Manager and **activate** the module.
4. Select your **Sony Camera** app (Camera or Video Pro) in the module's scope.

## Compatibility
Theoretically compatible with all Xperia devices on custom ROMs where the stereo recording bug is present.

## Feedback and Contributions
If you encounter a bug or have a suggestion, please open an issue!
* **[Report a Bug](https://github.com/SmArT-32-projects/SonyCamStereoEnabler/issues/new?template=bug_report.md)**

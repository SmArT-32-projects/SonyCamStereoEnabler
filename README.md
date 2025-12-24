# Sony Camera Stereo Enabler for Xperia Devices

**Sony Camera Stereo Enabler** is an Xposed module designed to restore stereo audio recording capabilities in official Sony Camera apps when running on custom ROMs (such as LineageOS, crDroid, EvolutionX, etc.) on Sony Xperia devices.

## Features
* **Restores Stereo Recording:** Enables true stereo audio capture in "All Directions" mode.
* **UI Fixes:** Ensures input level meters (VU meters) display correctly in apps where applicable.
* **Smart Switching:** Maintains the ability to toggle between "Voice Priority" (Mono/Rear) and "All Directions" (Stereo) within the camera settings.
* **Universal Compatibility:** Supports **Camera** and **Video Pro** apps including various modded versions and clones (package names starting with `jp.co.sony.mc.camera` and `jp.co.sony.mc.videopro`).

## When to Use This Module
You should use this module only if you are experiencing a bug where the camera app records mono sound from the rear microphone regardless of the settings chosen in the UI. This is a common issue on AOSP-based ROMs where the Sony hardware abstraction layer (HAL) is not addressed correctly by the app's standard calls.

## Known Issues
* **External Microphones:** Audio recording via the 3.5mm jack is currently not working. This is a known limitation of custom ROMs for Xperia devices and is not caused by this module.
* **Rear Mic Noise:** In "Voice Priority" mode, the recording might contain audible noise from the Optical Image Stabilization (OIS) system. This is a hardware design characteristic of Xperia devices; on stock ROMs, this is usually filtered out by proprietary Sony software processing which is absent on custom ROMs.

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

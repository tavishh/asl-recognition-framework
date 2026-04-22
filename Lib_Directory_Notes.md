# lib/ Directory - OpenCV Setup

## Current Status

Your project includes the OpenCV native library files locally:
- `lib/libopencv_java4130.dylib` (macOS native library)
- `lib/opencv-4130.jar` (Java bindings JAR)

## Important: .gitignore

**CRITICAL:** Do NOT commit the native library files to GitHub.

These files should be in your `.gitignore`:

```
# OpenCV native library (local system dependency)
/lib/libopencv_java4130.dylib
/lib/opencv-4130.jar
```

They are:
- Platform-specific (won't work on Windows/Linux)
- Large binary files (not suitable for version control)
- Available locally on each developer's machine

## Setup Instructions for New Developers

If someone clones your repo and needs to build it:

### On macOS
1. Download OpenCV 4.13.0 from https://opencv.org/releases/
2. Extract and navigate to: `opencv-python/opencv/build/lib/`
3. Copy `libopencv_java4130.dylib` to your project's `lib/` folder
4. Copy `opencv-java.jar` (rename to `opencv-4130.jar`) to `lib/`
5. Run `mvn clean compile`

### On Linux
1. Install via package manager: `sudo apt-get install libopencv-dev`
2. Find library path: `find /usr -name "libopencv_core.so*"`
3. Copy or symlink to `lib/` folder
4. Run `mvn clean compile`

### On Windows
1. Download OpenCV 4.13.0 Windows release
2. Build with CMake (or use pre-built binaries)
3. Copy `opencv_java4130.dll` and `opencv-4130.jar` to `lib/`
4. Run `mvn clean compile`

## Maven Configuration

Your `pom.xml` references the local OpenCV JAR:

```xml
<dependency>
  <groupId>org.opencv</groupId>
  <artifactId>opencv</artifactId>
  <version>4.13.0</version>
  <scope>system</scope>
  <systemPath>${project.basedir}/lib/opencv-4130.jar</systemPath>
</dependency>
```

And loads the native library at runtime:

```java
// In Main.java
ConfigLoader config = new ConfigLoader();
CameraService.loadNativeLibrary(config.getOpenCvLibPath());
```

## Current Git Status

Check what's currently committed:

```bash
git ls-files lib/
```

If the JAR or DLL are listed, remove them:

```bash
git rm --cached lib/libopencv_java4130.dylib
git rm --cached lib/opencv-4130.jar
git commit -m "Remove binary OpenCV files from version control"
```

## Summary

✓ Keep the `lib/` directory in your repo (it's referenced by pom.xml)  
✓ Keep the `.gitignore` file (prevents accidental commits)  
✗ Do NOT commit the binary library files themselves  
✓ Each developer sets up OpenCV locally on their machine

This is the standard approach for platform-specific native dependencies.
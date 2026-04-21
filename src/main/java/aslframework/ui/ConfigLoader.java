package aslframework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads local configuration from {@code config.properties} at the project root.
 *
 * <p>Only {@code camera.url} and {@code python.path} need to be configured per machine.
 * All other paths are resolved relative to the project root automatically.
 */
public class ConfigLoader {

  private static final String CONFIG_FILE = "config.properties";
  private final Properties props = new Properties();

  /**
   * Constructs a {@code ConfigLoader} and reads {@code config.properties}
   * from the current working directory if it exists.
   * The file is optional — only needed if camera.url or python.path is used.
   */
  public ConfigLoader() {
    File file = new File(CONFIG_FILE);
    if (file.exists()) {
      try (FileInputStream fis = new FileInputStream(file)) {
        props.load(fis);
      } catch (IOException e) {
        System.err.println("Warning: could not read config.properties: " + e.getMessage());
      }
    }
  }

  /**
   * Returns the absolute path to the OpenCV native library.
   * Resolved from lib/ in the project root.
   *
   * @return absolute path to libopencv_java4130.dylib
   */
  public String getOpenCvLibPath() {
    return new File(System.getProperty("user.dir"), "lib/libopencv_java4130.dylib")
        .getAbsolutePath();
  }

  /**
   * Returns the absolute path to the instruction video directory.
   * Resolved from assets/guidance/ in the project root.
   *
   * @return absolute path to the guidance video folder
   */
  public String getVideoDir() {
    return new File(System.getProperty("user.dir"), "assets/guidance")
        .getAbsolutePath();
  }

  /**
   * Returns the camera URL for IP camera streaming, or null if not set.
   * When null, the default webcam (index 0) will be used.
   *
   * @return value of {@code camera.url}, or null if not configured
   */
  public String getCameraUrl() {
    String value = props.getProperty("camera.url");
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }

  /**
   * Returns the path to the Python executable, or null if not configured.
   * When null, the system python3 will be used automatically.
   *
   * @return value of {@code python.path}, or null if not set
   */
  public String getPythonPath() {
    String value = props.getProperty("python.path");
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }
}
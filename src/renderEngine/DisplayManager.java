package renderEngine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import javax.vecmath.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class DisplayManager {

  private static GLFWErrorCallback errorCallback;
  // The window handle
  public static long windowID;
  public static final String GAME_NAME = "Pigeons vs People";
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;
  private static final int FPS_CAP = 120;

  private static double lastFrameTime;
  private static double delta;

  private GLFWScrollCallback scrollCallback;
  private static Vector2d deltaMouseWheel = new Vector2d(0, 0);
  private static Vector2d prevMouseWheel = new Vector2d(0, 0);
  private static Vector2d currentMouseWheel = new Vector2d(0, 0);

  private static Vector2d deltaMousePos = new Vector2d(0, 0);
  private static Vector2d prevMousePos = new Vector2d(0, 0);
  private static Vector2d currMousePos = new Vector2d(0, 0);

  public static Vector2d getPrevMouseWheel() {
    return prevMouseWheel;
  }

  public static void setPrevMouseWheel(Vector2d prevMouseWheel) {
    DisplayManager.prevMouseWheel = prevMouseWheel;
  }

  public static Vector2d getDeltaMousePos() {
    return deltaMousePos;
  }

  public static void setDeltaMousePos(Vector2d deltaMousePos) {
    DisplayManager.deltaMousePos = deltaMousePos;
  }

  public static Vector2d getPrevMousePos() {
    return prevMousePos;
  }

  public static void setPrevMousePos(Vector2d prevMousePos) {
    DisplayManager.prevMousePos = prevMousePos;
  }

  public static Vector2d getCurrMousePos() {
    return currMousePos;
  }

  public static void setCurrMousePos(Vector2d currMousePos) {
    DisplayManager.currMousePos = currMousePos;
  }

  public static Vector2d getCurrentMouseWheel() {
    return currentMouseWheel;
  }

  public static void setCurrentMouseWheel(Vector2d currentMouseWheel) {
    DisplayManager.currentMouseWheel = currentMouseWheel;
  }

  public static Vector2d getDeltaMouseWheel() {
    return deltaMouseWheel;
  }

  public static void setDeltaMouseWheel(Vector2d deltaMouseWheel) {
    DisplayManager.deltaMouseWheel = deltaMouseWheel;
  }

  public static void createDisplay() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    // Create the window
    windowID = glfwCreateWindow(WIDTH, HEIGHT, GAME_NAME, MemoryUtil.NULL, MemoryUtil.NULL);
    if (windowID == MemoryUtil.NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
    });

    glfwSetScrollCallback(windowID, (long win, double dx, double dy) -> {
      setCurrentMouseWheel(new Vector2d(dx, dy));
    });

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*
      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(windowID, pWidth, pHeight);
      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      // Center the window
      glfwSetWindowPos(
          windowID,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2
      );
    } // the stack frame is popped automatically

    glfwMakeContextCurrent(windowID);// Make the OpenGL context current
    glfwSwapInterval(1);// Enable v-sync 60fps - 1 // 0 = off, 1 = on
    glfwShowWindow(windowID);// Make the window visible
    lastFrameTime = getCurrentTime();
    prevMousePos = getMousePos();
  }

  private static Vector2d getMousePos() {
    DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
    DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
    glfwGetCursorPos(windowID, posX, posY);
    return new Vector2d(posX.get(0), posY.get(0));
  }

  public static void updateDisplay() {
    double currentFrameTime = getCurrentTime();
    delta = (currentFrameTime - lastFrameTime) / 1000f;//seconds
    lastFrameTime = currentFrameTime;

    currMousePos = getMousePos();
    deltaMousePos = new Vector2d(currMousePos.x - prevMousePos.x, currMousePos.y - prevMousePos.y);
    prevMousePos = currMousePos;
  }

  public static double getFrameTimeSeconds() {
    return delta;
  }

  public static void closeDisplay() {
    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(windowID);
    glfwDestroyWindow(windowID);
    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  public static long getWindowID() {
    return windowID;
  }

  //mseconds
  private static double getCurrentTime() {
    return GLFW.glfwGetTime() * 1000;// /Sys.getTimerResolution();
  }

}

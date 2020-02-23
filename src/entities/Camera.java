package entities;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjglx.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Camera {

  private GLFWKeyCallback keyCallback;
  private Vector3f position = new Vector3f(0, 1, 0);
  //rotation
  private float pitch; //how high or low the camera is aimed
  private float yaw;   //how much camera is left or right
  private float roll;

  public Camera() {
  }

  public void move() {
    if (glfwGetKey(DisplayManager.getWindowID(), GLFW_KEY_W) == 1) {
      position.z -= 0.1f;
    }
    if (glfwGetKey(DisplayManager.getWindowID(), GLFW_KEY_D) == 1) {
      position.x += 0.11f;
    }
    if (glfwGetKey(DisplayManager.getWindowID(), GLFW_KEY_A) == 1) {
      position.x -= 0.1f;
    }
    if (glfwGetKey(DisplayManager.getWindowID(), GLFW_KEY_S) == 1) {
      position.z += 0.1f;
    }
  }

  public Vector3f getPosition() {
    return position;
  }

  public float getPitch() {
    return pitch;
  }

  public float getYaw() {
    return yaw;
  }

  public float getRoll() {
    return roll;
  }
}

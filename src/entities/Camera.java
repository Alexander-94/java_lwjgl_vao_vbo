package entities;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

import org.lwjglx.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Camera {

  private float distanceFromPlayer = 50;
  private float angleAroundPlayer = 0;

  private Vector3f position = new Vector3f(0, 0, 0);
  private float pitch = 20; //how high or low the camera is aimed
  private float yaw = 0;   //how much camera is left or right
  private float roll;
  //private GLFWKeyCallback keyCallback;


  private Player player;

  public Camera(Player player) {
    this.player = player;
  }

  public void move() {
    calculateZoom();
    calculatePitch();
    calculateAngleAroundPlayer();
    float horizontalDistance = calculateHorizontalDistance();
    float verticalDistance = calculateVerticalDistance();
    calculateCameraPosition(horizontalDistance, verticalDistance);
    this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
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

  private void calculateCameraPosition(float horizDistance, float verticDistance) {
    float theta = player.getRotY() + angleAroundPlayer;
    float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
    float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
    position.x = player.getPosition().x - offsetX;
    position.z = player.getPosition().z - offsetZ;
    position.y = player.getPosition().y + verticDistance;
  }

  private float calculateHorizontalDistance() {
    return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
  }

  private float calculateVerticalDistance() {
    return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
  }

  private void calculateZoom() {
    //float zoomLevel = Mouse.getDWheel() * 0.1f;
    System.out.println("sdfdsf" + DisplayManager.getdMouseWheel().y);
    float zoomLevel = DisplayManager.getdMouseWheel().y * 0.1f;
    distanceFromPlayer -= zoomLevel;
  }

  private void calculatePitch() {
    if (glfwGetMouseButton(DisplayManager.getWindowID(), 1) == 1) {//right button
      float pitchChange = (float) (DisplayManager.getDeltaMousePos().y) * 0.1f;
      pitch -= pitchChange;
    }
  }

  private void calculateAngleAroundPlayer() {
    if (glfwGetMouseButton(DisplayManager.getWindowID(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
      float angleChange = (float) (DisplayManager.getDeltaMousePos().x) * 0.3f;
      angleAroundPlayer -= angleChange;
    }
  }


}

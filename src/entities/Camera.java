package entities;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import renderEngine.DisplayManager;

public class Camera {

  private float distanceFromPlayer = 50;
  private float angleAroundPlayer = 0;

  private Vector3d position = new Vector3d(0, 0, 0);
  private double pitch = 20; //how high or low the camera is aimed
  private double yaw = 0;   //how much camera is left or right
  private double roll;
  //private GLFWKeyCallback keyCallback;


  private Player player;

  public Camera(Player player) {
    this.player = player;
  }

  public void move() {
    calculateZoom();
    calculatePitch();
    calculateAngleAroundPlayer();
    double horizontalDistance = calculateHorizontalDistance();
    double verticalDistance = calculateVerticalDistance();
    calculateCameraPosition(horizontalDistance, verticalDistance);
    this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    DisplayManager.setCurrentMouseWheel(new Vector2d(0, 0));
  }

  public Vector3d getPosition() {
    return position;
  }

  public double getPitch() {
    return pitch;
  }

  public double getYaw() {
    return yaw;
  }

  public double getRoll() {
    return roll;
  }

  private void calculateCameraPosition(double horizDistance, double verticDistance) {
    double theta = player.getRotY() + angleAroundPlayer;
    double offsetX = horizDistance * Math.sin(Math.toRadians(theta));
    double offsetZ = horizDistance * Math.cos(Math.toRadians(theta));
    position.x = player.getPosition().x - offsetX;
    position.z = player.getPosition().z - offsetZ;
    position.y = player.getPosition().y + verticDistance;
  }

  private double calculateHorizontalDistance() {
    return distanceFromPlayer * Math.cos(Math.toRadians(pitch));
  }

  private double calculateVerticalDistance() {
    return distanceFromPlayer * Math.sin(Math.toRadians(pitch));
  }

  private void calculateZoom() {
    double zoomLevel = DisplayManager.getCurrentMouseWheel().y / 0.5;
    System.out.println("current wheel = " + DisplayManager.getCurrentMouseWheel().y);
    distanceFromPlayer -= zoomLevel;
  }

  private void calculatePitch() {
    if (glfwGetMouseButton(DisplayManager.getWindowID(), 1) == 1) {//right button
      double pitchChange = DisplayManager.getDeltaMousePos().y * 0.1f;
      pitch -= pitchChange;
    }
  }

  private void calculateAngleAroundPlayer() {
    if (glfwGetMouseButton(DisplayManager.getWindowID(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
      double angleChange = DisplayManager.getDeltaMousePos().x * 0.3f;
      angleAroundPlayer -= angleChange;
    }
  }


}

package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.TexturedModel;
import shaders.StaticShader;

//handle all of the rendering code in the game
public class MaterRenderer {

  private StaticShader shader = new StaticShader();
  private EntityRenderer entityRenderer = new EntityRenderer(shader);

  private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

  //once every frame
  public void render(Light sun, Camera camera) {
    entityRenderer.prepare();
    shader.start();
    shader.loadLight(sun);
    shader.loadViewMatrix(camera);
    entityRenderer.render(entities);
    shader.stop();
    entities.clear();
  }

  public void processEntity(Entity entity) {
    TexturedModel entityModel = entity.getModel();
    List<Entity> batch = entities.get(entityModel);
    if (batch != null) {
      batch.add(entity);
    } else {
      List<Entity> newBatch = new ArrayList<Entity>();
      newBatch.add(entity);
      entities.put(entityModel, newBatch);
    }
  }

  public void cleanUp() {
    shader.cleanUp();
  }

}

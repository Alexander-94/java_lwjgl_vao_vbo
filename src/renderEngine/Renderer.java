package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import shaders.StaticShader;
import toolbox.Maths;

public class Renderer {

  public void prepare() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);//clear the color from previous frame
    GL11.glClearColor(1, 0, 0, 1);
  }

  public void render(Entity entity, StaticShader shader) {
    TexturedModel texturedModel = entity.getModel();
    RawModel model = texturedModel.getRawModel();
    GL30.glBindVertexArray(model.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    //load up the entities transformations(positions, rotations and scale to the vertex shader
    Matrix4f transformationMatrix = Maths
        .createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(),
            entity.getRotZ(), entity.getScale());
    shader.loadTransformationMatrix(transformationMatrix);
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_PROXY_TEXTURE_2D, texturedModel.getTexture().getID());
    //GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL30.glBindVertexArray(0);
  }

}

package net.cazzar.mods.voxelplayers.bodyshop;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class EntityVoxel extends Entity {
    float[] vertex_data_array;
    public void setupVBO() {
        vertex_data_array = new float[]{
                // x       y      z     nx     ny     nz    r   g   b   a
                // back quad
                   0f,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,

                // front quad
                   0f,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,

                // left quad
                   0f,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,

                // right quad
                xSize,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,

                // top quad
                   0f,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,     0f,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,     0f, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,

                // bottom quad
                   0f,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                   0f,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize,    0f,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
                xSize,  ySize, zSize,  0.0f,  1.0f,  0.0f,  r,  g,  b,  1.0f,
        };

        FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vertex_data_array.length);
        vertex_buffer_data.put(vertex_data_array);
        vertex_buffer_data.rewind();

//        IntBuffer buffer = BufferUtils.createIntBuffer(1);
//        glGenBuffers(buffer);
//        vboId = buffer.get(0);

        vboId = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertex_buffer_data, GL_STATIC_DRAW);

//        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    @Override
    public void render() {
        glPushMatrix();

        glTranslatef(x, y, z);
//        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        glVertexPointer(3, GL_FLOAT, 40, 0);
        glNormalPointer(GL_FLOAT, 40, 12);
        glColorPointer(4, GL_FLOAT, 40, 24);

        glDrawArrays(GL_POINTS, 0, vertex_data_array.length / 10);

//        System.out.println("Render!");
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glPopMatrix();
    }
}

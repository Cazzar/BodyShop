package net.cazzar.mods.voxelplayers.bodyshop;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class EntityVoxel extends Entity {
    int[] vboId = new int[2];
    int f = 0;
    public void setupVBO() {
        FloatBuffer buffer = Application.asFloatBuffer(
                //top
                0f,    0f,    0f,    r, g, b, 0f,  ySize, 0f,
                xSize, 0f,    0f,    r, g, b, 0f,  ySize, 0f,
                xSize, 0f,    zSize, r, g, b, 0f,  ySize, 0f,
                0f,    0f,    zSize, r, g, b, 0f,  ySize, 0f,

                //bottom
                0f,    ySize, 0f,    r, g, b, 0f, -ySize, 0f,
                xSize, ySize, 0f,    r, g, b, 0f, -ySize, 0f,
                xSize, ySize, zSize, r, g, b, 0f, -ySize, 0f,
                0f,    ySize, zSize, r, g, b, 0f, -ySize, 0f,

                //left
                0f,    0f,    0f,    r, g, b, -xSize, 0f, 0f,
                0f,    ySize, 0f,    r, g, b, -xSize, 0f, 0f,
                0f,    ySize, zSize, r, g, b, -xSize, 0f, 0f,
                0f,    0f,    zSize, r, g, b, -xSize, 0f, 0f,

                //right
                xSize, 0f,    0f,    r, g, b, xSize, 0f, 0f,
                xSize, ySize, 0f,    r, g, b, xSize, 0f, 0f,
                xSize, ySize, zSize, r, g, b, xSize, 0f, 0f,
                xSize, 0f,    zSize, r, g, b, xSize, 0f, 0f,

                //front
                0f,    0f,    zSize, r, g, b, 0f, 0f,  zSize,
                xSize, 0f,    zSize, r, g, b, 0f, 0f,  zSize,
                xSize, ySize, zSize, r, g, b, 0f, 0f,  zSize,
                0f,    ySize, zSize, r, g, b, 0f, 0f,  zSize,

                //back
                0f,    0f,    0f,    r, g, b, 0f, 0f, -zSize,
                xSize, 0f,    0f,    r, g, b, 0f, 0f, -zSize,
                xSize, ySize, 0f,    r, g, b, 0f, 0f, -zSize,
                0f,    ySize, 0f,    r, g, b, 0f, 0f, -zSize
        );
        glBindBuffer(GL_ARRAY_BUFFER, vboId[0] = glGenBuffers());
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, vboId[1] = glGenBuffers());
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    @Override
    public void render() {
        glTranslatef(x, y, z);
        glBindBuffer(GL_ARRAY_BUFFER, vboId[f]);

        glVertexPointer(3, GL_FLOAT, 9 << 2, 0);
        glColorPointer (3, GL_FLOAT, 9 << 2, 3 << 2);
        glNormalPointer(   GL_FLOAT, 9 << 2, 6 << 2);

        glDrawArrays(GL_QUADS, 0, 24);
        glBindBuffer(GL_ARRAY_BUFFER, 0);


        f++;
        if (f > 1) {
            f = 0;
        }
    }

    private void updateVBO() {

    }

}

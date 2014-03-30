package net.cazzar.mods.voxelplayers.bodyshop;

import static org.lwjgl.opengl.GL11.*;

public class EntityVoxel extends Entity {
    float[] vertex_data_array;
    public void setupVBO() {


//        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    @Override
    public void render() {
//        glPushMatrix();

//        glTranslatef(x, y, z);

        glBegin(GL_TRIANGLE_STRIP);
        glColor3f(r, g, b);
        glVertex3f(-1, 0, -1);
        glVertex3f(1, 0, -1);
        glVertex3f(1, 0, 1);
        glVertex3f(-1, 0, 1);
        glEnd();

//        glPopMatrix();
    }
}

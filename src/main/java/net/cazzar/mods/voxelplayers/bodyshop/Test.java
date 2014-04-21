package net.cazzar.mods.voxelplayers.bodyshop;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Test {
    List<Entity> entityList = new ArrayList<>();

    public Test() {
        initDisplay();

        EntityVoxel voxel = new EntityVoxel(1);
        voxel.setPos(-5f, -5f, -80f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(1f);
        voxel.setupVBO();
        entityList.add(voxel);

        voxel = new EntityVoxel(2);
        voxel.setPos(-10f, -10f, -80f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setR(1f);
        voxel.setupVBO();
        entityList.add(voxel);


        mainLoop();
    }

    public static void main(String[] args) {
        new Test();
    }

    public void render() {
        for (Entity entity : entityList) {
            glLoadName(entity.id);
            glPushMatrix();
            entity.render();
            glPopMatrix();
        }
        /*GL11.glLoadName(1);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1, 1, 0);
        GL11.glVertex3f(-5, -5, -80);
        GL11.glVertex3f(5, -5, -80);
        GL11.glVertex3f(5, 5, -80);
        GL11.glVertex3f(-5, 5, -80);
        GL11.glEnd();

        GL11.glLoadName(2);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1, 0, 0);
        GL11.glVertex3f(-10, -10, -80);
        GL11.glVertex3f(0, -10, -80);
        GL11.glVertex3f(0, 0, -80);
        GL11.glVertex3f(-10, 0, -80);
        GL11.glEnd();*/
    }

    private void initDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setTitle("LWJGL Picking");
            Display.create();
        } catch (Exception ignored) {
        }

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(40f, 800 / 600f, 0.001f, 400f);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
    }

    private Entity select(int x, int y) {
        // The selection buffer
        IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
        int buffer[] = new int[256];

        IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        // The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
        int[] viewport = new int[4];

        // The number of "hits" (objects within the pick area).
        int hits;
        // Get the viewport info
        glGetInteger(GL_VIEWPORT, vpBuffer);
        vpBuffer.get(viewport);

        // Set the buffer that OpenGL uses for selection to our buffer
        glSelectBuffer(selBuffer);

        // Change to selection mode
        glRenderMode(GL_SELECT);

        // Initialize the name stack (used for identifying which object was selected)
        glInitNames();
        glPushName(0);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
           
            /*  create 5x5 pixel picking region near cursor location */
        GLU.gluPickMatrix((float) x, (float) y, 5.0f, 5.0f, IntBuffer.wrap(viewport));

        GLU.gluPerspective(40f, 800 / 600f, 0.001f, 400f);
        render();
        glPopMatrix();
        // Exit selection mode and return to render mode, returns number selected
        hits = glRenderMode(GL_RENDER);
        System.out.println("hits: " + hits);

        selBuffer.get(buffer);
        // Objects Were Drawn Where The Mouse Was
        if (hits > 0) {
            // If There Were More Than 0 Hits
            int choose = buffer[3]; // Make Our Selection The First Object
            int depth = buffer[1]; // Store How Far Away It Is
            for (int i = 1; i < hits; i++) {
                // Loop Through All The Detected Hits
                // If This Object Is Closer To Us Than The One We Have Selected
                if (buffer[i * 4 + 1] < depth) {
                    choose = buffer[i * 4 + 3]; // Select The Closer Object
                    depth = buffer[i * 4 + 1]; // Store How Far Away It Is
                }
            }
            System.out.println("Chosen: " + choose);
            return entityList.get(choose - 1);
        }

        return null;
    }


    private void mainLoop() {
        while (!Display.isCloseRequested()) {
            glMatrixMode(GL_MODELVIEW);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            render();

            Display.update();
            Display.sync(60);

            if (Mouse.isButtonDown(0)) {
                select(Mouse.getX(), Mouse.getY()).g += 0.01;
            }
        }
    }
}
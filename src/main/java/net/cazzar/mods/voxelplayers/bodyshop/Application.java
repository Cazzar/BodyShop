package net.cazzar.mods.voxelplayers.bodyshop;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;


public class Application {

    static double rotationY = 0.0;
    static double rotationX = 0.0;

    static Vector3f[] points = new Vector3f[100000];
    static int[] pointBuffers = new int[points.length];
    static Random random = new Random();
    static double posX = 0f;
    static double posY = 0f;
    static double posZ = 0f;
    static long lastFPS = getTime();
    static long lastFrame = getTime();
    static int fps;
    static List<Entity> entities = new LinkedList<Entity>();

    public static void main(String[] args) {
        initDisplay();

        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 1000f, (random.nextFloat() - 0.5f) * 1000f, random.nextInt(2000) - 2000);
        }

        final EntityVoxel voxel = new EntityVoxel();
        voxel.setPos(0f, 0f, 0f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(1);
        voxel.setupVBO();
        entities.add(voxel);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_COLOR_MATERIAL);
            glEnable(GL_DEPTH_TEST);

            render();

            glDisable(GL_CULL_FACE);
            pollInput();
            Display.update();
//            Display.sync(60);
            updateFPS();
        }
        glDisableVertexAttribArray(0);
        Display.destroy();
    }

    private static void render() {
//        glTranslatef(0, 0, pos);
//        glPushMatrix();
//        glTranslated(posX, posY, posZ);
//        glRotated(rotationX, 1, 0, 0);
//        glRotated(rotationY, 0, 1, 0);

        for (Entity entity : entities) {
//            glPushMatrix();
            entity.render();
//            glPopMatrix();
        }

        /*{
            float[] vertex_data_array = {
                    //   x      y      z      nx     ny     nz     r      g      b      a
                    // back quad
                     1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
                    -1.0f, -1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
                     1.0f, -1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,

                    // front quad
                     1.0f,  1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
                    -1.0f,  1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
                    -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
                     1.0f, -1.0f, -1.0f,  0.0f, -1.0f, 0.0f,  0.0f,  1.0f,  0.0f,  1.0f,

                    // left quad
                    -1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
                    -1.0f, -1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
                    -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,

                    // right quad
                    1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
                    1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
                    1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
                    1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,

                    // top quad
                    -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
                    -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
                     1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
                     1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,

                    // bottom quad
                    -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
                    -1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
                     1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
                     1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f
            };

            FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vertex_data_array.length);
            vertex_buffer_data.put(vertex_data_array);
            vertex_buffer_data.rewind();

            IntBuffer buffer = BufferUtils.createIntBuffer(1);
            glGenBuffers(buffer);

            int vertex_buffer_id = buffer.get(0);
            glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer_id);
            glBufferData(GL_ARRAY_BUFFER, vertex_buffer_data, GL_STATIC_DRAW);

            glVertexPointer(3, GL_FLOAT, 40, 0);
            glNormalPointer(GL_FLOAT, 40, 12);
            glColorPointer(4, GL_FLOAT, 40, 24);

            glDrawArrays(GL_QUADS, 0, vertex_data_array.length / 10);
        }*/


//        glPopMatrix();
    }

    private static void pollInput() {
//        final DisplayMode displayMode = Display.getDisplayMode();
//        Mouse.setCursorPosition(displayMode.getWidth() / 2, displayMode.getHeight() / 2);

        final int delta = getDelta();
        if (Mouse.isButtonDown(0)) {
//            int x = Mouse.getX();
//            int y = Mouse.getY();

            rotationX += Mouse.getDY();
            rotationY += Mouse.getDX();
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            posX = 0;
            posY = 0;
            posZ = 0;
            rotationX = 0;
            rotationY = 0;
//            glLoadIdentity();
        }

        final boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        final double offset = shift ? 0.04 : 0.01;

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) posZ += offset * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) posZ -= offset * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) posX -= offset * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) posX += offset * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) posY -= offset * delta;
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) posY += offset * delta;
    }

    public static void initDisplay() {
        try {
            Display.setTitle("Voxel Player Maker");
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();

            Keyboard.create();

            Mouse.create();

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GLU.gluPerspective(30F, 800F / 600F, 0.001F, 1000F);
            glMatrixMode(GL_MODELVIEW);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public static FloatBuffer asFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0; //reset the FPS counter
            lastFPS += 1000; //add one second
        }
        fps++;
    }

    private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        return delta;
    }
}

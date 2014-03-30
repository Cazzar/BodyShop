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


public class Application {

    static double rotationX = 0.0;
    static double rotationY = 0.0;

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

//        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 1000f, (random.nextFloat() - 0.5f) * 1000f, random.nextInt(2000) - 2000);
        }

        final EntityVoxel voxel = new EntityVoxel();
        voxel.setPos(0f, 0f, 0f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(1);
        voxel.setupVBO();
        entities.add(voxel);

        glEnable(GL_CULL_FACE);
        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glCullFace(GL_BACK);
            render();

            glDisable(GL_CULL_FACE);
            pollInput();
            Display.update();
//            Display.sync(60);
            updateFPS();
        }
        Display.destroy();
    }

    private static void render() {
//        glTranslatef(0, 0, pos);
        glPushMatrix();
        glTranslated(posX, posY, posZ);
        glRotated(rotationY, 1, 0, 0);
        glRotated(rotationX, 0, 1, 0);
        glShadeModel(GL_SMOOTH);
//        for (Entity entity : entities) {
//            glPushMatrix();
//            entity.render();
//            glPopMatrix();
//        }

        glBegin(GL_TRIANGLE_FAN);
        double s = 1;
        glColor3f(1, 0, 0);
        glVertex3f(-1, -1, -1); //0
        glVertex3f(1, -1, -1); //1
        glVertex3f(1, 1, -1); //5
        glVertex3f(-1, 1, -1); //4
        glVertex3f(-1, 1, 1); //7
        glVertex3f(-1, -1, 1); //3
        glVertex3f(1, -1, 1); //2
        glVertex3f(1, -1, -1); //1
        glEnd();

        glBegin(GL_POINTS);
        glColor3f(0, 1, 0);
        glVertex3f(0, 0, 0);
        glEnd();


        glPopMatrix();
    }

    private static void pollInput() {
//        final DisplayMode displayMode = Display.getDisplayMode();
//        Mouse.setCursorPosition(displayMode.getWidth() / 2, displayMode.getHeight() / 2);

        final int delta = getDelta();
        if (Mouse.isButtonDown(0)) {
//            int x = Mouse.getX();
//            int y = Mouse.getY();

            rotationY += Mouse.getDY();
            rotationX += Mouse.getDX();

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            posX = 0;
            posY = 0;
            posZ = 0;
            rotationY = 0;
            rotationX = 0;
//            glLoadIdentity();
        }

        final boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        final double offset = shift ? 0.4 : 0.1;

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
            GLU.gluPerspective(90F, 800F / 600F, 0.001F, 10000F);
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

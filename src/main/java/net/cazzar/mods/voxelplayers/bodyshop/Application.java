package net.cazzar.mods.voxelplayers.bodyshop;

import net.cazzar.mods.voxelplayers.bodyshop.util.Camera;
import net.cazzar.mods.voxelplayers.bodyshop.util.EulerCamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Color;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
    static int i;
    static Camera camera;
    static List<Entity> entities = new LinkedList<Entity>();

    public static void main(String[] args) {
        initDisplay();
        initGL();
        initCamera();
//        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++)
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 1000f, (random.nextFloat() - 0.5f) * 1000f, random.nextInt(2000) - 2000);

        EntityVoxel voxel = new EntityVoxel();
        voxel.setPos(0f, 0f, 0f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(1f);
        voxel.setupVBO();
        entities.add(voxel);

        voxel = new EntityVoxel();
        voxel.setPos(0f, 0f, 1f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setR(Color.YELLOW.getRed() / 255f);
        voxel.setG(Color.YELLOW.getGreen() / 255f);
        voxel.setB(Color.YELLOW.getBlue() / 255f);
        voxel.setupVBO();
        entities.add(voxel);

        lastFrame = getTime();

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            updateFPS();
            render();
            updateCamera();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }


    private static void updateCamera() {
        int delta = getDelta();
        if (delta == 0) return;

        if (Mouse.isGrabbed())
            camera.processMouse(1, 80, -80);
        camera.processKeyboard(delta, 1, 1, 1);
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (!Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(false);
        }
    }

    private static void initCamera() {
        camera = new EulerCamera.Builder()
                .setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setRotation(-1.12f, 0.16f, 0f)
                .setPosition(-1.38f, 1.36f, 7.95f)
                .setFieldOfView(60)
                .build();
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
    }

    public static Vector3f getMousePosition(int mouseX, int mouseY) {
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        FloatBuffer winZ = BufferUtils.createFloatBuffer(1);
        FloatBuffer position = BufferUtils.createFloatBuffer(3);
        float winX, winY;

        glGetFloat( GL_MODELVIEW_MATRIX, modelview );
        glGetFloat( GL_PROJECTION_MATRIX, projection );
        glGetInteger( GL_VIEWPORT, viewport );

        winX = (float)mouseX;
        winY = (float)viewport.get(3) - (float)mouseY;

        glReadPixels(mouseX, (int)winY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ);
        GLU.gluUnProject(winX, winY, winZ.get(), modelview, projection, viewport, position);
        return new Vector3f(position.get(0), position.get(1), position.get(2));
    }

    private static void initGL() {
        glMatrixMode(GL_PROJECTION);
        GLU.gluPerspective(45F, 800F / 600F, 0.001F, 10000F);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_VERTEX_ARRAY);
        glEnable(GL_NORMAL_ARRAY);
        glEnable(GL_COLOR_ARRAY);
        glShadeModel(GL_SMOOTH);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

//        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

//        glEnable(GL_LIGHTING);
//        glLight(GL_LIGHT0, GL_AMBIENT, asFloatBuffer(1f, 1f, 1f, 1.0f));
//        glLight(GL_LIGHT0, GL_DIFFUSE, asFloatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
//        glLight(GL_LIGHT0, GL_POSITION, asFloatBuffer(-1.0f, 1.0f, 0.0f, 1.0f));
//        glEnable(GL_LIGHT0);

//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
    }


    private static void render() {
        glPushMatrix();
        camera.applyTranslations();

        for (Entity entity : entities) {
            glPushMatrix();
            entity.render();
            glPopMatrix();
        }
        glEnd();

        glPopMatrix();
    }

    private static void pollInput() {

        final int delta = getDelta();
        if (Mouse.isButtonDown(0)) {
            rotationY -= Mouse.getDY();
            rotationX += Mouse.getDX();
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            posX = 0;
            posY = 0;
            posZ = 0;
            rotationY = 0;
            rotationX = 0;
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

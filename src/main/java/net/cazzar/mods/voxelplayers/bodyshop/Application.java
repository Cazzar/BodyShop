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
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    static List<Entity> entities = new LinkedList<>();

    public static void main(String[] args) {
        initDisplay();
        initGL();
        initCamera();
//        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++)
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 1000f, (random.nextFloat() - 0.5f) * 1000f, random.nextInt(2000) - 2000);

        EntityVoxel voxel = new EntityVoxel(1);
        voxel.setPos(0f, 0f, 0f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(1f);
        voxel.setupVBO();
        entities.add(voxel);

        voxel = new EntityVoxel(2);
        voxel.setPos(0f, 0f, -1f);
        voxel.setScale(1f, 1f, 1f);
        voxel.setB(2f);
        voxel.setupVBO();
        entities.add(voxel);

        lastFrame = getTime();

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            updateFPS();
            select(Mouse.getX(), Mouse.getY());
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

        if (Mouse.isButtonDown(0))
            camera.processMouse(1, 80, -80);

        camera.processKeyboard(delta, 1, 1, 1);
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (Mouse.isButtonDown(1)) {
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

    public static void select(int x, int y) {
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
        }

    }

    private static void render() {
        glPushMatrix();
        camera.applyTranslations();

        for (Entity entity : entities) {
            glLoadName(entity.id);
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

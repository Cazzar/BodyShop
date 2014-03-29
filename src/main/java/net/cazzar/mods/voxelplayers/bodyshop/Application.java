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
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Application {

    static double rotationY = 0.0;
    static double rotationX = 0.0;

    static Vector3f[] points = new Vector3f[10000];
    static Random random = new Random();
    static float speed = 0f;
    static long lastFPS = getTime();
    static int fps;


    public static void main(String[] args) {
        initDisplay();

        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 100f, (random.nextFloat() - 0.5f) * 100f, random.nextInt(200) - 200);
        }

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            GL11.glLoadIdentity();
            render();
            pollInput();
            Display.update();
            Display.sync(60);
            updateFPS();
        }

        Display.destroy();
    }

    private static void render() {
        glTranslatef(0, 0, speed);
        glBegin(GL_TRIANGLE_FAN);

        for (Vector3f point : points) {
            glVertex3f(point.x, point.y, point.z);
        }

        glEnd();
    }

    private static void pollInput() {

        if (Mouse.isButtonDown(0)) {
            int x = Mouse.getX();
            int y = Mouse.getY();

            System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            speed = 0;
            glLoadIdentity();
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) speed += 0.01;
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) speed -= 0.01;

//        while (Keyboard.next()) {
//            if (Keyboard.getEventKeyState()) {
//                if (Keyboard.getEventKey() == Keyboard.KEY_W) {
//                    speed = 0.01f;
//                    System.out.println("W Key Pressed");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
//                    System.out.println("A Key Pressed");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
//                    System.out.println("S Key Pressed");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
//                    speed = -0.01f;
//                    System.out.println("D Key Pressed");
//                }
//            } else {
//                if (Keyboard.getEventKey() == Keyboard.KEY_W) {
//                    speed = 0;
//                    System.out.println("W Key Released");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
//                    System.out.println("A Key Released");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
//                    System.out.println("S Key Released");
//                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
//                    System.out.println("D Key Released");
//                }
//            }
//        }
    }

    public static void initDisplay() {
        try {
            Display.setTitle("Voxel Player Maker");
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            GLU.gluPerspective(30F, 800F/600F, 0.001F, 100F);
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
}

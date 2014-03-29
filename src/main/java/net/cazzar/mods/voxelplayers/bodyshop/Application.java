package net.cazzar.mods.voxelplayers.bodyshop;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import static org.lwjgl.opengl.ARBBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;


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
    private static int vertexCount;
    private static int vboId;
    private static int vaoId;

    public static void main(String[] args) {
        initDisplay();

        System.out.println(glGetString(GL_VERSION));

        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector3f((random.nextFloat() - 0.5f) * 1000f, (random.nextFloat() - 0.5f) * 1000f, random.nextInt(2000) - 2000);
        }

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render();
            pollInput();
            Display.update();
//            Display.sync(60);
            updateFPS();
        }
        glDisableVertexAttribArray(0);

        // Delete the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        Display.destroy();
    }

    private static void render() {
//        glTranslatef(0, 0, pos);
        glPushMatrix();
        glTranslated(posX, posY, posZ);
        glRotated(rotationX, 1, 0, 0);
        glRotated(rotationY, 0, 1, 0);

        // create geometry buffers
        FloatBuffer cBuffer = BufferUtils.createFloatBuffer(9);
        cBuffer.put(1).put(0).put(0);
        cBuffer.put(0).put(1).put(0);
        cBuffer.put(0).put(0).put(1);
        cBuffer.flip();

        FloatBuffer vBuffer = BufferUtils.createFloatBuffer(9);
        vBuffer.put(-0.5f).put(-0.5f).put(0.0f);
        vBuffer.put(+0.5f).put(-0.5f).put(0.0f);
        vBuffer.put(+0.5f).put(+0.5f).put(0.0f);
        vBuffer.flip();

        // create index buffer
        ShortBuffer iBuffer = BufferUtils.createShortBuffer(3);
        iBuffer.put((short) 0);
        iBuffer.put((short) 1);
        iBuffer.put((short) 2);
        iBuffer.flip();

        //

        IntBuffer ib = BufferUtils.createIntBuffer(3);

        glGenBuffersARB(ib);
        int vHandle = ib.get(0);
        int cHandle = ib.get(1);
        int iHandle = ib.get(2);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vHandle);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, vBuffer, GL_STATIC_DRAW_ARB);
        glVertexPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

        glBindBufferARB(GL_ARRAY_BUFFER_ARB, cHandle);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, cBuffer, GL_STATIC_DRAW_ARB);
        glColorPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, iHandle);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, iBuffer, GL_STATIC_DRAW_ARB);

        glDrawElements(GL_TRIANGLES, /* elements */3, GL_UNSIGNED_SHORT, 0L);

        glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);

        glDisableClientState(GL_COLOR_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);

        // cleanup VBO handles
        ib.put(0, vHandle);
        ib.put(1, cHandle);
        ib.put(2, iHandle);
        glDeleteBuffersARB(ib);

        glPopMatrix();
    }

    private static void pollInput() {
//        final DisplayMode displayMode = Display.getDisplayMode();
//        Mouse.setCursorPosition(displayMode.getWidth() / 2, displayMode.getHeight() / 2);

        final int delta = getDelta();
        if (Mouse.isButtonDown(0)) {
            int x = Mouse.getX();
            int y = Mouse.getY();

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

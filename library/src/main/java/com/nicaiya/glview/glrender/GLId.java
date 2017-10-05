package com.nicaiya.glview.glrender;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

/**
 * This mimics corresponding GL functions.
 */
public interface GLId {

    int generateTexture();

    void glGenBuffers(int n, int[] buffers, int offset);

    void glDeleteTextures(GL11 gl, int n, int[] textures, int offset);

    void glDeleteBuffers(GL11 gl, int n, int[] buffers, int offset);

    void glDeleteFramebuffers(GL11ExtensionPack gl11ep, int n, int[] buffers, int offset);

}

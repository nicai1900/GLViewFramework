package com.nicaiya.glview.texture;

import com.nicaiya.glview.glrender.GLCanvas;

/**
 <p>
 Texture is a rectangular image which can be drawn on GLCanvas.
 The isOpaque() function gives a hint about whether the texture is opaque,
 so the drawing can be done faster.
 </p>

 <p>
 This is the current texture hierarchy:

 <pre>
 Texture
 -- ColorTexture
 -- FadeInTexture
 -- BasicTexture
    -- UploadedTexture
       -- BitmapTexture
       -- Tile
       -- ResourceTexture
          -- NinePatchTexture
       -- CanvasTexture
          -- StringTexture
 </pre>
 </p>
*/
public interface Texture {

    public int getWidth();

    public int getHeight();

    public void draw(GLCanvas canvas, int x, int y);

    public void draw(GLCanvas canvas, int x, int y, int w, int h);

    public boolean isOpaque();
}

package com.example.sora.coins;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016-04-05.
 */

public class LockScreenMovingUnit
{
    private Bitmap image;
    private float x, y, offsetX;
    private float width, height;
    int posX1 = 0, posX2 = 0;
    boolean isMoving = false;

    public LockScreenMovingUnit(Bitmap image)
    {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.x = 300;
        this.y = 400;
    }

    public void moving(MotionEvent event)
    {
        int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                if (inCourse(event.getX(), event.getY()))
                {
                    posX1 = (int) event.getX();
                    offsetX = posX1 - x;
                    isMoving = true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (isMoving)
                {
                    x = posX2 - offsetX;
                    posX2 = (int) event.getX();

                    if (Math.abs(posX2 - posX1) > 20)
                    {
                        posX1 = posX2;
                    }
                }

            case MotionEvent.ACTION_UP :

            case MotionEvent.ACTION_CANCEL :

            default :
                break;
        }
    }

    public Rect getRect()
    {
        Rect rect = new Rect();
        rect.set((int)x, (int)y, (int)(x + width), (int)(y + height));

        return rect;
    }

    public boolean inCourse(float eventX, float eventY)
    {
        if (eventX > x - 30 && eventX < (x+ width + 30) && eventY > y - 30 && eventY < (y + height + 30))
        {
            return true;
        }

        else
            return false;
    }

    public Bitmap getImage()
    {
        return image;
    }
}
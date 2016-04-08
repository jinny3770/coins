package com.example.sora.coins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016-04-05.
 */

public class LockScreenMovingView extends View
{
    private LockScreenMovingUnit lsmu;

    public LockScreenMovingView(Context context)
    {
        super(context);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_place);
        lsmu = new LockScreenMovingUnit(image);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        lsmu.moving(event);
        invalidate();
        return true;
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(lsmu.getImage(), null, lsmu.getRect(), null);
        super.draw(canvas);
    }
}
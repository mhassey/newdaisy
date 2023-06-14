package com.allyy.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

public class FixAdobeWebView extends WebView {

    View whiteView;
    private boolean eatenFirstFlashDraw;

    public FixAdobeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        whiteView = new WhiteSurfaceView(context);
        whiteView.setLayoutParams(new AbsoluteLayout.LayoutParams(800, 480, 0, 0));
        addView(whiteView);
    }


    private class WhiteSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        public WhiteSurfaceView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);
                holder.unlockCanvasAndPost(canvas);
            }
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) { }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) { }
    }


    //
    // Override drawChild to eat the first draw of the FlashPaintSurface
    //
    @Override
    protected boolean drawChild (Canvas canvas, View child, long drawingTime) {
        if (!eatenFirstFlashDraw && child.getClass().getName().equals("com.adobe.flashplayer.FlashPaintSurface")) {
            eatenFirstFlashDraw = true;
            return true;
        }
        return super.drawChild(canvas, child, drawingTime);
    }
}
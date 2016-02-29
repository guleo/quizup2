package com.example.frank.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by frank on 2016/2/5.
 */
public class Vertical3DAnim extends Animation {
    private Camera mcamera;

    private float fromDegree;
    private float toDegree;
    private float prioX;
    private float prioY;

    public Vertical3DAnim(float toDegree, float fromDegree, float prioX, float prioY) {
        this.toDegree = toDegree;
        this.fromDegree = fromDegree;
        this.prioX = prioX;
        this.prioY = prioY;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mcamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degree = fromDegree + (toDegree - fromDegree) * interpolatedTime;
        Matrix matrix = t.getMatrix();
        mcamera.save();
        mcamera.rotateZ(degree);
        mcamera.getMatrix(matrix);
        mcamera.restore();
        matrix.preTranslate(-prioX,-prioY);
        matrix.postTranslate(prioX,prioY);
    }
}

package com.mikhaellopez.circularprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by nigelcardozo on 18/07/2017.
 */

public class SemiCircularFuelGauge extends CircularProgressBar {

    // Properties
    private boolean showMarkers = true;
    private boolean showNeedle = true;
    private boolean showGaugeText = false;
    private boolean needleHalfSize = false;
    private boolean fillGaugeTextBackground=false;
    private int needleColor = Color.GRAY;
    private float needleWidth = getResources().getDimension(R.dimen.default_needle_stroke_width);
    private int markerSeparation = 36;
    private float markerDensity = getResources().getDimension(R.dimen.default_marker_density);
    private int gaugeTextColor = Color.BLACK;
    private int gaugeTextBackgroundColor = Color.WHITE;
    private float gaugeTextSize = 30;

    // Object used to draw
    float cx;
    float cy;
    float scaleMarkSize;
    float radius;
    private Paint needlePaint;
    private Paint gaugeTextPaint;

    private String fuelGaugeText;
    private int StringXOffset=0;
    private int StringYOffset=0;

    public SemiCircularFuelGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SemiCircularFuelGauge, 0, 0);

        //Reading values from the XML layout
        try {
            // Value
            showMarkers = typedArray.getBoolean(R.styleable.SemiCircularFuelGauge_scfg_show_markers, true);
            showNeedle = typedArray.getBoolean(R.styleable.SemiCircularFuelGauge_scfg_show_needle, true);
            showGaugeText = typedArray.getBoolean(R.styleable.SemiCircularFuelGauge_scfg_show_gauge_text, false);
            needleHalfSize = typedArray.getBoolean(R.styleable.SemiCircularFuelGauge_scfg_needle_half_size, false);
            needleColor = typedArray.getInt(R.styleable.SemiCircularFuelGauge_scfg_needle_color, needleColor);
            needleWidth = typedArray.getDimension(R.styleable.SemiCircularFuelGauge_scfg_needle_width, needleWidth);
            markerSeparation = typedArray.getInt(R.styleable.SemiCircularFuelGauge_scfg_marker_separation, markerSeparation);
            markerDensity = typedArray.getDimension(R.styleable.SemiCircularFuelGauge_scfg_marker_density, markerDensity);
            gaugeTextSize = typedArray.getFloat(R.styleable.SemiCircularFuelGauge_scfg_gauge_text_size, gaugeTextSize);
            gaugeTextColor = typedArray.getInt(R.styleable.SemiCircularFuelGauge_scfg_gauge_text_color, gaugeTextColor);
            gaugeTextBackgroundColor = typedArray.getInt(R.styleable.SemiCircularFuelGauge_scfg_gauge_text_background_color, gaugeTextBackgroundColor);
            fillGaugeTextBackground = typedArray.getBoolean(R.styleable.SemiCircularFuelGauge_scfg_gauge_text_fill_background, false);
        } finally {
            typedArray.recycle();
        }

        setupPaint();

        //This is used since we want to ensure the z ordering of all drawn items.
        super.setDoNotDraw(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateFixedValues();

        float angle = 180 * getProgress() / 100;

        //Draw Background
        canvas.drawArc(rectF, 180, 180, false, backgroundPaint);

        //Draw Markers
        if (showMarkers){
            drawMarkers(canvas);
        }

        //Draw Progress Bar
        canvas.drawArc(rectF, 180, angle, false, foregroundPaint);

        //Draw Needle
        if (showNeedle){
            drawNeedle(canvas, angle);
        }

        //Add Gauge Text
        if (showGaugeText){
            if (fuelGaugeText != null){
                drawText(canvas, fuelGaugeText, StringXOffset, StringYOffset);
            }
        }
    }

    private void calculateFixedValues(){
        cx = getWidth() / 2f;
        cy = getHeight() / 2f;
        scaleMarkSize = getResources().getDisplayMetrics().density * markerDensity;
        radius = Math.min(getWidth(), getHeight()) / 2;
    }

    private void setupPaint(){
        setupNeedlePaint();
        setupGaugeTextPaint();
    }

    private void setupNeedlePaint(){
        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(needleColor);
        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setStrokeWidth(needleWidth);
    }

    private void setupGaugeTextPaint(){
        gaugeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gaugeTextPaint.setColor(gaugeTextColor);
        gaugeTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gaugeTextPaint.setStrokeWidth(1);
        gaugeTextPaint.setTextSize(gaugeTextSize);

        Paint.FontMetrics fm = new Paint.FontMetrics();
        gaugeTextPaint.getFontMetrics(fm);
    }

    private void drawNeedle(Canvas canvas, float angle){
        float angleNeedle = (float) Math.toRadians(angle-90);

        float startNeedleX = (float) (cx + radius * Math.sin(angleNeedle));
        float startNeedleY = (float) (cy - radius * Math.cos(angleNeedle));

        if (!needleHalfSize){
            canvas.drawLine(startNeedleX, startNeedleY, ((rectF.width()/2) + (needleWidth/2)), ((rectF.width()/2) + (needleWidth/2)), needlePaint);
        } else {
            canvas.drawLine(startNeedleX, startNeedleY, ((rectF.width()/2) + (needleWidth/2)), ((rectF.width()/2) + (needleWidth/2)), needlePaint);
        }

    }

    private void drawMarkers(Canvas canvas){
        for (int i = 0; i <= 180; i += markerSeparation) {
            float angleMarker = (float) Math.toRadians(i);

            float startMarkerX = (float) (cx + radius * Math.sin(angleMarker));
            float startMarkerY = (float) (cy - radius * Math.cos(angleMarker));

            float stopMarkerX = (float) (cx + (radius - scaleMarkSize) * Math.sin(angleMarker));
            float stopMarkerY = (float) (cy - (radius - scaleMarkSize) * Math.cos(angleMarker));

            canvas.save();
            canvas.rotate((float)-90, canvas.getWidth()/2, canvas.getHeight()/2);
            canvas.drawLine(startMarkerX, startMarkerY, stopMarkerX, stopMarkerY, backgroundPaint);
            canvas.restore();
        }
    }

    private void drawText(Canvas canvas, String text, int xOffset, int yOffset){

        if (!fillGaugeTextBackground){
            int xPos = (int) (((canvas.getWidth() - gaugeTextPaint.getTextSize() * Math.abs(text.length() / 2)) / 2) + (xOffset));
            int yPos = (int) (((canvas.getHeight() / 2) + ((gaugeTextPaint.descent() + gaugeTextPaint.ascent()) / 2)) + (yOffset));

            canvas.drawText(text, xPos, yPos, gaugeTextPaint);
        } else {
            int xPos = (int) (((canvas.getWidth() - gaugeTextPaint.getTextSize() * Math.abs(text.length() / 2)) / 2) + (xOffset));
            int yPos = (int) (((canvas.getHeight() / 2) + ((gaugeTextPaint.descent() + gaugeTextPaint.ascent()) / 2)) + (yOffset));

            gaugeTextPaint.setColor(gaugeTextBackgroundColor);
            canvas.drawRect(xPos, yPos + gaugeTextPaint.getFontMetrics().top,
                    xPos + gaugeTextPaint.measureText(text), yPos + gaugeTextPaint.getFontMetrics().bottom, gaugeTextPaint);

            gaugeTextPaint.setColor(gaugeTextColor);
            canvas.drawText(text, xPos, yPos, gaugeTextPaint);
        }
    }

    public void setText(String textToDisplay, int xOffset, int yOffset){
        fuelGaugeText = textToDisplay;
        StringXOffset = xOffset;
        StringYOffset = yOffset;
    }
}

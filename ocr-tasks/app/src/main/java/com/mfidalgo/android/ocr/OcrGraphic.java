/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mfidalgo.android.ocr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.vision.text.Text;
import com.mfidalgo.android.camera.GraphicOverlay;

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private static final int MINIMUM_NUMBER_LENGTH = 9;
    private static final int TEXT_COLOR = Color.WHITE;
    private static Paint sTextPaint;
    private final Text mText;
    private String cleanedText;
    private int mId;

    OcrGraphic(GraphicOverlay overlay, Text text) {
        super(overlay);

        mText = text;
        cleanedText = removeText(mText.getValue());


        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            float scaledSize = (float) OcrCaptureActivity.getContext().getResources().getDimensionPixelSize(R.dimen.myFontSize);
            sTextPaint.setTextSize(scaledSize);
        }


        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getTextBlock() {
        return cleanedText;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     *
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        // Check if this graphic's text contains this point.
        if (mText == null) {
            return false;
        }
        RectF rect = new RectF(mText.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        // Draw the text onto the canvas.
        if (mText == null) {
            return;
        }

        // Break the text into multiple lines and draw each one that contains numbers
        List<? extends Text> textComponents = mText.getComponents();
        for (Text currentText : textComponents) {
            String textToDraw = removeText(currentText.getValue());
            if (countNumbers(textToDraw) >= MINIMUM_NUMBER_LENGTH) {
                float left = translateX(currentText.getBoundingBox().left);
                float bottom = translateY(currentText.getBoundingBox().bottom);

                Log.d("OcrGraphic", "Cleaned Text: " + textToDraw);
                canvas.drawText(textToDraw, left, bottom, sTextPaint);
                cleanedText = textToDraw.replaceAll("\\s+", "");
            }
        }
    }


   /* private boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str.trim());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }*/


    private int countNumbers(String str) {
        int numCount = 0;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                numCount++;
            }
        }

        return numCount;
    }


    private String removeText(String line) {
        StringBuilder newText = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (Character.isDigit(c) || c == '+') {
                newText.append(Character.toString(c));
            } else {
                newText.append(" ");
            }

        }
        return newText.toString();
    }


}

/*
 * Copyright (c) 2012-2015 Andrei Popleteev.
 * Licensed under the MIT license.
 */
package com.anmipo.android.trentobus.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.anmipo.android.trentobus.R;

public class TimetableView extends View {
    static final String TAG = "Timetable";

    // font size for cell text
    public static final int FONT_SIZE_SP = 16;
    // horizontal padding for all cells, in px
    private static final int CELL_PADDING_X = 5;
    
    // state-saving key
    private static final String STATE_OFFSET_X = "TimetableView.offsetX"; 
    private static final String STATE_OFFSET_Y = "TimetableView.offsetY"; 
    
    // table data
    private String[] fixedCol;
    private String[] fixedRow;
    private String[][] cells;

    // fixedCol content ellipsized to fit the column 
    private String[] fixedColEllipsized;
    
    
    // table dimensions
    private int rowCount;
    private int colCount;

    // paints for table parts
    private TextPaint fixedColumnPaint;
    private TextPaint fixedRowPaint;
    private TextPaint cellPaint; 

    // background drawables for fixed and normal cells
    private Drawable fixedBackgroundDrawable;
    private Drawable cellBackgroundDrawable;

    // viewport
    private int topRow = 0;      // currently visible top row number
    private int leftCol = 0;     // currently visible left column number
    private int offsetX = 0;     // table shift in relation to the viewport
    private int offsetY = 0;     // table shift in relation to the viewport

    // drawing sizes / dimensions (calculated based on View size and paints)  
    private int width, height;   // view/canvas size
    private int fixedColWidth;   // width of the left (fixed) column
    private int fixedRowHeight;
    private int maxOffsetX;      // max allowed horizontal offset
    private int maxOffsetY;      // max allowed vertical offset
    private int colWidth;        // cell columns width
    private int rowHeight;       // height of all rows
    
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private OnSizeChangedListener onSizeChangedListener = null;
    public interface OnSizeChangedListener {
        public void onSizeChanged(int width, int height);
    }
    private OnCellClickListener onCellClickListener = null;
    /**
     * Listener of table cell clicks.
     * <code>col</code> and <code>row</code> values are between -1 and 
     * number of columns/rows; -1 indicates the fixed column/row.
     */
    public interface OnCellClickListener {
        public void onCellSingleTap(int col, int row);
        public void onCellLongPress(int col, int row);
    }
    
    public TimetableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources(context);
        initPaints();
        setSaveEnabled(true);
        
        gestureDetector = new GestureDetector(context, new GestureListener());
        scroller = new Scroller(context);
        
        // TODO: remove this debug data
        setData(new String[]{"row1", "row2", "row3", "row4", "row5", "row6"},
                new String[]{"col1", "col2", "col3", "col4"},
                new String[][]
                {{"12:34", "56:78", "90:12", "34:56"},
                 {"12:34", "56:78", "*",     "34:56"},
                 {"12:34", "56:78", "90:12", "34:56"},
                 {"12:34", "56:78", "90:12", "34:56"},
                 {"12:34", "56:78", "90:12", "34:56"},
                 {"12:34", "56:78", "90:12", "34:56"}});
    }

    private void initResources(Context context) {
        Resources res = context.getResources();
        fixedBackgroundDrawable = res.getDrawable(R.drawable.fixed_bg);
        cellBackgroundDrawable = res.getDrawable(R.drawable.cell_bg);
    }
    
    private void initPaints() {
        float fontSizePixels = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE_SP,
                        getContext().getResources().getDisplayMetrics());
        fixedColumnPaint = new TextPaint();
        fixedColumnPaint.setTextAlign(Align.LEFT);
        fixedColumnPaint.setTextSize(fontSizePixels);
        fixedColumnPaint.setTypeface(Typeface.DEFAULT_BOLD);
        fixedColumnPaint.setAntiAlias(true);
        
        fixedRowPaint = new TextPaint();
        fixedRowPaint.setTextAlign(Align.CENTER);
        fixedRowPaint.setTextSize(fontSizePixels);
        fixedRowPaint.setTypeface(Typeface.DEFAULT_BOLD);
        fixedRowPaint.setAntiAlias(true);
        
        cellPaint = new TextPaint();
        cellPaint.setColor(Color.BLACK);
        cellPaint.setTextSize(fontSizePixels);
        cellPaint.setTextAlign(Align.CENTER);
        cellPaint.setAntiAlias(true);
        updateChildrenLayout();
        
        ellipsizeTexts();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        updateChildrenLayout();
        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(w, h);
        }
    }
    /**
     * (Re)evaluates dimensions of child elements (cell width/height).
     */
    protected void updateChildrenLayout() {
        setColWidth((int)cellPaint.measureText("88:88") + 2 * CELL_PADDING_X);
        setRowHeight((int)(1.5f * cellPaint.getTextSize()));
        setFixedColWidth((int) measureFixedColumnWidth());
        setFixedRowHeight(rowHeight);
        maxOffsetX = colCount * colWidth - (width - fixedColWidth);
        maxOffsetY = rowCount * rowHeight - (height - fixedRowHeight);
        // for small tables max values can become negative, forbid this.
        if (maxOffsetX < 0) maxOffsetX = 0;
        if (maxOffsetY < 0) maxOffsetY = 0;
    }
    
    protected void ellipsizeTexts() {
        if (fixedCol != null) {
            fixedColEllipsized = new String[fixedCol.length];
            for (int i = 0; i < fixedCol.length; i++) {
                fixedColEllipsized[i] = TextUtils.ellipsize(fixedCol[i], 
                        fixedColumnPaint, fixedColWidth, TruncateAt.END).toString(); 
            }
        }
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateChildrenLayout();
    }    

    /**
     * Calculates the fixed column's width, so that it either
     * fits all entries, or occupies not more than 50% of the view;
     * @return
     */
    protected float measureFixedColumnWidth() {
        float result = 0;
        for (int i = 0; i < rowCount; i++) {
            float w = fixedColumnPaint.measureText(fixedCol[i]);
            if (w > result) {
                result = w;
            }
        }
        result += 2 * CELL_PADDING_X;
        return (result <= width/2) ? result : width/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFixedColumn(canvas);
        drawFixedRow(canvas);
        drawCells(canvas);
        drawCorner(canvas);
    }
    
    protected void drawCorner(Canvas canvas) {
        // draw top-left empty corner 
        canvas.clipRect(0, 0, width, height, Op.REPLACE);
        fixedBackgroundDrawable.setBounds(0, 0, fixedColWidth, fixedRowHeight);
        fixedBackgroundDrawable.draw(canvas);        
    }

    protected void drawCells(Canvas canvas) {
        canvas.clipRect(
                fixedColWidth, fixedRowHeight,
                width, height, Op.REPLACE);
        int textOffset = getTextCenterOffset(rowHeight, cellPaint);
        int cellCenterOffset = colWidth / 2;
        int x = fixedColWidth + colWidth * leftCol - offsetX;
        int y0 = topRow * rowHeight + fixedRowHeight - offsetY;
        int xIndex = leftCol;
        while ((x < width) && (xIndex < colCount)) {
            int yIndex = topRow;
            int y = y0;
            int cellRight = x + colWidth; 
            while ((y < height) && (yIndex < rowCount)) {
                int cellBottom = y + rowHeight;
                cellBackgroundDrawable.setBounds(x, y, cellRight, cellBottom);
                cellBackgroundDrawable.draw(canvas);
                canvas.drawText(cells[yIndex][xIndex], 
                        x + cellCenterOffset, y + textOffset, 
                        cellPaint);
                y += rowHeight;
                yIndex++;
            }
            x += colWidth;
            xIndex++;
        }
    }

    protected void drawFixedRow(Canvas canvas) {
        canvas.clipRect(fixedColWidth, 0,
                width, fixedRowHeight, Op.REPLACE);
        int textOffsetX = colWidth / 2;
        int textOffsetY = getTextCenterOffset(fixedRowHeight, fixedRowPaint);
        int x = fixedColWidth + colWidth * leftCol - offsetX;
        int index = leftCol;
        while ((x < width) && (index < colCount)) {
            fixedBackgroundDrawable.setBounds(x, 0, x + colWidth, fixedRowHeight);
            fixedBackgroundDrawable.draw(canvas);
            canvas.drawText(fixedRow[index], 
                    x + textOffsetX, textOffsetY, fixedRowPaint);
            x += colWidth;
            index++;
        }
    }

    protected void drawFixedColumn(Canvas canvas) {
        canvas.clipRect(0, fixedRowHeight, fixedColWidth, height, 
                Op.REPLACE);
        int textOffsetY = getTextCenterOffset(rowHeight, fixedColumnPaint);
        int y = rowHeight * topRow + fixedRowHeight - offsetY;
        int index = topRow;
        while ((y < height) && (index < rowCount)) {
            Rect bounds = new Rect(0, y, fixedColWidth, (y + rowHeight));
            fixedBackgroundDrawable.setBounds(bounds);

            fixedBackgroundDrawable.draw(canvas);
            canvas.drawText(fixedColEllipsized[index], 
                    CELL_PADDING_X, y + textOffsetY , 
                    fixedColumnPaint);
            y += rowHeight;
            index++;
        }
        canvas.drawLine(fixedColWidth, 0, 
                fixedColWidth, y, fixedColumnPaint);
    }

    /**
     * Returns the vertical offset for text to be centered in a line with 
     * <code>lineHeight</code> height. 
     * @param lineHeight
     *            Height of line, in pixels. 
     * @param paint
     *            Paint defining text drawing parameters.
     * @return
     */
    protected static int getTextCenterOffset(int lineHeight, Paint paint) {
        /*
         * Full form:
         *     paint.descent() 
         *         - (paint.descent() - paint.ascent())/2
         *         + lineHeight/2;
         */
        return (int)(lineHeight - paint.ascent() - paint.descent()) / 2;
    }
    
    /**
     * Sets data for this table view.
     * Dimensions of <code>cells</code> must correspond to these of 
     * <code>fixedCol</code> and <code>fixedRow</code>, otherwise an
     * {@link IllegalArgumentException} is thrown.
     * Neither of dimensions can be zero (a {@link NullPointerException} 
     * is thrown otherwise). 
     * None of array items can be null (empty string ok).
     * 
     * @param fixedCol
     *            Contents for the left fixed column
     * @param fixedRow
     *            Contents for the top fixed row
     * @param cells
     *            Contents for the table cells
     */
    public void setData(String[] fixedCol, String[] fixedRow, String[][] cells) {
        this.fixedCol = fixedCol;
        this.fixedRow = fixedRow;
        this.cells = cells;
        colCount = fixedRow.length;
        rowCount = fixedCol.length;
        if (cells.length != rowCount || cells[0].length != colCount) {
            throw new IllegalArgumentException("Table dimensions do not match");
        }
        layout(0, 0, width, height);
        postInvalidate();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, 
                float dy) {
            scrollBy((int) dx, (int) dy);
            return true;
        }
    
        @Override
        public boolean onDown(MotionEvent ev) {
            scroller.forceFinished(true);
            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Point colRow = coordsToCell((int) e.getX(), (int) e.getY());
            if (colRow != null && onCellClickListener != null) {
                onCellClickListener.onCellSingleTap(colRow.x, colRow.y);
            }
            return true;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            Point colRow = coordsToCell((int) e.getX(), (int) e.getY());
            if (colRow != null && onCellClickListener != null) {
                onCellClickListener.onCellLongPress(colRow.x, colRow.y);
            }
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            // allow only single-axis fling, for user's convenience
            if (Math.abs(velocityX) < Math.abs(velocityY)) {
                velocityX = 0;
            } else {
                velocityY = 0;
            }
            scroller.fling(offsetX, offsetY, 
                    (int) -velocityX, (int) -velocityY, 
                    0, maxOffsetX, 
                    0, maxOffsetY);
            // It is necessary to call postInvalidate(), so that 
            // computeScroll() will eventually get called.
            // (For some reason, simple invalidate() won't work.)
            postInvalidate(); 
            return true;
        }
    }

    /**
     * Returns col/row number corresponding to the given graphical coords 
     * (in this view system). For fixed row/col, the value is -1.
     * If the coords do not correspond to any cell, returns null.
     * @param x
     *        X graphical coordinate within this view.
     * @param y
     *        Y graphical coordinate within this view.
     * @return
     */
    protected Point coordsToCell(int x, int y) {
        int col, row;
        if (y <= fixedRowHeight) {
            row = -1;
        } else {
            row = (y - fixedRowHeight + offsetY) / rowHeight;
        }
        if (x <= fixedColWidth) {
            col = -1;
        } else {
            col = (x - fixedColWidth + offsetX) / colWidth;
        }
        return new Point(col, row);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            setOffsetX(scroller.getCurrX());
            setOffsetY(scroller.getCurrY());
            // It is necessary to call postInvalidate(), so that 
            // computeScroll() will be called again later.
            // (For some reason, simple invalidate() won't work.)
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        setOffsetX(x);
        setOffsetY(y);
        postInvalidate();
    }
    
    @Override
    public void scrollBy(int dx, int dy) {
        setOffsetX(offsetX + dx);
        setOffsetY(offsetY + dy);
        postInvalidate();
    }

    protected void setOffsetX(int newOffsetX) {
        if (newOffsetX < 0) {
            offsetX = 0;
        } else if (newOffsetX > maxOffsetX) {
            offsetX = maxOffsetX;
        } else {
            offsetX = newOffsetX;
        }
        leftCol = offsetX / colWidth;
    }

    protected void setOffsetY(int newOffsetY) {
        if (newOffsetY < 0) {
            offsetY = 0;
        } else if (newOffsetY > maxOffsetY) {
            offsetY = maxOffsetY;
        } else {
            offsetY = newOffsetY;
        }
        topRow = offsetY / rowHeight;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // super must be called, but always returns null
        super.onSaveInstanceState(); 
        
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_OFFSET_X, offsetX);
        bundle.putInt(STATE_OFFSET_Y, offsetY);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
        
        Bundle bundle = (Bundle)state; 
        setOffsetX(bundle.getInt(STATE_OFFSET_X, 0));
        setOffsetY(bundle.getInt(STATE_OFFSET_Y, 0));
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        onSizeChangedListener = listener;
    }
    public void setOnCellClickListener(OnCellClickListener listener) {
        onCellClickListener = listener;
    }
    
    /*
     * Getters for descendant classes. 
     */
    protected int getRowHeight() {
        return rowHeight;
    }
    protected void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }
    protected int getFixedColWidth() {
        return fixedColWidth;
    }
    protected void setFixedColWidth(int width) {
        this.fixedColWidth = width;
        ellipsizeTexts();
    }
    protected int getColWidth() {
        return colWidth;
    }
    protected void setColWidth(int width) {
        this.colWidth = width;
    }
    protected int getFixedRowHeight() {
        return fixedRowHeight;
    }
    protected void setFixedRowHeight(int height) {
        this.fixedRowHeight = height;
    }
    protected int getTopRow() {
        return topRow;
    }
    protected int getLeftCol() {
        return leftCol;
    }
    protected int getOffsetX() {
        return offsetX;
    }
    protected int getOffsetY() {
        return offsetY;
    }
    protected int getColCount() {
        return colCount;
    }
    protected int getRowCount() {
        return rowCount;
    }
}

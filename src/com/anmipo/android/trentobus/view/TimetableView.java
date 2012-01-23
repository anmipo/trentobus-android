package com.anmipo.android.trentobus.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.anmipo.android.trentobus.db.Schedule;

public class TimetableView extends View implements OnGestureListener {
    static final String TAG = "Timetable";

	// width of the left fixed column with bus stop names
	public static final int FONT_SIZE_DP = 16;

	private String[] fixedCol;
	private String[] fixedRow;
	private String[][] cells;
	//cells dimensions
	private int rowCount;
	private int colCount;;

	private int width, height;   // view/canvas size
	private int fixedColWidth;   // width of the left (fixed) column
	private int borderWidth;     // border between cells
	private int halfBorderWidth; // borderWidth/2
	private int colWidth;        // cell columns width
	private int rowHeight = 40;  // height of all rows
	private int cellPaddingX = 5;// horizontal padding for all cells, in px

	private Paint fixedColumnPaint;
	private Paint fixedRowPaint;
	private Paint cellPaint; 

	private int topRow = 0;      // currently visible top row number
	private int leftCol = 0;     // currently visible left column number
	private int offsetX = 0;
	private int offsetY = 0;

	private GestureDetector gestureDetector;

	private Scroller scroller;


	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBorderWidth(2);
		
		gestureDetector = new GestureDetector(this);
		scroller = new Scroller(context);
		
		// TODO: remove this debug data
		setData(new String[]{"row1", "row2", "row3", "row4", "row5", "row6"},
				new String[]{"col1", "col2", "col3", "col4"},
				new String[][]
				{{"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "90:12", "34:56"}});
	}

	private void setupPaints() {
		float fontSizePixels = TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE_DP,
						getContext().getResources().getDisplayMetrics());
		fixedColumnPaint = new Paint();
		fixedColumnPaint.setTextAlign(Align.LEFT);
		fixedColumnPaint.setTextSize(fontSizePixels);
		fixedColumnPaint.setTypeface(Typeface.DEFAULT_BOLD);
		fixedColumnPaint.setAntiAlias(true);
		fixedColumnPaint.setStrokeWidth(borderWidth);
		
		fixedRowPaint = new Paint();
		fixedRowPaint.setTextAlign(Align.CENTER);
		fixedRowPaint.setTextSize(fontSizePixels);
		fixedRowPaint.setTypeface(Typeface.DEFAULT_BOLD);
		fixedRowPaint.setAntiAlias(true);
		fixedRowPaint.setStrokeWidth(borderWidth);
		
		cellPaint = new Paint();
		cellPaint.setColor(Color.BLACK);
		cellPaint.setTextSize(fontSizePixels);
		cellPaint.setStyle(Style.STROKE);
		cellPaint.setTextAlign(Align.LEFT);
		cellPaint.setAntiAlias(true);
		colWidth = (int)cellPaint.measureText("88:88") + 2 * cellPaddingX;
		rowHeight = (int)(1.5f * cellPaint.getTextSize());
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.halfBorderWidth = borderWidth/2;
		setupPaints();
	}
	public int getBorderWidth() {
		return borderWidth;
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
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		fixedColWidth = (int) measureFixedColumnWidth();
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
		result += 2 * cellPaddingX;
		return (result <= width/2) ? result : width/2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		debugDraw(canvas);
		drawFixedColumn(canvas);
		drawFixedRow(canvas);
		drawCells(canvas);
	}
	
	private void drawCells(Canvas canvas) {
		canvas.clipRect(fixedColWidth + borderWidth, rowHeight + halfBorderWidth,
				width, height, Op.REPLACE);
		float itemWidth = colWidth + borderWidth;
		float itemHeight = rowHeight + borderWidth;
		float textOffset = getTextCenterOffset(rowHeight, cellPaint);
		float x = fixedColWidth + borderWidth - offsetX;
		int xIndex = leftCol;
		while ((x < width) && (xIndex < colCount)) {
			int yIndex = topRow;
			float y = rowHeight + borderWidth - offsetY;
			while ((y < height) && (yIndex < rowCount)) {
				canvas.drawText(cells[yIndex][xIndex], 
						x + cellPaddingX, y + textOffset, 
						cellPaint);
				canvas.drawLine(x, y + rowHeight + halfBorderWidth, 
						x + colWidth + halfBorderWidth, y + rowHeight + halfBorderWidth, cellPaint);
				canvas.drawLine(x + colWidth + halfBorderWidth, y, 
						x + colWidth + halfBorderWidth, y + rowHeight + halfBorderWidth, cellPaint);
				y += itemHeight;
				yIndex++;
			}
			x += itemWidth;
			xIndex++;
		}
	}

	private void drawFixedRow(Canvas canvas) {
		canvas.clipRect(fixedColWidth + borderWidth, 0,
				width, rowHeight + borderWidth, Op.REPLACE);
		float textOffsetX = colWidth/2;
		float textOffsetY = getTextCenterOffset(rowHeight, fixedRowPaint);
		float x = fixedColWidth + borderWidth - offsetX;
		int index = leftCol;
		while ((x < width) && (index < colCount)) {
			canvas.drawText(fixedRow[index], 
					x + textOffsetX, textOffsetY, fixedRowPaint);
			x += colWidth + halfBorderWidth;
			canvas.drawLine(x, 0, x, rowHeight, fixedRowPaint);
			x += halfBorderWidth;
			index++;
		}
		// draw top-left empty corner 
		canvas.clipRect(0, 0, width, height, Op.REPLACE);
		canvas.drawLine(0, rowHeight + halfBorderWidth, 
				x, rowHeight + halfBorderWidth, fixedRowPaint);
		canvas.drawLine(fixedColWidth + halfBorderWidth, 0, 
				fixedColWidth + halfBorderWidth, rowHeight + halfBorderWidth, 
				fixedRowPaint);
	}

	private void drawFixedColumn(Canvas canvas) {
		canvas.clipRect(0, rowHeight, fixedColWidth + borderWidth, height, Op.REPLACE);
		float textOffsetY = getTextCenterOffset(rowHeight, fixedColumnPaint);
		float y = -offsetY + rowHeight + borderWidth; // skip the fixed row
		int index = topRow;
		while ((y < height) && (index < rowCount)) {
			canvas.drawText(fixedCol[index], 
					cellPaddingX, y + textOffsetY , 
					fixedColumnPaint);
			y += rowHeight + halfBorderWidth;
			canvas.drawLine(0, y, fixedColWidth, y, fixedColumnPaint);
			y += halfBorderWidth;
			index++;
		}
		canvas.drawLine(fixedColWidth + halfBorderWidth, 0, 
				fixedColWidth + halfBorderWidth, y, fixedColumnPaint);
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
	private static float getTextCenterOffset(int lineHeight, Paint paint) {
		/*
		 * Full form:
		 *     paint.descent() 
		 *         - (paint.descent() - paint.ascent())/2
		 *         + lineHeight/2;
		 */
		return (lineHeight - paint.ascent() - paint.descent())/2;
	}
	
	private void debugDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
	}

	/**
	 * Sets data for this table view.
	 * Dimensions of <code>cells</code> must correspond to these of 
	 * <code>fixedCol</code> and <code>fixedRow</code>, otherwise an
	 * {@link IllegalArgumentException} is thrown.
	 * Neither of dimensions can be zero (a {@link NullPointerException} 
	 * is thrown otherwise).
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
        onLayout(true, 0, 0, width, height);
        invalidate();
	}
	
	public void setSchedule(Schedule schedule) {
		setData(schedule.getStopNames(), 
				schedule.getLegends(), schedule.getTimes());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	public boolean onSingleTapUp(MotionEvent ev) {
//		Log.d("onSingleTapUp", ev.toString());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent ev) {
//		Log.d("onShowPress", ev.toString());
	}

	@Override
	public void onLongPress(MotionEvent ev) {
//		Log.d("onLongPress", ev.toString());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.d("onScroll", distanceX + ", " + distanceY);
		scrollBy((int) distanceX, (int) distanceY);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
//		Log.d("onDown", ev.toString());
		scroller.abortAnimation();
		return true;
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			offsetX = scroller.getCurrX();
			offsetY = scroller.getCurrY();
			postInvalidate();
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d("onFling", e1.toString() + ", " + e2.toString());
		scroller.fling(offsetX, offsetY, 
				(int) -velocityX, (int) -velocityY, 
				0, colCount * (colWidth + borderWidth), 
				0, rowCount * (rowHeight + borderWidth));
		
		return true;
	}
	
	public void scrollBy(int dx, int dy) {
		setOffsetX(offsetX + dx);
		setOffsetY(offsetY + dy);
		invalidate();
	}

	private void setOffsetY(int newOffsetY) {
		offsetY = newOffsetY;
	}

	private void setOffsetX(int newOffsetX) {
		offsetX = newOffsetX;
	}
}

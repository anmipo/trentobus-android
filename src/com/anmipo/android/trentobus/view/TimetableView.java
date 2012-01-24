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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.anmipo.android.trentobus.db.Schedule;

public class TimetableView extends View {
    static final String TAG = "Timetable";

	// width of the left fixed column with bus stop names
	public static final int FONT_SIZE_DP = 16;
	// horizontal padding for all cells, in px
	private static final int CELL_PADDING_X = 5;
	// default border width for fixed column/row
	private static final int DEFAULT_BORDER_WIDTH = 2;
	
	// table data
	private String[] fixedCol;
	private String[] fixedRow;
	private String[][] cells;
	
	// table dimensions
	private int rowCount;
	private int colCount;

	// paints for table parts
	private Paint fixedColumnPaint;
	private Paint fixedRowPaint;
	private Paint cellPaint; 
	private Paint cellBorderPaint; 

	// viewport
	private int topRow = 0;      // currently visible top row number
	private int leftCol = 0;     // currently visible left column number
	private int offsetX = 0;     // table shift in relation to the viewport
	private int offsetY = 0;     // table shift in relation to the viewport

	// drawing sizes / dimensions (calculated based on View size and paints)  
	private int width, height;   // view/canvas size
	private int fixedColWidth;   // width of the left (fixed) column
	private int borderWidth;     // border between cells
	private int halfBorderWidth; // borderWidth/2
	private int maxOffsetX;      // max allowed horizontal offset
	private int maxOffsetY;      // max allowed vertical offset
	private int colWidth;        // cell columns width
	private int rowHeight;       // height of all rows
	private int cellWidth;       // = colWidth + borderWidth	
	private int cellHeight;      // = rowHeight + borderWidth
	private int rowHeightAndHalfBorder; // = rowHeight + borderWidth / 2
	private int colWidthAndHalfBorder;  // = colWidth + borderWidth / 2

	private GestureDetector gestureDetector;
	private Scroller scroller;


	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBorderWidth(DEFAULT_BORDER_WIDTH);
		
		gestureDetector = new GestureDetector(context, new GestureListener());
		scroller = new Scroller(context);
		
		// TODO: remove this debug data
		setData(new String[]{"row1", "row2", "row3", "row4", "row5", "row6"},
				new String[]{"col1", "col2", "col3", "col4"},
				new String[][]
				{{"12:34", "56:78", "90:12", "34:56"},
				 {"12:34", "56:78", "*", "34:56"},
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
		
		cellBorderPaint = new Paint();
		cellBorderPaint.setColor(Color.GRAY);
		cellBorderPaint.setStyle(Style.STROKE);
		cellBorderPaint.setStrokeWidth(0.0f);
		cellBorderPaint.setAntiAlias(false);

		cellPaint = new Paint();
		cellPaint.setColor(Color.BLACK);
		cellPaint.setTextSize(fontSizePixels);
		cellPaint.setStyle(Style.STROKE);
		cellPaint.setTextAlign(Align.CENTER);
		cellPaint.setAntiAlias(true);
		
		colWidth = (int)cellPaint.measureText("88:88") + 2 * CELL_PADDING_X;
		rowHeight = (int)(1.5f * cellPaint.getTextSize());
		cellWidth = colWidth + borderWidth;
		cellHeight = rowHeight + borderWidth;
		rowHeightAndHalfBorder = rowHeight + halfBorderWidth;
		colWidthAndHalfBorder = colWidth + halfBorderWidth;
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
		maxOffsetX = colCount * cellWidth 
				- (width - fixedColWidth - borderWidth);
		maxOffsetY = rowCount * cellHeight - (height - cellHeight);
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
		debugDraw(canvas);
		drawFixedColumn(canvas);
		drawFixedRow(canvas);
		drawCells(canvas);
	}
	
	private void drawCells(Canvas canvas) {
		canvas.clipRect(
				fixedColWidth + borderWidth, rowHeightAndHalfBorder,
				width, height, Op.REPLACE);
		float textOffset = getTextCenterOffset(rowHeight, cellPaint);
		float cellCenterOffset = colWidth / 2;
		float x = fixedColWidth + borderWidth + cellWidth * leftCol - offsetX;
		float y0 = (topRow + 1) * cellHeight - offsetY;
		int xIndex = leftCol;
		while ((x < width) && (xIndex < colCount)) {
			int yIndex = topRow;
			//float y = rowHeight + borderWidth - offsetY;
			float y = y0;
			float cellRight = x + colWidthAndHalfBorder; 
			while ((y < height) && (yIndex < rowCount)) {
				float cellBottom = y + rowHeightAndHalfBorder;
				canvas.drawText(cells[yIndex][xIndex], 
						x + cellCenterOffset, y + textOffset, 
						cellPaint);
				canvas.drawLine(x, cellBottom, cellRight, cellBottom, 
						cellBorderPaint);
				canvas.drawLine(cellRight, y, cellRight, cellBottom, 
						cellBorderPaint);
				y += cellHeight;
				yIndex++;
			}
			x += cellWidth;
			xIndex++;
		}
	}

	private void drawFixedRow(Canvas canvas) {
		canvas.clipRect(fixedColWidth + borderWidth, 0,
				width, cellHeight, Op.REPLACE);
		float textOffsetX = colWidth / 2;
		float textOffsetY = getTextCenterOffset(rowHeight, fixedRowPaint);
		float x = fixedColWidth + borderWidth + cellWidth * leftCol - offsetX;
		int index = leftCol;
		while ((x < width) && (index < colCount)) {
			canvas.drawText(fixedRow[index], 
					x + textOffsetX, textOffsetY, fixedRowPaint);
			x += colWidthAndHalfBorder;
			canvas.drawLine(x, 0, x, rowHeight, fixedRowPaint);
			x += halfBorderWidth;
			index++;
		}
		// draw top-left empty corner 
		canvas.clipRect(0, 0, width, height, Op.REPLACE);
		canvas.drawLine(0, rowHeightAndHalfBorder, 
				x, rowHeightAndHalfBorder, fixedRowPaint);
		canvas.drawLine(fixedColWidth + halfBorderWidth, 0, 
				fixedColWidth + halfBorderWidth, rowHeightAndHalfBorder, 
				fixedRowPaint);
	}

	private void drawFixedColumn(Canvas canvas) {
		canvas.clipRect(0, rowHeight, fixedColWidth + borderWidth, height, 
				Op.REPLACE);
		float textOffsetY = getTextCenterOffset(rowHeight, fixedColumnPaint);
		// skip the fixed row and invisible entries
		float y = cellHeight * (topRow + 1) - offsetY;
		int index = topRow;
		while ((y < height) && (index < rowCount)) {
			canvas.drawText(fixedCol[index], CELL_PADDING_X, y + textOffsetY , 
					fixedColumnPaint);
			y += rowHeightAndHalfBorder;
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
		return (lineHeight - paint.ascent() - paint.descent()) / 2;
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
        postInvalidate();
	}
	
	public void setSchedule(Schedule schedule) {
		setData(schedule.getStopNames(), 
				schedule.getLegends(), schedule.getTimes());
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
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("onFling", velocityX + ", " + velocityY);
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

	private void setOffsetX(int newOffsetX) {
		if (newOffsetX < 0) {
			offsetX = 0;
		} else if (newOffsetX > maxOffsetX) {
			offsetX = maxOffsetX;
		} else {
			offsetX = newOffsetX;
		}
		leftCol = offsetX / (colWidth + borderWidth);
	}

	private void setOffsetY(int newOffsetY) {
		if (newOffsetY < 0) {
			offsetY = 0;
		} else if (newOffsetY > maxOffsetY) {
			offsetY = maxOffsetY;
		} else {
			offsetY = newOffsetY;
		}
		topRow = offsetY / (rowHeight + borderWidth);
	}
}

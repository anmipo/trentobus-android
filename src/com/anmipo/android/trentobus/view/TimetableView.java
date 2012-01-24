package com.anmipo.android.trentobus.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.anmipo.android.trentobus.R;
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
	private int maxOffsetX;      // max allowed horizontal offset
	private int maxOffsetY;      // max allowed vertical offset
	private int colWidth;        // cell columns width
	private int rowHeight;       // height of all rows
	
	private GestureDetector gestureDetector;
	private Scroller scroller;

	// background drawables for fixed and normal cells
	private Drawable fixedBackgroundDrawable;
	private Drawable fixedColumnEdgeDrawable;
	private Drawable cellBackgroundDrawable;

	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBorderWidth(DEFAULT_BORDER_WIDTH);
		initResources(context);
		
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

	private void initResources(Context context) {
		Resources res = context.getResources();
		fixedBackgroundDrawable = res.getDrawable(R.drawable.fixed_bg);
		fixedColumnEdgeDrawable = res.getDrawable(R.drawable.fixed_column_edge);
		cellBackgroundDrawable = res.getDrawable(R.drawable.cell_bg);
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
		
		fixedRowPaint = new Paint();
		fixedRowPaint.setTextAlign(Align.CENTER);
		fixedRowPaint.setTextSize(fontSizePixels);
		fixedRowPaint.setTypeface(Typeface.DEFAULT_BOLD);
		fixedRowPaint.setAntiAlias(true);
		
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
	}

	public void setBorderWidth(int borderWidth) {
		setupPaints();
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
		maxOffsetX = colCount * colWidth 
				- (width - fixedColWidth);
		maxOffsetY = rowCount * rowHeight - (height - rowHeight);
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
		// draw top-left empty corner 
		canvas.clipRect(0, 0, width, height, Op.REPLACE);
		fixedBackgroundDrawable.setBounds(0, 0, fixedColWidth, rowHeight);
		fixedBackgroundDrawable.draw(canvas);
	}
	
	private void drawCells(Canvas canvas) {
		canvas.clipRect(
				fixedColWidth, rowHeight,
				width, height, Op.REPLACE);
		int textOffset = getTextCenterOffset(rowHeight, cellPaint);
		int cellCenterOffset = colWidth / 2;
		int x = fixedColWidth + colWidth * leftCol - offsetX;
		int y0 = (topRow + 1) * rowHeight - offsetY;
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

	private void drawFixedRow(Canvas canvas) {
		canvas.clipRect(fixedColWidth, 0,
				width, rowHeight, Op.REPLACE);
		int textOffsetX = colWidth / 2;
		int textOffsetY = getTextCenterOffset(rowHeight, fixedRowPaint);
		int x = fixedColWidth + colWidth * leftCol - offsetX;
		int index = leftCol;
		while ((x < width) && (index < colCount)) {
			fixedBackgroundDrawable.setBounds(x, 0, x + colWidth, rowHeight);
			fixedBackgroundDrawable.draw(canvas);
			canvas.drawText(fixedRow[index], 
					x + textOffsetX, textOffsetY, fixedRowPaint);
			x += colWidth;
			index++;
		}
	}

	private void drawFixedColumn(Canvas canvas) {
		canvas.clipRect(0, rowHeight, fixedColWidth, height, 
				Op.REPLACE);
		int textOffsetY = getTextCenterOffset(rowHeight, fixedColumnPaint);
		int y = rowHeight * (topRow + 1) - offsetY;
		int index = topRow;
		while ((y < height) && (index < rowCount)) {
			Rect bounds = new Rect(0, y, fixedColWidth, (y + rowHeight));
			fixedBackgroundDrawable.setBounds(bounds);
			fixedColumnEdgeDrawable.setBounds(bounds);

			fixedBackgroundDrawable.draw(canvas);
			canvas.drawText(fixedCol[index], CELL_PADDING_X, y + textOffsetY , 
					fixedColumnPaint);
			fixedColumnEdgeDrawable.draw(canvas);
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
	private static int getTextCenterOffset(int lineHeight, Paint paint) {
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
		leftCol = offsetX / colWidth;
	}

	private void setOffsetY(int newOffsetY) {
		if (newOffsetY < 0) {
			offsetY = 0;
		} else if (newOffsetY > maxOffsetY) {
			offsetY = maxOffsetY;
		} else {
			offsetY = newOffsetY;
		}
		topRow = offsetY / rowHeight;
	}
}

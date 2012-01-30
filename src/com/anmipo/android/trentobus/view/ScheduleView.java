package com.anmipo.android.trentobus.view;

import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.anmipo.android.trentobus.db.Schedule;
import com.anmipo.android.trentobus.db.ScheduleLegend;

public class ScheduleView extends TimetableView {
	
	private ScheduleLegend[] legends;
	private Resources res;
	private int iconSize;
	
	public ScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		res = context.getResources();
		
		iconSize = ScheduleLegend.getIconSize(res);
		updateChildrenLayout();
	}
	
	public void setSchedule(Schedule schedule) {
		legends = schedule.getLegends();
		String[] fakeLegends = new String[legends.length];
		// none of the items can be null, so fill with empty strings
		Arrays.fill(fakeLegends, "");
		
		setData(schedule.getStopNames(), fakeLegends, 
				schedule.getTimesAsStrings());
	}

	@Override
	protected void setFixedRowHeight(int height) {
		// should be large enough for tapping
		super.setFixedRowHeight(2 * iconSize);
	}
	
	@Override
	protected void setColWidth(int width) {
		// the cells should fit the text and at least three icons
		super.setColWidth(Math.max(width, iconSize * 3));
	}
	
	@Override
	protected void drawFixedRow(Canvas canvas) {
		// parent draws background
		super.drawFixedRow(canvas);
		
		int width = canvas.getWidth();
		int fixedColWidth = getFixedColWidth();
		int colWidth = getColWidth();
		int colCount = getColCount();
		
		int y = (getFixedRowHeight() - iconSize) / 2;
		int x = fixedColWidth + colWidth * getLeftCol() - getOffsetX();
		int index = getLeftCol();
		while ((x < width) && (index < colCount)) {
			drawLegend(legends[index], canvas, x, y, colWidth);
			x += colWidth;
			index++;
		}
	}

	protected void drawLegend(ScheduleLegend legend, Canvas canvas, 
			int x, int y, int colWidth) {
		int legendLength = legend.getLength();
		// centering icons horizontally
		x += (colWidth - legendLength * iconSize) / 2;
		for (int i = 0; i < legendLength; i++) {
			Drawable d = res.getDrawable(legend.getItem(i).iconId);
			d.setBounds(x, y, x + iconSize, y + iconSize);
			d.draw(canvas);
			x += iconSize;
		}
	}
	
	public void scrollToColumn(int col) {
		setOffsetX(col * getColWidth());
	}
}

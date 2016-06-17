package hz.cdj.game.fmj.views;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.graphics.TextRender;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Style;

public class ScreenMessageBox extends BaseScreen {
	
	public interface OnOKClickListener {
		/**
		 * 选择“是”后被调用
		 */
		public void onOKClick();
	}
	
	private OnOKClickListener mOnOkClickListener;
	
	private static Bitmap bmpBg;
	
	static {
		bmpBg = Bitmap.createBitmap(137 - 27 + 1, 81 - 15 + 1, Config.ARGB_8888);
		Canvas c = new Canvas(bmpBg);
		c.drawColor(Global.COLOR_WHITE);
		Paint p = new Paint();
		p.setColor(Global.COLOR_BLACK);
		p.setStyle(Style.STROKE);
		c.drawRect(1, 1, bmpBg.getWidth() - 5, bmpBg.getHeight() - 5, p);
		c.drawRect(43 - 27, 51 - 15, 70 - 27, 70 - 15, p);
		c.drawRect(91 - 27, 51 - 15, 118 - 27, 70 - 15, p);
		p.setStyle(Style.FILL_AND_STROKE);
		c.drawRect(32 - 27, 77 - 15, 137 - 27, 81 - 15, p);
		c.drawRect(133 - 27, 20 - 15, bmpBg.getWidth() - 1, bmpBg.getHeight() - 1, p);
	}
	
	private int mIndex = 0;
	
	private String mMsg = "";
	
	public ScreenMessageBox(String msg, OnOKClickListener l) {
		mOnOkClickListener = l;
		if (msg != null) {
			mMsg = msg;
		}
	}

	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bmpBg, 27, 15, null);
		TextRender.drawText(canvas, mMsg, 33, 23);
		if (mIndex == 0) {
			TextRender.drawSelText(canvas, "是 ", 45, 53);
			TextRender.drawText(canvas, "否 ", 93, 53);
		} else if (mIndex == 1) {
			TextRender.drawText(canvas, "是 ", 45, 53);
			TextRender.drawSelText(canvas, "否 ", 93, 53);
		}
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_LEFT || key == Global.KEY_RIGHT) {
			mIndex = 1 - mIndex;
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_ENTER) {
			if (mIndex == 0 && mOnOkClickListener != null) {
				mOnOkClickListener.onOKClick();
			}
			exit();
		} else if (key == Global.KEY_CANCEL) {
			exit();
		}
	}
	
	private void exit() {
		GameView.getInstance().popScreen();
	}

	@Override
	public boolean isPopup() {
		return true;
	}

}

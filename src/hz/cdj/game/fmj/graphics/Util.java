package hz.cdj.game.fmj.graphics;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;

public class Util {
	
	public static void init() {
		Canvas canvas = new Canvas();
		Paint paint = new Paint();
		paint.setColor(Global.COLOR_WHITE);
		paint.setStyle(Style.FILL_AND_STROKE);
		
		if (bmpInformationBg == null) {
			bmpInformationBg = new Bitmap[5];
			for (int i = 0; i < 5; i++) {
				bmpInformationBg[i] = Bitmap.createBitmap(138, 23 + 16 * i, Config.ARGB_8888);
				canvas.setBitmap(bmpInformationBg[i]);
				canvas.drawColor(Global.COLOR_BLACK);
				canvas.drawRect(1, 1, 135, 20 + 16 * i, paint);
				canvas.drawRect(136, 0, 138, 3, paint);
				canvas.drawLine(0, 21 + 16 * i, 3, 21 + 16 * i, paint);
				canvas.drawLine(0, 22 + 16 * i, 3, 22 + 16 * i, paint);
			}
		}
		
		if (bmpSideFrame == null) {
			bmpSideFrame = Bitmap.createBitmap(8, 96, Bitmap.Config.ARGB_8888);
			canvas.setBitmap(bmpSideFrame);
			canvas.drawColor(Global.COLOR_WHITE);
			paint.setColor(Global.COLOR_BLACK);
			for (int i = 0; i < 8; i += 2) {
				canvas.drawLine(i, 0, i, 96, paint);
			}
		}
		
		if (bmpTriangleCursor == null) {
			bmpTriangleCursor = Bitmap.createBitmap(7, 13, Config.ARGB_8888);
			canvas.setBitmap(bmpTriangleCursor);
			canvas.drawColor(Global.COLOR_WHITE);
			for (int i = 0; i < 7; ++i) {
				canvas.drawLine(i, i, i, 13 - i, paint);
			}
		}
		
		if (imgSmallNum == null) {
			imgSmallNum = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 5);
		}
		
		if (bmpChuandai == null) {
			bmpChuandai = Bitmap.createBitmap(22, 39, Config.ARGB_8888);
			int b = Global.COLOR_BLACK, w = Global.COLOR_WHITE;
			int[] pixels = {
					w,w,w,w,w,w,w,w,w,b,b,b,w,w,w,w,w,w,w,w,w,w,
					w,w,w,b,b,w,w,b,b,b,b,b,b,b,b,b,b,b,b,b,w,w,
					w,w,b,b,b,b,b,w,w,w,w,w,w,w,w,b,b,b,b,b,w,w,
					w,w,b,b,w,w,w,b,b,b,w,w,b,b,b,w,w,w,b,b,w,w,
					w,w,w,b,w,w,b,w,w,w,w,w,w,w,w,w,w,w,b,b,w,w,
					w,w,w,b,w,w,b,b,b,b,b,b,b,b,b,b,b,w,w,w,w,w,
					w,w,w,w,w,w,w,b,w,w,w,w,b,b,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,
					w,w,w,w,w,w,b,b,w,w,w,b,b,b,b,w,b,b,b,b,b,b,
					w,w,w,w,w,w,w,w,w,b,b,b,b,b,b,w,w,w,b,b,b,w,
					w,w,w,w,w,w,b,b,b,b,w,w,b,b,b,w,w,w,w,w,w,w,
					b,b,b,b,b,b,b,b,w,w,w,w,b,b,b,w,w,w,w,w,w,w,
					w,b,b,b,b,w,w,w,w,w,w,w,b,b,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,b,b,b,b,b,b,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,b,b,b,b,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,
					w,w,w,w,w,w,b,w,w,w,w,b,b,w,w,w,b,w,w,w,w,w,
					w,w,w,b,b,b,b,b,b,b,w,b,b,w,w,b,b,w,w,w,w,w,
					w,w,b,b,w,w,b,w,w,w,w,b,b,w,b,w,w,w,w,w,w,w,
					w,w,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,w,w,w,w,
					b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,b,w,w,w,w,w,
					w,w,w,b,b,b,b,b,b,b,w,b,b,w,b,b,w,w,w,w,w,w,
					w,w,w,b,b,w,b,b,b,b,w,b,b,w,b,b,w,w,w,w,w,w,
					w,w,w,b,w,b,b,w,w,b,w,w,b,w,b,b,w,w,w,w,w,w,
					w,w,w,b,b,b,b,b,b,b,w,w,b,b,b,b,w,w,w,w,w,w,
					w,w,w,b,b,b,b,b,b,b,w,w,b,b,b,w,w,w,w,w,w,w,
					w,w,w,w,b,b,b,b,b,w,w,w,w,b,b,b,w,w,w,w,w,w,
					b,b,b,b,b,w,w,w,w,b,b,b,b,b,b,b,b,b,b,w,w,w,
					w,w,w,b,b,b,w,w,b,b,w,b,b,w,w,b,b,b,b,b,b,b,
					w,w,b,b,w,w,w,w,w,w,b,w,w,w,w,w,b,b,b,b,b,w,
					w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,w,b,b,w,w,
			};
			bmpChuandai.setPixels(pixels, 0, 22, 0, 0, 22, 39);
		}
	}
	
	// 显示message的方框
	private static Bitmap[] bmpInformationBg;
	
	// 用于showscenename
	public static void showInformation(Canvas canvas, String msg) {
		canvas.drawBitmap(bmpInformationBg[0], 11, 37, null);
		TextRender.drawText(canvas, msg, 16, 39);
	}
	
	// 显示message,每行最多显示8个汉字，最多可显示5行
	public static void showMessage(Canvas canvas, String msg) {
		try {
			byte[] text = msg.getBytes("GBK");
			int lineNum = text.length / 16; // 所需行数
			if (lineNum >= 5) lineNum = 4;
			int textY = 39 - lineNum * 8;
			canvas.drawBitmap(bmpInformationBg[lineNum], 11, textY - 2, null);
			TextRender.drawText(canvas, text, 0, new Rect(16, textY, 16 + 16 * 8, textY + 16 * lineNum + 16));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// 显示message,每行最多显示8个汉字，最多可显示5行
	public static void showMessage(Canvas canvas, byte[] msg) {
		int lineNum = msg.length / 16;
		if (lineNum >= 5) lineNum = 4;
		int textY = 39 - lineNum * 8;
		canvas.drawBitmap(bmpInformationBg[lineNum], 11, textY - 2, null);
		TextRender.drawText(canvas, msg, 0, new Rect(16, textY, 16 + 16 * 8, textY + 16 * lineNum + 16));
	}
	
	// 屏幕两边留白
	private static Bitmap bmpSideFrame;
	public static void drawSideFrame(Canvas canvas) {
		canvas.drawBitmap(bmpSideFrame, 0, 0, null);
		canvas.drawBitmap(bmpSideFrame, 152, 0, null);
	}

	// 用于菜单的矩形框，黑框白边
	private static Paint drawFramePaint;
	static {
		drawFramePaint = new Paint();
		drawFramePaint.setColor(Global.COLOR_BLACK);
		drawFramePaint.setStyle(Paint.Style.STROKE);
	}
	public static Bitmap getFrameBitmap(int w, int h) {
		// 先创建Bitmap
		Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas tmpC = new Canvas(bmp);
		tmpC.drawColor(Global.COLOR_WHITE);
		tmpC.drawRect(1, 1, w - 2, h - 2, drawFramePaint);
		return bmp;
	}

	/**
	 * 三角形。
	 */
	private static Bitmap bmpTriangleCursor;
	public static void drawTriangleCursor(Canvas canvas, int x, int y) {
		canvas.drawBitmap(bmpTriangleCursor, x, y, null);
	}
	
	private static ResImage imgSmallNum;
	/**
	 * 
	 * @return 画出的num宽度(像素)
	 */
	public static int drawSmallNum(Canvas canvas, int num, int x, int y) {
		if (num < 0) num = -num;
		byte[] nums = Integer.toString(num).getBytes();
		
		for (int i = 0; i < nums.length; i++) {
			imgSmallNum.draw(canvas, nums[i] - '0' + 1, x, y);
			x += imgSmallNum.getWidth() + 1;
		}
		
		return nums.length * imgSmallNum.getWidth();
	}
	
	public static Bitmap getSmallSignedNumBitmap(int num) {
		byte[] nums = Integer.toString(num > 0 ? num : -num).getBytes();
		ResImage sign = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, num > 0 ? 6 : 7);
		Bitmap bmp = Bitmap.createBitmap(sign.getWidth() + nums.length * imgSmallNum.getWidth() + 1 + nums.length,
				imgSmallNum.getHeight(), Config.ARGB_8888);
		
		Canvas c = new Canvas(bmp);
		sign.draw(c, 1, 0, 0);

		int x = sign.getWidth() + 1;
		for (int i = 0; i < nums.length; i++) {
			imgSmallNum.draw(c, nums[i] - '0' + 1, x, 0);
			x += imgSmallNum.getWidth() + 1;
		}
		
		return bmp;
	}
	
	public static void showMessage(final String msg, final long delay) {
		GameView.getInstance().pushScreen(new BaseScreen() {
			long cnt = 0;
			
			@Override
			public void update(long delta) {
				cnt += delta;
				if (cnt > delay) {
					GameView.getInstance().popScreen();
				}
			}
			
			@Override
			public void draw(Canvas canvas) {
				showMessage(canvas, msg);
			}
			
			@Override
			public void onKeyUp(int key) {
			}
			
			@Override
			public void onKeyDown(int key) {
				GameView.getInstance().popScreen();
			}

			@Override
			public boolean isPopup() {
				return true;
			}
			
		});
	}

	/**
	 * 穿戴
	 */
	public static Bitmap bmpChuandai;
	
	public static Paint sBlackPaint;
	static {
		sBlackPaint = new Paint();
		sBlackPaint.setColor(Global.COLOR_BLACK);
		sBlackPaint.setStyle(Style.STROKE);
		sBlackPaint.setStrokeWidth(1);
	}
	
}

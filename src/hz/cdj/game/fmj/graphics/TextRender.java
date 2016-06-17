package hz.cdj.game.fmj.graphics;

import hz.cdj.game.fmj.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class TextRender {
	private static byte[] mHZKBuf;
	private static byte[] mASCBuf;
	
	private static int[] mPixels;
	private static Bitmap mBmpHzk;
	private static Bitmap mBmpAsc;

	
	public static void init() {
		
		InputStream in = null;
		
		try {
			if (mHZKBuf == null) {
				in = new FileInputStream(new File("./assets/HZK16"));
				mHZKBuf = new byte[in.available()];
				in.read(mHZKBuf);
				in.close();
			}
			
			if (mASCBuf == null) {
				in = new FileInputStream(new File("./assets/ASC16"));//astman.open("ASC16");
				mASCBuf = new byte[128 * 16];
				in.read(mASCBuf);
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mBmpHzk = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
		mBmpAsc = Bitmap.createBitmap(8, 16, Bitmap.Config.ARGB_8888);
		mPixels = new int[16 * 16];
	}
	
	public static void drawText(Canvas canvas, String text, int x, int y) {
		try {
			drawText(canvas, text.getBytes("GBK"), x, y);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void drawSelText(Canvas canvas, String text, int x, int y) {
		int tmpColor = Global.COLOR_BLACK;
		Global.COLOR_BLACK = Global.COLOR_WHITE;
		Global.COLOR_WHITE = tmpColor;
		drawText(canvas, text, x, y);
		tmpColor = Global.COLOR_BLACK;
		Global.COLOR_BLACK = Global.COLOR_WHITE;
		Global.COLOR_WHITE = tmpColor;
	}
	
	public static void drawSelText(Canvas canvas, byte[] text, int x, int y) {
		int tmpColor = Global.COLOR_BLACK;
		Global.COLOR_BLACK = Global.COLOR_WHITE;
		Global.COLOR_WHITE = tmpColor;
		drawText(canvas, text, x, y);
		tmpColor = Global.COLOR_BLACK;
		Global.COLOR_BLACK = Global.COLOR_WHITE;
		Global.COLOR_WHITE = tmpColor;
	}
	
	public static void drawText(Canvas canvas, byte[] text, int x, int y) {
		for (int i = 0; i < text.length && text[i] != 0; i++) {
			int t = (int)text[i] & 0xFF;
			if (t >= 0xa1) {
				++i;
				int offset = (94 * (t - 0xa1) + ((int)text[i] & 0xFF) - 0xa1) * 32;
				canvas.drawBitmap(getHzk(offset), x, y, null);
				x += 16;
			} else if (t < 128) {
				int offset = t * 16;
				canvas.drawBitmap(getAsc(offset), x, y, null);
				x += 8;
			} else {
				x += 8;
			}
		}
	}
	
	/**
	 * 
	 * @param canvas
	 * @param text
	 * @param r
	 * @param y
	 * @return 0,文字都在r.top上方
	 * 1,文字在r中
	 * 2,文字都在r.bottom下方
	 * -1,出错
	 */
	public static int drawText(Canvas canvas, String text, Rect r, int y) {
		byte[] buf = null;
		try {
			buf = text.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return -1;
		}
		
		int i = 0;
		// 比r.top高的不画
		for (; y <= r.top - 16 && i < buf.length; y += 16) {
			for (int x = 0; x < 160 && i < buf.length;) {
				int t = (int)buf[i] & 0xFF;
				if (t >= 0xa1) {
					i += 2;
					x += 16;
				} else {
					++i;
					x += 8;
				}
			}
		}
		
		if (i >= buf.length) {
			return 0;
		}
		
		// 比r.bottom低的不画
		for (; y < r.bottom && i < buf.length; y += 16) {
			for (int x = 0; x < 160 && i < buf.length;) {
				int t = (int)buf[i] & 0xFF;
				if (t >= 0xa1) {
					++i;
					int offset = (94 * (t - 0xa1) + ((int)buf[i] & 0xFF) - 0xa1) * 32;
					canvas.drawBitmap(getHzk(offset), x, y, null);
					x += 16;
				} else if (t < 128) {
					int offset = t * 16;
					canvas.drawBitmap(getAsc(offset), x, y, null);
					x += 8;
				} else {
					x += 8;
				}
				++i;
			}
		}
		
		if (i == 0 && buf.length > 0) {
			return 2;
		}
		
		return 1;
	}

	/**
	 * call drawText(Canvas, byte[], int, Rect)
	 */
	@Deprecated
	public static int drawText(Canvas canvas, String text, int start, Rect r) {
		try {
			byte[] buf = text.getBytes("GBK");
			return drawText(canvas, buf, start, r);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @param canvas
	 * @param buf
	 * @param start buf中第一个要画的字节
	 * @param r
	 * @return 下一个要画的字节
	 */
	public static int drawText(Canvas canvas, byte[] buf, int start, Rect r) {
		int i = start;
		int y = r.top;
		// 比r.bottom低的不画
		for (; y <= r.bottom - 16 && i < buf.length; y += 16) {
			for (int x = r.left; x <= r.right - 16 && i < buf.length;) {
				int t = (int)buf[i] & 0xFF;
				if (t >= 0xa1) {
					++i;
					int offset = (94 * (t - 0xa1) + ((int)buf[i] & 0xFF) - 0xa1) * 32;
					canvas.drawBitmap(getHzk(offset), x, y, null);
					x += 16;
				} else if (t < 128) {
					int offset = t * 16;
					canvas.drawBitmap(getAsc(offset), x, y, null);
					x += 8;
				} else {
					x += 8;
				}
				++i;
			}
		}
		
		return i;
	}
	
	private static Bitmap getHzk(int offset) {
		for (int i = 0; i < 32; i++) {
			int t = mHZKBuf[offset + i];
			int k = i << 3;
			mPixels[k    ] = (t & 0x80) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 1] = (t & 0x40) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 2] = (t & 0x20) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 3] = (t & 0x10) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 4] = (t & 0x08) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 5] = (t & 0x04) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 6] = (t & 0x02) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 7] = (t & 0x01) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
		}
		mBmpHzk.setPixels(mPixels, 0, 16, 0, 0, 16, 16);
		return mBmpHzk;
	}
	
	private static Bitmap getAsc(int offset) {
		for (int i = 0; i < 16; i++) {
			int t = mASCBuf[offset + i];
			int k = i << 3;
			mPixels[k    ] = (t & 0x80) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 1] = (t & 0x40) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 2] = (t & 0x20) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 3] = (t & 0x10) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 4] = (t & 0x08) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 5] = (t & 0x04) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 6] = (t & 0x02) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
			mPixels[k | 7] = (t & 0x01) != 0 ? Global.COLOR_BLACK : Global.COLOR_WHITE;
		}
		mBmpAsc.setPixels(mPixels, 0, 8, 0, 0, 8, 16);
		return mBmpAsc;
	}
}

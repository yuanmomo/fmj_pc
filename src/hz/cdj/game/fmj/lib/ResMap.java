package hz.cdj.game.fmj.lib;

import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Tiles;
import hz.cdj.game.fmj.graphics.Util;

import java.io.UnsupportedEncodingException;

import android.graphics.Canvas;

public class ResMap extends ResBase {
	/**
	 * 横向渲染的地图块总数
	 */
	public static final int WIDTH = 160 / 16 - 1;

	/**
	 * 纵向渲染的地图块总数
	 */
	public static final int HEIGHT = 96 / 16;

	/**
	 * 该地图所用的til图块资源的索引号
	 */
	private int mTilIndex;

	/**
	 * 地图名称
	 */
	private String mName;

	/**
	 * 地图宽
	 */
	private int mWidth;

	/**
	 * 地图高
	 */
	private int mHeight;

	/**
	 * 地图数据 两个字节表示一个地图快（从左到右，从上到下）
	 * （低字节：最高位1表示可行走，0不可行走。高字节：事件号）
	 */
	private byte[] mData;

	/**
	 * 地图使用的地图块
	 */
	private Tiles mTiles;

	@Override
	public void setData(byte[] buf, int offset) {
		mType = buf[offset];
		mIndex = buf[offset + 1];
		mTilIndex = buf[offset + 2];
		try {
			int i = 0;
			while (buf[offset + 3 + i] != 0)
				++i;
			mName = new String(buf, offset + 3, i, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mWidth = buf[offset + 0x10];
		mHeight = buf[offset + 0x11];

		int len = mWidth * mHeight * 2;
		mData = new byte[len];
		System.arraycopy(buf, offset + 0x12, mData, 0, len);
	}
	
	/**
	 * 判断地图(x,y)是否可行走
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canWalk(int x, int y) {
		if (x < 0 || x >= mWidth || y < 0 || y >= mHeight) {
			return false;
		}
		
		int i = y * mWidth + x;
		return (mData[i * 2] & 0x80) != 0;
	}
	
	public boolean canPlayerWalk(int x, int y) {
		return canWalk(x, y) && (x >= 4) && (x < mWidth - 4)
				&& (y >= 3) && (y < mHeight - 2);
	}
	
	public int getEventNum(int x, int y) {
		if (x < 0 || x >= mWidth || y < 0 || y >= mHeight) {
			return -1;
		}
		
		int i = y * mWidth + x;
		return (int)mData[i * 2 + 1] & 0xFF;
	}

	/**
	 * 
	 * @param x
	 *            图块的x坐标
	 * @param y
	 *            图块的y坐标
	 * @return map中(x, y)位置的图块在til中的序号
	 */
	private int getTileIndex(int x, int y) {
		int i = y * mWidth + x;
		return (int)mData[i * 2] & 0x7F;
	}

	/**
	 * 水平方向 left --- left+WIDTH <br>
	 * 竖直方向 top --- top + HEIGHT
	 * 
	 * @param canvas
	 * @param left
	 *            地图的最左边
	 * @param top
	 *            地图的最上边
	 */
	public void drawMap(Canvas canvas, int left, int top) {
		if (mTiles == null) {
			mTiles = new Tiles(mTilIndex);
		}

		int minY = Math.min(HEIGHT, mHeight - top);
		int minX = Math.min(WIDTH, mWidth - left);
		for (int y = 0; y < minY; y++) {
			for (int x = 0; x < minX; x++) {
				mTiles.draw(canvas, x * Tiles.WIDTH + Global.MAP_LEFT_OFFSET,
						y * Tiles.HEIGHT, getTileIndex(left + x, top + y));
			}
		}
	}
	
	public void drawWholeMap(Canvas canvas, int x, int y) {
		if (mTiles == null) {
			mTiles = new Tiles(mTilIndex);
		}

		for (int ty = 0; ty < mHeight; ty++) {
			for (int tx = 0; tx < mWidth; tx++) {
				int sx = tx * Tiles.WIDTH + x;
				int sy = ty * Tiles.HEIGHT + y;
				mTiles.draw(canvas, sx, sy, getTileIndex(tx, ty));
				int event = getEventNum(tx, ty);
				if (event != 0) {
					int color = Global.COLOR_WHITE;
					Global.COLOR_WHITE = 0xFFFF0000;
					TextRender.drawText(canvas, "" + event, sx, sy);
					Global.COLOR_WHITE = color;
				}
			}
		}
	}
	
	public int getMapWidth() {
		return mWidth;
	}
	
	public int getMapHeight() {
		return mHeight;
	}
	
	public String getMapName() {
		return mName;
	}
}

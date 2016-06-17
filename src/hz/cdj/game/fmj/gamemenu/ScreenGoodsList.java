package hz.cdj.game.fmj.gamemenu;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

public class ScreenGoodsList extends BaseScreen {
	
	public enum Mode {
		Sale,
		Buy,
		Use,
	}
	
	private Mode mMode;
	
	public interface OnItemSelectedListener {
		void onItemSelected(BaseGoods goods);
	}
	
	private OnItemSelectedListener mOnItemSelectedListener;
	
	private List<BaseGoods> mGoodsList;
	
	private static Bitmap sbmpBg;
	
	private static Rect mRectGoodsDsp;
	
	private byte[] mDescription;
	private int mToDraw = 0; // 当前要画的描述中的字节
	private int mNextToDraw = 0; // 下一个要画的描述中的字节
	private Stack<Integer> mStackLastToDraw = new Stack<Integer>(); // 保存上次描述所画位置

	private static final int ITEM_NUM = 4; // 界面上显示的条目数
	
	private int mFirstItemIndex = 0; // 界面上显示的第一个物品的序号
	
	private int mCurItemIndex = 0; // 当前光标所在位置物品的序号
	
	public ScreenGoodsList(List<BaseGoods> list, OnItemSelectedListener l, Mode m) {
		if (list == null || l == null) {
			throw new IllegalArgumentException("ScreenGoodsList construtor params can't be null.");
		}
		
		mGoodsList = list;
		mOnItemSelectedListener = l;
		mMode = m;
		
		if (list.size() > 0) {
			mDescription = getGBKBytes(list.get(0).getDescription());
		}
		
		if (sbmpBg == null) {
			sbmpBg = Bitmap.createBitmap(160, 96, Config.ARGB_8888);
			float[] pts = {40,21, 40,95, 40,95, 0,95, 0,95, 0,5, 0,5, 5,0, 5,0, 39,0,
					39,0, 58,19, 38,0, 57,19, 57,19, 140,19, 41,20, 140,20, 41,21, 159,21,
					54,0, 140,0, 40,95, 159,95, 40,57, 160,57, 40,58, 140,58, 40,59, 159,59,
					41,20, 41,95, 42,20, 42,95, 159,21, 159,57, 159,59, 159,96};
			Canvas c = new Canvas(sbmpBg);
			c.drawColor(Global.COLOR_WHITE);
			c.drawLines(pts, Util.sBlackPaint);
			TextRender.drawText(c, "名:", 45, 23);
			TextRender.drawText(c, "价:", 45, 40);
			
			mRectGoodsDsp = new Rect(44, 61, 156, 94);
		}
	}
	
	public static byte[] getGBKBytes(String s) {
		try {
			return s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public void update(long delta) {
		if (mGoodsList.size() <= 0) {
			GameView.getInstance().popScreen();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(sbmpBg, 0, 0, null);
		if (mGoodsList.size() <= 0) return;

		while (mCurItemIndex >= mGoodsList.size()) showPreItem();
		
		BaseGoods g = mGoodsList.get(mCurItemIndex);
		TextRender.drawText(canvas, mMode == Mode.Buy ? "金钱:" + Player.sMoney : "数量:" + g.getGoodsNum(), 60, 2);
		TextRender.drawText(canvas, g.getName(), 69, 23);
		TextRender.drawText(canvas, "" + (mMode == Mode.Buy ? g.getBuyPrice() : g.getSellPrice()), 69, 40);
		Util.drawTriangleCursor(canvas, 4, 8 + 23 * (mCurItemIndex - mFirstItemIndex));
		
		for (int i = mFirstItemIndex; i < mFirstItemIndex + ITEM_NUM && i < mGoodsList.size(); i++) {
			mGoodsList.get(i).draw(canvas, 14, 2 + 23 * (i - mFirstItemIndex));
		}
		
		mNextToDraw = TextRender.drawText(canvas, mDescription, mToDraw, mRectGoodsDsp);
	}
	
	private void showNextItem() {
		++mCurItemIndex;
		mDescription = getGBKBytes(mGoodsList.get(mCurItemIndex).getDescription());
		if (mCurItemIndex >= mFirstItemIndex + ITEM_NUM) {
			++mFirstItemIndex;
		}
		mToDraw = mNextToDraw = 0;
		mStackLastToDraw.clear();
	}
	
	private void showPreItem() {
		--mCurItemIndex;
		mDescription = getGBKBytes(mGoodsList.get(mCurItemIndex).getDescription());
		if (mCurItemIndex < mFirstItemIndex) {
			--mFirstItemIndex;
		}
		mToDraw = mNextToDraw = 0;
		mStackLastToDraw.clear();
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP && mCurItemIndex > 0) {
			showPreItem();
		} else if (key == Global.KEY_DOWN && mCurItemIndex + 1 < mGoodsList.size()) {
			showNextItem();
		} else if (key == Global.KEY_PAGEDOWN) {
			int len = mDescription.length;
			if (mNextToDraw < len) {
				mStackLastToDraw.push(mToDraw);
				mToDraw = mNextToDraw;
			}
		} else if (key == Global.KEY_PAGEUP && mToDraw != 0) {
			if (!mStackLastToDraw.isEmpty()) {
				mToDraw = mStackLastToDraw.pop();
			}
		}
		mLastDownKey = key;
	}
	
	private int mLastDownKey = -1;

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_ENTER && mLastDownKey == Global.KEY_ENTER) {
			mOnItemSelectedListener.onItemSelected(mGoodsList.get(mCurItemIndex));
		} else if (key == Global.KEY_CANCEL) {
			GameView.getInstance().popScreen();
		}
	}

}

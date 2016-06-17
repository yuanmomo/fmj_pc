package hz.cdj.game.fmj.combat.ui;

import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.characters.ResLevelupChain;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class CombatSuccess {
	
	private List<BaseGoods> mGoodsList;
	
	private List<BaseScreen> mMsgList;
	
	private List<BaseScreen> mLvupList;
	
	private long mCnt;
	
	private boolean mIsAnyKeyPressed = false;

	public CombatSuccess(int exp, int money, List<BaseGoods> goodslist, List<Player> lvuplist) {
		mGoodsList = goodslist;
		mMsgList = new LinkedList<BaseScreen>();
		String e = String.valueOf(exp);
		mMsgList.add(new MsgScreen(18, "获得经验     ".substring(0, 9 - e.length()) + e));
		String m = String.valueOf(money);
		mMsgList.add(new MsgScreen(46, "战斗获得        ".substring(0, 10 - m.length()) + m + "钱"));
		
		mLvupList = new LinkedList<BaseScreen>();
		for (Player p : lvuplist) {
			mLvupList.add(new MsgScreen(p.getName() + "修行提升"));
			mLvupList.add(new LevelupScreen(p));
			if (p.getLevelupChain().getLearnMagicNum(p.getLevel()) >
			p.getLevelupChain().getLearnMagicNum(p.getLevel() - 1)) {
				mLvupList.add(new LearnMagicScreen(p.getName(),
						p.getMagicChain().getMagic(p.getLevelupChain().getLearnMagicNum(p.getLevel()) - 1).getMagicName()));
			}
		}
	}

	/**
	 * 
	 * @param delta
	 * @return <code>true</code> 所以内容显示完毕
	 */
	public boolean update(long delta) {
		mCnt += delta;
		if (mCnt > 1000 || mIsAnyKeyPressed) {
			mCnt = 0;
			mIsAnyKeyPressed = false;
			if (mGoodsList.size() == 0) {
				if (mLvupList.size() == 0) {
					return true;
				} else {
					mMsgList.add(mLvupList.remove(0));
				}
			} else {
				BaseGoods g = mGoodsList.remove(0);
				mMsgList.add(new MsgScreen("得到 " + g.getName() + " x" + g.getGoodsNum()));
			}
		}
		return false;
	}

	public void draw(Canvas canvas) {
		for (BaseScreen s : mMsgList) {
			s.draw(canvas);
		}
	}

	public void onKeyDown(int key) {
	}

	public void onKeyUp(int key) {
		mIsAnyKeyPressed = true;
	}
	
	/**
	 * 显示战斗胜利后玩家获得的东西
	 * @author Chen
	 *
	 */
	private class MsgScreen extends BaseScreen {
		
		private Bitmap mMsg;
		
		private int mX, mY;
		
		public MsgScreen(String msg) {
			this((96 - 24) / 2, msg);
		}
		
		public MsgScreen(int y, String _msg) {
			byte[] msg;
			try {
				msg = _msg.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				msg = new byte[0];
			}
			
			ResImage side = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 8);
			mMsg = Bitmap.createBitmap(msg.length * 8 + 8, 24, Config.ARGB_8888);
			Canvas c = new Canvas(mMsg);
			c.drawColor(Global.COLOR_WHITE);
			side.draw(c, 1, 0, 0);
			side.draw(c, 2, mMsg.getWidth() - 3, 0);
			Paint p = new Paint();
			p.setColor(Global.COLOR_BLACK);
			p.setStyle(Style.FILL_AND_STROKE);
			c.drawLine(0, 1, mMsg.getWidth(), 1, p);
			c.drawLine(0, 22, mMsg.getWidth(), 22, p);
			TextRender.drawText(c, msg, 4, 4);
			
			mX = (160 - mMsg.getWidth()) / 2;
			mY = y;
		}

		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mMsg, mX, mY, null);
		}

		@Override
		public void onKeyDown(int key) {
		}

		@Override
		public void onKeyUp(int key) {
		}
		
	}
	
	private class LevelupScreen extends BaseScreen {
		
		private Bitmap mInfo;
		
		public LevelupScreen(Player p) {
			ResImage ri = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 9);
			mInfo = ri.getBitmap(0);
			
			Canvas canvas = new Canvas(mInfo);
			ResLevelupChain lc = p.getLevelupChain();
			int curl = p.getLevel();
			Util.drawSmallNum(canvas, p.getHP(), 37, 9); p.setHP(p.getMaxHP());
			Util.drawSmallNum(canvas, p.getMaxHP() - (lc.getMaxHP(curl) - lc.getMaxHP(curl - 1)), 56, 9);
			Util.drawSmallNum(canvas, p.getMaxHP(), 86, 9);
			Util.drawSmallNum(canvas, p.getMaxHP(), 105, 9);
			Util.drawSmallNum(canvas, p.getMP(), 37, 21); p.setMP(p.getMaxMP());
			Util.drawSmallNum(canvas, p.getMaxMP() - (lc.getMaxMP(curl) - lc.getMaxMP(curl - 1)), 56, 21);
			Util.drawSmallNum(canvas, p.getMaxMP(), 86, 21);
			Util.drawSmallNum(canvas, p.getMaxMP(), 105, 21);
			Util.drawSmallNum(canvas, p.getAttack() - (lc.getAttack(curl) - lc.getAttack(curl - 1)), 47, 33);
			Util.drawSmallNum(canvas, p.getAttack(), 96, 33);
			Util.drawSmallNum(canvas, p.getDefend() - (lc.getDefend(curl) - lc.getDefend(curl - 1)), 47, 45);
			Util.drawSmallNum(canvas, p.getDefend(), 96, 45);
			Util.drawSmallNum(canvas, p.getSpeed() - (lc.getSpeed(curl) - lc.getSpeed(curl - 1)), 47, 57);
			Util.drawSmallNum(canvas, p.getSpeed(), 96, 57);
			Util.drawSmallNum(canvas, p.getLingli() - (lc.getLingli(curl) - lc.getLingli(curl - 1)), 47, 69);
			Util.drawSmallNum(canvas, p.getLingli(), 96, 69);
			Util.drawSmallNum(canvas, p.getLuck() - (lc.getLuck(curl) - lc.getLuck(curl - 1)), 47, 81);
			Util.drawSmallNum(canvas, p.getLuck(), 96, 81);
		}

		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mInfo, (160 - mInfo.getWidth()) / 2, (96 - mInfo.getHeight()) / 2, null);
		}

		@Override
		public void onKeyDown(int key) {
		}

		@Override
		public void onKeyUp(int key) {
		}
		
	}
	
	private class LearnMagicScreen extends BaseScreen {
		
		private Bitmap mInfo;
		
		public LearnMagicScreen(String playerName, String magicName) {
			mInfo = ((ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 10)).getBitmap(0);
			byte[] pn;
			byte[] mn;
			try {
				pn = playerName.getBytes("GBK");
				mn = magicName.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				pn = new byte[0];
				mn = new byte[0];
			}
			Canvas canvas = new Canvas(mInfo);
			TextRender.drawText(canvas, pn, (mInfo.getWidth() - pn.length * 8) / 2, 8);
			TextRender.drawText(canvas, mn, (mInfo.getWidth() - mn.length * 8) / 2, 42);
		}

		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mInfo, (160 - mInfo.getWidth()) / 2, (96 - mInfo.getHeight()) / 2, null);
		}

		@Override
		public void onKeyDown(int key) {
		}

		@Override
		public void onKeyUp(int key) {
		}
		
	}
	

}

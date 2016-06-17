package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResBase;
import hz.cdj.game.fmj.lib.ResImage;
import android.graphics.Canvas;

public abstract class BaseGoods extends ResBase {
	
	protected abstract void setOtherData(byte[] buf, int offset);

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xFF;
		mIndex = (int)buf[offset + 1] & 0xFF;
		mEnable = (int)buf[offset + 3] & 0xFF;
		mSumRound = (int)buf[offset + 4] & 0xff;
		mImage = (ResImage)DatLib.GetRes(DatLib.RES_GDP, mType, (int)buf[offset + 5] & 0xff);
		mName = getString(buf, offset + 6);
		mBuyPrice = get2BytesInt(buf, offset + 0x12);
		mSellPrice = get2BytesInt(buf, offset + 0x14);
		mDescription = getString(buf, offset + 0x1e);
		mEventId = get2BytesInt(buf, offset + 0x84);
		setOtherData(buf, offset);
	}
	
	/**
	 * 是否可装备，最低位为主角1
	 */
	private int mEnable;
	
	/**
	 * 
	 * @param playId 1-4
	 * @return
	 */
	public boolean canPlayerUse(int playId) {
		if (playId >= 1 && playId <= 4) {
			return (mEnable & (1 << (playId-1))) != 0;
		}
		return false;
	}
	
	private int mSumRound; // 持续回合
	
	public int getSumRound() {
		return mSumRound;
	}
	
	private ResImage mImage; // 物品图片
	
	public void draw(Canvas canvas, int x, int y) {
		mImage.draw(canvas, 1, x, y);
	}

	private String mName = ""; // 道具名称
	
	public String getName() {
		return mName;
	}
	
	private int mBuyPrice, mSellPrice; // 买价、卖价
	
	public int getBuyPrice() {
		return mBuyPrice;
	}
	
	public int getSellPrice() {
		return mSellPrice;
	}
	
	private String mDescription = ""; // 道具说明
	
	public String getDescription() {
		return mDescription;
	}
	
	/**
	 * 不为0时装备该道具时，就会设置该事件，而卸下时，
	 * 就会取消该事件，不能用来典当。
	 */
	private int mEventId;
	
	public int getEventId() {
		return mEventId;
	}
	
	/**
	 * 物品数量
	 */
	private int mGoodsNum = 0;
	
	public int getGoodsNum() {
		return mGoodsNum;
	}
	
	public void setGoodsNum(int num) {
		mGoodsNum = num;
	}
	
	/**
	 * 增加物品数量
	 * @param d 增量
	 */
	public void addGoodsNum(int d) {
		mGoodsNum += d;
	}
	
	/** 是否具有全体效果*/
	public boolean effectAll() {
		return false;
	}

	/**
	 * 比较物品编号是否相等
	 */
	@Override
	public boolean equals(Object o) {
		return mType == ((BaseGoods)o).getType() && mIndex == ((BaseGoods)o).getIndex();
	}
	
	
}

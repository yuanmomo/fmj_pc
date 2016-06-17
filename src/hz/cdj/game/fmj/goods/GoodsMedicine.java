package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResSrs;

/**
 * 09药物类
 * 普通药物，任何人都可以用
 * @author Chen
 *
 */
public class GoodsMedicine extends BaseGoods implements IEatMedicine {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mHp = get2BytesInt(buf, offset + 0x16);
		mMp = get2BytesInt(buf, offset + 0x18);
		mAni = (ResSrs)DatLib.GetRes(DatLib.RES_SRS, 2/*(int)buf[offset + 0x1b] & 0xff*/,
				(int)buf[offset + 0x1a] & 0xff);
		mBitMask = (int)buf[offset + 0x1c] & 0xff;
	}

	private int mHp;
	private int mMp;
	private ResSrs mAni;
	private int mBitMask; // 治疗 毒、乱、封、眠
	
	public ResSrs getAni() {
		return mAni;
	}
	
	@Override
	public void eat(Player player) {
		player.setHP(player.getHP() + mHp);
		if (player.getHP() > player.getMaxHP()) {
			player.setHP(player.getMaxHP());
		}
		player.setMP(player.getMP() + mMp);
		if (player.getMP() > player.getMaxMP()) {
			player.setMP(player.getMaxMP());
		}
		player.delDebuff(mBitMask);
		Player.sGoodsList.deleteGoods(mType, mIndex);
	}
	
	/**
	 * 是具有全体治疗效果
	 * @return
	 */
	@Override
	public boolean effectAll() {
		return (mBitMask & 0x10) != 0;
	}
}

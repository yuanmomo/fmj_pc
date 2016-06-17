package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResSrs;

/**
 * 08暗器
 * @author Chen
 *
 */
public class GoodsHiddenWeapon extends BaseGoods {
	
	private int get2ByteSint(byte[] buf, int start) {
		int i = ((int)buf[start] & 0xFF) | ((int)buf[start + 1] << 8 & 0x7F00);
		if (((int)buf[start + 1] & 0x80) != 0) {
			return -i;
		}
		return i;
	}

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mHp = get2ByteSint(buf, offset + 0x16);
		mMp = get2ByteSint(buf, offset + 0x18);
		mAni = (ResSrs)DatLib.GetRes(DatLib.RES_SRS, (int)buf[offset + 0x1b] & 0xff,
				(int)buf[offset + 0x1a] & 0xff);
		mBitMask = (int)buf[offset + 0x1c] & 0xff;
	}

	private int mHp; // 当该值为正时表示敌人损失多少生命，为负时表示从敌人身上吸取多少生命到投掷者身上
	private int mMp; // 当该值为正时表示敌人损失多少真气，为负时表示从敌人身上吸取多少真气到投掷者身上
	private ResSrs mAni;
	private int mBitMask; // 000 全体否 毒乱封眠
	
	/**
	 * 
	 * @return 当该值为正时表示敌人损失多少生命，
	 * 为负时表示从敌人身上吸取多少生命到投掷者身上
	 */
	public int getAffectHp() {
		return mHp;
	}
	
	/**
	 * 
	 * @return 当该值为正时表示敌人损失多少真气，
	 * 为负时表示从敌人身上吸取多少真气到投掷者身上
	 */
	public int getAffectMp() {
		return mMp;
	}
	
	public ResSrs getAni() {
		return mAni;
	}
	
	@Override
	public boolean effectAll() {
		return (mBitMask & 0x10) != 0;
	}
}

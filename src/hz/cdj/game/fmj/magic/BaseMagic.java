package hz.cdj.game.fmj.magic;

import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResBase;
import hz.cdj.game.fmj.lib.ResSrs;

public abstract class BaseMagic extends ResBase {
	
	protected abstract void setOtherData(byte[] buf, int offset);

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xFF;
		mIndex = (int)buf[offset + 1] & 0xFF;
		mRound = (int)buf[offset + 3] & 0x7f;
		mIsForAll = ((int)buf[offset + 3] & 0x80) != 0;
		mCostMp = (int)buf[offset + 4];
		mMagicAni = (ResSrs)DatLib.GetRes(DatLib.RES_SRS, 2, (int)buf[offset + 5] & 0xFF);
		mMagicName = getString(buf, offset + 6);
		if (((int)buf[offset + 2] & 0xff) > 0x70) { // 魔法描述过长
			buf[offset + 0x70] = 0;
		}
		mMagicDescription = getString(buf, offset + 0x1a);
		setOtherData(buf, offset);
	}

	private int mRound; // 持续回合
	
	/**
	 * 获取魔法的持续回合
	 * @return
	 */
	public int getRoundNum() {
		return mRound;
	}
	
	private boolean mIsForAll; // 是否影响全体
	
	/**
	 * 魔方是否影响全体
	 * @return
	 */
	public boolean isForAll() {
		return mIsForAll;
	}
	
	private int mCostMp; // 耗费真气
	
	/**
	 * 魔方耗费真气
	 * @return
	 */
	public int getCostMp() {
		return mCostMp;
	}
	
	private ResSrs mMagicAni; // 魔法动画
	
	/**
	 * 战斗中使用魔法时播放的动画
	 * @return
	 */
	public ResSrs getMagicAni() {
		return mMagicAni;
	}
	
	private String mMagicName; // 魔法名
	
	public String getMagicName() {
		return mMagicName;
	}
	
	private String mMagicDescription;  // 魔方描述
	
	public String getMagicDescription() {
		return mMagicDescription;
	}
	
	public void use(FightingCharacter src, FightingCharacter dst) {
		
	}
	
}

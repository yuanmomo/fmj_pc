package hz.cdj.game.fmj.magic;

import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResBase;

/**
 * 魔法链资源
 * @author Chen
 *
 */
public class ResMagicChain extends ResBase {
	
	private int mNum; // 魔法数量
	
	private BaseMagic[] mMagics;

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xff;
		mIndex = (int)buf[offset + 1] & 0xff;
		mNum = (int)buf[offset + 2] & 0xff;
		
		int index = offset + 3;
		mMagics = new BaseMagic[mNum];
		for (int i = 0; i < mNum; i++) {
			mMagics[i] = (BaseMagic)DatLib.GetRes(DatLib.RES_MRS,
					(int)buf[index++], (int)buf[index++]);
		}
	}

	private int mLearnNum = 0; // 学会的魔法数量
	
	/**
	 * 返回已经学会的魔法数量
	 * @return
	 */
	public int getLearnNum() {
		return mLearnNum;
	}
	
	public void setLearnNum(int num) {
		mLearnNum = num;
	}
	
	/**
	 * 学会魔法数量加一
	 */
	public void learnNextMagic() {
		++mLearnNum;
	}
	
	/**
	 * 得到魔法总数
	 * @return
	 */
	public int getMagicSum() {
		return mNum;
	}
	
	public BaseMagic getMagic(int index) { // TODO fix null
		return mMagics[index];
	}
}

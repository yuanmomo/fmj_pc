package hz.cdj.game.fmj.magic;

/**
 * 02增强型
 * @author Chen
 *
 */
public class MagicEnhance extends BaseMagic {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mDf = (int)buf[offset + 0x16] & 0xff;
		mAt = (int)buf[offset + 0x17] & 0xff;
		mRound = (int)(buf[offset + 0x18] >> 4) & 0xf;
		mBuff = (int)buf[offset + 0x19] & 0xff;
	}
	
	private int mDf;//0~100，被施展者的防御力增强的百分比
	private int mAt;//0~100，被施展者的攻击力增强的百分比
	private int mRound;//持续回合
	private int mBuff;//速 0~100，被施展者的身法加快的百分比

}

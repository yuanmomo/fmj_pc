package hz.cdj.game.fmj.magic;

import hz.cdj.game.fmj.characters.FightingCharacter;

import java.util.List;

/**
 * 01攻击型
 * @author Chen
 *
 */
public class MagicAttack extends BaseMagic {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mHp = get2BytesSInt(buf, offset + 0x12);
		mMp = get2BytesSInt(buf, offset + 0x14);
		mDf = (int)buf[offset + 0x16] & 0xff;
		mAt = (int)buf[offset + 0x17] & 0xff;
		mBuff = (int)buf[offset + 0x18] & 0xff;
		mDebuff = (int)buf[offset + 0x19] & 0xff;
	}
	
	private int mHp;//-8000~+8000，为正数时表示敌人损失生命的基数，为负数时表示从敌人身上吸取生命的基数
	private int mMp;//-8000~+8000，为正数时表示敌人损失真气的基数，为负数时表示从敌人身上吸取真气的基数
	private int mDf;//0~100，表示敌人的防御力减弱的百分比
	private int mAt;//0~100，表示敌人的攻击力减弱的百分比
	private int mBuff;//高四位 持续回合，低四位毒、乱、封、眠
	private int mDebuff;//速 0~100，表示敌人的身法减慢的百分比
	
	@Override
	public void use(FightingCharacter src, FightingCharacter dst) { // TODO 
		src.setMP(src.getMP() - getCostMp());
		dst.setHP(dst.getHP() - mHp);
	}
	
	public void use(FightingCharacter src, List<FightingCharacter> dst) {
		src.setMP(src.getMP() - getCostMp());
		for (FightingCharacter fc : dst) {
			fc.setHP(fc.getHP() - mHp);
		}
	}

}

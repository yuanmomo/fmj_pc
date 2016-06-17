package hz.cdj.game.fmj.magic;

import hz.cdj.game.fmj.characters.Player;

/**
 * 03恢复型
 * @author Chen
 *
 */
public class MagicRestore extends BaseMagic {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mHp = get2BytesInt(buf, offset + 0x12);
		mBuff = buf[offset + 0x18];
	}
	
	private int mHp; //  0~8000，表示被施展者恢复生命的数值。
	private int mBuff; // 低四位，毒、乱、封、眠 是否具有医疗相应异常状态的能力

	public void use(Player src, Player dst) {
		if (src.getMP() < getCostMp()) return;
		
		src.setMP(src.getMP() - getCostMp());
		
		dst.setHP(dst.getHP() + mHp);
		if (dst.getHP() > dst.getMaxHP()) {
			dst.setHP(dst.getMaxHP());
		}
		dst.delDebuff(mBuff);
	}
}

package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.magic.MagicAttack;

/**
 * 06饰品类
 * @author Chen
 *
 */
public class GoodsDecorations extends GoodsEquipment {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mMp = get1ByteSInt(buf, offset + 0x16);
		mHp = get1ByteSInt(buf, offset + 0x17);
		mdf = get1ByteSInt(buf, offset + 0x18);
		mat = (int)buf[offset + 0x19] & 0xff;
		mlingli = get1ByteSInt(buf, offset + 0x1a);
		mSpeed = get1ByteSInt(buf, offset + 0x1b);
		mMagic = (int)buf[offset + 0x1c] & 0xff;
		mLuck = get1ByteSInt(buf, offset + 0x1d);
	}

	@Override
	public void putOn(Player p) {
		super.putOn(p);
		// TODO 每回合的恢复扣除
	}
	
	@Override
	public void takeOff(Player p) {
		super.takeOff(p);
		// TODO 每回合的恢复扣除
	}

	private int mMp; // 表示战斗时，每回合恢复或扣除多少真气
	private int mHp; // 表示战斗时，每回合恢复或扣除多少生命
	private int mMagic; // 合体魔方序号
	
	public MagicAttack getCoopMagic() {
		return (MagicAttack)DatLib.GetRes(DatLib.RES_MRS, 1, mMagic);
	}
}

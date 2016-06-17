package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;

/**
 * 11仙药类
 * 永久性改变人物属性
 * @author Chen
 *
 */
public class GoodsMedicineChg4Ever extends BaseGoods implements IEatMedicine {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mMpMax = get1ByteSInt(buf, offset + 0x16);
		mHpMax = get1ByteSInt(buf, offset + 0x17);
		mdf = get1ByteSInt(buf, offset + 0x18);
		mat = get1ByteSInt(buf, offset + 0x19);
		mling = get1ByteSInt(buf, offset + 0x1a);
		mSpeed = get1ByteSInt(buf, offset + 0x1b);
		mLuck = get1ByteSInt(buf, offset + 0x1d);
	}

	private int mMpMax;
	private int mHpMax;
	private int mdf;
	private int mat;
	private int mling;
	private int mSpeed;
	private int mLuck;
	
	@Override
	public void eat(Player player) {
		player.setMaxMP(player.getMaxMP() + mMpMax);
		player.setMaxHP(player.getMaxHP() + mHpMax);
		player.setDefend(player.getDefend() + mdf);
		player.setAttack(player.getAttack() + mat);
		player.setLingli(player.getLingli() + mling);
		player.setSpeed(player.getSpeed() + mSpeed);
		player.setLuck(player.getLuck() + mLuck);
		Player.sGoodsList.deleteGoods(mType, mIndex);
	}
}

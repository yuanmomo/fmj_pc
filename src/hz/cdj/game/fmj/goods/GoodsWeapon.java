package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;


/**
 * 07武器类
 * @author Chen
 *
 */
public class GoodsWeapon extends GoodsEquipment {

	@Override
	public void putOn(Player p) {
		super.putOn(p);
		p.addAtbuff(mBitEffect, getSumRound());
	}

	@Override
	public void takeOff(Player p) {
		super.takeOff(p);
		p.delAtbuff(mBitEffect);
	}
	
	public boolean attackAll() {
		return (mBitEffect & 0x10) != 0;
	}
}

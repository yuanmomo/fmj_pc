package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;

/**
 * 10灵药类
 * 对生命的恢复0~100,表示恢复被使用者??%的生命，
 * 并解除死亡状态，但被使用者必须是死亡状态。
 * @author Chen
 *
 */
public class GoodsMedicineLife extends BaseGoods implements IEatMedicine {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mPercent = buf[offset + 0x17] & 0xff;
		if (mPercent > 100) {
			mPercent = 100;
		}
	}

	private int mPercent; // 恢复百分比

	@Override
	public void eat(Player player) {
		player.setMP(player.getMP() + player.getMaxMP() * mPercent / 100);
		if (player.getMP() > player.getMaxMP()) {
			player.setMP(player.getMaxMP());
		}
		Player.sGoodsList.deleteGoods(mType, mIndex);
	}
}

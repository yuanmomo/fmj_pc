package hz.cdj.game.fmj.goods;

import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.script.ScriptResources;

/**
 * 01冠类，02衣类，03鞋类，04护甲类，05护腕类
 */
public class GoodsEquipment extends BaseGoods {

	@Override
	protected void setOtherData(byte[] buf, int offset) {
		mMpMax = get1ByteSInt(buf, offset + 0x16);
		mHpMax = get1ByteSInt(buf, offset + 0x17);
		mdf = get1ByteSInt(buf, offset + 0x18);
		mat = (int)buf[offset + 0x19] & 0xff;
		mlingli = get1ByteSInt(buf, offset + 0x1a);
		mSpeed = get1ByteSInt(buf, offset + 0x1b);
		mBitEffect = (int)buf[offset + 0x1c] & 0xff;
		mLuck = get1ByteSInt(buf, offset + 0x1d);
	}

	public void putOn(Player p) {
		if (canPlayerUse(p.getIndex())) {
			p.setMaxMP(p.getMaxMP() + mMpMax);
			p.setMaxHP(p.getMaxHP() + mHpMax);
			p.setDefend(p.getDefend() + mdf);
			p.setAttack(p.getAttack() + mat);
			p.setLingli(p.getLingli() + mlingli);
			p.setSpeed(p.getSpeed() + mSpeed);
			if (!(this instanceof GoodsWeapon)) {
				p.addBuff(mBitEffect); // 添加免疫效果
			}
			p.setLuck(p.getLuck() + mLuck);
			if (getEventId() != 0) {
				// 设置装备触发的事件
				ScriptResources.setEvent(getEventId());
			}
		}
	}
	
	public void takeOff(Player p) {
		p.setMaxMP(p.getMaxMP() - mMpMax);
		p.setMaxHP(p.getMaxHP() - mHpMax);
		p.setDefend(p.getDefend() - mdf);
		p.setAttack(p.getAttack() - mat);
		p.setLingli(p.getLingli() - mlingli);
		p.setSpeed(p.getSpeed() - mSpeed);
		if (!(this instanceof GoodsWeapon)) {
			p.delBuff(mBitEffect); // 删掉免疫效果
		}
		p.setLuck(p.getLuck() - mLuck);
		if (getEventId() != 0) {
			// 取消该事件
			ScriptResources.clearEvent(getEventId());
		}
	}

	protected int mMpMax; // 加真气上限
	protected int mHpMax; // 加生命上限
	protected int mdf; // 防御
	protected int mat; // 攻击
	protected int mlingli; // 灵力
	protected int mSpeed; // 身法
	protected int mBitEffect; // 0、0、0、0（07武器类此处为全体效果）、毒、乱、封、眠（影响免疫效果，07武器类为攻击效果）
	protected int mLuck; // 吉运
}

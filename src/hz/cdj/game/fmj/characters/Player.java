package hz.cdj.game.fmj.characters;

import android.graphics.Canvas;
import hz.cdj.game.fmj.goods.GoodsEquipment;
import hz.cdj.game.fmj.goods.GoodsManage;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.magic.ResMagicChain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class Player extends FightingCharacter implements Externalizable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ResImage mImgHead;
	
	public void drawHead(Canvas canvas, int x, int y) {
		if (mImgHead != null) {
			mImgHead.draw(canvas, 1, x, y);
		}
	}
	
	public void setFrameByState() {
		if (isAlive()) {
			if (hasDebuff(BUFF_MASK_MIAN) || getHP() < getMaxHP() / 10) {
				getFightingSprite().setCurrentFrame(11);
			} else {
				getFightingSprite().setCurrentFrame(1);
			}
		} else {
			getFightingSprite().setCurrentFrame(12);
		}
	}

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xFF;
		mIndex = (int)buf[offset + 1] & 0xFF;
		mImgHead = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, mIndex);
		setWalkingSprite(new WalkingSprite(mType, (int)buf[offset + 0x16] & 0xFF));
		setFightingSprite(new FightingSprite(DatLib.RES_PIC, mIndex));
		Direction d = Direction.North;
		switch ((int)buf[offset + 2] & 0xFF) {
		case 1:	d = Direction.North;	break;
		case 2:	d = Direction.East;		break;
		case 3:	d = Direction.South;	break;
		case 4:	d = Direction.West;		break;
		}
		setDirection(d);
		setStep((int)buf[offset + 3] & 0xff);
		setPosInMap((int)buf[offset + 5] & 0xFF, (int)buf[offset + 6] & 0xFF);
		setMagicChain((ResMagicChain)DatLib.GetRes(DatLib.RES_MLR, 1, (int)buf[offset + 0x17] & 0xff));
		getMagicChain().setLearnNum((int)buf[offset + 9] & 0xff);
		setName(getString(buf, offset + 0x0a));
		setLevel((int)buf[offset + 0x20] & 0xff);
		setMaxHP(get2BytesInt(buf, offset + 0x26));
		setHP(get2BytesInt(buf, offset + 0x28));
		setMaxMP(get2BytesInt(buf, offset + 0x2a));
		setMP(get2BytesInt(buf, offset + 0x2c));
		setAttack(get2BytesInt(buf, offset + 0x2e));
		setDefend(get2BytesInt(buf, offset + 0x30));
		setSpeed((int)buf[offset + 0x36] & 0xff);
		setLingli((int)buf[offset + 0x37] & 0xff);
		setLuck((int)buf[offset + 0x38] & 0xff);
		setCurrentExp(get2BytesInt(buf, offset + 0x32));
		mLevelupChain = (ResLevelupChain)DatLib.GetRes(DatLib.RES_MLR, 2, mIndex);
		
		int tmp;
		
		tmp = (int)buf[offset + 0x1e] & 0xff;
		if (tmp != 0) {
			mEquipments[0] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 6, tmp);
		}
		
		tmp = (int)buf[offset + 0x1f] & 0xff;
		if (tmp != 0) {
			mEquipments[1] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 6, tmp);
		}
		
		tmp = (int)buf[offset + 0x1b] & 0xff;
		if (tmp != 0) {
			mEquipments[2] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 5, tmp);
		}
		
		tmp = (int)buf[offset + 0x1d] & 0xff;
		if (tmp != 0) {
			mEquipments[3] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 3, tmp);
		}
		
		tmp = (int)buf[offset + 0x1c] & 0xff;
		if (tmp != 0) {
			mEquipments[4] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 7, tmp);
		}
		
		tmp = (int)buf[offset + 0x19] & 0xff;
		if (tmp != 0) {
			mEquipments[5] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 2, tmp);
		}
		
		tmp = (int)buf[offset + 0x1a] & 0xff;
		if (tmp != 0) {
			mEquipments[6] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 4, tmp);
		}
		
		tmp = (int)buf[offset + 0x18] & 0xff;
		if (tmp != 0) {
			mEquipments[7] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, 1, tmp);
		}
		
	}
	
	private ResLevelupChain mLevelupChain;
	
	public ResLevelupChain getLevelupChain() {
		return mLevelupChain;
	}
	
	private int mCurrentExp; // 当前经验值
	
	public void setCurrentExp(int exp) {
		mCurrentExp = exp;
	}
	
	public int getCurrentExp() {
		return mCurrentExp;
	}

	/**
	 * 0装饰 1装饰 2护腕 3脚蹬 4手持 5身穿 6肩披 7头戴
	 */
	private GoodsEquipment[] mEquipments = new GoodsEquipment[8];
	/** 装备界面从左至右的装备类型号*/
	public static final int[] sEquipTypes = new int[]{6, 6, 5, 3, 7, 2, 4, 1};
	
	public GoodsEquipment getCurrentEquipment(int type) {
		for (int i = 0; i < 8; i++) {
			if (sEquipTypes[i] == type) {
				return mEquipments[i];
			}
		}
		return null;
	}
	
	public GoodsEquipment[] getEquipmentsArray() {
		return mEquipments;
	}
	
	/**
	 * 是否已经装备该装备，对装饰检测空位
	 * @param type
	 * @param id
	 * @return
	 */
	public boolean hasEquipt(int type, int id) {
		if (type == 6) {
			// 两个位置都装备同一件装备才返回真
			if ((mEquipments[0] != null && mEquipments[0].getType() == type && mEquipments[0].getIndex() == id) &&
				(mEquipments[1] != null && mEquipments[1].getType() == type && mEquipments[1].getIndex() == id)) {
				return true;
			}
			return false;
		}
		
		for (int i = 2; i < 8; i++) {
			if (mEquipments[i] != null && mEquipments[i].getType() == type
					&& mEquipments[i].getIndex() == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 穿上goods装备
	 * @param goods
	 */
	public void putOn(GoodsEquipment goods) {
		for (int i = 0; i < 8; i++) {
			if (goods.getType() == sEquipTypes[i]) {
				if (mEquipments[i] == null) { // 适用2个装饰
					goods.putOn(this);
					mEquipments[i] = goods;
					break;
				}
			}
		}
	}
	
	/**
	 * 脱下类型号为type的装备
	 * @param type
	 */
	public void takeOff(int type) {
		for (int i = 0; i < 8; i++) {
			if (type == sEquipTypes[i]) {
				if (mEquipments[i] != null) {
					mEquipments[i].takeOff(this);
					mEquipments[i] = null;
					break;
				}
			}
		}
	}
	
	/**
	 * type型装备位置是否已经有装备
	 * @param type 装备类型号 {@link GoodsEquipment#getType()}
	 * @return 是否有空
	 */
	public boolean hasSpace(int type) {
		if (type == 6) { // 饰品
			if (mEquipments[0] == null || mEquipments[1] == null) {
				return true;
			} else {
				return false;
			}
		} else {
			for (int i = 0; i < 8; i++) {
				if (sEquipTypes[i] == type && mEquipments[i] == null) {
					return true;
				}
			}
		}
		return false;
	}

	public static GoodsManage sGoodsList = new GoodsManage();
	
	public static int sMoney = 0;
	
	public void drawState(Canvas canvas, int page) {
		canvas.drawLine(37, 10, 37, 87, Util.sBlackPaint);
		if (page == 0) {
			TextRender.drawText(canvas, "等级   " + getLevel(), 41, 4);
			TextRender.drawText(canvas, "生命   " + getHP() + "/" + getMaxHP(), 41, 23);
			TextRender.drawText(canvas, "真气   " + getMP() + "/" + getMaxMP(), 41, 41);
			TextRender.drawText(canvas, "攻击力 " + getAttack(), 41, 59);
			TextRender.drawText(canvas, "防御力 " + getDefend(), 41, 77);
		} else if (page == 1) {
			TextRender.drawText(canvas, "经验值", 41, 4);
			int w = Util.drawSmallNum(canvas, getCurrentExp(), 97, 4);
			TextRender.drawText(canvas, "/", 97 + w + 2, 4);
			Util.drawSmallNum(canvas, getLevelupChain().getNextLevelExp(getLevel()), 97 + w + 9, 10);
			TextRender.drawText(canvas, "身法   " + getSpeed(), 41, 23);
			TextRender.drawText(canvas, "灵力   " + getLingli(), 41, 41);
			TextRender.drawText(canvas, "幸运   " + getLuck(), 41, 59);
			StringBuilder sb = new StringBuilder("免疫   ");
			StringBuilder tmp = new StringBuilder();
			if (hasBuff(BUFF_MASK_DU)) {
				tmp.append('毒');
			}
			if (hasBuff(BUFF_MASK_LUAN)) {
				tmp.append('乱');
			}
			if (hasBuff(BUFF_MASK_FENG)) {
				tmp.append('封');
			}
			if (hasBuff(BUFF_MASK_MIAN)) {
				tmp.append('眠');
			}
			if (tmp.length() > 0) {
				sb.append(tmp);
			} else {
				sb.append('无');
			}
			TextRender.drawText(canvas, sb.toString(), 41, 77);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		mType = in.readInt();
		mIndex = in.readInt();
		mImgHead = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, mIndex);
		mLevelupChain = (ResLevelupChain)DatLib.GetRes(DatLib.RES_MLR, 2, mIndex);
		setWalkingSprite(new WalkingSprite(mType, in.readInt()));
		setFightingSprite(new FightingSprite(DatLib.RES_PIC, mIndex));
		setDirection((Direction)in.readObject());
		setStep(in.readInt());
		setPosInMap(in.readInt(), in.readInt());
		setMagicChain((ResMagicChain)DatLib.GetRes(DatLib.RES_MLR, 1, in.readInt()));
		getMagicChain().setLearnNum(in.readInt());
		setName((String)in.readObject());
		setLevel(in.readInt());
		setMaxHP(in.readInt());
		setHP(in.readInt());
		setMaxMP(in.readInt());
		setMP(in.readInt());
		setAttack(in.readInt());
		setDefend(in.readInt());
		setSpeed(in.readInt());
		setLingli(in.readInt());
		setLuck(in.readInt());
		setCurrentExp(in.readInt());
		setmAtbuff(in.readInt());
		setmDebuff(in.readInt());
		for (int i = 0; i < 8; i++) {
			int type = in.readInt(), index = in.readInt();
			if (type != 0 && index != 0) {
				mEquipments[i] = (GoodsEquipment)DatLib.GetRes(DatLib.RES_GRS, type, index);
			}
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(mType);
		out.writeInt(mIndex);
		out.writeInt(getWalkingSpriteId());
		out.writeObject(getDirection());
		out.writeInt(getStep());
		out.writeInt(getPosInMap().x);
		out.writeInt(getPosInMap().y);
		out.writeInt(getMagicChain().getIndex());
		out.writeInt(getMagicChain().getLearnNum());
		out.writeObject(getName());
		out.writeInt(getLevel());
		out.writeInt(getMaxHP());
		out.writeInt(getHP());
		out.writeInt(getMaxMP());
		out.writeInt(getMP());
		out.writeInt(getAttack());
		out.writeInt(getDefend());
		out.writeInt(getSpeed());
		out.writeInt(getLingli());
		out.writeInt(getLuck());
		out.writeInt(getCurrentExp());
		out.writeInt(getmAtbuff());
		out.writeInt(getmDebuff());
		for (int i = 0; i < 8; i++) {
			if (mEquipments[i] == null) {
				out.writeInt(0);
				out.writeInt(0);
			} else {
				out.writeInt(mEquipments[i].getType());
				out.writeInt(mEquipments[i].getIndex());
			}
		}
	}
	
}

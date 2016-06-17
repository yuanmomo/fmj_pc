package hz.cdj.game.fmj.characters;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SceneObj extends NPC {

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int) buf[offset] & 0xFF;
		mIndex = (int) buf[offset + 1] & 0xFF;
		// 动作状态
		setCharacterState((int) buf[offset + 4] & 0xFF);
		// 姓名
		setName(getString(buf, offset + 9));
		// 延时
		mDelay = (int) buf[offset + 0x15] & 0xFF;
		// 行走图
		setWalkingSprite(new WalkingSprite(4, (int) buf[offset + 0x16] & 0xFF));
		// 面向
		setDirection(Direction.North);
		// 脚步
		setStep((int) buf[offset + 3] & 0xFF);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(mType);
		out.writeInt(mIndex);
		out.writeInt(getCharacterState());
		out.writeObject(getName());
		out.writeInt(mDelay);
		out.writeInt(getWalkingSpriteId());
		out.writeObject(getDirection());
		out.writeInt(getStep());
		out.writeInt(getPosInMap().x);
		out.writeInt(getPosInMap().y);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		mType = in.readInt();
		mIndex = in.readInt();
		setCharacterState(in.readInt());
		setName((String)in.readObject());
		mDelay = in.readInt();
		setWalkingSprite(new WalkingSprite(4, in.readInt()));
		setDirection((Direction)in.readObject());
		setStep(in.readInt());
		setPosInMap(in.readInt(), in.readInt());
	}

	@Override
	public void walk() {
	}

	@Override
	public void walk(Direction d) {
	}

	@Override
	public void walkStay(Direction d) {
	}

	// @Override
	// public void drawWalkingSprite(Canvas canvas, Point posMapScreen) {
	// }

}

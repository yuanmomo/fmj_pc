package hz.cdj.game.fmj.characters;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Random;

public class NPC extends Character implements Externalizable {
	
	/**
	 * 暂停状态，等到延时到了后转变为巡逻状态
	 */
	protected int mDelay;

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xFF;
		mIndex = (int)buf[offset + 1] & 0xFF;
		// 动作状态
		setCharacterState((int)buf[offset + 4] & 0xFF);
		// 姓名
		setName(getString(buf, offset + 9));
		// 延时
		mDelay = (int)buf[offset + 0x15] & 0xFF;
		if (mDelay == 0) {
			setCharacterState(STATE_STOP);
		}
		// 行走图
		setWalkingSprite(new WalkingSprite(2,
				(int)buf[offset + 0x16] & 0xFF));
		// 面向
		int faceto = (int)buf[offset + 2] & 0xFF;
		Direction d = Direction.North;
		switch (faceto) {
		case 1: d = Direction.North; break;
		case 2: d = Direction.East; break;
		case 3: d = Direction.South; break;
		case 4: d = Direction.West; break;
		}
		setDirection(d);
		// 脚步
		setStep((int)buf[offset + 3] & 0xFF);
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
		out.writeLong(mPauseCnt);
		out.writeLong(mActiveCnt);
		out.writeLong(mWalkingCnt);
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
		setWalkingSprite(new WalkingSprite(2, in.readInt()));
		setDirection((Direction)in.readObject());
		setStep(in.readInt());
		mPauseCnt = in.readLong();
		mActiveCnt = in.readLong();
		mWalkingCnt = in.readLong();
		setPosInMap(in.readInt(), in.readInt());
	}

	public interface ICanWalk {
		public boolean canWalk(int x, int y);
	}
	
	private ICanWalk mCanWalk;

	public void setICanWalk(ICanWalk arg) {
		mCanWalk = arg;
	}
	
	private Random mRandom = new Random();
	private long mPauseCnt = mDelay * 100;
	private long mActiveCnt = 0;
	private long mWalkingCnt = 0;
	
	public void update(long delta) {
		switch (getCharacterState()) {
		case STATE_PAUSE:
			mPauseCnt -= delta;
			if (mPauseCnt < 0) {
				setCharacterState(STATE_WALKING);
			}
			break;

		case STATE_FORCE_MOVE:
		case STATE_WALKING:
			mWalkingCnt += delta;
			if (mWalkingCnt < 500) break;
			mWalkingCnt = 0;
			if (mRandom.nextInt(5) == 0) { // 五分之一的概率暂停
				mPauseCnt = mDelay * 100;
				setCharacterState(STATE_PAUSE);
			} else if (mRandom.nextInt(5) == 0) { // 五分之一的概率改变方向
				int i = mRandom.nextInt(4);
				Direction d = Direction.North;
				switch (i) {
				case 0: d = Direction.North; break;
				case 1: d = Direction.East; break;
				case 2: d = Direction.South; break;
				case 3: d = Direction.West; break;
				}
				setDirection(d);
				walk();
			} else {
				walk();
			}
			break;

		case STATE_STOP:
			break;
			
		case STATE_ACTIVE:
			mActiveCnt += delta;
			if (mActiveCnt > 100) {
				mActiveCnt = 0;
				walkStay();
			}
			break;
		}
	}

	@Override
	public void walk() {
		int x = getPosInMap().x;
		int y = getPosInMap().y;
		switch (getDirection()) {
		case North: --y; break;
		case East: ++x; break;
		case South: ++y; break;
		case West: --x; break;
		}
		if (mCanWalk.canWalk(x, y)) {
			super.walk();
		}
	}

}

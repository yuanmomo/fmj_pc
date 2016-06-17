package hz.cdj.game.fmj.characters;

import hz.cdj.game.fmj.lib.ResBase;

public class ResLevelupChain extends ResBase {
	
	private static final int LEVEL_BYTES = 20; // 一个级别数据所占字节数

	@Override
	public void setData(byte[] buf, int offset) {
		mType = (int)buf[offset] & 0xff;
		mIndex = (int)buf[offset + 1] & 0xff;
		mMaxLevel = (int)buf[offset + 2] & 0xff;
		
		mLevelData = new byte[mMaxLevel * LEVEL_BYTES];
		System.arraycopy(buf, offset + 4, mLevelData, 0, mLevelData.length);
	}
	
	private int mMaxLevel; // 最高级别
	
	public int getMaxLevel() {
		return mMaxLevel;
	}
	
	private byte[] mLevelData; // 级别数据
	
	public int getMaxHP(int level) {
		if (level <= mMaxLevel) {
			return get2BytesInt(mLevelData, level * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getHP(int level) {
		if (level <= mMaxLevel) {
			return get2BytesInt(mLevelData, 2 + level * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getMaxMP(int l) {
		if (l <= mMaxLevel) {
			return get2BytesInt(mLevelData, 4 + l * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getMP(int l) {
		if (l <= mMaxLevel) {
			return get2BytesInt(mLevelData, 6 + l * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getAttack(int l) {
		if (l <= mMaxLevel) {
			return get2BytesInt(mLevelData, 8 + l * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getDefend(int l) {
		if (l <= mMaxLevel) {
			return get2BytesInt(mLevelData, 10 + l * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getNextLevelExp(int l) {
		if (l <= mMaxLevel) {
			return get2BytesInt(mLevelData, 14 + l * LEVEL_BYTES - LEVEL_BYTES);
		}
		return 0;
	}
	
	public int getSpeed(int l) {
		if (l <= mMaxLevel) {
			return (int)mLevelData[l * LEVEL_BYTES - LEVEL_BYTES + 16] & 0xff;
		}
		return 0;
	}
	
	public int getLingli(int l) {
		if (l <= mMaxLevel) {
			return (int)mLevelData[l * LEVEL_BYTES - LEVEL_BYTES + 17] & 0xff;
		}
		return 0;
	}
	
	public int getLuck(int l) {
		if (l <= mMaxLevel) {
			return (int)mLevelData[l * LEVEL_BYTES - LEVEL_BYTES + 18] & 0xff;
		}
		return 0;
	}
	
	public int getLearnMagicNum(int l) {
		if (l <= mMaxLevel) {
			return (int)mLevelData[l * LEVEL_BYTES - LEVEL_BYTES + 19] & 0xff;
		}
		return 0;
	}

}

package hz.cdj.game.fmj.characters;

import hz.cdj.game.fmj.magic.ResMagicChain;

public abstract class FightingCharacter extends Character {

	/**
	 * 人物战斗图
	 */
	private FightingSprite mFightingSprite;
	
	public FightingSprite getFightingSprite() {
		return mFightingSprite;
	}
	
	public void setFightingSprite(FightingSprite fightingSprite) {
		mFightingSprite = fightingSprite;
	}
	
	/** 设置中心坐标*/
	public void setCombatPos(int x, int y) {
		mFightingSprite.setCombatPos(x, y);
	}
	
	/**中心坐标*/
	public int getCombatX() {
		return mFightingSprite.getCombatX();
	}
	
	/**中心坐标*/
	public int getCombatY() {
		return mFightingSprite.getCombatY();
	}
	
	public int getCombatLeft() {
		return mFightingSprite.getCombatX() - mFightingSprite.getWidth() / 2;
	}
	
	public int getCombatTop() {
		return mFightingSprite.getCombatY() - mFightingSprite.getHeight() / 2;
	}
	
	/**
	 * 魔法链
	 */
	private ResMagicChain mMagicChain;
	
	public void setMagicChain(ResMagicChain magicChain) {
		mMagicChain = magicChain;
	}
	
	public ResMagicChain getMagicChain() {
		return mMagicChain;
	}
	
	/**
	 * 等级
	 */
	private int mLevel;
	
	public void setLevel(int level) {
		mLevel = level;
	}
	
	public int getLevel() {
		return mLevel;
	}
	
	private int mMaxHP;
	
	public void setMaxHP(int maxHP) {
		if (maxHP > 999) {
			maxHP = 999;
		}
		mMaxHP = maxHP;
	}
	
	public int getMaxHP() {
		return mMaxHP;
	}
	
	private int mMaxMP;
	
	public void setMaxMP(int maxMP) {
		if (maxMP > 999) {
			maxMP = 999;
		}
		mMaxMP = maxMP;
	}
	
	public int getMaxMP() {
		return mMaxMP;
	}
	
	private int mHP;
	
	public void setHP(int hp) {
		if (hp > mMaxHP) {
			hp = mMaxHP;
		}
		mHP = hp;
	}
	
	public int getHP() {
		return mHP;
	}
	
	public boolean isAlive() {
		return mHP > 0;
	}
	
	private boolean mIsVisiable = true;
	
	public boolean isVisiable() {
		return mIsVisiable;
	}
	
	public void setVisiable(boolean visiable) {
		mIsVisiable = visiable;
	}
	
	private int mMP;
	
	public void setMP(int mp) {
		if (mp > mMaxMP) {
			mp = mMaxMP;
		}
		mMP = mp;
	}
	
	public int getMP() {
		return mMP;
	}
	
	private int mAttack; // 攻击
	
	public void setAttack(int at) {
		if (at > 999) {
			at = 999;
		}
		mAttack = at;
	}
	
	public int getAttack() {
		return mAttack;
	}
	
	private int mDefend; // 防御
	
	public void setDefend(int d) {
		if (d > 999) {
			d = 999;
		}
		mDefend = d;
	}
	
	public int getDefend() {
		return mDefend;
	}
	
	private int mSpeed; // 身法
	
	public void setSpeed(int s) {
		if (s > 99) {
			s = 99;
		}
		mSpeed = s;
	}
	
	public int getSpeed() {
		return mSpeed;
	}
	
	private int mlingli; // 灵力
	
	public int getLingli() {
		return mlingli;
	}

	public int getmAtbuff() {
		return mAtbuff;
	}

	public void setmAtbuff(int mAtbuff) {
		this.mAtbuff = mAtbuff;
	}
	public int getmDebuff() {
		return mDebuff;
	}
	public void setmDebuff(int mDebuff) {
		this.mDebuff = mDebuff;
	}
	public void setLingli(int l) {
		if (l > 99) {
			l = 99;
		}
		mlingli = l;
	}
	
	private int mLuck; // 幸运
	
	public void setLuck(int l) {
		if (l > 99) {
			l = 99;
		}
		mLuck = l;
	}
	
	public int getLuck() {
		return mLuck;
	}
	
	public static final int BUFF_MASK_ALL = 16;
	public static final int BUFF_MASK_DU = 8;
	public static final int BUFF_MASK_LUAN = 4;
	public static final int BUFF_MASK_FENG = 2;
	public static final int BUFF_MASK_MIAN = 1;
	
	public static final int BUFF_MASK_GONG = 32;
	public static final int BUFF_MASK_FANG = 64;
	public static final int BUFF_MASK_SU   = 128;
	
	/** 免疫毒乱封眠，不同装备可能具有相同的免疫效果，叠加之*/
	protected int[] mBuff = new int[4], mBuffRound = new int[4];
	/** 身中毒乱封眠*/
	protected int mDebuff, mDebuffRound[] = new int[4];
	/** 普通攻击产生(全体)毒乱封眠，对于主角，只有武器具有该效果*/
	protected int mAtbuff, mAtbuffRound[] = new int[4];
	
	/**
	 * 是否免疫异常状态
	 * @param mask 只能为下面几个值，或者他们的位或<p>
	 * <code>BUFF_MASK_DU</code>，
	 * <code>BUFF_MASK_LUAN</code>，
	 * <code>BUFF_MASK_FENG</code>，
	 * <code>BUFF_MASK_MIAN</code>，
	 * @return 是否免疫mask状态
	 */
	public boolean hasBuff(int mask) {
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU && mBuff[0] <= 0) {
			return false;
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN && mBuff[1] <= 0) {
			return false;
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG && mBuff[2] <= 0) {
			return false;
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN && mBuff[3] <= 0) {
			return false;
		}
		if ((mask & BUFF_MASK_ALL) == BUFF_MASK_ALL && mBuff[4] <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 是否身中异常状态
	 * @param mask 只能为下面几个值，或者他们的位或中的任意一个<p>
	 * <code>BUFF_MASK_DU</code>，
	 * <code>BUFF_MASK_LUAN</code>，
	 * <code>BUFF_MASK_FENG</code>，
	 * <code>BUFF_MASK_MIAN</code>，
	 * @return 是否身中mask状态
	 */
	public boolean hasDebuff(int mask) {
		return (mDebuff & mask) != 0;
	}

	/**
	 * 攻击是否能够产生异常状态
	 * @param mask 只能为下面几个值，或者他们的位或中的任意一个<p>
	 * <code>BUFF_MASK_DU</code>，
	 * <code>BUFF_MASK_LUAN</code>，
	 * <code>BUFF_MASK_FENG</code>，
	 * <code>BUFF_MASK_MIAN</code>，
	 * @return 物理攻击是否具有mask效果
	 */
	public boolean hasAtbuff(int mask) {
		return (mAtbuff & mask) == mask;
	}
	
	/**
	 * 增加角色能够免疫的状态
	 * @see {@link #hasBuff(int)}
	 * @param mask
	 */
	public void addBuff(int mask) {
		addBuff(mask, Integer.MAX_VALUE);
	}
	
	/**
	 * 增加角色能够免疫的状态
	 * @see {@link #hasBuff(int)}
	 * @param mask
	 * @param rounds
	 */
	public void addBuff(int mask, int rounds) {
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU) {
			++mBuff[0];
			mBuffRound[0] = rounds;
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN) {
			++mBuff[1];
			mBuffRound[1] = rounds;
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG) {
			++mBuff[2];
			mBuffRound[2] = rounds;
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN) {
			++mBuff[3];
			mBuffRound[3] = rounds;
		}
	}

	public void delBuff(int mask) {
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU) {
			if (--mBuff[0] < 0) {
				mBuff[0] = 0;
			}
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN) {
			if (--mBuff[1] < 0) {
				mBuff[1] = 0;
			}
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG) {
			if (--mBuff[2] < 0) {
				mBuff[2] = 0;
			}
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN) {
			if (--mBuff[3] < 0) {
				mBuff[3] = 0;
			}
		}
	}

	public int getBuffRound(int mask) {
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU) {
			return mBuffRound[0];
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN) {
			return mBuffRound[1];
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG) {
			return mBuffRound[2];
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN) {
			return mBuffRound[3];
		}
		return 0;
	}
	
	/**
	 * 增加角色身中的异常状态
	 * @see {@link #hasDebuff(int)}
	 * @param mask
	 */
	public void addDebuff(int mask, int rounds) {
		mDebuff |= mask;
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU) {
			mDebuffRound[0] = rounds;
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN) {
			mDebuffRound[1] = rounds;
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG) {
			mDebuffRound[2] = rounds;
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN) {
			mDebuffRound[3] = rounds;
		}
	}
	
	public void delDebuff(int mask) {
		mDebuff &= (~mask);
	}
	
	/**
	 * 增加角色攻击能够产生的异常状态
	 * @see {@link #hasAtbuff(int)}
	 * @param mask
	 */
	public void addAtbuff(int mask, int rounds) {
		mAtbuff |= mask;
		if ((mask & BUFF_MASK_DU) == BUFF_MASK_DU) {
			mAtbuffRound[0] = rounds;
		}
		if ((mask & BUFF_MASK_LUAN) == BUFF_MASK_LUAN) {
			mAtbuffRound[1] = rounds;
		}
		if ((mask & BUFF_MASK_FENG) == BUFF_MASK_FENG) {
			mAtbuffRound[2] = rounds;
		}
		if ((mask & BUFF_MASK_MIAN) == BUFF_MASK_MIAN) {
			mAtbuffRound[3] = rounds;
		}
	}
	
	public void delAtbuff(int mask) {
		mAtbuff &= (~mask);
	}
	
}

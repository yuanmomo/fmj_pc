package hz.cdj.game.fmj.combat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.FightingSprite;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.characters.ResLevelupChain;
import hz.cdj.game.fmj.combat.actions.Action;
import hz.cdj.game.fmj.combat.actions.ActionCoopMagic;
import hz.cdj.game.fmj.combat.actions.ActionFlee;
import hz.cdj.game.fmj.combat.actions.ActionPhysicalAttackAll;
import hz.cdj.game.fmj.combat.actions.ActionPhysicalAttackOne;
import hz.cdj.game.fmj.combat.ui.CombatSuccess;
import hz.cdj.game.fmj.combat.ui.CombatUI;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.goods.GoodsManage;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.lib.ResSrs;
import hz.cdj.game.fmj.scene.ScreenMainGame;
import hz.cdj.game.fmj.script.ScriptExecutor;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Combat extends BaseScreen implements CombatUI.CallBack {
	
	private static boolean sIsEnable, sIsFighting;
	
	private static Combat sInstance, sInstanceBk;
	
	private static boolean sIsRandomFight;
	
	public static boolean IsActive() {
		return sIsEnable && (sInstance != null) && sIsFighting;
	}
	
	/**
	 * 开启随即战斗
	 */
	public static void FightEnable() {
		sIsEnable = true;
	}
	
	/**
	 * 关闭随即战斗
	 */
	public static void FightDisable() {
		sIsEnable = false;
		if (sInstance != null) {
			sInstance = null;
			System.gc();
		}
	}
	
	/**
	 * 初始化并开启随即战斗
	 * @param monstersType 0-7 可能出现的敌人种类
	 * @param scrb 战斗背景
	 * @param scrl 左下角图
	 * @param scrr 右上角图
	 */
	public static void InitFight(int[] monstersType, int scrb, int scrl, int scrr) {
		sIsEnable = true;
		sIsRandomFight = true;
		sIsFighting = false;
		
		sInstance = new Combat();
		sInstanceBk = null;
		
		int cnt = 0;
		for (int i = 0; i < monstersType.length; ++i) {
			if (monstersType[i] > 0) {
				++cnt;
			}
		}
		sInstance.mMonsterType = new int[cnt];
		for (int i = 0, j = 0; i < monstersType.length; ++i) {
			if (monstersType[i] > 0) {
				sInstance.mMonsterType[j++] = monstersType[i];
			}
		}
		
		sInstance.mRoundCnt = 0;
		sInstance.mMaxRound = 0; // 回合数无限制
		
		sInstance.createBackgroundBitmap(scrb, scrl, scrr);
	}
	
	private int mScrb, mScrl, mScrR;
	
	public static void write(ObjectOutputStream out) throws IOException {
		out.writeBoolean(IsActive());
		if (IsActive()) {
			out.writeObject(sInstance.mMonsterType);
			out.writeInt(sInstance.mScrb);
			out.writeInt(sInstance.mScrl);
			out.writeInt(sInstance.mScrR);
		}
	}
	
	public static void read(ObjectInputStream in) throws Exception {
		sIsEnable = in.readBoolean();
		if (sIsEnable) {
			int[] monsterType = (int[])in.readObject();
			int scrb = in.readInt();
			int scrl = in.readInt();
			int scrr = in.readInt();
			InitFight(monsterType, scrb, scrl, scrr);
		}
	}
	
	/**
	 * 剧情战斗
	 * @param roundMax 最多回合数，0为无限
	 * @param monstersType 0-3 敌人
	 * @param scr 0战斗背景，1左下角图，2右上角图
	 * @param evtRnds 0-3 战斗中，触发事件的回合
	 * @param evts 0-3 对应的事件号
	 * @param lossto 战斗失败跳转的地址
	 * @param winto 战斗成功跳转的地址
	 */
	public static void EnterFight(int roundMax, int[] monstersType, int[] scr, int[] evtRnds, int[] evts, int lossto, int winto) {
		sIsRandomFight = false;
		
		sInstanceBk = sInstance; // 保存当前随机战斗的引用
		sInstance = new Combat();

		sInstance.mMonsterList = new LinkedList<Monster>();
		for (int i = 0; i < monstersType.length; ++i) {
			if (monstersType[i] > 0) {
				Monster tmp = (Monster)DatLib.GetRes(DatLib.RES_ARS, 3, monstersType[i]);
				if (tmp != null) {
					sInstance.mMonsterList.add(tmp);
				}
			}
		}
		
		sInstance.mMaxRound = roundMax;
		sInstance.mRoundCnt = 0;
		
		PrepareForNewCombat();
		
		sInstance.createBackgroundBitmap(scr[0], scr[1], scr[2]);
		
		sInstance.mEventRound = evtRnds;
		sInstance.mEventNum = evts;
		
		sInstance.mLossAddr = lossto;
		sInstance.mWinAddr = winto;
	}
	
	private static void PrepareForNewCombat() {
		sIsEnable = true;
		sIsFighting = true;
		sInstance.prepareForNewCombat();
	}
	
	private static int COMBAT_PROBABILITY = 20;
	private static Random sRandom = new Random();
	
	/**
	 * 进入一个随机战斗
	 * @return <code>true</code>新战斗 <code>false</code>不开始战斗
	 */
	public static boolean StartNewRandomCombat() {
		if (!sIsEnable || sInstance == null || sRandom.nextInt(COMBAT_PROBABILITY) != 0) {
			sIsFighting = false;
			return false;
		}
		
		// 打乱怪物类型
		for (int i = sInstance.mMonsterType.length - 1; i > 1; --i) {
			int r = sRandom.nextInt(i);
			
			int t = sInstance.mMonsterType[i];
			sInstance.mMonsterType[i] = sInstance.mMonsterType[r];
			sInstance.mMonsterType[r] = t;
		}
		
		// 随机添加怪物
		sInstance.mMonsterList.clear();
		for (int i = sRandom.nextInt(3), j = 0; i >= 0; i--) {
			Monster m = (Monster)DatLib.GetRes(DatLib.RES_ARS, 3, sInstance.mMonsterType[j++]);
			if (m != null) {
				sInstance.mMonsterList.add(m);
			}
		}
		
		sInstance.mRoundCnt = 0;
		sInstance.mMaxRound = 0; // 回合不限
		
		PrepareForNewCombat();
		
		return true;
	}
	
	public static void Update(long delta) {
		sInstance.update(delta);
	}

	public static void Draw(Canvas canvas) {
		sInstance.draw(canvas);
	}
	
	public static void KeyDown(int key) {
		sInstance.onKeyDown(key);
	}
	
	public static void KeyUp(int key) {
		sInstance.onKeyUp(key);
	}
	
	private enum CombatState {
		SelectAction, // 玩家操作阶段，制定攻击策略
		PerformAction, // 执行动作队列，播放攻击动画
		Win, // 赢得战斗
		Loss, // 战斗失败
		Exit
	}
	
	/** 玩家角色中心坐标*/
	public static final Point[] sPlayerPos = new Point[]{
		new Point(64 + 12, 52 + 18), new Point(96 + 12, 48 + 18), new Point(128 + 12, 40 + 18)
	};
	
	private CombatState mCombatState = CombatState.SelectAction;
	
	/** 是否自动攻击，围攻状态*/
	private boolean mIsAutoAttack = false;
	
	/** 动作队列，一个回合中，双方的决策*/
	private LinkedList<Action> mActionQueue = new LinkedList<Action>();
	
	/** 动作队列的执行者*/
	private ActionExecutor mActionExecutor = new ActionExecutor(mActionQueue, this);
	
	/** 战斗的UI*/
	private CombatUI mCombatUI = new CombatUI(this, 0);
	
	/** 随机战斗中，可能出现的敌人类型*/
	private int[] mMonsterType;
	
	/** 参加战斗的怪物队列*/
	private List<Monster> mMonsterList = new LinkedList<Monster>();;
	
	/** 参加战斗的玩家角色队列*/
	private List<Player> mPlayerList;
	
	/** 当前选择动作的角色在{@link #mPlayerList}中的序号*/
	private int mCurSelActionPlayerIndex = 0;
	
	/** 当前回合*/
	private int mRoundCnt;
	
	private boolean mHasEventExed;
	
	/** 最多回合数，0为无限*/
	private int mMaxRound;
	
	/** 触发事件的回合，以及对应的事件号*/
	private int[] mEventRound, mEventNum;
	
	/** 战斗失败跳转地址，战斗成功跳转地址*/
	private int mLossAddr, mWinAddr;
	
	private ResSrs mFlyPeach = (ResSrs)DatLib.GetRes(DatLib.RES_SRS, 1, 249);
	
	private boolean mIsWin = false;
	
	/**战斗背景图*/
	Bitmap mBackground;
	
	/** 战斗胜利能获得的金钱和经验*/
	private int mWinMoney, mWinExp;
	
	private CombatSuccess mCombatSuccess;
	
	private long mTimeCnt = 0;
	
	private Combat() {}
	
	private void createBackgroundBitmap(int scrb, int scrl, int scrr) {
		mBackground = Bitmap.createBitmap(160, 96, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBackground);
		ResImage img;
		img = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 4, scrb);
		if (img != null) img.draw(canvas, 1, 0, 0); // 背景
		img = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 4, scrl);
		if (img != null) img.draw(canvas, 1, 0, 96 - img.getHeight()); // 左下角
		img = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 4, scrr);
		if (img != null) img.draw(canvas, 1, 160 - img.getWidth(), 0); // 右上角
		
		mScrb = scrb;
		mScrl = scrl;
		mScrR = scrr;
	}
	
	private void prepareForNewCombat() {
		mActionQueue.clear();
		
		mIsAutoAttack = false;
		mCombatState = CombatState.SelectAction;
		
		mCurSelActionPlayerIndex = 0;
		mPlayerList = ScreenMainGame.sPlayerList;
		
		mCombatUI.reset();
		mCombatUI.setCurrentPlayerIndex(0);
		mCombatUI.setMonsterList(mMonsterList);
		mCombatUI.setPlayerList(mPlayerList);
		
		setOriginalPlayerPos();
		setOriginalMonsterPos();
		
		mRoundCnt = 0;
		
		mHasEventExed = false;
		
		// 检查玩家血量
		for (Player p : mPlayerList) {
			if (p.getHP() <= 0) { // 确保血量大于0
				p.setHP(1);
			}
			p.setFrameByState();
		}
		
		// 怪物血量设置为其最大值
		for (Monster m : mMonsterList) {
			m.setHP(m.getMaxHP());
		}
		
		// 计算战斗胜利能获得的金钱和经验
		mWinMoney = 0;
		mWinExp = 0;
		for (Monster m : mMonsterList) {
			mWinMoney += m.getMoney();
			mWinExp += m.getExp();
		}
		
		if (!sIsRandomFight && mMonsterList.size() == 1) { // 剧情战斗，只有一个怪时，怪的位置在中间
			Monster m = mMonsterList.get(0);
			Monster n = (Monster)DatLib.GetRes(DatLib.RES_ARS, m.getType(), m.getIndex());
			n.setHP(-1);
			n.setVisiable(false);
			mMonsterList.add(0, n); // 加入一个看不见的怪
			setOriginalMonsterPos(); // 重置位置
		}
		
		mFlyPeach.startAni();
		mFlyPeach.setIteratorNum(5);
	}

	private void exitCurrentCombat() {
		if (!sIsRandomFight) {
			ScreenMainGame.instance.gotoAddress(mIsWin ? mWinAddr : mLossAddr);
			ScriptExecutor.goonExecute = true;
			sIsRandomFight = true;
			sInstance = sInstanceBk;
			sInstanceBk = null;
			System.gc();
		} else {
			if (!mIsWin) { // 死了，游戏结束
				GameView.getInstance().changeScreen(Global.SCREEN_MENU);
			}
		}
		
		sIsFighting = false;
		mActionQueue.clear();
		mActionExecutor.reset();
		mCombatUI.reset();
		mIsAutoAttack = false;
		
		// 恢复一定的血量
		for (Player p : mPlayerList) {
			if (p.getHP() <= 0) {
				p.setHP(1);
			}
			if (p.getMP() <= 0) {
				p.setMP(1);
			}
			p.setHP(p.getHP() + (p.getMaxHP() - p.getHP()) / 10);
			p.setMP(p.getMP() + p.getMaxMP() / 5);
			if (p.getMP() > p.getMaxMP()) {
				p.setMP(p.getMaxMP());
			}
		}
	}
	
	private void setOriginalPlayerPos() {
		for (int i = 0; i < mPlayerList.size(); i++) {
			mPlayerList.get(i).setCombatPos(sPlayerPos[i].x, sPlayerPos[i].y);
		}
	}
	
	private void setOriginalMonsterPos() {
		for (int i = 0; i < mMonsterList.size(); i++) {
			mMonsterList.get(i).setOriginalCombatPos(i);
		}
	}
	
	@Override
	public void update(long delta) {
		mTimeCnt += delta;
		switch (mCombatState) {
		case SelectAction:
			if (!mHasEventExed && !sIsRandomFight) {
				mHasEventExed = true;
				for (int i = 0; i < mEventRound.length; i++) {
					if (mRoundCnt == mEventRound[i] && mEventNum[i] != 0) {
						ScreenMainGame.instance.triggerEvent(mEventNum[i]);
					}
				}
			}
			if (mIsAutoAttack) { // 自动生成动作队列
				generateAutoActionQueue();
				mCombatState = CombatState.PerformAction;
			} else { // 玩家决策
				mCombatUI.update(delta);
			}
			break;
			
		case PerformAction:
			if (!mActionExecutor.update(delta)) { // 动作执行完毕
				if (isAllMonsterDead()) { // 怪物全挂
					mTimeCnt = 0; // 计时器清零
					mCombatState = CombatState.Win;
					
					Player.sMoney += mWinMoney; // 获得金钱
					List<Player> lvuplist = new LinkedList<Player>();
					for (Player p : mPlayerList) { // 获得经验
						if (p.isAlive()) {
							if (p.getLevel() >= p.getLevelupChain().getMaxLevel()) // 满级
								break;
							int nextExp = p.getLevelupChain().getNextLevelExp(p.getLevel());
							int exp = mWinExp + p.getCurrentExp();
							if (exp < nextExp) {
								p.setCurrentExp(exp);
							} else { // 升级
								int cl = p.getLevel(); // 当前等级
								ResLevelupChain c = p.getLevelupChain();
								p.setCurrentExp(exp - nextExp);
								p.setLevel(cl + 1);
								p.setMaxHP(p.getMaxHP() + c.getMaxHP(cl + 1) - c.getMaxHP(cl));
//								p.setHP(p.getMaxHP()); CombatSuccess 中设置
								p.setMaxMP(p.getMaxMP() + c.getMaxMP(cl + 1) - c.getMaxMP(cl));
//								p.setMP(p.getMaxMP()); CombatSuccess 中设置
								p.setAttack(p.getAttack() + c.getAttack(cl + 1) - c.getAttack(cl));
								p.setDefend(p.getDefend() + c.getDefend(cl + 1) - c.getDefend(cl));
								p.getMagicChain().setLearnNum(c.getLearnMagicNum(cl + 1));
								p.setSpeed(p.getSpeed() + c.getSpeed(cl + 1) - c.getSpeed(cl));
								p.setLingli(p.getLingli() + c.getLingli(cl + 1) - c.getLingli(cl));
								p.setLuck(p.getLuck() + c.getLuck(cl + 1) - c.getLuck(cl));
								lvuplist.add(p);
							}
						}
					}
					
					// 最大幸运值
					int ppt = 10;
					for (Player p : mPlayerList) {
						if (p.getLuck() > ppt) {
							ppt = p.getLuck();
						}
					}
					ppt -= 10;
					if (ppt > 100) {
						ppt = 100;
					} else if (ppt < 0) {
						ppt = 10;
					}
					
					// 战利品链表
					GoodsManage gm = new GoodsManage();
					LinkedList<BaseGoods> gl = new LinkedList<BaseGoods>();
					for (Monster m : mMonsterList) {
						BaseGoods g = m.getDropGoods();
						if (g != null && sRandom.nextInt(101) < ppt) { //  ppt%掉率
							gm.addGoods(g.getType(), g.getIndex(), g.getGoodsNum());
							Player.sGoodsList.addGoods(g.getType(), g.getIndex(), g.getGoodsNum()); // 添加到物品链表
						}
					}
					gl.addAll(gm.getGoodsList());
					gl.addAll(gm.getEquipList());
					mCombatSuccess = new CombatSuccess(mWinExp, mWinMoney, gl, lvuplist); // 显示玩家的收获
				} else { // 还有怪物存活
					if (isAnyPlayerAlive()) { // 有玩家角色没挂，继续打怪
						++mRoundCnt;
						updateFighterState();
						mCombatState = CombatState.SelectAction;
						mCurSelActionPlayerIndex = getFirstAlivePlayerIndex();
						mCombatUI.setCurrentPlayerIndex(mCurSelActionPlayerIndex);
						for (Player p : mPlayerList) {
							p.setFrameByState();
						}
					} else { // 玩家角色全挂，战斗失败
						mTimeCnt = 0;
						mCombatState = CombatState.Loss;
					}
				}
			}
			break;
			
		case Win:
			// TODO if (winAddr...)
//			if (mTimeCnt > 1000) {
//				mCombatState = CombatState.Exit;
//			}
			mIsWin = true;
			if (mCombatSuccess.update(delta)) {
				mCombatState = CombatState.Exit;
			}
			break;
			
		case Loss:
			// TODO if (lossAddr...)
			if (sIsRandomFight && mFlyPeach.update(delta)) {
				
			} else {
				mIsWin = false;
				mCombatState = CombatState.Exit;
			}
			break;
			
		case Exit:
			exitCurrentCombat();
			break;
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(mBackground, 0, 0, null);
		
		// draw the monsters and players
		for (int i = 0; i < mMonsterList.size(); i++) {
			FightingCharacter fc = mMonsterList.get(i);
			if (fc.isVisiable()) {
				fc.getFightingSprite().draw(canvas);
			}
		}
		
		for (int i = mPlayerList.size() - 1; i >= 0; i--) {
			FightingSprite f = mPlayerList.get(i).getFightingSprite();
			f.draw(canvas);
		}
		
		if (mCombatState == CombatState.SelectAction && !mIsAutoAttack) {
			mCombatUI.draw(canvas);
		} else if (mCombatState == CombatState.PerformAction) {
			mActionExecutor.draw(canvas);
		} else if (mCombatState == CombatState.Win) {
//			TextRender.drawText(canvas, "Win", 20, 40);
			mCombatSuccess.draw(canvas);
		} else if (mCombatState == CombatState.Loss && sIsRandomFight) {
//			TextRender.drawText(canvas, "Loss", 20, 40);
			mFlyPeach.draw(canvas, 0, 0);
		}
	}

	@Override
	public void onKeyDown(int key) {
		if (mCombatState == CombatState.SelectAction) {
			if (!mIsAutoAttack) {
				mCombatUI.onKeyDown(key);
			}
		} else if (mCombatState == CombatState.Win) {
			mCombatSuccess.onKeyDown(key);
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (mCombatState == CombatState.SelectAction) {
			if (!mIsAutoAttack) {
				mCombatUI.onKeyUp(key);
			}
		} else if (mCombatState == CombatState.Win) {
			mCombatSuccess.onKeyUp(key);
		}
		
		if (mIsAutoAttack && key == Global.KEY_CANCEL) { // 退出“围攻”模式
			mIsAutoAttack = false;
		}
	}
	
	private void generateAutoActionQueue() {
		Monster monster = getFirstAliveMonster();
		
		mActionQueue.clear();
		
		// 玩家的Action
		for (Player p : mPlayerList) {
			if (p.isAlive()) {
				mActionQueue.add(p.hasAtbuff(Player.BUFF_MASK_ALL) ? 
						new ActionPhysicalAttackAll(p, mMonsterList) :
						new ActionPhysicalAttackOne(p, monster));
			}
		}
		
		// 怪物的Action
		generateMonstersActions();
		
		sortActionQueue();
	}
	
	private void generateMonstersActions() {
		// TODO according to the monster's intelligence, add some magic attack
		for (Monster m : mMonsterList) {
			if (m.isAlive()) {
				Player p = getRandomAlivePlayer();
				if (p != null) {
					mActionQueue.add(m.hasAtbuff(Monster.BUFF_MASK_ALL) ?
							new ActionPhysicalAttackAll(m, mPlayerList) :
							new ActionPhysicalAttackOne(m, p));
				}
			}
		}
	}
	
	private static Comparator<Action> sComparator = new Comparator<Action>() {

		@Override
		public int compare(Action lhs, Action rhs) {
			return rhs.getPriority() - lhs.getPriority();
		}
		
		
	};
	
	/** 按敏捷从大到小排列*/
	private void sortActionQueue() {
		Collections.sort(mActionQueue, sComparator);
	}
	
	/** 是否有玩家角色存活*/
	private boolean isAnyPlayerAlive() {
		for (Player p : mPlayerList) {
			if (p.getHP() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/** 怪物是否都挂了*/
	private boolean isAllMonsterDead() {
		return getFirstAliveMonster() == null;
	}
	
	/** index 之后的主角是否都挂*/
	private boolean isPlayerBehindDead(int index) {
		for (int i = index + 1; i < mPlayerList.size(); i++) {
			if (mPlayerList.get(i).isAlive()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取下一个存活的主角序号
	 * @return 没有就返回-1
	 */
	private int getNextAlivePlayerIndex() {
		for (int i = mCurSelActionPlayerIndex + 1; i < mPlayerList.size(); i++) {
			if (mPlayerList.get(i).isAlive()) {
				return i;
			}
		}
		return -1;
	}
	
	private int getPreAlivePlayerIndex() {
		for (int i = mCurSelActionPlayerIndex - 1; i >= 0; i--) {
			if (mPlayerList.get(i).isAlive()) {
				return i;
			}
		}
		return -1;
	}
	
	private int getFirstAlivePlayerIndex() {
		for (int i = 0; i < mPlayerList.size(); i++) {
			if (mPlayerList.get(i).isAlive()) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @return 第一个活着的怪物，<code>null</code>怪物都挂了
	 */
	public Monster getFirstAliveMonster() {
		for (Monster m : mMonsterList) {
			if (m.isAlive()) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * 随机获取一个或者的玩家角色
	 * @return <code>null</code>全死了
	 */
	public Player getRandomAlivePlayer() {
		int cnt = 0;
		for (Player p : mPlayerList) {
			if (p.isAlive()) {
				++cnt;
			}
		}
		
		if (cnt == 0) return null; // 全死了
		
		Player[] arr = new Player[cnt];
		int i = 0;
		for (Player p : mPlayerList) {
			if (p.isAlive()) {
				arr[i++] = p;
			}
		}
		
		return arr[sRandom.nextInt(cnt)];
	}
	
	/** 更新双方状态*/
	private void updateFighterState() {
		// TODO decrease the buff's round count 
	}

	@Override
	public void onActionSelected(Action action) {
		mActionQueue.add(action);
		
		mCombatUI.reset(); // 重置战斗UI
		
		if (action instanceof ActionCoopMagic) { // 只保留合击
			mActionQueue.clear();
			mActionQueue.add(action);
			generateMonstersActions();
			sortActionQueue();
			mCombatState = CombatState.PerformAction;
		} else if (mCurSelActionPlayerIndex >= mPlayerList.size() - 1 ||
				isPlayerBehindDead(mCurSelActionPlayerIndex)) { // 全部玩家角色的动作选择完成
			generateMonstersActions();
			sortActionQueue();
			mCombatState = CombatState.PerformAction; // 开始执行动作队列
		} else { // 选择下一个玩家角色的动作
			mCurSelActionPlayerIndex = getNextAlivePlayerIndex();
//			if (mPlayerList.get(mCurSelActionPlayerIndex).hasDebuff(0)) TODO 乱眠死不能自己选择action
			mCombatUI.setCurrentPlayerIndex(mCurSelActionPlayerIndex);
		}
	}
	
	@Override
	public void onAutoAttack() {
		// clear all the actions that has been selected, enter into auto fight mode
		mCombatUI.reset();
		mActionQueue.clear();
		mIsAutoAttack = true;
		mCombatState = CombatState.SelectAction;
	}
	
	@Override
	public void onFlee() {
		// TODO add flee action to all the other actor
		
		mCombatUI.reset(); // 重置战斗UI
		
		for (int i = mCurSelActionPlayerIndex; i < mPlayerList.size(); i++) {
			if (mPlayerList.get(i).isAlive() && sRandom.nextBoolean() && sIsRandomFight) { // 50% 逃走
				mActionQueue.add(new ActionFlee(mPlayerList.get(i), true, new Runnable() {
					
					@Override
					public void run() {
						// 逃跑成功后执行 
						mIsWin = true;
						mCombatState = CombatState.Exit;
					}
				}));
				break;
			} else { // 逃跑失败
				mActionQueue.add(new ActionFlee(mPlayerList.get(i), false, null));
			}
		}
		generateMonstersActions();
		sortActionQueue();
		mCombatState = CombatState.PerformAction;
	}

	@Override
	public void onCancel() {
		int i = getPreAlivePlayerIndex();
		if (i >= 0) { // 不是第一个角色
			 // 重选上一个角色的动作
			mActionQueue.removeLast();
			mCurSelActionPlayerIndex = i;
			mCombatUI.setCurrentPlayerIndex(mCurSelActionPlayerIndex);
			
			mCombatUI.reset();
		}
	}

}

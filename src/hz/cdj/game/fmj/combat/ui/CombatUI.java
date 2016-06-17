package hz.cdj.game.fmj.combat.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.FightingCharacter;
import hz.cdj.game.fmj.characters.Monster;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.combat.actions.Action;
import hz.cdj.game.fmj.combat.actions.ActionCoopMagic;
import hz.cdj.game.fmj.combat.actions.ActionDefend;
import hz.cdj.game.fmj.combat.actions.ActionMagicAttackAll;
import hz.cdj.game.fmj.combat.actions.ActionMagicAttackOne;
import hz.cdj.game.fmj.combat.actions.ActionMagicHelpAll;
import hz.cdj.game.fmj.combat.actions.ActionMagicHelpOne;
import hz.cdj.game.fmj.combat.actions.ActionPhysicalAttackAll;
import hz.cdj.game.fmj.combat.actions.ActionPhysicalAttackOne;
import hz.cdj.game.fmj.combat.actions.ActionThrowItemAll;
import hz.cdj.game.fmj.combat.actions.ActionThrowItemOne;
import hz.cdj.game.fmj.combat.actions.ActionUseItemAll;
import hz.cdj.game.fmj.combat.actions.ActionUseItemOne;
import hz.cdj.game.fmj.combat.anim.FrameAnimation;
import hz.cdj.game.fmj.gamemenu.ScreenChgEquipment;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList.Mode;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.goods.GoodsEquipment;
import hz.cdj.game.fmj.goods.GoodsHiddenWeapon;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.magic.BaseMagic;
import hz.cdj.game.fmj.magic.MagicAttack;
import hz.cdj.game.fmj.magic.MagicSpecial;
import hz.cdj.game.fmj.magic.ScreenMagic;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class CombatUI extends BaseScreen {
	
	private static final Point[] sPlayerIndicatorPos = new Point[]{
		new Point(69, 45), new Point(101, 41), new Point(133, 33)
	};
	
	private static final Point[] sMonsterIndicatorPos = new Point[]{
		new Point(16, 14), new Point(48, 3), new Point(86, 0)
	};
	
	public interface CallBack {
		/**
		 * 当一个Action被选择后，会调用此方法
		 * @param action 选择的Action
		 */
		void onActionSelected(Action action);
		
		/**
		 * 选择围攻时，调用改方法
		 */
		void onAutoAttack();
		
		/**
		 * 选择逃跑时，调用该方法。对于已经做出决策的角色，其决策不变；之后的角色动作皆为逃跑
		 */
		void onFlee();
		
		/**
		 * 取消选择当前角色的Action，应该返回选择上一个角色的Action
		 */
		void onCancel();
	}
	
	private CallBack mCallBack;
	
	private Stack<BaseScreen> mScreenStack = new Stack<BaseScreen>();
	
	private List<Player> mPlayerList;
	private List<Monster> mMonsterList;
	private int mCurPlayerIndex;
	
	/** 标记发出的action的玩家角色*/
	private FrameAnimation mPlayerIndicator;
	
	/** 标记action作用的玩家角色*/
	private FrameAnimation mTargetIndicator;
	
	/** 标记action作用的敌人角色*/
	private FrameAnimation mMonsterIndicator;
	
	public CombatUI(CallBack callBack, int curPlayerIndex) {
		mCallBack = callBack;
		mCurPlayerIndex = curPlayerIndex;
		mScreenStack.push(new MainMenu());
		
		ResImage tmpImg;
		tmpImg = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 4);
		mPlayerIndicator = new FrameAnimation(tmpImg, 1, 2);
		mTargetIndicator = new FrameAnimation(tmpImg, 3, 4);
		tmpImg = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 3);
		mMonsterIndicator = new FrameAnimation(tmpImg);
	}

	@Override
	public void update(long delta) {
		for (BaseScreen bs : mScreenStack) {
			bs.update(delta);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		for (BaseScreen bs : mScreenStack) {
			bs.draw(canvas);
		}
	}

	@Override
	public void onKeyDown(int key) {
		BaseScreen bs = mScreenStack.peek();
		if (bs != null) {
			bs.onKeyDown(key);
		}
	}

	@Override
	public void onKeyUp(int key) {
		BaseScreen bs = mScreenStack.peek();
		if (bs != null) {
			bs.onKeyUp(key);
		}
	}
	
	public void reset() {
		mScreenStack.clear();
		mScreenStack.push(new MainMenu());
	}
	
	public void setPlayerList(List<Player> list) {
		mPlayerList = list;
	}
	
	public void setMonsterList(List<Monster> list) {
		mMonsterList = list;
	}
	
	/** */
	public void setCurrentPlayerIndex(int i) {
		mCurPlayerIndex = i;
	}
	
	/** helper for the callback interface*/
	private void onActionSelected(Action action) {
		if (mCallBack != null) {
			mCallBack.onActionSelected(action);
		}
	}

	/** helper for the callback interface*/
	private void onCancel() {
		if (mCallBack != null) {
			mCallBack.onCancel();
		}
	}
	
	public byte[] getGBKBytes(String s) {
		try {
			return s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ResImage[] mHeadsImg = new ResImage[]{
			(ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, 1),
			(ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, 2),
			(ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, 3)
	};
	

	/** 显示主菜单、角色信息*/
	private class MainMenu extends BaseScreen {
		
		/** 1↑、2←、3↓、4→*/
		private ResImage mMenuIcon = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 1);
		
		/** 显示角色HP MP的背景图*/
		private ResImage mPlayerInfoBg = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 2);
		
		private int mCurIconIndex = 1;

		@Override
		public void update(long delta) {
			mPlayerIndicator.update(delta);
		}

		@Override
		public void draw(Canvas canvas) {
			mMenuIcon.draw(canvas, mCurIconIndex, 7, 96 - mMenuIcon.getHeight());
			mPlayerInfoBg.draw(canvas, 1, 49, 66);
			Player p = CombatUI.this.mPlayerList.get(mCurPlayerIndex);
			mHeadsImg[p.getIndex() - 1].draw(canvas, 1, 50, 63); // 角色头像
			if (p != null) {
				Util.drawSmallNum(canvas, p.getHP(), 79, 72); // hp
				Util.drawSmallNum(canvas, p.getMaxHP(), 104, 72); // maxhp
				Util.drawSmallNum(canvas, p.getMP(), 79, 83); // mp
				Util.drawSmallNum(canvas, p.getMaxMP(), 104, 83); // maxmp
			}
			mPlayerIndicator.draw(canvas, sPlayerIndicatorPos[mCurPlayerIndex].x, sPlayerIndicatorPos[mCurPlayerIndex].y);
		}

		@Override
		public void onKeyDown(int key) {
			switch (key) {
			case Global.KEY_LEFT:
				if (mPlayerList.get(mCurPlayerIndex).hasDebuff(Player.BUFF_MASK_FENG)) {
					break; // 被封，不能用魔法
				}
				mCurIconIndex = 2;
				break;

			case Global.KEY_DOWN:
				mCurIconIndex = 3;
				break;
				
			case Global.KEY_RIGHT:
				if (mPlayerList.size() <= 1) { // 只有一人不能合击
					break;
				}
				mCurIconIndex = 4;
				break;
				
			case Global.KEY_UP:
				mCurIconIndex = 1;
				break;
			}
		}

		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_ENTER) {
				switch (mCurIconIndex) {
				case 1://物理攻击
					// 攻击全体敌人
					if (mPlayerList.get(mCurPlayerIndex).hasAtbuff(Player.BUFF_MASK_ALL)) {
						onActionSelected(new ActionPhysicalAttackAll(mPlayerList.get(mCurPlayerIndex), mMonsterList));
						break;
					}
					
					// 攻击单个敌人
					CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mMonsterIndicator, sMonsterIndicatorPos,
							mMonsterList, new OnCharacterSelectedListener() {
						
						@Override
						public void onCharacterSelected(FightingCharacter fc) {
							onActionSelected(new ActionPhysicalAttackOne(mPlayerList.get(mCurPlayerIndex), fc));
						}
					}, true));
					break;

				case 2://魔法技能
					GameView.getInstance().pushScreen(new ScreenMagic(mPlayerList.get(mCurPlayerIndex).getMagicChain(),
							new ScreenMagic.OnItemSelectedListener() {
								
								@Override
								public void onItemSelected(final BaseMagic magic) {
									GameView.getInstance().popScreen(); // 弹出魔法选择界面
									if ((magic instanceof MagicAttack) || (magic instanceof MagicSpecial)) { // 选一个敌人
										if (magic.isForAll()) {
											onActionSelected(new ActionMagicAttackAll(mPlayerList.get(mCurPlayerIndex),
													mMonsterList, (MagicAttack)magic));
										} else { // 选一个敌人
											CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mMonsterIndicator, sMonsterIndicatorPos,
													mMonsterList, new OnCharacterSelectedListener() {
												
												@Override
												public void onCharacterSelected(FightingCharacter fc) {
													onActionSelected(new ActionMagicAttackOne(mPlayerList.get(mCurPlayerIndex), fc, magic));
												}
											}, true));
										}
									} else { // 选队友或自己
										if (magic.isForAll()) {
											onActionSelected(new ActionMagicHelpAll(mPlayerList.get(mCurPlayerIndex),
													mPlayerList, magic));
										} else { // 选一个Player
											CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mTargetIndicator, sPlayerIndicatorPos,
													mPlayerList, new OnCharacterSelectedListener() {
												
												@Override
												public void onCharacterSelected(FightingCharacter fc) {
													onActionSelected(new ActionMagicHelpOne(mPlayerList.get(mCurPlayerIndex),
															fc, magic));
												}
											}, false));
										}
									}
								}
							}));
					break;
					
				case 3://杂项
					CombatUI.this.mScreenStack.push(new MenuMisc());
					break;
					
				case 4://合击
					CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mMonsterIndicator, sMonsterIndicatorPos,
							mMonsterList, new OnCharacterSelectedListener() {
						
						@Override
						public void onCharacterSelected(FightingCharacter fc) {
							onActionSelected(new ActionCoopMagic(mPlayerList, fc));
						}
					}, true));
					break;
				}
			} else if (key == Global.KEY_CANCEL) {
				CombatUI.this.onCancel();
			}
		}
		
	}

	/**
	 * @see MenuCharacterSelect
	 */
	private interface OnCharacterSelectedListener {
		/**
		 * 
		 * @param fc 被选择的角色
		 */
		void onCharacterSelected(FightingCharacter fc);
	}
	
	/** 角色标识，用于标记当前选择的角色*/
	private class MenuCharacterSelect extends BaseScreen {
		
		private OnCharacterSelectedListener mOnCharacterSelectedListener;
		
		private FrameAnimation mIndicator;
		
		private Point[] mIndicatorPos;
		
		private List<? extends FightingCharacter> mList;
		
		private int mCurSel;
		
		private boolean mIgnoreDead;
		
		/**
		 * 
		 * @param indicator 标记符的帧动画
		 * @param pos 标记符的位置
		 * @param list 角色链表
		 * @param ignoreDead 跳过死亡角色
		 */
		public MenuCharacterSelect(FrameAnimation indicator, Point[] pos,
				List<? extends FightingCharacter> list,
				OnCharacterSelectedListener l, boolean ignoreDead) {
			mIndicator = indicator;
			mIndicatorPos = pos;
			mList = list;
			mOnCharacterSelectedListener = l;
			mIgnoreDead = ignoreDead;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isAlive()) {
					mCurSel = i;
					break;
				}
			}
		}

		@Override
		public void update(long delta) {
			mIndicator.update(delta);
		}

		@Override
		public void draw(Canvas canvas) {
			mIndicator.draw(canvas, mIndicatorPos[mCurSel].x, mIndicatorPos[mCurSel].y);
			if (mIndicator == CombatUI.this.mTargetIndicator) { // 当前选择角色
				Player p = CombatUI.this.mPlayerList.get(mCurSel);
				mHeadsImg[p.getIndex() - 1].draw(canvas, 1, 50, 63); // 角色头像
				if (p != null) {
					Util.drawSmallNum(canvas, p.getHP(), 79, 72); // hp
					Util.drawSmallNum(canvas, p.getMaxHP(), 104, 72); // maxhp
					Util.drawSmallNum(canvas, p.getMP(), 79, 83); // mp
					Util.drawSmallNum(canvas, p.getMaxMP(), 104, 83); // maxmp
				}
			}
		}
		
		private void selectNextTarget() {
			do {
				++mCurSel;
				mCurSel %= mList.size();
			} while(mIgnoreDead && !mList.get(mCurSel).isAlive());
		}
		
		private void selectPreTarget() {
			do {
				--mCurSel;
				mCurSel = (mCurSel + mList.size()) % mList.size();
			} while (mIgnoreDead && !mList.get(mCurSel).isAlive());
		}

		@Override
		public void onKeyDown(int key) {
			if (key == Global.KEY_RIGHT) {
				selectNextTarget();
			} else if (key == Global.KEY_LEFT) {
				selectPreTarget();
			}
		}

		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_CANCEL) {
				CombatUI.this.mScreenStack.pop();
			} else if (key == Global.KEY_ENTER) {
				CombatUI.this.mScreenStack.pop();
				if (mOnCharacterSelectedListener != null) {
					mOnCharacterSelectedListener.onCharacterSelected(mList.get(mCurSel));
				}
			}
		}
		
	}
	
	/** 围攻、道具、防御、逃跑、状态*/
	private class MenuMisc extends BaseScreen {
		
		private Bitmap mBg = Util.getFrameBitmap(2 * 16 + 6, 5 * 16 + 6);
		
		private byte[] mText = getGBKBytes("围攻道具防御逃跑状态");
		
		private byte[][] mItemText = new byte[][]{
				getGBKBytes("围攻"),
				getGBKBytes("道具"),
				getGBKBytes("防御"),
				getGBKBytes("逃跑"),
				getGBKBytes("状态")
		};
		
		private Rect mTextRect = new Rect(9 + 3, 4 + 3, 9 + 4 + 16 * 2, 4 + 3 + 16 * 5);
		
		private int mCurSelIndex = 0;

		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mBg, 9, 4, null);
			TextRender.drawText(canvas, mText, 0, mTextRect);
			TextRender.drawSelText(canvas, mItemText[mCurSelIndex], mTextRect.left, mTextRect.top + mCurSelIndex * 16);
		}

		@Override
		public void onKeyDown(int key) {
			if (key == Global.KEY_UP) {
				--mCurSelIndex;
				mCurSelIndex = (mItemText.length + mCurSelIndex) % mItemText.length;
			} else if (key == Global.KEY_DOWN) {
				++mCurSelIndex;
				mCurSelIndex %= mItemText.length;
			}
		}

		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_ENTER) {
				switch (mCurSelIndex) {
				case 0://围攻
					if (mCallBack != null) {
						mCallBack.onAutoAttack();
					}
					break;
				case 1://道具
					CombatUI.this.mScreenStack.push(new MenuGoods());
					break;
				case 2://防御
					Player p = mPlayerList.get(mCurPlayerIndex);
					p.getFightingSprite().setCurrentFrame(9);
					CombatUI.this.onActionSelected(new ActionDefend(p));
					break;
				case 3://逃跑
					if (mCallBack != null) {
						mCallBack.onFlee();
					}
					break;
				case 4://状态
					CombatUI.this.mScreenStack.pop();
					CombatUI.this.mScreenStack.push(new MenuState());
					break;
				}
			} else if (key == Global.KEY_CANCEL) {
				CombatUI.this.mScreenStack.pop();
			}
		}
		
		/** 战斗中，显示玩家异常状态*/
		private class MenuState extends BaseScreen {
			
			private ResImage mBg = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 11);
			
			/**1↑2↓3×4√5回*/
			private ResImage mMarker = (ResImage)DatLib.GetRes(DatLib.RES_PIC, 2, 12);
			
			private int mCurPlayer;
			
			public MenuState() {
				mCurPlayer = CombatUI.this.mCurPlayerIndex;
			}

			@Override
			public void update(long delta) {
			}

			@Override
			public void draw(Canvas canvas) {
				int x = (160 - mBg.getWidth()) / 2;
				int y = (96 - mBg.getHeight()) / 2;
				mBg.draw(canvas, 1, x, y);
				Player p = mPlayerList.get(this.mCurPlayer);
				p.drawHead(canvas, x + 7, y + 4);
				Util.drawSmallNum(canvas, p.getHP(), x + 50, y + 9); // 命
				Util.drawSmallNum(canvas, p.getAttack(), x + 50, y + 21); // 攻
				Util.drawSmallNum(canvas, p.getLuck(), x + 87, y + 9); // 运
				Util.drawSmallNum(canvas, p.getSpeed(), x + 87, y + 21); // 身
				// TODO TODO TODO ...
				mMarker.draw(canvas, 1, x + 9, y + 48); // 攻
				mMarker.draw(canvas, 2, x + 25, y + 48); // 防
				mMarker.draw(canvas, 5, x + 41, y + 48); // 身
				mMarker.draw(canvas, 3, x + 57, y + 48); // 毒
				mMarker.draw(canvas, 4, x + 73, y + 48); // 乱
				mMarker.draw(canvas, 3, x + 88, y + 48); // 封
				mMarker.draw(canvas, 4, x + 104, y + 48); // 眠
				Util.drawSmallNum(canvas, 5, x + 10, y + 57); // 攻
				Util.drawSmallNum(canvas, 5, x + 26, y + 57); // 防
				Util.drawSmallNum(canvas, 5, x + 42, y + 57); // 身
				Util.drawSmallNum(canvas, 5, x + 58, y + 57); // 毒
				Util.drawSmallNum(canvas, 5, x + 74, y + 57); // 乱
				Util.drawSmallNum(canvas, 5, x + 90, y + 57); // 封
				Util.drawSmallNum(canvas, 5, x + 106, y + 57); // 眠
			}

			@Override
			public void onKeyDown(int key) {
				switch (key) {
				case Global.KEY_RIGHT:
				case Global.KEY_DOWN:
				case Global.KEY_PAGEDOWN:
				case Global.KEY_ENTER:
					++this.mCurPlayer;
					this.mCurPlayer %= CombatUI.this.mPlayerList.size();
					break;
					
				case Global.KEY_LEFT:
				case Global.KEY_UP:
				case Global.KEY_PAGEUP:
					--this.mCurPlayer;
					this.mCurPlayer = (this.mCurPlayer + mPlayerList.size()) % mPlayerList.size();
					break;
				}
			}

			@Override
			public void onKeyUp(int key) {
				if (key == Global.KEY_CANCEL) {
					CombatUI.this.mScreenStack.pop();
					CombatUI.this.mScreenStack.push(new MenuMisc());
				}
			}
			
		}
	}
	
	/** 道具子菜单，装备、投掷、使用*/
	private class MenuGoods extends BaseScreen {
		
		private Bitmap mBg = Util.getFrameBitmap(16 * 2 + 6, 16 * 3 + 6);
		
		private byte[] mText = getGBKBytes("装备投掷使用");
		
		private byte[][] mItemText = new byte[][]{getGBKBytes("装备"), getGBKBytes("投掷"), getGBKBytes("使用")};
		
		private Rect mTextRect = new Rect(29 + 3, 14 + 3, 29 + 3 + mBg.getWidth(), 14 + 3 + mBg.getHeight());
		
		private int mSelIndex = 0;

		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(mBg, 29, 14, null);
			TextRender.drawText(canvas, mText, 0, mTextRect);
			TextRender.drawSelText(canvas, mItemText[mSelIndex], mTextRect.left, mTextRect.top + 16 * mSelIndex);
		}

		@Override
		public void onKeyDown(int key) {
			if (key == Global.KEY_DOWN) {
				++mSelIndex;
				mSelIndex %= mItemText.length;
			} else if (key == Global.KEY_UP) {
				--mSelIndex;
				mSelIndex = (mSelIndex + mItemText.length) % mItemText.length;
			}
		}

		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_ENTER) {
				CombatUI.this.mScreenStack.pop(); // 弹出子菜单
				switch (mSelIndex) {
				case 0:// 装备
					GameView.getInstance().pushScreen(new ScreenGoodsList(Player.sGoodsList.getEquipList(),
							new ScreenGoodsList.OnItemSelectedListener() {
								
								@Override
								public void onItemSelected(BaseGoods goods) {
									equipSelected(goods);
								}
							}, Mode.Use));
					break;
					
				case 1:// 投掷
					GameView.getInstance().pushScreen(new ScreenGoodsList(getThrowableGoodsList(),
							new ScreenGoodsList.OnItemSelectedListener() {
								
								@Override
								public void onItemSelected(final BaseGoods goods) {
									GameView.getInstance().popScreen(); // pop goods list
									CombatUI.this.mScreenStack.pop(); // pop misc menu
									if (goods.effectAll()) {
										// 投掷伤害全体敌人
										onActionSelected(new ActionThrowItemAll(mPlayerList.get(mCurPlayerIndex), mMonsterList, (GoodsHiddenWeapon)goods));
									} else { // 选一个敌人
										CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mMonsterIndicator, sMonsterIndicatorPos, mMonsterList,
												new OnCharacterSelectedListener() {
													
													@Override
													public void onCharacterSelected(FightingCharacter fc) {
														// add throw action
														onActionSelected(new ActionThrowItemOne(mPlayerList.get(mCurPlayerIndex),
																fc, (GoodsHiddenWeapon)goods));
													}
												}, true));
									}
								}
							}, Mode.Use));
					break;
					
				case 2:// 使用
					GameView.getInstance().pushScreen(new ScreenGoodsList(getUseableGoodsList(),
							new ScreenGoodsList.OnItemSelectedListener() {
								
								@Override
								public void onItemSelected(final BaseGoods goods) {
									GameView.getInstance().popScreen(); // pop goods list
									CombatUI.this.mScreenStack.pop(); // pop misc menu
									if (goods.effectAll()) {
										onActionSelected(new ActionUseItemAll(mPlayerList.get(mCurPlayerIndex),
												mMonsterList, goods));
									} else { // 选一个角色治疗
										CombatUI.this.mScreenStack.push(new MenuCharacterSelect(mTargetIndicator, sPlayerIndicatorPos, mPlayerList,
												new OnCharacterSelectedListener() {
													
													@Override
													public void onCharacterSelected(FightingCharacter fc) {
														onActionSelected(new ActionUseItemOne(mPlayerList.get(mCurPlayerIndex),
																fc, goods));
													}
												}, false));
									}
								}
							}, Mode.Use));
					break;
				}
			} else if (key == Global.KEY_CANCEL) {
				CombatUI.this.mScreenStack.pop();
			}
		}
		
		/** 当前物品链表中，可用物品*/
		private List<BaseGoods> getUseableGoodsList() {
			List<BaseGoods> rlt = new LinkedList<BaseGoods>();
			List<BaseGoods> goodsList = Player.sGoodsList.getGoodsList();
			for (BaseGoods g : goodsList) {
				switch (g.getType()) {
				case 9:
				case 10:
				case 11:
				case 12:
					rlt.add(g);
					break;
				}
			}
			return rlt;
		}

		/** 当前物品链表中，可用于投掷敌人的物品*/
		private List<BaseGoods> getThrowableGoodsList() {
			List<BaseGoods> rlt = new LinkedList<BaseGoods>();
			// 可投掷物品
			List<BaseGoods> goodsList = Player.sGoodsList.getGoodsList();
			for (BaseGoods g : goodsList) {
				if (g.getType() == 8) {
					rlt.add(g);
				}
			}
			// 可投掷武器
			List<BaseGoods> weaponList = Player.sGoodsList.getEquipList();
			for (BaseGoods g : weaponList) {
				if (g.getType() == 7) {
					rlt.add(g);
				}
			}
			return rlt;
		}

		private void equipSelected(final BaseGoods goods) {
			final ArrayList<Player> list = new ArrayList<Player>();
			for (int i = 0; i < mPlayerList.size(); i++) {
				Player p = mPlayerList.get(i);
				if (goods.canPlayerUse(p.getIndex())) {
					list.add(p);
				}
			}
			if (list.size() == 0) { // 没人能装备
				Util.showMessage("不能装备!", 1000);
			} else if (list.size() == 1) { // 一个人能装备
				if (list.get(0).hasEquipt(goods.getType(), goods.getIndex())) {
					Util.showMessage("已装备!", 1000);
				} else {
					GameView.getInstance().pushScreen(new ScreenChgEquipment(list.get(0), (GoodsEquipment)goods));
				}
			} else { // 多人可装备
				GameView.getInstance().pushScreen(new BaseScreen() {
					Bitmap bg = Util.getFrameBitmap(16 * 5 + 6, 6 + 16 *
							list.size());
					int curSel = 0;
					byte[][] itemsText;
					
					{
						itemsText = new byte[list.size()][11];
						for (int i = 0; i < itemsText.length; i++) {
							for (int j = 0; j < 10; j++) {
								itemsText[i][j] = (byte)' ';
							}
							try {
								byte[] tmp = list.get(i).getName().getBytes("GBK");
								System.arraycopy(tmp, 0, itemsText[i], 0, tmp.length);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					}
					
					@Override
					public void update(long delta) {
					}
					
					@Override
					public void onKeyUp(int key) {
						if (key == Global.KEY_ENTER) {
							if (list.get(curSel).hasEquipt(goods.getType(), goods.getIndex())) {
								Util.showMessage("已装备!", 1000);
							} else {
								GameView.getInstance().popScreen();
								GameView.getInstance().pushScreen(new ScreenChgEquipment(list.get(curSel), (GoodsEquipment)goods));
							}
						} else if (key == Global.KEY_CANCEL) {
							GameView.getInstance().popScreen();
						}
					}
					
					@Override
					public void onKeyDown(int key) {
						if (key == Global.KEY_DOWN) {
							++curSel;
							curSel %= itemsText.length;
						} else if (key == Global.KEY_UP) {
							--curSel;
							curSel = (curSel + itemsText.length) % itemsText.length;
						}
					}
					
					@Override
					public void draw(Canvas canvas) {
						canvas.drawBitmap(bg, 50, 14, null);
						for (int i = 0; i < itemsText.length; i++) {
							if (i != curSel) {
								TextRender.drawText(canvas, itemsText[i], 50 + 3, 14 + 3 + 16 * i);
							} else {
								TextRender.drawSelText(canvas, itemsText[i], 50 + 3, 14 + 3 + 16 * i);
							}
						}
					}

				});
			}
		} // end of equipSelected
		
	}
	
}

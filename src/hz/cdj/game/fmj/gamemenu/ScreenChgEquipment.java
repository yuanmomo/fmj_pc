package hz.cdj.game.fmj.gamemenu;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.goods.GoodsEquipment;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.views.BaseScreen;
import android.graphics.Canvas;

public class ScreenChgEquipment extends BaseScreen {
	
	private Player mActor;
	
	private GoodsEquipment[] mGoods;
	private int mSelIndex;
	
	private int mPage = 0;
	
	/**
	 * 
	 * @param actorList 可装备选择的物品的角色链表
	 * @param goods 选择的物品
	 */
	public ScreenChgEquipment(Player actor, GoodsEquipment goods) {
		mActor = actor;
		if (actor.hasSpace(goods.getType())) {
			mGoods = new GoodsEquipment[1];
			mGoods[0] = goods;
			mSelIndex = 0;
		} else {
			mGoods = new GoodsEquipment[2];
			mGoods[0] = actor.getCurrentEquipment(goods.getType());
			mGoods[1] = goods;
			mSelIndex = 1;
			// 没有空间，脱掉当前装备的
			actor.takeOff(goods.getType());
		}
		actor.putOn(goods);
	}

	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Global.COLOR_WHITE);
		if (mActor != null) {
			mActor.drawState(canvas, mPage);
			mActor.drawHead(canvas, 5, 60);
		}
		for (int i = 0; i < mGoods.length; i++) {
			if (mGoods[i] != null) {
				mGoods[i].draw(canvas, 8, 2 + 32 * i);
			}
		}
		Util.drawTriangleCursor(canvas, 1, 10 + 32 * mSelIndex);
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP && mSelIndex > 0) {
			mActor.takeOff(mGoods[mSelIndex].getType());
			--mSelIndex;
			mActor.putOn(mGoods[mSelIndex]);
		} else if (key == Global.KEY_DOWN && mSelIndex < mGoods.length - 1) {
			mActor.takeOff(mGoods[mSelIndex].getType());
			++mSelIndex;
			mActor.putOn(mGoods[mSelIndex]);
		} else if (key == Global.KEY_PAGEDOWN || key == Global.KEY_PAGEUP) {
			mPage = 1 - mPage;
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_CANCEL) {
			// 换上原来的装备
			mActor.takeOff(mGoods[0].getType());
			if (mGoods.length > 1) {
				mActor.putOn(mGoods[0]);
			}
			GameView.getInstance().popScreen();
		} else if (key == Global.KEY_ENTER) {
			if (mSelIndex == mGoods.length - 1) { // 换了新装备
				// 物品链中删除该装备
				Player.sGoodsList.deleteGoods(mGoods[mGoods.length - 1].getType(),
						mGoods[mGoods.length - 1].getIndex());
				// 物品链中加入老装备
				if (mGoods.length > 1) {
					Player.sGoodsList.addGoods(mGoods[0].getType(), mGoods[0].getIndex());
				}
			}
			GameView.getInstance().popScreen();
		}
	}

}

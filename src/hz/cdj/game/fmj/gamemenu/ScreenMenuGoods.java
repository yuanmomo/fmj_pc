package hz.cdj.game.fmj.gamemenu;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList.Mode;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList.OnItemSelectedListener;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.goods.GoodsEquipment;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.scene.ScreenMainGame;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ScreenMenuGoods extends BaseScreen implements OnItemSelectedListener {

	private Bitmap mFrameBmp = Util.getFrameBitmap(77 - 39 + 1, 77 - 39 + 1);
	private String[] strs = {"使用", "装备"};
	private int mSelId = 0;
	
	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(mFrameBmp, 39, 39, null);
		if (mSelId == 0) {
			TextRender.drawSelText(canvas, strs[0], 39 + 3, 39 + 3);
			TextRender.drawText(canvas, strs[1], 39 + 3, 39 + 3 + 16);
		} else if (mSelId == 1) {
			TextRender.drawText(canvas, strs[0], 39 + 3, 39 + 3);
			TextRender.drawSelText(canvas, strs[1], 39 + 3, 39 + 3 + 16);
		}
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP || key == Global.KEY_DOWN) {
			mSelId = 1 - mSelId;
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_CANCEL) {
			GameView.getInstance().popScreen();
		} else if (key == Global.KEY_ENTER) {
			GameView.getInstance().popScreen();
			GameView.getInstance().pushScreen(new ScreenGoodsList(mSelId == 0 ?
					Player.sGoodsList.getGoodsList() : Player.sGoodsList.getEquipList(), this, Mode.Use));
		}
	}

	@Override
	public boolean isPopup() {
		return true;
	}

	@Override
	public void onItemSelected(BaseGoods goods) {
		if (mSelId == 0) { // 使用
			goodsSelected(goods);
		} else if (mSelId == 1) { // 装备
			equipSelected(goods);
		}
	}
	
	private void goodsSelected(BaseGoods goods) {
		switch (goods.getType()) {
		case 8: // 暗器
		case 12: // 兴奋剂
			Util.showMessage("战斗中才能使用!", 1000);
			break;
			
		case 13: // 土遁
			// TODO 迷宫中的用法，调用脚本
			ScreenMainGame.instance.triggerEvent(255);
			while (!(GameView.getInstance().getCurScreen() instanceof ScreenMainGame)) {
				GameView.getInstance().popScreen();
			}
			break;
			
		case 14: // 剧情类
			// TODO 剧情类物品用法
			Util.showMessage("当前无法使用!", 1000);
			break;
			
		case 9: // 药物
		case 10: // 灵药
		case 11: // 仙药
			GameView.getInstance().pushScreen(new ScreenTakeMedicine(goods));
			break;
		}
	}
	
	private void equipSelected(final BaseGoods goods) {
		final ArrayList<Player> list = new ArrayList<Player>();
		for (int i = 0; i < ScreenMainGame.sPlayerList.size(); i++) {
			Player p = ScreenMainGame.sPlayerList.get(i);
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
					if (key == Global.KEY_DOWN && curSel < itemsText.length - 1) {
						++curSel;
					} else if (key == Global.KEY_UP && curSel > 0) {
						--curSel;
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

				@Override
				public boolean isPopup() {
					return true;
				}
			});
		}
	}
}

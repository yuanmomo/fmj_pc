package hz.cdj.game.fmj.script;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList;
import hz.cdj.game.fmj.gamemenu.ScreenGoodsList.Mode;
import hz.cdj.game.fmj.goods.BaseGoods;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.scene.ScreenMainGame;
import hz.cdj.game.fmj.views.BaseScreen;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class OperateBuy extends Operate implements ScreenGoodsList.OnItemSelectedListener {
	
	byte[] data;
	int start;
	private LinkedList<BaseGoods> goodsList;
	
	public OperateBuy(byte[] data, int start) {
		this.data = data;
		this.start = start;
		goodsList = new LinkedList<BaseGoods>();
	}

	@Override
	public boolean process() {
		goodsList.clear();
		int i = start;
		while (data[i] != 0) {
			BaseGoods g = Player.sGoodsList.getGoods(
					(int)data[i + 1] & 0xff,
					(int)data[i] & 0xff);
			
			if (g == null) {
				g = (BaseGoods)DatLib.GetRes(DatLib.RES_GRS,
						(int)data[i + 1] & 0xff,
						(int)data[i] & 0xff);
				g.setGoodsNum(0);
			}
			
			goodsList.add(g);
			i += 2;
		}
		GameView.getInstance().pushScreen(new ScreenGoodsList(goodsList, this, Mode.Buy));
		return true;
	}

	@Override
	public boolean update(long delta) {
		return false;
	}

	@Override
	public void draw(Canvas canvas) {
		ScreenMainGame.instance.drawScene(canvas);
	}

	@Override
	public void onKeyDown(int key) {
	}

	@Override
	public void onKeyUp(int key) {
	}

	@Override
	public void onItemSelected(BaseGoods goods) {
		if (Player.sMoney < goods.getBuyPrice()) {
			Util.showMessage("金钱不足!", 1000);
		} else {
			mBuyScreen.init(goods);
			GameView.getInstance().pushScreen(mBuyScreen);
		}
	}
	
	private BuyGoodsScreen mBuyScreen = new BuyGoodsScreen();
	
	private static class BuyGoodsScreen extends BaseScreen {
		
		private static Bitmap bmpBg = Util.getFrameBitmap(136, 55);
		private BaseGoods goods;
		private int buyCnt;
		private int money;

		public void init(BaseGoods goods) {
			this.goods = goods;
			buyCnt = 0;
			money = Player.sMoney;
		}
		
		@Override
		public void update(long delta) {
		}
		
		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(bmpBg, 12, 21, null);
			TextRender.drawText(canvas, "金钱：" + money, 15, 24);
			TextRender.drawText(canvas, goods.getName(), 15, 40);
			TextRender.drawText(canvas, ": " + goods.getGoodsNum(), 93, 40);
			TextRender.drawText(canvas, "买入个数　：" + buyCnt, 15, 56);
		}
		
		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_ENTER) {
				Player.sMoney = money;
				if (buyCnt == goods.getGoodsNum() && buyCnt > 0) {
					Player.sGoodsList.addGoods(goods.getType(), goods.getIndex(), buyCnt);
				}
				GameView.getInstance().popScreen();
			} else if (key == Global.KEY_CANCEL) {
				goods.addGoodsNum(-buyCnt);
				GameView.getInstance().popScreen();
			}
		}
		
		@Override
		public void onKeyDown(int key) {
			if (key == Global.KEY_UP && goods.getGoodsNum() < 99) {
				if (money >= goods.getBuyPrice()) {
					++buyCnt;
					goods.addGoodsNum(1);
					money -= goods.getBuyPrice();
				} else {
					Util.showMessage("金钱不足!", 1000);
				}
			} else if (key == Global.KEY_DOWN && buyCnt > 0) {
				--buyCnt;
				goods.addGoodsNum(-1);
				money += goods.getBuyPrice();
			}
		}

		@Override
		public boolean isPopup() {
			return true;
		}
	}

}

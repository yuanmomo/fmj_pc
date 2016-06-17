package hz.cdj.game.fmj.gamemenu;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.characters.Player;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.magic.BaseMagic;
import hz.cdj.game.fmj.magic.MagicAttack;
import hz.cdj.game.fmj.magic.MagicRestore;
import hz.cdj.game.fmj.magic.ScreenMagic;
import hz.cdj.game.fmj.scene.ScreenMainGame;
import hz.cdj.game.fmj.views.BaseScreen;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ScreenGameMainMenu extends BaseScreen {

	private Bitmap bmpFrame1 = Util.getFrameBitmap(93, 16 + 6);
	private Bitmap bmpFrame2 = Util.getFrameBitmap(32 + 6, 64 + 6);
	private Rect menuItemsRect = new Rect();
	private byte[] menuItems;
	private String[] menuItemsS = { "属性", "魔法", "物品", "系统" };

	private int mSelIndex = 0;

	public ScreenGameMainMenu() {
		try {
			menuItems = "属性魔法物品系统".getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			menuItems = new byte[0];
		}
		menuItemsRect.left = 9 + 3;
		menuItemsRect.top = 3 + 16 + 6 - 1 + 3;
		menuItemsRect.right = menuItemsRect.left + 32;
		menuItemsRect.bottom = menuItemsRect.top + 64;
	}

	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bmpFrame1, 9, 3, null);
		TextRender.drawText(canvas, "金钱:" + Player.sMoney, 9 + 3, 3 + 3);
		canvas.drawBitmap(bmpFrame2, 9, 3 + 16 + 6 - 1, null);
		TextRender.drawText(canvas, menuItems, 0, menuItemsRect);
		TextRender.drawSelText(canvas, menuItemsS[mSelIndex], menuItemsRect.left,
				menuItemsRect.top + mSelIndex * 16);
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP) {
			if (--mSelIndex < 0) {
				mSelIndex = 3;
			}
		} else if (key == Global.KEY_DOWN) {
			if (++mSelIndex > 3) {
				mSelIndex = 0;
			}
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_ENTER) {
			BaseScreen screen = null;
			switch (mSelIndex) {
			case 0:
				screen = new ScreenMenuProperties();
				break;
			case 1:
				screen = ScreenMainGame.instance.getPlayerList().size() > 1 ?
						screenSelectActor : getScreenMagic(0);
				break;
			case 2:
				screen = new ScreenMenuGoods();
				break;
			case 3:
				screen = new ScreenMenuSystem();
				break;
			}
			if (screen != null) {
				GameView.getInstance().pushScreen(screen);
			}
		} else if (key == Global.KEY_CANCEL) {
			GameView.getInstance().popScreen();
		}
	}

	/**
	 * 
	 * @param id 0 1 2
	 * @return
	 */
	private ScreenMagic getScreenMagic(final int id) {
		return new ScreenMagic(ScreenMainGame.instance.getPlayerList().get(id).getMagicChain(),
				new ScreenMagic.OnItemSelectedListener() {

					@Override
					public void onItemSelected(BaseMagic magic) {
						if (magic instanceof MagicRestore) {
							GameView.getInstance().pushScreen(new ScreenUseMagic((MagicRestore)magic,
									ScreenMainGame.instance.getPlayerList().get(id)));
						} else {
							Util.showMessage("此处无法使用!", 1000);
						}
					}
				});
	}

	@Override
	public boolean isPopup() {
		return true;
	}
	
	BaseScreen screenSelectActor = new BaseScreen() {
		
		private int mIndex = 0;
		
		private Rect mFrameRect = new Rect(39, 29, 125, 67 - 32 + ScreenMainGame.instance.getPlayerList().size() * 16);
		
		private Bitmap bmpFrame = Util.getFrameBitmap(mFrameRect.width(), mFrameRect.height());
		
		private String[] mNames;
		
		private int mSum;
		
		{
			List<Player> list = ScreenMainGame.instance.getPlayerList();
			mSum = list.size();
			mNames = new String[mSum];
			for (int i = 0; i < mSum; i++) {
				mNames[i] = format(list.get(i).getName());
			}
		}

		private String format(String s) {
			try {
				while (s.getBytes("GBK").length < 10) s += " ";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return s;
		}
		
		@Override
		public void update(long delta) {
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(this.bmpFrame, this.mFrameRect.left, this.mFrameRect.top, null);
			for (int i = 0; i < mSum; i++) {
				if (i == mIndex) {
					TextRender.drawSelText(canvas, mNames[i], this.mFrameRect.left + 3,
							this.mFrameRect.top + 3 + 16 * i);
				} else {
					TextRender.drawText(canvas, mNames[i], this.mFrameRect.left + 3,
							this.mFrameRect.top + 3 + 16 * i);
				}
			}
		}

		@Override
		public void onKeyDown(int key) {
			if (key == Global.KEY_DOWN) {
				++mIndex;
				if (mIndex >= mSum) {
					mIndex = 0;
				}
			} else if (key == Global.KEY_UP) {
				--mIndex;
				if (mIndex < 0) {
					mIndex = mSum - 1;
				}
			}
		}
		
		@Override
		public void onKeyUp(int key) {
			if (key == Global.KEY_CANCEL) {
				GameView.getInstance().popScreen();
			} else if (key == Global.KEY_ENTER) {
				GameView.getInstance().popScreen();
				GameView.getInstance().pushScreen(getScreenMagic(mIndex));
			}
		}
		
		@Override
		public boolean isPopup() {
			return true;
		}
		
	};

}

package hz.cdj.game.fmj.views;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.lib.ResImage;
import hz.cdj.game.fmj.scene.SaveLoadGame;
import hz.cdj.game.fmj.script.ScriptResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.graphics.Canvas;

public class ScreenSaveLoadGame extends BaseScreen {
	
	public enum Operate {
		SAVE,	// 保存进度
		LOAD	// 读取进度
	}
	
	private int[][] mTextPos = {
			{68, 28},
			{68, 51},
			{68, 74}
	};
	private int mIndex = 0;
	private final String mEmpty = "空档案    ";
	private String[] mText = {mEmpty, mEmpty, mEmpty};
	private ArrayList<ArrayList<ResImage>> mHeadImgs = new ArrayList<ArrayList<ResImage>>();
	
	private final String[] mFileNames = {"fmjsave0", "fmjsave1", "fmjsave2"};
	
	private Operate mOperate;
	
	private ResImage mImgBg;	// 背景图片
	
	public ScreenSaveLoadGame(Operate opt) {
		mOperate = opt;
		mImgBg = (ResImage)DatLib.getInstance().getRes(DatLib.RES_PIC, 2,
				opt == Operate.LOAD ? 16 : 15);
		mHeadImgs.add(new ArrayList<ResImage>());
		mHeadImgs.add(new ArrayList<ResImage>());
		mHeadImgs.add(new ArrayList<ResImage>());
		File file =new File("./assets/"+mFileNames[0]);//GameActivity.instance.getFileStreamPath(mFileNames[0]);
		
		if (file.exists()) {
			mText[0] = format(getSceneNameAndHeads(file, mHeadImgs.get(0)));
		}
		file = new File("./assets/"+mFileNames[1]);//GameActivity.instance.getFileStreamPath(mFileNames[1]);
		if (file.exists()) {
			mText[1] = format(getSceneNameAndHeads(file, mHeadImgs.get(1)));
		}
		file = new File("./assets/"+mFileNames[2]);//GameActivity.instance.getFileStreamPath(mFileNames[2]);
		if (file.exists()) {
			mText[2] = format(getSceneNameAndHeads(file, mHeadImgs.get(2)));
		}
	}
	
	private String format(String s) {
		try {
			while (s.getBytes("GBK").length < mEmpty.getBytes("GBK").length) s += " ";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private String getSceneNameAndHeads(File f, ArrayList<ResImage> heads) {
		String name = "";
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			name = (String)in.readObject();
			int actorNum = in.readInt();
			for (int i = 0; i < actorNum; i++) {
				heads.add((ResImage)DatLib.GetRes(DatLib.RES_PIC, 1, in.readInt()));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public void update(long delta) {
	}

	@Override
	public void draw(Canvas canvas) {
		mImgBg.draw(canvas, 1, 0, 0);
		for (int i = 0; i < mHeadImgs.size(); i++) {
			for (int j = 0; j < mHeadImgs.get(i).size(); j++) {
				ResImage img = mHeadImgs.get(i).get(j);
				if (img != null) {
					img.draw(canvas, 7, 8 + 20 * j, mTextPos[i][1] - 6);
				}
			}
		}
		TextRender.drawText(canvas, mText[0], mTextPos[0][0], mTextPos[0][1]);
		TextRender.drawText(canvas, mText[1], mTextPos[1][0], mTextPos[1][1]);
		TextRender.drawText(canvas, mText[2], mTextPos[2][0], mTextPos[2][1]);
		TextRender.drawSelText(canvas, mText[mIndex], mTextPos[mIndex][0], mTextPos[mIndex][1]);
	}

	@Override
	public void onKeyDown(int key) {
		if (key == Global.KEY_UP) {
			if (--mIndex < 0) {
				mIndex = 2;
			}
		} else if (key == Global.KEY_DOWN) {
			if (++mIndex > 2) {
				mIndex = 0;
			}
		}
	}

	@Override
	public void onKeyUp(int key) {
		if (key == Global.KEY_CANCEL) {
			GameView.getInstance().popScreen();
		} else if (key == Global.KEY_ENTER) {
			final File file = new File("./assets/"+mFileNames[mIndex]);//GameActivity.instance.getFileStreamPath(mFileNames[mIndex]);
			if (mOperate == Operate.LOAD) { // 加载存档
				if (!file.exists()) {
					return;
				}
				if (loadGame(file)) { // 读档成功
					SaveLoadGame.startNewGame = false;
					GameView.getInstance().changeScreen(Global.SCREEN_MAIN_GAME);
				} else { // 读档失败
					SaveLoadGame.startNewGame = true;
					GameView.getInstance().changeScreen(Global.SCREEN_MENU);
				}
			} else { // 保存存档
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					saveGame(file);
					GameView.getInstance().popScreen();
					GameView.getInstance().popScreen();
					GameView.getInstance().popScreen();
				} else { // 询问是否覆盖存档
					GameView.getInstance().pushScreen(new ScreenMessageBox("覆盖原进度?",
							new ScreenMessageBox.OnOKClickListener() {
								
								@Override
								public void onOKClick() {
									saveGame(file);
									GameView.getInstance().popScreen();
									GameView.getInstance().popScreen();
									GameView.getInstance().popScreen();
								}
							}));
				}
			}
		}
	}
	
	public boolean loadGame(File file) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			SaveLoadGame.read(in);
			ScriptResources.read(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			//MobclickAgent.reportError(GameActivity.instance, e);
			//Toast.makeText(GameActivity.instance, "-_-。sorry！读档出错了。", Toast.LENGTH_SHORT).show();
			System.out.println("-_-。sorry！读档出错了。");
			return false;
		}
		return true;
	}
	
	public boolean saveGame(File file) {
		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file));
			SaveLoadGame.write(o);
			ScriptResources.write(o);
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
			//MobclickAgent.reportError(GameActivity.instance, e);
			//Toast.makeText(GameActivity.instance, "存档出错了!", Toast.LENGTH_SHORT).show();
			System.out.println("存档出错了!");
			return false;
		}
		return true;
	}

}

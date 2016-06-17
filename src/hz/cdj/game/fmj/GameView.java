package hz.cdj.game.fmj;

import android.graphics.Canvas;
import hz.cdj.game.fmj.graphics.TextRender;
import hz.cdj.game.fmj.graphics.Util;
import hz.cdj.game.fmj.lib.DatLib;
import hz.cdj.game.fmj.scene.ScreenMainGame;
import hz.cdj.game.fmj.script.ScriptProcess;
import hz.cdj.game.fmj.views.BaseScreen;
import hz.cdj.game.fmj.views.ScreenAnimation;
import hz.cdj.game.fmj.views.ScreenMenu;
import hz.cdj.game.fmj.views.ScreenSaveLoadGame;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Stack;

public class GameView extends JFrame implements Runnable{
	private static final long serialVersionUID = 4865220132498519554L;


	private static GameView instance;
	private Stack<BaseScreen> mScreenStack;
	
	public Panel panel;
	
	
	Canvas canvas;
	
	/**
	 * 控制逻辑线程执行
	 */
	private  boolean mKeepRunning = true;

	public GameView() {
		
		panel=new Panel();
		this.add(panel);//绘制类
		
		addL();//键盘监听
		
		canvas=new Canvas();
		instance = this;
	}
	
	public static void main(String[] args) throws IOException {
		
		//游戏窗口
		GameView gameView	=	new GameView();
		gameView.setTitle("伏魔记--PC.java版");
		gameView.setLocation(400, 200);
		gameView.setSize(Global.SCREEN_WIDTH*Global.Scale, Global.SCREEN_HEIGHT *Global.Scale);
		gameView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameView.setVisible(true);
		gameView.setResizable(false);
		gameView.setAlwaysOnTop(true);
				
		//加载游戏
		initRes();
		gameView.mScreenStack = new Stack<BaseScreen>();
		gameView.mScreenStack.push(new ScreenAnimation(247));
		
		gameView.mKeepRunning = true;
		new Thread(gameView, "logic update").start();
		
		
		
		//gameView.mKeepRunning  = false;
	}

	public synchronized static void initRes() throws IOException {
		DatLib.init();
		TextRender.init();
		Util.init();
		ScriptProcess.init();
    }
	
	

	public static GameView getInstance() {
		return instance;
	}
	
	public void changeScreen(int screenCode) {
		BaseScreen tmp = null;
		switch (screenCode) {
		case Global.SCREEN_DEV_LOGO:
			tmp = new ScreenAnimation(247);
			break;
			
		case Global.SCREEN_GAME_LOGO:
			tmp = new ScreenAnimation(248);
			break;
			
		case Global.SCREEN_MENU:
			tmp = new ScreenMenu();
			break;
			
		case Global.SCREEN_MAIN_GAME:
			tmp = new ScreenMainGame();
			break;
			
		case Global.SCREEN_GAME_FAIL:
			tmp = new ScreenAnimation(249);
			break;
			
		case Global.SCREEN_SAVE_GAME:
			tmp = new ScreenSaveLoadGame(ScreenSaveLoadGame.Operate.SAVE);
			break;
			
		case Global.SCREEN_LOAD_GAME:
			tmp = new ScreenSaveLoadGame(ScreenSaveLoadGame.Operate.LOAD);
			break;
		}
		if (tmp != null) {
			mScreenStack.clear();
			mScreenStack.push(tmp);
		}
		System.gc();
	}

	public void pushScreen(BaseScreen screen) {
		mScreenStack.push(screen);
	}
	
	public void popScreen() {
		mScreenStack.pop();
	}
	
	public BaseScreen getCurScreen() {
		return mScreenStack.peek();
	}

	@Override
	public void run() {
		long curTime = System.currentTimeMillis();
		long lastTime = curTime;
		while (mKeepRunning) {
 			synchronized (mScreenStack) {
 				curTime = System.currentTimeMillis();
 				mScreenStack.peek().update(curTime - lastTime);
 				lastTime = curTime;
				
				ListIterator<BaseScreen> iter = mScreenStack.listIterator(mScreenStack.size());
				// 找到第一个全屏窗口
				while (iter.hasPrevious()) {
					BaseScreen tmp = iter.previous();
					if (!tmp.isPopup()) {
						break;
					}
				}
				
				// 刷新
				Canvas canvas =GameView.instance.canvas;
				
				if (canvas != null) {
					while (iter.hasNext()) {
						iter.next().draw(canvas);
					}
					GameView.getInstance().panel.repaint();
				}
			}
			try {
				Thread.sleep(Global.TIME_GAMELOOP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void keyDown(int key) {
		
		
		synchronized (mScreenStack) {
			mScreenStack.peek().onKeyDown(key);
		}
	}
	
	public void keyUp(int key) {
		synchronized (mScreenStack) {
			mScreenStack.peek().onKeyUp(key);
		}
	}
	
	public void addL() {
		this.addKeyListener(new KeyListener() {//键盘监听
			
			
			
			
			
			
			@Override
			public void keyTyped(KeyEvent e) {
				//System.out.println("1"+e.getKeyChar());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int key = c(e.getKeyCode());
				synchronized (mScreenStack) {
					mScreenStack.peek().onKeyUp(key);
				}
			}
			
			
			
			
			@Override
			public void keyPressed(KeyEvent e) {
				int key = c(e.getKeyCode());
				synchronized (mScreenStack) {
					mScreenStack.peek().onKeyDown(key);
				}
			}
			
			int c(int c){
				int key = -1;
				switch (c) {
				case 65:
					key = Global.KEY_LEFT;
					break;
				case 68:
					key = Global.KEY_RIGHT;
					break;
				case 87:
					key = Global.KEY_UP;
					break;
				case 83:
					key = Global.KEY_DOWN;
					break;
				case 72:
					key = Global.KEY_PAGEDOWN;
					break;
				case 76:
					key = Global.KEY_PAGEUP;
					break;
				case 74:
					key = Global.KEY_ENTER;
					break;
				case 75:
					key = Global.KEY_CANCEL;
					break;
				}
				return key;
			}
		});
	}
	
	
	
	

}

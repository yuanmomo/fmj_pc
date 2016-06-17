package hz.cdj.game.fmj;

import java.awt.Color;



public class Global {
	public static int COLOR_WHITE = new Color(204, 199, 237, 255).getRGB();//Color.argb(255, 199, 237, 204);
	public static int COLOR_BLACK = new Color(0, 0, 0, 255).getRGB();//Color.argb(255, 0, 0, 0);
	public static int COLOR_TRANSP = new Color(0, 0, 0, 0).getRGB();//Color.argb(0, 0, 0, 0);

	public static final int Scale=3;//游戏放大缩小倍数
	public static final int SCREEN_WIDTH = 160;
	public static final int SCREEN_HEIGHT = 106;
	
	
	public static final int MAP_LEFT_OFFSET = 8;
	
	public static final int KEY_UP = 1;
	public static final int KEY_DOWN = 2;
	public static final int KEY_LEFT = 3;
	public static final int KEY_RIGHT = 4;
	public static final int KEY_PAGEUP = 5;
	public static final int KEY_PAGEDOWN = 6;
	public static final int KEY_ENTER = 7;
	public static final int KEY_CANCEL = 8;
	
	public static final long TIME_GAMELOOP = 45;
	
	public static final int SCREEN_DEV_LOGO = 1;
	public static final int SCREEN_GAME_LOGO = 2;
	public static final int SCREEN_MENU = 3;
	public static final int SCREEN_MAIN_GAME = 4;
	public static final int SCREEN_GAME_FAIL = 5;
	public static final int SCREEN_SAVE_GAME = 6;
	public static final int SCREEN_LOAD_GAME = 7;
}

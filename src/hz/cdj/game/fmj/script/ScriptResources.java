package hz.cdj.game.fmj.script;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ScriptResources {

	/**
	 * 全局事件标志1-2400
	 */
	public static boolean[] globalEvents = new boolean[2401];
	
	/**
	 * 全局变量0-199,局部变量200-239
	 */
	public static int[] variables = new int[240];
	
	/**
	 * 初始化局部变量
	 */
	public static void initLocalVar() {
		for (int i = 200; i < 240; i++) {
			variables[i] = 0;
		}
	}
	
	/**
	 * 初始化全局变量
	 */
	public static void initGlobalVar() {
		for (int i = 0; i < 200; i++) {
			variables[i] = 0;
		}
	}
	
	/**
	 * 初始化全局事件
	 */
	public static void initGlobalEvents() {
		for (int i = 1; i <= 2400; i++) {
			globalEvents[i] = false;
		}
	}
	
	/**
	 * 将全局事件num标志设置为true
	 * @param num 1-2400
	 */
	public static void setEvent(int num) {
		globalEvents[num] = true;
	}
	
	/**
	 * 将全局事件num标志设置为false
	 */
	public static void clearEvent(int num) {
		globalEvents[num] = false;
	}
	
	public static void write(ObjectOutputStream out) throws IOException {
		// 写全局事件
		for (int i = 1; i <= 2400; ++i) {
			out.writeBoolean(globalEvents[i]);
		}
		
		// 写全局变量&局部变量
		for (int i = 0; i < 240; ++i) {
			out.writeInt(variables[i]);
		}
	}
	
	public static void read(ObjectInputStream in) throws IOException {
		// 读全局事件
		for (int i = 1; i <= 2400; ++i) {
			globalEvents[i] = in.readBoolean();
		}
		
		// 读全局变量&局部变量
		for (int i = 0; i < 240; ++i) {
			variables[i] = in.read();
		}
	}
}

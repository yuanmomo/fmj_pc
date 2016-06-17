

package android.graphics;

import hz.cdj.game.fmj.GameView;
import hz.cdj.game.fmj.Global;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;




public class Canvas extends java.awt.Canvas{ 
	
		public Bitmap background;
	
	    private static final long serialVersionUID = 2862094636075150979L;
		public Canvas(Bitmap b){
			this.background=b;
	    }
		public Canvas() {
			this.background=new Bitmap( Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT , BufferedImage.TYPE_4BYTE_ABGR);
		}
		
		
		
		public void drawBitmap( Bitmap bitmap, float left, float top,  Paint paint) {
			
			Graphics g=background.getGraphics();
			
			if(paint!=null){
				g.setColor(new Color(paint.cOLOR_BLACK));
			}else{
				g.setColor(Color.WHITE);
			}
			g.drawImage(bitmap,(int)left, (int) top, null);
			
			GameView.getInstance().panel.setBackground(background);
			
		}
		public void drawColor(int color) {
		   
			
		    Graphics g=background.getGraphics();
			g.setColor(new Color(color));
			g.fillRect(0, 0, background.getWidth(),background.getHeight()); 
			GameView.getInstance().panel.setBackground(background);
			
		}
		
		
		
		public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
		    
			Graphics g=background.getGraphics();
			
			g.setColor(new Color(paint.cOLOR_BLACK));
			
			g.drawLine((int)startX, (int)startY, (int)stopX, (int)stopY);
			GameView.getInstance().panel.setBackground(background);
			
		}
		
		private void drawR(int x, int y, int i, int j,Paint sBlackPaint,Graphics g) {
			if(sBlackPaint.style==Paint.Style.FILL){
				g.fillRect(x, y, i, j);
			}else if(sBlackPaint.style==Paint.Style.STROKE){
				g.drawRect(x, y, i, j);
			}else {
				g.fillRect(x, y, i, j);
			}
			
			
		}
		public void drawRect(int x, int y, int i, int j, Paint sBlackPaint) {
			
			Graphics g=background.getGraphics();
			g.setColor(new Color(sBlackPaint.cOLOR_BLACK));
			
			drawR(x, y, i-x, j-y, sBlackPaint, g);
			GameView.getInstance().panel.setBackground(background);
			
			
		}
		public void drawRect(Rect mRectTop, Paint mFramePaint) {
			
			Graphics g=background.getGraphics();
			g.setColor(new Color(mFramePaint.cOLOR_BLACK));
			
			drawR(mRectTop.left, mRectTop.top, (mRectTop.right-mRectTop.left), (mRectTop.bottom-mRectTop.top), mFramePaint, g);
			
			
			GameView.getInstance().panel.setBackground(background);
		}
		public void drawRect(RectF rWithPic, Paint paint) {
			
			Graphics g=background.getGraphics();
			g.setColor(new Color(paint.cOLOR_BLACK));
			
			drawR((int)rWithPic.left, (int)rWithPic.top, (int)(rWithPic.right-rWithPic.left),(int) (rWithPic.bottom-rWithPic.top), paint, g);
			
			GameView.getInstance().panel.setBackground(background);
		}
		public void drawLines(float[] pts, Paint sBlackPaint) {
			
			 Graphics g=background.getGraphics();
			 g.setColor(new Color(sBlackPaint.cOLOR_BLACK));
			 
			 int size=pts.length/4;
			 for(int i=0;i<size;i++){
				 g.drawLine((int)pts[i*4], (int)pts[(i*4)+1], (int)pts[(i*4)+2], (int)pts[((i*4))+3]);
			 }
			 
			 GameView.getInstance().panel.setBackground(background);
		}
		public void setBitmap(Bitmap bmpArrowDown) {
			background=bmpArrowDown;
		}
		
		public void scale(float mScale, float mScale2) {
			//System.out.println("未实现的方法————scale(float mScale, float mScale2)");
			
		}
}

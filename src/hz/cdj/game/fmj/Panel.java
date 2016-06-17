package hz.cdj.game.fmj;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Panel extends JPanel{

	private static final long serialVersionUID = -104477368799466779L;
	
	
	
	AffineTransform transform = new AffineTransform();
	AffineTransformOp ato;
	public Panel(){
		transform.setToScale(Global.Scale,Global.Scale); //实现
		ato = new AffineTransformOp(transform,null);
		
	}
	
	public BufferedImage background;
	public BufferedImage dest;
	
	public void setBackground(BufferedImage background) {
		this.background = background;
	}


	public void paint(Graphics g){
		super.paint(g);
		if(background!=null){
			if(dest==null){
				dest=new BufferedImage(Global.SCREEN_WIDTH*Global.Scale, Global.SCREEN_HEIGHT *Global.Scale, BufferedImage.TYPE_4BYTE_ABGR);
			}
			
			
			ato.filter(background,dest);
			g.drawImage(dest,0,0,null);
		}
	}
}

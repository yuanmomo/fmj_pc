package android.graphics;


public class Paint {

	
	
	
	
	  public enum Style {
	        /**
	         * Geometry and text drawn with this style will be filled, ignoring all
	         * stroke-related settings in the paint.
	         */
	        FILL            (0),
	        /**
	         * Geometry and text drawn with this style will be stroked, respecting
	         * the stroke-related fields on the paint.
	         */
	        STROKE          (1),
	        /**
	         * Geometry and text drawn with this style will be both filled and
	         * stroked at the same time, respecting the stroke-related fields on
	         * the paint. This mode can give unexpected results if the geometry
	         * is oriented counter-clockwise. This restriction does not apply to
	         * either FILL or STROKE.
	         */
	        FILL_AND_STROKE (2);

	        Style(int nativeInt) {
	            this.nativeInt = nativeInt;
	        }
	        final int nativeInt;
	  }

	public  Style style;
	public  int cOLOR_BLACK;
	public void setColor(int cOLOR_BLACK) {
		this.cOLOR_BLACK=cOLOR_BLACK;
	}

	public void setStyle(Style fillAndStroke) {
		this.style=fillAndStroke;
	}

	public void setStrokeWidth(int i) {
		
	}
}

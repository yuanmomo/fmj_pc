package android.graphics;


public class Bitmap  extends java.awt.image.BufferedImage{

	
	
	
	public Bitmap(int width, int height, int imageType) {
		super(width, height, imageType);
		
		
	}
	public static  Bitmap createBitmap(int width, int height, Config c){
		int imageType=TYPE_4BYTE_ABGR ;
		if(c==Config.ARGB_8888){
			imageType=TYPE_4BYTE_ABGR;
		}
		return new Bitmap(width, height, imageType);
	}
	public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
		super.setRGB(x, y, width, height, pixels, offset, stride);
	}
	public static Bitmap createBitmap(int[] tmp, int mWidth, int mHeight,Config argb8888) {
		Bitmap b=new Bitmap(mWidth, mHeight, TYPE_4BYTE_ABGR);
		b.setRGB(0, 0, mWidth, mHeight, tmp, 0, mWidth);
		return b;
	}
	public Bitmap copy(Config argb8888, boolean b) {
		
		Bitmap b1=new Bitmap(this.getWidth(), this.getHeight(), TYPE_4BYTE_ABGR);
		
		int[] tmp=getRGB(0, 0, this.getWidth(),  this.getHeight(), null, 0, this.getWidth());
		b1.setRGB(0, 0, this.getWidth(),  this.getHeight(),tmp, 0,  this.getWidth());
		return b1;
	}
	public enum Config {
        // these native values must match up with the enum in SkBitmap.h

        /**
         * Each pixel is stored as a single translucency (alpha) channel.
         * This is very useful to efficiently store masks for instance.
         * No color information is stored.
         * With this configuration, each pixel requires 1 byte of memory.
         */
        ALPHA_8     (1),

        /**
         * Each pixel is stored on 2 bytes and only the RGB channels are
         * encoded: red is stored with 5 bits of precision (32 possible
         * values), green is stored with 6 bits of precision (64 possible
         * values) and blue is stored with 5 bits of precision.
         * 
         * This configuration can produce slight visual artifacts depending
         * on the configuration of the source. For instance, without
         * dithering, the result might show a greenish tint. To get better
         * results dithering should be applied.
         * 
         * This configuration may be useful when using opaque bitmaps
         * that do not require high color fidelity.
         */
        RGB_565     (3),

        /**
         * Each pixel is stored on 2 bytes. The three RGB color channels
         * and the alpha channel (translucency) are stored with a 4 bits
         * precision (16 possible values.)
         * 
         * This configuration is mostly useful if the application needs
         * to store translucency information but also needs to save
         * memory.
         * 
         * It is recommended to use {@link #ARGB_8888} instead of this
         * configuration.
         *
         * Note: as of {@link android.os.Build.VERSION_CODES#KITKAT},
         * any bitmap created with this configuration will be created
         * using {@link #ARGB_8888} instead.
         * 
         * @deprecated Because of the poor quality of this configuration,
         *             it is advised to use {@link #ARGB_8888} instead.
         */
        @Deprecated
        ARGB_4444   (4),

        /**
         * Each pixel is stored on 4 bytes. Each channel (RGB and alpha
         * for translucency) is stored with 8 bits of precision (256
         * possible values.)
         * 
         * This configuration is very flexible and offers the best
         * quality. It should be used whenever possible.
         */
        ARGB_8888   (5);

        final int nativeInt;

        @SuppressWarnings({"deprecation"})
        private static Config sConfigs[] = {
            null, ALPHA_8, null, RGB_565, ARGB_4444, ARGB_8888
        };
        
        Config(int ni) {
            this.nativeInt = ni;
        }

        static Config nativeToConfig(int ni) {
            return sConfigs[ni];
        }
    }
	
}

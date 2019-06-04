package de.jClipCorn.test;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.helper.SimpleFileUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestCoverPreview extends ClipCornBaseTest {

	@Test
	public void testColorQuantizersBigImage() throws Exception {
		for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
		{
			File filep = new File(SimpleFileUtils.getSystemTempFile("png"));
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep);

			quant.analyze(imgOrig, 16);
			BufferedImage imgNewBig = quant.quantize(imgOrig);
			BufferedImage imgNewSmall1 = ColorQuantizerConverter.shrink(quant.quantize(imgOrig), 24);
			BufferedImage imgNewSmall2 = quant.quantize(ColorQuantizerConverter.shrink(imgOrig, 24));

			assertEquals(imgOrig.getWidth(),  imgNewBig.getWidth());
			assertEquals(imgOrig.getHeight(), imgNewBig.getHeight());

			assertEquals(24, imgNewSmall1.getWidth());
			assertEquals(24, imgNewSmall2.getWidth());

			assertEquals(imgNewSmall1.getWidth(),  imgNewSmall2.getWidth());
			assertEquals(imgNewSmall1.getHeight(), imgNewSmall2.getHeight());

			filep.delete();
		}
	}

	@Test
	public void testColorQuantizersSerialize1() throws Exception {
		for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
		{
			File filep = new File(SimpleFileUtils.getSystemTempFile("png"));
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep);

			filep.delete();

			quant.analyze(imgOrig, 16);

			BufferedImage imgNewSmall = quant.quantize(ColorQuantizerConverter.shrink(imgOrig, 24));

			byte[] b1 = ColorQuantizerConverter.quantizeTo4BitRaw(quant, imgNewSmall);
			BufferedImage b2 = ColorQuantizerConverter.quantizeFrom4BitRaw(b1);
			byte[] b3 = ColorQuantizerConverter.quantizeTo4BitRaw(quant, b2);

			assertArrayEquals(b1, b3);
			assertImageEquals(imgNewSmall, b2);
		}
	}

	@Test
	public void testColorQuantizersSerialize2() throws Exception {
		for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
		{
			File filep = new File(SimpleFileUtils.getSystemTempFile("png"));
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep);
			imgOrig = ColorQuantizerConverter.shrink(imgOrig, 24);
			assertEquals(24, imgOrig.getWidth());

			filep.delete();

			quant.analyze(imgOrig, 16);

			BufferedImage imgNewSmall = quant.quantize(imgOrig);

			byte[] b1 = ColorQuantizerConverter.quantizeTo4BitRaw(quant, imgNewSmall);
			BufferedImage b2 = ColorQuantizerConverter.quantizeFrom4BitRaw(b1);
			byte[] b3 = ColorQuantizerConverter.quantizeTo4BitRaw(quant, b2);

			assertArrayEquals(b1, b3);
			assertImageEquals(imgNewSmall, b2);
		}
	}
}

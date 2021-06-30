package de.jClipCorn.test;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestCoverPreview extends ClipCornBaseTest {

	@Test
	public void testColorQuantizersBigImage() throws Exception {
		for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
		{
			var filep = SimpleFileUtils.getSystemTempFile("png");
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep.toFile());

			quant.analyze(imgOrig, 16);
			BufferedImage imgNewBig = quant.quantize(imgOrig);
			BufferedImage imgNewSmall1 = ColorQuantizerConverter.shrink(quant.quantize(imgOrig), ColorQuantizerConverter.PREVIEW_WIDTH);
			BufferedImage imgNewSmall2 = quant.quantize(ColorQuantizerConverter.shrink(imgOrig, ColorQuantizerConverter.PREVIEW_WIDTH));

			assertEquals(imgOrig.getWidth(),  imgNewBig.getWidth());
			assertEquals(imgOrig.getHeight(), imgNewBig.getHeight());

			assertEquals(ColorQuantizerConverter.PREVIEW_WIDTH, imgNewSmall1.getWidth());
			assertEquals(ColorQuantizerConverter.PREVIEW_WIDTH, imgNewSmall2.getWidth());

			assertEquals(imgNewSmall1.getWidth(),  imgNewSmall2.getWidth());
			assertEquals(imgNewSmall1.getHeight(), imgNewSmall2.getHeight());

			filep.deleteWithException();
		}
	}

	@Test
	public void testColorQuantizersSerialize1() throws Exception {
		for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
		{
			var filep = SimpleFileUtils.getSystemTempFile("png");
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep.toFile());

			filep.deleteWithException();

			quant.analyze(imgOrig, 16);

			BufferedImage imgNewSmall = quant.quantize(ColorQuantizerConverter.shrink(imgOrig, ColorQuantizerConverter.PREVIEW_WIDTH));

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
			var filep = SimpleFileUtils.getSystemTempFile("png");
			SimpleFileUtils.writeRawResource(filep, "/cover_example_small.png", ClipCornBaseTest.class);

			ColorQuantizer quant = m.create();

			BufferedImage imgOrig = ImageIO.read(filep.toFile());
			imgOrig = ColorQuantizerConverter.shrink(imgOrig, ColorQuantizerConverter.PREVIEW_WIDTH);
			assertEquals(ColorQuantizerConverter.PREVIEW_WIDTH, imgOrig.getWidth());

			filep.deleteWithException();

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

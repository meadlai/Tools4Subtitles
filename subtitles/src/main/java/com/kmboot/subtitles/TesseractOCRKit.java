package com.kmboot.subtitles;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;

import java.io.File;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class TesseractOCRKit {
	private static TesseractOCRKit instance = null;
	private static TessBaseAPI api = new TessBaseAPI();
	private static PIX image;
	private static BytePointer outText;

	private TesseractOCRKit() {
	}

	private void init() {
		String dataPath = this.getClass().getClassLoader().getResource("").getFile();
		System.out.println("p = " + dataPath);
		File data = new File(dataPath);
		if (data.exists() == false) {
			System.err.println("Could not initialize tesseract.");
			return;
		}
		System.out.println("p = " + data.getAbsolutePath());

		if (api.Init(data.getAbsolutePath(), "eng") != 0) {
			System.err.println("Could not initialize tesseract.");
			// throw new RuntimeException("Could not initialize tesseract");
		}
	}

	public static TesseractOCRKit getInstance() {
		if (instance == null) {
			instance = new TesseractOCRKit();
			instance.init();
		}
		return instance;
	}

	public String recognize(String imagePath) {
		image = pixRead(imagePath);
		api.SetImage(image);
		// Get OCR result
		outText = api.GetUTF8Text();
		String result = outText.getString();
		System.out.println("OCR output:\n" + result);

		outText.deallocate();
		pixDestroy(image);
		return result;
	}

	public void clear() {
		if(outText.isNull() == false) {
			outText.deallocate();
		}
		if(image.isNull() == false) {
			pixDestroy(image);
		}
	}

	public void release() {
		api.End();
		api.close();
	}
}

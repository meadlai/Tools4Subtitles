package com.kmboot.subtitles;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;

import java.io.File;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class TesseractExample {
	public static void main(String[] args) {

		TesseractOCRKit ocr = TesseractOCRKit.getInstance();
		String result = ocr.recognize(new File("target/screenshot_672.png").getAbsolutePath());
		ocr.clear();
		ocr.release();

	}
}
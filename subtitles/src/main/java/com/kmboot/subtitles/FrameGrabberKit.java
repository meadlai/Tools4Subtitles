package com.kmboot.subtitles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

public abstract class FrameGrabberKit {

	public static void main(String[] args) throws Exception {

//        randomGrabberFFmpegImage("e:/ffmpeg/aa.mp4", "./target", "screenshot", 5);

		grapImage("/Users/meadlai/Downloads/baidu_synch/百度云同步盘/english/BrainPop/JR/1_science/02_plants/01_forests.mov",
				"./target", "screenshot", (p) -> {
					String r = TesseractOCRKit.getInstance().recognize(p);
				});

	}

	public static void grapImage(String filePath, String targerFilePath, String targetFileName, Consumer<String> f)
			throws Exception {
		FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(filePath);
		ff.start();

		int ffLength = ff.getLengthInFrames();
		System.out.println("total length = " + ffLength);
		Frame frame;
		int i = 0;
		while (i < ffLength) {
			frame = ff.grabImage();
			if (i % 48 == 0) {
				doExecuteFrame(frame, targerFilePath, targetFileName, i, f);
			}
			i++;
		}
		ff.stop();
	}

	public static void doExecuteFrame(Frame frame, String targerFilePath, String targetFileName, int index,
			Consumer<String> f) {
		if (null == frame || null == frame.image) {
			return;
		}

		Java2DFrameConverter converter = new Java2DFrameConverter();

		String imageMat = "png";
		String FileName = targerFilePath + File.separator + targetFileName + "_" + index + "." + imageMat;
		BufferedImage bi = converter.getBufferedImage(frame);
		// cut the subtitle
		int yPoint = bi.getHeight() / 5 - 10;
		bi = bi.getSubimage(0, bi.getHeight() - yPoint, bi.getWidth(), yPoint);
		File output = new File(FileName);
		if (output.exists()) {
			output.delete();
		}
		try {
			ImageIO.write(bi, imageMat, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		f.accept(FileName);
//		TesseractOCRKit.getInstance().recognize(FileName);
	}

	public static List<Integer> random(int baseNum, int length) {
		List<Integer> list = new ArrayList<>(length);
		while (list.size() < length) {
			Integer next = (int) (Math.random() * baseNum);
			if (list.contains(next)) {
				continue;
			}
			list.add(next);
		}
		Collections.sort(list);
		return list;
	}
}
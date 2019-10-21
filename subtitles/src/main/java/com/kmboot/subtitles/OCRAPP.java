package com.kmboot.subtitles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

/**
 * Hello world!
 *
 */
public class OCRAPP {
	public static final String PATH = "/Users/meadlai/Desktop/image/";

	public static void main(String[] args) throws Exception {
		try (Stream<Path> walk = Files.walk(Paths.get(PATH))) {
			List<String> images = walk.filter(f -> f.toString().endsWith("png")).map(x -> x.toString())
					.collect(Collectors.toList());
			images.forEach(p -> {
				System.out.println(p);
				String r = TesseractOCRKit.getInstance().recognize(p);
				saveTxt(p, r);
			});
		}

		TesseractOCRKit.getInstance().clear();
		TesseractOCRKit.getInstance().release();

	}

	private static void saveTxt(String fname, String txt) {
		File file = new File(fname);
		String txtName = file.getParentFile().getAbsolutePath() + File.separator
				+ file.getName().replace(".png", ".txt");
		System.out.println("txtName = " + txt);
		File txtFile = new File(txtName);
		if (txtFile.exists()) {
			txtFile.delete();
		} else {
			try {
				txtFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.writeStringToFile(txtFile, txt, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

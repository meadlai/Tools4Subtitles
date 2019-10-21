/**
 * 
 */
package com.kmboot.subtitles;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.bytedeco.javacv.FrameGrabber.Exception;

/**
 * @author meadlai
 *
 */
public class MainApp {
	public static final String PATH = "/Users/meadlai/Downloads/baidu_synch/百度云同步盘/english/BrainPop/JR/1_science/01_animals/";
	private static Map<String, String> mv2Folder = new HashMap<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try (Stream<Path> walk = Files.walk(Paths.get(PATH))) {
			List<String> movies = walk.filter(f -> f.toString().endsWith("mov")).map(x -> x.toString())
					.collect(Collectors.toList());
			movies.forEach(f -> {
				File file = new File(f);
				String folderName = file.getParentFile().getAbsolutePath() + File.separator
						+ file.getName().replace(".mov", "");
				File folder = new File(folderName);
				if (folder.exists() == false) {
					folder.mkdirs();
				}
				mv2Folder.put(f, folderName);
				System.out.println(f + ":" + folderName);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String mv : mv2Folder.keySet()) {
			Set<String> set = new LinkedHashSet<>();
			Set<String> dup = new LinkedHashSet<>();
			try {
				FrameGrabberKit.grapImage(mv, mv2Folder.get(mv), "sb", (p) -> {
					String r = TesseractOCRKit.getInstance().recognize(p);
					String d = r.replaceAll(" ", "");
					if (dup.contains(d)) {
						return;
					} else {
						dup.add(d);
					}
					if (set.contains(r) == false) {
						set.add(r);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			//
			saveTxt(mv, set);
			//
			FileUtils.deleteQuietly(new File(mv2Folder.get(mv)));
		}

		TesseractOCRKit.getInstance().clear();
		TesseractOCRKit.getInstance().release();

	}

	private static void saveTxt(String fname, Set<String> set) {
		File file = new File(fname);
		String txtName = file.getParentFile().getAbsolutePath() + File.separator
				+ file.getName().replace(".mov", ".txt");
		String txt = set.stream().collect(Collectors.joining(""));
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

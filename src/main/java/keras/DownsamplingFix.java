package keras;

import java.io.File;
import java.util.ArrayList;

import ij.ImageJ;
import ij.io.FileSaver;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import util.ImgLib2Util;

public class DownsamplingFix {
	// fixes the intensity problem that happens because of the down-sampling
	
	// path - path to the folder with the images
	// suffix - prefix of the files (*dots?)
	public static ArrayList<File> getImagesList(String path, String suffix) {
		ArrayList<File> imgPaths = new ArrayList<>();

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(suffix)) {
				System.out.println(listOfFiles[i].getName());
				imgPaths.add(listOfFiles[i]);
			}
		}
		return imgPaths;
	}

	public static void fixLabels(String path, String folder, String suffix) {
		ArrayList<File> imgPaths = getImagesList(path + folder, suffix);

		for (File imgPath : imgPaths) {
			System.out.println(imgPath);
			Img<FloatType> img = ImgLib2Util.openAs32Bit(imgPath);
			processImage(img, path, imgPath.getName());
		}
	}

	public static void processImage(Img<FloatType> img, String path, String name) {
		long[] dimensions = new long[img.numDimensions()];
		img.dimensions(dimensions);

		Cursor<FloatType> cursor = img.cursor();

		while (cursor.hasNext()) {
			cursor.fwd();

			long [] position = new long [img.numDimensions()];
			cursor.localize(position);
			
			// check if the pixel corresponds to the manual annotation
			if (cursor.get().get() > 0) {
				cursor.get().set(255);
			}
		}
		
		// ImageJFunctions.show(patches);
		saveImage(img, path, name);
	}
	
	public static void saveImage(Img<FloatType> img, String path, String name) {
		String fullPath = path + "patches/" + name.substring(0, 3) + "dots.tif";	
		System.out.println(fullPath);
		
		new FileSaver(ImageJFunctions.wrap(img, "").duplicate()).saveAsTiffStack(fullPath);
	}
	
	public static void checkLabelsCount(String path, String folder1, String folder2, String suffix) {
		ArrayList<File> imgPaths = getImagesList(path + folder1, suffix);
		
		for (File imgPath : imgPaths) {
			
			String path1 = path + folder1 + "/" + imgPath.getName();
			String path2 = path + folder2 + "/" + imgPath.getName();
			
			System.out.println(path1);
			System.out.println(path2);
			Img<FloatType> img1 = ImgLib2Util.openAs32Bit(new File(path1));
			Img<FloatType> img2 = ImgLib2Util.openAs32Bit(new File(path2));
			
			long count1 = getCount(img1);
			long count2 = getCount(img2);
			
			System.out.println(count1 + " ?= " + count2);
		}	
	}
	
	
	public static long getCount(Img<FloatType> img) {
		long res = 0;
		
		Cursor<FloatType> cursor = img.cursor();
		
		while (cursor.hasNext()) {
			cursor.fwd();
			// check if the pixel corresponds to the manual annotation
			if (cursor.get().get() == 255)
				res++;
		}
		
		return res;
	}
	
	
	public static void main(String[] args) {
		new ImageJ();
		String folder = "labels-downsampled";
		String path = "/media/milkyklim/013B-7BC5/Klim/";

		// fixLabels(path, folder, "dots.tif");
		checkLabelsCount(path, folder, "patches", "dots.tif");
		
		
		System.out.println("Doge!");
	} 
}

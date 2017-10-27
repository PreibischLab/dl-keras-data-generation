package keras;

import java.io.File;
import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import ij.ImageJ;
import ij.io.FileSaver;
import util.ImgLib2Util;

public class LabelGeneration {
	// class for generating labels in 3D

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

	public static void generatePatches(String path, String folder, String suffix) {
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

		long patchSize = 32;

		for (int d = 0; d < img.numDimensions(); d++)
			dimensions[d] += patchSize;

		Img<FloatType> patches = img.factory().create(dimensions, new FloatType());

		Cursor<FloatType> cursor = img.cursor();

		while (cursor.hasNext()) {
			cursor.fwd();

			long [] position = new long [img.numDimensions()];
			cursor.localize(position);
			
			// check if the pixel corresponds to the manual annotation
			if (cursor.get().get() > 0) {

				long[] min = new long[img.numDimensions()];
				long[] max = new long[img.numDimensions()];

				for (int d = 0; d < img.numDimensions(); d++) {
					min[d] = position[d] - patchSize/2;
					max[d] = position[d] + patchSize/2 - 1;
				}
				
				long [] offset = new long [img.numDimensions()];
				for (int d  = 0 ; d < img.numDimensions(); d++)
					offset[d] = patchSize/2;
				
				Cursor<FloatType> cPathches = Views.interval(Views.offset(patches, offset), min, max).cursor();
				while(cPathches.hasNext()) {
					cPathches.fwd();
					cPathches.get().inc();
				}
			}
		}
		
		// ImageJFunctions.show(patches);
		saveImage(patches, path, name);
	}

	public static void saveImage(Img<FloatType> img, String path, String name) {
		String fullPath = path + "patches/" + name.substring(0, 3) + "patch.tif";	
		System.out.println(fullPath);
		
		new FileSaver(ImageJFunctions.wrap(img, "").duplicate()).saveAsTiffStack(fullPath);
	}

	public static void main(String args[]) {

		new ImageJ();
		String folder = "labels";
		String path = "/home/milkyklim/dl-cell-counting/algorithm/data/2017-10-16-dl-data-dapi/data/";

		generatePatches(path, folder, "dots.tif");
		System.out.println("Doge!");
	}
}

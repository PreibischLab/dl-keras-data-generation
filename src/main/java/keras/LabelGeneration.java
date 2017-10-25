package keras;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

import ij.io.FileSaver;
import util.ImgLib2Util;

public class LabelGeneration {
	// class for generating labels in 3D
	
	// path - path to the folder with the images
	// suffix - prefix of the files (*dots?)
	public static String[] getImagesList(String path, String suffix){
		String [] imgPaths = null;
		
		return imgPaths;
	} 
	
	
	public static void generateLabels(String path, String suffix){
		String [] imgPaths = getImagesList(path, suffix);
		
		for (String imgPath : imgPaths){
			Img<FloatType> img =  ImgLib2Util.openAs32Bit(new File(imgPath));
			// Img<FloatType> label = new ImageFactory<FloatType>.create(img, new FloatType);
			
			
			
		}	
	}
	
	
	public static void saveImage(String path, String folder){
		String fullPath = "";
		
		Img<FloatType> img =  ImgLib2Util.openAs32Bit(new File(fullPath + ".tif"));
		new FileSaver(ImageJFunctions.wrap(img, "").duplicate()).saveAsTiffStack(fullPath + "NEW" + ".tif");
	}
	
	
	public static void main(String args []){
		
	}
}

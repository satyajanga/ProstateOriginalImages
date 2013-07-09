package utilityFunctions;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageConverter;


public class DetectStraightNeedle {

	/**
	 * @param args
	 */
	double approximateTheta = 90;
	double approximateRho = 250;
	double thetaInc=0.25,rhoInc=0.25;
	int[][] houghArray; 
	int[] boundsOfRegion = new int[4];
	final Opener opener = new Opener();	
	double[] rhoRange = new double[2];
	double[] thetaRange = new double[2];
	HoughTransform ht = new HoughTransform();

	void findNeedle()
	{
		String filename = "/home/satya/Projects/NeedleDetectionNew/Original/ProstateImages_straight/image-38.jpg";
		ImagePlus inputImg =  opener.openImage(filename);
		//inputImg.show();
		boundsOfRegion[0] = 150;
		boundsOfRegion[1] = 400;
		boundsOfRegion[2] = 120;
		boundsOfRegion[3] = 450;
		thetaRange[0] = approximateTheta - 20;
		thetaRange[1] = approximateTheta + 20;
		rhoRange[0] = approximateRho - 30;
		rhoRange[1] = approximateRho + 30;
		ht.setRhoRange(rhoRange);
		ht.setThetaRange(thetaRange);
		ht.setRhoBinSize(rhoInc);
		ht.setThetaBinSize(thetaInc);
		ht.initialize();
		ht.printThis();
		drawBoundingBox((ImagePlus)inputImg.duplicate());
		//	inputImg.show();
		System.out.println(inputImg.getType() + " " + ImagePlus.GRAY8);
		processImage(inputImg);
		double[] lineParams = ht.getMaxLine();
		System.out.println("Line Parameters ::"+lineParams[0] +" " + lineParams[1]);
		drawLine(lineParams,(ImagePlus)inputImg.duplicate());
	}

	private void drawLine(double[] lineParams,ImagePlus img)
	{
		//243, 299
		double[] pos = new double[2];
		double l =100;
		pos[0] = 243;
		pos[1] = 299;
		ImageConverter converter = new ImageConverter(img);
		converter.convertToRGB();
		Image img2 = img.getImage();
		Graphics g = img2.getGraphics();
		Graphics2D g2D = (Graphics2D) g;

		double angleInRadians = (lineParams[1])*3.14/180;
		Color c = new Color((float)0, (float)0, (float)1);
		g2D.setColor(c);
		g2D.setStroke(new BasicStroke(1F)); // set stroke width of 10
		
		int x1 = (int) (pos[0] + l*Math.cos(angleInRadians));
		int y1 = (int) (pos[1] + l*Math.sin(angleInRadians));
			
		int x2 = (int) (pos[0] - l*Math.cos(angleInRadians));
		int y2 = (int) (pos[1] - l*Math.sin(angleInRadians));
		g2D.drawLine(x1, y1, x2, y2);
		g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10
		g2D.draw(new Line2D.Double(x2, y2,x2, y2));
		img.show();


	}

	private void drawBoundingBox(ImagePlus img) 
	{
		ImageConverter converter = new ImageConverter(img);
		converter.convertToRGB();
		Image img2 = img.getImage();
		Graphics g = img2.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		Color c = new Color((float)1, (float)0, (float)0);
		g2D.setColor(c);
		g2D.setStroke(new BasicStroke(1F));  // set stroke width of 10
		g2D.drawRect(boundsOfRegion[0], boundsOfRegion[2], boundsOfRegion[1]-boundsOfRegion[0], boundsOfRegion[3]-boundsOfRegion[2]);
		img.show();
	}

	void processImage(ImagePlus img)
	{
		img.getProcessor().threshold(60);

		img.updateAndDraw();

		IJ.save(img,"/home/satya/Projects/NeedleDetectionNew/Original/ProstateImages_straight/threshold.png");

		img.show();
		int b ;
		System.out.println( boundsOfRegion[0] + " "+ boundsOfRegion[1] + " " + boundsOfRegion[2] + " "+ boundsOfRegion[3]);

		for(int i=boundsOfRegion[0];i<boundsOfRegion[1];i++)
			for(int j=boundsOfRegion[2];j<boundsOfRegion[3];j++)
			{
				//b= img.getPixel(i, j)[0];//.get(i*height + j);
				b= img.getProcessor().get(i,j);
				if((b&0xFF) == 0)
				{
					ht.fillHoughBins(new Point(i,j));
				}
			}

	}

	public static void main(String[] args)
	{
		DetectStraightNeedle ds = new DetectStraightNeedle();
		ds.findNeedle();
	}

}

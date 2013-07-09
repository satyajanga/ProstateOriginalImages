package simulatedStraightNeedle;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.io.Opener;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.Vector;


public class CreateShape {

	/**
	 * @param args
	 */
	final Opener opener = new Opener();	

	Color c = new Color((float)0.07, (float)0.07, (float)0.07);

	private void createStraightNeedleImages(String filename)
	{
		ImagePlus	inputImg =  opener.openImage(filename);

		double[] lineParameters = getLineFromTwoPoints();

		filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/";
		ImagePlus needleImg,img;
		for(int i=1;i<200;i++)
		{
			img = inputImg.duplicate();
			needleImg = createStraightNeedle(img,i);

			ImagePlus comp = createComposition(img, needleImg);
			/*Overlay fuse = new Overlay();
			ImageRoi roi = new ImageRoi(0,0,needleImg.getBufferedImage());
			roi.setOpacity(0.8);
			fuse.add(roi);
			img.setOverlay(fuse);
			 */

		//	ImageConverter converter = new ImageConverter(comp);
			//converter.convertToRGB();
			IJ.save(comp,filename+i+".jpg");
		}

	}


	private ImagePlus createComposition(ImagePlus img, ImagePlus needleImg) 
	{
		int pixel1,pixel2,compositePixel;
		ImagePlus compositeImage = img.createImagePlus();
		int height,width;
		width = img.getWidth();
		height = img.getHeight();
		compositeImage.setProcessor(img.getProcessor().createProcessor(width, height));
		double randomOpacity;
		Random r = new Random();
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				pixel1 = img.getProcessor().getPixel(i,j);
				pixel2 = needleImg.getProcessor().getPixel(i, j);

				if(pixel2!=0)
				{
					compositePixel = pixel1;
				}
				else
				{ 
					randomOpacity = r.nextDouble();
					if(randomOpacity>0.4)
					{
						compositePixel =  (int)((0.2*pixel1)+(0.8*pixel2));
					}
					else
					{
						compositePixel = pixel1;
						//compositePixel = (int)((0.6*pixel1)+(0.4*pixel2));

					}
				}
				compositeImage.getProcessor().putPixel(i, j, compositePixel);
			}
		}

		compositeImage.getProcessor().smooth();
		//compositeImage.getProcessor().smooth();

		return compositeImage;

	}




	private ImagePlus createStraightNeedle(ImagePlus img, int l)
	{

		System.out.println(img.PROPERTIES);
		ImagePlus needleImg = (ImagePlus) img.createImagePlus();
		needleImg.setProcessor(img.getProcessor().createProcessor(img.getWidth(), img.getHeight()));
		System.out.println("Image Created " + needleImg.getWidth() + " " + needleImg.getHeight());
		needleImg.setTitle("Needle Image");



		double[] lineParams = getLineFromTwoPoints();
	
		needleImg.getProcessor().invert();

		needleImg.updateImage();
		
		Image img2 = needleImg.getImage();
		Graphics g = img2.getGraphics();

		Graphics2D g2D = (Graphics2D) g;   
		//	System.out.println(img.getBytesPerPixel());

		g2D.setColor(new Color(0,0,0));

		g2D.setStroke(new BasicStroke(5F));  // set stroke width of 1

		//g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10

		System.out.println("Line Parameters ::" + lineParams[0] +" "+ lineParams[1]+ " Angle: " + lineParams[2]);

		int x2 = (int) (lineParams[0] - l*Math.cos(lineParams[2]));
		int y2 = (int) (lineParams[1] - l*Math.sin(lineParams[2]));

		g2D.drawLine((int)lineParams[0], (int)lineParams[1], x2, y2);
		g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10
		g2D.drawLine(x2, y2, x2, y2);



		//		g2D.drawOval(x2,y2, 1, 1);
		//		g2D.fillOval(x2,y2, 1, 1);



		//needleImg.getProcessor().filter(0);

		//		needleImg.getProcessor().exp();

		//needleImg.getProcessor().filter(0);

		//needleImg.getProcessor().filter(0);
		//needleImg.getProcessor().filter(0);
		//needleImg.getProcessor().filter(0);
		//needleImg.getProcessor().filter(0);

		//needleImg.getProcessor().dilate();

		//needleImg.getProcessor().sharpen();

		//needleImg.getProcessor().filter(arg0)
		//needleImg.getProcessor().
		return needleImg;
	}



	private double[] getLineFromTwoPoints() {
		double[] lineParams = new double[3];

		lineParams[0] = 243; lineParams[1] = 417;
		//Prostate location 240 189


		double slope= (189.0-417.0)/(240.0-243.0);
		lineParams[2] = Math.atan(slope);

		System.out.println("Line Params ::" + lineParams[0] + " " + lineParams[1] + " " + lineParams[2]);
		return lineParams;
	}



	private void createCurvedNeedleImages(String filename)
	{

		ImagePlus	inputImg =  opener.openImage(filename);

		ImagePlus needleImg,img;

		filename = "/home/satya/Projects/NeedleDetection/ImagesToFischer/needle";
		for(int i=30;i<90;i++)
		{
			double[] cicleParameters = circleFromThreePoints();

			img = inputImg.duplicate();
			needleImg = createCurvedNeedle(img,i);

			//img.getProcessor().drawOval((int)cicleParameters[0], (int)cicleParameters[1], (int)(2*cicleParameters[2]), (int)(2*cicleParameters[2]));

			Overlay fuse = new Overlay();
			ImageRoi roi = new ImageRoi(0,0,needleImg.getBufferedImage());
			roi.setOpacity(0.8);
			fuse.add(roi);
			Image img2 = img.getImage();
			Graphics g = img2.getGraphics();
			Graphics2D g2D = (Graphics2D) g;   
			//	System.out.println(img.getBytesPerPixel());

			g2D.setColor(c);
			g2D.setStroke(new BasicStroke(3F));  // set stroke width of 10

			System.out.println("Circle Parameters ::" + cicleParameters[0] +" "+ cicleParameters[1]+ " Radius: " + cicleParameters[2]);
			cicleParameters[0] -= cicleParameters[2];
			cicleParameters[1] -= cicleParameters[2];
			//g2D.drawOval((int)cicleParameters[0], (int)cicleParameters[1], (int)(2*cicleParameters[2]), (int)(2*cicleParameters[2]));
			//		g2D.drawArc((int)cicleParameters[0], (int)cicleParameters[1], (int)(2*cicleParameters[2]), (int)(2*cicleParameters[2]),0,45);

			//g2D.drawArc(x, y, width, height, startAngle, arcAngle)
			//		g2D.drawArc((int)cicleParameters[0], (int)cicleParameters[1], (int)(2*cicleParameters[2]), (int)(2*cicleParameters[2]),30, 100);//((int)startingPoint[0], (int)startingPoint[1], (int)endPoint[0], (int)endPoint[1]);

			img.setOverlay(fuse);

			IJ.save(img,filename+i+".png");
			//img.show();

		}
	}


	private double[] circleFromThreePoints()
	{
		double[] point1 = new double[2];
		double[] point2 = new double[2];
		double[] point3 = new double[2];
		double m1,m2,b1,b2;
		double[] params = new double[3];

		double radius;

		point1[0] = 120; point1[1] = 145;
		point2[0] = 160; point2[1] = 181;
		point3[0] = 186; point3[1] = 217;


		m1 = (point2[1] - point1[1])/(point2[0] - point1[0]); m1 = (-1/m1);
		m2 = (point3[1] - point2[1])/(point3[0] - point2[0]); m2 = (-1/m2);

		b1 = point1[1] - point1[0]*m1;
		b2 = point2[1] - point2[0]*m2;

		params[0] = (b2-b1)/(m1-m2);
		params[1] = m1*params[0] + b1;

		radius = (params[0]-point1[0])*(params[0]-point1[0])  + (params[1]-point1[1])*(params[1]-point1[1]);

		radius = Math.sqrt(radius);
		System.out.println("Circle Parameters ::" + params[0] +" "+ params[1]+ " Radius: " + radius);
		params[2] = radius;
		return params; 
	}
	private ImagePlus createCurvedNeedle(ImagePlus img,int maxAngle)
	{

		ImagePlus needleImg = (ImagePlus) img.duplicate();
		System.out.println("Image Created " + needleImg.getWidth() + " " + needleImg.getHeight());
		needleImg.setTitle("Needle Image");




		double[] circleParameters = circleFromThreePoints();

		Image img2 = needleImg.getImage();
		Graphics g = img2.getGraphics();


		Graphics2D g2D = (Graphics2D) g;   
		//	System.out.println(img.getBytesPerPixel());

		g2D.setColor(c);
		g2D.setStroke(new BasicStroke(3F));  // set stroke width of 10

		System.out.println("Circle Parameters ::" + circleParameters[0] +" "+ circleParameters[1]+ " Radius: " + circleParameters[2]);

		int x;
		int y;
		double angle;

		Vector<Integer> xpoints = new Vector<Integer>();
		Vector<Integer> ypoints = new Vector<Integer>();


		for(int i=maxAngle;i>26;i--)
		{

			angle = (360-i)*(3.1428)/180;
			x = (int) (circleParameters[0] + circleParameters[2]*Math.cos(angle));
			y = (int) (circleParameters[1] + circleParameters[2]*Math.sin(angle));
			xpoints.add(x);
			ypoints.add(y);

		}

		for(int i=0;i<xpoints.size()-1;i++)
		{
			//	img.getProcessor().putPixel(xpoints.get(i),ypoints.get(i), 0);

			g2D.draw(new Line2D.Double(xpoints.get(i), ypoints.get(i),xpoints.get(i+1), ypoints.get(i+1)));
		}

		//		BasicStroke  s = new BasicStroke(10F);
		//		s.se
		g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10

		g2D.draw(new Line2D.Double(xpoints.get(0), ypoints.get(0),xpoints.get(0), ypoints.get(0)));

		g2D.drawOval(xpoints.get(0),ypoints.get(0), 1, 1);
		g2D.fillOval(xpoints.get(0),ypoints.get(0), 1, 1);



		needleImg.getProcessor().filter(0);

		//		needleImg.getProcessor().exp();

		needleImg.getProcessor().filter(0);


		//needleImg.getProcessor().dilate();

		needleImg.getProcessor().sharpen();

		return needleImg;
	}


	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

		CreateShape  s = new CreateShape();
		s.createStraightNeedleImages("/home/satya/Projects/NeedleDetectionNew/Simulated/templateImage.jpg");

	}

}

package simulatedStraightNeedle;

import utilityFunctions.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageConverter;

public class StraightNeedleDetection 
{
	public void trackStraightNeedle() throws IOException
	{
		final Opener opener = new Opener();	

		ImagePlus inputImg;
		StraightNeedleTracking needleTracking = new StraightNeedleTracking();
		double[] approxEntryPoint = new double[2];
		approxEntryPoint[0] = 243;
		approxEntryPoint[1] = 417;
		double[] actualParameters = lineFromTwoPoints();
		needleTracking.Initialize(actualParameters);
		double[] detectedParameters;


		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/";
		ImagePlus colorImage;
		String content = new String();
		content = "";


		File file = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/needleValues.txt");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		int[] bounds;
		for(int i=50;i<200;i++)
		{
			inputImg =  opener.openImage(	filename+Integer.toString((int) i)+".jpg");

			colorImage = (ImagePlus)inputImg.duplicate();
			ImageConverter converter = new ImageConverter(colorImage);
			converter.convertToRGB();			

			bounds = getBoundingBox(actualParameters, i, approxEntryPoint,inputImg);

			//actualParameters = needleTracking.getStateEstimate();
			detectedParameters = detectLine(inputImg, bounds, actualParameters);
			needleTracking.correctStateFromObservation(detectedParameters);

			System.out.println(actualParameters[0] +" " + detectedParameters[0] + " ; "+ actualParameters[1] +" "+ detectedParameters[1]);
			//System.out.println(inputImg.getType() + " " + ImagePlus.GRAY8 + " " + colorImage.getType());
			drawBoundingBox(bounds,colorImage, new Color(255,0,0));

			drawLine(actualParameters,approxEntryPoint,i,colorImage,new Color(255,0,0));

			drawLine(detectedParameters,getStartingPoint(detectedParameters, approxEntryPoint),i,colorImage,new Color(0,255,0));

			drawLine(needleTracking.getStateEstimate(),getStartingPoint(needleTracking.getStateEstimate(), approxEntryPoint),i,colorImage,new Color(0,0,255));

			for(int j=0;j<2;j++)
				content = content +"  " + actualParameters[j];
			for(int j=0;j<2;j++)
				content = content +"  " + detectedParameters[j];

			detectedParameters = needleTracking.getStateEstimate();
			for(int j=0;j<2;j++)
				content = content +"  " + detectedParameters[j];
			content = content+"\n";
			//	colorImage.show();
			IJ.save(colorImage,"/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraightDetected/"+Integer.toString((int) i)+".jpg");

		}



		bw.write(content);
		bw.flush();
		bw.close();


	}
	private double[] detectLine(ImagePlus img,int[] bounds,double[] approxParams)
	{
		double rhoInc =0.25;
		double thetaInc = 0.25;
		HoughTransform ht = new HoughTransform();

		double[] thetaRange = new double[2];

		double[] rhoRange = new double[2];

		thetaRange[0] = approxParams[1] - 20;
		thetaRange[1] = approxParams[1] + 20;
		rhoRange[0] = approxParams[0] - 30;
		rhoRange[1] = approxParams[0] + 30;
		ht.setRhoRange(rhoRange);
		ht.setThetaRange(thetaRange);
		ht.setRhoBinSize(rhoInc);
		ht.setThetaBinSize(thetaInc);
		ht.initialize();
		img.getProcessor().threshold(60);

		img.updateAndDraw();

		//IJ.save(img,"/home/satya/Projects/NeedleDetectionNew/Original/ProstateImages_straight/threshold.png");

		//	img.show();
		int b ;
		int pixelCount=0;
		for(int i=bounds[0];i<bounds[1];i++)
			for(int j=bounds[2];j<bounds[3];j++)
			{
				b= img.getProcessor().get(i,j);
				if((b&0xFF) == 0)
				{
					ht.fillHoughBins(new Point(i,j));
					pixelCount++;
				}
			}
		System.out.println("Pixel Count::" + pixelCount);
		ht.printThis();

		return ht.getMaxLine();

	}
	private int[] getBoundingBox(double[] approxParams,double length, double[] approxEntryPoint,ImagePlus img)
	{

		int[] bounds = new int[4];
		bounds[0] = bounds[2] = 0;
		bounds[1] = img.getWidth(); bounds[3] = img.getHeight();

		double angleInRadians;// =(approxParams[1]*3.14/180);
		

		if(approxParams[1]>180)
		{
			bounds[3] = Math.min((int) (approxEntryPoint[1] + 10),bounds[3]);
			bounds[0] = Math.max((int) (approxEntryPoint[0] - 10),bounds[0]);
			angleInRadians =((approxParams[1]-270)*3.14/180);
			
			bounds[1] = Math.min((int)(approxEntryPoint[0]+10 + length*Math.cos(angleInRadians)),bounds[1]);
			bounds[2] = Math.max((int)(approxEntryPoint[1]-10 - length*Math.sin(angleInRadians)),bounds[2]);
		}
		else
		{
			angleInRadians =((approxParams[1]+90)*3.14/180);
			
			bounds[3] = Math.min((int) (approxEntryPoint[1] + 10),bounds[3]);
			bounds[1] = Math.min((int) (approxEntryPoint[0] + 10),bounds[1]);

			bounds[0] = Math.max((int)(approxEntryPoint[0] -10 - length*Math.cos(angleInRadians)),bounds[0]);
			bounds[2] = Math.max((int)(approxEntryPoint[1] -10 - length*Math.sin(angleInRadians)),bounds[2]);

			System.out.println("Inside Bounds Function ::" + bounds[0] + " "+ bounds[1] + " "+bounds[2]+" "+bounds[3]);
			//		171 188 241 222
			//183.0 217.0 48.896044442710334
		}

		return bounds;
	}

	private double[] getStartingPoint(double[] lineParams,double[] approxEntryPoint)
	{
		/*
		 * Find out the perpendicular distance and add it to X coordinate 
		 */
		double theta = lineParams[1];
		double r = lineParams[0];
		double[] newStartingPoint = new double[2];
		newStartingPoint[1] = approxEntryPoint[1];
		newStartingPoint[0] = approxEntryPoint[0];
		double dist= (-r+approxEntryPoint[0]*Math.cos((theta*Math.PI)/180)+approxEntryPoint[1]*Math.sin((theta*Math.PI)/180));

		dist = dist/Math.sqrt((Math.pow(r, 2)+1));
		newStartingPoint[0]+= dist;
		/*
		double theta = lineParams[1];
		double r = lineParams[0];
		double m1 = -1/Math.tan((theta*Math.PI)/180);
		double b1 = r/Math.sin((theta*Math.PI)/180);

		double m2 = -1/m1;

		double b2 = approxEntryPoint[1]-(approxEntryPoint[0]*m2);

		double[] newStartingPoint = new double[2];
		newStartingPoint[0] = (int) ((b2-b1)/(m1-m2));
		newStartingPoint[1] = (int) (newStartingPoint[0]*m1 + b1);
		System.out.println(m1+" "+b1 +" " + m2+ " " + b2+" "+newStartingPoint[0]+" " + newStartingPoint[1]);
		 */
		return newStartingPoint;
	}
	private void drawLine(double[] lineParams,double[] approxEntryPoint,double length, ImagePlus img, Color c)
	{
		Image img2 = img.getImage();
		Graphics g = img2.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(c);
		double angleInRadians = (lineParams[1]-90)*(Math.PI/180);
		int x2 = (int) (approxEntryPoint[0] + length*Math.cos(angleInRadians));
		int y2 = (int) (approxEntryPoint[1] + length*Math.sin(angleInRadians));
		g2D.drawLine((int)approxEntryPoint[0], (int)approxEntryPoint[1], x2, y2);
		g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10
		g2D.draw(new Line2D.Double(x2, y2,x2, y2));
		g2D.draw(new Line2D.Double((int)approxEntryPoint[0], (int)approxEntryPoint[1],(int)approxEntryPoint[0], (int)approxEntryPoint[1]));

	}
	private void drawBoundingBox(int[] bounds, ImagePlus img, Color c)
	{	
		ImageConverter converter = new ImageConverter(img);
		converter.convertToRGB();
		Image img2 = img.getImage();
		Graphics g = img2.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(c);
		g2D.setStroke(new BasicStroke(1F));  // set stroke width of 10
		g2D.drawRect(bounds[0], bounds[2], bounds[1]-bounds[0], bounds[3]-bounds[2]);

		img.updateAndDraw();
	}
	private double[] lineFromTwoPoints()
	{
		double[] lineParams = new double[2];

		//lineParams[0] = 243; lineParams[1] = 417;
		//Prostate location 240 189
		//lineParams[0] = 243; lineParams[1] = 417;
				//Prostate location 240 189

		// r = xcost+ysint;
			//	double slope= (189.0-417.0)/(240.0-243.0);

				lineParams[1] = Math.atan((240.0-243.0)/(417-189.0))*(180.0/Math.PI);
		//
				if(lineParams[1] < 0)
					lineParams[1]+=360;

				//double b = (417 - slope*243);

				lineParams[0] = 243*Math.cos(lineParams[1]*Math.PI/180) + 417*Math.sin(lineParams[1]*Math.PI/180);
						//Math.abs(b/Math.sqrt(1+(slope*slope)));
				//lineParams[1] = lineParams[1] + 90;


/*		double slope= (189.0-417.0)/(240.0-243.0);

		lineParams[1] = (Math.atan(slope)*180/3.14);

		if(lineParams[1] < 0)
			lineParams[1]+=180;

		double b = (417 - slope*243);

		lineParams[0] = Math.abs(b/Math.sqrt(1+(slope*slope)));
		lineParams[1] = lineParams[1] + 90;

	*/	return lineParams;
	}


	public static void main(String[] args) throws IOException 
	{
		StraightNeedleDetection needleDetection = new StraightNeedleDetection();
		needleDetection.trackStraightNeedle();

	}

}

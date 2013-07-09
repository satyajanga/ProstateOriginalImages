package simulatedCurvedNeedle;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageConverter;

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
import java.util.Vector;

import simulatedStraightNeedle.StraightNeedleTracking;
import utilityFunctions.CircularHoughTransform;

public class CurvedNeedleDetection {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public void trackCurvedNeedle() throws IOException
	{
		final Opener opener = new Opener();	

		ImagePlus inputImg;
		CurvedNeedleTracking needleTracking = new CurvedNeedleTracking();
		int[] approxEntryPoint = new int[2];
		approxEntryPoint[0] = 261;
		approxEntryPoint[1] = 416;
		double[] actualParameters = circleFromThreePoints();
		needleTracking.Initialize(actualParameters);
		double[] detectedParameters;


		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/";
		ImagePlus colorImage;
		String content = new String();
		content = "";
		File file = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/needleValues.txt");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		int[] bounds;

		for(int i=152;i<270;i++)
		{
			inputImg =  opener.openImage(	filename+Integer.toString((int) i)+".png");

			colorImage = (ImagePlus)inputImg.duplicate();
			ImageConverter converter = new ImageConverter(inputImg);
			converter.convertToGray8();			

			bounds = calculateBounds(approxEntryPoint, actualParameters, i*Math.PI/180);
			//actualParameters = needleTracking.getStateEstimate();
			detectedParameters = detectCurvedNeedle(inputImg, actualParameters, bounds);
			
			needleTracking.correctStateFromObservation(detectedParameters);

			System.out.println(actualParameters[0] +" " + detectedParameters[0] + " ; "+ actualParameters[1] +" "+ detectedParameters[1]);
			System.out.println(inputImg.getType() + " " + ImagePlus.GRAY8 + " " + colorImage.getType());
			drawBoundingBox(bounds,colorImage, new Color(255,0,0));

			drawCircle(actualParameters,approxEntryPoint,i,colorImage,new Color(255,0,0));

			drawCircle(detectedParameters, approxEntryPoint, i, colorImage, new Color(0,255,0));//(detectedParameters,getStartingPoint(detectedParameters, approxEntryPoint),i,colorImage,new Color(0,255,0));

			

			System.out.println("Parameters ::" + actualParameters[0] +" => "+detectedParameters[0] + "\n"
					+actualParameters[1] +" => "+detectedParameters[1] +"\n "
					+actualParameters[2] +" => "+detectedParameters[2]);

			drawCircle(needleTracking.getStateEstimate(), approxEntryPoint,i,colorImage,new Color(0,0,255));

			for(int j=0;j<3;j++)
				content = content +"  " + actualParameters[j];
			for(int j=0;j<3;j++)
				content = content +"  " + detectedParameters[j];

			detectedParameters = needleTracking.getStateEstimate();
			for(int j=0;j<3;j++)
				content = content +"  " + detectedParameters[j];
			content = content+"\n";
			//	colorImage.show();
			IJ.save(colorImage,"/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurvedDetected/"+Integer.toString((int) i)+".jpg");


		}


		bw.write(content);
		bw.flush();
		bw.close();

	}

	

	private int[] calculateBounds(int[] approxEntryPoint,double[] circleParams,double angle)
	{
		int[] bounds = new int[4];
		bounds[1] = (int) (approxEntryPoint[0] +5);
		bounds[3] = (int) (approxEntryPoint[1] +5);
		if(angle < Math.PI)
		{
			bounds[0] = 0;
			bounds[2] = 0;
			bounds[0] = (int) ((bounds[0]>((circleParams[0]-20) + (circleParams[2]+30)*Math.cos(angle)))? bounds[0]:((circleParams[0]-20) + (circleParams[2]+30)*Math.cos(angle)));
			bounds[2] = (int) ((bounds[2]>((circleParams[1]-20) + (circleParams[2]-30)*Math.sin(angle)))? bounds[2]:((circleParams[1]-20) + (circleParams[2]-30)*Math.sin(angle)));
		}
		else
		{
			bounds[0] = Math.max((int)(circleParams[0]-20 - circleParams[2]-30),0);
			bounds[2] = Math.max((int)(circleParams[1]-20 - circleParams[2]-30),0);
			bounds[0] = (int) ((bounds[0]<((circleParams[0]-20) + (circleParams[2]+30)*Math.cos(angle)))? bounds[0]:((circleParams[0]-20) + (circleParams[2]+30)*Math.cos(angle)));
			bounds[2] = (int) ((bounds[2]>((circleParams[1]-20) + (circleParams[2]+30)*Math.sin(angle)))? bounds[2]:((circleParams[1]-20) + (circleParams[2]+30)*Math.sin(angle)));
		}

		return bounds;
	}

	public double[] detectCurvedNeedle(ImagePlus img,double[] approxParams, int[] bounds)
	{
		CircularHoughTransform cht = new CircularHoughTransform();

		cht.setCenterXBinSize(0.5);
		cht.setCenterYBinSize(0.5);
		cht.setRadiusBinSize(0.5);
		double[] radiusRange = new double[2];
		radiusRange[0] = approxParams[2]-30;
		radiusRange[1] = approxParams[2]+30;
		cht.setRadiusRange(radiusRange);

		double[] centerXRange = new double[2];
		centerXRange[0] = approxParams[0]-20;
		centerXRange[1] = approxParams[0]+20;
		cht.setCenterXRange(centerXRange);

		double[] centerYRange = new double[2];
		centerYRange[0] = approxParams[1]-20;
		centerYRange[1] = approxParams[1]+20;
		cht.setCenterYRange(centerYRange);

		cht.initialize();
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
					cht.fillHoughBins(new Point(i,j));
					pixelCount++;
				}
			}
		System.out.println("Pixel Count::" + pixelCount);
		//cht.printThis();
		return cht.getMaxCircleParams();

	}
	private double getStartingAngle(double[] circleParams,int[] approxEntryPoint)
	{

		double dist = 0;
		dist = dist + (approxEntryPoint[0]-circleParams[0])*(approxEntryPoint[0]-circleParams[0]);
		dist = dist + (approxEntryPoint[1]-circleParams[1])*(approxEntryPoint[1]-circleParams[1]);
		dist = Math.sqrt(dist);
		double[] newEntryPoint = new double[2];


		if(circleParams[2]>dist)
		{
			//newEntryPoint[0] = ((dist*center_x) - (r*entryPoint[0]))/(dist-r);
			//newEntryPoint[1] = ((dist*center_y) - (r*entryPoint[1]))/(dist-r);

			newEntryPoint[0] = ( (circleParams[2]*approxEntryPoint[0])-((circleParams[2]-dist)*circleParams[0]))/dist;
			newEntryPoint[1] = ( (circleParams[2]*approxEntryPoint[1])-((circleParams[2]-dist)*circleParams[1]))/dist;
		}
		else
		{
			newEntryPoint[0] = (circleParams[2]*approxEntryPoint[0] + (dist-circleParams[2])*circleParams[0] )/dist;
			newEntryPoint[1] = (circleParams[2]*approxEntryPoint[1] + (dist-circleParams[2])*circleParams[1] )/dist;

			//newEntryPoint[0] = ((dist-r)*center_x + r*entryPoint[0])/dist;
			//	newEntryPoint[1] = ((dist-r)*center_y + r*entryPoint[1])/dist;
		}


		double newStartingAngle = Math.acos((newEntryPoint[0] - circleParams[0])/circleParams[2]);

		//		newStartingAngle =newStartingAngle;
		return newStartingAngle*(180/Math.PI);
	}

	private void drawCircle(double[] circleParams,int[] approxEntryPoint,int maxAngle, ImagePlus img, Color c)
	{

		Image img2 = img.getImage();
		Graphics g = img2.getGraphics();
		Graphics2D g2D = (Graphics2D) g;   

		Vector<Integer> xpoints = new Vector<Integer>();
		Vector<Integer> ypoints = new Vector<Integer>();
		int x;
		int y;
		double angle;


		double startingAngle =  getStartingAngle(circleParams, approxEntryPoint);
//double maxAngle = startingAngle + ((length/circleParams[2])*(180.0/Math.PI));
		for(double i=maxAngle;i>=startingAngle;i--)
		{
			angle = i*(3.1428)/180;
			x = (int) (circleParams[0] + circleParams[2]*Math.cos(angle));
			y = (int) (circleParams[1] + circleParams[2]*Math.sin(angle));
			xpoints.add(x);
			ypoints.add(y);
		}
		g2D.setColor(c);
		g2D.setStroke(new BasicStroke(1F));  // set stroke width of 10
		for(int i=0;i<xpoints.size()-1;i++)
			g2D.draw(new Line2D.Double(xpoints.get(i), ypoints.get(i),xpoints.get(i+1), ypoints.get(i+1)));

		g2D.setStroke(new BasicStroke(10F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  // set stroke width of 10
		g2D.draw(new Line2D.Double(xpoints.get(0),ypoints.get(0),xpoints.get(0),ypoints.get(0)));

		xpoints.clear();
		ypoints.clear();


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

	private double[] circleFromThreePoints()
	{
		double[] point1 = new double[2];
		double[] point2 = new double[2];
		double[] point3 = new double[2];
		double m1,m2,b1,b2;
		double[] params = new double[3];

		double radius;

		point1[0] = 261; point1[1] = 416;

		point2[0] = 201; point2[1] = 246;

		point3[0] = 237; point3[1] = 140;


		m1 = (point2[1] - point1[1])/(point2[0] - point1[0]); m1 = (-1/m1);
		m2 = (point3[1] - point2[1])/(point3[0] - point2[0]); m2 = (-1/m2);

		point1[0] = (point1[0] + point2[0])/2;
		point1[1]= (point1[1] + point2[1])/2;


		point2[0] = (point2[0] + point3[0])/2;
		point2[1]= (point2[1] + point3[1])/2;

		b1 = point1[1] - point1[0]*m1;
		b2 = point2[1] - point2[0]*m2;

		params[0] = (b2-b1)/(m1-m2);
		params[1] = m1*params[0] + b1;

		radius = (params[0]-point3[0])*(params[0]-point3[0])  + (params[1]-point3[1])*(params[1]-point3[1]);

		radius = Math.sqrt(radius);
		System.out.println("Circle Parameters ::" + params[0] +" "+ params[1]+ " Radius: " + radius);
		params[2] = radius;
		return params; 
	}

	public static void main(String[] args) throws IOException
	{
		CurvedNeedleDetection cnd = new CurvedNeedleDetection();
		cnd.trackCurvedNeedle();
	}

}

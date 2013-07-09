package simulatedCurvedNeedle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class CalcStats {

	/**
	 * @param args
	 */


	public void calculateErrorForCurvedNeedle() throws IOException
	{

		File file_estimated = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/estimatedDiff.txt");
		if (!file_estimated.exists())
		{
			file_estimated.createNewFile();
		}
		FileWriter fw_estimated = new FileWriter(file_estimated.getAbsoluteFile());
		BufferedWriter bw_estimated = new BufferedWriter(fw_estimated);


		File file_observed = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/observedDiff.txt");
		if (!file_observed.exists())
		{
			file_observed.createNewFile();
		}
		FileWriter fw_observed = new FileWriter(file_observed.getAbsoluteFile());
		BufferedWriter bw_observed = new BufferedWriter(fw_observed);


		File file_finalEstimate = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/finalEstimateDiff.txt");
		if (!file_finalEstimate.exists())
		{
			file_finalEstimate.createNewFile();
		}
		FileWriter fw_finalEstimate = new FileWriter(file_finalEstimate.getAbsoluteFile());
		BufferedWriter bw_finalEstimate = new BufferedWriter(fw_finalEstimate);


		File file_estimatedParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/estimatedParamDiff.txt");
		if (!file_estimatedParams.exists())
		{
			file_estimatedParams.createNewFile();
		}
		FileWriter fw_estimatedParams = new FileWriter(file_estimatedParams.getAbsoluteFile());
		BufferedWriter bw_estimatedParams = new BufferedWriter(fw_estimatedParams);


		File file_observedParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/observedParamDiff.txt");
		if (!file_observedParams.exists())
		{
			file_observedParams.createNewFile();
		}
		FileWriter fw_observedParams = new FileWriter(file_observedParams.getAbsoluteFile());
		BufferedWriter bw_observedParams = new BufferedWriter(fw_observedParams);


		File file_finalEstimateParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/finalEstimateParamDiff.txt");
		if (!file_finalEstimateParams.exists())
		{
			file_finalEstimateParams.createNewFile();
		}
		FileWriter fw_finalEstimateParams = new FileWriter(file_finalEstimateParams.getAbsoluteFile());
		BufferedWriter bw_finalEstimateParams = new BufferedWriter(fw_finalEstimateParams);


		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/needleValues.txt";


		double[] rms_TipPos_Observed = new double[2];
		double[] rms_TipPos_finalEstimate = new double[2];

		double[] rms_Params_Observed = new double[3];
		double[] rms_Params_finalEstimate = new double[3];

		Scanner scanner;

		String estimatedDiff = "";
		String estimatedParamDiff = "";
		String observedDiff = "";
		String finalEstimateDiff = "";
		String observedParamDiff = "";
		String finalEstimateParamDiff = "";

		double[] val;
		double r;
		double center_x,center_y;
		double tipAngle = 152;
		int count =0;
		try {
			scanner = new Scanner(new File(filename));

			while(scanner.hasNextDouble())
			{	
				center_x= scanner.nextDouble();center_y= scanner.nextDouble();r = scanner.nextDouble();

				val = 	getTipPosDiff(r, center_x,center_y, tipAngle);
				for(int i=0;i<2;i++)
				{
					estimatedDiff = estimatedDiff +" " + val[i];		
				}
				estimatedDiff = estimatedDiff +"\n";

				val = getParamDiffCurved(r, center_x,center_y);
				for(int i=0;i<3;i++)
				{
					estimatedParamDiff = estimatedParamDiff +" " + val[i];
				}
				estimatedParamDiff = estimatedParamDiff +"\n" ;


				center_x= scanner.nextDouble();center_y= scanner.nextDouble();r = scanner.nextDouble();	
				val = getTipPosDiff(r, center_x,center_y, tipAngle);
				for(int i=0;i<2;i++)
				{
					observedDiff = observedDiff +" " + val[i]; 	
					rms_TipPos_Observed[i] +=  (val[i]*val[i]);
				}
				observedDiff = observedDiff +"\n";

				val = getParamDiffCurved(r, center_x,center_y);
				for(int i=0;i<3;i++)
				{
					observedParamDiff = observedParamDiff +" " + val[i];
					rms_Params_Observed[i] +=  (val[i]*val[i]);
				}
				observedParamDiff = observedParamDiff +"\n";

				center_x= scanner.nextDouble();center_y= scanner.nextDouble();r = scanner.nextDouble();
				val = 	getTipPosDiff(r, center_x,center_y, tipAngle);
				for(int i=0;i<2;i++)
				{
					finalEstimateDiff = finalEstimateDiff +" " + val[i];
					rms_TipPos_finalEstimate[i] +=  (val[i]*val[i]);
				}
				finalEstimateDiff = finalEstimateDiff +"\n" ;

				val = getParamDiffCurved(r, center_x,center_y);
				for(int i=0;i<3;i++)
				{
					finalEstimateParamDiff = finalEstimateParamDiff + " " + val[i];
					rms_Params_finalEstimate[i] +=  (val[i]*val[i]);
				}
				finalEstimateParamDiff = finalEstimateParamDiff + "\n" ;

				tipAngle++;
				count++;
			}

			bw_estimated.write(estimatedDiff);
			bw_estimated.close();

			bw_estimatedParams.write(estimatedParamDiff);
			bw_estimatedParams.close();

			bw_observed.write(observedDiff);
			bw_observed.close();
			double pixelSpacing = 2.8346;

			for(int i=0;i<2;i++)
			{
				rms_TipPos_Observed[i] = Math.sqrt(rms_TipPos_Observed[i]/count)*pixelSpacing;
				rms_TipPos_finalEstimate[i] = Math.sqrt(rms_TipPos_finalEstimate[i]/count)*pixelSpacing;
			}
			for(int i=0;i<3;i++)
			{
				rms_Params_Observed[i] = Math.sqrt(rms_Params_Observed[i]/count)*pixelSpacing;
				rms_Params_finalEstimate[i] = Math.sqrt(rms_Params_finalEstimate[i]/count)*pixelSpacing;
			}

			System.out.println("\n\n\n#============Error Curved Needle ===================#");
			System.out.println(count + " RMS Tip Pos Observed ::" +rms_TipPos_Observed[0] +" " + rms_TipPos_Observed[1]);
			System.out.println(count + " RMS Tip Pos KF Estimate ::" +rms_TipPos_finalEstimate[0] +" " + rms_TipPos_finalEstimate[1]);
			System.out.println(count + " RMS Needle Params Observed ::" +rms_Params_Observed[0] +" " + rms_Params_Observed[1]+" "+rms_Params_Observed[2] );
			System.out.println(count + " RMS Needle Params KF Estimated ::" +rms_Params_finalEstimate[0] +" " + rms_Params_finalEstimate[1]+" "+rms_Params_finalEstimate[2] );
			System.out.println("#============Error Curved Needle ===================#\n\n\n");


			bw_observedParams.write(observedParamDiff);
			bw_observedParams.close();

			bw_finalEstimate.write(finalEstimateDiff);
			bw_finalEstimate.close();

			bw_finalEstimateParams.write(finalEstimateParamDiff);
			bw_finalEstimateParams.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private double[] getParamDiffCurved(double r, double center_x, double center_y) {

		double[] originalParams = circleFromThreePoints();

		double[] temp = new double[3];
		temp[0] = originalParams[0]-center_x;
		temp[1] = originalParams[1]-center_y;
		temp[2] = originalParams[2]-r;
		//		String temp = "";
		//
		//		temp = temp + (originalParams[0]-center_x) + "  "+(originalParams[1]-center_y) +" "+ (originalParams[2]-r);

		return temp;
	}

	private double[] getTipPosDiff(double r, double center_x, double center_y, double tipAngle) {
		double[] originalParams = circleFromThreePoints();
		double[] entryPoint = new double[2];

		double angle = (134)*(Math.PI)/180;
		entryPoint[0] = originalParams[0] + originalParams[2]*Math.cos(angle);
		entryPoint[1] = originalParams[1] + originalParams[2]*Math.sin(angle);

		double dist = 0;
		dist = dist + (entryPoint[0]-center_x)*(entryPoint[0]-center_x);
		dist = dist + (entryPoint[1]-center_y)*(entryPoint[1]-center_y);
		dist = Math.sqrt(dist);
		double[] newEntryPoint = new double[2];


		if(r>dist)
		{
			//newEntryPoint[0] = ((dist*center_x) - (r*entryPoint[0]))/(dist-r);
			//newEntryPoint[1] = ((dist*center_y) - (r*entryPoint[1]))/(dist-r);

			newEntryPoint[0] = ( (r*entryPoint[0])-((r-dist)*center_x))/dist;
			newEntryPoint[1] = ( (r*entryPoint[1])-((r-dist)*center_y))/dist;
		}
		else
		{
			newEntryPoint[0] = (r*entryPoint[0] + (dist-r)*center_x )/dist;
			newEntryPoint[1] = (r*entryPoint[1] + (dist-r)*center_y )/dist;

			//newEntryPoint[0] = ((dist-r)*center_x + r*entryPoint[0])/dist;
			//	newEntryPoint[1] = ((dist-r)*center_y + r*entryPoint[1])/dist;
		}


		double newStartingAngle = (Math.acos((newEntryPoint[0] - center_x)/r));//*(180.0/Math.PI);


		double length = (tipAngle*Math.PI/180  - angle)*originalParams[2];
		double x1 = originalParams[0] + originalParams[2]*Math.cos(tipAngle*Math.PI/180);
		double y1 = originalParams[1] + originalParams[2]*Math.sin(tipAngle*Math.PI/180);

		double x2 = center_x + r*Math.cos(newStartingAngle + length/r);
		double y2 = center_y + r*Math.sin(newStartingAngle + length/r); 
		System.out.println("x1 ::" + x1 + " y1 ::" + y1 + " x2 ::" + x2 + " y2 ::" + y2 + " newTipAngle::" +(newStartingAngle + length/r)+" Tip Angle::" + tipAngle );
		//String temp = "\n" + dist+" "+r+" " + center_x+" "+  center_y+" "+ newStartingAngle+" "+newEntryPoint[0] +" "+ newEntryPoint[1] +" " + entryPoint[0] + " "+entryPoint[1]+" "+	 (x1-x2) + " "+(y1-y2);
		double[] temp = new double[2];
		temp[0] = (x1-x2);
		temp[1] = (y1-y2);
		//	String temp = (x1-x2) + " "+(y1-y2);
		/*"Orinal_m :: " + original_m +" m1:: " + m1+ " m2 :: " + m2+ " b1:: "+b1+" b2 :: "+b2 +" "*/
		return temp;
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

		//		point1[0] = 120; point1[1] = 145;
		//		point2[0] = 160; point2[1] = 181;
		//		point3[0] = 186; point3[1] = 217;


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


	public void calculateError()
	{		
		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateCurved/StatFiles/needleValues.txt";
		Vector<Double> center_x = new Vector<Double>();
		Vector<Double> center_y = new Vector<Double>();
		Vector<Double> radius = new Vector<Double>();
		double[] originalParameters = new double[3];
		Scanner scanner;
		try {
			scanner = new Scanner(new File(filename));
			int i = 0;
			while(scanner.hasNextDouble())
			{
				if(i%3 ==1)
				{
					center_x.add(scanner.nextDouble());
					center_y.add(scanner.nextDouble());
					radius.add(scanner.nextDouble());
				}
				else if(i%3 ==0)
				{
					originalParameters[0] = scanner.nextDouble();
					originalParameters[1] = scanner.nextDouble();
					originalParameters[2] = scanner.nextDouble();
				}

				else if(i%3 ==2)
				{
					scanner.nextDouble();
					scanner.nextDouble();
					scanner.nextDouble();
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		originalParameters = circleFromThreePoints();
		int numberOfValues = center_x.size();
		double variance_x = 0;
		double variance_y = 0;
		double variance_r = 0;
		double mean_x = 0;
		double mean_y = 0;
		double mean_r = 0;
		double diff;
		for(int i=0;i<numberOfValues;i++)
		{
			diff = originalParameters[0] - center_x.get(i);
			variance_x = variance_x + (diff*diff); 
			mean_x = mean_x + diff;

			diff = originalParameters[1] - center_y.get(i);
			variance_y = variance_y + (diff*diff);
			mean_y = mean_y + diff;

			diff = originalParameters[2] - radius.get(i);
			variance_r = variance_r + (diff*diff);
			mean_r = mean_r + diff;
		}

		variance_x = variance_x/numberOfValues;
		variance_y = variance_y/numberOfValues;
		variance_r = variance_r/numberOfValues;

		mean_x = mean_x/numberOfValues;
		mean_y = mean_y/numberOfValues;
		mean_r = mean_r/numberOfValues;

		variance_x = variance_x - (mean_x*mean_x);
		variance_y = variance_y - (mean_y*mean_y);
		variance_r = variance_r - (mean_r*mean_r);

		System.out.println("Mean => X ::" + mean_x + " Y :: "+ mean_y + " R :: " + mean_r );
		System.out.println("Varaince => X :: "+ variance_x + " Y :: " + variance_y + " R :: "+variance_r);
		System.out.println("Original => X :: "+originalParameters[0] + " Y :: " + originalParameters[1]+" R :: "+originalParameters[2] );

	}



	public static void main(String[] args) throws IOException
	{
		CalcStats cs = new CalcStats();
		cs.calculateError();
		cs.calculateErrorForCurvedNeedle();
	}

}

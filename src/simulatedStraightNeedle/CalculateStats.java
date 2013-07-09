package simulatedStraightNeedle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class CalculateStats {

	/**
	 * @param args
	 */
	public void calculateErrorForStraightNeedle() throws IOException
	{

		File file_estimated = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/estimatedDiff.txt");
		if (!file_estimated.exists())
		{
			file_estimated.createNewFile();
		}
		FileWriter fw_estimated = new FileWriter(file_estimated.getAbsoluteFile());
		BufferedWriter bw_estimated = new BufferedWriter(fw_estimated);


		File file_observed = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/observedDiff.txt");
		if (!file_observed.exists())
		{
			file_observed.createNewFile();
		}
		FileWriter fw_observed = new FileWriter(file_observed.getAbsoluteFile());
		BufferedWriter bw_observed = new BufferedWriter(fw_observed);


		File file_finalEstimate = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/finalEstimateDiff.txt");
		if (!file_finalEstimate.exists())
		{
			file_finalEstimate.createNewFile();
		}
		FileWriter fw_finalEstimate = new FileWriter(file_finalEstimate.getAbsoluteFile());
		BufferedWriter bw_finalEstimate = new BufferedWriter(fw_finalEstimate);


		File file_estimatedParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/estimatedParamDiff.txt");
		if (!file_estimatedParams.exists())
		{
			file_estimatedParams.createNewFile();
		}
		FileWriter fw_estimatedParams = new FileWriter(file_estimatedParams.getAbsoluteFile());
		BufferedWriter bw_estimatedParams = new BufferedWriter(fw_estimatedParams);


		File file_observedParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/observedParamDiff.txt");
		if (!file_observedParams.exists())
		{
			file_observedParams.createNewFile();
		}
		FileWriter fw_observedParams = new FileWriter(file_observedParams.getAbsoluteFile());
		BufferedWriter bw_observedParams = new BufferedWriter(fw_observedParams);


		File file_finalEstimateParams = new File("/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/finalEstimateParamDiff.txt");
		if (!file_finalEstimateParams.exists())
		{
			file_finalEstimateParams.createNewFile();
		}
		FileWriter fw_finalEstimateParams = new FileWriter(file_finalEstimateParams.getAbsoluteFile());
		BufferedWriter bw_finalEstimateParams = new BufferedWriter(fw_finalEstimateParams);


		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/needleValues.txt";


		Scanner scanner;

		String estimatedDiff = "";
		String estimatedParamDiff = "";
		String observedDiff = "";
		String finalEstimateDiff = "";
		String observedParamDiff = "";
		String finalEstimateParamDiff = "";


		double[] rms_TipPos_Observed = new double[2];
		double[] rms_TipPos_finalEstimate = new double[2];

		double[] rms_Params_Observed = new double[3];
		double[] rms_Params_finalEstimate = new double[3];


		double r;
		double theta;
		int length = 30;
		double[] val;
		int count=0;
		try {
			scanner = new Scanner(new File(filename));

			while(scanner.hasNextDouble())
			{	
				r = scanner.nextDouble();theta = scanner.nextDouble();
				val = getPoint(r,theta,length); 
				for(int i=0;i<2;i++)
				{
					estimatedDiff = estimatedDiff +" " + val[i];
				}
				estimatedDiff = estimatedDiff +"\n";

				val =  getParamDiff(r,theta);
				for(int i=0;i<2;i++)
				{
					estimatedParamDiff = estimatedParamDiff +" " + val[i];
				}
				estimatedParamDiff = estimatedParamDiff +"\n";

				r = scanner.nextDouble();theta = scanner.nextDouble();
				val =  getPoint(r,theta,length); 
				for(int i=0;i<2;i++)
				{
					observedDiff = observedDiff +" " + val[i];
					rms_TipPos_Observed[i] += (val[i]*val[i]);
				}
				observedDiff = observedDiff+"\n";


				val =  getParamDiff(r,theta);
				for(int i=0;i<2;i++)
				{
					observedParamDiff = observedParamDiff +" " +val[i];
					rms_Params_Observed[i] +=  (val[i]*val[i]);
				}
				observedParamDiff = observedParamDiff +"\n" ;

				r = scanner.nextDouble();theta = scanner.nextDouble();
				val =  getPoint(r,theta,length); 
				for(int i=0;i<2;i++)
				{
					finalEstimateDiff = finalEstimateDiff +" " + val[i];
					rms_TipPos_finalEstimate[i] += (val[i]*val[i]);
				}
				finalEstimateDiff = finalEstimateDiff +"\n" ;

				val =  getParamDiff(r,theta);
				for(int i=0;i<2;i++)
				{
					finalEstimateParamDiff = finalEstimateParamDiff + " " + val[i];
					rms_Params_finalEstimate[i]+= (val[i]*val[i]);
				}
				finalEstimateParamDiff = finalEstimateParamDiff + "\n";
				length++;
				count++;
			}

			// RMS with respective to pixel spacing.
			double pixelSpacing = 2.8346;
			for(int i=0;i<2;i++)
			{
				rms_TipPos_Observed[i] = Math.sqrt(rms_TipPos_Observed[i]/count)*pixelSpacing ;
				rms_TipPos_finalEstimate[i] = Math.sqrt(rms_TipPos_finalEstimate[i]/count)*pixelSpacing;
				rms_Params_Observed[i] = Math.sqrt(rms_Params_Observed[i]/count);
				rms_Params_finalEstimate[i] = Math.sqrt(rms_Params_finalEstimate[i]/count);
			}

			System.out.println("\n\n\n#============Error Straight Needle ===================#");
			System.out.println("RMS Tip Pos Observed ::" +rms_TipPos_Observed[0] +" " + rms_TipPos_Observed[1] );
			System.out.println("RMS Tip Pos KF Estimate ::" +rms_TipPos_finalEstimate[0] +" " + rms_TipPos_finalEstimate[1] );
			System.out.println("RMS Needle Params Observed ::" +rms_Params_Observed[0] +" " + rms_Params_Observed[1] );
			System.out.println("RMS Needle Params KF Estimated ::" +rms_Params_finalEstimate[0] +" " + rms_Params_finalEstimate[1] );
			System.out.println("#============Error Straight Needle ===================#\n\n\n");

			bw_estimated.write(estimatedDiff);
			bw_estimated.close();

			bw_estimatedParams.write(estimatedParamDiff);
			bw_estimatedParams.close();

			bw_observed.write(observedDiff);
			bw_observed.close();


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



	private double[] lineFromTwoPoints()
	{
		double[] lineParams = new double[2];

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

		return lineParams;
	}


	
	private double[] getParamDiff(double r1, double theta1) {


		double[] actualParams = lineFromTwoPoints();

		double[] temp = new double[2];
		temp[0] = actualParams[0]-r1;
		temp[1] = actualParams[1]-theta1;
		//		String temp = "";
		//
		//		temp = temp + (r2-r1) + "  "+ (theta2-theta1);

		return temp;
	}



	private double[] getPoint(double r, double theta, int length) {

		int[] approxEntryPoint = new int[2];
		approxEntryPoint[0] = 243;
		approxEntryPoint[1] = 417;

		double[] newStartingPoint = new double[2];
		newStartingPoint[1] = approxEntryPoint[1];
		newStartingPoint[0] = approxEntryPoint[0];
		double dist= (-r+approxEntryPoint[0]*Math.cos((theta*Math.PI)/180)+approxEntryPoint[1]*Math.sin((theta*Math.PI)/180));

		dist = dist/Math.sqrt((Math.pow(r, 2)+1));
		newStartingPoint[0]= newStartingPoint[0] + dist*Math.cos(theta*Math.PI/180);
		newStartingPoint[1]= newStartingPoint[1] + dist*Math.sin(theta*Math.PI/180);


		double[] actualParams = lineFromTwoPoints();
		double x1 = approxEntryPoint[0] + length*Math.cos((actualParams[1]+90)*Math.PI/180);
		double y1 = approxEntryPoint[1] - length*Math.sin((actualParams[1]+90)*Math.PI/180);

		double x2 = newStartingPoint[0] + length*Math.cos((theta+90)*Math.PI/180);
		double y2 = newStartingPoint[1] - length*Math.sin((theta+90)*Math.PI/180);
		double[] temp = new double[2];
		temp[0] = x1-x2;
		temp[1] = y1-y2;
		return temp;
		//		String temp = /*"Orinal_m :: " + original_m +" m1:: " + m1+ " m2 :: " + m2+ " b1:: "+b1+" b2 :: "+b2 +" "+ newStartingPoint[0] +" "+ newStartingPoint[1] +" " + entryPoint[0] + " "+entryPoint[1]+" "+*/	 (x1-x2) + " "+(y1-y2);
		//		return temp;
	}


	public void calculateError()
	{		
		String filename = "/home/satya/Projects/NeedleDetectionNew/Simulated/ProstateStraight/StatFiles/needleValues.txt";
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
				if(i%2 ==1)
				{
					center_x.add(scanner.nextDouble());
					center_y.add(scanner.nextDouble());
					radius.add(scanner.nextDouble());
				}
				else
				{
					originalParameters[0] = scanner.nextDouble();
					originalParameters[1] = scanner.nextDouble();
					originalParameters[2] = scanner.nextDouble();
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numberOfValues = center_x.size();
		double variance_x = 0;
		double variance_y = 0;
		double variance_r = 0;
		double mean_x = 0;
		double mean_y = 0;
		double mean_r = 0;

		for(int i=0;i<numberOfValues;i++)
		{
			variance_x = variance_x + (center_x.get(i)*center_x.get(i)); 
			variance_y = variance_y + (center_y.get(i)*center_y.get(i)); 
			variance_r = variance_r + (radius.get(i)*radius.get(i));

			mean_x = mean_x + center_x.get(i);
			mean_y = mean_y + center_y.get(i);
			mean_r = mean_r + radius.get(i);
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



	public static void main(String[] args) throws IOException {

		
		CalculateStats calc = new CalculateStats();
		calc.calculateError();
		calc.calculateErrorForStraightNeedle();
	}

}

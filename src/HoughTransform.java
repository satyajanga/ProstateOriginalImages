import java.awt.Point;
import java.util.Vector;


public class HoughTransform {

	/**
	 * @param args
	 */
	
	int numberOfRhoBins;
	int numberOfThetaBins;
	
	double rhoBinSize;
	double thetaBinSize;
	
	double thetaRange[] = new double[2];
	double rhoRange[] = new double[2];
	
	int[][] houghBins;
	
	Vector<Point> points;
	
	public void initialize()
	{
		numberOfRhoBins = (int)((rhoRange[1]-rhoRange[0])/rhoBinSize) + 1;
		numberOfThetaBins = (int)((thetaRange[1] - thetaRange[0])/thetaBinSize) + 1;
		houghBins = new int[numberOfRhoBins][numberOfThetaBins];
		
		for(int i=0;i<numberOfRhoBins;i++)
		{
			for(int j=0;j<numberOfThetaBins;j++)
			{
				houghBins[i][j] = 0;
			}
		}
	}
	
	public void fillHoughBins()
	{
		int numberOfPoints = points.size();
		for(int i=0;i<numberOfPoints;i++)
		{
			fillHoughBins(points.get(i));
		}
	}
		
	public void fillHoughBins(Point point) 
	{
		double rhoVal;
		int rhoIndex;
		double theta;
		for(int thetaIndex=0;thetaIndex<numberOfThetaBins;thetaIndex++)
		{
			theta = (((thetaIndex*thetaBinSize)+thetaRange[0])*Math.PI)/180;
			rhoVal  = (point.x * Math.cos(theta)) + (point.y * Math.sin(theta));

			if(rhoVal <0)
				continue;

			rhoIndex = (int) Math.round((rhoVal-rhoRange[0])/rhoBinSize);
			if (rhoIndex>=0 && rhoIndex < numberOfRhoBins  ) 
			{
				houghBins[rhoIndex][thetaIndex]++;
			}
		}
		
	}
	
	public double[] getMaxLine()
	{	
		int maxRhoIndex = -1;
		int maxThetaIndex = -1;
		int maxCount = -1;
		for(int i=0;i<numberOfRhoBins;i++)
		{
			for(int j=0;j<numberOfThetaBins;j++)
			{
				if(houghBins[i][j] > maxCount)
				{
					maxCount = houghBins[i][j];
					maxRhoIndex = i;
					maxThetaIndex = j;
				}				
			}
		}
		
		double maxParams[] = new double[2];
		
		System.out.println("Max Count :: " + maxCount + " Max RhoIndex :: " +maxRhoIndex + " Max Theta Index ::" + maxThetaIndex );
		maxParams[0] = rhoRange[0] + maxRhoIndex*rhoBinSize;
		maxParams[1] = thetaRange[0] + maxThetaIndex*thetaBinSize;
		
		return maxParams;
		
	}
	
	public void setRhoBinSize(double val)
	{
		rhoBinSize = val;	
	}
	
	public void setThetaBinSize(double val)
	{
		thetaBinSize = val;
	}
	
	public void setThetaRange(double[] range)
	{
		thetaRange = range;
	}
	
	public void setRhoRange(double[] range)
	{
		rhoRange = range;
	}
	
	public void setPoints(Vector<Point> p)
	{
		points = p;
	}
	public void printThis()
	{
		System.out.println("Number Of Bins => Rho Bins ::" + numberOfRhoBins + " Theta Bins ::" + numberOfThetaBins);
		System.out.println("Range => Rho  :: Min = " + rhoRange[0] + " Max =" +rhoRange[1] +" Theta :: Min =" + thetaRange[0] + " Max = " + thetaRange[1]);
		System.out.println("Bin Sizes => Rho :: " + rhoBinSize + "  Theta :: " + thetaBinSize);
	
	}
	public static void main(String[] args) 
	{
	
	}

}

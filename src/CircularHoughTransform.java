import java.awt.Point;
import java.util.Vector;


public class CircularHoughTransform {

	/**
	 * @param args
	 */

	int numberOfRadiusBins;
	int numberOfCenterXBins;
	int numberOfCenterYBins;

	double radiusBinSize;
	double centerXBinSize;
	double centerYBinSize;

	double radiusRange[];
	double centerXRange[];
	double centerYRange[];

	Vector<Point> points;

	int[][][] houghBins;

	double r_theta_values[][][];	
	public void initialize()
	{
		numberOfRadiusBins = (int)((radiusRange[1]-radiusRange[0])/radiusBinSize) + 1;
		numberOfCenterXBins = (int)((centerXRange[1]-centerXRange[0])/centerXBinSize) +1;
		numberOfCenterYBins = (int)((centerYRange[1]-centerYRange[0])/centerYBinSize) + 1;

		houghBins = new int[numberOfRadiusBins][numberOfCenterXBins][numberOfCenterYBins];

		for(int i=0;i<numberOfRadiusBins;i++)
		{
			for(int j=0;j<numberOfCenterXBins;j++)
			{
				for(int k=0;k<numberOfCenterYBins;k++)
				{
					houghBins[i][j][k] =0;
				}
			}
		}
		buildLookUpTable();
	}

	public void fillHoughBins()
	{
		int numberOfPoints = points.size();
		for(int i=0;i<numberOfPoints;i++)
		{
			fillHoughBins(points.get(i));
		}
	}

	private void fillHoughBins(Point point)
	{
		double x,y;
		int xindex,yindex;
		for(int i=0;i<numberOfRadiusBins;i++)
		{
			for(int j=0;j<360;j++)
			{
				x = point.x - r_theta_values[i][j][0];
				y = point.y - r_theta_values[i][j][1];
				if(x<=centerXRange[1] && x>=centerXRange[0] && y <= centerYRange[1] && y>=centerYRange[0])
				{
					xindex = (int) ((x- centerXRange[0])/centerXBinSize);
					yindex = (int) ((y-centerYRange[0])/centerYBinSize);
					houghBins[i][xindex][yindex]+= 1;
				}
			}
		}
	}

	public double[] getMaxCircleParams()
	{

		int maxRIndex=0;
		int maxCenterXIndex=0;
		int maxCenterYIndex=0;
		int maxCount = -1;

		for(int i=0;i<numberOfRadiusBins;i++)
		{
			for(int j=0;j<numberOfCenterXBins;j++)
			{
				for(int k=0;k<numberOfCenterYBins;k++)
				{
					if(houghBins[i][j][k] < maxCount)
					{
						maxCount = houghBins[i][j][k];
						maxRIndex = i;
						maxCenterXIndex = j;
						maxCenterYIndex = k;
					}
				}
			}
		}

		double[] maxParams = new double[3];
		maxParams[0] = radiusRange[0] + maxRIndex*radiusBinSize;
		maxParams[1] = centerXRange[0] + maxCenterXIndex*centerXBinSize;
		maxParams[2] = centerYRange[0] + maxCenterYIndex*centerYBinSize;
		return maxParams;
	}

	
	private void buildLookUpTable()
	{
		double r,theta;
		r_theta_values = new double[numberOfRadiusBins][360][2];
		for(int i=0;i<numberOfRadiusBins;i++)
		{
			for (int j=0; j<360; j+=1)
			{
				r = i*radiusBinSize + radiusRange[0];
				theta = (j * Math.PI) / 180;
				r_theta_values[i][j][0] = r * Math.cos(theta);
				r_theta_values[i][j][1] = r * Math.sin(theta);
			}
		}
	}

	public void setRadiusBinSize(double val)
	{
		radiusBinSize = val;
	}
	public void setCenterXBinSize(double val)
	{
		centerXBinSize = val;		
	}
	public void setCenterYBinSize(double val)
	{
		centerYBinSize = val;
	}
	public void setRadiusRange(double[] val)
	{
		radiusRange = val;
	}
	public void setCenterXRange(double[] val)
	{
		centerXRange = val;
	}
	public void setCenterYRange(double[] val)
	{
		centerYRange = val;
	}
	public void setPoints(Vector<Point> p)
	{
		points = p;		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

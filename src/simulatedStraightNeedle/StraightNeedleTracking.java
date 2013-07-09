package simulatedStraightNeedle;

public class StraightNeedleTracking
{
	double[] P;
	double[] Q;
	double[] R;

	double[] KalmanGain;

	double[] X_t;

	public void Initialize(double[] initialEstimate)
	{
		P = new double[2];

		P[0] = 10; P[1] = 5;

		Q = new double[2];
		Q[0] = 0.00001; Q[1] = 0.000001;
		
		R = new double[2];
		R[0] = 3 ; R[1] = 2; 
		
		X_t = new double[2];
		X_t[0] = initialEstimate[0]; X_t[1] = initialEstimate[1];	
		KalmanGain = new double[2];
		
	}
	public void correctStateFromObservation(double[] observedParams)
	{
		//Update Equations

		for(int i=0;i<2;i++)
		{
			KalmanGain[i] = P[i]/(P[i] + R[i]);
			
			System.out.println("P :: " + P[i] +" K :: "+KalmanGain[i] + " Observed - original :: " +(observedParams[i]-X_t[i]) );
			X_t[i] = X_t[i]+(KalmanGain[i]*(observedParams[i]-X_t[i]));
			P[i] = (1-KalmanGain[i])*P[i];
		}
		
		P[0] = P[0] + Q[0];
		P[1] = P[1] + Q[1];

	}
	public double[] getStateEstimate()
	{
		double[] temp = new double[2];
		temp[0] = X_t[0];
		temp[1] = X_t[1];	
		return temp;

	}


}

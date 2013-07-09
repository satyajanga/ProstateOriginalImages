package simulatedCurvedNeedle;

public class CurvedNeedleTracking
{

	double[] P;
	double[] Q;
	double[] R;

	double[] KalmanGain;

	double[] X_t;

	
	public void Initialize(double[] initialEstimate)
	{

		//	X :: 23.35555555555554 Y :: 5.2900000000000205 R :: 10.622222222222206
	//	65.81921110435766 73.30328678033474 102.57765875185913
		P = new double[3];

		P[0] = 25; P[1] = 26; P[2] =35;

		Q = new double[3];
		Q[0] = 0.00001; Q[1] = 0.00001; Q[2] =0.00001;

		R = new double[3];
		R[0] = 10 ; R[1] = 12; R[2] =20;

		X_t = new double[3];
		X_t[0] = initialEstimate[0]; X_t[1] = initialEstimate[1]; X_t[2] = initialEstimate[2];

		KalmanGain = new double[3];
	}
	public double[] getStateEstimate()
	{
		// Forward Equations
		double[] temp = new double[3];
		temp[0] = X_t[0];
		temp[1] = X_t[1];temp[2] = X_t[2];
		return temp;

	}

	public void correctStateFromObservation(double[] observedParams)
	{
		//Update Equations

		for(int i=0;i<3;i++)
		{
			KalmanGain[i] = P[i]/(P[i] + R[i]);

			//System.out.println("P :: " + P[i] +" K :: "+KalmanGain[i] + " Observed - original :: " +(observedParams[i]-X_t[i]) );
			X_t[i] = X_t[i]+(KalmanGain[i]*(observedParams[i]-X_t[i]));
			P[i] = (1-KalmanGain[i])*P[i];
		}

		P[0] = P[0] + Q[0];
		P[1] = P[1] + Q[1];
		P[2] = P[2] + Q[2];

	}

}

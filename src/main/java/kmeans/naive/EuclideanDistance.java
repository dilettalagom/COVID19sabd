package kmeans.naive;

import java.io.Serializable;
import static java.lang.Math.*;



public class EuclideanDistance implements Serializable {

	public double distance(Double p1, Double p2) {
		double sum = 0.0;
		sum += pow(abs(p1 - p2), getNorm());
		return root(sum, getNorm());
	}

	protected double getNorm(){return 1.0;};

	public double root(double a, double n) {

		if(new Double(1.0).equals(n))
			return a;
		if (new Double(2.0).equals(n))
			return sqrt(a);
		return pow(a, 1 / n);
	}
}

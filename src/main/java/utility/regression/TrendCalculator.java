package utility.regression;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;



public class TrendCalculator implements Serializable {


    private static TrendCalculator instance = null;

    private TrendCalculator(){

    }

    public static TrendCalculator getInstance()
    {
        if (instance == null)
            instance = new TrendCalculator();

        return instance;
    }

    public double getTrendCoefficient(double[] infected){

        double[] x = Arrays.stream(IntStream.range(1, infected.length+1).toArray()).asDoubleStream().toArray();
        SimpleRegression r = new SimpleRegression();
        for (int i = 0; i < infected.length; i++) {
            r.addData(x[i], infected[i]);
        }
        return r.getSlope();
    }


}

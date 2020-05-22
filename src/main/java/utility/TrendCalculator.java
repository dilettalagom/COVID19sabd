package utility;

import org.apache.commons.math3.stat.regression.SimpleRegression;


import javax.inject.Singleton;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;

@Singleton
public class TrendCalculator implements Serializable {


    private double[] convert_infectedValues(String[] infectedString){
        return Arrays.stream(infectedString).mapToDouble(Double::parseDouble).toArray();
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

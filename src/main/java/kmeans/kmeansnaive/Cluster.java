package kmeans.kmeansnaive;

import lombok.Data;
import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.util.StatCounter;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.Math.*;
import static java.lang.Math.pow;


@Data
public class Cluster implements Serializable {

    int id;
    double initialCentroid;
    double relocatedCentroid;
    JavaRDD<ClassificationMonthPojo> pointsOfCluster;


    public Cluster(int id, double currentCentroid) {
        this.id = id;
        this.initialCentroid = currentCentroid;
    }



    public void relocateCentroid() {

        StatCounter statCounter = pointsOfCluster.aggregate(
                new StatCounter(),
                (acc, p) -> acc.merge(p.getTrendMonth()),
                StatCounter::merge);

        this.relocatedCentroid = statCounter.mean();

    }


    public boolean isCentroidMoved (double epsilon)
    {
        double deltaAbs = Math.abs(this.initialCentroid - relocatedCentroid);
        return deltaAbs > 0;
    }



    public double getFinalCentroid(){
        return (isCentroidMoved(KMeansSimulation.CENTROID_EPSILON)) ? this.relocatedCentroid : this.initialCentroid;
    }


    public double getDistanceFromCentroid(Double pointToEvaluate)
    {
        double sum = 0.0;
        sum += pow(abs(pointToEvaluate- this.initialCentroid), getNorm());
        return root(sum, getNorm());
    }


    protected double getNorm(){return 1.0;};


    protected double root(double a, double n) {

        if(new Double(1.0).equals(n))
            return a;
        if (new Double(2.0).equals(n))
            return sqrt(a);
        return pow(a, 1 / n);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cluster cluster = (Cluster) o;
        return id == cluster.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

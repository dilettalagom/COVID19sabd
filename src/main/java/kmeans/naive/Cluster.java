package kmeans.naive;

import lombok.Data;
import model.ClassificationMonthPojo;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.List;

@Data
public class Cluster implements Serializable {

    int numCluster;
    double centroid;


    public Cluster(int numCluster, double centroid) {
        this.numCluster = numCluster;
        this.centroid = centroid;
    }
}

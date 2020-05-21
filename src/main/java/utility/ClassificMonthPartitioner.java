package utility;

import org.apache.spark.Partitioner;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.List;


public class ClassificMonthPartitioner extends Partitioner {

        private List<String> months = new ArrayList<>();
        private int numParts;

        public ClassificMonthPartitioner(List<String> months, int i ) {
            this.months = months;
            this.numParts = i;
        }

        @Override
        public int numPartitions()
        {
            return numParts;
        }


        @Override
            public int getPartition(Object key){
            Tuple2<String,Double> newKey = (Tuple2<String,Double>) key;
            //partition based on the first character of the key...you can have your logic here !!
            //numParts = months.indexOf(newKey._1);
            return months.indexOf(newKey._1)%numParts;

        }

        @Override
        public boolean equals(Object obj){
            if(obj instanceof ClassificMonthPartitioner)
            {
                ClassificMonthPartitioner partitionerObject = (ClassificMonthPartitioner)obj;
                if(partitionerObject.numParts == this.numParts)
                    return true;
            }

            return false;
        }

}


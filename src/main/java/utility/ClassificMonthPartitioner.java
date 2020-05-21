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
            System.out.println(months.toString());
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
            //int res = (months.indexOf(newKey._1) + 1)%numParts;
            int res = (months.indexOf(newKey._1))%numParts;
            return res;

        }

        //date2020-05 -> month index: 0 -> res :0
        //date2020-02 -> month index: 5 -> res :0
        //date2020-03 -> month index: 1 -> res :1
        //date2020-04 -> month index: 4 -> res :4
        //date2020-01 -> month index: 2 -> res :2


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


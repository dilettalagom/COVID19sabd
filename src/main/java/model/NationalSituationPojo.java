package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import java.io.Serializable;


@ToString
public class NationalSituationPojo implements Serializable {

    @Getter
    private DateTime date;
    @Getter @Setter
    private Integer numHealed;
    @Getter @Setter
    private Integer numTampon;


    public NationalSituationPojo(String date, String numHealed, String numTampon) {
        numHealed = ;
        dateTime = formatDate(dateTimeString);
        weatherCondition = weatherConditionString;
    }

    public DateTime getLocalDateTime(){
        if (dateTimezone!= null) {
            DateTime local = dateTime.withZone(this.dateTimezone);
            return  local;
        }
        return dateTime;
    }

    public void setDateTimezone(String dateTimezone) {
        this.dateTimezone = DateTimeZone.forID(dateTimezone);
    }

    public void setDateTime(String dateTimeString) { dateTime = formatDate(dateTimeString);
    }

    public static DateTime formatDate(String date){
        return DateTime.parse(date,formatter);
    }
}

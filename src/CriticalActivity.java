import java.util.Date;

/**
 * Created by natalia on 2014-03-08.
 */
public class CriticalActivity {
    String activityID;
    String activityConcept;
    Date startDate;
    Date endDate;
    int frameStamp;

    CriticalActivity CriticalActivity(String id, String activityConcept, int frameStamp){
        this.activityID = id;
        this.activityConcept = activityConcept;
        this.frameStamp = frameStamp;
        return this;
    }

}

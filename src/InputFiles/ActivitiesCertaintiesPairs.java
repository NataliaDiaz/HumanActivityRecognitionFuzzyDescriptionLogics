package InputFiles;

import java.util.ArrayList;

/**
 * Created by natalia on 2014-04-12.
 */
public class ActivitiesCertaintiesPairs {
    protected ArrayList<String> activities;
    protected ArrayList<Float> certainties;

    public ActivitiesCertaintiesPairs(ArrayList<String> activities, ArrayList<Float> certainties) {
        this.activities =activities;
        this.certainties = certainties;
    }
    public ArrayList<String> getActivities() {
        return this.activities;
    }

    public ArrayList<Float> getCertainties() {
        return this.certainties;
    }

}

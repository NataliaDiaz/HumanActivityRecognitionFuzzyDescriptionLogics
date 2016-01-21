package InputFiles;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by natalia on 2014-04-07.
 */
public class SubActivitiesSubSequence {
    protected ArrayList<HashMap<String, ArrayList<String>>> subActivities;
    protected boolean optional;
    protected boolean repeatable;
    protected int requiresMinReps;

    public SubActivitiesSubSequence(ArrayList<HashMap<String, ArrayList<String>>> subActivities, boolean optional, int requiresMinReps) {
        this.subActivities = subActivities;
        this.optional = optional;
        this.repeatable = repeatable;
        this.requiresMinReps = requiresMinReps;
    }
    public ArrayList<HashMap<String, ArrayList<String>>> getSubActivities() {
        return this.subActivities;
    }

    public boolean getOptional() {
        return this.optional;
    }

    public int getRequiresMinReps() {
        return this.requiresMinReps;
    }

    public boolean getRepeatable() {
        return this.repeatable;
    }
}

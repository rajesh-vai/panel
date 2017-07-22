package search;

import java.util.List;

/**
 * Created by rvairamani on 22-07-2017.
 */
public class DisplayItem {

    private String groupName;
    private String screenName;
    private String description;

    public DisplayItem(String groupName, String screenName, String description) {
        this.groupName = groupName;
        this.screenName = screenName;
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



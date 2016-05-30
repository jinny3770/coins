package coins.hansung.way.etc;

import java.util.ArrayList;

/**
 * Created by sora on 2016-05-26.
 */
public class Family {

    private static Family family;

    ArrayList<PersonInfo> familyArray;

    public static synchronized Family getInstance() {

        if( family == null)
            family = new Family();

        return family;
    }

    private Family() {
        familyArray = new ArrayList<PersonInfo>();
    }

    public ArrayList getFamilyArray() {
        return familyArray;
    }

    public void setFamilyArray(ArrayList familyArray) {
        this.familyArray = familyArray;
    }
}

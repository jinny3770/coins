package coins.hansung.way.etc;

import java.util.ArrayList;

import coins.hansung.way.R;

/**
 * Created by sora on 2016-05-27.
 */
public class FamilyMarkerResource {

    ArrayList<Integer> familyMarkerBitmap;

    public FamilyMarkerResource() {

        familyMarkerBitmap = new ArrayList<>();

        familyMarkerBitmap.add(R.drawable.red);
        familyMarkerBitmap.add(R.drawable.blue);
        familyMarkerBitmap.add(R.drawable.yellow);
        familyMarkerBitmap.add(R.drawable.purple);
        familyMarkerBitmap.add(R.drawable.pink);
        familyMarkerBitmap.add(R.drawable.green);

    }

    public ArrayList<Integer> getFamilyMarkerBitmap() {
        return this.familyMarkerBitmap;
    }
}

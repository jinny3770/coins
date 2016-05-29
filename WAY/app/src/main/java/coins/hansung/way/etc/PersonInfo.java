package coins.hansung.way.etc;

import android.graphics.Bitmap;

import com.skp.Tmap.TMapPoint;

/**
 * Created by sora on 2016-03-23.
 */
public class PersonInfo {

    private String ID;
    private String name;
    private String groupCode;
    private TMapPoint point;
    private Boolean gpsSig;
    private int battery;
    private Bitmap profileImage;

    private String phoneNumber;

    public PersonInfo() {
        ID = null;
        name = null;
        groupCode = null;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(TMapPoint point) {
        this.point = point;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public void setGpsSig(Boolean gpsSig) {
        this.gpsSig = gpsSig;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public void setProfileImage(Bitmap profileImage) { this.profileImage = profileImage; }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public TMapPoint getPoint() {
        return point;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public Boolean getGpsSig() {
        return gpsSig;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBattery() {
        return battery;
    }

    public Bitmap getProfileImage() { return profileImage; }
}
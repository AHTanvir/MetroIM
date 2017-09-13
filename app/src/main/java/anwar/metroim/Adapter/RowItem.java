package anwar.metroim.Adapter;

import android.graphics.Bitmap;

/**
 * Created by anwar on 12/16/2016.
 */

public class RowItem {
    private String contact_name;
    private String status;
    private int profile_picture_id;
    private String contact_type;
    private Bitmap pro_image;
    private String contact_number;
    private String name;
    private String number;
    private String subject;
    private String credit;
    private String gpa;
    public RowItem(String contact_number, String contact_name) {
        this.contact_number = contact_number;
        this.contact_name = contact_name;
    }

    public RowItem(String subject, String credit, String gpa) {
        this.subject = subject;
        this.credit = credit;
        this.gpa = gpa;
    }

    public RowItem(String contact_number) {
        this.contact_number = contact_number;
    }

    public RowItem(String contact_name, Bitmap pro_image, String status, String contact_type, String contact_number){
        this.contact_name=contact_name;
        this.pro_image=pro_image;
        //this.profile_picture_id=profile_picture_id;
        this.status=status;
        this.contact_type=contact_type;
        this.contact_number=contact_number;

    }
    public String getContact_name(){
        return contact_name;
    }
    public String getContact_type(){
        return contact_type;
    }
    public String getStatus(){
        return  status;
    }
    public String getContact_number(){
        return contact_number;
    }
    /*
    public int getProfile_picture_id(){
        return profile_picture_id;
    }
*/
    public Bitmap getPro_image(){
        return pro_image;
    }
    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }



    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public void setPro_image(Bitmap pro_image){
        this.pro_image=pro_image;
    }
    public void setContact_number(){
        this.contact_number=contact_number;
    }
/*
    public void setProfile_picture_id(int profile_picture_id) {
        this.profile_picture_id = profile_picture_id;
    }
    */
}

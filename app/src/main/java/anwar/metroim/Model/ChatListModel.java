package anwar.metroim.Model;

import android.graphics.Bitmap;

/**
 * Created by anwar on 9/27/2017.
 */

public class ChatListModel {
    private String name;
    private Bitmap image;
    private String msg;
    private boolean newmsg;
    private String date;
    private String number;
    private String total;

    public ChatListModel(String name, Bitmap image, String msg, boolean newmsg, String date, String number) {
        this.name = name;
        this.image = image;
        this.msg = msg;
        this.newmsg = newmsg;
        this.date = date;
        this.number = number;
    }

    public ChatListModel(String name, Bitmap image, String msg, boolean newmsg, String date, String number, String total) {
        this.name = name;
        this.image = image;
        this.msg = msg;
        this.newmsg = newmsg;
        this.date = date;
        this.number = number;
        this.total = total;
    }

    public ChatListModel(String name, Bitmap image, boolean newmsg, String date, String number) {
        this.name = name;
        this.image = image;
        this.newmsg = newmsg;
        this.date = date;
        this.number = number;
    }

    public ChatListModel(String name, Bitmap image, boolean newmsg, String date, String number, String total) {
        this.name = name;
        this.image = image;
        this.newmsg = newmsg;
        this.date = date;
        this.number = number;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isNewmsg() {
        return newmsg;
    }

    public void setNewmsg(boolean newmsg) {
        this.newmsg = newmsg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

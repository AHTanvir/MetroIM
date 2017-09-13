package anwar.metroim.Adapter;

/**
 * Created by anwar on 2/2/2017.
 */

public class ChatModel {
    private int type;
    private String content;
    private String imgpath;
    private String time;
    private String date;
    private String id;
    private int visibility=0;

    public ChatModel(int type, String content, String time, String date, String id, int visibility) {
        this.type = type;
        this.content = content;
        this.time = time;
        this.date = date;
        this.id = id;
        this.visibility = visibility;
    }
    public ChatModel(int type, String content, String time, String date, String id) {
        this.type = type;
        this.content = content;
        this.time = time;
        this.date = date;
        this.id = id;
    }
    public ChatModel(int type, String content, String time, String date) {
        this.type = type;
        this.content = content;
        this.time = time;
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}

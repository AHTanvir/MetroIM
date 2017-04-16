package anwar.metroim.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anwar on 3/30/2017.
 */

public class arrayList {
    private boolean serviceIsRunning=false;
    private  static arrayList mInstance;;
    private List<RowItem> chatlist;
    private List<RowItem> contactlist;
    private String  currentDateHolder;
    public arrayList(){
        chatlist=new ArrayList<>();
        contactlist=new ArrayList<>();
        currentDateHolder="noDate";
    }
    public static arrayList getmInstance(){
        if(mInstance==null)
        {
            mInstance=new arrayList();
        }
        return mInstance;
    }
    public List<RowItem> getChatlist() {
        return this.chatlist;
    }

    public void setChatlist(List<RowItem> chatlist) {
        this.chatlist = chatlist;
    }

    public List<RowItem> getContactlist() {
        return this.contactlist;
    }

    public void setContactlist(List<RowItem> contactlist) {
        this.contactlist = contactlist;
    }

    public String getCurrentDateHolder() {
        return currentDateHolder;
    }

    public void setCurrentDateHolder(String currentDateHolder) {
        this.currentDateHolder = currentDateHolder;
    }

    public boolean isServiceIsRunning() {
        return serviceIsRunning;
    }

    public void setServiceIsRunning(boolean serviceIsRunning) {
        this.serviceIsRunning = serviceIsRunning;
    }
}

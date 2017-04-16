package anwar.metroim.CustomListView;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anwar on 3/30/2017.
 */

public class ParcelableRowItm extends ArrayList<RowItem> implements Parcelable {
    private String contact_name;
    private Bitmap pro_image;
    private String status;
    private String contact_type;
    private String contact_number;
    private List<RowItem> list=new ArrayList<>();

    public ParcelableRowItm(String contact_name, Bitmap pro_image, String status, String contact_type, String contact_number, List<RowItem> list) {
        this.contact_name = contact_name;
        this.pro_image = pro_image;
        this.status = status;
        this.contact_type = contact_type;
        this.contact_number = contact_number;
        this.list = list;
    }
    public ParcelableRowItm(Parcel parcel) {
        this.contact_name = parcel.readString();
        this.pro_image = parcel.readParcelable(Bitmap.class.getClassLoader());
        this.status =parcel.readString();
        this.contact_type = parcel.readString();
        this.contact_number = parcel.readString();
        this.list=parcel.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contact_name);
        dest.writeValue(pro_image);
        dest.writeString(status);
        dest.writeString(contact_type);
        dest.writeString(contact_number);
        dest.writeList(list);
    }
    public static Creator<ParcelableRowItm> CREATOR = new Creator<ParcelableRowItm>() {

        @Override
        public ParcelableRowItm createFromParcel(Parcel source) {
            return new ParcelableRowItm(source);
        }

        @Override
        public ParcelableRowItm[] newArray(int size) {
            return new ParcelableRowItm[size];
        }

    };
}

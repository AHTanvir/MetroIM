package anwar.metroim.Adapter;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anwar.metroim.Model.ChatModel;
import anwar.metroim.R;

/**
 * Created by anwar on 2/2/2017.
 */

public class ChatAdapter extends BaseAdapter{
    private static final int TYPE_LEFT_TEXT =0;
    private static final int TYPE_LEFT_IMG = 1;
    private static final int TYPE_RIGHT_TEXT = 2;
    private static final int TYPE_RIGHT_IMG = 3;
    List<ChatModel> mChatList=new ArrayList<>();
    public ChatAdapter(List<ChatModel> list) {
        mChatList = list;
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public ChatModel getItem(int position) {
        return mChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return 4;
    }
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatModel item=mChatList.get(position);
        switch (item.getType()) {
            case TYPE_LEFT_TEXT:
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(),
                            R.layout.item_chat_left_text, null);
                }
                if(item.getContent().contains(".pdf"))
                {
                    String name=Uri.parse(item.getContent()).getLastPathSegment().toString();
                    ((TextView) convertView.findViewById(R.id.tv_text))
                            .setText(name+"\n"+"Click to open");
                }
                else {
                    ((TextView) convertView.findViewById(R.id.tv_text))
                            .setText(item.getContent());
                }
                ((TextView) convertView.findViewById(R.id.dateView))
                        .setText(item.getTime());
                if(item.getDate() !=null)
                {
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(item.getDate());
                }
                else
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText("");
                if(item.getVisibility()==4)
                {
                    ((ImageView)convertView.findViewById(R.id.send_status))
                            .setImageResource(R.drawable.fail);
                }
                if(item.getVisibility()!=4)
                {
                    ((ImageView)convertView.findViewById(R.id.send_status))
                            .setImageResource(R.drawable.send);
                }
                if(item.getVisibility()==1){
                    ((ProgressBar)convertView.findViewById(R.id.simpleProgressBar))
                            .setVisibility(View.VISIBLE);
                }
                else  ((ProgressBar)convertView.findViewById(R.id.simpleProgressBar))
                        .setVisibility(View.GONE);
                break;
            case TYPE_LEFT_IMG:
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(),
                            R.layout.item_chat_left_img, null);
                }

                    ((ImageView) convertView.findViewById(R.id.iv_img))
                            .setImageBitmap(BitmapFactory.decodeFile(item.getContent()));
                ((TextView) convertView.findViewById(R.id.dateView))
                        .setText(item.getTime());
                if(item.getDate() !=null)
                {
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(item.getDate());
                }
                else
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(" ");
                if(item.getVisibility()==4)
                {
                    ((ImageView)convertView.findViewById(R.id.send_status))
                            .setImageResource(R.drawable.fail);
                }
                if(item.getVisibility()!=4)
                {
                    ((ImageView)convertView.findViewById(R.id.send_status))
                            .setImageResource(R.drawable.send);
                }
                if(item.getVisibility()==1)
                {
                    ((ProgressBar)convertView.findViewById(R.id.simpleProgressBar))
                            .setVisibility(View.VISIBLE);
                }
                else  ((ProgressBar)convertView.findViewById(R.id.simpleProgressBar))
                        .setVisibility(View.GONE);
                break;
            case TYPE_RIGHT_TEXT:
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(),
                            R.layout.item_chat_right_text, null);
                }
                if(item.getContent().startsWith("http"))
                {
                    String name=Uri.parse(item.getContent()).getLastPathSegment().toString();
                    ((TextView) convertView.findViewById(R.id.tv_text))
                            .setText(name+"\n"+"Click to download");
                }
                else if (item.getContent().endsWith(".pdf"))
                {
                    String name=Uri.parse(item.getContent()).getLastPathSegment().toString();
                ((TextView) convertView.findViewById(R.id.tv_text))
                        .setText(name+"\n"+"Click to open");
                }
                else {
                    ((TextView) convertView.findViewById(R.id.tv_text))
                            .setText(item.getContent());
                }
                ((TextView) convertView.findViewById(R.id.dateView))
                        .setText(item.getTime());
                if(item.getDate() !=null)
                {
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(item.getDate());
                }
                else
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText("");
                if(item.getVisibility()==1)
                {
                    ((ProgressBar) convertView.findViewById(R.id.simpleProgressBar)).setVisibility(View.VISIBLE);
                }
                else  ((ProgressBar) convertView.findViewById(R.id.simpleProgressBar)).setVisibility(View.GONE);
                break;
            case TYPE_RIGHT_IMG:
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(),
                            R.layout.item_chat_right_img, null);
                }
                if(item.getContent().startsWith("http"))
                {
                    ((ImageView) convertView.findViewById(R.id.iv_img))
                            .setImageResource(R.drawable.click_todownload);
                }
                else {
                    ((ImageView) convertView.findViewById(R.id.iv_img))
                            .setImageBitmap(BitmapFactory.decodeFile(item.getContent()));
                }
                ((TextView) convertView.findViewById(R.id.tv_time))
                        .setText(item.getTime());
                if(item.getDate() !=null)
                {
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(item.getDate());
                }
                else
                    ((TextView) convertView.findViewById(R.id.tv_date))
                            .setText(" ");
                if(item.getVisibility()==1)
                {
                    ((ProgressBar) convertView.findViewById(R.id.simpleProgressBar)).setVisibility(View.VISIBLE);
                }
                else  ((ProgressBar) convertView.findViewById(R.id.simpleProgressBar)).setVisibility(View.GONE);
                break;
        }
        return convertView;
    }
    public void updateAdapter(List<ChatModel> updateList ) {
        mChatList=updateList;
        notifyDataSetChanged();
    }
    public void addMsg(ChatModel msg){
        mChatList.add(msg);
        notifyDataSetChanged();
    }
    public void updateMsg(int position,ChatModel msg){
        mChatList.add(position,msg);
        notifyDataSetChanged();
    }
    public void dateCheker(){

    }
    public void addList(List<ChatModel> list){
        if(list.size()!=0) {
            mChatList.addAll(list);
            notifyDataSetChanged();
        }
    }
}

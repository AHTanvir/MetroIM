package anwar.metroim.service;

import java.io.UnsupportedEncodingException;
import java.sql.RowId;

/**
 * Created by anwar on 12/5/2016.
 */

public interface imanager {
    void FileUpload(final String path, final String phone, final String type, final int position,final String RowId);
    public boolean isNetworkConnected();
    public String UpdateContacts() throws Exception;
   public void exit();
    public String getUserUpdateInfo() throws UnsupportedEncodingException;
    public String sendMessage(String to,String mType,String message,String RowId) throws UnsupportedEncodingException;
    public String updateInfo(String para)throws UnsupportedEncodingException;
    public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException;
    public String signUpUser(String nameText,String emailText,String passwordText,String deptText,String batchText,String idText,String phoneText)throws UnsupportedEncodingException;
    String donorOperation(String param)throws UnsupportedEncodingException;
    String getLastSeen() throws UnsupportedEncodingException;
    public void fileDownload(final String url,final String phone,final String rowId,final int position);
    public String test();
    String getBackupEmail();
    public String send_Sms(String phone);
    public String resetPassword(String Phone) throws UnsupportedEncodingException;
}

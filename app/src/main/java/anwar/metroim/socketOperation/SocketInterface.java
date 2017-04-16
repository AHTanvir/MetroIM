package anwar.metroim.socketOperation;

/**
 * Created by anwar on 12/3/2016.
 */

public interface SocketInterface {
    public String sendHttpRequest(String params);
    public int startListening(int port);
    public void stopListening();
    public void exit();
    public int getListeningPort();
   String sendHttpFileUploadRequest(String selectedPath);
    String sendHttpFileDownloadRequest(String durl);
    String sendSms(String phoneNumber, String message);

}

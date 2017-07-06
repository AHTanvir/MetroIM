package anwar.metroim.socketOperation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import anwar.metroim.LocalHandeler.DatabaseHandler;
import anwar.metroim.MessageInfo;
import anwar.metroim.service.*;

import static anwar.metroim.service.MetroImservice.TAKE_MESSAGE;

/**
 * Created by anwar on 12/3/2016.
 */

public class socketConnection implements SocketInterface {
    private static final String AUTHENTICATION_SERVER_ADDRESS = "http://192.168.43.95/metroim/index.php"; //TODO change to your WebAPI Address
    private int listeningPort=0;
    private static final String HTTP_REQUEST_FAILED = "0";
    private Socket client=null;
    private Context context;
    private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();
    private ServerSocket serverSocket = null;
    private boolean listening;
    private Handler h=new Handler();
    private DatabaseHandler db;
    private class ReceiveConnection extends Thread {
        Socket clientSocket = null;
        public ReceiveConnection(Socket socket)
        {
            this.clientSocket = socket;
            socketConnection.this.sockets.put(socket.getInetAddress(), socket);
        }

        @Override
        public void run() {
            try {
                System.out.println("socketServer");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    if (inputLine.equals("exit") == false)
                    {
                        final String i=inputLine;
                        //appManager.messageReceived(inputLine);
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("-handeler port="+listeningPort);
                                //new MetroImservice().t();
                                urlDecode(i);
                            }
                        });
                    }
                    else
                    {
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        socketConnection.this.sockets.remove(clientSocket.getInetAddress());
                    }
                }
                System.out.println("socketServer");

            } catch (IOException e) {
              
            }
        }
    }

    public socketConnection(imanager ma_nager) {

    }


    public String sendHttpRequest(String params)
    {
        URL url;
        String result = new String();
        try
        {
            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            PrintWriter out = new PrintWriter(connection.getOutputStream());

            out.println(params);
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);
            }
            in.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            result = HTTP_REQUEST_FAILED;
        }

        return result;
    }

    public String sendHttpFileUploadRequest(String selectedPath) {
        boolean successful=false;
        String UPLOAD_URL= "http://192.168.43.95/metroim/upload.php";
       int serverResponseCode=200;
            String fileName =selectedPath.replace(" ","_");
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize =1*1024*1024;
            File sourceFile = new File(selectedPath);
       int len=(int)sourceFile.length()/1024;
            if (!sourceFile.isFile()) {
                return null;
            }

            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
              if(len>10000)
                {
                    System.setProperty("http.keepAlive", "false");
                    conn.setChunkedStreamingMode(1024*1024);
                }
                else
                    conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("myFile", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = conn.getResponseCode();
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (serverResponseCode == 200) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                } catch (IOException ioex) {
                }
                return sb.toString();
            }else {
                return null;
            }

    }
    public String sendSms(String phoneNumber, String message){
        StringBuilder response = new StringBuilder();
        try {
            String appKey = "6316aff2-529d-42e1-a364-ade9818d4697";
            String appSecret = "SPwYt1VKAUqSvY2B5NhZpA==";
            URL url = new URL("https://messagingapi.sinch.com/v1/sms/" + phoneNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            String userCredentials = "application\\" + appKey + ":" + appSecret;
            byte[] encoded = Base64.encodeBase64(userCredentials.getBytes());
            String basicAuth = "Basic " + new String(encoded);
            connection.setRequestProperty("Authorization", basicAuth);
            String postData = "{\"Message\":\"" + message + "\"}";
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ( (line = br.readLine()) != null)
                response.append(line);
            br.close();
            os.close();
            System.out.println("Sms"+response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.length()==0)
            return "0";
        else
            return response.toString();
    }
    public String sendHttpFileDownloadRequest(String durl){
        boolean successful=false;
        URLConnection ucon=null;
        HttpURLConnection connection=null;
        InputStream inputStream=null;
        FileOutputStream fileOutputStream=null;
        int contentLength=0,counter=0;
       String StoragePath=null;
        File sdCard = Environment.getExternalStorageDirectory();
        String path= Uri.parse(durl).getLastPathSegment().toString();
        if(path.contains(".pdf"))
        {
            StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/Document/";
        }
        else if(path.contains(".jpg"))
        {
            StoragePath=sdCard.getAbsolutePath().toString()+"/MetroIM/Received/images/";
        }
        File file = new File(StoragePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {

            StoragePath=StoragePath+path;
            URL url = new URL(durl);
           fileOutputStream = new FileOutputStream(new File(StoragePath));
            long startTime = System.currentTimeMillis();
            ucon = url.openConnection();
            contentLength=ucon.getContentLength();
           inputStream= ucon.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len1);
                counter=counter+len1;
            }
            successful=true;
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (ucon!=null) {
            }
            try {
                if (inputStream !=null) {
                    inputStream.close();
                }
                if (fileOutputStream!=null) {
                    fileOutputStream.close();
                }
            }catch (IOException ee){
                ee.printStackTrace();
            }
        }
        if(successful)
            return StoragePath;
        else return null;
    }
    public int startListening(int portNo,Context context)
    {
        listening = true;

        try {
            serverSocket = new ServerSocket(portNo);
            this.listeningPort = portNo;
            this.context=context;
        } catch (IOException e) {
            //e.printStackTrace();
            this.listeningPort = 0;
            return 0;
        }

        while (listening) {
            try {
                new ReceiveConnection(serverSocket.accept()).start();
            } catch (IOException e) {
                //e.printStackTrace();
                return 2;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e("Exception server socket", "Exception when closing server socket");
            return 3;
        }


        return 1;
    }


    public void stopListening()
    {
        this.listening = false;
    }

    public void exit()
    {
        for (Iterator<Socket> iterator = sockets.values().iterator(); iterator.hasNext();)
        {
            Socket socket = (Socket) iterator.next();
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e)
            {
            }
        }

        sockets.clear();
        this.stopListening();
    }


    public int getListeningPort() {

        return this.listeningPort;
    }
    public String sendDirectMsg(String to,String sendDate,String msgType,String msg){
        /*this.MessageReceived(jsonObj.getString("sfrom"),jsonObj.getString("sentDt"),
                jsonObj.getString("messagesType"),jsonObj.getString("messageText"));*/
        try {
            String data= URLEncoder.encode(to, "UTF-8")+"&"+
                    URLEncoder.encode(sendDate, "UTF-8")+"&"+
                    URLEncoder.encode(msgType, "UTF-8")+"&"+
                    URLEncoder.encode(msg, "UTF-8");
            client=new Socket(MessageInfo.frndIP,Integer.parseInt(MessageInfo.frndPort));
            PrintStream outt = new PrintStream(client.getOutputStream());
            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            outt.println(data);
            client.close();
            outt.close();
            in.close();
            System.out.println("close");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "0";
        }
        return "1";
    }
    private void urlDecode(String msg){
        db=new DatabaseHandler(this.context);
       try {
            String d[]=msg.split("&");
           String []name=db.getContactName(d[0]);
           db.addMessage(URLDecoder.decode(d[0],"UTF-8"),URLDecoder.decode(d[3],"UTF-8"),
                   URLDecoder.decode(d[1],"UTF-8"),"from",URLDecoder.decode(d[2],"UTF-8"),"0");
           Intent i=new Intent(TAKE_MESSAGE);
           i.putExtra(MessageInfo.FROM,URLDecoder.decode(d[0],"UTF-8"));
           i.putExtra(MessageInfo.SENDERNAME,name[0]);
           i.putExtra(MessageInfo.SENDATE,URLDecoder.decode(d[1],"UTF-8"));
           i.putExtra(MessageInfo.MESSAGE_TYPE,URLDecoder.decode(d[2],"UTF-8"));
           i.putExtra(MessageInfo.MESSAGE_LIST,URLDecoder.decode(d[3],"UTF-8"));
           this.context.sendBroadcast(i);
       /*     new MetroImservice().MessageReceived(URLDecoder.decode(d[0],"UTF-8"),URLDecoder.decode(d[1],
                    "UTF-8"),URLDecoder.decode(d[2],"UTF-8"),URLDecoder.decode(d[3],"UTF-8"));*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

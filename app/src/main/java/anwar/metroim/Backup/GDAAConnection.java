package anwar.metroim.Backup;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by anwar on 4/4/2017.
 */

public class GDAAConnection implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private dbBackup backup;
    private ConnectionCallbacks callbacks;
    private GoogleApiClient googleApiClient;
    private boolean conStatus=false;
    private final String GDID = "gdid";
    static final String MIME_FLDR = "application/vnd.google-apps.folder";
    private final String T = "titl";
    private final String MIME = "mime";
   // private dbBackup dBinterface=new dbBackup();
    public boolean ConnectToDrive(Context context, String email,dbBackup backup){
        this.callbacks=(ConnectionCallbacks)backup;
      if(email !=null)
      {
          try {
              if (googleApiClient == null) {
                  // Create the API client and bind it to an instance variable.
                  // We use this instance as the callback for connection and connection
                  // failures.
                  // Since no account name is passed, the user is prompted to choose.
                  googleApiClient= new GoogleApiClient.Builder(context)
                          .addApi(Drive.API)
                          .addScope(Drive.SCOPE_FILE)
                          .addScope(Drive.SCOPE_APPFOLDER)
                          .addConnectionCallbacks(this)
                          .addOnConnectionFailedListener(this)
                          .setAccountName(email)
                          .build();
              }
              // Connect the client. Once connected, the camera is launched.
              googleApiClient.connect();
          }catch (Exception e){
              System.out.println(" Goo errror--------------->"+e);
          }
          return true;
      }
      else return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        this.callbacks.Connected();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.callbacks.Failed();
    }
    public ArrayList<ContentValues> search(String prnId, String titl, String mime) {
        ArrayList<ContentValues> gfs = new ArrayList<>();
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            try {
            // add query conditions, build query
            ArrayList<Filter> fltrs = new ArrayList<>();
            if (prnId != null) {
                if (prnId.equalsIgnoreCase("root")) {
                    fltrs.add(Filters.in(SearchableField.PARENTS, Drive.DriveApi.getRootFolder(googleApiClient).getDriveId()));
                } else if (prnId.equalsIgnoreCase("appfolder")) {
                    fltrs.add(Filters.in(SearchableField.PARENTS, Drive.DriveApi.getAppFolder(googleApiClient).getDriveId()));
                } else {
                    fltrs.add(Filters.in(SearchableField.PARENTS, DriveId.decodeFromString(prnId)));
                }
            }
            if (titl != null) fltrs.add(Filters.eq(SearchableField.TITLE, titl));
            if (mime != null) fltrs.add(Filters.eq(SearchableField.MIME_TYPE, mime));
            Query qry = new Query.Builder().addFilter(Filters.and(fltrs)).build();

            // fire the query
            DriveApi.MetadataBufferResult rslt = Drive.DriveApi.query(googleApiClient, qry).await();
            if (rslt.getStatus().isSuccess()) {
                MetadataBuffer mdb = null;
                try {
                    mdb = rslt.getMetadataBuffer();
                    for (Metadata md : mdb) {
                        if (md == null || !md.isDataValid() || md.isTrashed()) continue;
                        gfs.add(newCVs(md.getTitle(), md.getDriveId().encodeToString(), md.getMimeType()));
                    }
                } finally {
                    if (mdb != null) mdb.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GDaa excep" + e);
        }
    }else System.out.println("GDaa not s");
        return gfs;
    }
    public String createFolder(String prnId, String titl) {
        DriveId dId = null;
        if (googleApiClient != null && googleApiClient.isConnected() && titl != null) try {
            DriveFolder pFldr;
            if (prnId != null) {
                if (prnId.equalsIgnoreCase("root")) {
                    pFldr =  Drive.DriveApi.getRootFolder(googleApiClient);
                } else if (prnId.equalsIgnoreCase("appfolder")) {
                    pFldr =  Drive.DriveApi.getAppFolder(googleApiClient);
                } else {
                    pFldr = Drive.DriveApi.getFolder(googleApiClient, DriveId.decodeFromString(prnId));
                }
            } else
                pFldr = Drive.DriveApi.getRootFolder(googleApiClient);
            if (pFldr == null) return null; //----------------->>>
            MetadataChangeSet meta;
            meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType("application/vnd.google-apps.folder").build();
            DriveFolder.DriveFolderResult r1 = pFldr.createFolder(googleApiClient, meta).await();
            DriveFolder dFld = (r1 != null) && r1.getStatus().isSuccess() ? r1.getDriveFolder() : null;
            if (dFld != null) {
                DriveResource.MetadataResult r2 = dFld.getMetadata(googleApiClient).await();
                if ((r2 != null) && r2.getStatus().isSuccess()) {
                    dId = r2.getMetadata().getDriveId();
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        return dId == null ? null : dId.encodeToString();
    }
    /************************************************************************************************
     * create file in GOODrive
     * @param prnId parent's ID, (null or "root") for root
     * @param titl  file name
     * @param mime  file mime type
     * @param file  file (with content) to create
     * @return file id  / null on fail
     */
 /* static String createFile(String prnId, String titl, String mime, File file) {
    DriveId dId = null;
    if (mGAC != null && mGAC.isConnected() && titl != null && mime != null && file != null) try {
      DriveFolder pFldr = (prnId == null || prnId.equalsIgnoreCase("root")) ?
        Drive.DriveApi.getRootFolder(mGAC) :
        Drive.DriveApi.getFolder(mGAC, DriveId.decodeFromString(prnId));
      if (pFldr != null) {
        DriveContents cont=null;
        try {
          FileInputStream fis = new FileInputStream(file);
          OutputStream outputStream = cont.getOutputStream();

          // Transfer bytes from the inputfile to the outputfile
          byte[] buffer = new byte[1024];
          int length;
          while ((length = fis.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        }catch (Exception e){e.printStackTrace();}
        MetadataChangeSet meta = new Builder().setTitle("database_backup.db").setMimeType("application/db").build();
        DriveFileResult r1 = pFldr.createFile(mGAC, meta, cont).await();
        DriveFile dFil = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveFile() : null;
        if (dFil != null) {
          MetadataResult r2 = dFil.getMetadata(mGAC).await();
          if (r2 != null && r2.getStatus().isSuccess()) {
            dId = r2.getMetadata().getDriveId();
          }
        }
      }
    } catch (Exception e) { UT.le(e); }
    return dId == null ? null : dId.encodeToString();
  }*/
    public String createFile(String prnId, String titl, String mime, File file) {
        String fileName= Uri.parse(file.toString()).getLastPathSegment();
        DriveId dId = null;
        if (googleApiClient != null && googleApiClient.isConnected() && titl != null && mime != null && file != null) try {
            DriveFolder pFldr = (prnId == null || prnId.equalsIgnoreCase("root")) ?
                    Drive.DriveApi.getRootFolder(googleApiClient) :
                    Drive.DriveApi.getFolder(googleApiClient, DriveId.decodeFromString(prnId));
            if (pFldr != null) {
                DriveContents cont = file2Cont(null, file);
                MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(fileName).setMimeType("application/csv").build();
                DriveFolder.DriveFileResult r1 = pFldr.createFile(googleApiClient, meta, cont).await();
                DriveFile dFil = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveFile() : null;
                if (dFil != null) {
                    DriveResource.MetadataResult r2 = dFil.getMetadata(googleApiClient).await();
                    if (r2 != null && r2.getStatus().isSuccess()) {
                        dId = r2.getMetadata().getDriveId();
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dId == null ? null : dId.encodeToString();
    }
    /************************************************************************************************
     * get file contents
     * @param id file driveId
     * @return file's content  / null on fail
     */
    public byte[] read(String id) {
        byte[] buf = null;
        if (googleApiClient != null && googleApiClient.isConnected() && id != null){
            try {
                DriveFile df = Drive.DriveApi.getFile(googleApiClient, DriveId.decodeFromString(id));
                DriveApi.DriveContentsResult rslt = df.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
                if ((rslt != null) && rslt.getStatus().isSuccess()) {
                    DriveContents cont = rslt.getDriveContents();
                    buf =is2Bytes(cont.getInputStream());
                    cont.discard(googleApiClient);
                      // or cont.commit();  they are equiv if READONLY
                }
            } catch (Exception e) {e.printStackTrace(); }
        }
        return buf;
    }
    /************************************************************************************************
     * update file in GOODrive
     * @param drvId file  id
     * @param titl  new file name (optional)
     * @param mime  new mime type (optional, "application/vnd.google-apps.folder" indicates folder)
     * @param file  new file content (optional)
     * @return success status
     */
    public boolean update(String drvId, String titl, String mime, String desc, File file) {
        Boolean bOK = false;
        if (googleApiClient != null && googleApiClient.isConnected() && drvId != null) try {
            MetadataChangeSet.Builder mdBd = new MetadataChangeSet.Builder();
            if (titl != null) mdBd.setTitle(titl);
            if (mime != null) mdBd.setMimeType(mime);
            if (desc != null) mdBd.setDescription(desc);
            MetadataChangeSet meta = mdBd.build();

            if (mime != null &&MIME_FLDR.equals(mime)) {
                DriveFolder dFldr = Drive.DriveApi.getFolder(googleApiClient, DriveId.decodeFromString(drvId));
                DriveResource.MetadataResult r1 = dFldr.updateMetadata(googleApiClient, meta).await();
                bOK = (r1 != null) && r1.getStatus().isSuccess();

            } else {
                DriveFile dFile = Drive.DriveApi.getFile(googleApiClient, DriveId.decodeFromString(drvId));
                DriveResource.MetadataResult r1 = dFile.updateMetadata(googleApiClient, meta).await();
                if ((r1 != null) && r1.getStatus().isSuccess() && file != null) {
                    DriveApi.DriveContentsResult r2 = dFile.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
                    if (r2.getStatus().isSuccess()) {
                        DriveContents cont = file2Cont(r2.getDriveContents(), file);
                        Status r3 = cont.commit(googleApiClient, meta).await();
                        bOK = (r3 != null && r3.isSuccess());
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        return bOK;
    }
    /************************************************************************************************
     * trash file in GOODrive
     * @param drvId file  id
     * @return success status
     */
    public boolean trash(String drvId) {
        Boolean bOK = false;
        if (googleApiClient != null && googleApiClient.isConnected() && drvId != null) try {
            DriveId dId = DriveId.decodeFromString(drvId);
            DriveResource driveResource;
            if (dId.getResourceType() == DriveId.RESOURCE_TYPE_FOLDER) {
                driveResource = Drive.DriveApi.getFolder(googleApiClient, dId);
            } else {
                driveResource = Drive.DriveApi.getFile(googleApiClient, dId);
            }
            Status rslt = driveResource == null ? null : driveResource.trash(googleApiClient).await();
            bOK = rslt != null && rslt.isSuccess();
        } catch (Exception e) {e.printStackTrace();}
        return bOK;
    }

    /************************************************************************************************
     * create file/folder in GOODrive
     * @param prnId parent's ID, (null or "root") for root
     * @param titl  file name
     * @param mime  file mime type
     * @param file  file (with content) to create
     * @return intent sender/ null on fail
     */
    public IntentSender createFileAct(String prnId, String titl, String mime, File file) {
        if (googleApiClient != null && googleApiClient.isConnected() && titl != null && mime != null && file != null) try {
            DriveFolder pFldr = (prnId == null || prnId.equalsIgnoreCase("root")) ?
                    Drive.DriveApi.getRootFolder(googleApiClient) :
                    Drive.DriveApi.getFolder(googleApiClient, DriveId.decodeFromString(prnId));
            if (pFldr != null) {
                DriveContents dc = file2Cont(null, file);
                MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();

                return Drive.DriveApi.newCreateFileActivityBuilder()
                        .setActivityStartFolder(pFldr.getDriveId())
                        .setInitialMetadata(meta).setInitialDriveContents(dc)
                        .build(googleApiClient);
            }
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }
    static String getId(Intent data){
        return ((DriveId)data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID)).encodeToString();
    }

    /************************************************************************************************
     * pick a file in GOODrive
     * @param prnId parent's ID, (null or "root") for root
     * @param mimes  file mime types
     * @return intent sender/ null on fail
     */
    public IntentSender pickFile(String prnId, String[] mimes) {
        if (googleApiClient != null && googleApiClient.isConnected() && mimes != null) try {
            DriveFolder pFldr = (prnId == null || prnId.equalsIgnoreCase("root")) ?
                    Drive.DriveApi.getRootFolder(googleApiClient) :
                    Drive.DriveApi.getFolder(googleApiClient, DriveId.decodeFromString(prnId));
            if (pFldr != null) {
                return Drive.DriveApi.newOpenFileActivityBuilder()
                        .setActivityStartFolder(pFldr.getDriveId())
                        .setMimeType(mimes)
                        .build(googleApiClient);
            }
        } catch (Exception e) { e.printStackTrace();}
        return null;
    }

    /**
     * FILE / FOLDER type object inquiry
     *
     * @param cv oontent values
     * @return TRUE if FOLDER, FALSE otherwise
     */
   public boolean isFolder(ContentValues cv) {
        String gdId = cv.getAsString(GDID);
        DriveId dId = gdId != null ? DriveId.decodeFromString(gdId) : null;
        return dId != null && dId.getResourceType() == DriveId.RESOURCE_TYPE_FOLDER;
    }

    private  DriveContents file2Con(DriveContents cont, File file) {
        if (file == null) return null;  //--------------------->>>
        if (cont == null) {
            DriveApi.DriveContentsResult r1 = Drive.DriveApi.newDriveContents(googleApiClient).await();
            cont = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveContents() : null;
        }
        if (cont != null) try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream outputStream =cont.getOutputStream();

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception ignore)  {ignore.printStackTrace();}
        return cont;   //--------------------->>>
    }
    private  DriveContents file2Cont(DriveContents cont, File file) {
        if (file == null) return null;  //--------------------->>>
        if (cont == null) {
            DriveApi.DriveContentsResult r1 = Drive.DriveApi.newDriveContents(googleApiClient).await();
            cont = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveContents() : null;
        }
        if (cont != null) try {
            OutputStream oos = cont.getOutputStream();
            if (oos != null) try {
                FileInputStream is = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int length;
                while((length=is.read(buf))>0)
                {
                    oos.write(buf,0,length);
                }
            }catch (FileNotFoundException e){e.printStackTrace();
                System.out.println("-----------------------<-->"+e);}
            finally { oos.close();}
            return cont; //++++++++++++++++++++++++++++++>>>
        } catch (Exception ignore)  {
            System.out.println("-----------------------<-->"+ignore);
            ignore.printStackTrace();
        }
        return null;   //--------------------->>>
    }
    private static File cchFile(String flNm) {
        File cche = Environment.getExternalStorageDirectory();
        return (cche == null || flNm == null) ? null : new File(cche.getPath() + File.separator + flNm);
    }
  public boolean str2File(String str, String name) {
      String path=Environment.getExternalStorageDirectory()+ "/MetroIm/"+name;
      File f=new File(path);
        if (str == null) return false;
        byte[] buf = str.getBytes();
        if (f== null) return false;
        BufferedOutputStream bs = null;
        try {
            bs = new BufferedOutputStream(new FileOutputStream(f));
            bs.write(buf);
        } catch (Exception e) {System.out.println("errror--------------->"+e); }
        finally {
            if (bs != null) try {
                bs.close();
            } catch (Exception e) {System.out.println("errror--------------->"+e);  }
        }
        return true;
    }
    public byte[] is2Bytes(InputStream is) {
        byte[] buf = null;
        BufferedInputStream bufIS = null;
        if (is != null) try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            bufIS = new BufferedInputStream(is);
            buf = new byte[4096];
            int cnt;
            while ((cnt = bufIS.read(buf)) >= 0) {
                byteBuffer.write(buf, 0, cnt);
            }
            buf = byteBuffer.size() > 0 ? byteBuffer.toByteArray() : null;
        } catch (Exception ignore) {}
        finally {
            try {
                if (bufIS != null) bufIS.close();
            } catch (Exception ignore) {}
        }
        return buf;
    }
    private ContentValues newCVs(String titl, String gdId, String mime) {
        ContentValues cv = new ContentValues();
        if (titl != null) cv.put(T, titl);
        if (gdId != null) cv.put(GDID, gdId);
        if (mime != null) cv.put(MIME, mime);
        return cv;
    }
    public interface ConnectionCallbacks{
        void Connected();
        void Failed();
    }
}

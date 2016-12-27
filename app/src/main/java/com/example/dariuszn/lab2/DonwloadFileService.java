package com.example.dariuszn.lab2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DariuszN on 01.12.2016.
 */

public class DonwloadFileService extends IntentService{

    private static final String ACTION_NAME = "DOWNLOAD_FILE";
    private static String URL = "";
    private HttpURLConnection httpURLConnection;
    private PostepInfo postepInfo;


    public DonwloadFileService() {
        super("");
    }

    public DonwloadFileService(String name) {
        super(name);
    }

    public static void runService(Context context, String url) {
        Intent intent = new Intent(context, DonwloadFileService.class);
        intent.setAction(ACTION_NAME);
        intent.putExtra("URL", url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("Komunikat", "Uruchomiono on Handler");
        if (intent != null) {
            String action = intent.getAction();
            String url = intent.getStringExtra("URL");

            if (ACTION_NAME.equals(action)) {
                downloadFile(url, intent);
            }
        }
    }

    private void downloadFile(String url, Intent intent) {

        Log.e("Usługa działa", "DownloadFileService downloadFile");

        postepInfo = new PostepInfo();

        try {

            java.net.URL urlL = new URL(url);
            httpURLConnection = (HttpURLConnection) urlL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

//            String fileName  = url.substring(url.lastIndexOf('/')+1, url.length());
//            String sdcardPath = Environment.getExternalStorageDirectory().getPath().toString();
//            fileName = findCorrectFileName(sdcardPath, fileName);
//            File file = new File(sdcardPath, fileName);
//            file.getParentFile().mkdirs();


            String fileName  = url.substring(url.lastIndexOf('/')+1, url.length());
            //String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String sdcardPath = System.getenv("EXTERNAL_STORAGE");
            Log.i("SdcardPath", "SD: " + sdcardPath);
            //String sdcardPath = "/storage/7A58-1EE3/motur/";
            File directory = new File(sdcardPath);
            directory.mkdirs();
            Log.i("SD Path: ", sdcardPath + "");
            fileName = findCorrectFileName(sdcardPath, fileName);
            File file = new File(sdcardPath, fileName);
            file.getParentFile().mkdirs();

            if (file.createNewFile()) {
                Log.e("FileCreate", "Plik został utworzony");
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = httpURLConnection.getInputStream();

                int fileSize = httpURLConnection.getContentLength();

                postepInfo.setInfo(0, fileSize, Constants.DOWNLOAD_START);

                byte[] buffer = new byte[1024];
                int bufferLenght = 0;

                int numberDownloadedBytes = 0;

                while ((bufferLenght = inputStream.read(buffer)) > 0 ) {
                    fileOutput.write(buffer, 0, bufferLenght);
                    numberDownloadedBytes += bufferLenght;
                    Log.i("Donwlad Complete: ", Integer.toString(numberDownloadedBytes));
                    postepInfo.setInfo(numberDownloadedBytes, fileSize, Constants.DOWNLOAD_LAST);
                    sendDownloadProgressInfo();
                }

                fileOutput.close();
                inputStream.close();

                Log.e("DownloadFile", "Plik został pobrany");

                postepInfo.setInfo(numberDownloadedBytes, fileSize, Constants.DOWNLOAD_FINISHED);
                sendDownloadProgressInfo();

                file.setReadable(true);
                file.setWritable(true);

            }
            else {
                Log.e("FileCreate", "Plik nie został utworzony");
                postepInfo.setInfo(0, 0, Constants.DOWNLOAD_ERROR);
                sendDownloadProgressInfo();
            }

            httpURLConnection.disconnect();

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "Cannot download the file");
            postepInfo.setInfo(0, 0, Constants.DOWNLOAD_ERROR);
            sendDownloadProgressInfo();
        }
    }

    private void sendDownloadProgressInfo() {
        Intent receiver = new Intent("com.example.dariusz.lab2.MainActivity");
        receiver.putExtra("downloadFileInfo", postepInfo);
        sendBroadcast(receiver);
    }

    private String findCorrectFilePath(String path) {

        int i = 2;
        String newPath = path;
        String fileName = path.substring(0, path.indexOf("."));
        String extension = path.substring(path.indexOf("."), path.length());

        //Log.i("CorrectName:", fileName);
        //Log.i("CorrectExtension:", extension);

        File file = new File(path);
        file.getParentFile().mkdirs();

        while (file.exists()) {
            newPath = fileName + "(" + i + ")" + extension;
            i++;

            file = new File(newPath);
        }

        return newPath;
    }

    private String findCorrectFileName(String path, String name) {
        int i = 2;
        String fileName = name.substring(0, name.indexOf("."));
        String extension = name.substring(name.indexOf("."), name.length());
        String newName = name;

        Log.i("CorrectName:", fileName);
        Log.i("CorrectExtension:", extension);

        File file = new File(path, name);
        file.getParentFile().mkdirs();

        while (file.exists()) {
            newName = fileName + "(" + i + ")" + extension;
            i++;

            file = new File(path, newName);
        }

        Log.e("NewName", newName);
        return newName;
    }

}

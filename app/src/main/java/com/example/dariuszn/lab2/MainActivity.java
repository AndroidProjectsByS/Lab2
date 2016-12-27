package com.example.dariuszn.lab2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private EditText urlEditText;
    private Button getInformationButton;
    private TextView sizeOfFileView;
    private TextView typeOfFileView;
    private Button downloadFileButton;
    private TextView progressInfoTextView;
    private ProgressBar downloadProgressBar;

    public static final int STORAGE_CODE_PERMISSION = 123;

    private BroadcastReceiver downloadInfoReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        addListenerToButtons();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("onReqPermissionResult", "Odebrałem żadanie o zmiannę upr");

        switch (requestCode) {

            case STORAGE_CODE_PERMISSION :
                Log.i("switch", "Rozponałem żadanie do karty pamięci");
                Log.i("GrantResultLen", Integer.toString(grantResults.length));
                Log.i("GrantResultValue", Integer.toString(grantResults[0]));

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {

                }
                else {
                    Toast.makeText(this, "Bez wymaganych uprawień aplikacja nie bedzie działać poprawnie.", Toast.LENGTH_SHORT).show();
                }
                break;

                default:
                    Log.i("switch", "Nieznany numer uprawnienia");
                    break;
        }
    }

    private void initComponents() {
        urlEditText = (EditText) findViewById(R.id.urlEditText);
        getInformationButton = (Button) findViewById(R.id.getInformationButton);
        sizeOfFileView = (TextView) findViewById(R.id.sizeOfFileView);
        typeOfFileView = (TextView) findViewById(R.id.typeOfFileView);
        downloadFileButton = (Button) findViewById(R.id.downloadFileButton);
        progressInfoTextView = (TextView) findViewById(R.id.progressInfoTextView);
        downloadProgressBar = (ProgressBar) findViewById(R.id.progressBarDownload);

        initDownloadInfoReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadInfoReceiver, new IntentFilter("com.example.dariusz.lab2.MainActivity"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadInfoReceiver);
    }

    private void initDownloadInfoReceiver() {
        downloadInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getInfoAboutDownload(intent);
            }
        };
    }



    public void getInfoAboutDownload(Intent intent) {
        PostepInfo postepInfo = (PostepInfo) intent.getExtras().get("downloadFileInfo");
        int numberDownloadedBytes = postepInfo.getmPobranychBajtow();
        int sizeOfFile = postepInfo.getmRozmiar();
        int stateOfDownload = postepInfo.getmWynik();

        updateProgressInfoTextView(numberDownloadedBytes, sizeOfFile, stateOfDownload);
    }

    private void updateProgressInfoTextView(int numberDownloadedBytes, int sizeOfFile, int stateOfDownload) {
        String message = "";
        switch (stateOfDownload) {
            case Constants.DOWNLOAD_START:
                downloadProgressBar.setMax(sizeOfFile);
                break;
            case Constants.DOWNLOAD_LAST:
                message = numberDownloadedBytes + "/ " + sizeOfFile;
                downloadProgressBar.setMax(sizeOfFile);
                downloadProgressBar.setProgress(numberDownloadedBytes);
                break;

            case Constants.DOWNLOAD_FINISHED:
                message = "Pobieraniae zakończone";
                changeDownloadProgressBarVisibility(View.INVISIBLE);
                downloadProgressBar.setProgress(0);
                break;

            case Constants.DOWNLOAD_ERROR:
                message = "Pobieranie nie powiodło się.";
                changeDownloadProgressBarVisibility(View.INVISIBLE);
                downloadProgressBar.setProgress(0);
                break;

            default:
                message = "Nieznana opcja";
                break;
        }
        progressInfoTextView.setText(message);
    }


    private void addListenerToButtons() {
        addListenerToGetInfoButton();
        addListenerToDownloadFileButton();
    }


    private void addListenerToGetInfoButton() {
        getInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInformationOfFile();
            }
        });
    }

    private void addListenerToDownloadFileButton() {
        downloadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                changeDownloadProgressBarVisibility(View.VISIBLE);
                startDownloadFile();
            }
        });
    }


    private void checkPermissions() {
        getPermisionForWriteExternalStorage();
    }

    private void getPermisionForWriteExternalStorage() {
//        if (!checkPermissionForWriteExternalStorage()) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE_PERMISSION);
//        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                  PackageManager.PERMISSION_GRANTED) {

            showDialogPermission();
            //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE_PERMISSION);
        }
    }

    public void showDialogPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apliakcja wymgaga dostępu do karty pamięci, aby morzna było pobrać plik.");
        builder.setTitle("SD Card Permission");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeRequest();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void makeRequest() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_CODE_PERMISSION);
    }

    private void changeDownloadProgressBarVisibility(int visibilityOption) {
        downloadProgressBar.setVisibility(visibilityOption);
    }

    private boolean checkPermissionForWriteExternalStorage() {
        boolean isPermitted = true;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            isPermitted = false;

        }

        return isPermitted;
    }

    private void getInformationOfFile() {
        String url = urlEditText.getText().toString();
        FileTask fileTask = new FileTask(url, sizeOfFileView, typeOfFileView);
        fileTask.execute();
    }

    private void startDownloadFile() {
        if (checkPermissionForWriteExternalStorage()) {
            String url = urlEditText.getText().toString();
            DonwloadFileService.runService(this, url);
        }
    }
}

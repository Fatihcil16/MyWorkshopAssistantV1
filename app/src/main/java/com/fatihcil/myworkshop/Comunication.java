package com.fatihcil.myworkshop;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Comunication extends AppCompatActivity {

    private final int REQUEST_CODE_SPEECH_INPUT=100;

    String address;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth;
    BluetoothSocket btSocket;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket myserver;

    Button workon,workoff,printerlon,printerloff,printeron,printeroff,allon,alloff,talk;
    TextView textView;
    private boolean isBtConnected=false;
    static final UUID myUUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);
        Intent newint=getIntent();
        address=newint.getStringExtra(MainActivity.EXTRA_ADRESS);

        workon=(Button)findViewById(R.id.workon);
        workoff=(Button)findViewById(R.id.workoff);
        printerlon=(Button)findViewById(R.id.printerlon);
        printerloff=(Button)findViewById(R.id.printerloff);
        printeron=(Button)findViewById(R.id.printeron);
        printeroff=(Button)findViewById(R.id.printeroff);
        allon=(Button)findViewById(R.id.allon);
        alloff=(Button)findViewById(R.id.alloff);
        talk=(Button)findViewById(R.id.talk);
        textView=(TextView)findViewById(R.id.textView);

        workon.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("8"); }});
        workoff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("4"); }});
        printerlon.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("7"); }});
        printerloff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("3"); }});
        printeron.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("6"); }});
        printeroff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("2"); }});
        allon.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("9"); }});
        alloff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { SentData("0"); }});
        talk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { Talk();}});

        new BTbaglan().execute();
    }

    private void Talk(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        startActivityForResult(intent,100);
    }
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 100: {
                if(resultCode == RESULT_OK && data!=null){
                    ArrayList<String> donus = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String klm=donus.get(0);
                    Log.d("Kelime","--"+klm);

                    textView.setText(klm);
                    if (klm.equals("turn on Workshop light"))
                    {SentData("8");}
                    if (klm.equals("turn off Workshop light"))
                    { SentData("4");}
                    if (klm.equals("turn on printer light"))
                    {SentData("7");}
                    if (klm.equals("turn off printer light"))
                    {SentData("3");}
                    if (klm.equals("turn on printer"))
                    {SentData("6");}
                    if (klm.equals("turn off printer"))
                    {SentData("2"); }
                    if (klm.equals("turn on everything"))
                    {SentData("9");}
                    if (klm.equals("turn off everything"))
                    {SentData("0");}
                }
                break;
            }
        }
    }




    public void SentData(String data)
    {
        if (btSocket!=null)
        {
            try {
                btSocket.getOutputStream().write(data.toString().getBytes());
            }
            catch (IOException e)
            {}
        }
    }


    void Disconnect(){
        if(btSocket !=null){
            try {
                btSocket.close();
            }catch (IOException e){
                //msg("Error");
            }
        }
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }
    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Comunication.this, "Connecting...", "Please Wait");
        }
        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();


                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                Toast.makeText(getApplicationContext(), "Connect Error", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();

                isBtConnected = true;
            }
            progress.dismiss();
        }

    }
}

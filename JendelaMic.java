package com.medantechno.arduinobluetooth;

/**
 * Created by user on 4/14/2017.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static android.R.id.progress;

/**
 * Created by user on 4/13/2017.
 */

public class JendelaMic extends AppCompatActivity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    ListView devicelist;
    private Set<BluetoothDevice> pairedDevices;
    Button btnPaired;

    TextView status_bt;
    String status_btnya;


    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mic_layout);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.US);

                }
            }
        });


        Intent newint = getIntent();
        address = newint.getStringExtra("alamatnya");



        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
            msg(address);
            new ConnectBT().execute();

            status_btnya ="Conected to :"+address;

            //promptSpeechInput();
        }else
        {

            Disconnect();
            Intent i = new Intent(JendelaMic.this,BTSetting.class);
            startActivity(i);


        }


        status_bt = (TextView) findViewById(R.id.status_bt);

        status_bt.setText(status_btnya);


        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
                //speakOut("Let's try your command boss...");
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Ucapkan perintah...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Device tidak support",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));


                    try {
                        DatabaseHandler db = new DatabaseHandler(this);
                        Command dt = db.getByKata(result.get(0));

                        String ada_kata = dt.getName().toLowerCase();
                        String kata_sumber = result.get(0).toLowerCase();



                        if (kata_sumber.equals(ada_kata))
                        {
                            Toast.makeText(getApplicationContext(), "Ada:" + ada_kata + "-code:" + dt.getCodenya(), Toast.LENGTH_LONG).show();

                            //mengirim ke blutut
                            kirim_bt(dt.getCodenya());
                            promptSpeechInput();
                        }else {
                            speakOut("Mungkin maksud anda "+ada_kata+"....");
                            Toast.makeText(getApplicationContext(),"Anda ucapkan ["+kata_sumber+"] Mungkin maksud anda ["+ada_kata+"] ....?",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"["+result.get(0).toLowerCase()+"] tidak ada didatabase. Silahkan tambahkan.",Toast.LENGTH_LONG).show();
                        speakOut(result.get(0).toLowerCase()+" not found in database boss.. Please insert.");
                        //promptSpeechInput();
                    //    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }


                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        Disconnect();
        finish();
        Intent i = new Intent(JendelaMic.this,MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Disconnect();
                finish();
                Intent i = new Intent(JendelaMic.this,MainActivity.class);
                startActivity(i);

                break;
        }
        return true;
    }



    /*text to suara *****************************************/
    @Override
    public void onDestroy()
    {
        if(tts !=null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }




    private void speakOut(String text)
    {

        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    /*text to suara *****************************************/




    private void kirim_bt(String pesan)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(pesan.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(JendelaMic.this, "Connecting...", "tunggu!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
                msg("Disconected");
            }
            catch (IOException e)
            { msg("Error");}
        }

    }



}

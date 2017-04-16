package com.medantechno.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHealthCallback;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    Button button1,button2,button3;


    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ListCommand.class);
                startActivity(i);
            }
        });



        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,BTSetting.class);
                startActivity(i);
            }
        });




        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,BTSetting.class);
                startActivity(i);
            }
        });





        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.US);
                    speakOut("Wellcome My Boss...");
                }
            }
        });

        speakOut("Wellcome Boss...");


        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {

            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }



        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        //Log.d("Insert: ", "Inserting ..");
        //db.addContact(new Command("Ravi", "9100000000"));
        //db.addContact(new Command("Srinivas", "9199999999"));
        //db.addContact(new Command("Tommy", "9522222222"));
        //db.addContact(new Command("Karthik", "9533333333"));

        //db.addContact(new Command("Dulo", "085200000"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Command> contacts = db.getAllContacts();

        for (Command cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getCodenya();
            // Writing Contacts to log
            Log.d("Name: ", log);

        }

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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            speakOut("Database Setting");
            Intent i = new Intent(MainActivity.this, ListCommand.class);
            startActivity(i);

        }

        if(id==R.id.menu_mic)
        {
            speakOut("Mic Setting...");
            Intent i = new Intent(MainActivity.this, JendelaMic.class);
            startActivity(i);
        }

        if(id==R.id.menu_bt)
        {
            speakOut("Bluetooth setting");
            Intent i = new Intent(MainActivity.this, BTSetting.class);
            //Intent i = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivity(i);

        }

        if(id == R.id.menu_exit)
        {
            speakOut("Exit boss?");
            this.finish();
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}

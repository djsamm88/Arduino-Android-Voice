package com.medantechno.arduinobluetooth;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 4/14/2017.
 */

public class ListCommand extends AppCompatActivity {

    TextView name_byid;
    EditText editName;
    EditText editCommand;
    Button goEdit;
    Button goDelete;
    Button goCreate;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dua_sisi);


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.US);
                    //speakOut("Wellcome My Boss...");
                }
            }
        });



        munculkan_all();

        //tambah baru
        goCreate = (Button) findViewById(R.id.goCreate);
        editName = (EditText) findViewById(R.id.editName);
        editCommand = (EditText) findViewById(R.id.editCommand);


        goCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = editName.getText().toString();
                String command = editCommand.getText().toString();

                info = info.trim();
                command = command.trim();

                Toast.makeText(getApplicationContext(),info+","+command,Toast.LENGTH_LONG).show();

                if(info.equals("") && command.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Isi dulu field",Toast.LENGTH_LONG).show();
                    speakOut("Field tidak boleh kosong boss..");
                }else{

                    DatabaseHandler db = new DatabaseHandler(ListCommand.this);
                    db.addContact(new Command(info, command));
                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                    speakOut("Saved boss..");
                    refresh();
                }
                //DatabaseHandler db = new DatabaseHandler(ListCommand.this);
                //db.addContact(new Command("Karthik", "9533333333"));
            }
        });

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


    private void refresh()
    {
        finish();
        overridePendingTransition(0,0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }

    private void munculkan_all()
    {
        DatabaseHandler db = new DatabaseHandler(this);

        List<Command> commands = db.getAllContacts();


        LinearLayout list_button = (LinearLayout) findViewById(R.id.left_layout);

        int i=0;
        for (Command cn : commands) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getCodenya();
            // Writing Contacts to log
            Log.d("Name: ", log);


            Button li_button = new Button(this);
            li_button.setText(cn.getName());
            //li_button.setText(cn.getCodenya());
            li_button.setTag(cn.getID());

            list_button.addView(li_button);

            li_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {
                        String idnya = view.getTag().toString();
                        Toast.makeText(getApplicationContext(), idnya, Toast.LENGTH_LONG).show();

                        munculkan_detail(idnya);



                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Data Was Deleted",Toast.LENGTH_LONG).show();
                    }

                }

            });

            i++;
        }




    }

    private void munculkan_detail(String idnya)
    {
        DatabaseHandler db = new DatabaseHandler(this);


        final Command data = db.getById(idnya);


        speakOut(data.getName());

        editName = (EditText) findViewById(R.id.editName);
        editCommand = (EditText) findViewById(R.id.editCommand);

        editName.setText(data.getName());
        editCommand.setText(data.getCodenya());

        goDelete = (Button) findViewById(R.id.goDelete);
        //goDelete.setTag(data.getID());
        goDelete.setTag(data.getID());

        goEdit = (Button) findViewById(R.id.goEdit);
        goEdit.setTag(data.getID());

        goEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    int id = Integer.parseInt(view.getTag().toString());
                    String info = editName.getText().toString().trim();
                    String com  = editCommand.getText().toString().trim();

                    if(info.equals("") && com.equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "Field jangan kosong...", Toast.LENGTH_LONG).show();
                    }else {

                        DatabaseHandler db = new DatabaseHandler(ListCommand.this);
                        db.updateContact(new Command(id, info, com));

                        Toast.makeText(getApplicationContext(), "Sukses Edit..", Toast.LENGTH_LONG).show();
                        refresh();
                    }

                }catch (Exception e)
                {

                }
            }
        });



        goDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{


                    DatabaseHandler db = new DatabaseHandler(ListCommand.this);
                    Command data = db.getContact(Integer.parseInt(view.getTag().toString()));
                    db.deleteContact(data);
                    Toast.makeText(getApplicationContext(),"Sukses delete..",Toast.LENGTH_LONG).show();
                    refresh();

                }catch (Exception e)
                {

                }


            }
        });



    }


}

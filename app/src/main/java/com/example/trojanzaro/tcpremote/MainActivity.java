package com.example.trojanzaro.tcpremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    Button bt5;
    Button bt6;
    Button btOn;
    Button btOff;
    Button btS;
    Button addAddr;
    //EditText addrTb;
    Spinner addrTb;

    File addrListFile = new File("./TCORemoteAddrList");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //addrTb = findViewById(R.id.editText);
        addrTb = findViewById(R.id.spinner);

        bt1 = findViewById(R.id.button1);
        bt2 = findViewById(R.id.button2);
        bt3 = findViewById(R.id.button3);
        bt4 = findViewById(R.id.button4);
        bt5 = findViewById(R.id.button5);
        bt6 = findViewById(R.id.button6);
        btOn = findViewById(R.id.buttonOn);
        btOff = findViewById(R.id.buttonOff);
        btS = findViewById(R.id.buttonS);
        addAddr = findViewById(R.id.buttonAdd);

        addAddr.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        btOn.setOnClickListener(this);
        btOff.setOnClickListener(this);
        btS.setOnClickListener(this);

        if(addrListFile.exists())
        {
            List<String> addrList = new ArrayList<>();
            try {
                InputStream inputStream = this.openFileInput("./TCORemoteAddrList");
                BufferedReader inList = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
                String ln = "";
                while((ln = inList.readLine()) != null)
                {
                    addrList.add(ln);
                }
                inList.close();
            } catch (IOException e) {
                printToast(e.getMessage());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addrList);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            addrTb.setAdapter(dataAdapter);
        }
        else
        {
            try {
                addrListFile.createNewFile();
            } catch (IOException e) {
                printToast(e.getMessage());
            }
        }
    }

    @Override
    public void onClick(final View v){
        if(v == addAddr)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add new Address");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputText = input.getText().toString();
                    if(!inputText.equals(""))
                    {
                        String m_Text = input.getText().toString() + "\n";
                        try {
                            FileOutputStream addrOut = new FileOutputStream(addrListFile);
                            addrOut.write(m_Text.getBytes());
                            addrOut.close();
                        } catch (IOException e) {
                            printToast(e.getMessage());
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else
        {
            new Thread(() -> command(((Button)v).getText().toString())).start();
        }
    }

    private void command(String cmd)
    {
        try {
            Socket socket = new Socket(addrTb.getSelectedItem().toString(), 1234);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            if(cmd.equals("STATUS"))
            {
                out.write(cmd.getBytes());

                StringBuilder responce = new StringBuilder();
                String ln;
                while((ln = in.readLine()) != null)
                {
                    responce.append(ln).append('\n');
                }
                printToast(responce.substring(0, responce.length()-1));
                socket.close();
            }
            else
            {
                out.write(cmd.getBytes());
                out.flush();
                out.close();
                socket.close();
            }
        }
        catch(IOException ex)
        {
            printToast(ex.getMessage());
        }
    }

    private void printToast(final String message)
    {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}

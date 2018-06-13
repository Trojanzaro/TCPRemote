package com.example.trojanzaro.tcpremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String filePath = "TCPRemoteAddrList.txt";
    File addrFile = new File(filePath);

    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    Button bt5;
    Button bt6;
    Button btOn;
    Button btOff;
    Button btS;
    Button remAddr;
    Button addAddr;
    Spinner addrTb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        remAddr = findViewById(R.id.buttonRem);

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
        remAddr.setOnClickListener(this);
        addrTb.setOnItemSelectedListener(this);
        listAppended();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(final View v){
        if(v == addAddr)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add new Address");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputText = input.getText().toString();
                    if(!inputText.equals(""))
                    {
                        String m_Text = input.getText().toString() + System.getProperty("line.separator");
                        try {
                            FileOutputStream addrOut = openFileOutput(filePath, Context.MODE_APPEND);
                            addrOut.write(m_Text.getBytes());
                            addrOut.close();
                        } catch (IOException e) {
                            printToast(e.getMessage());
                        }
                    }
                    listAppended();
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
        else if(v == remAddr)
        {
            //TODO: Fix the address removing function
            String addrToRem = addrTb.getSelectedItem().toString();
            File tempFile = new File("myTempFile.txt");
            try
            {
                FileOutputStream writerStream = openFileOutput(tempFile.getPath(), Context.MODE_APPEND);
                BufferedWriter writer  = new BufferedWriter(new OutputStreamWriter(writerStream));

                InputStreamReader inputStream = new InputStreamReader(openFileInput(filePath));
                BufferedReader reader = new BufferedReader(inputStream);

                String currentLine;
                while((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if(trimmedLine.equals(addrToRem)) continue;
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
                writer.close();
                reader.close();

                writerStream.close();
                inputStream.close();

                boolean successfulDelete = addrFile.getAbsoluteFile().delete();
                boolean successfulReplace = tempFile.renameTo(addrFile);
                System.out.println();
            }catch(IOException ex)
            {
                printToast(ex.getMessage());
            }
            listAppended();
        }
        else
        {
            new Thread(() -> command(((Button)v).getText().toString())).start();
        }
    }

    private void command(String cmd)
    {
        try {
            String address = addrTb.getSelectedItem().toString() == null ? "" : addrTb.getSelectedItem().toString();
            Socket socket = new Socket(address, 1234);
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
            printToast("Successfull command send");
        }
        catch(IOException ex)
        {
            printToast(ex.getMessage());
        }
    }

    private void listAppended()
    {
        List<String> addrList = new ArrayList<>();
        try {
            InputStreamReader inputStream = new InputStreamReader(openFileInput(filePath));
            BufferedReader inList = new BufferedReader(inputStream);
            String ln;
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

    private void printToast(final String message)
    {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}

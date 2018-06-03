package com.example.trojanzaro.tcpremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
    EditText addrTb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addrTb = findViewById(R.id.editText);

        bt1 = findViewById(R.id.button1);
        bt2 = findViewById(R.id.button2);
        bt3 = findViewById(R.id.button3);
        bt4 = findViewById(R.id.button4);
        bt5 = findViewById(R.id.button5);
        bt6 = findViewById(R.id.button6);
        btOn = findViewById(R.id.buttonOn);
        btOff = findViewById(R.id.buttonOff);
        btS = findViewById(R.id.buttonS);

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        btOn.setOnClickListener(this);
        btOff.setOnClickListener(this);
        btS.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v){
        new Thread(() -> command(((Button)v).getText().toString())).start();
    }

    private void command(String cmd)
    {
        try {
            Socket socket = new Socket(addrTb.getText().toString(), 1234);
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

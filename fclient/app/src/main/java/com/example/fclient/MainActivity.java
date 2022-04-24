package com.example.fclient;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


public class MainActivity extends AppCompatActivity implements TransactionEvents {
    ActivityResultLauncher activityResultLauncher;
    private static final String TAG = "MyActivity";


    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        Log.i(TAG, "MyLog fclient_lib");
        System.loadLibrary("native-lib");
        Log.i(TAG, "MyLog native-lib");
        System.loadLibrary("mbedcrypto");
        Log.i(TAG, "MyLog mbedcrypto_lib");    //private ActivityMainBinding binding;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    private String pin;
    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                Log.i(TAG, ex.getMessage());
            }
        }
        return pin;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        int res = initRng();
        byte[] v = randomBytes(16);


        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        //TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());



        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();

                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });
    }

    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }

    public void onButtonClick(View v)
    {
        /*byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
        byte[] enc = encrypt(key, stringToHex("098200004900000102"));
        byte[] dec = decrypt(key, enc);
        String s = new String(Hex.encodeHex(dec)).toUpperCase();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();*/
        ////Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);
        ////activityResultLauncher.launch(it);

        byte[] trd = stringToHex("9F0206000000000100");
        boolean ok = transaction(trd);

        /*new Thread(()-> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                boolean ok = transaction(trd);
                runOnUiThread(()-> {
                    Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception ex) {
                Log.i(TAG, "Uuups, you have a mistake..."+ex.getMessage());
            }
        }).start();*/


    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */

    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);
}
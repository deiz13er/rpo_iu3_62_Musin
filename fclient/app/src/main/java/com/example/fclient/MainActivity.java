package com.example.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fclient.databinding.ActivityMainBinding;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        Log.i(TAG, "MyLog fclient_lib");
        System.loadLibrary("native-lib");
        Log.i(TAG, "MyLog native-lib");
        System.loadLibrary("mbedcrypto");
        Log.i(TAG, "MyLog mbedcrypto_lib");
    }

    //private ActivityMainBinding binding;



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
        byte[] key =new byte[] {0,1,0,0,1,0,1,0,1,0,1,0,1,1,0,1};
        byte[] shifr = encrypt(key, v);
        byte[] rashifr = decrypt(key, shifr);

        Log.i(TAG, "MyLog u_u");

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
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
        byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
        byte[] enc = encrypt(key, stringToHex("098200004900000102"));
        byte[] dec = decrypt(key, enc);
        String s = new String(Hex.encodeHex(dec)).toUpperCase();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
}
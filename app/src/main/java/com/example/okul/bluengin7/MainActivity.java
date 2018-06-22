package com.example.okul.bluengin7;

import android.content.pm.PackageManager;

import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    static int sayac=0;
    static int sayac2=0;
    private static final String ARANAN_CIHAZ="A4:C1:38:77:18:A0";//"98:D3:31:FD:52:66";
    private static final String ARANAN_ID="JDY-10";
    private Boolean cihaz_durum=false;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    private BluetoothAdapter benimbluetoothadaptorum;
    private Set<BluetoothDevice> eslesencihazlar;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private RelativeLayout zemin_layout;
    int sira;
    Random rnd = new Random();
    private Camera camera;
    private boolean flashAcik;
    private boolean flashVarmı;
    Camera.Parameters params;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // take an instance of BluetoothAdapter - Bluetooth radio
        benimbluetoothadaptorum = BluetoothAdapter.getDefaultAdapter();
        if(benimbluetoothadaptorum == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Durum: Desteklenmez");

            Toast.makeText(getApplicationContext(),"Cihazınız Bluetooth'u desteklemiyor.",
                    Toast.LENGTH_LONG).show();
        } else {
            text = (TextView) findViewById(R.id.text);
            zemin_layout = (RelativeLayout) findViewById(R.id.zemin_layout);
            onBtn = (Button)findViewById(R.id.turnOn);
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Bacik(v);
                }
            });

            offBtn = (Button)findViewById(R.id.turnOff);
            offBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Bkapali(v);
                }
            });

            listBtn = (Button)findViewById(R.id.paired);
            listBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Eslesenleribul(v);
                }
            });

            findBtn = (Button)findViewById(R.id.search);
            findBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    yenicihazbul(v);
                }
            });

            myListView = (ListView)findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);

        }
        this.mHandler = new Handler();

        this.mHandler.postDelayed(m_Runnable,3000);

    }

    public void Bacik(View view){
        if (!benimbluetoothadaptorum.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth Açıldı." ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth Zaten Açık.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(benimbluetoothadaptorum.isEnabled()) {
                text.setText("Durum: Açık");
            } else {
                text.setText("Durum: Kapalı");
            }
        }
    }

    public void Eslesenleribul(View view){
        // get paired devices
        eslesencihazlar = benimbluetoothadaptorum.getBondedDevices();
        // put it's one to the adapter
        for(BluetoothDevice device : eslesencihazlar)
            BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
        Toast.makeText(getApplicationContext(),"Tanımlı Cihazlar",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver Bbulunan = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//EXTRA_DEVICE
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
                //if(ARANAN_CIHAZ.equals(device.getAddress())){
                if(ARANAN_ID.equals(device.getName())){
                    cihaz_durum=true;
                    zemin_layout.setBackgroundColor(Color.LTGRAY);
                    Toast.makeText(getApplicationContext(),"Aranan Cihaz Bulundu.",
                            Toast.LENGTH_LONG).show();
                    text.setTextColor(Color.BLUE);
                    text.setText("("+device.getName()+") Cihaz Aktif.Babaanneden sinyal alınmaktadır.");


                }
            }
        }
    };

    public void yenicihazbul(View view) {
        if (benimbluetoothadaptorum.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            benimbluetoothadaptorum.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Cihaz Tanıma Pasif",
                    Toast.LENGTH_LONG).show();
            findBtn.setText("YENİ CİHAZ ARA");
        }
        else {
            BTArrayAdapter.clear();
            benimbluetoothadaptorum.startDiscovery();
            Toast.makeText(getApplicationContext(),"Cihaz Tanıma Aktif",
                    Toast.LENGTH_LONG).show();
            findBtn.setText("VAZGEÇ");
            registerReceiver(Bbulunan, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void Bkapali(View view){
        benimbluetoothadaptorum.disable();
        text.setText("Durum: Bağlantı Kesildi.");
        Toast.makeText(getApplicationContext(),"Bluetooth Kapalı.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(Bbulunan);
    }
    private Handler mHandler;
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            yenicihazbul2();
            sayac=0;
            SystemClock.sleep(5000);
            cihazDurum();
            MainActivity.this.mHandler.postDelayed(m_Runnable, 10000);


        }
    };


    public void yenicihazbul2() {
        if (benimbluetoothadaptorum.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            benimbluetoothadaptorum.cancelDiscovery();
            //Toast.makeText(getApplicationContext(),"Cihaz Tanıma Pasif",
                    //Toast.LENGTH_LONG).show();
            findBtn.setText("YENİ CİHAZ ARA");
        }
        else {
            BTArrayAdapter.clear();
            benimbluetoothadaptorum.startDiscovery();
            //Toast.makeText(getApplicationContext(),"Cihaz Tanıma Aktif",
                   // Toast.LENGTH_LONG).show();
            findBtn.setText("VAZGEÇ");
            registerReceiver(Bbulunan, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }
    public void  cihazDurum(){

        if(cihaz_durum){
                          cihaz_durum = false;
        }else{
            //RenkSirala();
                //if (sayac2==0){
                   // sayac2++;

                //}else {
                    text.setTextColor(Color.RED);
                    text.setText("Cihaz Bağlantısı Yok.Babaanneden sinyal Alınamıyor. Dikkat. Dikkat.");
                    //Toast.makeText(getApplicationContext(), "Babaannem Kayıp.  Sinyal Alınamıyor.",
                            //Toast.LENGTH_LONG).show();
            zemin_layout.setBackgroundColor(Color.YELLOW);
                    titret_zmanla();
                    //sayac2++;

        }
    }
   /* public void titret100(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(1000);
            }
        },100);
    }*/
   public void titret_zmanla(){
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(300);
                        if(cihaz_durum== false) {
                            RenkSirala();
                        }
                        sayac++;
                        if(sayac==5)
                            timer.cancel();
                    }
                });

            }
        };

       timer.schedule(doAsynchronousTask,10,1000);

   }


    public void RenkSirala(){
        sira = rnd.nextInt(3)+1;
        if(sira == 1)
            zemin_layout.setBackgroundColor(Color.CYAN);
        if(sira == 2)
            zemin_layout.setBackgroundColor(Color.GREEN);
        if(sira == 3)
            zemin_layout.setBackgroundColor(Color.YELLOW);
        cameraAc();
        flashAc();
        flashKapat();
    }

    private void cameraAc() {
        if (camera == null) {

            camera = Camera.open();
            params = camera.getParameters();

        }
    }

    /*
     * Flash'ı açma kısmı burada
     */
    private void flashAc() {
        if (!flashAcik) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            flashAcik = true;


        }

    }

    /*
     * Flash'ı kapatma kısmı
     */
    private void flashKapat() {
        if (flashAcik) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            flashAcik = false;


        }
    }


}

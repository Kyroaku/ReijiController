package pl.reijicontroller.spamsoftware.reijicontroller;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.data;
import static android.graphics.Canvas.EdgeType.AA;

public class MainActivity extends AppCompatActivity {
    Context context;

    SeekBar mPitchBar, mRollBar, mYawBar;
    TextView mPitchText, mRollText, mYawText;
    EditText mBoxId, mBoxMem, mBoxLen, mBoxByte1, mBoxByte2, mBoxByte3, mBoxByte4, mBoxByte5, mBoxByte6;
    Button mBtnSend;

    Bluetooth mBluetooth;
    BluetoothSocket mBtSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_main);

        mBluetooth = new Bluetooth(this);
        mBtSocket = mBluetooth.Connect("XX");
        if(mBtSocket == null) {
            Toast.makeText(context, "Connecting error.", Toast.LENGTH_SHORT).show();
        }

        /**
         * Find widgets.
         */
        mPitchBar = (SeekBar)findViewById(R.id.pitch_bar);
        mRollBar = (SeekBar)findViewById(R.id.roll_bar);
        mYawBar = (SeekBar)findViewById(R.id.yaw_bar);

        mPitchText = (TextView)findViewById(R.id.pitch_text);
        mRollText = (TextView)findViewById(R.id.roll_text);
        mYawText = (TextView)findViewById(R.id.yaw_text);

        mBoxId = (EditText)findViewById(R.id.box_id);
        mBoxMem = (EditText)findViewById(R.id.box_mem);
        mBoxLen = (EditText)findViewById(R.id.box_len);

        mBoxByte1 = (EditText)findViewById(R.id.box_byte1);
        mBoxByte2 = (EditText)findViewById(R.id.box_byte2);
        mBoxByte3 = (EditText)findViewById(R.id.box_byte3);
        mBoxByte4 = (EditText)findViewById(R.id.box_byte4);
        mBoxByte5 = (EditText)findViewById(R.id.box_byte5);
        mBoxByte6 = (EditText)findViewById(R.id.box_byte6);

        mBtnSend = (Button)findViewById(R.id.btn_send);

        /**
         * Set initial values.
         */
        mPitchText.setText("Pitch (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");
        mRollText.setText("Roll (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");
        mYawText.setText("Yaw (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");

        /**
         * Set listeners.
         */
        mPitchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPitchText.setText("Pitch (" + DynamixelToDegree(progress) + ")");
                byte[] data = new byte[2];
                data[0] = (byte)progress;
                data[1] = (byte)(progress>>8);
                DXLsend((byte)0x02, (byte)0x03, (byte)0x1E, data, (byte)0x02);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mRollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRollText.setText("Roll (" + DynamixelToDegree(progress) + ")");
                byte[] data = new byte[2];
                data[0] = (byte)progress;
                data[1] = (byte)(progress>>8);
                DXLsend((byte)0x03, (byte)0x03, (byte)0x1E, data, (byte)0x02);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mYawBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mYawText.setText("Yaw (" + DynamixelToDegree(progress) + ")");
                byte[] data = new byte[2];
                data[0] = (byte)progress;
                data[1] = (byte)(progress>>8);
                DXLsend((byte)0x01, (byte)0x03, (byte)0x1E, data, (byte)0x02);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtSocket != null) {
                    byte[] data = new byte[6];
                    data[0] = (byte)Integer.parseInt(mBoxByte1.getText().toString());
                    data[1] = (byte)Integer.parseInt(mBoxByte2.getText().toString());
                    /*data[2] = (byte)Integer.parseInt(mBoxByte3.getText().toString());
                    data[3] = (byte)Integer.parseInt(mBoxByte4.getText().toString());
                    data[4] = (byte)Integer.parseInt(mBoxByte5.getText().toString());
                    data[5] =(byte)Integer.parseInt(mBoxByte6.getText().toString());*/
                    byte id = (byte)Integer.parseInt(mBoxId.getText().toString());
                    byte mem = (byte)Integer.parseInt(mBoxMem.getText().toString());
                    byte len = (byte)Integer.parseInt(mBoxLen.getText().toString());
                    DXLsend(id, (byte)0x03, mem, data, len);
                }
                else {
                     Toast.makeText(context, "No connected device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int DynamixelToDegree(int a)
    {
        return (int)((a/1024.0)*300-150);
    }

    public void DXLsend(byte id, byte ins, byte memaddr, byte data[], byte datalen)
    {
        byte buffer[] = new byte[7+datalen];
        byte checksum = 0;
        buffer[0] = buffer[1] = (byte)0xFF; //2x start byte
        checksum += (buffer[2] = id);
        checksum += (buffer[3] = (byte)(3+datalen));
        checksum += (buffer[4] = ins);
        checksum += (buffer[5] = memaddr);
        for(int i = 0; i < datalen; i++)
            checksum += (buffer[6+i] = data[i]);
        buffer[6+datalen] = (byte)(~checksum);

        mBluetooth.Send(mBtSocket, buffer);
    }
}

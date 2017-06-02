package pl.reijicontroller.spamsoftware.reijicontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/* COMMITED ! */

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    SeekBar mPitchBar, mRollBar, mYawBar;
    TextView mPitchText, mRollText, mYawText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* Find widgets */
        mPitchBar = (SeekBar)findViewById(R.id.pitch_bar);
        mRollBar = (SeekBar)findViewById(R.id.roll_bar);
        mYawBar = (SeekBar)findViewById(R.id.yaw_bar);

        mPitchText = (TextView)findViewById(R.id.pitch_text);
        mRollText = (TextView)findViewById(R.id.roll_text);
        mYawText = (TextView)findViewById(R.id.yaw_text);

        mPitchText.setText("Pitch (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");
        mRollText.setText("Roll (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");
        mYawText.setText("Yaw (" + DynamixelToDegree(mPitchBar.getProgress()) + ")");

        /* Set listeners */
        mPitchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPitchText.setText("Pitch (" + DynamixelToDegree(progress) + ")");
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
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private int DynamixelToDegree(int a)
    {
        return (int)((a/1228.0)*360);
    }
}

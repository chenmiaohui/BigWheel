package wheel.chenmh.com.bigwheel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private BitWheelView mWheelView;
    private ImageView  mStartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWheelView = (BitWheelView) findViewById(R.id.id_luckypan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mWheelView.isStart())
                {
                    mStartBtn.setImageResource(R.drawable.stop);
                    mWheelView.luckyStart(1);
                } else
                {
                    if (!mWheelView.isShouldEnd())

                    {
                        mStartBtn.setImageResource(R.drawable.start);
                        mWheelView.luckyEnd();
                    }
                }

            }
        });
    }
    public static void startActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}

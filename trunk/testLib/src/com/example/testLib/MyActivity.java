package com.example.testLib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.firefly.walkietalkie.IUploadListener;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private EditText edtStaffidSend;
    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;

    private void assignViews() {
        edtStaffidSend = (EditText) findViewById(R.id.edtStaffidSend);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        assignViews();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendStaffid = edtStaffidSend.getText().toString();
                if (!sendStaffid.equals("")) {
                    myapp.walkietalkie.recordVoice(MyActivity.this, true, sendStaffid);
                } else {
                    Toast.makeText(MyActivity.this, "Nhập staffid để gửi ghi âm", Toast.LENGTH_LONG).show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendStaffid = edtStaffidSend.getText().toString();
                myapp.walkietalkie.recordVoice(MyActivity.this, false, sendStaffid);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.walkietalkie.appIdle(true);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.walkietalkie.appIdle(false);
            }
        });

        //Set listener upload for walkietalkie
        myapp.walkietalkie.setListenerUpload(new IUploadListener() {
            @Override
            public void onUploadStatus(boolean status, boolean isStaff_online) {
                if (status) {
                    if (isStaff_online) {
                        Toast.makeText(MyActivity.this, "Upload thành công ! - Trạng thái đang online", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MyActivity.this, "Upload thành công ! - Trạng thái đang offline", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MyActivity.this, "Xảy ra lỗi trong quá trình upload !", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

package com.example.testLib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by XuanTrung on 8/26/2014.
 */
public class actRegister extends Activity {
    private EditText staffid;
    private Button btnSubmit;
    Context mContext;

    private void assignViews() {
        staffid = (EditText) findViewById(R.id.staffid);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strStaffid = staffid.getText().toString();
                if (!strStaffid.equals("")) {
                    myapp.walkietalkie.initialise(mContext, "acresta", "123456", strStaffid);
                    startActivity(new Intent(mContext, MyActivity.class));
                    finish();
                } else {
                    Toast.makeText(mContext, "Vui lòng nhập staffid", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mContext = this;
        assignViews();
    }
}

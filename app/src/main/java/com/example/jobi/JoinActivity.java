package com.example.jobi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinActivity extends AppCompatActivity {

    private String sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3YmE4Mzk3NC00ZTgyLTRhN2EtYTg4NC00NGJmNTY4NDhjOTQiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY2OTc5MDEwMCwiZXhwIjoxNjcwMzk0OTAwfQ.BSJ-zx1zV0P7uBx6nei6mq57Jqwc9GzdPXm7oLFA53M";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final Button btnCreate = findViewById(R.id.btnCreateMeeting);
        final Button btnJoin = findViewById(R.id.btnJoinMeeting);
        final EditText etMeetingId = findViewById(R.id.etMeetingId);

        btnCreate.setOnClickListener(v -> {
            // we will explore this method in the next step
            createMeeting(sampleToken);
        });

        btnJoin.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
            intent.putExtra("token", sampleToken);
            intent.putExtra("meetingId", etMeetingId.getText().toString());
            startActivity(intent);
        });

        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID);
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID);

    }
    private void createMeeting(String token) {
        // we will make an API call to VideoSDK Server to get a roomId
        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
                .addHeaders("Authorization", token) //we will pass the token in the Headers
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // response will contain `roomId`
                            final String meetingId = response.getString("roomId");

                            // starting the MeetingActivity with received roomId and our sampleToken
                            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
                            intent.putExtra("token", sampleToken);
                            intent.putExtra("meetingId", meetingId);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(JoinActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

 }
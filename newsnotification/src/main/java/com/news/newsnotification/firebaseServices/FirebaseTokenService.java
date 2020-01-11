package com.news.newsnotification.firebaseServices;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
       // super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
    }
}

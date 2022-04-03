package com.example.chatapp.Notification;

import android.content.Context;
import android.content.ContextWrapper;

public class CustomNotification extends ContextWrapper {
    public CustomNotification(Context base) {
        super(base);
    }
}

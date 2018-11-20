package com.navispeed.greg.androidmodularize.controllers;

import android.content.Context;
import android.content.Intent;
import com.navispeed.greg.androidmodularize.services.NotificationService;

public class MainController {
  public void init(Context ctx) {
    final Intent intent = new Intent(ctx, NotificationService.class);
    ctx.startService(intent);
  }
}
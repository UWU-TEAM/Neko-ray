package com.neko.tools;

import android.os.Bundle;
import android.content.Intent;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.net.Uri;
import com.v2ray.ang.ui.BaseActivity;

public class NetworkSwitcher extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            Intent RadioInfo = new Intent("android.intent.action.MAIN");
            RadioInfo.setClassName("com.android.settings", "com.android.settings.RadioInfo");
            startActivity(RadioInfo);
            finish();
        }
        catch (Exception e)
        {
            Builder ErrorMessage = new Builder(this);
            ErrorMessage.setMessage("Sorry, this feature is not available for your device.");
            ErrorMessage.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                    finish();
                }
            });
            ErrorMessage.show();
        }
    }
}
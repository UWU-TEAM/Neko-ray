package com.neko.tools;

import android.os.Bundle;
import android.content.Intent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.DialogInterface;
import android.net.Uri;
import com.neko.v2ray.ui.BaseActivity;

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
            try
            {
                Intent RadioInfo = new Intent("android.intent.action.MAIN");
                RadioInfo.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
                startActivity(RadioInfo);
                finish();
            } catch (Exception e2)
            {
                MaterialAlertDialogBuilder ErrorMessage = new MaterialAlertDialogBuilder(this);
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
}
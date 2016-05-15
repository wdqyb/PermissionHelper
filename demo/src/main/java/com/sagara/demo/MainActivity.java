package com.sagara.demo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sagara.permissionhelper.PermissionDenied;
import com.sagara.permissionhelper.PermissionGrant;
import com.sagara.permissionhelper.PermissionHelper;


public class MainActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PermissionHelper.requestPermissions(MainActivity.this, 100,false ,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS);
      }
    });
  }

  @SuppressWarnings("MissingPermission")
  @PermissionGrant(Manifest.permission.CALL_PHONE)
  public void callPhone() {
    Toast.makeText(this,"CALL_PHONE grant",Toast.LENGTH_LONG).show();
    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+456)));
  }

  @PermissionDenied(Manifest.permission.CALL_PHONE)
  public void callPhoneDenied() {
    Toast.makeText(this,"CALL_PHONE denied",Toast.LENGTH_LONG).show();
  }


  @PermissionGrant(Manifest.permission.READ_CONTACTS)
  public  void readContact(){
    Toast.makeText(this,"readContact grant",Toast.LENGTH_LONG).show();
  }

  @PermissionDenied(Manifest.permission.READ_CONTACTS)
  public  void readContactDenied(){

    Toast.makeText(this,"readContact denied",Toast.LENGTH_LONG).show();
  }

}

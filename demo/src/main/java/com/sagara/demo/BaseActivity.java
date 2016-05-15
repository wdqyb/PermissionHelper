package com.sagara.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.sagara.permissionhelper.PermissionHelper;

/**
 * Created by yibiao.qin on 16/5/9.
 */
public class BaseActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    PermissionHelper.onRequestPermissionsResult(BaseActivity.this, requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    PermissionHelper.onActivityResult(this, requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);

  }
}

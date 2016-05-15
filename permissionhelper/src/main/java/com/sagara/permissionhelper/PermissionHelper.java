package com.sagara.permissionhelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PermissionHelper {

  private static int REQUEST_CODE;
  private static boolean isForce;
  private static boolean deniedAgain;
  private static String[] sPermissions;

  public static void requestPermissions(Activity object, int requestCode, boolean isForce, String... permissions) {
    requestPermission(object, requestCode, isForce, permissions);
  }

  public static void requestPermissions(Fragment object, int requestCode, boolean isForce, String... permissions) {
    requestPermission(object, requestCode, isForce, permissions);
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  private static void requestPermission(Object object, int requestCode, boolean _isForce, @NonNull String[] permissions) {

    REQUEST_CODE = requestCode;
    isForce = _isForce;
    deniedAgain = false;
    sPermissions = permissions;

    if (isMarshmallow()) {
      ArrayList<String> grantPermissions = new ArrayList<>();
      ArrayList<String> deniedPermissions = new ArrayList<>();
      findDeniedPermissions(getActivity(object), permissions, grantPermissions, deniedPermissions);

      if (deniedPermissions.size() > 0) {
        if (deniedPermissions.size() == permissions.length) {
          requestDeniedPermission(object, requestCode, permissions);
        } else {
          requestDeniedPermission(object, requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
          executePermission(object, grantPermissions.toArray(new String[grantPermissions.size()]), PermissionGrant.class);
        }
      } else {
        executePermission(object, permissions, PermissionGrant.class);
      }
    } else {
      executePermission(object, permissions, PermissionGrant.class);
    }
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  private static void requestDeniedPermission(Object object, int requestCode, String[] permissions) {
    if (object instanceof Activity) {
      ((Activity) object).requestPermissions(permissions, requestCode);
    } else if (object instanceof Fragment) {
      ((Fragment) object).requestPermissions(permissions, requestCode);
    } else {
      throw new IllegalArgumentException(object.getClass().getName() + " is not supported!");
    }
  }

  private static void executePermission(Object object, String[] permissions, Class<? extends Annotation> annotationType) {
    if (permissions.length == 0) {
      return;
    }

    for (Method method : object.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(annotationType)) {
        for (String permission : permissions) {
          if (isEqualRequestCodeFromAnnotation(method, annotationType, permission)) {
            try {
              if (!method.isAccessible()) {
                method.setAccessible(true);
              }
              method.invoke(object, null);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
                                                int[] grantResults) {
    requestResult(activity, requestCode, permissions, grantResults);
  }

  public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                int[] grantResults) {
    requestResult(fragment, requestCode, permissions, grantResults);
  }

  private static void requestResult(Object obj, int requestCode, String[] permissions,
                                    int[] grantResults) {
    if (requestCode == REQUEST_CODE) {
      ArrayList<String> deniedPermissions = new ArrayList<>();
      ArrayList<String> grantedPermissions = new ArrayList<>();
      for (int i = 0; i < grantResults.length; i++) {
        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
          deniedPermissions.add(permissions[i]);
        } else {
          grantedPermissions.add(permissions[i]);
        }
      }
      executePermission(obj, grantedPermissions.toArray(new String[grantedPermissions.size()]), PermissionGrant.class);
      if (isForce) {
        if (deniedAgain) {
          openSetting(getActivity(obj));
        } else {
          showRequestPermissionRational(obj, deniedPermissions.toArray(new String[deniedPermissions.size()]));
        }
      } else {
        executePermission(obj, deniedPermissions.toArray(new String[deniedPermissions.size()]), PermissionDenied.class);
      }
    }
  }

  public static void onActivityResult(Object object, int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE && sPermissions != null && sPermissions.length > 0) {
      requestPermission(object, requestCode, false, sPermissions);
    }
  }

  private static boolean isEqualRequestCodeFromAnnotation(Method m, Class clazz, String permission) {
    if (clazz.equals(PermissionDenied.class)) {
      return permission.equals(m.getAnnotation(PermissionDenied.class).value());
    } else if (clazz.equals(PermissionGrant.class)) {
      return permission.equals(m.getAnnotation(PermissionGrant.class).value());
    } else if (clazz.equals(ShowRequestPermissionRationale.class)) {
      return permission.equals(m.getAnnotation(ShowRequestPermissionRationale.class).value());
    } else {
      return false;
    }
  }

  private static Activity getActivity(Object object) {
    if (object instanceof Activity) {
      return ((Activity) object);
    } else if (object instanceof Fragment) {
      return ((Fragment) object).getActivity();
    } else {
      return null;
    }
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  private static void findDeniedPermissions(Activity activity, String[] permission, ArrayList<String> grantPermissions,
                                            ArrayList<String> deniedPermissions) {

    for (String per : permission) {
      if (ActivityCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
        deniedPermissions.add(per);
      } else {
        grantPermissions.add(per);
      }
    }
  }

  private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
    if (object instanceof Activity) {
      return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
    } else if (object instanceof Fragment) {
      return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
    } else {
      return false;
    }
  }

  private static void showRequestPermissionRational(final Object object, final String[] perm) {
    if (perm.length == 0) {
      return;
    }
    AlertDialog dialog = new AlertDialog.Builder(getActivity(object))
        .setMessage("the reason you want to show ！")
        .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            deniedAgain = true;
            requestDeniedPermission(object, REQUEST_CODE, perm);
          }
        }).create();
    dialog.setCanceledOnTouchOutside(false);
    dialog.show();
  }

  private static boolean isMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  public static void openSetting(Context context) {
    final Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:" + context.getPackageName()));
    getActivity(context).startActivityForResult(i, REQUEST_CODE);
  }

}

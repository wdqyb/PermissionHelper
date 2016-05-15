# PermissionHelper
an android library for android M runtime permission
##Usage
```
public class MainActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //param isForce : true guide user to grant the permission
        PermissionHelper.requestPermissions(MainActivity.this, 100,false ,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS);
      }
    });
  }
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
```
need override

```
 @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    PermissionHelper.onRequestPermissionsResult(BaseActivity.this, requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    PermissionHelper.onActivityResult(this, requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);

  }
```
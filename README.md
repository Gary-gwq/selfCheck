# selfCheck
检测某个app是否接入SDK资源

下面是配置文件例子：

<pre>
{
  "soFile": {
  },
  "assets": {
  },
  "code_application_name": {
    "name": "com.sdk.Application"
  },
  "code_api": {
    "no_scan": {
      "com.sdk": {
        "com.sdk.Helper": "init",
        "com.bun.miitmdid.core": "InitSdk,InitSdk,InitSdk,InitSdk,InitSdk"
      }
    }
  },
  "metaData": {
    "_APPID": "1",
    "_CLIENTID": "1",
    "_CLIENTKEY": "1"
  },
  "permission": {
    "android.permission.INTERNET": "1",
    "android.permission.ACCESS_WIFI_STATE": "1",
    "android.permission.ACCESS_NETWORK_STATE": "1",
    "android.permission.READ_PHONE_STATE": "1",
    "android.permission.WRITE_EXTERNAL_STORAGE": "1",
    "android.permission.REQUEST_INSTALL_PACKAGES": "1"
  },
  "activity": {
    "com.sdk.util.PermissionUtils$PermissionActivity": "1",
    "com.sdk.ui.RKLlqActivity": "1",
    "com.sdk.user.RKLoginActivity": "1",
    "com.sdk.view.PrivacyPolicyActivity": "1",
    "com.sdk.ui.RKMaiActivity": "1"
  },
  "service": {
    "com.sdk.service.OnlineService": "1",
    "com.sdk.service.sdkService": "1"
  },
  "provider": {
    "com.sdk.util.v4.FileProvider": ".fileprovider"
  }
}
</pre>

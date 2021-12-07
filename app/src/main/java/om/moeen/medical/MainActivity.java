package om.moeen.medical;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public WebView wv;
    private static final int ICS_REQUEST_CODE = 100;
    private ValueCallback<Uri[]> mUploadMessage;
    private static final int FILECHOOSER_RESULTCODE = 2888;
    Activity activity;
    private ProgressDialog progress;
    String fileURL = null;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        progress = new ProgressDialog(MainActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading....");
        wv = (WebView) findViewById(R.id.webview);
        progress.show();
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setAppCacheEnabled(false);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        wv.loadUrl("https://www.moeen.om/");
        wv.loadUrl("https://medical.moeen.om/");

        wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        Log.i("testcase", "UA: " + wv.getSettings().getUserAgentString());

        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("TAG", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.d("TAG", "Finished loading URL: " + url);
                if (progress != null) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d("TAG", "Error: " + description);
                if (progress != null) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            }
        });

        String[] permissionsRequired = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!hasPermissions(this, permissionsRequired)) {
            ActivityCompat.requestPermissions(this, permissionsRequired, 1);
        }

        wv.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILECHOOSER_RESULTCODE);
                return true;
            }
        });
        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                fileURL = url;
                boolean isAllow = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (isAllow) {
                    createAndSaveFileFromBase64Url();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (null == this.mUploadMessage) {
            return;
        }

        Uri result = null;
        try {
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (intent == null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                } else {
                    Uri[] resultsArray = new Uri[]{Uri.parse(intent.getDataString())};
                    mUploadMessage.onReceiveValue(resultsArray);
                    mUploadMessage = null;
                }
            } else if (resultCode != RESULT_OK) {
                result = null;
                mUploadMessage.onReceiveValue(null);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == ICS_REQUEST_CODE && grantResults.length == 2 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
//        }
//    }

    public void createAndSaveFileFromBase64Url() {
//        NotificationManager mNotifyManager;
//        NotificationCompat.Builder mBuilder;

        String url = fileURL;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
        String extension = url.substring(url.lastIndexOf("."));
        String filename = "" + System.currentTimeMillis() + extension;
        File file = new File(path, filename);
        try {
            if (!path.exists())
                path.mkdirs();
            if (!file.exists())
                file.createNewFile();

//            String base64EncodedString = url.substring(url.indexOf(",") + 1);
//            byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
//            OutputStream os = new FileOutputStream(file);
//            os.write(decodedBytes);
//            os.close();

            Uri selectedUri;
            if (Build.VERSION.SDK_INT >= 24) {
                selectedUri = FileProvider.getUriForFile(MainActivity.this, getString(R.string.file_provider_authority), file);
            } else {
                selectedUri = Uri.fromFile(file);
            }

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(selectedUri, "*/*");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
//
//            mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(this);
//            mBuilder.setContentTitle("")
//                    .setContentText("Downloaded Successfully")
//                    .setContentTitle(filename)
//                    .setContentIntent(pIntent)
//                    .setSmallIcon(R.drawable.ic_launcher);
//
//            int notificationId = 85851;
//            mNotifyManager.notify(notificationId, mBuilder.build());


            NotificationManager mNotificationManager;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, "notify_001");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(selectedUri, "*/*");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText("");
            bigText.setBigContentTitle("File Downloaded");
            bigText.setSummaryText("Click to open file");

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("File Downloaded");
            mBuilder.setContentText("Click to open file");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);

            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "notify_001";
                NotificationChannel channel = new NotificationChannel(channelId,
                        "File Downloaded successfully",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            mNotificationManager.notify(0, mBuilder.build());


            Toast.makeText(MainActivity.this, "File Downloaded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("testcase", "Error writing " + file + " ERR : " + e.getMessage());
        }
    }

    private boolean checkPermission(String permission) {
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}

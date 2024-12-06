package com.policyboss.policybosspro.webview;

import static com.policyboss.policybosspro.utils.FileUtilNew.generateFileName;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.policyboss.policybosspro.BaseJavaActivity;
import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.core.model.sysncContact.POSPHorizonEntity;
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.oldWayApi.controller.dynamicController.DynamicController;
import com.policyboss.policybosspro.core.oldWayApi.controller.zoho.ZohoController;
import com.policyboss.policybosspro.core.response.APIResponse;
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonSyncDetailsWebResponse;
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantEntity;
import com.policyboss.policybosspro.core.response.raiseTicket.RaiseTicketWebDocResponse;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailEntity;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailReponse;
import com.policyboss.policybosspro.core.response.webDocResponse.CommonWebDocResponse;
import com.policyboss.policybosspro.databinding.ProgressdialogLoadingBinding;
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager;
import com.policyboss.policybosspro.paymentEliteplan.RazorPaymentEliteActivity;
import com.policyboss.policybosspro.paymentEliteplan.SyncRazorPaymentActivity;
import com.policyboss.policybosspro.utility.Utility;
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler;
import com.policyboss.policybosspro.utils.CameraGalleryManager.CameraGalleryManager;
import com.policyboss.policybosspro.utils.Constant;
import com.policyboss.policybosspro.utils.FileDownloader;
import com.policyboss.policybosspro.utils.FileUtilNew;
import com.policyboss.policybosspro.utils.FileUtils;
import com.policyboss.policybosspro.view.home.HomeActivity;

import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.bridge.WebEngageMobileBridge;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MultipartBody;

// Added File For Camera gallery : FileUtils  , Utility, Base Activity
// crop Functinality.. Launcher of cmer, gallery pdf {cropLauncher}
@AndroidEntryPoint
public class CommonWebViewActivity extends BaseJavaActivity implements BaseJavaActivity.PopUpListener, BaseJavaActivity.WebViewPopUpListener, IResponseSubcriber {

    WebView webView;
    String url = "";
    String name = "";
    String title = "";
    String dashBoardtype = "";

    String DOC_TYPE = "";
    ActivityResultLauncher<Intent> resultLauncher;

    CountDownTimer countDownTimer;
    public static boolean isActive = false;
    Toolbar toolbar;

    // LoginResponseEntity loginResponseEntity;

    UserConstantEntity userConstantEntity;

    SyncContactEntity syncContactEntity;

    POSPHorizonEntity posphorizonEntity;
    // region Camera Permission
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1800;
    private int PICK_PDF_REQUEST = 1805;
    Button btnSubmit;
    EditText etComment;
    ImageView ivUser, ivCross, ivProfile;

    @Inject
    PolicyBossPrefsManager prefManager;


    CameraGalleryManager cameraGalleryManager;   //tempRahul

    PermissionHandler permissionHandler ;

    String[] perms = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES"

    };
    //endregion

    // region Camera Declaration

    File Docfile;
    File file;
    Uri imageUri;
    private Uri cropImageUri;
    //    InputStream inputStream;
//    ExifInterface ei;
//    Bitmap bitmapPhoto = null;
    private String PHOTO_File = "";
    MultipartBody.Part part;
    HashMap<String, String> body;

    private String DocCommonID = "", DocCommonCrn = "", DocCommonType = "", Docinsurer_id = "";


    AlertDialog alertDialog;
    //endregion

    Dialog showDialog ;
    String jsonResponse_doc="";

    ActivityResultLauncher<String> galleryLauncher;
    ActivityResultLauncher<Uri> cameraLauncher;

    private ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;
    Analytics weAnalytics;
    Map<String, Object> screenData;
    @Override
    protected void onStart() {
        super.onStart();
        try {

            Analytics weAnalytics = WebEngage.get().analytics();

            screenData.put("SS ID", userConstantEntity.getPOSPNo());
            screenData.put("FBA ID", userConstantEntity.getFBAId());
            screenData.put("Name", userConstantEntity.getFullName());

            screenData.put("url", url);
            screenData.put("title", title);


            weAnalytics.screenNavigated("Common WebView Screen", screenData);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web_view);
        webView = (WebView) findViewById(R.id.webView);

        screenData = new HashMap<String, Object>();
        weAnalytics  = WebEngage.get().analytics();

        url = getIntent().getStringExtra("URL");
        name = getIntent().getStringExtra("NAME");
        title = getIntent().getStringExtra("TITLE");
        if (getIntent().getStringExtra("dashBoardtype") != null) {

            dashBoardtype = getIntent().getStringExtra("dashBoardtype");
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        showDialog = new Dialog(CommonWebViewActivity.this,R.style.Dialog);


        prefManager = new PolicyBossPrefsManager(this);

        permissionHandler = new PermissionHandler(this);

       // cameraGalleryManager = new CameraGalleryManager(this, permissionHandler);

        userConstantEntity = prefManager.getUserConstantEntity();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);




        if (isNetworkConnected()) {
            settingWebview();
            startCountDownTimer();
        } else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

        // Initialize ActivityResultLaunchers
        try{
            cropImageLauncher = registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    Uri croppedImageUri = result.getUriContent();
                    // String croppedImageFilePath = result.getUriFilePath(this); // Optional

                    handleCropImage(croppedImageUri);   //tempRahul Call cropping Image here

                } else {
                    Exception exception = result.getError();
                    Log.e("ImageCropError", "Error during cropping: " + exception);
                }
            });
        } catch (Exception e) {
            Log.e("ImageCropError", "Error during cropping: " + e.getMessage());

        }


        // region  Camera and Gallery Launcher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                startCrop(imageUri); // Start cropping if the image was captured
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                startCrop(uri); // Start cropping if an image was picked
            }
        });

        //endregion

       // pdfFileLauncher();

        setupPdfLauncher();
    }


    private void startCrop(Uri imageUri) {
        // Create CropImageOptions
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.guidelines = CropImageView.Guidelines.ON; // Show guidelines during cropping
        cropImageOptions.autoZoomEnabled = true; // Ensures the image is zoomed for better visibility
        cropImageOptions.outputCompressFormat = Bitmap.CompressFormat.JPEG; // Set output format to JPEG

        // Create CropImageContractOptions with the imageUri and cropImageOptions
        CropImageContractOptions options = new CropImageContractOptions(
                imageUri,
                cropImageOptions
        );

        // Launch the crop image activity
        cropImageLauncher.launch(options);
    }



    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                try {
                    cancelDialogMain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        countDownTimer.start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void downloadPdf(String url, String name) {
        Toast.makeText(this, "Download started..", Toast.LENGTH_LONG).show();
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + ".pdf");
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        r.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url));
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    private void settingWebview() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(false);
        settings.setSupportMultipleWindows(false);

        settings.setLoadsImagesAutomatically(true);
        settings.setLightTouchEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);


      /*  MyWebViewClient webViewClient = new MyWebViewClient(this);
        webView.setWebViewClient(webViewClient);*/
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                if (isActive)
                    showDialogMain();
                // new ProgressAsync().execute();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image

                // webView.loadUrl("javascript:myJavaScriptFunc(' 123456ume ')");
//                if(jsonResponse_doc!="") {
//                    webView.evaluateJavascript("javascript: " +
//                            "viewImageData(\"" + jsonResponse_doc + "\")", null);
//                }
                cancelDialogMain();

                super.onPageFinished(view, url);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //whatsapp plugin call.. via WEB
//                if (url != null && url.startsWith("whatsapp://")) {
//                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    return true;
//                } else
                if (url.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        String googleDocs = "https://docs.google.com/viewer?url=";
                        webView.loadUrl(googleDocs + url);
                    }
                }

                /*qacamp@gmail.com/01011980
                download policy QA user
                878769 crn
                */
                return false;
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        webView.addJavascriptInterface(new PaymentInterface(), "PaymentInterface");
        webView.addJavascriptInterface(new WebEngageMobileBridge(this), WebEngageMobileBridge.BRIDGE_NAME);

        // webView.setWebChromeClient(new WebChromeClient();
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Required functionality here
                return super.onJsAlert(view, url, message, result);
            }
        });
        // webView.setWebChromeClient(new WebChromeClient());
        Log.d("URL", url);

        if (url.endsWith(".pdf")) {

            webView.loadUrl("https://docs.google.com/viewer?url=" + url);
            //webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        } else {
            webView.loadUrl(url);
        }

        //webView.loadUrl(url);
    }




    // region Popup Method Interface


    @Override
    public void onCancelClick(Dialog dialog, View view) {

        dialog.cancel();
    }

    //endregion


    private class ProgressAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            for (int i = 0; i < 8000000; i++) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cancelDialogMain();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:

                    Log.i("BACK", "Back Triggered");
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    class PaymentInterface {
        @JavascriptInterface
        public void success(String data) {
        }

        @JavascriptInterface
        public void error(String data) {
        }
    }

    class MyJavaScriptInterface {

        @JavascriptInterface
        public void crossselltitle(String dynamicTitle) {

            getSupportActionBar().setTitle(dynamicTitle);

        }

        // region Raise Ticket Note :Below Method  Upload_doc and Upload_doc_view Called For Activity Not For Dialog
        // For Dialog We have Used "Base Activity" JavaScript (All Insurance Popup Coming from there because it will already open from CommonWebView)
        // In Menu Raise Tickets Activity :Upload_doc and Upload_doc_view comming From Below code since its Activity Page.


        @JavascriptInterface
        public void Upload_doc(String randomID) {

            galleryCamPopUp(randomID);


        }

        @JavascriptInterface
        public void rating() {

            //FeedbackHelper.showFeedbackDialog(CommonWebViewActivity.this);


        }

        @JavascriptInterface
        public void Upload_doc_view(String randomID) {

            galleryCamPopUp(randomID);

        }

        @JavascriptInterface
        public void Upload_document(String crn, String document_id, String document_type , String insurer_id ) {

            galleryCamPopUp_Common(crn,document_id,document_type,insurer_id);
            //   showAlertDialog();
        }

        //download
        @JavascriptInterface
        public void document_download(String url) {


            FileDownloader.downloadFile(CommonWebViewActivity.this, url);


        }

        //open in drive
        @JavascriptInterface
        public void document_drive(String url) {
            openInGoogleDrive(url);
        }
        private void openInGoogleDrive(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        // endregion

        @JavascriptInterface
        public void getsynccontact() {

            Intent intent = new Intent(CommonWebViewActivity.this, WelcomeSyncContactActivityKotlin.class);
            //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();


        }
        @JavascriptInterface
        public void openbrowser(String url) {

            Utility.loadWebViewUrlInBrowser(CommonWebViewActivity.this, url);

        }



        @JavascriptInterface
        public void SendShareQuotePdf(String url, String shareHtml) {

            Intent intent = new Intent(CommonWebViewActivity.this, ShareQuoteActivity.class);
            intent.putExtra(Constant.SHARE_WHATSAPP, "SHARE_WHATSAPP");
            intent.putExtra("HTML", shareHtml);
            intent.putExtra("URL", url);
            startActivity(intent);


        }

        @JavascriptInterface
        public void AddNewMotorQuote() { //Android.AddNewMotorQuote();
            //05 temp
//            Intent intent;
//            if (url.contains("buynowTwoWheeler")) {
//                intent = new Intent(CommonWebViewActivity.this, TwoWheelerQuoteAppActivity.class);
//            } else {
//                intent = new Intent(CommonWebViewActivity.this, InputQuoteBottmActivity.class);
//            }
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
        }

        @JavascriptInterface
        public void AddNewTermQuote() {//Android.AddNewTermQuote();

            //05 temp
//            Intent intent;
//            intent = new Intent(CommonWebViewActivity.this, TermSelectionActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
        }

        @JavascriptInterface
        public void RedirectToHomepage() {//Android.RedirectToHomepage();

            Intent intent = new Intent(CommonWebViewActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void callPDFCREDIT(String u) {

            webView.loadUrl("https://www.google.com");

//            startActivity(new Intent(CommonWebViewActivity.this, CommonWebViewActivity.class)
//                    .putExtra("URL", url)
//                    .putExtra("NAME", "FREE CREDIT REPORT")
//                    .putExtra("TITLE", "LIC FREE CREDIT REPORT"));
        }





        @JavascriptInterface
        public void redirectbusinessloan() {//Android.RedirectToHomepage();
            // Intent intent = new Intent(CommonWebViewActivity.this, NewbusinessApplicaionActivity.class);
            //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //   startActivity(intent);
            //  finish();
        }

        @JavascriptInterface
        public void EliteKotakRazorPay(String cust_id) {
            Intent intent = new Intent(CommonWebViewActivity.this, RazorPaymentEliteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("cust_id", cust_id);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void syncrazorpay(String transactionId) {

            if (!transactionId.equals("")) {
                getSyncPaymentDetail(transactionId);
            }

//            Intent intent = new Intent(CommonWebViewActivity.this, SyncRazorPaymentActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.putExtra("transactionId", transactionId);
//            startActivity(intent);
//            finish();
        }

        @JavascriptInterface
        public void razorpayment(String ssid) {

            if (!ssid.equals("")) {
                gethorizonpospdetails(ssid);
            }
        }
        @JavascriptInterface
        public void copyToClipboard(String str) {

            if (!str.equals("")) {
                Utility.copyTextToClipboard(str,CommonWebViewActivity.this);
            }
        }

        @JavascriptInterface
        public void shareToText(String str)
        {
            if (!str.equals("")) {
                // UTILITY.copyTextToClipboard(str,CommonWebViewActivity.this);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;

                case R.id.action_home:

                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("MarkTYPE", "FROM_HOME");

                    startActivity(intent);
                    finish();
                    return true;

                case R.id.action_raise:
                    // Toast.makeText(this,"Popup",Toast.LENGTH_SHORT).show();
                    String url = userConstantEntity.getRaiseTickitUrl() + "&mobile_no=" + userConstantEntity.getMangMobile()
                            + "&UDID=" + userConstantEntity.getUserid();
                    Log.d("URL", "Raise Ticket URL: " + url);
                    //  openWebViewPopUp(webView,  url, true, CommonWebViewActivity.this);
                    openWebViewPopUp(webView, url, true, "Raise Ticket");
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (dashBoardtype.equals("INSURANCE")) {
            getMenuInflater().inflate(R.menu.insurance_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.home_menu, menu);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        if (countDownTimer != null)
            countDownTimer.cancel();
    }


    private void getSyncPaymentDetail(String transactionId) {

        showDialogMain();
        new DynamicController(this).getSync_trascat_detail(String.valueOf(transactionId), CommonWebViewActivity.this);
    }



    private void gethorizonpospdetails(String ssid) {

        showDialogMain();
        new DynamicController(this).getsyncDetailshorizon_java(String.valueOf(ssid), CommonWebViewActivity.this);
    }

    public void galleryCamPopUp_Common(String crn, String document_id, String document_type , String insurer_id) {

        DOC_TYPE =  "COMMON";
        DocCommonCrn = crn;
        DocCommonID = document_id;

        DocCommonType = document_type;
        Docinsurer_id = insurer_id;

        PHOTO_File = "policyBoss_file" + "_" + document_id;
        Log.i("All Uploding", PHOTO_File);

        //tempRahul

        //showCameraGalleryPopUp();

        if (!hasRequiredPermissions()) {

            if (checkRationalePermission()) {
                //Show Information about why you need the permission
                requestPermissions();

            } else {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission

                //  permissionAlert(navigationView,"Need Call Permission","This app needs Call permission.");
                openPopUp(ivUser, "Need  Permission", "This app needs all permissions.", "GRANT", true);


            }
        } else {

            showCameraGalleryPopUp();
        }
    }

    // region Camera & Gallery Popup For Raise Ticket

    public void galleryCamPopUp(String randomID) {

        DOC_TYPE =  "RAISE";
        PHOTO_File = "";
        PHOTO_File = "fm_file" + "_" + randomID;
        Log.i("RAISE_TICKET Uploding", PHOTO_File);

        if (!hasRequiredPermissions()) {

            if (checkRationalePermission()) {
                //Show Information about why you need the permission
                requestPermissions();

            } else {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission

                //  permissionAlert(navigationView,"Need Call Permission","This app needs Call permission.");
                openPopUp(ivUser, "Need  Permission", "This app needs all permissions.", "GRANT", true);


            }
        } else {

            showCameraGalleryPopUp();
        }
    }

    private void startCropImageActivity(Uri imageUri) {

        ///007
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMultiTouchEnabled(true)
//                .start(this);
    }

    // region permission
    private boolean hasRequiredPermissions() {

        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int readMediaImages = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            return camera == PackageManager.PERMISSION_GRANTED &&
                    readMediaImages == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int readExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return camera == PackageManager.PERMISSION_GRANTED &&
                    readExternal == PackageManager.PERMISSION_GRANTED;
        } else {
            int writeExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return camera == PackageManager.PERMISSION_GRANTED &&
                    writeExternal == PackageManager.PERMISSION_GRANTED &&
                    readExternal == PackageManager.PERMISSION_GRANTED;
        }
    }



    private boolean checkRationalePermission() {

        boolean camera = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean readMediaImages = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES);
            return camera || readMediaImages;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean readExternal = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return camera || readExternal;
        } else {
            boolean writeExternal = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean readExternal = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return camera || writeExternal || readExternal;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                    Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
            );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.PERMISSION_CAMERA_STORAGE_CONSTANT) {
            if (grantResults.length > 0) {
                boolean cameraGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean storageGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? grantResults[1] == PackageManager.PERMISSION_GRANTED
                        : grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (cameraGranted && storageGranted) {
                    showCameraGalleryPopUp();
                } else {
                    Toast.makeText(this, "Camera and Storage permissions are required.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion

    // region Camera Dialog
    private void showCameraGalleryPopUp() {

        if (alertDialog != null && alertDialog.isShowing()) {

            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        LinearLayout lyCamera, lyGallery, lyPdf;
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.layout_cam_gallery_pdf, null);

        builder.setView(dialogView);
        alertDialog = builder.create();
        // set the custom dialog components - text, image and button
        lyCamera = (LinearLayout) dialogView.findViewById(R.id.lyCamera);
        lyGallery = (LinearLayout) dialogView.findViewById(R.id.lyGallery);
        lyPdf = (LinearLayout) dialogView.findViewById(R.id.lyPdf);
      //  lyPdf.setVisibility(View.VISIBLE);

        lyCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
                alertDialog.dismiss();

            }
        });

        lyGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                alertDialog.dismiss();

            }
        });
        lyPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                alertDialog.dismiss();

            }
        });
        alertDialog.setCancelable(true);
        alertDialog.show();
        //  alertDialog.getWindow().setLayout(900, 600);

        // for user define height and width..
    }

    private void showFileChooser() {
             // Initialize intent
//        Intent intent
//                = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        resultLauncher.launch(intent);

//        try {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("application/pdf");
//            intent.addCategory(Intent.CATEGORY_OPENABLE); // Add this to ensure we get openable files
//            resultLauncher.launch(Intent.createChooser(intent, "Select PDF"));
//        } catch (Exception e) {
//            Log.e("FileChooser", "Error launching file chooser", e);
//            showAlert("Error launching file picker");
//        }

        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Optional: Add multiple configuration options
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Create a chooser to show all possible PDF locations
            Intent chooserIntent = Intent.createChooser(
                    intent,
                    "Select a PDF File from Document Only"
            );

            resultLauncher.launch(chooserIntent);
        } catch (Exception e) {

            // Optional: Show user-friendly error dialog
          //  resultLauncher("Unable to open file picker");
        }
    }





    private void setupPdfLauncher() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            handleSelectedPdf(data.getData());
                        } else {
                            showAlert("No file selected");
                        }
                    }
                });
    }

    private void handleSelectedPdf(Uri uri) {
        try {

            if (!FileUtils.isValidPdfFile(this, uri)) {
                showAlert("Please select a valid PDF file");
                return;
            }

            String filePath = FileUtils.getFilePath(this, uri);
           // String filePath = Utility.getFilePath(this, uri);

            if (filePath == null) {
                showAlert("Unable to process the selected file!!","Please Choose File From Document Folder Only");
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                showAlert("Selected file not found");
                return;
            }

            if (!FileUtils.isFileLessThan5MB(file)) {
                showAlert("File is too big, please select a file less than 5MB");
                return;
            }

            uploadFileViaPDF(file);

        } catch (Exception e) {

            Log.e("PDF_HANDLER", "Error handling PDF: " + e.getMessage(), e);
            showAlert("Error processing file");
        }
    }


    private void uploadFileViaPDF(File file) {

        showDialogMain();
        try {
            switch (DOC_TYPE) {
                case "RAISE":
                    part = Utility.getMultipartPdf(file, PHOTO_File, "doc_type");
                    new ZohoController(this).uploadRaiseTicketDocWeb(part, this);
                    break;

                case "COMMON":
                    body = Utility.getBodyCommon(this, DocCommonID, DocCommonCrn,
                            DocCommonType, Docinsurer_id);
                    part = Utility.getMultipartImage(file, "file_1");
                    new ZohoController(this).uploadCommonDocuments(part, body, this);
                    break;
            }
        } catch (Exception e) {
            cancelDialogMain();
            Log.e("UPLOAD", "Error uploading file: " + e.getMessage(), e);
            showAlert("Error uploading file");
        }
    }

    private void pdfFileLauncher(){

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(
                            ActivityResult result)
                    {
                        // Initialize result data
                        Intent data = result.getData();
                        // check condition
                        if (data != null) {
                            // When data is not equal to empty
                            // Get PDf uri
                            Uri sUri = data.getData();
                            // set Uri on text view
                            Log.d("URL",""+ sUri.toString());

                            // Get PDF path
                            String sPath = sUri.getPath();
                            Log.d("URL",""+ Html.fromHtml(
                                    "<big><b>PDF Path</b></big><br>" + sPath));
                            String path =   Utility.getFilePath(CommonWebViewActivity.this, sUri);
                            File file = new File(path);

                            if (file.exists()) {

                                if (Utility.isFileLessThan5MB(file)) {

                                    showAlert("File is too Big, please select a file less than 5mb");
                                    return;
                                } else {

                                    Log.e("TravellerLog :: ", "Problem creating Image folder");
                                    showDialogMain();

                                    switch (DOC_TYPE) {

                                        case "RAISE": {

                                            part = Utility.getMultipartPdf(file, PHOTO_File, "doc_type");
                                            new ZohoController(CommonWebViewActivity.this).uploadRaiseTicketDocWeb(part, CommonWebViewActivity.this);

                                            break;
                                        }
                                        case "COMMON": {


                                            body = Utility.getBodyCommon(CommonWebViewActivity.this, DocCommonID, DocCommonCrn, DocCommonType, Docinsurer_id);

                                            part = Utility.getMultipartImage(file, "file_1");


                                            new ZohoController(CommonWebViewActivity.this).uploadCommonDocuments(part, body, CommonWebViewActivity.this);


                                        }
                                    }

                                }


                            }


                        }
                    }
                });
    }

    private void launchCamera() {


        String FileName = PHOTO_File;


        Docfile = createImageFile(FileName);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            imageUri = Uri.fromFile(Docfile);
        } else {
            imageUri = FileProvider.getUriForFile(CommonWebViewActivity.this,
                    getString(R.string.file_provider_authority), Docfile);
        }


//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
//                imageUri);
        //     startActivityForResult(cameraIntent, CAMERA_REQUEST);

        cameraLauncher.launch(imageUri);
    }


    private void openGallery() {

        galleryLauncher.launch("image/*");

    }

    //endregion


    //region Base PopUp EventFor Handling the Default Setting


    @Override
    public void onPositiveButtonClick(Dialog dialog, View view) {

        dialog.cancel();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, Constant.REQUEST_PERMISSION_SETTING);
    }

    @Override
    public void onCancelButtonClick(Dialog dialog, View view) {
        dialog.cancel();
    }

    //endregion


    public String getFileFromDownload(Context context, Uri uri) {
        final String id = DocumentsContract.getDocumentId(uri);

        if (id != null && id.startsWith("raw:")) {
            return id.substring(4);
        }

        String[] contentUriPrefixesToTry = new String[]{
                "content://downloads/public_downloads",
                "content://downloads/my_downloads",
                "content://downloads/all_downloads"
        };

        for (String contentUriPrefix : contentUriPrefixesToTry) {
            Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
            try {
                String path = FileUtilNew.getDataColumn(context, contentUri, null, null);
                if (path != null) {
                    return path;
                }
            } catch (Exception e) {
            }
        }

        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        String fileName = FileUtilNew.getFileName(context, uri);
        File cacheDir = FileUtilNew.getDocumentCacheDir(context);
        File file = generateFileName(fileName, cacheDir);
        String destinationPath = null;
        if (file != null) {
            destinationPath = file.getAbsolutePath();
            FileUtilNew.saveFileFromUri(context, uri, destinationPath);
        }

        return destinationPath;
    }


    private void handleCropImage(Uri crop_uri){

        try {

            Bitmap mphoto = null;
            try {
                mphoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), crop_uri);
                //  mphoto = getResizedBitmap(mphoto, 800);

            } catch (Exception e) {
                e.printStackTrace();
            }
            showDialogMain();

            switch (DOC_TYPE){

                case "RAISE" : {

                    file = saveImageToStorage(mphoto, PHOTO_File);
                    // setProfilePhoto(mphoto);

                    part = Utility.getMultipartImage(file, "doc_type");

                    new ZohoController(this).uploadRaiseTicketDocWeb(part, this);

                    break;
                }
                case "COMMON" : {

                    file = saveImageToStorage(mphoto, PHOTO_File);
                    // setProfilePhoto(mphoto);


                    body = Utility.getBodyCommon(this, DocCommonID, DocCommonCrn,DocCommonType,Docinsurer_id);

                    part = Utility.getMultipartImage(file, "file_1");


                    new ZohoController(CommonWebViewActivity.this).uploadCommonDocuments(part, body, CommonWebViewActivity.this);


                }

            }



        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }





    }
    // region Event
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode ==101 ){

            String result = data.getStringExtra("CROP");
            Uri crop_uri = data.getData();

            if(result!= null){
                crop_uri = Uri.parse(result);

            }


            handleCropImage(crop_uri);



        }
        else if(requestCode== SELECT_PICTURE && resultCode ==101 ){

            String result = data.getStringExtra("CROP");
            Uri crop_uri = data.getData();

            if(result!= null){
                crop_uri = Uri.parse(result);
            }

            handleCropImage(crop_uri);


        }



        // Below For Cropping The Camera Image
        //     if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            //extractTextFromImage();
//            startCropImageActivity(imageUri);
//        }
//        // Below For Cropping The Gallery Image
//        else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
//            Uri selectedImageUri = data.getData();
//            startCropImageActivity(selectedImageUri);
//        }

        //region Below  handle result of CropImageActivity
        ///007
        /*
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                try {
                    cropImageUri = result.getUri();
                    Bitmap mphoto = null;
                    try {
                        mphoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cropImageUri);
                        //  mphoto = getResizedBitmap(mphoto, 800);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showDialogMain();

                    switch (DOC_TYPE){

                        case "RAISE" : {

                            file = saveImageToStorage(mphoto, PHOTO_File);
                            // setProfilePhoto(mphoto);

                            part = Utility.getMultipartImage(file, "doc_type");

                            new ZohoController(this).uploadRaiseTicketDocWeb(part, this);

                            break;
                        }
                        case "COMMON" : {

                            file = saveImageToStorage(mphoto, PHOTO_File);
                            // setProfilePhoto(mphoto);

                            body = Utility.getBody_Common(this, DocCommonID, DocCommonCrn,DocCommonType,Docinsurer_id);

                            part = Utility.getMultipartImage(file, "file_1");

                            new ZohoController(CommonWebViewActivity.this).uploadCommonDocuments(part, body, CommonWebViewActivity.this);


                        }

                    }



                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

         */

        //endregion

    }


    @Override
    public void OnSuccess(APIResponse response, String message) {


        cancelDialogMain();
        //RaiseTicketWebDocResponse
        if (response instanceof RaiseTicketWebDocResponse) {
            if (response.getStatusNo() == 0) {


                Toast.makeText(CommonWebViewActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
//                String jsonResponse =  new Gson().toJson(response).toString();
//                jsonResponse = jsonResponse.replace("\"", "'");

                String jsonResponse = ((RaiseTicketWebDocResponse) response).getMasterData().getFile_name() + "|" +
                        ((RaiseTicketWebDocResponse) response).getMasterData().getFile_path();
                Log.i("RAISE_TICKET RESPONSE", jsonResponse);

                // Sending Data to Web Using evaluateJavascript
                // When Activty Page Called than This One is rasied.
                webView.evaluateJavascript("javascript: " +
                        "uploadImagePath(\"" + jsonResponse + "\")", null);


                //////////// When Dialog Page Called via Base Activity below method raised
                uploadWebViewRaiserPath(jsonResponse);


            } else {
                Toast.makeText(CommonWebViewActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        else  if (response instanceof CommonWebDocResponse) {
            if (((CommonWebDocResponse) response).getStatus().equals("Success")) {


                Toast.makeText(CommonWebViewActivity.this, ((CommonWebDocResponse) response).getMsg(), Toast.LENGTH_LONG).show();
//                String jsonResponse =  new Gson().toJson(response).toString();
//                jsonResponse = jsonResponse.replace("\"", "'");

                //         jsonResponse_doc = ((CommonWebDocResponse) response).getMasterData().getCrn() + "|" +
                //                  ((CommonWebDocResponse) response).getMasterData().getDocumentID()+ "|" + ((CommonWebDocResponse) response).getMasterData().getDocumentType()+ "|"
                //                 +((CommonWebDocResponse) response).getMasterData().getInsurerID();
                Log.i("Common doc RESPONSE", jsonResponse_doc);

                // Sending Data to Web Using evaluateJavascript
                // When Activty Page Called than This One is rasied.
                String   op_getCrn = ((CommonWebDocResponse) response).getMasterData().getCrn().replace("\"", "");
                String getDocumentID= ((CommonWebDocResponse) response).getMasterData().getDocumentID().replace("\"", "");
                String getfile_path= ((CommonWebDocResponse) response).getMasterData().getFilePath().replace("\"", "");

                jsonResponse_doc = op_getCrn+ "|" + getDocumentID+ "|" +getfile_path;

                //  jsonResponse_doc="12345678";
                webView.evaluateJavascript("javascript: " +
                        "viewImageData(\"" + jsonResponse_doc + "\")", null);


                //////////// When Dialog Page Called via Base Activity below method raised
                //       uploadWebViewdocPath(webView,jsonResponse_doc);

            } else {
                Toast.makeText(CommonWebViewActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        else if (response instanceof synctransactionDetailReponse) {
            cancelDialogMain();
            if (((synctransactionDetailReponse) response).getStatus().equals("success")) {
                synctransactionDetailEntity synctransactionEntity = ((synctransactionDetailReponse) response).getMasterData();

                if (synctransactionEntity != null) {
                    Intent intent = new Intent(CommonWebViewActivity.this, SyncRazorPaymentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("SYNC_TRANSACTION", synctransactionEntity);
                    startActivity(intent);
                    finish();
                }

            }
        }
        else if (response instanceof HorizonSyncDetailsWebResponse)
        {
            cancelDialogMain();
            if (((HorizonSyncDetailsWebResponse) response).getStatus().equals("SUCCESS")) {
                // syncContactEntity = ((HorizonsyncDetailsResponse) response).getResult();

                posphorizonEntity =  ((HorizonSyncDetailsWebResponse) response).getPOSP();  // :-- 05 temp
                if (posphorizonEntity != null) {

                    Intent intent = new Intent(CommonWebViewActivity.this, SyncRazorPaymentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("posphorizon_TRANSACTION", posphorizonEntity);
                    intent.putExtra("payment_type", "POSP");
                    startActivity(intent);
                    finish();
                }


            }
        }


    }

    @Override
    public void OnFailure(Throwable t) {

        cancelDialogMain();
        Log.d(Constant.TAG, t.getMessage() );
        //  Toast.makeText(CommonWebViewActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    //endregion

    // endregion

    private void showDialogMain( ){

        try {
            if(! CommonWebViewActivity.this.isFinishing()){

                if(!showDialog.isShowing()) {
                    ProgressdialogLoadingBinding dialogLoadingBinding = ProgressdialogLoadingBinding.inflate(getLayoutInflater());
                    showDialog.setContentView(dialogLoadingBinding.getRoot());

                    showDialog.setCancelable(false);
                    showDialog.show();
                }
            }
        }catch (Exception e){


        }


    }

    private void cancelDialogMain() {
        try{
            if (showDialog != null) {
                showDialog.dismiss();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            showDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resultLauncher.unregister();
        galleryLauncher.unregister();
        cameraLauncher.unregister();
    }
}

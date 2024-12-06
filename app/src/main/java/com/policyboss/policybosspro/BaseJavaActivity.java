package com.policyboss.policybosspro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.policyboss.policybosspro.facade.PolicyBossPrefsManager;
import com.policyboss.policybosspro.utility.Utility;
import com.policyboss.policybosspro.utils.DBPersistanceController;
import com.policyboss.policybosspro.view.login.LoginActivity;

import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin;
import com.policyboss.policybosspro.webview.CommonWebViewActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.policyboss.policybosspro.BuildConfig;

public class BaseJavaActivity extends AppCompatActivity {


    ProgressDialog dialog;
    int height = 200;
    int textSize = 25;
    int textMargin = 10;
    int startHeight = (height - (4 * textSize) - (3 * textMargin)) / 2;
    PopUpListener popUpListener;
    PermissionListener permissionListener;

    String[] permissionsRequired = new String[]{android.Manifest.permission.CALL_PHONE};
    WebView webView;
    //


    String parent_ssid = "";
    final String mapKey = "map_switchuser";
    public static final String TERM_FOR_INPUT_FRAGMENT = "for_term_input";
    Dialog webviewDialog;
    Dialog webviewDialog_mrk;

    PolicyBossPrefsManager prefManager ;
    public String getDateFromAge(int age) {

        Calendar cal = Calendar.getInstance();
        int year = age;
        cal.add(Calendar.YEAR, -year);
        return new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
    }
    public String getDateFromweb(String birthdate) {
//        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date d = new Date(birthdate);
//        String dateString = inputDateFormat.format(d);
//        return dateString;
        SimpleDateFormat  outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            // Parse the input date string to a Date object
            Date date = inputDateFormat.parse(birthdate);

            // Format the Date object to the desired output format
            String formattedDate = outputDateFormat.format(date);

            // 'formattedDate' now contains the date in the "dd-MM-yyyy" format
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parse exception if the input date format is incorrect
            return "";
        }



    }
    public int getAgeFromDate(String birthdate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Calendar bithDate = Calendar.getInstance();
            bithDate.setTime(dateFormat.parse(birthdate));
            Calendar today = Calendar.getInstance();
            int curYear = today.get(Calendar.YEAR);
            int dobYear = bithDate.get(Calendar.YEAR);

            return curYear - dobYear;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    public static String getYYYYMMDDPattern(String dateCal) {

        String dateSelected = "";
        if (dateCal.equals("")) {
            return "";
        }
        long select_milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");

        Date d = null;
        try {
            d = f.parse(dateCal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        select_milliseconds = d.getTime();

        Date date = new Date(select_milliseconds); //Another date Formate ie yyyy-mm-dd
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        dateSelected = df2.format(date);
        return dateSelected;
    }

    public void dialogLogout(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseJavaActivity.this);
        builder.setTitle("");
        builder.setMessage("Do you really want to logout?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "LOGOUT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //05 temp
                        new PolicyBossPrefsManager(context).clearAll();



                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                });

        builder.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public void dialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseJavaActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit the application?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog exitdialog = builder.create();
        exitdialog.show();

        Button negative = exitdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positive = exitdialog.getButton(DialogInterface.BUTTON_POSITIVE);
        negative.setTextColor(getResources().getColor(R.color.header_light_text));
        positive.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            // Initialize Realm

        }catch ( Exception ex){

        }

    }


    //region all neccessary functions
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void cancelDialog() {
        try{
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
                dialog.dismiss();
        }
    }

    protected void cancelDialog(Context context) {
        try{
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
    }

    protected void showDialog() {
//        dialog = ProgressDialog.show(BaseJavaActivity.this, "", "Loading...", true);
        showDialog("Loading...");

    }

    protected void showDialog(Context context) {
        // dialog = ProgressDialog.show(context, "", "Loading...", true);
        try {
                if (context != null) {
                    showDialog("Loading...", context);
                }
            }
          catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showDialog(String msg, Context context) {
        try {
                if (dialog == null)
                    dialog = ProgressDialog.show(BaseJavaActivity.this, "", msg, true);
                else {
                    if (!dialog.isShowing())
                        dialog = ProgressDialog.show(BaseJavaActivity.this, "", msg, true);
                }
             }
        catch (Exception e) {
                    e.printStackTrace();

            }
    }

    protected void showDialog(String msg) {
        try {

            if (dialog == null)
                dialog = ProgressDialog.show(BaseJavaActivity.this, "", msg, true);
            else {
                if (!dialog.isShowing())

                    dialog = ProgressDialog.show(BaseJavaActivity.this, "", msg, true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendSms(String mobNumber) {
        try {
            mobNumber = mobNumber.replaceAll("\\s", "");
            mobNumber = mobNumber.replaceAll("\\+", "");
            mobNumber = mobNumber.replaceAll("-", "");
            mobNumber = mobNumber.replaceAll(",", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mobNumber, null)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
        }

    }

    public void dialNumber(String mobNumber) {

        try {
            mobNumber = mobNumber.replaceAll("\\s", "");
            mobNumber = mobNumber.replaceAll("\\+", "");
            mobNumber = mobNumber.replaceAll("-", "");
            mobNumber = mobNumber.replaceAll(",", "");
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + mobNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
        }


    }

    public void composeEmail(String addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{addresses});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        startActivity(Intent.createChooser(intent, "Email via..."));
    }

    public static boolean isValidePhoneNumber(EditText editText) {
        String phoneNumberPattern = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$";
        String phoneNumberEntered = editText.getText().toString().trim();
        return !(phoneNumberEntered.isEmpty() || !phoneNumberEntered.matches(phoneNumberPattern));
    }

    public static boolean isValideEmailID(EditText editText) {
        String emailEntered = editText.getText().toString().trim();
        return !(emailEntered.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches());
    }

    public static boolean isEmpty(EditText editText) {
        String text = editText.getText().toString().trim();
        return !(text.isEmpty());
    }

    public static boolean isValidPan(EditText editText) {
        String panNo = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        String panNoAlter = editText.getText().toString().toUpperCase();
        return !(panNoAlter.isEmpty() || !panNoAlter.matches(panNo));
    }

    public static boolean isValidAadhar(EditText editText) {
        String aadharPattern = "[0-9]{12}";
        String aadharNo = editText.getText().toString().toUpperCase();
        return !(aadharNo.isEmpty() || !aadharNo.matches(aadharPattern));
    }

    public File saveImageToStorage(Bitmap bitmap, String name) {
        FileOutputStream outStream = null;

        File dir = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            dir = Utility.createDirIfNotExists();
        }else{
            dir = createDirIfNotExistsNew(getApplicationContext());
        }

        String fileName = name + ".jpg";
        fileName = fileName.replaceAll("\\s+", "");
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFile;
    }

    public  File createDirIfNotExistsNew(Context context) {
        boolean ret = true;

        // external storage.
        File file = new File(context.getExternalFilesDir( Environment.DIRECTORY_PICTURES), "PolicyBossPro");
        if (file.mkdirs()) {
            Log.e("File Log", "Directory not created");
        }

        return file;
    }
    public File getImageFromStorage(String name) {
        try {
            File dir = Utility.createDirIfNotExists();
            String fileName = name + ".jpg";
            File outFile = new File(dir, fileName);
            return outFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public File createFile(String name) {
        FileOutputStream outStream = null;

        File dir = Utility.createDirIfNotExists();
        String fileName = name + ".jpg";
        fileName = fileName.replaceAll("\\s+", "");
        File outFile = new File(dir, fileName);

        return outFile;
    }


    public Bitmap createBitmap(Bitmap pospPhoto, String pospName, String pospDesg, String pospMob, String pospEmail) {

        pospPhoto = Bitmap.createScaledBitmap(pospPhoto, height - 20, height - 20, false);

        Bitmap textBitmap = Bitmap.createBitmap(2000, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(textBitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);

        Paint paintBold = new Paint();
        paintBold.setColor(Color.BLACK);
        paintBold.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintBold.setTextSize(textSize + 1);
        startHeight += 10;
        canvas.drawText("" + pospName, height + 20, startHeight + textMargin, paintBold);
        canvas.drawText("" + pospDesg, height + 20, startHeight + 1 * textSize + 2 * textMargin, paint);
        canvas.drawText("" + pospMob, height + 20, startHeight + 2 * textSize + 3 * textMargin, paint);
        canvas.drawText("" + pospEmail, height + 20, startHeight + 3 * textSize + 4 * textMargin, paint);
        //canvas.drawText("" + pospName + "\n" + pospDesg + "\n" + pospMob + "\n" + pospEmail, 10, 10, paint);


        Bitmap cs = null;

        cs = Bitmap.createBitmap(2000, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(textBitmap, 0f, 0f, null);
        comboImage.drawBitmap(pospPhoto, 0f, 15f, null);


        return cs;
    }

    public Bitmap combineImages(Bitmap first, Bitmap second) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;
        int width, height = 0;

        if (second == null) {
            width = first.getWidth();
            height = first.getHeight();
            cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);
            comboImage.drawBitmap(first, 0f, 0f, null);
        } else {


            width = first.getWidth();
            height = first.getHeight() + second.getHeight();

            cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas comboImage = new Canvas(cs);

            comboImage.drawBitmap(first, 0f, 0f, null);
            comboImage.drawBitmap(second, 0f, first.getHeight(), null);
        }

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    //region Sharing Old Logic
    public void datashareListOld(Context context, Bitmap bitmap, String prdSubject, String prdDetail) {

        //  String Deeplink = "https://nykaa.ly/P_" + Sharedata_product_id;

        //  String prdSubject = "Look what I found on Nykaa!";
        //  String prdDetail = "Check out " + Sharedata_product_name + " on Nykaa" + "\n" + Deeplink;
        try {


            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Finmart_product.jpg");

            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            //  Uri screenshotUri = Uri.fromFile(file);
            Uri screenshotUri = FileProvider.getUriForFile(BaseJavaActivity.this,
                    getString(R.string.file_provider_authority),
                    file);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);

            shareIntent.setType("text/plain");

            PackageManager pm = context.getPackageManager();


            List<ResolveInfo> resInfo = pm.queryIntentActivities(shareIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                String processName = ri.activityInfo.processName;
                String AppName = ri.activityInfo.name;

                if ((packageName.contains("android.email") || packageName.contains("mms") || packageName.contains("twitter") || (packageName.contains("whatsapp")) || packageName.contains("messaging") || packageName.contains("android.gm") || packageName.contains("com.google.android.apps.plus")) || (packageName.contains("apps.docs")) && processName.contains("android.apps.docs:Clipboard") || (packageName.contains("android.talk")) && AppName.contains("hangouts")) {

                    shareIntent.setComponent(new ComponentName(packageName, ri.activityInfo.name));

                    if (packageName.contains("android.email")) {
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("twitter")) {

                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        shareIntent.setPackage(packageName);

                    }
//                    else if (packageName.contains("facebook.katana")) {
//                        shareIntent.setType("text/plain");
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, product.getImageUrl());
//                        shareIntent.setPackage("com.facebook.katana");
//                        //shareIntent.putExtra(Intent.EXTRA_STREAM, Deeplink);
//                    }
//                    else if (packageName.contains("facebook.orca")) {
//                        shareIntent.setType("image/*");
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
//                        shareIntent.setPackage("com.facebook.orca");
//
//                    }
                    else if (packageName.contains("mms")) {
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("whatsapp")) {
                        shareIntent.setType("image/*");

                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        shareIntent.setPackage(packageName);


                    } else if (packageName.contains("messaging")) {
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage(packageName);
                    } else if (packageName.contains("com.google.android.apps.plus")) {
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("android.talk")) {
                        if (AppName.contains("hangouts")) {
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            shareIntent.setPackage(packageName);
                        }

                    } else if (packageName.contains("apps.docs")) {
                        if (processName.contains("android.apps.docs:Clipboard")) {
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                            shareIntent.setPackage(packageName);
                        }

                    } else if (packageName.contains("android.gm")) {
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        shareIntent.setPackage(packageName);

                    } else {
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);

                    }

                    intentList.add(new LabeledIntent(shareIntent, packageName, ri.loadLabel(pm), ri.icon));

                }
            }


            if (intentList.size() > 1) {
                intentList.remove(intentList.size() - 1);
            }

            Intent openInChooser = Intent.createChooser(shareIntent, "Share Via");
            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

            startActivity(openInChooser);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void datashareListOLD(Context context, String prdSubject,String Bodymsg, String link) {



        String Deeplink;
        //"Look! This can make you look gorgeous from Nykaa";
        Deeplink = Bodymsg + "\n" + link;
        if(prdSubject.isEmpty()){
            prdSubject = "PolicyBoss Pro";
        }

        String prdDetail = Deeplink;


        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);

            shareIntent.setType("text/plain");

            PackageManager pm = context.getPackageManager();



            List<ResolveInfo> resInfo = pm.queryIntentActivities(shareIntent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
            ///////////
            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                String processName = ri.activityInfo.processName;
                String AppName = ri.activityInfo.name;

                if ((packageName.contains("android.email") || packageName.contains("mms") || packageName.contains("twitter") || (packageName.contains("whatsapp")) || (packageName.contains("facebook.katana")) || (packageName.contains("facebook.orca")) || packageName.contains("messaging") || packageName.contains("android.gm") || packageName.contains("com.google.android.apps.plus")) || (packageName.contains("apps.docs")) && processName.contains("android.apps.docs:Clipboard") || (packageName.contains("android.talk")) && AppName.contains("hangouts")) {

                    shareIntent.setComponent(new ComponentName(packageName, ri.activityInfo.name));

                    if (packageName.contains("android.email")) {

                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("twitter")) {

                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("facebook.katana")) {

                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage("com.facebook.katana");

                    } else if (packageName.contains("facebook.orca")) {

                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage("com.facebook.orca");

                    } else if (packageName.contains("mms")) {

                        shareIntent.setPackage(packageName);

                    } else if (packageName.contains("whatsapp")) {

                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
                        shareIntent.setPackage(packageName);


                    } else if (packageName.contains("messaging")) {
                        shareIntent.setPackage(packageName);
                    } else if (packageName.contains("com.google.android.apps.plus")) {
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.setPackage(packageName);

                    }
//                    else if (packageName.contains("android.talk")) {
//                        if (AppName.contains("hangouts")) {
//
//                            shareIntent.setPackage(packageName);
//                        }
//
//                    }
                    else if (packageName.contains("apps.docs")) {
                        if (processName.contains("android.apps.docs:Clipboard")) {

                            shareIntent.setPackage(packageName);
                        }

                    } else if (packageName.contains("android.gm")) {
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
                        shareIntent.setPackage(packageName);

                    }

                    intentList.add(new LabeledIntent(shareIntent, packageName, ri.loadLabel(pm), ri.icon));

                }
            }


            if (intentList.size() > 1) {
                intentList.remove(intentList.size() - 1);
            }

            Intent openInChooser = Intent.createChooser(shareIntent, "Share Via");

            // convert intentList to array
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

            context.startActivity(openInChooser);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // endregion

    public void datashareList(Context context, Bitmap bitmap, String prdSubject, String prdDetail)  {



        OutputStream fos = null;
        Uri screenshotUri = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){




            try{

                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,Utility.getNewFileName("Finmart_product"));
                contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,Utility.getImageDirectoryPath());


                screenshotUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);


                fos = resolver.openOutputStream(Objects.requireNonNull(screenshotUri));


            }catch (Exception ex){

            }




        }else{

            File file = null;

            try{
                File imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "FINMART");


                if (!imagesDir.exists()){
                    imagesDir.mkdir();
                }

                //  File imagesDir  = getAppSpecificAlbumStorageDir(PermissionActivity.this, Environment.DIRECTORY_PICTURES,"DemoLatest1");

                file = new File(imagesDir, "PolicyBossPro_product" + ".jpg");
                fos = new FileOutputStream(file);

                screenshotUri = FileProvider.getUriForFile(BaseJavaActivity.this,
                        getString(R.string.file_provider_authority),
                        file);


            }catch (Exception ex){


                Log.d("PATH_ERROR" , ex.getLocalizedMessage().toString());
            }
        }

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.close();

            openNativeShare(screenshotUri,prdSubject,prdDetail);

        }catch (Exception ex){

        }

    }

    private void  openNativeShare( Uri screenshotUri, String prdSubject, String prdDetail) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);

        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));
    }

    public void datashareList(Context context, String prdSubject,String Bodymsg, String link) {


        String Deeplink;

        Deeplink = Bodymsg + "\n" + link;
        if(prdSubject.isEmpty()){
            prdSubject = "PolicyBoss Pro";
        }

        String prdDetail = Deeplink;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, prdSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));



    }


    public void sharePdfTowhatsAppOld(String pdfFileName, String urlToShare) {
        try {
            File outputFile = new File(Environment.getExternalStorageDirectory(), "/FINMART/QUOTES/" + pdfFileName + ".pdf");

            Uri uri = FileProvider.getUriForFile(BaseJavaActivity.this,
                    getString(R.string.file_provider_authority),
                    outputFile);
            //Uri uri = Uri.fromFile(outputFile);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            share.putExtra(Intent.EXTRA_TEXT, urlToShare);
            //share.setPackage("com.whatsapp");
            Intent intent = Intent.createChooser(share, "Share Quote");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sharePdfFile(String pdfFileName, String urlToShare, Uri uri) {


        try {

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            share.putExtra(Intent.EXTRA_TEXT, urlToShare);
            share.putExtra(Intent.EXTRA_SUBJECT, "Quote Details");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            Intent intent = Intent.createChooser(share, "Share Quote");
            startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sharePdfFileLink(String pdfFileName, String urlToShare, Uri uri) {


       String prdDetail = "Please click on the below link to get the quotation:" + "\n\n" + urlToShare;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);


        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quote Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT, prdDetail);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));



    }

    public File createImageFile(String name)  {
        // Create an image file name
        File temp;
        String timeStamp  =new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir  = getAppSpecificAlbumStorageDir(this, Environment.DIRECTORY_PICTURES,"PolicyBossPro");
        try {
            temp =  File.createTempFile(name + timeStamp, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );

            Log.d("IMAGE_PATH","File Name"+ temp.getName() + "File Path" +temp.getAbsolutePath());
            //  String  currentPhotoPath = temp.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return  temp;
    }

    public File getAppSpecificAlbumStorageDir(Context context,String albumName ,  String subAlbumName) {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        File file = new File(context.getExternalFilesDir(albumName), subAlbumName);
        if (file.mkdirs()) {
            Log.e("fssfsf", "Directory not created");
        }

        return file;
    }

    public Bitmap getBitmapFromContentResolver(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return  image;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public class createBitmapFromURL extends AsyncTask<Void, Void, Bitmap> {
        String imgURL;
        Bitmap bitmap;
        URL NotifyPhotoUrl;
        String title;
        public createBitmapFromURL(String imgURL,String title) {
            this.imgURL = imgURL;
            this.title = title;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap networkBitmap = null;
            try {
                NotifyPhotoUrl = new URL(imgURL);
                networkBitmap = BitmapFactory.decodeStream(
                        NotifyPhotoUrl.openConnection().getInputStream());


            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "Could not load Bitmap from: " + NotifyPhotoUrl);
            }

            return networkBitmap;
        }

        protected void onPostExecute(Bitmap result) {

            bitmap = result;
            cancelDialog();

            if(bitmap != null ){
                viewUploadFile(bitmap,title);
            }



        }
    }



    private void viewUploadFile(Bitmap bitmap, String title) {

        if (bitmap == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.CustomDialog);

        // TouchImageView ivDocFile;
        ImageView ivDocFile,ivClose;
        Button btnClose;
        TextView txtHdr;
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView;

        dialogView = inflater.inflate(R.layout.layout_view_doc, null);


        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        // set the custom dialog components - text, image and button
        btnClose = (Button) dialogView.findViewById(R.id.btnClose);
        ivClose   = (ImageView) dialogView.findViewById(R.id.ivClose);
        ivDocFile = (ImageView) dialogView.findViewById(R.id.ivDocFile);
        txtHdr = (TextView) dialogView.findViewById(R.id.txtHdr);

        ivDocFile.setImageBitmap(bitmap);
        txtHdr.setText(title);

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        Glide.with(this)
//                .load(stream)
//                .asBitmap()
//                .override(100, 100)
//                .into(ivDocFile);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        alertDialog.setCancelable(false);
        // alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        alertDialog.show();
        //  alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void registerPopUp(PopUpListener popUpListener) {
        if (popUpListener != null)
            this.popUpListener = popUpListener;
    }

    public interface PopUpListener {

        void onPositiveButtonClick(Dialog dialog, View view);

        void onCancelButtonClick(Dialog dialog, View view);
    }

    public interface WebViewPopUpListener {


        void onCancelClick(Dialog dialog, View view);
    }

    public interface PermissionListener {

        void onGrantButtonClick(View view);

    }


    public void registerPermission(PermissionListener permissionListener) {
        if (permissionListener != null)
            this.permissionListener = permissionListener;
    }

    public void openPopUp(final View view, String title, String desc, String positiveButtonName, boolean isCancelable) {
        try {
            final Dialog dialog;
            dialog = new Dialog(BaseJavaActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_common_popup);

            TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
            tvOk.setText(positiveButtonName);
            TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
            txtMessage.setText(desc);
            ImageView ivCross = (ImageView) dialog.findViewById(R.id.ivCross);

            dialog.setCancelable(isCancelable);
            dialog.setCanceledOnTouchOutside(isCancelable);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = lp.MATCH_PARENT;  // Width
            lp.height = lp.WRAP_CONTENT; // Height
            dialogWindow.setAttributes(lp);

            dialog.show();
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    if (popUpListener != null)
                        popUpListener.onPositiveButtonClick(dialog, view);
                }
            });

            ivCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    if (popUpListener != null)
                        popUpListener.onCancelButtonClick(dialog, view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String strTitle ,String strBody) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseJavaActivity.this);
            builder.setTitle(strTitle);

            builder.setMessage(strBody);
            String positiveText = "Ok";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show();
        }
    }
    public void showAlert(String strBody) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseJavaActivity.this);
            builder.setTitle("PolicyBossPro");

            builder.setMessage(strBody);
            String positiveText = "Ok";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show();
        }
    }

    public void permissionAlert(final View view, String Title, String strBody) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseJavaActivity.this);
            builder.setTitle(Title);

            builder.setMessage(strBody);
            String positiveText = "GRANT";
            String NegativeText = "CANCEL";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (permissionListener != null)
                                dialog.dismiss();
                            if (view != null) {
                                permissionListener.onGrantButtonClick(view);
                            }

                        }
                    });

            builder.setNegativeButton(NegativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show();
        }
    }


    public void openWebViewPopUp(final View view, String url, boolean isCancelable,String strHdr) {
        try {


            webviewDialog = new Dialog(BaseJavaActivity.this);
            webviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            webviewDialog.setContentView(R.layout.layout_common_webview_popup);

            webviewDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
             webView = webviewDialog.findViewById(R.id.webView);
            TextView txtTitle =  webviewDialog.findViewById(R.id.txtTitle);

            if(strHdr.trim().equals("")){
                txtTitle.setVisibility(View.GONE);
            }else{

                txtTitle.setText(""+ strHdr.toUpperCase());
                txtTitle.setVisibility(View.VISIBLE);
            }
            settingWebview(webView, url);
            ImageView ivCross = (ImageView) webviewDialog.findViewById(R.id.ivCross);

            webviewDialog.setCancelable(isCancelable);
            webviewDialog.setCanceledOnTouchOutside(isCancelable);
          //  webviewDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Window dialogWindow = webviewDialog.getWindow();

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = lp.MATCH_PARENT;  // Width
            lp.height = lp.WRAP_CONTENT; // Height
            dialogWindow.setAttributes(lp);
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


            webviewDialog.show();

            ivCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    webviewDialog.dismiss();
//                    if (webViewPopUpListener != null)
//                        webViewPopUpListener.onCancelClick(webviewDialog, view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openWebViewPopUp_marketing(final View view, String url, boolean isCancelable,String strHdr) {
        try {
            if ( webviewDialog_mrk.isShowing()) {

                return;
            }


            webviewDialog_mrk.requestWindowFeature(Window.FEATURE_NO_TITLE);
            webviewDialog_mrk.setContentView(R.layout.layout_common_webview_popup);

            webviewDialog_mrk.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            webView = webviewDialog_mrk.findViewById(R.id.webView);
            TextView txtTitle =  webviewDialog_mrk.findViewById(R.id.txtTitle);

            if(strHdr.trim().equals("")){
                txtTitle.setVisibility(View.GONE);
            }else{

                txtTitle.setText(""+ strHdr.toUpperCase());
                txtTitle.setVisibility(View.VISIBLE);
            }
            settingWebview(webView, url);
            ImageView ivCross = (ImageView) webviewDialog_mrk.findViewById(R.id.ivCross);

            webviewDialog_mrk.setCancelable(isCancelable);
            webviewDialog_mrk.setCanceledOnTouchOutside(isCancelable);
            //  webviewDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            Window dialogWindow = webviewDialog_mrk.getWindow();

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = lp.MATCH_PARENT;  // Width
            lp.height = lp.WRAP_CONTENT; // Height
            dialogWindow.setAttributes(lp);
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


            webviewDialog_mrk.show();

            ivCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    webviewDialog_mrk.dismiss();
//                    if (webViewPopUpListener != null)
//                        webViewPopUpListener.onCancelClick(webviewDialog, view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadWebViewRaiserPath( String jsonResponse )
    {
       if(webView != null) {
           webView.evaluateJavascript("javascript: " +
                   "uploadImagePath(\"" + jsonResponse + "\")", null);
       }
    }


    public void uploadWebViewdocPath(WebView webView1,String jsonResponse )
    {
        if(webView != null) {
            webView.evaluateJavascript("javascript: " +
                    "viewImageData(\"" + jsonResponse + "\")", null);
        }
    }

    public class MyJavaScriptInterface {
        Context mContext;
        String data;

        MyJavaScriptInterface(Context ctx) {
            this.mContext = ctx;
        }


        // region Raise Ticket
        @JavascriptInterface
        public void Upload_doc(String randomID) {

           ((CommonWebViewActivity)mContext).galleryCamPopUp( randomID);

        }

        @JavascriptInterface
        public void Upload_doc_view(String randomID) {

            ((CommonWebViewActivity)mContext).galleryCamPopUp( randomID);


        }

        @JavascriptInterface
        public void synccontacts() {
            //Get the string value to process
            //shareQuote();
            startActivity(new Intent(BaseJavaActivity.this, WelcomeSyncContactActivityKotlin.class));
        }

        @JavascriptInterface
        public void syncsummary() {
            //Get the string value to process
            //shareQuote();

            PolicyBossPrefsManager prefManager  =    new PolicyBossPrefsManager(BaseJavaActivity.this);
            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class) // .putExtra("URL", "https://bo.magicfinmart.com/motor-lead-details/" + String.valueOf(loginResponseEntity.getFBAId()))
                    .putExtra("URL", "" + prefManager.getLeadDashUrl())
                    .putExtra("NAME", "" + "Sync Contact DashBoard")
                    .putExtra("TITLE", "" + "Sync Contact DashBoard"));
        }

        // endregion+
        @JavascriptInterface
        public void incomePotential() {
            //Get the string value to process
            //shareQuote();
            //05 temp
           // startActivity(new Intent(BaseJavaActivity.this, IncomePotentialActivity.class));
        }

        @JavascriptInterface
        public void incomeCalculator() {
            //Get the string value to process
            //shareQuote();
            // //05 temp
           // startActivity(new Intent(BaseJavaActivity.this, IncomePotentialActivity.class));
            // startActivity(new Intent(BaseJavaActivity.this, IncomeCalculatorActivity.class));
        }

        @JavascriptInterface
        public void processComplete() {
            //Get the string value to process
            //shareQuote();
        }

        @JavascriptInterface
        public void callPDF(String url) {
            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", url)
                    .putExtra("NAME", "LIC Business")
                    .putExtra("TITLE", "LIC Business"));
        }

        @JavascriptInterface
        public void callPDFCREDIT(String url) {
            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", url)
                    .putExtra("NAME", "FREE CREDIT REPORT")
                    .putExtra("TITLE", "LIC FREE CREDIT REPORT"));
        }


        @JavascriptInterface
        public void showcar() {//Android.RedirectToHomepage();

            if(webviewDialog!= null && webviewDialog.isShowing()){
                webviewDialog.dismiss();
            }

            if(prefManager==null)
            {
                prefManager = new PolicyBossPrefsManager(BaseJavaActivity.this);  }

            String motorUrl = prefManager.getFourWheelerUrl();

            String ipaddress = "0.0.0.0";
            try {
                ipaddress = "";
            } catch (Exception io) {
                ipaddress = "0.0.0.0";
            }

            //fetching parent ss_id in case of switch user
//            Map<String, String> map = (BaseJavaActivity.this).loadMap();
//            String parent_ssid = "";
//            if (map.size() > 0) {
//                parent_ssid = map.get("Parent_POSPNo");
//            }


            //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
            String append = "&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                    + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                    + "&device_id=" + Utility.getDeviceID(BaseJavaActivity.this)
                    + "&product_id=1&login_ssid=" + parent_ssid;
            motorUrl = motorUrl + append;



            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", motorUrl)
                    .putExtra("NAME", "Motor Insurance")
                    .putExtra("TITLE", "Motor Insurance"));
        }

        @JavascriptInterface
        public void showtw() {//Android.RedirectToHomepage();

            if(webviewDialog!= null && webviewDialog.isShowing()){
                webviewDialog.dismiss();
            }
            if(prefManager==null)
            {
                prefManager = new PolicyBossPrefsManager(BaseJavaActivity.this);

            }

            String motorUrl = prefManager.getTwoWheelerUrl();

            String ipaddress = "0.0.0.0";
            try {
                ipaddress = "";
            } catch (Exception io) {
                ipaddress = "0.0.0.0";
            }

            //fetching parent ss_id in case of switch user
//            Map<String, String> map = (BaseJavaActivity.this).loadMap();
//            String parent_ssid = "";
//            if (map.size() > 0) {
//                parent_ssid = map.get("Parent_POSPNo");
//            }


            //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
            String append = "&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                    + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                    + "&device_id=" +Utility.getDeviceID(BaseJavaActivity.this)
                    + "&product_id=10&login_ssid=" + parent_ssid;
            motorUrl = motorUrl + append;


            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", motorUrl)
                    .putExtra("NAME", "Two Wheeler Insurance")
                    .putExtra("TITLE", "Two Wheeler Insurance"));

        }

        @JavascriptInterface
        public void showcv() {//Android.RedirectToHomepage();

            if(webviewDialog!= null && webviewDialog.isShowing()){
                webviewDialog.dismiss();
            }
            if(prefManager==null)
            {
                prefManager = new PolicyBossPrefsManager(BaseJavaActivity.this);

            }
            String cvUrl = prefManager.getCVUrl();

            String ipaddress = "0.0.0.0";
            try {
                ipaddress = "";
            } catch (Exception io) {
                ipaddress = "0.0.0.0";
            }

            //fetching parent ss_id in case of switch user
//            Map<String, String> map = (BaseJavaActivity.this).loadMap();
//            String parent_ssid = "";
//            if (map.size() > 0) {
//                parent_ssid = map.get("Parent_POSPNo");
//            }


            //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
            String append = "&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                    + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                    + "&device_id=" + Utility.getDeviceID(BaseJavaActivity.this)
                    + "&product_id=12&login_ssid=" + parent_ssid;
            cvUrl = cvUrl + append;


            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", cvUrl)
                    .putExtra("NAME", "Commercial Vehicle Insurance")
                    .putExtra("TITLE", "Commercial Vehicle Insurance"));

        }

        @JavascriptInterface
        public void showhealth() {//Android.RedirectToHomepage();

            if(webviewDialog!= null && webviewDialog.isShowing()){
                webviewDialog.dismiss();
            }

            if(prefManager==null)
            {
                prefManager = new PolicyBossPrefsManager(BaseJavaActivity.this);

            }


            String healthUrl = prefManager.getHealthurl();

            String ipaddress = "0.0.0.0";
            try {
                ipaddress = "";
            } catch (Exception io) {
                ipaddress = "0.0.0.0";
            }

            //fetching parent ss_id in case of switch user
//            Map<String, String> map = (BaseJavaActivity.this).loadMap();
//            String parent_ssid = "";
//            if (map.size() > 0) {
//                parent_ssid = map.get("Parent_POSPNo");
//            }


            //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1

            String append = "&ip_address=" + ipaddress
                    + "&app_version=policyboss-" + Utility.getVersionName(BaseJavaActivity.this)
                    + "&device_id=" + Utility.getDeviceID(BaseJavaActivity.this) + "&login_ssid=" + parent_ssid;

            healthUrl = healthUrl + append;


            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", healthUrl)
                    .putExtra("NAME", "Health Insurance")
                    .putExtra("TITLE", "Health Insurance"));

        }
        @JavascriptInterface
        public void showicici() {

            //05 temp
//            if(webviewDialog!= null && webviewDialog.isShowing()){
//                webviewDialog.dismiss();
//            }
//            Intent intent = new Intent(BaseJavaActivity.this, IciciTermActivity.class);
//            intent.putExtra(TERM_FOR_INPUT_FRAGMENT, 39);
//            //intent.putExtra(TERM_INPUT_FRAGMENT, null);
//            startActivity(intent);
        }
        @JavascriptInterface
        public void showhdfc() {

            //05 temp
//            if(webviewDialog!= null && webviewDialog.isShowing()){
//                webviewDialog.dismiss();
//            }
//            Intent intent = new Intent(BaseJavaActivity.this, HdfcTermActivity.class);
//            intent.putExtra(TERM_FOR_INPUT_FRAGMENT, 28);
//            //intent.putExtra(TERM_INPUT_FRAGMENT, null);
//            startActivity(intent);
        }
        @JavascriptInterface
        public void showtermcomp() {

            // 05 temp
//            if(webviewDialog!= null && webviewDialog.isShowing()){
//                webviewDialog.dismiss();
//            }
//            //Life Insurance
//            // mContext.startActivity(new Intent(mContext, TermSelectionActivity.class));
//            startActivity(new Intent(BaseJavaActivity.this, TermSelectionActivity.class));
        }
        @JavascriptInterface
        public void userdefurl(String url,String title) {
            if(webviewDialog!= null && webviewDialog.isShowing()){
                webviewDialog.dismiss();
            }
            startActivity(new Intent(BaseJavaActivity.this, CommonWebViewActivity.class)
                    .putExtra("URL", url)
                    .putExtra("NAME", title)
                    .putExtra("TITLE", title));
        }
    }

    private void settingWebview(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setLightTouchEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        String googleDocs = "https://docs.google.com/viewer?url=";
                        webView.loadUrl(googleDocs + url);
                    }
                }*/
                return false;
            }
        });
        webView.getSettings().setBuiltInZoomControls(true);

        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "Android");

        Log.d("URL", url);
        if (url.endsWith(".pdf")) {

            webView.loadUrl("https://docs.google.com/viewer?url=" + url);
            //webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        } else {
            webView.loadUrl(url);
        }
    }


    //endregion


}


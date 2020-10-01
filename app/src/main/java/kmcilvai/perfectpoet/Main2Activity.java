package kmcilvai.perfectpoet;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.argb;
import static android.graphics.Color.rgb;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class Main2Activity extends AppCompatActivity {

    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;

        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    boolean proUser = false;
    static Activity main2ActivityHelper;
    int amount = 3;
    LinearLayout currentSaveSelection;
    TextView currentSample;
    boolean foldersPresent = false;
    LinearLayout selectedLyric = null;
    String colorTheme;
    HashMap<Integer, Integer> lyricFolderPairs = new HashMap<>();
    HashMap<LinearLayout, Integer> hiddenLyrics = new HashMap<>();
    // line added for admob
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView upgradeYes1 = (TextView) findViewById(R.id.upgrade_yes);
        SpannableString spannable1 = new SpannableString("$3.99 $2.99");
        spannable1.setSpan(new StrikethroughSpan(), 0, 5, 0);
        upgradeYes1.setText(spannable1);

        if (Build.FINGERPRINT.contains("generic")) {
            Toast.makeText(this, "Use test ad ID",
                    Toast.LENGTH_LONG).show();
        }

        final ConstraintLayout main2Activity = (ConstraintLayout) findViewById(R.id.main_activity2);
        final ConstraintLayout deletePopup = (ConstraintLayout) findViewById(R.id.delete_popup);
        final ConstraintLayout createFolderPopup = (ConstraintLayout) findViewById(R.id.createfolder_popup);
//        final ConstraintLayout oldSavesPopup = (ConstraintLayout) findViewById(R.id.oldsave_popup);
        final ConstraintLayout bugPopup = (ConstraintLayout) findViewById(R.id.bugfix_popup);
        final ConstraintLayout verifyPopup = (ConstraintLayout) findViewById(R.id.verify_popup);
        final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
        final TextView deleteYesPopup = (TextView) findViewById(R.id.popup_yes);
        final TextView createFolderYesPopup = (TextView) findViewById(R.id.createfolder_yes);
        final TextView createFolderNoPopup = (TextView) findViewById(R.id.createfolder_no);
        final TextView verifyYesPopup = (TextView) findViewById(R.id.verify_yes);
//        final TextView oldsaveYes = (TextView) findViewById(R.id.oldsave_yes);
//        final TextView oldsaveNo = (TextView) findViewById(R.id.oldsave_no);
        final TextView bugfixOk = (TextView) findViewById(R.id.bugfix_okay);
        final TextView upgradeYesPopup = (TextView) findViewById(R.id.upgrade_yes);
        final TextView upgradeNoPopup = (TextView) findViewById(R.id.upgrade_no);
        final TextView deleteNoPopup = (TextView) findViewById(R.id.popup_no);
        final TextView verifyNoPopup = (TextView) findViewById(R.id.verify_no);
        TextView lyricLogo = (TextView) findViewById(R.id.homescreenlyric);
        final ImageView addNew = (ImageView) findViewById(R.id.imageButton8);
        final ImageView createFolder = (ImageView) findViewById(R.id.createfolder);
        final Typeface Signika = Typeface.createFromAsset(getAssets(), "fonts/Signika-Regular.ttf");
        final Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");
        final Typeface sourceSansPro = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        final TextView sortByLabel = (TextView) findViewById(R.id.sortbylabel);
        sortByLabel.setTypeface(sourceSansPro);
        main2ActivityHelper = Main2Activity.this;
        lyricLogo.setTypeface(satisfy);
        currentSaveSelection = new LinearLayout(Main2Activity.this);
        currentSample = new TextView(Main2Activity.this);
        final LinearLayout titles = (LinearLayout) findViewById(R.id.titleselection);
        final SharedPreferences mPrefs1 = getSharedPreferences("lyricPref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor mEditor1 = mPrefs1.edit();


        String firstUseInternal = getStringFromInternal("firstusesinceinternalupdate", "TRUE");

        if (firstUseInternal.equals("TRUE")) {
            DeleteRecursive(getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE));
            putStringToInternal("firstusesinceinternalupdate", "FALSE");
            Log.d("errorcat", "erased");
        }

        String path = getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath();
        Map<String, ?> keys = mPrefs1.getAll();
        final Set<String> oldFiles = getInternalKeys();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("errorcat", entry.getKey() + ": " +
                    entry.getValue().toString());
            if(oldFiles.contains(entry.getKey())){
                mEditor1.remove(entry.getKey());
                mEditor1.commit();
            }else {
                try {
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/" + entry.getKey() + ".txt")));
                        String[] lines = mPrefs1.getString(entry.getKey(), "").split("[\\r\\n]");
                        for (String line : lines) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (ClassCastException e) {
                    File file = new File(path + "/" + entry.getKey() + ".txt");
                    boolean deleted = file.delete();
                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/" + entry.getKey() + ".txt")));
                        bufferedWriter.write(Integer.toString(mPrefs1.getInt(entry.getKey(), 0)));
                        bufferedWriter.close();
                    } catch (IOException f) {
                        f.printStackTrace();
                    }
                }
            }
        }


        //delete all user data
//        DeleteRecursive(getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE));

        colorTheme = getStringFromInternal("lyriccolortheme", "royal");
        final Toolbar topToolbar = (Toolbar) findViewById(R.id.include2);
        final Toolbar bottomToolbar = (Toolbar) findViewById(R.id.include3);

        String first1000 = getStringFromInternal("lyricfirst1000", "true");
        if (first1000.equals("true")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Main2Activity.this.getResources().openRawResource(R.raw.startrhymes)));
            String startRhymes = null;
            try {
                startRhymes = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            putStringToInternal("lyricfoundrhymes", startRhymes);


            BufferedReader readerNear = new BufferedReader(new InputStreamReader(Main2Activity.this.getResources().openRawResource(R.raw.startnearrhymes)));
            String startNearRhymes = null;
            try {
                startNearRhymes = readerNear.readLine();
                readerNear.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            putStringToInternal("lyricfoundnearrhymes", startNearRhymes);


            BufferedReader readerSyllables = new BufferedReader(new InputStreamReader(Main2Activity.this.getResources().openRawResource(R.raw.startsyllables)));
            String startSyllables = null;
            try {
                startSyllables = readerSyllables.readLine();
                readerSyllables.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            putStringToInternal("lyricfoundsyllables", startSyllables);


            putStringToInternal("lyricfirst1000", "false");

        }

        String pro = getStringFromInternal("lyricprouser", "false");

        if (pro.equals("true0518")) {
            proUser = true;
            lyricLogo.setText("Lyric Pro");
        }

        if (!proUser) {
            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mService != null) {
                            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
                            if (ownedItems.getInt("RESPONSE_CODE") == 0) {
                                if (ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST").toString().contains("lyricprouser")) {
                                    proUser = true;

                                    putStringToInternal("lyricprouser", "true0518");

                                    final ImageView addNew = (ImageView) findViewById(R.id.imageButton8);
                                    addNew.setImageResource(R.drawable.add_new);
                                    final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
                                    upgradePopup.setVisibility(View.GONE);
                                }
                            }
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 3000);

        }


        // if statement added for admob
        if (!proUser) {
            MobileAds.initialize(this, "ca-app-pub-1445128870529161~2452237328");
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(Main2Activity.this);
            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewarded(RewardItem rewardItem) {
                    int lyricIndex;

                    final Set<String> allData = getInternalKeys();
                    if (!allData.contains("counter")) {
                        putIntToInternal("counter", 0);

                    } else {
                        int incrementer = getIntFromInternal("counter", 0) + 1;
                        putIntToInternal("counter", incrementer);

                    }
                    lyricIndex = getIntFromInternal("counter", 0);
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("lyric", lyricIndex);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();

                }

                @Override
                public void onRewardedVideoAdClosed() {
                    loadRewardedVideoAd();
                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);

                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                    loadRewardedVideoAd();
                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    Log.d("errorcat", "Reward Error Code: " + i);
                }

                @Override
                public void onRewardedVideoAdLoaded() {
                }

                @Override
                public void onRewardedVideoAdOpened() {
                }

                @Override
                public void onRewardedVideoStarted() {
                }

                @Override
                public void onRewardedVideoCompleted() {
                }
            });
            loadRewardedVideoAd();
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        String trialDate = getStringFromInternal("lyricfreetriallastdate2", "");
        int trialDay = getIntFromInternal("lyricfreetrialday2", 0);
        String optedForTrial = getStringFromInternal("lyricoptedfortrial", "false");

//        if (trialDay > 0 || optedForTrial.equals("true")) {
        if ((trialDay < 8) && !proUser) {
            if (!trialDate.equals(formattedDate)) {
                amount = 2;
                SpannableString spannable = new SpannableString("$2.99 $1.99");
                spannable.setSpan(new StrikethroughSpan(), 0, 5, 0);
                final TextView upgradeTitle = (TextView) findViewById(R.id.upgrade_title);
                final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
                final TextView upgradeNo = (TextView) findViewById(R.id.upgrade_no);
                final TextView upgradeText = (TextView) findViewById(R.id.upgrade_text);
                if (trialDay < 7) {
                    upgradeTitle.setText("7-day Discount");
                    switch (trialDay) {
                        case 0:
                            upgradeText.setText("Upgrade in the next 7 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 1:
                            upgradeText.setText("Upgrade in the next 6 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 2:
                            upgradeText.setText("Upgrade in the next 5 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 3:
                            upgradeText.setText("Upgrade in the next 4 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 4:
                            upgradeText.setText("Upgrade in the next 3 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 5:
                            upgradeText.setText("Upgrade in the next 2 days and receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        case 6:
                            upgradeText.setText("Today is the last day to upgrade and still receive a discounted rate. Would you like to pay the 1-time fee and upgrade to Lyric Pro?");
                            break;
                        default:
                            break;
                    }
                    upgradeYes.setText(spannable);
                    if (trialDay != 0) {
                        upgradePopup.setVisibility(View.VISIBLE);
                    }
                    upgradeNo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            upgradePopup.setVisibility(View.GONE);
                        }
                    });
                    upgradeYes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final String sku = "lyricprouser" + amount;
                            Log.d("errorcat", sku);
                            try {
                                if (mService != null) {
                                    Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                                    } else {
                                        Toast.makeText(main2ActivityHelper, "Response Code: " + Integer.toString(buyIntentBundle.getInt("RESPONSE_CODE")),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(main2ActivityHelper, "Unable to connect to Play Store",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            }

            proUser = true;
        }
//        }

        if (colorTheme.equals("royal")) {
            deletePopup.setBackgroundResource(R.drawable.popup);
            createFolderPopup.setBackgroundResource(R.drawable.popup);
            verifyPopup.setBackgroundResource(R.drawable.popup);
            upgradePopup.setBackgroundResource(R.drawable.popup);
//            oldSavesPopup.setBackgroundResource(R.drawable.popup);
            bugPopup.setBackgroundResource(R.drawable.popup);
//            oldsaveYes.setTextColor(getResources().getColor(R.color.color1));
//            oldsaveNo.setTextColor(getResources().getColor(R.color.color1));
            bugfixOk.setTextColor(getResources().getColor(R.color.color1));
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color14));
            bottomToolbar.setBackgroundColor(getResources().getColor(R.color.color1));
            deleteYesPopup.setTextColor(getResources().getColor(R.color.color1));
            deleteNoPopup.setTextColor(getResources().getColor(R.color.color1));
            createFolderYesPopup.setTextColor(getResources().getColor(R.color.color1));
            createFolderNoPopup.setTextColor(getResources().getColor(R.color.color1));
            verifyYesPopup.setTextColor(getResources().getColor(R.color.color1));
            verifyNoPopup.setTextColor(getResources().getColor(R.color.color1));
            upgradeYesPopup.setTextColor(getResources().getColor(R.color.color1));
            upgradeNoPopup.setTextColor(getResources().getColor(R.color.color1));
        } else if (colorTheme.equals("sunset")) {
            deletePopup.setBackgroundResource(R.drawable.popup_orange);
            createFolderPopup.setBackgroundResource(R.drawable.popup_orange);
            verifyPopup.setBackgroundResource(R.drawable.popup_orange);
            upgradePopup.setBackgroundResource(R.drawable.popup_orange);
//            oldSavesPopup.setBackgroundResource(R.drawable.popup_orange);
            bugPopup.setBackgroundResource(R.drawable.popup_orange);
//            oldsaveYes.setTextColor(getResources().getColor(R.color.color4));
//            oldsaveNo.setTextColor(getResources().getColor(R.color.color4));
            bugfixOk.setTextColor(getResources().getColor(R.color.color4));
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color5));
            bottomToolbar.setBackgroundColor(getResources().getColor(R.color.color4));
            deleteYesPopup.setTextColor(getResources().getColor(R.color.color4));
            deleteNoPopup.setTextColor(getResources().getColor(R.color.color4));
            createFolderYesPopup.setTextColor(getResources().getColor(R.color.color4));
            createFolderNoPopup.setTextColor(getResources().getColor(R.color.color4));
            verifyYesPopup.setTextColor(getResources().getColor(R.color.color4));
            verifyNoPopup.setTextColor(getResources().getColor(R.color.color4));
            upgradeYesPopup.setTextColor(getResources().getColor(R.color.color4));
            upgradeNoPopup.setTextColor(getResources().getColor(R.color.color4));
        } else if (colorTheme.equals("joy")) {
            deletePopup.setBackgroundResource(R.drawable.popup_blue);
            createFolderPopup.setBackgroundResource(R.drawable.popup_blue);
            verifyPopup.setBackgroundResource(R.drawable.popup_blue);
            upgradePopup.setBackgroundResource(R.drawable.popup_blue);
//            oldSavesPopup.setBackgroundResource(R.drawable.popup_blue);
            bugPopup.setBackgroundResource(R.drawable.popup_blue);
//            oldsaveYes.setTextColor(getResources().getColor(R.color.color9));
//            oldsaveNo.setTextColor(getResources().getColor(R.color.color9));
            bugfixOk.setTextColor(getResources().getColor(R.color.color9));
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color10));
            bottomToolbar.setBackgroundColor(getResources().getColor(R.color.color9));
            deleteYesPopup.setTextColor(getResources().getColor(R.color.color9));
            deleteNoPopup.setTextColor(getResources().getColor(R.color.color9));
            createFolderYesPopup.setTextColor(getResources().getColor(R.color.color9));
            createFolderNoPopup.setTextColor(getResources().getColor(R.color.color9));
            verifyYesPopup.setTextColor(getResources().getColor(R.color.color9));
            verifyNoPopup.setTextColor(getResources().getColor(R.color.color9));
            upgradeYesPopup.setTextColor(getResources().getColor(R.color.color9));
            upgradeNoPopup.setTextColor(getResources().getColor(R.color.color9));
        } else if (colorTheme.equals("dark")) {
            deletePopup.setBackgroundResource(R.drawable.popup_black);
            createFolderPopup.setBackgroundResource(R.drawable.popup_black);
            verifyPopup.setBackgroundResource(R.drawable.popup_black);
            upgradePopup.setBackgroundResource(R.drawable.popup_black);
//            oldSavesPopup.setBackgroundResource(R.drawable.popup_black);
            bugPopup.setBackgroundResource(R.drawable.popup_black);
//            oldsaveYes.setTextColor(getResources().getColor(R.color.color16));
//            oldsaveNo.setTextColor(getResources().getColor(R.color.color16));
            bugfixOk.setTextColor(getResources().getColor(R.color.color16));
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color17));
            bottomToolbar.setBackgroundColor(getResources().getColor(R.color.color16));
            deleteYesPopup.setTextColor(getResources().getColor(R.color.color16));
            deleteNoPopup.setTextColor(getResources().getColor(R.color.color16));
            createFolderYesPopup.setTextColor(getResources().getColor(R.color.color16));
            createFolderNoPopup.setTextColor(getResources().getColor(R.color.color16));
            verifyYesPopup.setTextColor(getResources().getColor(R.color.color16));
            verifyNoPopup.setTextColor(getResources().getColor(R.color.color16));
            upgradeYesPopup.setTextColor(getResources().getColor(R.color.color16));
            upgradeNoPopup.setTextColor(getResources().getColor(R.color.color16));
        }

//        final ImageButton openOldSaves = (ImageButton) findViewById(R.id.open_saves);
//
//        openOldSaves.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldSavesPopup.setVisibility(View.VISIBLE);
//                final TextView oldSavesYes = (TextView) findViewById(R.id.oldsave_yes);
//                TextView oldSavesNo = (TextView) findViewById(R.id.oldsave_no);
//                TextView oldSavesText = (TextView) findViewById(R.id.oldsave_text);
//                LinearLayout oldSaveLinear = (LinearLayout) findViewById(R.id.old_linear);
//                ScrollView oldSaveScrollview = (ScrollView) findViewById(R.id.old_scrollview);
//                if (oldSaveLinear.getChildCount() > 0) {
//                    oldSaveLinear.removeAllViews();
//                }

//                final Set<String> allData = getInternalKeys();
//                final Map<String, Integer> unorderedData = new HashMap<>();
//                ArrayList<String> allPoems = new ArrayList<>();
//                for (String lyric : allData) {
//                    if (lyric.substring(0, lyric.length()).contains("poem") && !lyric.substring(0, lyric.length()).contains("date")) {
//                        allPoems.add(lyric);
//                    }
//                }
//                if (allPoems.size() == 0) {
//                    oldSavesText.setVisibility(View.VISIBLE);
//                    oldSaveScrollview.setVisibility(View.GONE);
//                } else {
//                    oldSavesText.setVisibility(View.GONE);
//                    oldSaveScrollview.setVisibility(View.VISIBLE);
//                }
//
//                Map<Date, LinearLayout> savesWithDates = new TreeMap<>(Collections.<Date>reverseOrder());
//                ArrayList<LinearLayout> savesWithoutDates = new ArrayList<>();
//                for (final String poem : allPoems) {
//
//                    final LinearLayout savedItem = new LinearLayout(Main2Activity.this);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    params.setMargins(0, 5, 0, 5);
//                    savedItem.setLayoutParams(params);
//                    savedItem.setOrientation(LinearLayout.VERTICAL);
//                    savedItem.setBackgroundColor(Color.WHITE);
//                    Typeface sourceSansPro = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
//                    LinearLayout row = new LinearLayout(Main2Activity.this);
//                    final TextView sample = new TextView(Main2Activity.this);
//                    final TextView savedPoem = new TextView(Main2Activity.this);
//                    TextView savedDate = new TextView(Main2Activity.this);
//                    TextView savedNumber = new TextView(Main2Activity.this);
//                    sample.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    row.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    row.setWeightSum(2);
//                    row.setOrientation(LinearLayout.HORIZONTAL);
//                    savedPoem.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    savedNumber.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//                    savedDate.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//                    sample.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    savedNumber.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//                    savedDate.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
//                    savedPoem.setMaxLines(1);
//                    savedNumber.setMaxLines(1);
//                    savedDate.setMaxLines(1);
//                    sample.setMaxLines(1);
//                    savedPoem.setTypeface(sourceSansPro);
//                    savedNumber.setTypeface(sourceSansPro);
//                    savedDate.setTypeface(sourceSansPro);
//                    sample.setTypeface(sourceSansPro);
//                    savedPoem.setPadding(45, 25, 45, 0);
//                    savedNumber.setPadding(45, 5, 15, 0);
//                    savedDate.setPadding(15, 5, 45, 0);
//                    sample.setPadding(45, 15, 45, 25);
//                    savedNumber.setTextColor(Color.BLACK);
//                    savedDate.setTextColor(Color.BLACK);
//                    savedPoem.setTextColor(Color.BLACK);
//                    savedNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                    savedDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                    savedPoem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                    sample.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//                    sample.setText(getStringFromInternal(poem, ""));
//                    savedPoem.setText(getStringFromInternal(poem.substring(0, poem.indexOf("poem")) + "title", "Untitled?"));
//                    int number = 1;
//                    if (poem.substring(poem.indexOf("poem")).matches(".*\\d+.*")) {
//                        number = Integer.parseInt(poem.substring(poem.indexOf("poem")).replaceAll("[\\D]", ""));
//                    }
//                    String numberString = "Save " + number;
//                    if (poem.contains("softsave")) {
//                        numberString = "Autosave";
//                    }
//                    Date date = null;
//                    if (!getStringFromInternal(poem + "date", "").equals("")) {
//                        savedDate.setText(getStringFromInternal(poem + "date", ""));
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss aa");
//                        try {
//                            date = simpleDateFormat.parse(getStringFromInternal(poem + "date", ""));
//                        } catch (ParseException ex) {
//                            Log.d("errorcat", "Exception " + ex);
//                        }
//                    } else {
//                        savedDate.setText("Pre-Update");
//                    }
//                    savedNumber.setText(numberString);
//
//                    row.addView(savedNumber);
//                    row.addView(savedDate);
//                    savedItem.addView(savedPoem);
//                    savedItem.addView(row);
//                    savedItem.addView(sample);
//
//                    if (date == null) {
//                        savesWithoutDates.add(savedItem);
//                    } else {
//                        savesWithDates.put(date, savedItem);
//                    }
//
//
//                    savedItem.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (savedItem != currentSaveSelection) {
//                                savedItem.setBackgroundColor(rgb(200, 200, 200));
//                                currentSaveSelection.setBackgroundColor(WHITE);
//                                currentSaveSelection = savedItem;
//                                sample.setMaxLines(1000);
//                                currentSample.setMaxLines(1);
//                                currentSample = sample;
//
//                                oldSavesYes.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//

//                                        if (getStringFromInternal("lyricprouser", "false").equals("true0518") || (titles.getChildCount() < 5)) {
//                                            for (String data : getInternalKeys()) {
//                                                if (data.substring(0, data.length()).contains("order")) {
//                                                    int incrementer = getIntFromInternal(data, 0) + 1;
//                                                    putIntToInternal(data, incrementer);
//
//                                                }
//                                            }
//                                            int lyricIndex;
//                                            if (!getInternalKeys().contains("counter")) {
//                                                putIntToInternal("counter", 0);
//
//                                            } else {
//                                                int incrementer = getIntFromInternal("counter", 0) + 1;
//                                                putIntToInternal("counter", incrementer);
//
//                                            }
//                                            lyricIndex = getIntFromInternal("counter", 0);
//                                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                                            putStringToInternal("lyric" + lyricIndex + "title", getStringFromInternal(poem.substring(0, poem.indexOf("poem")) + "title", "Untitled?"));
//                                            putStringToInternal("lyric" + lyricIndex + "poem", getStringFromInternal(poem, ""));
//                                            putStringToInternal("lyric" + lyricIndex + "poemdate", currentDateTimeString);
//                                            if (poem.contains("softsave")) {
//                                                putStringToInternal("lyric" + lyricIndex + "measures", getStringFromInternal(poem.substring(0, poem.indexOf("poem")) + "measuressoftsave", ""));
//
//                                            } else {
//                                                putStringToInternal("lyric" + lyricIndex + "measures", getStringFromInternal(poem.substring(0, poem.indexOf("poem")) + "measures", ""));
//                                            }
//
//                                            putIntToInternal("lyric" + lyricIndex + "order", 0);
//
//                                            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
//                                            Bundle b = new Bundle();
//                                            b.putInt("lyric", lyricIndex);
//                                            intent.putExtras(b);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//
//                                            oldSavesPopup.setVisibility(View.GONE);
//                                            final TextView upgradeText = (TextView) findViewById(R.id.upgrade_text);
//                                            final TextView upgradeTitle = (TextView) findViewById(R.id.upgrade_title);
//                                            if (proUser) {
//                                                amount = 2;
//                                                final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
//                                                SpannableString spannable = new SpannableString("$2.99 $1.99");
//                                                spannable.setSpan(new StrikethroughSpan(), 0, 5, 0);
//                                                upgradeYes.setText(spannable);
//                                                 }
//                                                 upgradeTitle.setText("Max Free Lyrics Reached");
//                                            upgradeText.setText("To add more Lyrics you must watch a video ad or upgrade to Lyric Pro. Would you like to pay the 1-time fee and upgrade?");
//
//                                            final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
//                                            upgradePopup.setVisibility(View.VISIBLE);
//                                            final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
//                                            final TextView upgradeNo = (TextView) findViewById(R.id.upgrade_no);
//                                            upgradeNo.setOnClickListener(new View.OnClickListener() {
//                                                public void onClick(View v) {
//                                                    upgradePopup.setVisibility(View.GONE);
//                                                    // 6 lines added for admob
////                                                    if (mRewardedVideoAd.isLoaded()) {
////                                                        mRewardedVideoAd.show();
////                                                    } else{
////                                                        Toast.makeText(main2ActivityHelper, "Video is still loading. Ensure you have internet connection and try again.",
////                                                                Toast.LENGTH_SHORT).show();
////                                                    }
//                                                }
//                                            });
//                                            upgradeYes.setOnClickListener(new View.OnClickListener() {
//                                                public void onClick(View v) {
//                                                    final String sku = "lyricprouser" + amount;
//                                                    try {
//                                                        if (mService != null) {
//                                                            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
//                                                            if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
//                                                                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
//                                                                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
//                                                            }else{
//                                                                Toast.makeText(main2ActivityHelper, "Response Code: "+ Integer.toString(buyIntentBundle.getInt("RESPONSE_CODE")),
//                                                                        Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        } else {
//                                                            Toast.makeText(main2ActivityHelper, "Unable to connect to Play Store",
//                                                                    Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    } catch (RemoteException e) {
//                                                        e.printStackTrace();
//                                                    } catch (IntentSender.SendIntentException e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//                                            });
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    });
//
//
//                }
//
//                for (LinearLayout item : savesWithDates.values()) {
//                    oldSaveLinear.addView(item);
//                }
//                for (LinearLayout item : savesWithoutDates) {
//                    oldSaveLinear.addView(item);
//                }
//                oldSavesNo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        oldSavesPopup.setVisibility(View.GONE);
//
//                    }
//                });
//                oldSavesYes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(Main2Activity.this, "Selection required",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                if (oldSaveLinear.getChildCount() <= 2) {
//                    oldSaveScrollview.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
//                } else {
//                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//                    int pixels = (int) (150 * scale + 0.5f);
//                    oldSaveScrollview.getLayoutParams().height = pixels;
//                }
//            }
//        });


        String firstUse = getStringFromInternal("firstuse", "TRUE");

        if (firstUse.equals("TRUE")){
                for (String data : getInternalKeys()) {
                    if (data.substring(0, data.length()).contains("order")) {
                        int incrementer = getIntFromInternal(data, 0) + 1;
                        putIntToInternal(data, incrementer);

                    }
                }
                int lyricIndex;
                if (!getInternalKeys().contains("counter")) {
                    putIntToInternal("counter", 0);

                } else {
                    int incrementer = getIntFromInternal("counter", 0) + 1;
                    putIntToInternal("counter", incrementer);

                }
                lyricIndex = getIntFromInternal("counter", 0);
                putStringToInternal("firstuse", "FALSE");
            putStringToInternal("firstusesinceios2", "FALSE");
            putStringToInternal("lyric" + lyricIndex + "title", "Sample");
            putStringToInternal("lyric" + lyricIndex + "poemsoftsave",
                        "The bed is red.\n" +
                                "The shoe is blue.\n" +
                                "The light is white.\n" +
                                "Red, white, and blue.\n" +
                                "Ed, Dwight, and Sue." +
                                "\n\n" +
                                "+Heading\n" +
                                "*italics*\n" +
                                "!bold!");
            putIntToInternal("lyric" + lyricIndex + "order", 0);

        } else {
            String firstUseiOS = getStringFromInternal("firstusesinceios2", "TRUE");
            if (firstUseiOS.equals("TRUE")) {
                final ConstraintLayout iospopup = (ConstraintLayout) findViewById(R.id.bugfix_popup);
                final TextView iosOk = (TextView) findViewById(R.id.bugfix_okay);

                iospopup.setVisibility(View.VISIBLE);
                iosOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        iospopup.setVisibility(View.GONE);
                        putStringToInternal("firstusesinceios2", "FALSE");

                    }
                });
            }
        }



        final Set<String> allData = getInternalKeys();
        final Map<String, Integer> unorderedData = new HashMap<>();
        String orientationMode = getStringFromInternal("lyricorientation", "NEW");
        if (orientationMode.equals("LANDSCAPE")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;



        final String sortOption = getStringFromInternal("lyrichomesortby", "REVISEDATE");
        String firstUseSortBy = getStringFromInternal("firstusesortby", "TRUE");
        if (firstUseSortBy.equals("TRUE")) {
            sortByLabel.setVisibility(View.INVISIBLE);
        }

        sortByLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String firstUse = getStringFromInternal("firstusesortby", "TRUE");
                if (firstUse.equals("TRUE")) {
                    putStringToInternal("firstusesortby", "FALSE");

                }
                if (sortOption.equals("ALPHABETICAL")) {
                    putStringToInternal("lyrichomesortby", "CREATIONDATE");

                } else if (sortOption.equals("CREATIONDATE")) {
                    putStringToInternal("lyrichomesortby", "REVISEDATE");

                } else {
                    putStringToInternal("lyrichomesortby", "ALPHABETICAL");

                }
                Intent intent = new Intent();
                intent.setClass(Main2Activity.this, Main2Activity.this.getClass());
                Main2Activity.this.startActivity(intent);
                Main2Activity.this.finish();
            }
        });

        ImageButton sortByImage = (ImageButton) findViewById(R.id.sortby);
        sortByImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortByLabel.performClick();
            }
        });

        Map<String, Integer> orderedData;
        if (sortOption.equals("ALPHABETICAL")) {
            sortByLabel.setText("A to Z ");
            Map<String, String> dataTitles = new HashMap<>();
            for (String lyric : allData) {
                if (lyric.substring(0, lyric.length()).contains("order")) {
                    final String title = getStringFromInternal(lyric.substring(0, lyric.length() - 5) + "title", "");
                    dataTitles.put(lyric, title.toLowerCase());
                }
            }
            Map<String, String> sortedTitles = MapUtil.sortByValue(dataTitles);
            int i = 0;
            for (String lyric : sortedTitles.keySet()) {
                unorderedData.put(lyric, i);
                i++;
            }

        } else if (sortOption.equals("CREATIONDATE")) {
            sortByLabel.setText("Created ");
            for (String lyric : allData) {
                if (lyric.substring(0, lyric.length()).contains("order")) {
                    unorderedData.put(lyric, Integer.parseInt(lyric.substring(5, lyric.length() - 5)));
                }
            }
        } else {
            sortByLabel.setText("Revised ");
            for (String lyric : allData) {
                if (lyric.substring(0, lyric.length()).contains("order")) {
                    unorderedData.put(lyric, getIntFromInternal(lyric, 99));
                }
            }
        }

        final Map<String, Integer> unorderedFolderData = new HashMap<>();
        Map<String, Integer> orderedFolderData;

        if (sortOption.equals("ALPHABETICAL")) {
            Map<String, String> dataFolderTitles = new HashMap<>();
            for (String data : getInternalKeys()) {
                if (data.substring(0, data.length()).contains("folder") && !data.substring(0, data.length()).contains("counter") && !data.substring(0, data.length()).contains("matches")) {
                    final String title = getStringFromInternal(data, "");
                    dataFolderTitles.put(data, title.toLowerCase());
                }
            }
            Map<String, String> sortedFolderTitles = MapUtil.sortByValue(dataFolderTitles);
            int i = 0;
            for (String data : sortedFolderTitles.keySet()) {
                unorderedFolderData.put(data, i);
                i++;
            }
        } else if (sortOption.equals("CREATIONDATE")) {
            for (String data : getInternalKeys()) {

                if (data.substring(0, data.length()).contains("folder") && !data.substring(0, data.length()).contains("counter") && !data.substring(0, data.length()).contains("matches")) {
                    unorderedFolderData.put(data, Integer.parseInt(data.substring(6)));
                }
            }
        } else {
            for (String data : getInternalKeys()) {

                if (data.substring(0, data.length()).contains("folder") && !data.substring(0, data.length()).contains("counter") && !data.substring(0, data.length()).contains("matches")) {
                    unorderedFolderData.put(data, -Integer.parseInt(data.substring(6)));
                }
            }
        }
        String foldersWithLyrics = getStringFromInternal("folderlyricmatches", "");
        if (foldersWithLyrics.contains("=")) {
            String[] foldersWithLyricsSplit = foldersWithLyrics.trim().split(" ");

            for (String combo : foldersWithLyricsSplit) {
                Integer lyric = Integer.parseInt(combo.substring(0, combo.indexOf("=")));
                Integer folder = Integer.parseInt(combo.substring(combo.indexOf("=") + 1));
                lyricFolderPairs.put(lyric, folder);
            }
        }
        orderedData = MapUtil.sortByValue(unorderedData);
        orderedFolderData = MapUtil.sortByValue(unorderedFolderData);
        for (String data : orderedFolderData.keySet()) {
            foldersPresent = true;
            final String title = getStringFromInternal(data, "Error");
            final TextView textview = new TextView(Main2Activity.this);
            ImageView tab = new ImageView(Main2Activity.this);
            ImageView filler = new ImageView(Main2Activity.this);
            final ImageButton close = new ImageButton(Main2Activity.this);
            final LinearLayout horizontal = new LinearLayout(Main2Activity.this);
            final LinearLayout.LayoutParams params;
            if (height > width) {
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        height / 9);
            } else {
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        height / 6);
            }
            params.setMargins(0, 0, 0, 5); // left, top, right, bottom
            horizontal.setLayoutParams(params);
            horizontal.setOrientation(LinearLayout.HORIZONTAL);
            horizontal.setWeightSum(5);
            horizontal.setBackgroundColor(WHITE);
            textview.setTypeface(Signika);
            textview.setTextColor(rgb(0, 0, 0));
            textview.setText(title);
            textview.setPadding(5, 0, 5, 0);
            textview.setTextSize(25);
            textview.setGravity(Gravity.LEFT);
            textview.setLines(1);
            textview.getShadowRadius();
            textview.setGravity(Gravity.CENTER_VERTICAL);
            textview.setTypeface(Signika);
            LinearLayout.LayoutParams textViewParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.7f));
            textview.setLayoutParams(textViewParams);
            textview.setMaxLines(1);
            LinearLayout.LayoutParams addLyricsFillerParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, .3f));
            filler.setLayoutParams(addLyricsFillerParams);
            close.setImageResource(R.drawable.homecloseicon);
            close.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams closeParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            close.setLayoutParams(closeParams);
            close.setBackgroundColor(TRANSPARENT);
            horizontal.addView(filler);
            horizontal.addView(textview);
            horizontal.addView(close);

            tab.setImageResource(R.drawable.folder_tab);
            tab.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams tabParams;
            if (height > width) {
                tabParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / 45);
            } else {
                tabParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / 30);
            }
            tabParams.setMargins(0, 5, 0, 0);
            tab.setImageAlpha(190);
            tab.setLayoutParams(tabParams);

            titles.addView(tab);
            titles.addView(horizontal);

            final Intent myIntent = new Intent(this, Main2Activity.class);

            final String finalData = data;
            close.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!deletePopup.isShown()) {

                        final TextView yes = (TextView) findViewById(R.id.popup_yes);
                        final TextView no = (TextView) findViewById(R.id.popup_no);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deletePopup.setVisibility(View.VISIBLE);
                                final TextView deleteText = (TextView) findViewById(R.id.delete_text);
                                deleteText.setText("Do you really want to delete \"" + title + "\"?");
                                yes.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        verifyPopup.setVisibility(View.VISIBLE);
                                        deletePopup.setVisibility(View.GONE);
                                        final TextView verifyYes = (TextView) findViewById(R.id.verify_yes);
                                        final TextView verifyNo = (TextView) findViewById(R.id.verify_no);
                                        final TextView verifyText = (TextView) findViewById(R.id.verify_text);
                                        verifyText.setText("This is permanent. Are you sure?");
                                        verifyYes.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {

                                                removeFromInternal(finalData);

                                                String foldersWithLyrics = getStringFromInternal("folderlyricmatches", "");
                                                if (foldersWithLyrics.contains("=")) {
                                                    String newFoldersWithLyrics = "";
                                                    String[] foldersWithLyricsSplit = foldersWithLyrics.trim().split(" ");
                                                    for (String combo : foldersWithLyricsSplit) {
                                                        if (!combo.contains("=" + finalData.substring(6))) {
                                                            newFoldersWithLyrics = newFoldersWithLyrics + " " + combo;
                                                        }
                                                    }
                                                    putStringToInternal("folderlyricmatches", newFoldersWithLyrics);

                                                }
                                                startActivity(myIntent);
                                                finish();
                                            }
                                        });
                                        verifyNo.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                deletePopup.setVisibility(View.GONE);
                                                verifyPopup.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                });
                                no.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        deletePopup.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                }
            });

            textview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (selectedLyric != null) {
                        String oldlyricfoldermatches = getStringFromInternal("folderlyricmatches", "");
                        putStringToInternal("folderlyricmatches", oldlyricfoldermatches + " " + selectedLyric.getChildAt(2).getId() + "=" + finalData.substring(6));

                        Intent intent = new Intent();
                        intent.setClass(Main2Activity.this, Main2Activity.this.getClass());
                        Main2Activity.this.startActivity(intent);
                        Main2Activity.this.finish();
                    } else {

                        for (LinearLayout hiddenLyric : hiddenLyrics.keySet()) {
                            if (hiddenLyrics.get(hiddenLyric) == Integer.parseInt(finalData.substring(6))) {
                                if (hiddenLyric.isShown()) {
                                    hiddenLyric.setVisibility(View.GONE);
                                    horizontal.setLayoutParams(params);
                                    params.setMargins(0, 0, 0, 5);
                                    horizontal.setLayoutParams(params);
                                } else {
                                    hiddenLyric.setVisibility(View.VISIBLE);
                                    horizontal.setLayoutParams(params);
                                    params.setMargins(0, 0, 0, 10);
                                    horizontal.setLayoutParams(params);
                                }
                            }
                        }
                    }
                }
            });
            if (lyricFolderPairs.values().contains(Integer.parseInt(data.substring(6)))) {
                final Map<String, Integer> unorderedInFolderData = new HashMap<>();
                Map<String, Integer> orderedInFolderData;
                if (sortOption.equals("ALPHABETICAL")) {
                    Map<String, String> dataInFolderTitles = new HashMap<>();
                    for (Integer lyric : lyricFolderPairs.keySet()) {
                        if (lyricFolderPairs.get(lyric) == Integer.parseInt(data.substring(6))) {
                            String lyricString = "lyric" + lyric + "order";
                            final String lyricTitle = getStringFromInternal(lyricString.substring(0, lyricString.length() - 5) + "title", "");
                            dataInFolderTitles.put(lyricString, lyricTitle.toLowerCase());
                        }
                    }
                    Map<String, String> sortedInFolderTitles = MapUtil.sortByValue(dataInFolderTitles);
                    int i = 0;
                    for (String lyric : sortedInFolderTitles.keySet()) {
                        unorderedInFolderData.put(lyric, i);
                        i++;
                    }
                } else if (sortOption.equals("CREATIONDATE")) {
                    for (Integer lyric : lyricFolderPairs.keySet()) {
                        if (lyricFolderPairs.get(lyric) == Integer.parseInt(data.substring(6))) {
                            String lyricString = "lyric" + lyric + "order";
                            unorderedInFolderData.put(lyricString, lyric);
                        }
                    }
                } else {
                    for (Integer lyric : lyricFolderPairs.keySet()) {
                        if (lyricFolderPairs.get(lyric) == Integer.parseInt(data.substring(6))) {
                            String lyricString = "lyric" + lyric + "order";
                            unorderedInFolderData.put(lyricString, getIntFromInternal(lyricString, 99));
                        }
                    }
                }
                orderedInFolderData = MapUtil.sortByValue(unorderedInFolderData);
                for (String lyric : orderedInFolderData.keySet()) {
                    addLyric(lyric, true, Integer.parseInt(data.substring(6)));
                }
            }
        }
        for (String data : orderedData.keySet()) {
            if (!lyricFolderPairs.keySet().contains(Integer.parseInt(data.substring(5, data.length() - 5)))) {
                if (Integer.parseInt(data.substring(5, data.length() - 5)) != -1) {
                    addLyric(data, false, -1);
                }
            }
        }

        createFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String lyricprouser = getStringFromInternal("lyricprouser", "false");
                if (!lyricprouser.equals("true0518") && foldersPresent) {
                    final TextView upgradeText = (TextView) findViewById(R.id.upgrade_text);
                    final TextView upgradeTitle = (TextView) findViewById(R.id.upgrade_title);
                    if (proUser) {
                        amount = 2;
                        final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
                        SpannableString spannable = new SpannableString("$2.99 $1.99");
                        spannable.setSpan(new StrikethroughSpan(), 0, 5, 0);
                        upgradeYes.setText(spannable);
                    }
                    upgradeTitle.setText("Lyric Pro");
                    upgradeText.setText("Adding more folders requires Lyric Pro. Would you like to pay the 1-time fee and upgrade?");

                    final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
                    upgradePopup.setVisibility(View.VISIBLE);
                    final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
                    final TextView upgradeNo = (TextView) findViewById(R.id.upgrade_no);
                    upgradeNo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            upgradePopup.setVisibility(View.GONE);
                        }
                    });
                    upgradeYes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final String sku = "lyricprouser" + amount;
                            try {
                                if (mService != null) {
                                    Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                                    } else {
                                        Toast.makeText(main2ActivityHelper, "Response Code: " + Integer.toString(buyIntentBundle.getInt("RESPONSE_CODE")),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(main2ActivityHelper, "Unable to connect to Play Store",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    final EditText createFolderTitle = (EditText) findViewById(R.id.createfoldertitleinput);
                    createFolderPopup.setVisibility(View.VISIBLE);
                    createFolderYesPopup.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (!createFolderTitle.getText().toString().trim().equals("")) {

                                int folderIndex;
                                if (!allData.contains("foldercounter")) {
                                    putIntToInternal("foldercounter", 0);

                                } else {
                                    int incrementer = getIntFromInternal("foldercounter", 0) + 1;
                                    putIntToInternal("foldercounter", incrementer);

                                }
                                folderIndex = getIntFromInternal("foldercounter", 0);
                                putStringToInternal("folder" + folderIndex, createFolderTitle.getText().toString());

                                Intent intent = new Intent();
                                intent.setClass(Main2Activity.this, Main2Activity.this.getClass());
                                Main2Activity.this.startActivity(intent);
                                Main2Activity.this.finish();
                            } else {
                                Toast.makeText(main2ActivityHelper, "Folder Title Required",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    createFolderNoPopup.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            createFolderPopup.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!deletePopup.isShown()) {


                    int openCount = getIntFromInternal("lyricopencount", 0);
                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) + 1);

                    // if added for admob
                    String lyricprouser = getStringFromInternal("lyricprouser", "false");

                    if (lyricprouser.equals("true0518") || (titles.getChildCount() <= 5) || !isNetworkAvailable() || (openCount % 10) != 0) {

                        int lyricIndex;
                        if (!allData.contains("counter")) {
                            putIntToInternal("counter", 0);

                        } else {
                            int incrementer = getIntFromInternal("counter", 0) + 1;
                            putIntToInternal("counter", incrementer);

                        }
                        lyricIndex = getIntFromInternal("counter", 0);
                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("lyric", lyricIndex);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();

                    } else {
//                      added for admob
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                        } else {
                            if (openCount % 10 == 0) {
                                putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);

                            }
                            int lyricIndex;
                            if (!allData.contains("counter")) {
                                putIntToInternal("counter", 0);

                            } else {
                                int incrementer = getIntFromInternal("counter", 0) + 1;
                                putIntToInternal("counter", incrementer);

                            }
                            lyricIndex = getIntFromInternal("counter", 0);
                            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                            Bundle b = new Bundle();
                            b.putInt("lyric", lyricIndex);
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    void addLyric(final String data, final boolean inFolder, Integer folder) {
        final Typeface Signika = Typeface.createFromAsset(getAssets(), "fonts/Signika-Regular.ttf");
        final LinearLayout titles = (LinearLayout) findViewById(R.id.titleselection);

        final ConstraintLayout deletePopup = (ConstraintLayout) findViewById(R.id.delete_popup);
        final ConstraintLayout verifyPopup = (ConstraintLayout) findViewById(R.id.verify_popup);
        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        String lyric = data.substring(0, data.length() - 5) + "title";
        final TextView textview = new TextView(Main2Activity.this);
        textview.setId(Integer.parseInt(lyric.replaceAll("[\\D]", "")));
        final String title = getStringFromInternal(lyric, "");

        ImageView addLyrics = new ImageView(Main2Activity.this);
        ImageView addLyricsFiller = new ImageView(Main2Activity.this);
        final ImageButton close = new ImageButton(Main2Activity.this);
        final LinearLayout horizontal = new LinearLayout(Main2Activity.this);
        LinearLayout.LayoutParams params;
        if (height > width) {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    height / 9);
        } else {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    height / 6);
        }
        params.setMargins(0, 5, 0, 0); // left, top, right, bottom
        horizontal.setLayoutParams(params);
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        horizontal.setWeightSum(5);
        horizontal.setBackgroundColor(WHITE);
        if (colorTheme.equals("royal")) {
            addLyrics.setImageResource(R.drawable.addlyricstofolder);
        } else if (colorTheme.equals("sunset")) {
            addLyrics.setImageResource(R.drawable.addlyricstofolder_orange);
        } else if (colorTheme.equals("joy")) {
            addLyrics.setImageResource(R.drawable.addlyricstofolder_blue);
        } else if (colorTheme.equals("dark")) {
            addLyrics.setImageResource(R.drawable.addlyricstofolder_black);
        }

        addLyrics.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams addLyricsParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, .7f));
        addLyrics.setLayoutParams(addLyricsParams);
        LinearLayout.LayoutParams addLyricsFillerParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, .3f));
        addLyricsFiller.setLayoutParams(addLyricsFillerParams);
        textview.setTypeface(Signika);
        textview.setTextColor(rgb(0, 0, 0));
        textview.setText(title);
        textview.setPadding(5, 0, 5, 0);
        textview.setTextSize(25);
        textview.setGravity(Gravity.LEFT);
        textview.setLines(1);
        textview.getShadowRadius();
        textview.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams textViewParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.3f));
        textview.setLayoutParams(textViewParams);
        textview.setMaxLines(1);
        close.setId(textview.getId() + 12345);
        close.setImageResource(R.drawable.homecloseicon);
        close.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams closeParams = (new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        close.setLayoutParams(closeParams);
        close.setBackgroundColor(TRANSPARENT);
        horizontal.addView(addLyricsFiller);
        horizontal.addView(addLyrics);
        horizontal.addView(textview);
        horizontal.addView(close);
        titles.addView(horizontal);
        final Integer finalFolder = folder;

        if (inFolder) {

            params.setMargins(0, 0, 0, 5);
            horizontal.setLayoutParams(params);
            hiddenLyrics.put(horizontal, folder);
            horizontal.setVisibility(View.GONE);
            horizontal.setAlpha(0.7f);
            if (colorTheme.equals("royal")) {
                addLyrics.setImageResource(R.drawable.removelyricsfromfolder);
            } else if (colorTheme.equals("sunset")) {
                addLyrics.setImageResource(R.drawable.removelyricsfromfolder_orange);
            } else if (colorTheme.equals("joy")) {
                addLyrics.setImageResource(R.drawable.removelyricsfromfolder_blue);
            } else if (colorTheme.equals("dark")) {
                addLyrics.setImageResource(R.drawable.removelyricsfromfolder_black);
            }
            addLyricsFiller.setVisibility(View.GONE);

            addLyrics.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String foldersWithLyrics = getStringFromInternal("folderlyricmatches", "");

                    if (foldersWithLyrics.contains(" " + textview.getId() + "=" + finalFolder)) {
                        String stringToRemove = " " + textview.getId() + "=" + finalFolder;
                        foldersWithLyrics = (foldersWithLyrics.substring(0, foldersWithLyrics.indexOf(stringToRemove)) + foldersWithLyrics.substring(foldersWithLyrics.indexOf(stringToRemove) + stringToRemove.length()));

                        putStringToInternal("folderlyricmatches", foldersWithLyrics);


                        Intent intent = new Intent();
                        intent.setClass(Main2Activity.this, Main2Activity.this.getClass());
                        Main2Activity.this.startActivity(intent);
                        Main2Activity.this.finish();

                    }
                }
            });
        } else {
            if (!foldersPresent) {
                addLyrics.setVisibility(View.GONE);
                textview.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.7f));
            } else {
                addLyricsFiller.setVisibility(View.GONE);
            }

            addLyrics.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (selectedLyric == horizontal) {
                        selectedLyric.setBackgroundColor(WHITE);
                        selectedLyric = null;
                    } else if (selectedLyric != null) {
                        selectedLyric.setBackgroundColor(WHITE);
                        selectedLyric = horizontal;
                        selectedLyric.setBackgroundColor(rgb(220, 220, 220));
                    } else {
                        selectedLyric = horizontal;
                        selectedLyric.setBackgroundColor(rgb(220, 220, 220));
                    }
                }
            });
        }
        final Intent myIntent = new Intent(this, Main2Activity.class);
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!deletePopup.isShown()) {

                    final TextView yes = (TextView) findViewById(R.id.popup_yes);
                    final TextView no = (TextView) findViewById(R.id.popup_no);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deletePopup.setVisibility(View.VISIBLE);
                            final TextView deleteText = (TextView) findViewById(R.id.delete_text);
                            deleteText.setText("Do you really want to delete \"" + title + "\"?");
                            yes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    verifyPopup.setVisibility(View.VISIBLE);
                                    deletePopup.setVisibility(View.GONE);
                                    final TextView verifyYes = (TextView) findViewById(R.id.verify_yes);
                                    final TextView verifyNo = (TextView) findViewById(R.id.verify_no);
                                    final TextView verifyText = (TextView) findViewById(R.id.verify_text);
                                    verifyText.setText("This is permanent. Are you sure?");
                                    verifyYes.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {

                                            removeFromInternal("lyric" + textview.getId() + "title");
                                            removeFromInternal("lyric" + textview.getId() + "poem");
                                            removeFromInternal("lyric" + textview.getId() + "order");
                                            removeFromInternal("lyric" + textview.getId() + "notes");
                                            removeFromInternal("lyric" + textview.getId() + "measures");
                                            removeFromInternal("lyric" + textview.getId() + "poem");
                                            removeFromInternal("lyric" + textview.getId() + "poem2");
                                            removeFromInternal("lyric" + textview.getId() + "poem3");
                                            removeFromInternal("lyric" + textview.getId() + "poem4");
                                            removeFromInternal("lyric" + textview.getId() + "poem5");
                                            removeFromInternal("lyric" + textview.getId() + "poemdate");
                                            removeFromInternal("lyric" + textview.getId() + "poem2date");
                                            removeFromInternal("lyric" + textview.getId() + "poem3date");
                                            removeFromInternal("lyric" + textview.getId() + "poem4date");
                                            removeFromInternal("lyric" + textview.getId() + "poem5date");
                                            removeFromInternal("lyric" + textview.getId() + "measures");
                                            removeFromInternal("lyric" + textview.getId() + "measures2");
                                            removeFromInternal("lyric" + textview.getId() + "measures3");
                                            removeFromInternal("lyric" + textview.getId() + "measures4");
                                            removeFromInternal("lyric" + textview.getId() + "measures5");
                                            removeFromInternal("lyric" + textview.getId() + "poemsoftsavedate");
                                            removeFromInternal("lyric" + textview.getId() + "poemsoftsave");
                                            removeFromInternal("lyricsavedrecordings" + textview.getId());


                                            File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(textview.getId()), Context.MODE_PRIVATE).getAbsolutePath());
                                            if (dir.isDirectory()) {
                                                String[] children = dir.list();
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(dir, children[i]).delete();
                                                }
                                            }

                                            if (inFolder) {
                                                String foldersWithLyrics = getStringFromInternal("folderlyricmatches", "");
                                                if (foldersWithLyrics.contains(" " + textview.getId() + "=" + finalFolder)) {
                                                    String stringToRemove = " " + textview.getId() + "=" + finalFolder;
                                                    foldersWithLyrics = (foldersWithLyrics.substring(0, foldersWithLyrics.indexOf(stringToRemove)) + foldersWithLyrics.substring(foldersWithLyrics.indexOf(stringToRemove) + stringToRemove.length()));
                                                    putStringToInternal("folderlyricmatches", foldersWithLyrics);

                                                }
                                            }
                                            startActivity(myIntent);
                                            finish();
                                        }
                                    });
                                    verifyNo.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            deletePopup.setVisibility(View.GONE);
                                            verifyPopup.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    deletePopup.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
                if (!deletePopup.isShown()) {

                    final TextView yes = (TextView) findViewById(R.id.popup_yes);
                    final TextView no = (TextView) findViewById(R.id.popup_no);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deletePopup.setVisibility(View.VISIBLE);
                            final TextView deleteText = (TextView) findViewById(R.id.delete_text);
                            deleteText.setText("Do you really want to delete \"" + title + "\"?");
                            yes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    verifyPopup.setVisibility(View.VISIBLE);
                                    deletePopup.setVisibility(View.GONE);
                                    final TextView verifyYes = (TextView) findViewById(R.id.verify_yes);
                                    final TextView verifyNo = (TextView) findViewById(R.id.verify_no);
                                    final TextView verifyText = (TextView) findViewById(R.id.verify_text);
                                    verifyText.setText("This is permanent. Are you sure?");
                                    verifyYes.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {

                                            removeFromInternal("lyric" + textview.getId() + "title");
                                            removeFromInternal("lyric" + textview.getId() + "poem");
                                            removeFromInternal("lyric" + textview.getId() + "order");
                                            removeFromInternal("lyric" + textview.getId() + "notes");
                                            removeFromInternal("lyric" + textview.getId() + "measures");
                                            removeFromInternal("lyric" + textview.getId() + "poem");
                                            removeFromInternal("lyric" + textview.getId() + "poem2");
                                            removeFromInternal("lyric" + textview.getId() + "poem3");
                                            removeFromInternal("lyric" + textview.getId() + "poem4");
                                            removeFromInternal("lyric" + textview.getId() + "poem5");
                                            removeFromInternal("lyric" + textview.getId() + "poemdate");
                                            removeFromInternal("lyric" + textview.getId() + "poem2date");
                                            removeFromInternal("lyric" + textview.getId() + "poem3date");
                                            removeFromInternal("lyric" + textview.getId() + "poem4date");
                                            removeFromInternal("lyric" + textview.getId() + "poem5date");
                                            removeFromInternal("lyric" + textview.getId() + "measuressoftsave");
                                            removeFromInternal("lyric" + textview.getId() + "measures2");
                                            removeFromInternal("lyric" + textview.getId() + "measures3");
                                            removeFromInternal("lyric" + textview.getId() + "measures4");
                                            removeFromInternal("lyric" + textview.getId() + "measures5");
                                            removeFromInternal("lyric" + textview.getId() + "poemsoftsavedate");
                                            removeFromInternal("lyric" + textview.getId() + "poemsoftsave");
                                            removeFromInternal("lyricsavedrecordings" + textview.getId());
                                            removeFromInternal("lyricmetronomebpm" + textview.getId());
                                            removeFromInternal("lyricmetronomeaccent" + textview.getId());


                                            File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(textview.getId()), Context.MODE_PRIVATE).getAbsolutePath());
                                            if (dir.isDirectory()) {
                                                String[] children = dir.list();
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(dir, children[i]).delete();
                                                }
                                            }

                                            startActivity(myIntent);
                                            finish();
                                        }
                                    });
                                    verifyNo.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            deletePopup.setVisibility(View.GONE);
                                            verifyPopup.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    deletePopup.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            }
        });

        textview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!deletePopup.isShown()) {


                    int openCount = getIntFromInternal("lyricopencount", 0);
                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) + 1);

                    String lyricprouser = getStringFromInternal("lyricprouser", "false");

                    if (lyricprouser.equals("true0518") || (titles.getChildCount() <= 5) || !isNetworkAvailable() || (openCount % 10) != 0) {
                        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("lyric", textview.getId());
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();

                    } else {
//                      added for admob
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                                @Override
                                public void onRewarded(RewardItem rewardItem) {
                                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                                    Bundle b = new Bundle();
                                    b.putInt("lyric", textview.getId());
                                    intent.putExtras(b);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onRewardedVideoAdClosed() {
                                    loadRewardedVideoAd();
                                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);

                                }

                                @Override
                                public void onRewardedVideoAdLeftApplication() {
                                    loadRewardedVideoAd();
                                    putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);

                                }

                                @Override
                                public void onRewardedVideoAdFailedToLoad(int i) {
                                    Log.d("errorcat", "Reward Error Code: " + i);
                                }

                                @Override
                                public void onRewardedVideoAdLoaded() {
                                }

                                @Override
                                public void onRewardedVideoAdOpened() {
                                }

                                @Override
                                public void onRewardedVideoStarted() {
                                }

                                @Override
                                public void onRewardedVideoCompleted() {
                                }
                            });
                            mRewardedVideoAd.show();
                        } else {
                            if ((openCount % 10) == 0) {
                                putIntToInternal("lyricopencount", getIntFromInternal("lyricopencount", 0) - 1);


                            }
                            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                            Bundle b = new Bundle();
                            b.putInt("lyric", textview.getId());
                            intent.putExtras(b);
                            startActivity(intent);
                            finish();
                        }

                    }

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {

                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    proUser = true;


                    putStringToInternal("lyricprouser", "true0518");
                    Toast.makeText(main2ActivityHelper, "Upgrade Successful",
                            Toast.LENGTH_SHORT).show();

                    final ImageView addNew = (ImageView) findViewById(R.id.imageButton8);
                    addNew.setImageResource(R.drawable.add_new);
                    final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
                    upgradePopup.setVisibility(View.GONE);
                } catch (JSONException e) {

                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // method added for admob
    private void loadRewardedVideoAd() {
        String adID = "ca-app-pub-1445128870529161/4638635704";
        if (Build.FINGERPRINT.contains("generic")) {
            adID = "ca-app-pub-3940256099942544/5224354917";
        }
        mRewardedVideoAd.loadAd(adID,
                new AdRequest.Builder().build());
    }
    void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }

    String getStringFromInternal(String fileName, String defaultvalue){
        String value = "";
        File file = new File(getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath() + "/" + fileName + ".txt");
        if(!file.exists()){
            return defaultvalue;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try {
                String line;
                boolean firstLine = true;
                while( (line = reader.readLine()) != null){
                    if(firstLine){
                        value += line;
                        firstLine = false;
                    }else{
                        value += "\n" + line;
                    }
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    int getIntFromInternal(String fileName, int defaultvalue){
        String value = "";
        File file = new File(getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath() + "/" + fileName + ".txt");
        if(!file.exists()){
            return defaultvalue;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            try {
                String line;
                boolean firstLine = true;
                while( (line = reader.readLine()) != null){
                    if(firstLine){
                        value += line;
                        firstLine = false;
                    }else{
                        value += "\n" + line;
                    }
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(value);
    }

    void putIntToInternal(String fileName, int value){

        String path = getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/" + fileName + ".txt")));
            bufferedWriter.write(Integer.toString(value, 0));
            bufferedWriter.close();
        }catch (IOException f){
            f.printStackTrace();
        }
    }

    void putStringToInternal(String fileName, String value){

        String path = getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/" + fileName + ".txt")));
            String[] lines =  value.split("[\\r\\n]");
            for(String line: lines){
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    void removeFromInternal(String fileName) {
        String path = getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE).getAbsolutePath();
        File file = new File(path + "/" + fileName + ".txt");
        boolean deleted = file.delete();
    }

    Set<String> getInternalKeys() {
        Set<String> keys = new HashSet<String>();
        File yourDir = getApplicationContext().getDir("LyricLocalBackup", Context.MODE_PRIVATE);
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                keys.add(f.getName().substring(0, f.getName().length() - 4));
            }
        }
        return keys;
    }

}

//Author Kenny Mcilvaine

package kmcilvai.perfectpoet;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import android.os.Handler;

import com.android.vending.billing.IInAppBillingService;
// 3 lines for admob
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.argb;
import static android.graphics.Color.rgb;
import static kmcilvai.perfectpoet.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {
    //used to check if poem changed
    String poemChangeChecker = "";
    String wordChangeChecker = "";
    //lets us know which lines have inaccurate syllable counts
    ArrayList<Integer> flaggedLines = new ArrayList<>();
    //all the words syllables have already been retrieved for.
    HashMap<String, String> foundWordSyllables = new HashMap<>();
    //all the words rhymes have already been found for
    HashMap<String, ArrayList<String>> foundWordRhymes = new HashMap<>();
    HashMap<String, ArrayList<String>> foundWordNearRhymes = new HashMap<>();
    //the current location of each word currently in poem
    TreeMap<Integer, String> currentWordIndexes = new TreeMap<>();
    HashMap<Integer, String> currentLastWordIndexes = new HashMap<>();
    HashMap<String, Integer> savedRecordings = new HashMap<>();
    ArrayList<String> ignoredWords = new ArrayList<>();
    HashMap<String, ArrayList<String>> createdRhymes = new HashMap<>();
    SpannableString nextSpannable;
    //Spinner dropdown;
    TextView dropdownOutput;
    //used to figure out which lines need to be flagged
    int lineCount = 0;
    //all the colors used for rhyming words
    //needs more colors still
    int[] colors = {
            50, 50, 255, //blue
            255, 193, 7, //light orange
            126, 87, 194, //light purple
            100, 240, 100, //green
            77, 208, 225, //light turquiose
            255, 0, 0, //red
            104, 159, 56, //grass green
            236, 64, 122, //light pink
            13, 71, 161, //aqua
            0, 137, 123, //light teal
            139, 119, 23, //pea green
            106, 7, 49, //pink
            171, 51, 148, //light pink/purple
            74, 20, 140, //purple
            92, 107, 192, //light blue
            239, 83, 80, //salmon
            41, 182, 246, //light aqua
            0, 96, 100, //turquiose
            67, 160, 71, //light green
            205, 220, 57, //light pea green
            255, 111, 0, //orange

    };
    boolean firstMeasureModeUse = true;
    boolean typing = false;
    boolean measureTyping = false;
    boolean measureChanged = false;
    HashMap<TextView, String> textviewsToChange = new HashMap<>();
    ArrayList<TextView> flaggedMeasureTextviews = new ArrayList<>();
    boolean processing = false;
    boolean offlineBool = false;
    boolean globalForceOffline = false;
    //we need the local variables so that the changes are not made until done is hit fort he revise rate popup
    boolean localForceOffline = false;
    //keeps track of which color was used last
    int colorCounter = 0;
    int lyricIndex;
    long delay = 2000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    long last_measure_edit = 0;
    int fontsize;
    String colorTheme;
    int screenHeight;
    int screenWidth;
    boolean notesFocus = false;
    String searchWord = "";
    String spinnerSelection = "Find Exact Rhymes";
    String measureSpinnerSelection = "Add New Measure";
    String lengthSpinnerSelection = "";
    String pitchSpinnerSelection = "";
    String pitchSpinnerPitch = "";
    static EditText inputWordSelectionHelper;
    static EditText poemSelectionHelper;
    static EditText notesSelectionHelper;
    static TextView wordInfoSelectionHelper;
    static Activity mainActivityHelper;
    static ConstraintLayout mainActivityLayoutHelper;
    static TextView wordInfoHelper;
    static Toolbar spinnerToolbarHelper;
    static ScrollView scrollviewHelper;
    static Toolbar measureToolbarHelper;
    static EditText titleHelper;
    static Toolbar mainToolbarHelper;
    static TextView notesTitleHelper;
    static TextView wordInfoTitleHelper;
    static ImageButton exitMeasureModeHelper;
    //    static boolean hideToolbar = false;
    static boolean keyboardOpen = false;
    boolean lastWords = false;
    boolean highSensitivity = false;
    boolean copyMeasures = false;
    boolean firstRun = true;
    boolean metronomeRunning = false;
    boolean recorderRunning = false;
    boolean editRecordingPlaying = false;
    boolean lineRecordingPlaying = false;
    boolean flipped = false;
    boolean first = true;
    MediaPlayer mediaPlayerEdit;
    MediaPlayer mediaPlayerLine;
    MediaPlayer userFileMediaPlayer;
    boolean audioFileRunning = false;
    boolean backgroundMetronome = false;
    boolean measureMetronome = false;
    boolean scrollMetronome = false;
    boolean noteMetronome = false;
    LinearLayout previousRow;
    LinearLayout currentSaveSelection;
    LinearLayout previousSylRow;
    LinearLayout selectedBars;
    ImageView selectedSixteenth;
    TextView previousTextView;
    TextView previousPreviousTextView;
    String savedMeasures = null;
    AudioManager audio;
    //    Timer taskTimer;
    boolean proUser = false;
    boolean topToolbarVisible = true;
    boolean bottomToolbarVisible = true;
    int topToolbarHeight = 0;
    int bottomToolbarHeight = 0;
    int upgradeAmount = 3;
    SpannableString upgradeSpannable = new SpannableString("$3.99 $2.99");
    String undoStringPrevious;
    String undoStringCurrent;
    int undoStringPreviousPosition;
    int undoStringCurrentPosition;
    boolean warningCheckbox = false;
    boolean newRecordingCheckbox = false;
    boolean nearRhymesCheckbox = false;
    //    CallbackManager callbackManager;
    MediaRecorder mediaRecorder;
    ImageView popupBackground;
    ArrayList<TextView> linesWithRecordings;
    HashMap<LinearLayout, String> allBars;
    HashMap<Integer, TextView> linesWithoutRecordings;
    String recordingToEdit = "Untitled.3gp";
    boolean nextRecordingIsNew = false;
    boolean barsCurrentlySelected = false;
    HashMap<Integer, Integer> headings = new HashMap<>();
    ArrayList<String> boldWords = new ArrayList<>();
    ArrayList<String> italicWords = new ArrayList<>();
    int globalAccentCounter = 0;
    int globalMeasureCounter = 0;
    int globalScrollCounter = 0;
    int globalNoteCounter = 0;
    boolean seekbarMovedWhilePlaying = false;
    boolean recordingCopied = false;
    boolean foreignUser = false;
    Timer pulseTimer = new Timer();
    int spinnerCount = 0;
    Handler seekBarHandler = new Handler();
    int sampleRate = 48000;
    int mediaPlayerPosition = 0;
    AsyncSyllableCount AsyncSyllableCountThread = new AsyncSyllableCount();
    AsyncMeasureSyllableCount AsyncMeasureSyllableCountThread = new AsyncMeasureSyllableCount();
    int defaultFontSize = 15;
    // 2 lines added for admob
    private AdView mAdView;
    pl.droidsonroids.gif.GifImageView adLoading;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(activity_main);


        upgradeSpannable.setSpan(new StrikethroughSpan(), 0, 5, 0);


        final EditText poem = (EditText) findViewById(R.id.poem);
        poemSelectionHelper = poem;
        notesSelectionHelper = (EditText) findViewById(R.id.notes);
        wordInfoSelectionHelper = (TextView) findViewById(R.id.wordInfo);
        final LinearLayout spinnerList = (LinearLayout) findViewById(R.id.spinner_list);
        final LinearLayout measureList = (LinearLayout) findViewById(R.id.measure_list);
        final LinearLayout lengthList = (LinearLayout) findViewById(R.id.measure_bars_length_list);
        final LinearLayout pitchList = (LinearLayout) findViewById(R.id.measure_bars_pitch_list);
        final LinearLayout settingList = (LinearLayout) findViewById(R.id.settings_list);
        final EditText title = (EditText) findViewById(R.id.title);
        titleHelper = title;
        final EditText inputWord = (EditText) findViewById(R.id.inputWord);
        inputWordSelectionHelper = inputWord;
        final TextView wordInfo = (TextView) findViewById(R.id.wordInfo);
        final TextView syllables = (TextView) findViewById(R.id.syllables);
        final TextView options = (TextView) findViewById(R.id.optionsSpinner);
        final TextView measureToolbarText = (TextView) findViewById(R.id.select_words_measure);
        final ImageButton measureGo = (ImageButton) findViewById(R.id.go_measure);
        final ImageButton measureNotesGo = (ImageButton) findViewById(R.id.go_note);
        final ImageButton optionsButton = (ImageButton) findViewById(R.id.settings);
        final ImageButton openSpinner = (ImageButton) findViewById(R.id.open_spinner);
        final ImageButton openMeasureSpinner = (ImageButton) findViewById(R.id.open_measure_spinner);
        final ImageButton openNotePitch = (ImageButton) findViewById(R.id.open_note_pitch);
        final ImageButton openNoteLength = (ImageButton) findViewById(R.id.open_note_length);
        final Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        mainToolbarHelper = mainToolbar;

        final Toolbar spinnerToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
        final Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
        spinnerToolbarHelper = spinnerToolbar;
        final Toolbar measureToolbar = (Toolbar) findViewById(R.id.measure_toolbar);
        measureToolbarHelper = measureToolbar;
        final ImageButton closeNotes = (ImageButton) findViewById(R.id.close_notes);
        final ImageButton closeWordInfo = (ImageButton) findViewById(R.id.close_wordinfo);
        final TextView notesTitle = (TextView) findViewById(R.id.notes_title);
        notesTitleHelper = notesTitle;
        final View notesView = (View) findViewById(R.id.view4);
        final View wordInfoView = (View) findViewById(R.id.view2);
        final TextView wordInfoTitle = (TextView) findViewById(R.id.wordinfo_title);
        wordInfoTitleHelper = wordInfoTitle;
//        final TextView startTrial = (TextView) findViewById(R.id.start_trial);
//        final ImageView startTrialIcon = (ImageView) findViewById(R.id.start_trial_checkbox);
        final TextView hardSave = (TextView) findViewById(R.id.hardsave);
        final ImageView hardSaveIcon = (ImageView) findViewById(R.id.hardsave_checkbox);
        final TextView rhymeFeatures = (TextView) findViewById(R.id.rhyme_features);
        final TextView changeFont = (TextView) findViewById(R.id.change_font_size);
        final ImageButton metronomeIcon = (ImageButton) findViewById(R.id.metronome);
        final TextView makeADonation = (TextView) findViewById(R.id.donate);
        final TextView tutorial = (TextView) findViewById(R.id.tutorial);
//        final TextView share = (TextView) findViewById(R.id.share);
        final TextView upgrade = (TextView) findViewById(R.id.upgrade);
        final TextView generalSettings = (TextView) findViewById(R.id.general_settings);
        final ImageButton changeMode = (ImageButton) findViewById(R.id.change_mode);
        exitMeasureModeHelper = changeMode;
        final TextView orientation = (TextView) findViewById(R.id.orientation);
        final TextView darkmode = (TextView) findViewById(R.id.darkmode);
//        final TextView hideToolbarOption = (TextView) findViewById(R.id.hideToolbar);
        final TextView reviseRate = (TextView) findViewById(R.id.calculate_frequency);
        final TextView changeColor = (TextView) findViewById(R.id.changecolor);
        final TextView changeFontFamily = (TextView) findViewById(R.id.changefont);
        final EditText metronomeInput = (EditText) findViewById(R.id.metronome_input);
        final EditText metronomeAccentInput = (EditText) findViewById(R.id.metronome_input_accent);

        final TextView barsToolbarLengthText = (TextView) findViewById(R.id.select_note_length);
        final TextView barsToolbarPitchText = (TextView) findViewById(R.id.select_note_pitch);


        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView3);
        scrollviewHelper = scrollView;
        final ConstraintLayout mainActivity = (ConstraintLayout) findViewById(R.id.main_activity);
        mainActivityLayoutHelper = mainActivity;
        final ImageButton notesIcon = (ImageButton) findViewById(R.id.notes_icon);
        final EditText notes = (EditText) findViewById(R.id.notes);
        final EditText test = (EditText) findViewById(R.id.poemtest);
        final TextView lastWordsTextView = (TextView) findViewById(R.id.popup_last_words);
        final TextView changeSensitivityTextView = (TextView) findViewById(R.id.popup_change_sensitivity);
        final ImageView wordinfoBar = (ImageView) findViewById(R.id.wordinfo_bar);
        final ImageView notesBar = (ImageView) findViewById(R.id.notes_bar);
        final ImageButton button = (ImageButton) findViewById(R.id.search);


        popupBackground = new ImageView(MainActivity.this);
        popupBackground.setBackgroundResource(R.drawable.popup);
        linesWithRecordings = new ArrayList<>();
        linesWithoutRecordings = new HashMap<>();
        allBars = new HashMap<>();
        mainActivity.addView(popupBackground);
        popupBackground.setVisibility(View.GONE);

        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

//        callbackManager = CallbackManager.Factory.create();

//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("kmcilvai.perfectpoet", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.d("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.d("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.d("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.d("exception", e.toString());
//        }
        final pl.droidsonroids.gif.GifImageView loading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView2);

        spinnerList.animate().translationY(screenHeight);
        measureList.animate().translationY(screenHeight);
        lengthList.animate().translationY(screenHeight);
        pitchList.animate().translationY(screenHeight);
        settingList.animate().translationY(-screenHeight);
        currentSaveSelection = new LinearLayout(MainActivity.this);
        previousRow = new LinearLayout(MainActivity.this);
        selectedBars = new LinearLayout(MainActivity.this);
        selectedSixteenth = new ImageView(MainActivity.this);
        previousSylRow = new LinearLayout(MainActivity.this);
        previousTextView = new TextView(MainActivity.this);
        previousPreviousTextView = new TextView(MainActivity.this);
        mainActivityHelper = MainActivity.this;
        wordInfoHelper = wordInfo;




        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final Typeface Signika = Typeface.createFromAsset(getAssets(), "fonts/Signika-Regular.ttf");
        final Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");




        title.setTypeface(Signika);
        wordInfoTitle.setTypeface(Signika);
        notesTitle.setTypeface(Signika);

//        revisePopupTitle.setTypeface(Signika);
//        fontPopupTitle.setTypeface(Signika);
//        offlinePopupTitle.setTypeface(Signika);
//        orientationPopupTitle.setTypeface(Signika);
//        hideToolbarPopupTitle.setTypeface(Signika);
//        generalSettingsTitle.setTypeface(Signika);
//        rhymeFeaturesTitle.setTypeface(Signika);

        //hellomydaisy
        final Handler handler = new Handler();
        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if ((System.currentTimeMillis() > (last_text_edit + delay - 500))) {
                    typing = false;

                    TextView title = (TextView) findViewById(R.id.title);
                    if (title.getText().toString().equals("")) {
                        putStringToInternal("lyric" + lyricIndex + "title", "Untitled");
                    } else {
                        putStringToInternal("lyric" + lyricIndex + "title", title.getText().toString());
                    }
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    putStringToInternal("lyric" + lyricIndex + "poemsoftsavedate", currentDateTimeString);
                    putStringToInternal("lyric" + lyricIndex + "poemsoftsave", poem.getText().toString());
                    requestBackup();

                    if (!undoStringCurrent.equals(poem.getText().toString())) {
                        undoStringPrevious = undoStringCurrent;
                        undoStringPreviousPosition = undoStringCurrentPosition;
                        undoStringCurrent = poem.getText().toString();
                        undoStringCurrentPosition = poem.getSelectionEnd();
                        final ImageView undo = (ImageView) findViewById(R.id.undo);
                        undo.setVisibility(View.VISIBLE);

                    }

                }
            }

        };

        String tutorialHint = getStringFromInternal("lyrictutorial", "TRUE");
        if (tutorialHint.equals("TRUE")) {
            putStringToInternal("lyrictutorial", "FALSE");

            final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
            final ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
            TextView hintText = (TextView) findViewById(R.id.hint_text);
            final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
            hintImage.setImageResource(R.drawable.tutoriallink);

            hintText.setText("Click the icon below to watch the Lyric Notepad Tutorial and learn about all of its awesome features!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openPopup(hintPopup);
                    hintDone.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            hintPopup.setVisibility(View.GONE);
                            popupBackground.setVisibility(View.GONE);
                        }

                    });
                    hintImage.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "kMCBejmsk9U"));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + "kMCBejmsk9U"));
                            try {
                                startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                startActivity(webIntent);
                            }
                        }
                    });
                }
            });
        }

        final Handler measureHandler = new Handler();
        final Runnable input_measure_finish_checker = new Runnable() {
            public void run() {
                if ((System.currentTimeMillis() > (last_measure_edit + delay - 500))) {
                    measureTyping = false;
//
//                    TextView title = (TextView) findViewById(R.id.title);
//                    if (title.getText().toString().equals("")) {
//                        putStringToInternal("lyric" + lyricIndex + "title", "Untitled");
//                    } else {
//                        putStringToInternal("lyric" + lyricIndex + "title", title.getText().toString());
//                    }
//                    putStringToInternal("lyric" + lyricIndex + "poem", poem.getText().toString());
//
//                                    boolean success = false;


                }
            }

        };


        final Bundle b = getIntent().getExtras();
        lyricIndex = -1; // or other values
        if (b != null) {
            lyricIndex = b.getInt("lyric");
        }


        String newTitle = getStringFromInternal("lyric" + lyricIndex + "title", null);
        final String hardsaveString = getStringFromInternal("lyric" + lyricIndex + "poem", "1empty0518");
        final String softsaveString = getStringFromInternal("lyric" + lyricIndex + "poemsoftsave", "2empty0518");
        String softsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", "3empty0518");
        String hardsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measures", "4empty0518");
        if (newTitle != null) {
            title.setText(newTitle);
        }
        if (!softsaveString.equals("2empty0518")) {
            poem.setText(softsaveString);
        }
//        if (!hardsaveString.equals("1empty0518")) {
//            poem.setText(hardsaveString);
//        }
//
        if (!softsaveString.equals(hardsaveString) || !softsavemeasure.equals(hardsavemeasure)) {
            if (!softsaveString.equals("2empty0518") && !softsaveString.equals(hardsaveString)) {
                Toast.makeText(mainActivityHelper, "Lyrics recovered. Don't forget to save them!",
                        Toast.LENGTH_LONG).show();
            }
            if (!softsavemeasure.equals("3empty0518") && !softsavemeasure.equals(hardsavemeasure)) {
                Toast.makeText(mainActivityHelper, "Lyrics recovered. Don't forget to save them!",
                        Toast.LENGTH_LONG).show();
            }
        }
//
        undoStringCurrent = poem.getText().toString();
        undoStringCurrentPosition = 0;

        final Set<String> allData = getInternalKeys();
        for (String data : allData) {
            if (data.substring(0, data.length()).contains("order")) {
                int incrementer = getIntFromInternal(data, 0) + 1;
                putIntToInternal(data, incrementer);


            }
        }
        putIntToInternal("lyric" + lyricIndex + "order", 0);

        if (newTitle == null) {
            putStringToInternal("lyric" + lyricIndex + "title", "Untitled");

        }
        String pro = getStringFromInternal("lyricprouser", "false");
        if (pro.equals("true0518")) {
            proUser = true;
        }

        if (!Locale.getDefault().getLanguage().equals("en")) {
            final ConstraintLayout englishFeaturesPopup = (ConstraintLayout) findViewById(R.id.english_features_popup);
            final TextView englishFeatures = (TextView) findViewById(R.id.toggle_english_only_features);
            ImageView englishFeaturesCheckbox = (ImageView) findViewById(R.id.toggle_english_only_features_checkbox);
            englishFeatures.setVisibility(View.VISIBLE);
            englishFeaturesCheckbox.setVisibility(View.VISIBLE);
            final TextView englishFeaturesYes = (TextView) findViewById(R.id.popup_yes_english_features);
            final TextView englishFeaturesNo = (TextView) findViewById(R.id.popup_no_english_features);
            final LinearLayout general_settings_items_horizontal = (LinearLayout) findViewById(R.id.general_settings_items_horizontal);
            final LinearLayout general_settings_items_horizontal_checkboxes = (LinearLayout) findViewById(R.id.general_settings_items_horizontal_checkboxes);

            if (getStringFromInternal("lyricforeignuseroverride", "true").equals("false")) {
                general_settings_items_horizontal_checkboxes.setWeightSum(6);
                general_settings_items_horizontal.setWeightSum(6);
            } else {
                general_settings_items_horizontal_checkboxes.setWeightSum(7);
                general_settings_items_horizontal.setWeightSum(7);
            }
            englishFeatures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getStringFromInternal("lyricforeignuseroverride", "true").equals("false")) {
                        final TextView englishFeaturesText = (TextView) findViewById(R.id.english_features_text);
                        englishFeaturesText.setText("Would you like to turn the English-Only Features on?");
                        general_settings_items_horizontal_checkboxes.setWeightSum(6);
                        general_settings_items_horizontal.setWeightSum(6);
                    } else {
                        final TextView englishFeaturesText = (TextView) findViewById(R.id.english_features_text);
                        englishFeaturesText.setText("Would you like to turn the English-Only Features off? You can re-enable these features any time by going to \"General Settings\".");

                    }
                    openPopup(englishFeaturesPopup);
                    englishFeaturesYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getStringFromInternal("lyricforeignuseroverride", "true").equals("false")) {
                                putStringToInternal("lyricforeignuseroverride", "true");

                            } else {
                                putStringToInternal("lyricforeignuseroverride", "false");

                            }
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                    englishFeaturesNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            englishFeaturesPopup.setVisibility(View.GONE);
                            popupBackground.setVisibility(View.GONE);
                        }
                    });

                }
            });
            englishFeaturesCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    englishFeatures.performClick();
                }
            });
            if (getStringFromInternal("lyricforeignuseroverride", "true").equals("false")) {
                foreignUser = true;
                ImageView bottomToolbarUnlock = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
                spinnerToolbar.setVisibility(View.GONE);
                bottomToolbarUnlock.setVisibility(View.GONE);
                ImageView reviseRateCheckbox = (ImageView) findViewById(R.id.calculate_frequency_checkbox);
                reviseRate.setVisibility(View.GONE);
                reviseRateCheckbox.setVisibility(View.GONE);

            }
        }

        String clef = getStringFromInternal("lyriccurrentclef", "treble");
        changeClef(clef);

        if (!proUser) {
            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

            Handler handlerPro = new Handler();
            handlerPro.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mService != null) {
                            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
                            if (ownedItems.getInt("RESPONSE_CODE") == 0) {
                                if (ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST").toString().contains("lyricprouser")) {
                                    proUser = true;

                                    ImageView ignoreListIcon = (ImageView) findViewById(R.id.checkbox_ignore_words);
                                    ImageView rhymeListIcon = (ImageView) findViewById(R.id.checkbox_create_rhymes);
                                    ImageView darkmodeIcon = (ImageView) findViewById(R.id.darkmode_checkbox);
                                    ImageView changeThemeIcon = (ImageView) findViewById(R.id.changecolor_checkbox);
                                    ImageView changeFontFamilyIcon = (ImageView) findViewById(R.id.changefont_checkbox);
                                    ImageView topToolbarUnlock = (ImageView) findViewById(R.id.toggle_top_toolbar);
                                    ImageView bottomToolbarUnlock = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
                                    topToolbarUnlock.setImageResource(R.drawable.closespinner_purple);
                                    bottomToolbarUnlock.setImageResource(R.drawable.openspinner_purple);
                                    ignoreListIcon.setImageResource(R.drawable.pluswhite);
                                    rhymeListIcon.setImageResource(R.drawable.pluswhite);
                                    darkmodeIcon.setImageResource(R.drawable.darkmodeicon);
                                    changeThemeIcon.setImageResource(R.drawable.color_change);
                                    changeFontFamilyIcon.setImageResource(R.drawable.fontfamily_change);
                                    TextView upgradeToPro = (TextView) findViewById(R.id.upgrade);
                                    ImageView upgradeToProCheckbox = (ImageView) findViewById(R.id.upgrade_checkbox);
                                    upgradeToProCheckbox.setVisibility(View.GONE);
                                    upgradeToPro.setVisibility(View.GONE);
//                                    startTrial.setVisibility(View.GONE);
//                                    startTrialIcon.setVisibility(View.GONE);
//                                    TextView upgradeStartTrialButton = (TextView) findViewById(R.id.upgrade_start_trial);
//                                    upgradeStartTrialButton.setVisibility(View.GONE);
                                }
                            }
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 3000);


        }
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        String trialDate = getStringFromInternal("lyricfreetriallastdate2", "");
        int trialDay = getIntFromInternal("lyricfreetrialday2", 0);
        String optedForTrial = getStringFromInternal("lyricoptedfortrial", "false");
        //use this instead to reinclude trial as an option
//        if (proUser || optedForTrial.equals("true") || trialDay > 0) {
//        if (true) {
//            startTrial.setVisibility(View.GONE);
//            startTrialIcon.setVisibility(View.GONE);
//            TextView upgradeStartTrialButton = (TextView) findViewById(R.id.upgrade_start_trial);
//            upgradeStartTrialButton.setVisibility(View.GONE);
//
//
//        }

//        if (trialDay > 0 || optedForTrial.equals("true")) {
        if ((trialDay < 8) && !proUser) {
            if (!trialDate.equals(formattedDate)) {
                putIntToInternal("lyricfreetrialday2", getIntFromInternal("lyricfreetrialday2", 0) + 1);
                putStringToInternal("lyricfreetriallastdate2", formattedDate);

            }
            SpannableString spannable = new SpannableString("$2.99 $1.99");
            spannable.setSpan(new StrikethroughSpan(), 0, 5, 0);
            upgradeSpannable = spannable;
            upgradeAmount = 2;

        }
//        }

        // 7 lines added for admob
        adLoading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.adLoading);

        if (Build.FINGERPRINT.contains("generic")) {
            mAdView = (AdView) findViewById(R.id.adViewtest);
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdView mAdViewToRemove = (AdView) findViewById(R.id.adViewtest);
            mainActivity.removeView(mAdViewToRemove);
        }

        if (!proUser) {
            putStringToInternal("lyricignorelist", "");
            putStringToInternal("lyriccreatedrhymes", "");
            putStringToInternal("lyricdarkmode", "FALSE");
            putStringToInternal("lyriccolortheme", "royal");
            putStringToInternal("lyrictypeface", "SOURCESANSPRO");

            // if else added for admob
            if (trialDay > 2) {
                MobileAds.initialize(this, "ca-app-pub-1445128870529161~2452237328");
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        Log.d("errorcat", "Banner Error Code: " + errorCode);
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the user is about to return
                        // to the app after tapping on an ad.
                    }
                });
            } else {
                mAdView.setVisibility(View.GONE);
            }
        } else {
            // line added for admob
            mAdView.setVisibility(View.GONE);
            ImageView darkmodeIcon = (ImageView) findViewById(R.id.darkmode_checkbox);
            ImageView changeThemeIcon = (ImageView) findViewById(R.id.changecolor_checkbox);
            ImageView changeFontFamilyIcon = (ImageView) findViewById(R.id.changefont_checkbox);
            darkmodeIcon.setImageResource(R.drawable.darkmodeicon);
            changeThemeIcon.setImageResource(R.drawable.color_change);
            changeFontFamilyIcon.setImageResource(R.drawable.fontfamily_change);
            TextView upgradeToPro = (TextView) findViewById(R.id.upgrade);
            ImageView upgradeToProCheckbox = (ImageView) findViewById(R.id.upgrade_checkbox);
            if (getStringFromInternal("lyricprouser", "false").equals("true0518")) {
                upgradeToProCheckbox.setVisibility(View.GONE);
                upgradeToPro.setVisibility(View.GONE);
//                startTrial.setVisibility(View.GONE);
//                startTrialIcon.setVisibility(View.GONE);
//                TextView upgradeStartTrialButton = (TextView) findViewById(R.id.upgrade_start_trial);
//                upgradeStartTrialButton.setVisibility(View.GONE);
            }
        }


//        putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), "");
//                        boolean success = false;

//        DeleteRecursive(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE));


        String oldSavedRecordings = getStringFromInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), "");

        if (!oldSavedRecordings.equals("{}") && !oldSavedRecordings.equals("")) {
            oldSavedRecordings = oldSavedRecordings.replace("{", "").replace("}", "");
            String[] oldSavedRecordingsSplit = oldSavedRecordings.split(",");
            for (String oldSavedRecording : oldSavedRecordingsSplit) {
                oldSavedRecording = oldSavedRecording.trim();
                String word = oldSavedRecording.trim().substring(0, oldSavedRecording.indexOf("="));
                Integer line = Integer.parseInt(oldSavedRecording.substring(oldSavedRecording.indexOf("=") + 1));

                String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                File directory = new File(path);
                File[] files = directory.listFiles();
                ArrayList<String> fileNames = new ArrayList<>();
//                Log.d("errorcat", "Path: " + path);
//                Log.d("errorcat", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++) {
                    fileNames.add(files[i].getName());
//                    Log.d("errorcat", "FileName:" + files[i].getName());
                }
                if (fileNames.contains(word)) {
                    savedRecordings.put(word, line);
                }
            }
        }

        String first1000 = getStringFromInternal("lyricfirst1000", "true");
        if (first1000.equals("true")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(MainActivity.this.getResources().openRawResource(R.raw.startrhymes)));
            String startRhymes = null;
            try {
                startRhymes = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startRhymes = startRhymes.replace("{", "");
            if (startRhymes.contains("],")) {
                String[] startRhymesSplit = startRhymes.split("],");
                for (String word : startRhymesSplit) {
                    word = word.replace("}", "");
                    word = word.replace("]", "");
                    String mainWord = word.substring(0, word.indexOf("="));
                    word = word.substring(word.indexOf("["));
                    word = word.replace("[", "");
                    ArrayList<String> rhymes;
                    if (word.contains(", ")) {
                        rhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                    } else {
                        rhymes = new ArrayList<>();
                        rhymes.add(word);
                    }
                    mainWord = mainWord.trim();
                    foundWordRhymes.put(mainWord, rhymes);
                }
            } else if (startRhymes.contains("=")) {
                String word = startRhymes;
                word = word.replace("}", "");
                word = word.replace("]", "");
                String mainWord = word.substring(0, word.indexOf("="));
                word = word.substring(word.indexOf("["));
                word = word.replace("[", "");
                ArrayList<String> rhymes;
                if (word.contains(", ")) {
                    rhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                } else {
                    rhymes = new ArrayList<>();
                    rhymes.add(word);
                }
                mainWord = mainWord.trim();
                foundWordRhymes.put(mainWord, rhymes);

            }

            BufferedReader readerNear = new BufferedReader(new InputStreamReader(MainActivity.this.getResources().openRawResource(R.raw.startnearrhymes)));
            String startNearRhymes = null;
            try {
                startNearRhymes = readerNear.readLine();
                readerNear.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startNearRhymes = startNearRhymes.replace("{", "");
            if (startNearRhymes.contains("],")) {
                String[] oldNearRhymesSplit = startNearRhymes.split("],");
                for (String word : oldNearRhymesSplit) {
                    word = word.replace("}", "");
                    word = word.replace("]", "");
                    String mainWord = word.substring(0, word.indexOf("="));
                    word = word.substring(word.indexOf("["));
                    word = word.replace("[", "");
                    ArrayList<String> nearRhymes;
                    if (word.contains(", ")) {
                        nearRhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                    } else {
                        nearRhymes = new ArrayList<>();
                        nearRhymes.add(word);
                    }
                    mainWord = mainWord.trim();
                    foundWordNearRhymes.put(mainWord, nearRhymes);

                }
            } else if (startNearRhymes.contains("=")) {
                String word = startNearRhymes;
                word = word.replace("}", "");
                word = word.replace("]", "");
                String mainWord = word.substring(0, word.indexOf("="));
                word = word.substring(word.indexOf("["));
                word = word.replace("[", "");
                ArrayList<String> nearRhymes;
                if (word.contains(", ")) {
                    nearRhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                } else {
                    nearRhymes = new ArrayList<>();
                    nearRhymes.add(word);
                }
                mainWord = mainWord.trim();
                foundWordNearRhymes.put(mainWord, nearRhymes);

            }

            BufferedReader readerSyllables = new BufferedReader(new InputStreamReader(MainActivity.this.getResources().openRawResource(R.raw.startsyllables)));
            String startSyllables = null;
            try {
                startSyllables = readerSyllables.readLine();
                readerSyllables.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startSyllables = startSyllables.replace("{", "");
            if (startSyllables.contains("="))

            {
                if (startSyllables.contains(",")) {
                    String[] oldSyllablesSplit = startSyllables.split(",");
                    for (String word : oldSyllablesSplit) {
                        word = word.replace("}", "");
                        String mainWord = word.substring(0, word.indexOf("="));
                        word = word.substring(word.indexOf("="));
                        word = word.replace("=", "");
                        String wordSyllables = word.trim();
                        mainWord = mainWord.trim();
                        foundWordSyllables.put(mainWord, wordSyllables);
                    }
                } else {
                    String word = startSyllables;
                    word = word.replace("}", "");
                    String mainWord = word.substring(0, word.indexOf("="));
                    word = word.substring(word.indexOf("="));
                    word = word.replace("=", "");
                    String wordSyllables = word.trim();
                    mainWord = mainWord.trim();
                    foundWordSyllables.put(mainWord, wordSyllables);
                }

            }
            putStringToInternal("lyricfirst1000", "false");

        }

        String oldNearRhymes = getStringFromInternal("lyricfoundnearrhymes", "");
        String oldRhymes = getStringFromInternal("lyricfoundrhymes", "");
        String oldSyllables = getStringFromInternal("lyricfoundsyllables", "");

        oldRhymes = oldRhymes.replace("{", "");
        if (oldRhymes.contains("],")) {
            String[] oldRhymesSplit = oldRhymes.split("],");
            for (String word : oldRhymesSplit) {
                word = word.replace("}", "");
                word = word.replace("]", "");
                String mainWord = word.substring(0, word.indexOf("="));
                word = word.substring(word.indexOf("["));
                word = word.replace("[", "");
                ArrayList<String> rhymes;
                if (word.contains(", ")) {
                    rhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                } else {
                    rhymes = new ArrayList<>();
                    rhymes.add(word);
                }
                mainWord = mainWord.trim();
                foundWordRhymes.put(mainWord, rhymes);
            }
        } else if (oldRhymes.contains("=")) {
            String word = oldRhymes;
            word = word.replace("}", "");
            word = word.replace("]", "");
            String mainWord = word.substring(0, word.indexOf("="));
            word = word.substring(word.indexOf("["));
            word = word.replace("[", "");
            ArrayList<String> rhymes;
            if (word.contains(", ")) {
                rhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
            } else {
                rhymes = new ArrayList<>();
                rhymes.add(word);
            }
            mainWord = mainWord.trim();
            foundWordRhymes.put(mainWord, rhymes);

        }

        oldNearRhymes = oldNearRhymes.replace("{", "");
        if (oldNearRhymes.contains("],")) {
            String[] oldNearRhymesSplit = oldNearRhymes.split("],");
            for (String word : oldNearRhymesSplit) {
                word = word.replace("}", "");
                word = word.replace("]", "");
                String mainWord = word.substring(0, word.indexOf("="));
                word = word.substring(word.indexOf("["));
                word = word.replace("[", "");
                ArrayList<String> nearRhymes;
                if (word.contains(", ")) {
                    nearRhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
                } else {
                    nearRhymes = new ArrayList<>();
                    nearRhymes.add(word);
                }
                mainWord = mainWord.trim();
                foundWordNearRhymes.put(mainWord, nearRhymes);

            }
        } else if (oldNearRhymes.contains("=")) {
            String word = oldNearRhymes;
            word = word.replace("}", "");
            word = word.replace("]", "");
            String mainWord = word.substring(0, word.indexOf("="));
            word = word.substring(word.indexOf("["));
            word = word.replace("[", "");
            ArrayList<String> nearRhymes;
            if (word.contains(", ")) {
                nearRhymes = new ArrayList<>(Arrays.asList(word.split(", ")));
            } else {
                nearRhymes = new ArrayList<>();
                nearRhymes.add(word);
            }
            mainWord = mainWord.trim();
            foundWordNearRhymes.put(mainWord, nearRhymes);

        }

        oldSyllables = oldSyllables.replace("{", "");
        if (oldSyllables.contains("="))

        {
            if (oldSyllables.contains(",")) {
                String[] oldSyllablesSplit = oldSyllables.split(",");
                for (String word : oldSyllablesSplit) {
                    word = word.replace("}", "");
                    String mainWord = word.substring(0, word.indexOf("="));
                    word = word.substring(word.indexOf("="));
                    word = word.replace("=", "");
                    String wordSyllables = word.trim();
                    mainWord = mainWord.trim();
                    foundWordSyllables.put(mainWord, wordSyllables);
                }
            } else {
                String word = oldSyllables;
                word = word.replace("}", "");
                String mainWord = word.substring(0, word.indexOf("="));
                word = word.substring(word.indexOf("="));
                word = word.replace("=", "");
                String wordSyllables = word.trim();
                mainWord = mainWord.trim();
                foundWordSyllables.put(mainWord, wordSyllables);
            }

        }

        String orientationMode = getStringFromInternal("lyricorientation", "NEW");
        if (orientationMode.equals("LANDSCAPE"))

        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            TextView orientationText = (TextView) findViewById(R.id.orientation_text);
            orientationText.setText("Do you want to switch your orientation to portrait?");

        } else

        {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            TextView orientationText = (TextView) findViewById(R.id.orientation_text);
            orientationText.setText("Do you want to switch your orientation to landscape?");
        }

        String darkMode = getStringFromInternal("lyricdarkmode", "FALSE");
        if (darkMode.equals("TRUE")) {

            findViewById(R.id.main_activity).setBackgroundColor(Color.BLACK);
            wordInfo.setBackgroundColor(Color.BLACK);
            notes.setBackgroundColor(Color.BLACK);
            title.setTextColor(Color.WHITE);
            poem.setTextColor(Color.WHITE);
            poem.setHintTextColor(Color.GRAY);
            title.setHintTextColor(Color.GRAY);
            syllables.setTextColor(Color.WHITE);
            wordInfo.setTextColor(Color.WHITE);
            notes.setTextColor(Color.WHITE);
            notes.setHintTextColor(Color.GRAY);


            final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
            for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                    continue;
                }
                LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                i++;
                LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                EditText col1 = (EditText) row1.getChildAt(0);
                EditText col2 = (EditText) row1.getChildAt(1);
                EditText col3 = (EditText) row1.getChildAt(2);
                EditText col4 = (EditText) row1.getChildAt(3);
                TextView sylCol1 = (TextView) row2.getChildAt(0);
                TextView sylCol2 = (TextView) row2.getChildAt(1);
                TextView sylCol3 = (TextView) row2.getChildAt(2);
                TextView sylCol4 = (TextView) row2.getChildAt(3);
                col1.setTextColor(Color.WHITE);
                col2.setTextColor(Color.WHITE);
                col3.setTextColor(Color.WHITE);
                col4.setTextColor(Color.WHITE);
                sylCol1.setTextColor(Color.WHITE);
                sylCol2.setTextColor(Color.WHITE);
                sylCol3.setTextColor(Color.WHITE);
                sylCol4.setTextColor(Color.WHITE);
                col1.setHintTextColor(Color.GRAY);
                col2.setHintTextColor(Color.GRAY);
                col3.setHintTextColor(Color.GRAY);
                col4.setHintTextColor(Color.GRAY);
                sylCol1.setHintTextColor(Color.GRAY);
                sylCol2.setHintTextColor(Color.GRAY);
                sylCol3.setHintTextColor(Color.GRAY);
                sylCol4.setHintTextColor(Color.GRAY);
            }


            TextView darkmodeText = (TextView) findViewById(R.id.darkmode_text);
            darkmodeText.setText("Do you want to turn off Night Mode?");

        } else

        {
            findViewById(R.id.main_activity).setBackgroundColor(Color.WHITE);
            wordInfo.setBackgroundResource(R.color.lavender);
            notes.setBackgroundColor(Color.WHITE);
            title.setTextColor(Color.BLACK);
            poem.setTextColor(Color.BLACK);
            poem.setHintTextColor(Color.GRAY);
            title.setHintTextColor(Color.GRAY);
            syllables.setTextColor(Color.BLACK);
            wordInfo.setTextColor(Color.BLACK);
            notes.setTextColor(Color.BLACK);
            notes.setHintTextColor(Color.GRAY);
            final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
            for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                    continue;
                }
                LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                i++;
                LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                EditText col1 = (EditText) row1.getChildAt(0);
                EditText col2 = (EditText) row1.getChildAt(1);
                EditText col3 = (EditText) row1.getChildAt(2);
                EditText col4 = (EditText) row1.getChildAt(3);
                TextView sylCol1 = (TextView) row2.getChildAt(0);
                TextView sylCol2 = (TextView) row2.getChildAt(1);
                TextView sylCol3 = (TextView) row2.getChildAt(2);
                TextView sylCol4 = (TextView) row2.getChildAt(3);
                col1.setTextColor(Color.BLACK);
                col2.setTextColor(Color.BLACK);
                col3.setTextColor(Color.BLACK);
                col4.setTextColor(Color.BLACK);
                sylCol1.setTextColor(Color.BLACK);
                sylCol2.setTextColor(Color.BLACK);
                sylCol3.setTextColor(Color.BLACK);
                sylCol4.setTextColor(Color.BLACK);
                col1.setHintTextColor(Color.GRAY);
                col2.setHintTextColor(Color.GRAY);
                col3.setHintTextColor(Color.GRAY);
                col4.setHintTextColor(Color.GRAY);
                sylCol1.setHintTextColor(Color.GRAY);
                sylCol2.setHintTextColor(Color.GRAY);
                sylCol3.setHintTextColor(Color.GRAY);
                sylCol4.setHintTextColor(Color.GRAY);
            }
            TextView darkmodeText = (TextView) findViewById(R.id.darkmode_text);
            darkmodeText.setText("Do you want to turn on Night Mode?");

        }

        putStringToInternal("lyrichidetoolbar", "FALSE");

//        String hideToolbarSaved = getStringFromInternal("lyrichidetoolbar", "FALSE");
//        if (hideToolbarSaved.equals("TRUE"))
//
//        {
//            hideToolbar = true;
//            ImageView hideToolbarIcon = (ImageView) findViewById(R.id.hideToolbar_checkbox);
//            TextView hideToolbarText = (TextView) findViewById(R.id.hideToolbar_text);
//            TextView hideToolbarTitle = (TextView) findViewById(R.id.hideToolbar_title);
//            hideToolbarIcon.setImageResource(R.drawable.displayicon);
//            hideToolbarText.setText("Do you want to display the bottom toolbar when the keyboard is open?");
//            hideToolbarTitle.setText("Display Bottom Toolbar");
//            hideToolbarOption.setText("Display Bottom Toolbar");
//
//        } else
//
//        {
//            hideToolbar = false;
//            ImageView hideToolbarIcon = (ImageView) findViewById(R.id.hideToolbar_checkbox);
//            TextView hideToolbarText = (TextView) findViewById(R.id.hideToolbar_text);
//            TextView hideToolbarTitle = (TextView) findViewById(R.id.hideToolbar_title);
//            hideToolbarIcon.setImageResource(R.drawable.hideicon);
//            hideToolbarText.setText("Do you want to hide the bottom toolbar when the keyboard is open?");
//            hideToolbarTitle.setText("Hide Bottom Toolbar");
//            hideToolbarOption.setText("Hide Bottom Toolbar");
//        }


        //25 to 7
        if (getStringFromInternal("lyricorientation", "NEW").equals("LANDSCAPE")) {
            if (((int) (screenWidth / 120)) > defaultFontSize) {
                defaultFontSize = (int) (screenWidth / 120);
            }
        } else {
            if (((int) (screenHeight / 120)) > defaultFontSize) {
                defaultFontSize = (int) (screenHeight / 120);
            }
        }
        fontsize = getIntFromInternal("lyricfontsize", defaultFontSize);
        syllables.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        poem.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, ((fontsize * 25) / 15));
        wordInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        notes.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
        test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);

        String savedTypeface = getStringFromInternal("lyrictypeface", "SOURCESANSPRO");


        if (proUser) {
            changeFontFamily(savedTypeface);

        }else{
            changeFontFamily("SOURCESANSPRO");
        }

        colorTheme = getStringFromInternal("lyriccolortheme", "royal");
        final ImageButton checkboxRoyal = (ImageButton) findViewById(R.id.checkbox_royal);
        final ImageButton checkboxSunset = (ImageButton) findViewById(R.id.checkbox_sunset);
        final ImageButton checkboxJoy = (ImageButton) findViewById(R.id.checkbox_joy);
        final ImageButton checkboxDark = (ImageButton) findViewById(R.id.checkbox_dark);
        checkboxRoyal.setImageResource(R.drawable.small_check);
        if (proUser) {
            if (colorTheme.equals("royal")) {
                changeColors("royal");
                checkboxRoyal.setImageResource(R.drawable.small_check);
                checkboxSunset.setImageResource(R.drawable.circle);
                checkboxJoy.setImageResource(R.drawable.circle);
                checkboxDark.setImageResource(R.drawable.circle);
            } else if (colorTheme.equals("sunset")) {
                changeColors("sunset");
                checkboxRoyal.setImageResource(R.drawable.circle);
                checkboxSunset.setImageResource(R.drawable.small_check);
                checkboxJoy.setImageResource(R.drawable.circle);
                checkboxDark.setImageResource(R.drawable.circle);
            } else if (colorTheme.equals("joy")) {
                changeColors("joy");
                checkboxRoyal.setImageResource(R.drawable.circle);
                checkboxSunset.setImageResource(R.drawable.circle);
                checkboxJoy.setImageResource(R.drawable.small_check);
                checkboxDark.setImageResource(R.drawable.circle);
            } else if (colorTheme.equals("dark")) {

                changeColors("dark");
                checkboxRoyal.setImageResource(R.drawable.circle);
                checkboxSunset.setImageResource(R.drawable.circle);
                checkboxJoy.setImageResource(R.drawable.circle);
                checkboxDark.setImageResource(R.drawable.small_check);
            }
        }

        final ImageButton checkboxTurnOffAuto = (ImageButton) findViewById(R.id.checkbox_turn_off);
        final ImageButton checkboxSwipeDelay = (ImageButton) findViewById(R.id.checkbox_swipe_delay);
        final ImageButton checkboxAfterTyping = (ImageButton) findViewById(R.id.checkbox_after_typing);
        String savedReviseRate = getStringFromInternal("lyricreviserate", "AFTER_TYPING");

        if (savedReviseRate.equals("AFTER_TYPING"))

        {
            checkboxAfterTyping.setImageResource(R.drawable.small_check);

            delay = 2000;
            globalForceOffline = false;
        }
        if (savedReviseRate.equals("AUTO_OFF") || foreignUser) {
            checkboxTurnOffAuto.setImageResource(R.drawable.small_check);

            globalForceOffline = true;
        }
        if (savedReviseRate.equals("SWIPE_DELAY"))

        {
            checkboxSwipeDelay.setImageResource(R.drawable.small_check);
            delay = 5000;
            globalForceOffline = false;
        }

        final ImageButton checkboxChangeSensitivity = (ImageButton) findViewById(R.id.checkbox_change_sensitivity);
        final EditText ignoreListEditText = (EditText) findViewById(R.id.ignore_list_enter);
        final EditText createRhymesEditText = (EditText) findViewById(R.id.create_rhymes_enter);
        final ImageButton checkboxLastWords = (ImageButton) findViewById(R.id.checkbox_last_words);

        String savedHighSensitivity = getStringFromInternal("lyrichighsensitivity", "FALSE");
        String savedLastWords = getStringFromInternal("lyriclastwords", "FALSE");
        String savedIgnoreWords = getStringFromInternal("lyricignorelist", "");
        String savedCreatedRhymePairs = getStringFromInternal("lyriccreatedrhymes", "");

        if (!savedIgnoreWords.equals("")) {
            ignoreListEditText.setText(savedIgnoreWords);
            ignoredWords = new ArrayList<>(Arrays.asList(savedIgnoreWords.split(" ")));
        }
        if (!savedCreatedRhymePairs.equals("")) {
            createRhymesEditText.setText(savedCreatedRhymePairs);
            ArrayList<String> pairs = new ArrayList<>(Arrays.asList(savedCreatedRhymePairs.split(",")));
            for (String pair : pairs) {
                if (pair.indexOf("=") > 0 && pair.indexOf("=") < pair.length() - 1) {
                    if (createdRhymes.containsKey(pair.substring(0, pair.indexOf("="))) && createdRhymes.containsKey(pair.substring(pair.indexOf("=") + 1))) {
                        ArrayList<String> first = createdRhymes.get(pair.substring(0, pair.indexOf("=")));
                        first.add(pair.substring(pair.indexOf("=") + 1));
                        ArrayList<String> second = createdRhymes.get(pair.substring(pair.indexOf("=") + 1));
                        second.add(pair.substring(0, pair.indexOf("=")));
                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                    } else if (createdRhymes.containsKey(pair.substring(0, pair.indexOf("=")))) {
                        ArrayList<String> first = createdRhymes.get(pair.substring(0, pair.indexOf("=")));
                        first.add(pair.substring(pair.indexOf("=") + 1));
                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                        ArrayList<String> second = new ArrayList<>();
                        second.add(pair.substring(0, pair.indexOf("=")));
                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                    } else if (createdRhymes.containsKey(pair.substring(pair.indexOf("=") + 1))) {
                        ArrayList<String> second = createdRhymes.get(pair.substring(pair.indexOf("=") + 1));
                        second.add(pair.substring(0, pair.indexOf("=")));
                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                        ArrayList<String> first = new ArrayList<>();
                        first.add(pair.substring(pair.indexOf("=") + 1));
                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                    } else {
                        ArrayList<String> first = new ArrayList<>();
                        first.add(pair.substring(pair.indexOf("=") + 1));
                        ArrayList<String> second = new ArrayList<>();
                        second.add(pair.substring(0, pair.indexOf("=")));
                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                    }

                }
            }
        }
        if (savedHighSensitivity.equals("TRUE"))

        {
            checkboxChangeSensitivity.setImageResource(R.drawable.small_check);
            highSensitivity = true;
        }
        if (savedLastWords.equals("TRUE"))

        {
            lastWords = true;
            checkboxLastWords.setImageResource(R.drawable.small_check);

        }


        final int wordInfoHeight;
        wordInfoHeight = screenHeight / 5;
        poem.setPadding(0, 0, 0, 0);
        test.setPadding(0, 0, 0, 0);
        wordInfoTitle.getLayoutParams().width = (int) (screenWidth / 1.9);
        notesTitle.getLayoutParams().width = (int) (screenWidth / 1.9);
        wordInfo.getLayoutParams().height = (wordInfoHeight);
        notes.getLayoutParams().height = (wordInfoHeight);
        spinnerList.getLayoutParams().width = screenWidth * 5 / 6;
        measureList.getLayoutParams().width = screenWidth * 5 / 6;
        lengthList.getLayoutParams().width = screenWidth * 5 / 12;
        pitchList.getLayoutParams().width = screenWidth * 5 / 12;
        settingList.getLayoutParams().width = screenWidth * 3 / 4;
        title.setPadding(screenWidth / 7, 0, 0, title.getPaddingBottom());

        inputWord.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    button.performClick();

                    return true;
                }
                return false;
            }
        });


        final ImageView topToolbarToggler = (ImageView) findViewById(R.id.toggle_top_toolbar);
        final ImageView bottomToolbarToggler = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
        final ImageView undo = (ImageView) findViewById(R.id.undo);

        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        poem.setText(undoStringPrevious);
                        poem.setSelection(undoStringPreviousPosition);
                        undoStringPrevious = undoStringCurrent;
                        undoStringPreviousPosition = undoStringCurrentPosition;
                        undoStringCurrent = poem.getText().toString();
                        undoStringCurrentPosition = poem.getSelectionEnd();
                        Toast.makeText(mainActivityHelper, "Undo complete",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        topToolbarToggler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Toolbar topToolbar = (Toolbar) findViewById(R.id.toolbar);

                if (topToolbarVisible) {
                    topToolbarVisible = false;
                    if (topToolbarHeight == 0) {
                        topToolbarHeight = topToolbar.getHeight();
                    }
                    ValueAnimator anim = ValueAnimator.ofInt(topToolbarHeight, 10);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) topToolbar.getLayoutParams();
                            layoutParams.height = val;
                            topToolbar.setLayoutParams(layoutParams);
                            if (val == 10) {
                                topToolbar.setVisibility(View.GONE);
                            }

                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            topToolbar.getLayoutParams().height = 10;
                            topToolbar.requestLayout();
                            topToolbar.setVisibility(View.VISIBLE);

                        }

                    });
                    anim.setDuration(500);
                    anim.start();
                    if (colorTheme.equals("royal")) {
                        topToolbarToggler.setImageResource(R.drawable.closespinner_purple);
                    } else if (colorTheme.equals("sunset")) {
                        topToolbarToggler.setImageResource(R.drawable.closespinner_orange);
                    } else if (colorTheme.equals("joy")) {
                        topToolbarToggler.setImageResource(R.drawable.closespinner_blue);
                    } else if (colorTheme.equals("dark")) {
                        topToolbarToggler.setImageResource(R.drawable.closespinner_black);
                    }

                } else {
                    topToolbarVisible = true;
                    ValueAnimator anim = ValueAnimator.ofInt(10, topToolbarHeight);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) topToolbar.getLayoutParams();
                            layoutParams.height = val;
                            topToolbar.setLayoutParams(layoutParams);


                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            topToolbar.getLayoutParams().height = topToolbarHeight;
                            topToolbar.requestLayout();
                            topToolbar.setVisibility(View.VISIBLE);

                        }

                    });
                    anim.setDuration(500);
                    anim.start();
                    if (colorTheme.equals("royal")) {
                        topToolbarToggler.setImageResource(R.drawable.openspinner_purple);
                    } else if (colorTheme.equals("sunset")) {
                        topToolbarToggler.setImageResource(R.drawable.openspinner_orange);
                    } else if (colorTheme.equals("joy")) {
                        topToolbarToggler.setImageResource(R.drawable.openspinner_blue);
                    } else if (colorTheme.equals("dark")) {
                        topToolbarToggler.setImageResource(R.drawable.openspinner_black);
                    }
                }
            }
        });

        bottomToolbarToggler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Toolbar bottomToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
                final Toolbar measureToolbar = (Toolbar) findViewById(R.id.measure_toolbar);
                final Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                final LinearLayout poemAndSyllables = (LinearLayout) findViewById(R.id.poemandsyllables);

                if (bottomToolbarVisible) {
                    bottomToolbarVisible = false;
                    if (bottomToolbarHeight == 0) {
                        bottomToolbarHeight = bottomToolbar.getHeight();
                    }
                    ValueAnimator anim = ValueAnimator.ofInt(bottomToolbarHeight, 10);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) bottomToolbar.getLayoutParams();
                            ConstraintLayout.LayoutParams measureParams = (ConstraintLayout.LayoutParams) measureToolbar.getLayoutParams();
                            ConstraintLayout.LayoutParams barsParams = (ConstraintLayout.LayoutParams) barsToolbar.getLayoutParams();
                            layoutParams.height = val;
                            measureParams.height = val;
                            barsParams.height = val;

                            bottomToolbar.setLayoutParams(layoutParams);
                            if (val == 10) {
                                bottomToolbar.setVisibility(View.GONE);
                            }
                            measureToolbar.setLayoutParams(measureParams);
                            if (val == 10) {
                                measureToolbar.setVisibility(View.GONE);
                            }
                            barsToolbar.setLayoutParams(barsParams);
                            if (val == 10) {
                                barsToolbar.setVisibility(View.GONE);
                            }


                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            bottomToolbar.getLayoutParams().height = 10;
                            bottomToolbar.requestLayout();
                            bottomToolbar.setVisibility(View.VISIBLE);

                        }
                    });
                    anim.setDuration(500);
                    anim.start();
                    if (colorTheme.equals("royal")) {
                        bottomToolbarToggler.setImageResource(R.drawable.openspinner_purple);
                    } else if (colorTheme.equals("sunset")) {
                        bottomToolbarToggler.setImageResource(R.drawable.openspinner_orange);
                    } else if (colorTheme.equals("joy")) {
                        bottomToolbarToggler.setImageResource(R.drawable.openspinner_blue);
                    } else if (colorTheme.equals("dark")) {
                        bottomToolbarToggler.setImageResource(R.drawable.openspinner_black);
                    }

                } else {
                    bottomToolbarVisible = true;
                    ValueAnimator anim = ValueAnimator.ofInt(10, bottomToolbarHeight);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) bottomToolbar.getLayoutParams();
                            ConstraintLayout.LayoutParams measureParams = (ConstraintLayout.LayoutParams) measureToolbar.getLayoutParams();
                            ConstraintLayout.LayoutParams barsParams = (ConstraintLayout.LayoutParams) barsToolbar.getLayoutParams();
                            layoutParams.height = val;
                            measureParams.height = val;
                            barsParams.height = val;
                            if (!poemAndSyllables.isShown()) {
                                measureToolbar.setVisibility(View.VISIBLE);
                                if (barsCurrentlySelected) {
                                    barsToolbar.setVisibility(View.VISIBLE);
                                }
                            }

                            bottomToolbar.setLayoutParams(layoutParams);
                            measureToolbar.setLayoutParams(measureParams);
                            barsToolbar.setLayoutParams(measureParams);
                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            bottomToolbar.getLayoutParams().height = bottomToolbarHeight;
                            bottomToolbar.requestLayout();
                            bottomToolbar.setVisibility(View.VISIBLE);
                            if (!poemAndSyllables.isShown()) {
                                measureToolbar.getLayoutParams().height = bottomToolbarHeight;
                                measureToolbar.requestLayout();
                                measureToolbar.setVisibility(View.VISIBLE);
                                if (barsCurrentlySelected) {
                                    barsToolbar.getLayoutParams().height = bottomToolbarHeight;
                                    barsToolbar.requestLayout();
                                    barsToolbar.setVisibility(View.VISIBLE);
                                }
                            }


                        }

                    });
                    anim.setDuration(500);
                    anim.start();
                    if (colorTheme.equals("royal")) {
                        bottomToolbarToggler.setImageResource(R.drawable.closespinner_purple);
                    } else if (colorTheme.equals("sunset")) {
                        bottomToolbarToggler.setImageResource(R.drawable.closespinner_orange);
                    } else if (colorTheme.equals("joy")) {
                        bottomToolbarToggler.setImageResource(R.drawable.closespinner_blue);
                    } else if (colorTheme.equals("dark")) {
                        bottomToolbarToggler.setImageResource(R.drawable.closespinner_black);
                    }
                }
            }
        });

        notes.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                if (!(spinnerList.getTranslationY() == 0) && !(settingList.getTranslationY() == 0) && !(measureList.getTranslationY() == 0) && !(pitchList.getTranslationY() == 0) && !(lengthList.getTranslationY() == 0)) {
                    notes.bringToFront();
                    notesTitle.bringToFront();
                    notesBar.bringToFront();
                    notesView.bringToFront();
                    closeNotes.bringToFront();
                    bottomToolbarToggler.bringToFront();
                    undo.bringToFront();
                    notesIcon.setImageResource(R.drawable.noteicondisabled);
                    notesFocus = true;
                    if (colorTheme.equals("royal")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                        notesBar.setBackgroundResource(R.drawable.focus);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right);

                    } else if (colorTheme.equals("sunset")) {
                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                        notesBar.setBackgroundResource(R.drawable.focus_orange);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);

                    } else if (colorTheme.equals("joy")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                        notesBar.setBackgroundResource(R.drawable.focus_blue);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);

                    } else if (colorTheme.equals("dark")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                        notesBar.setBackgroundResource(R.drawable.focus_black);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);

                    }

                }
            }
        });

        //only need wordinfo touchlistener because wordinfotitle is behind notes title and without an event on notetitle, it will click wordinfotitle and move them both
        notesTitle.setOnTouchListener(new View.OnTouchListener()

        {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int y = (int) event.getY();
                switch (event.getAction()) {

                    //when the code is removed from action_down, the textview is moved ( the distance from old position to new position) from the bottom
                    //so if I try to make it 100px higher, instead it will be 100 px from the bottom. similiarly if it is moved down AT ALL, this is a negative distance
                    //and therefore moves to the lowest possible location. so somehow add the distance you moved to the distance you started from the bottom before moving.
                    case MotionEvent.ACTION_DOWN:
                        if (!(spinnerList.getTranslationY() == 0) && !(settingList.getTranslationY() == 0) && !(measureList.getTranslationY() == 0) && !(lengthList.getTranslationY() == 0) && !(pitchList.getTranslationY() == 0)) {
                            notes.bringToFront();
                            notesTitle.bringToFront();
                            notesBar.bringToFront();
                            notesView.bringToFront();
                            closeNotes.bringToFront();
                            bottomToolbarToggler.bringToFront();
                            undo.bringToFront();
                            notesIcon.setImageResource(R.drawable.noteicondisabled);
                            notesFocus = true;
                            if (colorTheme.equals("royal")) {
                                wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                                notesBar.setBackgroundResource(R.drawable.focus);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right);

                            } else if (colorTheme.equals("sunset")) {

                                wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                                notesBar.setBackgroundResource(R.drawable.focus_orange);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);

                            } else if (colorTheme.equals("joy")) {

                                wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                                notesBar.setBackgroundResource(R.drawable.focus_blue);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);

                            } else if (colorTheme.equals("dark")) {
                                wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                                notesBar.setBackgroundResource(R.drawable.focus_black);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);

                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ((-y + (view.getHeight() / 2) + notes.getHeight()) > (screenHeight - (2 * (mainToolbar.getHeight() + spinnerToolbar.getHeight())))) {
                            notes.getLayoutParams().height = (int) (screenHeight - (2 * (mainToolbar.getHeight() + spinnerToolbar.getHeight())));
                            notes.requestLayout();
                        } else if ((-y + (view.getHeight() / 2) + notes.getHeight()) < (spinnerToolbar.getHeight() * .1)) {
                            notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            notes.requestLayout();
                        } else {
                            notes.getLayoutParams().height = -y + (view.getHeight() / 2) + notes.getHeight();
                            notes.requestLayout();
                        }
                        if (wordInfo.isShown() && wordInfo.getHeight() > notes.getHeight()) {

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                            constraintSet.applyTo(mainActivity);

                        } else {
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                            constraintSet.applyTo(mainActivity);
                        }
                        break;
                }

                return true;
            }
        });

        wordInfo.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!(spinnerList.getTranslationY() == 0) && !(settingList.getTranslationY() == 0) && !(measureList.getTranslationY() == 0) && !(lengthList.getTranslationY() == 0) && !(pitchList.getTranslationY() == 0)) {
                        wordInfo.bringToFront();
                        wordInfoTitle.bringToFront();
                        wordinfoBar.bringToFront();
                        wordInfoView.bringToFront();
                        loading.bringToFront();
                        closeWordInfo.bringToFront();
                        bottomToolbarToggler.bringToFront();
                        undo.bringToFront();
                        notesIcon.setImageResource(R.drawable.noteicon);
                        notesFocus = false;
                        if (colorTheme.equals("royal")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus);
                            notesBar.setBackgroundResource(R.drawable.no_focus);
                            notesTitle.setBackgroundResource(R.drawable.tab_left);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);
                        } else if (colorTheme.equals("sunset")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                            notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                        } else if (colorTheme.equals("joy")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                            notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);

                        } else if (colorTheme.equals("dark")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                            notesBar.setBackgroundResource(R.drawable.no_focus_black);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);

                        }
                    }
                }
            }
        });

        wordInfoTitle.setOnTouchListener(new View.OnTouchListener()

        {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int y = (int) event.getY();
                switch (event.getAction()) {

                    //when the code is removed from action_down, the textview is moved ( the distance from old position to new position) from the bottom
                    //so if I try to make it 100px higher, instead it will be 100 px from the bottom. similiarly if it is moved down AT ALL, this is a negative distance
                    //and therefore moves to the lowest possible location. so somehow add the distance you moved to the distance you started from the bottom before moving.
                    case MotionEvent.ACTION_DOWN:
                        if (!(spinnerList.getTranslationY() == 0) && !(settingList.getTranslationY() == 0) && !(measureList.getTranslationY() == 0) && !(lengthList.getTranslationY() == 0) && !(pitchList.getTranslationY() == 0)) {
                            wordInfo.bringToFront();
                            wordInfoTitle.bringToFront();
                            wordinfoBar.bringToFront();
                            wordInfoView.bringToFront();
                            loading.bringToFront();
                            closeWordInfo.bringToFront();
                            bottomToolbarToggler.bringToFront();
                            undo.bringToFront();
                            notesIcon.setImageResource(R.drawable.noteicon);
                            notesFocus = false;
                            if (colorTheme.equals("royal")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus);
                                notesBar.setBackgroundResource(R.drawable.no_focus);
                                notesTitle.setBackgroundResource(R.drawable.tab_left);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);

                            } else if (colorTheme.equals("sunset")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                                notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                            } else if (colorTheme.equals("joy")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                                notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);

                            } else if (colorTheme.equals("dark")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                                notesBar.setBackgroundResource(R.drawable.no_focus_black);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);

                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if ((-y + (view.getHeight() / 2) + wordInfo.getHeight()) > (screenHeight - (2 * (mainToolbar.getHeight() + spinnerToolbar.getHeight())))) {
                            wordInfo.getLayoutParams().height = (int) (screenHeight - (2 * (mainToolbar.getHeight() + spinnerToolbar.getHeight())));
                            wordInfo.requestLayout();
                        } else if ((-y + (view.getHeight() / 2) + wordInfo.getHeight()) < (spinnerToolbar.getHeight() * .1)) {
                            wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            wordInfo.requestLayout();
                        } else {
                            wordInfo.getLayoutParams().height = -y + (view.getHeight() / 2) + wordInfo.getHeight();
                            wordInfo.requestLayout();
                        }
                        if (notes.isShown() && notes.getHeight() > wordInfo.getHeight()) {

                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                            constraintSet.applyTo(mainActivity);

                        } else {
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                            constraintSet.applyTo(mainActivity);
                        }
                        break;
                }

                return true;
            }
        });


        final ArrayList<TextView> spinnerItems = new ArrayList<>();
        spinnerItems.add((TextView)

                findViewById(R.id.exact_rhymes));
        spinnerItems.add((TextView)

                findViewById(R.id.near_rhymes));
        spinnerItems.add((TextView)

                findViewById(R.id.synonyms));
        spinnerItems.add((TextView)

                findViewById(R.id.definitions));

        for (
                final TextView spinnerItem : spinnerItems)

        {
            spinnerItem.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {
                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                    options.setVisibility(View.GONE);
                    inputWord.setVisibility(View.VISIBLE);
                    String checkboxID = getResources().getResourceName(spinnerItem.getId()) + "_checkbox";
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);

                    ImageView exact_rhymes_checkbox = (ImageView) findViewById(R.id.exact_rhymes_checkbox);
                    ImageView near_rhymes_checkbox = (ImageView) findViewById(R.id.near_rhymes_checkbox);
                    ImageView synonyms_checkbox = (ImageView) findViewById(R.id.synonyms_checkbox);
                    ImageView definitions_checkbox = (ImageView) findViewById(R.id.definitions_checkbox);

                    exact_rhymes_checkbox.setImageResource(0);
                    near_rhymes_checkbox.setImageResource(0);
                    synonyms_checkbox.setImageResource(0);
                    definitions_checkbox.setImageResource(0);

                    pulse(button, true);
                    if (checkboxID.equals("exact_rhymes_checkbox")) {
                        exact_rhymes_checkbox.setImageResource(R.drawable.check);
                    }
                    if (checkboxID.equals("near_rhymes_checkbox")) {
                        near_rhymes_checkbox.setImageResource(R.drawable.check);
                    }
                    if (checkboxID.equals("synonyms_checkbox")) {
                        synonyms_checkbox.setImageResource(R.drawable.check);
                    }

                    if (checkboxID.equals("definitions_checkbox")) {
                        definitions_checkbox.setImageResource(R.drawable.check);
                    }
                    spinnerSelection = spinnerItem.getText().toString();
                    int hintCheck = getIntFromInternal("lyricsearchhint", 0);
                    if (hintCheck < 1) {
                        putIntToInternal("lyricsearchhint", hintCheck + 1);

                    } else if (hintCheck == 1) {
                        putIntToInternal("lyricsearchhint", hintCheck + 1);

                        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
                        ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
                        TextView hintText = (TextView) findViewById(R.id.hint_text);
                        final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
                        hintImage.setImageResource(R.drawable.hint1);
                        hintText.setText("You can highlight a word in your lyric to have it automatically populate the search field below. Give it a try!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openPopup(hintPopup);
                                hintDone.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        hintPopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }

                                });
                            }
                        });
                    }
                }
            });
        }

        final ArrayList<ImageView> spinneritemsCheckboxes = new ArrayList<>();
        spinneritemsCheckboxes.add((ImageView)

                findViewById(R.id.exact_rhymes_checkbox));
        spinneritemsCheckboxes.add((ImageView)

                findViewById(R.id.near_rhymes_checkbox));
        spinneritemsCheckboxes.add((ImageView)

                findViewById(R.id.synonyms_checkbox));
        spinneritemsCheckboxes.add((ImageView)

                findViewById(R.id.definitions_checkbox));

        for (
                final ImageView spinnerItemCheckbox : spinneritemsCheckboxes)

        {
            spinnerItemCheckbox.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView exactRhymesTextView = (TextView) findViewById(R.id.exact_rhymes);
                    TextView nearRhymesTextView = (TextView) findViewById(R.id.near_rhymes);
                    TextView synonymTextView = (TextView) findViewById(R.id.synonyms);
                    TextView definitionsTextView = (TextView) findViewById(R.id.definitions);

                    String checkboxID = getResources().getResourceName(spinnerItemCheckbox.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);

                    if (checkboxID.equals("exact_rhymes_checkbox")) {
                        exactRhymesTextView.performClick();
                    }
                    if (checkboxID.equals("near_rhymes_checkbox")) {
                        nearRhymesTextView.performClick();
                    }
                    if (checkboxID.equals("synonyms_checkbox")) {
                        synonymTextView.performClick();
                    }

                    if (checkboxID.equals("definitions_checkbox")) {
                        definitionsTextView.performClick();
                    }

                }
            });
        }

        final ArrayList<TextView> measurePitchItems = new ArrayList<>();
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_1));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_2));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_3));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_4));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_5));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_6));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_7));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_8));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_9));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_10));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_11));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_12));
        measurePitchItems.add((TextView) findViewById(R.id.measure_bars_pitch_13));

        for (final TextView measurePitchItem : measurePitchItems) {
            measurePitchItem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    pitchList.animate().translationY(screenHeight);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                    String checkboxID = getResources().getResourceName(measurePitchItem.getId()) + "_checkbox";
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);

                    ImageView measure_bars_pitch_1_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_1_checkbox);
                    ImageView measure_bars_pitch_2_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_2_checkbox);
                    ImageView measure_bars_pitch_3_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_3_checkbox);
                    ImageView measure_bars_pitch_4_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_4_checkbox);
                    ImageView measure_bars_pitch_5_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_5_checkbox);
                    ImageView measure_bars_pitch_6_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_6_checkbox);
                    ImageView measure_bars_pitch_7_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_7_checkbox);
                    ImageView measure_bars_pitch_8_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_8_checkbox);
                    ImageView measure_bars_pitch_9_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_9_checkbox);
                    ImageView measure_bars_pitch_10_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_10_checkbox);
                    ImageView measure_bars_pitch_11_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_11_checkbox);
                    ImageView measure_bars_pitch_12_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_12_checkbox);
                    ImageView measure_bars_pitch_13_chechbox = (ImageView) findViewById(R.id.measure_bars_pitch_13_checkbox);


                    measure_bars_pitch_1_chechbox.setImageResource(0);
                    measure_bars_pitch_2_chechbox.setImageResource(0);
                    measure_bars_pitch_3_chechbox.setImageResource(0);
                    measure_bars_pitch_4_chechbox.setImageResource(0);
                    measure_bars_pitch_5_chechbox.setImageResource(0);
                    measure_bars_pitch_6_chechbox.setImageResource(0);
                    measure_bars_pitch_7_chechbox.setImageResource(0);
                    measure_bars_pitch_8_chechbox.setImageResource(0);
                    measure_bars_pitch_9_chechbox.setImageResource(0);
                    measure_bars_pitch_10_chechbox.setImageResource(0);
                    measure_bars_pitch_11_chechbox.setImageResource(0);
                    measure_bars_pitch_12_chechbox.setImageResource(0);
                    measure_bars_pitch_13_chechbox.setImageResource(0);
                    pulse(measureNotesGo, true);

                    if (checkboxID.equals("measure_bars_pitch_1_checkbox")) {
                        measure_bars_pitch_1_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "1";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_2_checkbox")) {
                        measure_bars_pitch_2_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "2";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_3_checkbox")) {
                        measure_bars_pitch_3_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "3";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_4_checkbox")) {
                        measure_bars_pitch_4_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "4";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_5_checkbox")) {
                        measure_bars_pitch_5_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "5";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_6_checkbox")) {
                        measure_bars_pitch_6_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "6";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_7_checkbox")) {
                        measure_bars_pitch_7_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "7";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_8_checkbox")) {
                        measure_bars_pitch_8_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "8";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_9_checkbox")) {
                        measure_bars_pitch_9_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "9";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_10_checkbox")) {
                        measure_bars_pitch_10_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "10";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_11_checkbox")) {
                        measure_bars_pitch_11_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "11";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_12_checkbox")) {
                        measure_bars_pitch_12_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "12";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }
                    if (checkboxID.equals("measure_bars_pitch_13_checkbox")) {
                        measure_bars_pitch_13_chechbox.setImageResource(R.drawable.check);
                        barsToolbarPitchText.setText(measurePitchItem.getText().toString());
                        pitchSpinnerSelection = "13";
                        pitchSpinnerPitch = measurePitchItem.getText().toString();
                    }


                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        final ArrayList<ImageView> measurePitchItemsImages = new ArrayList<>();
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_1_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_2_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_3_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_4_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_5_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_6_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_7_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_8_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_9_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_10_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_11_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_12_image));
        measurePitchItemsImages.add((ImageView) findViewById(R.id.measure_bars_pitch_13_image));

        for (final ImageView measurePitchItemImage : measurePitchItemsImages) {
            measurePitchItemImage.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView measure_bars_pitch_1 = (TextView) findViewById(R.id.measure_bars_pitch_1);
                    TextView measure_bars_pitch_2 = (TextView) findViewById(R.id.measure_bars_pitch_2);
                    TextView measure_bars_pitch_3 = (TextView) findViewById(R.id.measure_bars_pitch_3);
                    TextView measure_bars_pitch_4 = (TextView) findViewById(R.id.measure_bars_pitch_4);
                    TextView measure_bars_pitch_5 = (TextView) findViewById(R.id.measure_bars_pitch_5);
                    TextView measure_bars_pitch_6 = (TextView) findViewById(R.id.measure_bars_pitch_6);
                    TextView measure_bars_pitch_7 = (TextView) findViewById(R.id.measure_bars_pitch_7);
                    TextView measure_bars_pitch_8 = (TextView) findViewById(R.id.measure_bars_pitch_8);
                    TextView measure_bars_pitch_9 = (TextView) findViewById(R.id.measure_bars_pitch_9);
                    TextView measure_bars_pitch_10 = (TextView) findViewById(R.id.measure_bars_pitch_10);
                    TextView measure_bars_pitch_11 = (TextView) findViewById(R.id.measure_bars_pitch_11);
                    TextView measure_bars_pitch_12 = (TextView) findViewById(R.id.measure_bars_pitch_12);
                    TextView measure_bars_pitch_13 = (TextView) findViewById(R.id.measure_bars_pitch_13);


                    String checkboxID = getResources().getResourceName(measurePitchItemImage.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);
                    if (checkboxID.equals("measure_bars_pitch_1_image")) {
                        measure_bars_pitch_1.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_2_image")) {
                        measure_bars_pitch_2.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_3_image")) {
                        measure_bars_pitch_3.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_4_image")) {
                        measure_bars_pitch_4.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_5_image")) {
                        measure_bars_pitch_5.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_6_image")) {
                        measure_bars_pitch_6.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_7_image")) {
                        measure_bars_pitch_7.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_8_image")) {
                        measure_bars_pitch_8.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_9_image")) {
                        measure_bars_pitch_9.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_10_image")) {
                        measure_bars_pitch_10.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_11_image")) {
                        measure_bars_pitch_11.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_12_image")) {
                        measure_bars_pitch_12.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_13_image")) {
                        measure_bars_pitch_13.performClick();
                    }


                }
            });
        }

        final ArrayList<ImageView> measurePitchItemsCheckboxes = new ArrayList<>();
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_1_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_2_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_3_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_4_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_5_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_6_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_7_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_8_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_9_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_10_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_11_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_12_checkbox));
        measurePitchItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_pitch_13_checkbox));

        for (final ImageView measurePitchItemCheckbox : measurePitchItemsCheckboxes) {
            measurePitchItemCheckbox.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView measure_bars_pitch_1 = (TextView) findViewById(R.id.measure_bars_pitch_1);
                    TextView measure_bars_pitch_2 = (TextView) findViewById(R.id.measure_bars_pitch_2);
                    TextView measure_bars_pitch_3 = (TextView) findViewById(R.id.measure_bars_pitch_3);
                    TextView measure_bars_pitch_4 = (TextView) findViewById(R.id.measure_bars_pitch_4);
                    TextView measure_bars_pitch_5 = (TextView) findViewById(R.id.measure_bars_pitch_5);
                    TextView measure_bars_pitch_6 = (TextView) findViewById(R.id.measure_bars_pitch_6);
                    TextView measure_bars_pitch_7 = (TextView) findViewById(R.id.measure_bars_pitch_7);
                    TextView measure_bars_pitch_8 = (TextView) findViewById(R.id.measure_bars_pitch_8);
                    TextView measure_bars_pitch_9 = (TextView) findViewById(R.id.measure_bars_pitch_9);
                    TextView measure_bars_pitch_10 = (TextView) findViewById(R.id.measure_bars_pitch_10);
                    TextView measure_bars_pitch_11 = (TextView) findViewById(R.id.measure_bars_pitch_11);
                    TextView measure_bars_pitch_12 = (TextView) findViewById(R.id.measure_bars_pitch_12);
                    TextView measure_bars_pitch_13 = (TextView) findViewById(R.id.measure_bars_pitch_13);


                    String checkboxID = getResources().getResourceName(measurePitchItemCheckbox.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);
                    if (checkboxID.equals("measure_bars_pitch_1_checkbox")) {
                        measure_bars_pitch_1.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_2_checkbox")) {
                        measure_bars_pitch_2.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_3_checkbox")) {
                        measure_bars_pitch_3.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_4_checkbox")) {
                        measure_bars_pitch_4.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_5_checkbox")) {
                        measure_bars_pitch_5.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_6_checkbox")) {
                        measure_bars_pitch_6.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_7_checkbox")) {
                        measure_bars_pitch_7.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_8_checkbox")) {
                        measure_bars_pitch_8.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_9_checkbox")) {
                        measure_bars_pitch_9.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_10_checkbox")) {
                        measure_bars_pitch_10.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_11_checkbox")) {
                        measure_bars_pitch_11.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_12_checkbox")) {
                        measure_bars_pitch_12.performClick();
                    }
                    if (checkboxID.equals("measure_bars_pitch_13_checkbox")) {
                        measure_bars_pitch_13.performClick();
                    }


                }
            });
        }

        final ArrayList<TextView> measureLengthItems = new ArrayList<>();
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_whole));
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_half));
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_quarter));
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_eighth));
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_sixteenth));
        measureLengthItems.add((TextView) findViewById(R.id.measure_bars_length_remove));

        for (
                final TextView measureLengthItem : measureLengthItems)

        {
            measureLengthItem.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {
                    lengthList.animate().translationY(screenHeight);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                    String checkboxID = getResources().getResourceName(measureLengthItem.getId()) + "_checkbox";
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);

                    ImageView measure_bars_length_whole_checkbox = (ImageView) findViewById(R.id.measure_bars_length_whole_checkbox);
                    ImageView measure_bars_length_half_checkbox = (ImageView) findViewById(R.id.measure_bars_length_half_checkbox);
                    ImageView measure_bars_length_quarter_checkbox = (ImageView) findViewById(R.id.measure_bars_length_quarter_checkbox);
                    ImageView measure_bars_length_eighth_checkbox = (ImageView) findViewById(R.id.measure_bars_length_eighth_checkbox);
                    ImageView measure_bars_length_sixteenth_checkbox = (ImageView) findViewById(R.id.measure_bars_length_sixteenth_checkbox);
                    ImageView measure_bars_length_remove_checkbox = (ImageView) findViewById(R.id.measure_bars_length_remove_checkbox);

                    measure_bars_length_whole_checkbox.setImageResource(0);
                    measure_bars_length_half_checkbox.setImageResource(0);
                    measure_bars_length_quarter_checkbox.setImageResource(0);
                    measure_bars_length_eighth_checkbox.setImageResource(0);
                    measure_bars_length_sixteenth_checkbox.setImageResource(0);
                    measure_bars_length_remove_checkbox.setImageResource(0);
                    pulse(measureNotesGo, true);
                    if (checkboxID.equals("measure_bars_length_whole_checkbox")) {
                        measure_bars_length_whole_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Whole");
                        measureNotesGo.setImageResource(R.drawable.addnew);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        if (pitchSpinnerPitch.equals("")) {
                            barsToolbarPitchText.setText("Pitch");
                        } else {
                            barsToolbarPitchText.setText(pitchSpinnerPitch);
                        }
                        openNotePitch.setImageResource(R.drawable.openspinner);

//                        String[] sixteenths = allBars.get(selectedBars).split("</sixteenth>");
//                        for (int i = 0; i < sixteenths.length; i++) {
//                            sixteenths[i] = sixteenths[i].replace("<sixteenth>", "");
//                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            selectedBars.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                            selectedBars.getChildAt(i).setBackgroundColor(argb(50, 100, 100, 100));
                                        }
                                    } else {
                                        if (selectedSixteenth.getParent() != null) {
                                            for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                                ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                            }
                                        }
                                    }
                                    selectedSixteenth = (ImageView) selectedBars.getChildAt(0);
                                }
                            });
                        }

                    }
                    if (checkboxID.equals("measure_bars_length_half_checkbox")) {
                        measure_bars_length_half_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Half");
                        measureNotesGo.setImageResource(R.drawable.addnew);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        if (pitchSpinnerPitch.equals("")) {
                            barsToolbarPitchText.setText("Pitch");
                        } else {
                            barsToolbarPitchText.setText(pitchSpinnerPitch);
                        }
                        openNotePitch.setImageResource(R.drawable.openspinner);

                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i = i + 8) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                            final ImageView thirdSixteenth = (ImageView) selectedBars.getChildAt(i + 2);
                            final ImageView fourthSixteenth = (ImageView) selectedBars.getChildAt(i + 3);
                            final ImageView fifthSixteenth = (ImageView) selectedBars.getChildAt(i + 4);
                            final ImageView sixthSixteenth = (ImageView) selectedBars.getChildAt(i + 5);
                            final ImageView seventhSixteenth = (ImageView) selectedBars.getChildAt(i + 6);
                            final ImageView eighthSixteenth = (ImageView) selectedBars.getChildAt(i + 7);
                            ArrayList<ImageView> allSixteenths = new ArrayList<>();
                            allSixteenths.add(sixteenth);
                            allSixteenths.add(secondSixteenth);
                            allSixteenths.add(thirdSixteenth);
                            allSixteenths.add(fourthSixteenth);
                            allSixteenths.add(fifthSixteenth);
                            allSixteenths.add(sixthSixteenth);
                            allSixteenths.add(seventhSixteenth);
                            allSixteenths.add(eighthSixteenth);
                            for (ImageView s : allSixteenths) {
                                s.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                            }
                                        } else {
                                            if (selectedSixteenth.getParent() != null) {
                                                for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                                    ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                }
                                            }
                                        }
                                        selectedSixteenth = sixteenth;
                                        sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        thirdSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        fourthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        fifthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        sixthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        seventhSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        eighthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                    }
                                });
                            }

                        }

                    }
                    if (checkboxID.equals("measure_bars_length_quarter_checkbox")) {
                        measure_bars_length_quarter_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Quarter");
                        measureNotesGo.setImageResource(R.drawable.addnew);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        if (pitchSpinnerPitch.equals("")) {
                            barsToolbarPitchText.setText("Pitch");
                        } else {
                            barsToolbarPitchText.setText(pitchSpinnerPitch);
                        }
                        openNotePitch.setImageResource(R.drawable.openspinner);

                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i = i + 4) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                            final ImageView thirdSixteenth = (ImageView) selectedBars.getChildAt(i + 2);
                            final ImageView fourthSixteenth = (ImageView) selectedBars.getChildAt(i + 3);
                            ArrayList<ImageView> allSixteenths = new ArrayList<>();
                            allSixteenths.add(sixteenth);
                            allSixteenths.add(secondSixteenth);
                            allSixteenths.add(thirdSixteenth);
                            allSixteenths.add(fourthSixteenth);
                            for (ImageView s : allSixteenths) {
                                s.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                            }
                                        } else {
                                            if (selectedSixteenth.getParent() != null) {
                                                for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                                    ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                }
                                            }
                                        }
                                        selectedSixteenth = sixteenth;
                                        sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        thirdSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        fourthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                    }
                                });
                            }

                        }

                    }
                    if (checkboxID.equals("measure_bars_length_eighth_checkbox")) {
                        measure_bars_length_eighth_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Eighth");
                        measureNotesGo.setImageResource(R.drawable.addnew);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        if (pitchSpinnerPitch.equals("")) {
                            barsToolbarPitchText.setText("Pitch");
                        } else {
                            barsToolbarPitchText.setText(pitchSpinnerPitch);
                        }
                        openNotePitch.setImageResource(R.drawable.openspinner);

                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i = i + 2) {

                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                            ArrayList<ImageView> allSixteenths = new ArrayList<>();
                            allSixteenths.add(sixteenth);
                            allSixteenths.add(secondSixteenth);
                            for (ImageView s : allSixteenths) {
                                s.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                            }
                                        } else {
                                            if (selectedSixteenth.getParent() != null) {
                                                for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                                    ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                }
                                            }
                                        }
                                        selectedSixteenth = sixteenth;
                                        sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                        secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                    }
                                });
                            }

                        }
                    }
                    if (checkboxID.equals("measure_bars_length_sixteenth_checkbox")) {
                        measure_bars_length_sixteenth_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Sixteenth");
                        measureNotesGo.setImageResource(R.drawable.addnew);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        if (pitchSpinnerPitch.equals("")) {
                            barsToolbarPitchText.setText("Pitch");
                        } else {
                            barsToolbarPitchText.setText(pitchSpinnerPitch);
                        }
                        openNotePitch.setImageResource(R.drawable.openspinner);
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                        selectedSixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                                    } else {
                                        selectedSixteenth.setBackgroundColor(TRANSPARENT);
                                    }
                                    selectedSixteenth = sixteenth;
                                    sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                }
                            });

                        }
                    }
                    if (checkboxID.equals("measure_bars_length_remove_checkbox")) {
                        measure_bars_length_remove_checkbox.setImageResource(R.drawable.check);
                        barsToolbarLengthText.setText("Remove");
                        measureNotesGo.setImageResource(R.drawable.delete);
                        lengthSpinnerSelection = measureLengthItem.getText().toString();

                        barsToolbarPitchText.setText("");
                        openNotePitch.setImageResource(0);
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                            selectedBars.getChildAt(i).setClickable(true);
                        }
                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                            final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                            sixteenth.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                        selectedSixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                                    } else {
                                        selectedSixteenth.setBackgroundColor(TRANSPARENT);
                                    }
                                    selectedSixteenth = sixteenth;
                                    sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                                }
                            });

                        }
                    }
                    selectedBars.getChildAt(0).performClick();
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        final ArrayList<ImageView> measureLengthItemsImages = new ArrayList<>();
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_whole_image));
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_half_image));
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_quarter_image));
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_eighth_image));
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_sixteenth_image));
        measureLengthItemsImages.add((ImageView) findViewById(R.id.measure_bars_length_remove_image));

        for (final ImageView measureLengthItemImage : measureLengthItemsImages) {
            measureLengthItemImage.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView measure_bars_length_whole = (TextView) findViewById(R.id.measure_bars_length_whole);
                    TextView measure_bars_length_half = (TextView) findViewById(R.id.measure_bars_length_half);
                    TextView measure_bars_length_quarter = (TextView) findViewById(R.id.measure_bars_length_quarter);
                    TextView measure_bars_length_eighth = (TextView) findViewById(R.id.measure_bars_length_eighth);
                    TextView measure_bars_length_sixteenth = (TextView) findViewById(R.id.measure_bars_length_sixteenth);
                    TextView measure_bars_length_remove = (TextView) findViewById(R.id.measure_bars_length_remove);


                    String checkboxID = getResources().getResourceName(measureLengthItemImage.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);
                    if (checkboxID.equals("measure_bars_length_whole_image")) {
                        measure_bars_length_whole.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_half_image")) {
                        measure_bars_length_half.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_quarter_image")) {
                        measure_bars_length_quarter.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_eighth_image")) {
                        measure_bars_length_eighth.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_sixteenth_image")) {
                        measure_bars_length_sixteenth.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_remove_image")) {
                        measure_bars_length_remove.performClick();
                    }
                }
            });
        }

        final ArrayList<ImageView> measureLengthItemsCheckboxes = new ArrayList<>();
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_whole_checkbox));
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_half_checkbox));
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_quarter_checkbox));
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_eighth_checkbox));
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_sixteenth_checkbox));
        measureLengthItemsCheckboxes.add((ImageView) findViewById(R.id.measure_bars_length_remove_checkbox));

        for (final ImageView measureLengthItemCheckbox : measureLengthItemsCheckboxes) {
            measureLengthItemCheckbox.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView measure_bars_length_whole = (TextView) findViewById(R.id.measure_bars_length_whole);
                    TextView measure_bars_length_half = (TextView) findViewById(R.id.measure_bars_length_half);
                    TextView measure_bars_length_quarter = (TextView) findViewById(R.id.measure_bars_length_quarter);
                    TextView measure_bars_length_eighth = (TextView) findViewById(R.id.measure_bars_length_eighth);
                    TextView measure_bars_length_sixteenth = (TextView) findViewById(R.id.measure_bars_length_sixteenth);
                    TextView measure_bars_length_remove = (TextView) findViewById(R.id.measure_bars_length_remove);


                    String checkboxID = getResources().getResourceName(measureLengthItemCheckbox.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);
                    if (checkboxID.equals("measure_bars_length_whole_checkbox")) {
                        measure_bars_length_whole.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_half_checkbox")) {
                        measure_bars_length_half.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_quarter_checkbox")) {
                        measure_bars_length_quarter.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_eighth_checkbox")) {
                        measure_bars_length_eighth.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_sixteenth_checkbox")) {
                        measure_bars_length_sixteenth.performClick();
                    }
                    if (checkboxID.equals("measure_bars_length_remove_checkbox")) {
                        measure_bars_length_remove.performClick();
                    }
                }
            });
        }


        final ArrayList<TextView> measureItems = new ArrayList<>();
        measureItems.add((TextView) findViewById(R.id.add_new_measure));
        measureItems.add((TextView) findViewById(R.id.delete_selected_measure));
        measureItems.add((TextView) findViewById(R.id.delete_selected_measure_notes));
        measureItems.add((TextView) findViewById(R.id.scramble_selected_measure));
        measureItems.add((TextView) findViewById(R.id.add_bars_to_measure));
        measureItems.add((TextView) findViewById(R.id.play_measure_bars));

        for (
                final TextView measureItem : measureItems)

        {
            measureItem.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {
                    measureList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    String checkboxID = getResources().getResourceName(measureItem.getId()) + "_checkbox";
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);

                    ImageView add_new_measure_checkbox = (ImageView) findViewById(R.id.add_new_measure_checkbox);
                    ImageView add_bars_to_measure_checkbox = (ImageView) findViewById(R.id.add_bars_to_measure_checkbox);
                    ImageView play_measure_bars_checkbox = (ImageView) findViewById(R.id.play_measure_bars_checkbox);
                    ImageView delete_selected_measure_checkbox = (ImageView) findViewById(R.id.delete_selected_measure_checkbox);
                    ImageView delete_selected_measure_notes_checkbox = (ImageView) findViewById(R.id.delete_selected_measure_notes_checkbox);
                    ImageView scramble_selected_measure_checkbox = (ImageView) findViewById(R.id.scramble_selected_measure_checkbox);

                    add_new_measure_checkbox.setImageResource(0);
                    play_measure_bars_checkbox.setImageResource(0);
                    delete_selected_measure_checkbox.setImageResource(0);
                    delete_selected_measure_notes_checkbox.setImageResource(0);
                    scramble_selected_measure_checkbox.setImageResource(0);
                    add_bars_to_measure_checkbox.setImageResource(0);

                    pulse(measureGo, true);

                    if (checkboxID.equals("add_new_measure_checkbox")) {
                        add_new_measure_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Add New Measure");
                        measureSpinnerSelection = measureItem.getText().toString();
                        measureGo.setImageResource(R.drawable.addnew);
                        int hintCheck = getIntFromInternal("lyricmeasurehint", 0);
                        if (hintCheck < 1) {
                            putIntToInternal("lyricmeasurehint", hintCheck + 1);

                        } else if (hintCheck == 1) {
                            putIntToInternal("lyricmeasurehint", hintCheck + 1);

                            final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
                            ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
                            TextView hintText = (TextView) findViewById(R.id.hint_text);
                            final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
                            hintImage.setImageResource(R.drawable.hint3);
                            hintText.setText("You can highlight a portion of your lyrics when adding a new measure to have the highlighted portion automatically added to it. Give it a try!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openPopup(hintPopup);
                                    hintDone.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            hintPopup.setVisibility(View.GONE);
                                            popupBackground.setVisibility(View.GONE);
                                        }

                                    });
                                }
                            });
                        }
                    }
                    if (checkboxID.equals("delete_selected_measure_checkbox")) {
                        delete_selected_measure_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Delete Measure");
                        measureGo.setImageResource(R.drawable.delete);
                        measureSpinnerSelection = measureItem.getText().toString();

                    }
                    if (checkboxID.equals("play_measure_bars_checkbox")) {
                        play_measure_bars_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Play Measure Notes");
                        measureGo.setImageResource(R.drawable.measureplay);
                        measureSpinnerSelection = measureItem.getText().toString();

                    }
                    if (checkboxID.equals("delete_selected_measure_notes_checkbox")) {
                        delete_selected_measure_notes_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Delete Measure Notes");
                        measureGo.setImageResource(R.drawable.delete);
                        measureSpinnerSelection = measureItem.getText().toString();
                    }
                    if (checkboxID.equals("scramble_selected_measure_checkbox")) {
                        scramble_selected_measure_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Scramble Measure");
                        measureGo.setImageResource(R.drawable.random);
                        measureSpinnerSelection = measureItem.getText().toString();
                    }
                    if (checkboxID.equals("add_bars_to_measure_checkbox")) {

                        add_bars_to_measure_checkbox.setImageResource(R.drawable.check);
                        measureToolbarText.setText("Add Notes to Measure");
                        measureGo.setImageResource(R.drawable.addnew);
                        measureSpinnerSelection = measureItem.getText().toString();


                    }
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        final ArrayList<ImageView> measureitemsCheckboxes = new ArrayList<>();
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.add_new_measure_checkbox));
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.add_bars_to_measure_checkbox));
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.play_measure_bars_checkbox));
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.delete_selected_measure_checkbox));
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.delete_selected_measure_notes_checkbox));
        measureitemsCheckboxes.add((ImageView) findViewById(R.id.scramble_selected_measure_checkbox));

        for (final ImageView measureItemCheckbox : measureitemsCheckboxes) {
            measureItemCheckbox.setOnClickListener(new View.OnClickListener()

            {
                public void onClick(View v) {

                    TextView addNewMeasureTextView = (TextView) findViewById(R.id.add_new_measure);
                    TextView addBarsToMeasureTextView = (TextView) findViewById(R.id.add_bars_to_measure);
                    TextView playMeasureBarsTextView = (TextView) findViewById(R.id.play_measure_bars);
                    TextView deletedMeasureTextView = (TextView) findViewById(R.id.delete_selected_measure);
                    TextView deletedMeasureNotesTextView = (TextView) findViewById(R.id.delete_selected_measure_notes);
                    TextView scrambleMeasureTextView = (TextView) findViewById(R.id.scramble_selected_measure);

                    String checkboxID = getResources().getResourceName(measureItemCheckbox.getId());
                    checkboxID = checkboxID.substring(checkboxID.indexOf("/") + 1);
                    if (checkboxID.equals("add_bars_to_measure_checkbox")) {
                        addBarsToMeasureTextView.performClick();
                    }
                    if (checkboxID.equals("play_measure_bars_checkbox")) {
                        playMeasureBarsTextView.performClick();
                    }
                    if (checkboxID.equals("add_new_measure_checkbox")) {
                        addNewMeasureTextView.performClick();
                    }
                    if (checkboxID.equals("delete_selected_measure_checkbox")) {
                        deletedMeasureTextView.performClick();
                    }
                    if (checkboxID.equals("delete_selected_measure_notes_checkbox")) {
                        deletedMeasureNotesTextView.performClick();
                    }
                    if (checkboxID.equals("scramble_selected_measure_checkbox")) {
                        scrambleMeasureTextView.performClick();
                    }


                }
            });
        }

        measureNotesGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                pulse(measureNotesGo, false);
                spinnerList.animate().translationY(screenHeight);
                openSpinner.setImageResource(R.drawable.openspinner);
                measureList.animate().translationY(screenHeight);
                lengthList.animate().translationY(screenHeight);
                pitchList.animate().translationY(screenHeight);
                openMeasureSpinner.setImageResource(R.drawable.openspinner);
                openNoteLength.setImageResource(R.drawable.openspinner);
                openNoteLength.setImageResource(R.drawable.openspinner);
                settingList.animate().translationY(-screenHeight);
                optionsButton.setImageResource(R.drawable.settings1);
                if (!lengthSpinnerSelection.equals("")) {
                    if (lengthSpinnerSelection.equals("Whole")) {
                        selectedBars.getChildAt(0).performClick();
                    }
                    if (lengthSpinnerSelection.equals("Remove")) {
                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                            String[] sixteenths = allBars.get(selectedBars).split("</sixteenth>");
                            for (int i = 0; i < sixteenths.length; i++) {
                                sixteenths[i] = sixteenths[i].replace("<sixteenth>", "");
                            }
                            String jsonNoteClip = sixteenths[selectedBars.indexOfChild(selectedSixteenth)];
                            if (jsonNoteClip.contains("_")) {

                                String newJson = "";
                                String noteType = jsonNoteClip.substring(0, jsonNoteClip.indexOf("_"));
                                if (noteType.equals("whole")) {
                                    for (int i = 0; i < 16; i++) {
                                        newJson = newJson + "<sixteenth></sixteenth>";
                                    }
                                } else if (noteType.equals("half")) {
                                    for (int i = 0; i < 16; i++) {
                                        if ((i < selectedBars.indexOfChild(selectedSixteenth)) || i >= selectedBars.indexOfChild(selectedSixteenth) + 8) {
                                            newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                        } else {
                                            newJson = newJson + "<sixteenth></sixteenth>";
                                        }
                                    }
                                } else if (noteType.equals("quarter")) {
                                    for (int i = 0; i < 16; i++) {
                                        if ((i < selectedBars.indexOfChild(selectedSixteenth)) || i >= selectedBars.indexOfChild(selectedSixteenth) + 4) {
                                            newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                        } else {
                                            newJson = newJson + "<sixteenth></sixteenth>";
                                        }
                                    }
                                } else if (noteType.equals("eighth")) {
                                    for (int i = 0; i < 16; i++) {
                                        if ((i < selectedBars.indexOfChild(selectedSixteenth)) || i >= selectedBars.indexOfChild(selectedSixteenth) + 2) {
                                            newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                        } else {
                                            newJson = newJson + "<sixteenth></sixteenth>";
                                        }
                                    }
                                } else if (noteType.equals("sixteenth")) {
                                    for (int i = 0; i < 16; i++) {
                                        if ((i < selectedBars.indexOfChild(selectedSixteenth)) || i >= selectedBars.indexOfChild(selectedSixteenth) + 1) {
                                            newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                        } else {
                                            newJson = newJson + "<sixteenth></sixteenth>";
                                        }
                                    }
                                }
                                allBars.put(selectedBars, newJson);
                                selectedSixteenth.setImageResource(0);
                                Toast.makeText(MainActivity.this,
                                        "Note removed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "No note selected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "No note selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!pitchSpinnerSelection.equals("")) {
                            if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                String[] sixteenths = allBars.get(selectedBars).split("</sixteenth>");
                                for (int i = 0; i < sixteenths.length; i++) {
                                    sixteenths[i] = sixteenths[i].replace("<sixteenth>", "");
                                }
                                String jsonNoteClip = sixteenths[selectedBars.indexOfChild(selectedSixteenth)];
                                String noteType = " ";
                                if (jsonNoteClip.contains("_")) {
                                    noteType += jsonNoteClip.substring(0, jsonNoteClip.indexOf("_"));
                                } else {
                                    noteType += jsonNoteClip;
                                }
                                if (lengthSpinnerSelection.equals("Whole")) {
                                    for (int i = 0; i < 16; i++) {
                                        if (!sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i].equals("")) {
                                            String jsonNoteClip2 = sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i];
                                            String noteType2 = " ";
                                            if (jsonNoteClip2.contains("_")) {
                                                noteType2 += jsonNoteClip2.substring(0, jsonNoteClip2.indexOf("_"));
                                            } else {
                                                noteType2 += jsonNoteClip2;
                                            }
                                            Toast.makeText(MainActivity.this, "Selection occupied by a" + noteType2 + " note", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                } else if (lengthSpinnerSelection.equals("Half")) {
                                    for (int i = 0; i < 8; i++) {
                                        if (!sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i].equals("")) {
                                            String jsonNoteClip2 = sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i];
                                            String noteType2 = " ";
                                            if (jsonNoteClip2.contains("_")) {
                                                noteType2 += jsonNoteClip2.substring(0, jsonNoteClip2.indexOf("_"));
                                            } else {
                                                noteType2 += jsonNoteClip2;
                                            }
                                            Toast.makeText(MainActivity.this, "Selection occupied by a" + noteType2 + " note", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                } else if (lengthSpinnerSelection.equals("Quarter")) {
                                    for (int i = 0; i < 4; i++) {
                                        if (!sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i].equals("")) {
                                            String jsonNoteClip2 = sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i];
                                            String noteType2 = " ";
                                            if (jsonNoteClip2.contains("_")) {
                                                noteType2 += jsonNoteClip2.substring(0, jsonNoteClip2.indexOf("_"));
                                            } else {
                                                noteType2 += jsonNoteClip2;
                                            }
                                            Toast.makeText(MainActivity.this, "Selection occupied by a" + noteType2 + " note", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                } else if (lengthSpinnerSelection.equals("Eighth")) {
                                    for (int i = 0; i < 2; i++) {
                                        if (!sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i].equals("")) {
                                            String jsonNoteClip2 = sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i];
                                            String noteType2 = " ";
                                            if (jsonNoteClip2.contains("_")) {
                                                noteType2 += jsonNoteClip2.substring(0, jsonNoteClip2.indexOf("_"));
                                            } else {
                                                noteType2 += jsonNoteClip2;
                                            }
                                            Toast.makeText(MainActivity.this, "Selection occupied by a" + noteType2 + " note", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                } else if (lengthSpinnerSelection.equals("Sixteenth")) {
                                    for (int i = 0; i < 1; i++) {
                                        if (!sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i].equals("")) {
                                            String jsonNoteClip2 = sixteenths[selectedBars.indexOfChild(selectedSixteenth) + i];
                                            String noteType2 = " ";
                                            if (jsonNoteClip2.contains("_")) {
                                                noteType2 += jsonNoteClip2.substring(0, jsonNoteClip2.indexOf("_"));
                                            } else {
                                                noteType2 += jsonNoteClip2;
                                            }
                                            Toast.makeText(MainActivity.this, "Selection occupied by a" + noteType2 + " note", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                                if (jsonNoteClip.equals("")) {
                                    String color = "black";


                                    if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                        color = "white";
                                    }
                                    String noteName = lengthSpinnerSelection.toLowerCase() + "_" + pitchSpinnerSelection + "_" + color;
                                    String savedNote = lengthSpinnerSelection.toLowerCase() + "_" + pitchSpinnerSelection + "_";
                                    String newJson = "";
                                    if (lengthSpinnerSelection.equals("Whole")) {
                                        for (int i = 0; i < 16; i++) {
                                            if (i == 0) {
                                                newJson = newJson + "<sixteenth>" + savedNote + "</sixteenth>";
                                            } else {
                                                newJson = newJson + "<sixteenth>" + lengthSpinnerSelection.toLowerCase() + "</sixteenth>";
                                            }
                                        }
                                    } else if (lengthSpinnerSelection.equals("Half")) {
                                        for (int i = 0; i < 16; i++) {

                                            if (i == selectedBars.indexOfChild(selectedSixteenth)) {
                                                newJson = newJson + "<sixteenth>" + savedNote + "</sixteenth>";
                                            } else if ((i > selectedBars.indexOfChild(selectedSixteenth)) && (i < selectedBars.indexOfChild(selectedSixteenth) + 8)) {
                                                newJson = newJson + "<sixteenth>" + lengthSpinnerSelection.toLowerCase() + "</sixteenth>";
                                            } else {
                                                newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                            }
                                        }
                                    } else if (lengthSpinnerSelection.equals("Quarter")) {
                                        for (int i = 0; i < 16; i++) {

                                            if (i == selectedBars.indexOfChild(selectedSixteenth)) {
                                                newJson = newJson + "<sixteenth>" + savedNote + "</sixteenth>";
                                            } else if ((i > selectedBars.indexOfChild(selectedSixteenth)) && (i < selectedBars.indexOfChild(selectedSixteenth) + 4)) {
                                                newJson = newJson + "<sixteenth>" + lengthSpinnerSelection.toLowerCase() + "</sixteenth>";
                                            } else {
                                                newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                            }
                                        }
                                    } else if (lengthSpinnerSelection.equals("Eighth")) {
                                        for (int i = 0; i < 16; i++) {
                                            if (i == selectedBars.indexOfChild(selectedSixteenth)) {
                                                newJson = newJson + "<sixteenth>" + savedNote + "</sixteenth>";
                                            } else if ((i > selectedBars.indexOfChild(selectedSixteenth)) && (i < selectedBars.indexOfChild(selectedSixteenth) + 2)) {
                                                newJson = newJson + "<sixteenth>" + lengthSpinnerSelection.toLowerCase() + "</sixteenth>";
                                            } else {
                                                newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                            }
                                        }
                                    } else if (lengthSpinnerSelection.equals("Sixteenth")) {
                                        for (int i = 0; i < 16; i++) {
                                            if (i == selectedBars.indexOfChild(selectedSixteenth)) {
                                                newJson = newJson + "<sixteenth>" + savedNote + "</sixteenth>";
                                            } else if ((i > selectedBars.indexOfChild(selectedSixteenth)) && (i < selectedBars.indexOfChild(selectedSixteenth) + 1)) {
                                                newJson = newJson + "<sixteenth>" + lengthSpinnerSelection.toLowerCase() + "</sixteenth>";
                                            } else {
                                                newJson = newJson + "<sixteenth>" + sixteenths[i] + "</sixteenth>";
                                            }
                                        }
                                    }
                                    selectedSixteenth.setImageResource(getResources().getIdentifier(noteName, "drawable", getPackageName()));
                                    allBars.put(selectedBars, newJson);
                                    measureChanged = true;
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Selection occupied by a" + noteType + " note", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Must select note position", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Must select a pitch", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Must select a note", Toast.LENGTH_SHORT).show();
                }


            }
        });


        measureGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                pulse(measureGo, false);
                spinnerList.animate().translationY(screenHeight);
                openSpinner.setImageResource(R.drawable.openspinner);
                measureList.animate().translationY(screenHeight);
                lengthList.animate().translationY(screenHeight);
                pitchList.animate().translationY(screenHeight);
                openMeasureSpinner.setImageResource(R.drawable.openspinner);
                settingList.animate().translationY(-screenHeight);
                optionsButton.setImageResource(R.drawable.settings1);

                if (measureSpinnerSelection.equals("Add New Measure")) {
                    if (metronomeRunning) {
                        metronomeIcon.performClick();
                    }


                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                    final LinearLayout row1 = new LinearLayout(MainActivity.this);
                    final LinearLayout row2 = new LinearLayout(MainActivity.this);
                    row1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row1.setOrientation(LinearLayout.HORIZONTAL);
                    row2.setOrientation(LinearLayout.HORIZONTAL);
                    row1.setPadding(20, 20, 20, 0);
                    row2.setPadding(20, 0, 20, 20);
                    row1.setWeightSum(4);
                    row2.setWeightSum(4);
                    final EditText col1 = new EditText(MainActivity.this);
                    final EditText col2 = new EditText(MainActivity.this);
                    final EditText col3 = new EditText(MainActivity.this);
                    final EditText col4 = new EditText(MainActivity.this);
                    col1.setBackground(null);
                    col2.setBackground(null);
                    col3.setBackground(null);
                    col4.setBackground(null);
                    col1.setGravity(Gravity.CENTER);
                    col2.setGravity(Gravity.CENTER);
                    col3.setGravity(Gravity.CENTER);
                    col4.setGravity(Gravity.CENTER);
                    col1.setPadding(5, 0, 5, 5);
                    col2.setPadding(5, 0, 5, 5);
                    col3.setPadding(5, 0, 5, 5);
                    col4.setPadding(5, 0, 5, 5);
                    col1.setTextColor(

                            rgb(0, 0, 0));
                    col2.setTextColor(

                            rgb(0, 0, 0));
                    col3.setTextColor(

                            rgb(0, 0, 0));
                    col4.setTextColor(

                            rgb(0, 0, 0));
                    final TextView sylCol1 = new TextView(MainActivity.this);
                    final TextView sylCol2 = new TextView(MainActivity.this);
                    final TextView sylCol3 = new TextView(MainActivity.this);
                    final TextView sylCol4 = new TextView(MainActivity.this);
                    col1.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    col2.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    col3.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    col4.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    col1.setTypeface(typeface);
                    col2.setTypeface(typeface);
                    col3.setTypeface(typeface);
                    col4.setTypeface(typeface);
                    sylCol1.setTypeface(typeface);
                    sylCol2.setTypeface(typeface);
                    sylCol3.setTypeface(typeface);
                    sylCol4.setTypeface(typeface);
                    sylCol1.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    sylCol2.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    sylCol3.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    sylCol4.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    sylCol1.setPadding(5, 5, 5, 0);
                    sylCol2.setPadding(5, 5, 5, 0);
                    sylCol3.setPadding(5, 5, 5, 0);
                    sylCol4.setPadding(5, 5, 5, 0);
                    col1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    col2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    col3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    col4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    sylCol1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    sylCol2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    sylCol3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    sylCol4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    col1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    col2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    col3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    col4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    sylCol1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    sylCol2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    sylCol3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    sylCol4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    String darkmodeMode = getStringFromInternal("lyricdarkmode", "FALSE");
                    if (darkmodeMode.equals("TRUE"))

                    {
                        col1.setTextColor(Color.WHITE);
                        col2.setTextColor(Color.WHITE);
                        col3.setTextColor(Color.WHITE);
                        col4.setTextColor(Color.WHITE);
                        sylCol1.setTextColor(Color.WHITE);
                        sylCol2.setTextColor(Color.WHITE);
                        sylCol3.setTextColor(Color.WHITE);
                        sylCol4.setTextColor(Color.WHITE);
                        col1.setHintTextColor(Color.GRAY);
                        col2.setHintTextColor(Color.GRAY);
                        col3.setHintTextColor(Color.GRAY);
                        col4.setHintTextColor(Color.GRAY);
                        sylCol1.setHintTextColor(Color.GRAY);
                        sylCol2.setHintTextColor(Color.GRAY);
                        sylCol3.setHintTextColor(Color.GRAY);
                        sylCol4.setHintTextColor(Color.GRAY);
                    }
                    if (

                            isNetworkAvailable())

                    {
                        sylCol1.setText(" ");
                        sylCol2.setText(" ");
                        sylCol3.setText(" ");
                        sylCol4.setText(" ");
                    } else

                    {
                        sylCol1.setText("");
                        sylCol2.setText("");
                        sylCol3.setText("");
                        sylCol4.setText("");
                    }
                    col1.setHint("Write");
                    col2.setHint("your");
                    col3.setHint("lyrics");
                    col4.setHint("here");

                    row1.setOnClickListener(new View.OnClickListener()

                    {
                        public void onClick(View v) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                            if (!metronomeRunning) {
                                previousRow.setBackgroundColor(TRANSPARENT);
                                previousSylRow.setBackgroundColor(TRANSPARENT);
                                row1.setBackgroundColor(argb(50, 200, 200, 200));
                                row2.setBackgroundColor(argb(50, 200, 200, 200));
                            }
                            previousRow = row1;
                            previousSylRow = row2;
                            barsCurrentlySelected = false;
                            selectedSixteenth = new ImageView(MainActivity.this);
                            measureList.bringToFront();
                            measureToolbar.bringToFront();
                            // 2 lines add for admob
                            adLoading.bringToFront();
                            mAdView.bringToFront();
                            openNoteLength.setImageResource(R.drawable.openspinner);
                            openNotePitch.setImageResource(R.drawable.openspinner);
                            lengthList.animate().translationY(screenHeight);
                            pitchList.animate().translationY(screenHeight);
                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                selectedBars.getChildAt(i).setClickable(false);
                            }
                            Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                            barsToolbar.setVisibility(View.GONE);
                            row1.requestFocus();
                            //row1.clearFocus();
//                            }
                            spinnerList.animate().translationY(screenHeight);
                            settingList.animate().translationY(-screenHeight);
                            measureList.animate().translationY(screenHeight);
                            pitchList.animate().translationY(screenHeight);
                            lengthList.animate().translationY(screenHeight);

                            if (notes.isShown()) {
                                notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            }
                            if (wordInfo.isShown()) {
                                wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            }

                        }
                    });

                    row2.setOnClickListener(new View.OnClickListener()

                    {
                        public void onClick(View v) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                            if (!metronomeRunning) {
                                previousRow.setBackgroundColor(TRANSPARENT);
                                previousSylRow.setBackgroundColor(TRANSPARENT);
                                row1.setBackgroundColor(argb(50, 200, 200, 200));
                                row2.setBackgroundColor(argb(50, 200, 200, 200));
                            }
                            previousRow = row1;
                            previousSylRow = row2;
                            barsCurrentlySelected = false;
                            selectedSixteenth = new ImageView(MainActivity.this);
                            measureList.bringToFront();
                            measureToolbar.bringToFront();
                            // 2 lines add for admob
                            adLoading.bringToFront();
                            mAdView.bringToFront();
                            openNoteLength.setImageResource(R.drawable.openspinner);
                            openNotePitch.setImageResource(R.drawable.openspinner);
                            lengthList.animate().translationY(screenHeight);
                            pitchList.animate().translationY(screenHeight);
                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                selectedBars.getChildAt(i).setClickable(false);
                            }
                            Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                            barsToolbar.setVisibility(View.GONE);
                            row1.requestFocus();
                            //row1.clearFocus();
//                            }

                            spinnerList.animate().translationY(screenHeight);
                            settingList.animate().translationY(-screenHeight);
                            measureList.animate().translationY(screenHeight);
                            lengthList.animate().translationY(screenHeight);
                            pitchList.animate().translationY(screenHeight);
                            if (notes.isShown()) {
                                notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            }
                            if (wordInfo.isShown()) {
                                wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            }

                        }
                    });


                    col1.setOnFocusChangeListener(new View.OnFocusChangeListener()

                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {

//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                if (!metronomeRunning) {
                                    previousRow.setBackgroundColor(TRANSPARENT);
                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                }

//                            }
                                if (previousRow != row1) {
                                    col1.clearFocus();
                                }
                                previousRow = row1;
                                previousSylRow = row2;
                                barsCurrentlySelected = false;
                                selectedSixteenth = new ImageView(MainActivity.this);
                                measureList.bringToFront();
                                measureToolbar.bringToFront();
                                // 2 lines add for admob
                                adLoading.bringToFront();
                                mAdView.bringToFront();
                                openNoteLength.setImageResource(R.drawable.openspinner);
                                openNotePitch.setImageResource(R.drawable.openspinner);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    selectedBars.getChildAt(i).setClickable(false);
                                }
                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                barsToolbar.setVisibility(View.GONE);
                                spinnerList.animate().translationY(screenHeight);
                                settingList.animate().translationY(-screenHeight);
                                measureList.animate().translationY(screenHeight);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                if (notes.isShown()) {
                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                                if (wordInfo.isShown()) {
                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                            }
                        }
                    });


                    col2.setOnFocusChangeListener(new View.OnFocusChangeListener()

                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                if (!metronomeRunning) {
                                    previousRow.setBackgroundColor(TRANSPARENT);
                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                }

//                            }
                                if (previousRow != row1) {
                                    col2.clearFocus();
                                }
                                previousRow = row1;
                                previousSylRow = row2;
                                barsCurrentlySelected = false;
                                selectedSixteenth = new ImageView(MainActivity.this);
                                measureList.bringToFront();
                                measureToolbar.bringToFront();
                                // 2 lines add for admob
                                adLoading.bringToFront();
                                mAdView.bringToFront();
                                openNoteLength.setImageResource(R.drawable.openspinner);
                                openNotePitch.setImageResource(R.drawable.openspinner);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    selectedBars.getChildAt(i).setClickable(false);
                                }
                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                barsToolbar.setVisibility(View.GONE);
                                spinnerList.animate().translationY(screenHeight);
                                settingList.animate().translationY(-screenHeight);
                                measureList.animate().translationY(screenHeight);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                if (notes.isShown()) {
                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                                if (wordInfo.isShown()) {
                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                            }
                        }
                    });


                    col3.setOnFocusChangeListener(new View.OnFocusChangeListener()

                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                if (!metronomeRunning) {
                                    previousRow.setBackgroundColor(TRANSPARENT);
                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                }

//                            }
                                if (previousRow != row1) {
                                    col3.clearFocus();
                                }
                                previousRow = row1;
                                previousSylRow = row2;
                                barsCurrentlySelected = false;
                                selectedSixteenth = new ImageView(MainActivity.this);
                                measureList.bringToFront();
                                measureToolbar.bringToFront();
                                // 2 lines add for admob
                                adLoading.bringToFront();
                                mAdView.bringToFront();
                                openNoteLength.setImageResource(R.drawable.openspinner);
                                openNotePitch.setImageResource(R.drawable.openspinner);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    selectedBars.getChildAt(i).setClickable(false);
                                }
                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                barsToolbar.setVisibility(View.GONE);
                                spinnerList.animate().translationY(screenHeight);
                                settingList.animate().translationY(-screenHeight);
                                measureList.animate().translationY(screenHeight);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                if (notes.isShown()) {
                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                                if (wordInfo.isShown()) {
                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                            }
                        }
                    });


                    col4.setOnFocusChangeListener(new View.OnFocusChangeListener()

                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                if (!metronomeRunning) {
                                    previousRow.setBackgroundColor(TRANSPARENT);
                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                }

//                            }
                                if (previousRow != row1) {
                                    col4.clearFocus();
                                }
                                previousRow = row1;
                                previousSylRow = row2;
                                barsCurrentlySelected = false;
                                selectedSixteenth = new ImageView(MainActivity.this);
                                measureList.bringToFront();
                                measureToolbar.bringToFront();
                                // 2 lines add for admob
                                adLoading.bringToFront();
                                mAdView.bringToFront();
                                openNoteLength.setImageResource(R.drawable.openspinner);
                                openNotePitch.setImageResource(R.drawable.openspinner);
                                lengthList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    selectedBars.getChildAt(i).setClickable(false);
                                }
                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                barsToolbar.setVisibility(View.GONE);
                                spinnerList.animate().translationY(screenHeight);
                                settingList.animate().translationY(-screenHeight);
                                measureList.animate().translationY(screenHeight);
                                pitchList.animate().translationY(screenHeight);
                                lengthList.animate().translationY(screenHeight);
                                if (notes.isShown()) {
                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                                if (wordInfo.isShown()) {
                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                }
                            }
                        }
                    });
                    col1.addTextChangedListener(new

                                                        TextWatcher() {

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                                last_measure_edit = System.currentTimeMillis();
                                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                                            }

                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start,
                                                                                          int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start,
                                                                                      int before, int count) {
                                                                //You need to remove this to run only once
                                                                measureTyping = true;
                                                                measureChanged = true;
                                                                textviewsToChange.put(sylCol1, col1.getText().toString());
                                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                                            }
                                                        });
                    col2.addTextChangedListener(new

                                                        TextWatcher() {

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                                last_measure_edit = System.currentTimeMillis();
                                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                                            }

                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start,
                                                                                          int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start,
                                                                                      int before, int count) {
                                                                //You need to remove this to run only once
                                                                measureTyping = true;
                                                                measureChanged = true;
                                                                textviewsToChange.put(sylCol2, col2.getText().toString());
                                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                                            }
                                                        });
                    col3.addTextChangedListener(new

                                                        TextWatcher() {

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                                last_measure_edit = System.currentTimeMillis();
                                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                                            }

                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start,
                                                                                          int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start,
                                                                                      int before, int count) {
                                                                //You need to remove this to run only once
                                                                measureTyping = true;
                                                                measureChanged = true;
                                                                textviewsToChange.put(sylCol3, col3.getText().toString());
                                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                                            }
                                                        });
                    col4.addTextChangedListener(new

                                                        TextWatcher() {

                                                            @Override
                                                            public void afterTextChanged(Editable s) {

                                                                last_measure_edit = System.currentTimeMillis();
                                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                                            }

                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start,
                                                                                          int count, int after) {

                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start,
                                                                                      int before, int count) {
                                                                //You need to remove this to run only once
                                                                measureTyping = true;
                                                                measureChanged = true;
                                                                textviewsToChange.put(sylCol4, col4.getText().toString());
                                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                                            }
                                                        });
                    if (wordInfo.getSelectionEnd() != wordInfo.getSelectionStart())

                    {
                        String wordInfoString = wordInfo.getText().toString().substring(wordInfo.getSelectionStart(), wordInfo.getSelectionEnd());
                        String[] possibleWords = wordInfoString.split(" +");
                        ArrayList<String> words = new ArrayList<String>();
                        for (String word : possibleWords) {
                            if (!word.replaceAll("[^a-zA-Z0-9]+", "").equals("")) {
                                words.add(word);
                            }
                        }
                        int firstQuarterWords = 0;
                        int secondQuarterWords = 0;
                        int thirdQuarterWords = 0;
                        int fourthQuarterWords = 0;

                        Random rand = new Random();
                        for (int i = 0; i < words.size(); i++) {
                            int n = rand.nextInt(4);
                            switch (n) {
                                case 0:
                                    firstQuarterWords++;
                                    break;
                                case 1:
                                    secondQuarterWords++;
                                    break;
                                case 2:
                                    thirdQuarterWords++;
                                    break;
                                case 3:
                                    fourthQuarterWords++;
                                    break;
                                default:
                                    break;
                            }
                        }
                        int wordTracker = 0;
                        String text = "";
                        for (int i = 0; i < firstQuarterWords; i++) {
                            text += words.get(wordTracker) + " ";
                            wordTracker++;
                        }
                        col1.setText(text.trim());
                        text = "";
                        for (int i = 0; i < secondQuarterWords; i++) {
                            text += words.get(wordTracker) + " ";
                            wordTracker++;
                        }
                        col2.setText(text.trim());
                        text = "";
                        for (int i = 0; i < thirdQuarterWords; i++) {
                            text += words.get(wordTracker) + " ";
                            wordTracker++;
                        }
                        col3.setText(text.trim());
                        text = "";
                        for (int i = 0; i < fourthQuarterWords; i++) {
                            text += words.get(wordTracker) + " ";
                            wordTracker++;
                        }
                        col4.setText(text.trim());
                    }

                    row1.addView(col1);
                    row1.addView(col2);
                    row1.addView(col3);
                    row1.addView(col4);
                    row2.addView(sylCol1);
                    row2.addView(sylCol2);
                    row2.addView(sylCol3);
                    row2.addView(sylCol4);
                    if (measureModeLayout.getChildCount() > 1)

                    {
                        measureModeLayout.addView(row1, measureModeLayout.indexOfChild(previousRow) + 2);
                        measureModeLayout.addView(row2, measureModeLayout.indexOfChild(previousSylRow) + 2);
                    } else

                    {
                        measureModeLayout.addView(row1);
                        measureModeLayout.addView(row2);
                    }
                    // previousRow.clearFocus();
                    previousRow.setBackgroundColor(TRANSPARENT);
                    previousSylRow.setBackgroundColor(TRANSPARENT);
                    row1.setBackgroundColor(

                            argb(50, 200, 200, 200));
                    row2.setBackgroundColor(

                            argb(50, 200, 200, 200));
                    previousRow = row1;
                    previousSylRow = row2;
                    barsCurrentlySelected = false;
                    selectedSixteenth = new ImageView(MainActivity.this);
                    measureList.bringToFront();
                    measureToolbar.bringToFront();
                    // 2 lines add for admob
                    adLoading.bringToFront();
                    mAdView.bringToFront();
                    openNoteLength.setImageResource(R.drawable.openspinner);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                    lengthList.animate().translationY(screenHeight);
                    pitchList.animate().translationY(screenHeight);
                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                        selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                        selectedBars.getChildAt(i).setClickable(false);
                    }
                    Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                    barsToolbar.setVisibility(View.GONE);
                    measureChanged = true;
                } else if (measureSpinnerSelection.equals("Delete Measure")) {
                    if (metronomeRunning) {
                        metronomeIcon.performClick();
                    }
                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1))) {
                        LinearLayout toRemove = (LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1);
                        measureModeLayout.removeView(toRemove);
                        allBars.remove(toRemove);

                    }
                    int previousRowIndex = measureModeLayout.indexOfChild(previousRow);
                    int previousSylRowIndex = measureModeLayout.indexOfChild(previousSylRow);
                    measureModeLayout.removeView(previousRow);
                    measureModeLayout.removeView(previousSylRow);
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(previousRowIndex))) {
                        previousRowIndex++;
                        previousSylRowIndex++;
                    }
                    if (measureModeLayout.getChildCount() > 1) {
                        if (measureModeLayout.getChildCount() > previousRowIndex) {
                            previousRow = (LinearLayout) measureModeLayout.getChildAt(previousRowIndex);
                            previousSylRow = (LinearLayout) measureModeLayout.getChildAt(previousSylRowIndex);
                            previousRow.setBackgroundColor(argb(50, 200, 200, 200));
                            previousSylRow.setBackgroundColor(argb(50, 200, 200, 200));
                        } else {
                            previousRow = (LinearLayout) measureModeLayout.getChildAt(previousRowIndex - 2);
                            previousSylRow = (LinearLayout) measureModeLayout.getChildAt(previousSylRowIndex - 2);
                            previousRow.setBackgroundColor(argb(50, 200, 200, 200));
                            previousSylRow.setBackgroundColor(argb(50, 200, 200, 200));
                        }
                    }
                    measureChanged = true;
                    measureModeLayout.requestLayout();
                } else if (measureSpinnerSelection.equals("Delete Measure Notes")) {
                    if (metronomeRunning) {
                        metronomeIcon.performClick();
                    }
                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1))) {
                        LinearLayout toRemove = (LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1);
                        measureModeLayout.removeView(toRemove);
                        allBars.remove(toRemove);

                    }
                    measureChanged = true;
                    measureModeLayout.requestLayout();
                } else if (measureSpinnerSelection.equals("Scramble Measure")) {
                    EditText firstTV = (EditText) previousRow.getChildAt(0);
                    EditText secondTV = (EditText) previousRow.getChildAt(1);
                    EditText thirdTV = (EditText) previousRow.getChildAt(2);
                    EditText fourthTV = (EditText) previousRow.getChildAt(3);
                    String allWordsString = "";
                    if (!firstTV.getText().toString().trim().equals("")) {
                        allWordsString += firstTV.getText().toString();
                    }
                    if (!secondTV.getText().toString().trim().equals("")) {
                        allWordsString += " " + secondTV.getText().toString();
                    }
                    if (!thirdTV.getText().toString().trim().equals("")) {
                        allWordsString += " " + thirdTV.getText().toString();
                    }
                    if (!fourthTV.getText().toString().trim().equals("")) {
                        allWordsString += " " + fourthTV.getText().toString();
                    }
                    String[] possibleWords = allWordsString.split(" +");
                    ArrayList<String> words = new ArrayList<String>();
                    for (String word : possibleWords) {
                        if (!word.replaceAll("[^a-zA-Z0-9]+", "").equals("")) {
                            words.add(word);
                        }
                    }
                    int firstQuarterWords = 0;
                    int secondQuarterWords = 0;
                    int thirdQuarterWords = 0;
                    int fourthQuarterWords = 0;

                    Random rand = new Random();
                    for (int i = 0; i < words.size(); i++) {
                        int n = rand.nextInt(4);
                        switch (n) {
                            case 0:
                                firstQuarterWords++;
                                break;
                            case 1:
                                secondQuarterWords++;
                                break;
                            case 2:
                                thirdQuarterWords++;
                                break;
                            case 3:
                                fourthQuarterWords++;
                                break;
                            default:
                                break;
                        }
                    }
                    int wordTracker = 0;
                    String text = "";
                    for (int i = 0; i < firstQuarterWords; i++) {
                        text += words.get(wordTracker) + " ";
                        wordTracker++;
                    }
                    firstTV.setText(text.trim());
                    text = "";
                    for (int i = 0; i < secondQuarterWords; i++) {
                        text += words.get(wordTracker) + " ";
                        wordTracker++;
                    }
                    secondTV.setText(text.trim());
                    text = "";
                    for (int i = 0; i < thirdQuarterWords; i++) {
                        text += words.get(wordTracker) + " ";
                        wordTracker++;
                    }
                    thirdTV.setText(text.trim());
                    text = "";
                    for (int i = 0; i < fourthQuarterWords; i++) {
                        text += words.get(wordTracker) + " ";
                        wordTracker++;
                    }
                    fourthTV.setText(text.trim());
                } else if (measureSpinnerSelection.equals("Play Measure Notes")) {
                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                    final LinearLayout barsToPlay;
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1))) {
                        barsToPlay = ((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1));
                        if (!allBars.get(barsToPlay).replaceAll("<sixteenth>", "").replaceAll("</sixteenth>", "").equals("")) {
                            final String[] sixteenths = allBars.get(barsToPlay).split("</sixteenth>");
                            for (int i = 0; i < sixteenths.length; i++) {
                                sixteenths[i] = sixteenths[i].replace("<sixteenth>", "");
                            }
                            Integer milli = 60000 / getIntFromInternal("lyricmetronomebpm" + lyricIndex, 80);
                            Integer accent = getIntFromInternal("lyricmetronomeaccent" + lyricIndex, 4);
                            Timer m = new Timer();
                            m.scheduleAtFixedRate(new TimerTask() {
                                int i = 0;

                                @Override
                                //run() reads the sensor data, applies the appropriate status and if necassary updates the server
                                public void run() {
                                    if (i < sixteenths.length) {
                                        if (sixteenths[i].contains("_")) {
                                            String noteLabel = "measure_bars_pitch_" + sixteenths[i].substring(ordinalIndexOf(sixteenths[i], "_", 1) + 1, ordinalIndexOf(sixteenths[i], "_", 2));
                                            TextView noteView = (TextView) findViewById(getResources().getIdentifier(noteLabel, "id", getPackageName()));
                                            MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(noteView.getText().toString().toLowerCase(), "raw", getPackageName()));
                                            note.start();
                                            note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                public void onCompletion(MediaPlayer note) {
                                                    note.release();
                                                }
                                            });
                                        }
                                        i++;
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }, 0, (milli * accent) / 16);

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Measure has no notes", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Measure has no notes", Toast.LENGTH_SHORT).show();
                    }
                } else if (measureSpinnerSelection.equals("Add Notes to Measure")) {

                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                    if (measureModeLayout.getChildCount() == 0) {
                        Toast.makeText(MainActivity.this,
                                "Requires a measure", Toast.LENGTH_SHORT).show();
                    } else if (allBars.keySet().contains(measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1))) {
                        Toast.makeText(MainActivity.this,
                                "Measure already has notes", Toast.LENGTH_SHORT).show();
                    } else {

                        final LinearLayout bars = new LinearLayout(MainActivity.this);
                        String color = "black";


                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                            color = "white";
                        }
                        String barsName = "bars_" + color;
                        bars.setBackgroundResource((getResources().getIdentifier(barsName, "drawable", getPackageName())));
                        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                        int pixels = (int) (100 * scale + 0.5f);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels);
                        params.setMargins(20, 0, 20, 0);
                        bars.setLayoutParams(params);
                        bars.setWeightSum(16);
                        bars.setOrientation(LinearLayout.HORIZONTAL);


                        for (int i = 0; i < 16; i++) {
                            ImageView sixteenth = new ImageView(MainActivity.this);
                            sixteenth.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                            sixteenth.setScaleType(ImageView.ScaleType.FIT_XY);
                            bars.addView(sixteenth);
                        }

                        bars.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                openBarsToolbar(bars);
                                // addNotesDuration();

                            }
                        });
                        measureModeLayout.addView(bars, measureModeLayout.indexOfChild(previousRow));
                        allBars.put(bars, "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>" +
                                "<sixteenth></sixteenth><sixteenth></sixteenth>");
                        measureChanged = true;
                        selectedBars = bars;
                        selectedBars.performClick();
                    }

                }

            }
        });


        lastWordsTextView.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (proUser) {
                    if (lastWords) {
                        lastWords = false;
                        checkboxLastWords.setImageResource(R.drawable.square);
                        putStringToInternal("lyriclastwords", "FALSE");

                    } else {
                        lastWords = true;
                        checkboxLastWords.setImageResource(R.drawable.small_check);
                        putStringToInternal("lyriclastwords", "TRUE");

                    }
                    poemChangeChecker = "";
                } else {
                    openUpgradePopup("Upgrade to Lyric Pro", "These features are very powerful but require Lyric Pro. ", upgradeSpannable, upgradeAmount);
                }
            }
        });

        checkboxLastWords.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                lastWordsTextView.performClick();

            }
        });

        changeSensitivityTextView.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (proUser) {
                    if (highSensitivity) {
                        highSensitivity = false;
                        checkboxChangeSensitivity.setImageResource(R.drawable.square);
                        putStringToInternal("lyrichighsensitivity", "FALSE");

                        poemChangeChecker = "";
                    } else {
                        final ConstraintLayout verifyPopup = (ConstraintLayout) findViewById(R.id.verify_popup);
                        final TextView verifyNo = (TextView) findViewById(R.id.verify_no);
                        final TextView verifyYes = (TextView) findViewById(R.id.verify_yes);
                        final TextView doNotShowAgainLabel = (TextView) findViewById(R.id.warning_hide_near_rhymes_label);
                        final ImageButton doNotShowAgainImage = (ImageButton) findViewById(R.id.warning_hide_near_rhymes_image);
                        doNotShowAgainLabel.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                if (!nearRhymesCheckbox) {
                                    nearRhymesCheckbox = true;
                                    doNotShowAgainImage.setImageResource(R.drawable.small_check);
                                } else {
                                    nearRhymesCheckbox = false;
                                    doNotShowAgainImage.setImageResource(R.drawable.square);
                                }
                            }
                        });
                        doNotShowAgainImage.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                doNotShowAgainLabel.performClick();
                            }
                        });
                        if (nearRhymesCheckbox == true) {
                            putStringToInternal("lyricnearrhymeshidepopup", "true");

                        }
                        String doNotShowAgain = getStringFromInternal("lyricnearrhymeshidepopup", "false");
                        if (doNotShowAgain.equals("true")) {
                            verifyYes.performClick();
                        } else {
                            openPopup(verifyPopup);
                        }

                        verifyYes.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                highSensitivity = true;
                                checkboxChangeSensitivity.setImageResource(R.drawable.small_check);
                                putStringToInternal("lyrichighsensitivity", "TRUE");

                                rhymeFeatures.performClick();
                                poemChangeChecker = "";
                            }
                        });
                        verifyNo.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                rhymeFeatures.performClick();
                            }
                        });
                    }
                } else {
                    openUpgradePopup("Upgrade to Lyric Pro", "These features are very powerful but require Lyric Pro. ", upgradeSpannable, upgradeAmount);
                }
            }
        });

        checkboxChangeSensitivity.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                changeSensitivityTextView.performClick();

            }
        });

        final Intent myIntent = new Intent(this, Main2Activity.class);
        ImageButton homeButton = (ImageButton) findViewById(R.id.menu);
        homeButton.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
//
//
//                String softsave = getStringFromInternal("lyric" + lyricIndex + "poemsoftsave", "soft");
//                String hardsave = getStringFromInternal("lyric" + lyricIndex + "poem", "hard");
//                String softsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", "soft");
//                String hardsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measures", "hard");
//                if (softsave.equals(hardsave) && softsavemeasure.equals(hardsavemeasure)) {
                startActivity(myIntent);
                finish();
//                } else {
//                    hardSave(true);
//                }
            }
        });
        optionsButton.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (spinnerList.getTranslationY() == 0) {
                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                }
                if (measureList.getTranslationY() == 0) {
                    measureList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                }
                if (lengthList.getTranslationY() == 0) {
                    lengthList.animate().translationY(screenHeight);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                }
                if (pitchList.getTranslationY() == 0) {
                    pitchList.animate().translationY(screenHeight);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                }
                if (settingList.getTranslationY() == 0) {
                    settingList.animate().translationY(-screenHeight);
                    optionsButton.setImageResource(R.drawable.settings1);
                } else {
                    settingList.setVisibility(View.VISIBLE);
                    settingList.animate().translationY(0);
                    settingList.bringToFront();
                    mainToolbar.bringToFront();
                    // 2 lines add for admob
                    adLoading.bringToFront();
                    mAdView.bringToFront();
                    findViewById(R.id.view).bringToFront();
                    optionsButton.setImageResource(R.drawable.settings_disabled);
                }

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        notesIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                openPopup(null);
                if (notesFocus) {
                    if (wordInfo.isShown()) {
                        wordInfo.bringToFront();
                        loading.bringToFront();
                        wordInfoTitle.bringToFront();
                        wordinfoBar.bringToFront();
                        wordInfoView.bringToFront();
                        closeWordInfo.bringToFront();
                        bottomToolbarToggler.bringToFront();
                        undo.bringToFront();
                        notesIcon.setImageResource(R.drawable.noteicon);
                        spinnerList.animate().translationY(screenHeight);
                        measureList.animate().translationY(screenHeight);
                        pitchList.animate().translationY(screenHeight);
                        lengthList.animate().translationY(screenHeight);
                        openMeasureSpinner.setImageResource(R.drawable.openspinner);
                        openNoteLength.setImageResource(R.drawable.openspinner);
                        openNotePitch.setImageResource(R.drawable.openspinner);
                        settingList.animate().translationY(-screenHeight);
                        openSpinner.setImageResource(R.drawable.openspinner);
                        optionsButton.setImageResource(R.drawable.settings1);
                        notesFocus = false;
                        if (colorTheme.equals("royal")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus);
                            notesBar.setBackgroundResource(R.drawable.no_focus);
                            notesTitle.setBackgroundResource(R.drawable.tab_left);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);

                        } else if (colorTheme.equals("sunset")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                            notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                        } else if (colorTheme.equals("joy")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                            notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);
                        } else if (colorTheme.equals("dark")) {

                            wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                            notesBar.setBackgroundResource(R.drawable.no_focus_black);
                            notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                            wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);
                        }
                    }

                } else {
                    String oldNotes = getStringFromInternal("lyric" + lyricIndex + "notes", "");
                    notes.setText(oldNotes);
                    notes.bringToFront();
                    notesTitle.bringToFront();
                    notesBar.bringToFront();
                    notesView.bringToFront();
                    closeNotes.bringToFront();
                    bottomToolbarToggler.bringToFront();
                    undo.bringToFront();
                    spinnerList.animate().translationY(screenHeight);
                    settingList.animate().translationY(-screenHeight);
                    measureList.animate().translationY(screenHeight);
                    lengthList.animate().translationY(screenHeight);
                    pitchList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                    openSpinner.setImageResource(R.drawable.openspinner);
                    optionsButton.setImageResource(R.drawable.settings1);
                    notesIcon.setImageResource(R.drawable.noteicondisabled);
                    notes.setVisibility(View.VISIBLE);
                    closeNotes.setVisibility(View.VISIBLE);
                    notesBar.setVisibility(View.VISIBLE);
                    notesTitle.setVisibility(View.VISIBLE);
                    notesView.setVisibility(View.VISIBLE);

                    notesFocus = true;
                    if (colorTheme.equals("royal")) {
                        wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                        notesBar.setBackgroundResource(R.drawable.focus);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right);

                    } else if (colorTheme.equals("sunset")) {
                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                        notesBar.setBackgroundResource(R.drawable.focus_orange);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);

                    } else if (colorTheme.equals("joy")) {
                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                        notesBar.setBackgroundResource(R.drawable.focus_blue);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);

                    } else if (colorTheme.equals("dark")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                        notesBar.setBackgroundResource(R.drawable.focus_black);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);

                    }
                }
                if (wordInfo.isShown() && wordInfo.getHeight() > notes.getHeight()) {

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                    constraintSet.applyTo(mainActivity);

                } else {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                    constraintSet.applyTo(mainActivity);
                }
                if (notesTitle.isShown() && wordInfoTitle.isShown()) {
                    int hintCheck = getIntFromInternal("lyricnotesiconhint", 0);
                    if (hintCheck < 1) {
                        putIntToInternal("lyricnotesiconhint", hintCheck + 1);

                    } else if (hintCheck == 1) {
                        putIntToInternal("lyricnotesiconhint", hintCheck + 1);

                        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
                        ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
                        TextView hintText = (TextView) findViewById(R.id.hint_text);
                        final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
                        hintImage.setImageResource(R.drawable.hint2);
                        hintText.setText("When both the SEARCH tab and NOTES tab are open, you can click the notes icon to toggle which tab has the focus/foreground. Give it a try!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openPopup(hintPopup);
                                openPopup(hintPopup);
                                hintDone.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        hintPopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }

                                });
                            }
                        });
                    }
                }

            }
        });


        closeNotes.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                notesIcon.setImageResource(R.drawable.noteicon);
                notesFocus = false;
                if (colorTheme.equals("royal")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus);
                    notesBar.setBackgroundResource(R.drawable.no_focus);
                    notesTitle.setBackgroundResource(R.drawable.tab_left);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);

                } else if (colorTheme.equals("sunset")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                    notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                } else if (colorTheme.equals("joy")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                    notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);

                } else if (colorTheme.equals("dark")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                    notesBar.setBackgroundResource(R.drawable.no_focus_black);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);

                }
                notes.getLayoutParams().height = wordInfoHeight;
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mainActivity);
                constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                constraintSet.applyTo(mainActivity);
                notes.setVisibility(View.GONE);
                closeNotes.setVisibility(View.GONE);
                notesBar.setVisibility(View.GONE);
                notesTitle.setVisibility(View.GONE);
                notesView.setVisibility(View.GONE);
            }
        });

        closeWordInfo.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                pl.droidsonroids.gif.GifImageView loading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView2);
                loading.setVisibility(View.GONE);
                if (notes.isShown()) {
                    notesIcon.setImageResource(R.drawable.noteicondisabled);
                    if (colorTheme.equals("royal")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                        notesBar.setBackgroundResource(R.drawable.focus);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right);

                    } else if (colorTheme.equals("sunset")) {
                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                        notesBar.setBackgroundResource(R.drawable.focus_orange);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);

                    } else if (colorTheme.equals("joy")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                        notesBar.setBackgroundResource(R.drawable.focus_blue);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);

                    } else if (colorTheme.equals("dark")) {

                        wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                        notesBar.setBackgroundResource(R.drawable.focus_black);
                        notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                        wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);

                    }
                }
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mainActivity);
                constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                constraintSet.applyTo(mainActivity);
                wordInfo.setVisibility(View.GONE);
                closeWordInfo.setVisibility(View.GONE);
                wordinfoBar.setVisibility(View.GONE);
                wordInfoTitle.setVisibility(View.GONE);
                wordInfoView.setVisibility(View.GONE);

            }
        });


        ImageView orientationIcon = (ImageView) findViewById(R.id.orientation_checkbox);
        orientationIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                orientation.performClick();
            }
        });
        orientation.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                final ConstraintLayout orientationPopup = (ConstraintLayout) findViewById(R.id.orientation_popup);

                final TextView yes = (TextView) findViewById(R.id.popup_yes_orientation);
                final TextView no = (TextView) findViewById(R.id.popup_no_orientation);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(orientationPopup);

                        yes.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (metronomeRunning) {
                                    final ImageButton metronomeIcon = (ImageButton) findViewById(R.id.metronome);
                                    metronomeIcon.performClick();
                                }
                                orientationPopup.setVisibility(View.GONE);
                                popupBackground.setVisibility(View.GONE);
                                String orientationMode = getStringFromInternal("lyricorientation", "NEW");
                                if (orientationMode.equals("LANDSCAPE")) {
                                    putStringToInternal("lyricorientation", "PORTRAIT");

                                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    TextView orientationText = (TextView) findViewById(R.id.orientation_text);
                                    orientationText.setText("Do you want to switch your orientation to portrait?");

                                } else {
                                    putStringToInternal("lyricorientation", "LANDSCAPE");

                                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    TextView orientationText = (TextView) findViewById(R.id.orientation_text);
                                    orientationText.setText("Do you want to switch your orientation to landscape?");
                                }
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                generalSettings.performClick();
                            }
                        });
                    }
                });
            }
        });

        ImageView darkmodeIcon = (ImageView) findViewById(R.id.darkmode_checkbox);
        darkmodeIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                darkmode.performClick();
            }
        });
        darkmode.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                final ConstraintLayout darkmodePopup = (ConstraintLayout) findViewById(R.id.darkmode_popup);

                final TextView yes = (TextView) findViewById(R.id.popup_yes_darkmode);
                final TextView no = (TextView) findViewById(R.id.popup_no_darkmode);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(darkmodePopup);
                        if (proUser) {
                            yes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    generalSettings.performClick();
                                    String darkmodeMode = getStringFromInternal("lyricdarkmode", "FALSE");
                                    if (darkmodeMode.equals("FALSE")) {
                                        putStringToInternal("lyricdarkmode", "TRUE");

                                        findViewById(R.id.main_activity).setBackgroundColor(Color.BLACK);
                                        wordInfo.setBackgroundColor(Color.BLACK);
                                        notes.setBackgroundColor(Color.BLACK);
                                        title.setTextColor(Color.WHITE);
                                        poem.setTextColor(Color.WHITE);
                                        poem.setHintTextColor(Color.GRAY);
                                        title.setHintTextColor(Color.GRAY);
                                        syllables.setTextColor(Color.WHITE);
                                        wordInfo.setTextColor(Color.WHITE);
                                        notes.setTextColor(Color.WHITE);
                                        notes.setHintTextColor(Color.GRAY);
                                        for (int lineNum : linesWithoutRecordings.keySet()) {
                                            linesWithoutRecordings.get(lineNum).setTextColor(Color.WHITE);
                                        }
                                        final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                        for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                                            if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                                ((LinearLayout) measureModeLayout.getChildAt(i)).setBackgroundResource(R.drawable.bars_white);
                                                int sixteenthIndex = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).indexOf("<sixteenth>");
                                                int sixteenthCount = 0;
                                                while (sixteenthIndex >= 0) {
                                                    String imageResource = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).substring(sixteenthIndex + 11, ordinalIndexOf(allBars.get((LinearLayout) measureModeLayout.getChildAt(i)), "</sixteenth>", sixteenthCount + 1));
                                                    if (imageResource.contains("_")) {
                                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                                            imageResource = imageResource + "white";
                                                        } else {
                                                            imageResource = imageResource + "black";
                                                        }
                                                        ((ImageView) ((LinearLayout) measureModeLayout.getChildAt(i)).getChildAt(sixteenthCount)).setImageResource(getResources().getIdentifier(imageResource, "drawable", getPackageName()));
                                                    }
                                                    sixteenthIndex = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).indexOf("<sixteenth>", sixteenthIndex + 1);
                                                    sixteenthCount++;
                                                }
                                                continue;
                                            }
                                            LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                                            i++;
                                            LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                                            EditText col1 = (EditText) row1.getChildAt(0);
                                            EditText col2 = (EditText) row1.getChildAt(1);
                                            EditText col3 = (EditText) row1.getChildAt(2);
                                            EditText col4 = (EditText) row1.getChildAt(3);
                                            TextView sylCol1 = (TextView) row2.getChildAt(0);
                                            TextView sylCol2 = (TextView) row2.getChildAt(1);
                                            TextView sylCol3 = (TextView) row2.getChildAt(2);
                                            TextView sylCol4 = (TextView) row2.getChildAt(3);
                                            col1.setTextColor(Color.WHITE);
                                            col2.setTextColor(Color.WHITE);
                                            col3.setTextColor(Color.WHITE);
                                            col4.setTextColor(Color.WHITE);
                                            sylCol1.setTextColor(Color.WHITE);
                                            sylCol2.setTextColor(Color.WHITE);
                                            sylCol3.setTextColor(Color.WHITE);
                                            sylCol4.setTextColor(Color.WHITE);
                                            col1.setHintTextColor(Color.GRAY);
                                            col2.setHintTextColor(Color.GRAY);
                                            col3.setHintTextColor(Color.GRAY);
                                            col4.setHintTextColor(Color.GRAY);
                                            sylCol1.setHintTextColor(Color.GRAY);
                                            sylCol2.setHintTextColor(Color.GRAY);
                                            sylCol3.setHintTextColor(Color.GRAY);
                                            sylCol4.setHintTextColor(Color.GRAY);
                                        }
                                        TextView darkmodeText = (TextView) findViewById(R.id.darkmode_text);
                                        darkmodeText.setText("Do you want to turn off Night Mode?");


                                    } else {
                                        putStringToInternal("lyricdarkmode", "FALSE");

                                        findViewById(R.id.main_activity).setBackgroundColor(Color.WHITE);
                                        wordInfo.setBackgroundResource(R.color.lavender);
                                        notes.setBackgroundColor(Color.WHITE);
                                        title.setTextColor(Color.BLACK);
                                        poem.setTextColor(Color.BLACK);
                                        poem.setHintTextColor(Color.GRAY);
                                        title.setHintTextColor(Color.GRAY);
                                        syllables.setTextColor(Color.BLACK);
                                        wordInfo.setTextColor(Color.BLACK);
                                        notes.setTextColor(Color.BLACK);
                                        notes.setHintTextColor(Color.GRAY);
                                        for (int lineNum : linesWithoutRecordings.keySet()) {
                                            linesWithoutRecordings.get(lineNum).setTextColor(Color.BLACK);
                                        }
                                        final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                        for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                                            if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                                ((LinearLayout) measureModeLayout.getChildAt(i)).setBackgroundResource(R.drawable.bars_black);
                                                int sixteenthIndex = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).indexOf("<sixteenth>");
                                                int sixteenthCount = 0;
                                                while (sixteenthIndex >= 0) {
                                                    String imageResource = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).substring(sixteenthIndex + 11, ordinalIndexOf(allBars.get((LinearLayout) measureModeLayout.getChildAt(i)), "</sixteenth>", sixteenthCount + 1));
                                                    if (imageResource.contains("_")) {
                                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                                            imageResource = imageResource + "white";
                                                        } else {
                                                            imageResource = imageResource + "black";
                                                        }
                                                        ((ImageView) ((LinearLayout) measureModeLayout.getChildAt(i)).getChildAt(sixteenthCount)).setImageResource(getResources().getIdentifier(imageResource, "drawable", getPackageName()));
                                                    }
                                                    sixteenthIndex = allBars.get((LinearLayout) measureModeLayout.getChildAt(i)).indexOf("<sixteenth>", sixteenthIndex + 1);
                                                    sixteenthCount++;
                                                }
                                                continue;
                                            }
                                            LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                                            i++;
                                            LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                                            EditText col1 = (EditText) row1.getChildAt(0);
                                            EditText col2 = (EditText) row1.getChildAt(1);
                                            EditText col3 = (EditText) row1.getChildAt(2);
                                            EditText col4 = (EditText) row1.getChildAt(3);
                                            TextView sylCol1 = (TextView) row2.getChildAt(0);
                                            TextView sylCol2 = (TextView) row2.getChildAt(1);
                                            TextView sylCol3 = (TextView) row2.getChildAt(2);
                                            TextView sylCol4 = (TextView) row2.getChildAt(3);
                                            col1.setTextColor(Color.BLACK);
                                            col2.setTextColor(Color.BLACK);
                                            col3.setTextColor(Color.BLACK);
                                            col4.setTextColor(Color.BLACK);
                                            sylCol1.setTextColor(Color.BLACK);
                                            sylCol2.setTextColor(Color.BLACK);
                                            sylCol3.setTextColor(Color.BLACK);
                                            sylCol4.setTextColor(Color.BLACK);
                                            col1.setHintTextColor(Color.GRAY);
                                            col2.setHintTextColor(Color.GRAY);
                                            col3.setHintTextColor(Color.GRAY);
                                            col4.setHintTextColor(Color.GRAY);
                                            sylCol1.setHintTextColor(Color.GRAY);
                                            sylCol2.setHintTextColor(Color.GRAY);
                                            sylCol3.setHintTextColor(Color.GRAY);
                                            sylCol4.setHintTextColor(Color.GRAY);
                                        }
                                        TextView darkmodeText = (TextView) findViewById(R.id.darkmode_text);
                                        darkmodeText.setText("Do you want to turn on Night Mode?");


                                    }
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    generalSettings.performClick();
                                }
                            });
                        } else {
                            openUpgradePopup("Upgrade to Lyric Pro", "Night mode requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
                        }
                    }
                });
            }
        });


//        final ImageView hideToolbarIcon = (ImageView) findViewById(R.id.hideToolbar_checkbox);
//        hideToolbarIcon.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//                hideToolbarOption.performClick();
//            }
//        });
//        hideToolbarOption.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//
//                final ConstraintLayout hideToolbarPopup = (ConstraintLayout) findViewById(R.id.hideToolbar_popup);
//
//                final TextView yes = (TextView) findViewById(R.id.popup_yes_hideToolbar);
//                final TextView no = (TextView) findViewById(R.id.popup_no_hideToolbar);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        openPopup(hideToolbarPopup);
//
//                        yes.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//
//                                hideToolbarPopup.setVisibility(View.GONE);

//        popupBackground.setVisibility(View.GONE);
//                                String hideToolbarMode = getStringFromInternal("lyrichidetoolbar", "FALSE");
//                                if (hideToolbarMode.equals("FALSE")) {
//                                    putStringToInternal("lyrichidetoolbar", "TRUE");
//                                                    boolean success = false;

//                                    hideToolbar = true;
//                                    TextView hideToolbarText = (TextView) findViewById(R.id.hideToolbar_text);
//                                    TextView hideToolbarTitle = (TextView) findViewById(R.id.hideToolbar_title);
//                                    ImageView hideToolbarIcon = (ImageView) findViewById(R.id.hideToolbar_checkbox);
//                                    hideToolbarIcon.setImageResource(R.drawable.displayicon);
//                                    hideToolbarText.setText("Do you want to display the bottom toolbar when the keyboard is open?");
//                                    hideToolbarTitle.setText("Display Bottom Toolbar");
//                                    hideToolbarOption.setText("Display Bottom Toolbar");
//
//                                } else {
//                                    putStringToInternal("lyrichidetoolbar", "FALSE");
//                                                    boolean success = false;

//                                    hideToolbar = false;
//                                    TextView hideToolbarText = (TextView) findViewById(R.id.hideToolbar_text);
//                                    TextView hideToolbarTitle = (TextView) findViewById(R.id.hideToolbar_title);
//                                    ImageView hideToolbarIcon = (ImageView) findViewById(R.id.hideToolbar_checkbox);
//                                    hideToolbarIcon.setImageResource(R.drawable.hideicon);
//                                    hideToolbarText.setText("Do you want to hide the bottom toolbar when the keyboard is open?");
//                                    hideToolbarTitle.setText("Hide Bottom Toolbar");
//                                    hideToolbarOption.setText("Hide Bottom Toolbar");
//                                }
//                            }
//                        });
//                        no.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                hideToolbarPopup.setVisibility(View.GONE);
//        popupBackground.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                });
//            }
//        });

        ImageView changeColorIcon = (ImageView) findViewById(R.id.changecolor_checkbox);
        changeColorIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                changeColor.performClick();
            }
        });
        changeColor.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout changeColorPopup = (ConstraintLayout) findViewById(R.id.changecolor_popup);


                final TextView royal = (TextView) findViewById(R.id.popup_royal);
                final TextView sunset = (TextView) findViewById(R.id.popup_sunset);
                final TextView joy = (TextView) findViewById(R.id.popup_joy);
                final TextView dark = (TextView) findViewById(R.id.popup_dark);
                final ImageButton checkboxRoyal = (ImageButton) findViewById(R.id.checkbox_royal);
                final ImageButton checkboxSunset = (ImageButton) findViewById(R.id.checkbox_sunset);
                final ImageButton checkboxJoy = (ImageButton) findViewById(R.id.checkbox_joy);
                final ImageButton checkboxDark = (ImageButton) findViewById(R.id.checkbox_dark);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(changeColorPopup);
                        TextView done = (TextView) findViewById(R.id.popup_done_changecolor);

                        royal.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                colorTheme = "royal";
                                changeColors("royal");
                                checkboxRoyal.setImageResource(R.drawable.small_check);
                                checkboxSunset.setImageResource(R.drawable.circle);
                                checkboxJoy.setImageResource(R.drawable.circle);
                                checkboxDark.setImageResource(R.drawable.circle);
                                putStringToInternal("lyriccolortheme", "royal");

                            }
                        });
                        sunset.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeColors("sunset");
                                colorTheme = "sunset";
                                checkboxRoyal.setImageResource(R.drawable.circle);
                                checkboxSunset.setImageResource(R.drawable.small_check);
                                checkboxJoy.setImageResource(R.drawable.circle);
                                checkboxDark.setImageResource(R.drawable.circle);
                                putStringToInternal("lyriccolortheme", "sunset");

                            }
                        });
                        joy.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeColors("joy");
                                colorTheme = "joy";
                                checkboxRoyal.setImageResource(R.drawable.circle);
                                checkboxSunset.setImageResource(R.drawable.circle);
                                checkboxJoy.setImageResource(R.drawable.small_check);
                                checkboxDark.setImageResource(R.drawable.circle);
                                putStringToInternal("lyriccolortheme", "joy");


                            }
                        });
                        dark.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                changeColors("dark");
                                colorTheme = "dark";
                                checkboxRoyal.setImageResource(R.drawable.circle);
                                checkboxSunset.setImageResource(R.drawable.circle);
                                checkboxJoy.setImageResource(R.drawable.circle);
                                checkboxDark.setImageResource(R.drawable.small_check);
                                putStringToInternal("lyriccolortheme", "dark");

                            }
                        });
                        checkboxRoyal.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                royal.performClick();
                            }
                        });
                        checkboxSunset.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                sunset.performClick();
                            }
                        });
                        checkboxJoy.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                joy.performClick();
                            }
                        });
                        checkboxDark.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                dark.performClick();
                            }
                        });
                        done.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                generalSettings.performClick();
                                if (!proUser) {
                                    royal.performClick();
                                    openUpgradePopup("Upgrade to Lyric Pro", "Changing the color theme requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
                                }


                            }
                        });


                    }
                });

            }
        });

        ImageView changeFontFamilyIcon = (ImageView) findViewById(R.id.changefont_checkbox);
        changeFontFamilyIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                changeFontFamily.performClick();
            }
        });

        changeFontFamily.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout changeFontFamilyPopup = (ConstraintLayout) findViewById(R.id.changefont_popup);


                final TextView sourceSansProSelect = (TextView) findViewById(R.id.popup_sourcesanspro);
                final ImageButton sourceSansProSelectBox = (ImageButton) findViewById(R.id.checkbox_sourcesanspro);
                final TextView markaziSelect = (TextView) findViewById(R.id.popup_markazi);
                final ImageButton markaziSelectBox = (ImageButton) findViewById(R.id.checkbox_markazi);
                final TextView slaboSelect = (TextView) findViewById(R.id.popup_slabo);
                final ImageButton slaboSelectBox = (ImageButton) findViewById(R.id.checkbox_slabo);
                final TextView ubuntuSelect = (TextView) findViewById(R.id.popup_ubuntu);
                final ImageButton ubuntuSelectBox = (ImageButton) findViewById(R.id.checkbox_ubuntu);
                final TextView patrickHandSelect = (TextView) findViewById(R.id.popup_patrickhand);
                final ImageButton patrickHandSelectBox = (ImageButton) findViewById(R.id.checkbox_patrickhand);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(changeFontFamilyPopup);
                        TextView done = (TextView) findViewById(R.id.popup_done_changefont);

                        sourceSansProSelect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeFontFamily("SOURCESANSPRO");
                                putStringToInternal("lyrictypeface", "SOURCESANSPRO");

                            }
                        });
                        sourceSansProSelectBox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                sourceSansProSelect.performClick();
                            }
                        });
                        markaziSelect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeFontFamily("MARKAZI");
                                putStringToInternal("lyrictypeface", "MARKAZI");

                            }
                        });
                        markaziSelectBox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                markaziSelect.performClick();
                            }
                        });
                        slaboSelect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeFontFamily("SLABO");
                                putStringToInternal("lyrictypeface", "SLABO");

                            }
                        });
                        slaboSelectBox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                slaboSelect.performClick();
                            }
                        });
                        ubuntuSelect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeFontFamily("UBUNTU");
                                putStringToInternal("lyrictypeface", "UBUNTU");

                            }
                        });
                        ubuntuSelectBox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {

                                ubuntuSelect.performClick();
                            }
                        });
                        patrickHandSelect.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                changeFontFamily("PATRICKHAND");
                                putStringToInternal("lyrictypeface", "PATRICKHAND");

                            }
                        });
                        patrickHandSelectBox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                patrickHandSelect.performClick();
                            }
                        });
                        done.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                generalSettings.performClick();
                                if (!proUser) {
                                    sourceSansProSelect.performClick();
                                    openUpgradePopup("Upgrade to Lyric Pro", "Changing the font requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
                                }


                            }
                        });


                    }
                });

            }
        });

        ImageView reviseRateIcon = (ImageView) findViewById(R.id.calculate_frequency_checkbox);
        reviseRateIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                reviseRate.performClick();
            }
        });

        reviseRate.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout revisePopup = (ConstraintLayout) findViewById(R.id.revise_popup);


                final TextView turnOffAuto = (TextView) findViewById(R.id.popup_turn_off);
                final TextView afterTyping = (TextView) findViewById(R.id.popup_after_typing);
                final TextView swipeDelay = (TextView) findViewById(R.id.popup_swipe_delay);
                final ImageButton checkboxTurnOffAuto = (ImageButton) findViewById(R.id.checkbox_turn_off);
                final ImageButton checkboxSwipeDelay = (ImageButton) findViewById(R.id.checkbox_swipe_delay);
                final ImageButton checkboxAfterTyping = (ImageButton) findViewById(R.id.checkbox_after_typing);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(revisePopup);
                        TextView done = (TextView) findViewById(R.id.popup_done_revision);

                        turnOffAuto.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                checkboxAfterTyping.setImageResource(R.drawable.circle);
                                checkboxTurnOffAuto.setImageResource(R.drawable.small_check);
                                checkboxSwipeDelay.setImageResource(R.drawable.circle);
                                putStringToInternal("lyricreviserate", "AUTO_OFF");

                                localForceOffline = true;
                            }
                        });
                        swipeDelay.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                checkboxAfterTyping.setImageResource(R.drawable.circle);
                                checkboxTurnOffAuto.setImageResource(R.drawable.circle);
                                checkboxSwipeDelay.setImageResource(R.drawable.small_check);
                                putStringToInternal("lyricreviserate", "SWIPE_DELAY");

                                delay = 5000;
                                localForceOffline = false;
                            }
                        });
                        afterTyping.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                checkboxAfterTyping.setImageResource(R.drawable.small_check);
                                checkboxTurnOffAuto.setImageResource(R.drawable.circle);
                                checkboxSwipeDelay.setImageResource(R.drawable.circle);
                                putStringToInternal("lyricreviserate", "AFTER_TYPING");

                                delay = 2000;
                                localForceOffline = false;
                            }
                        });
                        checkboxTurnOffAuto.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                checkboxAfterTyping.setImageResource(R.drawable.circle);
                                checkboxTurnOffAuto.setImageResource(R.drawable.small_check);
                                checkboxSwipeDelay.setImageResource(R.drawable.circle);
                                putStringToInternal("lyricreviserate", "AUTO_OFF");

                                localForceOffline = true;
                            }
                        });
                        checkboxSwipeDelay.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                checkboxAfterTyping.setImageResource(R.drawable.circle);
                                checkboxTurnOffAuto.setImageResource(R.drawable.circle);
                                checkboxSwipeDelay.setImageResource(R.drawable.small_check);
                                putStringToInternal("lyricreviserate", "SWIPE_DELAY");

                                delay = 5000;
                                localForceOffline = false;
                            }
                        });
                        checkboxAfterTyping.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                checkboxAfterTyping.setImageResource(R.drawable.small_check);
                                checkboxTurnOffAuto.setImageResource(R.drawable.circle);
                                checkboxSwipeDelay.setImageResource(R.drawable.circle);
                                putStringToInternal("lyricreviserate", "AFTER_TYPING");

                                delay = 2000;
                                localForceOffline = false;
                            }
                        });
                        done.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                globalForceOffline = localForceOffline;
                                generalSettings.performClick();

                            }
                        });
                    }
                });

            }
        });

        ImageView tutorialIcon = (ImageView) findViewById(R.id.tutorial_checkbox);
        tutorialIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                tutorial.performClick();
            }
        });

        tutorial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "kMCBejmsk9U"));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + "kMCBejmsk9U"));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
        ImageView makeADonationIcon = (ImageView) findViewById(R.id.donate_checkbox);
        makeADonationIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                makeADonation.performClick();
            }
        });

        makeADonation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ConstraintLayout donatePopup = (ConstraintLayout) findViewById(R.id.donate_popup);
                final TextView donateDone = (TextView) findViewById(R.id.donate_okay);
                final TextView donateText = (TextView) findViewById(R.id.donate_text);
                final ImageView export = (ImageView) findViewById(R.id.export_icon);
                final LinearLayout exportAndBackup = (LinearLayout) findViewById(R.id.backup_export);
                final LinearLayout justBackup = (LinearLayout) findViewById(R.id.backup);
                exportAndBackup.setVisibility(View.VISIBLE);
                justBackup.setVisibility(View.GONE);
                openPopup(donatePopup);
                donateDone.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        donatePopup.setVisibility(View.GONE);
                        popupBackground.setVisibility(View.GONE);
                    }
                });

                export.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {




                        int hintCheck = getIntFromInternal("lyricemailhint", 0);
                        if (hintCheck < 1) {
                            putIntToInternal("lyricemailhint", hintCheck + 1);

                            final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
                            ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
                            TextView hintText = (TextView) findViewById(R.id.hint_text);
                            final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
                            hintImage.setImageResource(0);
                            hintText.setText("You can export your lyrics to any appropriate application like: Google Drive, Gmail, etc. Give it a try!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openPopup(hintPopup);
                                    hintDone.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            hintPopup.setVisibility(View.GONE);
                                            popupBackground.setVisibility(View.GONE);
                                            openPopup(donatePopup);
                                            export.performClick();
                                        }

                                    });
                                }
                            });
                        } else {
                            String[] TO = {""};
                            String[] CC = {""};
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("text/plain");
                            String body = "";
                            if (poem.getSelectionStart() != poem.getSelectionEnd()) {
                                body = poem.getText().toString().substring(poem.getSelectionStart(), poem.getSelectionEnd()).trim();
                            } else {
                                body = poem.getText().toString().trim();
                            }


                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                            emailIntent.putExtra(Intent.EXTRA_CC, CC);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, title.getText().toString());
                            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(MainActivity.this,
                                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

//                backup.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        exportAndBackup.setVisibility(View.GONE);
//                        justBackup.setVisibility(View.VISIBLE);
//                        requestBackup();
//                        Toast.makeText(mainActivityHelper, "Lyric backup triggered",
//                                Toast.LENGTH_SHORT).show();
//
//                        final int doNotShowAgain = getIntFromInternal("lyricdonotshowbackupwarning", 0);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (doNotShowAgain < 2) {
//                                    openPopup(donatePopup);
//                                    final TextView doNotShowAgainLabel = (TextView) findViewById(R.id.warning_hide_label);
//                                    final ImageButton doNotShowAgainImage = (ImageButton) findViewById(R.id.warning_hide_image);
//                                    if (doNotShowAgain == 1) {
//                                        doNotShowAgainImage.setVisibility(View.VISIBLE);
//                                        doNotShowAgainLabel.setVisibility(View.VISIBLE);
//                                        doNotShowAgainLabel.setOnClickListener(new View.OnClickListener()
//
//                                        {
//                                            public void onClick(View v) {
//                                                if (!warningCheckbox) {
//                                                    warningCheckbox = true;
//                                                    doNotShowAgainImage.setImageResource(R.drawable.small_check);
//                                                } else {
//                                                    warningCheckbox = false;
//                                                    doNotShowAgainImage.setImageResource(R.drawable.square);
//                                                }
//                                            }
//                                        });
//                                        doNotShowAgainImage.setOnClickListener(new View.OnClickListener()
//
//                                        {
//                                            public void onClick(View v) {
//                                                doNotShowAgainLabel.performClick();
//                                            }
//                                        });
//                                    }
//                                    donateDone.setOnClickListener(new View.OnClickListener() {
//                                        public void onClick(View v) {
//                                            donatePopup.setVisibility(View.GONE);
//                                            popupBackground.setVisibility(View.GONE);
//
//                                            if (doNotShowAgain == 1) {
//                                                donatePopup.setVisibility(View.GONE);
//                                                popupBackground.setVisibility(View.GONE);
//                                                if (warningCheckbox) {
//                                                    putIntToInternal("lyricdonotshowbackupwarning", 2);
//
//                                                }
//                                            } else {
//                                                donateText.setText("Warning: If you have disabled backups on your device, Lyric will not be able to backup your data.\n\nAlso, It can take up to a day for Google Play to backup these changes so we recommend backing up your lyrics daily.");
//                                                doNotShowAgainImage.setVisibility(View.VISIBLE);
//                                                doNotShowAgainLabel.setVisibility(View.VISIBLE);
//                                                openPopup(donatePopup);
//                                                donateDone.setOnClickListener(new View.OnClickListener() {
//                                                    public void onClick(View v) {
//                                                        donatePopup.setVisibility(View.GONE);
//                                                        popupBackground.setVisibility(View.GONE);
//                                                        doNotShowAgainImage.setVisibility(View.GONE);
//                                                        doNotShowAgainLabel.setVisibility(View.GONE);
//                                                        donateText.setText("Your lyrics are being backed up to your Google Play account. Future installations using your Google Play account will restore your lyrics and settings.");
//                                                        if (warningCheckbox) {
//                                                            putIntToInternal("lyricdonotshowbackupwarning", 1);
//
//                                                            warningCheckbox = false;
//                                                            doNotShowAgainImage.setImageResource(R.drawable.square);
//                                                        }
//                                                    }
//                                                });
//                                                doNotShowAgainLabel.setOnClickListener(new View.OnClickListener()
//
//                                                {
//                                                    public void onClick(View v) {
//                                                        if (!warningCheckbox) {
//                                                            warningCheckbox = true;
//                                                            doNotShowAgainImage.setImageResource(R.drawable.small_check);
//                                                        } else {
//                                                            warningCheckbox = false;
//                                                            doNotShowAgainImage.setImageResource(R.drawable.square);
//                                                        }
//                                                    }
//                                                });
//                                                doNotShowAgainImage.setOnClickListener(new View.OnClickListener()
//
//                                                {
//                                                    public void onClick(View v) {
//                                                        doNotShowAgainLabel.performClick();
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });
//                                } else {
//                                    donateDone.performClick();
//                                }
//                            }
//                        });
//                    }
//                });


            }
        });
//        final ImageView shareIcon = (ImageView) findViewById(R.id.share_checkbox);

//        shareIcon.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//                share.performClick();
//            }
//        });
//
//        share.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//                final ConstraintLayout sharePopup = (ConstraintLayout) findViewById(R.id.share_popup);
//                final ImageView followFacebook = (ImageView) findViewById(R.id.follow_facebook);
//                final ImageView followTwitter = (ImageView) findViewById(R.id.follow_twitter);
//                final TextView openShare = (TextView) findViewById(R.id.open_share_lyric);
//                final TextView shareDone = (TextView) findViewById(R.id.share_okay);
//                final TextView title = (TextView) findViewById(R.id.share_title);
//                final TextView text = (TextView) findViewById(R.id.share_text);
//                final LinearLayout followContent = (LinearLayout) findViewById(R.id.follow_lyric);
//                final LinearLayout shareContent = (LinearLayout) findViewById(R.id.share_lyric);
//                followContent.setVisibility(View.VISIBLE);
//                popupBackground.setVisibility(View.GONE);
//                title.setText("Follow & Like Lyric");
//                text.setText("Help support the app, learn and contribute to new updates and promotions.");
//                shareContent.setVisibility(View.GONE);
//                openPopup(sharePopup);
//                followFacebook.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//
//
//
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            try {
//                                MainActivity.this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
//                                i = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/825719137620739"));
//                            } catch (Exception e) {
//                                i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/LyricNotepad"));
//                            }
//                            startActivity(i);
//
//                    }
//                });
//                followTwitter.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//
//
//
//                            String url = "";
//                            url = "http://twitter.com/LyricNotepad";
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(url));
//                            startActivity(i);
//
//                    }
//                });
//                openShare.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        followContent.setVisibility(View.GONE);
//                        shareContent.setVisibility(View.VISIBLE);
//                        final ImageView shareFacebook = (ImageView) findViewById(R.id.share_facebook);
//                        final ImageView shareTwitter = (ImageView) findViewById(R.id.share_twitter);
//                        title.setText("Share Your Lyric");
//                        text.setText("Share your lyrics on facebook or twitter.");
//                        openPopup(sharePopup);
//                        shareFacebook.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//
//
//
//
//                                    int hintCheck = getIntFromInternal("lyricfacebookhint", 0);
//                                    if (hintCheck < 1) {
//                                        putIntToInternal("lyricfacebookhint", hintCheck + 1);
//
//                                        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
//                                        ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
//                                        TextView hintText = (TextView) findViewById(R.id.hint_text);
//                                        final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
//                                        hintImage.setImageResource(0);
//                                        hintText.setText("When using the Facebook share feature, your lyrics are automatically copied to the clipboard so all you have to do is paste them into the input field. Give it a try!");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                openPopup(hintPopup);
//                                                hintDone.setOnClickListener(new View.OnClickListener() {
//                                                    public void onClick(View v) {
//                                                        hintPopup.setVisibility(View.GONE);
//                                                        popupBackground.setVisibility(View.GONE);
//                                                        openPopup(sharePopup);
//                                                        shareFacebook.performClick();
//
//
//                                                    }
//
//                                                });
//                                            }
//                                        });
//                                    } else {
//                                        shareDialog = new ShareDialog(MainActivity.this);
//                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                                        if (poem.getSelectionStart() != poem.getSelectionEnd()) {
//                                            ClipData clip = ClipData.newPlainText("Poem", poem.getText().toString().substring(poem.getSelectionStart(), poem.getSelectionEnd()).trim());
//                                            clipboard.setPrimaryClip(clip);
//                                        } else {
//                                            ClipData clip = ClipData.newPlainText("Poem", poem.getText().toString().trim());
//                                            clipboard.setPrimaryClip(clip);
//                                        }
//
//                                        if (ShareDialog.canShow(ShareLinkContent.class)) {
//                                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                                                    .setContentUrl(Uri.parse("https://www.facebook.com/LyricNotepad/"))
//                                                    .build();
//                                            shareDialog.show(linkContent);
//                                            Toast toast = Toast.makeText(mainActivityHelper, "Lyric copied to clipboard",
//                                                    Toast.LENGTH_LONG);
//                                            toast.setGravity(Gravity.CENTER, 0, 0);
//                                            toast.show();
//                                        }
//                                    }
//
//                            }
//                        });
//                        shareTwitter.setOnClickListener(new View.OnClickListener()
//
//                        {
//                            public void onClick(View v) {
//
//
//
//                                    int hintCheck = getIntFromInternal("lyrictwitterhint", 0);
//                                    if (hintCheck < 1) {
//                                        putIntToInternal("lyrictwitterhint", hintCheck + 1);
//
//                                        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
//                                        ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
//                                        TextView hintText = (TextView) findViewById(R.id.hint_text);
//                                        final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
//                                        hintImage.setImageResource(0);
//                                        hintText.setText("If you have a portion of your lyrics highlighted when clicking a social media or the share icon only that portion will be shared. Give it a try!");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                openPopup(hintPopup);
//                                                hintDone.setOnClickListener(new View.OnClickListener() {
//                                                    public void onClick(View v) {
//                                                        hintPopup.setVisibility(View.GONE);
//                                                        popupBackground.setVisibility(View.GONE);
//                                                        openPopup(sharePopup);
//                                                        shareTwitter.performClick();
//                                                    }
//
//                                                });
//                                            }
//                                        });
//                                    } else {
//                                        String url = "";
//                                        if (poem.getSelectionStart() != poem.getSelectionEnd()) {
//                                            try {
//                                                url = "http://www.twitter.com/intent/tweet?&text=@LyricNotepad%0a" + URLEncoder.encode(poem.getText().toString().substring(poem.getSelectionStart(), poem.getSelectionEnd()).trim(), "UTF-8");
//                                            } catch (UnsupportedEncodingException e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else {
//                                            try {
//                                                url = "http://www.twitter.com/intent/tweet?&text=@LyricNotepad%0a" + URLEncoder.encode(poem.getText().toString().trim(), "UTF-8");
//                                            } catch (UnsupportedEncodingException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                        Intent i = new Intent(Intent.ACTION_VIEW);
//                                        i.setData(Uri.parse(url));
//                                        startActivity(i);
//                                    }
//
//                            }
//                        });
//                    }
//                });
//
//                shareDone.setOnClickListener(new View.OnClickListener()
//
//                {
//                    public void onClick(View v) {
//                        sharePopup.setVisibility(View.GONE);
//                        popupBackground.setVisibility(View.GONE);
//                    }
//                });
//
//            }
//        });
        final ImageView upgradeIcon = (ImageView) findViewById(R.id.upgrade_checkbox);

        upgradeIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                upgrade.performClick();
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                openUpgradePopup("Upgrade to Lyric Pro", "", upgradeSpannable, upgradeAmount);

            }
        });


        ImageView rhymeFeaturesIcon = (ImageView) findViewById(R.id.rhyme_features_checkbox);
        rhymeFeaturesIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                rhymeFeatures.performClick();
            }
        });

        rhymeFeatures.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout rhymeFeaturesPopup = (ConstraintLayout) findViewById(R.id.rhyme_features_popup);
                final ConstraintLayout ignoreListPopup = (ConstraintLayout) findViewById(R.id.ignore_list_popup);
                final ConstraintLayout createRhymesPopup = (ConstraintLayout) findViewById(R.id.create_rhymes_popup);
                final ConstraintLayout changeClefPopup = (ConstraintLayout) findViewById(R.id.change_clef_popup);

                final TextView ignoreWordsClick = (TextView) findViewById(R.id.popup_ignore_words);
                final TextView createRhymesClick = (TextView) findViewById(R.id.popup_create_rhymes);
                final ImageButton ignoreWordsCheckbox = (ImageButton) findViewById(R.id.checkbox_ignore_words);
                final ImageButton createRhymesCheckbox = (ImageButton) findViewById(R.id.checkbox_create_rhymes);

                final TextView changeClefClick = (TextView) findViewById(R.id.popup_change_clef);
                final ImageButton changeClefCheckbox = (ImageButton) findViewById(R.id.checkbox_change_clef);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(rhymeFeaturesPopup);


                        ignoreWordsClick.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (proUser) {
                                    openPopup(ignoreListPopup);
                                    TextView done = (TextView) findViewById(R.id.ignore_list_okay);
                                    done.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            String ignoreListEditTextString = ignoreListEditText.getText().toString();
                                            ignoreListEditTextString = ignoreListEditTextString.replaceAll("[^a-zA-Z0-9']+", " ").replaceAll(" +", " ").toLowerCase();
                                            ignoredWords = new ArrayList<>(Arrays.asList(ignoreListEditTextString.split(" ")));
                                            ignoreListEditText.setText(ignoreListEditTextString);
                                            putStringToInternal("lyricignorelist", ignoreListEditText.getText().toString());

                                            rhymeFeatures.performClick();
                                            poemChangeChecker = "";
                                        }
                                    });
                                } else {
                                    openUpgradePopup("Upgrade to Lyric Pro", "These features are very powerful but require Lyric Pro. ", upgradeSpannable, upgradeAmount);
                                }
                            }
                        });

                        createRhymesClick.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (proUser) {
                                    openPopup(createRhymesPopup);
                                    TextView done = (TextView) findViewById(R.id.create_rhymes_okay);
                                    done.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            String createRhymesEditTextString = createRhymesEditText.getText().toString();
                                            createRhymesEditTextString = createRhymesEditTextString.replaceAll("[^a-zA-Z0-9'=,]+", " ").replaceAll(" +", "").toLowerCase();
                                            ArrayList<String> pairs = new ArrayList<>(Arrays.asList(createRhymesEditTextString.split(",")));
                                            createdRhymes.clear();
                                            for (String pair : pairs) {
                                                if (pair.indexOf("=") > 0 && pair.indexOf("=") < pair.length() - 1) {
                                                    if (createdRhymes.containsKey(pair.substring(0, pair.indexOf("="))) && createdRhymes.containsKey(pair.substring(pair.indexOf("=") + 1))) {
                                                        ArrayList<String> first = createdRhymes.get(pair.substring(0, pair.indexOf("=")));
                                                        first.add(pair.substring(pair.indexOf("=") + 1));
                                                        ArrayList<String> second = createdRhymes.get(pair.substring(pair.indexOf("=") + 1));
                                                        second.add(pair.substring(0, pair.indexOf("=")));
                                                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                                                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                                                    } else if (createdRhymes.containsKey(pair.substring(0, pair.indexOf("=")))) {
                                                        ArrayList<String> first = createdRhymes.get(pair.substring(0, pair.indexOf("=")));
                                                        first.add(pair.substring(pair.indexOf("=") + 1));
                                                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                                                        ArrayList<String> second = new ArrayList<>();
                                                        second.add(pair.substring(0, pair.indexOf("=")));
                                                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                                                    } else if (createdRhymes.containsKey(pair.substring(pair.indexOf("=") + 1))) {
                                                        ArrayList<String> second = createdRhymes.get(pair.substring(pair.indexOf("=") + 1));
                                                        second.add(pair.substring(0, pair.indexOf("=")));
                                                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                                                        ArrayList<String> first = new ArrayList<>();
                                                        first.add(pair.substring(pair.indexOf("=") + 1));
                                                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                                                    } else {
                                                        ArrayList<String> first = new ArrayList<>();
                                                        first.add(pair.substring(pair.indexOf("=") + 1));
                                                        ArrayList<String> second = new ArrayList<>();
                                                        second.add(pair.substring(0, pair.indexOf("=")));
                                                        createdRhymes.put(pair.substring(0, pair.indexOf("=")), first);
                                                        createdRhymes.put(pair.substring(pair.indexOf("=") + 1), second);
                                                    }

                                                }
                                            }
                                            createRhymesEditText.setText(createRhymesEditTextString.toLowerCase());
                                            putStringToInternal("lyriccreatedrhymes", createRhymesEditText.getText().toString());

                                            rhymeFeatures.performClick();
                                            poemChangeChecker = "";
                                        }
                                    });
                                } else {
                                    openUpgradePopup("Upgrade to Lyric Pro", "These features are very powerful but require Lyric Pro. ", upgradeSpannable, upgradeAmount);
                                }
                            }
                        });


                        changeClefClick.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (proUser) {
                                    openPopup(changeClefPopup);
                                    TextView done = (TextView) findViewById(R.id.change_clef_done);

                                    final TextView trebleClef = (TextView) findViewById(R.id.treble_clef);
                                    final TextView bassClef = (TextView) findViewById(R.id.bass_clef);
                                    final TextView altoClef = (TextView) findViewById(R.id.alto_clef);
                                    ImageButton trebleClefCheckbox = (ImageButton) findViewById(R.id.checkbox_treble_clef);
                                    ImageButton bassClefCheckbox = (ImageButton) findViewById(R.id.checkbox_bass_clef);
                                    ImageButton altoClefCheckbox = (ImageButton) findViewById(R.id.checkbox_alto_clef);


                                    trebleClef.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            changeClef("treble");
                                        }
                                    });
                                    bassClef.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            changeClef("bass");
                                        }
                                    });
                                    altoClef.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            changeClef("alto");
                                        }
                                    });
                                    trebleClefCheckbox.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            trebleClef.performClick();
                                        }
                                    });
                                    bassClefCheckbox.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            bassClef.performClick();
                                        }
                                    });
                                    altoClefCheckbox.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            altoClef.performClick();
                                        }
                                    });
                                    done.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            rhymeFeatures.performClick();
                                        }
                                    });
                                } else {
                                    openUpgradePopup("Upgrade to Lyric Pro", "These features are very powerful but require Lyric Pro. ", upgradeSpannable, upgradeAmount);
                                }
                            }
                        });
                        changeClefCheckbox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                changeClefClick.performClick();
                            }
                        });
                        ignoreWordsCheckbox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                ignoreWordsClick.performClick();
                            }
                        });
                        createRhymesCheckbox.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                createRhymesClick.performClick();
                            }
                        });
                        TextView done = (TextView) findViewById(R.id.rhyme_features_done);
                        done.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                rhymeFeaturesPopup.setVisibility(View.GONE);
                                popupBackground.setVisibility(View.GONE);
                                poemChangeChecker = "";

                            }
                        });
                    }
                });

            }
        });
        hardSaveIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                hardSave.performClick();
            }
        });

        hardSave.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                hardSave(false);
            }
        });

//        startTrialIcon.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//                startTrial.performClick();
//            }
//        });
//
//        startTrial.setOnClickListener(new View.OnClickListener()
//
//        {
//            public void onClick(View v) {
//                final ConstraintLayout startTrialPopup = (ConstraintLayout) findViewById(R.id.trial_popup);
//                TextView startTrialYes = (TextView) findViewById(R.id.trial_yes);
//                TextView startTrialNo = (TextView) findViewById(R.id.trial_no);
//                openPopup(startTrialPopup);
//                startTrialYes.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        putStringToInternal("lyricoptedfortrial", "true");
//
//                        final Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
//                        startActivity(myIntent);
//                        finish();
//                    }
//                });
//                startTrialNo.setOnClickListener(new View.OnClickListener()
//
//                {
//                    public void onClick(View v) {
//                        startTrialPopup.setVisibility(View.GONE);
//                        popupBackground.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });

        ImageView changeFontIcon = (ImageView) findViewById(R.id.change_font_size_checkbox);
        changeFontIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                changeFont.performClick();
            }
        });


        changeFont.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout fontPopup = (ConstraintLayout) findViewById(R.id.font_popup);
                final ImageButton increase = (ImageButton) findViewById(R.id.popup_increase);
                final ImageButton decrease = (ImageButton) findViewById(R.id.popup_decrease);

                final TextView done = (TextView) findViewById(R.id.popup_done);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fontsize >= (int) defaultFontSize * 2) {
                            increase.setImageResource(R.drawable.plus_disabled);
                        }
                        if (fontsize <= (int) defaultFontSize / 2) {
                            decrease.setImageResource(R.drawable.minus_disabled);
                        }
                        openPopup(fontPopup);

                        increase.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (fontsize < (int) defaultFontSize * 2) {
                                    decrease.setImageResource(R.drawable.minuswhite);
                                    fontsize++;
                                    syllables.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    poem.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, ((fontsize * 25) / 15));
                                    wordInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    notes.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    for (int lineNum : linesWithoutRecordings.keySet()) {
                                        linesWithoutRecordings.get(lineNum).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        int lineHeight = poem.getLineHeight();
                                        linesWithoutRecordings.get(lineNum).setLayoutParams(new LinearLayout.LayoutParams(lineHeight, lineHeight));
                                    }

                                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                    for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                                        if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                            continue;
                                        }
                                        LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                                        i++;
                                        LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                                        EditText col1 = (EditText) row1.getChildAt(0);
                                        EditText col2 = (EditText) row1.getChildAt(1);
                                        EditText col3 = (EditText) row1.getChildAt(2);
                                        EditText col4 = (EditText) row1.getChildAt(3);
                                        TextView sylCol1 = (TextView) row2.getChildAt(0);
                                        TextView sylCol2 = (TextView) row2.getChildAt(1);
                                        TextView sylCol3 = (TextView) row2.getChildAt(2);
                                        TextView sylCol4 = (TextView) row2.getChildAt(3);
                                        col1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    }
                                    putIntToInternal("lyricfontsize", fontsize);

                                    if (fontsize >= (int) defaultFontSize * 2) {
                                        increase.setImageResource(R.drawable.plus_disabled);
                                    }
                                }
                            }
                        });
                        decrease.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (fontsize > (int) defaultFontSize / 2) {
                                    increase.setImageResource(R.drawable.pluswhite);
                                    fontsize--;
                                    syllables.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    poem.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, ((fontsize * 25) / 15));
                                    wordInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    notes.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    for (int lineNum : linesWithoutRecordings.keySet()) {
                                        linesWithoutRecordings.get(lineNum).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        int lineHeight = poem.getLineHeight();
                                        linesWithoutRecordings.get(lineNum).setLayoutParams(new LinearLayout.LayoutParams(lineHeight, lineHeight));
                                    }
                                    final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                    for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                                        if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                            continue;
                                        }
                                        LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                                        i++;
                                        LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                                        EditText col1 = (EditText) row1.getChildAt(0);
                                        EditText col2 = (EditText) row1.getChildAt(1);
                                        EditText col3 = (EditText) row1.getChildAt(2);
                                        EditText col4 = (EditText) row1.getChildAt(3);
                                        TextView sylCol1 = (TextView) row2.getChildAt(0);
                                        TextView sylCol2 = (TextView) row2.getChildAt(1);
                                        TextView sylCol3 = (TextView) row2.getChildAt(2);
                                        TextView sylCol4 = (TextView) row2.getChildAt(3);
                                        col1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                    }
                                    putIntToInternal("lyricfontsize", fontsize);

                                    if (fontsize <= (int) defaultFontSize / 2) {
                                        decrease.setImageResource(R.drawable.minus_disabled);
                                    }
                                }
                            }
                        });
                        done.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                generalSettings.performClick();
                            }
                        });
                    }
                });
            }
        });

        ImageButton recorderIcon = (ImageButton) findViewById(R.id.recorder);

        recorderIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                TextView editRecordingTitle = (TextView) findViewById(R.id.edit_recording_title);
                editRecordingTitle.setText("Edit Recording");
                nextRecordingIsNew = true;
                recordingCopied = false;
                mediaPlayerPosition = 0;
                final ImageButton record = (ImageButton) findViewById(R.id.record);

                final ConstraintLayout recorderPopup = (ConstraintLayout) findViewById(R.id.recorder_popup);
                TextView recorderDone = (TextView) findViewById(R.id.recorder_done);
                ImageButton recorderManage = (ImageButton) findViewById(R.id.recorder_manage_icon);
                final ImageButton recorderNew = (ImageButton) findViewById(R.id.recorder_new_icon);
                //IF NO RECORDINGS, HIDE MANAGE OPTION
                //IF NO RECORDINGS, HIDE MANAGE OPTION
                //IF NO RECORDINGS, HIDE MANAGE OPTION
                //IF NO RECORDINGS, HIDE MANAGE OPTION
                //IF NO RECORDINGS, HIDE MANAGE OPTION
                openPopup(recorderPopup);
                recorderManage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        nextRecordingIsNew = false;
                        final ConstraintLayout recorderManagePopup = (ConstraintLayout) findViewById(R.id.manage_recording_popup);
                        final LinearLayout recorderManageRecordingHolder = (LinearLayout) findViewById(R.id.recorder_recordings);
                        final ScrollView recorderManageRecordingHolderScrollview = (ScrollView) findViewById(R.id.manage_recording_scrollview);
                        final TextView recorderManageDone = (TextView) findViewById(R.id.manage_recording_done);
                        final TextView recorderManageText = (TextView) findViewById(R.id.manage_recording_text);
                        openPopup(recorderManagePopup);
                        Handler handlerBackground = new Handler();
                        handlerBackground.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int lineNum : linesWithoutRecordings.keySet()) {
                                    linesWithoutRecordings.get(lineNum).setText(Integer.toString(lineNum));
                                }
                            }
                        }, 20);
                        if (savedRecordings.size() > 0) {
                            recorderManageText.setVisibility(View.GONE);
                            recorderManageRecordingHolderScrollview.setVisibility(View.VISIBLE);
                            recorderManageRecordingHolder.removeAllViews();
                            SortedSet<String> keys = new TreeSet<>(savedRecordings.keySet());
                            if (savedRecordings.size() <= 2) {
                                recorderManageRecordingHolderScrollview.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                            } else {
                                final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                                int pixels = (int) (150 * scale + 0.5f);
                                recorderManageRecordingHolderScrollview.getLayoutParams().height = pixels;
                            }
                            for (final String savedRecording : keys) {
                                LinearLayout recordingItem = new LinearLayout(MainActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 5, 0, 5);
                                recordingItem.setLayoutParams(params);
                                recordingItem.setOrientation(LinearLayout.VERTICAL);
                                recordingItem.setBackgroundColor(Color.WHITE);
                                LinearLayout secondRow = new LinearLayout(MainActivity.this);
                                TextView recordingTitle = new TextView(MainActivity.this);
                                TextView recordingDuration = new TextView(MainActivity.this);
                                TextView recordingLocation = new TextView(MainActivity.this);
                                recordingTitle.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                secondRow.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                secondRow.setWeightSum(2);
                                secondRow.setOrientation(LinearLayout.HORIZONTAL);
                                recordingDuration.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                recordingLocation.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                recordingDuration.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                recordingLocation.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                                recordingTitle.setMaxLines(1);
                                recordingDuration.setTypeface(typeface);
                                recordingLocation.setTypeface(typeface);
                                recordingTitle.setTypeface(typeface);
                                recordingTitle.setPadding(45, 25, 45, 0);
                                recordingDuration.setPadding(45, 0, 0, 25);
                                recordingLocation.setPadding(0, 0, 45, 25);
                                recordingTitle.setTextColor(Color.BLACK);
                                recordingTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                recordingDuration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                recordingLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                MediaPlayer mp = MediaPlayer.create(MainActivity.this, Uri.parse(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath() + "/" + savedRecording));

                                int durationInMilli = mp.getDuration();
                                String seconds = Integer.toString((int) ((durationInMilli / 1000) % 60));
                                String minutes = Integer.toString((int) ((durationInMilli / (1000 * 60)) % 60));
                                String lineNumber = Integer.toString(savedRecordings.get(savedRecording));
                                if (seconds.length() == 1) {
                                    seconds = "0" + seconds;
                                }
                                if (minutes.length() == 1) {
                                    minutes = "0" + minutes;
                                }
                                String duration = "Duration: " + minutes + ":" + seconds;
                                if (lineNumber.equals("0")) {
                                    lineNumber = "";
                                } else {
                                    lineNumber = "  Location: Line " + lineNumber;
                                }
                                String basicTitle = "";
                                if (savedRecording.contains(".3gp")) {
                                    basicTitle = savedRecording.substring(0, savedRecording.indexOf(".3gp"));
                                } else {
                                    basicTitle = savedRecording;
                                }
                                recordingDuration.setText(duration);
                                recordingLocation.setText(lineNumber);
                                secondRow.addView(recordingDuration);
                                secondRow.addView(recordingLocation);
                                recordingTitle.setText(basicTitle);
                                recordingItem.addView(recordingTitle);
                                recordingItem.addView(secondRow);
                                recorderManageRecordingHolder.addView(recordingItem);

                                recordingItem.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        final ConstraintLayout editRecordingPopup = (ConstraintLayout) findViewById(R.id.edit_recording_popup);
                                        final ImageButton editRecordingPlay = (ImageButton) findViewById(R.id.edit_recording_play_icon);
                                        final ImageButton editRecordingRerecord = (ImageButton) findViewById(R.id.edit_recording_rerecord_icon);
                                        final ImageButton editRecordingDelete = (ImageButton) findViewById(R.id.edit_recording_delete_icon);
                                        final EditText recordingTitle = (EditText) findViewById(R.id.edit_recording_create_title_input);
                                        final EditText recordingInsertAtLine = (EditText) findViewById(R.id.edit_recording_addtoline_input);
                                        final TextView editRecordingSave = (TextView) findViewById(R.id.edit_recording_done);
                                        final ImageButton editRecordingCopy = (ImageButton) findViewById(R.id.edit_recording_copy_icon);
                                        final ImageButton editRecordingDownload = (ImageButton) findViewById(R.id.edit_recording_download_icon);
                                        final TextView editRecordingRerecordLabel = (TextView) findViewById(R.id.edit_recording_rerecord_label);
                                        final TextView editRecordingCopyLabel = (TextView) findViewById(R.id.edit_recording_copy_label);
                                        editRecordingRerecord.setVisibility(View.GONE);
                                        editRecordingCopy.setVisibility(View.VISIBLE);
                                        editRecordingRerecordLabel.setVisibility(View.GONE);
                                        editRecordingCopyLabel.setVisibility(View.VISIBLE);
                                        final SeekBar seekBar = (SeekBar) findViewById(R.id.audioSeekbar);
                                        recordingToEdit = savedRecording;
                                        if (savedRecordings.get(savedRecording) == 0) {
                                            recordingInsertAtLine.setText("");
                                        } else {
                                            recordingInsertAtLine.setText(Integer.toString(savedRecordings.get(savedRecording)));
                                        }
                                        seekbarMovedWhilePlaying = false;
                                        seekBar.setProgress(0);
                                        String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                        path += "/" + recordingToEdit;
                                        MediaPlayer mediaPlayerTemp = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                        final int mediaMax_new = mediaPlayerTemp.getDuration();
                                        String seconds = Integer.toString((int) ((mediaMax_new / 1000) % 60));
                                        String minutes = Integer.toString((int) ((mediaMax_new / (1000 * 60)) % 60));
                                        if (seconds.length() == 1) {
                                            seconds = "0" + seconds;
                                        }
                                        final TextView seekBarTimer = (TextView) findViewById(R.id.audioSeekbarTimer);
                                        seekBarTimer.setText("0:00 / " + minutes + ":" + seconds);
                                        Handler handlerBackground = new Handler();
                                        handlerBackground.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int lineNum : linesWithoutRecordings.keySet()) {
                                                    linesWithoutRecordings.get(lineNum).setText(Integer.toString(lineNum));
                                                }
                                            }
                                        }, 20);

                                        recordingTitle.setText(recordingToEdit.substring(0, recordingToEdit.indexOf(".3gp")));
                                        openPopup(editRecordingPopup);
                                        recordingTitle.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                String str = s.toString();
                                                while (!(str.matches("[a-zA-Z0-9-_ ]*"))) {
                                                    str = removeIllegalChar(str).trim(); //trim whitespaces
                                                    recordingTitle.setText(str);
                                                }
                                                recordingTitle.setSelection(str.length());
                                            }
                                        });


                                        editRecordingPlay.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                path += "/" + recordingToEdit;
                                                if (editRecordingPlaying) {
                                                    editRecordingPlaying = false;
                                                    mediaPlayerEdit.pause();
                                                    mediaPlayerPosition = mediaPlayerEdit.getCurrentPosition();
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    editRecordingPlay.setImageResource(R.drawable.play);
                                                } else {
                                                    if (mediaPlayerPosition > 0) {
                                                        mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                        mediaPlayerEdit.seekTo(mediaPlayerPosition);
                                                        mediaPlayerEdit.start();
                                                    } else {
                                                        mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                        mediaPlayerEdit.start();
                                                    }
                                                    int mediaPos = mediaPlayerEdit.getCurrentPosition();
                                                    int mediaMax = mediaPlayerEdit.getDuration();
                                                    seekBar.setMax(mediaMax); // Set the Maximum range of the
                                                    seekBar.setProgress(mediaPos);// set current progress to song's
                                                    seekBarHandler.removeCallbacks(moveSeekBarThread);
                                                    seekBarHandler.postDelayed(moveSeekBarThread, 100);
                                                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                                                        @Override
                                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                                            seekBar.setMax(mediaMax_new);
                                                            mediaPlayerPosition = seekBar.getProgress();
                                                            if (seekbarMovedWhilePlaying) {
                                                                String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                                path += "/" + recordingToEdit;
                                                                mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                                mediaPlayerEdit.seekTo(mediaPlayerPosition);
                                                                mediaPlayerEdit.start();
                                                                seekbarMovedWhilePlaying = false;
                                                                int mediaPos = mediaPlayerEdit.getCurrentPosition();
                                                                int mediaMax = mediaPlayerEdit.getDuration();
                                                                seekBar.setMax(mediaMax); // Set the Maximum range of the
                                                                seekBar.setProgress(mediaPos);// set current progress to song's
                                                                seekBarHandler.removeCallbacks(moveSeekBarThread);
                                                                seekBarHandler.postDelayed(moveSeekBarThread, 100);
                                                            }
                                                        }

                                                        @Override
                                                        public void onStartTrackingTouch(SeekBar seekBar) {
                                                            try {
                                                                if (mediaPlayerEdit.isPlaying()) {
                                                                    mediaPlayerEdit.stop();
                                                                    mediaPlayerEdit.release();
                                                                    seekbarMovedWhilePlaying = true;
                                                                }
                                                            } catch (IllegalStateException e) {

                                                            }

                                                        }

                                                        @Override
                                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                            // TODO Auto-generated method stub
                                                            int mediaPos_new = seekBar.getProgress();
                                                            int mediaMax_new = seekBar.getMax();
                                                            String currentSeconds = Integer.toString((int) ((mediaPos_new / 1000) % 60));
                                                            String currentMinutes = Integer.toString((int) ((mediaPos_new / (1000 * 60)) % 60));
                                                            if (currentSeconds.length() == 1) {
                                                                currentSeconds = "0" + currentSeconds;
                                                            }
                                                            String seconds = Integer.toString((int) ((mediaMax_new / 1000) % 60));
                                                            String minutes = Integer.toString((int) ((mediaMax_new / (1000 * 60)) % 60));
                                                            if (seconds.length() == 1) {
                                                                seconds = "0" + seconds;
                                                            }
                                                            final TextView seekBarTimer = (TextView) findViewById(R.id.audioSeekbarTimer);
                                                            seekBarTimer.setText(currentMinutes + ":" + currentSeconds + " / " + minutes + ":" + seconds);
                                                        }
                                                    });
                                                    editRecordingPlaying = true;

                                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    editRecordingPlay.setImageResource(R.drawable.pause);
                                                    mediaPlayerEdit.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        public void onCompletion(MediaPlayer mediaPlayerEdit) {
                                                            editRecordingPlaying = false;
                                                            seekBar.setProgress(seekBar.getMax());
                                                            mediaPlayerEdit.release();
                                                            mediaPlayerPosition = 0;
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                            editRecordingPlay.setImageResource(R.drawable.play);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        editRecordingCopy.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                // removed when admob was introduced
                                                //if (proUser || savedRecordings.size() < 4) {
                                                editRecordingCopy.setVisibility(View.GONE);
                                                editRecordingCopyLabel.setVisibility(View.GONE);
                                                TextView editRecordingTitle = (TextView) findViewById(R.id.edit_recording_title);
                                                editRecordingTitle.setText("Create Copy");
                                                recordingTitle.setText("Copy");
                                                recordingInsertAtLine.setText("");
                                                recordingCopied = true;

//                                                } else {
//                                                    openUpgradePopup("Upgrade to Lyric Pro", "Adding more than 4 recordings requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
//
//                                                }
                                            }
                                        });
                                        editRecordingDelete.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                final ConstraintLayout deletePopup = (ConstraintLayout) findViewById(R.id.delete_popup);
                                                openPopup(deletePopup);
                                                TextView deleteYes = (TextView) findViewById(R.id.popup_delete_yes);
                                                TextView deleteNo = (TextView) findViewById(R.id.popup_delete_no);
                                                deleteNo.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        deletePopup.setVisibility(View.GONE);
                                                        popupBackground.setVisibility(View.GONE);
                                                        openPopup(editRecordingPopup);
                                                    }
                                                });
                                                deleteYes.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        if (recordingCopied) {
                                                            editRecordingPopup.setVisibility(View.GONE);
                                                            popupBackground.setVisibility(View.GONE);
                                                            poemChangeChecker = "";
                                                        } else {
                                                            if (editRecordingPlaying) {
                                                                editRecordingPlaying = false;
                                                                mediaPlayerEdit.stop();
                                                                mediaPlayerEdit.release();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                editRecordingPlay.setImageResource(R.drawable.play);
                                                            }
                                                            File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                            File file = new File(dir, recordingToEdit);
                                                            boolean deleted = file.delete();
                                                            for (int lineNum : linesWithoutRecordings.keySet()) {
                                                                linesWithoutRecordings.get(lineNum).setText("");
                                                            }
                                                            if (savedRecordings.containsKey(recordingToEdit)) {
                                                                savedRecordings.remove(recordingToEdit);
                                                                putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                            }
                                                            poemChangeChecker = "";
                                                        }
                                                        deletePopup.setVisibility(View.GONE);
                                                        popupBackground.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });
                                        editRecordingDownload.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {

                                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                    String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                    InputStream in = null;
                                                    OutputStream out = null;
                                                    try {
                                                        in = new FileInputStream(path + "/" + recordingToEdit);
                                                        out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "Lyric_" + title.getText().toString() + "_" + recordingToEdit.substring(0, recordingToEdit.indexOf(".")) + ".mp3");
                                                        byte[] buffer = new byte[1024];
                                                        int read;
                                                        while ((read = in.read(buffer)) != -1) {
                                                            out.write(buffer, 0, read);
                                                        }
                                                        in.close();
                                                        in = null;
                                                        // write the output file (You have now copied the file)
                                                        out.flush();
                                                        out.close();
                                                        out = null;

                                                    } catch (FileNotFoundException fnfe1) {
                                                        Log.e("tag", fnfe1.getMessage());
                                                    } catch (Exception e) {
                                                        Log.e("tag", e.getMessage());
                                                    }
                                                    Toast.makeText(MainActivity.this,
                                                            "Saved to Downloads folder", Toast.LENGTH_LONG).show();
                                                } else {
                                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                                    Toast.makeText(MainActivity.this,
                                                            "Requires Permission", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                        editRecordingSave.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {


                                                if (recordingTitle.getText().toString().equals("")) {
                                                    Toast toast = Toast.makeText(mainActivityHelper, "Title cannot be blank",
                                                            Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                } else if (recordingToEdit.equals(recordingTitle.getText().toString() + ".3gp") && !recordingCopied) {
                                                    if (editRecordingPlaying) {
                                                        editRecordingPlaying = false;
                                                        mediaPlayerEdit.stop();
                                                        mediaPlayerEdit.release();
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                        editRecordingPlay.setImageResource(R.drawable.play);
                                                    }
                                                    File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                    File file = new File(dir, recordingToEdit);
                                                    file.renameTo(new File(new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath()), recordingTitle.getText().toString() + ".3gp"));
                                                    for (int lineNum : linesWithoutRecordings.keySet()) {
                                                        linesWithoutRecordings.get(lineNum).setText("");
                                                    }
                                                    savedRecordings.remove(recordingToEdit);
                                                    editRecordingPopup.setVisibility(View.GONE);
                                                    popupBackground.setVisibility(View.GONE);
                                                    if (recordingInsertAtLine.getText().toString().equals("")) {
                                                        savedRecordings.put(recordingTitle.getText().toString() + ".3gp", 0);
                                                        putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                        poemChangeChecker = "";
                                                    } else {
                                                        for (String savedRecording : savedRecordings.keySet()) {
                                                            if (savedRecordings.get(savedRecording) == Integer.parseInt(recordingInsertAtLine.getText().toString())) {
                                                                savedRecordings.put(savedRecording, 0);

                                                            }
                                                        }
                                                        savedRecordings.put(recordingTitle.getText().toString() + ".3gp", Integer.parseInt(recordingInsertAtLine.getText().toString()));
                                                        putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                        poemChangeChecker = "";
                                                    }
                                                } else if (savedRecordings.keySet().contains(recordingTitle.getText().toString() + ".3gp")) {
                                                    Toast toast = Toast.makeText(mainActivityHelper, "Title already in use",
                                                            Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                } else {
                                                    if (editRecordingPlaying) {
                                                        editRecordingPlaying = false;
                                                        mediaPlayerEdit.stop();
                                                        mediaPlayerEdit.release();
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                        editRecordingPlay.setImageResource(R.drawable.play);
                                                    }

                                                    for (int lineNum : linesWithoutRecordings.keySet()) {
                                                        linesWithoutRecordings.get(lineNum).setText("");
                                                    }
                                                    if (recordingCopied) {

                                                        String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();

                                                        InputStream in = null;
                                                        OutputStream out = null;
                                                        try {
                                                            in = new FileInputStream(path + "/" + recordingToEdit);
                                                            out = new FileOutputStream(path + "/" + recordingTitle.getText().toString() + ".3gp");
                                                            byte[] buffer = new byte[1024];
                                                            int read;
                                                            while ((read = in.read(buffer)) != -1) {
                                                                out.write(buffer, 0, read);
                                                            }
                                                            in.close();
                                                            in = null;
                                                            // write the output file (You have now copied the file)
                                                            out.flush();
                                                            out.close();
                                                            out = null;

                                                        } catch (FileNotFoundException fnfe1) {
                                                            Log.e("tag", fnfe1.getMessage());
                                                        } catch (Exception e) {
                                                            Log.e("tag", e.getMessage());
                                                        }
                                                    } else {
                                                        File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                        File file = new File(dir, recordingToEdit);
                                                        file.renameTo(new File(new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath()), recordingTitle.getText().toString() + ".3gp"));
                                                        savedRecordings.remove(recordingToEdit);
                                                    }
                                                    editRecordingPopup.setVisibility(View.GONE);
                                                    popupBackground.setVisibility(View.GONE);
                                                    if (recordingInsertAtLine.getText().toString().equals("")) {
                                                        savedRecordings.put(recordingTitle.getText().toString() + ".3gp", 0);
                                                        putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                        poemChangeChecker = "";
                                                    } else {
                                                        for (String savedRecording : savedRecordings.keySet()) {
                                                            if (savedRecordings.get(savedRecording) == Integer.parseInt(recordingInsertAtLine.getText().toString())) {
                                                                savedRecordings.put(savedRecording, 0);

                                                            }
                                                        }
                                                        savedRecordings.put(recordingTitle.getText().toString() + ".3gp", Integer.parseInt(recordingInsertAtLine.getText().toString()));
                                                        putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                        poemChangeChecker = "";
                                                    }

                                                }
                                            }
                                        });

                                    }
                                });
                            }
                        } else {
                            recorderManageText.setVisibility(View.VISIBLE);
                            recorderManageRecordingHolderScrollview.setVisibility(View.GONE);
                        }
                        recorderManageDone.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                for (int lineNum : linesWithoutRecordings.keySet()) {
                                    linesWithoutRecordings.get(lineNum).setText("");
                                }
                                recorderManagePopup.setVisibility(View.GONE);
                                popupBackground.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                recorderNew.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // removed when admob was introduced
//                        if (proUser || savedRecordings.size() < 4 || !nextRecordingIsNew) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                            Toast.makeText(MainActivity.this,
                                    "Requires Microphone Permission", Toast.LENGTH_LONG).show();
                        } else {

                            final ConstraintLayout newRecordingPopup = (ConstraintLayout) findViewById(R.id.new_recording_popup);
                            final TextView newRecordingOkay = (TextView) findViewById(R.id.new_recording_okay);

                            newRecordingOkay.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    newRecordingPopup.setVisibility(View.GONE);
                                    popupBackground.setVisibility(View.GONE);
                                    if (colorTheme.equals("royal")) {
                                        record.setImageResource(R.drawable.record_purple);
                                    } else if (colorTheme.equals("sunset")) {
                                        record.setImageResource(R.drawable.record_orange);
                                    } else if (colorTheme.equals("joy")) {
                                        record.setImageResource(R.drawable.record_blue);
                                    } else if (colorTheme.equals("dark")) {
                                        record.setImageResource(R.drawable.record_black);
                                    }
                                    record.setVisibility(View.VISIBLE);
                                    final EditText recordingTitle = (EditText) findViewById(R.id.edit_recording_create_title_input);
                                    final EditText recordingInsertAtLine = (EditText) findViewById(R.id.edit_recording_addtoline_input);

                                    if (nextRecordingIsNew) {
                                        recordingToEdit = "Untitled.3gp";
                                        recordingInsertAtLine.setText("");
                                        recordingTitle.setText("");
                                    } else {
                                        recordingTitle.setText(recordingToEdit.substring(0, recordingToEdit.indexOf(".3gp")));
                                        if (savedRecordings.containsKey(recordingToEdit)) {
                                            if (savedRecordings.get(recordingToEdit) != 0) {
                                                recordingInsertAtLine.setText(Integer.toString(savedRecordings.get(recordingToEdit)));
                                            }
                                        }
                                    }
                                    record.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            record.bringToFront();
                                            if (recorderRunning) {
                                                recorderRunning = false;
                                                if (colorTheme.equals("royal")) {
                                                    record.setImageResource(R.drawable.record_purple);
                                                } else if (colorTheme.equals("sunset")) {
                                                    record.setImageResource(R.drawable.record_orange);
                                                } else if (colorTheme.equals("joy")) {
                                                    record.setImageResource(R.drawable.record_blue);
                                                } else if (colorTheme.equals("dark")) {
                                                    record.setImageResource(R.drawable.record_black);
                                                }
                                                record.setVisibility(View.INVISIBLE);
                                                try {
                                                    mediaRecorder.stop();
                                                    mediaRecorder.release();
                                                } catch (RuntimeException e) {
                                                }
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                                final ConstraintLayout editRecordingPopup = (ConstraintLayout) findViewById(R.id.edit_recording_popup);
                                                final ImageButton editRecordingPlay = (ImageButton) findViewById(R.id.edit_recording_play_icon);
                                                final ImageButton editRecordingRerecord = (ImageButton) findViewById(R.id.edit_recording_rerecord_icon);
                                                final ImageButton editRecordingDelete = (ImageButton) findViewById(R.id.edit_recording_delete_icon);
                                                final EditText recordingTitle = (EditText) findViewById(R.id.edit_recording_create_title_input);
                                                final TextView editRecordingSave = (TextView) findViewById(R.id.edit_recording_done);
                                                final ImageButton editRecordingCopy = (ImageButton) findViewById(R.id.edit_recording_copy_icon);
                                                final ImageButton editRecordingDownload = (ImageButton) findViewById(R.id.edit_recording_download_icon);
                                                final TextView editRecordingRerecordLabel = (TextView) findViewById(R.id.edit_recording_rerecord_label);
                                                final TextView editRecordingCopyLabel = (TextView) findViewById(R.id.edit_recording_copy_label);
                                                editRecordingRerecord.setVisibility(View.VISIBLE);
                                                editRecordingCopy.setVisibility(View.GONE);
                                                editRecordingRerecordLabel.setVisibility(View.VISIBLE);
                                                editRecordingCopyLabel.setVisibility(View.GONE);
                                                final SeekBar seekBar = (SeekBar) findViewById(R.id.audioSeekbar);
                                                seekbarMovedWhilePlaying = false;
                                                seekBar.setProgress(0);
                                                String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                path += "/" + recordingToEdit;
                                                final MediaPlayer mediaPlayerTemp = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                final int mediaMax_new = mediaPlayerTemp.getDuration();
                                                String seconds = Integer.toString((int) ((mediaMax_new / 1000) % 60));
                                                String minutes = Integer.toString((int) ((mediaMax_new / (1000 * 60)) % 60));
                                                if (seconds.length() == 1) {
                                                    seconds = "0" + seconds;
                                                }
                                                final TextView seekBarTimer = (TextView) findViewById(R.id.audioSeekbarTimer);
                                                seekBarTimer.setText("0:00 / " + minutes + ":" + seconds);
                                                Handler handlerBackground = new Handler();
                                                handlerBackground.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int lineNum : linesWithoutRecordings.keySet()) {
                                                            linesWithoutRecordings.get(lineNum).setText(Integer.toString(lineNum));
                                                        }
                                                    }
                                                }, 20);

                                                openPopup(editRecordingPopup);
                                                recordingTitle.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                    }

                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    }

                                                    @Override
                                                    public void afterTextChanged(Editable s) {
                                                        String str = s.toString();
                                                        while (!(str.matches("[a-zA-Z0-9-_ ]*"))) {
                                                            str = removeIllegalChar(str).trim(); //trim whitespaces
                                                            recordingTitle.setText(str);
                                                        }
                                                        recordingTitle.setSelection(str.length());  //use only if u want to set cursor to end
                                                    }
                                                });


                                                editRecordingPlay.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                        path += "/" + recordingToEdit;
                                                        if (editRecordingPlaying) {
                                                            editRecordingPlaying = false;
                                                            mediaPlayerEdit.pause();
                                                            mediaPlayerPosition = mediaPlayerEdit.getCurrentPosition();
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                            editRecordingPlay.setImageResource(R.drawable.play);
                                                        } else {
                                                            if (mediaPlayerPosition > 0) {
                                                                mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                                mediaPlayerEdit.seekTo(mediaPlayerPosition);
                                                                mediaPlayerEdit.start();
                                                            } else {
                                                                mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                                mediaPlayerEdit.start();
                                                            }
                                                            int mediaPos = mediaPlayerEdit.getCurrentPosition();
                                                            int mediaMax = mediaPlayerEdit.getDuration();
                                                            seekBar.setMax(mediaMax); // Set the Maximum range of the
                                                            seekBar.setProgress(mediaPos);// set current progress to song's
                                                            seekBarHandler.removeCallbacks(moveSeekBarThread);
                                                            seekBarHandler.postDelayed(moveSeekBarThread, 100);
                                                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                                                                @Override
                                                                public void onStopTrackingTouch(SeekBar seekBar) {

                                                                    seekBar.setMax(mediaMax_new);
                                                                    mediaPlayerPosition = seekBar.getProgress();
                                                                    if (seekbarMovedWhilePlaying) {
                                                                        String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                                        path += "/" + recordingToEdit;
                                                                        mediaPlayerEdit = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                                                        mediaPlayerEdit.seekTo(mediaPlayerPosition);
                                                                        mediaPlayerEdit.start();
                                                                        seekbarMovedWhilePlaying = false;
                                                                        int mediaPos = mediaPlayerEdit.getCurrentPosition();
                                                                        int mediaMax = mediaPlayerEdit.getDuration();
                                                                        seekBar.setMax(mediaMax); // Set the Maximum range of the
                                                                        seekBar.setProgress(mediaPos);// set current progress to song's
                                                                        seekBarHandler.removeCallbacks(moveSeekBarThread);
                                                                        seekBarHandler.postDelayed(moveSeekBarThread, 100);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onStartTrackingTouch(SeekBar seekBar) {
                                                                    try {
                                                                        if (mediaPlayerEdit.isPlaying()) {
                                                                            mediaPlayerEdit.stop();
                                                                            mediaPlayerEdit.release();
                                                                            seekbarMovedWhilePlaying = true;
                                                                        }
                                                                    } catch (IllegalStateException e) {

                                                                    }

                                                                }

                                                                @Override
                                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                                    // TODO Auto-generated method stub
                                                                    int mediaPos_new = seekBar.getProgress();
                                                                    int mediaMax_new = seekBar.getMax();
                                                                    String currentSeconds = Integer.toString((int) ((mediaPos_new / 1000) % 60));
                                                                    String currentMinutes = Integer.toString((int) ((mediaPos_new / (1000 * 60)) % 60));
                                                                    if (currentSeconds.length() == 1) {
                                                                        currentSeconds = "0" + currentSeconds;
                                                                    }
                                                                    String seconds = Integer.toString((int) ((mediaMax_new / 1000) % 60));
                                                                    String minutes = Integer.toString((int) ((mediaMax_new / (1000 * 60)) % 60));
                                                                    if (seconds.length() == 1) {
                                                                        seconds = "0" + seconds;
                                                                    }
                                                                    final TextView seekBarTimer = (TextView) findViewById(R.id.audioSeekbarTimer);
                                                                    seekBarTimer.setText(currentMinutes + ":" + currentSeconds + " / " + minutes + ":" + seconds);
                                                                }
                                                            });
                                                            editRecordingPlaying = true;

                                                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                            editRecordingPlay.setImageResource(R.drawable.pause);
                                                            mediaPlayerEdit.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                public void onCompletion(MediaPlayer mediaPlayerEdit) {
                                                                    editRecordingPlaying = false;
                                                                    mediaPlayerEdit.release();
                                                                    mediaPlayerPosition = 0;
                                                                    seekBar.setProgress(seekBar.getMax());
                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                    editRecordingPlay.setImageResource(R.drawable.play);
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                editRecordingRerecord.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                                                            Toast.makeText(MainActivity.this,
                                                                    "Requires Microphone Permission", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            mediaPlayerPosition = 0;
                                                            if (editRecordingPlaying) {
                                                                editRecordingPlaying = false;
                                                                mediaPlayerEdit.stop();
                                                                mediaPlayerEdit.release();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                editRecordingPlay.setImageResource(R.drawable.play);
                                                            }
                                                            recorderNew.performClick();

                                                        }

                                                    }
                                                });
                                                editRecordingDelete.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        final ConstraintLayout deletePopup = (ConstraintLayout) findViewById(R.id.delete_popup);
                                                        openPopup(deletePopup);
                                                        TextView deleteYes = (TextView) findViewById(R.id.popup_delete_yes);
                                                        TextView deleteNo = (TextView) findViewById(R.id.popup_delete_no);
                                                        deleteNo.setOnClickListener(new View.OnClickListener() {
                                                            public void onClick(View v) {
                                                                deletePopup.setVisibility(View.GONE);
                                                                popupBackground.setVisibility(View.GONE);
                                                                openPopup(editRecordingPopup);
                                                            }
                                                        });
                                                        deleteYes.setOnClickListener(new View.OnClickListener() {
                                                            public void onClick(View v) {
                                                                if (editRecordingPlaying) {
                                                                    editRecordingPlaying = false;
                                                                    mediaPlayerEdit.stop();
                                                                    mediaPlayerEdit.release();
                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                    editRecordingPlay.setImageResource(R.drawable.play);
                                                                }
                                                                File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                                File file = new File(dir, recordingToEdit);
                                                                boolean deleted = file.delete();
                                                                for (int lineNum : linesWithoutRecordings.keySet()) {
                                                                    linesWithoutRecordings.get(lineNum).setText("");
                                                                }
                                                                if (savedRecordings.containsKey(recordingToEdit)) {
                                                                    savedRecordings.remove(recordingToEdit);
                                                                    putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                                }
                                                                poemChangeChecker = "";
                                                                deletePopup.setVisibility(View.GONE);
                                                                popupBackground.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }
                                                });
                                                editRecordingDownload.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {

                                                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                            String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                                            InputStream in = null;
                                                            OutputStream out = null;
                                                            try {
                                                                in = new FileInputStream(path + "/" + recordingToEdit);
                                                                out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "Lyric_" + title.getText().toString() + "_" + recordingToEdit.substring(0, recordingToEdit.indexOf(".")) + ".mp3");
                                                                byte[] buffer = new byte[1024];
                                                                int read;
                                                                while ((read = in.read(buffer)) != -1) {
                                                                    out.write(buffer, 0, read);
                                                                }
                                                                in.close();
                                                                in = null;
                                                                // write the output file (You have now copied the file)
                                                                out.flush();
                                                                out.close();
                                                                out = null;

                                                            } catch (FileNotFoundException fnfe1) {
                                                                Log.e("tag", fnfe1.getMessage());
                                                            } catch (Exception e) {
                                                                Log.e("tag", e.getMessage());
                                                            }
                                                            Toast.makeText(MainActivity.this,
                                                                    "Saved to Downloads folder", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                                            Toast.makeText(MainActivity.this,
                                                                    "Requires Permission", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                                editRecordingSave.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {

                                                        if (recordingTitle.getText().toString().equals("")) {

                                                            Toast toast = Toast.makeText(mainActivityHelper, "Title cannot be blank",
                                                                    Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                        } else if (recordingToEdit.equals(recordingTitle.getText().toString() + ".3gp")) {

                                                            if (editRecordingPlaying) {
                                                                editRecordingPlaying = false;
                                                                mediaPlayerEdit.stop();
                                                                mediaPlayerEdit.release();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                editRecordingPlay.setImageResource(R.drawable.play);
                                                            }
                                                            File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                            File file = new File(dir, recordingToEdit);
                                                            file.renameTo(new File(new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath()), recordingTitle.getText().toString() + ".3gp"));
                                                            for (int lineNum : linesWithoutRecordings.keySet()) {
                                                                linesWithoutRecordings.get(lineNum).setText("");
                                                            }
                                                            savedRecordings.remove(recordingToEdit);
                                                            editRecordingPopup.setVisibility(View.GONE);
                                                            popupBackground.setVisibility(View.GONE);
                                                            if (recordingInsertAtLine.getText().toString().equals("")) {
                                                                savedRecordings.put(recordingTitle.getText().toString() + ".3gp", 0);
                                                                putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                                poemChangeChecker = "";
                                                            } else {
                                                                for (String savedRecording : savedRecordings.keySet()) {
                                                                    if (savedRecordings.get(savedRecording) == Integer.parseInt(recordingInsertAtLine.getText().toString())) {
                                                                        savedRecordings.put(savedRecording, 0);

                                                                    }
                                                                }
                                                                savedRecordings.put(recordingTitle.getText().toString() + ".3gp", Integer.parseInt(recordingInsertAtLine.getText().toString()));
                                                                putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                                poemChangeChecker = "";
                                                            }

                                                        } else if (savedRecordings.keySet().contains(recordingTitle.getText().toString() + ".3gp")) {

                                                            Toast toast = Toast.makeText(mainActivityHelper, "Title already in use",
                                                                    Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                                            toast.show();
                                                        } else {

                                                            if (editRecordingPlaying) {
                                                                editRecordingPlaying = false;
                                                                mediaPlayerEdit.stop();
                                                                mediaPlayerEdit.release();
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                                editRecordingPlay.setImageResource(R.drawable.play);
                                                            }
                                                            File dir = new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath());
                                                            File file = new File(dir, recordingToEdit);
                                                            file.renameTo(new File(new File(getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath()), recordingTitle.getText().toString() + ".3gp"));
                                                            for (int lineNum : linesWithoutRecordings.keySet()) {
                                                                linesWithoutRecordings.get(lineNum).setText("");
                                                            }
                                                            savedRecordings.remove(recordingToEdit);
                                                            editRecordingPopup.setVisibility(View.GONE);
                                                            popupBackground.setVisibility(View.GONE);
                                                            if (recordingInsertAtLine.getText().toString().equals("")) {
                                                                savedRecordings.put(recordingTitle.getText().toString() + ".3gp", 0);
                                                                putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                                poemChangeChecker = "";
                                                            } else {
                                                                for (String savedRecording : savedRecordings.keySet()) {
                                                                    if (savedRecordings.get(savedRecording) == Integer.parseInt(recordingInsertAtLine.getText().toString())) {
                                                                        savedRecordings.put(savedRecording, 0);

                                                                    }
                                                                }
                                                                savedRecordings.put(recordingTitle.getText().toString() + ".3gp", Integer.parseInt(recordingInsertAtLine.getText().toString()));
                                                                putStringToInternal("lyricsavedrecordings" + Integer.toString(lyricIndex), savedRecordings.toString());

                                                                poemChangeChecker = "";
                                                            }

                                                        }
                                                    }
                                                });
                                            } else {

                                                if (sampleRate > 0) {

                                                    String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();

                                                    path += "/" + recordingToEdit;
                                                    mediaRecorder = new MediaRecorder();
                                                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                                                    mediaRecorder.setAudioSamplingRate(sampleRate);
                                                    mediaRecorder.setAudioEncodingBitRate(384000);
                                                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                                                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//                                                        // older version of Android, use crappy sounding voice codec
////                                                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                                                        mediaRecorder.setAudioSamplingRate(8000);
//                                                        mediaRecorder.setAudioEncodingBitRate(12200);
//                                                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                                                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                                    mediaRecorder.setOutputFile(path);
                                                    try {
                                                        mediaRecorder.prepare();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        mediaRecorder.start();
                                                    } catch (RuntimeException e) {
                                                        if (sampleRate == 48000) {
                                                            sampleRate = 44100;
                                                        } else if (sampleRate == 44100) {
                                                            sampleRate = 22050;
                                                        } else if (sampleRate == 22050) {
                                                            sampleRate = 16000;
                                                        } else if (sampleRate == 16000) {
                                                            sampleRate = 11025;
                                                        } else if (sampleRate == 11025) {
                                                            sampleRate = 8000;
                                                        } else if (sampleRate == 8000) {
                                                            sampleRate = 0;
                                                        }
                                                        Handler handlerBackground = new Handler();
                                                        handlerBackground.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                poem.performClick();
                                                            }
                                                        }, 100);
                                                        return;
                                                    }
                                                    recorderRunning = true;
                                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    if (colorTheme.equals("royal")) {
                                                        record.setImageResource(R.drawable.record_stop_purple);
                                                    } else if (colorTheme.equals("sunset")) {
                                                        record.setImageResource(R.drawable.record_stop_orange);
                                                    } else if (colorTheme.equals("joy")) {
                                                        record.setImageResource(R.drawable.record_stop_blue);
                                                    } else if (colorTheme.equals("dark")) {
                                                        record.setImageResource(R.drawable.record_stop_black);
                                                    }
                                                } else {
                                                    recorderRunning = false;
                                                    if (colorTheme.equals("royal")) {
                                                        record.setImageResource(R.drawable.record_purple);
                                                    } else if (colorTheme.equals("sunset")) {
                                                        record.setImageResource(R.drawable.record_orange);
                                                    } else if (colorTheme.equals("joy")) {
                                                        record.setImageResource(R.drawable.record_blue);
                                                    } else if (colorTheme.equals("dark")) {
                                                        record.setImageResource(R.drawable.record_black);
                                                    }
                                                    record.setVisibility(View.INVISIBLE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    Toast.makeText(mainActivityHelper, "Recording not supported",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                            final TextView doNotShowAgainLabel = (TextView) findViewById(R.id.recording_warning_hide_label);
                            final ImageButton doNotShowAgainImage = (ImageButton) findViewById(R.id.recording_warning_hide_image);
                            doNotShowAgainLabel.setOnClickListener(new View.OnClickListener()

                            {
                                public void onClick(View v) {
                                    if (!newRecordingCheckbox) {
                                        newRecordingCheckbox = true;
                                        doNotShowAgainImage.setImageResource(R.drawable.small_check);
                                    } else {
                                        newRecordingCheckbox = false;
                                        doNotShowAgainImage.setImageResource(R.drawable.square);
                                    }
                                }
                            });
                            doNotShowAgainImage.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    doNotShowAgainLabel.performClick();
                                }
                            });
                            if (newRecordingCheckbox == true) {
                                putStringToInternal("lyricrecordinghidepopup", "true");

                            }
                            String doNotShowAgain = getStringFromInternal("lyricrecordinghidepopup", "false");
                            if (doNotShowAgain.equals("true")) {
                                newRecordingOkay.performClick();
                                openPopup(null);
                            } else {
                                openPopup(newRecordingPopup);
                            }

                        }
//                        } else {
//                            openUpgradePopup("Upgrade to Lyric Pro", "Adding more than 4 recordings requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
//                        }
                    }
                });
                recorderDone.setOnClickListener(new View.OnClickListener()

                {
                    public void onClick(View v) {
                        recorderPopup.setVisibility(View.GONE);
                        popupBackground.setVisibility(View.GONE);
                    }
                });
            }
        });
        metronomeIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                if (audioFileRunning) {
                    audioFileRunning = false;
                    userFileMediaPlayer.release();
                } else if (metronomeRunning) {
                    metronomeRunning = false;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    metronomeIcon.getRootView().setBackgroundColor(rgb(255, 255, 255));
                } else {
                    final ConstraintLayout metronomePopup = (ConstraintLayout) findViewById(R.id.metronome_popup);
                    final TextView done = (TextView) findViewById(R.id.metronome_done);
                    final TextView no = (TextView) findViewById(R.id.metronome_no);
                    final TextView metronomePlayFileLabel = (TextView) findViewById(R.id.metronome_play_file);
                    final ImageButton metronomePlayFileImage = (ImageButton) findViewById(R.id.metronome_play_file_image);

                    final TextView metronomeBackgroundLabel = (TextView) findViewById(R.id.metronome_background_label);
                    final ImageButton metronomeBackgroundImage = (ImageButton) findViewById(R.id.metronome_background_image);
                    final TextView metronomeMeasureLabel = (TextView) findViewById(R.id.metronome_measure_label);
                    final TextView metronomeNoteLabel = (TextView) findViewById(R.id.metronome_notes_label);
                    final TextView metronomeScrollLabel = (TextView) findViewById(R.id.metronome_scroll_label);
                    final ImageButton metronomeMeasureImage = (ImageButton) findViewById(R.id.metronome_measure_image);
                    final ImageButton metronomeNoteImage = (ImageButton) findViewById(R.id.metronome_notes_image);
                    final ImageButton metronomeScrollImage = (ImageButton) findViewById(R.id.metronome_scroll_image);
                    final LinearLayout metronomeMeasureOptions = (LinearLayout) findViewById(R.id.metronome_options);
                    final TextView metronomeMeasureText = (TextView) findViewById(R.id.metronome_measure_text);
                    metronomeInput.setText(Integer.toString(getIntFromInternal("lyricmetronomebpm" + lyricIndex, 80)));
                    metronomeAccentInput.setText(Integer.toString(getIntFromInternal("lyricmetronomeaccent" + lyricIndex, 4)));

                    metronomePlayFileLabel.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (proUser) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, 10);
                                } else {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                    Toast.makeText(MainActivity.this,
                                            "Requires Permission", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                openUpgradePopup("Upgrade to Lyric Pro", "Playing audio files requires Lyric Pro. ", upgradeSpannable, upgradeAmount);
                            }

                        }
                    });

                    metronomePlayFileImage.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            metronomePlayFileLabel.performClick();
                        }
                    });

                    metronomeBackgroundImage.setOnClickListener(new View.OnClickListener()

                    {
                        public void onClick(View v) {
                            metronomeBackgroundLabel.performClick();
                        }
                    });
                    metronomeBackgroundLabel.setOnClickListener(new View.OnClickListener()

                    {
                        public void onClick(View v) {
                            if (backgroundMetronome) {
                                backgroundMetronome = false;
                                metronomeBackgroundImage.setImageResource(R.drawable.circle);
                            } else {
                                backgroundMetronome = true;
                                measureMetronome = false;
                                metronomeBackgroundImage.setImageResource(R.drawable.small_check);
                                metronomeMeasureImage.setImageResource(R.drawable.circle);

                            }
                        }
                    });
                    LinearLayout poemAndSyllables = (LinearLayout) findViewById(R.id.poemandsyllables);
                    if (!poemAndSyllables.isShown()) {
                        metronomeMeasureImage.setVisibility(View.VISIBLE);
                        metronomeMeasureLabel.setVisibility(View.VISIBLE);
                        if (allBars.size() > 0) {
                            metronomeNoteImage.setVisibility(View.VISIBLE);
                            metronomeNoteLabel.setVisibility(View.VISIBLE);
                        } else {
                            metronomeNoteImage.setVisibility(View.GONE);
                            metronomeNoteLabel.setVisibility(View.GONE);
                            noteMetronome = false;
                        }
                        metronomeScrollImage.setVisibility(View.VISIBLE);
                        metronomeScrollLabel.setVisibility(View.VISIBLE);
                        metronomeMeasureText.setVisibility(View.GONE);
                        metronomeMeasureOptions.setWeightSum(5);
                        if (measureMetronome) {
                            metronomeMeasureImage.setImageResource(R.drawable.small_check);
                        } else {
                            metronomeMeasureImage.setImageResource(R.drawable.circle);
                        }
                        metronomeMeasureImage.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                metronomeMeasureLabel.performClick();
                            }
                        });
                        metronomeMeasureLabel.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                if (measureMetronome) {
                                    measureMetronome = false;
                                    metronomeMeasureImage.setImageResource(R.drawable.circle);
                                } else {
                                    backgroundMetronome = false;
                                    measureMetronome = true;
                                    metronomeBackgroundImage.setImageResource(R.drawable.circle);
                                    metronomeMeasureImage.setImageResource(R.drawable.small_check);
                                }
                            }
                        });
                        metronomeScrollImage.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                metronomeScrollLabel.performClick();
                            }
                        });
                        metronomeScrollLabel.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                if (scrollMetronome) {
                                    scrollMetronome = false;
                                    metronomeScrollImage.setImageResource(R.drawable.square);
                                } else {
                                    scrollMetronome = true;
                                    metronomeScrollImage.setImageResource(R.drawable.small_check);
                                }
                            }
                        });
                        metronomeNoteImage.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                metronomeNoteLabel.performClick();
                            }
                        });
                        metronomeNoteLabel.setOnClickListener(new View.OnClickListener()

                        {
                            public void onClick(View v) {
                                if (noteMetronome) {
                                    noteMetronome = false;
                                    metronomeNoteImage.setImageResource(R.drawable.square);
                                } else {
                                    noteMetronome = true;
                                    metronomeNoteImage.setImageResource(R.drawable.small_check);
                                }
                            }
                        });
                    } else {
                        scrollMetronome = false;
                        metronomeScrollImage.setImageResource(R.drawable.square);
                        noteMetronome = false;
                        metronomeNoteImage.setImageResource(R.drawable.square);
                        metronomeMeasureImage.setVisibility(View.GONE);
                        metronomeMeasureLabel.setVisibility(View.GONE);
                        metronomeNoteImage.setVisibility(View.GONE);
                        metronomeNoteLabel.setVisibility(View.GONE);
                        metronomeScrollImage.setVisibility(View.GONE);
                        metronomeScrollLabel.setVisibility(View.GONE);
                        metronomeMeasureText.setVisibility(View.VISIBLE);
                        metronomeMeasureOptions.setWeightSum(2);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openPopup(metronomePopup);


                            done.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    if (metronomeInput.getText().toString().equals("")) {
                                        Toast.makeText(mainActivityHelper, "Enter BPM",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (metronomeAccentInput.getText().toString().equals("")) {
                                            Toast.makeText(mainActivityHelper, "Enter accent",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Integer bpm = Integer.parseInt(metronomeInput.getText().toString());
                                            Integer accent = Integer.parseInt(metronomeAccentInput.getText().toString());
                                            putIntToInternal("lyricmetronomebpm" + lyricIndex, bpm);
                                            putIntToInternal("lyricmetronomeaccent" + lyricIndex, accent);

                                            if (accent == 0) {
                                                accent = 1;
                                            }
                                            startMetronome(bpm, accent);
                                            metronomePopup.setVisibility(View.GONE);
                                            popupBackground.setVisibility(View.GONE);
                                            try {
                                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    metronomePopup.setVisibility(View.GONE);
                                    popupBackground.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }

            }
        });


        ImageView generalSettingsIcon = (ImageView) findViewById(R.id.change_font_size_checkbox);
        generalSettingsIcon.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                generalSettings.performClick();
            }
        });

        generalSettings.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final ConstraintLayout generalSettingsPopup = (ConstraintLayout) findViewById(R.id.general_settings_popup);

                final TextView done = (TextView) findViewById(R.id.general_settings_done);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openPopup(generalSettingsPopup);

                        done.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                generalSettingsPopup.setVisibility(View.GONE);
                                popupBackground.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });

        changeMode.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                final Toolbar spinnerToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
                final Toolbar measureToolbar = (Toolbar) findViewById(R.id.measure_toolbar);
                final Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                final ImageButton metronomeIcon = (ImageButton) findViewById(R.id.metronome);

                final ConstraintLayout changeModePopup = (ConstraintLayout) findViewById(R.id.changemode_popup);

                final TextView yes = (TextView) findViewById(R.id.popup_yes_changemode);
                final TextView no = (TextView) findViewById(R.id.popup_no_changemode);

                final TextView changeModeOption = (TextView) findViewById(R.id.measure_copy_label);
                final ImageButton changeModeImage = (ImageButton) findViewById(R.id.measure_copy_image);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView changeModeText = (TextView) findViewById(R.id.changemode_text);
                        TextView changeModeTitle = (TextView) findViewById(R.id.changemode_title);
                        LinearLayout poemAndSyllables = (LinearLayout) findViewById(R.id.poemandsyllables);
                        if (poemAndSyllables.isShown()) {
                            changeModeTitle.setText("Enter Measure Mode");
                            changeModeText.setText("Would you like to enter Measure Mode?");
                        } else {
                            changeModeTitle.setText("Exit Measure Mode");
                            changeModeText.setText("Would you like to exit Measure Mode?");
                        }

                        changeModeOption.setVisibility(View.GONE);
                        changeModeImage.setVisibility(View.GONE);

                        openPopup(changeModePopup);

                        if (!poemAndSyllables.isShown()) {

                            final String measureText = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", null);
                            if (measureText != null && !measureText.equals("")) {
                                String tester = measureText.replaceAll("<measure>\n", "").replaceAll("</measure>\n", "").replaceAll("<quarter>\n", "").replaceAll("</quarter>\n", "").trim();
                                int index = tester.indexOf("<bars>");
                                while (index >= 0) {
                                    tester = tester.substring(0, tester.indexOf("<bars>", index) + 6) + tester.indexOf("</bars>", index);
                                    index = tester.indexOf("<bars>", index + 1);
                                }
                                if (!tester.equals("")) {
                                    changeModeOption.setVisibility(View.VISIBLE);
                                    changeModeImage.setVisibility(View.VISIBLE);

                                    changeModeOption.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            if (copyMeasures) {
                                                changeModeImage.setImageResource(R.drawable.square);
                                                copyMeasures = false;
                                            } else {
                                                changeModeImage.setImageResource(R.drawable.small_check);
                                                copyMeasures = true;
                                            }
                                        }
                                    });

                                    changeModeImage.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            changeModeOption.performClick();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (metronomeRunning) {
                            metronomeIcon.performClick();
                        }
                        LinearLayout poemAndSyllables = (LinearLayout) findViewById(R.id.poemandsyllables);
                        if (poemAndSyllables.isShown()) {

                            changeMode.setImageResource(R.drawable.pen);
                            if (firstMeasureModeUse) {

                                final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                if (measureModeLayout.getChildCount() > 0) {
                                    measureModeLayout.removeAllViews();
                                }
                                firstMeasureModeUse = false;

                                String newMeasures = "";
                                if (savedMeasures != null) {
                                    newMeasures = savedMeasures;
                                } else {
                                    newMeasures = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", "thisisanullvalue0518");
                                }
                                if (newMeasures.equals("thisisanullvalue0518") || newMeasures.equals("")) {
                                    newMeasures =
                                            "<bars>" +
                                                    "<sixteenth></sixteenth>" +
                                                    "<sixteenth></sixteenth>" +
                                                    "<sixteenth>eighth_1_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_1_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_1_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_2_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_2_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_4_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "<sixteenth>eighth_4_</sixteenth>" +
                                                    "<sixteenth>eighth</sixteenth>" +
                                                    "</bars>\n" +
                                                    "<measure>\n" +
                                                    "<quarter>\n</quarter>" +
                                                    "\n<quarter>\n</quarter>" +
                                                    "\n<quarter>\n</quarter>" +
                                                    "\n<quarter>\n</quarter>" +
                                                    "\n</measure>";
                                }
                                if (newMeasures != null && !newMeasures.equals("")) {
                                    ArrayList<String> measures = new ArrayList<>();
                                    Map<Integer, String> savedBars = new TreeMap(Collections.reverseOrder());
                                    int index = newMeasures.indexOf("<measure>");
                                    int measureCount = 0;
                                    int barIndex = newMeasures.indexOf("<bars>");
                                    while (index >= 0) {
                                        if (index > 8) {
                                            if ((newMeasures.substring(index - 8, index - 1).equals("</bars>"))) {
                                                savedBars.put(measureCount, newMeasures.substring(newMeasures.indexOf("<bars>", barIndex) + 6, index - 8));
                                                barIndex = newMeasures.indexOf("<bars>", barIndex + 1);
                                            }
                                        }
                                        measures.add(newMeasures.substring(index + 10, ordinalIndexOf(newMeasures, "</measure>", measures.size() + 1)));
                                        index = newMeasures.indexOf("<measure>", index + 1);
                                        measureCount++;
                                        measureCount++;
                                    }

                                    for (String measure : measures) {
                                        final LinearLayout row1 = new LinearLayout(MainActivity.this);
                                        final LinearLayout row2 = new LinearLayout(MainActivity.this);
                                        row1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        row1.setOrientation(LinearLayout.HORIZONTAL);
                                        row2.setOrientation(LinearLayout.HORIZONTAL);
                                        row1.setPadding(20, 20, 20, 0);
                                        row2.setPadding(20, 0, 20, 20);
                                        row1.setWeightSum(4);
                                        row2.setWeightSum(4);
                                        final EditText col1 = new EditText(MainActivity.this);
                                        final EditText col2 = new EditText(MainActivity.this);
                                        final EditText col3 = new EditText(MainActivity.this);
                                        final EditText col4 = new EditText(MainActivity.this);
                                        col1.setBackground(null);
                                        col2.setBackground(null);
                                        col3.setBackground(null);
                                        col4.setBackground(null);
                                        col1.setGravity(Gravity.CENTER);
                                        col2.setGravity(Gravity.CENTER);
                                        col3.setGravity(Gravity.CENTER);
                                        col4.setGravity(Gravity.CENTER);
                                        col1.setPadding(5, 0, 5, 5);
                                        col2.setPadding(5, 0, 5, 5);
                                        col3.setPadding(5, 0, 5, 5);
                                        col4.setPadding(5, 0, 5, 5);
                                        col1.setTextColor(rgb(0, 0, 0));
                                        col2.setTextColor(rgb(0, 0, 0));
                                        col3.setTextColor(rgb(0, 0, 0));
                                        col4.setTextColor(rgb(0, 0, 0));
                                        final TextView sylCol1 = new TextView(MainActivity.this);
                                        final TextView sylCol2 = new TextView(MainActivity.this);
                                        final TextView sylCol3 = new TextView(MainActivity.this);
                                        final TextView sylCol4 = new TextView(MainActivity.this);
                                        col1.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        col2.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        col3.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        col4.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        col1.setTypeface(typeface);
                                        col2.setTypeface(typeface);
                                        col3.setTypeface(typeface);
                                        col4.setTypeface(typeface);
                                        sylCol1.setTypeface(typeface);
                                        sylCol2.setTypeface(typeface);
                                        sylCol3.setTypeface(typeface);
                                        sylCol4.setTypeface(typeface);
                                        sylCol1.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        sylCol2.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        sylCol3.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        sylCol4.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                                        sylCol1.setPadding(5, 5, 5, 0);
                                        sylCol2.setPadding(5, 5, 5, 0);
                                        sylCol3.setPadding(5, 5, 5, 0);
                                        sylCol4.setPadding(5, 5, 5, 0);
                                        col1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        col2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        col3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        col4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        sylCol1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        sylCol2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        sylCol3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        sylCol4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        col1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        col4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        sylCol4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                        String darkmodeMode = getStringFromInternal("lyricdarkmode", "FALSE");
                                        if (darkmodeMode.equals("TRUE")) {
                                            col1.setTextColor(Color.WHITE);
                                            col2.setTextColor(Color.WHITE);
                                            col3.setTextColor(Color.WHITE);
                                            col4.setTextColor(Color.WHITE);
                                            sylCol1.setTextColor(Color.WHITE);
                                            sylCol2.setTextColor(Color.WHITE);
                                            sylCol3.setTextColor(Color.WHITE);
                                            sylCol4.setTextColor(Color.WHITE);
                                            col1.setHintTextColor(Color.GRAY);
                                            col2.setHintTextColor(Color.GRAY);
                                            col3.setHintTextColor(Color.GRAY);
                                            col4.setHintTextColor(Color.GRAY);
                                            sylCol1.setHintTextColor(Color.GRAY);
                                            sylCol2.setHintTextColor(Color.GRAY);
                                            sylCol3.setHintTextColor(Color.GRAY);
                                            sylCol4.setHintTextColor(Color.GRAY);
                                        }
                                        if (isNetworkAvailable()) {
                                            sylCol1.setText(" ");
                                            sylCol2.setText(" ");
                                            sylCol3.setText(" ");
                                            sylCol4.setText(" ");
                                        } else {
                                            sylCol1.setText("");
                                            sylCol2.setText("");
                                            sylCol3.setText("");
                                            sylCol4.setText("");
                                        }
                                        col1.setHint("Write");
                                        col2.setHint("your");
                                        col3.setHint("lyrics");
                                        col4.setHint("here");

                                        row1.setOnClickListener(new View.OnClickListener()

                                        {
                                            public void onClick(View v) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                if (!metronomeRunning) {
                                                    previousRow.setBackgroundColor(TRANSPARENT);
                                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                }
                                                previousRow = row1;
                                                previousSylRow = row2;
                                                barsCurrentlySelected = false;
                                                selectedSixteenth = new ImageView(MainActivity.this);
                                                measureList.bringToFront();
                                                measureToolbar.bringToFront();
                                                // 2 lines add for admob
                                                adLoading.bringToFront();
                                                mAdView.bringToFront();
                                                openNoteLength.setImageResource(R.drawable.openspinner);
                                                openNotePitch.setImageResource(R.drawable.openspinner);
                                                lengthList.animate().translationY(screenHeight);
                                                pitchList.animate().translationY(screenHeight);
                                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                    selectedBars.getChildAt(i).setClickable(false);
                                                }
                                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                barsToolbar.setVisibility(View.GONE);
                                                row1.requestFocus();
                                                // row1.clearFocus();
//                            }
                                                spinnerList.animate().translationY(screenHeight);
                                                settingList.animate().translationY(-screenHeight);
                                                measureList.animate().translationY(screenHeight);
                                                lengthList.animate().translationY(screenHeight);
                                                pitchList.animate().translationY(screenHeight);
                                                if (notes.isShown()) {
                                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                }
                                                if (wordInfo.isShown()) {
                                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                }

                                            }
                                        });

                                        row2.setOnClickListener(new View.OnClickListener()

                                        {
                                            public void onClick(View v) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                if (!metronomeRunning) {
                                                    previousRow.setBackgroundColor(TRANSPARENT);
                                                    previousSylRow.setBackgroundColor(TRANSPARENT);
                                                    row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                    row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                }
                                                previousRow = row1;
                                                previousSylRow = row2;
                                                barsCurrentlySelected = false;
                                                selectedSixteenth = new ImageView(MainActivity.this);
                                                measureList.bringToFront();
                                                measureToolbar.bringToFront();
                                                // 2 lines add for admob
                                                adLoading.bringToFront();
                                                mAdView.bringToFront();
                                                openNoteLength.setImageResource(R.drawable.openspinner);
                                                openNotePitch.setImageResource(R.drawable.openspinner);
                                                lengthList.animate().translationY(screenHeight);
                                                pitchList.animate().translationY(screenHeight);
                                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                    selectedBars.getChildAt(i).setClickable(false);
                                                }
                                                Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                barsToolbar.setVisibility(View.GONE);
                                                row1.requestFocus();
                                                //row1.clearFocus();
//                            }
                                                spinnerList.animate().translationY(screenHeight);
                                                settingList.animate().translationY(-screenHeight);
                                                measureList.animate().translationY(screenHeight);
                                                pitchList.animate().translationY(screenHeight);
                                                lengthList.animate().translationY(screenHeight);
                                                if (notes.isShown()) {
                                                    notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                }
                                                if (wordInfo.isShown()) {
                                                    wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                }

                                            }
                                        });

                                        col1.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                        {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                    if (!metronomeRunning) {
                                                        previousRow.setBackgroundColor(TRANSPARENT);
                                                        previousSylRow.setBackgroundColor(TRANSPARENT);
                                                        row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                        row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                    }

//                            }
                                                    if (previousRow != row1) {
                                                        col1.clearFocus();
                                                    }
                                                    previousRow = row1;
                                                    previousSylRow = row2;
                                                    barsCurrentlySelected = false;
                                                    selectedSixteenth = new ImageView(MainActivity.this);
                                                    measureList.bringToFront();
                                                    measureToolbar.bringToFront();
                                                    // 2 lines add for admob
                                                    adLoading.bringToFront();
                                                    mAdView.bringToFront();
                                                    openNoteLength.setImageResource(R.drawable.openspinner);
                                                    openNotePitch.setImageResource(R.drawable.openspinner);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                        selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                        selectedBars.getChildAt(i).setClickable(false);
                                                    }
                                                    Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                    barsToolbar.setVisibility(View.GONE);
                                                    spinnerList.animate().translationY(screenHeight);
                                                    settingList.animate().translationY(-screenHeight);
                                                    measureList.animate().translationY(screenHeight);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    if (notes.isShown()) {
                                                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                    if (wordInfo.isShown()) {
                                                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                }
                                            }
                                        });

                                        col2.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                        {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                    if (!metronomeRunning) {
                                                        previousRow.setBackgroundColor(TRANSPARENT);
                                                        previousSylRow.setBackgroundColor(TRANSPARENT);
                                                        row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                        row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                    }

//                            }
                                                    if (previousRow != row1) {
                                                        col2.clearFocus();
                                                    }
                                                    previousRow = row1;
                                                    previousSylRow = row2;
                                                    barsCurrentlySelected = false;
                                                    selectedSixteenth = new ImageView(MainActivity.this);
                                                    measureList.bringToFront();
                                                    measureToolbar.bringToFront();
                                                    // 2 lines add for admob
                                                    adLoading.bringToFront();
                                                    mAdView.bringToFront();
                                                    openNoteLength.setImageResource(R.drawable.openspinner);
                                                    openNotePitch.setImageResource(R.drawable.openspinner);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                        selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                        selectedBars.getChildAt(i).setClickable(false);
                                                    }
                                                    Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                    barsToolbar.setVisibility(View.GONE);
                                                    spinnerList.animate().translationY(screenHeight);
                                                    settingList.animate().translationY(-screenHeight);
                                                    measureList.animate().translationY(screenHeight);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    if (notes.isShown()) {
                                                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                    if (wordInfo.isShown()) {
                                                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                }
                                            }
                                        });


                                        col3.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                        {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                    if (!metronomeRunning) {
                                                        previousRow.setBackgroundColor(TRANSPARENT);
                                                        previousSylRow.setBackgroundColor(TRANSPARENT);
                                                        row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                        row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                    }

//                            }
                                                    if (previousRow != row1) {
                                                        col3.clearFocus();
                                                    }
                                                    previousRow = row1;
                                                    previousSylRow = row2;
                                                    barsCurrentlySelected = false;
                                                    selectedSixteenth = new ImageView(MainActivity.this);
                                                    measureList.bringToFront();
                                                    measureToolbar.bringToFront();
                                                    // 2 lines add for admob
                                                    adLoading.bringToFront();
                                                    mAdView.bringToFront();
                                                    openNoteLength.setImageResource(R.drawable.openspinner);
                                                    openNotePitch.setImageResource(R.drawable.openspinner);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                        selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                        selectedBars.getChildAt(i).setClickable(false);
                                                    }
                                                    Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                    barsToolbar.setVisibility(View.GONE);
                                                    spinnerList.animate().translationY(screenHeight);
                                                    settingList.animate().translationY(-screenHeight);
                                                    measureList.animate().translationY(screenHeight);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    if (notes.isShown()) {
                                                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                    if (wordInfo.isShown()) {
                                                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                }
                                            }
                                        });


                                        col4.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                        {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
//                                if (previousRow == row1) {
//                                    row1.setBackgroundColor(TRANSPARENT);
//                                    row2.setBackgroundColor(TRANSPARENT);
//                                    previousRow = new LinearLayout(MainActivity.this);
//                                    previousSylRow = new LinearLayout(MainActivity.this);
//                                } else {
                                                    if (!metronomeRunning) {
                                                        previousRow.setBackgroundColor(TRANSPARENT);
                                                        previousSylRow.setBackgroundColor(TRANSPARENT);
                                                        row1.setBackgroundColor(argb(50, 200, 200, 200));
                                                        row2.setBackgroundColor(argb(50, 200, 200, 200));
                                                    }

//                            }
                                                    if (previousRow != row1) {
                                                        col4.clearFocus();
                                                    }
                                                    previousRow = row1;
                                                    previousSylRow = row2;
                                                    barsCurrentlySelected = false;
                                                    selectedSixteenth = new ImageView(MainActivity.this);
                                                    measureList.bringToFront();
                                                    measureToolbar.bringToFront();
                                                    // 2 lines add for admob
                                                    adLoading.bringToFront();
                                                    mAdView.bringToFront();
                                                    openNoteLength.setImageResource(R.drawable.openspinner);
                                                    openNotePitch.setImageResource(R.drawable.openspinner);
                                                    lengthList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                                        selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                                        selectedBars.getChildAt(i).setClickable(false);
                                                    }
                                                    Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                                    barsToolbar.setVisibility(View.GONE);
                                                    spinnerList.animate().translationY(screenHeight);
                                                    settingList.animate().translationY(-screenHeight);
                                                    measureList.animate().translationY(screenHeight);
                                                    pitchList.animate().translationY(screenHeight);
                                                    lengthList.animate().translationY(screenHeight);
                                                    if (notes.isShown()) {
                                                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                    if (wordInfo.isShown()) {
                                                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                                                    }
                                                }
                                            }
                                        });
                                        col1.addTextChangedListener(new TextWatcher() {

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                                last_measure_edit = System.currentTimeMillis();
                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                            }

                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start,
                                                                          int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start,
                                                                      int before, int count) {
                                                //You need to remove this to run only once
                                                measureTyping = true;
                                                measureChanged = true;
                                                textviewsToChange.put(sylCol1, col1.getText().toString());
                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                            }
                                        });
                                        col2.addTextChangedListener(new TextWatcher() {

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                                last_measure_edit = System.currentTimeMillis();
                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                            }

                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start,
                                                                          int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start,
                                                                      int before, int count) {
                                                //You need to remove this to run only once
                                                measureTyping = true;
                                                measureChanged = true;
                                                textviewsToChange.put(sylCol2, col2.getText().toString());
                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                            }
                                        });
                                        col3.addTextChangedListener(new TextWatcher() {

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                                last_measure_edit = System.currentTimeMillis();
                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                            }

                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start,
                                                                          int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start,
                                                                      int before, int count) {
                                                //You need to remove this to run only once
                                                measureTyping = true;
                                                measureChanged = true;
                                                textviewsToChange.put(sylCol3, col3.getText().toString());
                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                            }
                                        });
                                        col4.addTextChangedListener(new TextWatcher() {

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                                last_measure_edit = System.currentTimeMillis();
                                                measureHandler.postDelayed(input_measure_finish_checker, delay);


                                            }

                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start,
                                                                          int count, int after) {


                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start,
                                                                      int before, int count) {
                                                //You need to remove this to run only once
                                                measureTyping = true;
                                                measureChanged = true;
                                                textviewsToChange.put(sylCol4, col4.getText().toString());
                                                measureHandler.removeCallbacks(input_measure_finish_checker);
                                            }
                                        });

                                        String[] quarters = new String[4];
                                        quarters[0] = measure.substring(ordinalIndexOf(measure, "<quarter>", 1) + 10, ordinalIndexOf(measure, "</quarter>", 1));
                                        quarters[1] = measure.substring(ordinalIndexOf(measure, "<quarter>", 2) + 10, ordinalIndexOf(measure, "</quarter>", 2));
                                        quarters[2] = measure.substring(ordinalIndexOf(measure, "<quarter>", 3) + 10, ordinalIndexOf(measure, "</quarter>", 3));
                                        quarters[3] = measure.substring(ordinalIndexOf(measure, "<quarter>", 4) + 10, ordinalIndexOf(measure, "</quarter>", 4));


                                        if (!quarters[0].equals("")) {
                                            col1.setText(quarters[0]);
                                        }
                                        if (!quarters[1].equals("")) {
                                            col2.setText(quarters[1]);
                                        }
                                        if (!quarters[2].equals("")) {
                                            col3.setText(quarters[2]);
                                        }
                                        if (!quarters[3].equals("")) {
                                            col4.setText(quarters[3]);
                                        }

                                        row1.addView(col1);
                                        row1.addView(col2);
                                        row1.addView(col3);
                                        row1.addView(col4);
                                        row2.addView(sylCol1);
                                        row2.addView(sylCol2);
                                        row2.addView(sylCol3);
                                        row2.addView(sylCol4);
                                        if (measureModeLayout.getChildCount() > 1) {
                                            measureModeLayout.addView(row1, measureModeLayout.indexOfChild(previousRow) + 2);
                                            measureModeLayout.addView(row2, measureModeLayout.indexOfChild(previousSylRow) + 2);
                                        } else {
                                            measureModeLayout.addView(row1);
                                            measureModeLayout.addView(row2);
                                        }
                                        //previousRow.clearFocus();
                                        previousRow.setBackgroundColor(TRANSPARENT);
                                        previousSylRow.setBackgroundColor(TRANSPARENT);
                                        row1.setBackgroundColor(argb(50, 200, 200, 200));
                                        row2.setBackgroundColor(argb(50, 200, 200, 200));
                                        previousRow = row1;
                                        previousSylRow = row2;
                                        barsCurrentlySelected = false;
                                        selectedSixteenth = new ImageView(MainActivity.this);
                                        measureList.bringToFront();
                                        measureToolbar.bringToFront();
                                        // 2 lines add for admob
                                        adLoading.bringToFront();
                                        mAdView.bringToFront();
                                        openNoteLength.setImageResource(R.drawable.openspinner);
                                        openNotePitch.setImageResource(R.drawable.openspinner);
                                        lengthList.animate().translationY(screenHeight);
                                        pitchList.animate().translationY(screenHeight);
                                        for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                            selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                            selectedBars.getChildAt(i).setClickable(false);
                                        }
                                        Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                                        barsToolbar.setVisibility(View.GONE);


                                    }
                                    //need to sort savedbars so that the highest keys are first
                                    //this also needs to come after the measure for loop
                                    //notes also need to beadded based on the values of savedbars
                                    for (Integer savedBarIndex : savedBars.keySet()) {
                                        final LinearLayout bars = new LinearLayout(MainActivity.this);
                                        String color = "black";


                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                            color = "white";
                                        }
                                        String barsName = "bars_" + color;
                                        bars.setBackgroundResource((getResources().getIdentifier(barsName, "drawable", getPackageName())));
                                        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                                        int pixels = (int) (100 * scale + 0.5f);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels);
                                        params.setMargins(20, 0, 20, 0);
                                        bars.setLayoutParams(params);
                                        bars.setWeightSum(16);
                                        bars.setOrientation(LinearLayout.HORIZONTAL);


                                        for (int i = 0; i < 16; i++) {
                                            ImageView sixteenth = new ImageView(MainActivity.this);
                                            sixteenth.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                                            sixteenth.setScaleType(ImageView.ScaleType.FIT_XY);
                                            bars.addView(sixteenth);
                                        }
                                        int sixteenthIndex = savedBars.get(savedBarIndex).indexOf("<sixteenth>");
                                        int sixteenthCount = 0;
                                        while (sixteenthIndex >= 0) {
                                            String imageResource = savedBars.get(savedBarIndex).substring(sixteenthIndex + 11, ordinalIndexOf(savedBars.get(savedBarIndex), "</sixteenth>", sixteenthCount + 1));
                                            if (imageResource.contains("_")) {
                                                if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                                    imageResource = imageResource + "white";
                                                } else {
                                                    imageResource = imageResource + "black";
                                                }
                                                ((ImageView) bars.getChildAt(sixteenthCount)).setImageResource(getResources().getIdentifier(imageResource, "drawable", getPackageName()));
                                            }
                                            sixteenthIndex = savedBars.get(savedBarIndex).indexOf("<sixteenth>", sixteenthIndex + 1);
                                            sixteenthCount++;
                                        }
                                        bars.setOnClickListener(new View.OnClickListener()

                                        {
                                            public void onClick(View v) {
                                                openBarsToolbar(bars);
                                                // addNotesDuration();
                                            }
                                        });
                                        measureModeLayout.addView(bars, savedBarIndex);
                                        allBars.put(bars, savedBars.get(savedBarIndex));
                                    }


                                }

                            }
                            LinearLayout measureMode = (LinearLayout) findViewById(R.id.measuremode);
                            poemAndSyllables.setVisibility(View.GONE);
                            undo.setVisibility(View.GONE);
                            options.setClickable(false);
                            openSpinner.setClickable(false);
                            button.setClickable(false);
                            if (foreignUser) {
                                ImageView bottomToolbarUnlock = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
                                spinnerToolbar.setVisibility(View.VISIBLE);
                                bottomToolbarUnlock.setVisibility(View.VISIBLE);
                            }
                            measureMode.setVisibility(View.VISIBLE);
                            measureToolbar.setVisibility(View.VISIBLE);
                            measureToolbar.bringToFront();
                            if (barsCurrentlySelected) {
                                barsToolbar.setVisibility(View.VISIBLE);
                                barsToolbar.bringToFront();
                            }
                            //changeMode.setText("Exit Measure Mode");
                            wordInfoTitle.setText("LYRIC");
                            wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                            inputWord.requestFocus();
                            wordInfo.setVisibility(View.VISIBLE);
                            wordinfoBar.setVisibility(View.VISIBLE);
                            closeWordInfo.setVisibility(View.GONE);
                            wordInfoTitle.setVisibility(View.VISIBLE);
                            wordInfoView.setVisibility(View.VISIBLE);
                            wordInfo.bringToFront();
                            loading.bringToFront();
                            wordInfoTitle.bringToFront();
                            wordinfoBar.bringToFront();
                            wordInfoView.bringToFront();
                            bottomToolbarToggler.bringToFront();
                            undo.bringToFront();
                            notesIcon.setImageResource(R.drawable.noteicon);

                            notesFocus = false;
                            if (colorTheme.equals("royal")) {
                                wordinfoBar.setBackgroundResource(R.drawable.focus);
                                notesBar.setBackgroundResource(R.drawable.no_focus);
                                notesTitle.setBackgroundResource(R.drawable.tab_left);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);

                            } else if (colorTheme.equals("sunset")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                                notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                            } else if (colorTheme.equals("joy")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                                notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);

                            } else if (colorTheme.equals("dark")) {

                                wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                                notesBar.setBackgroundResource(R.drawable.no_focus_black);
                                notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);

                            }
                            wordInfo.requestFocus();
                            wordInfo.setTextKeepState(poem.getText().toString());
                            spinnerList.animate().translationY(screenHeight);
                            measureList.animate().translationY(screenHeight);
                            lengthList.animate().translationY(screenHeight);
                            pitchList.animate().translationY(screenHeight);
                            settingList.animate().translationY(-screenHeight);
                            openMeasureSpinner.setImageResource(R.drawable.openspinner);
                            openNoteLength.setImageResource(R.drawable.openspinner);
                            openNotePitch.setImageResource(R.drawable.openspinner);
                            openSpinner.setImageResource(R.drawable.openspinner);
                            optionsButton.setImageResource(R.drawable.settings1);
                            try {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //we don't need the if statement here because when you make a search, it minimizes notes.
                            if (notes.isShown() && notes.getHeight() > wordInfo.getHeight()) {

                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(mainActivity);
                                constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                                constraintSet.applyTo(mainActivity);

                            } else {
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(mainActivity);
                                constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                                constraintSet.applyTo(mainActivity);
                            }

                        } else {

                            changeMode.setImageResource(R.drawable.measure_mode);
                            if (copyMeasures) {
                                final String measureText = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", null);
                                String textToCopy = "";
                                textToCopy = measureText.replaceAll("<measure>\n", "").replaceAll("</measure>", "").replaceAll("<quarter>\n", "").replaceAll("</quarter>\n", " ");
                                int index = textToCopy.indexOf("<bars>");
                                while (index >= 0) {
                                    textToCopy = textToCopy.substring(0, textToCopy.indexOf("<bars>", index)) + textToCopy.substring(textToCopy.indexOf("</bars>") + 7);
                                    index = textToCopy.indexOf("<bars>", index + 1);
                                }
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Measure", textToCopy);
                                clipboard.setPrimaryClip(clip);
                            }
                            LinearLayout measureMode = (LinearLayout) findViewById(R.id.measuremode);
                            poemAndSyllables.setVisibility(View.VISIBLE);
                            undo.setVisibility(View.VISIBLE);
                            measureMode.setVisibility(View.GONE);
                            if (isNetworkAvailable()) {
                                options.setClickable(true);
                                openSpinner.setClickable(true);
                                button.setClickable(true);
                            }
                            if (foreignUser) {
                                ImageView bottomToolbarUnlock = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
                                spinnerToolbar.setVisibility(View.GONE);
                                bottomToolbarUnlock.setVisibility(View.GONE);
                            }
                            measureToolbar.setVisibility(View.GONE);
                            barsToolbar.setVisibility(View.GONE);
//                            changeMode.setText("Enter Measure Mode");
                            wordInfoTitle.setText("SEARCH");
                            if (notes.isShown()) {
                                notesIcon.setImageResource(R.drawable.noteicondisabled);
                                if (colorTheme.equals("royal")) {

                                    wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                                    notesBar.setBackgroundResource(R.drawable.focus);
                                    notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right);

                                } else if (colorTheme.equals("sunset")) {
                                    wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                                    notesBar.setBackgroundResource(R.drawable.focus_orange);
                                    notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);

                                } else if (colorTheme.equals("joy")) {

                                    wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                                    notesBar.setBackgroundResource(R.drawable.focus_blue);
                                    notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);

                                } else if (colorTheme.equals("dark")) {

                                    wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                                    notesBar.setBackgroundResource(R.drawable.focus_black);
                                    notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);

                                }
                            }
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                            constraintSet.applyTo(mainActivity);
                            wordInfo.setVisibility(View.GONE);
                            closeWordInfo.setVisibility(View.GONE);
                            wordinfoBar.setVisibility(View.GONE);
                            wordInfoTitle.setVisibility(View.GONE);
                            wordInfoView.setVisibility(View.GONE);
                        }
                        changeModePopup.setVisibility(View.GONE);
                        popupBackground.setVisibility(View.GONE);
                    }
                });

                no.setOnClickListener(new View.OnClickListener()

                {
                    public void onClick(View v) {
                        changeModePopup.setVisibility(View.GONE);
                        popupBackground.setVisibility(View.GONE);
                    }
                });

            }
        });


        poem.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

                if (processing) {

                    AsyncSyllableCountThread.cancel(true);
                    currentLastWordIndexes.clear();
                    currentWordIndexes.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
                            cornerLoader.postDelayed(new Runnable() {
                                public void run() {
                                    cornerLoader.setVisibility(View.GONE);
                                }
                            }, 500);
                        }
                    });
                    processing = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //You need to remove this to run only once

                typing = true;
                handler.removeCallbacks(input_finish_checker);
                String addBuffer = "\n          ";
                if (poem.getText().toString().equals(addBuffer)) {
                    poem.setText("");
                }
                int poemPosition = poem.getSelectionEnd();
                if (poem.getText().toString().length() != 0) {
                    if (poem.getText().toString().length() < addBuffer.length()) {
                        String poemWithBuffer = poem.getText().toString() + addBuffer;
                        poem.setText(poemWithBuffer);
                        poemChangeChecker = poemChangeChecker + addBuffer;
                        if (!titleHelper.hasFocus()) {
                            poem.setSelection(poemPosition);
                        }
                    } else if (!poem.getText().toString().substring(poem.getText().toString().length() - addBuffer.length(), poem.getText().toString().length()).equals(addBuffer)) {
                        String poemWithBuffer = poem.getText().toString() + addBuffer;
                        poem.setText(poemWithBuffer);
                        poemChangeChecker = poemChangeChecker + addBuffer;
                        if (!titleHelper.hasFocus()) {
                            poem.setSelection(poemPosition);
                        }

                    }

                }
            }
        });
        metronomeAccentInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!metronomeAccentInput.getText().toString().equals("")) {
                    if (Integer.parseInt(metronomeAccentInput.getText().toString()) > 16) {
                        metronomeAccentInput.setText("16");
                        metronomeAccentInput.setSelection(2);
                    }
                }

            }
        });

        metronomeInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!metronomeInput.getText().toString().equals("")) {
                    if (Integer.parseInt(metronomeInput.getText().toString()) > 220) {
                        metronomeInput.setText("220");
                        metronomeInput.setSelection(3);
                    }
                }

            }
        });
        final EditText editRecordingInput = (EditText) findViewById(R.id.edit_recording_addtoline_input);
        editRecordingInput.addTextChangedListener(new

                                                          TextWatcher() {

                                                              @Override
                                                              public void afterTextChanged(Editable s) {

                                                              }

                                                              @Override
                                                              public void beforeTextChanged(CharSequence s, int start,
                                                                                            int count, int after) {

                                                              }

                                                              @Override
                                                              public void onTextChanged(CharSequence s, int start,
                                                                                        int before, int count) {
                                                                  if (!editRecordingInput.getText().toString().equals("")) {
                                                                      if (Integer.parseInt(editRecordingInput.getText().toString()) >= poem.getLineCount()) {
                                                                          editRecordingInput.setText(Integer.toString(poem.getLineCount() - 1));
                                                                          editRecordingInput.setSelection(Integer.toString(poem.getLineCount() - 1).length());
                                                                          Toast toast = Toast.makeText(mainActivityHelper, "Line number too high",
                                                                                  Toast.LENGTH_SHORT);
                                                                          toast.setGravity(Gravity.CENTER, 0, 0);
                                                                          toast.show();
                                                                      }
                                                                  }

                                                              }
                                                          });
        title.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                TextView title = (TextView) findViewById(R.id.title);
                if (title.getText().toString().equals("")) {
                    putStringToInternal("lyric" + lyricIndex + "title", "Untitled");
                } else {
                    putStringToInternal("lyric" + lyricIndex + "title", title.getText().toString());
                }
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                putStringToInternal("lyric" + lyricIndex + "poemsoftsavedate", currentDateTimeString);
                putStringToInternal("lyric" + lyricIndex + "poemsoftsave", poem.getText().toString());

            }
        });

        notes.addTextChangedListener(new

                                             TextWatcher() {

                                                 @Override
                                                 public void afterTextChanged(Editable s) {
                                                 }

                                                 @Override
                                                 public void beforeTextChanged(CharSequence s, int start,
                                                                               int count, int after) {
                                                 }

                                                 @Override
                                                 public void onTextChanged(CharSequence s, int start,
                                                                           int before, int count) {


                                                     TextView notes = (TextView) findViewById(R.id.notes);
                                                     putStringToInternal("lyric" + lyricIndex + "notes", notes.getText().toString());

                                                 }
                                             });


        poem.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                    measureList.animate().translationY(screenHeight);
                    lengthList.animate().translationY(screenHeight);
                    pitchList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                    settingList.animate().translationY(-screenHeight);
                    optionsButton.setImageResource(R.drawable.settings1);
                    if (notes.isShown()) {
                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                    }
                    if (wordInfo.isShown()) {
                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                    }

                }

            }
        });


        title.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                    settingList.animate().translationY(-screenHeight);
                    optionsButton.setImageResource(R.drawable.settings1);
                    measureList.animate().translationY(screenHeight);
                    lengthList.animate().translationY(screenHeight);
                    pitchList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                    if (notes.isShown()) {
                        notes.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                    }
                    if (wordInfo.isShown()) {
                        wordInfo.getLayoutParams().height = (int) (spinnerToolbar.getHeight() * .1);
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {


                pulse(button, false);
                if (!wordInfo.isShown() || wordInfo.getLayoutParams().height < wordInfoHeight) {
                    wordInfo.getLayoutParams().height = (wordInfoHeight);
                }
                openPopup(null);
                inputWord.requestFocus();
                wordInfo.setVisibility(View.VISIBLE);
                closeWordInfo.setVisibility(View.VISIBLE);
                wordinfoBar.setVisibility(View.VISIBLE);
                wordInfoTitle.setVisibility(View.VISIBLE);
                wordInfoView.setVisibility(View.VISIBLE);
                wordInfo.bringToFront();
                loading.bringToFront();
                wordInfoTitle.bringToFront();
                wordinfoBar.bringToFront();
                wordInfoView.bringToFront();
                closeWordInfo.bringToFront();
                bottomToolbarToggler.bringToFront();
                undo.bringToFront();
                notesIcon.setImageResource(R.drawable.noteicon);

                notesFocus = false;
                if (colorTheme.equals("royal")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus);
                    notesBar.setBackgroundResource(R.drawable.no_focus);
                    notesTitle.setBackgroundResource(R.drawable.tab_left);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);

                } else if (colorTheme.equals("sunset")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                    notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);

                } else if (colorTheme.equals("joy")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                    notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);

                } else if (colorTheme.equals("dark")) {

                    wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                    notesBar.setBackgroundResource(R.drawable.no_focus_black);
                    notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                    wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);

                }
                if (!inputWord.getText().toString().equals("")) {
                    getWordInfo(spinnerSelection);
                } else {
                    wordInfo.setText("No Results. Please make a selection and enter a word.");
                }
                wordInfo.requestFocus();
                spinnerList.animate().translationY(screenHeight);
                openSpinner.setImageResource(R.drawable.openspinner);
                measureList.animate().translationY(screenHeight);
                pitchList.animate().translationY(screenHeight);
                lengthList.animate().translationY(screenHeight);
                openMeasureSpinner.setImageResource(R.drawable.openspinner);
                openNoteLength.setImageResource(R.drawable.openspinner);
                openNotePitch.setImageResource(R.drawable.openspinner);
                settingList.animate().translationY(-screenHeight);
                optionsButton.setImageResource(R.drawable.settings1);

                //we don't need the if statement here because when you make a search, it minimizes notes.
                if (notes.isShown() && notes.getHeight() > wordInfo.getHeight()) {

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, notes.getId(), ConstraintSet.TOP, 0);
                    constraintSet.applyTo(mainActivity);

                } else {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(scrollView.getId(), ConstraintSet.BOTTOM, wordInfo.getId(), ConstraintSet.TOP, 0);
                    constraintSet.applyTo(mainActivity);
                }
                if (notesTitle.isShown() && wordInfoTitle.isShown()) {
                    int hintCheck = getIntFromInternal("lyricnotesiconhint", 0);
                    if (hintCheck < 1) {
                        putIntToInternal("lyricnotesiconhint", hintCheck + 1);

                    } else if (hintCheck == 1) {
                        putIntToInternal("lyricnotesiconhint", hintCheck + 1);

                        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
                        ImageView hintImage = (ImageView) findViewById(R.id.hint_image);
                        TextView hintText = (TextView) findViewById(R.id.hint_text);
                        final TextView hintDone = (TextView) findViewById(R.id.hint_okay);
                        hintImage.setImageResource(R.drawable.hint2);
                        hintText.setText("When both the SEARCH tab and NOTES tab are open, you can click the notes icon to toggle which tab has the focus/foreground. Give it a try!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openPopup(hintPopup);
                                hintDone.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        hintPopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }

                                });
                            }
                        });
                    }
                }
            }
        });


        options.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (keyboardOpen) {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (settingList.getTranslationY() == 0) {
                        settingList.animate().translationY(-screenHeight);
                        optionsButton.setImageResource(R.drawable.settings1);
                    }
                    if (measureList.getTranslationY() == 0) {
                        measureList.animate().translationY(screenHeight);
                        openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    }
                    if (pitchList.getTranslationY() == 0) {
                        pitchList.animate().translationY(screenHeight);
                        openNotePitch.setImageResource(R.drawable.openspinner);
                    }
                    if (lengthList.getTranslationY() == 0) {
                        lengthList.animate().translationY(screenHeight);
                        openNoteLength.setImageResource(R.drawable.openspinner);
                    }
                    if (spinnerList.getTranslationY() == 0) {
                        spinnerList.animate().translationY(screenHeight);
                        openSpinner.setImageResource(R.drawable.openspinner);
                    } else {
                        spinnerList.setVisibility(View.VISIBLE);
                        spinnerList.bringToFront();
                        spinnerToolbar.bringToFront();
                        // 2 lines add for admob
                        adLoading.bringToFront();
                        mAdView.bringToFront();
                        spinnerList.animate().translationY(0);
                        openSpinner.setImageResource(R.drawable.closespinner);
                    }

                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        openSpinner.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {

                options.performClick();

            }
        });

        measureToolbarText.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (settingList.getTranslationY() == 0) {
                    settingList.animate().translationY(-screenHeight);
                    optionsButton.setImageResource(R.drawable.settings1);
                }

                if (spinnerList.getTranslationY() == 0) {
                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                }
                if (pitchList.getTranslationY() == 0) {
                    pitchList.animate().translationY(screenHeight);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                }
                if (lengthList.getTranslationY() == 0) {
                    lengthList.animate().translationY(screenHeight);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                }
                if (measureList.getTranslationY() == 0) {
                    measureList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                } else {
                    measureList.setVisibility(View.VISIBLE);
                    measureList.bringToFront();
                    measureToolbar.bringToFront();
                    // 2 lines add for admob
                    adLoading.bringToFront();
                    mAdView.bringToFront();
                    measureList.animate().translationY(0);
                    openMeasureSpinner.setImageResource(R.drawable.closespinner);
                }
                ImageView delete_measure_checkbox = (ImageView) findViewById(R.id.delete_selected_measure_notes_checkbox);
                TextView delete_measure = (TextView) findViewById(R.id.delete_selected_measure_notes);
                ImageView play_measure_checkbox = (ImageView) findViewById(R.id.play_measure_bars_checkbox);
                TextView play_measure = (TextView) findViewById(R.id.play_measure_bars);
                final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                if (allBars.keySet().contains(measureModeLayout.getChildAt(measureModeLayout.indexOfChild(previousRow) - 1))) {
                    delete_measure_checkbox.setVisibility(View.VISIBLE);
                    delete_measure.setVisibility(View.VISIBLE);
                    play_measure_checkbox.setVisibility(View.VISIBLE);
                    play_measure.setVisibility(View.VISIBLE);
                } else {
                    delete_measure_checkbox.setVisibility(View.GONE);
                    delete_measure.setVisibility(View.GONE);
                    play_measure_checkbox.setVisibility(View.GONE);
                    play_measure.setVisibility(View.GONE);
                }

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        barsToolbarPitchText.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if(!barsToolbarLengthText.getText().toString().equals("Remove")) {
                    if (settingList.getTranslationY() == 0) {
                        settingList.animate().translationY(-screenHeight);
                        optionsButton.setImageResource(R.drawable.settings1);
                    }

                    if (spinnerList.getTranslationY() == 0) {
                        spinnerList.animate().translationY(screenHeight);
                        openSpinner.setImageResource(R.drawable.openspinner);
                    }
                    if (measureList.getTranslationY() == 0) {
                        measureList.animate().translationY(screenHeight);
                        openMeasureSpinner.setImageResource(R.drawable.openspinner);
                    }
                    if (lengthList.getTranslationY() == 0) {
                        lengthList.animate().translationY(screenHeight);
                        openNoteLength.setImageResource(R.drawable.openspinner);
                    }
                    if (pitchList.getTranslationY() == 0) {
                        pitchList.animate().translationY(screenHeight);
                        openNotePitch.setImageResource(R.drawable.openspinner);
                    } else {
                        pitchList.setVisibility(View.VISIBLE);
                        pitchList.bringToFront();
                        barsToolbar.bringToFront();
                        // 2 lines add for admob
                        adLoading.bringToFront();
                        mAdView.bringToFront();
                        pitchList.animate().translationY(0);
                        openNotePitch.setImageResource(R.drawable.closespinner);
                    }

                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        barsToolbarLengthText.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                if (settingList.getTranslationY() == 0) {
                    settingList.animate().translationY(-screenHeight);
                    optionsButton.setImageResource(R.drawable.settings1);
                }

                if (spinnerList.getTranslationY() == 0) {
                    spinnerList.animate().translationY(screenHeight);
                    openSpinner.setImageResource(R.drawable.openspinner);
                }
                if (measureList.getTranslationY() == 0) {
                    measureList.animate().translationY(screenHeight);
                    openMeasureSpinner.setImageResource(R.drawable.openspinner);
                }
                if (pitchList.getTranslationY() == 0) {
                    pitchList.animate().translationY(screenHeight);
                    openNotePitch.setImageResource(R.drawable.openspinner);
                }
                if (lengthList.getTranslationY() == 0) {
                    lengthList.animate().translationY(screenHeight);
                    openNoteLength.setImageResource(R.drawable.openspinner);
                } else {
                    lengthList.setVisibility(View.VISIBLE);
                    lengthList.bringToFront();
                    barsToolbar.bringToFront();
                    // 2 lines add for admob
                    adLoading.bringToFront();
                    mAdView.bringToFront();
                    lengthList.animate().translationY(0);
                    openNoteLength.setImageResource(R.drawable.closespinner);
                }

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        openMeasureSpinner.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                measureToolbarText.performClick();

            }
        });
        openNotePitch.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                barsToolbarPitchText.performClick();

            }
        });
        openNoteLength.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                barsToolbarLengthText.performClick();

            }
        });
        Timer t = new Timer();
        poemChangeChecker = poem.getText().toString();
        wordChangeChecker = inputWord.getText().toString();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
//run() reads the sensor data, applies the appropriate status and if necassary updates the server
            public void run() {
                if (proUser) {
                    if (!getStringFromInternal("lyricprouser", "false").equals("true0518")) {
                        putStringToInternal("lyricprouser", "true0518");
                    }
                }
                if (isNetworkAvailable() && !globalForceOffline) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            final ConstraintLayout offlinePopup = (ConstraintLayout) findViewById(R.id.offline_popup);
//                            if (offlinePopup.isShown()) {
//                                offlinePopup.setVisibility(View.GONE);
//                                popupBackground.setVisibility(View.GONE);
//                            }
//                        }
//                    });
                    if (poem.isShown()) {
                        try {
                            //FIXX java.lang.IndexOutOfBoundsException:
                            if (poem.length() == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final LinearLayout recordingLineHolder = (LinearLayout) findViewById(R.id.recording_line_numbers);
                                        syllables.setText("");
                                        recordingLineHolder.removeAllViews();
                                    }
                                });
                                //FIXX java.lang.ArrayIndexOutOfBoundsException:
                            } else if (!poemChangeChecker.equals(poem.getText().toString()) || (poem.getLineCount() != (syllables.getLineCount() - 1))) {
                                if (!typing) {
                                    addRecordingsInline();
                                    getSyllablesAndRhymes(poem);
                                    poemChangeChecker = poem.getText().toString();
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {

                        }
                    } else {
                        if (measureChanged) {
                            if (!measureTyping) {
                                measureChanged = false;
                                updateMeasureSave(false);
                                getMeasureSyllables();
                            }
                        }
                    }
                    if (offlineBool == true) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final TextView options = (TextView) findViewById(R.id.optionsSpinner);
                                final ImageButton openSpinner = (ImageButton) findViewById(R.id.open_spinner);
                                final ImageButton button = (ImageButton) findViewById(R.id.search);
                                options.setClickable(true);
                                options.setText("Find a Word");
                                openSpinner.setClickable(true);
                                button.setClickable(true);
                                // 3 lines add for admob
                                if (!proUser) {
                                    mAdView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    offlineBool = false;
                } else {
                    if (!poem.isShown()) {
                        if (measureChanged) {
                            if (!measureTyping) {
                                measureChanged = false;
                                updateMeasureSave(false);
                            }
                        }
                    }
                    if (isNetworkAvailable() && options.getText().toString().equals("Not Available Offline")) {
                        offlineBool = false;
                    }
                    if (!isNetworkAvailable() && options.getText().toString().equals("Find a Word")) {
                        offlineBool = false;
                    }
                    if (offlineBool == false) {
                        poemChangeChecker = "";
                        measureChanged = true;
                        final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                        for (int i = 0; (i + 1) < measureModeLayout.getChildCount(); i++) {
                            if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                continue;
                            }
                            LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                            i++;
                            LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                            TextView sylCol1 = (TextView) row2.getChildAt(0);
                            TextView sylCol2 = (TextView) row2.getChildAt(1);
                            TextView sylCol3 = (TextView) row2.getChildAt(2);
                            TextView sylCol4 = (TextView) row2.getChildAt(3);
                            EditText col1 = (EditText) row1.getChildAt(0);
                            EditText col2 = (EditText) row1.getChildAt(1);
                            EditText col3 = (EditText) row1.getChildAt(2);
                            EditText col4 = (EditText) row1.getChildAt(3);
                            textviewsToChange.put(sylCol1, col1.getText().toString());
                            textviewsToChange.put(sylCol2, col2.getText().toString());
                            textviewsToChange.put(sylCol3, col3.getText().toString());
                            textviewsToChange.put(sylCol4, col4.getText().toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                if (!foreignUser) {
                                    try {
//                                                                      final ConstraintLayout offlinePopup = (ConstraintLayout) findViewById(R.id.offline_popup);
//                                                                      TextView offlineText = (TextView) findViewById(R.id.offline_text);
//                                                                      TextView offlineTitle = (TextView) findViewById(R.id.offline_title);
                                        String offlineMode = getStringFromInternal("lyricreviserate", "NULL");
                                        if (!isNetworkAvailable()) {
//                                                                          offlineTitle.setText("Offline Mode");
//                                                                          offlineText.setText("You are currently offline. Rhyme Matching, Syllable Counting, and Word Lookup will all be disabled.");
                                            final TextView options = (TextView) findViewById(R.id.optionsSpinner);
                                            final ImageButton openSpinner = (ImageButton) findViewById(R.id.open_spinner);
                                            final ImageButton button = (ImageButton) findViewById(R.id.search);

                                            options.setClickable(false);
                                            options.setText("Not Available Offline");
                                            openSpinner.setClickable(false);
                                            button.setClickable(false);
                                            options.setVisibility(View.VISIBLE);
                                            inputWord.setVisibility(View.GONE);
                                            //line add for admob
                                            mAdView.setVisibility(View.GONE);


                                        } else {
                                            if (offlineMode.equals("AUTO_OFF")) {
//                                                                              offlineTitle.setText("Updates Disabled");
//                                                                              offlineText.setText("All updates are currently disabled. To enable updates, click the settings icon, go to \"General Settings\" and go to \"Change Update Rate\"");
                                            }

                                            options.setClickable(true);
                                            options.setText("Find a Word");
                                            openSpinner.setClickable(true);
                                            button.setClickable(true);
                                            // 3 lines add for admob
                                            if (!proUser) {
                                                mAdView.setVisibility(View.VISIBLE);
                                            }
                                        }
//                                                                      openPopup(offlinePopup);
//                                                                      TextView okButton = (TextView) findViewById(R.id.popup_okay);
//
//                                                                      okButton.setOnClickListener(new View.OnClickListener() {
//                                                                          public void onClick(View v) {
//
//                                                                              offlinePopup.setVisibility(View.GONE);
//                                                                              popupBackground.setVisibility(View.GONE);
//                                                                          }
//                                                                      });
                                    } catch (NullPointerException e) {

                                    }
                                }
                                String addBuffer = "\n          ";
                                if (poem.getText().toString().equals(addBuffer)) {
                                    poem.setText("");
                                }
                                int poemPosition = poem.getSelectionEnd();
                                if (poem.getText().toString().length() != 0) {
                                    if (poem.getText().toString().length() < addBuffer.length()) {
                                        String poemWithBuffer = poem.getText().toString() + addBuffer;
                                        poem.setText(poemWithBuffer);
                                        poemChangeChecker = poemChangeChecker + addBuffer;
                                        if (!titleHelper.hasFocus()) {
                                            poem.setSelection(poemPosition);
                                        }
                                    } else if (!poem.getText().toString().substring(poem.getText().toString().length() - addBuffer.length(), poem.getText().toString().length()).equals(addBuffer)) {
                                        String poemWithBuffer = poem.getText().toString() + addBuffer;
                                        poem.setText(poemWithBuffer);
                                        poemChangeChecker = poemChangeChecker + addBuffer;
                                        if (!titleHelper.hasFocus()) {
                                            poem.setSelection(poemPosition);
                                        }

                                    }

                                }
                                final LinearLayout recordingLineHolder = (LinearLayout) findViewById(R.id.recording_line_numbers);
                                syllables.setText("");
                                recordingLineHolder.removeAllViews();
                                poem.setText(poem.getText().toString());
                                test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                                final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
                                for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                                        continue;
                                    }
                                    LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
                                    i++;
                                    LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
                                    TextView sylCol1 = (TextView) row2.getChildAt(0);
                                    TextView sylCol2 = (TextView) row2.getChildAt(1);
                                    TextView sylCol3 = (TextView) row2.getChildAt(2);
                                    TextView sylCol4 = (TextView) row2.getChildAt(3);
                                    sylCol1.setText("");
                                    sylCol2.setText("");
                                    sylCol3.setText("");
                                    sylCol4.setText("");
                                }
                            }
                        });


                    }
                    offlineBool = true;
                }

            }
        }, 0, 1000);

        Timer ti = new Timer();

        ti.scheduleAtFixedRate(new TimerTask() {
            @Override
//run() reads the sensor data, applies the appropriate status and if necassary updates the server
            public void run() {
                if (offlineBool && !poemChangeChecker.equals(poem.getText().toString())) {
                    if (!typing) {
                        addRecordingsInline();
                        findHeadingsBoldItalics(true);
                        poemChangeChecker = poem.getText().toString();
                    }
                }
            }
        }, 0, 5000);
    }


    void getWordInfo(String selection) {
        final EditText inputWord = (EditText) findViewById(R.id.inputWord);
        final TextView wordInfo = (TextView) findViewById(R.id.wordInfo);

        searchWord = inputWord.getText().toString().trim().replace("'", "");
        String[] input = {searchWord, selection};
        pl.droidsonroids.gif.GifImageView loading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView2);
        wordInfo.setText("");
        loading.setVisibility(View.VISIBLE);

        new AsyncWordInfo().execute(input);
    }

    void getMeasureSyllables() {
        if (!processing) {
            processing = true;
            final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cornerLoader.setVisibility(View.VISIBLE);
                }
            });

            ArrayList<TextView> keys = new ArrayList<>(textviewsToChange.keySet());
            TextView[] keysArray = new TextView[keys.size()];
            keysArray = keys.toArray(keysArray);
            AsyncMeasureSyllableCountThread = new AsyncMeasureSyllableCount();
            AsyncMeasureSyllableCountThread.execute(keysArray);


        }

    }


    void getSyllablesAndRhymes(EditText poem) {

        String poemString = poem.getText().toString().toLowerCase() + "      ";

        if (!processing) {
            processing = true;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    EditText poem = (EditText) findViewById(R.id.poem);
////                    poem.setFocusable(false);
////                    poem.setFocusableInTouchMode(false);
//                }
//            });

            //clear the current words
            currentWordIndexes.clear();
            //split the poem into lines
            String fullPoem = poem.getText().toString();
            String[] webPoemLines = fullPoem.split("\n");
            //if poem is empty, pass in a space only otherwise remove all nonalphanumeric characters to repopulate currentwordindexes and pass in webpoemlines to asyncsyllablecount
            if (poem.getText().toString().trim().length() == 0) {
                String[] emptyPoem = {" "};
                AsyncSyllableCountThread = new AsyncSyllableCount();
                AsyncSyllableCountThread.execute(emptyPoem);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
                        cornerLoader.postDelayed(new Runnable() {
                            public void run() {
                                cornerLoader.setVisibility(View.GONE);
                            }
                        }, 500);
                    }
                });
            } else {
                String wordsString = poem.getText().toString().replaceAll("[^a-zA-Z0-9']+", " ");
                wordsString = wordsString.trim().replaceAll(" +", " ");
                String[] words;
                if (!wordsString.equals("")) {
                    words = wordsString.split(" ");
                } else {
                    words = null;
                }
                if (words != null) {
                    for (String word : words) {
                        try {
                            int index = poem.getText().toString().indexOf(word);

                            while (index >= 0 && (index + word.length() + 1) < poem.getText().toString().length()) {
                                boolean goodToGoR = false;
                                //make sure the word isn't inside another word.

                                if (index == 0) {
                                    if (!poemString.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                        goodToGoR = true;
                                    }
                                } else {
                                    // FIXX java.lang.StringIndexOutOfBoundsException: length=0; regionStart=0; regionLength=1
                                    if (!poemString.substring(index - 1, index).matches("[a-zA-Z0-9']+")) {
                                        if (!poemString.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                            goodToGoR = true;
                                        }
                                    }


                                }
                                if (goodToGoR) {
                                    currentWordIndexes.put(index, word.toLowerCase());
                                }
                                index = poemString.indexOf(word, index + 1);
                            }
                        } catch (IndexOutOfBoundsException e) {

                        }
                    }
                    currentLastWordIndexes.clear();
                    if (lastWords) {

                        int pastIndexes = 0;
                        String[] lines = poem.getText().toString().split("[\\r\\n]");
                        for (String line : lines) {
                            String newLine = line.trim().replaceAll(" +", " ");
                            String[] lineWords = newLine.split(" ");
                            String theLastWord = lineWords[lineWords.length - 1].trim().replaceAll("[^a-zA-Z0-9']+", "");

                            currentLastWordIndexes.put(line.lastIndexOf(theLastWord) + pastIndexes, theLastWord);

                            pastIndexes += line.length() + 1;

                        }

                    }
                    final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cornerLoader.setVisibility(View.VISIBLE);
                        }
                    });
                    AsyncSyllableCountThread = new AsyncSyllableCount();
                    AsyncSyllableCountThread.execute(webPoemLines);
                }
            }

        }
    }

    //check if user has internet access

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class AsyncWordInfo extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... input) {
            return GetWordData(input[0], input[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            dropdownOutput = (TextView) findViewById(R.id.wordInfo);
            if (result.equals("\n\n") || result.equals("")) {
                dropdownOutput.setText("Sorry. No " + spinnerSelection.substring(5) + " for \"" + searchWord.toLowerCase() + "\".");
            } else {
                dropdownOutput.setText(spinnerSelection.substring(5) + " for \"" + searchWord + "\":" + result + "\nPowered by Datamuse API.\n");
            }

            pl.droidsonroids.gif.GifImageView loading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView2);
            loading.setVisibility(View.GONE);

        }
    }

    private String GetWordData(String input, String dropdownSelection) {
        try {
            String fetchedJSON = "";
            URL url;

            if (dropdownSelection.equals("Find Synonyms")) {
                url = new URL("http://api.datamuse.com/words?rel_syn=" + input);
            } else if (dropdownSelection.equals("Find Exact Rhymes")) {
                url = new URL("http://api.datamuse.com/words?rel_rhy=" + input);
            } else if (dropdownSelection.equals("Find Near Rhymes")) {
                url = new URL("http://api.datamuse.com/words?rel_nry=" + input);
            } else if (dropdownSelection.equals("Find Definitions")) {
                url = new URL("https://api.datamuse.com/words?sp=" + input + "&md=d&max=1");
            } else {
                return "";
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                fetchedJSON += str;
            }
            in.close();
            if (dropdownSelection.equals("Find Synonyms")) {
                String synonyms = "";
                String[] seperateJSONs = fetchedJSON.split(",\\{");
                if (!fetchedJSON.equals("[]")) {
                    boolean firstEntry = true;
                    for (String JSON : seperateJSONs) {
                        if (JSON.contains("word\":\"")) {
                            if ((JSON.length() > JSON.indexOf("word") + 8) && (JSON.indexOf(",") > JSON.indexOf("word") + 8)) {
                                String synonym = JSON.substring(JSON.indexOf("word") + 7, JSON.indexOf(",") - 1).toLowerCase();

                                if (firstEntry) {
                                    synonyms += synonym;
                                    firstEntry = false;
                                } else {
                                    synonyms = synonyms + ", " + synonym;
                                }

                            }
                        }

                    }
                }

                return "\n" + synonyms + "\n";
            } else if (dropdownSelection.equals("Find Exact Rhymes")) {
                String rhymes = "";
                String multipleWords = "";
                String[] seperateJSONs = fetchedJSON.split(",\\{");
                if (!fetchedJSON.equals("[]")) {
                    boolean firstEntry = true;
                    boolean firstMulti = true;
                    for (String JSON : seperateJSONs) {
                        if (JSON.contains("word\":\"")) {
                            if ((JSON.length() > JSON.indexOf("word") + 8) && (JSON.indexOf(",") > JSON.indexOf("word") + 8)) {
                                String rhyme = JSON.substring(JSON.indexOf("word") + 7, JSON.indexOf(",") - 1).toLowerCase();
                                if (firstEntry) {
                                    rhymes += rhyme;
                                    firstEntry = false;
                                } else {
                                    rhymes = rhymes + ", " + rhyme;
                                }
                            }
                        }
                    }

                }
                return "\n" + rhymes + "\n";
            } else if (dropdownSelection.equals("Find Near Rhymes")) {
                String nRhymes = "";
                String[] seperateJSONs = fetchedJSON.split(",\\{");
                if (!fetchedJSON.equals("[]")) {
                    boolean firstEntry = true;
                    for (String JSON : seperateJSONs) {
                        if (JSON.contains("word\":\"")) {
                            if ((JSON.length() > JSON.indexOf("word") + 8) && (JSON.indexOf(",") > JSON.indexOf("word") + 8)) {
                                String nRhyme = JSON.substring(JSON.indexOf("word") + 7, JSON.indexOf(",") - 1).toLowerCase();

                                if (firstEntry) {
                                    nRhymes += nRhyme;
                                    firstEntry = false;
                                } else {
                                    nRhymes = nRhymes + ", " + nRhyme;
                                }
                            }
                        }
                    }

                }
                return "\n" + nRhymes + "\n";
            } else if (dropdownSelection.equals("Find Definitions")) {
                if (!fetchedJSON.equals("[]") && fetchedJSON.contains("defs")) {
                    String output = fetchedJSON.substring(fetchedJSON.lastIndexOf("defs") + 8, fetchedJSON.indexOf("]") - 1).replace("\\t", ") ").replace("\",\"", "\n");
                    return "\n" + output + "\n";
                }
            }

        } catch (IOException e) {
            System.out.println("IO exception occurred");
        }

        return "";
    }

    void startMetronome(Integer bpm, final Integer accent) {
        metronomeRunning = true;
        globalAccentCounter = 0;
        globalMeasureCounter = 0;
        globalScrollCounter = 0;
        globalNoteCounter = 0;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Timer t = new Timer();
        Timer m = new Timer();
        if (bpm > 0) {
            final Integer milli = 60000 / bpm;
            final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
            final ArrayList<TextView> allMeasureLayouts = new ArrayList<>();
            final ArrayList<LinearLayout> allMeasureScrollLayouts = new ArrayList<>();
            final ArrayList<LinearLayout> allMeasureEditTexts = new ArrayList<>();
            if (scrollMetronome) {
                for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                        i++;
                    }
                    i++;
                    for (int a = 0; a < ((LinearLayout) measureModeLayout.getChildAt(i)).getChildCount(); a++) {
                        allMeasureScrollLayouts.add((LinearLayout) measureModeLayout.getChildAt(i));
                    }
                }
            }
            if (noteMetronome) {
                for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                        i++;
                    }
                    allMeasureEditTexts.add((LinearLayout) measureModeLayout.getChildAt(i));
                    i++;
                }
                if (globalNoteCounter < allMeasureEditTexts.size()) {
                    Timer b = new Timer();
                    b.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (metronomeRunning) {
                                if (globalNoteCounter < allMeasureEditTexts.size()) {

                                    final LinearLayout barsToPlay;
                                    String[] sixteenthsTemp = new String[16];
                                    for (int s = 0; s < sixteenthsTemp.length; s++) {
                                        sixteenthsTemp[s] = "";
                                    }
                                    for (int b = 0; b < allMeasureEditTexts.size(); b++) {

                                        Log.d("errocat", "running" + (measureModeLayout.indexOfChild(allMeasureEditTexts.get(b))));
                                    }

                                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(allMeasureEditTexts.get(globalNoteCounter)) - 1))) {

//                                        Log.d("errocat", "running"+allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(allMeasureEditTexts.get(globalNoteCounter)) - 1))+globalNoteCounter);
                                        barsToPlay = ((LinearLayout) measureModeLayout.getChildAt(measureModeLayout.indexOfChild(allMeasureEditTexts.get(globalNoteCounter)) - 1));
                                        if (!allBars.get(barsToPlay).replaceAll("<sixteenth>", "").replaceAll("</sixteenth>", "").equals("")) {
                                            sixteenthsTemp = allBars.get(barsToPlay).split("</sixteenth>");
                                            for (int i = 0; i < sixteenthsTemp.length; i++) {
                                                sixteenthsTemp[i] = sixteenthsTemp[i].replace("<sixteenth>", "");
                                            }
                                        }
//                                        Log.d("errocat", "running"+sixteenthsTemp[0]);
                                    }
                                    final String[] sixteenths = sixteenthsTemp;

                                    Timer n = new Timer();
                                    n.scheduleAtFixedRate(new TimerTask() {
                                        int i = 0;

                                        @Override
                                        //run() reads the sensor data, applies the appropriate status and if necassary updates the server
                                        public void run() {
                                            if (i < sixteenths.length && metronomeRunning) {
                                                if (sixteenths[i].contains("_")) {
                                                    String noteLabel = "measure_bars_pitch_" + sixteenths[i].substring(ordinalIndexOf(sixteenths[i], "_", 1) + 1, ordinalIndexOf(sixteenths[i], "_", 2));
                                                    TextView noteView = (TextView) findViewById(getResources().getIdentifier(noteLabel, "id", getPackageName()));
                                                    MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(noteView.getText().toString().toLowerCase(), "raw", getPackageName()));
                                                    note.start();
                                                    note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        public void onCompletion(MediaPlayer note) {
                                                            note.release();
                                                        }
                                                    });
                                                }
                                                i++;
                                            } else {
                                                this.cancel();
                                            }
                                        }
                                    }, 0, (milli * accent) / 16);

                                    globalNoteCounter++;
                                    if (globalNoteCounter >= allMeasureEditTexts.size()) {
                                        globalNoteCounter = 0;
                                    }
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }, 0, (milli * accent));
                }
            }
            if (measureMetronome) {
                for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                    if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                        i++;
                    }
                    i++;
                    for (int a = 0; a < ((LinearLayout) measureModeLayout.getChildAt(i)).getChildCount(); a++) {
                        allMeasureLayouts.add((TextView) ((LinearLayout) measureModeLayout.getChildAt(i)).getChildAt(a));
                    }
                }

                m.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    //run() reads the sensor data, applies the appropriate status and if necassary updates the server
                    public void run() {

                        if (metronomeRunning) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (globalMeasureCounter < allMeasureLayouts.size()) {



                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                            (allMeasureLayouts.get(globalMeasureCounter)).setBackgroundResource(R.drawable.measure_transition_middle_darkmode);
                                            (previousTextView).setBackgroundResource(R.drawable.measure_transition_end_darkmode);
                                        } else {
                                            (allMeasureLayouts.get(globalMeasureCounter)).setBackgroundResource(R.drawable.measure_transition_middle);
                                            (previousTextView).setBackgroundResource(R.drawable.measure_transition_end);
                                        }

                                        TransitionDrawable transition = (TransitionDrawable) (allMeasureLayouts.get(globalMeasureCounter)).getBackground();
                                        transition.startTransition((milli * accent) / 4);

                                        TransitionDrawable transition2 = (TransitionDrawable) (previousTextView).getBackground();
                                        transition2.startTransition((milli * accent) / 4);

                                        previousPreviousTextView.setBackground(null);
                                        previousPreviousTextView = (previousTextView);
                                        previousTextView = (allMeasureLayouts.get(globalMeasureCounter));


                                        globalMeasureCounter++;
                                        if (globalMeasureCounter >= allMeasureLayouts.size()) {
                                            globalMeasureCounter = 0;
                                        }

                                    }
                                }
                            });
                        } else {
                            this.cancel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < allMeasureLayouts.size(); i++) {
                                        allMeasureLayouts.get(i).setBackground(null);
                                    }
                                }
                            });
                        }
                    }
                }, 0, (milli * accent) / 4);
            }
            final AnimatorSet scrollHelperSet = new AnimatorSet();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                //run() reads the sensor data, applies the appropriate status and if necassary updates the server
                public void run() {
                    if (metronomeRunning) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                previousRow.setBackground(null);
                                previousSylRow.setBackground(null);
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
                                }
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MediaPlayer ring;
                                if (globalAccentCounter == 0) {
                                    ring = MediaPlayer.create(MainActivity.this, R.raw.first);
                                    if (backgroundMetronome) {


                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                            findViewById(R.id.main_activity).setBackgroundResource(R.drawable.transition_drawable_first_darkmode);
                                        } else {
                                            findViewById(R.id.main_activity).setBackgroundResource(R.drawable.transition_drawable_first);
                                        }
                                        TransitionDrawable transition = (TransitionDrawable) findViewById(R.id.main_activity).getBackground();
                                        transition.startTransition(0);
                                        transition.reverseTransition((int) (milli * .5));
                                    }
                                } else {
                                    ring = MediaPlayer.create(MainActivity.this, R.raw.metronome);
                                    if (backgroundMetronome) {


                                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
                                            findViewById(R.id.main_activity).setBackgroundResource(R.drawable.transition_drawable_darkmode);
                                        } else {

                                            findViewById(R.id.main_activity).setBackgroundResource(R.drawable.transition_drawable);
                                        }
                                        TransitionDrawable transition = (TransitionDrawable) findViewById(R.id.main_activity).getBackground();
                                        transition.startTransition(0);
                                        transition.reverseTransition((int) (milli * .5));
                                    }
                                }
                                globalAccentCounter++;
                                if (globalAccentCounter >= accent) {
                                    globalAccentCounter = 0;
                                }
                                if (!noteMetronome) {
                                    ring.start();
                                    ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener()

                                    {
                                        public void onCompletion(MediaPlayer ring) {
                                            ring.release();
                                        }
                                    });
                                }
                                ImageButton metronome = (ImageButton) findViewById(R.id.metronome);
                                if (flipped) {
                                    metronome.setImageResource(R.drawable.metronome);
                                    flipped = false;
                                } else {
                                    metronome.setImageResource(R.drawable.metronome_flipped);
                                    flipped = true;
                                }
                                if (scrollMetronome) {
                                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView3);
                                    if (globalScrollCounter < (allMeasureScrollLayouts.size() * accent) / 4) {

                                        //scrolls the entire scrollview smoothly ATTEMPT
//                                        if (globalScrollCounter == 0) {
//                                            scrollHelperSet.cancel();
//                                            ObjectAnimator scrollHelperTop = ObjectAnimator.ofInt(scrollView, "scrollY", 0).setDuration(0);
//                                            scrollHelperSet.play(scrollHelperTop);
//                                            scrollHelperSet.start();
//                                            scrollHelperSet.cancel();
//                                            ObjectAnimator scrollHelper = ObjectAnimator.ofInt(scrollView, "scrollY", (int) (measureModeLayout.getHeight() - (scrollView.getHeight() * .75))).setDuration(milli *(allMeasureScrollLayouts.size()/4)*accent);
//                                            scrollHelperSet.play(scrollHelper);
//                                            scrollHelperSet.start();
//                                        }

                                        // scrolls to each measure smoothly ATTEMPT
//                                        if (globalScrollCounter == 0) {
//                                            scrollHelperSet.cancel();
//                                            ObjectAnimator scrollHelperTop = ObjectAnimator.ofInt(scrollView, "scrollY", 0).setDuration(0);
//                                            scrollHelperSet.play(scrollHelperTop);
//                                            scrollHelperSet.start();
//                                        } else if (globalScrollCounter % accent == 0) {
//                                            scrollHelperSet.cancel();
//                                            scrollView.scrollTo(0,allMeasureScrollLayouts.get((globalScrollCounter * 4 / accent)-accent).getBottom());
//                                            ObjectAnimator scrollHelper = ObjectAnimator.ofInt(scrollView, "scrollY", allMeasureScrollLayouts.get(globalScrollCounter * 4 / accent).getBottom() - (scrollView.getHeight() / 3)).setDuration(milli * accent);
//                                            scrollHelperSet.play(scrollHelper);
//                                            scrollHelperSet.start();
//                                        }

                                        if (globalScrollCounter % accent == 0) {
                                            scrollView.smoothScrollTo(0, allMeasureScrollLayouts.get(globalScrollCounter * 4 / accent).getBottom() - (scrollView.getHeight() / 3));
                                        }
                                        globalScrollCounter++;
                                        if (globalScrollCounter >= (allMeasureScrollLayouts.size() / 4) * accent) {
                                            globalScrollCounter = 0;
                                        }

                                    }
                                }
                            }
                        });
                    } else {
                        this.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView3);
                                scrollHelperSet.cancel();
//                                scrollHelper.ofInt(scrollView, "scrollY", 0).setDuration(0).start();
                                previousTextView.setBackground(null);
                                previousTextView = new TextView(MainActivity.this);
                                previousPreviousTextView.setBackground(null);
                                previousPreviousTextView = new TextView(MainActivity.this);
                                if (barsCurrentlySelected) {
                                    for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                        selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                    }
                                } else {
                                    previousRow.setBackgroundColor(argb(50, 200, 200, 200));
                                    previousSylRow.setBackgroundColor(argb(50, 200, 200, 200));
                                }
                            }
                        });
                    }
                }
            }, 0, milli);
        }
        return;
    }

    private String removeIllegalChar(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!(String.valueOf(str.charAt(i)).matches("[a-zA-Z0-9-_]*"))) {
                //as the callback is called for each character entered, we can return on first non-match
                //maybe show a short toast
                Toast toast = Toast.makeText(mainActivityHelper, "Not a valid character",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return str.substring(0, i) + str.substring(i + 1);
            }
        }
        return str;
    }

    void pulse(final ImageButton button, boolean start) {
        //cancel any pulses, then start a pulse for 10 seconds if true.
        spinnerCount = 0;
        pulseTimer.cancel();
        final ImageButton barsToolbarButton = (ImageButton) findViewById(R.id.go_note);
        final ImageButton spinnerToolbarButton = (ImageButton) findViewById(R.id.search);
        final ImageButton measureToolbarButton = (ImageButton) findViewById(R.id.go_measure);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (colorTheme.equals("royal")) {
                    barsToolbarButton.setBackgroundResource(R.drawable.left_border_search);
                    spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search);
                    measureToolbarButton.setBackgroundResource(R.drawable.left_border_search);
                } else if (colorTheme.equals("sunset")) {
                    barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
                    spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
                    measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
                } else if (colorTheme.equals("joy")) {
                    barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
                    spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
                    measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
                } else if (colorTheme.equals("dark")) {
                    barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
                    spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
                    measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
                }

            }
        });
        if (start) {
            pulseTimer = new Timer();
            pulseTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (spinnerCount < 10) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (colorTheme.equals("royal")) {
                                    button.setBackgroundResource(R.drawable.transition_pulse_purple);
                                } else if (colorTheme.equals("sunset")) {
                                    button.setBackgroundResource(R.drawable.transition_pulse_orange);
                                } else if (colorTheme.equals("joy")) {
                                    button.setBackgroundResource(R.drawable.transition_pulse_blue);
                                } else if (colorTheme.equals("dark")) {
                                    button.setBackgroundResource(R.drawable.transition_pulse_black);
                                }

                                final TransitionDrawable transition = (TransitionDrawable) button.getBackground();
                                transition.startTransition(1000);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        transition.reverseTransition(1000);
                                    }

                                }, 1000);
                            }
                        });
                        spinnerCount++;
                    } else {
                        pulseTimer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (colorTheme.equals("royal")) {
                                    button.setBackgroundResource(R.drawable.left_border_search);
                                } else if (colorTheme.equals("sunset")) {
                                    button.setBackgroundResource(R.drawable.left_border_search_orange);
                                } else if (colorTheme.equals("joy")) {
                                    button.setBackgroundResource(R.drawable.left_border_search_blue);
                                } else if (colorTheme.equals("dark")) {
                                    button.setBackgroundResource(R.drawable.left_border_search_black);
                                }
                            }
                        });
                    }
                }
            }, 0, 2000);

        }

    }

    void changeFontFamily(String savedTypeface){
        final ImageButton sourceSansProSelectBox = (ImageButton) findViewById(R.id.checkbox_sourcesanspro);
        final ImageButton markaziSelectBox = (ImageButton) findViewById(R.id.checkbox_markazi);
        final ImageButton slaboSelectBox = (ImageButton) findViewById(R.id.checkbox_slabo);
        final ImageButton ubuntuSelectBox = (ImageButton) findViewById(R.id.checkbox_ubuntu);
        final ImageButton patrickHandSelectBox = (ImageButton) findViewById(R.id.checkbox_patrickhand);

        if (savedTypeface.equals("MARKAZI")) {
            sourceSansProSelectBox.setImageResource(R.drawable.circle);
            markaziSelectBox.setImageResource(R.drawable.small_check);
            slaboSelectBox.setImageResource(R.drawable.circle);
            ubuntuSelectBox.setImageResource(R.drawable.circle);
            patrickHandSelectBox.setImageResource(R.drawable.circle);
        } else if (savedTypeface.equals("SLABO")) {
            sourceSansProSelectBox.setImageResource(R.drawable.circle);
            markaziSelectBox.setImageResource(R.drawable.circle);
            slaboSelectBox.setImageResource(R.drawable.small_check);
            ubuntuSelectBox.setImageResource(R.drawable.circle);
            patrickHandSelectBox.setImageResource(R.drawable.circle);
        } else if (savedTypeface.equals("UBUNTU")) {
            sourceSansProSelectBox.setImageResource(R.drawable.circle);
            markaziSelectBox.setImageResource(R.drawable.circle);
            slaboSelectBox.setImageResource(R.drawable.circle);
            ubuntuSelectBox.setImageResource(R.drawable.small_check);
            patrickHandSelectBox.setImageResource(R.drawable.circle);
        } else if (savedTypeface.equals("PATRICKHAND")) {
            sourceSansProSelectBox.setImageResource(R.drawable.circle);
            markaziSelectBox.setImageResource(R.drawable.circle);
            slaboSelectBox.setImageResource(R.drawable.circle);
            ubuntuSelectBox.setImageResource(R.drawable.circle);
            patrickHandSelectBox.setImageResource(R.drawable.small_check);
        } else {
            sourceSansProSelectBox.setImageResource(R.drawable.small_check);
            markaziSelectBox.setImageResource(R.drawable.circle);
            slaboSelectBox.setImageResource(R.drawable.circle);
            ubuntuSelectBox.setImageResource(R.drawable.circle);
            patrickHandSelectBox.setImageResource(R.drawable.circle);
        }

        if(savedTypeface.equals("PATRICKHAND")){
            typeface = Typeface.createFromAsset(getAssets(), "fonts/PatrickHand-Regular.ttf");
        }else if(savedTypeface.equals("MARKAZI")){
            typeface = Typeface.createFromAsset(getAssets(), "fonts/MarkaziText-Regular.ttf");
        }else if(savedTypeface.equals("SLABO")){
            typeface = Typeface.createFromAsset(getAssets(), "fonts/Slabo27px-Regular.ttf");
        }else if(savedTypeface.equals("UBUNTU")){
            typeface = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Regular.ttf");
        }else{
            typeface = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        }

        final EditText inputWord = (EditText) findViewById(R.id.inputWord);
        final TextView wordInfo = (TextView) findViewById(R.id.wordInfo);
        final TextView syllables = (TextView) findViewById(R.id.syllables);
        final EditText poem = (EditText) findViewById(R.id.poem);
        final EditText notes = (EditText) findViewById(R.id.notes);
        final EditText test = (EditText) findViewById(R.id.poemtest);
        final TextView hardSave = (TextView) findViewById(R.id.hardsave);
        final TextView rhymeFeatures = (TextView) findViewById(R.id.rhyme_features);
        final TextView makeADonation = (TextView) findViewById(R.id.donate);
        final TextView tutorial = (TextView) findViewById(R.id.tutorial);
        final TextView upgrade = (TextView) findViewById(R.id.upgrade);
        final TextView generalSettings = (TextView) findViewById(R.id.general_settings);
        final TextView barsToolbarLengthText = (TextView) findViewById(R.id.select_note_length);
        final TextView barsToolbarPitchText = (TextView) findViewById(R.id.select_note_pitch);
        final TextView options = (TextView) findViewById(R.id.optionsSpinner);
        final TextView measureToolbarText = (TextView) findViewById(R.id.select_words_measure);
        final TextView exactRhymesTV = (TextView) findViewById(R.id.exact_rhymes);
        final TextView nearRhymesTV = (TextView) findViewById(R.id.near_rhymes);
        final TextView synonymsTV = (TextView) findViewById(R.id.synonyms);
        final TextView definitionsTV = (TextView) findViewById(R.id.definitions);
        final TextView addNewMeasureTV = (TextView) findViewById(R.id.add_new_measure);
        final TextView addBarsToMeasureTV = (TextView) findViewById(R.id.add_bars_to_measure);
        final TextView playBarsMeasureTV = (TextView) findViewById(R.id.play_measure_bars);
        final TextView scrambleSelectedMeasureTV = (TextView) findViewById(R.id.scramble_selected_measure);
        final TextView deleteSelectedMeasureTV = (TextView) findViewById(R.id.delete_selected_measure);
        final TextView deleteSelectedMeasureNotesTV = (TextView) findViewById(R.id.delete_selected_measure_notes);
        final TextView pitch1TV = (TextView) findViewById(R.id.measure_bars_pitch_1);
        final TextView pitch2TV = (TextView) findViewById(R.id.measure_bars_pitch_2);
        final TextView pitch3TV = (TextView) findViewById(R.id.measure_bars_pitch_3);
        final TextView pitch4TV = (TextView) findViewById(R.id.measure_bars_pitch_4);
        final TextView pitch5TV = (TextView) findViewById(R.id.measure_bars_pitch_5);
        final TextView pitch6TV = (TextView) findViewById(R.id.measure_bars_pitch_6);
        final TextView pitch7TV = (TextView) findViewById(R.id.measure_bars_pitch_7);
        final TextView pitch8TV = (TextView) findViewById(R.id.measure_bars_pitch_8);
        final TextView pitch9TV = (TextView) findViewById(R.id.measure_bars_pitch_9);
        final TextView pitch10TV = (TextView) findViewById(R.id.measure_bars_pitch_10);
        final TextView pitch11TV = (TextView) findViewById(R.id.measure_bars_pitch_11);
        final TextView pitch12TV = (TextView) findViewById(R.id.measure_bars_pitch_12);
        final TextView pitch13TV = (TextView) findViewById(R.id.measure_bars_pitch_13);
        final TextView lengthWholeTV = (TextView) findViewById(R.id.measure_bars_length_whole);
        final TextView lengthHalfTV = (TextView) findViewById(R.id.measure_bars_length_half);
        final TextView lengthQuarterTV = (TextView) findViewById(R.id.measure_bars_length_quarter);
        final TextView lengthEighthTV = (TextView) findViewById(R.id.measure_bars_length_eighth);
        final TextView lengthSixteenthTV = (TextView) findViewById(R.id.measure_bars_length_sixteenth);
        final TextView lengthRemoveTV = (TextView) findViewById(R.id.measure_bars_length_remove);



        exactRhymesTV.setTypeface(typeface);
        nearRhymesTV.setTypeface(typeface);
        synonymsTV.setTypeface(typeface);
        definitionsTV.setTypeface(typeface);
        addNewMeasureTV.setTypeface(typeface);
        addBarsToMeasureTV.setTypeface(typeface);
        playBarsMeasureTV.setTypeface(typeface);
        scrambleSelectedMeasureTV.setTypeface(typeface);
        deleteSelectedMeasureTV.setTypeface(typeface);
        deleteSelectedMeasureNotesTV.setTypeface(typeface);
        options.setTypeface(typeface);
        measureToolbarText.setTypeface(typeface);
        pitch1TV.setTypeface(typeface);
        pitch2TV.setTypeface(typeface);
        pitch3TV.setTypeface(typeface);
        pitch4TV.setTypeface(typeface);
        pitch5TV.setTypeface(typeface);
        pitch6TV.setTypeface(typeface);
        pitch7TV.setTypeface(typeface);
        pitch8TV.setTypeface(typeface);
        pitch9TV.setTypeface(typeface);
        pitch10TV.setTypeface(typeface);
        pitch11TV.setTypeface(typeface);
        pitch12TV.setTypeface(typeface);
        pitch13TV.setTypeface(typeface);
        lengthWholeTV.setTypeface(typeface);
        lengthHalfTV.setTypeface(typeface);
        lengthQuarterTV.setTypeface(typeface);
        lengthEighthTV.setTypeface(typeface);
        lengthSixteenthTV.setTypeface(typeface);
        lengthRemoveTV.setTypeface(typeface);
        barsToolbarLengthText.setTypeface(typeface);
        barsToolbarPitchText.setTypeface(typeface);
//        changeMode.setTypeface(typeface);
        rhymeFeatures.setTypeface(typeface);
//        startTrial.setTypeface(typeface);
        hardSave.setTypeface(typeface);
        generalSettings.setTypeface(typeface);
        makeADonation.setTypeface(typeface);
        tutorial.setTypeface(typeface);
        upgrade.setTypeface(typeface);
//        share.setTypeface(typeface);
        poem.setTypeface(typeface);
        test.setTypeface(typeface);
        syllables.setTypeface(typeface);
        notes.setTypeface(typeface);
        wordInfo.setTypeface(typeface);
        wordInfo.setTypeface(typeface);
        inputWord.setTypeface(typeface);
        final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
        for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
            if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                continue;
            }
            LinearLayout row1 = (LinearLayout) measureModeLayout.getChildAt(i);
            i++;
            LinearLayout row2 = (LinearLayout) measureModeLayout.getChildAt(i);
            EditText col1 = (EditText) row1.getChildAt(0);
            EditText col2 = (EditText) row1.getChildAt(1);
            EditText col3 = (EditText) row1.getChildAt(2);
            EditText col4 = (EditText) row1.getChildAt(3);
            TextView sylCol1 = (TextView) row2.getChildAt(0);
            TextView sylCol2 = (TextView) row2.getChildAt(1);
            TextView sylCol3 = (TextView) row2.getChildAt(2);
            TextView sylCol4 = (TextView) row2.getChildAt(3);
            col1.setTypeface(typeface);
            col2.setTypeface(typeface);
            col3.setTypeface(typeface);
            col4.setTypeface(typeface);
            sylCol1.setTypeface(typeface);
            sylCol2.setTypeface(typeface);
            sylCol3.setTypeface(typeface);
            sylCol4.setTypeface(typeface);
        }
    }

    void changeColors(final String color) {
        final Toolbar topToolbar = (Toolbar) findViewById(R.id.toolbar);
        final Toolbar spinnerToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
        final Toolbar measureToolbar = (Toolbar) findViewById(R.id.measure_toolbar);
        final Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
        final ImageButton barsToolbarButton = (ImageButton) findViewById(R.id.go_note);
        final ImageButton spinnerToolbarButton = (ImageButton) findViewById(R.id.search);
        final ImageButton measureToolbarButton = (ImageButton) findViewById(R.id.go_measure);
        final ImageView wordinfoBar = (ImageView) findViewById(R.id.wordinfo_bar);
        final ImageView notesBar = (ImageView) findViewById(R.id.notes_bar);
        final pl.droidsonroids.gif.GifImageView loading = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView2);
        final pl.droidsonroids.gif.GifImageView loading2 = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
        final TextView notesTitle = (TextView) findViewById(R.id.notes_title);
        final TextView wordInfoTitle = (TextView) findViewById(R.id.wordinfo_title);
        final ScrollView spinnerList = (ScrollView) findViewById(R.id.spinner_list_scroll);
        final ScrollView measureList = (ScrollView) findViewById(R.id.measure_list_scroll);
        final ScrollView lengthList = (ScrollView) findViewById(R.id.measure_bars_length_list_scroll);
        final ScrollView pitchList = (ScrollView) findViewById(R.id.measure_bars_pitch_list_scroll);
        final ScrollView settingList = (ScrollView) findViewById(R.id.settings_items_scroll);
        final EditText inputWord = (EditText) findViewById(R.id.inputWord);
        final TextView donateDone = (TextView) findViewById(R.id.donate_okay);
        final TextView changeColorButton = (TextView) findViewById(R.id.popup_done_changecolor);
        final TextView changeFontFamilyButton = (TextView) findViewById(R.id.popup_done_changefont);
        final TextView rhymeFeaturesButton = (TextView) findViewById(R.id.rhyme_features_done);
//        final TextView startTrialYes = (TextView) findViewById(R.id.trial_yes);
//        final TextView startTrialNo = (TextView) findViewById(R.id.trial_no);
        final TextView hardSaveYes = (TextView) findViewById(R.id.hardsave_yes);
        final TextView hardSaveNo = (TextView) findViewById(R.id.hardsave_no);
        final TextView englishFeaturesYes = (TextView) findViewById(R.id.popup_yes_english_features);
        final TextView englishFeaturesNo = (TextView) findViewById(R.id.popup_no_english_features);
        final TextView verifyYes = (TextView) findViewById(R.id.verify_yes);
        final TextView verifyNo = (TextView) findViewById(R.id.verify_no);
        final TextView reviseButton = (TextView) findViewById(R.id.popup_done_revision);
        final TextView fontButton = (TextView) findViewById(R.id.popup_done);
//        final TextView offlineButton = (TextView) findViewById(R.id.popup_okay);
        final TextView orientationNo = (TextView) findViewById(R.id.popup_no_orientation);
        final TextView orientationYes = (TextView) findViewById(R.id.popup_yes_orientation);
        final TextView darkmodeNo = (TextView) findViewById(R.id.popup_no_darkmode);
        final TextView darkmodeYes = (TextView) findViewById(R.id.popup_yes_darkmode);
//        final TextView hideToolbarNo = (TextView) findViewById(R.id.popup_no_hideToolbar);
//        final TextView hideToolbarYes = (TextView) findViewById(R.id.popup_yes_hideToolbar);
        final TextView ignoreButton = (TextView) findViewById(R.id.ignore_list_okay);
        final TextView createRhymesButton = (TextView) findViewById(R.id.create_rhymes_okay);
        final TextView generalSettingsButton = (TextView) findViewById(R.id.general_settings_done);
//        final TextView outoftimeOff = (TextView) findViewById(R.id.turnoff_outoftime);
        final TextView metronomeYes = (TextView) findViewById(R.id.metronome_done);
        final TextView metronomeNo = (TextView) findViewById(R.id.metronome_no);
        final TextView changeModeNo = (TextView) findViewById(R.id.popup_no_changemode);
        final TextView changeModeYes = (TextView) findViewById(R.id.popup_yes_changemode);
        final TextView hintButton = (TextView) findViewById(R.id.hint_okay);
//        final TextView shareButton = (TextView) findViewById(R.id.share_okay);
        final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
        final TextView upgradeNo = (TextView) findViewById(R.id.upgrade_no);
//        final TextView upgradeStartTrialButton = (TextView) findViewById(R.id.upgrade_start_trial);
        final TextView rateYes = (TextView) findViewById(R.id.rate_yes);
        final TextView rateNo = (TextView) findViewById(R.id.rate_no);
        final TextView deleteYes = (TextView) findViewById(R.id.popup_delete_yes);
        final TextView deleteNo = (TextView) findViewById(R.id.popup_delete_no);
        final TextView newRecordingOkay = (TextView) findViewById(R.id.new_recording_okay);
        final TextView editRecordingOkay = (TextView) findViewById(R.id.edit_recording_done);
        final TextView manageRecordingOkay = (TextView) findViewById(R.id.manage_recording_done);
        final TextView RecorderOkay = (TextView) findViewById(R.id.recorder_done);
        final TextView changeClefDone = (TextView) findViewById(R.id.change_clef_done);
        final ImageButton record = (ImageButton) findViewById(R.id.record);
        ImageView topToolbarToggler = (ImageView) findViewById(R.id.toggle_top_toolbar);
        ImageView bottomToolbarToggler = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
        ImageView undo = (ImageView) findViewById(R.id.undo);
        TextView add_new_measure = (TextView) findViewById(R.id.add_new_measure);
        TextView scramble_selected_measure = (TextView) findViewById(R.id.scramble_selected_measure);
        TextView delete_selected_measure = (TextView) findViewById(R.id.delete_selected_measure);
        ImageView add_new_measure_checkbox = (ImageView) findViewById(R.id.add_new_measure_checkbox);
        ImageView scramble_selected_measure_checkbox = (ImageView) findViewById(R.id.scramble_selected_measure_checkbox);
        ImageView delete_selected_measure_checkbox = (ImageView) findViewById(R.id.delete_selected_measure_checkbox);

        if (color.equals("royal")) {
            if (notesFocus) {
                wordinfoBar.setBackgroundResource(R.drawable.no_focus);
                notesBar.setBackgroundResource(R.drawable.focus);
                notesTitle.setBackgroundResource(R.drawable.tab_left_focus);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right);
            } else {
                wordinfoBar.setBackgroundResource(R.drawable.focus);
                notesBar.setBackgroundResource(R.drawable.no_focus);
                notesTitle.setBackgroundResource(R.drawable.tab_left);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus);
            }
            if (topToolbarVisible) {
                topToolbarToggler.setImageResource(R.drawable.openspinner_purple);
            } else {
                topToolbarToggler.setImageResource(R.drawable.closespinner_purple);
            }
            if (bottomToolbarVisible) {
                bottomToolbarToggler.setImageResource(R.drawable.closespinner_purple);
            } else {
                bottomToolbarToggler.setImageResource(R.drawable.openspinner_purple);
            }
            for (TextView tv : linesWithRecordings) {
                tv.setBackgroundResource(R.drawable.play_purple);
            }
            if (recorderRunning) {
                record.setImageResource(R.drawable.record_stop_purple);
            } else {
                record.setImageResource(R.drawable.record_purple);
            }
            add_new_measure.setBackgroundResource(R.drawable.bottom_border_purple);
            scramble_selected_measure.setBackgroundResource(R.drawable.bottom_border_purple);
            delete_selected_measure.setBackgroundResource(R.drawable.bottom_border_purple);
            add_new_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_purple);
            scramble_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_purple);
            delete_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_purple);
            undo.setImageResource(R.drawable.undo_purple);
            loading.setImageResource(R.drawable.loading_icon_color);
            loading2.setImageResource(R.drawable.loading_icon_color);
            popupBackground.setBackgroundResource(R.drawable.popup);
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color14));
            spinnerToolbar.setBackgroundColor(getResources().getColor(R.color.color1));
            measureToolbar.setBackgroundColor(getResources().getColor(R.color.color1));
            barsToolbar.setBackgroundColor(getResources().getColor(R.color.color1));
            barsToolbarButton.setBackgroundResource(R.drawable.left_border_search);
            inputWord.setBackgroundResource(R.drawable.inputword_border);
            measureToolbarButton.setBackgroundResource(R.drawable.left_border_search);
            spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search);
            spinnerList.setBackgroundColor(getResources().getColor(R.color.color14));
            measureList.setBackgroundColor(getResources().getColor(R.color.color14));
            pitchList.setBackgroundColor(getResources().getColor(R.color.color14));
            lengthList.setBackgroundColor(getResources().getColor(R.color.color14));
            settingList.setBackgroundColor(getResources().getColor(R.color.color14));
            changeColorButton.setTextColor(getResources().getColor(R.color.color1));
            changeFontFamilyButton.setTextColor(getResources().getColor(R.color.color1));
            rhymeFeaturesButton.setTextColor(getResources().getColor(R.color.color1));
//            startTrialYes.setTextColor(getResources().getColor(R.color.color1));
            hardSaveNo.setTextColor(getResources().getColor(R.color.color1));
            hardSaveYes.setTextColor(getResources().getColor(R.color.color1));
            englishFeaturesYes.setTextColor(getResources().getColor(R.color.color1));
            englishFeaturesNo.setTextColor(getResources().getColor(R.color.color1));
//            startTrialNo.setTextColor(getResources().getColor(R.color.color1));
            verifyYes.setTextColor(getResources().getColor(R.color.color1));
            verifyNo.setTextColor(getResources().getColor(R.color.color1));
            reviseButton.setTextColor(getResources().getColor(R.color.color1));
            fontButton.setTextColor(getResources().getColor(R.color.color1));
//            offlineButton.setTextColor(getResources().getColor(R.color.color1));
            orientationNo.setTextColor(getResources().getColor(R.color.color1));
            orientationYes.setTextColor(getResources().getColor(R.color.color1));
            darkmodeNo.setTextColor(getResources().getColor(R.color.color1));
            darkmodeYes.setTextColor(getResources().getColor(R.color.color1));

            deleteNo.setTextColor(getResources().getColor(R.color.color1));
            deleteYes.setTextColor(getResources().getColor(R.color.color1));
//            hideToolbarNo.setTextColor(getResources().getColor(R.color.color1));
//            hideToolbarYes.setTextColor(getResources().getColor(R.color.color1));
            ignoreButton.setTextColor(getResources().getColor(R.color.color1));
            createRhymesButton.setTextColor(getResources().getColor(R.color.color1));
            generalSettingsButton.setTextColor(getResources().getColor(R.color.color1));
//            outoftimeOff.setTextColor(getResources().getColor(R.color.color1));
            metronomeNo.setTextColor(getResources().getColor(R.color.color1));
            metronomeYes.setTextColor(getResources().getColor(R.color.color1));
            changeModeNo.setTextColor(getResources().getColor(R.color.color1));
            changeModeYes.setTextColor(getResources().getColor(R.color.color1));
            hintButton.setTextColor(getResources().getColor(R.color.color1));
//            shareButton.setTextColor(getResources().getColor(R.color.color1));
            upgradeYes.setTextColor(getResources().getColor(R.color.color1));
            upgradeNo.setTextColor(getResources().getColor(R.color.color1));
//            upgradeStartTrialButton.setTextColor(getResources().getColor(R.color.color1));
            rateYes.setTextColor(getResources().getColor(R.color.color1));
            rateNo.setTextColor(getResources().getColor(R.color.color1));
            newRecordingOkay.setTextColor(getResources().getColor(R.color.color1));
            editRecordingOkay.setTextColor(getResources().getColor(R.color.color1));
            manageRecordingOkay.setTextColor(getResources().getColor(R.color.color1));
            RecorderOkay.setTextColor(getResources().getColor(R.color.color1));
            changeClefDone.setTextColor(getResources().getColor(R.color.color1));
            donateDone.setTextColor(getResources().getColor(R.color.color1));
        } else if (color.equals("sunset")) {
            if (notesFocus) {
                wordinfoBar.setBackgroundResource(R.drawable.no_focus_orange);
                notesBar.setBackgroundResource(R.drawable.focus_orange);
                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_orange);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_orange);
            } else {
                wordinfoBar.setBackgroundResource(R.drawable.focus_orange);
                notesBar.setBackgroundResource(R.drawable.no_focus_orange);
                notesTitle.setBackgroundResource(R.drawable.tab_left_orange);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_orange);
            }
            if (topToolbarVisible) {
                topToolbarToggler.setImageResource(R.drawable.openspinner_orange);
            } else {
                topToolbarToggler.setImageResource(R.drawable.closespinner_orange);
            }
            if (bottomToolbarVisible) {
                bottomToolbarToggler.setImageResource(R.drawable.closespinner_orange);
            } else {
                bottomToolbarToggler.setImageResource(R.drawable.openspinner_orange);
            }
            for (TextView tv : linesWithRecordings) {
                tv.setBackgroundResource(R.drawable.play_orange);
            }
            if (recorderRunning) {
                record.setImageResource(R.drawable.record_stop_orange);
            } else {
                record.setImageResource(R.drawable.record_orange);
            }
            add_new_measure.setBackgroundResource(R.drawable.bottom_border_orange);
            scramble_selected_measure.setBackgroundResource(R.drawable.bottom_border_orange);
            delete_selected_measure.setBackgroundResource(R.drawable.bottom_border_orange);
            add_new_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_orange);
            scramble_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_orange);
            delete_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_orange);
            popupBackground.setBackgroundResource(R.drawable.popup_orange);
            undo.setImageResource(R.drawable.undo_orange);
            loading.setImageResource(R.drawable.loading_icon_color_orange);
            loading2.setImageResource(R.drawable.loading_icon_color_orange);
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color5));
            spinnerToolbar.setBackgroundColor(getResources().getColor(R.color.color4));
            inputWord.setBackgroundResource(R.drawable.inputword_border_orange);
            barsToolbar.setBackgroundColor(getResources().getColor(R.color.color4));
            barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
            measureToolbar.setBackgroundColor(getResources().getColor(R.color.color4));
            measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
            spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_orange);
            spinnerList.setBackgroundColor(getResources().getColor(R.color.color5));
            measureList.setBackgroundColor(getResources().getColor(R.color.color5));
            pitchList.setBackgroundColor(getResources().getColor(R.color.color5));
            lengthList.setBackgroundColor(getResources().getColor(R.color.color5));
            settingList.setBackgroundColor(getResources().getColor(R.color.color5));
            changeColorButton.setTextColor(getResources().getColor(R.color.color4));
            changeFontFamilyButton.setTextColor(getResources().getColor(R.color.color4));
            rhymeFeaturesButton.setTextColor(getResources().getColor(R.color.color4));
//            startTrialYes.setTextColor(getResources().getColor(R.color.color4));
            hardSaveNo.setTextColor(getResources().getColor(R.color.color4));
            hardSaveYes.setTextColor(getResources().getColor(R.color.color4));
            englishFeaturesYes.setTextColor(getResources().getColor(R.color.color4));
            englishFeaturesNo.setTextColor(getResources().getColor(R.color.color4));
//            startTrialNo.setTextColor(getResources().getColor(R.color.color4));
            verifyYes.setTextColor(getResources().getColor(R.color.color4));
            verifyNo.setTextColor(getResources().getColor(R.color.color4));
            deleteNo.setTextColor(getResources().getColor(R.color.color4));
            deleteYes.setTextColor(getResources().getColor(R.color.color4));
            reviseButton.setTextColor(getResources().getColor(R.color.color4));
            fontButton.setTextColor(getResources().getColor(R.color.color4));
//            offlineButton.setTextColor(getResources().getColor(R.color.color4));
            orientationNo.setTextColor(getResources().getColor(R.color.color4));
            orientationYes.setTextColor(getResources().getColor(R.color.color4));
            darkmodeNo.setTextColor(getResources().getColor(R.color.color4));
            darkmodeYes.setTextColor(getResources().getColor(R.color.color4));
//            hideToolbarNo.setTextColor(getResources().getColor(R.color.color4));
//            hideToolbarYes.setTextColor(getResources().getColor(R.color.color4));
            ignoreButton.setTextColor(getResources().getColor(R.color.color4));
            createRhymesButton.setTextColor(getResources().getColor(R.color.color4));
            generalSettingsButton.setTextColor(getResources().getColor(R.color.color4));
//            outoftimeOff.setTextColor(getResources().getColor(R.color.color4));
            metronomeNo.setTextColor(getResources().getColor(R.color.color4));
            metronomeYes.setTextColor(getResources().getColor(R.color.color4));
            changeModeNo.setTextColor(getResources().getColor(R.color.color4));
            changeModeYes.setTextColor(getResources().getColor(R.color.color4));
            hintButton.setTextColor(getResources().getColor(R.color.color4));
//            shareButton.setTextColor(getResources().getColor(R.color.color4));
            upgradeYes.setTextColor(getResources().getColor(R.color.color4));
            upgradeNo.setTextColor(getResources().getColor(R.color.color4));
//            upgradeStartTrialButton.setTextColor(getResources().getColor(R.color.color4));
            rateYes.setTextColor(getResources().getColor(R.color.color4));
            rateNo.setTextColor(getResources().getColor(R.color.color4));
            newRecordingOkay.setTextColor(getResources().getColor(R.color.color4));
            editRecordingOkay.setTextColor(getResources().getColor(R.color.color4));
            manageRecordingOkay.setTextColor(getResources().getColor(R.color.color4));
            RecorderOkay.setTextColor(getResources().getColor(R.color.color4));
            changeClefDone.setTextColor(getResources().getColor(R.color.color4));
            donateDone.setTextColor(getResources().getColor(R.color.color4));
        } else if (color.equals("joy")) {
            if (notesFocus) {
                wordinfoBar.setBackgroundResource(R.drawable.no_focus_blue);
                notesBar.setBackgroundResource(R.drawable.focus_blue);
                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_blue);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_blue);
            } else {
                wordinfoBar.setBackgroundResource(R.drawable.focus_blue);
                notesBar.setBackgroundResource(R.drawable.no_focus_blue);
                notesTitle.setBackgroundResource(R.drawable.tab_left_blue);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_blue);
            }
            if (topToolbarVisible) {
                topToolbarToggler.setImageResource(R.drawable.openspinner_blue);
            } else {
                topToolbarToggler.setImageResource(R.drawable.closespinner_blue);
            }
            if (bottomToolbarVisible) {
                bottomToolbarToggler.setImageResource(R.drawable.closespinner_blue);
            } else {
                bottomToolbarToggler.setImageResource(R.drawable.openspinner_blue);
            }
            for (TextView tv : linesWithRecordings) {
                tv.setBackgroundResource(R.drawable.play_blue);
            }
            if (recorderRunning) {
                record.setImageResource(R.drawable.record_stop_blue);
            } else {
                record.setImageResource(R.drawable.record_blue);
            }
            add_new_measure.setBackgroundResource(R.drawable.bottom_border_blue);
            scramble_selected_measure.setBackgroundResource(R.drawable.bottom_border_blue);
            delete_selected_measure.setBackgroundResource(R.drawable.bottom_border_blue);
            add_new_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_blue);
            scramble_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_blue);
            delete_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_blue);
            popupBackground.setBackgroundResource(R.drawable.popup_blue);
            undo.setImageResource(R.drawable.undo_blue);
            loading.setImageResource(R.drawable.loading_icon_color_blue);
            loading2.setImageResource(R.drawable.loading_icon_color_blue);
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color10));
            spinnerToolbar.setBackgroundColor(getResources().getColor(R.color.color9));
            inputWord.setBackgroundResource(R.drawable.inputword_border_blue);
            barsToolbar.setBackgroundColor(getResources().getColor(R.color.color9));
            barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
            measureToolbar.setBackgroundColor(getResources().getColor(R.color.color9));
            measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
            spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_blue);
            spinnerList.setBackgroundColor(getResources().getColor(R.color.color10));
            measureList.setBackgroundColor(getResources().getColor(R.color.color10));
            pitchList.setBackgroundColor(getResources().getColor(R.color.color10));
            lengthList.setBackgroundColor(getResources().getColor(R.color.color10));
            settingList.setBackgroundColor(getResources().getColor(R.color.color10));
            changeColorButton.setTextColor(getResources().getColor(R.color.color9));
            changeFontFamilyButton.setTextColor(getResources().getColor(R.color.color9));
            rhymeFeaturesButton.setTextColor(getResources().getColor(R.color.color9));
//            startTrialYes.setTextColor(getResources().getColor(R.color.color9));
            hardSaveNo.setTextColor(getResources().getColor(R.color.color9));
            hardSaveYes.setTextColor(getResources().getColor(R.color.color9));
            englishFeaturesYes.setTextColor(getResources().getColor(R.color.color9));
            englishFeaturesNo.setTextColor(getResources().getColor(R.color.color9));
//            startTrialNo.setTextColor(getResources().getColor(R.color.color9));
            verifyYes.setTextColor(getResources().getColor(R.color.color9));
            verifyNo.setTextColor(getResources().getColor(R.color.color9));
            reviseButton.setTextColor(getResources().getColor(R.color.color9));
            fontButton.setTextColor(getResources().getColor(R.color.color9));
//            offlineButton.setTextColor(getResources().getColor(R.color.color9));
            orientationNo.setTextColor(getResources().getColor(R.color.color9));
            orientationYes.setTextColor(getResources().getColor(R.color.color9));
            darkmodeNo.setTextColor(getResources().getColor(R.color.color9));
            darkmodeYes.setTextColor(getResources().getColor(R.color.color9));
            deleteNo.setTextColor(getResources().getColor(R.color.color9));
            deleteYes.setTextColor(getResources().getColor(R.color.color9));
//            hideToolbarNo.setTextColor(getResources().getColor(R.color.color9));
//            hideToolbarYes.setTextColor(getResources().getColor(R.color.color9));
            ignoreButton.setTextColor(getResources().getColor(R.color.color9));
            createRhymesButton.setTextColor(getResources().getColor(R.color.color9));
            generalSettingsButton.setTextColor(getResources().getColor(R.color.color9));
//            outoftimeOff.setTextColor(getResources().getColor(R.color.color9));
            metronomeYes.setTextColor(getResources().getColor(R.color.color9));
            metronomeNo.setTextColor(getResources().getColor(R.color.color9));
            changeModeNo.setTextColor(getResources().getColor(R.color.color9));
            changeModeYes.setTextColor(getResources().getColor(R.color.color9));
            hintButton.setTextColor(getResources().getColor(R.color.color9));
//            shareButton.setTextColor(getResources().getColor(R.color.color9));
            upgradeYes.setTextColor(getResources().getColor(R.color.color9));
            upgradeNo.setTextColor(getResources().getColor(R.color.color9));
//            upgradeStartTrialButton.setTextColor(getResources().getColor(R.color.color9));
            rateYes.setTextColor(getResources().getColor(R.color.color9));
            rateNo.setTextColor(getResources().getColor(R.color.color9));
            newRecordingOkay.setTextColor(getResources().getColor(R.color.color9));
            editRecordingOkay.setTextColor(getResources().getColor(R.color.color9));
            manageRecordingOkay.setTextColor(getResources().getColor(R.color.color9));
            RecorderOkay.setTextColor(getResources().getColor(R.color.color9));
            changeClefDone.setTextColor(getResources().getColor(R.color.color9));
            donateDone.setTextColor(getResources().getColor(R.color.color9));
        } else if (color.equals("dark")) {
            if (notesFocus) {
                wordinfoBar.setBackgroundResource(R.drawable.no_focus_black);
                notesBar.setBackgroundResource(R.drawable.focus_black);
                notesTitle.setBackgroundResource(R.drawable.tab_left_focus_black);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_black);
            } else {
                wordinfoBar.setBackgroundResource(R.drawable.focus_black);
                notesBar.setBackgroundResource(R.drawable.no_focus_black);
                notesTitle.setBackgroundResource(R.drawable.tab_left_black);
                wordInfoTitle.setBackgroundResource(R.drawable.tab_right_focus_black);
            }
            if (topToolbarVisible) {
                topToolbarToggler.setImageResource(R.drawable.openspinner_black);
            } else {
                topToolbarToggler.setImageResource(R.drawable.closespinner_black);
            }
            if (bottomToolbarVisible) {
                bottomToolbarToggler.setImageResource(R.drawable.closespinner_black);
            } else {
                bottomToolbarToggler.setImageResource(R.drawable.openspinner_black);
            }
            for (TextView tv : linesWithRecordings) {
                tv.setBackgroundResource(R.drawable.play_black);
            }
            if (recorderRunning) {
                record.setImageResource(R.drawable.record_stop_black);
            } else {
                record.setImageResource(R.drawable.record_black);
            }
            add_new_measure.setBackgroundResource(R.drawable.bottom_border_black);
            scramble_selected_measure.setBackgroundResource(R.drawable.bottom_border_black);
            delete_selected_measure.setBackgroundResource(R.drawable.bottom_border_black);
            add_new_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_black);
            scramble_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_black);
            delete_selected_measure_checkbox.setBackgroundResource(R.drawable.bottom_border_black);
            popupBackground.setBackgroundResource(R.drawable.popup_black);
            undo.setImageResource(R.drawable.undo_black);
            loading.setImageResource(R.drawable.loading_icon_color_black);
            loading2.setImageResource(R.drawable.loading_icon_color_black);
            topToolbar.setBackgroundColor(getResources().getColor(R.color.color17));
            spinnerToolbar.setBackgroundColor(getResources().getColor(R.color.color16));
            inputWord.setBackgroundResource(R.drawable.inputword_border_black);
            barsToolbar.setBackgroundColor(getResources().getColor(R.color.color16));
            barsToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
            measureToolbar.setBackgroundColor(getResources().getColor(R.color.color16));
            measureToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
            spinnerToolbarButton.setBackgroundResource(R.drawable.left_border_search_black);
            spinnerList.setBackgroundColor(getResources().getColor(R.color.color17));
            measureList.setBackgroundColor(getResources().getColor(R.color.color17));
            pitchList.setBackgroundColor(getResources().getColor(R.color.color17));
            lengthList.setBackgroundColor(getResources().getColor(R.color.color17));
            settingList.setBackgroundColor(getResources().getColor(R.color.color17));
            changeColorButton.setTextColor(getResources().getColor(R.color.color16));
            changeFontFamilyButton.setTextColor(getResources().getColor(R.color.color16));
            rhymeFeaturesButton.setTextColor(getResources().getColor(R.color.color16));
//            startTrialYes.setTextColor(getResources().getColor(R.color.color16));
            hardSaveNo.setTextColor(getResources().getColor(R.color.color16));
            hardSaveYes.setTextColor(getResources().getColor(R.color.color16));
            englishFeaturesYes.setTextColor(getResources().getColor(R.color.color16));
            englishFeaturesNo.setTextColor(getResources().getColor(R.color.color16));
//            startTrialNo.setTextColor(getResources().getColor(R.color.color16));
            verifyYes.setTextColor(getResources().getColor(R.color.color16));
            verifyNo.setTextColor(getResources().getColor(R.color.color16));
            reviseButton.setTextColor(getResources().getColor(R.color.color16));
            fontButton.setTextColor(getResources().getColor(R.color.color16));
//            offlineButton.setTextColor(getResources().getColor(R.color.color16));
            orientationNo.setTextColor(getResources().getColor(R.color.color16));
            orientationYes.setTextColor(getResources().getColor(R.color.color16));
            darkmodeNo.setTextColor(getResources().getColor(R.color.color16));
            darkmodeYes.setTextColor(getResources().getColor(R.color.color16));
//            hideToolbarNo.setTextColor(getResources().getColor(R.color.color16));
//            hideToolbarYes.setTextColor(getResources().getColor(R.color.color16));
            ignoreButton.setTextColor(getResources().getColor(R.color.color16));
            createRhymesButton.setTextColor(getResources().getColor(R.color.color16));
            generalSettingsButton.setTextColor(getResources().getColor(R.color.color16));
//            outoftimeOff.setTextColor(getResources().getColor(R.color.color16));
            metronomeYes.setTextColor(getResources().getColor(R.color.color16));
            metronomeNo.setTextColor(getResources().getColor(R.color.color16));
            changeModeNo.setTextColor(getResources().getColor(R.color.color16));
            changeModeYes.setTextColor(getResources().getColor(R.color.color16));
            hintButton.setTextColor(getResources().getColor(R.color.color16));
//            shareButton.setTextColor(getResources().getColor(R.color.color16));
            upgradeYes.setTextColor(getResources().getColor(R.color.color16));
            upgradeNo.setTextColor(getResources().getColor(R.color.color16));
            deleteNo.setTextColor(getResources().getColor(R.color.color16));
            deleteYes.setTextColor(getResources().getColor(R.color.color16));
//            upgradeStartTrialButton.setTextColor(getResources().getColor(R.color.color16));
            rateYes.setTextColor(getResources().getColor(R.color.color16));
            rateNo.setTextColor(getResources().getColor(R.color.color16));
            newRecordingOkay.setTextColor(getResources().getColor(R.color.color16));
            editRecordingOkay.setTextColor(getResources().getColor(R.color.color16));
            manageRecordingOkay.setTextColor(getResources().getColor(R.color.color16));
            RecorderOkay.setTextColor(getResources().getColor(R.color.color16));
            changeClefDone.setTextColor(getResources().getColor(R.color.color16));
            donateDone.setTextColor(getResources().getColor(R.color.color16));
        }
    }

    void openUpgradePopup(String title, String text, SpannableString upgradeButtonText, final int amount) {
        final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
//        TextView upgradeStartTrialButton = (TextView) findViewById(R.id.upgrade_start_trial);
        final TextView upgradeTitle = (TextView) findViewById(R.id.upgrade_title);
        final TextView upgradeYes = (TextView) findViewById(R.id.upgrade_yes);
        final TextView upgradeNo = (TextView) findViewById(R.id.upgrade_no);
        final TextView upgradeText = (TextView) findViewById(R.id.upgrade_text);
        upgradeText.setText(text + "Would you like to upgrade?");
        upgradeYes.setText(upgradeButtonText);
        upgradeTitle.setText(title);
        openPopup(upgradePopup);
//        final TextView startTrial = (TextView) findViewById(R.id.start_trial);
//        upgradeStartTrialButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startTrial.performClick();
//            }
//        });
        upgradeNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                upgradePopup.setVisibility(View.GONE);
                popupBackground.setVisibility(View.GONE);


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
                            Toast.makeText(mainActivityHelper, "Response Code: " + Integer.toString(buyIntentBundle.getInt("RESPONSE_CODE")),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mainActivityHelper, "Unable to connect to Play Store",
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

    void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }


    private Runnable moveSeekBarThread = new Runnable() {

        public void run() {
            try {
                if (mediaPlayerEdit.isPlaying()) {

                    SeekBar seekBar = (SeekBar) findViewById(R.id.audioSeekbar);
                    int mediaPos_new = mediaPlayerEdit.getCurrentPosition();
                    int mediaMax_new = mediaPlayerEdit.getDuration();
                    seekBar.setMax(mediaMax_new);
                    seekBar.setProgress(mediaPos_new);
                    String currentSeconds = Integer.toString((int) ((mediaPos_new / 1000) % 60));
                    String currentMinutes = Integer.toString((int) ((mediaPos_new / (1000 * 60)) % 60));
                    if (currentSeconds.length() == 1) {
                        currentSeconds = "0" + currentSeconds;
                    }
                    String seconds = Integer.toString((int) ((mediaMax_new / 1000) % 60));
                    String minutes = Integer.toString((int) ((mediaMax_new / (1000 * 60)) % 60));
                    if (seconds.length() == 1) {
                        seconds = "0" + seconds;
                    }
                    final TextView seekBarTimer = (TextView) findViewById(R.id.audioSeekbarTimer);
                    seekBarTimer.setText(currentMinutes + ":" + currentSeconds + " / " + minutes + ":" + seconds);
                    seekBarHandler.postDelayed(this, 10); //Looping the thread after 0.01 second
                    // seconds
                }
            } catch (IllegalStateException e) {

            }
        }
    };


    void addRecordingsInline() {
        final LinearLayout recordingLineHolder = (LinearLayout) findViewById(R.id.recording_line_numbers);
        final EditText poem = (EditText) findViewById(R.id.poem);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (recordingLineHolder.getChildCount() > 0) {
                    recordingLineHolder.removeAllViews();
                }
                linesWithRecordings.clear();
                linesWithoutRecordings.clear();
                for (int i = 1; i < poem.getLineCount(); i++) {
                    final int finalI = i;
                    final TextView line = new TextView(MainActivity.this);
                    int lineHeight = poem.getLineHeight();
                    line.setLayoutParams(new LinearLayout.LayoutParams(lineHeight, lineHeight));


                    String darkmodeMode = getStringFromInternal("lyricdarkmode", "FALSE");
                    if (darkmodeMode.equals("TRUE")) {
                        line.setTextColor(Color.WHITE);
                    }else{
                        line.setTextColor(Color.BLACK);
                    }
                    if (savedRecordings.containsValue(i)) {

                        linesWithRecordings.add(line);
                        if (colorTheme.equals("royal")) {
                            line.setBackgroundResource(R.drawable.play_purple);
                        } else if (colorTheme.equals("sunset")) {
                            line.setBackgroundResource(R.drawable.play_orange);
                        } else if (colorTheme.equals("joy")) {
                            line.setBackgroundResource(R.drawable.play_blue);
                        } else if (colorTheme.equals("dark")) {
                            line.setBackgroundResource(R.drawable.play_black);
                        }
                        line.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String path = getApplicationContext().getDir("Lyric" + Integer.toString(lyricIndex), Context.MODE_PRIVATE).getAbsolutePath();
                                for (String recordingFile : savedRecordings.keySet()) {
                                    if (savedRecordings.get(recordingFile) == finalI) {
                                        path += "/" + recordingFile;
                                    }
                                }
                                if (lineRecordingPlaying) {
                                    lineRecordingPlaying = false;
                                    mediaPlayerLine.stop();
                                    mediaPlayerLine.release();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    for (TextView lineItem : linesWithRecordings) {
                                        if (colorTheme.equals("royal")) {
                                            lineItem.setBackgroundResource(R.drawable.play_purple);
                                        } else if (colorTheme.equals("sunset")) {
                                            lineItem.setBackgroundResource(R.drawable.play_orange);
                                        } else if (colorTheme.equals("joy")) {
                                            lineItem.setBackgroundResource(R.drawable.play_blue);
                                        } else if (colorTheme.equals("dark")) {
                                            lineItem.setBackgroundResource(R.drawable.play_black);
                                        }
                                    }
                                    if (colorTheme.equals("royal")) {
                                        line.setBackgroundResource(R.drawable.play_purple);
                                    } else if (colorTheme.equals("sunset")) {
                                        line.setBackgroundResource(R.drawable.play_orange);
                                    } else if (colorTheme.equals("joy")) {
                                        line.setBackgroundResource(R.drawable.play_blue);
                                    } else if (colorTheme.equals("dark")) {
                                        line.setBackgroundResource(R.drawable.play_black);
                                    }
                                } else {
                                    mediaPlayerLine = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
                                    lineRecordingPlaying = true;
                                    mediaPlayerLine.start();
                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    if (colorTheme.equals("royal")) {
                                        line.setBackgroundResource(R.drawable.stop_purple);
                                    } else if (colorTheme.equals("sunset")) {
                                        line.setBackgroundResource(R.drawable.stop_orange);
                                    } else if (colorTheme.equals("joy")) {
                                        line.setBackgroundResource(R.drawable.stop_blue);
                                    } else if (colorTheme.equals("dark")) {
                                        line.setBackgroundResource(R.drawable.stop_black);
                                    }
                                    mediaPlayerLine.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        public void onCompletion(MediaPlayer mediaPlayerLine) {
                                            lineRecordingPlaying = false;
                                            mediaPlayerLine.release();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                            if (colorTheme.equals("royal")) {
                                                line.setBackgroundResource(R.drawable.play_purple);
                                            } else if (colorTheme.equals("sunset")) {
                                                line.setBackgroundResource(R.drawable.play_orange);
                                            } else if (colorTheme.equals("joy")) {
                                                line.setBackgroundResource(R.drawable.play_blue);
                                            } else if (colorTheme.equals("dark")) {
                                                line.setBackgroundResource(R.drawable.play_black);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        linesWithoutRecordings.put(i, line);
                    }
                    line.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    line.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    try {
                        line.setTypeface(typeface);
                    } catch (RuntimeException e) {

                    }
                    recordingLineHolder.addView(line);

                }
            }
        });
    }

    void openBarsToolbar(LinearLayout bars) {
        selectedSixteenth = new ImageView(MainActivity.this);
        for (int i = 0; i < previousRow.getChildCount(); i++) {
            previousRow.getChildAt(i).clearFocus();
        }
        final LinearLayout measureList = (LinearLayout) findViewById(R.id.measure_list);
        final ImageButton openMeasureSpinner = (ImageButton) findViewById(R.id.open_measure_spinner);
        if (measureList.getTranslationY() == 0) {
            measureList.animate().translationY(screenHeight);
            openMeasureSpinner.setImageResource(R.drawable.openspinner);
        }
        previousRow.setBackgroundColor(TRANSPARENT);
        previousSylRow.setBackgroundColor(TRANSPARENT);
        Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
        final LinearLayout lengthList = (LinearLayout) findViewById(R.id.measure_bars_length_list);
        final LinearLayout pitchList = (LinearLayout) findViewById(R.id.measure_bars_pitch_list);
        barsToolbar.setVisibility(View.VISIBLE);
        for (int i = 0; i < selectedBars.getChildCount(); i++) {
            selectedBars.getChildAt(i).setBackgroundColor(TRANSPARENT);
            selectedBars.getChildAt(i).setClickable(false);
        }
        lengthList.bringToFront();
        pitchList.bringToFront();
        barsToolbar.bringToFront();
        // 2 lines add for admob
        adLoading.bringToFront();
        mAdView.bringToFront();
        barsCurrentlySelected = true;
        selectedBars = bars;
        for (int i = 0; i < selectedBars.getChildCount(); i++) {
            selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
            selectedBars.getChildAt(i).setClickable(true);
        }
        barsToolbar.setVisibility(View.VISIBLE);
        if (lengthSpinnerSelection.equals("Whole")) {
            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                selectedBars.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                selectedBars.getChildAt(i).setBackgroundColor(argb(50, 100, 100, 100));
                            }
                        } else {
                            if (selectedSixteenth.getParent() != null) {
                                for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                    ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                }
                            }
                        }
                        selectedSixteenth = (ImageView) selectedBars.getChildAt(0);
                    }
                });
            }

        } else if (lengthSpinnerSelection.equals("Half")) {
            for (int i = 0; i < selectedBars.getChildCount(); i = i + 8) {
                final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                final ImageView thirdSixteenth = (ImageView) selectedBars.getChildAt(i + 2);
                final ImageView fourthSixteenth = (ImageView) selectedBars.getChildAt(i + 3);
                final ImageView fifthSixteenth = (ImageView) selectedBars.getChildAt(i + 4);
                final ImageView sixthSixteenth = (ImageView) selectedBars.getChildAt(i + 5);
                final ImageView seventhSixteenth = (ImageView) selectedBars.getChildAt(i + 6);
                final ImageView eighthSixteenth = (ImageView) selectedBars.getChildAt(i + 7);
                ArrayList<ImageView> allSixteenths = new ArrayList<>();
                allSixteenths.add(sixteenth);
                allSixteenths.add(secondSixteenth);
                allSixteenths.add(thirdSixteenth);
                allSixteenths.add(fourthSixteenth);
                allSixteenths.add(fifthSixteenth);
                allSixteenths.add(sixthSixteenth);
                allSixteenths.add(seventhSixteenth);
                allSixteenths.add(eighthSixteenth);
                for (ImageView s : allSixteenths) {
                    s.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                }
                            } else {
                                if (selectedSixteenth.getParent() != null) {
                                    for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                        ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    }
                                }
                            }
                            selectedSixteenth = sixteenth;
                            sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            thirdSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            fourthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            fifthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            sixthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            seventhSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            eighthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                        }
                    });
                }

            }
        } else if (lengthSpinnerSelection.equals("Quarter")) {
            for (int i = 0; i < selectedBars.getChildCount(); i = i + 4) {
                final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                final ImageView thirdSixteenth = (ImageView) selectedBars.getChildAt(i + 2);
                final ImageView fourthSixteenth = (ImageView) selectedBars.getChildAt(i + 3);
                ArrayList<ImageView> allSixteenths = new ArrayList<>();
                allSixteenths.add(sixteenth);
                allSixteenths.add(secondSixteenth);
                allSixteenths.add(thirdSixteenth);
                allSixteenths.add(fourthSixteenth);
                for (ImageView s : allSixteenths) {
                    s.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                }
                            } else {
                                if (selectedSixteenth.getParent() != null) {
                                    for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                        ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    }
                                }
                            }
                            selectedSixteenth = sixteenth;
                            sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            thirdSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            fourthSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                        }
                    });
                }

            }
        } else if (lengthSpinnerSelection.equals("Eighth")) {
            for (int i = 0; i < selectedBars.getChildCount(); i = i + 2) {

                final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                final ImageView secondSixteenth = (ImageView) selectedBars.getChildAt(i + 1);
                ArrayList<ImageView> allSixteenths = new ArrayList<>();
                allSixteenths.add(sixteenth);
                allSixteenths.add(secondSixteenth);
                for (ImageView s : allSixteenths) {
                    s.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                                for (int i = 0; i < selectedBars.getChildCount(); i++) {
                                    selectedBars.getChildAt(i).setBackgroundColor(argb(50, 200, 200, 200));
                                }
                            } else {
                                if (selectedSixteenth.getParent() != null) {
                                    for (int i = 0; i < ((LinearLayout) selectedSixteenth.getParent()).getChildCount(); i++) {
                                        ((LinearLayout) selectedSixteenth.getParent()).getChildAt(i).setBackgroundColor(TRANSPARENT);
                                    }
                                }
                            }
                            selectedSixteenth = sixteenth;
                            sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                            secondSixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                        }
                    });
                }

            }
        } else if (lengthSpinnerSelection.equals("Sixteenth")) {
            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                sixteenth.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                            selectedSixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                        } else {
                            selectedSixteenth.setBackgroundColor(TRANSPARENT);
                        }
                        selectedSixteenth = sixteenth;
                        sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                    }
                });

            }
        } else if (lengthSpinnerSelection.equals("Remove")) {
            for (int i = 0; i < selectedBars.getChildCount(); i++) {
                final ImageView sixteenth = (ImageView) selectedBars.getChildAt(i);
                sixteenth.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (selectedBars.indexOfChild(selectedSixteenth) >= 0) {
                            selectedSixteenth.setBackgroundColor(argb(50, 200, 200, 200));
                        } else {
                            selectedSixteenth.setBackgroundColor(TRANSPARENT);
                        }
                        selectedSixteenth = sixteenth;
                        sixteenth.setBackgroundColor(argb(50, 100, 100, 100));
                    }
                });
            }
        }
        lengthList.bringToFront();
        pitchList.bringToFront();
        barsToolbar.bringToFront();
        // 2 lines add for admob
        adLoading.bringToFront();
        mAdView.bringToFront();
    }

    void changeClef(String clef) {

        final ImageButton changeClefImage = (ImageButton) findViewById(R.id.checkbox_change_clef);



        final TextView measure_bars_pitch_1 = (TextView) findViewById(R.id.measure_bars_pitch_1);
        final TextView measure_bars_pitch_2 = (TextView) findViewById(R.id.measure_bars_pitch_2);
        final TextView measure_bars_pitch_3 = (TextView) findViewById(R.id.measure_bars_pitch_3);
        final TextView measure_bars_pitch_4 = (TextView) findViewById(R.id.measure_bars_pitch_4);
        final TextView measure_bars_pitch_5 = (TextView) findViewById(R.id.measure_bars_pitch_5);
        final TextView measure_bars_pitch_6 = (TextView) findViewById(R.id.measure_bars_pitch_6);
        final TextView measure_bars_pitch_7 = (TextView) findViewById(R.id.measure_bars_pitch_7);
        final TextView measure_bars_pitch_8 = (TextView) findViewById(R.id.measure_bars_pitch_8);
        final TextView measure_bars_pitch_9 = (TextView) findViewById(R.id.measure_bars_pitch_9);
        final TextView measure_bars_pitch_10 = (TextView) findViewById(R.id.measure_bars_pitch_10);
        final TextView measure_bars_pitch_11 = (TextView) findViewById(R.id.measure_bars_pitch_11);
        final TextView measure_bars_pitch_12 = (TextView) findViewById(R.id.measure_bars_pitch_12);
        final TextView measure_bars_pitch_13 = (TextView) findViewById(R.id.measure_bars_pitch_13);
        final ImageView measure_bars_pitch_1_play = (ImageView) findViewById(R.id.measure_bars_pitch_1_play);
        final ImageView measure_bars_pitch_2_play = (ImageView) findViewById(R.id.measure_bars_pitch_2_play);
        final ImageView measure_bars_pitch_3_play = (ImageView) findViewById(R.id.measure_bars_pitch_3_play);
        final ImageView measure_bars_pitch_4_play = (ImageView) findViewById(R.id.measure_bars_pitch_4_play);
        final ImageView measure_bars_pitch_5_play = (ImageView) findViewById(R.id.measure_bars_pitch_5_play);
        final ImageView measure_bars_pitch_6_play = (ImageView) findViewById(R.id.measure_bars_pitch_6_play);
        final ImageView measure_bars_pitch_7_play = (ImageView) findViewById(R.id.measure_bars_pitch_7_play);
        final ImageView measure_bars_pitch_8_play = (ImageView) findViewById(R.id.measure_bars_pitch_8_play);
        final ImageView measure_bars_pitch_9_play = (ImageView) findViewById(R.id.measure_bars_pitch_9_play);
        final ImageView measure_bars_pitch_10_play = (ImageView) findViewById(R.id.measure_bars_pitch_10_play);
        final ImageView measure_bars_pitch_11_play = (ImageView) findViewById(R.id.measure_bars_pitch_11_play);
        final ImageView measure_bars_pitch_12_play = (ImageView) findViewById(R.id.measure_bars_pitch_12_play);
        final ImageView measure_bars_pitch_13_play = (ImageView) findViewById(R.id.measure_bars_pitch_13_play);

        ImageButton checkboxTrebleCLef = (ImageButton) findViewById(R.id.checkbox_treble_clef);
        ImageButton checkboxBassCLef = (ImageButton) findViewById(R.id.checkbox_bass_clef);
        ImageButton checkboxAltoCLef = (ImageButton) findViewById(R.id.checkbox_alto_clef);

        checkboxTrebleCLef.setImageResource(R.drawable.circle);
        checkboxBassCLef.setImageResource(R.drawable.circle);
        checkboxAltoCLef.setImageResource(R.drawable.circle);

        if (clef.equals("treble")) {
            changeClefImage.setImageResource(R.drawable.trebleclef);
            putStringToInternal("lyriccurrentclef", "treble");

            measure_bars_pitch_1.setText("C4");
            measure_bars_pitch_2.setText("D4");
            measure_bars_pitch_3.setText("E4");
            measure_bars_pitch_4.setText("F4");
            measure_bars_pitch_5.setText("G4");
            measure_bars_pitch_6.setText("A4");
            measure_bars_pitch_7.setText("B4");
            measure_bars_pitch_8.setText("C5");
            measure_bars_pitch_9.setText("D5");
            measure_bars_pitch_10.setText("E5");
            measure_bars_pitch_11.setText("F5");
            measure_bars_pitch_12.setText("G5");
            measure_bars_pitch_13.setText("A5");
            checkboxTrebleCLef.setImageResource(R.drawable.small_check);

        } else if (clef.equals("bass")) {
            changeClefImage.setImageResource(R.drawable.bassclef);
            putStringToInternal("lyriccurrentclef", "bass");

            measure_bars_pitch_1.setText("E2");
            measure_bars_pitch_2.setText("F2");
            measure_bars_pitch_3.setText("G2");
            measure_bars_pitch_4.setText("A2");
            measure_bars_pitch_5.setText("B2");
            measure_bars_pitch_6.setText("C3");
            measure_bars_pitch_7.setText("D3");
            measure_bars_pitch_8.setText("E3");
            measure_bars_pitch_9.setText("F3");
            measure_bars_pitch_10.setText("G3");
            measure_bars_pitch_11.setText("A3");
            measure_bars_pitch_12.setText("B3");
            measure_bars_pitch_13.setText("C4");
            checkboxBassCLef.setImageResource(R.drawable.small_check);

        } else if (clef.equals("alto")) {
            changeClefImage.setImageResource(R.drawable.altoclef);
            putStringToInternal("lyriccurrentclef", "alto");

            measure_bars_pitch_1.setText("D3");
            measure_bars_pitch_2.setText("E3");
            measure_bars_pitch_3.setText("F3");
            measure_bars_pitch_4.setText("G3");
            measure_bars_pitch_5.setText("A3");
            measure_bars_pitch_6.setText("B3");
            measure_bars_pitch_7.setText("C4");
            measure_bars_pitch_8.setText("D4");
            measure_bars_pitch_9.setText("E4");
            measure_bars_pitch_10.setText("F4");
            measure_bars_pitch_11.setText("G4");
            measure_bars_pitch_12.setText("A4");
            measure_bars_pitch_13.setText("B4");
            checkboxAltoCLef.setImageResource(R.drawable.small_check);
        }
        measure_bars_pitch_1_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_1.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_2_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_2.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_3_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_3.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_4_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_4.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_5_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_5.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_6_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_6.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_7_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_7.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_8_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_8.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_9_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_9.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_10_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_10.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_11_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_11.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_12_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_12.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
        measure_bars_pitch_13_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer note = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(measure_bars_pitch_13.getText().toString().toLowerCase(), "raw", getPackageName()));
                note.start();
                note.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer note) {
                        note.release();
                    }
                });
            }
        });
    }

    //    void addNotesDuration() {
//        final ConstraintLayout addMusicNotesPopup = (ConstraintLayout) findViewById(R.id.add_music_notes_popup);
//        TextView back = (TextView) findViewById(R.id.add_music_notes_back);
//        TextView next = (TextView) findViewById(R.id.add_music_notes_next);
//        TextView title = (TextView) findViewById(R.id.add_music_notes_title);
//        TextView text = (TextView) findViewById(R.id.add_music_notes_text);
//        LinearLayout selectMusicNotesPosition = (LinearLayout) findViewById(R.id.select_music_notes_position);
//        LinearLayout selectMusicNotesPositionWhole = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_whole);
//        LinearLayout selectMusicNotesPositionHalf = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_halves);
//        LinearLayout selectMusicNotesPositionQuarter = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_quarters);
//        LinearLayout selectMusicNotesPositionEighth = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_eighths);
//        HorizontalScrollView selectMusicNotesPositionSixteenths = (HorizontalScrollView) findViewById(R.id.select_music_notes_position_sixteenths_scrollview);
//        LinearLayout selectMusicNotesDuration = (LinearLayout) findViewById(R.id.select_music_notes_duration);
//        HorizontalScrollView selectMusicNotesPitch = (HorizontalScrollView) findViewById(R.id.select_music_notes_pitch);
//        final ImageButton wholeImage = (ImageButton) findViewById(R.id.select_music_notes_duration_whole);
//        final ImageButton halfImage = (ImageButton) findViewById(R.id.select_music_notes_duration_half);
//        final ImageButton quarterImage = (ImageButton) findViewById(R.id.select_music_notes_duration_quarter);
//        final ImageButton eighthImage = (ImageButton) findViewById(R.id.select_music_notes_duration_eighth);
//        final ImageButton sixteenthImage = (ImageButton) findViewById(R.id.select_music_notes_duration_sixteenth);
//        final ImageButton wholeCheckbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_duration_whole);
//        final ImageButton halfCheckbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_duration_half);
//        final ImageButton quarterCheckbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_duration_quarter);
//        final ImageButton eighthCheckbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_duration_eighth);
//        final ImageButton sixteenthCheckbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_duration_sixteenth);
//        if (addMusicNotesDuration.equals("")) {
//            wholeCheckbox.setImageResource(R.drawable.circle);
//            halfCheckbox.setImageResource(R.drawable.circle);
//            quarterCheckbox.setImageResource(R.drawable.circle);
//            eighthCheckbox.setImageResource(R.drawable.circle);
//            sixteenthCheckbox.setImageResource(R.drawable.circle);
//        }
//        wholeImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                wholeCheckbox.setImageResource(R.drawable.small_check);
//                halfCheckbox.setImageResource(R.drawable.circle);
//                quarterCheckbox.setImageResource(R.drawable.circle);
//                eighthCheckbox.setImageResource(R.drawable.circle);
//                sixteenthCheckbox.setImageResource(R.drawable.circle);
//                addMusicNotesDuration = "whole";
//            }
//        });
//        halfImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                wholeCheckbox.setImageResource(R.drawable.circle);
//                halfCheckbox.setImageResource(R.drawable.small_check);
//                quarterCheckbox.setImageResource(R.drawable.circle);
//                eighthCheckbox.setImageResource(R.drawable.circle);
//                sixteenthCheckbox.setImageResource(R.drawable.circle);
//                addMusicNotesDuration = "half";
//            }
//        });
//        quarterImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                wholeCheckbox.setImageResource(R.drawable.circle);
//                halfCheckbox.setImageResource(R.drawable.circle);
//                quarterCheckbox.setImageResource(R.drawable.small_check);
//                eighthCheckbox.setImageResource(R.drawable.circle);
//                sixteenthCheckbox.setImageResource(R.drawable.circle);
//                addMusicNotesDuration = "quarter";
//            }
//        });
//        eighthImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                wholeCheckbox.setImageResource(R.drawable.circle);
//                halfCheckbox.setImageResource(R.drawable.circle);
//                quarterCheckbox.setImageResource(R.drawable.circle);
//                eighthCheckbox.setImageResource(R.drawable.small_check);
//                sixteenthCheckbox.setImageResource(R.drawable.circle);
//                addMusicNotesDuration = "eighth";
//            }
//        });
//        sixteenthImage.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                wholeCheckbox.setImageResource(R.drawable.circle);
//                halfCheckbox.setImageResource(R.drawable.circle);
//                quarterCheckbox.setImageResource(R.drawable.circle);
//                eighthCheckbox.setImageResource(R.drawable.circle);
//                sixteenthCheckbox.setImageResource(R.drawable.small_check);
//                addMusicNotesDuration = "sixteenth";
//            }
//        });
//        wholeCheckbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                wholeImage.performClick();
//            }
//        });
//        halfCheckbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                halfImage.performClick();
//            }
//        });
//        quarterCheckbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                quarterImage.performClick();
//            }
//        });
//        eighthCheckbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                eighthImage.performClick();
//            }
//        });
//        sixteenthCheckbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                sixteenthImage.performClick();
//            }
//        });
//        selectMusicNotesPosition.setVisibility(View.GONE);
//        selectMusicNotesPositionSixteenths.setVisibility(View.GONE);
//        selectMusicNotesPositionEighth.setVisibility(View.GONE);
//        selectMusicNotesPositionQuarter.setVisibility(View.GONE);
//        selectMusicNotesPositionHalf.setVisibility(View.GONE);
//        selectMusicNotesPositionWhole.setVisibility(View.GONE);
//        selectMusicNotesDuration.setVisibility(View.GONE);
//        selectMusicNotesPitch.setVisibility(View.GONE);
//        selectMusicNotesPosition.setVisibility(View.GONE);
//        selectMusicNotesDuration.setVisibility(View.VISIBLE);
//        selectMusicNotesPitch.setVisibility(View.GONE);
//        title.setText("Add Music Note");
//        text.setVisibility(View.GONE);
//        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (500 * scale + 0.5f);
//        int screen90 = (int) (screenWidth * .9);
//        if (screen90 < pixels) {
//            addMusicNotesPopup.getLayoutParams().width = screen90;
//        } else {
//            addMusicNotesPopup.getLayoutParams().width = (pixels);
//        }
//        openPopup(addMusicNotesPopup);
//        back.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                addMusicNotesPopup.setVisibility(View.GONE);
//                popupBackground.setVisibility(View.GONE);
//                addMusicNotesPitch = 0;
//                addMusicNotesPosition = 0;
//                addMusicNotesDuration = "";
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (addMusicNotesDuration == "") {
//                    Toast.makeText(mainActivityHelper, "Must select a note length",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    addNotesPitch(addMusicNotesDuration);
//                }
//            }
//        });
//    }
//
//    void addNotesPitch(final String duration) {
//        final ConstraintLayout addMusicNotesPopup = (ConstraintLayout) findViewById(R.id.add_music_notes_popup);
//        TextView back = (TextView) findViewById(R.id.add_music_notes_back);
//        TextView next = (TextView) findViewById(R.id.add_music_notes_next);
//        TextView title = (TextView) findViewById(R.id.add_music_notes_title);
//        TextView text = (TextView) findViewById(R.id.add_music_notes_text);
//        LinearLayout selectMusicNotesPosition = (LinearLayout) findViewById(R.id.select_music_notes_position);
//        LinearLayout selectMusicNotesPositionWhole = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_whole);
//        LinearLayout selectMusicNotesPositionHalf = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_halves);
//        LinearLayout selectMusicNotesPositionQuarter = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_quarters);
//        LinearLayout selectMusicNotesPositionEighth = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_eighths);
//        HorizontalScrollView selectMusicNotesPositionSixteenths = (HorizontalScrollView) findViewById(R.id.select_music_notes_position_sixteenths_scrollview);
//        LinearLayout selectMusicNotesDuration = (LinearLayout) findViewById(R.id.select_music_notes_duration);
//        HorizontalScrollView selectMusicNotesPitch = (HorizontalScrollView) findViewById(R.id.select_music_notes_pitch);
//        final ImageButton pitch1 = (ImageButton) findViewById(R.id.select_music_notes_pitch_1);
//        final ImageButton pitch2 = (ImageButton) findViewById(R.id.select_music_notes_pitch_2);
//        final ImageButton pitch3 = (ImageButton) findViewById(R.id.select_music_notes_pitch_3);
//        final ImageButton pitch4 = (ImageButton) findViewById(R.id.select_music_notes_pitch_4);
//        final ImageButton pitch5 = (ImageButton) findViewById(R.id.select_music_notes_pitch_5);
//        final ImageButton pitch6 = (ImageButton) findViewById(R.id.select_music_notes_pitch_6);
//        final ImageButton pitch7 = (ImageButton) findViewById(R.id.select_music_notes_pitch_7);
//        final ImageButton pitch8 = (ImageButton) findViewById(R.id.select_music_notes_pitch_8);
//        final ImageButton pitch9 = (ImageButton) findViewById(R.id.select_music_notes_pitch_9);
//        final ImageButton pitch10 = (ImageButton) findViewById(R.id.select_music_notes_pitch_10);
//        final ImageButton pitch11 = (ImageButton) findViewById(R.id.select_music_notes_pitch_11);
//        final ImageButton pitch12 = (ImageButton) findViewById(R.id.select_music_notes_pitch_12);
//        final ImageButton pitch13 = (ImageButton) findViewById(R.id.select_music_notes_pitch_13);
//        final ImageButton pitch1Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_1);
//        final ImageButton pitch2Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_2);
//        final ImageButton pitch3Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_3);
//        final ImageButton pitch4Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_4);
//        final ImageButton pitch5Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_5);
//        final ImageButton pitch6Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_6);
//        final ImageButton pitch7Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_7);
//        final ImageButton pitch8Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_8);
//        final ImageButton pitch9Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_9);
//        final ImageButton pitch10Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_10);
//        final ImageButton pitch11Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_11);
//        final ImageButton pitch12Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_12);
//        final ImageButton pitch13Checkbox = (ImageButton) findViewById(R.id.checkbox_select_music_notes_pitch_13);
//        if (addMusicNotesPitch == 0) {
//            pitch1.setBackgroundColor(TRANSPARENT);
//            pitch2.setBackgroundColor(TRANSPARENT);
//            pitch3.setBackgroundColor(TRANSPARENT);
//            pitch4.setBackgroundColor(TRANSPARENT);
//            pitch5.setBackgroundColor(TRANSPARENT);
//            pitch6.setBackgroundColor(TRANSPARENT);
//            pitch7.setBackgroundColor(TRANSPARENT);
//            pitch8.setBackgroundColor(TRANSPARENT);
//            pitch9.setBackgroundColor(TRANSPARENT);
//            pitch10.setBackgroundColor(TRANSPARENT);
//            pitch11.setBackgroundColor(TRANSPARENT);
//            pitch12.setBackgroundColor(TRANSPARENT);
//            pitch13.setBackgroundColor(TRANSPARENT);
//            pitch1Checkbox.setImageResource(R.drawable.circle);
//            pitch2Checkbox.setImageResource(R.drawable.circle);
//            pitch3Checkbox.setImageResource(R.drawable.circle);
//            pitch4Checkbox.setImageResource(R.drawable.circle);
//            pitch5Checkbox.setImageResource(R.drawable.circle);
//            pitch6Checkbox.setImageResource(R.drawable.circle);
//            pitch7Checkbox.setImageResource(R.drawable.circle);
//            pitch8Checkbox.setImageResource(R.drawable.circle);
//            pitch9Checkbox.setImageResource(R.drawable.circle);
//            pitch10Checkbox.setImageResource(R.drawable.circle);
//            pitch11Checkbox.setImageResource(R.drawable.circle);
//            pitch12Checkbox.setImageResource(R.drawable.circle);
//            pitch13Checkbox.setImageResource(R.drawable.circle);
//        }
//        pitch1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.small_check);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 1;
//            }
//        });
//        pitch2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.small_check);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 2;
//            }
//        });
//        pitch3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.small_check);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 3;
//            }
//        });
//        pitch4.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.small_check);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 4;
//            }
//        });
//        pitch5.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.small_check);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 5;
//            }
//        });
//        pitch6.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.small_check);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 6;
//            }
//        });
//        pitch7.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.small_check);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 7;
//            }
//        });
//        pitch8.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.small_check);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 8;
//            }
//        });
//        pitch9.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.small_check);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 9;
//            }
//        });
//        pitch10.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.small_check);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 10;
//            }
//        });
//        pitch11.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.small_check);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 11;
//            }
//        });
//        pitch12.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch13.setBackgroundColor(TRANSPARENT);
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.small_check);
//                pitch13Checkbox.setImageResource(R.drawable.circle);
//                addMusicNotesPitch = 12;
//            }
//        });
//        pitch13.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.setBackgroundColor(TRANSPARENT);
//                pitch2.setBackgroundColor(TRANSPARENT);
//                pitch3.setBackgroundColor(TRANSPARENT);
//                pitch4.setBackgroundColor(TRANSPARENT);
//                pitch5.setBackgroundColor(TRANSPARENT);
//                pitch6.setBackgroundColor(TRANSPARENT);
//                pitch7.setBackgroundColor(TRANSPARENT);
//                pitch8.setBackgroundColor(TRANSPARENT);
//                pitch9.setBackgroundColor(TRANSPARENT);
//                pitch10.setBackgroundColor(TRANSPARENT);
//                pitch11.setBackgroundColor(TRANSPARENT);
//                pitch12.setBackgroundColor(TRANSPARENT);
//                pitch13.setBackgroundColor(argb(50, 200, 200, 200));
//                pitch1Checkbox.setImageResource(R.drawable.circle);
//                pitch2Checkbox.setImageResource(R.drawable.circle);
//                pitch3Checkbox.setImageResource(R.drawable.circle);
//                pitch4Checkbox.setImageResource(R.drawable.circle);
//                pitch5Checkbox.setImageResource(R.drawable.circle);
//                pitch6Checkbox.setImageResource(R.drawable.circle);
//                pitch7Checkbox.setImageResource(R.drawable.circle);
//                pitch8Checkbox.setImageResource(R.drawable.circle);
//                pitch9Checkbox.setImageResource(R.drawable.circle);
//                pitch10Checkbox.setImageResource(R.drawable.circle);
//                pitch11Checkbox.setImageResource(R.drawable.circle);
//                pitch12Checkbox.setImageResource(R.drawable.circle);
//                pitch13Checkbox.setImageResource(R.drawable.small_check);
//                addMusicNotesPitch = 13;
//            }
//        });
//        pitch1Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch1.performClick();
//            }
//        });
//        pitch2Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch2.performClick();
//            }
//        });
//        pitch3Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch3.performClick();
//            }
//        });
//        pitch4Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch4.performClick();
//            }
//        });
//        pitch5Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch5.performClick();
//            }
//        });
//        pitch6Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch6.performClick();
//            }
//        });
//        pitch7Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch7.performClick();
//            }
//        });
//        pitch8Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch8.performClick();
//            }
//        });
//        pitch9Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch9.performClick();
//            }
//        });
//        pitch10Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch10.performClick();
//            }
//        });
//        pitch11Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch11.performClick();
//            }
//        });
//        pitch12Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch12.performClick();
//            }
//        });
//        pitch13Checkbox.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pitch13.performClick();
//            }
//        });
//
//        //check if addMusicNotesPitch is 0 or no
//        selectMusicNotesPosition.setVisibility(View.GONE);
//        selectMusicNotesDuration.setVisibility(View.GONE);
//        selectMusicNotesPosition.setVisibility(View.GONE);
//        selectMusicNotesPositionSixteenths.setVisibility(View.GONE);
//        selectMusicNotesPositionEighth.setVisibility(View.GONE);
//        selectMusicNotesPositionQuarter.setVisibility(View.GONE);
//        selectMusicNotesPositionHalf.setVisibility(View.GONE);
//        selectMusicNotesPositionWhole.setVisibility(View.GONE);
//        selectMusicNotesDuration.setVisibility(View.GONE);
//        selectMusicNotesPitch.setVisibility(View.GONE);
//        selectMusicNotesPitch.setVisibility(View.VISIBLE);
//        title.setText("Select Note Pitch");
//        text.setVisibility(View.GONE);
//
//        openPopup(addMusicNotesPopup);
//        back.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                addNotesDuration();
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (addMusicNotesPitch == 0) {
//                    Toast.makeText(mainActivityHelper, "Must select a note pitch",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    addNotesPosition(duration, addMusicNotesPitch);
//                }
//            }
//        });
//    }
//
//    void addNotesPosition(final String duration, final int pitch) {
//        final ConstraintLayout addMusicNotesPopup = (ConstraintLayout) findViewById(R.id.add_music_notes_popup);
//        TextView back = (TextView) findViewById(R.id.add_music_notes_back);
//        TextView next = (TextView) findViewById(R.id.add_music_notes_next);
//        TextView title = (TextView) findViewById(R.id.add_music_notes_title);
//        final TextView text = (TextView) findViewById(R.id.add_music_notes_text);
//        LinearLayout selectMusicNotesPosition = (LinearLayout) findViewById(R.id.select_music_notes_position);
//        LinearLayout selectMusicNotesPositionWhole = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_whole);
//        LinearLayout selectMusicNotesPositionHalf = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_halves);
//        LinearLayout selectMusicNotesPositionQuarter = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_quarters);
//        LinearLayout selectMusicNotesPositionEighth = (LinearLayout) findViewById(R.id.checkbox_select_music_notes_position_eighths);
//        HorizontalScrollView selectMusicNotesPositionSixteenths = (HorizontalScrollView) findViewById(R.id.select_music_notes_position_sixteenths_scrollview);
//        LinearLayout selectMusicNotesDuration = (LinearLayout) findViewById(R.id.select_music_notes_duration);
//        HorizontalScrollView selectMusicNotesPitch = (HorizontalScrollView) findViewById(R.id.select_music_notes_pitch);
//        LinearLayout barsHolder;
//        addMusicNotesPosition = 0;
//        if (duration.equals("sixteenth")) {
//            barsHolder = (LinearLayout) findViewById(R.id.select_music_notes_position_bars_sixteenths);
//        } else {
//            barsHolder = (LinearLayout) findViewById(R.id.select_music_notes_position_bars);
//        }
//        barsHolder.removeAllViews();
//        final HashMap<Integer, ImageView> selectedBarSixteenths = new HashMap<>();
//        final HashMap<Integer, ImageView> popupBarSixteenths = new HashMap<>();
//        for (int i = 0; i < 16; i++) {
//            ImageView sixteenth = new ImageView(MainActivity.this);
//            sixteenth.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
//            sixteenth.setScaleType(ImageView.ScaleType.FIT_XY);
//            if (((ImageView) selectedBars.getChildAt(i)).getDrawable() != null) {
//                Drawable selectedBarsChildDrawable = ((ImageView) selectedBars.getChildAt(i)).getDrawable();
//                Drawable copy = selectedBarsChildDrawable.getConstantState().newDrawable().mutate();
//                copy.setColorFilter(WHITE, PorterDuff.Mode.SRC_IN);
//                sixteenth.setImageDrawable(copy);
//            }
//            barsHolder.addView(sixteenth);
//            popupBarSixteenths.put(i + 1, sixteenth);
//            selectedBarSixteenths.put(i + 1, ((ImageView) selectedBars.getChildAt(i)));
//        }
//        selectMusicNotesDuration.setVisibility(View.GONE);
//        selectMusicNotesPitch.setVisibility(View.GONE);
//        selectMusicNotesPosition.setVisibility(View.GONE);
//        selectMusicNotesPositionSixteenths.setVisibility(View.GONE);
//        selectMusicNotesPositionEighth.setVisibility(View.GONE);
//        selectMusicNotesPositionQuarter.setVisibility(View.GONE);
//        selectMusicNotesPositionHalf.setVisibility(View.GONE);
//        selectMusicNotesPositionWhole.setVisibility(View.GONE);
//        selectMusicNotesDuration.setVisibility(View.GONE);
//        selectMusicNotesPitch.setVisibility(View.GONE);
//
//        if (duration.equals("sixteenth")) {
//            selectMusicNotesPositionSixteenths.setVisibility(View.VISIBLE);
//            final ImageButton first = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_first_sixteenth);
//            final ImageButton second = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_second_sixteenth);
//            final ImageButton third = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_third_sixteenth);
//            final ImageButton fourth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fourth_sixteenth);
//            final ImageButton fifth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fifth_sixteenth);
//            final ImageButton sixth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_sixth_sixteenth);
//            final ImageButton seventh = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_seventh_sixteenth);
//            final ImageButton eighth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_eighth_sixteenth);
//            final ImageButton ninth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_ninth_sixteenth);
//            final ImageButton tenth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_tenth_sixteenth);
//            final ImageButton eleventh = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_eleventh_sixteenth);
//            final ImageButton twelth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_twelth_sixteenth);
//            final ImageButton thirteenth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_thirteenth_sixteenth);
//            final ImageButton fourteenth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fourteenth_sixteenth);
//            final ImageButton fifteenth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fifteenth_sixteenth);
//            final ImageButton sixteenth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_sixteenth_sixteenth);
//            final HashMap<Integer, ImageButton> allCheckboxes = new HashMap<>();
//            allCheckboxes.put(1, first);
//            allCheckboxes.put(2, second);
//            allCheckboxes.put(3, third);
//            allCheckboxes.put(4, fourth);
//            allCheckboxes.put(5, fifth);
//            allCheckboxes.put(6, sixth);
//            allCheckboxes.put(7, seventh);
//            allCheckboxes.put(8, eighth);
//            allCheckboxes.put(9, ninth);
//            allCheckboxes.put(10, tenth);
//            allCheckboxes.put(11, eleventh);
//            allCheckboxes.put(12, twelth);
//            allCheckboxes.put(13, thirteenth);
//            allCheckboxes.put(14, fourteenth);
//            allCheckboxes.put(15, fifteenth);
//            allCheckboxes.put(16, sixteenth);
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setImageResource(R.drawable.circle);
//            }
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        for (int num = 1; num <= 16; num++) {
//                            popupBarSixteenths.get(num).setBackgroundColor(TRANSPARENT);
//                            allCheckboxes.get(num).setImageResource(R.drawable.circle);
//                        }
//                        popupBarSixteenths.get(i).setBackgroundColor(argb(50, 200, 200, 200));
//                        allCheckboxes.get(i).setImageResource(R.drawable.small_check);
//                        addMusicNotesPosition = i;
//                    }
//                });
//                popupBarSixteenths.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//            }
//        } else if (duration.equals("eighth")) {
//            selectMusicNotesPosition.setVisibility(View.VISIBLE);
//            selectMusicNotesPositionEighth.setVisibility(View.VISIBLE);
//            final ImageButton first = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_first_eighth);
//            final ImageButton second = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_second_eighth);
//            final ImageButton third = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_third_eighth);
//            final ImageButton fourth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fourth_eighth);
//            final ImageButton fifth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fifth_eighth);
//            final ImageButton sixth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_sixth_eighth);
//            final ImageButton seventh = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_seventh_eighth);
//            final ImageButton eighth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_eighth_eighth);
//            final HashMap<Integer, ImageButton> allCheckboxes = new HashMap<>();
//            allCheckboxes.put(1, first);
//            allCheckboxes.put(2, second);
//            allCheckboxes.put(3, third);
//            allCheckboxes.put(4, fourth);
//            allCheckboxes.put(5, fifth);
//            allCheckboxes.put(6, sixth);
//            allCheckboxes.put(7, seventh);
//            allCheckboxes.put(8, eighth);
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setImageResource(R.drawable.circle);
//            }
//
//            for (final Integer i : allCheckboxes.keySet()) {
//                final int firstSixteenth = i + (i - 1);
//                final int secondSixteenth = firstSixteenth + 1;
//                allCheckboxes.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        for (int num = 1; num <= 16; num++) {
//                            popupBarSixteenths.get(num).setBackgroundColor(TRANSPARENT);
//                        }
//                        for (int num = 1; num <= 8; num++) {
//                            allCheckboxes.get(num).setImageResource(R.drawable.circle);
//                        }
//                        popupBarSixteenths.get(firstSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(secondSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        allCheckboxes.get(i).setImageResource(R.drawable.small_check);
//                        addMusicNotesPosition = firstSixteenth;
//                    }
//                });
//                popupBarSixteenths.get(firstSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(secondSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//            }
//        } else if (duration.equals("quarter")) {
//            selectMusicNotesPosition.setVisibility(View.VISIBLE);
//            selectMusicNotesPositionQuarter.setVisibility(View.VISIBLE);
//            final ImageButton first = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_first_quarter);
//            final ImageButton second = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_second_quarter);
//            final ImageButton third = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_third_quarter);
//            final ImageButton fourth = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_fourth_quarter);
//            final HashMap<Integer, ImageButton> allCheckboxes = new HashMap<>();
//            allCheckboxes.put(1, first);
//            allCheckboxes.put(2, second);
//            allCheckboxes.put(3, third);
//            allCheckboxes.put(4, fourth);
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setImageResource(R.drawable.circle);
//            }
//            for (final Integer i : allCheckboxes.keySet()) {
//                final int firstSixteenth = (i + (2 * (i - 1)) + (i - 1));
//                final int secondSixteenth = firstSixteenth + 1;
//                final int thirdSixteenth = firstSixteenth + 2;
//                final int fourthSixteenth = firstSixteenth + 3;
//                allCheckboxes.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        for (int num = 1; num <= 16; num++) {
//                            popupBarSixteenths.get(num).setBackgroundColor(TRANSPARENT);
//                        }
//                        for (int num = 1; num <= 4; num++) {
//                            allCheckboxes.get(num).setImageResource(R.drawable.circle);
//                        }
//                        popupBarSixteenths.get(firstSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(secondSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(thirdSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fourthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        allCheckboxes.get(i).setImageResource(R.drawable.small_check);
//                        addMusicNotesPosition = firstSixteenth;
//                    }
//                });
//                popupBarSixteenths.get(firstSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(secondSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(thirdSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fourthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//            }
//        } else if (duration.equals("half")) {
//            selectMusicNotesPosition.setVisibility(View.VISIBLE);
//            selectMusicNotesPositionHalf.setVisibility(View.VISIBLE);
//            final ImageButton first = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_first_half);
//            final ImageButton second = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_second_half);
//            final HashMap<Integer, ImageButton> allCheckboxes = new HashMap<>();
//            allCheckboxes.put(1, first);
//            allCheckboxes.put(2, second);
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setImageResource(R.drawable.circle);
//            }
//            for (final Integer i : allCheckboxes.keySet()) {
//                final int firstSixteenth = (i + (4 * (i - 1)) + (2 * (i - 1)) + (i - 1));
//                final int secondSixteenth = firstSixteenth + 1;
//                final int thirdSixteenth = firstSixteenth + 2;
//                final int fourthSixteenth = firstSixteenth + 3;
//                final int fifthSixteenth = firstSixteenth + 4;
//                final int sixthSixteenth = firstSixteenth + 5;
//                final int seventhSixteenth = firstSixteenth + 6;
//                final int eighthSixteenth = firstSixteenth + 7;
//                allCheckboxes.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        for (int num = 1; num <= 16; num++) {
//                            popupBarSixteenths.get(num).setBackgroundColor(TRANSPARENT);
//                        }
//                        for (int num = 1; num <= 2; num++) {
//                            allCheckboxes.get(num).setImageResource(R.drawable.circle);
//                        }
//                        popupBarSixteenths.get(firstSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(secondSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(thirdSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fourthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fifthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(sixthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(seventhSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(eighthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        allCheckboxes.get(i).setImageResource(R.drawable.small_check);
//                        addMusicNotesPosition = firstSixteenth;
//                    }
//                });
//                popupBarSixteenths.get(firstSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(secondSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(thirdSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fourthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fifthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(sixthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(seventhSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(eighthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//            }
//        } else if (duration.equals("whole")) {
//            selectMusicNotesPosition.setVisibility(View.VISIBLE);
//            selectMusicNotesPositionWhole.setVisibility(View.VISIBLE);
//            final ImageButton first = (ImageButton) findViewById(R.id.checkbox_select_music_notes_position_first_whole);
//            final HashMap<Integer, ImageButton> allCheckboxes = new HashMap<>();
//            allCheckboxes.put(1, first);
//            for (final Integer i : allCheckboxes.keySet()) {
//                allCheckboxes.get(i).setImageResource(R.drawable.circle);
//            }
//            for (final Integer i : allCheckboxes.keySet()) {
//                final int firstSixteenth = (i + (4 * (i - 1)) + (2 * (i - 1)) + (i - 1));
//                final int secondSixteenth = firstSixteenth + 1;
//                final int thirdSixteenth = firstSixteenth + 2;
//                final int fourthSixteenth = firstSixteenth + 3;
//                final int fifthSixteenth = firstSixteenth + 4;
//                final int sixthSixteenth = firstSixteenth + 5;
//                final int seventhSixteenth = firstSixteenth + 6;
//                final int eighthSixteenth = firstSixteenth + 7;
//                final int ninthSixteenth = firstSixteenth + 8;
//                final int tenthSixteenth = firstSixteenth + 9;
//                final int eleventhSixteenth = firstSixteenth + 10;
//                final int twelthSixteenth = firstSixteenth + 11;
//                final int thirteenthSixteenth = firstSixteenth + 12;
//                final int fourteenthSixteenth = firstSixteenth + 13;
//                final int fifteenthSixteenth = firstSixteenth + 14;
//                final int sixteenthSixteenth = firstSixteenth + 15;
//
//                allCheckboxes.get(i).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        for (int num = 1; num <= 16; num++) {
//                            popupBarSixteenths.get(num).setBackgroundColor(TRANSPARENT);
//                        }
//                        for (int num = 1; num <= 1; num++) {
//                            allCheckboxes.get(num).setImageResource(R.drawable.circle);
//                        }
//                        popupBarSixteenths.get(firstSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(secondSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(thirdSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fourthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fifthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(sixthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(seventhSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(eighthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(ninthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(tenthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(eleventhSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(twelthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(thirteenthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fourteenthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(fifteenthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        popupBarSixteenths.get(sixteenthSixteenth).setBackgroundColor(argb(50, 200, 200, 200));
//                        allCheckboxes.get(i).setImageResource(R.drawable.small_check);
//                        addMusicNotesPosition = firstSixteenth;
//                    }
//                });
//                popupBarSixteenths.get(firstSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(secondSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(thirdSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fourthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fifthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(sixthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(seventhSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(eighthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(ninthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(tenthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(eleventhSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(twelthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(thirteenthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fourteenthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(fifteenthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//                popupBarSixteenths.get(sixteenthSixteenth).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        allCheckboxes.get(i).performClick();
//                    }
//                });
//            }
//        }
//        title.setText("Select Note Position");
//        text.setVisibility(View.VISIBLE);
//        openPopup(addMusicNotesPopup);
//        back.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                addNotesPitch(addMusicNotesDuration);
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (addMusicNotesPosition == 0) {
//                    Toast.makeText(mainActivityHelper, "Must select a note position",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    boolean warningRequired = false;
//                    if (warningRequired) {
//                        text.setVisibility(View.VISIBLE);
//                    } else {
//                        String color = "black";
//
//                        if (getStringFromInternal("lyricdarkmode", "FALSE").equals("TRUE")) {
//                            color = "white";
//                        }
//                        String noteName = duration + "_" + pitch + "_" + color;
//                        String savedNote = duration + "_" + pitch + "_";
//                        ((ImageView) selectedBars.getChildAt(addMusicNotesPosition - 1)).setImageResource(getResources().getIdentifier(noteName, "drawable", getPackageName()));
//                        String allNotes = allBars.get(selectedBars);
//                        allNotes = allNotes.substring(0, ordinalIndexOf(allNotes, "<sixteenth>", addMusicNotesPosition) + 11) + savedNote + allNotes.substring(ordinalIndexOf(allNotes, "</sixteenth>", addMusicNotesPosition));
//                        allBars.put(selectedBars, allNotes);
//                        addMusicNotesPopup.setVisibility(View.GONE);
//                        popupBackground.setVisibility(View.GONE);
//                        addMusicNotesPitch = 0;
//                        addMusicNotesPosition = 0;
//                        addMusicNotesDuration = "";
//                        measureChanged = true;
//                    }
//                }
//            }
//        });
//    }

    void revertToPreviousSave(final boolean recovered) {
        final TextView hardSaveYes = (TextView) findViewById(R.id.hardsave_yes);
        final ConstraintLayout hardSavePopup = (ConstraintLayout) findViewById(R.id.hardsave_popup);
        final TextView hardSaveNo = (TextView) findViewById(R.id.hardsave_no);
        final TextView hardSaveText = (TextView) findViewById(R.id.hardsave_text);
        final TextView hardSaveTitle = (TextView) findViewById(R.id.hardsave_title);
        final TextView openRevert = (TextView) findViewById(R.id.open_revert);
        final ScrollView revertScrollView = (ScrollView) findViewById(R.id.revert_scrollview);
        final LinearLayout revertLinear = (LinearLayout) findViewById(R.id.revert_linear);

        hardSavePopup.setVisibility(View.GONE);
        popupBackground.setVisibility(View.GONE);
        int timer = 0;
        if (recovered) {
            timer = 3000;
        }
        Handler handlerBackground = new Handler();
        handlerBackground.postDelayed(new Runnable() {
            @Override
            public void run() {
                hardSaveYes.setText("LOAD");
                hardSaveNo.setText("KEEP");
                hardSaveTitle.setText("Your Saved Lyrics");
                revertScrollView.setVisibility(View.VISIBLE);
                openRevert.setVisibility(View.GONE);
                if (revertLinear.getChildCount() > 0) {
                    revertLinear.removeAllViews();
                }


                String poemStr = getStringFromInternal("lyric" + lyricIndex + "poem", null);
                String poem2 = getStringFromInternal("lyric" + lyricIndex + "poem2", null);
                String poem3 = getStringFromInternal("lyric" + lyricIndex + "poem3", null);
                String poem4 = getStringFromInternal("lyric" + lyricIndex + "poem4", null);
                String poem5 = getStringFromInternal("lyric" + lyricIndex + "poem5", null);
                String poemStrDate = getStringFromInternal("lyric" + lyricIndex + "poemdate", null);
                String poem2Date = getStringFromInternal("lyric" + lyricIndex + "poem2date", null);
                String poem3Date = getStringFromInternal("lyric" + lyricIndex + "poem3date", null);
                String poem4Date = getStringFromInternal("lyric" + lyricIndex + "poem4date", null);
                String poem5Date = getStringFromInternal("lyric" + lyricIndex + "poem5date", null);
                final ArrayList<String> poems = new ArrayList<>();
                ArrayList<String> dates = new ArrayList<>();
                if (poemStr != null && poemStrDate != null) {
                    if (poem2 != null && poem2Date != null) {
                        if (poem3 != null && poem3Date != null) {
                            if (poem4 != null && poem4Date != null) {
                                if (poem5 != null && poem5Date != null) {
                                    poems.add(poem5);
                                    dates.add(poem5Date);
                                }
                                poems.add(poem4);
                                dates.add(poem4Date);
                            }
                            poems.add(poem3);
                            dates.add(poem3Date);
                        }
                        poems.add(poem2);
                        dates.add(poem2Date);
                    }
                    poems.add(poemStr);
                    dates.add(poemStrDate);
                }
                String measuresStr = getStringFromInternal("lyric" + lyricIndex + "measures", null);
                String measures2 = getStringFromInternal("lyric" + lyricIndex + "measures2", null);
                String measures3 = getStringFromInternal("lyric" + lyricIndex + "measures3", null);
                String measures4 = getStringFromInternal("lyric" + lyricIndex + "measures4", null);
                String measures5 = getStringFromInternal("lyric" + lyricIndex + "measures5", null);
                final ArrayList<String> measures = new ArrayList<>();
                if (measuresStr != null) {
                    if (measures2 != null) {
                        if (measures3 != null) {
                            if (measures4 != null) {
                                if (measures5 != null) {
                                    measures.add(measures5);
                                }
                                measures.add(measures4);
                            }
                            measures.add(measures3);
                        }
                        measures.add(measures2);
                    }
                    measures.add(measuresStr);
                }
                if (poems.size() <= 2) {
                    revertScrollView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (150 * scale + 0.5f);
                    revertScrollView.getLayoutParams().height = pixels;
                }
                for (int i = poems.size() - 1; i >= 0; i--) {
                    final int finalI = i;
                    final LinearLayout savedItem = new LinearLayout(MainActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 5, 0, 5);
                    savedItem.setLayoutParams(params);
                    savedItem.setOrientation(LinearLayout.VERTICAL);
                    savedItem.setBackgroundColor(Color.WHITE);
                    LinearLayout row = new LinearLayout(MainActivity.this);
                    LinearLayout row2 = new LinearLayout(MainActivity.this);
                    TextView savedPoemLength = new TextView(MainActivity.this);
                    TextView savedMeasureLength = new TextView(MainActivity.this);
                    TextView savedDate = new TextView(MainActivity.this);
                    TextView savedTime = new TextView(MainActivity.this);
                    savedPoemLength.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row.setWeightSum(2);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row2.setWeightSum(2);
                    row2.setOrientation(LinearLayout.HORIZONTAL);
                    savedDate.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    savedTime.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    savedPoemLength.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    savedMeasureLength.setLayoutParams(new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    savedDate.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    savedTime.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    savedPoemLength.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    savedMeasureLength.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    savedDate.setMaxLines(1);
                    savedTime.setMaxLines(1);
                    savedPoemLength.setMaxLines(1);
                    savedMeasureLength.setMaxLines(1);
                    savedDate.setTypeface(typeface);
                    savedTime.setTypeface(typeface);
                    savedPoemLength.setTypeface(typeface);
                    savedMeasureLength.setTypeface(typeface);
                    savedDate.setPadding(45, 25, 0, 0);
                    savedTime.setPadding(0, 25, 45, 0);
                    savedPoemLength.setPadding(45, 15, 0, 25);
                    savedMeasureLength.setPadding(0, 15, 45, 25);
                    savedTime.setTextColor(Color.BLACK);
                    savedDate.setTextColor(Color.BLACK);
                    savedTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    savedDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    savedPoemLength.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    savedMeasureLength.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                    final EditText test = (EditText) findViewById(R.id.poemtest);
                    test.setText(poems.get(i));
                    test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    int measureCount = 0;
                    if (i < measures.size()) {
                        String measureString = measures.get(i);
                        int index = measureString.indexOf("<measure>");
                        while (index >= 0) {
                            measureCount++;
                            index = measureString.indexOf("<measure>", index + 1);
                        }
                    }

                    savedDate.setText(dates.get(i).substring(0, dates.get(i).indexOf(",") + 6));
                    savedTime.setText(dates.get(i).substring(dates.get(i).indexOf(",") + 7));
                    row.addView(savedDate);
                    row.addView(savedTime);
                    savedPoemLength.setText("Lyric Lines: " + (test.getLineCount() - 1));
                    savedMeasureLength.setText("Measures: " + measureCount);
                    row2.addView(savedPoemLength);
                    row2.addView(savedMeasureLength);
                    savedItem.addView(row);
                    savedItem.addView(row2);

                    savedItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (savedItem != currentSaveSelection) {
                                savedItem.setBackgroundColor(rgb(200, 200, 200));
                                currentSaveSelection.setBackgroundColor(WHITE);
                                currentSaveSelection = savedItem;
                                hardSaveYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText realPoem = (EditText) findViewById(R.id.poem);
                                        realPoem.setText(poems.get(finalI));
                                        if (measures.size() > finalI) {
                                            savedMeasures = measures.get(finalI);
                                        } else {
                                            savedMeasures = "";
                                        }
                                        measureChanged = true;
                                        firstMeasureModeUse = true;
                                        poemChangeChecker = "";
                                        final LinearLayout poemAndSyllables = (LinearLayout) findViewById(R.id.poemandsyllables);
                                        if (!poemAndSyllables.isShown()) {
                                            final TextView yes = (TextView) findViewById(R.id.popup_yes_changemode);
                                            yes.performClick();
                                            yes.performClick();
                                        }


                                        hardSavePopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });

                    revertLinear.addView(savedItem);
                }

                LinearLayout yesOrNo = (LinearLayout) findViewById(R.id.hardsave_yes_no);

                if (recovered) {
                    if (poems.size() == 0) {
                        hardSaveText.setText("Your unsaved lyrics have been recovered.");
                        revertScrollView.setVisibility(View.GONE);
                        hardSaveNo.setText("OK");
                        hardSaveYes.setVisibility(View.GONE);
                        yesOrNo.setWeightSum(1);
                    } else {
                        hardSaveText.setText("Your unsaved lyrics have been recovered. Would you like to keep them or load a previous save?");
                    }
                } else {
                    hardSaveText.setText("Load a previous save or keep your current lyrics?");
                }
                openPopup(hardSavePopup);
                hardSaveYes.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(mainActivityHelper, "Selection required",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                hardSaveNo.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        hardSavePopup.setVisibility(View.GONE);
                        popupBackground.setVisibility(View.GONE);
                    }
                });

            }
        }, timer);

    }


    void hardSave(final boolean exiting) {
        final ConstraintLayout hardSavePopup = (ConstraintLayout) findViewById(R.id.hardsave_popup);
        TextView hardSaveYes = (TextView) findViewById(R.id.hardsave_yes);
        TextView hardSaveNo = (TextView) findViewById(R.id.hardsave_no);
        TextView openRevert = (TextView) findViewById(R.id.open_revert);
        TextView hardSaveTitle = (TextView) findViewById(R.id.hardsave_title);
        TextView hardSaveText = (TextView) findViewById(R.id.hardsave_text);
        ScrollView revertScrollView = (ScrollView) findViewById(R.id.revert_scrollview);
        LinearLayout revertLinear = (LinearLayout) findViewById(R.id.revert_linear);
        LinearLayout yesOrNo = (LinearLayout) findViewById(R.id.hardsave_yes_no);
        hardSaveYes.setVisibility(View.VISIBLE);
        yesOrNo.setWeightSum(2);
        hardSaveYes.setText("YES");
        hardSaveNo.setText("NO");
        hardSaveTitle.setText("Save Your Lyrics");
        revertScrollView.setVisibility(View.GONE);
        hardSaveText.setText("Would you like to save your lyrics?");


        String poemStr = getStringFromInternal("lyric" + lyricIndex + "poem", "thisisanullvalue0518");
        final String poemStrDate = getStringFromInternal("lyric" + lyricIndex + "poemdate", "thisisanullvalue0518");

        if (poemStr != "thisisanullvalue0518" && !exiting && poemStrDate != "thisisanullvalue0518") {
            openRevert.setVisibility(View.VISIBLE);
        }
        openPopup(hardSavePopup);
        final EditText poem = (EditText) findViewById(R.id.poem);
        final EditText title = (EditText) findViewById(R.id.title);
        openRevert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                revertToPreviousSave(false);
            }
        });
        hardSaveYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (title.getText().toString().equals("")) {
                    putStringToInternal("lyric" + lyricIndex + "title", "Untitled");
                } else {
                    putStringToInternal("lyric" + lyricIndex + "title", title.getText().toString());
                }
                String measuresStr = getStringFromInternal("lyric" + lyricIndex + "measures", "thisisanullvalue0518");
                String storedMeasure = updateMeasureSave(true);
                String poemStr = getStringFromInternal("lyric" + lyricIndex + "poem", "thisisanullvalue0518");
                String poem2 = getStringFromInternal("lyric" + lyricIndex + "poem2", "thisisanullvalue0518");
                String poem3 = getStringFromInternal("lyric" + lyricIndex + "poem3", "thisisanullvalue0518");
                String poem4 = getStringFromInternal("lyric" + lyricIndex + "poem4", "thisisanullvalue0518");
                if ((poemStr != "thisisanullvalue0518" && !poemStr.equals(poem.getText().toString())) || (measuresStr != "thisisanullvalue0518" && !measuresStr.equals(storedMeasure))) {
                    if (poem2 != "thisisanullvalue0518") {
                        if (poem3 != "thisisanullvalue0518") {
                            if (poem4 != "thisisanullvalue0518") {
                                putStringToInternal("lyric" + lyricIndex + "poem5", getStringFromInternal("lyric" + lyricIndex + "poem4", ""));
                                putStringToInternal("lyric" + lyricIndex + "poem5date", getStringFromInternal("lyric" + lyricIndex + "poem4date", ""));
                            }
                            putStringToInternal("lyric" + lyricIndex + "poem4", getStringFromInternal("lyric" + lyricIndex + "poem3", ""));
                            putStringToInternal("lyric" + lyricIndex + "poem4date", getStringFromInternal("lyric" + lyricIndex + "poem3date", ""));
                        }
                        putStringToInternal("lyric" + lyricIndex + "poem3", getStringFromInternal("lyric" + lyricIndex + "poem2", ""));
                        putStringToInternal("lyric" + lyricIndex + "poem3date", getStringFromInternal("lyric" + lyricIndex + "poem2date", ""));
                    }
                    putStringToInternal("lyric" + lyricIndex + "poem2", getStringFromInternal("lyric" + lyricIndex + "poem", ""));
                    putStringToInternal("lyric" + lyricIndex + "poem2date", getStringFromInternal("lyric" + lyricIndex + "poemdate", ""));
                }

                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                putStringToInternal("lyric" + lyricIndex + "poemdate", currentDateTimeString);
                putStringToInternal("lyric" + lyricIndex + "poemsoftsavedate", currentDateTimeString);
                putStringToInternal("lyric" + lyricIndex + "poem", poem.getText().toString());
                putStringToInternal("lyric" + lyricIndex + "poemsoftsave", poem.getText().toString());

                requestBackup();

                Toast.makeText(mainActivityHelper, "Lyrics Saved", Toast.LENGTH_SHORT).show();
                if (exiting) {
                    final Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(myIntent);
                    finish();
                } else {

                    hardSavePopup.setVisibility(View.GONE);
                    popupBackground.setVisibility(View.GONE);
                    //this code asks people every 50 saves to review the app.
                    int askForReviewCounter = getIntFromInternal("lyricaskforreview", 1);
                    String askForReviewStop = getStringFromInternal("lyricaskforreviewstop", "FALSE");
                    if (askForReviewCounter % 50 == 0 && askForReviewStop.equals("FALSE")) {
                        final ConstraintLayout ratePopup = (ConstraintLayout) findViewById(R.id.rate_popup);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openPopup(ratePopup);
                                TextView rateYes = (TextView) findViewById(R.id.rate_yes);
                                TextView rateNo = (TextView) findViewById(R.id.rate_no);
                                rateYes.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        putStringToInternal("lyricaskforreviewstop", "TRUE");

                                        Uri uri = Uri.parse("market://details?id=kmcilvai.perfectpoet");
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        // To count with Play market backstack, After pressing back button,
                                        // to taken back to our application, we need to add following flags to intent.
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(goToMarket);
                                        } catch (ActivityNotFoundException e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=kmcilvai.perfectpoet")));
                                        }
                                        ratePopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }
                                });
                                rateNo.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        ratePopup.setVisibility(View.GONE);
                                        popupBackground.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                    putIntToInternal("lyricaskforreview", askForReviewCounter + 1);

                }
            }
        });
        hardSaveNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (exiting) {


                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    putStringToInternal("lyric" + lyricIndex + "poemsoftsavedate", currentDateTimeString);
                    putStringToInternal("lyric" + lyricIndex + "poemsoftsave", getStringFromInternal("lyric" + lyricIndex + "poem", ""));

                    final Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    hardSavePopup.setVisibility(View.GONE);
                    popupBackground.setVisibility(View.GONE);
                }
            }
        });
    }

    String updateMeasureSave(boolean hardsave) {
        final LinearLayout measureModeLayout = (LinearLayout) findViewById(R.id.measuremode);
        String storedMeasure = "";
        try {
            for (int i = 0; i < measureModeLayout.getChildCount(); i++) {
                if (allBars.keySet().contains((LinearLayout) measureModeLayout.getChildAt(i))) {
                    storedMeasure += "<bars>";
                    String notes = allBars.get((LinearLayout) measureModeLayout.getChildAt(i));
                    storedMeasure += notes;
                    storedMeasure += "</bars>\n";
                    continue;
                }
                LinearLayout row = (LinearLayout) measureModeLayout.getChildAt(i);
                i++;
                storedMeasure += "<measure>\n";
                for (int a = 0; a < 4; a++) {
                    storedMeasure += "<quarter>\n";
                    EditText quarter = (EditText) row.getChildAt(a);
                    storedMeasure += quarter.getText().toString();
                    storedMeasure += "</quarter>\n";
                }
                storedMeasure += "</measure>\n";
            }
        } catch (NullPointerException e) {

        }


        TextView title = (TextView) findViewById(R.id.title);
        if (title.getText().toString().equals("")) {
            putStringToInternal("lyric" + lyricIndex + "title", "Untitled");
        } else {
            putStringToInternal("lyric" + lyricIndex + "title", title.getText().toString());
        }
        if (hardsave) {
            final EditText poem = (EditText) findViewById(R.id.poem);
            String poemStr = getStringFromInternal("lyric" + lyricIndex + "poem", "thisisanullvalue0518");
            String measuresStr = getStringFromInternal("lyric" + lyricIndex + "measures", "thisisanullvalue0518");
            String measures2 = getStringFromInternal("lyric" + lyricIndex + "measures2", "thisisanullvalue0518");
            String measures3 = getStringFromInternal("lyric" + lyricIndex + "measures3", "thisisanullvalue0518");
            String measures4 = getStringFromInternal("lyric" + lyricIndex + "measures4", "thisisanullvalue0518");
            if ((poemStr != "thisisanullvalue0518" && !poemStr.equals(poem.getText().toString())) || (measuresStr != "thisisanullvalue0518" && !measuresStr.equals(storedMeasure))) {
                if (measures2 != "thisisanullvalue0518") {
                    if (measures3 != "thisisanullvalue0518") {
                        if (measures4 != "thisisanullvalue0518") {
                            putStringToInternal("lyric" + lyricIndex + "measures5", getStringFromInternal("lyric" + lyricIndex + "measures4", ""));
                        }
                        putStringToInternal("lyric" + lyricIndex + "measures4", getStringFromInternal("lyric" + lyricIndex + "measures3", ""));
                    }
                    putStringToInternal("lyric" + lyricIndex + "measures3", getStringFromInternal("lyric" + lyricIndex + "measures2", ""));
                }
                putStringToInternal("lyric" + lyricIndex + "measures2", getStringFromInternal("lyric" + lyricIndex + "measures", ""));
            }
            //make sure they loaded the measures at all otherwise it will be overwritten by empty measures. This leaves the measures as whatevery they were previously
            if (!firstMeasureModeUse) {
                putStringToInternal("lyric" + lyricIndex + "measures", storedMeasure);
                putStringToInternal("lyric" + lyricIndex + "measuressoftsave", storedMeasure);
            } else {
                putStringToInternal("lyric" + lyricIndex + "measures", measuresStr);
                putStringToInternal("lyric" + lyricIndex + "measuressoftsave", measuresStr);
            }
        } else {
            //do not need to check if firstmeasuremodeuser because it only softsaves in measuremode
            putStringToInternal("lyric" + lyricIndex + "measuressoftsave", storedMeasure);
        }

        return storedMeasure;
    }

    void openPopup(final ConstraintLayout cl) {
        final ConstraintLayout rhymeFeaturesPopup = (ConstraintLayout) findViewById(R.id.rhyme_features_popup);
        final ConstraintLayout newRecordingPopup = (ConstraintLayout) findViewById(R.id.new_recording_popup);
        final ConstraintLayout manageRecordingPopup = (ConstraintLayout) findViewById(R.id.manage_recording_popup);
        final ConstraintLayout editRecordingPopup = (ConstraintLayout) findViewById(R.id.edit_recording_popup);
        final ConstraintLayout recorderPopup = (ConstraintLayout) findViewById(R.id.recorder_popup);
//        final ConstraintLayout startTrialPopup = (ConstraintLayout) findViewById(R.id.trial_popup);
        final ConstraintLayout hardSavePopup = (ConstraintLayout) findViewById(R.id.hardsave_popup);
        final ConstraintLayout verifyPopup = (ConstraintLayout) findViewById(R.id.verify_popup);
        final ConstraintLayout revisePopup = (ConstraintLayout) findViewById(R.id.revise_popup);
        final ConstraintLayout changeColorPopup = (ConstraintLayout) findViewById(R.id.changecolor_popup);
        final ConstraintLayout changeFontFamilyPopup = (ConstraintLayout) findViewById(R.id.changefont_popup);
        final ConstraintLayout fontPopup = (ConstraintLayout) findViewById(R.id.font_popup);
//        final ConstraintLayout offlinePopup = (ConstraintLayout) findViewById(R.id.offline_popup);
        final ConstraintLayout englishFeaturesPopup = (ConstraintLayout) findViewById(R.id.english_features_popup);
        final ConstraintLayout orientationPopup = (ConstraintLayout) findViewById(R.id.orientation_popup);
        final ConstraintLayout darkmodePopup = (ConstraintLayout) findViewById(R.id.darkmode_popup);
//        final ConstraintLayout sharePopup = (ConstraintLayout) findViewById(R.id.share_popup);
        final ConstraintLayout donatePopup = (ConstraintLayout) findViewById(R.id.donate_popup);
        final ConstraintLayout ratePopup = (ConstraintLayout) findViewById(R.id.rate_popup);
        final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
//        final ConstraintLayout hideToolbarPopup = (ConstraintLayout) findViewById(R.id.hideToolbar_popup);
        final ConstraintLayout ignoreListPopup = (ConstraintLayout) findViewById(R.id.ignore_list_popup);
        final ConstraintLayout deletePopup = (ConstraintLayout) findViewById(R.id.delete_popup);
        final ConstraintLayout hintPopup = (ConstraintLayout) findViewById(R.id.hint_popup);
        final ConstraintLayout createRhymesPopup = (ConstraintLayout) findViewById(R.id.create_rhymes_popup);
        final ConstraintLayout changeClefPopup = (ConstraintLayout) findViewById(R.id.change_clef_popup);
        final ConstraintLayout generalSettingsPopup = (ConstraintLayout) findViewById(R.id.general_settings_popup);
//        final ConstraintLayout outOfTimePopup = (ConstraintLayout) findViewById(R.id.outoftime_popup);
        final ConstraintLayout metronomePopup = (ConstraintLayout) findViewById(R.id.metronome_popup);
        final ConstraintLayout changeModePopup = (ConstraintLayout) findViewById(R.id.changemode_popup);
        final ImageButton optionsButton = (ImageButton) findViewById(R.id.settings);
        final LinearLayout settingList = (LinearLayout) findViewById(R.id.settings_list);
        final ImageButton openSpinner = (ImageButton) findViewById(R.id.open_spinner);
        final ImageButton openMeasureSpinner = (ImageButton) findViewById(R.id.open_measure_spinner);
        final ImageButton openNotePitch = (ImageButton) findViewById(R.id.open_note_pitch);
        final ImageButton openNoteLength = (ImageButton) findViewById(R.id.open_note_length);
        final LinearLayout spinnerList = (LinearLayout) findViewById(R.id.spinner_list);
        final LinearLayout measureList = (LinearLayout) findViewById(R.id.measure_list);
        final LinearLayout lengthList = (LinearLayout) findViewById(R.id.measure_bars_length_list);
        final LinearLayout pitchList = (LinearLayout) findViewById(R.id.measure_bars_pitch_list);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!proUser && changeColorPopup.isShown()) {
                    TextView royal = (TextView) findViewById(R.id.popup_royal);
                    royal.performClick();
                }
                if (!proUser && changeFontFamilyPopup.isShown()) {
                    TextView sourceSansProSelect = (TextView) findViewById(R.id.popup_sourcesanspro);
                    sourceSansProSelect.performClick();
                }
                DisplayMetrics metrics;
                metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                final int screenHeight = metrics.heightPixels;
                settingList.animate().translationY(-screenHeight);
                optionsButton.setImageResource(R.drawable.settings1);
                spinnerList.animate().translationY(screenHeight);
                openSpinner.setImageResource(R.drawable.openspinner);
                openMeasureSpinner.setImageResource(R.drawable.openspinner);
                openNoteLength.setImageResource(R.drawable.openspinner);
                openNotePitch.setImageResource(R.drawable.openspinner);
                measureList.animate().translationY(screenHeight);
                lengthList.animate().translationY(screenHeight);
                pitchList.animate().translationY(screenHeight);
                darkmodePopup.setVisibility(View.GONE);
//                sharePopup.setVisibility(View.GONE);
                donatePopup.setVisibility(View.GONE);
                ratePopup.setVisibility(View.GONE);
                upgradePopup.setVisibility(View.GONE);
//                offlinePopup.setVisibility(View.GONE);
                englishFeaturesPopup.setVisibility(View.GONE);
                fontPopup.setVisibility(View.GONE);
                revisePopup.setVisibility(View.GONE);
                deletePopup.setVisibility(View.GONE);
                changeColorPopup.setVisibility(View.GONE);
                changeFontFamilyPopup.setVisibility(View.GONE);
//                startTrialPopup.setVisibility(View.GONE);
                hardSavePopup.setVisibility(View.GONE);
                orientationPopup.setVisibility(View.GONE);
//                hideToolbarPopup.setVisibility(View.GONE);
//                outOfTimePopup.setVisibility(View.GONE);
                ignoreListPopup.setVisibility(View.GONE);
                hintPopup.setVisibility(View.GONE);
                createRhymesPopup.setVisibility(View.GONE);
                changeClefPopup.setVisibility(View.GONE);
                changeModePopup.setVisibility(View.GONE);
                verifyPopup.setVisibility(View.GONE);
                metronomePopup.setVisibility(View.GONE);
                newRecordingPopup.setVisibility(View.GONE);
                manageRecordingPopup.setVisibility(View.GONE);
                editRecordingPopup.setVisibility(View.GONE);
                recorderPopup.setVisibility(View.GONE);
                rhymeFeaturesPopup.setVisibility(View.GONE);
                generalSettingsPopup.setVisibility(View.GONE);
                popupBackground.setVisibility(View.GONE);
                for (int lineNum : linesWithoutRecordings.keySet()) {
                    linesWithoutRecordings.get(lineNum).setText("");
                }

                if (cl != null) {
                    cl.setVisibility(View.VISIBLE);

                    final ConstraintLayout mainActivity = (ConstraintLayout) findViewById(R.id.main_activity);
                    popupBackground.getLayoutParams().height = cl.getHeight();
                    popupBackground.getLayoutParams().width = cl.getWidth();
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(popupBackground.getId(), ConstraintSet.BOTTOM, cl.getId(), ConstraintSet.BOTTOM, 0);
                    constraintSet.connect(popupBackground.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(popupBackground.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 0);
                    constraintSet.connect(popupBackground.getId(), ConstraintSet.RIGHT, cl.getId(), ConstraintSet.RIGHT, 0);
                    constraintSet.applyTo(mainActivity);
                    popupBackground.setVisibility(View.VISIBLE);
                    popupBackground.bringToFront();
                    cl.bringToFront();
                    Handler handlerBackground = new Handler();
                    handlerBackground.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final ConstraintLayout mainActivity = (ConstraintLayout) findViewById(R.id.main_activity);
                            popupBackground.getLayoutParams().height = cl.getHeight();
                            popupBackground.getLayoutParams().width = cl.getWidth();
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(mainActivity);
                            constraintSet.connect(popupBackground.getId(), ConstraintSet.BOTTOM, cl.getId(), ConstraintSet.BOTTOM, 0);
                            constraintSet.connect(popupBackground.getId(), ConstraintSet.TOP, cl.getId(), ConstraintSet.TOP, 0);
                            constraintSet.connect(popupBackground.getId(), ConstraintSet.LEFT, cl.getId(), ConstraintSet.LEFT, 0);
                            constraintSet.connect(popupBackground.getId(), ConstraintSet.RIGHT, cl.getId(), ConstraintSet.RIGHT, 0);
                            constraintSet.applyTo(mainActivity);
                            popupBackground.setVisibility(View.VISIBLE);
                            popupBackground.bringToFront();
                            cl.bringToFront();

                        }
                    }, 20);
                }

                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void findHeadingsBoldItalics(final boolean offline) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final EditText poem = (EditText) findViewById(R.id.poem);
                int selection = poem.getSelectionEnd();
                SpannableString nextSpannableOffline = new SpannableString(poem.getText().toString());
                headings.clear();
                //starts at 1 because ordinal index starts at 1
                int lineCounter = 0;
                String[] webPoemLines = poem.getText().toString().split("\n");
                for (String line : webPoemLines) {
                    final EditText test = (EditText) findViewById(R.id.poemtest);
                    test.setText(line);
                    test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
                    if (line.length() > 0) {
                        if (line.charAt(0) == '+') {
                            headings.put(lineCounter, 0);
//                    if(line.length()>1) {
//                        if (line.charAt(1) == '+') {
//                            headings.put(lineCounter, 1);
//                            if(line.length()>2) {
//                                if (line.charAt(2) == '+') {
//                                    headings.put(lineCounter, 2);
//                                }
//                            }
//                        }
//                    }
                        }
                        if (line.contains("*")) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                if (word.length() > 0) {
                                    if (word.charAt(0) == '*' && word.charAt(word.length() - 1) == '*' && !word.equals("*") && !word.equals("**")) {
                                        italicWords.add(word);
                                    }
                                }
                            }
                        }
                        if (line.contains("!")) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                if (word.length() > 0) {
                                    if (word.charAt(0) == '!' && word.charAt(word.length() - 1) == '!' && !word.equals("!") && !word.equals("!!")) {
                                        boldWords.add(word);
                                    }
                                }
                            }
                        }
                    }

                    lineCounter++;
                }
                if (offline) {
                    for (String boldWord : boldWords) {
                        int index = poem.getText().toString().indexOf(boldWord);
                        while (index >= 0) {
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + boldWord.length() - 1, index + boldWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new StyleSpan(Typeface.BOLD), index + 1, index + boldWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(boldWord, index + 1);
                        }
                    }
                    for (String italicWord : italicWords) {
                        int index = poem.getText().toString().indexOf(italicWord);
                        while (index >= 0) {
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + italicWord.length() - 1, index + italicWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new StyleSpan(Typeface.ITALIC), index + 1, index + italicWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(italicWord, index + 1);
                        }
                    }
                    for (Integer headingLine : headings.keySet()) {
                        Float f = 1.1f;
                        switch (headings.get(headingLine)) {
                            case 0:
                                f = 1.3f;
                                break;
//                            case 0:
//                                f = 1.1f;
//                                break;
//                            case 1:
//                                f = 1.3f;
//                                break;
//                            case 2:
//                                f = 1.5f;
//                                break;
                            default:
                                f = 1.1f;
                                break;
                        }
                        if (headingLine == 0) {
//                            nextSpannableOffline.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ordinalIndexOf(poem.getText().toString(), "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new RelativeSizeSpan(f), 0, ordinalIndexOf(poem.getText().toString(), "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
//                            nextSpannableOffline.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ordinalIndexOf(poem.getText().toString(), "\n", headingLine), ordinalIndexOf(poem.getText().toString(), "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(poem.getText().toString(), "\n", headingLine), ordinalIndexOf(poem.getText().toString(), "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannableOffline.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), ordinalIndexOf(poem.getText().toString(), "\n", headingLine), ordinalIndexOf(poem.getText().toString(), "\n", headingLine) + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        //attempt to make the recording line labels match the lines with headings
//                        if(linesWithoutRecordings.keySet().contains(headingLine)){
//                            Log.d("errorcat", "working"+headingLine);
//                            TextView lineItem = linesWithoutRecordings.get(headingLine);
//                            lineItem.setLayoutParams(new LinearLayout.LayoutParams((int)(lineItem.getHeight()*1.3), (int)(lineItem.getHeight()*1.3)));
//                        }
                    }

                    poem.setText(nextSpannableOffline);
                    if (!titleHelper.hasFocus()) {
                        poem.setSelection(selection);
                    }

                }
            }
        });
    }

    int[] flagOverflowingLines(int difference) {
        int[] overFlowingLines = new int[difference];
        final EditText poem = (EditText) findViewById(R.id.poem);
        //starts at 1 because ordinal index starts at 1
        int lineCounter = 1;
        int index = 0;
        String[] webPoemLines = poem.getText().toString().split("\n");

        for (String line : webPoemLines) {
            final EditText test = (EditText) findViewById(R.id.poemtest);
            SpannableString helperSpannable = new SpannableString(line);
            for (String boldWord : boldWords) {
                int index2 = line.indexOf(boldWord);
                while (index2 >= 0) {
                    helperSpannable.setSpan(new StyleSpan(Typeface.BOLD), index2 + 1, index2 + boldWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index2 = line.indexOf(boldWord, index2 + 1);
                }
            }
            for (String italicWord : italicWords) {
                int index2 = line.indexOf(italicWord);
                while (index2 >= 0) {
                    helperSpannable.setSpan(new StyleSpan(Typeface.ITALIC), index2 + 1, index2 + italicWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index2 = line.indexOf(italicWord, index2 + 1);
                }
            }
            if (headings.keySet().contains(lineCounter - 1)) {
                Float f = 1.1f;
                switch (headings.get(lineCounter - 1)) {
                    case 0:
                        f = 1.3f;
                        break;
//                            case 0:
//                                f = 1.1f;
//                                break;
//                            case 1:
//                                f = 1.3f;
//                                break;
//                            case 2:
//                                f = 1.5f;
//                                break;
                    default:
                        f = 1.1f;
                        break;
                }
                helperSpannable.setSpan(new RelativeSizeSpan(f), 0, line.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            test.setText(helperSpannable);
            test.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontsize);
            for (int i = 1; i < test.getLineCount(); i++) {
                if (index < overFlowingLines.length) {
                    overFlowingLines[index] = lineCounter;
                }
                index++;
            }
            lineCounter++;
        }
        return overFlowingLines;
    }

    private class AsyncMeasureSyllableCount extends AsyncTask<TextView, Void, HashMap<TextView, String>> {

        @Override
        //create a string containing all the syllables and pass them to onpostexecute
        protected HashMap<TextView, String> doInBackground(TextView... input) {
            ArrayList<TextView> keys = new ArrayList<>(Arrays.asList(input));
            HashMap<TextView, String> measureSyllables = new HashMap<>();
            for (int i = 0; i < keys.size(); i++) {
                int syllables = syllablesPerLine(textviewsToChange.get(keys.get(i)), keys.get(i));
                measureSyllables.put(keys.get(i), Integer.toString(syllables));
                textviewsToChange.remove(keys.get(i));
            }
            return measureSyllables;
        }

        //pass in the JSON file from doinbackground, parse it, and apply the number of syllables to the outputview
        @Override
        protected void onPostExecute(HashMap<TextView, String> result) {
            ArrayList<TextView> keys = new ArrayList<>(result.keySet());
            for (int i = 0; i < keys.size(); i++) {
                if (flaggedMeasureTextviews.contains(keys.get(i))) {
                    result.put(keys.get(i), result.get(keys.get(i)) + "+");
                }
                if (result.get(keys.get(i)).equals("0")) {
                    keys.get(i).setText(" ");
                } else {
                    keys.get(i).setText(result.get(keys.get(i)));
                }
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
                    cornerLoader.postDelayed(new Runnable() {
                        public void run() {
                            cornerLoader.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            });
            flaggedMeasureTextviews.clear();
            processing = false;


        }

    }

    private class AsyncSyllableCount extends AsyncTask<String, Void, String> {

        @Override
        //create a string containing all the syllables and pass them to onpostexecute
        protected String doInBackground(String... input) {
//            task = this;
//            taskTimer = new Timer();
//            int timerTime = 30000;
//            taskTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            final ConstraintLayout outOfTimePopup = (ConstraintLayout) findViewById(R.id.outoftime_popup);
//                            final TextView outOfTimeCancel = (TextView) findViewById(R.id.turnoff_outoftime);
//                            openPopup(outOfTimePopup);
//                            if (task.getStatus().equals("RUNNING")) {
//                                task.cancel(true);
//                            }
//                            final ImageButton checkboxTurnOffAuto = (ImageButton) findViewById(R.id.checkbox_turn_off);
//                            final ImageButton checkboxSwipeDelay = (ImageButton) findViewById(R.id.checkbox_swipe_delay);
//                            final ImageButton checkboxAfterTyping = (ImageButton) findViewById(R.id.checkbox_after_typing);
//                            checkboxAfterTyping.setImageResource(R.drawable.circle);
//                            checkboxTurnOffAuto.setImageResource(R.drawable.small_check);
//                            checkboxSwipeDelay.setImageResource(R.drawable.circle);
//
//                            putStringToInternal("lyricreviserate", "AUTO_OFF");
//                                            boolean success = false;
//            while(!success){
//            }
//                            globalForceOffline = true;
//                            processing = false;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    EditText poem = (EditText) findViewById(R.id.poem);
////                                    poem.setFocusable(true);
////                                    poem.setFocusableInTouchMode(true);
//                                    if (firstRun) {
//                                        firstRun = false;
//                                    } else {
//                                        poem.requestFocus();
//
//                                    }
//                                }
//                            });
//                            final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
//                            cornerLoader.postDelayed(new Runnable() {
//                                public void run() {
//                                    cornerLoader.setVisibility(View.GONE);
//                                }
//                            }, 500);
//                            outOfTimeCancel.setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//
//                                    outOfTimePopup.setVisibility(View.GONE);
//                                    popupBackground.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    });
//
//                }
//
//            }, timerTime);

            int lineCounter = 1;
            String allSyllables = "";
            for (String line : input) {

                int syllablesForLine = syllablesPerLine(line, null);
                if (syllablesForLine != 0) {
                    if (syllablesForLine > 99) {
                        if (flaggedLines.contains(lineCounter)) {
                            allSyllables += syllablesForLine + "+\n";
                        } else {
                            allSyllables += syllablesForLine + "\n";
                        }
                        lineCounter++;
                    } else if (syllablesForLine > 9) {
                        if (flaggedLines.contains(lineCounter)) {
                            allSyllables += "  " + syllablesForLine + "+  \n";
                        } else {
                            allSyllables += "  " + syllablesForLine + "  \n";
                        }
                        lineCounter++;
                    } else {
                        if (flaggedLines.contains(lineCounter)) {
                            allSyllables += " " + syllablesForLine + "+ \n";
                        } else {
                            allSyllables += " " + syllablesForLine + " \n";
                        }
                        lineCounter++;
                    }
                } else {
                    if (flaggedLines.contains(lineCounter)) {
                        allSyllables += "  0+  \n";
                    } else {
                        allSyllables += "    \n";
                    }
                    lineCounter++;

                }
            }
            if (allSyllables.equals("")) {
                return "  ";
            }
//            taskTimer.cancel();
            return allSyllables;
        }


        //pass in the JSON file from doinbackground, parse it, and apply the number of syllables to the outputview
        @Override
        protected void onPostExecute(String result) {
            if (globalForceOffline == true) {
                return;
            }
            TextView outputView = (TextView) findViewById(R.id.syllables);
            EditText poem = (EditText) findViewById(R.id.poem);


            putStringToInternal("lyricfoundrhymes", foundWordRhymes.toString());
            putStringToInternal("lyricfoundnearrhymes", foundWordNearRhymes.toString());
            putStringToInternal("lyricfoundsyllables", foundWordSyllables.toString());
            int resultCount = result.length() - result.replace("\n", "").length();
            outputView.setText(result);
            int[] overFlowingLines;

            if (resultCount != 0 && (poem.getLineCount()) > resultCount) {

                overFlowingLines = flagOverflowingLines((poem.getLineCount()) - resultCount);
            } else {
                overFlowingLines = new int[0];
            }
            findHeadingsBoldItalics(false);

            int[] originalOverFlowingLines = new int[overFlowingLines.length];
            int x = 0;
            for(int originalOverflowingLine: overFlowingLines){
                originalOverFlowingLines[x] = originalOverflowingLine;
                x++;
            }

            //add a buffer to the poem to reduce exceptions
            if (overFlowingLines.length > 0 && isNetworkAvailable()) {
                Arrays.sort(overFlowingLines);
                for (int i = 0; i < overFlowingLines.length; i++) {
                    try {
                        result = result.substring(0, ordinalIndexOf(result, "\n", overFlowingLines[i])) + "\n   " + result.substring(ordinalIndexOf(result, "\n", overFlowingLines[i]), result.length());
                    } catch (IndexOutOfBoundsException e) {

                    }
                    for (int a = i + 1; a < overFlowingLines.length; a++) {
                        overFlowingLines[a] = overFlowingLines[a] + 1;
                    }
                    Collections.sort(flaggedLines);
                    for (int b = 0; b < flaggedLines.size(); b++) {
                        if (flaggedLines.get(b) > overFlowingLines[i] + 1) {
                            flaggedLines.add(flaggedLines.get(b) + 1);
                            flaggedLines.remove(b);
                        }
                    }

                }
            }

            SpannableString spannable = new SpannableString(result);
            //if this is incommented, you also need to uncomment both the spanstoremove sections
            //           mark all lines flagged red
//            if (!flaggedLines.isEmpty()) {
//                for (int i = 0; i < flaggedLines.size(); i++) {
//                    if ((ordinalIndexOf(result, "\n", flaggedLines.get(i)) - 3) >= 0) {
//                        spannable.setSpan(new ForegroundColorSpan(Color.RED), ordinalIndexOf(result, "\n", flaggedLines.get(i)) - 3, ordinalIndexOf(result, "\n", flaggedLines.get(i)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//            }

            outputView.setText(spannable);
            lineCount = 0;
            flaggedLines.clear();
            //find all the rhymes and color them
            Set<String> alreadyColoredWords = new HashSet<>();

            if (lastWords) {
                if (!currentLastWordIndexes.isEmpty()) {
                    nextSpannable = new SpannableString(poem.getText().toString());
                    String lowercasePoem = poem.getText().toString().toLowerCase();
                    SortedSet<Integer> keys = new TreeSet<>(currentLastWordIndexes.keySet());
                    SortedSet<String> values = new TreeSet<>(currentLastWordIndexes.values());

                    for (Integer index : keys) {
                        String word = currentLastWordIndexes.get(index).toLowerCase();
                        if (!alreadyColoredWords.contains(word) && !ignoredWords.contains(word)) {

                            ArrayList<String> rhymes = new ArrayList<>();
                            boolean nearRhymeChecker = false;
                            boolean createdrhymeChecker = false;
                            if (highSensitivity) {
                                if (foundWordNearRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                    nearRhymeChecker = true;
                                }
                            }
                            if (createdRhymes.containsKey(word.toLowerCase())) {
                                createdrhymeChecker = true;
                            }
                            if (foundWordRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")) || nearRhymeChecker || createdrhymeChecker) {
                                if (foundWordRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                    if (foundWordRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                        rhymes.addAll(foundWordRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                    }
                                }
                                if (nearRhymeChecker) {
                                    if (foundWordNearRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                        rhymes.addAll(foundWordNearRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                    }
                                }
                                try {
                                    if (createdrhymeChecker) {
                                        if (createdRhymes.get(word.toLowerCase()).get(0) != "") {
                                            ArrayList<String> allCreatedRhymes = new ArrayList<>(createdRhymes.get(word.toLowerCase()));

                                            boolean allRhymesRetrieved = false;
                                            while (!allRhymesRetrieved) {
                                                boolean atLeastOneNew = false;
                                                for (String createdRhyme : allCreatedRhymes) {
                                                    if (createdRhymes.get(createdRhyme.toLowerCase()).get(0) != "") {
                                                        ArrayList<String> someCreatedRhymes = new ArrayList<>(createdRhymes.get(createdRhyme));
                                                        for (String rhyme : someCreatedRhymes) {
                                                            if (!allCreatedRhymes.contains(rhyme) && !rhyme.equals(word)) {
                                                                allCreatedRhymes.add(rhyme);
                                                                atLeastOneNew = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                if (atLeastOneNew == false) {
                                                    allRhymesRetrieved = true;
                                                }
                                            }
                                            rhymes.addAll(allCreatedRhymes);
                                            for (String createdRhyme : allCreatedRhymes) {
                                                if (foundWordRhymes.containsKey(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                                    if (foundWordRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                                        rhymes.addAll(foundWordRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                                    }
                                                }
                                                if (nearRhymeChecker) {
                                                    if (foundWordNearRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                                        rhymes.addAll(foundWordNearRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }catch (Exception e){
                                    Log.d("errorcatCreatedRhyme", e+"");
                                }
                                for (String ignoredWord : ignoredWords) {
                                    rhymes.remove(ignoredWord);
                                }

                                if (!Collections.disjoint(values, rhymes)) {
                                    int r = colors[colorCounter];
                                    colorCounter++;
                                    int g = colors[colorCounter];
                                    colorCounter++;
                                    int b = colors[colorCounter];
                                    colorCounter++;
                                    if (colorCounter == 63) {
                                        colorCounter = 0;
                                    }
                                    boolean atLeastOne = false;

                                    try {
                                        while (index >= 0 && (index + word.length() + 1) < poem.getText().toString().length()) {
                                            if (currentLastWordIndexes.containsKey(index)) {

                                                atLeastOne = true;
                                                boolean goodToGoR = false;
                                                //make sure the word isn't inside another word.
                                                if (index == 0) {
                                                    if (!lowercasePoem.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                        goodToGoR = true;
                                                    }
                                                } else {
                                                    if (!lowercasePoem.substring(index - 1, index).matches("[a-zA-Z0-9']+")) {
                                                        if (!lowercasePoem.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                            goodToGoR = true;
                                                        }
                                                    }
                                                }
                                                if (goodToGoR) {
                                                    if (index >= 0 && index < lowercasePoem.length()) {
                                                        alreadyColoredWords.add(word);
                                                        nextSpannable.setSpan(new ForegroundColorSpan(rgb(r, g, b)), index, index + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                                                    }
                                                }
                                            }
                                            index = lowercasePoem.toLowerCase().indexOf(word, index + 1);
                                        }
                                    } catch (IndexOutOfBoundsException e) {

                                    }

                                    if (atLeastOne) {
                                        for (String rhyme : rhymes) {
                                            try {
                                                int rhymeIndex = lowercasePoem.indexOf(rhyme);
                                                while (rhymeIndex >= 0 && (rhymeIndex + rhyme.length() + 1) < poem.getText().toString().length()) {
                                                    if (currentLastWordIndexes.containsKey(rhymeIndex)) {
                                                        boolean goodToGoR = false;
                                                        //make sure the word isn't inside another word.
                                                        if (rhymeIndex == 0) {
                                                            if (!lowercasePoem.substring(rhymeIndex + rhyme.length(), rhymeIndex + rhyme.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                                goodToGoR = true;
                                                            }
                                                        } else {
                                                            if (!lowercasePoem.substring(rhymeIndex - 1, rhymeIndex).matches("[a-zA-Z0-9']+")) {
                                                                if (!lowercasePoem.substring(rhymeIndex + rhyme.length(), rhymeIndex + rhyme.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                                    goodToGoR = true;
                                                                }
                                                            }
                                                        }

                                                        if (goodToGoR) {
                                                            if (rhymeIndex >= 0 && rhymeIndex < lowercasePoem.length()) {
                                                                alreadyColoredWords.add(rhyme);
                                                                nextSpannable.setSpan(new ForegroundColorSpan(rgb(r, g, b)), rhymeIndex, rhymeIndex + rhyme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                            }
                                                        }
                                                    }
                                                    rhymeIndex = lowercasePoem.toLowerCase().indexOf(rhyme, rhymeIndex + 1);
                                                }
                                            } catch (IndexOutOfBoundsException e) {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (String boldWord : boldWords) {
                        int index = poem.getText().toString().indexOf(boldWord);
                        while (index >= 0) {
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + boldWord.length() - 1, index + boldWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new StyleSpan(Typeface.BOLD), index + 1, index + boldWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(boldWord, index + 1);
                        }
                    }
                    for (String italicWord : italicWords) {
                        int index = poem.getText().toString().indexOf(italicWord);
                        while (index >= 0) {
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + italicWord.length() - 1, index + italicWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new StyleSpan(Typeface.ITALIC), index + 1, index + italicWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(italicWord, index + 1);
                        }
                    }
                    for (Integer headingLine : headings.keySet()) {
                        Float f = 1.1f;
                        switch (headings.get(headingLine)) {
                            case 0:
                                f = 1.3f;
                                break;
//                            case 0:
//                                f = 1.1f;
//                                break;
//                            case 1:
//                                f = 1.3f;
//                                break;
//                            case 2:
//                                f = 1.5f;
//                                break;
                            default:
                                f = 1.1f;
                                break;
                        }
                        if (headingLine == 0) {
                            nextSpannable.setSpan(new RelativeSizeSpan(f), 0, ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            nextSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new RelativeSizeSpan(f), 0, ordinalIndexOf(result, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), 0, ordinalIndexOf(result, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        } else {
                            nextSpannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(lowercasePoem, "\n", headingLine), ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            nextSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ordinalIndexOf(lowercasePoem, "\n", headingLine), ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), ordinalIndexOf(lowercasePoem, "\n", headingLine) + 1, ordinalIndexOf(lowercasePoem, "\n", headingLine) + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            int a = 0;
                            for(int overflowingLine: originalOverFlowingLines){
                                if(overflowingLine - 1 < headingLine){
                                    a++;
                                }
                            }
                            spannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(result, "\n", headingLine + a), ordinalIndexOf(result, "\n", headingLine + a + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), ordinalIndexOf(result, "\n", headingLine + a), ordinalIndexOf(result, "\n", headingLine + a + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        int i = 1;
                        for(int overflowingLine: originalOverFlowingLines){
                            if(overflowingLine - 1 < headingLine){
                                i++;
                            }
                        }

                        for(int overflowingLine: originalOverFlowingLines){
                            if(overflowingLine - 1 == headingLine){

                                spannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(result, "\n", headingLine + i), ordinalIndexOf(result, "\n", headingLine + i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), ordinalIndexOf(result, "\n", headingLine + i), ordinalIndexOf(result, "\n", headingLine + i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                i++;
                            }
                        }

                    }
                    alreadyColoredWords.clear();
                    int position = poem.getSelectionEnd();
                    poem.setText(nextSpannable);
                    outputView.setText(spannable);
                    if (!titleHelper.hasFocus()) {
                        poem.setSelection(position);
                    }
                    Spannable str = poem.getText();
                    Object spansToRemove[] = str.getSpans(0, poem.length() - 1, Object.class);
                    for (Object span : spansToRemove) {
                        if (span instanceof CharacterStyle)
                            spannable.removeSpan(span);
                    }
                    colorCounter = 0;

                }
            } else {
                if (!currentWordIndexes.isEmpty()) {
                    nextSpannable = new SpannableString(poem.getText().toString());
                    //}
                    String lowercasePoem = poem.getText().toString().toLowerCase();
                    SortedSet<Integer> keys = new TreeSet<>(currentWordIndexes.keySet());
                    SortedSet<String> values = new TreeSet<>(currentWordIndexes.values());
                    for (Integer index : keys) {
                        String word = currentWordIndexes.get(index).toLowerCase();
                        if (!alreadyColoredWords.contains(word) && !ignoredWords.contains(word)) {
                            ArrayList<String> rhymes = new ArrayList<>();
                            boolean nearRhymeChecker = false;
                            boolean createdrhymeChecker = false;
                            if (highSensitivity) {
                                if (foundWordNearRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                    nearRhymeChecker = true;

                                }
                            }
                            if (createdRhymes.containsKey(word.toLowerCase())) {
                                createdrhymeChecker = true;
                            }
                            if (foundWordRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")) || nearRhymeChecker || createdrhymeChecker) {
                                if (foundWordRhymes.containsKey(word.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                    if (!foundWordRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0).equals("")) {
                                        rhymes.addAll(foundWordRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                    }
                                }
                                if (nearRhymeChecker) {
                                    if (!foundWordNearRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0).equals("")) {
                                        rhymes.addAll(foundWordNearRhymes.get(word.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                    }
                                }
                                try {
                                    if (createdrhymeChecker) {
                                        if (createdRhymes.get(word.toLowerCase()).get(0) != "") {
                                            ArrayList<String> allCreatedRhymes = new ArrayList<>(createdRhymes.get(word.toLowerCase()));
                                            boolean allRhymesRetrieved = false;
                                            while (!allRhymesRetrieved) {
                                                boolean atLeastOneNew = false;
                                                for (String createdRhyme : allCreatedRhymes) {
                                                    if (createdRhymes.get(createdRhyme.toLowerCase()).get(0) != "") {
                                                        ArrayList<String> someCreatedRhymes = new ArrayList<>(createdRhymes.get(createdRhyme));
                                                        for (String rhyme : someCreatedRhymes) {
                                                            if (!allCreatedRhymes.contains(rhyme) && !rhyme.equals(word)) {

                                                                allCreatedRhymes.add(rhyme);
                                                                atLeastOneNew = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                if (atLeastOneNew == false) {
                                                    allRhymesRetrieved = true;
                                                }
                                            }
                                            rhymes.addAll(allCreatedRhymes);
                                            for (String createdRhyme : allCreatedRhymes) {
                                                if (foundWordRhymes.containsKey(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", ""))) {
                                                    if (foundWordRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                                        rhymes.addAll(foundWordRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                                    }
                                                }
                                                if (nearRhymeChecker) {
                                                    try {
                                                        if (foundWordNearRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")).get(0) != "") {
                                                            rhymes.addAll(foundWordNearRhymes.get(createdRhyme.toLowerCase().replaceAll("'", "").replaceAll("'s", "")));
                                                        }
                                                    } catch (NullPointerException e) {

                                                    }
                                                }
                                            }


                                        }
                                    }
                                }catch (Exception e){
                                    Log.d("errorcatCreatedRhyme", e+"");
                                }
                                for (String ignoredWord : ignoredWords) {
                                    rhymes.remove(ignoredWord);
                                }
                                if (!rhymes.isEmpty()) {


                                    if (!Collections.disjoint(values, rhymes)) {
                                        int r = colors[colorCounter];
                                        colorCounter++;
                                        int g = colors[colorCounter];
                                        colorCounter++;
                                        int b = colors[colorCounter];
                                        colorCounter++;
                                        if (colorCounter == 63) {
                                            colorCounter = 0;
                                        }
                                        try {
                                            while (index >= 0 && (index + word.length() + 1) < poem.getText().toString().length()) {
                                                boolean goodToGoR = false;
                                                //make sure the word isn't inside another word.
                                                if (index == 0) {
                                                    if (!lowercasePoem.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                        goodToGoR = true;
                                                    }
                                                } else {
                                                    // FIXX java.lang.StringIndexOutOfBoundsException:
                                                    if (!lowercasePoem.substring(index - 1, index).matches("[a-zA-Z0-9']+")) {
                                                        if (!lowercasePoem.substring(index + word.length(), index + word.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                            goodToGoR = true;
                                                        }
                                                    }
                                                }
                                                if (goodToGoR) {
                                                    if (index >= 0 && index < lowercasePoem.length()) {
                                                        alreadyColoredWords.add(word);
                                                        nextSpannable.setSpan(new ForegroundColorSpan(rgb(r, g, b)), index, index + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                    }
                                                }

                                                index = lowercasePoem.toLowerCase().indexOf(word, index + 1);
                                            }
                                        } catch (IndexOutOfBoundsException e) {

                                        }
                                        for (String rhyme : rhymes) {

                                            try {
                                                int rhymeIndex = lowercasePoem.indexOf(rhyme);
                                                while (rhymeIndex >= 0 && (rhymeIndex + rhyme.length() + 1) < poem.getText().toString().length()) {
                                                    boolean goodToGoR = false;
                                                    //make sure the word isn't inside another word.
                                                    if (rhymeIndex == 0) {
                                                        if (!lowercasePoem.substring(rhymeIndex + rhyme.length(), rhymeIndex + rhyme.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                            goodToGoR = true;
                                                        }
                                                    } else {
                                                        if (!lowercasePoem.substring(rhymeIndex - 1, rhymeIndex).matches("[a-zA-Z0-9']+")) {
                                                            if (!lowercasePoem.substring(rhymeIndex + rhyme.length(), rhymeIndex + rhyme.length() + 1).matches("[a-zA-Z0-9']+")) {
                                                                goodToGoR = true;
                                                            }
                                                        }
                                                    }
                                                    if (goodToGoR) {
                                                        if (rhymeIndex >= 0 && rhymeIndex < lowercasePoem.length()) {
                                                            alreadyColoredWords.add(rhyme);
                                                            nextSpannable.setSpan(new ForegroundColorSpan(rgb(r, g, b)), rhymeIndex, rhymeIndex + rhyme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                        }
                                                    }
                                                    rhymeIndex = lowercasePoem.toLowerCase().indexOf(rhyme, rhymeIndex + 1);
                                                }
                                            } catch (IndexOutOfBoundsException e) {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (String boldWord : boldWords) {
                        int index = poem.getText().toString().indexOf(boldWord);
                        while (index >= 0) {
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + boldWord.length() - 1, index + boldWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new StyleSpan(Typeface.BOLD), index + 1, index + boldWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(boldWord, index + 1);
                        }
                    }
                    for (String italicWord : italicWords) {
                        int index = poem.getText().toString().indexOf(italicWord);
                        while (index >= 0) {
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), index + italicWord.length() - 1, index + italicWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new StyleSpan(Typeface.ITALIC), index + 1, index + italicWord.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = poem.getText().toString().indexOf(italicWord, index + 1);
                        }
                    }
                    for (Integer headingLine : headings.keySet()) {
                        Float f = 1.1f;
                        switch (headings.get(headingLine)) {
                            case 0:
                                f = 1.3f;
                                break;
//                            case 0:
//                                f = 1.1f;
//                                break;
//                            case 1:
//                                f = 1.3f;
//                                break;
//                            case 2:
//                                f = 1.5f;
//                                break;
                            default:
                                f = 1.1f;
                                break;
                        }
                        if (headingLine == 0) {
                            nextSpannable.setSpan(new RelativeSizeSpan(f), 0, ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            nextSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new RelativeSizeSpan(f), 0, ordinalIndexOf(result, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), 0, ordinalIndexOf(result, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        } else {
                            nextSpannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(lowercasePoem, "\n", headingLine), ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            nextSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ordinalIndexOf(lowercasePoem, "\n", headingLine), ordinalIndexOf(lowercasePoem, "\n", headingLine + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nextSpannable.setSpan(new ForegroundColorSpan(argb(50, 130, 130, 130)), ordinalIndexOf(lowercasePoem, "\n", headingLine), ordinalIndexOf(lowercasePoem, "\n", headingLine) + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            int a = 0;
                            for(int overflowingLine: originalOverFlowingLines){
                                if(overflowingLine - 1 < headingLine){
                                    a++;
                                }
                            }
                            spannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(result, "\n", headingLine + a), ordinalIndexOf(result, "\n", headingLine + a + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), ordinalIndexOf(result, "\n", headingLine + a), ordinalIndexOf(result, "\n", headingLine + a + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        int i = 1;
                        for(int overflowingLine: originalOverFlowingLines){
                            if(overflowingLine - 1 < headingLine){
                                i++;
                            }
                        }

                        for(int overflowingLine: originalOverFlowingLines){
                            if(overflowingLine - 1 == headingLine){

                                spannable.setSpan(new RelativeSizeSpan(f), ordinalIndexOf(result, "\n", headingLine + i), ordinalIndexOf(result, "\n", headingLine + i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannable.setSpan(new ForegroundColorSpan(argb(0, 0, 0, 0)), ordinalIndexOf(result, "\n", headingLine + i), ordinalIndexOf(result, "\n", headingLine + i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                i++;
                            }
                        }

                    }
                    alreadyColoredWords.clear();
                    int position = poem.getSelectionEnd();
                    outputView.setText(spannable);
                    poem.setText(nextSpannable);
                    if (!titleHelper.hasFocus()) {
                        poem.setSelection(position);
                    }
                    Spannable str = poem.getText();
                    Object spansToRemove[] = str.getSpans(0, poem.length() - 1, Object.class);
                    for (Object span : spansToRemove) {
                        if (span instanceof CharacterStyle)
                            spannable.removeSpan(span);
                    }
                    colorCounter = 0;
                }
            }
            processing = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EditText poem = (EditText) findViewById(R.id.poem);
//                    poem.setFocusable(true);
//                    poem.setFocusableInTouchMode(true);
                    if (firstRun) {
                        firstRun = false;
                    } else {
                        if (!titleHelper.hasFocus()) {
                            poem.requestFocus();
                        }

                    }
                }
            });
            final pl.droidsonroids.gif.GifImageView cornerLoader = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imageView5);
            cornerLoader.postDelayed(new Runnable() {
                public void run() {
                    cornerLoader.setVisibility(View.GONE);
                }
            }, 500);
        }

    }

    int syllablesPerLine(String input, TextView tv) {

        lineCount++;
        int syllableCount = 0;
        if (input.trim().equals("")) {
            return syllableCount;
        }
        String wordsString = input;
        wordsString = wordsString.replaceAll("'", "");
        wordsString = wordsString.replaceAll("'s", "");
        wordsString = wordsString.replaceAll("[^a-zA-Z0-9]+", " ");
        wordsString = wordsString.trim().replaceAll(" +", " ");

        String[] words = wordsString.split(" ");

        //for every word, retrieve a JSON file from datamuse API and increment syllableCount with the syllables in that word
        for (String word : words) {
            //connect to the API using the next, specifying syllables and maximum one return
            if (!foundWordSyllables.containsKey(word.toLowerCase()) || (!foundWordNearRhymes.containsKey(word.toLowerCase()) && highSensitivity)) {
                try {

                    String fetchedJSON = "";
                    URL url = new URL("http://api.datamuse.com/words?sp=" + word + "&md=s&max=1");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // Read all the text returned by the server

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String str;
                    // Read each line of "in" until done, adding each to "response"
                    while ((str = in.readLine()) != null) {
                        // str is one line of text readLine() strips newline characters
                        fetchedJSON += str;
                    }
                    in.close();

                    //parse the syllables from the JSON and increment syllableCount
                    if ((!fetchedJSON.equals("[]")) && (!word.matches(".*\\d+.*"))) {
                        if (fetchedJSON.contains("word\":\"")) {
                            if (fetchedJSON.length() > fetchedJSON.indexOf("word") + 8) {
                                String wordReturned = fetchedJSON.substring(fetchedJSON.indexOf("word") + 7, fetchedJSON.lastIndexOf("\",\""));
                                if (wordReturned.equals(word.toLowerCase())) {
                                    String syllables = fetchedJSON.substring(fetchedJSON.lastIndexOf("Syllables") + 11, fetchedJSON.lastIndexOf("}"));
                                    syllableCount += Integer.parseInt(syllables);
                                    foundWordSyllables.put(word.toLowerCase(), syllables);
                                    if (!foundWordRhymes.containsKey(word.toLowerCase())) {
                                        getRhymes(word);
                                    }
                                    if (highSensitivity) {
                                        if (!foundWordNearRhymes.containsKey(word.toLowerCase())) {
                                            getNearRhymes(word);
                                        }
                                    }
                                } else {
                                    if (poemSelectionHelper.isShown()) {
                                        flagLine();
                                    } else {
                                        flagTextView(tv);
                                    }
                                }
                            }
                        }
                    } else {
                        if (poemSelectionHelper.isShown()) {
                            flagLine();
                        } else {
                            flagTextView(tv);
                        }
                    }


                } catch (IOException e) {
                    System.out.println("IO exception occurred");
                }
            } else {
                syllableCount += Integer.parseInt(foundWordSyllables.get(word.toLowerCase()));
            }
        }

        return syllableCount;
    }

    void flagLine() {
        if (!flaggedLines.contains(lineCount)) {
            flaggedLines.add(lineCount);
        }

    }

    void flagTextView(TextView textview) {
        flaggedMeasureTextviews.add(textview);
    }

    int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    void getRhymes(String word) {
        //check if any previous words are one of this words rhymes.
        try {
            String fetchedJSON = "";
            URL url = new URL("http://api.datamuse.com/words?rel_rhy=" + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                fetchedJSON += str;
            }
            in.close();
            ArrayList<String> rhymes = new ArrayList<>();
            String[] seperateJSONs = fetchedJSON.split(",\\{");

            if (!fetchedJSON.equals("[]")) {
                for (String JSON : seperateJSONs) {
                    if (JSON.contains("word\":\"")) {
                        if ((JSON.length() > JSON.indexOf("word") + 8) && (JSON.indexOf(",") > JSON.indexOf("word") + 8)) {
                            String rhyme = JSON.substring(JSON.indexOf("word") + 7, JSON.indexOf(",") - 1).toLowerCase();
                            if (!rhyme.contains(" ")) {
                                rhymes.add(rhyme);
                            }
                        }
                    }
                }
                if (rhymes.size() == 0) {
                    rhymes.add("");
                }
                foundWordRhymes.put(word.toLowerCase(), rhymes);

            } else {
                rhymes.add("");
                foundWordRhymes.put(word.toLowerCase(), rhymes);
            }


        } catch (IOException e) {
            System.out.println("IO exception occurred");
        }
    }

    void getNearRhymes(String word) {
        //check if any previous words are one of this words rhymes.
        try {
            String fetchedJSON = "";
            URL url = new URL("http://api.datamuse.com/words?rel_nry=" + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                fetchedJSON += str;
            }
            in.close();
            ArrayList<String> nearRhymes = new ArrayList<>();
            String[] seperateJSONs = fetchedJSON.split(",\\{");
            if (!fetchedJSON.equals("[]")) {
                for (String JSON : seperateJSONs) {
                    if (JSON.contains("word\":\"")) {
                        if ((JSON.length() > JSON.indexOf("word") + 8) && (JSON.indexOf(",") > JSON.indexOf("word") + 8)) {
                            String nearRhyme = JSON.substring(JSON.indexOf("word") + 7, JSON.indexOf(",") - 1).toLowerCase();
                            if (!nearRhyme.contains(" ")) {
                                nearRhymes.add(nearRhyme);
                            }
                        }
                    }
                }
                if (nearRhymes.size() == 0) {
                    nearRhymes.add("");
                }
                foundWordNearRhymes.put(word.toLowerCase(), nearRhymes);

            } else {
                nearRhymes.add("");
                foundWordNearRhymes.put(word.toLowerCase(), nearRhymes);
            }


        } catch (IOException e) {
            System.out.println("IO exception occurred");
        }
    }

    public static void inputSelection(int start, int end, int id) {

        if (inputWordSelectionHelper.isShown()) {
            //this is causing crashes
            if (end > start) {
                if (start >= 0) {

                    if (id == notesSelectionHelper.getId()) {
                        if (!notesSelectionHelper.getText().toString().substring(start, end).contains(" ")) {
                            inputWordSelectionHelper.setText(notesSelectionHelper.getText().toString().substring(start, end));
                        }
                    } else if (id == poemSelectionHelper.getId()) {
                        if (!poemSelectionHelper.getText().toString().substring(start, end).contains(" ")) {
                            inputWordSelectionHelper.setText(poemSelectionHelper.getText().toString().substring(start, end));
                        }
                    } else if (id == wordInfoSelectionHelper.getId()) {
                        if (!wordInfoSelectionHelper.getText().toString().substring(start, end).contains(" ")) {
                            inputWordSelectionHelper.setText(wordInfoSelectionHelper.getText().toString().substring(start, end));
                        }
                    }

                }
            }

//            if (!keyboardOpen) {
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideSoftKeyboard(mainActivityHelper, poemSelectionHelper);
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                hideSoftKeyboard(mainActivityHelper, poemSelectionHelper);
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        hideSoftKeyboard(mainActivityHelper, poemSelectionHelper);
//                                        handler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                hideSoftKeyboard(mainActivityHelper, poemSelectionHelper);
//                                                handler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        hideSoftKeyboard(mainActivityHelper, poemSelectionHelper);
//                                                    }
//                                                }, 100);
//                                            }
//                                        }, 100);
//                                    }
//                                }, 100);
//                            }
//                        }, 100);
//                    }
//                }, 100);
//            }
        }
    }


    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    long userInteractionTime = 0;

    @Override
    public void onUserInteraction() {
        userInteractionTime = System.currentTimeMillis();
        super.onUserInteraction();
    }

    @Override
    public void onUserLeaveHint() {
        long uiDelta = (System.currentTimeMillis() - userInteractionTime);

        super.onUserLeaveHint();
        if (uiDelta < 100) {
            if (metronomeRunning || audioFileRunning) {
                final ImageButton metronomeIcon = (ImageButton) findViewById(R.id.metronome);
                metronomeIcon.performClick();
            }
            if (recorderRunning) {
                final EditText poem = (EditText) findViewById(R.id.poem);
                poem.performClick();
            }
            if (editRecordingPlaying) {
                final ImageButton editRecordingPlay = (ImageButton) findViewById(R.id.edit_recording_play_icon);
                editRecordingPlay.performClick();
            }
            if (lineRecordingPlaying && linesWithRecordings.size() > 0) {
                linesWithRecordings.get(0).performClick();
            }

        }


    }

    @Override
    public void onBackPressed() {

        if (findViewById(R.id.spinner_list).getTranslationY() == 0) {
            (findViewById(R.id.spinner_list)).animate().translationY(screenHeight);
        } else if (findViewById(R.id.settings_list).getTranslationY() == 0) {
            findViewById(R.id.settings_list).animate().translationY(-screenHeight);
        } else if (findViewById(R.id.measure_list).getTranslationY() == 0) {
            findViewById(R.id.measure_list).animate().translationY(screenHeight);
        } else if (!scrollviewHelper.isShown()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    exitMeasureModeHelper.performClick();
                }
            });
        } else if (popupBackground.isShown()) {
            openPopup(null);
        } else if (!poemSelectionHelper.isShown()) {
            final ImageButton changeMode = (ImageButton) findViewById(R.id.change_mode);
            changeMode.performClick();
        } else {
//
//
//            String softsave = getStringFromInternal("lyric" + lyricIndex + "poemsoftsave", "soft");
//            String hardsave = getStringFromInternal("lyric" + lyricIndex + "poem", "hard");
//            String softsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measuressoftsave", "soft");
//            String hardsavemeasure = getStringFromInternal("lyric" + lyricIndex + "measures", "hard");
//            if (softsave.equals(hardsave) && softsavemeasure.equals(hardsavemeasure)) {
            final Intent myIntent = new Intent(this, Main2Activity.class);
            startActivity(myIntent);
            finish();
//            } else {
//                hardSave(true);
//            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = powerManager.isInteractive();
        } else {
            isScreenOn = powerManager.isScreenOn();
        }

        if (!isScreenOn) {
            if (metronomeRunning) {
                final ImageButton metronomeIcon = (ImageButton) findViewById(R.id.metronome);
                metronomeIcon.performClick();
            }
            if (recorderRunning) {
                final EditText poem = (EditText) findViewById(R.id.poem);
                poem.performClick();
            }
            if (editRecordingPlaying) {
                final ImageButton editRecordingPlay = (ImageButton) findViewById(R.id.edit_recording_play_icon);
                editRecordingPlay.performClick();
            }
            if (lineRecordingPlaying) {
                linesWithRecordings.get(0).performClick();
            }

        }
    }
//
//    @Override
//    protected void onShowKeyboard(int keyboardHeight) {
//        keyboardOpen = true;
//        final LinearLayout spinnerList = (LinearLayout) findViewById(R.id.spinner_list);
//        final LinearLayout measureList = (LinearLayout) findViewById(R.id.measure_list);
//        final LinearLayout settingList = (LinearLayout) findViewById(R.id.settings_list);
//        final ImageButton optionsButton = (ImageButton) findViewById(R.id.settings);
//        final ImageButton openSpinner = (ImageButton) findViewById(R.id.open_spinner);
//        final ImageButton openMeasureSpinner = (ImageButton) findViewById(R.id.open_spinner);
//        spinnerList.animate().translationY(screenHeight);
//        openSpinner.setImageResource(R.drawable.openspinner);
//        measureList.animate().translationY(screenHeight);
//        openMeasureSpinner.setImageResource(R.drawable.openspinner);
//        settingList.animate().translationY(-screenHeight);
//
//
//            screenHeight = (int) (findViewById(R.id.main_activity).getHeight() + ((mainToolbarHelper.getHeight())));
//            if (((notesTitleHelper.getHeight() / 2) + notesSelectionHelper.getHeight()) > (screenHeight - (2 * (mainToolbarHelper.getHeight() + spinnerToolbarHelper.getHeight())))) {
//                notesSelectionHelper.getLayoutParams().height = (int) (screenHeight - (2 * (mainToolbarHelper.getHeight() + spinnerToolbarHelper.getHeight())));
//                notesSelectionHelper.requestLayout();
//            }
//            if (((wordInfoTitleHelper.getHeight() / 2) + wordInfoHelper.getHeight()) > (screenHeight - (2 * (mainToolbarHelper.getHeight() + spinnerToolbarHelper.getHeight())))) {
//                wordInfoHelper.getLayoutParams().height = (int) (screenHeight - (2 * (mainToolbarHelper.getHeight() + spinnerToolbarHelper.getHeight())));
//                wordInfoHelper.requestLayout();
//
//        }
//    }
//
//    @Override
//    protected void onHideKeyboard() {
//        keyboardOpen = false;
//        if (scrollviewHelper.isShown()) {
//            spinnerToolbarHelper.setVisibility(View.VISIBLE);
//        } else {
//            spinnerToolbarHelper.setVisibility(View.VISIBLE);
//            measureToolbarHelper.setVisibility(View.VISIBLE);
//        }
//
//        screenHeight = (int) (findViewById(R.id.main_activity).getHeight() + ((mainToolbarHelper.getHeight() + spinnerToolbarHelper.getHeight()) / 2));
//    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 10) {
            final Uri uriSound = data.getData();
            userFileMediaPlayer = new MediaPlayer();
            try {
                userFileMediaPlayer = MediaPlayer.create(MainActivity.this, uriSound);
                userFileMediaPlayer.setLooping(true);
                userFileMediaPlayer.start();
                userFileMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer userFileMediaPlayer) {
                        userFileMediaPlayer.release();
                    }
                });
                audioFileRunning = true;
                final ConstraintLayout metronomePopup = (ConstraintLayout) findViewById(R.id.metronome_popup);
                metronomePopup.setVisibility(View.GONE);
                popupBackground.setVisibility(View.GONE);
            } catch (Exception e) {
                Toast.makeText(mainActivityHelper, "Error Playing File",
                        Toast.LENGTH_SHORT).show();
            }
        }
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    proUser = true;
                    final ConstraintLayout mainActivity = (ConstraintLayout) findViewById(R.id.main_activity);
                    mainActivity.removeView(mAdView);
                    mAdView = new AdView(MainActivity.this);
                    final Toolbar spinnerToolbar = (Toolbar) findViewById(R.id.spinner_toolbar);
                    final Toolbar barsToolbar = (Toolbar) findViewById(R.id.measure_bars_toolbar);
                    final Toolbar measureToolbar = (Toolbar) findViewById(R.id.measure_toolbar);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mainActivity);
                    constraintSet.connect(spinnerToolbar.getId(), ConstraintSet.BOTTOM, mainActivity.getId(), ConstraintSet.BOTTOM, 0);
                    constraintSet.connect(barsToolbar.getId(), ConstraintSet.BOTTOM, mainActivity.getId(), ConstraintSet.BOTTOM, 0);
                    constraintSet.connect(measureToolbar.getId(), ConstraintSet.BOTTOM, mainActivity.getId(), ConstraintSet.BOTTOM, 0);
                    constraintSet.applyTo(mainActivity);
                    ImageView ignoreListIcon = (ImageView) findViewById(R.id.checkbox_ignore_words);
                    ImageView rhymeListIcon = (ImageView) findViewById(R.id.checkbox_create_rhymes);
                    ImageView darkmodeIcon = (ImageView) findViewById(R.id.darkmode_checkbox);
                    ImageView changeThemeIcon = (ImageView) findViewById(R.id.changecolor_checkbox);
                    ImageView changeFontFamilyIcon = (ImageView) findViewById(R.id.changefont_checkbox);
                    ImageView topToolbarUnlock = (ImageView) findViewById(R.id.toggle_top_toolbar);
                    ImageView bottomToolbarUnlock = (ImageView) findViewById(R.id.toggle_bottom_toolbars);
                    topToolbarUnlock.setImageResource(R.drawable.closespinner_purple);
                    bottomToolbarUnlock.setImageResource(R.drawable.openspinner_purple);
                    ignoreListIcon.setImageResource(R.drawable.pluswhite);
                    rhymeListIcon.setImageResource(R.drawable.pluswhite);
                    darkmodeIcon.setImageResource(R.drawable.darkmodeicon);
                    changeThemeIcon.setImageResource(R.drawable.color_change);
                    changeFontFamilyIcon.setImageResource(R.drawable.fontfamily_change);
                    Toast.makeText(mainActivityHelper, "Upgrade Successful",
                            Toast.LENGTH_SHORT).show();
                    final ConstraintLayout upgradePopup = (ConstraintLayout) findViewById(R.id.upgrade_popup);
                    upgradePopup.setVisibility(View.GONE);
                    popupBackground.setVisibility(View.GONE);
                    final ConstraintLayout ratePopup = (ConstraintLayout) findViewById(R.id.rate_popup);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openPopup(ratePopup);
                            TextView rateYes = (TextView) findViewById(R.id.rate_yes);
                            TextView rateNo = (TextView) findViewById(R.id.rate_no);
                            rateYes.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("market://details?id=kmcilvai.perfectpoet");
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    // To count with Play market backstack, After pressing back button,
                                    // to taken back to our application, we need to add following flags to intent.
                                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                    try {
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=kmcilvai.perfectpoet")));
                                    }
                                    ratePopup.setVisibility(View.GONE);
                                    popupBackground.setVisibility(View.GONE);
                                }
                            });
                            rateNo.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    ratePopup.setVisibility(View.GONE);
                                    popupBackground.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
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

    public void requestBackup() {
        BackupManager bm = new BackupManager(MainActivity.this);
        bm.dataChanged();

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
            String[] lines = value.split("[\\r\\n]");
            for(String line: lines){
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }

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



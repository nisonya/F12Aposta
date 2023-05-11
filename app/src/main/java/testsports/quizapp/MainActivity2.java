package testsports.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    public int level = 0;
    ProgressBar pb;
    int quest_number=1;
    int points=0;
    public Button btn1, btn2, btn3, btn4, btnStartOver, btnNext;
    public TextView questTV, numberTV;
    public ImageView photoIV;
    public  ArrayList<String> answers = new ArrayList<String>();
    public Question mQuestion = new Question();
    public boolean to;
    private static final String FILE_NAME="MY_FILE_NAME";
    private static final String URL_STRING="URL_STRING";
    public Bundle savedInst;
    String url_FB;
    String url_SP;
    SQLiteDatabase database;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    private FirebaseRemoteConfig mfirebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInst = savedInstanceState;
        System.out.println(isBatteryLevelInRange()+"!!!!!!!!!!!!!");
        //проверка сохранена ли ссылка
        url_SP = getSharedPrefStr();
        if(url_SP=="") {
            //подключение к FireBase
            getFireBaseUrlConnection();
            getURLStr();
        }else{
            //проверка на подключение к интернету
            if(!hasConnection(this)){
                Intent intent = new Intent(MainActivity2.this, NoInternet.class);
                startActivity(intent);
            }
            else{//запускаем WebView
                browse(url_SP);
            }
        }
    }

    //включение WebView
    public void browse(String url){
        Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    //проверка эмулятора
    private boolean checkIsEmu() {
        String phoneModel = Build.MODEL;
        String buildProduct = Build.PRODUCT;
        String buildHardware = Build.HARDWARE;
        String brand = Build.BRAND;
        return (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.toLowerCase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware.equals("goldfish")
                || brand.contains("google")
                || buildHardware.equals("vbox86")
                || buildProduct.equals("sdk")
                || buildProduct.equals("google_sdk")
                || buildProduct.equals("sdk_x86")
                || buildProduct.equals("vbox86p")
                || Build.BOARD.toLowerCase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.toLowerCase(Locale.getDefault()).contains("nox")
                || buildHardware.toLowerCase(Locale.getDefault()).contains("nox")
                || buildProduct.toLowerCase(Locale.getDefault()).contains("nox"))
                || (brand.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                ||"google_sdk".equals(Build.PRODUCT)
                || "sdk_gphone_x86_arm".equals(Build.PRODUCT)
                ||"sdk_google_phone_x86".equals(Build.PRODUCT);
    }

    private boolean isBatteryLevelInRange() {
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return (1<=batLevel)&&(batLevel<=99);
    }
    //проверка интернет подключения
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private boolean getBool(){
        mfirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            Log.i("To", String.valueOf(task.getResult()));
                            String value = mfirebaseRemoteConfig.getString("to");
                            if(value.equals("true")){
                                to = true;
                            } else if(value.equals("false")) {
                                to = false;
                            } else if(value.equals("")) {
                                to= false;
                            }

                        } else {
                            Log.i("To", "null");
                        }
                    }
                });
        return to;
    }

    //получение ссылки и обработка вызова заглушки/WebView
    public void getURLStr(){
        try {
            mfirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                Log.i("Fire", String.valueOf(task.getResult()));
                                url_FB = mfirebaseRemoteConfig.getString("url");
                                if (url_FB.isEmpty()||checkIsEmu()||(!isBatteryLevelInRange())) {
                                    plug();
                                } else {
                                    Log.i("Fire", url_FB);
                                    saveToSP();
                                    browse(url_FB);
                                }

                            } else {
                                url_FB = "";
                                plug();
                                Log.i("Fire", "null2");
                            }
                        }
                    });
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            Intent intent = new Intent(MainActivity2.this, NoInternet.class);
            startActivity(intent);
        }
    }

    //получение локальной ссылки
    public String getSharedPrefStr(){
        sPref = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String url_SP = sPref.getString(URL_STRING,"");
        return url_SP;
    }

    //подключение к Firebase
    public void getFireBaseUrlConnection(){
        //подключение к FireBase
        mfirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mfirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mfirebaseRemoteConfig.setDefaultsAsync(R.xml.url_values);
    }

    //вызыв зваглушки
    public void plug(){
        setContentView(R.layout.activity_main2);
        DBHelper dbHelper = new DBHelper(this);
        database= dbHelper.getWritableDatabase();
        btn1 =findViewById(R.id.answ1);
        btn2 =findViewById(R.id.answ2);
        btn3 =findViewById(R.id.answ3);
        btn4 =findViewById(R.id.answ4);
        questTV =findViewById(R.id.quest);
        numberTV = findViewById(R.id.numberQuest);
        photoIV = findViewById(R.id.quPhoto);

        btnStartOver =findViewById(R.id.start_over);
        btnNext =findViewById(R.id.next_quest);
        btnNext.setEnabled(false);
        getDate(quest_number);
        fillDate(mQuestion);

    }
    //сохранение ссылки локально
    public void saveToSP(){
        ed = sPref.edit();
        ed.putString(URL_STRING, url_FB);
        ed.apply();
        browse(url_FB);
    }

    public void getDate(int i){
        ContentValues cv = new ContentValues();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, "_id =="+i, null, null, null,null);
        if(cursor.moveToFirst()){
            int idIndex =cursor.getColumnIndex(DBHelper.KEY_ID);
            int questIndex =cursor.getColumnIndex(DBHelper.KEY_QUEST);
            int a1Index =cursor.getColumnIndex(DBHelper.KEY_ANSWER1);
            int a2Index =cursor.getColumnIndex(DBHelper.KEY_ANSWER2);
            int a3Index =cursor.getColumnIndex(DBHelper.KEY_ANSWER3);
            int a4Index =cursor.getColumnIndex(DBHelper.KEY_ANSWER4);
            int rigntAnswIndex =cursor.getColumnIndex(DBHelper.KEY_RIGHT_ANSWER);
            int photoIndex =cursor.getColumnIndex(DBHelper.KEY_PHOTO);
            do {
                mQuestion = new Question(cursor.getInt(idIndex),
                        cursor.getString(a1Index),cursor.getString(a2Index),cursor.getString(a3Index),cursor.getString(a4Index),cursor.getString(rigntAnswIndex),
                        cursor.getString(questIndex), cursor.getString(photoIndex));
            }while(cursor.moveToNext());
        }
        else{
            Log.d("mLog","0 rows");
        }
        cursor.close();
    }

    public void fillDate(Question question){
        btn1.setText(question.getA1());
        btn2.setText(question.getA2());
        btn3.setText(question.getA3());
        btn4.setText(question.getA4());
        questTV.setText(question.getQuest_text());
        numberTV.setText("Question "+question.getId()+"/12");
        Glide
                .with(MainActivity2.this)
                .load(question.getPhoto())
                .into(photoIV);
    }

    public void getAnswer(View view) {
        switch (view.getId()){
            case R.id.answ1:
                checkAnswer(btn1);
                break;
            case R.id.answ2:
                checkAnswer(btn2);
                break;
            case R.id.answ3:
                checkAnswer(btn3);
                break;
            case R.id.answ4:
                checkAnswer(btn4);
                break;
        }
    }

    public void checkAnswer(Button btn){
        if(btn.getText().equals(mQuestion.getRightQnsw())) {
            btn.setBackgroundColor(getResources().getColor(R.color.green));
            points=points+1;
        }
        else  btn.setBackgroundColor(getResources().getColor(R.color.red));
        btnNext.setEnabled(true);
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
    }

    public void goToNext(View view) {
        if(quest_number==12) finishGame();
        quest_number = quest_number+1;
        getDate(quest_number);
        fillDate(mQuestion);
        btnNext.setEnabled(false);
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
        btn1.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btn2.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btn3.setBackgroundColor(getResources().getColor(R.color.purple_500));
        btn4.setBackgroundColor(getResources().getColor(R.color.purple_500));
    }

    public void finishGame(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView text_dialog = (TextView) dialog.findViewById(R.id.dialog_remainder);
        text_dialog.setText("Congratulations! you scored "+points+" points");

        Button btn_ave= (Button) dialog.findViewById(R.id.tryAgain);
        btn_ave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quest_number=1;
                points=0;
                getDate(quest_number);
                fillDate(mQuestion);
                dialog.dismiss();
            }
        });

    }
    public void startOver(View view) {
        quest_number=1;
        points=0;
        getDate(quest_number);
        fillDate(mQuestion);
    }
}
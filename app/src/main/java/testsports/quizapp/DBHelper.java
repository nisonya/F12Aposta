package testsports.quizapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public final static int DATABASE_VERSION=1;
    public final static String DATABASE_NAME="my_db";
    public final static String TABLE_NAME="question";


    public final static String KEY_ID="_id";
    public final static String KEY_QUEST="quest";
    public final static String KEY_ANSWER1="answer1";
    public final static String KEY_ANSWER2="answer2";
    public final static String KEY_ANSWER3="answer3";
    public final static String KEY_ANSWER4="answer4";
    public final static String KEY_RIGHT_ANSWER="right_answer";
    public final static String KEY_PHOTO="photo";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] quest= {"How long is a marathon?", " How many players are there on a baseball team?","Which country won the World Cup 2018?","https://img.olympicchannel.com/images/image/private/t_social_share_thumb/f_auto/primary/qjxgsf7pqdmyqzsptxju",
                "What sport is played with this ball?", "What sport is played with this ball?","Which swimming style is not allowed in the Olympics?",
                "For which team did Michael Jordan spend most of his career playing?"
        };
        String[] answ1= {"56.5 kilometres","5","Russia","Racquetball","Cricket","Polo","Dog paddle",
        };
        String[] answ2= {"42.195 kilometres","8","France","TagPro","Golf","Rugby","Backstroke","Chicago Bulls"
        };
        String[] answ3= {"36.850 kilometres","11","Argentina","Stickball","Baseball","Lacrosse","Freestyle",
        };
        String[] answ4= {"25 kilometres","9","Croatia","Tennis","Tennis","Dodgeball","Butterfly",
        };
        String[] right_answer= {"42.195 kilometres","9","France","Tennis","Baseball","Lacrosse","Dog paddle","Chicago Bulls"
        };
        String[] photo= {"https://medias.paris2024.org/uploads/2022/10/Parcours-Jeux-Olympiques-de-Tokyo-2020-_-athletisme-marathon-min-scaled.jpg?x-oss-process=image/resize,w_2560,h_1617,m_lfit/format,jpeg",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/Mookie_Betts_hitting_the_ball_%2836478781664%29.jpg/640px-Mookie_Betts_hitting_the_ball_%2836478781664%29.jpg",
                "https://img.olympicchannel.com/images/image/private/t_social_share_thumb/f_auto/primary/qjxgsf7pqdmyqzsptxju",
                "https://lh6.googleusercontent.com/NZGz5QujUlssRFhUqzaf6SivYhpyrvR1n_gLxRL0WogUYKJJz2yQM0K_WKxNLjSehdryxQWQmQp4atHyV_UmG0NOpbt2Ojvm18sNZ4ZghENoF8CeHc79ZxSYRPsJmdn0mj3V3MnY8QShp3tfvQ",
                "https://lh4.googleusercontent.com/47L44lPj89mD4IovNMhAeiQ0o9ChK_JRQM9EwqmhirRJ5JPDdbKkVzHwcSHXDSLQpuJZPtQVrkVXSpTY_Sfb89rovqpMCjq25PJ0FOX_KFlPrplWpjCddQviFm3oOrFJEeVqjh5juCYIbP-5Jw",
                "https://lh3.googleusercontent.com/ukyun0LPuapHOP1Qp2O5TKxjTnWD66Bbw2_aaHE0uPdcbWNakKspxJUoe-w4vQWRJn36p4QrZ3ozNK1PBNsgQ5-Zr1ZaqE3uO_TNor8sMhATgUudQ7sdrqeMXUzSKvLevH2LdhER_JZpQe624w",
                "https://domf5oio6qrcr.cloudfront.net/medialibrary/9833/GettyImages-526245433.jpg",

        };


        ContentValues cv = new ContentValues();
        //создаем таблицу
        db.execSQL("create table if not exists "+ TABLE_NAME + "("
                + KEY_ID+" integer primary key autoincrement, "+KEY_QUEST +" text, "
                +KEY_ANSWER1+" text, "+KEY_ANSWER2+" text, "+KEY_ANSWER3+" text, "+KEY_ANSWER4+" text, "
                +KEY_RIGHT_ANSWER+" text, "+KEY_PHOTO+" text"+")");

        Log.d("mLog","Created");
        //заполняем её
        for(int i =0;i< quest.length;i++){
            cv.clear();
            cv.put(KEY_QUEST, quest[i]);
            cv.put(KEY_ANSWER1, answ1[i]);
            cv.put(KEY_ANSWER2, answ2[i]);
            cv.put(KEY_ANSWER3, answ3[i]);
            cv.put(KEY_ANSWER4, answ4[i]);
            cv.put(KEY_RIGHT_ANSWER, right_answer[i]);
            cv.put(KEY_PHOTO, photo[i]);
            db.insert(TABLE_NAME, null, cv);
            Log.d("mLog","added");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }
}


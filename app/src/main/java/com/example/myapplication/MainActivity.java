package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView questionTextView, resultTextView, scoreTextView;
    private LinearLayout answersContainer;
    private int correctAnswer;
    private int numberOfOptions;  // מספר האופציות שנבחרות
    private int correctAnswersCount = 0;  // כמות התשובות הנכונות
    private int totalQuestionsCount = 0;  // כמות השאלות שנשאלו

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול של ה-TextViews ו-LinearLayout
        questionTextView = findViewById(R.id.questionTextView);
        resultTextView = findViewById(R.id.resultTextView);
        scoreTextView = findViewById(R.id.scoreTextView);  // אתחול של TextView להציג את הסקירה
        answersContainer = findViewById(R.id.answersContainer);

        // אתחול מספר אופציות (ברירת מחדל 5)
        numberOfOptions = 5;

        // צור שאלה ראשונה
        generateQuestion();

        // כל פעם שתלחץ על השאלה תוכל לשנות את מספר האופציות
        questionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();  // מציג את דיאלוג הבחירה של מספר האופציות
            }
        });

        // כפתור שינוי מספר האופציות
        Button changeOptionsButton = findViewById(R.id.changeOptionsButton);
        changeOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();  // מציג את דיאלוג הבחירה של מספר האופציות
            }
        });
    }

    // מציג את דיאלוג הבחירה של מספר האופציות (1-5)
    private void showOptionsDialog() {
        final String[] options = {"1", "2", "3", "4", "5"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Number of Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numberOfOptions = Integer.parseInt(options[which]);
                        generateQuestion();  // צור שאלה חדשה עם מספר האופציות החדש
                    }
                })
                .setPositiveButton("Cancel", null);  // כפתור ביטול לדיאלוג

        builder.create().show();
    }

    // יצירת שאלה אקראית עם תשובות אקראיות
    private void generateQuestion() {
        Random rand = new Random();

        // מספרים אקראיים לשאלה
        int num1 = rand.nextInt(10) + 1;
        int num2 = rand.nextInt(10) + 1;

        // בחירה אקראית של סוג פעולה מתמטית
        int operation = rand.nextInt(4); // 0: חיבור, 1: חיסור, 2: כפל, 3: חילוק

        // ביצוע הפעולה המתמטית
        switch (operation) {
            case 0: // חיבור
                correctAnswer = num1 + num2;
                questionTextView.setText("What is " + num1 + " + " + num2 + "?");
                break;
            case 1: // חיסור
                // לוודא שלא נבצע חיסור של מספר קטן יותר ממספר גדול יותר
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                correctAnswer = num1 - num2;
                questionTextView.setText("What is " + num1 + " - " + num2 + "?");
                break;
            case 2: // כפל
                correctAnswer = num1 * num2;
                questionTextView.setText("What is " + num1 + " * " + num2 + "?");
                break;
            case 3: // חילוק
                // לוודא שהחילוק יתן תוצאה שלמה (בלי שארית)
                num2 = rand.nextInt(9) + 1;  // לא לאפס את num2
                while (num1 % num2 != 0) {
                    num1 = rand.nextInt(10) + 1;
                }
                correctAnswer = num1 / num2;
                questionTextView.setText("What is " + num1 + " / " + num2 + "?");
                break;
        }

        // קביעת האופציות לשאלה
        setAnswerOptions();
    }

    // קביעת האופציות לשאלה בהתבסס על מספר האופציות שנבחר
    private void setAnswerOptions() {
        Random rand = new Random();
        answersContainer.removeAllViews();  // מנקה את התשובות הקודמות

        String[] options = new String[numberOfOptions];

        // ממקמים את התשובה הנכונה במקום אקראי
        int correctPosition = rand.nextInt(numberOfOptions);
        options[correctPosition] = String.valueOf(correctAnswer);

        // ממלאים את האופציות בשגיאות אקראיות
        for (int i = 0; i < numberOfOptions; i++) {
            if (options[i] == null) {
                options[i] = String.valueOf(rand.nextInt(20) + 1);  // תשובה שגויה אקראית
            }
        }

        // יצירת כפתורי תשובות
        for (int i = 0; i < numberOfOptions; i++) {
            Button answerButton = new Button(MainActivity.this);
            answerButton.setText(options[i]);
            answerButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(((Button) v).getText().toString());  // בדיקת התשובה
                }
            });
            answersContainer.addView(answerButton);
        }
    }

    // בדיקת אם התשובה שנבחרה נכונה
    private void checkAnswer(String selectedAnswer) {
        totalQuestionsCount++;

        if (Integer.parseInt(selectedAnswer) == correctAnswer) {
            correctAnswersCount++;
            resultTextView.setText("Correct!");
            resultTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            resultTextView.setText("Wrong answer. Try again!");
            resultTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        // עדכון טקסט הסקירה
        scoreTextView.setText("Correct Answers: " + correctAnswersCount + " / " + totalQuestionsCount);

        // אחרי זמן קצר, הצגת שאלה חדשה
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                generateQuestion();  // יצירת שאלה חדשה לאחר 1.5 שניות
            }
        }, 1500);  // המתן 1.5 שניות לפני הצגת שאלה חדשה
    }
}

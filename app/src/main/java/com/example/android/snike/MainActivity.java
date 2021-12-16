package com.example.android.snike;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class MainActivity extends Activity {

    // Экземпляр SnakeGame
    SnakeGame mSnakeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Получить размер экрана в пикселях
        Display display = getWindowManager().getDefaultDisplay();

        // Инициализировать результат в объекте Point
        Point size = new Point();
        display.getSize(size);

        // Создайте новый экземпляр SnakeEngine
        mSnakeGame = new SnakeGame(this, size);

        Log.d("myLogs", "size.x: " + size.x + ", size.y: " + size.y );

        // Сделайте SnakeEngine вид деятельности
        setContentView(mSnakeGame);
    }

    // Начать обсуждение в SnakeEngine

    @Override
    protected void onResume() {
        super.onResume();
        mSnakeGame.resume();
    }


    // Останавливаем поток в SnakeEngine


    @Override
    protected void onPause() {
        super.onPause();
        mSnakeGame.pause();
    }
}
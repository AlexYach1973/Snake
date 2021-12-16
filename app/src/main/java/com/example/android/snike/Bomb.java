package com.example.android.snike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.Random;

public class Bomb {

    // Расположение бомбы на сетке
    // Не в пикселях
    private Point location = new Point();

    // Диапазон значений
    private Point mSpawnRange;
    private int mSize;

    // Изображение, представляющее бомбу
    private Bitmap mBitmapBomb;

    // Настроить конструктор
    Bomb(Context context, Point sp, int s) {

        // Пройденный диапазон
        mSpawnRange = sp;
        // Размер бомбьы
        mSize = s;
        // Скрыть бомбу за кабром, пока игра не началась
        location.x = -10;

        // Загрузка изображение и растровое изображение
        mBitmapBomb = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb_75_75);

        // Изменение размеров растрового изображения
        mBitmapBomb = Bitmap.createScaledBitmap(mBitmapBomb, s, s,false);

    }

    // Это вызывается каждый раз, когда сьедается бомба
    void spawn() {
        // Два случайных значения и поместить туда бомбу
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;

        Log.d("myLogs", "Bomb.class, random X: " + location.x + ", Y: " + location.y);

    }

    // Сообщить SnakeGame, где находится яблоко
    // SnakeGame может поделиться этим со змеей
    Point getLocation() {
        return location;
    }

    // Нарисуем бомбу
    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapBomb, location.x * mSize, location.y * mSize, paint);

    }



}

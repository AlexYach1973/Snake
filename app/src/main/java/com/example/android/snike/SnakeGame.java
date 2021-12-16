package com.example.android.snike;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SnakeGame extends SurfaceView implements Runnable {

    // Объекты для игрового цикла / потока
    private Thread mThread = null;

    // Контроль паузы между обновлениями
    private long mNextFrameTime;

    // Игра идет или приостановлена?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // Звуковые эффекты
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // Размер в сегментах игровой области
    private final int NUM_BLOCK_WIDE = 20;
    private int mNumBlocksHigh;

    // Сколько очков у игрока
    private int mScore;

    // Объекты для рисования
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // Змея
    private Snake mSnake;

    // Бомба
    private Bomb mBomb;

    // это метод конструктора, который вызывается из SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Определите сколько пикселей в каждом блоке
        int blockSize = size.x / NUM_BLOCK_WIDE;

        Log.d("myLogs", "сколько пикселей в каждом блоке- blockSize: " +
                blockSize);

        // Сколько блоков одинакового размера уместится в высоту
        mNumBlocksHigh = size.y / blockSize;

        Log.d("myLogs", "Сколько блоков одинакового размера уместится в высоту- " +
                "mNumBlocksHigh: " + mNumBlocksHigh);

        // Инициализируйте SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Подготовте звуки в памяти
//            descriptor = assetManager.openFd(get_bomb.ogg);
//            mEat_ID = mSP.load(descriptor, 0);

//            descriptor = assetManager.openFd(snake_death.ogg);
//            mCrashID = mSP.load(descriptor, 0);
        } catch (Exception e) {
            // Error
        }

        // Инициализировать объекты
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Вызовите конструкторы двух наших игровых объектов

        mBomb = new Bomb(context, new Point(NUM_BLOCK_WIDE, mNumBlocksHigh), blockSize);

//        mBomb = new Bomb(context, new Point(size.x - 2*blockSize, size.y - 2*blockSize),
//                blockSize);

        mSnake = new Snake(context, new Point(NUM_BLOCK_WIDE, mNumBlocksHigh), blockSize);

    }

    // Новая игра
    public void newGame() {

        // Сбросить змею
        mSnake.reset(NUM_BLOCK_WIDE, mNumBlocksHigh);

        // Приготовить бомбу
        mBomb.spawn();

        // Сбросить mScore
        mScore = 0;

        // Настройте mNextFrameTime, чтобы запускалось обновление
        mNextFrameTime = System.currentTimeMillis();
    }

    // Обрабатываем игровой цикл

    @Override
    public void run() {
        while (mPlaying) {

            try {
                // Регулироум скорость
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!mPaused) {
                // Обновляет 10 раз в секунду
                if (updateRquired()) {
                    update();
                }
            }
            draw();
        }
    }

    // Проверка, не пришло ли время обновлять
    public boolean updateRquired() {

        // Запускаем со скоростьб 10 кадров в секунду
        final long TARGET_FPS = 10;

        // В секунду 1000 миллисекунд
        final long MILLIS_PER_SECOND = 1000;

        // Мы должны обновить фрейм
        if (mNextFrameTime <= System.currentTimeMillis()) {
            // Прошла десятая секунда

            // Настроить, ятобы обновить и нарисовать
            // методы выполняются
            return true;

        }

        return false;
    }

    //  Обновить все игровые объекты
    public void update() {

        // Переместитеп змею
        mSnake.move();

        // Голова змеи съела бомбу?
        if (mSnake.checkDinner(mBomb.getLocation())) {

            mBomb.spawn();

            // Добавить в счет
            mScore = mScore + 1;

            // Play & sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Змея умерла?
        if (mSnake.detectDeath()) {
            // Поставьте игру на паузу, чтобы начать заново
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused = true;
        }

    }

    // Сделаем рисование
    public void draw() {
        // получите доступ к mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {

            mCanvas = mSurfaceHolder.lockCanvas();

            // Заполните экран цветом
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Установите размер и цвет mPaint для текста
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            // Нарисуем счет
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            // Нарисуем бомбу и змею
            mBomb.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Нарисуем текст во время паузы
            if (mPaused) {

                // Установите размер и цвет mPaint для текста
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(200);

                mCanvas.drawText("start", 500, 200, mPaint);

            }

            // Разблокируем mCanvas и откройте графику для этого кадра
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    newGame();

                    // Не хочу обрабатывать направление змеи для этого экрана
                    return true;
                }
                // Пусть класс Snake обрабатывает ввод
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;

        }
        return true;

    }

    // Остановить поток
    public  void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Начать обсуждение
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

}

package com.example.android.snike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Snake {

    // Расположение в сетке всех сегментов
    private ArrayList<Point> segmentLocation;

    // Насколько велик каждый сегмент змеи?
    private int nSegmentSize;

    // Насколько велика вся сетка
    private Point mMoveRange;

    // Где центр экрана по горизонтали в пикселях?
    private int halfWayPoint;

    // Длч отслеживания движения по курсу
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    // Начните с направления вправо
    private Heading heading = Heading.RIGHT;

    // Растровое изобрвжение для каждого правления, в которое может смотреть голова
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;


    // Растровое изображение для тела
    private Bitmap mBitmapBody;

    // Constructor
    Snake(Context context, Point mr, int ss) {

        // Инициализируем наш ArrayList
        segmentLocation = new ArrayList<>();

        // Инициализируем размер сегмента и движение
        // диапазон от переданных параметров
        nSegmentSize = ss;
        mMoveRange = mr;

        // Создание и масштабирование растровых изображений
        mBitmapHeadRight = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.eye_100);

        // Сщздание еще 3 варианта головы для разных заголовков
        mBitmapHeadLeft = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.eye_100);

        mBitmapHeadUp = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.eye_100);

        mBitmapHeadDown = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.eye_100);

        // Изменяем растровые изображения, чтобы они смотрели на голову змеи
        // в правильном направлении
        mBitmapHeadRight = Bitmap
                .createScaledBitmap(mBitmapHeadRight, ss, ss, false);

        // Матрица для масштабирования
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);

        mBitmapHeadLeft = Bitmap
                .createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        // Матрица для вращения
        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap
                .createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        // Матричные операторы суммируются
        // так, что поверните на 180 лицлм вниз
        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap
                .createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        // Создайте и масштабируйте тело
        mBitmapBody = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.rad);

        mBitmapBody = Bitmap
                .createScaledBitmap(mBitmapBody, ss, ss, false);

        // The halfway point across the screen in pixels
        // Used to detect which side of screen was pressed
        // Точка середины экрана в пикселях. Используется для определения,
        // какая сторона экрана была нажата
        halfWayPoint = mr.x * ss / 2;

    }

    // Get the snake ready for a new game
    // Подготовьте змею к новой игре
    void reset(int w, int h) {

        Log.d("myLogs", "Snake, NUM_BLOCK_WIDE: " + w + "; mNumBlocksHigh: " + h);

        // Reset the heading
        heading = Heading.RIGHT;

        // Delete the old contents of the ArrayList
        segmentLocation.clear();

        // Start with a single snake segment
        segmentLocation.add(new Point(w / 2, h / 2));
        // С левого края
//        segmentLocation.add(new Point(0, h / 2));
    }

    void move() {

        // Move the body
        // Start at the back and move it to the position of the segment in front of it
        for (int i = segmentLocation.size() - 1; i > 0; i--) {

            // Make it the same value as the next segment going forwards towards the head
            // Сделайте то же значение, что и следующий сегмент,
            // идущий вперед по направлению к голове.
            segmentLocation.get(i).x = segmentLocation.get(i - 1).x;
            segmentLocation.get(i).y = segmentLocation.get(i - 1).y;
        }

        // Move the Head in the appropriate heading
        // get the existing head position
        Point p = segmentLocation.get(0);

        // Move it appropriately
        switch (heading) {
            case UP:
                p.y--;
                break;

            case RIGHT:
                p.x++;
                break;

            case DOWN:
                p.y++;
                break;

            case LEFT:
                p.x--;
                break;
        }

    }

    boolean detectDeath() {
        // Has the snak died?
        boolean dead = false;

        // Hit any of the screen edges
        // Ударьте по любому краю экрана
        if (segmentLocation.get(0).x == -1 ||
                segmentLocation.get(0).x > mMoveRange.x ||
                segmentLocation.get(0).y == -1 ||
                segmentLocation.get(0).y > mMoveRange.y) {
            dead = true;
        }

        // Eaten itself>
        for (int i = segmentLocation.size() - 1; i > 0; i--) {
            // Have any of the section collided with the head
//            Столкнулся ли какой-либо из разделов с головой
            if (segmentLocation.get(0).x == segmentLocation.get(i).x &&
                    segmentLocation.get(0).y == segmentLocation.get(i).y) {
                dead = true;
            }
        }
        return  dead;
    }

    boolean checkDinner(Point l) {
        if (segmentLocation.get(0).x == l.x &&
                segmentLocation.get(0).y == l.y) {
            // Add a new Point to the list located off-screen.
            // This is Ok because on the next call to move it will take
            // the position of the segment in front of it
            // Добавьте новую точку в список за пределами экрана.
            // Это нормально, потому что при следующем вызове перемещения он
            // займет позицию сегмента перед ним
            segmentLocation.add(new Point(-10, -10));

            return true;
        }
        return  false;
    }

    void draw(Canvas canvas, Paint paint) {

        // Don't run this code if ArrayList has nothing in it
        if (!segmentLocation.isEmpty()) {

            // All the code from this method goes here. Draw the head
            switch (heading) {
                case RIGHT:
                    canvas.drawBitmap(mBitmapHeadRight,
                            segmentLocation.get(0).x * nSegmentSize,
                            segmentLocation.get(0).y * nSegmentSize,
                            paint);
                    break;

                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft,
                            segmentLocation.get(0).x * nSegmentSize,
                            segmentLocation.get(0).y * nSegmentSize,
                            paint);
                    break;

                case UP:
                    canvas.drawBitmap(mBitmapHeadUp,
                            segmentLocation.get(0).x * nSegmentSize,
                            segmentLocation.get(0).y * nSegmentSize,
                            paint);
                    break;

                case DOWN:
                canvas.drawBitmap(mBitmapHeadDown,
                        segmentLocation.get(0).x * nSegmentSize,
                        segmentLocation.get(0).y * nSegmentSize,
                        paint);
                break;
            }

            // Draw the snake body one block at a time
            for (int i = 1; i < segmentLocation.size(); i++) {
                canvas.drawBitmap(mBitmapBody,
                        segmentLocation.get(i).x * nSegmentSize,
                        segmentLocation.get(i).y * nSegmentSize,
                        paint);
            }
        }
    }

    // Handle changing direction
    // Управление изменением направления
    void switchHeading(MotionEvent motionEvent) {

        // Is the tap on the right hand side?
        // Кран на правой стороне?
        if (motionEvent.getX() >= halfWayPoint) {
            switch (heading) {
                // Rotate right
                case UP:
                    heading = Heading.RIGHT;
                    break;

                case RIGHT:
                    heading = Heading.DOWN;
                    break;

                case DOWN:
                    heading = Heading.LEFT;
                    break;

                case LEFT:
                    heading = Heading.UP;
                    break;
            }

        } else {
            switch (heading) {
                // Rotate left
                case UP:
                    heading = Heading.LEFT;
                    break;

                case LEFT:
                    heading = Heading.DOWN;
                    break;

                case DOWN:
                    heading = Heading.RIGHT;
                    break;

                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }

    }


}

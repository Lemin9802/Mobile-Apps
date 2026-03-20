package com.example.adminyogaapp.extras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adminyogaapp.models.Course;
import com.example.adminyogaapp.models.YogaClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Yoga";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COURSE = "course";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_COURSE_NAME = "name";
    public static final String COLUMN_COURSE_DAY_OF_WEEK = "dayOfWeek";
    public static final String COLUMN_COURSE_TIME_START = "timeStart";
    public static final String COLUMN_COURSE_CAPACITY = "capacity";
    public static final String COLUMN_COURSE_DURATION = "duration";
    public static final String COLUMN_COURSE_PRICE = "price";
    public static final String COLUMN_COURSE_CLASS_TYPE = "classType";
    public static final String COLUMN_COURSE_DESCRIPTION = "description";

    public static final String TABLE_CLASS = "class";
    public static final String COLUMN_CLASS_ID = "_id";
    public static final String COLUMN_CLASS_NAME = "name";
    public static final String COLUMN_CLASS_COURSE_ID = "courseId";
    public static final String COLUMN_CLASS_DATE = "date";
    public static final String COLUMN_CLASS_TEACHER = "teacher";
    public static final String COLUMN_CLASS_COMMENT = "comment";

    // Set up Firebase Realtime Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference classesRef = database.getReference("Class");
    DatabaseReference coursesRef = database.getReference("Course");

    private static final String TABLE_COURSE_CREATE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    COLUMN_COURSE_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_COURSE_NAME + " TEXT, " +
                    COLUMN_COURSE_DAY_OF_WEEK + " TEXT, " +
                    COLUMN_COURSE_TIME_START + " TEXT, " +
                    COLUMN_COURSE_CAPACITY + " INTEGER, " +
                    COLUMN_COURSE_DURATION + " INTEGER, " +
                    COLUMN_COURSE_PRICE + " REAL, " +
                    COLUMN_COURSE_CLASS_TYPE + " TEXT, " +
                    COLUMN_COURSE_DESCRIPTION + " TEXT);";

    private static final String TABLE_CLASS_CREATE =
            "CREATE TABLE " + TABLE_CLASS + " (" +
                    COLUMN_CLASS_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_CLASS_NAME + " TEXT, " +
                    COLUMN_CLASS_COURSE_ID + " TEXT, " +
                    COLUMN_CLASS_DATE + " TEXT, " +
                    COLUMN_CLASS_TEACHER + " TEXT, " +
                    COLUMN_CLASS_COMMENT + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_CLASS_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + "));";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_COURSE_CREATE);
        db.execSQL(TABLE_CLASS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        onCreate(db);
    }

    public YogaClass getClassById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASS, new String[]{
                        COLUMN_CLASS_ID,
                        COLUMN_CLASS_NAME,
                        COLUMN_CLASS_COURSE_ID,
                        COLUMN_CLASS_DATE,
                        COLUMN_CLASS_TEACHER,
                        COLUMN_CLASS_COMMENT
                },
                COLUMN_CLASS_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        YogaClass YogaClass = new YogaClass(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_COURSE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TEACHER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_COMMENT))
        );

        cursor.close();
        return YogaClass;
    }

    public Course getCourseById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSE, new String[]{
                        COLUMN_COURSE_ID,
                        COLUMN_COURSE_NAME,
                        COLUMN_COURSE_DAY_OF_WEEK,
                        COLUMN_COURSE_TIME_START,
                        COLUMN_COURSE_CAPACITY,
                        COLUMN_COURSE_DURATION,
                        COLUMN_COURSE_PRICE,
                        COLUMN_COURSE_CLASS_TYPE,
                        COLUMN_COURSE_DESCRIPTION
                },
                COLUMN_COURSE_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Course course = new Course(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DAY_OF_WEEK)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_TIME_START)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_CAPACITY)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DURATION)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_COURSE_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_CLASS_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DESCRIPTION))
        );

        cursor.close();
        return course;
    }

    public void syncCoursesWithFirebase() {
        // Set up SQLite Database
        SQLiteDatabase db = getWritableDatabase();

        // Retrieve data from Firebase and store it in SQLite
        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    ContentValues values = new ContentValues();
                    values.put(DbHelper.COLUMN_COURSE_ID, course.getId());
                    values.put(DbHelper.COLUMN_COURSE_NAME, course.getName());
                    values.put(DbHelper.COLUMN_COURSE_DAY_OF_WEEK, course.getDayOfWeek());
                    values.put(DbHelper.COLUMN_COURSE_TIME_START, course.getTimeStart());
                    values.put(DbHelper.COLUMN_COURSE_CAPACITY, course.getCapacity());
                    values.put(DbHelper.COLUMN_COURSE_DURATION, course.getDuration());
                    values.put(DbHelper.COLUMN_COURSE_PRICE, course.getPrice());
                    values.put(DbHelper.COLUMN_COURSE_CLASS_TYPE, course.getClassType());
                    values.put(DbHelper.COLUMN_COURSE_DESCRIPTION, course.getDescription());

//                    Course courseExisted = getCourseById(course.getId());
//                    if (courseExisted.getId().isEmpty()) {
                        db.insert(DbHelper.TABLE_COURSE, null, values);
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


    public void syncClassesWithFirebase() {
        // Set up SQLite Database
        SQLiteDatabase db = getWritableDatabase();

        // Retrieve data from Firebase and store it in SQLite
        classesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    YogaClass yogaClass = courseSnapshot.getValue(YogaClass.class);
                    ContentValues values = new ContentValues();
                    values.put(DbHelper.COLUMN_CLASS_ID, yogaClass.getId());
                    values.put(DbHelper.COLUMN_CLASS_NAME, yogaClass.getName());
                    values.put(DbHelper.COLUMN_CLASS_COURSE_ID, yogaClass.getCourseId());
                    values.put(DbHelper.COLUMN_CLASS_TEACHER, yogaClass.getTeacher());
                    values.put(DbHelper.COLUMN_CLASS_DATE, yogaClass.getDate());
                    values.put(DbHelper.COLUMN_CLASS_COMMENT, yogaClass.getComment());

//                    YogaClass classExist = getClassById(yogaClass.getId());
//                    if (classExist.getId().isEmpty()) {
                        db.insert(DbHelper.TABLE_CLASS, null, values);
//                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public void deleteCourseFirebase(String id) {
        DatabaseReference courseRef = coursesRef.child(String.valueOf(id));
        courseRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseDelete", "Course deleted successfully");
            } else {
                Log.e("FirebaseDelete", "Error deleting course", task.getException());
            }
        });
    }

    public void saveCourseFirebase(String courseId, Course course) {
        coursesRef.child(courseId).setValue(course).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseAdd", "Course added successfully");
            } else {
                Log.e("FirebaseAdd", "Error adding course", task.getException());
            }
        });
    }

    public String saveCourseFirebase(Course course) {
        String courseId = classesRef.push().getKey();

        course.setId(courseId);

        coursesRef.child(courseId).setValue(course).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseAdd", "Course added successfully");
            } else {
                Log.e("FirebaseAdd", "Error adding course", task.getException());
            }
        });

        return courseId;
    }


    public void saveClassFirebase(String classId, YogaClass yogaClass) {
        classesRef.child(String.valueOf(classId)).setValue(yogaClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseAdd", "Class added successfully");
            } else {
                Log.e("FirebaseAdd", "Error adding class", task.getException());
            }
        });
    }

    public String saveClassFirebase(YogaClass yogaClass) {
        String classId = classesRef.push().getKey();
        yogaClass.setId(classId);

        classesRef.child(String.valueOf(classId)).setValue(yogaClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseAdd", "Class added successfully");
            } else {
                Log.e("FirebaseAdd", "Error adding class", task.getException());
            }
        });

        return classId;
    }

    public void deleteClassFirebase(String id) {
        DatabaseReference classRef = classesRef.child(String.valueOf(id));
        classRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseDelete", "Course deleted successfully");
            } else {
                Log.e("FirebaseDelete", "Error deleting course", task.getException());
            }
        });
    }

    // Method to delete all data in all tables
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM course");
        db.execSQL("DELETE FROM class");
    }
}

package com.akindo.popularmovies;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jannik on 25/02/2016.
 */
public abstract class ASortOrder {
    @IntDef({MOST_POPULAR, HIGHEST_RATED})

    // Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)

    // Declare the SortOrder annotation
    public @interface SortOrder {}

    //Declare the constants
    public static final int MOST_POPULAR = 0;
    public static final int HIGHEST_RATED = 1;

    //Decorate the target methods with the annotation
    @SortOrder
    public abstract int getNavigationMode();

    //Attach the annotation
    public abstract void setNavigationMode(@SortOrder int mode);
}

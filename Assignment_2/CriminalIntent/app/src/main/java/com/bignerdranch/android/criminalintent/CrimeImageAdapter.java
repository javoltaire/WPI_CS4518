package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author Jules Voltaire on 11/8/2016.
 * This class is an adapter to handle displaying a bitmap item in a viewGroup
 */
public class CrimeImageAdapter extends BaseAdapter {

    //region Variables
    /**
     * Current context of the application
     */
    private Context context;

    /**
     * List that will hold all the items for the parent viewGroup
     */
    private ArrayList<Bitmap> images;
    //endregion

    //region Constructor/s

    /**
     * Initializes a new instance of this class
     * @param context The current context of the Application
     * @param images The list of images that will be displayed in the viewGroup
     */
    public CrimeImageAdapter(Context context, ArrayList<Bitmap> images){
        this.context = context;
        this.images = images;
    }
    //endregion

    //region Getters

    /**
     * Gets the number of images that the parent view group contains
     * @return The number of images that the parent view group contains
     */
    @Override
    public int getCount() {
        return images.size();
    }

    /**
     * Gets the item at the position given as parameter
     * @param i The index of the item to be returned
     * @return The item at the position in the parameter
     */
    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    /**
     * Not implemented, Just returns 0
     * @param i The index of the item to get the id for
     * @return The id of the item at the position of the parameter
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Gets the View object to be displayed as item in the parent viewGroup
     * @param i The position of the item
     * @param view The old view to reuse, if possible
     * @param viewGroup The parent that this view will eventually be attached to
     * @return The view to be shown as a single item in parent view group
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;                // Image View that will hold the current bitmap item

        if(view == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(images.get(i));

            return imageView;
        }
        else{
            return view;
        }
    }
    //endregion

}

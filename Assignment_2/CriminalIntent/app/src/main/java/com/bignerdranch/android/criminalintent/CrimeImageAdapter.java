package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author Jules Voltaire on 11/8/2016.
 * This class is an adapter to handle displaying a bitmap item in a viewGroup
 */
public class CrimeImageAdapter extends ArrayAdapter<Bitmap> {

    //region Variables
    /**
     * Resource layout of the view for an item in the view group
     */
    private int mLayoutResource;
    //endregion

    //region Constructor/s

    /**
     * Initializes a new instance of this class
     * @param context The current context of the Application
     * @param resource The id for the resource layout file
     * @param images The list of images that will be displayed in the viewGroup
     */
    public CrimeImageAdapter(Context context, int resource, ArrayList<Bitmap> images) {
        super(context, resource, images);
        this.mLayoutResource = resource;
    }

    //endregion

    //region Overridden Methods

    /**
     * Gets the View object to be displayed as item in the parent viewGroup
     * @param i The position of the item
     * @param view The old view to reuse, if possible
     * @param viewGroup The parent that this view will eventually be attached to
     * @return The view to be shown as a single item in parent view group
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View newView = view;                // View that will hold the picture
        ImageView imageView;                // Image View that will hold the current bitmap item
        LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
        newView = inflater.inflate(mLayoutResource, viewGroup, false);

        Bitmap bitmap = this.getItem(i);

        // Updating the image.
        imageView = (ImageView)newView.findViewById(R.id.crime_image_view_item);
        imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);

        return imageView;
    }
    //endregion

}

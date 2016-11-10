package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    //region Constants
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    /**
     * Represents the top left image view in index form when determining where the next
     * captured picture should go
     */
    private final int IMAGE_TOP_LEFT = -1;

    /**
     * Represents the left most image view in the linear layout that contains extra images in
     * index form when determining where the next captured picture should go
     */
    private final int IMAGE_CONTAINER_FIRST = 0;

    /**
     * Represents the middle image view in the linear layout that contains extra images in
     * index form when determining where the next captured picture should go
     */
    private final int IMAGE_CONTAINER_MIDDLE = 1;

    /**
     * Represents the right most image view in the linear layout that contains extra images in
     * index form when determining where the next captured picture should go
     */
    private final int IMAGE_CONTAINER_LAST = 2;

    /**
     * The maximum number of pictures allowed for a crime report
     */
    private final int MAX_NUM_IMAGES = 4;
    //endregion

    //region UI instances
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    /**
     * A GridView that will hold the crime pictures
     */
    private GridView crimeImageContainer;
    //endregion

    //region variables
    private Crime mCrime;
    private File mPhotoFile;
    /**
     * This integer will hold the next location that the picture should be added.
     * It will tell whether the image should be added to the top left image view
     * or at some index in the crimeImageContainer LinearLayout.
     * -1 - top left
     * 0 - index 0 in the linear layout container
     * 1 - index 1 in the linear layout container
     * 2 - index 2 in the linear layout container
     * At the start, we will start with the image view in the top left
     */
    private int imageLocationIndex = IMAGE_TOP_LEFT;

    /**
     * List that will hold all images contained in the grid view only
     */
    private ArrayList<Bitmap> images = new ArrayList<>();

    /**
     * Adapter instance to be used for the image gridview.
     */
    CrimeImageAdapter imageGridViewAdapter;
    //endregion

    //region Overridden Activity Listeners
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime, imageLocationIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);

        // Get an instance of the container that will hold all the crime pictures
        crimeImageContainer = (GridView) v.findViewById(R.id.crime_image_container);
        imageGridViewAdapter = new CrimeImageAdapter(getContext(), R.layout.item_crime_image, images);
        crimeImageContainer.setAdapter(imageGridViewAdapter);

//        updatePhotoView();
        showSavedPictures();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();

                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            showNewImage();
//            updatePhotoView();
        }
    }
    //endregion

    //region Helper Methods
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    //endregion









    private void showSavedPictures(){
        while(mPhotoFile != null && mPhotoFile.exists()){
            displayImage(mPhotoFile);
            mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime, imageLocationIndex);
        }
    }

    private void showNewImage(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            Toast.makeText(getContext(), "Photo file is null or does not exist", Toast.LENGTH_LONG);
        }
        else{
            displayImage(mPhotoFile);
            mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime, imageLocationIndex);
        }
    }

    /**
     * Converts a file to a bitmap and either sets it as the image for the top left imageView
     * or adds it to the list of bitmaps to be displayed as extra image depending
     * on the imageLocationIndex
     * @param imageLocation The file object containing the image
     */
    private void displayImage(File imageLocation){
        Bitmap bitmap = PictureUtils.getScaledBitmap(imageLocation.getPath(), getActivity());
        if(imageLocationIndex == IMAGE_TOP_LEFT){
            mPhotoView.setImageBitmap(bitmap);
        }
        else{
            images.add(imageLocationIndex, bitmap);
            imageGridViewAdapter.notifyDataSetChanged();
        }
        incrementImageLocationIndex();

    }

    /**
     * Determines the next location that the new taken image should go.
     */
    private void incrementImageLocationIndex(){
        if(imageLocationIndex == IMAGE_CONTAINER_LAST)
            imageLocationIndex = IMAGE_TOP_LEFT;
        else
            imageLocationIndex++;
    }





    /**
     * Adds an image to the proper location, it could be the upper left corner image view or
     * in the list view as extra crime photos
     */
//    private void addImage(){
//        if(mPhotoFile == null || !mPhotoFile.exists()){
//            Toast.makeText(getContext(), "Photo file is null or does not exist", Toast.LENGTH_LONG);
//        }
//        else{
//            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
//            if(imageLocationIndex == IMAGE_TOP_LEFT){
//                mPhotoView.setImageBitmap(bitmap);
//            }
//            else{
//                images.add(imageLocationIndex, bitmap);
//                imageGridViewAdapter.notifyDataSetChanged();
//            }
//            incrementImageLocationIndex();
//        }
//    }













//    private void updatePhotoView() {
//        if (mPhotoFile == null || !mPhotoFile.exists()) {
//            mPhotoView.setImageDrawable(null);
//        } else {
//            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
//            mPhotoView.setImageBitmap(bitmap);
//        }
//    }
}

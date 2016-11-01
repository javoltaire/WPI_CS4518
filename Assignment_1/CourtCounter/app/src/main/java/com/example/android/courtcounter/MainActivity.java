/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.courtcounter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity keeps track of the basketball score for 2 teams.
 * Tutorial on how to load an image: http://viralpatel.net/blogs/pick-image-from-galary-android-app/
 * Jules Voltaire - javoltaire
 */
public class MainActivity extends AppCompatActivity {

    //region CONSTANTS
    /**
     * Key for storing and retrieving the score for team a in the Bundle
     */
    private final String TEAM_A_SCORE_KEY = "TEAM_A_SCORE";

    /**
     * Key for storing and retrieving the score for team b in the Bundle
     */
    private final String TEAM_B_SCORE_KEY = "TEAM_B_SCORE";

    /**
     * Key for storing/retrieving the profile to be updated.
     * This is in case the user rotates the screen while choosing an image.
     */
    private final String TO_BE_UPDATED_KEY = "TO_BE_UPDATED";

    /**
     * This serves as key to retrieve the path for profile b as well as comparison to figure out which
     * profile should be updated when the user picks an image.
     */
    private final String PROFILE_B = "PROFILE_B";

    /**
     * This serves as key to retrieve the path for profile a as well as comparison to figure out which
     * profile should be updated when the user picks an image.
     */
    private final String PROFILE_A = "PROFILE_A";
    //endregion

    //region Static variables
    /**
     * Used to store the result from loading an image the user picks using an intent
     */
    private static int RESULT_LOAD_IMAGE = 1;
    //endregion

    //region Variables
    /**
     * Tracks the score for Team A
     */
    int scoreTeamA = 0;

    /**
     * Tracks the score for Team B
     */
    int scoreTeamB = 0;

    /**
     * Stores the current application Context instace
     */
    Context currentContext;

    /**
     * Tracks which profile needs to be updated
     */
    private String toBeUpdated = "";

    /**
     * Stores the path for the image for profile a if the user updates it
     */
    private String profileAPath;

    /**
     * Stores the path for the image for profile b if the user updates it
     */
    private String profileBPath;
    //endregion

    //region Overridden Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting the current application context
        currentContext = getApplicationContext();

        String jamesbond = "hi";
        String jamesBond = "hello";
        String s = jamesBond + jamesbond;
        showToastMessage("Thank you for choosing CourtCounter");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimpSlifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Retrieving the saved score
        scoreTeamA = savedInstanceState.getInt(TEAM_A_SCORE_KEY);
        scoreTeamB = savedInstanceState.getInt(TEAM_B_SCORE_KEY);
        toBeUpdated = savedInstanceState.getString(TO_BE_UPDATED_KEY);
        profileAPath = savedInstanceState.getString(PROFILE_A);
        profileBPath = savedInstanceState.getString(PROFILE_B);

        // Update the image profiles if they were updated by the user
        if(profileAPath != null)
            updateProfile(R.id.team_a_imageView, profileAPath);

        if(profileBPath != null)
            updateProfile(R.id.team_b_imageView, profileBPath);

        // Updating the view
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving the data
        outState.putInt(TEAM_A_SCORE_KEY, scoreTeamA);
        outState.putInt(TEAM_B_SCORE_KEY, scoreTeamB);
        outState.putString(TO_BE_UPDATED_KEY, toBeUpdated);
        outState.putString(PROFILE_A, profileAPath);
        outState.putString(PROFILE_B, profileBPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imagePath = cursor.getString(columnIndex);

                cursor.close();

                switch (toBeUpdated){
                    case PROFILE_A : updateProfile(R.id.team_a_imageView, imagePath);
                                     profileAPath = imagePath;
                                     break;
                    case PROFILE_B : updateProfile(R.id.team_b_imageView, imagePath);
                                     profileBPath = imagePath;
                                     break;
                    default: break;
                }
            }
            else{
                showToastMessage("No Image picked");
            }
        }
        catch(Exception e){
            showToastMessage("Failed to select image" + e.getMessage());
        }


    }
    //endregion

    //region Event Listeners
    /**
     * Resets the score for both teams back to 0.
     */
    public void resetScore(View v) {
        scoreTeamA = 0;
        scoreTeamB = 0;
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
    }

    /**
     * Lets the user choose an email app and sends an email with the current scores and the winning team.
     */
    public void sendScore(View v) {
        // Grabbing the team names
        String teamA = getEditTextContent(R.id.team_a_editText);
        String teamB = getEditTextContent(R.id.team_b_editText);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, formatEmailSubject(teamA, teamB));
        emailIntent.putExtra(Intent.EXTRA_TEXT, formatEmailBody(teamA, teamB));
        emailIntent.setType("message/rfc822");

        startActivity(Intent.createChooser(emailIntent, "Send Scores"));

    }

    /**
     * Upon Clicking on the profile for team A, The user can choose to update the image
     * @param v
     */
    public void updateTeamAProfile(View v){
        toBeUpdated = PROFILE_A;
        showImagePickerIntent();
    }

    /**
     * Upon Clicking on the profile for team B, The user can choose to update the image
     * @param v
     */
    public void updateTeamBProfile(View v){
        toBeUpdated = PROFILE_B;
        showImagePickerIntent();
    }
    //endregion

    //region Helper Methods
    /**
     * Increase the score for Team A by 1 point.
     */
    public void addOneForTeamA(View v) {
        scoreTeamA = scoreTeamA + 1;
        displayForTeamA(scoreTeamA);
    }

    /**
     * Increase the score for Team A by 2 points.
     */
    public void addTwoForTeamA(View v) {
        scoreTeamA = scoreTeamA + 2;
        displayForTeamA(scoreTeamA);
    }

    /**
     * Increase the score for Team A by 3 points.
     */
    public void addThreeForTeamA(View v) {
        scoreTeamA = scoreTeamA + 3;
        displayForTeamA(scoreTeamA);
    }

    /**
     * Increase the score for Team B by 1 point.
     */
    public void addOneForTeamB(View v) {
        scoreTeamB = scoreTeamB + 1;
        displayForTeamB(scoreTeamB);
    }

    /**
     * Increase the score for Team B by 2 points.
     */
    public void addTwoForTeamB(View v) {
        scoreTeamB = scoreTeamB + 2;
        displayForTeamB(scoreTeamB);
    }

    /**
     * Increase the score for Team B by 3 points.
     */
    public void addThreeForTeamB(View v) {
        scoreTeamB = scoreTeamB + 3;
        displayForTeamB(scoreTeamB);
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayForTeamA(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Displays a message as a toeas message to the user
     * @param message The message to be shown in the toast
     */
    private void showToastMessage(String message){
        // Creating the toat
        Toast toast = Toast.makeText(currentContext, message, Toast.LENGTH_SHORT);

        // Actually show the message
        toast.show();
    }

    /**
     * Given the name of team a and team b, this method  will format and return a string that will be
     * Used as the subject for an email
     * @param teamA The name of team a
     * @param teamB The name of team b
     * @return A formatted string to used as the subject for an email
     */
    private String formatEmailSubject(String teamA, String teamB){
        return "Scores for Team: " + teamA + " and Team: " + teamB;
    }

    /**
     * Given the name of team a and team b, this method  will format and return a string that will be
     * Used as the body for an email, informing the receiver of the winning team and the score of each team
     * @param teamA The name of team a
     * @param teamB The name of team b
     * @return A formatted formatted message containing the name of the winning team as well as the score for each team
     */
    private String formatEmailBody(String teamA, String teamB){
        String newLine = "\n";

        // String builder to hold the formated body of the email
        StringBuilder formattedEmailBody = new StringBuilder();

        formattedEmailBody.append("Winning Team: ");

        // Determining the winning team
        String winning;
        if(scoreTeamA > scoreTeamB)
            winning = teamA;
        else if(scoreTeamB > scoreTeamA)
            winning = teamB;
        else
            winning = "Tie";
        formattedEmailBody.append(winning + newLine);
        formattedEmailBody.append("Team A: team " + teamA + " has: " + scoreTeamA + newLine);
        formattedEmailBody.append("Team B: team " + teamB + " has: " + scoreTeamB + newLine);

        return formattedEmailBody.toString();

    }

    /**
     * Finds an editText Widget and grabs the content
     * @param editTextId The id of the editText widget
     * @return The content of the widget if any
     * @throws ClassCastException When the id of is not the id of an editText
     */
    private String getEditTextContent(int editTextId){
        try{
            EditText teamBNameEditText = (EditText) findViewById(editTextId);
            return teamBNameEditText.getText().toString();
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Displays an intent to the user to be able to choose a new image
     */
    private void showImagePickerIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /**
     * Updates the image bitmat of an ImageView widget
     * @param imageViewId The id of the ImageView widget to be updated
     * @param imagePath The path to the new image to update to
     */
    private void updateProfile(int imageViewId, String imagePath){
        try{
            ImageView imageView = (ImageView) findViewById(imageViewId);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        catch(Exception e){
            showToastMessage("Unable to Decode Image. Make sure the app has permission to Storage");
        }
        toBeUpdated = "";
    }
    //endregion
}

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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This activity keeps track of the basketball score for 2 teams.
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
     * Permission to be checked when needed to get user's account information
     */
    private final String GET_ACCOUNT_PERMISSION = "android.permission.GET_ACCOUNTS";
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
     * Lets the user choose an email app and sends an email with the current scores
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
    //endregion

    //region private Methods
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

    private String formatEmailSubject(String teamA, String teamB){
        return "Scores for Team: " + teamA + " and Team: " + teamB;
    }

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
    //endregion



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


}

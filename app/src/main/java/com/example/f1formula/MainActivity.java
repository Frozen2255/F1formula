/*  Starter project for Mobile Platform Development in main diet 2023/2024
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Mohammed Sahli
// Student ID           S2337756
// Programme of Study   F1 Formula App
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.f1formula;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView driverStandingDataPull;
    private TextView raceScheduleDataPull;
    private TextView lastDataPullDisplay;
    private Boolean singleLoad;
    private Boolean driverStandingLoadComplete;
    private Boolean raceScheduleLoadComplete;
    private Handler handler;
    private Runnable runnable;
    private LinearLayout main;
    private TableLayout driverStandingTable;
    private TableLayout raceScheduleTable;
    private Button startButton;
    private Button raceScheduleButton;
    private Spinner seasonDropDown;
    private String driverStandingResults;
    private String raceScheduleResults;
    private String url1="";
    private String driverStandingURLSource ="http://ergast.com/api/f1/current/driverStandings.xml";

    private String raceScheduleURLSource = "http://ergast.com/api/f1/";
    private PopupWindow loadingPopupWindow;
    private List<DriverStanding> driverStandings;
    private List<RaceSchedule> raceSchedules;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = (LinearLayout) findViewById(R.id.main);
        // Set up the raw links to the graphical components
        driverStandingTable = (TableLayout) findViewById(R.id.driver_standing_table);
        raceScheduleTable = (TableLayout) findViewById(R.id.race_schedule_table);
        driverStandingDataPull = (TextView) findViewById(R.id.driver_standing_data_pull);
        raceScheduleDataPull = (TextView) findViewById(R.id.race_schedule_data_pull);
        lastDataPullDisplay = (TextView) findViewById(R.id.last_data_pull_display);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        seasonDropDown = (Spinner)findViewById(R.id.season_dropdown);
        raceScheduleButton = (Button)findViewById(R.id.raceScheduleButton);
        raceScheduleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                singleLoad = true;
                startRaceScheduleProgress();
            }
        });
        driverStandingLoadComplete = false;
        raceScheduleLoadComplete = false;
        singleLoad = false;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run()
            {
                startDriverStandingProgress();
                startRaceScheduleProgress();
                handler.postDelayed(this,2*60*60*1000);
            }
        };
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setUpLoading();
                handler.post(runnable);
            }
        });

        // More Code goes here
    }
    public void setUpLoading ()
    {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View loadingPopUpView = inflater.inflate(R.layout.loading_popup,null);
        loadingPopupWindow = new PopupWindow(loadingPopUpView, width, height, focusable);
    }
    public void onClick(View aview)
    {
        singleLoad = true;
        startDriverStandingProgress();
    }

    public void startDriverStandingProgress()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View loadingPopUpView = inflater.inflate(R.layout.loading_popup,null);
        loadingPopupWindow.showAtLocation(loadingPopUpView, Gravity.CENTER,0,0);
        // Run network access on a separate thread;
        new Thread(new DriverStandingTask(driverStandingURLSource)).start();
    } //
    public void startRaceScheduleProgress()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View loadingPopUpView = inflater.inflate(R.layout.loading_popup,null);
        loadingPopupWindow.showAtLocation(loadingPopUpView, Gravity.CENTER,0,0);
        // Run network access on a separate thread;
        new Thread(new RaceScheduleTask(raceScheduleURLSource + seasonDropDown.getSelectedItem().toString())).start();
    }
    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class DriverStandingTask implements Runnable
    {
        private String url;

        public DriverStandingTask(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");
            LocalDateTime dataPullTime = LocalDateTime.now();
            try
            {
                driverStandingResults = "";
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    Log.e("run check","this is running");
                    driverStandingResults = driverStandingResults + inputLine;
                    System.out.println(inputLine);
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", ae.getMessage());
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = driverStandingResults.indexOf(">");
            driverStandingResults = driverStandingResults.substring(i+1);
            Log.e("MyTag - cleaned",driverStandingResults);


            //
            // Now that you have the xml data you can parse it
            //
            driverStandings = new ArrayList<>();
            DriverStanding driverStanding = null;
            XmlPullParserFactory factory = null;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput( new StringReader( driverStandingResults ) );
                int eventType = xpp.getEventType();
                boolean nextDriverStanding = true;
                boolean gotDriverNationality = false;
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (nextDriverStanding)
                    {
                        gotDriverNationality = false;
                        driverStanding = new DriverStanding();
                        nextDriverStanding = false;
                    }
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        System.out.println("Start document");
                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("DriverStanding") )
                    {

                       driverStanding.setPosition(Integer.valueOf(xpp.getAttributeValue(null,"position")));
                       driverStanding.setPoints(Integer.valueOf(xpp.getAttributeValue(null,"points")));
                       driverStanding.setWins(Integer.valueOf(xpp.getAttributeValue(null,"wins")));
                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("GivenName") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            driverStanding.setFirstName(xpp.getText());
                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("FamilyName") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            driverStanding.setSurName(xpp.getText());

                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("DateOfBirth") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            driverStanding.setDateOfBirth(xpp.getText());

                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Nationality") && !gotDriverNationality )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            gotDriverNationality = true;
                            driverStanding.setNationality(xpp.getText());

                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Name") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            driverStanding.setConstructorName(xpp.getText());
                            driverStandings.add(driverStanding);
                            nextDriverStanding = true;
                        }

                    }
                    eventType = xpp.next();
                }
                for (DriverStanding driver: driverStandings)
                {
                    Log.e("driver",driver.toString());
                }
                Log.e("drivers",driverStandings.toString());

                System.out.println("End document");
                driverStandingLoadComplete = true;
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    if (singleLoad || (driverStandingLoadComplete && raceScheduleLoadComplete))
                    {
                        loadingPopupWindow.dismiss();
                        driverStandingLoadComplete = false;
                        raceScheduleLoadComplete = false;
                        String dataPullInformation = dataPullTime.getDayOfWeek() + " "
                                + dataPullTime.getDayOfMonth() + " " + dataPullTime.getMonth() + " "
                                + dataPullTime.getYear() + " " + dataPullTime.getHour() + ":"
                                + dataPullTime.getMinute() + ":" + dataPullTime.getSecond();
                        if (!singleLoad)
                        {
                            raceScheduleDataPull.setText("Last Pull: " + dataPullInformation);
                            lastDataPullDisplay.setText("Last Auto Pull: " + dataPullInformation);
                        }
                        driverStandingDataPull.setText("Last Pull: " + dataPullInformation);
                        singleLoad = false;
                    }
                    Log.d("UI thread", "I am the UI thread");
                    String resultsToDisplay = driverStandings.toString().replace(", ","\n---------------------------------------------------------\n");
                    resultsToDisplay = "\nDriver Standings:\n\n" + resultsToDisplay.substring(1,resultsToDisplay.length()-1);
                    if (driverStandingTable.getChildCount() > 1)
                    {
                        driverStandingTable.removeViews(1,driverStandingTable.getChildCount() - 1);
                    }
                    for (DriverStanding driver:driverStandings)
                    {
                        TableRow tableRow = new TableRow(MainActivity.this);
                        TextView positionTextView = new TextView(MainActivity.this);
                        TextView firstNameTextView = new TextView(MainActivity.this);
                        TextView SurnameTextView = new TextView(MainActivity.this);
                        TextView pointsTextView = new TextView(MainActivity.this);
                        TextView winsTextView = new TextView(MainActivity.this);
                        TableRow.LayoutParams params1=new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

                        positionTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        positionTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        positionTextView.setLayoutParams(params1);

                        firstNameTextView.setBackgroundColor(Color.parseColor("#C7B9EA"));
                        firstNameTextView.setTextColor(Color.parseColor("#000000"));
                        firstNameTextView.setLayoutParams(params1);

                        SurnameTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        SurnameTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        SurnameTextView.setLayoutParams(params1);

                        pointsTextView.setBackgroundColor(Color.parseColor("#C7B9EA"));
                        pointsTextView.setTextColor(Color.parseColor("#000000"));
                        pointsTextView.setLayoutParams(params1);

                        winsTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        winsTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        winsTextView.setLayoutParams(params1);

                        positionTextView.setText(String.valueOf(driver.getPosition()));
                        firstNameTextView.setText(driver.getFirstName());
                        SurnameTextView.setText(driver.getSurName());
                        pointsTextView.setText(String.valueOf(driver.getPoints()));
                        winsTextView.setText(String.valueOf(driver.getWins()));

                        tableRow.addView(positionTextView);
                        tableRow.addView(firstNameTextView);
                        tableRow.addView(SurnameTextView);
                        tableRow.addView(pointsTextView);
                        tableRow.addView(winsTextView);

                        tableRow.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Toast toast = Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT);
                                toast.show();
                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                View driverPopUpView = inflater.inflate(R.layout.driver_popup,null);

                                TextView driverPopUpPosition = driverPopUpView.findViewById(R.id.driver_popup_position);
                                driverPopUpPosition.setText("Position : " + (driver.getPosition()));

                                TextView driverPopUpFirstName = driverPopUpView.findViewById(R.id.driver_popup_firstname);
                                driverPopUpFirstName.setText("First Name : " + (driver.getFirstName()));

                                TextView driverPopUpSurName = driverPopUpView.findViewById(R.id.driver_popup_Surname);
                                driverPopUpSurName.setText("Surname : " + (driver.getSurName()));

                                TextView driverPopUpPoints = driverPopUpView.findViewById(R.id.driver_popup_Points);
                                driverPopUpPoints.setText("Points : " + (driver.getPoints()));

                                TextView driverPopUpWins = driverPopUpView.findViewById(R.id.driver_popup_wins);
                                driverPopUpWins.setText("Wins : " + (driver.getWins()));

                                TextView driverPopUpNationality = driverPopUpView.findViewById(R.id.driver_popup_Nationality);
                                driverPopUpNationality.setText("Nationality : " + (driver.getNationality()));

                                TextView driverPopUpDOB = driverPopUpView.findViewById(R.id.driver_popup_DOB);
                                driverPopUpDOB.setText("Date Of Birth : " + (driver.getDateOfBirth()));

                                TextView driverPopUpConstructor = driverPopUpView.findViewById(R.id.driver_popup_Constructor);
                                driverPopUpConstructor.setText("Constructor : " + (driver.getConstructorName()));


                                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                                boolean focusable = true;
                                PopupWindow popupWindow = new PopupWindow(driverPopUpView, width, height, focusable);
                                popupWindow.showAtLocation(driverPopUpView, Gravity.CENTER,0,0);

                                driverPopUpView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event)
                                    {
                                        popupWindow.dismiss();
                                        return true;
                                    }
                                });
                            }
                        });

                        driverStandingTable.addView(tableRow);
                    }

                }
            });
        }

    }
    private class RaceScheduleTask implements Runnable
    {
        private String url;

        public RaceScheduleTask(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");
            LocalDateTime dataPullTime = LocalDateTime.now();
            try
            {
                raceScheduleResults = "";
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    Log.e("run check","this is running");
                    raceScheduleResults = raceScheduleResults + inputLine;
                    System.out.println(inputLine);
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", ae.getMessage());
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = raceScheduleResults.indexOf(">");
            raceScheduleResults = raceScheduleResults.substring(i+1);
            Log.e("MyTag - cleaned",raceScheduleResults);


            //
            // Now that you have the xml data you can parse it
            //
            raceSchedules = new ArrayList<>();
            RaceSchedule raceSchedule = null;
            XmlPullParserFactory factory = null;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput( new StringReader( raceScheduleResults ) );
                int eventType = xpp.getEventType();
                boolean nextRace = true;
                boolean gotDate = false;
                boolean gotTime = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (nextRace)
                    {
                        raceSchedule = new RaceSchedule();
                        nextRace = false;
                        gotDate = false;
                        gotTime = false;
                    }
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        System.out.println("Start document");
                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Race") )
                    {
                        raceSchedule.setRound(Integer.valueOf(xpp.getAttributeValue(null,"round")));
                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("RaceName") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            raceSchedule.setRaceName(xpp.getText());
                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("CircuitName") )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            raceSchedule.setCircuit(xpp.getText());

                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Date") && !gotDate )
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            raceSchedule.setDate(xpp.getText());
                            gotDate = true;
                        }

                    }
                    else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Time")  && !gotTime)
                    {
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.TEXT)
                        {
                            raceSchedule.setTime(xpp.getText());
                            gotTime = true;
                        }

                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equals("Race"))
                    {
                        raceSchedules.add(raceSchedule);
                        nextRace = true;

                    }
                    eventType = xpp.next();
                }
                for (RaceSchedule race: raceSchedules)
                {
                    Log.e("race",race.toString());
                }
                Log.e("raceSchedules",raceSchedules.toString());

                System.out.println("End document");
                raceScheduleLoadComplete = true;
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    if (singleLoad || (driverStandingLoadComplete && raceScheduleLoadComplete))
                    {
                        loadingPopupWindow.dismiss();
                        driverStandingLoadComplete = false;
                        raceScheduleLoadComplete = false;

                        String dataPullInformation = dataPullTime.getDayOfWeek() + " "
                                + dataPullTime.getDayOfMonth() + " " + dataPullTime.getMonth() + " "
                                + dataPullTime.getYear() + " " + dataPullTime.getHour() + ":"
                                + dataPullTime.getMinute() + ":" + dataPullTime.getSecond();
                        if (!singleLoad)
                        {
                            driverStandingDataPull.setText("Last Pull: " + dataPullInformation);
                            lastDataPullDisplay.setText("Last Auto Pull: " + dataPullInformation);
                        }
                        raceScheduleDataPull.setText("Last Pull: " + dataPullInformation);
                        singleLoad = false;
                    }
                    Log.d("UI thread", "I am the UI thread");
                    String resultsToDisplay = raceSchedules.toString().replace(", ","\n---------------------------------------------------------\n");
                    resultsToDisplay = "\nDriver Standings:\n\n" + resultsToDisplay.substring(1,resultsToDisplay.length()-1);
                    boolean gotNextScheduleRace = false;
                    if (raceScheduleTable.getChildCount() > 1)
                    {
                        raceScheduleTable.removeViews(1,raceScheduleTable.getChildCount() - 1);
                    }
                    for (RaceSchedule race:raceSchedules)
                    {
                        TableRow tableRow = new TableRow(MainActivity.this);
                        TextView roundTextView = new TextView(MainActivity.this);
                        TextView raceNameTextView = new TextView(MainActivity.this);
                        TextView dateTextView = new TextView(MainActivity.this);
                        TextView timeTextView = new TextView(MainActivity.this);
                        TextView circuitTextView = new TextView(MainActivity.this);
                        TableRow.LayoutParams params1=new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

                        roundTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        roundTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        roundTextView.setLayoutParams(params1);

                        raceNameTextView.setBackgroundColor(Color.parseColor("#C7B9EA"));
                        raceNameTextView.setTextColor(Color.parseColor("#000000"));
                        raceNameTextView.setLayoutParams(params1);

                        dateTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        dateTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        dateTextView.setLayoutParams(params1);

                        timeTextView.setBackgroundColor(Color.parseColor("#C7B9EA"));
                        timeTextView.setTextColor(Color.parseColor("#000000"));
                        timeTextView.setLayoutParams(params1);

                        circuitTextView.setBackgroundColor(Color.parseColor("#957ADD"));
                        circuitTextView.setTextColor(Color.parseColor("#FEFEFE"));
                        circuitTextView.setLayoutParams(params1);

                        roundTextView.setText(String.valueOf(race.getRound()));
                        raceNameTextView.setText(race.getRaceName());
                        dateTextView.setText(race.getDate());
                        timeTextView.setText(String.valueOf(race.getTime()));
                        circuitTextView.setText(String.valueOf(race.getCircuit()));

                        if (race.isFutureRace())
                        {
                            String primaryColor = "";
                            String secondaryColor = "";
                            if (!gotNextScheduleRace)
                            {
                                primaryColor = "#78094C";
                                secondaryColor = "#9E78094C" ;
                                gotNextScheduleRace = true;
                            }
                            else
                            {
                               primaryColor = "#FFBA66B1" ;
                               secondaryColor = "#EFB8E9" ;
                            }
                            roundTextView.setBackgroundColor(Color.parseColor(primaryColor));
                            raceNameTextView.setBackgroundColor(Color.parseColor(secondaryColor));
                            dateTextView.setBackgroundColor(Color.parseColor(primaryColor));
                            timeTextView.setBackgroundColor(Color.parseColor(secondaryColor));
                            circuitTextView.setBackgroundColor(Color.parseColor(primaryColor));
                        }

                        List<Integer> heights = new ArrayList<>();

                        heights.add(roundTextView.getLayoutParams().height);
                        heights.add(raceNameTextView.getLayoutParams().height);
                        heights.add(dateTextView.getLayoutParams().height);
                        heights.add(timeTextView.getLayoutParams().height);
                        heights.add(circuitTextView.getLayoutParams().height);

                        int currentHighest = -1;
                        for (Integer height: heights)
                        {
                            if (currentHighest < height)
                            {
                                currentHighest = height;
                            }
                        }

                        roundTextView.getLayoutParams().height = currentHighest;
                        raceNameTextView.getLayoutParams().height = currentHighest;
                        dateTextView.getLayoutParams().height = currentHighest;
                        timeTextView.getLayoutParams().height = currentHighest;
                        circuitTextView.getLayoutParams().height = currentHighest;

                        tableRow.addView(roundTextView);
                        tableRow.addView(raceNameTextView);
                        tableRow.addView(dateTextView);
                        tableRow.addView(timeTextView);
                        tableRow.addView(circuitTextView);


                        raceScheduleTable.addView(tableRow);
                    }

                }
            });
        }

    }
}
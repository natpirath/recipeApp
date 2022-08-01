package com.cst2335.recipeapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RecipePage extends Fragment {

    final String TAG = "recipeAct";
    String idMeal, mealName, mealThumb, mealInst, mealCat;
    private View view;
    private String email;
    Button button;
    SharedPreferences sp;
    public static final String EMAIL = "email";
    private ConstraintLayout constraintLayout;
    String emailStr;
    private EditText editTextEmail;
    private TextView description;


    /**
     * sets the view of the recipe page to be able to access it elements
     * @param myView the view of the recipe
     */
    public void setView(View myView) {
        this.view = myView;
    }
    /**
     * gets the view that was set in the onCreateView() method to be able to access
     * the recipe page elements.
     * @return the view
     */
    public View getView(){
        return this.view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View recipePage = inflater.inflate(R.layout.activity_recipe_page, container, false);

        setView(recipePage);

        // For ToolBar
        Toolbar toolbar = recipePage.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar); //toolbar id
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Finding Views by Id for the email Sender
        editTextEmail = recipePage.findViewById(R.id.editTextEmail);
        button = recipePage.findViewById(R.id.SendToEmailButton);
        constraintLayout = recipePage.findViewById(R.id.ConstraintLayout);
        description = recipePage.findViewById(R.id.textView2);

        SharedPreferences prefs= this.getContext().getSharedPreferences("EmailPrefs", Context.MODE_PRIVATE);
        email = prefs.getString(EMAIL, "");

        description.setMovementMethod(new ScrollingMovementMethod());


        /*
         * Set email address of Email Edit Text
         */
        editTextEmail.setText(email);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Email was sent", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar = Snackbar.make(constraintLayout, "Email wasn't sent", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        });

                snackbar.show();
            }


        });

        // FAB when clicked will show AlertDialog with "help" instructions on how to use the layout
        FloatingActionButton fab = recipePage.findViewById(R.id.fab);
        fab.setOnClickListener( clickFab -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // TODO: change the message of the help to show instructions of how to use this activity
            builder.setTitle(R.string.help)
                    .setMessage(R.string.search_result_help)
                    .setNegativeButton("Close", (click, arg) -> {})
                    .create().show();
        }); //end fab onClick



        ProgressBar progressBar = recipePage.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Bundle passedData = getArguments();
        idMeal = passedData.getString("idMeal");


        String api = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + idMeal;
        JsonFetcher fetcher = new JsonFetcher();
        fetcher.execute(api);

        return recipePage;

    }

    /* Enable options menu in this fragment */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.home_item:
                message = "You clicked on home";
                Intent i = new Intent (getActivity(), HomePage.class);
                startActivity(i);
                break;
            case R.id.cook_item:
                message = "You clicked on cook";
                Intent ii = new Intent (getActivity(), result_page.class);
                startActivity(ii);
                break;
            case R.id.favourites_item:
                message = "You clicked on favourites";
                Intent iii = new Intent (getActivity(), Favourites.class);
                startActivity(iii);
                break;
            case R.id.help_item:
                message = "You clicked on help";
                break;
        }
        if ( message != null ) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
            return true;
    }



    private class JsonFetcher extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... args) {

            try {
                Log.e(TAG, "in doInBackground");
                // URL object of the api we will use
                URL url = new URL(args[0]);
                Log.e(TAG, "url: " + url);
                // open a connection with the url
                HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
                // wait for data to be retrieved
                InputStream stream = Connection.getInputStream();
                Log.e(TAG, "stream: " + stream);
                // in case we don't get any stream
                if (stream == null) {
                    return "Data not fetched";
                }

                // reading the Json data:-
                // build the json string from the input stream
                BufferedReader myReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();

                String line1 = null;
                // read the lines in the string read from the stream
                while ((line1 = myReader.readLine()) != null) {
                    stringBuilder.append(line1).append("\n");
                }
                // return the Json string to be worked on in the onPostExecute() method
                return stringBuilder.toString();

            } catch (Exception e) {
                Log.e(TAG, "exception in doInBackground====");
                e.printStackTrace();
            }
            return null;
        }// end doInBackground

        /**
         * this method runs on the main UI thread and controls it.
         * it reads data form the Json string provided by the doInBackground, and passes them to
         * a new Meals object to be viewed on the listView.
         *
         * @param s1 the returned string from doInBackground() method
         */
        @Override
        protected void onPostExecute(String s1) {
            super.onPostExecute(s1);
            Log.e(TAG, "in onPostExecute");
            Log.e(TAG, "result from fetching: " + s1);
            // after the doInBackground is done we make the progressbar invisible
            // progressBar.setVisibility(View.INVISIBLE);

            // in case the data was not fetched
            if (s1 != null && s1.equalsIgnoreCase("Data not fetched")) {
                // show alert dialog with an error message
                /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle(R.string.help)
                        .setMessage(R.string.not_fetched)
                        .setNegativeButton("Close", (click, arg) -> {})
                        .create().show();*/
                Log.e(TAG, "Data is not fetched");
            } else if (s1 == null) {
                Log.e(TAG, " s1 is null");
            }
            // or if it was fetched, do the Json parsing
            else {
                try {
                    // convert the string we built to JSON
                    JSONObject jsonObject = new JSONObject(s1);

                    // fetch the Json array with the key "meals", there's1 only one element in the array
                    JSONArray jsonArray = jsonObject.getJSONArray("meals");
                    // get the only Json object in this Json data
                    JSONObject meal = jsonArray.getJSONObject(0);
                    // here we retrieve each data and set it to it corresponding view in the activity
                    mealName = meal.getString("strMeal");
                    mealThumb = meal.getString("strMealThumb");
                    idMeal = meal.getString("idMeal");


                    Log.e(TAG, "meal name: "+ mealName);
                    Log.e(TAG, "image URL"+ mealThumb);
                    Log.e(TAG, "idMeal"+ idMeal);

                    View theView = getView();
                    ProgressBar progressBar = theView.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                    TextView tv_test = theView.findViewById(R.id.textView2);
                    tv_test.setText("ONLY TESTING: Meal name from API is  "+mealName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // save a user's email address
        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("EmailPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, editTextEmail.getText().toString());

        editor.apply();
    }
}



package com.example.targetsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "CENSORED"; //Removed for public viewing
    private final int QUERY_COUNT = 48;
    private int storeID = 353;
    private RequestQueue requestQueue;

    private boolean searchedBefore = false;

    private TextView searchBar;
    private EditText editText;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);

        //use a GradientDrawable with only one color set, to make it a solid color
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(2, Color.parseColor("#D3D3D3")); //black border with full opacity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            searchBar.setBackgroundDrawable(border);
        } else {
            searchBar.setBackground(border);
        }
        searchBar.setPadding(18,0,0,0);


        editText = findViewById(R.id.searchBar);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //Event ACTION_DOWN is enter button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER) && !((searchBar.getText() + "").isEmpty())) {
                    deleteAllSearchResultElements();
                    Log.i("Button Message", searchBar.getText() + " ]");
                    getSearchResults(searchBar.getText() + "");
                }
                return false;
            }
        });


    }

    public void searchButtonHandler(View view) {
        if (!((searchBar.getText() + "").isEmpty())) {
            deleteAllSearchResultElements();
            searchBar = findViewById(R.id.searchBar);
            getSearchResults(searchBar.getText() + "");


        }

        //Hides keyboard after click
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void getSearchResults(String query) {
        //instantiate the request queue
        requestQueue = Volley.newRequestQueue(this);

        if (query.equals("")) {
            searchedBefore = false;
            return;

        }

        //create object request
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(
                        Request.Method.GET,    //the request method
                        "https://redsky.target.com/v2/plp/search?keyword=" + query + "&count=" + QUERY_COUNT + "&offset=0&pricing_store_id=" + storeID + "&key=" + API_KEY,  //the URL
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //this prints the WHOLE string
                                //Log.i("JSON response", response.toString());
                                int counter = 0;
                                //Gets the title of first item in search query
                                try {

                                    while (true) {


                                        String theTitle = "";

                                        String imageURLText = "";

                                        String formattedCurrentPrice = "";
                                        String formattedCurrentPriceType = "";

                                        String availabilityStatus = "";
                                        int inStock = 0;

                                        String DPCI = "";
                                        String TCIN = "";
                                        String UPC = "";

                                        //--------------- Gets main items ---------------

                                        JSONObject theList = response.getJSONObject("search_response");

                                        JSONObject theItems = theList.getJSONObject("items");

                                        JSONArray theItem = theItems.getJSONArray("Item");

                                        JSONObject currentItemObject = theItem.getJSONObject(counter);

                                        //------ Gets Images


                                        JSONArray imageArray = currentItemObject.getJSONArray("images");

                                        JSONObject imageURL = imageArray.getJSONObject(0);

                                        imageURLText = imageURL.getString("base_url") + imageURL.getString("primary");

                                        //-------- Gets Title

                                        theTitle = currentItemObject.getString("title");

                                        //-------- Gets Price

                                        JSONObject priceObject = currentItemObject.getJSONObject("price");

                                        //Log.i("Test",priceObjectArray.getString("formatted_current_price"));
                                        try {
                                            formattedCurrentPrice = priceObject.getString("formatted_current_price");
                                            formattedCurrentPriceType = priceObject.getString("formatted_current_price_type");


                                        } catch (JSONException ex) {
                                            //Log.e("JSON Price Error", ex.getMessage());
                                        }

                                        //----- Gets DPCI

                                        try {
                                            DPCI = currentItemObject.getString("dpci");
                                        } catch (JSONException ex) {
                                            //Log.e("JSON DPCI Error", ex.getMessage());
                                            DPCI = "N/A";
                                        }

                                        //----- Gets TCIN

                                        try {
                                            TCIN = currentItemObject.getString("tcin");
                                        } catch (JSONException ex) {
                                            //Log.e("JSON TCIN Error", ex.getMessage());
                                            TCIN = "N/A";
                                        }

                                        //------ Gets UPS

                                        try {
                                            UPC = currentItemObject.getString("upc");
                                        } catch (JSONException ex) {
                                            //Log.e("JSON UPC Error", ex.getMessage());
                                            UPC = "N/A";
                                        }


                                        //--------- Gets IN_STOCK

                                        try {
                                            availabilityStatus = currentItemObject.getString("availability_status");
                                            if (availabilityStatus.equals("IN_STOCK")) {
                                                inStock = 1;
                                            } else if (availabilityStatus.equals("OUT_OF_STOCK")) {
                                                inStock = 0;
                                            }
                                        } catch (JSONException ex) {
                                            //Log.e("JSON IN_STOCK Error", ex.getMessage());
                                            inStock = 3;
                                        }


                                        //-----Sends to Search Result Element

                                        addSearchResultElement(theTitle, imageURLText, formattedCurrentPrice, formattedCurrentPriceType, inStock, DPCI, TCIN, UPC,currentItemObject.toString());
                                        counter++;
                                    }
                                } catch (JSONException ex) {
                                    Log.e("JSON Error", ex.getMessage());
                                    if (counter == 0) {
                                        noSearchResults();
                                    }


                                }

                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );//end of JSON object request

        requestQueue.add(jsonObjectRequest);
    }


    public void addSearchResultElement(String itemName, String imageURL, String price, String sale, int inStock, String DPCI, String TCIN, String UPC, final String itemJSON) {

        if (sale.equals("reg")) {
            sale = "";
        }

        linearLayout = findViewById(R.id.dynmaicLayout);

        LinearLayout linearLayoutMainHor = new LinearLayout(this);
        linearLayoutMainHor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutMainHor.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayoutDetails = new LinearLayout(this);
        linearLayoutDetails.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutDetails.setOrientation(LinearLayout.VERTICAL);

        /*
        LinearLayout linearLayoutPrice = new LinearLayout(this);
        linearLayoutPrice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutPrice.setOrientation(LinearLayout.HORIZONTAL);
         */

        ImageView imageOfItem = new ImageView(this);
        TextView titleOfItem = new TextView(this);

        TextView priceOfItem = new TextView(this);
        TextView saleOfItem = new TextView(this);

        TextView availabilityStatus = new TextView(this);

        TextView DPCIView = new TextView(this);
        TextView TCINView = new TextView(this);
        TextView UPCView = new TextView(this);


        imageOfItem.setPadding(10, 20, 10, 80);
        Picasso.get().load(imageURL).into(imageOfItem);

        titleOfItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(itemName.length()<64) {
            titleOfItem.setText(itemName);
        } else {
            itemName = itemName.substring(0, Math.min(itemName.length(), 61))+"...";
            titleOfItem.setText(itemName);
        }


        priceOfItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        priceOfItem.setText(price);
        priceOfItem.setTextSize(16);
        priceOfItem.setTypeface(null, Typeface.BOLD);
        if (sale.equals("sale")) {
            priceOfItem.setTextColor(Color.parseColor("#CC0000"));
        }
        if (price.equals("See low price in cart")) {
            priceOfItem.setTypeface(null, Typeface.BOLD_ITALIC);
            priceOfItem.setTextSize(14);
        }
        priceOfItem.setPadding(0, 20, 0, 0);

        saleOfItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        saleOfItem.setText(sale.toUpperCase());
        saleOfItem.setTextColor(Color.parseColor("#CC0000"));

        availabilityStatus.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (inStock == 1) {
            availabilityStatus.setText("In Stock");
            availabilityStatus.setTextColor(Color.parseColor("#008000"));
        } else if (inStock == 0) {
            availabilityStatus.setText("Out of Stock");
            availabilityStatus.setTextColor(Color.parseColor("#CC0000"));
        } else {
            availabilityStatus.setText("Stock not available");
        }
        availabilityStatus.setPadding(0, 20, 0, 0);


        titleOfItem.setPadding(0, 20, 10, 0);// in pixels (left, top, right, bottom)

        linearLayoutMainHor.setPadding(0, 20, 0, 20);
        linearLayoutDetails.setPadding(10, 0, 0, 0);

        DPCIView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        DPCIView.setText("DPCI: " + DPCI);
        DPCIView.setPadding(0, 20, 0, 0);

        TCINView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TCINView.setText("TCIN: " + TCIN);
        TCINView.setPadding(0, 0, 0, 0);

        UPCView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        UPCView.setText("UPC: " + UPC);
        UPCView.setPadding(0, 0, 0, 0);

        //linearLayoutPrice.setPadding(0,20,0,20);

        linearLayoutMainHor.addView(imageOfItem);

        linearLayoutDetails.addView(titleOfItem);

        //linearLayoutPrice.addView(priceOfItem);
        //linearLayoutPrice.addView(saleOfItem);
        linearLayoutDetails.addView(priceOfItem);
        if (sale.equals("sale")) {
            linearLayoutDetails.addView(saleOfItem);
        }


        linearLayoutDetails.addView(availabilityStatus);

        //linearLayoutDetails.addView(linearLayoutPrice);

        linearLayoutDetails.addView(DPCIView);
        linearLayoutDetails.addView(TCINView);
        linearLayoutDetails.addView(UPCView);

        linearLayoutMainHor.addView(linearLayoutDetails);

        //use a GradientDrawable with only one color set, to make it a solid color
        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(2, Color.parseColor("#D3D3D3")); //black border with full opacity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            linearLayoutMainHor.setBackgroundDrawable(border);
        } else {
            linearLayoutMainHor.setBackground(border);
        }
        final Intent pageDetails = new Intent(this, SecondActivity.class);
        linearLayoutDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageDetails.putExtra("Item",itemJSON);
                startActivity(pageDetails);

            }
        });
        linearLayout.addView(linearLayoutMainHor);
        searchedBefore = true;
    }

    public void deleteAllSearchResultElements() {
        if (searchedBefore == true) {
            linearLayout.removeAllViews();
        }
    }

    public void noSearchResults() {
        linearLayout = findViewById(R.id.dynmaicLayout);
        TextView noResults = new TextView(this);

        noResults.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        noResults.setText("Sorry no reuslts");
        noResults.setTextColor(Color.parseColor("#CC0000"));
        noResults.setTypeface(null, Typeface.BOLD);
        noResults.setTextSize(30);
        noResults.setGravity(Gravity.CENTER);
        noResults.setPadding(400, 200, 400, 0);

        TextView search = new TextView(this);
        search.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        search.setTextColor(Color.parseColor("#CC0000"));
        search.setTextSize(20);
        search.setPadding(400, 0, 400, 400);
        search.setGravity(Gravity.CENTER);
        search.setText("For \"" + searchBar.getText() + "\"");

        linearLayout.addView(noResults);
        linearLayout.addView(search);
        searchedBefore = true;

    }



}

//TODO Known bug. If your search for TV's the titles get too long and UPC gets cut off (Did quick fix: if title longer than 64chars then cut and add ... at end)
//TODO Known bug. If you search for TV's then the last search result gets cut off a bit
//TODO Known bug. No proper way of handling "See low price in cart". Works but errors are made. Should fix
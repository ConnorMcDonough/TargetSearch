package com.example.targetsearch;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {


    private String itemJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle itemData = getIntent().getExtras();
        itemJSON = itemData.getString("Item");

        sortJSONItem();
    }

    public void sortJSONItem() {

        try {
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


            JSONObject theItem = new JSONObject(itemJSON);


            //------ Gets Images


            JSONArray imageArray = theItem.getJSONArray("images");

            JSONObject imageURL = imageArray.getJSONObject(0);

            imageURLText = imageURL.getString("base_url") + imageURL.getString("primary");

            Log.i("JSON Image URL",imageURLText);

            createPage(imageURLText);
            /*
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


             */

        } catch (JSONException ex) {
            Log.e("JSON Error", ex.getMessage());
        }


    }

    public void createPage(String imageURL) {
        //Main Layout of page
        LinearLayout linearLayout = findViewById(R.id.layout);

        //Image layout
        LinearLayout linearLayoutImages = new LinearLayout(this);
        linearLayoutImages.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutImages.setOrientation(LinearLayout.HORIZONTAL);

        //All TextViews creation
        TextView text = new TextView(this);

        //All ImageViews creation
        ImageView mainImage = new ImageView(this);

        //All TextViews definition
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setText(itemJSON);

        //All ImageViews definition
        Picasso.get().load(imageURL).into(mainImage);


        //Adding views to ImageLayout
        linearLayoutImages.addView(mainImage);

        //Set Image size
        mainImage.getLayoutParams().width = 600;
        mainImage.getLayoutParams().height = 600;

        //Adding layouts to master layout
        linearLayout.addView(linearLayoutImages);

    }


}

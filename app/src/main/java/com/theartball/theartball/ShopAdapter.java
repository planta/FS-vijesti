package com.theartball.theartball;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mario on 22.11.2015..
 */
public class ShopAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShopItem> oglasi;

    public ShopAdapter(Context c, ArrayList<ShopItem> shopList) {
        context = c;
        oglasi = shopList;
    }

    @Override
    public int getCount() {
        return oglasi.size();
    }

    @Override
    public Object getItem(int position) {
        return oglasi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View shopItemCellView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            shopItemCellView = new View(context);
        } else {
            shopItemCellView = (View)convertView;
        }
        ShopItem shopItem = oglasi.get(position);
        shopItemCellView = layoutInflater.inflate(R.layout.listing_cell, null);
        ImageView image = (ImageView)shopItemCellView.findViewById(R.id.listingImage);
        Log.d("TAG", shopItem.imageURL);
        Picasso.with(context).load(shopItem.imageURL).placeholder(R.drawable.placeholder).resize(300,300).centerCrop().into(image);
        TextView title = (TextView)shopItemCellView.findViewById(R.id.listingTitle);
        title.setText(shopItem.title);
        TextView seller = (TextView)shopItemCellView.findViewById(R.id.listingSeller);
        seller.setText(shopItem.seller);
        TextView price = (TextView)shopItemCellView.findViewById(R.id.listingPrice);
        price.setText(shopItem.price + "€");

        return shopItemCellView;
    }
}

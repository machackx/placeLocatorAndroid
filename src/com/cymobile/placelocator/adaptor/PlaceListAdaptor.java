package com.cymobile.placelocator.adaptor;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cymobile.placelocator.R;
import com.cymobile.placelocator.model.CYPlace;

public class PlaceListAdaptor extends BaseAdapter{
	
	ArrayList<CYPlace> _placeList;
	LayoutInflater _layoutInflator;
	Context _context;
		
	public PlaceListAdaptor(Context context, LayoutInflater layoutInflator, ArrayList<CYPlace> placeList){
		_context = context;
		_layoutInflator = layoutInflator;
		_placeList = placeList;
		
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _placeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		LayoutInflater layoutInflator = LayoutInflater.from(_context);
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflator.inflate(R.layout.placelistview, null);
			viewHolder.placeNameTextView = (TextView) convertView.findViewById(R.id.place_name);
			viewHolder.addressTextView = (TextView) convertView.findViewById(R.id.place_address);
			viewHolder.distanceTextView = (TextView) convertView.findViewById(R.id.place_distance);
			viewHolder.ratingImageView = (ImageView) convertView.findViewById(R.id.place_rating);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (_placeList.get(position) != null) {
			CYPlace aPlace = _placeList.get(position);
			viewHolder.placeNameTextView.setText(aPlace.getPlaceName());
			if(Integer.parseInt(aPlace.getPaidCustomer())	 == 1){
				
				
				
				viewHolder.placeNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
			}
			viewHolder.addressTextView.setText(aPlace.getPlaceAddress());
			float distance =  Float.valueOf(aPlace.getDistance());
			viewHolder.distanceTextView.setText(String.format("%.2f", distance) + "M");
			int ratingNumber = Integer.valueOf(aPlace.getRatingNumbers());
			int rating = 0;
			if(ratingNumber > 0) {
				rating = Integer.valueOf(aPlace.getRating())/ratingNumber;
			}
			
			if(rating > 0 && rating < 6) {
				Bitmap ratingMap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.reviews);
				int height = (ratingMap.getHeight() + 1) / 6;
				int width = ratingMap.getWidth();
				Bitmap resizeMap = Bitmap.createBitmap(ratingMap, 0, rating * height, width, (ratingMap.getHeight() + 1)/12);
				viewHolder.ratingImageView.setImageBitmap(resizeMap);
			}
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		public TextView placeNameTextView;
		public ImageView ratingImageView;
		public TextView addressTextView;
		public TextView distanceTextView;
		public LinearLayout rootLayout;
	}
	
}	

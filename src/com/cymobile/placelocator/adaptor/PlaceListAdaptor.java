package com.cymobile.placelocator.adaptor;

import java.util.ArrayList;

import android.content.Context;
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
		
		if (convertView != null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflator.inflate(R.layout.placelistview, null);
			viewHolder.placeNameTextView = (TextView) convertView.findViewById(R.id.place_name);
			viewHolder.addressTextView = (TextView) convertView.findViewById(R.id.place_address);
			viewHolder.distanceTextView = (TextView) convertView.findViewById(R.id.place_distance);
			
		}
		return null;
	}
	
	public static class ViewHolder {
		public TextView placeNameTextView;
		public ImageView ratingImageView;
		public TextView addressTextView;
		public TextView distanceTextView;
		public LinearLayout rootLayout;
	}
	
}	

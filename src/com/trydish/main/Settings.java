package com.trydish.main;

import com.trydish.main.R;
import com.trydish.main.R.layout;
import com.trydish.main.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_settings,
				container, false);

		return view;

	}
	
}

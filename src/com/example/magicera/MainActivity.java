package com.example.magicera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements TextWatcher,
		OnItemSelectedListener {

	private static final int MESSAGE_TEXT_CHANGED = 0;
	private static final int AUTOCOMPLETE_DELAY = 500;
	private static final int THRESHOLD = 3;
	private String latitude, longitude;
	private List<Address> autoCompleteSuggestionAddresses;
	private ArrayAdapter<String> autoCompleteAdapter;
	private Handler messageHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView tv = (TextView) findViewById(R.id.text);
		tv.setText("SB");
		messageHandler = new MyMessageHandler(this);
		autoCompleteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				new ArrayList<String>());
		autoCompleteAdapter.setNotifyOnChange(false);
		AutoCompleteTextView locationinput = (AutoCompleteTextView) this
				.findViewById(R.id.destination);
		// TextView tv = (TextView) findViewById(R.id.hew);
		locationinput.addTextChangedListener(this);
		locationinput.setOnItemSelectedListener(this);
		locationinput.setThreshold(THRESHOLD);
		locationinput.setAdapter(autoCompleteAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg2 < autoCompleteSuggestionAddresses.size()) {
			Address selected = autoCompleteSuggestionAddresses.get(arg2);
			latitude = Double.toString(selected.getLatitude());
			longitude = Double.toString(selected.getLongitude());
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		latitude = longitude = null;

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		String value = arg0.toString();
		if (!"".equals(value) && value.length() >= THRESHOLD) {
			Message msg = Message.obtain(messageHandler, MESSAGE_TEXT_CHANGED,
					arg0.toString());
			messageHandler.sendMessageDelayed(msg, AUTOCOMPLETE_DELAY);
		} else {
			autoCompleteAdapter.clear();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	public class MyMessageHandler extends Handler {

		private Context context;

		// private AsyncTaskSubscriber subscriber;

		public MyMessageHandler(Context context/*
												 * , AsyncTaskSubscriber
												 * subscriber
												 */) {
			this.context = context;
			// this.subscriber = subscriber;
		}

		private void notifyResult(String suggestions) throws IOException {
			autoCompleteSuggestionAddresses = new Geocoder(getBaseContext())
					.getFromLocationName(suggestions, 10);
			latitude = longitude = null;
			autoCompleteAdapter.clear();
			for (Address a : autoCompleteSuggestionAddresses) {
				autoCompleteAdapter.add(a.toString());
			}
			autoCompleteAdapter.notifyDataSetChanged();
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_TEXT_CHANGED) {
				final String enteredText = (String) msg.obj;

				try {
					autoCompleteSuggestionAddresses = new Geocoder(context)
							.getFromLocationName(enteredText, 10);

					Thread t = new Thread() {
						public void run() {
							try {

								runOnUiThread(new Runnable() {
									public void run() {
										try {
											notifyResult(enteredText);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
							} catch (Exception e) {
							}
						}
					};
					t.start();
					// notifyResult(response);
				} catch (IOException ex) {
					// Log.e(GeoCoderAsyncTask.class.getName(),
					// "Failed to get autocomplete suggestions", ex);
					ex.printStackTrace();
				}
			}
		}
	}
}

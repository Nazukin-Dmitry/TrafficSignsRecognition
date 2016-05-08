package com.my.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener {
	private Button btRuntime;
	private Button btPickPhoto;
	private Button btTakePhoto;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Initialize();
		btRuntime.setOnClickListener(this);
		btTakePhoto.setOnClickListener(this);
		btPickPhoto.setOnClickListener(this);
	}
	public void Initialize(){
		btRuntime = (Button)findViewById(R.id.btRuntime);
		btPickPhoto = (Button)findViewById(R.id.btPickPhoto);
		btTakePhoto = (Button)findViewById(R.id.btTakePhoto);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//		MenuItem mi = menu.add(0, 1, 0, "Preferences");
//		mi.setIntent(new Intent(this, PrefActivity.class));
//		return super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, PrefActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btRuntime:
			Intent runtimeIntent = new Intent(MainActivity.this, CameraActivity.class);
			startActivity(runtimeIntent);
			return;
		case R.id.btTakePhoto:
			//Intent takePhotoIntent = new Intent(MainActivity.this, TakePhotoActivity.class);
			//startActivity(takePhotoIntent);
			return;
		case R.id.btPickPhoto:
			Intent pickPhotoIntent = new Intent(MainActivity.this, PhotoActivity.class);
			startActivity(pickPhotoIntent);
			break;
		default:
			break;
		}
	}

}

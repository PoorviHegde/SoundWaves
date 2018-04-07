package com.example.poorvi_hegde.upgradedsoundwaves;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

import com.synaptik.soundwave.R;

public class MainActivity extends Activity
{
	MySurfaceView mView1, mView2, mView3, mView4;
	AudioMonitor r1, r2, r3, r4;
	OnUpdateListener listener1, listener2, listener3, listener4;
	Thread mThread1, mThread2, mThread3, mThread4;
	RadioGroup radioGroup;
	int selected = -1;

	private static final int MY_PERMISSIONS_REQUEST = 1234;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Button start = findViewById(R.id.button);
		Button stop = findViewById(R.id.button3);
		mView1 = findViewById(R.id.my_surface_view);
		mView2 = findViewById(R.id.my_surface_view2);
		mView3 = findViewById(R.id.my_surface_view3);
		mView4 = findViewById(R.id.my_surface_view4);
		radioGroup = findViewById(R.id.radio_group);

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				if(checkedId == R.id.one)
				{
					selected = 1;
				}
				else if(checkedId == R.id.two)
				{
					selected = 2;
				}
				else if(checkedId == R.id.three)
				{
					selected = 3;
				}
				else if(checkedId == R.id.four)
				{
					selected = 4;
				}
			}
		});

		start.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				initialize(selected);
			}
		});

		stop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				done(selected);
			}
		});

		setupListeners();
	}

	private void setupListeners()
	{
		listener1 = new OnUpdateListener()
		{
			@Override
			public void update(final short[] bytes, final int length, final float sampleLength)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						mView1.setData(bytes, length, sampleLength);
					}
				});
			}

			@Override
			public void quit(int errorCode)
			{
				MainActivity.this.quit(errorCode);
			}
		};

		listener2 = new OnUpdateListener()
		{
			@Override
			public void update(final short[] bytes, final int length, final float sampleLength)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						mView2.setData(bytes, length, sampleLength);
					}
				});
			}

			@Override
			public void quit(int errorCode)
			{
				MainActivity.this.quit(errorCode);
			}
		};

		listener3 = new OnUpdateListener()
		{
			@Override
			public void update(final short[] bytes, final int length, final float sampleLength)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						mView3.setData(bytes, length, sampleLength);
					}
				});
			}

			@Override
			public void quit(int errorCode)
			{
				MainActivity.this.quit(errorCode);
			}
		};

		listener4 = new OnUpdateListener()
		{
			@Override
			public void update(final short[] bytes, final int length, final float sampleLength)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						mView4.setData(bytes, length, sampleLength);
					}
				});
			}

			@Override
			public void quit(int errorCode)
			{
				MainActivity.this.quit(errorCode);
			}
		};
	}

	private void quit(int errorCode)
	{
		if(errorCode == OnUpdateListener.ERROR_CODE_MICROPHONE_LOCKED)
		{
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Error")
					.setMessage("Unable to capture audio. Likely permission for the microphone isn\\'t enabled. Please verify this in your device\\'s application settings.")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							ActivityCompat.finishAffinity(MainActivity.this);
						}
					})
					.show();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		permissionCheck();
	}

	protected void initialize(int n)
	{
		switch(n)
		{
			case 1:
			{
				r1 = new AudioMonitor(listener1, 44100);
				if(r1.isInitialized())
				{
					mThread1 = new Thread(r1);
					mThread1.start();
				}
			}
			case 2:
			{
				r2 = new AudioMonitor(listener2, 29400);
				if(r2.isInitialized())
				{
					mThread2 = new Thread(r2);
					mThread2.start();
				}
			}
			case 3:
			{
				r3 = new AudioMonitor(listener3, 34900);
				if(r3.isInitialized())
				{
					mThread3 = new Thread(r3);
					mThread3.start();
				}
			}
			case 4:
			{
				r4 = new AudioMonitor(listener4, 52300);
				if(r4.isInitialized())
				{
					mThread4 = new Thread(r4);
					mThread4.start();
				}
			}
		}
	}

	protected void done(int n)
	{
		switch(n)
		{
			case 1:
			{
				if(r1 != null && r1.isInitialized())
				{
					r1.done = true;
					try
					{
						mThread1.join();
					}
					catch(InterruptedException e)
					{
						Log.e("main",Log.getStackTraceString(e));
					}
				}
			}
			case 2:
			{
				if(r2 != null && r2.isInitialized())
				{
					r2.done = true;
					try
					{
						mThread2.join();
					}
					catch(InterruptedException e)
					{
						Log.e("main",Log.getStackTraceString(e));
					}
				}
			}
			case 3:
			{
				if(r3 != null && r3.isInitialized())
				{
					r3.done = true;
					try
					{
						mThread3.join();
					}
					catch(InterruptedException e)
					{
						Log.e("main",Log.getStackTraceString(e));
					}
				}
			}
			case 4:
			{
				if(r4 != null && r4.isInitialized())
				{
					r4.done = true;
					try
					{
						mThread4.join();
					}
					catch(InterruptedException e)
					{
						Log.e("main",Log.getStackTraceString(e));
					}
				}
			}

		}


	}

	@Override
	protected void onStop()
	{
		super.onStop();

        /*if (r != null && r.isInitialized()) {
            r.done = true;
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	protected boolean permissionCheck()
	{
		boolean result = false;

		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

		if(permissionCheck == PackageManager.PERMISSION_GRANTED)
		{
			//initialize();
		}
		else
		{
			if(ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.RECORD_AUDIO))
			{

				new AlertDialog.Builder(this)
						.setTitle("Alert")
						.setMessage("This application requires the RECORD_AUDIO permission in order to display audio waves on the screen. Without it, this application will not be able to function")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								ActivityCompat.requestPermissions(MainActivity.this,
										new String[]{Manifest.permission.RECORD_AUDIO},
										MY_PERMISSIONS_REQUEST);
							}
						})
						.show();
			}
			else
			{
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.RECORD_AUDIO},
						MY_PERMISSIONS_REQUEST);
			}
		}
		return result;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if(requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			initialize(1);
		}
		else
		{
			ActivityCompat.finishAffinity(this);
		}
	}
}

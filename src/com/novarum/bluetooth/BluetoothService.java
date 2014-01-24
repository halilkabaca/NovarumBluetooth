package com.novarum.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiIntentWrapper;

import com.novarum.bluetooth.NovarumbluetoothModule.dataReceiver;

import android.R;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BluetoothService extends Service  
{
	
	 KrollDict devicelist              = null;
     BluetoothDevice  bluetoothDevice  = null;
     static BluetoothAdapter bluetoothAdapter = null;
     public static BluetoothSocket  btsocket;
     
     public static String DEFAULT_UUID        = "00001101-0000-1000-8000-00805F9B34FB";
     
     private static InputStream inputStream   = null;
     private static OutputStream outputStream = null;
     public boolean isConnected               = false; 
     public dataReceiver datareceiver;
     public AcceptThread acceptthread;
     public String SERVERNAME                 = "NovarumBluetooth";     
     private static final String TAG          = "NovarumbluetoothModule";
     
	
	public BluetoothService() 
	{
		Log.w(TAG,"Service Created");
	}

	

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Log.w(TAG,"onLowMemory");
		NovarumbluetoothModule.sendEvent("nb_onLowMemory",null);
	}



	public void onStart(Intent intent, int startId) 
	{
		Log.w(TAG,"Bluetooth service onStart");
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		
		//Start foreground//
		Notification notification = new Notification();
		Intent notificationIntent = new Intent(this, BluetoothService.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "bluetooth","Bluetooth running", pendingIntent);
		startForeground(2, notification);		
		//Start foreground//
		
		
		//if there is a device mac, try to connect it//
		String mac = intent.getExtras().getString("MacAddress");
		
		if(!mac.equals(""))
		{
			connect(mac);
		}
		
	}
	
	public void onCreate(Bundle savedInstanceState) 
	{
       
		Log.w(TAG,"Bluetooth service onCreate");
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		Log.w(TAG,"Bluetooth service onDestroy");
		
		//close connection//
		try
		{
			if(isConnected)
			{
				if(inputStream != null)
				   inputStream.close();
				
				if(outputStream != null)
				   outputStream.close();
				
				if(btsocket != null)
					btsocket.close();
				
				
				isConnected = false;
			}
		}
		catch(Exception e)
		{
		   e.printStackTrace();
		}
		
	}
	
	
	public boolean pairDevice(BluetoothDevice btDevice)
	{
		
		Method createBondMethod = null;
		boolean returnValue = false;
		
		try 
		{
			createBondMethod = BluetoothDevice.class.getMethod("createBond");
			
		} 
		catch (NoSuchMethodException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			returnValue = (Boolean) createBondMethod.invoke(btDevice);
		} 
		catch (IllegalAccessException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return returnValue;
	}	
	
	public boolean connect(String devicemac)
	{
		
		Log.w("TAG","connect() called on BluetoothService");
		
		if(devicemac == null)
		   return false;
		
		bluetoothDevice = bluetoothAdapter.getRemoteDevice(devicemac);
		
		if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) 
		{
			if(pairDevice(bluetoothDevice))
			{
				
				return socketConnect(bluetoothDevice);
			}
			else
			{
				postError("Could not pair device");
				return false;
			}
		}
		else
		{
			return socketConnect(bluetoothDevice);				
		}
	
	}	
	
	
	public boolean socketConnect(BluetoothDevice btDevice)
	{
		try
		{
			btsocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(DEFAULT_UUID));
			
			Method m = btDevice.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
			
			btsocket = (BluetoothSocket) m.invoke(btDevice, 1);
			btsocket.connect();	

			inputStream   = btsocket.getInputStream();
			outputStream  = btsocket.getOutputStream();			
			
			isConnected = true;
			
			//open a thread for reading data//
			datareceiver = new dataReceiver();
			datareceiver.start();			
			//open a thread for reading data//
			
			//Fire an event//
			NovarumbluetoothModule.sendEvent("nb_onConnect",null);
			return true;
			
		}
		catch(Exception e)
		{
			postError(e.getMessage());
			return false;
		}
		
		
	}	
	
	
	private void postError(String Error)
	{
		KrollDict data = new KrollDict();
		data.put("error", Error);
		NovarumbluetoothModule.sendEvent("nb_onError", data);
	}	
	
	
	private class AcceptThread extends Thread 
	{
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() 
	    {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try 
	        {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVERNAME, UUID.fromString(DEFAULT_UUID));
	        } 
	        catch (IOException e) 
	        { 
	        	postError(e.getMessage());
	        }
	        
	        mmServerSocket = tmp;
	    }
	 
	    public void run() 
	    {
	        btsocket = null;
	        
	        // Keep listening until exception occurs or a socket is returned
	        while (true) 
	        {
	            try 
	            {
	            	btsocket = mmServerSocket.accept();
	            } 
	            catch (IOException e) 
	            {
	            	postError(e.getMessage());
	            }
	            
	            // If a connection was accepted
	            if (btsocket != null) 
	            {
	                // Do work to manage the connection (in a separate thread)
	                manageConnectedSocket();
	                
	                try 
	                {
						mmServerSocket.close();
					} 
	                catch (IOException e) 
	                {
	                	postError(e.getMessage());
	                	// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() 
	    {
	        try 
	        {
	            mmServerSocket.close();
	        } 
	        catch (IOException e) 
	        { 
	        	postError(e.getMessage());
	        }
	    }
	}
	
	
	private void manageConnectedSocket()
	{
		try 
		{
			inputStream   = btsocket.getInputStream();
			outputStream  = btsocket.getOutputStream();
		} 
		catch (IOException e) 
		{
			postError(e.getMessage());
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		isConnected = true;
				
		//open a thread for reading data//
		try
		{
			datareceiver = new dataReceiver();
			datareceiver.start();			
		}
		catch(Exception e)
		{
			postError(e.getMessage());
		}
		//open a thread for reading data//
		
		NovarumbluetoothModule.sendEvent("nb_onConnect",null);
		
	}
	
	
	//Halilk: Bluetooth data reciever thread//
	class dataReceiver extends Thread 
	{
		public void run() 
		{

			while (isConnected) 
			{
				if (inputStream != null) 
				{
					try 
					{

						byte[] data = new byte[1024]; //read data 1kb

						int length = inputStream.read(data);
						
						
						if(length > 0)
						{
							String ReceivedData = new String(data,0,length,"UTF-8");
							
							PostReceivedData(ReceivedData); // send received data to the app
						}

					} 
					catch (IOException e) 
					{
						postError(e.getMessage());
					}
				}
			}
		}
	}
	
	
	private void PostReceivedData(String data)
	{
		if(data == null)
			return;
		
		//Activity can be closed, open the activity//
		TiApplication appContext = TiApplication.getInstance();
		Activity activity = appContext.getRootOrCurrentActivity();
		
		final TiIntentWrapper barcodeIntent = new TiIntentWrapper(new Intent(activity,appContext.getRootOrCurrentActivity().getClass()));
		
		try
		{			
			barcodeIntent.getIntent().putExtra("BluetoothData",data);
			appContext.startActivity(barcodeIntent.getIntent());
		}
		catch(Exception e)
		{
			Log.w(TAG,"Opening activity error on Service-PostReceivedData: "+e.getMessage());
			e.printStackTrace();
			
			//Then activity is background, try setting the flag//
			barcodeIntent.getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try
			{
				appContext.startActivity(barcodeIntent.getIntent());
			}
			catch(Exception e2)
			{
				Log.w(TAG,"Opening activity error on Service-PostReceivedData: "+e.getMessage());
				e.printStackTrace();				
			}
			
		}
		
		
		//Send the data//
		KrollDict receivedict = new KrollDict();
		
		receivedict.put("data", data);
		NovarumbluetoothModule.sendEvent("nb_onReceiveData", receivedict);		
		
		
		
	}	
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}
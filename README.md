NovarumBluetooth
================

Appcelerator Titanium Bluetooth Module for Android

Update: 

--Right now running as a background service is supported. To activate it, you should call 
novarumbluetooth.useService() and if data is received from bluetooth while the application is not running
then application will be started again and you can get the data from the intent. Please check the example for details.

--New event (nb_onLowMemory) added for background service to be fired when the device is running low on memory.

Note: This is not an official bluetooth module nor 100% ready code for production.
Please use it at your own risk.

Right now only connecting as a client is supported. Tested especially for
connecting bluetooth modules such as JY-MCU

## Accessing the novarumbluetooth Module

To access this module from JavaScript, you would do the following:

	var novarumbluetooth = require("com.novarum.bluetooth");

The novarumbluetooth variable is a reference to the Module object.	

## Reference

### novarumbluetooth.Disconnect
Disconnects from the bluetooth and if service is used, destroys the service

### novarumbluetooth.useService
Enables the background service to be used for bluetooth communication. If this function is called, even when the application
is not running bluetooth connection will be kept alive and on receiving data main activity will be started with the data (as intent extra)
Please check the example for the details

### novarumbluetooth.enableBluetooth

Enables bluetooth adapter

### novarumbluetooth.disableBluetooth

Disables bluetooth adapter

### novarumbluetooth.searchDevices

Searches for bluetooth devices

### novarumbluetooth.getPairedDevices

Retrieves already paired bluetooth devices. You can connect to them using connect method and providing mac address

### novarumbluetooth.connect(macadress)

connects to the device with the given mac address

### novarumbluetooth.sendData(data)

if device is connected, it sends the given text data to the other end. To receive data,
you must register nb_onReceiveData event


### novarumbluetooth.isConnected()

checks if the device is connected

### novarumbluetooth.startServer()

starts server. Note that this functions are not fully implemented yet and may not work

### novarumbluetooth.stopServer()

stops server. Note that this functions are not fully implemented yet and may not work

### novarumbluetooth.setServerName(name)

sets the server name. Note that this functions are not fully implemented yet and may not work

### novarumbluetooth.setUUID(uuid)

sets the uuid will be used. Default is: 00001101-0000-1000-8000-00805F9B34FB

### novarumbluetooth.makeDiscoverable()

makes the device be discoverable for 300 seconds

## Events

###nb_onLowMemory

If background service is used, this is fired when the phone is running low on memory. It's advised to clean up some code
or the service can be destroyed by the system

###nb_DevicesFound

Fired when a new bluetooth device is found. This event is fired after searchDevices function call

###nb_onConnect

Fired when a new successfull connection is made

###nb_onReceiveData

Fired when new data recevied

###nb_onError

Fired when an error ocurred. Contains the details of the error

###nb_onServerStarted

Fired when server is started successfully.


## Usage

If you don't want to build it from the source code, please copy the module zip file provided to your project folder.
Please refer to example app, it covers the main functionality.


## Author

Halil Kabaca
halil.kabaca@novarumsoftware.com 

## License

The MIT License (MIT)


----------------------------------
Stuff our legal folk make us say:

Appcelerator, Appcelerator Titanium and associated marks and logos are 
trademarks of Appcelerator, Inc. 

Titanium is Copyright (c) 2009-2012 by Appcelerator, Inc. All Rights Reserved.

Titanium is licensed under the Apache Public License (Version 2). Please
see the LICENSE file for the full license.


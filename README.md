NovarumBluetooth
================

Appcelerator Titanium Bluetooth Module for Android

Note: This is not an official bluetooth module nor 100% ready code for production.
Please use it at your own risk.

Right now only connecting as a client is supported. Tested especially for
connecting bluetooth modules such as JY-MCU

## Accessing the novarumbluetooth Module

To access this module from JavaScript, you would do the following:

	var novarumbluetooth = require("com.novarum.bluetooth");

The novarumbluetooth variable is a reference to the Module object.	

## Reference


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

###nb_DevicesFound

Fired when a new bluetooth device is found. This event is fired after searchDevices function call

###nb_onConnect

Fired when a new successfull connection is made

###nb_onReceiveData

Fired when new data recevied

###nb_onReceiveData

Fired when new data recevied

###nb_onError

Fired when an error ocurred. Contains the details of the error

###nb_onServerStarted

Fired when server is started successfully.

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
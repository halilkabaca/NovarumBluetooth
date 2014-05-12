// Novarum bluetooth test app
// Only connecting as a client is tested
// Server connections are half-implemented and not tested.
// Module gets bluetooth and bluetooth admin rights


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'white',
	layout:'vertical'
});



// TODO: write your module tests here
var novarumbluetooth = require('com.novarum.bluetooth');
Ti.API.info("module is => " + novarumbluetooth);


//Enable bluetooth//
novarumbluetooth.enableBluetooth();



	if (Ti.Platform.name == "android") 
	{
	  
		var mac = "";
		
		var tabledata = [];
		var section;
		
		var devicesTemplate = {
		    properties:
		    {
		    	backgroundColor:'#fff'
		    },
		    
		    childTemplates: [
		        {
		            type: 'Ti.UI.Label', // Use a label
		            bindId: 'rowname',  // Bind ID for this label
		            properties: {        // Sets the Label.left property
		                color:'#b60000',
		                left:0,
		                top:5,
		                backgroundColor:'#fff',
		                font: {fontSize:14},
		                text:'Name',
		                width:150
		            }
		        },
		        {
		            type: 'Ti.UI.Label',
		            bindId: 'rowmac', 
		            properties: {        
		                color:'#808080',
		                left:160,
		                top:5,
		                backgroundColor:'#fff',
		                font: {fontSize:14},
		                text:'',
		                width:'auto'
		            }
		        },
  
		     ]   	    
		
		
		}	
	
	
	
		var devicestable = Ti.UI.createListView({
		    // Maps the plainTemplate object to the 'plain' style name
		    templates: { 'plain': devicesTemplate},
		    // Use the plain template, that is, the plainTemplate object defined earlier
		    // for all data list items in this list view
		    defaultItemTemplate: 'plain',
		    top:50,
		    height:200          
		});
		
		devicestable.addEventListener('itemclick', function(e)
		{	
			var item = section.getItemAt(e.itemIndex);
			
			alert("Connecting to: "+item.rowname.text);
			novarumbluetooth.connect(item.rowmac.text);
			
		});			 
		
		
		win.add(devicestable);
		
		novarumbluetooth.addEventListener('nb_DevicesFound', function(e)
		{
			    tabledata.push({
			        // Maps to the rowtitle component in the template
			        // Sets the text property of the Label component
			        rowname : { text: e.name},
			        rowmac : { text: e.macaddress},
			        // Sets the regular list data properties
			        properties : 
			        {
			            itemId: 0
			        }
			    });		    
		    
			   section = Ti.UI.createListSection({items: tabledata});
			   devicestable.sections = [section];
			   
	 
		});
		
		novarumbluetooth.addEventListener('nb_onReceiveData', function(e)
		{
		   
		   alert(e.data); 
		    
		});
		
	
		novarumbluetooth.addEventListener('nb_onError', function(e)
		{
		   
		   alert(e.error); 
		    
		});
		
		novarumbluetooth.addEventListener('nb_onConnect', function(e)
		{
		   
		   alert("Connected"); 
		    
		});						
		
		
		
	    var sendbutton = Titanium.UI.createButton({
		   title: 'Send',
		   top: 10,
		   color: '#000',
		   width: '80%',
		   font: { fontSize:20},   
		});	
		
		sendbutton.addEventListener("click",function()
		{		 
			novarumbluetooth.sendData("Test");
		});			
		
		win.add(sendbutton);
		
		
	    var searchbutton = Titanium.UI.createButton({
		   title: 'Search Devices',
		   top: 10,
		   color: '#000',
		   width: '80%',
		   font: { fontSize:20},   
		});
		
		searchbutton.addEventListener("click",function()
		{		 
			novarumbluetooth.searchDevices();		
		});					
		
		win.add(searchbutton);
		 
	  
	}
	
win.open();	


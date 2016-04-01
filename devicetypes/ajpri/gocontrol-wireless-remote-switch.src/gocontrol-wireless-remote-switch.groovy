/**
 *  GoControl WA00Z-1
 *
 *  Copyright 2016 Austin Pritchett
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	
 */
 
 /*
 
 	Version: Milestone 4
	What Works:
		Basic Functionality
 		Battery Reporting
        Device Fingerprinting (Auto-Identify)
        Simulator Data
        Held Events
	What still needs work:
        Inverting buttons
		Performance Optimizations
		Code Clean-up
 */

metadata {
	definition (name: "GoControl Wireless Remote Switch", namespace: "ajpri", author: "Austin Pritchett") {
		capability "Button"
		capability "Configuration"
        capability "Battery"

		fingerprint deviceId:"0x1801", inClusters:"0x5E, 0x86, 0x72, 0x5B, 0x85, 0x59, 0x73, 0x70, 0x80, 0x84, 0x5A, 0x7A", outClusters:"0x5B, 0x20"        
	}

	simulator {
		// TODO: define status and reply messages here
        status "button 1 pushed":  "command: 5B03, payload: 40 00 01"
		status "button 2 pushed":  "command: 5B03, payload: 41 00 02"
	}

	tiles {
		standardTile("button", "device.button", width: 2, height: 2) {
			state "default", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
        main "button"
		details(["button", "battery"])
	}
    preferences {
       input "invBtn", "bool", title: "Invert Top/Bottom Buttons", dafault: false,
              required: false, displayDuringSetup: true
    }
}

// parse events into attributes
def parse(String description) {
	//log.debug "Parsing '${description}'"
    
    def results = []
	if (description.startsWith("Err")) {
	    results = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description)

		if(cmd) results += zwaveEvent(cmd)
		if(!results) results = [ descriptionText: cmd, displayed: true ]
	}
	return results

}

def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
    Integer button = (cmd.sceneNumber) as Integer    

    if(cmd.keyAttributes == 0){
		createEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
    }else if(cmd.keyAttributes == 1){
		createEvent(name: "button", value: "released", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was released", isStateChange: true)
    }else if(cmd.keyAttributes == 2){
		createEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)

    }   
   
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpNotification cmd) {
	def result = [createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)]

	// Only ask for battery if we haven't had a BatteryReport in a while
	if (!state.lastbatt || (new Date().time) - state.lastbatt > 24*60*60*1000) {
		result << response(zwave.batteryV1.batteryGet())
		result << response("delay 1200")  // leave time for device to respond to batteryGet
	}
    
    //int invBtnI = (invBtn) ? 1 : 0;
    //This line could help with inverting buttons, but not tested.
	//result << response(zwave.configurationV1.configurationSet(parameterNumber:4, size:1, scaledConfigurationValue:invBtnI).format())
    //result << response(zwave.wakeUpV1.wakeUpIntervalSet(seconds:4 * 3600, nodeid:zwaveHubNodeId).format())    

    
	result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())
    result
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
    //log.debug cmd.batteryLevel
    
    def result = []
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} battery is low"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	state.lastbatt = now()
	result << createEvent(map)

	result
}

def configurationCmds() {
	def cmds = []
	def hubId = zwaveHubNodeId
	int invBtnI = (invBtn) ? 1 : 0;	
	cmds << zwave.configurationV1.configurationSet(parameterNumber:4, size:1, scaledConfigurationValue:invBtnI).format()
		
	cmds
}

def configure() {
	//def cmds = configurationCmds()
	//log.debug("Sending configuration: $cmds")
	//return cmds
}
/*
	log.debug "Executing 'configure'"
    zwave.wakeUpV1.wakeUpIntervalSet(seconds:4 * 3600, nodeid:zwaveHubNodeId).format()
    
    
    int invBtnI = (invBtn) ? 1 : 0;
    //This line could help with inverting buttons, but not tested.
	zwave.configurationV1.configurationSet(parameterNumber:4, size:1, scaledConfigurationValue:invBtnI).format()
*/    
        

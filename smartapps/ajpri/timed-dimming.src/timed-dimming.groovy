/**
 *  Timed Dimming
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
 */
definition(
    name: "Timed Dimming",
    namespace: "ajpri",
    author: "Austin Pritchett",
    description: "When Light is turned on, dimming state will change based on time of day",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Light") {
		  input "dimmer", "capability.switchLevel", title: "Select Light to control"
	}
    
    section("Time") {
		  input "startTime", "time", title: "From"
		  input "endTime", "time", title: "Until"
          input "num1", "number", title: "Set brightness to"
	}
       

}

def installed() {
	log.debug "Installed with settings: ${settings}"
    state.isRunning = false

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(dimmer, "switch", switchHandler)
}


def switchHandler(evt) {
	def startTOD = timeToday(startTime, location.timeZone)
	def endTOD = timeToday(endTime, location.timeZone)
    
    
	    if (evt.value == "on") {		
    	    if (timeOfDayIsBetween(startTOD, endTOD, new Date(), location.timeZone)) { 
            	log.debug "Time Of Day Pass"
				dimmer.setLevel(num1)
			} else {
				log.debug "TOD Fail"
                
                log.debug "startTime: " + startTime
                log.debug new Date()
                log.debug "endTime: " + endTime
                log.debug "---------------------"
                log.debug "startTOD: " + startTOD
                log.debug new Date()
                log.debug "endTOD: " + endTOD
        		dimmer.setLevel(100)
        	}
		} else if (evt.value == "off") {
        
		}
    
}

def handler() {
       state.isRunning = false
}
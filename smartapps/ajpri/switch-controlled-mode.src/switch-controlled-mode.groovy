/**
 *  Switch Mode
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
    name: "Switch-controlled Mode",
    namespace: "ajpri",
    author: "Austin Pritchett",
    description: "Change SmartThings Mode depending state of Switch",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Control this switch") {
        input "switch1", "capability.switch", multiple: false
    }
section("Select Modes") {
        input "onMode", "mode", title: "When switch is on"
		input "offMode", "mode", title: "When switch is off"       
    }

}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(switch1, "switch", switchHandler)
}

def switchHandler(evt) {
    log.debug "$evt.value"
    if (evt.value == "on") {
        setLocationMode(onMode)
    } else if (evt.value == "off") {
        setLocationMode(offMode)

    }
}
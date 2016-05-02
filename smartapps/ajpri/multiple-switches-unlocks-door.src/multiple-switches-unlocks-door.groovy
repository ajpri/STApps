/**
 *  Multiple Switches Unlocks Door
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
 
 /*
 Github Installation (recommended, easier to update):
	For easier updating, add the GitHub repository in your IDE account. Click "Update from Repo" and select "
    	"Multiple Switches Unlocks Door"
	- Owner: ajpri
	- Name: STApps
	- Branch: master

	Manual Installation:
	1) Go the the “My SmartApps” section of the IDE.
	2) Click “New SmartApp”
	3) Click “From Code" and paste in the code from the GitHub link. Click Create.
	4) Click Publish > For Me. 
	5) The SmartApp is ready for use! It'll be in the "My Apps" Section of the IDE.
 
 */
 
definition(
    name: "Multiple Switches Unlocks Door",
    namespace: "ajpri",
    author: "Austin Pritchett",
    description: "If both switches are on, a door will unlock.",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Select Switches") {
		input "switch1", "capability.switch", title: "If this switch is on"
        input "switch2", "capability.switch", title: "And this switch is on"
	}
    section("Select Lock"){
		input "lock1", "capability.lock", title: "Unlock this door."

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
    subscribe(switch2, "switch", switchHandler)

}

def switchHandler(evt) {
    if (evt.value == "on") {
        
        def s1Value = switch1.currentValue("switch")
        def s2Value = switch2.currentValue("switch")

		if (s1Value == "on" && s2Value == "on"){
        	log.debug "Unlocking door"
            lock1.unlock()
        }

        
    }
}
// TODO: implement event handlers
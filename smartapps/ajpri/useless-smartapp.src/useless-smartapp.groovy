/**
*  Useless SmartApp
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
Version 1
April 14th, 2016
Initial Release
*/

definition(
    name: "More Useless SmartApp",
    namespace: "ajpri",
    author: "Austin Pritchett",
    description: "When a switch is turned off, it'll turn on after a random delay. ",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Select Lights...") {
        input "switches", "capability.switch", multiple: true
    }

    section("Delay before turning off.") {
        input "delay", "decimal", title: "Number of seconds", required: true, defaultValue: "2"
    }
    section("Easter Egg Devices to Randomly Dim...") {
    	input "levels", "capability.switchLevel", title: "Dimmers", required: false, multiple: true
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
    subscribe(switches, "switch", switchHandler)
state.lastCmds = []
}

def switchHandler(evt) {
    log.debug "$evt.value"
    //track if last x button presses are up, down, up, down (on/off)
    //if (state.lastCmds == null) {
    //	state.lastCmds = []
    //}
    //if(!state.lastCmds) {
    //	state.lastCmds = []
    //}
    state.lastCmds.push(evt.value)
    log.debug state.lastCmds
    //def state.statesToCheck =  switches.statesSince("switch", new Date(0), [max:4]).collect{[it.date,it.value]}
    //log.debug statesToCheck
    if (state.lastCmds == ["on","off","on","off"])
    {
        state.easterEggMode = true
        log.debug "In easter egg mode"
    }

    if (state.easterEggMode == false) {
        if (evt.value == "on") {
            runIn(delay, switchesOff)
        } else if (evt.value == "off") {

        }
    } else
    {
    //have some fun, in easter egg mode
    log.debug "In easter egg mode again"
    runIn(60,resetEasterEggMode)
    doEasterEggMode()
    }
}

def switchesOff() {
    switches.off()
}

def resetEasterEggMode() {
	state.easterEggMode = false
    //state.statesToCheck = null
    state.lastCmds.clear()
}

def doEasterEggMode() {
	levels.each {
    	Random random = new Random()
        //it.on()
        def val = random.nextInt(100)+1
        log.debug val
        it.setLevel(val)
    }
}

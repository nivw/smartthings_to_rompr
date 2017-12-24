import groovy.json.JsonSlurper
import groovy.json.JsonOutput

metadata {
	definition (name: "Rompr", namespace: "ngw", author: "Niv Gal Waizer") {
    	capability "Music Player"
	capability "Switch"
        capability "Switch Level"
        capability "Refresh"
        command "preset1"
        command "preset2"
        command "preset3"
        command "preset4"
        command "preset5"
        command "preset6"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
    	    //main
	    standardTile("status", "device.status", width: 1, height: 1, decoration: "flat") {
      	        state "play", label:'Playing', action:"music Player.pause", icon:"st.Electronics.electronics19", nextState:"pause", backgroundColor: "#00a0dc"
      		state "stop", label:'Stopped', action:"music Player.play", icon:"st.Electronics.electronics19", nextState:"play", backgroundColor:"#ffffff"
      		state "pause", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics19", nextState:"play", backgroundColor:"#ffffff"
	    }
	    // Row 1
            standardTile("nextTrack", "device.status", width: 3, height: 1, decoration: "flat") {
      		state "next", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn", backgroundColor:"#ffffff"
	    }
	    standardTile("playpause", "device.status", width: 3, height: 2, decoration: "flat") {
	        state "default", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"play", backgroundColor:"#ffffff"
		state "play", label:'', action:"music Player.pause", icon:"st.sonos.pause-btn", nextState:"pause"
            	state "pause", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"play", backgroundColor:"#ffffff"
            	state "stop", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"play", backgroundColor:"#ffffff"
	    }
            standardTile("previousTrack", "device.status", width: 3, height: 1, decoration: "flat") {
      		state "previous", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn", backgroundColor:"#ffffff"
		}
	    standardTile("stop", "device.status", width: 3, height: 2, decoration: "flat") {
	      	state "stop", label:'', action:"music Player.stop", icon:"st.sonos.stop-btn", nextState:"play", backgroundColor:"#ffffff"
	    }
	    // Row 2
	     standardTile("preset1", "device.preset1", width: 1, height: 1, decoration: "flat") {
	         state "default", label:'${currentValue}', action:"preset1"
	     }
	     standardTile("preset2", "device.preset2", width: 1, height: 1, decoration: "flat") {
	      		state "default", label:'${currentValue}', action:"preset2"
	     }
	     standardTile("preset3", "device.preset3", width: 1, height: 1, decoration: "flat") {
	      		state "default", label:'${currentValue}', action:"preset3"
             }
	     standardTile("preset4", "device.preset4", width: 1, height: 1, decoration: "flat") {
		    state "default", label:'${currentValue}', action:"preset4"
	     }
	     standardTile("preset5", "device.preset5", width: 1, height: 1, decoration: "flat") {
	      		state "default", label:'${currentValue}', action:"preset5"
	     }
	     standardTile("preset6", "device.preset6", width: 1, height: 1, decoration: "flat") {
	      		state "default", label:'${currentValue}', action:"preset6"
	     }
		// Row 3
		standardTile("refresh", "capability.refresh", width: 2, height: 2,  decoration: "flat") {
      		state ("default", label:"Refresh", action:"refresh.refresh", icon:"st.secondary.refresh")
    	    }
	    standardTile("mute", "device.mute", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
		state "unmuted", label:"Mute", action:"music Player.mute", icon:"st.custom.sonos.unmuted", nextState:"muted"
		state "muted", label:"Unmute", action:"music Player.unmute", icon:"st.custom.sonos.muted", nextState:"unmuted"
	    }
	    controlTile("level", "device.level", "slider", width: 2, height: 2) {
		state "level", label:'${currentValue}', action:"switch level.setLevel"
	    }
	    valueTile("file", "device.file", width: 3, height: 2, decoration: "flat") {
		state ("name", label:'${currentValue}')
	    }
            valueTile("error", "device.error", width: 3, height: 2, decoration: "flat") {
		state ("error", label:'${currentValue}')
            }
            standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
                state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
                state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
            }
        main("status")
        details([
        "stop","playpause",
        "previousTrack","nextTrack",
        "preset1","preset2","preset3","preset4","preset5","preset6",
        "refresh","mute","level",
        "file","error"
        ])
	}
}

preferences {
    input name: "IP", type: "text", title: "IP", defaultValue:"192.168.1.12", required: false, displayDuringSetup: true
    input name: "Port", type: "text", title: "Port", defaultValue:"80", required: false, displayDuringSetup: true
    input name: "Preset1", type: "text", title: "Preset1", defaultValue:"kcrw", required: false, displayDuringSetup: true
    input name: "Preset2", type: "text", title: "Preset2", defaultValue:"bet", required: false, displayDuringSetup: true
    input name: "Preset3", type: "text", title: "Preset3", defaultValue:"99fm", required: false, displayDuringSetup: true
    input name: "Preset4", type: "text", title: "Preset4", defaultValue:"kol4", required: false, displayDuringSetup: true
    input name: "Preset5", type: "text", title: "Preset5", defaultValue:"kol5", required: false, displayDuringSetup: true
    input name: "Preset6", type: "text", title: "Preset6", defaultValue:"kol6", required: false, displayDuringSetup: true
          
}

def message(msg){
    log.debug("***** '${msg}'")
}

// parse events into attributes
def parse(String description) {
    message("Parsing '${description}'")
    def returned_events = []
    def map = stringToMap(description)
    if (map.headers && map.body) { //got device info response
        if (map.body) {
            def bodyString = new String(map.body.decodeBase64())
            message("body = $bodyString")
            def slurper = new JsonSlurper()
            def result = slurper.parseText(bodyString)
            if (result.containsKey("volume")) {
                message("setting volume to ${result.volume}")
                returned_events << createEvent(name: "level", value: result.volume)
            }
            if (result.containsKey("state")) {
                message("setting state to ${result.state}")
                returned_events << createEvent(name: "status", value: result.state)
                message("setting playpause to ${result.state}")
                returned_events << createEvent(name: "playpause", value: result.state)
                if (result.state == 'play') {
                    message("setting switch to on")
                    returned_events << createEvent(name: "switch", value: 'on')
                } else {
                    message("setting switch to off")
                    returned_events << createEvent(name: "switch", value: 'off')
                }
	    }
            if (result.containsKey("file")) {
      	        message("replay said that file is ${result.file}")
                switch (result.file) {
        	    case {it.contains("$Preset1") }:
            	        message("preset $Preset1 is playing")
            	        returned_events << createEvent(name: "file", value: "$Preset1")
                        break
 		    case {it.contains("$Preset2") }:
 		        message("preset $Preset2 is playing")
			returned_events << createEvent(name: "file", value: "$Preset2")
                	break
		    case {it.contains("$Preset3") }:
            	        message("preset $Preset3 is playing")
            	        returned_events << createEvent(name: "file", value: "$Preset3")
                        break
                    case {it.contains("$Preset4") } :
            	        message("preset $Preset4 is playing")
            	        returned_events << createEvent(name: "file", value: "$Preset4")
                        break
                    case {it.contains("$Preset5") } :
            	        message("preset $Preset5 is playing")
            	        returned_events << createEvent(name: "file", value: "$Preset5")
                        break
                    case {it.contains("$Preset6") } :
            	        message("preset $Preset6 is playing")
            	        returned_events << createEvent(name: "file", value: "$Preset6")
                        break
                    default:
            	        message('No match to preset')
                        returned_events << createEvent(name: "file", value: "0")
        
       	    }
        }
        if (result.containsKey("playlist")) {
            def json = new groovy.json.JsonBuilder(result.playlist)
            //message("setting playlist to ${json.toString()}")
            //returned_events << createEvent(name: "playlist",value: json.toString())
        }
        if (result.containsKey("error")) {
            def json = new groovy.json.JsonBuilder(result.error)
            message("setting error to ${json.toString()}")
      	    returned_events << createEvent(name: "error",value: json.toString())
        } else {
            message("No error found")
      	    returned_events << createEvent(name: "error",value: null)
        }
    }
  }
  message(returned_events)
  message('Events to UI are above:')
  return returned_events
}
def updated() {
  message('updated unvoked')
  sendEvent(name: "preset1", value: settings.Preset1, displayed: false)
  sendEvent(name: "preset2", value: settings.Preset2, displayed: false)
  sendEvent(name: "preset3", value: settings.Preset3, displayed: false)
  sendEvent(name: "preset4", value: settings.Preset4, displayed: false)
  sendEvent(name: "preset5", value: settings.Preset5, displayed: false)
  sendEvent(name: "preset6", value: settings.Preset6, displayed: false)

}

def refresh(){
    executeCommand("[]")
}

// handle commands
def on() {
    message('on')
    play()
}

def off() {
    message('off')
    stop()
}

def play() {
    message("play")
    executeCommand("[[\"play\"]]")
}

def pause() {
    message( "pause" )
    executeCommand("[[\"pause\"]]")
}

def stop() {
    message("stop")
    executeCommand("[[\"stop\"]]")
}

def previousTrack() {
    message('previousTrack')
    executeCommand("[[\"play\",\"-1\"]]")
}

def nextTrack() {
    message('nextTrack')
    executeCommand("[[\"play\",\"1\"]]")
}

def setLevel(value) {
    message("setLevel to '${value}'")
    executeCommand("[[\"setvol\",${value}]]")
}

def mute() {
    message('mute')
    executeCommand("[[\"disableoutput\",0]]")
}

def unmute() {
    message('unmute')
    executeCommand("[[\"enableoutput\",0]]")
}

def preset1() {
    message('Play preset 1')
    executeCommand("[[\"playid\",\"1\"]]")
}
def preset2() {
    message('Play preset 2')
    executeCommand("[[\"playid\",\"2\"]]")
}
def preset3() {
    message('Play preset 3')
    executeCommand("[[\"playid\",\"3\"]]")
}
def preset4() {
    message('Play preset 4')
    executeCommand("[[\"playid\",\"4\"]]")
}
def preset5() {
    message('Play preset 5')
    executeCommand("[[\"playid\",\"5\"]]")
}
def preset6() {
    message('Play preset 6')
    executeCommand("[[\"playid\",\"6\"]]")
}
def poll(){
    message('Polling')
    refresh()
}

def createDNI(){
    if (!settings.IP && !settings.Port) {
        settings.IP = "192.168.1.12"
    	settings.Port = "80"
    }	
    def gatewayIPHex = convertIPtoHex(settings.IP)
    def gatewayPortHex = convertPortToHex(settings.Port)
    device.deviceNetworkId = "$gatewayIPHex:$gatewayPortHex"
    message (device.deviceNetworkId)
}

private get(path){
    message ("**** Issue GET to: ${settings.IP}:${settings.Port}")
    createDNI()
    def headers = [:] 
    headers.put("HOST", "${settings.IP}:${settings.Port}")
    try {
      def hubAction = new physicalgraph.device.HubAction([
          method: "GET",
          path: path,
          headers: headers
          ], 
          device.deviceNetworkId //, 
          //[callback: "hubActionResponse"]
      )
      message("${hubAction}")
      hubAction
    } catch (e) {
      message(e.message)
    }
}

private executeCommand(command){
    message ("**** POSTing to: ${settings.IP}:${settings.Port} $command")
    createDNI()
    def path = "/player/mpd/postcommand.php"
    def headers = [:] 
    headers.put("HOST", "${settings.IP}:${settings.Port}")
    headers.put("Accept", "application/json, text/javascript, */*; q=0.01")
    def command_in_json = new groovy.json.JsonOutput().toJson(command)
    message("JSON is ${command_in_json}")
    try {
      def hubAction = new physicalgraph.device.HubAction([
          method: "POST",
          path: path,
          headers: headers,
          body: "${command}"
          ],
          device.deviceNetworkId
      )
      message("${hubAction}")
      hubAction
    } catch (e) {
      message(e.message)
    }
}

private String convertIPtoHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
    String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

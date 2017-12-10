import groovy.json.JsonSlurper
import groovy.json.JsonOutput

metadata {
	definition (name: "Rompr", namespace: "ngw", author: "Niv Gal Waizer") {
    	capability "Music Player"
		capability "Switch"
        capability "Refresh"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
        }
		// Row 1
        standardTile("nextTrack", "device.status", width: 1, height: 1, decoration: "flat") {
      		state "next", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn", backgroundColor:"#ffffff"
		}
		standardTile("playpause", "device.status", width: 1, height: 1, decoration: "flat") {
			state "default", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"play", backgroundColor:"#ffffff"
			state "play", label:'', action:"music Player.pause", icon:"st.sonos.pause-btn", nextState:"pause", backgroundColor:"#ffffff"
            state "pause", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"play", backgroundColor:"#ffffff"
		}
        standardTile("previousTrack", "device.status", width: 1, height: 1, decoration: "flat") {
      		state "previous", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn", backgroundColor:"#ffffff"
		}
		// Row 2
        standardTile("status", "device.status", width: 1, height: 1, decoration: "flat", canChangeIcon: true) {
      		state "play", label:'Playing', action:"music Player.pause", icon:"st.Electronics.electronics19", nextState:"pause", backgroundColor:"#ffffff"
      		state "stop", label:'Stopped', action:"music Player.play", icon:"st.Electronics.electronics19", nextState:"play", backgroundColor:"#ffffff"
      		state "pause", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics19", nextState:"play", backgroundColor:"#ffffff"
		}

		standardTile("refresh", "capability.refresh", width: 1, height: 1,  decoration: "flat") {
      		state ("default", label:"Refresh", action:"refresh.refresh", icon:"st.secondary.refresh")
    	}
		// Row 3
		standardTile("mute", "device.mute", inactiveLabel: false, decoration: "flat") {
			state "unmuted", label:"Mute", action:"music Player.mute", icon:"st.custom.sonos.unmuted", backgroundColor:"#ffffff", nextState:"muted"
			state "muted", label:"Unmute", action:"music Player.unmute", icon:"st.custom.sonos.muted", backgroundColor:"#ffffff", nextState:"unmuted"
		}
		controlTile("volume", "device.volume", "slider", width: 3, height: 1) {
			state "volume", label:'${currentValue}', action:"music Player.setLevel", backgroundColor:"#ffffff"
		}
		valueTile("file", "device.status", width: 3, height: 1, decoration: "flat") {
        	state ("name", label:'${currentValue}')
        }
        
        main("status")
        details([
        "previousTrack","playpause","nextTrack",
        "file",
        "status", "refresh",
        "mute", "volume"
        ])
	}
}

preferences {
  input("IP", "text", title: "IP", defaultValue:"192.168.1.12", required: false, displayDuringSetup: true)
  input("Port", "text", title: "Port", defaultValue:"80", required: false, displayDuringSetup: true)
}

def message(msg){
  log.debug("***** '${msg}'")
}

// parse events into attributes
def parse(String description) {
  message("Parsing '${description}'")
  def map = stringToMap(description)
  if (map.headers && map.body) { //got device info response
    if (map.body) {
      def bodyString = new String(map.body.decodeBase64())
      message("body = $bodyString")
      def slurper = new JsonSlurper()
      def result = slurper.parseText(bodyString)
      if (result.containsKey("volume")) {
        message("setting volume to ${result.volume}")
        sendEvent(name: "volume", value: result.volume)
      }
      if (result.containsKey("state")) {
        message("setting state to ${result.state}")
        sendEvent(name: "status", value: result.state)
        sendEvent(name: "playpause", value: result.state)
      }
      if (result.containsKey("file")) {
      	message("setting file to ${result.file}")
        sendEvent(name: "file", value: result.file)
      }
      if (result.containsKey("playlist")) {
        def json = new groovy.json.JsonBuilder(result.playlist)
        log.debug "setting playlist to ${json.toString()}"
      //  sendEvent(name: "playlist",value: json.toString())
      }
    }
  }
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
    //sendEvent(name: "playpause", value: "play")
    //sendEvent(name: "switch", value: "on", isStateChange: true)
    executeCommand("[[\"play\"]]")
}

def pause() {
	message( "pause" )
	//sendEvent(name: "playpause", value: "pause")
	executeCommand("[[\"pause\"]]")
}

def stop() {
	message("stop")
    sendEvent(name: "switch", value: "off", isStateChange: true)
    sendEvent(name: "status", value: "stop", isStateChange: true)
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
  sendEvent(name: "volume", value: value, isStateChange: true)
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


def hubActionResponse(response){
  message("**** Niv hubActionResponse: '${response}'")
  message(response)
}

def poll(){
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

if (!window.WebSocket)
	alert("WebSocket not supported by this browser");

function $() {
	return document.getElementById(arguments[0]);
}
function $F() {
	return document.getElementById(arguments[0]).value;
}

var x, startx;
var y, starty;
var fingers = 0;

function touchStart(event) {
	fingers = event.touches.length;
	x = event.touches[0].pageX;
	y = event.touches[0].pageY;
	startx = x;
	starty = y;
	// room.chat("touch "+x);
}

function touchMove(event) {

	event.preventDefault();

	var nx = event.touches[0].pageX;
	var ny = event.touches[0].pageY;
	var dx = nx - x;
	var dy = ny - y;
	// room.chat("touch "+nx+" "+x+" "+dx);
	if (fingers == 1) {
		room.chat("touch " + dx + " " + dy);
	}
	if (fingers == 2) {
		room.chat("drag " + dx + " " + dy);
	}

	x = nx;
	y = ny;
}

function touchEnd(event) {

	room.chat("tend " + fingers);

	if (startx == x && starty == y) {
		if (fingers == 1) {
			room.chat("leftclick");
		}
		if (fingers == 2) {
			room.chat("rightclick");
		}
	}
}

function getKeyCode(ev) {
	if (window.event)
		return window.event.keyCode;
	return ev.keyCode;
}

var room = {
	join : function() {

		var loc = window.location, new_uri;
		if (loc.protocol === "https:") {
			new_uri = "wss:";
		} else {
			new_uri = "ws:";
		}
		new_uri += "//" + loc.host;
		new_uri += loc.pathname + "/to/ws";
		this._ws = new WebSocket(new_uri);
		this._ws.onopen = this._onopen;
		this._ws.onmessage = this._onmessage;
		this._ws.onclose = this._onclose;
		this._ws.onerror = this._onerror;
	},

	chat : function(text) {
		if (text != null && text.length > 0)
			room._send(text);
	},

	_onopen : function() {
		$('join').className = 'hidden';
		$('joined').className = '';
		$('phrase').focus();
		$('phrase').click();
		room._send('M');
	},

	_onmessage : function(m) {
		if (m.data) {
			var c = m.data.indexOf(':');
			var from = m.data.substring(0, c).replace('<', '&lt;').replace('>',
					'&gt;');
			var text = m.data.substring(c + 1).replace('<', '&lt;').replace(
					'>', '&gt;');

			var chat = $('chat');
			var spanFrom = document.createElement('span');
			spanFrom.className = 'from';
			spanFrom.innerHTML = from + ':&nbsp;';
			var spanText = document.createElement('span');
			spanText.className = 'text';
			spanText.innerHTML = text;
			var lineBreak = document.createElement('br');
			chat.appendChild(spanFrom);
			chat.appendChild(spanText);
			chat.appendChild(lineBreak);
			chat.scrollTop = chat.scrollHeight - chat.clientHeight;
		}
	},

	_onclose : function(m) {
		this._ws = null;
		$('join').className = '';
		$('joined').className = 'hidden';
		// $('username').focus();
		$('chat').innerHTML = '';
	},

	_onerror : function(e) {
		alert(e);
	},

	_send : function(message) {
		if (this._ws)
			this._ws.send(message);
	}
};
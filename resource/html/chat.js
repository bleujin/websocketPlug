

	WebSocketClient = function(){
	}

	$.extend(WebSocketClient.prototype, {
  	  	ws : null,
  	  	requestId : "anonymous",

  		isConnected : function(){
  	  		return this.ws != null ; 
  	  	},

  	  	getServer : function() {
			return this.ws ;
  	  	},

  	  	createServer : function(url) {
  	  		this.ws = new WebSocket(url);
			return this.ws ;
    	}, 

  	  	disconnect : function() {
  			this.ws.close('logout') ;
  			this.ws = null ;
  	  	},

  	  	sendData : function(myData){
  	  		this.ws.send(myData) ;
  	  	}, 
  	  	
  	  	setUserId : function(userId){
  	  		this.requestId = userId ;
  	  	}, 
  	  	
  	  	getUserId : function(){
  	  		return this.requestId ;
  	  	},

  	 	startKeepAlive: function( options ) {
  			// if we have a keep alive running already stop it

  			if( this.hKeepAlive ) {
  				this.stopKeepAlive();
  			}
  			
  			// return if not (yet) connected
  			if( !this.isConnected() ) {
  				// TODO: provide reasonable result here!
  				return;
  			}

  			var interval = 10; // second
  			var echo = true;
  			var immediate = true;
  			if( options ) {
				interval = options.interval ? options.interval : interval ;
				echo = options.echo ? options.echo : echo ;
				immediate = options.immediate ? options.immediate : immediate ;
  			} 
  			
  			if( immediate ) { // send first ping immediately, if requested
  				this.ping({
  					'echo': echo
  				});
  			}
  			// and then initiate interval...
  			var mySelf = this;
  			this.keepAliveHash = setInterval(
  				function() {
  					if( mySelf.isConnected() ) {
  						mySelf.ping({
  							'echo': echo
  						});
  					} else {
  						mySelf.stopKeepAlive();
  					}
  				},
  				interval * 1000
  			);
  		},

  		stopKeepAlive: function() {
  			// TODO: return reasonable results here
  			if( this.keepAliveHash ) {
  				clearInterval( this.keepAliveHash );
  				this.keepAliveHash = null;
  			}
  		},
 

		ping : function( options ) {
			var echo = false;
			if( options ) {
				if( options.echo ) {
					echo = true;
				}
			}
			var result = {};
			if( this.isConnected() ) {
				this.ws.send("{head:{command:'PING',requestid:'" + this.requestId + "'},body:{message:''}}") ;
			} else {
				result.code = -1;
				result.localeKey = "jws.jsc.res.notConnected";
				result.msg = "Not connected.";
			}
			return result;
		},

		toggleKeepAlive : function(checked) {
			(checked == true) ? this.startKeepAlive({interval:5}) : this.stopKeepAlive() ;
		}
  	}) ;
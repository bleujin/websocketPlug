
	MessagePacket = function(_currentPath, _valueObj){
		this.currentPath = _currentPath || '' ; 
		this.valueObj = _valueObj || {} ;
	}
	
	MessagePacket.load = function(packet) {
		return new MessagePacket('', eval("(" + packet + ")"));
	}
	
	$.extend(MessagePacket.prototype, {
		
		put : function(key, value) {
			this.valueObj[this.currentPath + key] = value;
			return this;
		},

		get : function(key) {
			return this.valueObj[key.replace(/\//g, '.')];
		},
		
		inner : function(key) {
			this.currentPath += key + '.' ;
			return this;
		},

		toRoot : function() {
			this.currentPath = '' ;
			return this;
		},
		
		toParent : function() {
			this.currentPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("."));
			return this;
		},
		
		getAllUserCount : function() {
			return this.valueObj.body.usercount;
		},
		
		getCommand : function() {
			return this.valueObj.head.command.toLowerCase();
		},
		
		getSender : function() {
			return this.valueObj.head.sender;
		},
		
		setSender : function(_sender) {
			this.valueObj.head.sender = _sender;
		},
		
		getReceiver : function() {
			return this.valueObj.head.receiver;
		},
		
		setReceiver : function(_receiver) {
			this.valueObj.head.receiver = _receiver;
		},
		
		getMessage : function() {
			return this.valueObj.body.message;
		},
		
		getUsers : function() {
			return this.valueObj.body.users;
		},
		
		getRoom : function() {
			return this.valueObj.body.room;
		},
		
		toJSONString : function() {
			return $.toJSON(this.valueObj);
		}
		
  	}) ;
	
	
	
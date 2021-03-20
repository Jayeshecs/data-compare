//
// Copyright (c) 2004 SmartStream Technologies Ltd. All Rights Reserved.
//
// This software is the confidential and proprietary information
// of SmartStream Technologies Ltd ("Confidential Information").
// You shall not disclose such Confidential Information and shall use
// it only in accordance with the terms of your license agreement.
//

function StringBuffer() {
	this.buffer=[];
}

var p = StringBuffer.prototype;

p.append=function(src){
	this.buffer[this.buffer.length]=src;
	return this;
};

p.flush=function(){
	this.buffer.length=0;
};

p.getLength=function(){
	return this.buffer.join('').length;
};

p.toString=function(delim){
	return this.buffer.join(delim||'');
};



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


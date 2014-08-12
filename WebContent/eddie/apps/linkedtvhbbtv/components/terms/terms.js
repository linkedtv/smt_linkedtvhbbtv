var Terms = function(options){
	var self = this;
	
	this.center();
	this.show();
	
	this.closeButton.on('click', function(){
		self.hide();
	});
	
	this.agreeButton.on('click', function(){
		self.hide();
	});
};

Terms.prototype.element = $('#terms');
Terms.prototype.closeButton = $('#terms .close');
Terms.prototype.agreeButton = $('#terms button');
Terms.prototype.putMsg = function(command){
	var argsStart = command.originalMessage.indexOf("(");
	var fn = command.originalMessage.substring(0, argsStart);
		
	if(typeof this[fn] == "function"){
		this[fn](command.content);
	}
};
Terms.prototype.show = function(){
	this.element.addClass('visible');
};
Terms.prototype.hide = function(){
	this.element.removeClass('visible');
};
Terms.prototype.center = function(){
	var contents = this.element.find('.contents');
	contents.css('margin-top', (this.element.height() - contents.height()) / 2);
};

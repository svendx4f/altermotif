// utility methods in order to display a confirmation method to the user and then to execution some method

var Confirm =  {

	/////////////////////// 
	// constructor for AskAndProceed: simple class to ask a confirmation to the end user before doing something 
	//
	// example usage:
	// 		new Confirm.AskAndProceed("#editProjectAssetsContainer", "img.deleteTaskLink", confirmRemoveProjectAssetText, undefined, this.afterUserConfirmsRemoveAssetFromProject).init();
    //
	/////////////////////// 
	AskAndProceed : function (callerObj, clickableJqParentSelector, clickableJqSubSelector, confirmationText, beforeAsking, onConfirmation) {
	
		this.callerObj = callerObj;
		this.clickableJqParentSelector = clickableJqParentSelector;
		this.clickableJqSubSelector = clickableJqSubSelector;
		this.confirmationText = confirmationText;
		this.beforeAskingFunction = beforeAsking;
		this.onConfirmationFunction = onConfirmation;
		
		this.myHtmlDialog; 
		
		////////////////////
		// init
		this.init = function () {
			
			var self = this;
			
			this.myHtmlDialog = $("#confirmationPopup").clone();
			
			// first action: click on the trigger link
			$(self.clickableJqParentSelector).on("click", self.clickableJqSubSelector, function(event) {
				self.myHtmlDialog.find("span").text(self.confirmationText);
				if (self.beforeAskingFunction != undefined) {
					self.beforeAskingFunction(self.callerObj, event);
				}
				self.myHtmlDialog.dialog("open");		
			});
	
			this.initDialogConstruction(this);
		};
		
		
		////////////////////////////
		this.initDialogConstruction = function (self) {
			// OK/Cancel dialog => when click OK, we callback the executeConfirmationFunction function otherwise we simply close
			self.myHtmlDialog.dialog({
				autoOpen : false,
				width: 400,
				"buttons" : [ 
				{
					text : okLabelValue,
					click : function(event){
						self.onConfirmationFunction(self.callerObj, event);
						self.closeDialog();
					}
				},
				{
					text : cancelLabelValue,
					click : function () {
						self.closeDialog();
					}
				}
				]
			});
			
		};
		
		////////////////////////////
		this.closeDialog = function() {
			this.myHtmlDialog.dialog("close");		
		}	
	},
	
	MessageDisplayer : function(message) {
		
		this.myHtmlDialog;
		
		this.init = function() {
			
			var self = this;
			this.myHtmlDialog = $("#messagePopup").clone();
			this.myHtmlDialog.find("span").text(message);
			
			this.myHtmlDialog.dialog({
				autoOpen : false,
				"buttons" : [ 
				{
					text : cancelLabelValue,
					click : function () {
						self.closeDialog();
					}
				}
				]
			});
		};

		
		////////////////////////////
		this.closeDialog = function() {
			this.myHtmlDialog.dialog("close");		
		},
		
		this.showDialog = function() {
			this.myHtmlDialog.dialog("open");		
		},	
		
		this.init();
		
	},
	

}



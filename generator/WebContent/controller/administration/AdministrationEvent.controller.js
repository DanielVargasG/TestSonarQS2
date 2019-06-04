sap.ui.controller("generator.controller.administration.AdministrationEvent", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
	},	
	onPressEventRegister: function() {
		this._oRouter.navTo("/adm/evenlisterctr");
	},
	onPressEventPending : function(){
		this._oRouter.navTo("/adm/evenlisterctrprocess");
	},

})
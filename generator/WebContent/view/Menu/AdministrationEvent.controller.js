sap.ui.controller("generator.view.AdministrationEvent", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
	},	
	onPressEventConf : function() {
		this._oRouter.navTo("/adm/events");
	},
	onPressEventRegister: function() {
		this._oRouter.navTo("/adm/evenlisterctr");
	},
	onPressEventPending : function(){
		this._oRouter.navTo("/adm/evenlisterctrprocess");
	},

})
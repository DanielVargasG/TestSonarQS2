sap.ui.controller("generator.view.Menu.AdministrationAttachment", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
	},
	onPressAttachmentToProcess : function() {
		this._oRouter.navTo("/adm/synch");
	},
	onPressAttachmentHistory: function() {
		this._oRouter.navTo("/adm/synchhisto");
	}
})
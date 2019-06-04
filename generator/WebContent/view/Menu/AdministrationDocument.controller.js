sap.ui.controller("generator.view.Menu.AdministrationDocument", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
	},
	onPressMappingTempFields : function() {
		this._oRouter.navTo("/adm/mappingtempfield");
	},
	onPressMappingFieldPpd : function() {
		this._oRouter.navTo("/adm/mappingfieldppd");
	},
	onPressMappingRolePpd : function() {
		this._oRouter.navTo("/adm/mappingroleppd");
	},
	onPressMappingTempFields : function() {
		this._oRouter.navTo("/adm/mappingtempfield");
	},
	onPressMappingSignatures : function() {
		this._oRouter.navTo("/adm/mappingsignature");
	},
	onPressEventLookup : function(){
		this._oRouter.navTo("adm/Lookup");
	},
	onPressTemplates : function(){
		this._oRouter.navTo("templates");
	}
})
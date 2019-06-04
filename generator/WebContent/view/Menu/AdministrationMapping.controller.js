sap.ui.controller("generator.view.Menu.AdministrationMapping", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
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
	onPressEventCountry : function(){
		this._oRouter.navTo("adm/Country");
	},
	onPressStructure : function(){
		this._oRouter.navTo("/adm/structure");
	},
	onPressLanguage : function(){
		this._oRouter.navTo("adm/Language");
	},
	onPressManagerRole : function() {
		this._oRouter.navTo("managerRole");
	}
})
sap.ui.controller("generator.view.Menu.AdministrationData", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("administration");
	},
	onPressAdmParameters: function() {
		this._oRouter.navTo("/adm/admParameter");
	},
	onPressStructure : function(){
		this._oRouter.navTo("/adm/structure");
	},
	onPressBDRefresh: function(){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		
		var dialog = new sap.m.Dialog({
			title: i18n.getText("global_Confirm"),
			type: 'Message',
			content: new sap.m.Text({ text: i18n.getText("admindata_Update_database") }),
			beginButton: new sap.m.Button({
				text: i18n.getText("global_Submit"),
				press: function () {
					sap.ui.core.BusyIndicator.show(0);
					
					oDataModel.read("/admin/initBD", {
						sync : false,
						success : function(oData, response) {
							var json = JSON.parse(response.body);
							amModel.setSizeLimit(json.length);
							amModel.setData(json);
							sap.ui.core.BusyIndicator.hide();
							sap.m.MessageToast.show(i18n.getText("admindata_Update_bd"));
						},
						error : function(oData, response) {
							// items = oData.results.length;
						}
					});
					dialog.close();
				}
			}),
			endButton: new sap.m.Button({
				text: i18n.getText("global_Cancel"),
				press: function () {
					dialog.close();
				}
			}),
			afterClose: function() {
				dialog.destroy();
			}
			
		});
		dialog.open();
		
	},
	onPressBDInit: function(){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		sap.ui.core.BusyIndicator.show(0);
		
		oDataModel.read("/admin/initData", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				sap.ui.core.BusyIndicator.hide();
				sap.m.MessageToast.show(i18n.getText("admindata_Data_init"));
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		//window._this.getView().setModel(amModel);
	},
	onPressEventAdmExpImpData : function(){
		this._oRouter.navTo("adm/ExpImpData");
	},
	onPressEventAdmControlPanel : function(){
		this._oRouter.navTo("adm/ControlPanel");
	},
	onPressLanguage : function(){
		this._oRouter.navTo("adm/Language");
	},
	onPressEventMenu : function() {
		this._oRouter.navTo("administrationEvent");
	},
	onPressEventConf : function() {
		this._oRouter.navTo("/adm/events");
	},
	onPressEventLogger : function(){
		this._oRouter.navTo("logger");
	}
})
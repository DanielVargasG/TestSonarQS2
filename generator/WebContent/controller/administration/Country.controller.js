sap.ui.controller("generator.controller.administration.Country", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window._this['ctrlCountry'] = this;
		window.refreshviewCountry = function() {

			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/country_all", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlCountry'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlCountry'].getView().byId("idPrincipalTable");
			window._this['ctrlCountry'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);

		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {

		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "adm/Country") {
			return;
		}

		window.refreshviewCountry();
	},
	
	onNavPressed : function() {
		this._oRouter.myNavBack("administrationMapping");
	},
	// Delete country
	handleDeleteCountry : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idCountry = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/countries/delete_country/' + idCountry, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("country_Delete"));
			window.refreshviewCountry();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("country_Delete_error"));
		});
	},
	//-----------Method Create Edit---------------------
	handleCreateEditCountry : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var id = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var title = i18n.getText("global_Update")
		if(id === null){
			title=i18n.getText("global_Insert");
		}
		var code = this.getView().getModel().getProperty("code", oEvent.getSource().getBindingContext());
		var parameters = this.getView().getModel().getProperty("parameters", oEvent.getSource().getBindingContext());
		var active = this.getView().getModel().getProperty("active", oEvent.getSource().getBindingContext());
		var toProcessUser = this.getView().getModel().getProperty("toProcessUser", oEvent.getSource().getBindingContext());
		var processAttach = this.getView().getModel().getProperty("processAttach", oEvent.getSource().getBindingContext());
		var timeZoneId = this.getView().getModel().getProperty("timeZoneId", oEvent.getSource().getBindingContext());
		
		var dialog = new sap.m.Dialog({
			title : title+' '+i18n.getText("country_"),
			contentWidth : '70%',
			type : 'Message',
			content : 
				[ 
					new sap.m.Label({
						text : i18n.getText("country_Code"),
						labelFor : 'submitDialogCodeValue'
					}), 
					
					new sap.m.Input('submitDialogCodeValue',  {
						liveChange : function(oEvent) 
						{
							var sText = oEvent.getParameter('value');
							var parent = oEvent.getSource().getParent();
							parent.getBeginButton().setEnabled(sText.length > 0);
						},
						fieldWidth : '25%',
						value: code
					}),
					
					new sap.m.Label({text : i18n.getText("adminparameter_Parameter"),labelFor : 'submitDialogParametersValue'}), 
					
					new sap.m.Input('submitDialogParametersValue',  {
						liveChange : function(oEvent) 
						{
							var sText = oEvent.getParameter('value');
							var parent = oEvent.getSource().getParent();
							parent.getBeginButton().setEnabled(sText.length > 0);
						},
						fieldWidth : '25%',
						value: parameters
					}),
					
					new sap.m.Label({text : i18n.getText("global_timeZoneId"),labelFor : 'submitDialogTimeZoneIdValue'}),
					
					new sap.m.Input('submitDialogTimeZoneIdValue',  {
						liveChange : function(oEvent) 
						{
							var sText = oEvent.getParameter('value');
							var parent = oEvent.getSource().getParent();
							parent.getBeginButton().setEnabled(sText.length > 0);
						},
						fieldWidth : '25%',
						value: timeZoneId
					}),			
					
					new sap.m.Label({
						text : i18n.getText("country_Is_active"),
						labelFor : 'submitDialogIsActiveValue'
					}), new sap.m.CheckBox('submitDialogIsActiveValue', {
						selected : active
					}),
			
					new sap.m.Label({text : '',labelFor : '',width : '5%'}),

					new sap.m.Label({
						text : i18n.getText("country_To_process_user"),
						labelFor : 'submitDialogProcessValue'
					}), new sap.m.CheckBox('submitDialogProcessValue', {
						selected : toProcessUser
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '5%'}),

					new sap.m.Label({
						text : i18n.getText("country_Process_attach"),
						labelFor : 'submitDialogProcessAttachValue'
					}), new sap.m.CheckBox('submitDialogProcessAttachValue', {
						selected : processAttach
					}),
					

				],
			
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : true,
				press : function() {
					
					var codeStr = sap.ui.getCore().byId('submitDialogCodeValue').getValue();
					var parametersStr = sap.ui.getCore().byId('submitDialogParametersValue').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : id,
						code : codeStr,
						parameters: parametersStr,
						active : sap.ui.getCore().byId('submitDialogIsActiveValue').getSelected(),
						toProcessUser : sap.ui.getCore().byId('submitDialogProcessValue').getSelected(),
						processAttach : sap.ui.getCore().byId('submitDialogProcessAttachValue').getSelected(),
						timeZoneId    : sap.ui.getCore().byId('submitDialogTimeZoneIdValue').getValue(),
						employeeVault : true
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/country/insert_update/', oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("country_Operation_successfull"));
						window.refreshviewCountry();

					}, function() {
						
						sap.m.MessageToast.show(i18n.getText("country_Operation_error"));
						
					});
				}
			}),
			endButton : new sap.m.Button({
				text : i18n.getText("global_Cancel"),
				press : function() {
					dialog.close();
				}
			}),
			afterClose : function() {
				dialog.destroy();
			}
		});

		dialog.open();

	}

})
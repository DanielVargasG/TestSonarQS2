sap.ui.controller("generator.controller.administration.Language", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window._this['ctrLanguage'] = this;
		window.refreshviewLanguage = function() {

			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/language/all", {
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
			window._this['ctrLanguage'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrLanguage'].getView().byId("idPrincipalTable");
			window._this['ctrLanguage'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	
	_handleRouteMatched : function(evt) {

		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "adm/Language") {
			return;
		}

		window.refreshviewLanguage();
	},
	
	onNavPressed : function() {
		this._oRouter.myNavBack("administrationMapping");
	},
	
	//-----------Method Create Edit---------------------
	handleCreateEditLanguage : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var id = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var title = i18n.getText("global_Update");
		if(id === null){
			title= i18n.getText("global_Insert");
		}
		var code = this.getView().getModel().getProperty("code", oEvent.getSource().getBindingContext());
		var desc = this.getView().getModel().getProperty("description", oEvent.getSource().getBindingContext());
		var status = this.getView().getModel().getProperty("status", oEvent.getSource().getBindingContext());
		
		var dialog = new sap.m.Dialog({
			title : title+' '+i18n.getText("language"),
			contentWidth : '50%',
			type : 'Message',
			content : [ 
			new sap.m.Label({
				text : i18n.getText("language_Code"),
				labelFor : 'submitDialogCodeValue'
			}), new sap.m.Input('submitDialogCodeValue',  {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '25%',
				value: code
			}),
			

			new sap.m.Label({
				text : i18n.getText("language_Name"),
				labelFor : 'submitDialogDescription'
			}), new sap.m.Input('submitDialogDescription',  {
				fieldWidth : '25%',
				value: desc
			}),
			
			new sap.m.Label({
				text : i18n.getText("global_Active"),
				labelFor : 'submitDialogStatus'
			}), new sap.m.CheckBox('submitDialogStatus', {
				selected : status
			}),
			],
			beginButton : new sap.m.Button({
				text : title,
				enabled : true,
				press : function() {
					
					var codeStr = sap.ui.getCore().byId('submitDialogCodeValue').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : id,
						code : sap.ui.getCore().byId('submitDialogCodeValue').getValue(),
						description : sap.ui.getCore().byId('submitDialogDescription').getValue(),
						status: sap.ui.getCore().byId('submitDialogStatus').getSelected()
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/language/insert_update/', oparameters, null, function(oData, response) {
						
						window.refreshviewLanguage();

					}, function() {
						
						sap.m.MessageToast.show(i18n.getText("language_Error_operation"));
						
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

	},
	
	handleDeleteLanguage : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idCountry = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/language/delete/' + idCountry, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("language_Delete"));
			window.refreshviewLanguage();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("language_Error_delete"));
		});
	}
	
})
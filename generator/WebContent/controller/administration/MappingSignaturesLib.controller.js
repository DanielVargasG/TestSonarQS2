sap.ui.controller("generator.controller.administration.MappingSignaturesLib", {

	// -------------------------------------------------
	onInit : function() 
	{
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlMappSign'] = this;
		window.refreshview = function() 
		{
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/mappingsignall", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
				},
				error : function(oData, response) {
				}
			});
			
			window._this['ctrlMappSign'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlMappSign'].getView().byId("idPrincipalTable");
			window._this['ctrlMappSign'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/mappingsignature") {
			return;
		}
		window._this['ctrlMappSign'] = this;
		window.refreshview();
	},
	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationDocument");
	},

	// -------------------------------------------------
	// Delete template field
	handleDelete : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/mappingsign/delete/' + idField, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18ngetText("mappingsign_Signature_delete"));
			window.refreshview();

		}, function() {
			sap.m.MessageToast.show(i18ngetText("mappingsign_Signature_delete_error"));
		});
	},

	// -------------------------------------------------
	// Edit Mapping Field
	handleEditInsert : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameSourceField = this.getView().getModel().getProperty("nameSource", oEvent.getSource().getBindingContext());
		var nameDestinationField = this.getView().getModel().getProperty("nameDestination", oEvent.getSource().getBindingContext());
		var nameFullField = this.getView().getModel().getProperty("nameFull", oEvent.getSource().getBindingContext());
		var title=i18n.getText("global_Update");
		if(idField==null){title=i18n.getText("global_Insert");}
		var dialog = new sap.m.Dialog({
			title : title+' '+i18n.getText("adminmapping_Mapping_signatures"),
			type : 'Message',
			content : [ 				
				new sap.m.Label({
				text : i18n.getText("mappingsign_Signature_name"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				fieldWidth : '50%',
				value : nameSourceField,
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), new sap.m.Label({
				text : i18n.getText("mappingsign_Path"),
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				value : nameDestinationField,
				width : '100%',
			}), 
			
			
			new sap.m.Label({
				text : 'FullName',
				labelFor : 'submitDialogSlug2',
				width : '100%'
			}), new sap.m.Input('submitDialogSlug2Value', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				fieldWidth : '50%',
				value : nameFullField,
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}),		
			
						
			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			})			
			],
			beginButton : new sap.m.Button({
				text : title,				
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();					
					var sFull = sap.ui.getCore().byId('submitDialogSlug2Value').getValue();	
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idField,
						nameSource : sName,
						nameDestination : sValue,
						nameFull : sFull						
						};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingsign/insertupdate', oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("mappingsign_Operation_successfull"));
						window.refreshview();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingsign_Operation_successfull"));
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
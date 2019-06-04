sap.ui.controller("generator.controller.administration.Lookup", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window._this['ctrlLookup'] = this;
		window.refreshviewLookup = function() {

			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/lookup_all", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					
					console.log(json);
					
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlLookup'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlLookup'].getView().byId("idPrincipalTable");
			window._this['ctrlLookup'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);

		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {

		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "adm/Lookup") {
			return;
		}

		window.refreshviewLookup();
	},
	
	onNavPressed : function() {
		this._oRouter.myNavBack("administrationDocument");
	},
	// Delete country
	handleDeleteLookup : function(oEvent) {
		var idlookup = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/lookups/delete_lookup/' + idlookup, null, function(oData, response) {
			var json = JSON.parse(response.body);
			
			window.refreshviewLookup();

		}, function() {
			sap.m.MessageToast.show('Error delete Lookup');
		});
	},
	//-----------Method Create Edit---------------------
	handleCreateEditLookup : function(oEvent) {
		var id = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var title = "Update"
		if(id === null){
			title="Insert";
		}
		var codeTable = this.getView().getModel().getProperty("codeTable", oEvent.getSource().getBindingContext());
		var valueIn = this.getView().getModel().getProperty("valueIn", oEvent.getSource().getBindingContext());
		var valueOut = this.getView().getModel().getProperty("valueOut", oEvent.getSource().getBindingContext());
		
		var dialog = new sap.m.Dialog({
			title : title+' Lookup',
			contentWidth : '70%',
			type : 'Message',
			content : [ 
			new sap.m.Label({
				text : 'codeTable: ',
				labelFor : 'submitDialogCodeValue'
			}), new sap.m.Input('submitDialogCodeValue',  {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '25%',
				value: codeTable
			}),
			
			

			new sap.m.Label({
				text : 'Value In: ',
				labelFor : 'submitDialogValueInValue'
			}), new sap.m.Input('submitDialogValueInValue',  {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '25%',
				value: valueIn
			}),
			
			

			new sap.m.Label({
				text : 'Value Out: ',
				labelFor : 'submitDialogValueOutValue'
			}), new sap.m.Input('submitDialogValueOutValue',  {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '25%',
				value: valueOut
			}),
			],
			
			beginButton : new sap.m.Button({
				text : 'Submit',
				enabled : true,
				press : function() {
					
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : id,
						codeTable : sap.ui.getCore().byId('submitDialogCodeValue').getValue(),
						valueIn : sap.ui.getCore().byId('submitDialogValueInValue').getValue(),
						valueOut : sap.ui.getCore().byId('submitDialogValueOutValue').getValue()
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/lookup/insert_update/', oparameters, null, function(oData, response) {
						console.log("ok");
						window.refreshviewLookup();

					}, function() {
						
						sap.m.MessageToast.show('Error '+title+' lookup');
						
					});
				}
			}),
			endButton : new sap.m.Button({
				text : 'Cancel',
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
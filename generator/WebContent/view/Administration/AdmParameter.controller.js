sap.ui.controller("generator.view.Administration.AdmParameter", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this["ctrlParam"] = this;
		window.refreshviewParam = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/admParameter", {
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
			window._this["ctrlParam"].getView().setModel(amModel);
			window.objectTTT = window._this["ctrlParam"].getView().byId("idPrincipalTable");
			window._this["ctrlParam"].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/admParameter") {
			return;
		}
		window._this["ctrlParam"] = this;
		window.refreshviewParam();
	},
	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationData");
	},

	// -------------------------------------------------
	// Delete adm Parameter
	handleDeleteParameter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/admParameter/delete/' + idField, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("adminparameter_Delete"));
			window.refreshviewParam();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("adminparameter_Delete_error"));
		});
	},
// -------------------------------------------------
	handleEditInsertAdmParameter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var title = i18n.getText("global_Update")
			if(idField === null){
				title=i18n.getText("global_Insert");
			}
		var namParam = this.getView().getModel().getProperty("nameCode", oEvent.getSource().getBindingContext());		
		var valueParam = this.getView().getModel().getProperty("value", oEvent.getSource().getBindingContext());

		var dialog = new sap.m.Dialog({
			title : title +' ' +i18n.getText("adminparameter_title"),
			type : 'Message',
			content : [ new sap.m.Label({
				text : i18n.getText("adminparameter_name"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),

			new sap.m.Input('submitDialogNameValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				fieldWidth : '50%',
				value : namParam,
			}),


			new sap.m.Label({
				text : i18n.getText("adminparameter_Value"),
				labelFor : 'submitDialogValue'
			}),

			new sap.m.Input('submitDialogValueValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				value : valueParam,
				width : '100%',
			}),

			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}),

			],
			beginButton : new sap.m.Button({
				text : title,
				enabled : true,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogNameValue').getValue();				
					var sValue = sap.ui.getCore().byId('submitDialogValueValue').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idField,
						nameCode : sName,						
						value : sValue
					};

					console.log(oparameters);
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/admParameter/insert_upload/', oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("adminparameter_Operation_successfull"));
						window.refreshviewParam();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("adminparameter__Operation_error"));
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
	
	//-----------------------------------------------------
	onAdminInfo : function(oEvent) {
		console.log("Info");
		
		var amModelInfo = new sap.ui.model.json.JSONModel();
		var oDataModelInfo = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelInfo.read("/admin/infoadminparameters", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelInfo.setSizeLimit(json.length);
				amModelInfo.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		sap.ui.getCore().setModel(amModelInfo, "infoModel");
		
		// create popover
		if (!this._oPopover) {
			this._oPopover = sap.ui.xmlfragment("popoverNavConParamter", "generator.view.fragments.DialogInfoAdmin", this);
			this._oPopover.setModel(amModelInfo,"infoModel");
			this.getView().addDependent(this._oPopover);
		}

		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
		
		
	},
	
	//--------------------------------------------------------
	handleIconTabBarSelect : function(oEvent) 
	{	
		var idTable = this.getView().getModel().getProperty("idPrincipalTable", oEvent.getSource().getBindingContext());
		
		var oBinding = window._this["ctrlParam"].getView().byId("idPrincipalTable").getBinding("items"), sKey = oEvent.getParameter("key"), oFilter;
		
		console.log(sKey);
		console.log(oBinding);
		if (sKey === "Admin") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "Admin");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "AttachmentJob") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "AttachmentJob");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "EventListener") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "EventListener");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "MassiveLoad") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "MassiveLoad");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "Signature") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "Signature");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "Structure") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "Structure");
			oBinding.filter([ oFilter ]);			
		} else if (sKey === "Log") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "Log");
			oBinding.filter([ oFilter ]);			
		}else if (sKey === "Parameters Not Configured") {
			oFilter = new sap.ui.model.Filter("group", "EQ", "Parameters Not Configured");
			oBinding.filter([ oFilter ]);	
		} else {
			oBinding.filter([]);
		}
		if (sKey != "Admin" && sKey != "AttachmentJob" && sKey !="EventListener" 
				&& sKey !="MassiveLoad" && sKey !="Signature" && sKey !="Structure" && sKey !="Log" && sKey !="All" )
		{
			var str= ["Admin","AttachmentJob","EventListener","MassiveLoad","Signature","Structure","Log"];
			var aFilter = [];
			aFilter.push(new sap.ui.model.Filter("group", "NE", "Admin"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "AttachmentJob"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "EventListener"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "MassiveLoad"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "Signature"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "Log"));
			aFilter.push(new sap.ui.model.Filter("group", "NE", "Structure"));
			oBinding.filter(new sap.ui.model.Filter( aFilter, true));
		}
	},
	
})
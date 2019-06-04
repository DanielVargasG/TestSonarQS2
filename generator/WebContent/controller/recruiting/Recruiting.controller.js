jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("generator.controller.recruiting.Recruiting", {

	onInit : function() {

		// sap.ui.core.BusyIndicator.show(0);
		window._this['ctrlRecruting'] = this;
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window.temprouter = this._oRouter;

		var amModel = new sap.ui.model.json.JSONModel();

		var amModel2 = new sap.ui.model.json.JSONModel();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.read("/templatesRec", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				//amModel2.setSizeLimit(json.length);
				amModel2.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		this.getView().setModel(amModel2, "tplModel");

	},

	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},
	onSearch : function(oEvt) {

		if(this.odataR)this.odataR.destroy();
		// add filter for search
		var aFilters = [];
		var sQuery = oEvt.getSource().getValue();

		if (sQuery.length > 2) {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/recruiting/search/" + sQuery, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					//amModel.setSizeLimit(json.length);
					amModel.setData(json);
					sap.ui.core.BusyIndicator.hide();
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});

			this.getView().setModel(amModel, "SearchModel");
			this.odataR = oDataModel;
		}
	},
	handleRecListItemSelect : function(oEvent) {

		var mod = oEvent.getSource().getBindingContext("SearchModel");
		var res = mod.sPath.split("/");
		window.tempUser = mod.oModel.oData.d.results[res[3]].applicationId;

		// create popover
		if (!this._oPopover) {
			this._oPopover = sap.ui.xmlfragment("popoverNavCon2", "generator.view.fragments.DialogSingleCustomTabRec", this);
			this.getView().addDependent(this._oPopover);
		}

		// delay because addDependent will do a async rerendering and
		// the popover will immediately close without it
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},
	onNavToProduct : function(oEvent) {
		var oItem, oCtx, sDayId;
		oItem = oEvent.getSource();
		oCtx = oItem.getBindingContext("tplModel");
		// var oCtx = oEvent.getSource().getBindingContext();
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon2", "navCon");
		var oDetailPage = sap.ui.core.Fragment.byId("popoverNavCon2", "detail");
		oNavCon.to(oDetailPage);

		oDetailPage.bindElement("tplModel>" + oCtx.getPath());

	},
	onValidatedPressed : function(oEvent) {
		// alert("oui");

		sap.ui.core.BusyIndicator.show();

		var mod = oEvent.getSource().getBindingContext("tplModel");
		var res = mod.sPath.split("/");

		// window.tempUser = mod.oModel.oData[res[1]].userNav.userId;

		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.create('/createRecDocument/' + mod.oModel.oData[res[1]].idTemplate + "/" + window.tempUser, null, null,

		function(oData, response) {
			var json = JSON.parse(response.body);
			// oTable.getModel().oData.push(json);
			var msg = window._this['ctrlRecruting'].getView().getModel("i18n").getResourceBundle().getText("recluting_Created_document");
			sap.m.MessageToast.show(msg);
			window.tempUser = null;
			sap.ui.core.BusyIndicator.hide();
			window.temprouter.navTo("docs", {
				id : json.id
			});

		}, function() {
			sap.ui.core.BusyIndicator.hide();
			var msg = window._this['ctrlRecruting'].getView().getModel("i18n").getResourceBundle().getText("recluting_Error_creating_document");
			sap.m.MessageToast.show(msg);
		});

		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon2", "navCon");
		oNavCon.back();

		this._oPopover.close();

	},
	handleShowFilter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("logger_filters"),	
			type : 'Message',
			content : [ 
			
			
			
			new sap.m.Label({
				text : "FirstName",
				labelFor : 'submitDialogFN',
				width : '50%'
			}), 

			new sap.m.Input('submitDialogFN', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();	
				},
				fieldWidth : '50%',
			}),		
			
			new sap.m.Label({
				text : "lastName",
				labelFor : 'submitDialogLN',
				width : '50%'
			}), 

			new sap.m.Input('submitDialogLN', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();	
				},
				fieldWidth : '50%',
			}),		

			new sap.m.Label({
				text : "Job Application Name",
				labelFor : 'submitDialogTT',
				width : '50%'
			}), 

			new sap.m.Input('submitDialogTT', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();	
				},
				fieldWidth : '50%',
			}),		
			
			],

			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),				
				press : function() 
				{
					var sFN = sap.ui.getCore().byId('submitDialogFN').getValue();
					var sLN = sap.ui.getCore().byId('submitDialogLN').getValue();
					var sTT = sap.ui.getCore().byId('submitDialogTT').getValue();
					
					dialog.close();

					var oparameters = {
						firstName : sFN,
						lastName : sLN,
						title : sTT
					};

					var amModel = new sap.ui.model.json.JSONModel();

					var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel.create("/recruiting/searchadv/",oparameters,null, function(oData, response) {						

							var json = JSON.parse(response.body);
							console.log(json);
							amModel.setData(json);
							sap.ui.core.BusyIndicator.hide();
							
							
						}
					);
					
					window._this['ctrlRecruting'].getView().setModel(amModel, "SearchModel");
					window._this['ctrlRecruting'].odataR = oDataModel;
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
})
sap.ui.controller("generator.view.Administration.Events", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window._this['ctrlEvents'] = this;
		window.refreshviewEvents = function() {

			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/events", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);					
					amModel.setSizeLimit(json.listEvents.length);
					amModel.setData(json.listEvents);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlEvents'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlEvents'].getView().byId("idPrincipalTable");
			window._this['ctrlEvents'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);

		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/events") {
			return;
		}

		window.refreshviewEvents();
	},
	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationData");
	},

	// -------------------------------------------------
	// Delete event listener
	handleDeleteEvent : function(oEvent) {
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/eventListener/delete_event/' + idEvent, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("event_Delete"));
			window.refreshviewEvents();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("event_Delete_error"));
		});
	},

	// -------------------------------------------------
	// Edit Event Listener
	handleEditEvent : function(oEvent) {
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameEvent = this.getView().getModel().getProperty("eventId", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var isUpdate = this.getView().getModel().getProperty("isUpdate", oEvent.getSource().getBindingContext());
		var isHire = this.getView().getModel().getProperty("isHire", oEvent.getSource().getBindingContext());
		var isTerminate = this.getView().getModel().getProperty("isTerminate", oEvent.getSource().getBindingContext());
		var isEnabled = this.getView().getModel().getProperty("isEnabled", oEvent.getSource().getBindingContext());
		var isDateinstant = this.getView().getModel().getProperty("isDateinstant", oEvent.getSource().getBindingContext());
		var isIndiContrToManager = this.getView().getModel().getProperty("isIndiContrToManager", oEvent.getSource().getBindingContext());
		var isManagerToIndiContr = this.getView().getModel().getProperty("isManagerToIndiContr", oEvent.getSource().getBindingContext());
		var isProcessAgain = this.getView().getModel().getProperty("isProcessAgain", oEvent.getSource().getBindingContext());		
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_Edit_event_listener"),
			type : 'Message',
			contentWidth : '70%',
			content : [ new sap.m.Label({
				text : i18n.getText("event_Name"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '75%',
				value : nameEvent,
			}),
			
			new sap.m.Panel(
			{
				headerText: i18n.getText("team_Actions")	,
				expandable:true,
				expanded:true,
				content : 
				[
					new sap.m.Label({
						text : i18n.getText("event_Is_update"),
						labelFor : 'submitDialogIsUpdateValue',width : '25%'
					}), 
					
					new sap.m.CheckBox('submitDialogIsUpdateValue', {
						selected : isUpdate, width : '25%'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_Is_hire"),
						labelFor : 'isHire', width : '25%'
					}), new sap.m.CheckBox('submitDialogIsHireValue', {
						selected : isHire,width : '25%'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_Is_terminate"),
						labelFor : 'isTerminate',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsTerminateValue', {
						selected : isTerminate,width : '25%'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
								
					new sap.m.Label({
						text : i18n.getText("event_Effective_date_instaneous"),
						labelFor : 'isDateinstant',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsDateinstant', {
						selected : isDateinstant,width : '25%'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_IndiContriBecomesManager"),
						labelFor : 'isIndiContrToManager',width : '25%'
					}),
					
					new sap.m.CheckBox('submitDialogIsIndiContrToManager', {
						selected : isIndiContrToManager,width : '25%'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_ManagerBecomesIndiContri"),
						labelFor : 'isManagerToIndiContr',width : '25%'
					}),
					
					new sap.m.CheckBox('submitDialogIsManagerToIndiContr', {
						selected : isManagerToIndiContr,width : '25%'
					}),

					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({text : i18n.getText("event_Enabled"),labelFor : 'isEnabled',width : '25%'}),			
					new sap.m.CheckBox('submitDialogIsEnabledValue', {selected : isEnabled,width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({text : i18n.getText("event_process_again"),labelFor : 'isProcessAgain',width : '25%'}), 
					new sap.m.CheckBox('submitDialogIsProcessAgain', {selected : isProcessAgain,width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '5%'}),
					]					
			}),
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : true,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sUpdate = sap.ui.getCore().byId('submitDialogIsUpdateValue').getSelected();
					var sIsHire = sap.ui.getCore().byId('submitDialogIsHireValue').getSelected();
					var sIsTerminate = sap.ui.getCore().byId('submitDialogIsTerminateValue').getSelected();
					var sIsEnabled = sap.ui.getCore().byId('submitDialogIsEnabledValue').getSelected();
					var sIsDateinstant = sap.ui.getCore().byId('submitDialogIsDateinstant').getSelected();
					var sIsManagerToIndiContr = sap.ui.getCore().byId('submitDialogIsManagerToIndiContr').getSelected();
					var sIsIndiContrToManager = sap.ui.getCore().byId('submitDialogIsIndiContrToManager').getSelected();
					var sIsProcessAgain = sap.ui.getCore().byId('submitDialogIsProcessAgain').getSelected();					
					
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idEvent,
						eventId : sName,
						isUpdate : sUpdate,
						isHire : sIsHire,
						isTerminate : sIsTerminate,
						isEnabled : sIsEnabled,
						isDateinstant : sIsDateinstant,
						isIndiContrToManager : sIsIndiContrToManager,
						isManagerToIndiContr : sIsManagerToIndiContr,
						isProcessAgain : sIsProcessAgain
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/eventListener/upload_event/' + idEvent, oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("event_Edit_successfull"));
						window.refreshviewEvents();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("event_Edit_error"));
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
	// -------------------------------------------------
	// Add Event listener
	handleAddEvent : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_Insert"),
			contentWidth : '75%',
			type : 'Message',
			content : [ new sap.m.Label({
				text : i18n.getText("event_Name"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
			}),
			
			new sap.m.Panel(
			{
				headerText: i18n.getText("team_Actions")	,
				expandable:true,
				expanded:true,
				content : 
				[
					new sap.m.Label({
						text : i18n.getText("event_Is_update"),
						labelFor : 'isUpdate',
						width : '25%'
					}), new sap.m.CheckBox('submitDialogIsUpdateValue', {width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
		
					new sap.m.Label({
						text : i18n.getText("event_Is_hire"),
						labelFor : 'isHire',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsHireValue', {width : '100%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
		
					new sap.m.Label({
						text : i18n.getText("event_Is_terminate"),
						labelFor : 'isTerminate',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsTerminateValue', {width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_Effective_date_instaneous"),
						labelFor : 'isDateinstant',width : '25%'
					}),
					new sap.m.CheckBox('submitDialogIsDateinstant', {width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_IndiContriBecomesManager"),
						labelFor : 'isIndiContrToManager',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsIndiContrToManager', {width : '25%'}),
		
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({
						text : i18n.getText("event_ManagerBecomesIndiContri"),
						labelFor : 'isManagerToIndiContr',width : '25%'
					}), new sap.m.CheckBox('submitDialogIsManagerToIndiContr', {width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
		
					new sap.m.Label({text : i18n.getText("event_Enabled"),labelFor : 'isEnabled',width : '25%'}), 
					new sap.m.CheckBox('submitDialogIsEnabledValue', {selected : true,width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					
					new sap.m.Label({text : i18n.getText("event_process_again"),labelFor : 'isProcessAgain',width : '25%'}), 
					new sap.m.CheckBox('submitDialogIsProcessAgain', {selected : false,width : '25%'}),
					
					new sap.m.Label({text : '',labelFor : '',width : '5%'}),
					]					
			}),
			
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Insert"),
				enabled : false,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sUpdate = sap.ui.getCore().byId('submitDialogIsUpdateValue').getSelected();
					var sIsHire = sap.ui.getCore().byId('submitDialogIsHireValue').getSelected();
					var sIsTerminate = sap.ui.getCore().byId('submitDialogIsTerminateValue').getSelected();
					var sIsEnabled = sap.ui.getCore().byId('submitDialogIsEnabledValue').getSelected();
					var sIsDateinstant = sap.ui.getCore().byId('submitDialogIsDateinstant').getSelected();
					var sIsIndiContrToManager = sap.ui.getCore().byId('submitDialogIsIndiContrToManager').getSelected();
					var sIsManagerToIndiContr = sap.ui.getCore().byId('submitDialogIsManagerToIndiContr').getSelected();
					var sIsProcessAgain = sap.ui.getCore().byId('submitDialogIsProcessAgain').getSelected();					
					

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						eventId : sName,
						isUpdate : sUpdate,
						isHire : sIsHire,
						isTerminate : sIsTerminate,
						isEnabled : sIsEnabled,
						isDateinstant : sIsDateinstant,
						isIndiContrToManager: sIsIndiContrToManager,
						isManagerToIndiContr: sIsManagerToIndiContr,
						isProcessAgain: sIsProcessAgain
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/eventListener/insert_event/', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						sap.m.MessageToast.show(i18n.getText("event_Insert_successfull"));
						window.refreshviewEvents();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("event_Insert_error"));
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
// -------------------------------------------------
})
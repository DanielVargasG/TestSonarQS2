sap.ui.controller("generator.view.Administration.MappingTempField", {

	// -------------------------------------------------
	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlTempField'] = this;
		window.refreshviewTempField = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/templatefieldlib", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
				},
				error : function(oData, response) {
				}
			});

			window._this['ctrlTempField'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlTempField'].getView().byId("idPrincipalTable");
			window._this['ctrlTempField'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/mappingtempfield") {
			return;
		}
		window._this['ctrlTempField'] = this;
		window.refreshviewTempField();
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
		oDataModel2.remove('/templatefieldlib/delete_field/' + idField, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("mappingtemp_Fields_delete"));
			window.refreshviewTempField();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("mappingtemp_Fields_error_delete"));
		});
	},

	// -------------------------------------------------
	// Edit Mapping Field
	handleEdit : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameDestinationField = this.getView().getModel().getProperty("nameDestination", oEvent.getSource().getBindingContext());
		var nameDestinationExtField = this.getView().getModel().getProperty("nameDestinationExt", oEvent.getSource().getBindingContext());
		var nameSourceField = this.getView().getModel().getProperty("nameSource", oEvent.getSource().getBindingContext());
		var isTableField = this.getView().getModel().getProperty("isTableValue", oEvent.getSource().getBindingContext());
		var isConstantField = this.getView().getModel().getProperty("isConstants", oEvent.getSource().getBindingContext());
		var actionsField = this.getView().getModel().getProperty("actions", oEvent.getSource().getBindingContext());

		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingtemp_Edit_mapping_template_field"),
			type : 'Message',
			content : [
				
			new sap.m.Label({
				text : i18n.getText("mappingtemp_Name_field"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), 
			
			new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
				},
				fieldWidth : '50%',
				value : nameSourceField,
			}), 
			
			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), 
			
			new sap.m.Label({
				text : i18n.getText("mappingtemp_Path"),
				labelFor : 'submitDialogPath'
			}), 
			
			new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
				},
				value : nameDestinationField,
				width : '100%',
			}),

			new sap.m.Label({
				text : i18n.getText("global_is_table"),
				labelFor : 'isTableValue'
			}), 
			
			new sap.m.CheckBox('submitDialogisTableValue', {
				selected : isTableField,
				select : function(){
					var sIsFilter = sap.ui.getCore().byId('submitDialogisTableValue').getSelected();
					
					if(sIsFilter){
						sap.ui.getCore().byId('submitDialogPathSignValue2').setVisible(true);
						sap.ui.getCore().byId('labelTable').setVisible(true);
					}
					else{
						sap.ui.getCore().byId('submitDialogPathSignValue2').setVisible(false);
						sap.ui.getCore().byId('labelTable').setVisible(false);
					}
				}
			}),

			new sap.m.Label({
				text : i18n.getText("document_Is_constants_value"),
				labelFor : 'isConstants'
			}), 
			
			new sap.m.CheckBox('submitDialogisConstant', {
				selected : isConstantField,
			}),
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}), 
			
			new sap.m.Label('labelTable',{
				text : "Table",
				labelFor : 'submitDialogPathSignValue2',
				visible: isTableField,
			}), 
			
			new sap.m.Input('submitDialogPathSignValue2', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
				},
				value : nameDestinationExtField,
				width : '100%',
				visible: isTableField,
			}),

			new sap.m.Panel(
			{
				headerText:i18n.getText("team_Actions"),
				expandable:true,
				expanded:false,
				content : 
				[
					new sap.m.Label({
						text : i18n.getText("mappingfield_actions"),
						labelFor : 'submitDialogActionsValue'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					new sap.m.Label({text : i18n.getText("actions_act2"),}),					
					new sap.m.Input('submitDialogActionsValue', {width : '100%',value:actionsField}),
				]
			}),
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sValue2 = sap.ui.getCore().byId('submitDialogPathSignValue2').getValue();
					var sisTableValue = sap.ui.getCore().byId('submitDialogisTableValue').getSelected();
					var sisConstantValue = sap.ui.getCore().byId('submitDialogisConstant').getSelected();
					var sisActionsValue = sap.ui.getCore().byId('submitDialogActionsValue').getValue();
					
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idField,
						nameSource : sName,
						nameDestination : sValue,
						nameDestinationExt :  sValue2,
						isTableValue : sisTableValue,
						isConstants : sisConstantValue,
						actions : sisActionsValue
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templatefieldlib/upload_field/' + idField, oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("mappingtemp_Edit_updated"));
						window.refreshviewTempField();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingtemp_Edit_updated_error"));
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
	// Add Mapping Field
	handleAdd : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingtemp_Insert_mapping_template_field"),
			type : 'Message',
			content : [new sap.m.Label({
				text : i18n.getText("mappingtemp_Name_field"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), new sap.m.Label({
				text : i18n.getText("mappingtemp_Path"),
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
			}),

			new sap.m.Label({text : i18n.getText("global_is_table"),labelFor : 'isTable'}),
			new sap.m.CheckBox('submitDialogisTableValue',{
				select : function(){
				var sIsFilter = sap.ui.getCore().byId('submitDialogisTableValue').getSelected();
				
				if(sIsFilter){
					sap.ui.getCore().byId('submitDialogPathSignValue2').setVisible(true);
					sap.ui.getCore().byId('labelTable').setVisible(true);
				}
				else{
					sap.ui.getCore().byId('submitDialogPathSignValue2').setVisible(false);
					sap.ui.getCore().byId('labelTable').setVisible(false);
				}
			}}),

			new sap.m.Label({
				text : i18n.getText("document_Is_constants_value"),
				labelFor : 'isConstants'
			}), new sap.m.CheckBox('submitDialogisConstant', {}),
			,
			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			})
			, 
			
			new sap.m.Label("labelTable",{text : "Table",labelFor : 'submitDialogPath2',visible: false}), 
			
			new sap.m.Input('submitDialogPathSignValue2', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
				},
				width : '100%',visible: false
			}),
			
			
			new sap.m.Panel(
			{
				headerText:i18n.getText("team_Actions"),
				expandable:true,
				expanded:false,
				content : 
				[
					new sap.m.Label({
						text : i18n.getText("mappingfield_actions"),
						labelFor : 'submitDialogActionsValue'
					}),
					
					new sap.m.Label({text : '',labelFor : '',width : '100%'}),
					new sap.m.Label({text : i18n.getText("actions_act2"),}),					
					new sap.m.Input('submitDialogActionsValue', {width : '100%'}),
				]
			}),
			
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Insert"),
				enabled : false,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sValue2 = sap.ui.getCore().byId('submitDialogPathSignValue2').getValue();
					var sisTable = sap.ui.getCore().byId('submitDialogisTableValue').getSelected();
					var sisConstants = sap.ui.getCore().byId('submitDialogisConstant').getSelected();
					var sActions = sap.ui.getCore().byId('submitDialogActionsValue').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						nameSource : sName,
						nameDestination : sValue,
						nameDestinationExt : sValue2,
						isTableValue : sisTable,
						isConstants : sisConstants,
						actions : sActions,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templatefieldlib/insert_field/', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show(i18n.getText("mappingtemp_Insert_field_template"));
						window.refreshviewTempField();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingtemp_Insert_field_template_error"));
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
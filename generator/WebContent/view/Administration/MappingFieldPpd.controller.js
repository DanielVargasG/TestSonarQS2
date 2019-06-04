sap.ui.controller("generator.view.Administration.MappingFieldPpd", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlFieldPpd'] = this;
		window.refreshviewFieldPpd = function() 
		{
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/mappingFieldPpd", {
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
			
			window._this['ctrlFieldPpd'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlFieldPpd'].getView().byId("idPrincipalTable");
			window._this['ctrlFieldPpd'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
			
			//load documents types
			var amModelDocType = new sap.ui.model.json.JSONModel();
			var oDataModelDocType = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelDocType.read("/admin/docsType", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelDocType.setSizeLimit(json.length);
					amModelDocType.setData(json);
				},
				error : function(oData, response) {
				}
			});		
			
			sap.ui.getCore().setModel(amModelDocType, "modelPathDocType");
			
			//load token mapping
			var amModelTemplateFieldLib = new sap.ui.model.json.JSONModel();
			var oDataModelTemplateField = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelTemplateField.read("/templatefieldlib", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelTemplateFieldLib.setSizeLimit(json.length);
					amModelTemplateFieldLib.setData(json);
				},
				error : function(oData, response) {
				}
			});		
			
			sap.ui.getCore().setModel(amModelTemplateFieldLib, "modelTemplateFieldLib");			
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/mappingfieldppd") {
			return;
		}
		window._this['ctrlFieldPpd'] = this;
		window.refreshviewFieldPpd();
	},
	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationDocument");
	},

	// -------------------------------------------------
	// Delete field mapping
	handleDeleteMappingField : function(oEvent) {
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/mappingFieldPpd/delete_field/' + idField, null, function(oData, response) {
			var json = JSON.parse(response.body);
			var msg = window._this['ctrlFieldPpd'] .getView().getModel("i18n").getResourceBundle().getText("mappingfield_Mapping_field_delete");
			sap.m.MessageToast.show(msg);
			window.refreshviewFieldPpd();

		}, function() {
			var msg = window._this['ctrlFieldPpd'] .getView().getModel("i18n").getResourceBundle().getText("mappingfield_Field_mapping_temporarily_referenced");	
			sap.m.MessageToast.show(msg,{duration: 5000, width: "25em"});
		});
	},

	// -------------------------------------------------
	// Edit Mapping Field
	handleEditMappingField : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectTemplate = new sap.ui.core.Item({
			key : "{modelPathDocType>id}",
			text : "{modelPathDocType>label}"
		});
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameDestinationField = this.getView().getModel().getProperty("nameDestination", oEvent.getSource().getBindingContext());
		var nameSourceField = this.getView().getModel().getProperty("nameSource", oEvent.getSource().getBindingContext());
		var actionsField = this.getView().getModel().getProperty("actions", oEvent.getSource().getBindingContext());
		var isFilterField = this.getView().getModel().getProperty("isFilter", oEvent.getSource().getBindingContext());
		var isAttachedField = this.getView().getModel().getProperty("isAttached", oEvent.getSource().getBindingContext());
		var isConstantsField = this.getView().getModel().getProperty("isConstants", oEvent.getSource().getBindingContext());
		var isObligatoryField = this.getView().getModel().getProperty("isObligatory", oEvent.getSource().getBindingContext());
		var isActiveField = this.getView().getModel().getProperty("isActive", oEvent.getSource().getBindingContext());
		var sSel1 = this.getView().getModel().getProperty("typeModule", oEvent.getSource().getBindingContext());
		var sSelParam = this.getView().getModel().getProperty("parameters", oEvent.getSource().getBindingContext());		
		var isAttach = ((isAttachedField!=null)?isAttachedField:true);
		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingfield_Edit"),
			contentWidth : '70%',
			type : 'Message',
			content : [ 				
				new sap.m.Label({
				text : i18n.getText("mappingfield_Key"),
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
			}), 
			
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_Path_success_factor"),
				labelFor : 'submitDialogPath'
			}), 
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("mappingfield_token_1")}),	
			

			
			new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				value : nameDestinationField,
				width : '100%',
			}), 
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({
				text : i18n.getText("mappingfield_actions"),
				labelFor : 'submitDialogActionsValue'
			}), 
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act1")}),
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act2")}),	
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act3")}),

			
			new sap.m.Input('submitDialogActionsValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();					
				},
				value : actionsField,
				width : '100%',
			}),
			
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_mappingfield_Is_filter_or_update_user")+i18n.getText("mappingfield_Only_applies_module_attachment"),
				labelFor : 'isFilter',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsFilterValue', {
				selected : isFilterField,
				width : '100%'
			}),

			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_attached"),
				labelFor : 'isAttached',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsAttachedValue', {
				selected : isAttachedField,
				width : '100%',
				select : function(){					
					var sIsFilter = sap.ui.getCore().byId('submitDialogIsAttachedValue').getSelected();
					
					if(sIsFilter){
						sap.ui.getCore().byId('submitDialogSelect1').setVisible(true);
						sap.ui.getCore().byId('submitDialogParam').setVisible(true);
						sap.ui.getCore().byId('labelAtt').setVisible(true);
						sap.ui.getCore().byId('labeldocument').setVisible(true);
					}else{
						sap.ui.getCore().byId('submitDialogSelect1').setVisible(false);
						sap.ui.getCore().byId('submitDialogParam').setVisible(false);
						sap.ui.getCore().byId('labelAtt').setVisible(false);
						sap.ui.getCore().byId('labeldocument').setVisible(false);
					}
				},
				
			}),

			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_constants"),
				labelFor : 'isAttached',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsConstantsValue', {
				selected : isConstantsField,
				width : '100%',
			}), new sap.m.Label({
				text : '',
				labelFor : ''
			}),
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_obligatory"),
				labelFor : 'submitDialogIsObligatoryValue',
				width : '80%'
					
			}), new sap.m.CheckBox('submitDialogIsObligatoryValue', {
				selected : isObligatoryField,
				width : '20%'
			}), new sap.m.Label({
				text : '',
				labelFor : ''
			}),
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_active"),
				width : '80%',
				labelFor : 'submitDialogIsActiveValue'
			}), new sap.m.CheckBox('submitDialogIsActiveValue', {
				selected : isActiveField,
				width : '20%',
			}), new sap.m.Label({
				text : '',
				labelFor : ''
			}),			
			
			new sap.m.Label("labelAtt",{
				text : i18n.getText("mappingfield_Module_attachment"),
				labelFor : 'submitDialogSlug',
				width : '100%',
				visible:isAttach					
			}),
			
				new sap.m.Select('submitDialogSelect1', {
				width : '100%',
				selectedKey:sSel1,
				visible:isAttach
			}).addItem(new sap.ui.core.Item({
				key: "",
				text : i18n.getText("global_None")
			})).addItem(new sap.ui.core.Item({
				key: "EMC",
				text : i18n.getText("mappingfield_Employee_central")
			})).addItem(new sap.ui.core.Item({
				key: 'REC',
				text : i18n.getText("recluting_Title")
			})).addItem(new sap.ui.core.Item({
				key: 'ONB',
				text : i18n.getText("OnBoarding")
			})).addItem(new sap.ui.core.Item({
				key: 'ONB-V2',
				text : i18n.getText("mappingfield_OnBoarding_v2")
			})),
			
			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}),	
			
			new sap.m.Label("labeldocument",{
				text : i18n.getText("mappingfield_Type_document"),
				labelFor : 'submitDialogParam',
				width : '100%',
				visible:isAttach	
			}),		
			
			new sap.m.Select('submitDialogParam',{
				width : '100%',
				selectedKey : sSelParam,
				visible : isAttach
				}).bindAggregation("", {
				path : "modelPathDocType>/",
				template : oItemSelectTemplate
			}),
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),				
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sActions = sap.ui.getCore().byId('submitDialogActionsValue').getValue();
					
					var sIsFilter = sap.ui.getCore().byId('submitDialogIsFilterValue').getSelected();
					var sIsAttached = sap.ui.getCore().byId('submitDialogIsAttachedValue').getSelected();
					var sIsConstants = sap.ui.getCore().byId('submitDialogIsConstantsValue').getSelected();
					var sIsObligatory = sap.ui.getCore().byId('submitDialogIsObligatoryValue').getSelected();
					var sIsActive = sap.ui.getCore().byId('submitDialogIsActiveValue').getSelected();
					var sSel1 = sap.ui.getCore().byId('submitDialogSelect1').getSelectedItem().getKey();
					var sSelParam = sap.ui.getCore().byId('submitDialogParam').getSelectedItem().getKey();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idField,
						nameSource : sName,
						nameDestination : sValue,
						actions: sActions,
						isFilter : sIsFilter,
						isAttached : sIsAttached,
						isConstants : sIsConstants,
						isObligatory : sIsObligatory,
						isActive : sIsActive,
						typeModule : sSel1,
						parameters:  sSelParam,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingFieldPpd/upload_field/' + idField, oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_peopledoc_updated"));
						window.refreshviewFieldPpd();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_error_peopledoc_updated "));
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
	// Add Metadata Field
	handleAddMetadata:function(oEvent){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var metadataList = this.getView().getModel().getProperty("metadataList", oEvent.getSource().getBindingContext());
		var nameSource = this.getView().getModel().getProperty("nameSource", oEvent.getSource().getBindingContext());
		
		
		var oItemSelectTokenLib = new sap.ui.core.Item({
			key : "{modelTemplateFieldLib>id}",
			text : "{modelTemplateFieldLib>nameSource}"
		});
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Edit")+" Metadata",
			contentWidth : '70%',
			type : 'Message',
			content : [ 
				
			new sap.m.Label({text : i18n.getText("mappingfield_Key")+": "+nameSource,width : '100%'}),
			new sap.m.Label({text :"",width : '100%'}),
				
			new sap.m.Label({
				text : i18n.getText("adminmapping_Mapping_template_fields_library"),
				labelFor : 'submitDialogSlug',
				width : '100%'}),
						
			new sap.m.MultiComboBox({id : "selectTemplateFieldLib",placeholder : i18n.getText("adminmapping_Mapping_template_fields_library")})
				.bindAggregation("", {path : "modelTemplateFieldLib>/",template : oItemSelectTokenLib})
				.addSelectedKeys(metadataList)
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),				
				press : function() 
				{
					var templateFieldId = sap.ui.getCore().byId('selectTemplateFieldLib').getSelectedKeys();
					var oparameters = {metaData: templateFieldId};
					
					dialog.close();
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingFieldPpd/upload_fieldmeta/' + idField, oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_peopledoc_updated"));
						window.refreshviewFieldPpd();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_error_peopledoc_updated "));
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
	handleAddMappingField : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectTemplate = new sap.ui.core.Item({
			key : "{modelPathDocType>id}",
			text : "{modelPathDocType>label}"
		});
		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingfield_Insert"),
			contentWidth : '70%',
			type : 'Message',
			content : [ new sap.m.Label({
				text : i18n.getText("mappingfield_Key"),
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
				text : i18n.getText("mappingfield_Path_success_factor"),
				labelFor : 'submitDialogPath'
			}), 
			

			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("mappingfield_token_1")}),	
			
			new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
			}),
						
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_actions"),
				labelFor : 'submitDialogActionsValue'
			}), 
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act1")}),
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act2")}),	
			
			new sap.m.Label({text : '',labelFor : '',width : '100%'}),
			new sap.m.Label({text : i18n.getText("actions_act3")}),
			
			
			new sap.m.Input('submitDialogActionsValue',{				
				width : '100%',
			}),
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_mappingfield_Is_filter_or_update_user")+i18n.getText("mappingfield_Only_applies_module_attachment"),
				labelFor : 'isFilter',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsFilterValue', {
				width : '20%'
			}),

			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_attached"),
				labelFor : 'isAttached',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsAttachedValue', {
				width : '20%',
				select : function(){
					console.log("Clic")
					var sIsFilter = sap.ui.getCore().byId('submitDialogIsAttachedValue').getSelected();
					
					if(sIsFilter){
						sap.ui.getCore().byId('submitDialogSelect1').setVisible(true);
						sap.ui.getCore().byId('submitDialogParam').setVisible(true);
						sap.ui.getCore().byId('labelAtt').setVisible(true);
						sap.ui.getCore().byId('labeldocument').setVisible(true);
					}else{
						sap.ui.getCore().byId('submitDialogSelect1').setVisible(false);
						sap.ui.getCore().byId('submitDialogParam').setVisible(false);
						sap.ui.getCore().byId('labelAtt').setVisible(false);
						sap.ui.getCore().byId('labeldocument').setVisible(false);
					}
				},
			}),

			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_constants"),
				labelFor : 'isAttached',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsConstantsValue', {
				width : '20%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_obligatory"),
				labelFor : 'submitDialogIsObligatoryValue',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsObligatoryValue', {
				width : '20%'
			}), 
			
			new sap.m.Label({
				text : i18n.getText("mappingfield_Is_active"),
				labelFor : 'submitDialogIsActiveValue',
				width : '80%'
			}), new sap.m.CheckBox('submitDialogIsActiveValue', {
				width : '20%'
			}),			

			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}),
			
			new sap.m.Label("labelAtt",{
				text : i18n.getText("mappingfield_Module_attachment"),
				labelFor : 'submitDialogSlug',
				visible : false,
				width : '100%'
			}),
			
			new sap.m.Select('submitDialogSelect1',{
				width : '100%',
				visible : false
			}).addItem(new sap.ui.core.Item({
				key: "",
				text : i18n.getText("global_None")
			})).addItem(new sap.ui.core.Item({
				key: "EMC",
				text : i18n.getText("mappingfield_Employee_central")
			})).addItem(new sap.ui.core.Item({
				key: 'REC',
				text : i18n.getText("recluting_Title")
			})).addItem(new sap.ui.core.Item({
				key: 'ONB',
				text : i18n.getText("mappingfield_OnBoarding")
			})).addItem(new sap.ui.core.Item({
				key: 'ONB-V2',
				text : i18n.getText("mappingfield_OnBoarding_v2")
			})),
			
			new sap.m.Label({
					text : '',
					labelFor : '',
					width : '90%'
				}),
			
			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}),	
			
			new sap.m.Label("labeldocument",{
				text : i18n.getText("mappingfield_Type_document"),
				labelFor : 'submitDialogParam',
				width : '100%',
				visible: false
			}),
			
			new sap.m.Select('submitDialogParam',{
				width : '100%',
				visible: false
			}).bindAggregation("", {
				path : "modelPathDocType>/",
				template : oItemSelectTemplate
			}),			
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Create"),
				enabled : false,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sActions = sap.ui.getCore().byId('submitDialogActionsValue').getValue();
					var sIsFilter = sap.ui.getCore().byId('submitDialogIsFilterValue').getSelected();
					var sIsAttached = sap.ui.getCore().byId('submitDialogIsAttachedValue').getSelected();
					var sIsConstants = sap.ui.getCore().byId('submitDialogIsConstantsValue').getSelected();
					var sIsObligatory = sap.ui.getCore().byId('submitDialogIsObligatoryValue').getSelected();
					var sIsActive = sap.ui.getCore().byId('submitDialogIsActiveValue').getSelected();
					var sSel1 = sap.ui.getCore().byId('submitDialogSelect1').getSelectedItem().getKey();
					var sSelParam = sap.ui.getCore().byId('submitDialogParam').getSelectedItem().getKey();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						nameSource : sName,
						nameDestination : sValue,
						actions : sActions,
						isFilter : sIsFilter,
						isAttached : sIsAttached,
						isConstants : sIsConstants,
						isObligatory : sIsObligatory,
						isActive : sIsActive,
						typeModule : sSel1,
						parameters : sSelParam,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingFieldPpd/insert_field/', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
		 				sap.m.MessageToast.show(i18n.getText("mappingfield_Added"));
						window.refreshviewFieldPpd();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingfield_Added_error"));
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
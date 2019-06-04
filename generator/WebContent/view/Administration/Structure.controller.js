sap.ui.controller("generator.view.Administration.Structure", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlStruc'] = this;
		window.refreshviewStructure = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/structure", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					
					

				
					json[json.length] = {
							id : -1,
							structureName : "None"
						};
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});

			window._this['ctrlStruc'].getView().setModel(amModel);

			window.objectTTT = window._this['ctrlStruc'].getView().byId("idPrincipalTable");
			window._this['ctrlStruc'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/structure") {
			return;
		}
		window._this['ctrlStruc'] = this;
		window.refreshviewStructure();
	},

	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationMapping");
	},

	// -------------------------------------------------
	// Delete event listener
	handleDeleteStructure : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idStruc = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/structure/delete/' + idStruc, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("structure_Delete"));
			window.refreshviewStructure();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("structure_Error_delete"));
		});
	},
	
	// Edit Insert Structure
	handleEditInsertStructure : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idStruct = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameStruct = this.getView().getModel().getProperty("structureName", oEvent.getSource().getBindingContext());
		var parentStruct = this.getView().getModel().getProperty("parentStructure", oEvent.getSource().getBindingContext());
		var nameObj = this.getView().getModel().getProperty("structureObject", oEvent.getSource().getBindingContext());
		var title =i18n.getText("global_Update"); 
		if(idStruct == null){title=i18n.getText("global_Insert");}
		var update= false;
		if(title == i18n.getText("global_Update")){update= true;}
		var oItemSelectTemplate = new sap.ui.core.Item({
			key : "{id}",
			text : "{structureName}"
		});

		var dialog = new sap.m.Dialog({
			title : title+' '+i18n.getText("structure_Element"),
			type : 'Message',
			content : [ new sap.m.Label({
				text : i18n.getText("structure_Element_name"),
				labelFor : 'submitDialogNameValue',
				width : '100%'
			}), new sap.m.Input('submitDialogNameValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
				value : nameStruct,
			}),
			
			new sap.m.Label({
				text : "Object",
				labelFor : 'submitDialogNameObject',
				width : '100%'
			}), new sap.m.Input('submitDialogNameObject', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
				value : nameObj,
			}),

			new sap.m.Label({
				text : i18n.getText("structure_Element_parent"),
				labelFor : 'submitDialogParentStructure',

			}), new sap.m.Select('submitDialogParentStructure', {
				width : '100%',
			}).bindAggregation("", {
				path : "/",
				template : oItemSelectTemplate
			}).setSelectedKey((parentStruct == null) ? '' : parentStruct.id), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}),

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : update,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogNameValue').getValue();
					var sObj = sap.ui.getCore().byId('submitDialogNameObject').getValue();
					var sParent = (sap.ui.getCore().byId('submitDialogParentStructure').getSelectedItem() == null ) ? "" : {
						id : sap.ui.getCore().byId('submitDialogParentStructure').getSelectedItem().getKey()
					}

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						structureName : sName,
						parentStructure : sParent,
						structureObject : sObj,
						id : idStruct,
					};
					console.log("id "+oparameters.id)
					if(title==i18n.getText("global_Insert")){oparameters.id=0;}else{}
					if (oparameters.id != oparameters.parentStructure.id) {
						if (oparameters.parentStructure == "" || oparameters.parentStructure.id == -1) {
							delete (oparameters.parentStructure);
						}
						if(title==i18n.getText("global_Insert")){oparameters.id=null;}
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel2.create('/admin/structure/insert_update/', oparameters, null, function(oData, response) {
							sap.m.MessageToast.show(i18n.getText("adminparameter_Operation_successfull"));
							window.refreshviewStructure();

						}, function() {
							sap.m.MessageToast.show(i18n.getText("global_Error")+' '+title+' '+i18n.getText("structure_Element"));
						});
					} else {
						sap.m.MessageToast.show(i18n.getText("structure_Impossible_to")+' '+title+' '+i18n.getText("structure_Structure_itself"));
					}
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
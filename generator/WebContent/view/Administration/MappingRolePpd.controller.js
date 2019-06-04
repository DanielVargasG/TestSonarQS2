sap.ui.controller("generator.view.Administration.MappingRolePpd", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlRolePpd'] = this;
		window.refreshviewRolePpd = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/mappingRolePpd", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setData(json);
					console.log(json);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlRolePpd'].getView().setModel(amModel);
			window.objectTTT = window._this['ctrlRolePpd'].getView().byId("idPrincipalTable");
			window._this['ctrlRolePpd'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/mappingroleppd") {
			return;
		}
		window._this['ctrlRolePpd'] = this;
		window.refreshviewRolePpd();
	},
	// -------------------------------------------------
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationMapping");
	},

	// -------------------------------------------------
	// Delete field mapping
	handleDeleteMappingField : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/admin/mappingRolePpd/delete/' + idField, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("mappingrole_Delete"));
			window.refreshviewRolePpd();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("mappingrole_Delete_error"));
		});
	},

	// -------------------------------------------------
	// Edit Mapping Field
	handleEditMappingField : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idField = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var nameDestinationField = this.getView().getModel().getProperty("namePpd", oEvent.getSource().getBindingContext());
		var nameSourceField = this.getView().getModel().getProperty("nameSf", oEvent.getSource().getBindingContext());
		var nameLinkField = this.getView().getModel().getProperty("roleOrga", oEvent.getSource().getBindingContext());
		var isDynaField = this.getView().getModel().getProperty("roleOrgaDyna", oEvent.getSource().getBindingContext());
		var isSFGroup = this.getView().getModel().getProperty("roleUseSfGroups", oEvent.getSource().getBindingContext());
		var isSFRole = this.getView().getModel().getProperty("roleUseSfRoles", oEvent.getSource().getBindingContext());
		var sSel1 = this.getView().getModel().getProperty("roleType", oEvent.getSource().getBindingContext());
		var sSel2 = this.getView().getModel().getProperty("roleOperator", oEvent.getSource().getBindingContext());
		var statCont = this.getView().getModel().getProperty("roleStaticContent", oEvent.getSource().getBindingContext());

		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingrole_Update_role_peopledoc"),
			type : 'Message',
			content : [

			new sap.m.Label({
				text : i18n.getText("mappingrole_Name_successfactor"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Input('submitDialogSlugValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
				value : nameSourceField,
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Name_peopledoc"),
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : nameDestinationField,
				width : '100%',
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_type"),
				labelFor : 'submitDialogSelect1',
				width : '100%'
			}), new sap.m.Select('submitDialogSelect1', {
				width : '100%',
				selectedKey : sSel1,
			}).addItem(new sap.ui.core.Item({
				key : "organization",
				text : i18n.getText("mappingrole_Organization")
			})).addItem(new sap.ui.core.Item({
				key : "organization_group",
				text : i18n.getText("mappingrole_Organization_group")
			})).addItem(new sap.ui.core.Item({
				key : 'organization_list',
				text : i18n.getText("mappingrole_Organization_list")
			})), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_operator"),
				labelFor : 'submitDialogSelect2',
				width : '100%'
			}), new sap.m.Select('submitDialogSelect2', {
				width : '100%',
				selectedKey : sSel2,
			}).addItem(new sap.ui.core.Item({
				key : "=",
				text : "="
			})).addItem(new sap.ui.core.Item({
				key : "<=",
				text : "<="
			})).addItem(new sap.ui.core.Item({
				key : '<>',
				text : "<>"
			})), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_link"),
				labelFor : 'submitDialogLinkValue'
			}), new sap.m.Input('submitDialogLinkValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
				value : nameLinkField,
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Is_static_link"),
				labelFor : 'submitDialogIsFilterValue'
			}), new sap.m.CheckBox('submitDialogIsFilterValue', {
				selected : isDynaField,
				select : function(oEvent) {
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(true);
				}
			}), new sap.m.Label({
				text : "Use Successfactors Groups",
				labelFor : 'submitDialogIsSfGroupValue'
			}), new sap.m.CheckBox('submitDialogIsSfGroupValue', {
				selected : isSFGroup,
				select : function(oEvent) {
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(true);
					
				}
			}), new sap.m.Label({
				text : "Use Successfactors Roles",
				labelFor : 'submitDialogIsSfRoleValue'
			}), new sap.m.CheckBox('submitDialogIsSfRoleValue', {
				selected : isSFRole,
				select : function(oEvent) {
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(true);
				}
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Custom_static_content"),
				labelFor : 'submitDialogSttcContValue'
			}), new sap.m.Input('submitDialogSttcContValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : statCont,
				width : '100%',
			})

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : false,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sSel1 = sap.ui.getCore().byId('submitDialogSelect1').getSelectedItem().getKey();
					var sSel2 = sap.ui.getCore().byId('submitDialogSelect2').getSelectedItem().getKey();
					var sValue2 = sap.ui.getCore().byId('submitDialogLinkValue').getValue();
					var sIsStatic = sap.ui.getCore().byId('submitDialogIsFilterValue').getSelected();
					var sIsSfGroup = sap.ui.getCore().byId('submitDialogIsSfGroupValue').getSelected();
					var sIsSfRole = sap.ui.getCore().byId('submitDialogIsSfRoleValue').getSelected();
					var sSttVal = sap.ui.getCore().byId('submitDialogSttcContValue').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idField,
						nameSf : sName,
						namePpd : sValue,
						roleType : sSel1,
						roleOperator : sSel2,
						roleOrga : sValue2,
						roleOrgaDyna : sIsStatic,
						roleUseSfGroups : sIsSfGroup,
						roleUseSfRoles : sIsSfRole,
						roleStaticContent : sSttVal
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingRolePpd/update/' + idField, oparameters, null, function(oData, response) {
						sap.m.MessageToast.show(i18n.getText("mappingrole_Update"));
						window.refreshviewRolePpd();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingrole_Update_error"));
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
	// HANDLE GROUPS
	handleGroupMappingField : function(event) {

		var bindingContext = event.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");

		role = bindingContext.oModel.oData[res[1]];
		
		console.log(role);

		// load documents types
		var amModelGroups = new sap.ui.model.json.JSONModel();
		var oDataModelGroups = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelGroups.read("/admin/mappingRolePpd/groups/" + role.id, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelGroups.setData(json);
				console.log(json);
			},
			error : function(oData, response) {
			}   
		});
		window.modelGroups = amModelGroups;

		window._this['ctrlRolePpd'].getView().setModel(amModelGroups, "modelGroups");
		var i18n = window._this['ctrlRolePpd'].getView().getModel("i18n").getResourceBundle();
		if (!this.resizableDialog) {
			this.resizableDialog = new sap.m.Dialog({
				title : i18n.getText("template_Groups"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelGroups).bindItems("/", new sap.m.StandardListItem({
					title : "{groupId}",
					type : "Active",
					icon : "sap-icon://delete",
					press : function(oEvent) {
						
						var bindingContext = oEvent.getSource().getBindingContext();
						var res = bindingContext.sPath.split("/");

						userfolder = bindingContext.oModel.oData[res[1]];
						
						window.userfld = userfolder;
						
						var dialog = new sap.m.Dialog({
							title : i18n.getText("global_Confirm"),
							type : 'Message',
							content : new sap.m.Text({
								text : i18n.getText("template_Want_delete_user")
							}),
							beginButton : new sap.m.Button({
								text : i18n.getText("global_Delete"),
								press : function() {
									

									var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
									oDataModel2.remove('/admin/mappingRolePpd/groups/' + window.userfld.id, null, function(oData, response) {

										var json = JSON.parse(response.body);
										console.log(json);
										amModelGroups.setData(json);
										var oTable = sap.ui.getCore().getElementById("listUser");

										// oTable.setBindingContext("folderUser");

										sap.m.MessageToast.show(i18n.getText("template_User_removed"));
									}, function() {
										sap.m.MessageToast.show(i18n.getText("template_User_removed_error"));
									});

									console.log("delete !!!");
									dialog.close();
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
				}))],
				buttons : [new sap.m.Button({
					text : i18n.getText("template_Add_user"),
					press : function() {

						if (!this.resizableDialog2) {
							this.resizableDialog2 = new sap.m.Dialog({
								title : i18n.getText("template_Add_user"),
								resizable : true,
								content : new sap.m.Input('submitDialogGroup', {

									width : '100%',
									placeholder : i18n.getText("template_Add_description")
								}),
								buttons : [new sap.m.Button({
									text : i18n.getText("global_Save"),
									press : function(oEvent) {
										var sTextGroup = sap.ui.getCore().byId('submitDialogGroup').getValue();

										var oparameters = {
											group : sTextGroup,

										};
										var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
										oDataModel2.create('/admin/mappingRolePpd/groups/' + role.id, oparameters, null, function(oData, response) {
											var json = JSON.parse(response.body);

											var oTable = sap.ui.getCore().getElementById("listUser");
											var oModel = window.modelGroups;
											oTable.getModel().oData.push(json);
											sap.m.MessageToast.show(i18n.getText("template_User_add"));
											oModel.refresh(true);	
										}, function() {
											sap.m.MessageToast.show('Error adding new user');
										});

										this.resizableDialog2.close();
										this.resizableDialog2.destroy(true);
										this.resizableDialog2=null;
									}.bind(this)
								}), new sap.m.Button({
									text : i18n.getText("global_Close"),
									press : function() {
										this.resizableDialog2.close();
										this.resizableDialog2.destroy(true);
										this.resizableDialog2=null;
									}.bind(this)
								})]
							});

							// to get access to the global model
							this.getView().addDependent(this.resizableDialog2);

						}

						this.resizableDialog2.open();

						// this.resizableDialog.close();
					}.bind(this)
				}), new sap.m.Button({
					text : i18n.getText("global_Close"),
					press : function() {
						this.resizableDialog.close();
						this.resizableDialog.destroy(true);
						this.resizableDialog = null;
					}.bind(this)
				})]
			});

			// to get access to the global model
			this.getView().addDependent(this.resizableDialog);
		}

		this.resizableDialog.open();

	},

	onCancel : function(oEvent) {
		this.oDataBeforeOpen = {};
		oEvent.getSource().close();
	},
	// -------------------------------------------------
	// Add Mapping Field
	handleAddMappingField : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("mappingrole_Insert_role_peopledoc"),
			type : 'Message',
			content : [new sap.m.Label({
				text : i18n.getText("mappingrole_Name_successfactor"),
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
				text : i18n.getText("mappingrole_Name_peopledoc"),
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathSignValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_type"),
				labelFor : 'submitDialogSelect1',
				width : '100%'
			}), new sap.m.Select('submitDialogSelect1', {
				width : '100%',
			}).addItem(new sap.ui.core.Item({
				key : "organization",
				text : i18n.getText("mappingrole_Organization")
			})).addItem(new sap.ui.core.Item({
				key : "organization_group",
				text : i18n.getText("mappingrole_Organization_group")
			})).addItem(new sap.ui.core.Item({
				key : 'organization_list',
				text : i18n.getText("mappingrole_Organization_list")
			})), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_operator"),
				labelFor : 'submitDialogSelect2',
				width : '100%'
			}), new sap.m.Select('submitDialogSelect2', {
				width : '100%',
			}).addItem(new sap.ui.core.Item({
				key : "=",
				text : "="
			})).addItem(new sap.ui.core.Item({
				key : "<=",
				text : "<="
			})).addItem(new sap.ui.core.Item({
				key : '<>',
				text : "<>"
			})), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Role_link"),
				labelFor : 'submitDialogLinkValue'
			}), new sap.m.Input('submitDialogLinkValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Is_static_link"),
				labelFor : 'submitDialogIsFilterValue'
			}), new sap.m.CheckBox('submitDialogIsFilterValue', {}), new sap.m.Label({
				text : '',
				labelFor : ''
			}), new sap.m.Label({
				text : "Use Successfactors Groups",
				labelFor : 'submitDialogIsSfGroupValue'
			}), new sap.m.CheckBox('submitDialogIsSfGroupValue', {}), new sap.m.Label({
				text : '',
				labelFor : ''
			}), new sap.m.Label({
				text : "Use Successfactors Roles",
				labelFor : 'submitDialogIsSfRoleValue'
			}), new sap.m.CheckBox('submitDialogIsSfRoleValue', {}), new sap.m.Label({
				text : '',
				labelFor : ''
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), new sap.m.Label({
				text : i18n.getText("mappingrole_Custom_static_content"),
				labelFor : 'submitDialogSttcContValue'
			}), new sap.m.Input('submitDialogSttcContValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
			})

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Insert"),
				enabled : false,
				press : function() {
					var sName = sap.ui.getCore().byId('submitDialogSlugValue').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogPathSignValue').getValue();
					var sSel1 = sap.ui.getCore().byId('submitDialogSelect1').getSelectedItem().getKey();
					var sSel2 = sap.ui.getCore().byId('submitDialogSelect2').getSelectedItem().getKey();
					var sValue2 = sap.ui.getCore().byId('submitDialogLinkValue').getValue();
					var sIsStatic = sap.ui.getCore().byId('submitDialogIsFilterValue').getSelected();
					var sIsSfGroup = sap.ui.getCore().byId('submitDialogIsSfGroupValue').getSelected();
					var sIsSfRole = sap.ui.getCore().byId('submitDialogIsSfRoleValue').getSelected();
					var sSttVal = sap.ui.getCore().byId('submitDialogSttcContValue').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						nameSf : sName,
						namePpd : sValue,
						roleType : sSel1,
						roleOperator : sSel2,
						roleOrga : sValue2,
						roleOrgaDyna : sIsStatic,
						roleUseSfGroups : sIsSfGroup,
						roleUseSfRoles : sIsSfRole,
						roleStaticContent : sSttVal
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/mappingRolePpd/insert/', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show(i18n.getText("mappingrole_Insert_add"));
						window.refreshviewRolePpd();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("mappingrole_Insert_add_error"));
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
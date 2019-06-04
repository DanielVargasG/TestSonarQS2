sap.ui.controller("generator.controller.administration.ManagerRole", {
	
	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		window._this['ctrlMgRole'] = this;
		
		window.managerRole = function()
		{
			var amModelManager = new sap.ui.model.json.JSONModel();
			var oDataModelManager = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelManager.read("/managerrole/getall" ,{
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelManager.setSizeLimit(json.length);
					amModelManager.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelManager, "modelManager");
			window._this['ctrlMgRole'].getView().setModel(amModelManager, "modelManager");
		}
		window.managerRole();
	},
	
	onNavPressed : function() {
		this._oRouter.myNavBack("administrationMapping");
		window.managerRole();
	},
	
	onPressAdd : function(){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("managerRole_Add"),
			contentWidth : '50%',
			type : 'Message',
			content : [ 
				new sap.m.Label({
					text : i18n.getText("managerRole_Source"),
					labelFor : 'submitDialogSlug',
					width : '100%'
			}),
					new sap.m.Input('submitDialogSource', {
					width : '100%'
			}),
				new sap.m.Label({
				text : i18n.getText("managerRole_Description"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
				new sap.m.Input('submitDialogDescription', {
				width : '100%'
			}),
				new sap.m.Label({
				text : i18n.getText("managerRole_Icon"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
				new sap.m.Input('submitDialogIcon', {
				width : '100%'
			}),
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Create"),
				enabled : true,
				press : function() {
					var obj = {
							description : sap.ui.getCore().byId('submitDialogDescription').getValue(),
							namesource : sap.ui.getCore().byId('submitDialogSource').getValue(),
							icon : sap.ui.getCore().byId('submitDialogIcon').getValue(),
					};
					
					var amModelManager = new sap.ui.model.json.JSONModel();
					var oDataModelManager = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModelManager.create("/managerrole/insert",obj ,{
						sync : false,
						success : function(oData, response) {
							console.log("insert");
							sap.m.MessageToast.show(i18n.getText("global_Create"));
							window.managerRole();
							dialog.close();
						},
						error : function(oData, response) {
						}
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
	
	onPressDelete : function(oEvent){
		var idField = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("id", oEvent.getSource().getParent().getBindingContext("modelManager"));
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var amModelManager = new sap.ui.model.json.JSONModel();
		var oDataModelManager = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelManager.remove("/managerrole/"+idField ,{
			sync : false,
			success : function(oData, response) {
				console.log("insert");
				sap.m.MessageToast.show(i18n.getText("global_Delete"));
				window.managerRole();
			},
			error : function(oData, response) {
			}
		});
	},
	
	onPressUpdate : function(oEvent){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var id = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("id", oEvent.getSource().getParent().getBindingContext("modelManager"));
		var namesource = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("namesource", oEvent.getSource().getParent().getBindingContext("modelManager"));
		var description = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("description", oEvent.getSource().getParent().getBindingContext("modelManager"));
		var icon = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("icon", oEvent.getSource().getParent().getBindingContext("modelManager"));
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Update"),
			contentWidth : '50%',
			type : 'Message',
			content : [ 
				new sap.m.Label({
					text : i18n.getText("managerRole_Source"),
					labelFor : 'submitDialogSlug',
					width : '100%'
			}),
					new sap.m.Input('submitDialogSource', {
					width : '100%',
					value : namesource
			}),
				new sap.m.Label({
				text : i18n.getText("managerRole_Description"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
				new sap.m.Input('submitDialogDescription', {
				width : '100%',
				value : description
			}),
				new sap.m.Label({
				text : i18n.getText("managerRole_Icon"),
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
				new sap.m.Input('submitDialogIcon', {
				width : '100%',
				value : icon
			}),
			
			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : true,
				press : function() {
					var obj = {
							id : id,
							description : sap.ui.getCore().byId('submitDialogDescription').getValue(),
							namesource : sap.ui.getCore().byId('submitDialogSource').getValue(),
							icon : sap.ui.getCore().byId('submitDialogIcon').getValue(),
					};
					
					var amModelManager = new sap.ui.model.json.JSONModel();
					var oDataModelManager = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModelManager.create("/managerrole/update",obj ,{
						sync : false,
						success : function(oData, response) {
							
							sap.m.MessageToast.show(i18n.getText("global_Update"));
							window.managerRole();
							dialog.close();
						},
						error : function(oData, response) {
						}
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
	
	onPressUser : function(event) {

		var id = window._this['ctrlMgRole'].getView().getModel("modelManager").getProperty("id", event.getSource().getParent().getBindingContext("modelManager"));

		var amModelUsers = new sap.ui.model.json.JSONModel();
		var oDataModelUsers = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelUsers.read("/managerroleuser/getall/" + id, {
			sync : false,
			success : function(oData, response) {
				if(response.body != null && response.body != ""){
					var json = JSON.parse(response.body);
					amModelUsers.setData(json);
				}
				
			},
			error : function(oData, response) {
			}
		});
		window.userModel = amModelUsers;

		window._this['ctrlMgRole'].getView().setModel(amModelUsers);
		var i18n = window._this['ctrlMgRole'].getView().getModel("i18n").getResourceBundle();
		if (!this.resizableDialog) {
			this.resizableDialog = new sap.m.Dialog({
				title : i18n.getText("template_Groups"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelUsers).bindItems("/", new sap.m.StandardListItem({
					title : "{groupId}",
					type : "Active",
					icon : "sap-icon://delete",
					press : function(oEvent) {
						
						var bindingContext = oEvent.getSource().getBindingContext();
						var res = bindingContext.sPath.split("/");

						user = bindingContext.oModel.oData[res[1]];
						
						window.user = user;
						console.log(window.user );
						
						var dialog = new sap.m.Dialog({
							title : i18n.getText("global_Confirm"),
							type : 'Message',
							content : new sap.m.Text({
								text : i18n.getText("template_Want_delete_group")
							}),
							beginButton : new sap.m.Button({
								text : i18n.getText("global_Delete"),
								press : function() {
									

									var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
									oDataModel2.remove('/managergroup/delete/' + window.user.id, null, function(oData, response) {

										var oDataModelUsers = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
										oDataModelUsers.read("/managerroleuser/getall/" + window.user.managerRoleId, {
											sync : false,
											success : function(oData, response) {
												if(response.body != null && response.body != ""){
													var json = JSON.parse(response.body);
													console.log(json);
													amModelUsers.setData(json);
													var oTable = sap.ui.getCore().getElementById("listUser");

													sap.m.MessageToast.show(i18n.getText("template_Group_removed"));
												}
												
											},
											error : function(oData, response) {
											}
										});
										

									}, function() {
										sap.m.MessageToast.show(i18n.getText("global_Error"));
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
					text : i18n.getText("template_Add_group"),
					press : function() {

						if (!this.resizableDialog2) {
							this.resizableDialog2 = new sap.m.Dialog({
								title : i18n.getText("template_Add_group"),
								resizable : true,
								content : new sap.m.Input('submitDialogUser', {

									width : '100%',
									placeholder : i18n.getText("template_Add_description")
								}),
								buttons : [new sap.m.Button({
									text : i18n.getText("global_Save"),
									press : function(oEvent) {
										var sTextUser = sap.ui.getCore().byId('submitDialogUser').getValue();

										var oparameters = {
												groupId : sTextUser,
												managerRoleId : id

										};
										var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
										oDataModel2.create('/managergroup/insert', oparameters, null, function(oData, response) {
											var json = JSON.parse(response.body);

											var oTable = sap.ui.getCore().getElementById("listUser");
											var oModel = window.userModel;
											oTable.getModel().oData.push(json);
											sap.m.MessageToast.show(i18n.getText("managerRole_User_add"));
											oModel.refresh(true);	
										}, function() {
											sap.m.MessageToast.show(i18n.getText("global_Error"));
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

	}
	
});
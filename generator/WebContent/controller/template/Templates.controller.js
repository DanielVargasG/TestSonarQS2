sap.ui.controller("generator.controller.template.Templates", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrltmpls'] = this;

		window.refreshviewTmpls = function() {

			
			var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
			var amModel2 = new sap.ui.model.json.JSONModel();
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.read("/admin/events", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					
					
					json.listEvents[json.listEvents.length] = {
						id : 0,
						eventId : i18n.getText("template_None")
					};
					
					json.listEvents[json.listEvents.length] = {
							id : -1,
							eventId : i18n.getText("template_N_a")
						};
					
					amModel2.setData(json.listEvents);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			
			
			sap.ui.getCore().setModel(amModel2, "EventModel");

			// load documents types
			var amModelDocType = new sap.ui.model.json.JSONModel();
			var oDataModelDocType = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelDocType.read("/admin/docsType", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelDocType.setData(json);
				},
				error : function(oData, response) {
				}
			});
			
			sap.ui.getCore().setModel(amModelDocType, "modelPathDocType");
			

			// load mapping fields mails 
			var amModelMails = new sap.ui.model.json.JSONModel();
			var oDataModelMails = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelDocType.read("/admin/mappingFieldPpd_mails", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelMails.setSizeLimit(json.length);
					amModelMails.setData(json);
				},
				error : function(oData, response) {
				}
			});

			sap.ui.getCore().setModel(amModelMails, "modelEmails");

			// load folders
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/foldersStructure", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					amModelFolder.setData(json);
				},
				error : function(oData, response) {
				}
			});
			window._this['ctrltmpls'].amModelFolder = amModelFolder;
			sap.ui.getCore().setModel(amModelFolder, "modelFolder");
			window._this['ctrltmpls'].getView().setModel(amModelFolder, "modelFolder");
			
			// load folders Parents
			var amModelFolderParent = new sap.ui.model.json.JSONModel();
			var oDataModelFolderParent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolderParent.read("/foldersparent", {
				
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					amModelFolderParent.setData(json);
				},
				error : function(oData, response) {
				}
			}
			);
			window._this['ctrltmpls'].amModelFolderParent = amModelFolderParent;
			sap.ui.getCore().setModel(amModelFolderParent, "modelFolderParent");
			window._this['ctrltmpls'].getView().setModel(amModelFolderParent, "modelFolderParent");
			
			// load folders List
			var amModelFolderList = new sap.ui.model.json.JSONModel();
			var oDataModelFolderList = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolderList.read("/foldersList", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					amModelFolderList.setData(json);
				},
				error : function(oData, response) {
				}
			});
			window._this['ctrltmpls'].amModelFolderList = amModelFolderList;
			sap.ui.getCore().setModel(amModelFolderList, "modelFolderList");
			window._this['ctrltmpls'].getView().setModel(amModelFolderList, "modelFolderList");
			
			// load Language
			var amModelLanguage = new sap.ui.model.json.JSONModel();
			var oDataModelLanguage = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelLanguage.read("/admin/language/all", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					window.lang = json;
					console.log(json);
					amModelLanguage.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelLanguage, "modelLanguage");
			window._this['ctrltmpls'].getView().setModel(amModelLanguage, "modelLanguage");
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},

	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "templates") {
			return;
		}

		window._this['ctrltmpls'] = this;

		window.refreshviewTmpls();

	},

	// -------------------------------------------------
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationDocument");
	},

	// -------------------------------------------------
	onSubmitDialog : function() {
		
		window.refreshviewTmpls();		
		var oItemSelectCategory = new sap.ui.core.Item({
			key : "{modelPathDocType>id}",
			text : "{modelPathDocType>label}"
		});
		var oItemSelectTemplate = new sap.ui.core.Item({
			key : "{EventModel>id}",
			text : "{EventModel>eventId}",
			enabled : "{= ${EventModel>id} !== -1}"
		});
		
		var oItemSelectFolder = new sap.ui.core.Item({
			key : "{modelFolderList>id}",
			text : "{modelFolderList>title}"
		});
		
		var oItemSelectMails = new sap.ui.core.Item({
			key : "{modelEmails>nameDestination}",
			text : "{modelEmails>nameSource}"
		});
		
		var oItemSelectLanguage = new sap.ui.core.Item({
			key : "{modelLanguage>code}",
			text : "{modelLanguage>description}",
			enabled : "{modelLanguage>status}"
		});
		
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		var sendManager = false;
		var sendSignTech = true;
		var dialog = new sap.m.Dialog({
			title : i18n.getText("template_Create_template"),
			type : 'Message',
			content : [
				new sap.m.Label({
					text : i18n.getText("template_Folder"),
					labelFor : 'submitDialogSelect12'
				}), new sap.m.Select('submitDialogSelect12', {
					width : '100%',

				}).bindAggregation("", {
					path : "modelFolderList>/",
					template : oItemSelectFolder
				}),
				
				
				,new sap.m.Label({
				text : i18n.getText("template_Title_new_template"),
				labelFor : 'submitDialogTitle'
			}), new sap.m.Input('submitDialogTitle', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				width : '100%',
				placeholder : i18n.getText("template_Add_title_required")
			}), new sap.m.Label({
				text : i18n.getText("template_Description_new_template"),
				labelFor : 'submitDialogDescription'
			}), new sap.m.Input('submitDialogDescription', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				width : '100%',
				placeholder : i18n.getText("template_Add_description")
			}), 
			
			new sap.m.Label({
				text : i18n.getText("template_Document_generation_format"),
				labelFor : 'submitDialogProcessFormat'
			}),
			new sap.m.Select('submitDialogProcessFormat', {
				width : '100%',
				change : function (){
					var select = sap.ui.getCore().byId('submitDialogProcessFormat').getSelectedItem().getKey();
					var esign = sap.ui.getCore().byId('submitDialogSelect2');
					if(select === 'DOCX'){
						esign.setSelectedKey('No');
						esign.setEnabled(false);
					}else{
						esign.setEnabled(true);
					}
				}
			}).addItem(new sap.ui.core.Item({
				key : "PDF",
				text : "PDF"
			})).addItem(new sap.ui.core.Item({
				key : "DOCX",
				text : "DOCX"
			})),
			new sap.m.Label({
				text : i18n.getText("template_Language_new_template"),
				labelFor : 'submitDialogLocale',
				width : '100%'
			}), 
			
			new sap.m.Select('submitDialogSelect', {
				width : '100%',
			}).bindAggregation("", {
				path : "modelLanguage>/",
				template : oItemSelectLanguage,
			}), 
			new sap.m.Label({
				text : 'Does the document support E-Signature',
				labelFor : 'submitDialogLocale'
			}), new sap.m.Select('submitDialogSelect2', {
				width : '100%',

			}).addItem(new sap.ui.core.Item({
				key : "Yes",
				text : i18n.getText("global_Yes")
			})).addItem(new sap.ui.core.Item({
				key : "No",
				text : i18n.getText("global_No")
			})), new sap.m.Label({
				text : i18n.getText("template_Root_access"),
				labelFor : 'submitDialogLocale'
			}),

			new sap.m.Select('submitDialogSelect3', {
				width : '100%',

			}).addItem(new sap.ui.core.Item({
				key : "EMP",
				text : i18n.getText("template_Employee_profile")
			})).addItem(new sap.ui.core.Item({
				key : "EMC",
				text : i18n.getText("template_Employee_central")
			})).addItem(new sap.ui.core.Item({
				key : 'REC',
				text : i18n.getText("template_Recruiting")
			})),

			new sap.m.Label({
				text : i18n.getText("template_Event_launcher"),
				labelFor : 'submitDialogLocale'
			}), new sap.m.Select('submitDialogSelect4', {
				width : '100%',

			}).bindAggregation("", {
				path : "EventModel>/",
				template : oItemSelectTemplate,
			}),

			new sap.m.Label({
				text : i18n.getText("template_Document_type"),
				labelFor : 'submitDialogSelect5'
			}), new sap.m.Select('submitDialogSelect5', {
				width : '100%',

			}).bindAggregation("", {
				path : "modelPathDocType>/",
				template : oItemSelectCategory
			}),
			
			new sap.m.Label({
				text : i18n.getText("template_Send_mail_signature"),
				labelFor : 'submitDialogLocale6'
			}), new sap.m.Select('submitDialogSelect6', {
				width : '100%',

			}).bindAggregation("", {
				path : "modelEmails>/",
				template : oItemSelectMails
			}),	
				
			new sap.m.Label({
				text : i18n.getText("template_Self_generation"),
				labelFor : 'submitDialogSelfDocument',
				width : '100%'
			}), new sap.m.Switch('submitDialogSelfDocument',{
				name:'SWITCH BUTTON',
				customTextOff: i18n.getText("global_No"),
				customTextOn:i18n.getText("global_Yes"),
				state:true,
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("template_Manager_confirmation"),
				labelFor : 'submitDialogManagerConfirm',
				width : '100%'
			}), new sap.m.Select('submitDialogManagerConfirm', {
				width : '100%',
				change : function (){
					
					var select = sap.ui.getCore().byId('submitDialogManagerConfirm').getSelectedItem().getKey();
					
					if(select == "Authorization" || select == "None"){
						
						sendManager = true;
						sendSignTech = false;
						
						sap.ui.getCore().byId('labelSendAuthorized').setVisible(sendManager);
						sap.ui.getCore().byId('submitDialogSendManagerConfirm').setVisible(sendManager);
						
						sap.ui.getCore().byId('labelSendSignTech').setVisible(sendSignTech);
						sap.ui.getCore().byId('submitDialogSendSignTech').setVisible(sendSignTech);
						
					}else{
						sendManager = false;
						sendSignTech = true;
						
						sap.ui.getCore().byId('labelSendAuthorized').setVisible(sendManager);
						sap.ui.getCore().byId('submitDialogSendManagerConfirm').setVisible(sendManager);
						
						sap.ui.getCore().byId('labelSendSignTech').setVisible(sendSignTech);
						sap.ui.getCore().byId('submitDialogSendSignTech').setVisible(sendSignTech);
					}
				}

			}).addItem(new sap.ui.core.Item({
				key : "Signature",
				text : i18n.getText("template_Signature")
			})).addItem(new sap.ui.core.Item({
				key : "Authorization",
				text : i18n.getText("template_Authorization")
			})).addItem(new sap.ui.core.Item({
				key : "None",
				text : i18n.getText("template_None")
			})),
			
			new sap.m.Label("labelSendAuthorized",{text : i18n.getText("template_Send_Authorized_Document"),labelFor : 'submitDialogSendManagerConfirm',width : '100%',visible : sendManager}), 
			new sap.m.Select('submitDialogSendManagerConfirm', {width : '100%',visible : sendManager
				
			}).addItem(new sap.ui.core.Item({
				key : "CompanyVault",
				text : i18n.getText("template_CompanyVault")
			})).addItem(new sap.ui.core.Item({
				key : "EmployeeVault",
				text : i18n.getText("template_EmployeeVault")
			})).addItem(new sap.ui.core.Item({
				key : "Download",
				text : i18n.getText("global_download")
			})),
			
			
			new sap.m.Label("labelSendSignTech",{text : i18n.getText("signature_technology"),labelFor : 'submitDialogSendManagerConfirm',width : '100%',visible : sendSignTech}), 
			new sap.m.Select('submitDialogSendSignTech', {width : '100%',visible : sendSignTech})
			
			.addItem(new sap.ui.core.Item({key : "Docusign",text : "Docusign"}))
			.addItem(new sap.ui.core.Item({key : "Opentrust",text : "Opentrust"})),			

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Create"),
				enabled : false,
				press : function() {
					
					if(window.lang.length == 0){
						sap.m.MessageToast.show(i18n.getText("template_Error_language"));
					}
					else{
						var sText = sap.ui.getCore().byId('submitDialogTitle').getValue();
						var sDesc = sap.ui.getCore().byId('submitDialogDescription').getValue();
						var sSel = sap.ui.getCore().byId('submitDialogSelect').getSelectedItem().getKey();
						var sSel1 = sap.ui.getCore().byId('submitDialogSelect2').getSelectedItem().getKey();
						var sSel2 = sap.ui.getCore().byId('submitDialogSelect3').getSelectedItem().getKey();
						var sSel3 = sap.ui.getCore().byId('submitDialogSelect4').getSelectedItem().getKey();
						var sSel4 = sap.ui.getCore().byId('submitDialogSelect5').getSelectedItem().getKey();
						var sSel6 = sap.ui.getCore().byId('submitDialogSelect6').getSelectedItem().getKey();
						var sSel12 = (sap.ui.getCore().byId('submitDialogSelect12').getSelectedItem().getKey() == "") ? null : sap.ui.getCore().byId('submitDialogSelect12').getSelectedItem().getKey();
						var sGeneration="";
						if(sap.ui.getCore().byId('submitDialogSelfDocument').getState()){
							sGeneration='1';
						}else{
							sGeneration='0';
						}
						var sManagerC = sap.ui.getCore().byId('submitDialogManagerConfirm').getSelectedItem().getKey();
						
						var sFormat = sap.ui.getCore().byId('submitDialogProcessFormat').getSelectedItem().getKey();
						var sSendDocAutho = sap.ui.getCore().byId('submitDialogSendManagerConfirm').getSelectedItem().getKey();
						var sSendSignTech = sap.ui.getCore().byId('submitDialogSendSignTech').getSelectedItem().getKey();
						
						dialog.close();
	
						var oparameters = {
							title : sText,
							description : sDesc,
							locale : sSel,
							esign : sSel1,
							folder : ((sSel12 == null || sSel12 == "") ? null : sSel12),
							module : sSel2,
							documentType : sSel4,
							idEventListener : sSel3,
							format : "DOCX",
							enabled : true,
							active_version : 0,
							emailSign: sSel6,
							selfGeneration: sGeneration,
							formatGenerated: sFormat,
							selfGeneration: sGeneration,
							managerConfirm: sManagerC,
							sendDocAutho: sSendDocAutho,
							typeSign:sSendSignTech,
							
						};
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						
						oDataModel2.create('/templates', oparameters, null, function(oData, response) {
							var json = JSON.parse(response.body);
							var oTable = sap.ui.getCore().getElementById(window._this['ctrltmpls'].createId("idProductsTable"));
							sap.m.MessageToast.show(i18n.getText("template_Create_new_template"));
							console.log(json);
							window._this['ctrltmpls'].amModelFolder.setData(json);
						}, function() {
							sap.m.MessageToast.show(i18n.getText("template_Error_creating_template"));
						});

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

		onFolderDialog : function(event) {
		
		window.refreshviewTmpls();
	
		var oItemSelectFolderParent = new sap.ui.core.Item({
			key : "{modelFolderParent>id}",
			text : "{modelFolderParent>title}"
		});	

		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("template_Create_new_folder"),
			type : 'Message',
			content : 
				[				
					new sap.m.Label({
						text : i18n.getText("template_Folder_parent"),
						labelFor : 'submitDialogSelect13'
					}), new sap.m.Select('submitDialogSelect13', {
						width : '100%',	
					}).bindAggregation("", {
						path : "modelFolderParent>/",
						template : oItemSelectFolderParent 
					}),
					
	                new sap.m.Label({
					text : i18n.getText("template_Title_new_folder"),
					labelFor : 'submitDialogTitle'
			}), new sap.m.Input('submitDialogTitle', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				width : '100%',
				placeholder : i18n.getText("template_Add_title_required")
			}), ,

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Create"),
				enabled : false,
				press : function() 
				{
					var sText = sap.ui.getCore().byId('submitDialogTitle').getValue();
					var sSel13 = sap.ui.getCore().byId('submitDialogSelect13').getSelectedItem().getKey();
					
					dialog.close();
					var oparameters = {
						title : sText,
						parentFolder: {id:sSel13} ,
					};
					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/folders', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						var oTable = sap.ui.getCore().getElementById(window._this['ctrltmpls'].createId("Tree"));
						var oModel = window._this['ctrltmpls'].amModelFolder;
						console.log(json);

						window._this['ctrltmpls'].amModelFolder.setData(json);
						sap.m.MessageToast.show(i18n.getText("template_Folder_created"));
						oModel.refresh(true);
					}, function() {
						sap.m.MessageToast.show(i18n.getText("template_Folder_error_created"));
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

	handleUserPress : function(event) {

		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		folder = bindingContext.oModel.oData[res[1]];

		// load documents types
		var amModelUsers = new sap.ui.model.json.JSONModel();
		var oDataModelUsers = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelUsers.read("/folders/users/" + folder.id, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelUsers.setData(json);
			},
			error : function(oData, response) {
			}
		});
		window.userModel = amModelUsers;

		window._this['ctrltmpls'].getView().setModel(amModelUsers, "folderUser");
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		if (!this.resizableDialog) {
			this.resizableDialog = new sap.m.Dialog({
				title : i18n.getText("template_Users"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelUsers).bindItems("/", new sap.m.StandardListItem({
					title : "{userId}",
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
									oDataModel2.remove('/folders/users/' + window.userfld.id, null, function(oData, response) {

										var json = JSON.parse(response.body);
										console.log(json);
										amModelUsers.setData(json);
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
								content : new sap.m.Input('submitDialogUser', {

									width : '100%',
									placeholder : i18n.getText("template_Add_description")
								}),
								buttons : [new sap.m.Button({
									text : i18n.getText("global_Save"),
									press : function(oEvent) {
										var sTextUser = sap.ui.getCore().byId('submitDialogUser').getValue();

										var oparameters = {
											user : sTextUser,

										};
										var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
										oDataModel2.create('/folders/users/' + folder.id, oparameters, null, function(oData, response) {
											var json = JSON.parse(response.body);

											var oTable = sap.ui.getCore().getElementById("listUser");
											var oModel = window.userModel;
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

	handlePermissionPress : function(event) {
		console.log("view permissions");
		
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		folder = bindingContext.oModel.oData[res[1]];

		// load documents types
		var amModelPerms = new sap.ui.model.json.JSONModel();
		var oDataModelPerms = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelPerms.read("/folders/permissions/" + folder.id, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelPerms.setData(json);
			},
			error : function(oData, response) {
			}
		});
		window.permModel = amModelPerms;

		window._this['ctrltmpls'].getView().setModel(amModelPerms, "folderPerms");

		if (!this.resizableDialog) {
			this.resizableDialog = new sap.m.Dialog({
				title : i18n.getText("template_Groups"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listGroup").setModel(amModelPerms).bindItems("/", new sap.m.StandardListItem({
					title : "{groupId}",
					type : "Active",
					icon : "sap-icon://delete",
					press : function(oEvent) {
						
						var bindingContext = oEvent.getSource().getBindingContext();
						var res = bindingContext.sPath.split("/");

						permfolder = bindingContext.oModel.oData[res[1]];
						
						window.permfld = permfolder;
						
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
									oDataModel2.remove('/folders/permissions/' + window.permfld.id, null, function(oData, response) {

										var json = JSON.parse(response.body);
										console.log(json);
										amModelPerms.setData(json);
										var oTable = sap.ui.getCore().getElementById("listGroup");

										// oTable.setBindingContext("folderUser");

										sap.m.MessageToast.show(i18n.getText("template_Group_removed"));
									}, function() {
										sap.m.MessageToast.show(i18n.getText("template_Error_removed_group"));
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
								content : new sap.m.Input('submitDialogGroup', {

									width : '100%',
									placeholder : i18n.getText("template_Add_description")
								}),
								buttons : [new sap.m.Button({
									text : i18n.getText("global_Save"),
									press : function(oEvent) {
										var sTextGroup = sap.ui.getCore().byId('submitDialogGroup').getValue();

										var oparameters = {
											groupId : sTextGroup,

										};
										var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
										oDataModel2.create('/folders/permissions/' + folder.id, oparameters, null, function(oData, response) {
											var json = JSON.parse(response.body);

											var oTable = sap.ui.getCore().getElementById("listGroup");
											var oModel = window.permModel;
											oTable.getModel().oData.push(json);
											sap.m.MessageToast.show(i18n.getText("template_Group_added"));
											oModel.refresh(true);
										}, function() {
											sap.m.MessageToast.show(i18n.getText("template_Group_error_added"));
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

	handleEditPress : function(event) {
		console.log("Edit folder");

		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		//folder = bindingContext.oModel.oData[res[1]];
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		var idEvent = window._this['ctrltmpls'].getView().getModel("modelFolder").getProperty("title", bindingContext);
		var idFolder = window._this['ctrltmpls'].getView().getModel("modelFolder").getProperty("id", bindingContext);

		var dialog = new sap.m.Dialog({
			title : i18n.getText("template_Update_folder"),
			type : 'Message',
			content : [new sap.m.Label({
				text : i18n.getText("template_Title_folder"),
				labelFor : 'submitDialogTitle'
			}), new sap.m.Input('submitDialogTitle', {
				value : idEvent,
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				width : '100%',
				placeholder : i18n.getText("template_Add_title_required")
			}), ,

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : false,
				press : function() {

					var sText = sap.ui.getCore().byId('submitDialogTitle').getValue();

					dialog.close();

					var oparameters = {
						id : idFolder,
						title : sText,
					};
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/foldersE', oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						var oTable = sap.ui.getCore().getElementById(window._this['ctrltmpls'].createId("Tree"));
						var oModel = window._this['ctrltmpls'].amModelFolder;
						console.log(json);

						oTable.getModel("modelFolder").setData(json);
						sap.m.MessageToast.show(i18n.getText("template_Folder_updated"));
						oModel.refresh(true);
					}, function() {
						sap.m.MessageToast.show(i18n.getText("template_Folder_error_updated"));
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

	handleDeletePress : function(event) {
		console.log("delete");

		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		//folder = bindingContext.oModel.oData[res[1]];
		var idFolder = window._this['ctrltmpls'].getView().getModel("modelFolder").getProperty("id", bindingContext);

		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Confirm"),
			type : 'Message',
			content : new sap.m.Text({
				text : i18n.getText("template_Want_delete_folder")
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Delete"),
				press : function() {
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.remove('/folders/' + idFolder, null, function(oData, response) {

						sap.m.MessageToast.show(i18n.getText("template_Folder_deleted"));

						var json = JSON.parse(response.body);
						console.log(json);
						window._this['ctrltmpls'].amModelFolder.setData(json);

					}, function() {
						sap.m.MessageToast.show(i18n.getText("template_Folder_error_deleted"));
					});
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

	// -------------------------------------------------
	handleColumnListItemSelect : function(event) {
		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		console.log(res);
		var idTmp = window._this['ctrltmpls'].getView().getModel("modelFolder").getProperty("idTemplate", bindingContext);
		
		
		this._oRouter.navTo("templatesdetails", {
			id : idTmp
		});
	},

	// -------------------------------------------------
	onFavPressed : function(oEvent) {
		var i18n = window._this['ctrltmpls'].getView().getModel("i18n").getResourceBundle();
		var bindingContext = oEvent.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var unikID = bindingContext.oModel.oData[res[1]].id;
		if (oEvent.getSource().getProperty("pressed") == true) {
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.create('user/favorite/' + unikID, null, null, function(oData, response) {
				var json = JSON.parse(response.body);
				sap.m.MessageToast.show('Updated');
			}, function() {
				sap.m.MessageToast.show('Error');
			});
		} else {
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.remove('user/favorite/' + unikID, null, function(oData, response) {
				sap.m.MessageToast.show(i18n.getText("template_Updated"));
			}, function() {
				sap.m.MessageToast.show(i18n.getText("global_Errors"));
			});

		}

	},
	
	onTemplateFilter : function(oEvent){
		var oItemSelectFolder = new sap.ui.core.Item({
			key : "{modelFolderList>id}",
			text : "{modelFolderList>title}"
		});
		var oItemSelectEventTemplate = new sap.ui.core.Item({
			key : "{EventModel>id}",
			text : "{EventModel>eventId}"
		});
		
		var dialog = new sap.m.Dialog({
			title : 'Search Template',
			type : 'Message',
			content : [
				
				new sap.m.Label({
					text : 'Folder',
					labelFor : 'submitDialogSelectFolder'
				}), new sap.m.Select('submitDialogSelectFolder', {
					width : '100%',

				}).bindAggregation("", {
					path : "modelFolderList>/",
					template : oItemSelectFolder
				}),
				//------------------------------------------------
				new sap.m.Label({
					text : 'Event Listener',
					labelFor : 'submitDialogSelectEvent'
				}), new sap.m.Select('submitDialogSelectEvent', {
					width : '100%',

				}).bindAggregation("", {
					path : "EventModel>/",
					template : oItemSelectEventTemplate,
				}),
				
				
			],
			beginButton : new sap.m.Button({
				text : 'Search',
				enabled : true,
				press : function() {
					var sEvent = sap.ui.getCore().byId('submitDialogSelectEvent').getSelectedItem().getKey();
					var sFolder = sap.ui.getCore().byId('submitDialogSelectFolder').getSelectedItem().getKey();
					//function ...
					var oparameters = {
							idFolder : sFolder,
							idEvent : sEvent,
						};
					var amModelFolder = new sap.ui.model.json.JSONModel();
					var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModelFolder.create("/filtertemplate", oparameters,{
						sync : true,
						success : function(oData, response) {
							var json = JSON.parse(response.body);
							amModelFolder.setData(json);
							if(json !== null){
								window._this['ctrltmpls'].amModelFolder = amModelFolder;
								sap.ui.getCore().setModel(amModelFolder, "modelFolder");
								window._this['ctrltmpls'].getView().setModel(amModelFolder, "modelFolder");
								sap.m.MessageToast.show(' Made Filter');
							}
						},
						error : function(oData, response) {
							sap.m.MessageToast.show('Error Filter');
						}
					});
					dialog.close();
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
		
	},
	
	onSearchTemplate: function(oEvt){
		var sQuery = oEvt.getSource().getValue();
		// load folders
		if(sQuery.length > 2){
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/searchtemplate/"+sQuery, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelFolder.setData(json);
				},
				error : function(oData, response) {
				}
			});
			window._this['ctrltmpls'].amModelFolder = amModelFolder;
			sap.ui.getCore().setModel(amModelFolder, "modelFolder");
			window._this['ctrltmpls'].getView().setModel(amModelFolder, "modelFolder");
			this.byId("idTableTemplate").setVisible(true);
			this.byId("Tree").setVisible(false);
		}
		if(sQuery.length === 0){
			this.byId("idTableTemplate").setVisible(false);
			this.byId("Tree").setVisible(true);
			window.refreshviewTmpls();
		}
	},
	
	handleColumnListItemDelete : function(oEvent) {
		
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var bindingContext = oEvent.getSource().getBindingContext("modelFolder");
		var idTemplate = window._this['ctrltmpls'].getView().getModel("modelFolder").getProperty("idTemplate", bindingContext);
		console.log("Delete Template "+idTemplate);
		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Confirm"),
			type : 'Message',
			content : new sap.m.Text({
				text : i18n.getText("templatedetails_Want_delete_template"),
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Delete"),
				press : function() {
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.remove('/template/' + idTemplate, null, function(oData, response) {

						sap.m.MessageToast.show(i18n.getText("templatedetails_Deleted_template"));
						window.refreshviewTmpls();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Deleted_error_template"));
					});
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

	}
	
	})
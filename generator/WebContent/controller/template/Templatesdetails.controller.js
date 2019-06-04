jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.controller.template.Templatesdetails", {

	// -------------------------------------------------
	onInit : function(id) {
		var iconTab = this.byId("idIconTabBar");
		var carousel = this.byId("previewCarousel");
		var idObjectStatus = this.byId("idObjectStatus");
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
		
		window._this['ctrltmplD'] = this;
		window.refreshviewTmplD = function() 
		{
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);

			oDataModel.read("/template/" + window.idTemplate, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setData(json);
					window.idTemplatePpDoc = json.id;
					window.lastVersion = json.latest_version;
					window.metadata= json.metadataList;
					window.manager = json.managerConfirm;
					window._this['ctrltmplD'].createMenuFilters(json.module);
					
					if(window.idTemplatePpDoc !== null){
						var oDataModel4 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel4.read("/template/infowarn/" + window.idTemplatePpDoc+"/"+window.lastVersion, {
							sync : false,
							success : function(oData, response) {
								if(response.body == 200 || json.latest_version === 0){
									idObjectStatus.setVisible(false);
								}else{
									idObjectStatus.setVisible(true);									
								}
								
							},
							error : function(oData, response) {
								var msg = this.getView().getModel("i18n").getResourceBundle().getText("templatedetails_Error_info_template");
								sap.m.MessageToast.show('Error Info Template');
							}
						});
					}
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrltmplD'].getView().setModel(amModel);
			
			var amModel2 = new sap.ui.model.json.JSONModel();
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.read("/admin/events", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel2.setData(json.listEvents);
					json.listEvents[json.listEvents.length] = {
						id : 0,
						eventId : "None"
					};
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
					amModelMails.setData(json);
				},
				error : function(oData, response) {
				}
			});

			sap.ui.getCore().setModel(amModelMails, "modelEmails");			
			
			// load folders
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/foldersList", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelFolder.setData(json);
				},
				error : function(oData, response) {
				}
			});
			
			sap.ui.getCore().setModel(amModelFolder, "modelFolder");
			if(amModelFolder[0]==null){
			
				console.log("Null  folder"); 
				
			}
			
			// load Mapping Signature
			var amModelSign = new sap.ui.model.json.JSONModel();
			var oDataModelSign = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelSign.read("/admin/mappingsignall", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					
					amModelSign.setSizeLimit(json.length);
					amModelSign.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelSign, "modelSign");
			iconTab.setSelectedKey("upload");
			carousel.destroyPages();
			
			// load Language
			var amModelLanguage = new sap.ui.model.json.JSONModel();
			var oDataModelLanguage = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelLanguage.read("/admin/language/all", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					
					amModelLanguage.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelLanguage, "modelLanguage");
			window._this['ctrltmplD'].getView().setModel(amModelLanguage, "modelLanguage");
			
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
			window._this['ctrltmplD'].getView().setModel(amModelTemplateFieldLib, "modelTemplateFieldLib");
			var oItemSelectTokenLib = new sap.ui.core.Item({
				key : "{modelTemplateFieldLib>id}",
				text : "{modelTemplateFieldLib>nameSource}"
			});
			window._this['ctrltmplD'].byId('multiMetada').bindAggregation("", {path : "modelTemplateFieldLib>/",template : oItemSelectTokenLib});
			window._this['ctrltmplD'].byId('multiMetada').removeAllSelectedItems();
			window._this['ctrltmplD'].byId('multiMetada').addSelectedKeys(window.metadata);
			window._this['ctrltmplD'].byId('idIconTabBar').setSelectedKey("upload");
			
			
			
			jQuery.sap.delayedCall(850, this, function () {
				if(window.manager == ("Authorization")){
					var i18n = window._this['ctrltmplD'] .getView().getModel("i18n").getResourceBundle();
					window._this['ctrltmplD'].byId("id1").setText(i18n.getText("template_Authorization"));
					window._this['ctrltmplD'].byId("id2").setText(i18n.getText("template_Authorization"));
					window._this['ctrltmplD'].byId("id3").setText(i18n.getText("template_Authorization"));
				}
				else{
					var i18n = window._this['ctrltmplD'] .getView().getModel("i18n").getResourceBundle();
					window._this['ctrltmplD'].byId("id1").setText(i18n.getText("home_Signature"));
					window._this['ctrltmplD'].byId("id2").setText(i18n.getText("home_Signature"));
					window._this['ctrltmplD'].byId("id3").setText(i18n.getText("home_Signature"));
				}
			});
			
			
		}

	},

	//------------------------------------------------------
	recursiveMenu : function(r, b) {

		var forbidden = ['lastModifiedOn','createdDateTime','lastModifiedDateTime','createdBy','lastModifiedBy','createdOn', 'operation'];
		var itemsList = [];
		for (i = 0; i < r.length; i++) {
			// console.log(r[i]);
			if (r[i].relation) {

				var oMenuItem1 = new sap.m.MenuItem({
					items : this.recursiveMenu2(r[i].relation, forbidden),
					text : r[i].label,
					key : "",
				});
				itemsList.push(oMenuItem1);

				// oMenuItem1.setSubmenu(oMenu2);

			} else {
				if(! forbidden.includes(r[i].label) ){
				var oMenuItem1 = new sap.m.MenuItem({
					text : r[i].label,
					key : r[i].url
				});
				itemsList.push(oMenuItem1);
				}

			}
		}

		var oMenu1 = new sap.m.Menu({
			items : itemsList,
			itemSelected : function(oE) {

				sap.ui.getCore().byId('submitDialogPathValueF').setValue(oE.getParameter("item").getKey());
				// console.log(oE);
				sap.m.MessageToast.show(oE.getParameter("item").getKey());
			}
		});

		return oMenu1;
	},

	//-------------------------------------------
	recursiveMenu2 : function(rr, ff) {

		//console.log(rr);

		var nn = [];
		var thisContext = this;

		
			rr.forEach(function(b) {
			if (b.relation) 
			{
				
				var oMenuItem1 = new sap.m.MenuItem({
					items : thisContext.recursiveMenu2(b.relation, ff),
					text : b.label,
					key : "",
				});
				nn.push(oMenuItem1);
					
			} else 
			{
				if(! ff.includes(b.label) ){
				var oMenuItem1 = new sap.m.MenuItem({
					text : b.label,
					key : b.url
				});
				nn.push(oMenuItem1);
				}	
			}
		});

		return nn;
	},

	//-------------------------------------------
	createMenuFilters : function(typeModule) {

		// Create a MenuButton Control

		var type = "PerPerson";

		if (typeModule == "EMC")
			type = "PerPerson";

		var amModel2 = new sap.ui.model.json.JSONModel();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.read("/admin/mapObjects/" + type, {
			sync : true,
			success : function(oData, response) 
			{
				var json = JSON.parse(response.body);
				window._this['ctrltmplD'].menuData = json;
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});

	},
	// -------------------------------------------------
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("templates");
	},

	// -------------------------------------------------
	_handleRouteMatched : function(oEvent) {
		if (oEvent.getParameter("name") !== "templatesdetails") {
			return;
		}

		window._this['ctrltmplD'] = this;
		var oParameters = oEvent.getParameter("name");
		var varPath = oEvent.getParameter("arguments").id;
		window.idTemplate = varPath;
		window.refreshviewTmplD();

	},

	// -------------------------------------------------
	handleSignaturePress : function(oEvent) {
		var idSignature = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var slugField = this.getView().getModel().getProperty("slug", oEvent.getSource().getBindingContext());
		var pathField = this.getView().getModel().getProperty("path", oEvent.getSource().getBindingContext());
		var orderField = this.getView().getModel().getProperty("order", oEvent.getSource().getBindingContext());
		var constantFlag = this.getView().getModel().getProperty("flag", oEvent.getSource().getBindingContext());
		var constantValue = this.getView().getModel().getProperty("value", oEvent.getSource().getBindingContext());
		var constantPath = this.getView().getModel().getProperty("path", oEvent.getSource().getBindingContext());
		var idSignTmp = this.getView().getModel().getProperty("idSignTempLib", oEvent.getSource().getBindingContext());
		var manager = this.getView().getModel().getProperty("idSignTempLib", oEvent.getSource().getBindingContext());
		var iconTab = this.byId("idIconTabBar");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectSign = new sap.ui.core.Item({
			key : "{modelSign>id}",
			text : "{modelSign>nameSource}"
		});
		var json= JSON.parse(sap.ui.getCore().getModel("modelSign").getJSON());
		var idValue=-2;
		var valueSign='';
		
		if(idSignTmp == null){
			if(constantPath == null || constantPath == ""){
				idSignTmp=-2;
				constantFlag=false;
			}
		}
		json.forEach(function(element) {
			if(idSignTmp==element.id){
				  idValue=element.id;
				  valueSign=element.nameSource;
			}
		});
		
		var dialog = new sap.m.Dialog({
			title : (window.manager == 'Authorization')? i18n.getText("templatedetails_Signature").replace("Signature", i18n.getText("template_Authorization")) : i18n.getText("templatedetails_Current_signature")+ " "+ slugField,
			type : 'Message',
			content : [
				
				
			new sap.m.Panel(
			{
				headerText:(window.manager == 'Authorization')? i18n.getText("templatedetails_Current_signature").replace("Signature", i18n.getText("template_Authorization")) : i18n.getText("templatedetails_Current_signature"),
				expandable:true,
				expanded:true,
				content : 
				[	
					new sap.m.Label({
								text : '',
								labelFor : '',
								width : '90%'
							}),
					new sap.m.Label({
								text : i18n.getText("global_Value")+" : "+ constantPath,
								labelFor : 'submitDialogPath',
								width : '100%',
							}),
					new sap.m.Label({
								text : '',
								labelFor : '',
								width : '90%'
							}),
					new sap.m.Label({
								text : i18n.getText("global_Order") +" : "+orderField,
								labelFor : 'valueOrder',
								width : '100%'
							}),
				]					
			}),
			
			new sap.m.Panel(
			{
				headerText:(window.manager == 'Authorization')? i18n.getText("templatedetails_Edit_signature_template").replace("signature", i18n.getText("template_Authorization")) : i18n.getText("templatedetails_Edit_signature_template"),
				expandable:true,
				expanded:false,
				content :
				[						
					new sap.m.Label({
						text : '',
						labelFor : '',
						width : '90%'
					}), new sap.m.Label({
						text :(window.manager == 'Authorization')? i18n.getText("templatedetails_Signature_name").replace("Signature", i18n.getText("template_Authorization")) : i18n.getText("templatedetails_Signature_name"),
						labelFor : 'submitDialogPath'
					}), 
					
					new sap.m.Select('submitDialogSign', 
					{
						width : '100%',
						change : function(oEvent) 
						{
							var sText2 = sap.ui.getCore().byId('submitDialogSign').getSelectedKey();
							if(sText2 == -1)
							{
								sap.ui.getCore().byId('submitDialogSignConstantValue').setVisible(true);
								sap.ui.getCore().byId('labelConstant').setVisible(true);
								sap.ui.getCore().byId('labelEmailFormat').setVisible(true);
							}							
							else
							{
								sap.ui.getCore().byId('submitDialogSignConstantValue').setVisible(false);
								sap.ui.getCore().byId('labelConstant').setVisible(false);
								sap.ui.getCore().byId('labelEmailFormat').setVisible(false);
							}
							
							sap.ui.getCore().byId('submitDialogSignConstantValue').setValue("");
						},
		
					}).bindAggregation("", {
						path : "modelSign>/",
						template : oItemSelectSign
					}).setSelectedKey(idValue),
					new sap.m.Label({
						text : '',
						labelFor : '',
						width : '90%'
					}),
					
					new sap.m.Label("labelConstant",{text :(window.manager == 'Authorization')?i18n.getText("templatedetails_Sign_constant").replace("Signature", i18n.getText("template_Authorization")):i18n.getText("templatedetails_Signature_name"),
						visible: constantFlag,labelFor : 'submitDialogSignConstantValue'}), 
						
					new sap.m.Label("labelEmailFormat",{text:" ["+i18n.getText("document_Signature_value_email")+" ] ",visible: constantFlag}), 
					
					new sap.m.Input('submitDialogSignConstantValue', {
						visible : constantFlag,
						fieldWidth : '90%',
					}).setValue(constantPath),
					
					new sap.m.Label({
						text : '',
						labelFor : '',
						width : '90%'
					}),
					
					new sap.m.Label({
						text : i18n.getText("global_Order"),
						labelFor : 'valueOrder',
						width : '100%'
					}), new sap.m.Input('submitDialogOrderValue', {
						liveChange : function(oEvent) {
							var sText = oEvent.getParameter('value');
							var parent = oEvent.getSource().getParent();
							parent.getBeginButton().setEnabled(sText.length > 0);
						},
						type : "Number",
						fieldWidth : '50%',
						value : orderField
					})
				]
			}
			),

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : true,
				press : function() {
					var sValue = sap.ui.getCore().byId('submitDialogSign').getSelectedItem().getText();
					var sIdSign = sap.ui.getCore().byId('submitDialogSign').getSelectedKey();
					var sIsConst = false
					if(sIdSign==-1 || sIdSign==-2){sIsConst=true;}
					var sOrder = sap.ui.getCore().byId('submitDialogOrderValue').getValue();
					var sConstant = sap.ui.getCore().byId('submitDialogSignConstantValue').getValue();
					if(sIdSign==-1){sValue = sConstant;}
					if(sIdSign==-2){sValue = "";}
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					// var oModel = this.getView().getModel();

					var oparameters = {
						id : idSignature,
						path : sValue,
						flag : sIsConst,
						order : sOrder,
						idSignTempLib : sIdSign,
					};
					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templates/upload_signature/' + idSignature, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show((window.manager == 'Authorization')? i18n.getText("templatedetails_Signature_updated").replace("Signature", i18n.getText("template_Authorization")):i18n.getText("templatedetails_Signature_updated"));

						window.refreshviewTmplD();
						iconTab.setSelectedKey("sign");

					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Signature_error_updated"));
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
	handleDetailsPress : function(oEvent) {

		var idFild = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var slugField = this.getView().getModel().getProperty("slug", oEvent.getSource().getBindingContext());
		var pathField = this.getView().getModel().getProperty("path", oEvent.getSource().getBindingContext());

		var dialog = new sap.m.Dialog({
			title : 'Edit field Template',
			type : 'Message',
			content : [new sap.m.Label({
				text : 'Name Field: ' + slugField,
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), new sap.m.Label({
				text : 'Path SuccessFactor: ',
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},

				width : '100%',
				value : pathField,
			}),

			],
			beginButton : new sap.m.Button({
				text : 'Submit',
				enabled : false,
				press : function() {
					var sValue = sap.ui.getCore().byId('submitDialogPathValue').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();

					var oparameters = {
						id : idFild,
						path : sValue
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templates/upload_field/' + idFild, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show('Field Updated');

						window.refreshviewTmplD();

					}, function() {
						sap.m.MessageToast.show('Error creating update field Template');
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
	},

	// -------------------------------------------------
	onPressUpdate : function(oEvent) {

		var oView = this.getView();
		var oModel = this.getView().getModel();
		var variables = oModel.getProperty("/generateVariables/variables");

		for (i = 0; i < variables.length; i++) {
			var sText = oView.byId("item_" + variables[i].id).getValue();
		}
	},

	// -------------------------------------------------
	onPressDelete : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
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
					oDataModel2.remove('/template/' + window.idTemplate, null, function(oData, response) {

						sap.m.MessageToast.show(i18n.getText("templatedetails_Deleted_template"));
						window.refreshviewTmplD();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Deleted_error_template"));
					});
					window._this['ctrltmplD']._oRouter.myNavBack("templates");
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

	onPressEdit : function(oEvent) {

		var titleField = this.getView().getModel().oData.title;
		var descField = this.getView().getModel().oData.description;
		var idField = this.getView().getModel().oData.id;
		var moduleStruct = this.getView().getModel().oData.module;
		var lgStruct = this.getView().getModel().oData.locale;
		var evtStruct = this.getView().getModel().oData.idEventListener;
		var docStruct = this.getView().getModel().oData.documentType;
		var esignStruct = this.getView().getModel().oData.esign;
		var folderStruct = this.getView().getModel().oData.folder;
		var emailSign = this.getView().getModel().oData.emailSign;
		var state = true;
		var sGeneration = this.getView().getModel().oData.selfGeneration;
		var sformatGenerated = this.getView().getModel().oData.formatGenerated;
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var sManager = this.getView().getModel().oData.managerConfirm;
		var sendDocAutho = this.getView().getModel().oData.sendDocAutho;
		var sendSignTechValue = this.getView().getModel().oData.typeSign;
		
		console.log(sendSignTechValue);
		
		var sendManager = false;
		var sendSignTech = false;
		console.log("/*/*/ " + sendDocAutho+" / "+sendSignTechValue);
		
		var formatValue = true;
		if(sformatGenerated==='DOCX'){
			formatValue = false;
		}
		if(sGeneration === '0'){
			state=false;
		}
		
		if(sManager == "Authorization" || sManager == "None"){
			sendManager = true;
		}
		else{
			sendSignTech = true;
		}
		
		var oItemSelectCategory = new sap.ui.core.Item({
			key : "{modelPathDocType>id}",
			text : "{modelPathDocType>label}"
		});

		var oItemSelectTemplate = new sap.ui.core.Item({
			key : "{EventModel>id}",
			text : "{EventModel>eventId}"
		});
		
		var oItemSelectFolder = new sap.ui.core.Item({
			key : "{modelFolder>id}",
			text : "{modelFolder>title}"
		});
		
		var oItemSelectMail = new sap.ui.core.Item({
			key : "{modelEmails>nameDestination}",
			text : "{modelEmails>nameSource}"
		});		

		var oItemSelectLanguage = new sap.ui.core.Item({
			key : "{modelLanguage>code}",
			text : "{modelLanguage>description}",
			enabled : "{modelLanguage>status}"
		});

		var dialog = new sap.m.Dialog({
			title : i18n.getText("templatedetails_Edit_template"),
			type : 'Message',
			content : [
				new sap.m.Label({
					text : i18n.getText("global_Folder"),
					labelFor : 'submitDialogSelect12'
				}), new sap.m.Select('submitDialogSelect12', {
					width : '100%',

				}).bindAggregation("", {
					path : "modelFolder>/",
					template : oItemSelectFolder
				}).setSelectedKey((folderStruct == null) ? '' : folderStruct),
				
				new sap.m.Label({
				text : i18n.getText("templatedetails_Title_template"),
				labelFor : 'submitDialogTitle',
			}), new sap.m.Input('submitDialogTitle', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : titleField,
				width : '100%',
				placeholder : i18n.getText("template_Add_title_required")
			}), new sap.m.Label({
				text : i18n.getText("templatedetails_Description_template"),
				labelFor : 'submitDialogDescription'
			}), new sap.m.Input('submitDialogDescription', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : descField,
				width : '100%',
				placeholder : i18n.getText("template_Add_description")
			}), 
			
			
			new sap.m.Label({
				text : i18n.getText("template_Document_generation_format")+': ',
				labelFor : 'submitDialogProcessFormat'
			}),	new sap.m.Select('submitDialogProcessFormat', {
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
			})).setSelectedKey(sformatGenerated),
			
			
			new sap.m.Label({
				text : i18n.getText("templatedetails_Language_template"),
				labelFor : 'submitDialogLocale',
				width : '100%'
			}), new sap.m.Select('submitDialogSelect', {
				width : '100%',
			}).bindAggregation("", {
				path : "modelLanguage>/",
				template : oItemSelectLanguage,
			}).setSelectedKey((lgStruct == null) ? '' : lgStruct), 
			
			new sap.m.Label({
				text : i18n.getText("template_Does_document_support_E-Signature"),
				labelFor : 'submitDialogLocale'
			}), new sap.m.Select('submitDialogSelect2', {
				width : '100%',

			}).addItem(new sap.ui.core.Item({
				key : "Yes",
				text : i18n.getText("global_Yes")
			})).addItem(new sap.ui.core.Item({
				key : "No",
				text : i18n.getText("global_No")
			})).setSelectedKey((esignStruct == null) ? '' : esignStruct), new sap.m.Label({
				text : i18n.getText("template_Root_access"),
				labelFor : 'submitDialogLocale'
			}), new sap.m.Select('submitDialogSelect3', {
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
			})).setSelectedKey((moduleStruct == null) ? '' : moduleStruct), new sap.m.Label({
				text : i18n.getText("template_Event_launcher"),
				labelFor : 'submitDialogLocale'
			}), new sap.m.Select('submitDialogSelect4', {
				width : '100%',

			}).bindAggregation("", {
				path : "EventModel>/",
				template : oItemSelectTemplate
			}).setSelectedKey((evtStruct == null) ? '0' : evtStruct),

			new sap.m.Label({
				text : i18n.getText("template_Document_type"),
				labelFor : 'submitDialogSelect5'
			}), new sap.m.Select('submitDialogSelect5', {
				width : '100%',

			}).bindAggregation("", {
				path : "modelPathDocType>/",
				template : oItemSelectCategory
			}).setSelectedKey((docStruct == null) ? '' : docStruct),
			
			new sap.m.Label({
				text : i18n.getText("template_Send_mail_signature"),
				labelFor : 'submitDialogLocale6'
			}), new sap.m.Select('submitDialogSelect6', {
				width : '100%',

			}).bindAggregation("", {
				path : "modelEmails>/",
				template : oItemSelectMail
			}).setSelectedKey((emailSign == null) ? '' : emailSign),			

			new sap.m.Label({
				text : i18n.getText("template_Employee_document_self_generation"),
				labelFor : 'submitDialogSelfDocument',
				width : '100%'
			}), new sap.m.Switch('submitDialogSelfDocument',{
				name:'SWITCH BUTTON',
				customTextOff: i18n.getText("global_No"),
				customTextOn: i18n.getText("global_Yes"),
				state:true,
				width : '100%'
			}).setState(state),
			
			new sap.m.Label({
				text : i18n.getText("template_Manager_confirmation"),
				labelFor : 'submitDialogManagerConfirm',
				width : '100%'
			}),  new sap.m.Select('submitDialogManagerConfirm', {
				width : '100%',
				change : function (){
					console.log("change")
					var select = sap.ui.getCore().byId('submitDialogManagerConfirm').getSelectedItem().getKey();
					console.log("select")
					
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
			}))
			.setSelectedKey((sManager == null) ? '' : sManager),
			
			new sap.m.Label("labelSendAuthorized",{
				text : i18n.getText("template_Send_Authorized_Document"),
				labelFor : 'submitDialogSendManagerConfirm',
				width : '100%',
				visible : sendManager
			}), 
			new sap.m.Select('submitDialogSendManagerConfirm', {
				width : '100%',
				visible : sendManager
				
			}).addItem(new sap.ui.core.Item({
				key : "CompanyVault",
				text : i18n.getText("template_CompanyVault")
			})).addItem(new sap.ui.core.Item({
				key : "EmployeeVault",
				text : i18n.getText("template_EmployeeVault")
			})).addItem(new sap.ui.core.Item({
				key : "Download",
				text : i18n.getText("global_download")
			})).setSelectedKey((sendDocAutho == null) ? '' : sendDocAutho),
			
			
			new sap.m.Label("labelSendSignTech",{text : i18n.getText("signature_technology"),labelFor : 'submitDialogSendManagerConfirm',width : '100%',visible : sendSignTech}), 
			new sap.m.Select('submitDialogSendSignTech', {width : '100%',visible : sendSignTech})
			
			.addItem(new sap.ui.core.Item({key : "Docusign",text : "Docusign"}))
			.addItem(new sap.ui.core.Item({key : "Opentrust",text : "Opentrust"}))
			.setSelectedKey((sendSignTechValue == null) ? 'Docusign' : sendSignTechValue),

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : false,
				press : function() {

					var sText = sap.ui.getCore().byId('submitDialogTitle').getValue();
					var sDesc = sap.ui.getCore().byId('submitDialogDescription').getValue();
					var sSel = sap.ui.getCore().byId('submitDialogSelect').getSelectedItem().getKey();
					var sSel1 = sap.ui.getCore().byId('submitDialogSelect2').getSelectedItem().getKey();
					var sSel2 = sap.ui.getCore().byId('submitDialogSelect3').getSelectedItem().getKey();
					var sSel3 = sap.ui.getCore().byId('submitDialogSelect4').getSelectedItem().getKey();
					var sSel4 = sap.ui.getCore().byId('submitDialogSelect5').getSelectedItem().getKey();
					
					var sSel12 = null;
					if(sap.ui.getCore().byId('submitDialogSelect12').getSelectedItem()!=null){
						sSel12 = sap.ui.getCore().byId('submitDialogSelect12').getSelectedItem().getKey();
					}
			
					var sSel6 = sap.ui.getCore().byId('submitDialogSelect6').getSelectedItem().getKey();

					var idField = window._this['ctrltmplD'].getView().getModel().oData.id;
					var idTField = window._this['ctrltmplD'].getView().getModel().oData.idTemplate;
					var sFormat = sap.ui.getCore().byId('submitDialogProcessFormat').getSelectedItem().getKey();
					var sGeneration="";
					if(sap.ui.getCore().byId('submitDialogSelfDocument').getState()){
						sGeneration='1';
					}else{
						sGeneration='0';
					}
					var sManagerC= sap.ui.getCore().byId('submitDialogManagerConfirm').getSelectedItem().getKey();
					var sSendDocAutho = sap.ui.getCore().byId('submitDialogSendManagerConfirm').getSelectedItem().getKey();
					var sSendSignTech = sap.ui.getCore().byId('submitDialogSendSignTech').getSelectedItem().getKey();
										
					dialog.close();

					var oparameters = {
						id : idField,
						idTemplate : idTField,
						title : sText,
						description : sDesc,
						documentType : sSel4,
						locale : sSel,
						folder : ((sSel12 == null || sSel12 == "") ? -1 : sSel12),
						esign : sSel1,
						module : sSel2,
						idEventListener : sSel3,
						emailSign : sSel6,
						enabled : true,
						validity_start_date : "2017-07-30T15:28:43.220+00:00",
						validity_end_date : "2019-07-30T15:28:43.220+00:00",
						selfGeneration : sGeneration,
						formatGenerated: sFormat,
						managerConfirm: sManagerC,
						sendDocAutho: (sManagerC == "Signature")? null : sSendDocAutho,
						typeSign:sSendSignTech,
					};
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.update('/template/' + window.idTemplate, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						sap.m.MessageToast.show(i18n.getText("templatedetails_Updated_template"));
						window.refreshviewTmplD();
						dialog.close();
					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Updated_error_template"));
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
		dialog.getBeginButton().setEnabled(true);
	},

	// -------------------------------------------------
	handleUploadComplete : function(oEvent) {
		var sResponse = oEvent.getParameter("response");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var sMsg=i18n.getText("global_Upload_sucess");
		if(oEvent.getParameter("status")===500){sMsg=i18n.getText("templatedetails_Error_uploaded_template");}
		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			if (sResponse == "200") {
				oEvent.getSource().setValue("");
				window.refreshviewTmplD();
			} else {
				sMsg = i18n.getText("global_Upload_error");
			}
			sap.m.MessageToast.show(sMsg);
		} else {
			oEvent.getSource().setValue("");
			window.refreshviewTmplD();
			sap.m.MessageToast.show(sMsg);
		}
		jQuery.sap.delayedCall(3000, this, function () {
			sap.ui.core.BusyIndicator.hide();
		});
	},

	// -------------------------------------------------
	handleUploadPress : function(oEvent) {
		sap.ui.core.BusyIndicator.show(0);
		var oFileUploader = this.getView().byId("template");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show(i18n.getText("global_Choose_file_first"));
			return;
		}
		oFileUploader.upload();
	},

	// -------------------------------------------------
	handleTypeMissmatch : function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show(i18n.getText("templatedetails_File_type") + oEvent.getParameter("fileType") + i18n.getText("templatedetails_Not_supported_choose_types")+ sSupportedFileTypes);
	},

	handleValueChange : function(oEvent) {
		var msg = this.getView().getModel("i18n").getResourceBundle().getText("templatedetails_Press_upload_file");
		sap.m.MessageToast.show(msg + oEvent.getParameter("newValue") + "'");
	},

	// -----------------------------------------------------
	// --carga el listadod de variables
	lcFuncLoadFieldsTemplate : function(oEvent) {
		var amModel = new sap.ui.model.json.JSONModel();

	},

	// -------------------------------------------------------
	// -------------------------------------------------
	// Add template filter
	handleAddTemplateFilter : function(oEvent) 
	{
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog(
				{
					title : i18n.getText("templatedetails_Insert_template_filter"),
					type : 'Message',
					content : 
						[						
						new sap.m.Label({text : i18n.getText("templatedetails_Path"),labelFor : 'submitDialogPathF',width : '100%'}),
						new sap.m.Input('submitDialogPathValueF', {
							liveChange : function(oEvent) {
								var sText = oEvent.getParameter('value');
								var parent = oEvent.getSource().getParent();
								parent.getBeginButton().setEnabled(sText.length > 0);
							},
							fieldWidth : '50%',
						}),
	
						new sap.m.Label({
							text : i18n.getText("global_Value"),
							labelFor : 'submitDialogPathF'
						}),
	
						new sap.m.Input('submitDialogKeyValueF', {
							liveChange : function(oEvent) {
								var sText = oEvent.getParameter('value');
								var parent = oEvent.getSource().getParent();
								parent.getBeginButton().setEnabled(sText.length > 0);
							},
							width : '100%',
						}),
	

						new sap.m.MenuButton({text : i18n.getText("templatedetails_Path_create"),tooltip : i18n.getText("templatedetails_File_related_actiones")})			
						.setMenu(window._this['ctrltmplD'].recursiveMenu( window._this['ctrltmplD'].menuData,"PerPerson")),

	
						new sap.m.Label({
							text : '',
							labelFor : '',
							width : '100%'
						}), 
						],

			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : false,
				press : function() {
					var sPath = sap.ui.getCore().byId('submitDialogPathValueF').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogKeyValueF').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						value : sValue,
						path : sPath,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templates/insert_filter/' + window.idTemplate, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show(i18n.getText("templatedetails_Filter_added"));
						window.refreshviewTmplD();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Filter_error_added"));
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
	// Delete tempalte filter
	handleDeleteTemplateFilter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idFilter = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.remove('/templates/delete_filter/' + window.idTemplate + '/' + idFilter, null, function(oData, response) {
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(i18n.getText("templatedetails_Filter_delete"));
			window.refreshviewTmplD();

		}, function() {
			sap.m.MessageToast.show(i18n.getText("templatedetails_Filter_error_delete"));
		});
	},

	//-----------------------------------------------------------
	// Edit template filter
	handleEditTemplateFilter : function(oEvent) {
		var idFilter = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var path = this.getView().getModel().getProperty("path", oEvent.getSource().getBindingContext());
		var value = this.getView().getModel().getProperty("value", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("templatedetails_Edit_template_filter"),
			type : 'Message',
			content : [new sap.m.Label({
				text :  i18n.getText("templatedetails_Path"),
				labelFor : 'submitDialogPathF',
				width : '100%'
			}),

			new sap.m.Input('submitDialogPathValueF', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : path,
				fieldWidth : '50%',
			}),

			new sap.m.Label({
				text : i18n.getText("global_Value"),
				labelFor : 'submitDialogPathF'
			}),

			new sap.m.Input('submitDialogKeyValueF', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				value : value,
				width : '100%',
			}),

			new sap.m.Label({
				text : '',
				labelFor : '',
				width : '100%'
			}), ],

			beginButton : new sap.m.Button({
				text : i18n.getText("global_Update"),
				enabled : false,
				press : function() {
					var sPath = sap.ui.getCore().byId('submitDialogPathValueF').getValue();
					var sValue = sap.ui.getCore().byId('submitDialogKeyValueF').getValue();

					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					var oparameters = {
						id : idFilter,
						value : sValue,
						path : sPath,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/templates/update_filter/' + window.idTemplate, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);

						sap.m.MessageToast.show(i18n.getText("templatedetails_Update_filter"));
						window.refreshviewTmplD();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("templatedetails_Update_error_filter"));
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
	handleIconTabBarSelect: function (oEvent) {
		
		var iconTab = this.byId("idIconTabBar");
		var select= iconTab.getSelectedKey();
		var carousel = this.byId("previewCarousel");
		carousel.destroyPages();
		var images ;		
		if (select==="preview"){
			sap.ui.core.BusyIndicator.show(0);
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.read('/template/infopage/' + window.idTemplatePpDoc+'/'+window.lastVersion,null, null, function(oData, response) {
				
			}, function(oData, response) {
				var json = JSON.parse(response.body);
				
				for(var i in json){
					
					images = new sap.m.Image( {
						src : json[i],
						alt : "image/"+i,
						width:'100%',
						height:'100%'
					});
					
					carousel.addPage(images);
				}
				jQuery.sap.delayedCall(3000, this, function () {
				sap.ui.core.BusyIndicator.hide();
				});
				if(response.body==500){
					var msg = this.getView().getModel("i18n").getResourceBundle().getText("templatedetails_Error_image_preview");
					sap.m.MessageToast.show(msg);
				}
			});
			jQuery.sap.delayedCall(3000, this, function () {
			sap.ui.core.BusyIndicator.hide();});
		}
		if(select==="variable"){
			window._this['ctrltmplD'].byId('multiMetada').removeAllSelectedItems();
			window._this['ctrltmplD'].byId('multiMetada').addSelectedKeys(window.metadata);
		}
	},
	
	handleSelectionFinish : function (evt){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var templateFieldId = window._this['ctrltmplD'].byId('multiMetada').getSelectedKeys();
		var oparameters = {metaData: templateFieldId};
		
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.create('/template/metadatainsert/' + window.idTemplate, oparameters, null, function(oData, response) {
			sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_peopledoc_updated"));

		}, function() {
			sap.m.MessageToast.show(i18n.getText("mappingfield_Mapping_field_error_peopledoc_updated"));
		});
		window._this['ctrltmplD'].byId('multiMetada').removeAllSelectedItems();
		window.metadata = templateFieldId;
		window._this['ctrltmplD'].byId('multiMetada').addSelectedKeys(window.metadata);
	}
	
});
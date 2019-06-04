jQuery.sap.require("sap.m.MessageBox");
jQuery.sap.require("generator.util.AjaxUtil");

sap.ui.controller("generator.controller.document.DocumentDetails", 
{
	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var iconTab = this.byId("idIconTabBar");
		var carousel = this.byId("previewCarousel");
		var idObjectStatus = this.byId("idObjectStatus");
		var idSignature = this.byId("idSignature");
		window.selectKey ="info";
		window._this['ctrldetails'] = this;
		window.refreshviewDetails = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/document/" + window.idDocument, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					window.idTemplatePpDoc = json.document.templateId.identifierPpd;
					window.lastVersion = json.document.templateId.latesVersion;
					window.hashDownload = json.hashDownloadDoc; 
					window.format= json.document.outputFormat;
					window.managerConfirm = json.document.templateId.managerConfirm;
					
					if (json.user) {
						json.userpresent = true;
						var ml = eval(json.user.employmentNav.results[0].startDate.replace(/\/Date\((\d+)\)\//gi, "$1"));
						var date = new Date(ml + 5 * 3600 * 1000);
						json.user.hireDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
					} else {
						json.userpresent = false;
					}

					if (json.generated.length > 0) {
						lgh = json.generated.length;
						window.currentObjId = json.generated[lgh - 1].id;
						window.currentObjIdPpd = json.generated[lgh - 1].generatedIdPpd;
						json.size = lgh;
						json.image = "/generator/rst/json/generated/" + json.generated[lgh - 1].generatedIdPpd + "/preview";
						json.activated = true;
					} else {
						json.activated = false;
					}

					if (json.document.templateId.esign != "Yes") {
						json.visible = false;
						json.visible2 = true;
					} else {
						
						if(json.document.outputFormat === 'PDF'){
							json.visible = true;
							json.visible2 = false;
						}else{
							json.visible = false;
							json.visible2 = true;
						}
					
					} 
					
					
					if(window.myself){
						json.myself = false;
					}else{
						json.myself = true;
					}
					
					if(json.document.templateId.managerConfirm == "Authorization"){
						json.visible = false;
						idSignature.setVisible(false);
						json.docInfo.generateVariables.variables.visible = false;
					}
					
					if(json.document.templateId.managerConfirm == "Signature" || json.document.templateId.managerConfirm == "None"){
						json.docInfo.generateVariables.variables.visible = true;
					}
					
					if(json.document.status == "Validated"){
						json.visible2=true;
					}
					
					var codeLang= json.document.templateId.locale;
					var oDataModelCode = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModelCode.read("/admin/language/" + codeLang, {
						sync : true,
						success : function(oData, response) {
							if(response.body !=null && response.body !=""){
								var code = JSON.parse(response.body);
								json.document.language= code[0].description;
								amModel.setData(json);
							}
						},
						error : function(oData, response) {
							sap.m.MessageToast.show('Error Info Document (language)');
						}
					});
					
					amModel.setData(json);
					sap.ui.core.BusyIndicator.hide();
					var oDataModel4 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel4.read("/generated/infowarn/" + window.currentObjIdPpd+"/"+window.lastVersion, {
						sync : false,
						success : function(oData, response) {
							if(response.body == 200 || json.generated.length === 0){
								idObjectStatus.setVisible(false);
							}else{
								idObjectStatus.setVisible(true);
								/*sap.m.MessageBox.show("Problems in the Document Generated", {
									icon: sap.m.MessageBox.Icon.WARNING,
								    title: "Warning Document",
								    onClose: null,
								    styleClass: "",
								    initialFocus: null ,
								    textDirection: sap.ui.core.TextDirection.Inherit
								    });*/
							}
						},
						error : function(oData, response) {
							sap.m.MessageToast.show('warning - get Info from ppd (infowarn) '+ window.currentObjIdPpd+"/"+window.lastVersion);
						}
					});
				},
				error : function(oData, response) {
					// items = oData.results.length;
					sap.ui.core.BusyIndicator.hide();
				}
			});
			// this.getView().setModel(amModel);

			window.objectTTT2 = window._this['ctrldetails'].getView().byId("docDetailsMasterPage");
			sap.ui.getCore().setModel(amModel, "detailModel");
			window._this['ctrldetails'].getView().byId("docDetailsMasterPage").setModel(amModel);
			window._this['ctrldetails'].getView().setModel(amModel, "detailModel");
			
			iconTab.setSelectedKey(window.selectKey);
			carousel.destroyPages();
			//sign mapping
			var amModelSign = new sap.ui.model.json.JSONModel();
			var oDataModelSign = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelSign.read("/admin/mappingsignall", {
				sync : true,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelSign.setSizeLimit(json.length);
					amModelSign.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelSign, "modelSign");
			
			
			/*if(window.idTmp){
				oDataModel.read("/template/getdocauthorization/"+window.idTmp+"/"+window.idDocument, {
					sync : true,
					success : function(oData, response) {
						
					},
					error : function(oData, response) {
					}
				});
			}*/
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},

	// -----------------------------------------------------------------------
	_handleRouteMatched : function(oEvent) {
		if (oEvent.getParameter("name") !== "docs") {
			return;
		}
		window._this['ctrldetails'] = this;
		sap.ui.core.BusyIndicator.show(0);

		var oParameters = oEvent.getParameter("name");
		var varPath = oEvent.getParameter("arguments").id;
		window.idDocument = varPath;

		window.refreshviewDetails();
	},
	// -----------------------------------------------------------------------
	onValidatedPressed : function() {

		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		oDataModel2.create('/generated/' + window.currentObjIdPpd + "/" + window.idDocument + "/validated", null, null, function(oData, response) {
			var json = JSON.parse(response.body);
			// oTable.getModel().oData.push(json);
			var msg = i18n.getText("global_Sent");
			sap.m.MessageToast.show(msg);

		}, function() {
			var msg=document_Error_sending_documentgetText("document_Error_sending_document");
			sap.m.MessageToast.show(msg);
		});
	},
	// -----------------------------------------------------------------------
	onGeneratePressed : function() {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("document_Confirm_generation"),
			type : 'Message',
			content : new sap.m.Text({
				text : i18n.getText("document_Signature_pending_canceled")
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				press : function() 
				{
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/generated/' + window.idDocument, null, null, function(oData, response) {
						
						console.log(response)
						var json = JSON.parse(response.body);
						console.log(json)
						sap.m.MessageToast.show(i18n.getText("document_Document_generated"));												
						window.refreshviewDetails();
						
					}, function() {
						sap.m.MessageToast.show(i18n.getText("document_Document_error_generated"));
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
	// -----------------------------------------------------------------------
	onBeforeRendering : function() {

	},
	// -----------------------------------------------------------------------
	onVersionPressed : function(oEvent) {
		var iconTab = this.byId("idIconTabBar");
		var select= iconTab.getSelectedKey();
		var carousel = this.byId("previewCarousel");
		carousel.destroyPages();
		var images ;
		if (select==="preview"){
			sap.ui.core.BusyIndicator.show(0);
			console.log("preview "+window.lastVersion);
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.read('/generated/infopage/' + window.currentObjIdPpd+'/'+window.lastVersion,null, null, function(oData, response) {
				
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
			});
			
		}
		if (select === "signProcess"){
			console.log("Sign Process");
			sap.ui.core.BusyIndicator.show(0);
			var amModelSign = new sap.ui.model.json.JSONModel();
			var oDataModelSign = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			console.log(window.idDocument);
			oDataModelSign.read('/signatureprocess/' + window.idDocument,{
				sync : true,
				success : function(oData, response) {
					if(response.body!==""){
						var json = JSON.parse(response.body);
						window.observation = json;
						amModelSign.setData(json);
					}
				},
				error : function(oData, response) {
					var msg = this.getView().getModel("i18n").getResourceBundle().getText("document_Error_signature_document");
					sap.m.MessageToast.show(msg);
				}
			});
			sap.ui.getCore().setModel(amModelSign, "aModelSignature");
			window._this['ctrldetails'].getView().byId("idTableSignObservation").setModel(amModelSign);
			jQuery.sap.delayedCall(3000, this, function () {
			sap.ui.core.BusyIndicator.hide();
			});
		}
		if (select === "variable"){
			
			if(window.managerConfirm == "Authorization" || window.managerConfirm == "None"){
				var editVariable = this.byId("editVariable");
				editVariable.setVisible(false);
			}
			
		}
	},
	// -----------------------------------------------------------------------
	onDownloadPressed : function(oEvent) 
	{
		/*sap.m.MessageToast.show('Download started');
		window.location.replace("https://api.staging.eu.people-doc.com/api/v1/enterprise/documents/" + window.hashDownload);*/
		
		
		if (window.currentObjIdPpd != '') {
			if(window.format==='PDF' || window.format == null){
				window.open("/generator/rst/json/generated/" + window.currentObjIdPpd + "/file", "_blank");
			}
			if(window.format==='DOCX'){
				window.open("/generator/rst/json/generated/" + window.currentObjIdPpd + "/filedocx", "_blank");
			}
		}
	},
	// -----------------------------------------------------------------------
	onNavPressed : function() {
		delete window.currentObjIdPpd; 
		delete window.lastVersion;
		delete window.selectKey;
		delete window.hashDownload;
		if(window.back){
			window.history.back();	
		}else{

			this._oRouter.myNavBack("documents");
		}
	},
	// -----------------------------------------------------------------------
	onPressDelete : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("document_Confirm_delete_document"),
			type : 'Message',
			content : new sap.m.Text({
				text :  i18n.getText("document_Question_confirm_delete_document")
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Delete"),
				press : function() {
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.remove('/deleteDocument/' + window.idDocument, null, function(oData, response) {
						
						var json = JSON.parse(response.body);
						sap.m.MessageToast.show(json.response);

					}, function() {
						sap.m.MessageToast.show(i18n.getText("document_Deleting_document"));
					});
					window._this['ctrldetails']._oRouter.myNavBack("documents");
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
	// -----------------------------------------------------------------------
	onInfoError : function(oEvent) {

		// create popover
		if (!this._oPopover) {
			this._oPopover = sap.ui.xmlfragment("popoverNavCon", "generator.view.fragments.DialogErrorsTab", this);
			this._oPopover.bindElement("detailModel>" + oEvent.getSource().getParent().getBindingContext().getPath() + "/errors");
			this.getView().addDependent(this._oPopover);
		}

		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},
	// -----------------------------------------------------------------------
	onSignaturePressed : function(oEvent) {
		sap.ui.core.BusyIndicator.show(0);
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		var i18 = this.getView().getModel("i18n").getResourceBundle()
		oDataModel2.create('/generated/' + window.currentObjId + "/" + window.idDocument + "/signature", null, null, function(oData, response) {
			
			var json = JSON.parse(response.body);
			sap.m.MessageToast.show(json.response);

		}, function() {
			var msg = i18.getText("document_Sending_error_document");
			sap.m.MessageToast.show(msg);
		});
		jQuery.sap.delayedCall(3000, this, function () {
			sap.ui.core.BusyIndicator.hide();
		});
	},
	// -----------------------------------------------------------------------
	onCompanyPressed : function(oEvent) {

		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		oDataModel2.create('/generated/' + window.currentObjIdPpd + "/" + window.idDocument + "/company", null, null, function(oData, response) {
			var json = JSON.parse(response.body);
			// oTable.getModel().oData.push(json);
			var msg = i18n.getText("global_Sent");
			sap.m.MessageToast.show(msg);

		}, function() {
			var msg = ti18n.getText("document_Sending_error_document");
			sap.m.MessageToast.show(msg);
		});
	},

	// -------------------------------------------------
	handleEditSign : function(oEvent) {
		// load Mapping Signature
		window.selectKey = "signature";
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var idSignature = this.getView().getModel("detailModel").getProperty("id", oEvent.getSource().getBindingContext());
		var idSignDocument = this.getView().getModel("detailModel").getProperty("idSignDocument", oEvent.getSource().getBindingContext());
		var idSingGroupTemplate = this.getView().getModel("detailModel").getProperty("idSingGroupTemplate", oEvent.getSource().getBindingContext());
		var slugField = this.getView().getModel("detailModel").getProperty("slug", oEvent.getSource().getBindingContext());
		var pathField = this.getView().getModel("detailModel").getProperty("path", oEvent.getSource().getBindingContext());
		var orderField = this.getView().getModel("detailModel").getProperty("order", oEvent.getSource().getBindingContext());
		var constantFlag = this.getView().getModel("detailModel").getProperty("flag", oEvent.getSource().getBindingContext());
		var constantValue = this.getView().getModel("detailModel").getProperty("value", oEvent.getSource().getBindingContext());
		var constantPath = this.getView().getModel("detailModel").getProperty("path", oEvent.getSource().getBindingContext());
		var idSignTmp = this.getView().getModel("detailModel").getProperty("idSignTempLib", oEvent.getSource().getBindingContext());
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
			}else{
				idSignTmp=-1;
			}
		}
		json.forEach(function(element) {
			console.log(element);  
			if(idSignTmp==element.id){
				  idValue=element.id;
				  valueSign=element.nameSource;
			}
		});

		var dialog = new sap.m.Dialog({
			title : i18n.getText("templatedetails_Signature")+" "+ slugField,
			type : 'Message',
			content : 
			[
				new sap.m.Panel(
				{
					headerText: i18n.getText("templatedetails_Current_signature")	,
					expandable:true,
					expanded:true,
					content : 
					[
						new sap.m.Label({
							design: "Bold",
							text : i18n.getText("templatedetails_Current_signature"),
							labelFor : 'submitDialogPath',
							width : '100%',
						}),
						new sap.m.Label({
								text : '',
								labelFor : '',
								width : '90%'
							}),
						new sap.m.Label({
								text : i18n.getText("templatedetails_Signature_name") + " : "+valueSign,
								labelFor : 'submitDialogPath',
								width : '100%',
							}),
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
					headerText: i18n.getText("templatedetails_Edit_signature_template"),
					expandable:true,
					expanded:false,
					content :
					[	 
						new sap.m.Label({
						text : '',
						labelFor : '',
						width : '90%'
						}), 
						new sap.m.Label({
						text : i18n.getText("document_Signature_name"),
						labelFor : 'submitDialogPath'
						}),
						new sap.m.Select('submitDialogSign', {
						width : '100%',
						change : function(oEvent) {
							console.log("liveChange");
							var sText2 = sap.ui.getCore().byId('submitDialogSign').getSelectedKey();
							console.log("***"+sText2);
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
						new sap.m.Label("labelConstant",{text : i18n.getText("document_Sign_constant"),visible: constantFlag,labelFor : 'isSignConstant'}), 
						new sap.m.Label("labelEmailFormat",{text:" ["+i18n.getText("document_Signature_value_email")+" ] ",visible: constantFlag}),
						
						new sap.m.Input('submitDialogSignConstantValue', {
							visible : constantFlag,
							fieldWidth : '50%',
						}).setValue(constantPath),
						new sap.m.Label({
							text : '',
							labelFor : '',
							width : '100%'
						}),
						new sap.m.Label({
							text : i18n.getText("document_Sign_order"),
							labelFor : 'valueOrder',
							width : '10%'
						}), new sap.m.Input('submitDialogOrderValue', {
							liveChange : function(oEvent) {
								var sText = oEvent.getParameter('value');
								var parent = oEvent.getSource().getParent();
							},
							type : "Number",
							fieldWidth : '50%',
							value : orderField
						}),
					]
				}
				),

			],
			buttons : [ new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : true,
				press : function() {
					var sValue = sap.ui.getCore().byId('submitDialogSign').getSelectedItem().getText();
					var sOrder = sap.ui.getCore("detailModel").byId('submitDialogOrderValue').getValue();
					var sConstant = sap.ui.getCore().byId('submitDialogSignConstantValue').getValue();
					var sIdSign = sap.ui.getCore().byId('submitDialogSign').getSelectedKey();
					var sIsConst = false
					if(sIdSign==-1 || sIdSign==-2){sIsConst=true;}
					if(sIdSign==-1){sValue = sConstant;}
					if(sIdSign==-2){sValue = "";}
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					// var oModel = this.getView().getModel();
					if(sValue!=null && sValue!="")
					{
						var oparameters = {
							id : idSignature,
							idSignDocument : idSignDocument,
							idSingGroupTemplate : idSingGroupTemplate,
							slug : slugField,
							path : sValue,
							flag : sIsConst,
							order : sOrder,
							idSignTempLib : sIdSign,
						};
	
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel2.create('/updateDocSing/' + window.idDocument, oparameters, null, function(oData, response) {
							var json = JSON.parse(response.body);
							sap.m.MessageToast.show(i18n.getText("document_Signature_updated"));
							window.refreshviewDetails();
						}, function() {
							sap.m.MessageToast.show(i18n.getText("document_Error_update_signature"));
						});
					}
					else{
						sap.m.MessageToast.show(i18n.getText("document_Value_not_empty"));
					}
				}
			}), new sap.m.Button({
				text : i18n.getText("document_Reset_sign"),
				enabled : true,
				press : function() {
					var sValue = sap.ui.getCore().byId('submitDialogSign').getSelectedItem().getText();
					var sOrder = sap.ui.getCore("detailModel").byId('submitDialogOrderValue').getValue();
					var sConstant = sap.ui.getCore().byId('submitDialogSignConstantValue').getValue();
					var sIdSign = sap.ui.getCore().byId('submitDialogSign').getSelectedKey();
					var sIsConst = false
					if(sIdSign==-1 || sIdSign==-2){sIsConst=true;}
					if(sIdSign==-1){sValue = sConstant;}
					if(sIdSign==-2){sValue = "";}
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					// var oModel = this.getView().getModel();

					var oparameters = {
						id : idSignature,
						idSignDocument : idSignDocument,
						idSingGroupTemplate : idSingGroupTemplate,
						slug : slugField,
						path : sValue,
						flag : sIsConst,
						order : sOrder,
						idSignTempLib : sIdSign,
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/resetDocSing/' + window.idDocument, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						sap.m.MessageToast.show(i18n.getText("document_Document_signature_reset"));
						window.refreshviewDetails();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("document_Error_reset_document_signature"));
					});
				}
			}), new sap.m.Button({
				text : i18n.getText("global_Cancel"),
				press : function() {
					dialog.close();
				}
			}) ],
			afterClose : function() {
				dialog.destroy();
			}
		});

		dialog.open();

	},
	// ----------------------------------------------------------------------
	handleEditField : function(oEvent) {
		window.selectKey = "variable";
		var idField = this.getView().getModel("detailModel").getProperty("id", oEvent.getSource().getBindingContext());
		var idFieldDocument = this.getView().getModel("detailModel").getProperty("idAux", oEvent.getSource().getBindingContext());
		var slugField = this.getView().getModel("detailModel").getProperty("slug", oEvent.getSource().getBindingContext());
		var pathField = this.getView().getModel("detailModel").getProperty("path", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("document_Edit_document_field"),
			type : 'Message',
			content : [ new sap.m.Label({
				text : i18n.getText("document_Name_field")+': ' + slugField,
				labelFor : 'submitDialogSlug',
				width : '100%'
			}), new sap.m.Label({
				text : '',
				labelFor : '',
				width : '90%'
			}), new sap.m.Label({
				text : i18n.getText("document_Edit_field_constant_values"),
				labelFor : 'submitDialogPath'
			}), new sap.m.Input('submitDialogPathFieldValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
				},

				width : '100%',
				value : "",
			})

			],
			buttons : [ new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : true,
				press : function() {
					var sValue = sap.ui.getCore("detailModel").byId('submitDialogPathFieldValue').getValue();
					dialog.close();

					if(sValue!=null && sValue!="")
					{
						var amModel = new sap.ui.model.json.JSONModel();
	
						//send parameters for DocumentFields entity
						var oparameters = {
							id : idFieldDocument,
							nameSource : slugField,
							nameDestination : sValue,
							isConstants : true
						};
	
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel2.create('/updateDocField/' + window.idDocument, oparameters, null, function(oData, response) {
							var json = JSON.parse(response.body);
							sap.m.MessageToast.show('Document Field Updated');
							window.refreshviewDetails();
						}, function() {
							sap.m.MessageToast.show('Error update Document Field');
						});
					}
					else
					{
						sap.m.MessageToast.show('the value can not be empty');
					}
				}
			}), new sap.m.Button({
				text : i18n.getText("document_Reset_field"),
				enabled : true,
				press : function() {
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					console.log("idFieldDocument "+idFieldDocument );
					
					//send parameters for DocumentFields entity
					var oparameters = {
						id : idFieldDocument
					};

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/resetDocField/' + window.idDocument, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);
						sap.m.MessageToast.show('Document Field Reset');
						window.refreshviewDetails();

					}, function() {
						sap.m.MessageToast.show('Error reset Document Field');
					});
				}
			}), new sap.m.Button({
				text : i18n.getText("global_Cancel"),
				press : function() {
					dialog.close();
				}
			}) ],
			afterClose : function() {
				dialog.destroy();
			}
		});

		dialog.open();

	},
	
	onObservationSign : function(oEvent) {
		var signObservation =sap.ui.getCore().getModel("aModelSignature").getProperty("observations", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var message = sap.m.MessageBox.show(signObservation, {
			icon: sap.m.MessageBox.Icon.NONE,
		    title: i18n.getText("document_Signature_observation"),
		    onClose: null,
		    styleClass: "",
		    initialFocus: null ,
		    textDirection: sap.ui.core.TextDirection.Inherit
		    });
	},
	
	onPressArchive : function(oEvent){
		console.log("Archive");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var obj=[]
		var tmp = new Object();
		tmp.id = window.idDocument;
		tmp.check = true;
		obj.push(tmp);
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Confirm"),
			type : 'Message',
			content : new sap.m.Text({
				text : i18n.getText("document_Msg_archive")
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				press : function() {
					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/documentsitemsarchive/' , obj,{
						sync : false,
						success : function(oData2, response) {
							sap.m.MessageToast.show(i18n.getText("pending_Archive_documents"));
						},
						error : function(oData2, response) {
							
							sap.m.MessageToast.show(i18n.getText("pending_Archive_documents_error"));
						}
					});
					dialog.close();
					
					jQuery.sap.delayedCall(2000, this, function () {
						if(window.back){
							window.history.back();	
						}else{
							this._oRouter.myNavBack("documents");
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
		
	}

});
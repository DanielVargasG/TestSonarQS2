jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("generator.controller.employee.Sign", {

	onInit : function() 
	{	
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
	
		window._this['ctrlsign'] = this;
		window.refreshviewPerso = function() 
		{
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("user/me/null", {
				sync : false,
				success : function(oData, response) {

					var json = JSON.parse(response.body);
					if (json.employmentNav) {
						if (json.employmentNav.results[0].startDate) {
							var ml = eval(json.employmentNav.results[0].startDate.replace(/\/Date\((\d+)\)\//gi, "$1"));
							var date = new Date(ml + 5 * 3600 * 1000);
							json.HireDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
						}
					}
					

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);

					// call JsonDocumentService
					oDataModel2.read("/admin/docsTypeDocs/" + json.personIdExternal, {
						sync : false,
						success : function(oData, response) {
							if (response.body) {
								json.documents = JSON.parse(response.body);
								sap.ui.core.BusyIndicator.hide();

								var oDataModel3 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
								// call JsonUserService
								oDataModel3.read("user/signatures", {
									sync : false,
									success : function(oData, response) {
										console.log("****");
										if (response.body != "") {
											json.signatures = JSON.parse(response.body);
											amModel.setData(json);
											
										}
										
										sap.ui.core.BusyIndicator.hide();
										amModel.setData(json);
										sap.ui.core.BusyIndicator.hide();
									},
									error : function(oData, response) {
										sap.ui.core.BusyIndicator.hide();
										// items = oData.results.length;
									}
								});
							} else {
								sap.ui.core.BusyIndicator.hide();
							}
						},
						error : function(oData, response) {
							sap.ui.core.BusyIndicator.hide();
							// items = oData.results.length;
						}
					});

				},
				error : function(oData, response) {
					sap.ui.core.BusyIndicator.hide();
					// items = oData.results.length;
				}
			});
			window._this['ctrlsign'].getView().setModel(amModel);
		}
		
		//-----------
		window.refreshviewAutho = function() {
			var amModelAutho = new sap.ui.model.json.JSONModel();
			var oDataModel= new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("template/getinfoauthorization", {
				sync : false,
				success : function(oData, response) {
					if(response.body != null && response.body !==""){
						var json = JSON.parse(response.body);
						console.log(json);
						amModelAutho.setData(json);						
					}
					
				},	
				error : function(oData, response) {
					console.log(response)
						sap.ui.core.BusyIndicator.hide();
				}
			});
			
			window._this['ctrlsign'].amModelAutho = amModelAutho;
			sap.ui.getCore().setModel(amModelAutho, "modelAutho");
			window._this['ctrlsign'].getView().setModel(amModelAutho, "modelAutho");
			sap.ui.core.BusyIndicator.hide();
		}
		
		//-------------
		window.refreshviewMyAutho = function ()
		{			var amModelAuth = new sap.ui.model.json.JSONModel();
			var oDataModelAuth = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelAuth.read("/template/getauthuser",{
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					if(json){
						amModelAuth.setData(json);
					}
					
				},
				error : function(oData, response) {
				}
			});
			
			sap.ui.getCore().setModel(amModelAuth, "modelMyAuth");
			window._this['ctrlsign'].getView().setModel(amModelAuth, "modelMyAuth");
	
		}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	
	
	//--------------------------------------------------	
	onCollapseExpandPress : function(evt){
		var oNavigationList = sap.ui.getCore().byId(evt.getParameter("id"));
		var bExpanded = oNavigationList.getExpanded();
		oNavigationList.setExpanded(!bExpanded);
	},
	
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "signature") {
			return;
		}
		window._this['ctrlsign'] = this;
		sap.ui.core.BusyIndicator.show(0);
		window.refreshviewAutho();
	},

	handleListItemPress : function(oEvent) {
		// console.log("inside the HandleListItem " + oEvent);
		var context = oEvent.getSources().getBindingContext();
		this.nav.to("Detail", context);
	},

	handleSearch : function(oEvent) {
		var filters = [];
		var query = oEvent.getParameter("query");
		if (query && query.length > 0) {
			var filterVar = new sap.ui.model.Filter("SoId", sap.ui.model.FilterOperator.Contains, query);
			filters.push(filterVar);
		}

		var list = this.getView().byId("list");
		var binding = list.getBinding("items");
		binding.filter(filters);
	},
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},
	onDownPressed : function(oEvent) {
		var bindingContext = oEvent.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var unikID = bindingContext.oModel.oData.documents[res[2]].id;
		var unikURL = bindingContext.oModel.oData.documents[res[2]].hash;
		sap.m.MessageToast.show('Download started');
		window.location.replace("https://api.staging.eu.people-doc.com/api/v1/enterprise/documents/" + unikID + "/download/" + unikURL);

	},
	handleListSelect : function(oEvent) {
		var context = oEvent.getParameter("listItem").getBindingContext();
		var odata = context.getPath();
		this.oHasher = sap.ui.core.routing.HashChanger.getInstance();
		this.oHasher.setHash(context);
		var sViewId = this.oHasher.getHash().split("/")[2];
		this._oRouter.navTo("Detail", {
			contextPath : sViewId
		});
	},
	handleUploadComplete : function(oEvent) {
		var sResponse = oEvent.getParameter("response");

		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			if (sResponse == "200") {
				sMsg = "Upload Success";
				oEvent.getSource().setValue("");
			} else {
				sMsg = "Upload Error";
			}
			sap.m.MessageToast.show(sMsg);
		}
	},

	handleUploadPress : function(oEvent) {
		var oFileUploader = this.getView().byId("fileUploader");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show("Choose a file first");
			return;
		}
		oFileUploader.upload();
	},

	handleTypeMissmatch : function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show("The file type *." + oEvent.getParameter("fileType") + " is not supported. Choose one of the following types: " + sSupportedFileTypes);
	},

	handleValueChange : function(oEvent) {
		sap.m.MessageToast.show("Press 'Upload File' to upload file '" + oEvent.getParameter("newValue") + "'");
	},
	
	//---------------------------------------------------------------------------
	onPressReloadAutho : function(oEvt){
		console.log("Reload")
		sap.ui.core.BusyIndicator.show(0);
		window.refreshviewAutho();
	},
	
	//------------------------------------------------------------------
	handleIconTabBarSelect : function(oEvent) 
	{	 
		var sKey = oEvent.getParameter("key"), oFilter;
		console.log(sKey);
		if(sKey == "MyAuthoRequest"){
			window.refreshviewAutho();
		}			
		else if(sKey == "ByAutho")
		{	
			window.refreshviewMyAutho();
		}
	},
	
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//Autho-------------------------------------------------------------
	onPressAuthoVariablesDoc: function(evt)
	{
		var bindingContext = evt.getSource().getBindingContext("modelAutho");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var idAutho = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("id", bindingContext);		
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		
		var amModelVars = new sap.ui.model.json.JSONModel();
		oDataModel2.read('/template/authogetvaridoc/'+idAutho ,{
			sync : false,
			success : function(oData2, response) 
			{	
				var json = JSON.parse(response.body);
				amModelVars.setData(json);
				console.log(json);				
			},
			error : function(oData2, response) {				
				sap.m.MessageToast.show(i18n.getText("global_Error"));
			}
		});
		
		if (!this.resizableDialog) 
		{
			this.resizableDialog = new sap.m.Dialog(
			{
				title : i18n.getText("global_Variables"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelVars).bindItems("/generateVariables/variables/", new sap.m.StandardListItem({
					title : "{slug}",
					description: "{value}",
					type : "Active",
					icon : "sap-icon://less",
				}))],
				buttons : 
					[ new sap.m.Button({
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
	
	//------------------------------------------------------------------------------
	onDocumentPressed : function(oEvent) 
	{
		var bindingContext = oEvent.getSource().getBindingContext("modelAutho");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var template = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("template", bindingContext);
		var statusDocumentId = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("statusDocumentId", bindingContext);
		var generatedIdPpd = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("generatedIdPpd", bindingContext);
		var documentId = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("documentId", bindingContext);
		var oRouter = this._oRouter;

		var dialog = new sap.m.Dialog({
			title: i18n.getText("document_Generated_document"),
			type: 'Message',
			content: new sap.m.Text({ text: i18n.getText("document_Id_document")+": "+documentId+", "
											+i18n.getText("global_Status")+": "+statusDocumentId+", "
											+i18n.getText("template_Send_Authorized_Document")+": "+template.sendDocAutho}),
			beginButton: new sap.m.Button({
				text: i18n.getText("document_Confirm_generation"),
				press: function () 
				{
					if(documentId!=null)
					{
						if(template!=null && template.sendDocAutho == "Download")
						{
							dialog.close();
							if (window.currentObjIdPpd != '') {
								if(window.format==='PDF' || window.format == null){
									window.open("/generator/rst/json/generated/" + generatedIdPpd + "/file", "_blank");
								}
								if(window.format==='DOCX'){
									window.open("/generator/rst/json/generated/" + generatedIdPpd + "/filedocx", "_blank");
								}
							}
						}
						else if(template!=null && template.sendDocAutho == "EmployeeVault")
						{
							dialog.close();
							var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
							oDataModel2.create('/generated/' + generatedIdPpd + "/" + documentId + "/validated", null, null, function(oData, response) 
							{
								var json = JSON.parse(response.body);
								var msg=i18n.getText("global_Sent");
								sap.m.MessageToast.show(msg);

							}, function() {
								var msg=i18n.getText("document_Error_sending_document");
								sap.m.MessageToast.show(msg);
							});
						}
						else if(template!=null && template.sendDocAutho == "CompanyVault")
						{
							dialog.close();
							var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
							oDataModel2.create('/generated/' + generatedIdPpd + "/" + documentId + "/company", null, null, function(oData, response) 
							{
								var json = JSON.parse(response.body);
								var msg= i18n.getText("global_Sent");
								sap.m.MessageToast.show(msg);

							}, function() {
								var msg = i18n.getText("document_Error_sending_document");
								sap.m.MessageToast.show(msg);
							});
						}
					}							
				}
			}),
			endButton: new sap.m.Button({
				text: i18n.getText("global_Close"),
				press: function () {
					dialog.close();
				}
			}),
			afterClose: function() {
				dialog.destroy();
			}
			
		});
		dialog.open();

	},
	
	//--------------------------------------------------------------------------
	onPressDeleteAutho : function(oEvent){
		var bindingContext = oEvent.getSource().getBindingContext("modelAutho");
		console.log("Delete authorization")
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		
		var dialog = new sap.m.Dialog({
			title: i18n.getText("global_Cancel"),
			type: 'Message',
			content: new sap.m.Text({ text: i18n.getText("managerAuth_delete_confirm")}),
			beginButton: new sap.m.Button({
				text: i18n.getText("global_Yes"),
				press: function () {
					var id = window._this['ctrlsign'].getView().getModel("modelAutho").getProperty("id", bindingContext);
					console.log(id);
					
					oDataModel.read("/template/authocancel/"+id, {
						sync : false,
						success : function(oData, response) 
						{
							window.refreshviewAutho();
							dialog.destroy();
						},
						error : function(oData, response) {
							// items = oData.results.length;
						}
					});
					dialog.close();
				}
			}),
			endButton: new sap.m.Button({
				text: i18n.getText("global_No"),
				press: function () {
					dialog.close();
				}
			}),
			afterClose: function() {
				dialog.destroy();
			}
			
		});
		dialog.open();
	},
	
	//--------------------------------------------------------------------
	//--------------------------------------------------------------------
	//--------------------------------------------------------------------
	//--------------------------------------------------------------------
	//My Authorizations---------------------------------------------------
	
	onPressMyAuthoVariablesDoc: function(evt)
	{
		var bindingContext = evt.getSource().getBindingContext("modelMyAuth");
		var i18n =window._this['ctrlsign'].getView().getModel("i18n").getResourceBundle();
		var idAutho = window._this['ctrlsign'].getView().getModel("modelMyAuth").getProperty("authDocument", bindingContext);		
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		
		var amModelVars = new sap.ui.model.json.JSONModel();
		oDataModel2.read('/template/authogetvaridoc/'+idAutho.id ,{
			sync : false,
			success : function(oData2, response) 
			{	
				var json = JSON.parse(response.body);
				amModelVars.setData(json);
				console.log(json);				
			},
			error : function(oData2, response) {				
				sap.m.MessageToast.show(i18n.getText("global_Error"));
			}
		});
		
		if (!this.resizableDialog) 
		{
			this.resizableDialog = new sap.m.Dialog(
			{
				title : i18n.getText("global_Variables"),
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelVars).bindItems("/generateVariables/variables/", new sap.m.StandardListItem({
					title : "{slug}",
					description: "{value}",
					type : "Active",
					icon : "sap-icon://less",
				}))],
				buttons : 
					[ new sap.m.Button({
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
	
	//---------------------------------------
	onPressMyAuthorization : function(evt){		
		var bindingContext = evt.getSource().getBindingContext("modelMyAuth");
		var mod = bindingContext.sPath.split("/");
		var i18n =window._this['ctrlsign'].getView().getModel("i18n").getResourceBundle();
		var select = window._this['ctrlsign'].getView().getModel("modelMyAuth").getProperty("status", bindingContext);
		console.log("/*/"+select);
		
		if(select == "Pending"){
			var dialog = new sap.m.Dialog({
				title : i18n.getText("global_Status"),
				type : 'Message',
				content : 
				[				
						new sap.m.Label({
							text : i18n.getText("pending_Info_status"),
							labelFor : 'submitDialogSelect13',
							width : '100%',
						}),
						new sap.m.Select('submitDialogSelect2', {
							width : '100%',

						}).addItem(new sap.ui.core.Item({
							key : "Authorized",
							text : i18n.getText("managerAuth_Authorized")
						})).addItem(new sap.ui.core.Item({
							key : "Rejected",
							text : i18n.getText("managerAuth_Rejected")
						})).setSelectedKey(select)

				],
				beginButton : new sap.m.Button({
					text : i18n.getText("global_Submit"),
					press : function() 
					{
						
						var id= window._this['ctrlsign'].getView().getModel("modelMyAuth").getProperty("id", bindingContext);
						var status= sap.ui.getCore().byId('submitDialogSelect2').getSelectedKey();
						console.log("/*/*/*/*/*"+status);
						var amModelTmpAutho = new sap.ui.model.json.JSONModel();
						var oDataModelTmpAutho = new sap.ui.model.odata.ODataModel("/generator/rst/json/", true);
						oDataModelTmpAutho.read("/template/updateauthuser/"+id+"/"+status, {
							sync : false,
							success : function(oData, response) {
								window.refreshviewMyAutho();
							},
							error : function(oData, response) {
							}
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
		
	},
	
	
	


})
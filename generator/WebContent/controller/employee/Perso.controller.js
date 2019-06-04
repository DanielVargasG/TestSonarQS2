jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("generator.controller.employee.Perso", {

	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		// console.log("hi in masters--" + this._oRouter);
		window.temprouter = this._oRouter;
		window._this['ctrlperso'] = this;
		window.refreshviewPerso = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("user/me/" + window.tempUserId, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);

					if (json.employmentNav) {
						if (json.employmentNav.results[0].startDate) {
							var ml = eval(json.employmentNav.results[0].startDate.replace(/\/Date\((\d+)\)\//gi, "$1"));
							var date = new Date(ml + 5 * 3600 * 1000);
							json.HireDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();

							amModel.setData(json);
						}
					}

					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);

					console.log("json.personIdExternal " + json.personIdExternal)
					// call JsonDocumentService
					oDataModel2.read("/admin/docsTypeDocs/" + json.personIdExternal, {
						sync : false,
						success : function(oData, response) {
							if (response.body) {
								json.documents = JSON.parse(response.body);
								sap.ui.core.BusyIndicator.hide();
								
								console.log(json.documents);

								amModel.setData(json);
								var oDataModel3 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
								// call JsonUserService
								oDataModel3.read("user/signatures", {
									sync : false,
									success : function(oData, response) {
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
			window._this['ctrlperso'].getView().setModel(amModel);
		}

		window._this['ctrltmplsemp'] = this;
		window.back = "templatesemployee";
		window.myself = true;
		
		//-------------------------------------------
		window.refreshviewTmplsEmp = function() {

			// load auto self templates
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/searchtselfemplate/" + "all", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					sap.ui.core.BusyIndicator.hide();
					amModelFolder.setData(json);
				},
				error : function(oData, response) {
					sap.ui.core.BusyIndicator.hide();
				}
			});
			
			window._this['ctrltmplsemp'].amModelFolder = amModelFolder;
			sap.ui.getCore().setModel(amModelFolder, "tplModel");
			window._this['ctrltmplsemp'].getView().setModel(amModelFolder, "tplModel");
		}		
		//------------------------------------------

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},

	//--------------------------------------------------
	onCollapseExpandPress : function(evt) {
		var oNavigationList = sap.ui.getCore().byId(evt.getParameter("id"));
		var bExpanded = oNavigationList.getExpanded();
		oNavigationList.setExpanded(!bExpanded);
	},

	//--------------------------------------------------
	onDownloadPress : function(evt) {
		var bindingContext = evt.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var unikID = bindingContext.oModel.oData.documents[res[2]].employeeDocs[res[4]].id;
		var unikURL = bindingContext.oModel.oData.documents[res[2]].employeeDocs[res[4]].hash;
		var msg = this.getView().getModel("i18n").getResourceBundle().getText("person_Download_start");
		sap.m.MessageToast.show(msg);
		window.location.replace(this.getView().getModel("iConfig").oData.url + "api/v1/enterprise/documents/" + unikID + "/download/" + unikURL);

	},
	
	//--------------------------------------------------
	onNavBack : function(oEvent) {
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon2", "navCon");
		oNavCon.back();
	},
	onNavToProduct : function(oEvent) {
		var oItem, oCtx, sDayId;
		oItem = oEvent.getSource();
		oCtx = oItem.getBindingContext("tplModel");
		// var oCtx = oEvent.getSource().getBindingContext();
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon2", "navCon");
		var oDetailPage = sap.ui.core.Fragment.byId("popoverNavCon2", "detail");
		oNavCon.to(oDetailPage);

		oDetailPage.bindElement("tplModel>" + oCtx.getPath());

	},
	
	//--------------------------------------------------
	onNavToSelfTemplate : function(oEvent) {		
		var mod = oEvent.getSource().getBindingContext("tplModel");
		// create popover
		if (!this._oPopover) {
			this._oPopover = sap.ui.xmlfragment("popoverNavCon2", "generator.view.fragments.DialogSingleCustomTabRec", this);
			this.getView().addDependent(this._oPopover);
		}

		// delay because addDependent will do a async rerendering and
		// the popover will immediately close without it
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "perso") {
			return;
		}
		sap.ui.core.BusyIndicator.show(0);

		window.tempUserId = "null";
		if (evt.getParameter("arguments").userId) {
			window.tempUserId = evt.getParameter("arguments").userId;
			window.refreshviewTmplsEmp();
			window.refreshviewPerso();			
		} else {
			console.log("userId Arg perso " + window.tempUserId)
			window._this['ctrlperso'] = this;
			window.refreshviewTmplsEmp();
		}
	},

	//---------------------------------------------------
	handleListItemPress : function(oEvent) {
		// console.log("inside the HandleListItem " + oEvent);
		var context = oEvent.getSources().getBindingContext();
		this.nav.to("Detail", context);
	},

	//---------------------------------------------------
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
	
	//---------------------------------------------------
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},
	
	//---------------------------------------------------
	onDownPressed : function(oEvent) {
		var bindingContext = oEvent.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var unikID = bindingContext.oModel.oData.documents[res[2]].id;
		var unikURL = bindingContext.oModel.oData.documents[res[2]].hash;
		sap.m.MessageToast.show('Download started');
		window.location.replace(this.getView().getModel("iConfig").oData.url + "api/v1/enterprise/documents/" + unikID + "/download/" + unikURL);

	},
	
	//-----------------------------------------------------
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
	
	//--------------------------------------------------------
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

	//--------------------------------------------------------
	handleUploadPress : function(oEvent) {
		var oFileUploader = this.getView().byId("fileUploader");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show("Choose a file first");
			return;
		}
		oFileUploader.upload();
	},

	//--------------------------------------------------------
	handleTypeMissmatch : function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show("The file type *." + oEvent.getParameter("fileType") + " is not supported. Choose one of the following types: " + sSupportedFileTypes);
	},

	//--------------------------------------------------------
	handleValueChange : function(oEvent) {
		sap.m.MessageToast.show("Press 'Upload File' to upload file '" + oEvent.getParameter("newValue") + "'");
	},

	//--------------------------------------------------------
	handleIconTabBarSelect : function(oEvent) {
		sKey = oEvent.getParameter("key");
		
		if ("mydoc" === sKey) {
			sap.ui.core.BusyIndicator.show(0);
			window.refreshviewTmplsEmp();
			window.refreshviewPerso();			
		}
	},

	//------------------------------------------------------	
	onValidatedPressed : function(oEvent) {
		
		var mod = oEvent.getSource().getBindingContext("tplModel");
		var res = mod.sPath.split("/");
		console.log(mod.oModel.oData);
		idTemplate = mod.oModel.oData[res[1]].idTemplate;
		idExternal = mod.oModel.oData[res[1]].id;
		managerConfirm = mod.oModel.oData[res[1]].managerConfirm;
		
		if(managerConfirm!=null && managerConfirm == "Authorization"){
			this.handleManagerConfirm(idTemplate);	
		}
		else{
			this.handleGenerateDoc(idTemplate,idExternal);
		}		
	},
	
	
	//-----------------------------------------------------------
	handleGenerateDoc : function(idTemplate,idExternal) {

		sap.ui.core.BusyIndicator.show();
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		jQuery.sap.delayedCall(2000, this, function() {
			oDataModel2.create('/createSelfDocEmp/' + idTemplate + "/" + idExternal, null, null,

			function(oData, response) {
				var json = JSON.parse(response.body);				
				sap.m.MessageToast.show('Created');
				sap.ui.core.BusyIndicator.hide();				
				sap.m.MessageToast.show(i18n.getText("team_Created_document")+". Id: "+json.field+". "+json.message);				

			}, function() {
				sap.ui.core.BusyIndicator.hide();
				sap.m.MessageToast.show('Error creating the document');
			});
		});

	},

	// -------------------------------------------------
	onSearchTemplate : function(oEvt) {
		var sQuery = oEvt.getSource().getValue();
		// load folders
		if (sQuery.length > 2) {
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/searchtselfemplate/" + sQuery, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);

					amModelFolder.setData(json);
				},
				error : function(oData, response) {
				}
			});
			window._this['ctrltmplsemp'].amModelFolder = amModelFolder;
			sap.ui.getCore().setModel(amModelFolder, "modelFolder");
			window._this['ctrltmplsemp'].getView().setModel(amModelFolder, "modelFolder");
			this.byId("idTableTemplate").setVisible(true);
			this.byId("Tree").setVisible(false);
		}
		if (sQuery.length === 0) {
			window.refreshviewTmplsEmp();
		}
	},

	// -------------------------------------------------

	handleManagerConfirm : function(idTmp) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var amModelTmpAutho = new sap.ui.model.json.JSONModel();
		var oDataModelTmpAutho = new sap.ui.model.odata.ODataModel("/generator/rst/json/", true);
		oDataModelTmpAutho.read("/template/getauth/" + idTmp, {
			sync : false,
			success : function(oData, response) {
				if (response.body != "") {
					var json = JSON.parse(response.body);
					console.log("json.code "+json.code)
					if (json.code === "true")
					{
						var dialog = new sap.m.Dialog({
							title : i18n.getText("global_Send"),
							type : 'Message',
							content : [new sap.m.Label({
								text : i18n.getText("person_Send_manager_confirm"),
								labelFor : 'submitDialogSelect13'
							})

							],
							beginButton : new sap.m.Button({
								text : i18n.getText("global_Submit"),
								press : function() {
									console.log("+++***+++" + idTmp);									
									var amModelTmpAutho = new sap.ui.model.json.JSONModel();
									var oDataModelTmpAutho = new sap.ui.model.odata.ODataModel("/generator/rst/json/", true);
									oDataModelTmpAutho.read("/template/auth/" + idTmp, {
										sync : false,
										success : function(oData, response) {
											sap.m.MessageToast.show(i18n.getText("Authorization")+" "+i18n.getText("global_Create"));											
										},
										error : function(oData, response) {
											sap.m.MessageToast.show(i18n.getText("managerAuth_error_1001"));
											console.log(oData);
											console.log("Error incial");
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
					else if (json.code === "false")
					{						
						sap.m.MessageToast.show(i18n.getText("managerAuth_error_1002"));
					}

					if (json.code != "" && json.code != "true") {
						sap.m.MessageToast.show(i18n.getText("template_Authorization") + ' ' + json.status);
					}
				}
				window.refreshviewTmplsEmp();
			},
			error : function(oData, response) {
			}
		});

	},
})
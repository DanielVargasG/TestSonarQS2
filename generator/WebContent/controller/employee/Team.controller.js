sap.ui.controller("generator.controller.employee.Team", {

	onInit : function() {

		//sap.ui.core.BusyIndicator.show();

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window.temprouter = this._oRouter;
		window.back = "team";
		window.stsActive = true;
		window.myself = false;
		

		// load documents types
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

		this.getView().setModel(amModelDocType, "modelPathDocType");

		var idTab = this.getView().byId('idTab');
		var amModelManager = new sap.ui.model.json.JSONModel();
		var oDataModelManager = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelManager.read("/managerrole/getallUser" ,{
			sync : false,
			success : function(oData, response) {
				
				if(response.body != null && response.body !==""){
					var json = JSON.parse(response.body);
					
					for(i in json){
						console.log("/***/"+json[i].namesource);
						var item = new sap.m.IconTabFilter(json[i].namesource,{
							text:json[i].description,
							icon:json[i].icon
						});
						idTab.addItem(item);
					}
				}
			},
			error : function(oData, response) {
			}
		});
		
		

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "team") {
			return;
		}

		var amModel2 = new sap.ui.model.json.JSONModel();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.read("/templates", {
			sync : false,
			success : function(oData, response) 
			{
				var json = JSON.parse(response.body);
				var i;
				
				for(i=0; i<json.length; i++)
				{
					if(json[i].module!=null && json[i].module=="EMC")
					{	
						if(((json[i].formatGenerated == "")||(json[i].formatGenerated === null)))
						{
							json[i].formatGenerated = "PDF";
							amModel2.setData(json);
						}
					}
					else
					{
						console.log(json.splice(i, 1));
					}
				}
				
				amModel2.setSizeLimit(json.length);
				amModel2.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		this.getView().setModel(amModel2, "tplModel");

		// console.log('on est de retour');
	},
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},
	handleTeamListItemSelect : function(oEvent) {

		var mod = oEvent.getSource().getBindingContext("teamModel");
		var res = mod.sPath.split("/");

		window.tempUser = mod.oModel.oData[res[1]].userId;

		// create popover
		if (this._oPopover) {
			this._oPopover.destroy();
		}

		this._oPopover = sap.ui.xmlfragment("popoverNavCon", "generator.view.fragments.DialogSingleCustomTab", this);
		this.getView().addDependent(this._oPopover);

		// delay because addDependent will do a async rerendering and
		// the popover will immediately close without it
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},
	handleTeamHRListItemSelect : function(oEvent) {

		var mod = oEvent.getSource().getBindingContext("teamManagerModel");
		var res = mod.sPath.split("/");

		window.tempUser = mod.oModel.oData[res[1]].userId;

		// create popover
		if (this._oPopover) {
			this._oPopover.destroy();
		}

		this._oPopover = sap.ui.xmlfragment("popoverNavCon", "generator.view.fragments.DialogSingleCustomTab", this);
		this.getView().addDependent(this._oPopover);
		// delay because addDependent will do a async rerendering and
		// the popover will immediately close without it
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},

	onTplClick : function(oEvent) {

		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon", "navCon");
		var oHomePage = sap.ui.core.Fragment.byId("popoverNavCon", "master");
		oNavCon.to(oHomePage);
	},
	
	onViewDocuments : function(oEvent) {
		this._oRouter.navTo("perso",{userId : window.tempUser});
	},	

	handleTeamSearchListItemSelect : function(oEvent) {

		var mod = oEvent.getSource().getBindingContext("teamSearchModel");
		var res = mod.sPath.split("/");

		window.tempUser = mod.oModel.oData[res[1]].userId;

		// create popover
		if (!this._oPopover) {
			this._oPopover = sap.ui.xmlfragment("popoverNavCon", "generator.view.fragments.DialogSingleCustomTab", this);
			this.getView().addDependent(this._oPopover);
		}

		// delay because addDependent will do a async rerendering and
		// the popover will immediately close without it
		var oButton = oEvent.getSource();
		jQuery.sap.delayedCall(0, this, function() {
			this._oPopover.openBy(oButton);
		});
	},

	onNavToProduct : function(oEvent) {
		var oItem, oCtx, sDayId;
		oItem = oEvent.getSource();
		oCtx = oItem.getBindingContext("tplModel");
		// var oCtx = oEvent.getSource().getBindingContext();
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon", "navCon");
		var oDetailPage = sap.ui.core.Fragment.byId("popoverNavCon", "detail");
		oNavCon.to(oDetailPage);
		
		oDetailPage.bindElement("tplModel>" + oCtx.getPath());
		var date=new Date();
		window.date = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
		
		sap.ui.core.Fragment.byId("popoverNavCon", "effectiveDate").setValue(window.date);

	},
	onValidatedPressed : function(oEvent) {
		// alert("oui");

		sap.ui.core.BusyIndicator.show();

		var mod = oEvent.getSource().getBindingContext("tplModel");
		var res = mod.sPath.split("/");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		// window.tempUser = mod.oModel.oData[res[1]].userNav.userId;

		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.create('/createDocument/' + mod.oModel.oData[res[1]].idTemplate + "/" + mod.oModel.oData[res[1]].id + "/" + window.tempUser + "/"+window.date, null, null,

		function(oData, response) {
			var json = JSON.parse(response.body);
			// oTable.getModel().oData.push(json);
			var msg = i18n.getText("global_Created");
			sap.m.MessageToast.show(msg);
			window.tempUser = null;
			sap.ui.core.BusyIndicator.hide();
			window.temprouter.navTo("docs", {
				id : json.id
			});

		}, function() {
			sap.ui.core.BusyIndicator.hide();
			var msg = i18n.getText("team_Created_error_document");
			sap.m.MessageToast.show(msg);
		});

		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon", "navCon");
		oNavCon.back();

		this._oPopover.close();

	},

	onNavBack : function(oEvent) {
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon", "navCon");
		oNavCon.back();
	},
	onNavBack2 : function(oEvent) {
		var oNavCon = sap.ui.core.Fragment.byId("popoverNavCon", "navCon");
		oNavCon.back();
	},
	onSearch : function(oEvt) {

		if (this.odata)
			this.odata.fireAnnotationsFailed();
		// add filter for search
		var aFilters = [];
		var sQuery = oEvt.getSource().getValue();

		if (sQuery.length > 2) {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/user/search/" + sQuery + "/" + window.stsActive, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					sap.ui.core.BusyIndicator.hide();
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});

			this.getView().setModel(amModel, "teamSearchModel");
			this.odata = oDataModel;
		}
	},

	// ---------------------------------------------------------

	onAddFile : function(oEvent) {
		console.log(oEvent);
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectCategory = new sap.ui.core.Item({
			key : "{modelPathDocType>id}",
			text : "{modelPathDocType>label}"
		});

		if (!this.resizableDialog) {
			var oItemSelectCategory = new sap.ui.core.Item({
				key : "{modelPathDocType>id}",
				text : "{modelPathDocType>label}"
			});

			this.resizableDialog = new sap.m.Dialog("dial1", {
				title : i18n.getText("team_Upload_file"),
				contentWidth : "420px",
				contentHeight : "300px",

				resizable : true,

				content : [new sap.m.Label({
					text : '',
					width : '100%'
				}), new sap.m.Label({
					text : i18n.getText("team_Document_type"),
					width : '90%'
				}), new sap.m.Select('submitDialogSelectFile_' + window.tempUser, {
					width : '90%',

				}).bindAggregation("", {
					path : "modelPathDocType>/",
					template : oItemSelectCategory
				}), new sap.m.Label({
					text : '',
					width : '100%'
				}), new sap.m.Label({
					text : i18n.getText("team_Document_attachment"),
					width : '90%'
				}), new sap.ui.unified.FileUploader('idFileUpload', {
					name : 'uploadFile',
					sendXHR : true,
					uploadComplete : function(oEvent) {

						// Function Uploader
						console.log("FileUploader ");
						var sResponse = oEvent.getParameter("response");
						console.log("response " + sResponse)
						if (sResponse) {
							var sMsg = "";
							var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
							console.log(sResponse);
							if (sResponse == "ok") {
								sMsg = i18n.getText("global_Upload_sucess");
								oEvent.getSource().setValue("");
								this.resizableDialog.close();

							} else {
								sMsg = "" + "Upload Error" + "";
							}
							sap.m.MessageToast.show(sMsg, {
								duration : 4000,
								width : "20em",
								my : "center bottom",
								animationTimingFunction : "ease-out",
								animationDuration : 1000,
							});
						} else {
							sMsg = i18n.getText("global_Upload_sucess");
							oEvent.getSource().setValue("");
							// window.refreshviewTmplD();
							this.resizableDialog.close();
							sap.m.MessageToast.show(sMsg);
						}

					}.bind(this)
				})

				],
				beginButton : new sap.m.Button({
					text : i18n.getText("global_Save"),
					press : function(oEvent) {

						// Upload File
						// var oFileUploader = content;
						var oFileUploader = sap.ui.getCore().byId("idFileUpload");
						console.log(oFileUploader);
						if (!oFileUploader.getValue()) {
							sap.m.MessageToast.show(i18n.getText("global_Choose_file_first"));
							return;
						}
						var idType = sap.ui.getCore().byId("submitDialogSelectFile_" + window.tempUser);
						console.log("--- " + idType.getSelectedKey());
						oFileUploader.setUploadUrl("/generator/rst/json/generated/addfile/" + window.tempUser + "/" + idType.getSelectedKey());
						oFileUploader.upload();

					}.bind(this)
				}),
				endButton : new sap.m.Button({
					text : i18n.getText("global_Close"),
					press : function() {

						// /
						this.resizableDialog.close();
					}.bind(this)
				})
			});

			// to get access to the global model
			this.getView().addDependent(this.resizableDialog);
		}

		this.resizableDialog.open();

	},
	handleLinkObjectAttributePress : function(oEvt) {
		sap.ui.core.BusyIndicator.show();
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		if (window.stsActive) {
			this.byId("actID").setText(i18n.getText("team_View_active_users"));
			window.stsActive = false;
		} else {
			this.byId("actID").setText(i18n.getText("team_Include_inactive_users"));
			window.stsActive = true;
		}
		var idTab = this.getView().byId('idTab').getSelectedKey();
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		console.log(idTab);
		if(idTab.endsWith("all")){
			idTab ="all";
		}
		oDataModel.read("/user/teamHR/" + window.stsActive+"/"+idTab, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setData(json);
				amModel.setSizeLimit(json.length);
				sap.ui.core.BusyIndicator.hide();
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});

		this.getView().setModel(amModel, "teamModel");
		
		

	},

	onSearchTemplate : function(oEvt) {

		var aFilters = [];
		var sQuery = oEvt.getSource().getValue();
		if (sQuery && sQuery.length > 0) {
			var filter = new sap.ui.model.Filter("title", sap.ui.model.FilterOperator.Contains, sQuery);
			aFilters.push(filter);
		}
		// update list binding sap.ui.core.Fragment.byId
		var list = sap.ui.core.Fragment.byId("popoverNavCon", "idList");
		var binding = list.getBinding("items");
		binding.filter(aFilters, "Application");
	},
	
	changeDate: function(){
		var date = sap.ui.core.Fragment.byId("popoverNavCon", "effectiveDate").getDateValue();
		window.date = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
	},
	
	onVersionPressed : function(oEvent){
		sap.ui.core.BusyIndicator.show();
		console.log("/***/"+this.getView().byId('idTab').getSelectedKey());
		var idTab = this.getView().byId('idTab').getSelectedKey();
		var tabFilter = sap.ui.getCore().byId(idTab);
		var list = this.getView().byId("list");
		tabFilter.addContent(list);
		console.log(tabFilter); 
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/user/teamHR/" + window.stsActive+"/"+idTab, {
			sync : false,
			success : function(oData, response) {
				
				if(response.body !=null && response.body !== ""){
					var json = JSON.parse(response.body);
					amModel.setData(json);
					amModel.setSizeLimit(json.length);
				}
				
				sap.ui.core.BusyIndicator.hide();
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});

		this.getView().setModel(amModel, "teamModel");
	}

});
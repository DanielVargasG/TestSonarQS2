sap.ui.controller("generator.controller.template.Templatesemployee", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		window.temprouter = this._oRouter;

		window._this['ctrltmplsemp'] = this;
		window.back = "templatesemployee";
		window.myself = true;
		window.refreshviewTmplsEmp = function() {

			
			// load folders
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/searchtselfemplate/"+"all", {
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
			
			}

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},

	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "templatesemployee") {
			return;
		}

		window._this['ctrltmplsemp'] = this;

		window.refreshviewTmplsEmp();

	},

	// -------------------------------------------------
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},

	// -------------------------------------------------
	
	onCancel : function(oEvent) {
		this.oDataBeforeOpen = {};
		oEvent.getSource().close();
	},

	
	handleEditPress : function(event) {
		console.log("Edit folder");

		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		//folder = bindingContext.oModel.oData[res[1]];

		var idEvent = window._this['ctrltmplsemp'].getView().getModel("modelFolder").getProperty("title", bindingContext);
		var idFolder = window._this['ctrltmplsemp'].getView().getModel("modelFolder").getProperty("id", bindingContext);

		var dialog = new sap.m.Dialog({
			title : 'Update Folder',
			type : 'Message',
			content : [new sap.m.Label({
				text : 'Title of the folder',
				labelFor : 'submitDialogTitle'
			}), new sap.m.Input('submitDialogTitle', {
				value : idEvent,
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();

					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				width : '100%',
				placeholder : 'Add title (required)'
			}), ,

			],
			beginButton : new sap.m.Button({
				text : 'Submit',
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
						var oTable = sap.ui.getCore().getElementById(window._this['ctrltmplsemp'].createId("Tree"));
						var oModel = window._this['ctrltmplsemp'].amModelFolder;
						console.log(json);

						oTable.getModel("modelFolder").setData(json);
						sap.m.MessageToast.show('Folder Update');
						oModel.refresh(true);
					}, function() {
						sap.m.MessageToast.show('Error update Folder Template');
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

	handleDeletePress : function(event) {
		console.log("delete");

		var bindingContext = event.getSource().getBindingContext("modelFolder");
		var res = bindingContext.sPath.split("/");

		//folder = bindingContext.oModel.oData[res[1]];
		var idFolder = window._this['ctrltmplsemp'].getView().getModel("modelFolder").getProperty("id", bindingContext);

		var dialog = new sap.m.Dialog({
			title : 'Confirm',
			type : 'Message',
			content : new sap.m.Text({
				text : 'Are you sure you want to delete the folder and all its contents ?'
			}),
			beginButton : new sap.m.Button({
				text : 'Submit',
				press : function() {
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.remove('/folders/' + idFolder, null, function(oData, response) {

						sap.m.MessageToast.show('Folder Deleted');

						var json = JSON.parse(response.body);
						console.log(json);
						window._this['ctrltmplsemp'].amModelFolder.setData(json);

					}, function() {
						sap.m.MessageToast.show('Error deleteing folder');
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

	// -------------------------------------------------
	handleColumnListItemSelect : function(event) {
		
		
		sap.ui.core.BusyIndicator.show();

		var mod = event.getSource().getBindingContext("modelFolder");
		var res = mod.sPath.split("/");

		// window.tempUser = mod.oModel.oData[res[1]].userNav.userId;

		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		jQuery.sap.delayedCall(2000, this, function () {
			oDataModel2.create('/createDocumentEmp/' + mod.oModel.oData[res[1]].idTemplate + "/" + mod.oModel.oData[res[1]].id , null, null,

					function(oData, response) {
						var json = JSON.parse(response.body);
						// oTable.getModel().oData.push(json);
						sap.m.MessageToast.show('Created');
						sap.ui.core.BusyIndicator.hide();
						window.temprouter.navTo("docs", {
							id : json.id
						});

					}, function() {
						sap.ui.core.BusyIndicator.hide();
						sap.m.MessageToast.show('Error creating the document');
					});
		});
		
	},
	
	// -------------------------------------------------
	
	onSearchTemplate: function(oEvt){
		var sQuery = oEvt.getSource().getValue();
		// load folders
		if(sQuery.length > 2){
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.read("/searchtselfemplate/"+sQuery, {
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
		if(sQuery.length === 0){
			window.refreshviewTmplsEmp();
		}
	}
	
	})
sap.ui.controller("generator.controller.home.Home", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlhome'] = this;
		window.refreshviewHome = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("user/count/pending", {
				sync : true,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setData({
						'businessData' : {
							'Status' : "Success",
							'Count' : json.count,
							'CountSign' : json.count_sign,
						}
					});

					/*window._this['ctrlhome'].T1 = new sap.m.StandardTile({
						icon : "sap-icon://basket",
						title : "{i18n>global_Mydocument}",
						press : function() {
							window._this['ctrlhome'].onPressMyDoc();
						},
						visible : "{iConfig>/seesPerso}"
					});

					window._this['ctrlhome'].T2 = new sap.m.StandardTile({
						icon : "sap-icon://signature",
						title : "{i18n>home_Signature}",
						press : function() {
							window._this['ctrlhome'].onPressMySign();
						},
						visible : "{iConfig>/hasTemplates}"
					});

					window._this['ctrlhome'].T3 = new sap.m.StandardTile({
						icon : "sap-icon://restart",
						title : "{i18n>home_PendingDocs}",
						press : function() {
							window._this['ctrlhome'].onPressDocuments();
						},
						number : "{/businessData/Count}",
						numberUnit : "docs",
						info : "{i18n>home_Pending}",
						infoState : "{/businessData/Status}",
						visible : "{iConfig>/seesTemplates}"
					});

					window._this['ctrlhome'].T4 = new sap.m.StandardTile({
						icon : "sap-icon://company-view",
						title : "{i18n>home_Generate_document_for_team}",
						press : function() {
							window._this['ctrlhome'].onPressTeam();
						},
						number : "{iConfig>/countMng}/{iConfig>/countHr}",
						numberUnit : "{i18n>home_Reports}",
						visible : "{iConfig>/seesTeamTemplate}"
					});

					window._this['ctrlhome'].T5 = new sap.m.StandardTile({
						icon : "sap-icon://company-view",
						title : "{i18n>home_Get_my_team}",
						press : function() {
							window._this['ctrlhome'].onPressTeam();
						},
						number : "{iConfig>/countMng}/{iConfig>/countHr}",
						numberUnit : "{i18n>home_Reports}",
						visible : "{iConfig>/seesTeamNoTemplate}"
					});

					window._this['ctrlhome'].T6 = new sap.m.StandardTile({
						icon : "sap-icon://employee-pane",
						title : "{i18n>home_Recruiting}",
						press : function() {
							window._this['ctrlhome'].onPressRecruiting();
						},
						visible : "{iConfig>/seesRecruiting}"
					});

					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T1);
					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T2);
					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T3);
					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T4);
					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T5);
					window._this['ctrlhome'].getView().byId("container").addTile(window._this['ctrlhome'].T6);*/

					if (window.configModel.getData().administrator)
						window._this['ctrlhome'].getView().byId("adminHome").setVisible(true);

					
					window._this['ctrlhome'].getView().byId("container").setVisible(true);
					sap.ui.core.BusyIndicator.hide();
				},
				error : function(oData, response) {
					sap.ui.core.BusyIndicator.hide();
					sap.m.MessageToast.show('Error Getting pending document count');
				}
			});
			window._this['ctrlhome'].getView().setModel(amModel);

		}

		window._this['ctrlhome']._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

	},

	_handleRouteMatched : function(evt) {

		console.log(evt.getParameter("name"));
		/*if (window._this['ctrlhome'].T1) {
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T1);
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T2);
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T3);
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T4);
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T5);
			window._this['ctrlhome'].getView().byId("container").deleteTile(window._this['ctrlhome'].T6);
		}*/
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "main") {
			return;
		}
		window._this['ctrlhome'].getView().byId("container").setVisible(false);
		
		sap.ui.core.BusyIndicator.show(0);
		window._this['ctrlhome'] = this;

		if (!window.configModel.getData().path) {
			console.log("null");
			window.configModel.attachRequestCompleted(function() {
				window.refreshviewHome();
			});

		} else {
			window.refreshviewHome();
		}

	},
	onPressAdmin : function(oEvent) {
		this._oRouter.navTo("administration");
	},
	onPressSignature : function(oEvent) {
		this._oRouter.navTo("perso", {
			userId : "null"
		});
	},
	onPressPending : function() {
		this._oRouter.navTo("documents");
	},

	onPressMyDoc : function() {
		this._oRouter.navTo("perso", {
			userId : "null"
		});
	},
	onPressDocuments : function() {
		this._oRouter.navTo("documents");
	},
	onPressTeam : function() {
		this._oRouter.navTo("team");
	},
	onPressTemplates : function() {
		this._oRouter.navTo("templates");
	},
	onPressLogger : function() {
		this._oRouter.navTo("logger");
	},
	onPressRecruiting : function() {
		this._oRouter.navTo("recruiting");
	},
	onPressMySign : function() {
		this._oRouter.navTo("signature");
	},
	onPressTemplateEmployee : function() {
		this._oRouter.navTo("templatesemployee");
	},
	onPressDocArchive : function() {
		this._oRouter.navTo("Documentarchive");
	}
})
sap.ui.controller("generator.view.Administration.EventListenerCtrlHistoAttach", {

	// -------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	// -------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		console.log(evt.getParameter("name"));
		if (evt.getParameter("name") !== "/adm/evenlisterctrAttachHisto") {
			return;
		}

		var varId = evt.getParameter("arguments").idCtrlH;
		window.idEventHisto = varId;
		window.refreshview = this.refreshView();
	},
	// -------------------------------------------------
	// Refresh view
	refreshView : function() {
		console.log("ok");

		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrlHistoAttach/" + window.idEventHisto, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				console.log(json);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});

		this.getView().setModel(amModel);

		window.objectTTT = this.getView().byId("idPrincipalTable");
		this.getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);

	},
	onObservationPress : function(oEvent) {
		var ObservationValue = this.getView().getModel().getProperty("observations", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		
		var find = 'END_ ';
		var re = new RegExp(find, 'g');
		var res = ObservationValue.replace(re, "<br/><br/>");
		
		var find = 'END_';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "<br/>");
		
		var find = '_INIT';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "");
		
		var find = '\&quot;';
		var re = new RegExp(find, 'g');
		res = res.replace(re, " ");
		
		var find = '{';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "(");
		
		var find = '}';
		var re = new RegExp(find, 'g');
		res = res.replace(re, ")");

		var dialog = new sap.m.Dialog({
			contentWidth : '50%',
			contentHeight : '40%',
			title: i18n.getText("global_Observations"),
			content : [new sap.ui.core.HTML({
				content : "<p style='margin:20px; font-size:14px;'>" + res + "</p>",
				editable : false,
				width : "100%",
				growing : true,
				maxLength : 2000,
				rows : 2000,
				height : '90%',
				growingMaxLines : 100,
				growing : true,
			})],

			endButton : new sap.m.Button({
				text : 'Close',
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
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("/adm/evenlisterctr");
	},

// -------------------------------------------------
// Delete event listener
// -------------------------------------------------
// Edit Event Listener
// -------------------------------------------------
// Add Event listener
// -------------------------------------------------
})
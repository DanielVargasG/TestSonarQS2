jQuery.sap.require("sap.ui.core.routing.Router");
jQuery.sap.require("sap.m.routing.RouteMatchedHandler");
jQuery.sap.declare("generator.MyRouter");

sap.ui.core.routing.Router.extend("generator.MyRouter", {
	constructor : function() {
		sap.ui.core.routing.Router.apply(this, arguments);
		this.oRouteMatchedHandler = new sap.m.routing.RouteMatchedHandler(this);
	},
	myNavBack : function(sRoute, mData) {
		var oHistory = sap.ui.core.routing.History.getInstance();
		var sUrl = this.getURL(sRoute, mData);
		var sDirection = oHistory.getDirection(sUrl);
		if ("Backwards" === sDirection) {
			window.history.go(-1);
		} else {
			var bReplace = true;
			this.navTo(sRoute, mData, bReplace)
		}
	},
	myNavToWithoutHash : function(viewName, viewType, master, data) {
		var oSplitApp = sap.ui.getCore().byId("splitApp");
		var oView = this.getView(viewName, viewType);
		oSplitApp.addPage(oView, master);
		oSplitApp.to(oView.getId(), "show", data);
	},
});
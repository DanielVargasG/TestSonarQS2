sap.ui.jsview("generator.view.App", {
	getControllerName : function() {
		return "generator.controller.App";
	},

	createContent : function(oController) {
		this.setDisplayBlock(true);

		this.app = new sap.m.App({
			id : "App"
		});

		return new sap.m.Shell("app-generator-ppdoc-shell", {
			title : "",
			showLogout : false,
			app : this.app
		})
	}
})
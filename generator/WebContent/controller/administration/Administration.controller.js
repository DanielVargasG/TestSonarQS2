sap.ui.controller("generator.controller.administration.Administration", {

	onInit : function() {

		window._this['ctrlAdm'] = this;
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['ctrlAdm']._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	_handleRouteMatched : function(evt) {
		if (evt.getParameter("name") !== "administration") {
			return;
		}
		if (!window.configModel.getData().path) {
			window.configModel.attachRequestCompleted(function() {
				if (!window.configModel.getData().administrator)
					window._this['ctrlAdm']._oRouter.myNavBack("main");
			});
		} else {
			if (!window.configModel.getData().administrator)
				window._this['ctrlAdm']._oRouter.myNavBack("main");
		}
	},
	onNavPressed : function() {
		this._oRouter.myNavBack("main");
	},
	onPressEventMenu : function() {
		this._oRouter.navTo("administrationEvent");
	},
	onPressAttachmentMenu : function() {
		this._oRouter.navTo("administrationAttachment");
	},

	onPressLoadUser : function() {
		this._oRouter.navTo("massiveLoadUser");
	},
	onPressLoadUser : function() {
		this._oRouter.navTo("massiveLoadUser");
	},
	onPressEventMapping : function() {
		this._oRouter.navTo("administrationMapping");
	},
	onPressEventDataInit : function() {
		this._oRouter.navTo("administrationData");
	},
	onPressEventAdmExpImpData : function() {
		this._oRouter.navTo("adm/ExpImpData");
	},
	onPressEventAdmControlPanel : function() {
		this._oRouter.navTo("adm/ControlPanel");
	},
	onPressEventLogger : function() {
		this._oRouter.navTo("logger");
	},
	onPressAdmDocument : function() {
		this._oRouter.navTo("administrationDocument");
	},
})
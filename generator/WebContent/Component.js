var pathname = window.location.pathname;
if (pathname.substring(0, 6) == "/sites") {
	pathname = "/sap/fiori/ppldoc/"
}
window.custompath = pathname;
jQuery.sap.registerModulePath("generator", "/generator");
jQuery.sap.require("generator.routes.MyRouter");
sap.ui.core.UIComponent.extend("generator.Component", {
	metadata : {
		routing : {
			config : {
				routerClass : generator.MyRouter,
				viewType : "XML",
				viewPath : "generator.view",
				controlId : "App",
				targetControl : "App",
				clearTarget : false,
				controlAggregation : "pages",
				transition : "slide",
			},
			routes : [{
				pattern : "",
				name : "main",
				view : "home.Home",
				targetAggregation : "pages",
			}, {
				pattern : "logger",
				name : "logger",
				view : "logger.Logger",
				targetAggregation : "pages",
			}, {
				pattern : "recruiting",
				name : "recruiting",
				view : "recruiting.Recruiting",
				targetAggregation : "pages",
			}, {
				pattern : "administration",
				name : "administration",
				view : "Administration.Administration",
				targetAggregation : "pages",
			}, {
				pattern : "administrationAttachment",
				name : "administrationAttachment",
				view : "Menu/AdministrationAttachment",
				targetAggregation : "pages",
			}, {
				pattern : "administrationEvent",
				name : "administrationEvent",
				view : "Administration.AdministrationEvent",
				targetAggregation : "pages",
			}, {
				pattern : "administrationData",
				name : "administrationData",
				view : "Menu/AdministrationData",
				targetAggregation : "pages",
			}, {
				pattern : "administrationMapping",
				name : "administrationMapping",
				view : "Menu/AdministrationMapping",
			}, {
				pattern : "administration/evenlisterctrattach/{idCtrl}",
				name : "/adm/evenlisterctrattach",
				view : "Administration/EventListenerCtrlAttach",
			}, {
				pattern : "administration/evenlisterctr_histo_attach/{idCtrlH}",
				name : "/adm/evenlisterctrAttachHisto",
				view : "Administration/EventListenerCtrlHistoAttach",
			}, {
				pattern : "administration/evenlisterctr",
				name : "/adm/evenlisterctr",
				view : "Administration/EventListenerCtrlHisto",
			}, {
				pattern : "administration/evenlisterctrprocess",
				name : "/adm/evenlisterctrprocess",
				view : "Administration/EventListenerCtrlProcess",
			}, {
				pattern : "administration/synch",
				name : "/adm/synch",
				view : "Administration/synch/Synch",
			}, {
				pattern : "administration/synchhisto",
				name : "/adm/synchhisto",
				view : "Administration/synch/SynchHisto",
			}, {
				pattern : "administration/events",
				name : "/adm/events",
				view : "Administration/Events",
			}, {
				pattern : "administration/mappingfieldppd",
				name : "/adm/mappingfieldppd",
				view : "Administration/MappingFieldPpd",
			}, {
				pattern : "administration/mappingroleppd",
				name : "/adm/mappingroleppd",
				view : "Administration/MappingRolePpd",
			}, {
				pattern : "administration/mappingtempfield",
				name : "/adm/mappingtempfield",
				view : "Administration/MappingTempField",
			}, {
				pattern : "administration/admParameter",
				name : "/adm/admParameter",
				view : "Administration/AdmParameter",
			}, {
				pattern : "administration/structure",
				name : "/adm/structure",
				view : "Administration/Structure",
			}, {
				pattern : "perso/{userId}",
				name : "perso",
				view : "employee.Perso",
				targetAggregation : "pages",
			}, {
				pattern : "templates",
				name : "templates",
				view : "template.Templates",
				targetAggregation : "pages",
			}, {
				pattern : "templatesemployee",
				name : "templatesemployee",
				view : "template.Templatesemployee",
				targetAggregation : "pages",
			}, {
				pattern : "team",
				name : "team",
				view : "employee.Team",
				targetAggregation : "pages",
			}, {
				pattern : "templates/{id}",
				name : "templatesdetails",
				view : "template.Templatesdetails",
				targetAggregation : "pages",

			}, {
				pattern : "documents",
				name : "documents",
				view : "document.Pending",
				targetAggregation : "pages",
			}, {
				pattern : "documents/{id}",
				name : "docs",
				view : "document.DocumentDetails",
			}, {
				pattern : "admExpImpData",
				name : "adm/ExpImpData",
				view : "Administration/EventAdmExpImpData",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "admControlPanel",
				name : "adm/ControlPanel",
				view : "Administration/ControlPanel",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "country",
				name : "adm/Country",
				view : "Administration/Country",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "lookup",
				name : "adm/Lookup",
				view : "Administration/Lookup",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "signature",
				name : "signature",
				view : "employee/Sign",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "mappingsignature",
				name : "/adm/mappingsignature",
				view : "Administration/MappingSignaturesLib",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "language",
				name : "adm/Language",
				view : "Administration/Language",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "Documentarchive",
				name : "Documentarchive",
				view : "document/DocumentArchive",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "administrationDocument",
				name : "administrationDocument",
				view : "Menu/AdministrationDocument",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "managerRole",
				name : "managerRole",
				view : "Administration/ManagerRole",
				viewType : "XML",
				targetAggregation : "pages",
			}, {
				pattern : "massiveLoadUser",
				name : "massiveLoadUser",
				view : "Administration/MassiveLoadUser",
				viewType : "XML",
				targetAggregation : "pages"
			}, {
				pattern : "massiveLoadUserDet/{idMass}",
				name : "massiveLoadUserDet",
				view : "Administration/MassiveLoadUserDet",
				viewType : "XML",
				targetAggregation : "pages"
			}, {
				pattern : "massiveLoadUserDoc/{idUser}/{idMass}/{idCtrlHisto}",
				name : "massiveLoadUserDoc",
				view : "Administration/MassiveLoadUserDoc",
				viewType : "XML",
				targetAggregation : "pages"
			}]
		},
		includes : [window.custompath + "css/style.css"]
	},
	init : function() {

		jQuery.sap.require("generator.MyRouter");
		jQuery.sap.require("sap.m.routing.RouteMatchedHandler");

		sap.ui.core.UIComponent.prototype.init.apply(this, arguments);
		var router = this.getRouter();

		this.routeHandler = new sap.m.routing.RouteMatchedHandler(router);

		router.initialize();

	},
	destroy : function() {
		if (this.routeHandler) {
			this.routeHandler.destroy();
		}
		sap.ui.core.UIComponent.prototype.destroy.apply(this.arguments);
	},
	createContent : function() {

		window._this = new Array();

		var pathname = window.location.pathname;
		if (pathname.substring(0, 6) == "/sites") {
			pathname = "/sap/fiori/ppldoc/"
		}

		window.custompath = pathname;

		var oView = sap.ui.view({
			id : "app-generator-ppdoc",
			viewName : "generator.view.App",
			type : "JS",
			viewData : {
				component : this
			}
		});

		window.configModel = new sap.ui.model.json.JSONModel(pathname + "rst/json/admin/uiConfig");

		window.configModel.attachRequestCompleted(function() {
			configModel.getData().path = window.custompath;
			oView.setModel(configModel, "iConfig");
			
			console.log(configModel);

			//sap.ui.getCore().getConfiguration().setLanguage("fr_FR");
			sap.ui.getCore().getConfiguration().setLanguage(configModel.oData.defaultLanguage);
			console.log("Language " + sap.ui.getCore().getConfiguration().getLanguage());

		});

		var i18nModel = new sap.ui.model.resource.ResourceModel({
			bundleUrl : pathname + "i18n/messageBundle.properties",
		});

		var deviceModel = new sap.ui.model.json.JSONModel({
			isPhone : sap.ui.Device.system.phone,
			listMode : (sap.ui.Device.system.phone) ? "None" : "SingleSelectMaster",
			listItemType : (sap.ui.Device.system.phone) ? "Active" : "Inactive"
		});
		deviceModel.setDefaultBindingMode("OneWay");

		oView.setModel(i18nModel, "i18n");
		oView.setModel(deviceModel, "device");

		return oView;
	},
});
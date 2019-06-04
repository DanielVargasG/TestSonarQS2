jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.view.Administration.EventAdmExpImpData", {
	
	
	onInit : function() {
		console.log("IN");
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		
		window._this['ctrlEvents'] = this;
		window.refreshviewEvents = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			
			

		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		
		/*
		 * if (evt.getParameter("name") !== "/adm/events") { return; }
		 */
		console.log("metodo Handle route");
		
		window.refreshviewEvents();
		
	},
	
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationData");
	},
	
	onPressExport:function(oEvent) {
		
		var idSelect = this.getView().byId("selectId").getSelectedKey();
		var text = this.getView().byId("selectId").getSelectedItem().getText();
		var title = text.split(" ").join("");
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		console.log(idSelect);
		// console.log("-> ",item.selectedKey);
		oDataModel.read("admin/exportdata/"+idSelect, {
			
			sync : false,
			success : function(oData, response) {
				console.log("Hi ... ");
				var json = JSON.parse(response.body);
				console.log(json);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				
				var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(json));
			    var downloadAnchorNode = document.createElement('a');
			    downloadAnchorNode.setAttribute("href",     dataStr);
			    downloadAnchorNode.setAttribute("download", title + ".json");
			    downloadAnchorNode.click();
			    downloadAnchorNode.remove();
				
			},
			error : function(oData, response) {
			}
		});
	},
	onPressExportTable:function(oEvent) {
		
		var idSelect = this.getView().byId("selectIdTable").getSelectedKey();
		var text = this.getView().byId("selectIdTable").getSelectedItem().getText();
		var title = text.split(" ").join("");
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		console.log(idSelect);
		// console.log("-> ",item.selectedKey);
		oDataModel.read("admin/exportdata/"+idSelect, {
			
			sync : false,
			success : function(oData, response) {
				console.log("Hi ... ");
				var json = JSON.parse(response.body);
				console.log(json);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				
				var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(json));
			    var downloadAnchorNode = document.createElement('a');
			    downloadAnchorNode.setAttribute("href",     dataStr);
			    downloadAnchorNode.setAttribute("download", title + ".json");
			    downloadAnchorNode.click();
			    downloadAnchorNode.remove();
				
			},
			error : function(oData, response) {
			}
		});
	},
	
	handleUploadComplete : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var sResponse = oEvent.getParameter("response");
		console.log(oEvent.getParameter("status"));
		var response = oEvent.getParameter("responseRaw").split(":");
		if (oEvent.getParameter("status")== 500){
			
			sap.m.MessageToast.show(i18n.getText("eventexpimp_Upload_error")+"\n"+response[1]);
			return;
		}
		
		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			console.log(sResponse);
			if (sResponse == "200") {
				sMsg = i18n.getText("eventexpimp_Upload_success");
				oEvent.getSource().setValue("");
				
			} else {
				
				sMsg = i18n.getText("eventexpimp_Upload_error");
			}
			sap.m.MessageToast.show(sMsg);
		} else {
			sMsg = i18n.getText("eventexpimp_Upload_success");
			oEvent.getSource().setValue("");
			sap.m.MessageToast.show(sMsg );
		}
		
	},

	// -------------------------------------------------
	handleTypeMissmatch: function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show(i18n.getText("templatedetails_File_type") + oEvent.getParameter("fileType") + i18n.getText("templatedetails_Not_supported_choose_types") + sSupportedFileTypes);
	},

	// -------------------------------------------------
	onPressImport : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle(); 
		var oFileUploader = this.getView().byId("template");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show(i18n.getText("templatedetails_Choose_file_upload"));
			return;
		}
		var idSelect = this.getView().byId("selectId").getSelectedKey();
		//oFileUploader.setSendXHR("true");
		oFileUploader.setUploadUrl("/generator/rst/json/admin/importdata/"+idSelect);
		oFileUploader.upload();
		
	},
	
})


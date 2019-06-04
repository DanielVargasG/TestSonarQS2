sap.ui.controller("generator.controller.document.DocumentArchive", {
	
	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		window._this['ctrlArchive'] = this;
		
		window.RefreshArchive = function(obj){
			
			var amModelFolder = new sap.ui.model.json.JSONModel();
			var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelFolder.create("/documents/archive", obj,{
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					if(json){
						amModelFolder.setData(json);
					}
					
				},
				error : function(oData, response) {
				}
			});
			
			sap.ui.getCore().setModel(amModelFolder, "modelFolder");
			window._this['ctrlArchive'].getView().setModel(amModelFolder, "modelFolder");
			
		}
		
		//window.RefreshArchive();
	},
	
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},
	
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "Documentarchive") {
			return;
		}

		window._this['ctrlArchive'] = this;

	},
	
	onPressDate : function(evt){
		
		var obj = new Object();
		obj.date1=this.getView().byId("startDate").getValue();
		obj.date2=this.getView().byId("finishDate").getValue();
		obj.user=this.getView().byId("idUser").getValue();
		var table= this.getView().byId("idTableDoc"); //
		window.obj = obj;
		
		var amModelFolder = new sap.ui.model.json.JSONModel();
		var oDataModelFolder = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelFolder.create("/documents/archive", obj,{
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				console.log(json);
				if(json){
					amModelFolder.setData(json);
					table.setVisible(true);
				}
				
			},
			error : function(oData, response) {
			}
		});
		
		sap.ui.getCore().setModel(amModelFolder, "modelFolder");
		window._this['ctrlArchive'].getView().setModel(amModelFolder, "modelFolder");

	},
	
	onPressArchive : function(oEvent){
		var i18n =window._this['ctrlArchive'].getView().getModel("i18n").getResourceBundle();
		var idDoc = window._this['ctrlArchive'].getView().getModel("modelFolder").getProperty("id", oEvent.getSource().getParent().getBindingContext("modelFolder"));
		var cell = this.getView().byId("idCell").indexOfCell(); 
		console.log(oEvent.getSource());
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("global_Confirm"),
			type : 'Message',
			content : new sap.m.Text({
				text : i18n.getText("archive_Change_status_document")
			}),
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				press : function() {
					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.read('/documentsnoarchive/'+idDoc ,{
						sync : false,
						success : function(oData2, response) {
							sap.m.MessageToast.show(i18n.getText("archive_Update_document"));
							//cell.removeCell(cell);
							window.RefreshArchive(window.obj);
						},
						error : function(oData2, response) {
							
							sap.m.MessageToast.show(i18n.getText("global_Error"));
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
	},
	
	onPressDetails : function(oEvent){
		var idDoc =window._this['ctrlArchive'].getView().getModel("modelFolder").getProperty("id", oEvent.getSource().getParent().getBindingContext("modelFolder"));
		window.back = "Documentarchive";
		this._oRouter.navTo("docs", {
			id : idDoc
		});
	}
	
});
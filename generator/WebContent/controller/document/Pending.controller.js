sap.ui.controller("generator.controller.document.Pending", {

	onInit : function() {
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		var oEventBus = sap.ui.getCore().getEventBus();
		oEventBus.subscribe("Table", "GoToPage", this.handleColumnListItemSelect, this);

		var oComp = sap.ui.getCore().createComponent({
			name : 'generator.view.Table'
		});
		window._this['ctrlPend'] = this;
		this._oTable = oComp.getTable();
		this.getView().byId("idIconTabBar").insertContent(this._oTable);
		window.back = "documents";
		// update table
		this._oTable.setHeaderText(null);
		this._oTable.setShowSeparators("Inner");

		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

		var defaultSelect = window._this['ctrlPend'].getView().byId("idIconTabBar");
		defaultSelect.setSelectedKey("Pending");
		
		window.showResumeSignature = function(json) 
		{			
			console.log(json);
			
			var amModelResume = new sap.ui.model.json.JSONModel();
			amModelResume.setSizeLimit(json.length);
			amModelResume.setData(json);			
			
			this.pressDialog = new sap.m.Dialog({
				title: 'Summary Process',
				content: new sap.m.List().setModel(amModelResume).bindItems("/",
						 new sap.m.StandardListItem({
							 title: "IdDoc: "+"{field}"+" Status: "+"{code}",
							 description:"{message}"})
				),
				beginButton: new sap.m.Button({
					text: 'Close',
					press: function () {
						this.pressDialog.close();
						//this.refreshviewPending(null);
					}.bind(this)
				}),
				afterClose : function() {
					pressDialog.destroy();
					//amModelResume=null;
				}
			});

			//to get access to the global model
			window._this['ctrlPend'].getView().addDependent(this.pressDialog);

			this.pressDialog.open();
		}
		
		window.signature = function(obj,busy){
			var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel2.create('/documentsItemsSign/' , obj,{
				sync : true,
				success : function(oData2, response) 
				{
					
					var json = JSON.parse(response.body);
					window.showResumeSignature(json);
					busy.close();
					window.refreshviewPending(null);
				},
				error : function(oData2, response) {
					sap.m.MessageToast.show("Error signature documents");
				}
			});
		}
		
		window.refreshviewPending = function(parameters) {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);

			oDataModel.create("/documents",parameters,null,function(oData,response) {		
					var json = JSON.parse(response.body);

					var pend = 0;
					var val = 0;
					var other = 0;
					var all = 0;

					json.forEach(function(element, i) {
						json[i].check=false;
						if (element.status.includes("Pending")) {
							pend += 1;
							json[i].check=true;
						}
						else if (element.status == "Validated") {
							val += 1;
						}
						else{
							other +=1;
						}
						all += 1;
					});
					json.pend = pend; 
					json.name_pend = "Pending documents";
					json.val = val;
					json.all = all;
					json.others = other;
					console.log(json);
					amModel.setSizeLimit(json.length); 
					amModel.setData(json);
					console.log("total ... "+json.length);
					
					
					//window._this['ctrlPend'].getView().getId("idIconTabBar").setSelectedKey(1);
					var defaultSelect = window._this['ctrlPend'].getView().byId("idIconTabBar");
					defaultSelect.setSelectedKey("Pending");
					
				}
			);
			// reuse table sample component
			var oComp = sap.ui.getCore().createComponent({
				name : 'generator.view.Table'
			});
			oComp.setModel(amModel);
			window._this['ctrlPend'].getView().setModel(amModel);
		}
		
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

	},
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "documents") {
			return;
		}

		window.refreshviewPending(null);
		
	},

	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("main");
	},

	handleColumnListItemSelect : function(sChanel, sEvent, oData) {
		this._oRouter.navTo("docs", {
			id : oData.item
		});
	},
	handleIconTabBarSelect : function(oEvent) {
		var oBinding = this._oTable.getBinding("items"), sKey = oEvent.getParameter("key"), oFilter;
		
		if (sKey === "Pending") {
			oFilter = new sap.ui.model.Filter("status", "Contains", "Pending");
			oBinding.filter([ oFilter ]);
		} else if (sKey === "Validated") {
			oFilter = new sap.ui.model.Filter("status", "EQ", "Validated");
			oBinding.filter([ oFilter ]);
		} else {
			oBinding.filter([]);
		}
		if (sKey != "Pending" && sKey != "Validated" && sKey !="All"){
			var str= ["Pending","Validated"];
			var aFilter = [];
			aFilter.push(new sap.ui.model.Filter("status", "NE", "Pending"));
			aFilter.push(new sap.ui.model.Filter("status", "NE", "PendingSign"));
			oBinding.filter(new sap.ui.model.Filter( aFilter, true));
		}
		if(sKey != "Auth"){
			window.auth = function (){
				console.log("Authorization");
			
				var amModelAuth = new sap.ui.model.json.JSONModel();
				var oDataModelAuth = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
				oDataModelAuth.read("/template/getauthuser",{
					sync : false,
					success : function(oData, response) {
						var json = JSON.parse(response.body);
						console.log(json);
						if(json){
							amModelAuth.setData(json);
						}
						
					},
					error : function(oData, response) {
					}
				});
				
				sap.ui.getCore().setModel(amModelAuth, "modelAuth");
				window._this['ctrlArchive'].getView().setModel(amModelAuth, "modelAuth");
		
			}
			window.auth();
		}
	},
	
	//-----------------------------------
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

})
jQuery.sap.require("sap.m.MessageToast");
sap.ui.controller("generator.controller.logger.Logger", {

	onInit : function() {

		sap.ui.core.BusyIndicator.show(0);
		window.keys = new Map();
		window.id = new Array();
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

		window._this['loggerCtrlId'] = this;
		window.refreshviewLogger = function() {
			console.log("+ refresh logger");
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("logger", {
				sync : false,
				success : function(oData, response) {
	
					var json = JSON.parse(response.body);
					//console.log(json);
					
					json.forEach(function(element) {
						//console.log(element.addedOn);
						var date = new Date(element.addedOn + 5*3600*1000 + (3600000*-5) );
						element.realDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
					});
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					sap.ui.core.BusyIndicator.hide();
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['loggerCtrlId'].getView().setModel(amModel);
		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "logger") {
			return;
		}
		window._this['loggerCtrlId'] = this;
		window.refreshviewLogger();
	},

	handleListItemPress : function(oEvent) {
		//console.log("inside the HandleListItem " + oEvent);
		var context = oEvent.getSources().getBindingContext();
		this.nav.to("Detail", context);
	},

	handleSearch : function(oEvent) {
		var filters = [];
		var query = oEvent.getParameter("query");
		if (query && query.length > 0) {
			var filterVar = new sap.ui.model.Filter("SoId", sap.ui.model.FilterOperator.Contains, query);
			filters.push(filterVar);
		}

		var list = this.getView().byId("list");
		var binding = list.getBinding("items");
		binding.filter(filters);
	},
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationData");
	},
	onDownPressed : function(oEvent) {
		var bindingContext = oEvent.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var unikID = bindingContext.oModel.oData.documents[res[2]].id;
		var unikURL = bindingContext.oModel.oData.documents[res[2]].hash;
		sap.m.MessageToast.show('Download started');
		window.location.replace("https://api.staging.eu.people-doc.com/api/v1/enterprise/documents/"+unikID+"/download/"+ unikURL );
		

	},
	handleListSelect : function(oEvent) {
		var context = oEvent.getParameter("listItem").getBindingContext();
		var odata = context.getPath();
		this.oHasher = sap.ui.core.routing.HashChanger.getInstance();
		this.oHasher.setHash(context);
		var sViewId = this.oHasher.getHash().split("/")[2];
		this._oRouter.navTo("Detail", {
			contextPath : sViewId
		});
	},
	handleUploadComplete : function(oEvent) {
		var sResponse = oEvent.getParameter("response");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			if (sResponse == "200") {
				sMsg = i18n.getText("global_Upload_sucess");
				oEvent.getSource().setValue("");
			} else {
				sMsg = i18n.getText("global_Upload_error");
			}
			sap.m.MessageToast.show(sMsg);
		}
	},

	handleUploadPress : function(oEvent) {
		var oFileUploader = this.getView().byId("fileUploader");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show("Choose a file first");
			return;
		}
		oFileUploader.upload();
	},

	handleTypeMissmatch : function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show("The file type *." + oEvent.getParameter("fileType") + " is not supported. Choose one of the following types: " + sSupportedFileTypes);
	},

	handleValueChange : function(oEvent) {
		sap.m.MessageToast.show("Press 'Upload File' to upload file '" + oEvent.getParameter("newValue") + "'");
	},
	beginButton : new sap.m.Button({
		text : 'Submit',
		enabled : false,
		press : function() {
		}
	}),
	
	handleShowFilter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("logger_filters"),	
			type : 'Message',
			content : [ 

			new sap.m.Label({
				text : i18n.getText("logger_Order_by"),
				labelFor : 'submitDialogSlug',
				width : '50%'
			}), 

			new sap.m.Select('submitDialogOrder', {
				width : '100%',

			}).addItem(new sap.ui.core.Item({
				key : "NA",
				text : i18n.getText("global_None")
			})).addItem(new sap.ui.core.Item({
				key : "ASC_DATE",
				text : i18n.getText("logger_Ascending_creation_date")
			})).addItem(new sap.ui.core.Item({
				key : "DESC_DATE",
				text : i18n.getText("logger_Descendantdate_creation_date")
			})), 
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("global_Filters"),
				labelFor : 'submitDialogSlug',
				width : '50%'
			}),
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("global_User"),
				labelFor : 'submitDialogSlug',
				width : '50%'
			}), 

			new sap.m.Input('submitDialogUserValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();	
				},
				fieldWidth : '50%',
			}),		
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("logger_Maximum_number_records"),
				labelFor : 'submitDialogSlug',
				width : '50%'
			}), 

			new sap.m.Input('submitDialogMaxRValue', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();	
				},
				fieldWidth : '50%',
			}),	
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),			
			
			
			new sap.m.Label({
				text : i18n.getText("logger_Creation_date"),
				labelFor : 'submitDialogSlug',
				width : '20%'
			}), 			
			
			new sap.m.DatePicker("submitDialogCalendar", {}).setValueFormat("yyyy-MM-dd")
			],

			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),				
				press : function() 
				{
					var sUser = sap.ui.getCore().byId('submitDialogUserValue').getValue();
					var sDate = sap.ui.getCore().byId('submitDialogCalendar').getValue();
					
					var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({pattern : "yyyy-MM-dd" }); 
					var dateStr = "";
											
					var sOrder = sap.ui.getCore().byId('submitDialogOrder').getSelectedItem().getKey();
					var maxResult = sap.ui.getCore().byId('submitDialogMaxRValue').getValue();
					
					dialog.close();

					var oparameters = {
						user : sUser,
						date : sDate,
						order : sOrder,
						maxResult : maxResult
					};

					var amModel = new sap.ui.model.json.JSONModel();

					var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel.create("/loggerfilter/",oparameters,null, function(oData, response) {						

							var json = JSON.parse(response.body);
							
							json.forEach(function(element) {
								//console.log(element.addedOn);
								var date = new Date(element.addedOn + 5*3600*1000 + (3600000*-5) );
								element.realDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
							});
							amModel.setSizeLimit(json.length);
							amModel.setData(json);
							sap.ui.core.BusyIndicator.hide();
						}
					);
					
					window._this['loggerCtrlId'].getView().setModel(amModel);
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

	handleShowInfoVersion : function(oEvent){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("logger_Generator_version"),	
			type : 'Message',
			content : 
			[ 
				new sap.m.Label({
				text : i18n.getText("logger_Version"),
				labelFor : 'submitDialogSlug',
				width : '50%'
				}),			
			],
			
			endButton : new sap.m.Button({
				text : i18n.getText("global_Close"),
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
	handleRefreshData : function(oEvent) {
		window.refreshviewLogger();
		
	},
	
	handleDeleteAll : function(oEvent){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.remove("/loggerdelete/", function() {						

			},function(oData, response){
				var message = sap.m.MessageToast.show(i18n.getText("logger_Delete"));
			}
		);
		
		window.refreshviewLogger();
	},
	
	onPressCheck : function(evt){
		
		var bindingContext = evt.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var oEventBus = sap.ui.getCore().getEventBus();
		var value = evt.getParameter('selected');
		var clave=bindingContext.oModel.oData[res[1]].id;
		window.id.push(evt.getParameter('id'));
		window.keys.set(clave,value);
		console.log(window.keys);
	
	},
	
	onDeleteItems : function(){
		jQuery.sap.require("sap.m.MessageBox");
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var obj = [];
		var isTrue=false;
		window.keys.forEach(function(valor, clave) {
			  var tmp = new Object();
			  tmp.id = clave;
			  tmp.check = valor;
			  obj.push(tmp);
			  if(valor) {isTrue=true;}
			});
		
		if(isTrue){
			var dialog = new sap.m.Dialog({
				title : i18n.getText("global_Confirm"),
				type : 'Message',
				content : new sap.m.Text({
					text : i18n.getText("logger_Delete_item_selected")
				}),
				beginButton : new sap.m.Button({
					text : i18n.getText("global_Submit"),
					press : function() {
						
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel2.create('/loggerdelete/items' ,obj,{
							sync : false,
							success : function(oData2, response) {
								sap.m.MessageToast.show(i18n.getText("global_Delete"));
							},
							error : function(oData2, response) {
								
								sap.m.MessageToast.show(i18n.getText("pending_Archive_documents_error"));
							}
						});
						window.keys=new Map();
						window.refreshviewLogger();
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
			
		}else
		{
			
			var msg = sap.m.MessageBox.information(i18n.getText("logger_Select_item"), {
			    title: "Alert",
			    onClose: null,  
			    styleClass: ""  , 
			    initialFocus: null , 
			    textDirection: sap.ui.core.TextDirection.Inherit 
			    });
		}
		
		
		
	}
	

})
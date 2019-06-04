sap.ui.define([ 'jquery.sap.global', './Formatter', 'sap/ui/core/mvc/Controller', 'sap/ui/model/json/JSONModel' ], function(jQuery, Formatter, Controller, JSONModel) {
	"use strict";

	var TableController = Controller.extend("generator.view.Table.Table", {

		onInit : function() {
			window.keys = new Map();
			window.id = new Array();
						
		},
		handleColumnListItemSelect : function(event) {
			var bindingContext = event.getSource().getBindingContext();
			var res = bindingContext.sPath.split("/");
			var oEventBus = sap.ui.getCore().getEventBus();
			oEventBus.publish("Table", "GoToPage", {
				item : bindingContext.oModel.oData[res[1]].id
			});
		},
		
		onPressFilter : function(){
			var i18n=window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle();
			var dialog = new sap.m.Dialog({
				title : i18n.getText("pending_Pending_Documents_Filters"),	
				type : 'Message',
				content : [ 

				new sap.m.Label({
					text : 'Filters ',
					labelFor : 'submitDialogSlug',
					width : '50%'
				}),
				
				new sap.m.Label({
					text : ' ',
					labelFor : 'submitDialogSlug',
					width : '100%'
				}),
				
				new sap.m.Label({
					text : i18n.getText("pending_Document_User"),
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
					text : i18n.getText("pending_Id_template"),
					labelFor : 'submitDialogSlug',
					width : '50%'
				}), 

				new sap.m.Input('submitDialogIdTemplateValue', {
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
					text : i18n.getText("pending_Maximum_number_records"),
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
					text : i18n.getText("pending_Initial_generation_date"),
					labelFor : 'submitDialogSlug',
					width : '20%'
				}), 			
				
				new sap.m.DatePicker("submitDialogCalendar1", {
					displayFormatType : sap.ui.core.CalendarType.Gregorian}),
				
				new sap.m.Label({
					text : i18n.getText("pending_Final_generation_date"),
					labelFor : 'submitDialogSlug',
					width : '20%'
				}), 			
				
				new sap.m.DatePicker("submitDialogCalendar2", {
					displayFormatType : sap.ui.core.CalendarType.Gregorian})
				
				],

				beginButton : new sap.m.Button({
					text : i18n.getText("global_Submit"),				
					press : function() 
					{
						var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({pattern : "yyyy-MM-dd" });
						var sUser = sap.ui.getCore().byId('submitDialogUserValue').getValue();
						var sDate = sap.ui.getCore().byId('submitDialogCalendar1').getValue();
						var sDateF = sap.ui.getCore().byId('submitDialogCalendar2').getValue();
						
						var maxResult = sap.ui.getCore().byId('submitDialogMaxRValue').getValue();
						var idTemplate = sap.ui.getCore().byId('submitDialogIdTemplateValue').getValue();
						
						var dateStrInit = "";
						var dateStrEnd = "";
						
						if(sDate!=null && sDate!="")
						{
							var parts = sDate.split('/');
							// Please pay attention to the month (parts[1]); JavaScript counts months from 0:
							// January - 0, February - 1, etc.
							
							if(parts[2]<100){
								parts[2]='20'+parts[2];
							} 
							
							var date = new Date(parts[2],parts[1] - 1,parts[0]); 
							
							console.log("date new date "+date)
							dateStrInit = dateFormat.format(date);
						}
						
						console.log(dateStrInit)
						
						if(sDateF!=null && sDateF!=""){	
							var parts = sDateF.split('/');
							// Please pay attention to the month (parts[1]); JavaScript counts months from 0:
							// January - 0, February - 1, etc.
							
							if(parts[2]<100) {
								parts[2]='20'+parts[2];
							}
							
							var date = new Date(parts[2],parts[1] - 1,parts[0]);
							
							console.log("date new date f "+date)
							dateStrEnd = dateFormat.format(date);
						}						
						
						dialog.close();

						var oparameters = {
							user : sUser,
							date : dateStrInit,							
							maxResult : maxResult,
							id: idTemplate,
							dateFinish : dateStrEnd
						};

					
						window.refreshviewPending(oparameters);
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
		
		onPressInfo : function(oEvent) {
			
			if (!this._oPopover) {
				
				var amModelStatusInfo = new sap.ui.model.json.JSONModel();
				var oDataModelStatusInfo = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
				oDataModelStatusInfo.read("/infostatusattach", {
					sync : false,
					success : function(oData, response) {
						var json = JSON.parse(response.body);
						amModelStatusInfo.setSizeLimit(json.length);
						amModelStatusInfo.setData(json);
						console.log(amModelStatusInfo);
					},
					error : function(oData, response) {
						
					}
				});		
				
				this._oPopover = sap.ui.xmlfragment("popoverNavCon", "generator.view.fragments.DialogStatusInfo", this);
				this._oPopover.setModel(amModelStatusInfo);
				this._oPopover.setModel(window._this['ctrlPend'].getView().getModel("i18n"), "i18n");
				this.getView().addDependent(this._oPopover);
			}
			

			var oButton = oEvent.getSource();
			jQuery.sap.delayedCall(0, this, function() {
				this._oPopover.openBy(oButton);
			});
		},
		
		handleClosePress : function (oEvent) {
			this._oPopover.close();
			//this._oPopover.destroy();
		},
		
		onPressDelete: function(oEvt) {
			jQuery.sap.require("sap.m.MessageBox");
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
				var msgConfirm=window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("global_Confirm");
				var msgSubmit=window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("global_Delete");
				var msgCancel=window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("global_Cancel");
				var msgDelete = window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("pending_Delete_selected_documents");
				var dialog = new sap.m.Dialog({
					title : msgConfirm,
					type : 'Message',
					content : new sap.m.Text({
						text : msgDelete
					}),
					beginButton : new sap.m.Button({
						text : msgSubmit,
						press : function() {
							
							var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
							oDataModel2.create('/documentsitemsdelete/' , obj,{
								sync : false,
								success : function(oData2, response) {
									var msgRemoved = window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("pending_Removed_documents");
									sap.m.MessageToast.show(msgRemoved );
								},
								error : function(oData2, response) {
									var msgRemovedError = window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("pending_Removed_documents_error");
									sap.m.MessageToast.show(msgRemovedError);
								}
							});
							
							window.refreshviewPending(null);
							window.keys=new Map();
							dialog.close();
							
						}
					}),
					endButton : new sap.m.Button({
						text : msgCancel,
						press : function() {
							dialog.close();
							
						}
					}),
					afterClose : function() {
						dialog.destroy();
					}
				});
				dialog.open();
				var i;
				for (i = 0; i < window.id.length; i++) { 
					console.log(window.id[i]);
					sap.ui.getCore().byId(window.id[i]).setSelected(false);
				}
				window.id = new Array();
				
			}
			else
			{
				var msg = sap.m.MessageBox.information(window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle().getText("pending_Select_documents_remove"), {
				    title: "Alert",
				    onClose: null,  
				    styleClass: ""  , 
				    initialFocus: null , 
				    textDirection: sap.ui.core.TextDirection.Inherit 
				    });
			}
		},
		
		onPressSignature: function(oEvt) 
		{
			pressDialog: null,
			jQuery.sap.require("sap.m.MessageBox");
			var obj = [];
			var isTrue=false;
			window.keys.forEach(function(valor, clave) {
				  var tmp = new Object();
				  tmp.id = clave;
				  tmp.check = valor;
				  obj.push(tmp);
				  if(valor) {isTrue=true;}
				});
			
			if(isTrue)
			{
				
				var dialog = new sap.m.Dialog("dialogSignature",{
					title : 'Confirm',
					type : 'Message',
					content : new sap.m.Text({
						text : 'Are you sure you want to sign the selected documents?'
					}),
					
					beginButton : new sap.m.Button({
						text : 'Submit',
						press : function(oEvt) 
						{
							var dataLoaderBusyIndicator = new sap.m.BusyDialog({
                                        showCancelButton : false,
                                        title : "Please Wait..",
                                        text : "Send signature"
                                    });
							dataLoaderBusyIndicator.open();
							
							dialog.close();
							
							jQuery.sap.delayedCall(500, this, function () {
								window.signature(obj,dataLoaderBusyIndicator);
								window.keys=new Map();;
							});
							
							
						}
					}),
					
					endButton : new sap.m.Button({
						text : 'Cancel',
						press : function() {
							dialog.close();
							window.keys=new Map();
						}
					}),
					afterClose : function() {
						dialog.destroy();
					}
				});
				dialog.open();
				var i;
				for (i = 0; i < window.id.length; i++) { 
					console.log(window.id[i]);
					sap.ui.getCore().byId(window.id[i]).setSelected(false);
				}
				window.id = new Array();
				window.keys = new Map(); 
			}
			else
			{
				var msg = sap.m.MessageBox.information("select documents to sign", {
				    title: "Alert",
				    onClose: null,  
				    styleClass: ""  , 
				    initialFocus: null , 
				    textDirection: sap.ui.core.TextDirection.Inherit 
				    });
			}
		},
				
		
		onPressCheck : function(evt){
			
			var bindingContext = evt.getSource().getBindingContext();
			var res = bindingContext.sPath.split("/");
			var oEventBus = sap.ui.getCore().getEventBus();
			var value = evt.getParameter('selected');
			var clave=bindingContext.oModel.oData[res[1]].id;
			window.id.push(evt.getParameter('id'));
			window.keys.set(clave,value);
		
		},
		
		onPressArchive: function(oEvt) {
			jQuery.sap.require("sap.m.MessageBox");
			var i18n = window._this['ctrlPend'].getView().getModel("i18n").getResourceBundle();
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
						text : i18n.getText("pending_Archive_selected_documents")
					}),
					beginButton : new sap.m.Button({
						text : i18n.getText("global_Submit"),
						press : function() {
							
							var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
							oDataModel2.create('/documentsitemsarchive/' , obj,{
								sync : false,
								success : function(oData2, response) {
									sap.m.MessageToast.show(i18n.getText("pending_Archive_documents"));
								},
								error : function(oData2, response) {
									
									sap.m.MessageToast.show(i18n.getText("pending_Archive_documents_error"));
								}
							});
							
							window.refreshviewPending(null);
							window.keys=new Map();
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
				var i;
				for (i = 0; i < window.id.length; i++) { 
					console.log(window.id[i]);
					sap.ui.getCore().byId(window.id[i]).setSelected(false);
				}
				window.id = new Array();
				
			}
			else
			{
				var msg = sap.m.MessageBox.information(i18n.getText("pending_Select_documents_archive"), {
				    title: "Alert",
				    onClose: null,  
				    styleClass: ""  , 
				    initialFocus: null , 
				    textDirection: sap.ui.core.TextDirection.Inherit 
				    });
			}
		},
		
	});

	return TableController;

});
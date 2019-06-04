sap.ui.controller("generator.view.Administration.EventListenerCtrlProcess", {

	//-------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
		window.time="Current";
		var jobEvent = this.getView().byId("JobEventMsg");
		//load status ctrl event
		var amModelStatusCtrEvent = new sap.ui.model.json.JSONModel();
		var oDataModelStatusCtrEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelStatusCtrEvent.read("/admin/statusCtrEvent", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelStatusCtrEvent.setSizeLimit(json.length);
				amModelStatusCtrEvent.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});		
		
		sap.ui.getCore().setModel(amModelStatusCtrEvent, "modelStatusCtrEvent");	
		
		window._this['ctrlCtr'] = this;
		window.refreshviewCtr = function()
		{
			//load status ctrl event
			var amModelLastExecution = new sap.ui.model.json.JSONModel();
			var oDataModelLastCtrEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelLastCtrEvent.read("/admin/eventsCtrl/last_executions", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelLastExecution.setSizeLimit(json.length);
					amModelLastExecution.setData(json);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			
			
			sap.ui.getCore().setModel(amModelLastExecution, "amModelLastExecution");	
			window._this['ctrlCtr'].getView().byId("idTableLastProcess").setModel(amModelLastExecution);
			
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/eventsCtrl/processtime/"+window.time, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					console.log(json);
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			
			window._this['ctrlCtr'].getView().setModel(amModel);	
			
			window.objectTTT = window._this['ctrlCtr'].getView().byId("idPrincipalTable");
			window._this['ctrlCtr'].getView().byId("idPrincipalTable").setModel(amModel);
			sap.ui.getCore().setModel(amModel);	
			
			// Load Select list Even Listener 
			var amModelEventlist = new sap.ui.model.json.JSONModel();
			var oDataModelEventlist = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelEventlist.read("/eventlist/selectevenlistener", {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					amModelEventlist.setSizeLimit(json.length);
					amModelEventlist.setData(json);
				},
				error : function(oData, response) {
				}
			});
			sap.ui.getCore().setModel(amModelEventlist, "modelEventlist");
			
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/controlpanel/jobevent", {
				sync : false,
				success : function(oData, response) {
					var job = (response.body === 'true');
					
					if(job === false){
						jobEvent.setVisible(true);
					}
					
					else{
						jobEvent.setVisible(false);
					}
					
					if(response.body == null || response.body === ""){
						jobEvent.setVisible(false);
					}
				},
				error : function(oData, response) {
					sap.m.MessageToast.show("Error" );
					jobEvent.setVisible(false);
				}
			});
			
		}
	},
	//-------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/evenlisterctrprocess") {
			return;
		}
		
		window.refreshviewCtr();
	},
	onChangeStatus:function(oEvent)
	{
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());					
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectStatus = new sap.ui.core.Item({
			key : "{modelStatusCtrEvent>id}",
			text : "{modelStatusCtrEvent>label}"
		});
		
		var idSignature = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_Edit_status"),
			type : 'Message',
			content : [ 
			
			new sap.m.Label({text : i18n.getText("event_New_status"),labelFor : '',width : '90%'}), 
			
			new sap.m.Select('submitDialogStatus',{width : '100%'}).bindAggregation("", {
				path : "modelStatusCtrEvent>/",
				template : oItemSelectStatus
			}),
			
			new sap.m.Label({text : i18n.getText("event_Observation"),labelFor : 'submitDialogSlug',width : '100%'}),			
			
			new sap.m.Input('submitDialogObservation', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
			}),
			
			new sap.m.Label({
				text : i18n.getText("event_Does_apply_attached_documents"),
				labelFor : 'isApplyAttach'
			}), new sap.m.CheckBox('submitDialogIsApplyAttach', {}),


			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
				enabled : false,
				press : function() {
					var sStatus = sap.ui.getCore().byId('submitDialogStatus').getSelectedItem().getKey();
					var sIsAttach = sap.ui.getCore().byId('submitDialogIsApplyAttach').getSelected();
					var sObse = sap.ui.getCore().byId('submitDialogObservation').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					
					var oparameters = {
						id : idEvent,
						value : sStatus,
						label : sObse,
						flag : sIsAttach,
					};

					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/eventsCtrl/uploadCtrlEvent/' + idEvent, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);												
						sap.m.MessageToast.show(i18n.getText("event_Updated"));						
						
						window.refreshviewCtr();


					}, function() {
						sap.m.MessageToast.show(i18ngetText("event_Updated_error"));
					});
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
	onObservationPress: function (oEvent)
	{	
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
			contentWidth: '50%',
			contentHeight: '40%',
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
	onAttachPress: function (oEvent)
	{
		var idValue = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		this._oRouter.navTo("/adm/evenlisterctrattach", 
		{
			idCtrl : idValue
		});
	},

	//-------------------------------------------------
	//Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationEvent");
	},
	
	onResizableDialog: function () 
	{
		if (!this.resizableDialog) {
			this.resizableDialog = new sap.m.Dialog({
				title: 'meaning of Status',
				contentWidth: "550px",
				contentHeight: "300px",
				resizable: true,
				content: new sap.m.List({
					items: 
					{
						path: '/ProductCollection',
						template: new new sap.m.StandardListItem({
							title: "TransferAttach",
							counter:1
						})
					}
				}),
				beginButton: new Button({
					text: 'Close',
					press: function () {
						this.resizableDialog.close();
					}.bind(this)
				})
			});

			//to get access to the global model
			this.getView().addDependent(this.resizableDialog);
		}

		this.resizableDialog.open();
	},	
	handleRefreshData : function(oEvent) {
		window.refreshviewCtr();
		
	},
	
	//-------------------------------------------------	
	//Delete event listener	
	//-------------------------------------------------
	//Edit Event Listener		
	//-------------------------------------------------
	//Add Event listener	
	//-------------------------------------------------
	
	onPressFilter : function(){
		
		var oItemSelectEventList = new sap.ui.core.Item({
			key : "{modelEventlist>id}",
			text : "{modelEventlist>label}"
		});
		
		var dialog = new sap.m.Dialog({
			title : 'Event Listener Filters',	
			type : 'Message',
			content : [ 

			new sap.m.Label({
				text : 'Order by: ',
				labelFor : 'submitDialogSlug',
				width : '50%'
			}), 

			new sap.m.Select('submitDialogOrder', {
				width : '100%',

			}).addItem(new sap.ui.core.Item({
				key : "NA",
				text : "None"
			})).addItem(new sap.ui.core.Item({
				key : "ASC_DATE",
				text : "Ascending Creation Date"
			})).addItem(new sap.ui.core.Item({
				key : "DESC_DATE",
				text : "Descendantdate Creation Date"
			})), 
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
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
				text : 'Status by: ',
				labelFor : 'submitDialogSlug',
				width : '50%'
			}), 

			new sap.m.Select('submitDialogEventListener', {
				width : '100%',

			}).bindAggregation("", {
				path : "modelEventlist>/",
				template : oItemSelectEventList
			}),
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : 'User: ',
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
				text : 'Id : ',
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
				text : 'Maximum number of records: ',
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
				text : 'Efective Start Date: ',
				labelFor : 'submitDialogSlug',
				width : '20%'
			}), 			
			
			new sap.m.DatePicker("submitDialogCalendar1", {}),
			
			new sap.m.Label({
				text : 'Efective End Date: ',
				labelFor : 'submitDialogSlug',
				width : '20%'
			}), 			
			
			new sap.m.DatePicker("submitDialogCalendar2", {})
			
			],

			beginButton : new sap.m.Button({
				text : 'Submit',
				enabled : true,
				press : function() 
				{					
					console.log ("Submit ... ");
					
					var sUser = sap.ui.getCore().byId('submitDialogUserValue').getValue();
					var sDate = sap.ui.getCore().byId('submitDialogCalendar1').getValue();
					var sDateF = sap.ui.getCore().byId('submitDialogCalendar2').getValue();
					// submitDialogEventListener
					var sOrder = sap.ui.getCore().byId('submitDialogOrder').getSelectedItem().getKey();
					var sTatus = sap.ui.getCore().byId('submitDialogEventListener').getSelectedItem().getKey();
					var maxResult = sap.ui.getCore().byId('submitDialogMaxRValue').getValue();
					var idTemplate = sap.ui.getCore().byId('submitDialogIdTemplateValue').getValue();
					
					dialog.close();

					var oparameters = {
						user : sUser,
						date : sDate,
						order : sOrder,
						maxResult : maxResult,
						id: idTemplate,
						dateFinish : sDateF,
						status: sTatus
					};

					var amModel = new sap.ui.model.json.JSONModel();

					var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json/admin", true);
					oDataModel.create("/eventsCtrl/processeventfilters",oparameters,null, function(oData, response) {						

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
				}
			}),

			endButton : new sap.m.Button({
				text : 'Cancel',
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
			oDataModelStatusInfo.read("/infostatusevenlist", {
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
	
	onPressCurrent : function (oEvent){
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrl/processtime/Current", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				console.log("exit ...");
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		window.time="Current";
		window._this['ctrlCtr'].getView().setModel(amModel);	
		
		window.objectTTT = window._this['ctrlCtr'].getView().byId("idPrincipalTable");
		window._this['ctrlCtr'].getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);
	},
	
	onPressFuture : function (oEvent){
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrl/processtime/Future", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				console.log("exit ...");
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		window.time="Future";
		window._this['ctrlCtr'].getView().setModel(amModel);	
		
		window.objectTTT = window._this['ctrlCtr'].getView().byId("idPrincipalTable");
		window._this['ctrlCtr'].getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);	
	}

})
sap.ui.controller("generator.view.Administration.synch.Synch", {

	//-------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);

		var amModelStatusAttachEvent = new sap.ui.model.json.JSONModel();
		var oDataModelStatusCtrAttachEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelStatusCtrAttachEvent.read("/admin/statusCtrEventAttach", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelStatusAttachEvent.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});		
		
		sap.ui.getCore().setModel(amModelStatusAttachEvent, "modelStatusCtrAttachEvent");
		
		
		// Load Select list Even Listener 
		var amModelEventlist = new sap.ui.model.json.JSONModel();
		var oDataModelEventlist = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelEventlist.read("/eventlist/selectevenlistener", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				console.log(json);
				amModelEventlist.setData(json);
			},
			error : function(oData, response) {
			}
		});
		sap.ui.getCore().setModel(amModelEventlist, "modelEventlist");

		// Load Select Info Status Attach
		var amModelStatusInfo = new sap.ui.model.json.JSONModel();
		var oDataModelStatusInfo = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelStatusInfo.read("/infostatusattach", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelStatusInfo.setData(json);
			},
			error : function(oData, response) {
				
			}
		});		
		
		sap.ui.getCore().setModel(amModelStatusInfo, "detailModel");
		var jobEmployee =  this.getView().byId("JobEmployeeMsg");
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/controlpanel/jobemployee", {
			sync : false,
			success : function(oData, response) {
				var job = (response.body === 'true');
				
				if(job === false){
					jobEmployee.setVisible(true);
				}else{
					jobEmployee.setVisible(false);
				}
				if(response.body == null || response.body === "")
				{
					jobEmployee.setVisible(false);
				}
			},
			error : function(oData, response) {
				sap.m.MessageToast.show("Error" );
				jobEmployee.setVisible(false);
			}
		});
		
	},
	//-------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/attachment") {
			return;
		}
		
		var varId = evt.getParameter("arguments").idCtrl;
		window.idProcess = varId;
		window.refreshview = this.refreshView();		
	},
	//-------------------------------------------------
	//Refresh view
	refreshView : function()
	{
		//load status jobs
		var amModelLastExecution = new sap.ui.model.json.JSONModel();
		var oDataModelLastCtrEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelLastCtrEvent.read("/admin/attachments/last_executions", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelLastExecution.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		
		
		sap.ui.getCore().setModel(amModelLastExecution, "amModelLastExecution");	
		this.getView().byId("idTableLastProcess").setModel(amModelLastExecution);
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/attachments/process/", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				console.log(json)
				amModel.setData(json);
			},
			error : function(oData, response) {
			}
		});
		
		this.getView().setModel(amModel);	
		
		console.log(amModel)
		window.objectTTT = this.getView().byId("idPrincipalTable");
		this.getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);		

		
	},
	onObservationPress: function (oEvent)
	{
		var ObservationValue = this.getView().getModel().getProperty("observations", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();

		var find = 'END_ ';
		var re = new RegExp(find, 'g');
		var res = ObservationValue.replace(re, "\n\n");
		
		var find = 'END_';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "\n");
		
		var find = '_INIT';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "");
		
		
		
		
		var dialog = new sap.m.Dialog({
			contentWidth: '50%',
			contentHeight: '40%',
			title: i18n.getText("global_Observations"),
			content: 
			[
				new sap.m.TextArea({value : res,editable: false,width: "100%",growing: true,maxLength: 2000,rows:2000,height:'90%',growingMaxLines:100,growing:true,})
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
	onChangeStatus:function(oEvent)
	{
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());					
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectStatus = new sap.ui.core.Item({
			key : "{modelStatusCtrAttachEvent>id}",
			text : "{modelStatusCtrAttachEvent>label}"
		});	
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("attach_Edit_status"),
			type : 'Message',
			content : [ 
			
			new sap.m.Label({text : i18n.getText("attach_New_status"),labelFor : '',width : '90%'}), 
			
			new sap.m.Select('submitDialogStatus',{width : '100%'}).bindAggregation("", {
				path : "modelStatusCtrAttachEvent>/",
				template : oItemSelectStatus
			}),
			
			new sap.m.Label({text : i18n.getText("attach_Observation"),labelFor : 'submitDialogSlug',width : '100%'}),			
			
			new sap.m.Input('submitDialogObservation', {
				liveChange : function(oEvent) {
					var sText = oEvent.getParameter('value');
					var parent = oEvent.getSource().getParent();
					parent.getBeginButton().setEnabled(sText.length > 0);
				},
				fieldWidth : '50%',
			}),

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Edit"),
				enabled : false,
				press : function() {
					var sStatus = sap.ui.getCore().byId('submitDialogStatus').getSelectedItem().getKey();					
					var sObse = sap.ui.getCore().byId('submitDialogObservation').getValue();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					
					var oparameters = {
						id : idEvent,
						value : sStatus,
						label : sObse					
					};

					console.log(oparameters);
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/attachment/uploadAttachment/' + idEvent, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);												
						sap.m.MessageToast.show(i18n.getText("attach_Updated"));						
						window.refreshview();

					}, function() {
						sap.m.MessageToast.show(i18n.getText("attach_Error_updated"));
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
	

	//-------------------------------------------------
	//Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationAttachment");
	},
	showInfo : function(oEvent) {
		this.refreshView();
	},
	
onPressFilter : function(){
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectEventList = new sap.ui.core.Item({
			key : "{modelEventlist>id}",
			text : "{modelEventlist>label}"
		});
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("attach_Process_filters"),	
			type : 'Message',
			content : [ 

			new sap.m.Label({
				text : i18n.getText("attach_Order_by"),
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
				text : i18n.getText("attach_Ascending_creation_date")
			})).addItem(new sap.ui.core.Item({
				key : "DESC_DATE",
				text : i18n.getText("attach_Descendantdate_creation_date")
			})), 
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18ngetText("global_Filters"),
				labelFor : 'submitDialogSlug',
				width : '50%'
			}),
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			new sap.m.Label({
				text : i18n.getText("attach_Status_by"),
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
				text : i18n.getText("global_Id"),
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
				text : i18n.getText("attach_Maximum_number_records"),
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
				text : i18n.getText("attach_Efective_start_date"),
				labelFor : 'submitDialogSlug',
				width : '20%'
			}), 			
			
			new sap.m.DatePicker("submitDialogCalendar1", {}),
			
			new sap.m.Label({
				text : i18n.getText("attach_Efective_end_date"),
				labelFor : 'submitDialogSlug',
				width : '20%'
			}), 			
			
			new sap.m.DatePicker("submitDialogCalendar2", {})
			
			],

			beginButton : new sap.m.Button({
				text : i18n.getText("global_Insert"),
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
					oDataModel.create("/attachments/processattachfilters",oparameters,null, function(oData, response) {						

							var json = JSON.parse(response.body);
						
							json.forEach(function(element) {
								//console.log(element.addedOn);
								var date = new Date(element.addedOn + 5*3600*1000 + (3600000*-5) );
								element.realDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
							});
							
							amModel.setData(json);
							sap.ui.core.BusyIndicator.hide();
						}
					);	
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
					amModelStatusInfo.setData(json);					
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
	}
	
	//-------------------------------------------------	
	//Delete event listener	
	//-------------------------------------------------
	//Edit Event Listener		
	//-------------------------------------------------
	//Add Event listener	
	//-------------------------------------------------
})
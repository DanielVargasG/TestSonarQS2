sap.ui.controller("generator.view.Administration.EventListenerCtrlHisto", {

	//-------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	//-------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/evenlisterctr") {
			return;
		}
		
		window.refreshview = this.refreshView();
	},
	//-------------------------------------------------
	//Refresh view
	refreshView : function(){

		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		var oparameters = {};
		
		oDataModel.create("/admin/eventsCtrlHisto",oparameters,null, 
				function(oData, response) 
				{
					var json = JSON.parse(response.body);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
				});
		
		this.getView().setModel(amModel);	
		
		window.objectTTT = this.getView().byId("idPrincipalTable");
		this.getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);		

		
	},
	onDeleteAll:function(oEvent)
	{
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrlHistoDelttAll", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		
		this.getView().setModel(amModel);	
		
		window.objectTTT = this.getView().byId("idPrincipalTable");
		this.getView().byId("idPrincipalTable").setModel(amModel);
		sap.ui.getCore().setModel(amModel);	
	},
	onDelete:function(oEvent)
	{
		var idValue = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrlHistoDelete/" + idValue, {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		
		this.getView().setModel(amModel);	
		
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
			title: i18n.getText("event_Observation"),
			title: i18n.getText("global_Observations"),
			content : [new sap.ui.core.HTML({
				content : "<p style='margin:20px; font-size:14px;'>" + res + "</p>",
				editable : false,
				width : "100%",
				growing : true,
				maxLength : 5000,
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
		this._oRouter.navTo("/adm/evenlisterctrAttachHisto", 
		{
			idCtrlH : idValue
		});
	},

	//-------------------------------------------------
	//Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationEvent");
	},
	
	//-------------------------------------------------	
	//Delete event listener	
	//-------------------------------------------------
	//Edit Event Listener		
	//-------------------------------------------------
	//Recovery register	
	//-------------------------------------------------
	onChangeStatus:function(oEvent)
	{
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());					
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var view =  this.getView();
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_process_again"),
			type : 'Message',
			content : [new sap.m.Label({text : i18n.getText("event_process_again_msg"),labelFor : '',width : '90%'}),],
			beginButton : new sap.m.Button(
			{
				text : i18n.getText("global_Submit"),
				enabled : true,
				press : function() 
				{	
					var obj = [];
					var tmp = new Object();
					tmp.id = idEvent;
					tmp.check = "true";
					obj.push(tmp);					
					
					dialog.close();
					var amModel = new sap.ui.model.json.JSONModel();										
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.create('/admin/eventsCtrlHistoProcessAgain/EVENT', obj,null,
					function(oData, response) 
					{
						var json = JSON.parse(response.body);
						console.log(json);
						amModel.setData(json);
						sap.m.MessageToast.show(i18n.getText("event_Edit_successfull"));						
												
						if (!this.resizableDialog) 
						{
							this.resizableDialog = new sap.m.Dialog(
							{
								title : i18n.getText("global_resul_process"),
								contentWidth : "550px",
								contentHeight : "300px",
								resizable : true,
								content : [new sap.m.List("listResults").setModel(amModel).bindItems("/", new sap.m.StandardListItem({
									title : "{code} {field}",
									description: "{message}",
									type : "Active",
									icon : "sap-icon://less",
								}))],
								buttons : 
									[ new sap.m.Button({
									text : i18n.getText("global_Close"),
									press : function() {
										this.resizableDialog.close();
										this.resizableDialog.destroy(true);
										this.resizableDialog = null;
									}.bind(this)
								})]
							});

							// to get access to the global model
							view.addDependent(this.resizableDialog);
						}

						this.resizableDialog.open();


					}, function() {
						sap.m.MessageToast.show(i18n.getText("event_Edit_successfull"));
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
	
	//------------------------------------------------------
	handleShowFilter : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var view = this.getView();
		var dialog = new sap.m.Dialog({
			title : i18n.getText("logger_filters"),	
			type : 'Message',
			content : [ 
			
			new sap.m.Label({
				text : ' ',
				labelFor : 'submitDialogSlug',
				width : '100%'
			}),
			
			new sap.m.Label({
				text : i18n.getText("event_User_id"),
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
				text : i18n.getText("event_Start_date"),
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
					dialog.close();

					var oparameters = {
						user : sUser,
						date : sDate,
						typeDate : "start",						
					};

					var amModel = new sap.ui.model.json.JSONModel();

					var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel.create("/admin/eventsCtrlHisto",oparameters,null, function(oData, response) 
							{
								var json = JSON.parse(response.body);							
								amModel.setSizeLimit(json.length);
								amModel.setData(json);
								sap.ui.core.BusyIndicator.hide();
							}
					);
					
					view.setModel(amModel);
					window.objectTTT = view.byId("idPrincipalTable");
					view.byId("idPrincipalTable").setModel(amModel);
					sap.ui.getCore().setModel(amModel);
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

	
})
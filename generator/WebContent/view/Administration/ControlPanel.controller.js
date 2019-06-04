jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.view.Administration.ControlPanel", 
{	
	onInit : function() 
	{
		console.log("IN");
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var jobEvent = this.getView().byId("JobEventListener").setState(false);
		var jobEmployee =  this.getView().byId("JobEmployee").setState(false);
		var jobLoad = this.getView().byId("JobLoad").setState(false);
		window._this['ctrlEvents'] = this;
		window.refreshviewControlPanel = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/controlpanel/jobevent", {
				sync : false,
				success : function(oData, response) {
					var job = (response.body === 'true');
					jobEvent.setState(job);
					if(response.body == null || response.body ===''){
						jobEvent.setState(true);
					}
				},
				error : function(oData, response) {
					sap.m.MessageToast.show("Error" );
				}
			});
			
			oDataModel.read("/admin/controlpanel/jobemployee", {
				sync : false,
				success : function(oData, response) {
					var job2 = (response.body === 'true');
					jobEmployee.setState(job2);
					if(response.body == null || response.body ===''){
						jobEmployee.setState(true);
					}
				},
				error : function(oData, response) {
					sap.m.MessageToast.show("Error" );
				}
			});
			
			oDataModel.read("/admin/controlpanel/jobload", {
				sync : false,
				success : function(oData, response) {
					var job3 = (response.body === 'true');
					jobLoad.setState(job3);
					if(response.body == null || response.body ===''){
						jobLoad.setState(true);
					}
				},
				error : function(oData, response) {
					sap.m.MessageToast.show("Error" );
				}
			});
		}
		
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
		
		//load info events
		var amModel2 = new sap.ui.model.json.JSONModel();
		var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel2.read("/admin/events", {
			sync : false,
			success : function(oData, response) 
			{
				var json = JSON.parse(response.body);
				amModel2.setData(json);
				console.log(json);
				json[json.length] = {id : 0,eventId : "None"};
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});
		
		window._this["ctrlEvents"].getView().setModel(amModel2);
		
	},
	
	_handleRouteMatched : function(evt) {
		window.refreshviewControlPanel();		
	},
	
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationData");
	},
	
	onItemSelectedTableDelete:function(oEvent)
	{
		console.log(oEvent);		
		var idSelect = this.getView().byId("selectId").getSelectedKey();
		if(idSelect!=null && idSelect == "TABLE_MASSIVE_LOAD")
		{
			
			this.getView().byId("deleteParameter").setVisible(true);
			this.getView().byId("labelIdDelete").setVisible(true);
			
		}
		else
		{
			var idSelect = this.getView().byId("deleteParameter").setVisible(false);
			this.getView().byId("labelIdDelete").setVisible(false);
		}
	},
	
	onPressDelete:function(oEvent) 
	{
		var idSelect = this.getView().byId("selectId").getSelectedKey();
		var idDeleteParam = this.getView().byId("deleteParameter").getValue();
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/controlPanelDelAll/"+idSelect+"/"+idDeleteParam, {
			sync : false,
			success : function(oData, response) 
			{				
				var json = JSON.parse(response.body);				
				console.log(json);
				sap.m.MessageToast.show(json.message);
			},
			error : function(oData, response) {
				sap.m.MessageToast.show("Error deleted Data" );
			}
		});			
	},
	
	onPressSendEmailNoti:function(oEvent) 
	{
		var idSelect = this.getView().byId("selectIdNoti").getSelectedKey();
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/sendNotificationEventEmail/"+idSelect, {
			sync : false,
			success : function(oData, response) {
				
				sap.m.MessageToast.show("sent" );
			},
			error : function(oData, response) {
				sap.m.MessageToast.show("Error send" );
			}
		});			
	},
	
	OnPressJob : function(){
		var switchJob = this.getView().byId("JobEventListener").getState();//JobEmployee
		var switchJob2 = this.getView().byId("JobEmployee").getState();
		var switchJob3 = this.getView().byId("JobLoad").getState();
		
		var parameters = {
			JobEventListener : switchJob,
			JobEmployee : switchJob2,
			JobLoad : switchJob3
		}
		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.create("/admin/controlpanel/",parameters, {
			sync : false,
			success : function(oData, response) {
				sap.m.MessageToast.show("Successfull");
				window.refreshviewControlPanel();
			},
			error : function(oData, response) {
				sap.m.MessageToast.show("Error" );
			}
		});
	},
		
	onPressQuery:function(oEvent)
	{
		var queryValue = this.getView().byId("queryValue").getValue();
		var responseQuery = this.getView().byId("responseQueryValue");		
		var oparameters = {query : queryValue};
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.create("/admin/executeQuery/",oparameters,null, 
				function(oData, response) 
				{
					var json = JSON.parse(response.body);
					responseQuery.setValue(response.body);
					console.log(json);
					amModel.setData(json);					
				}
		);		
	},
	
	onGenerateQuery:function(oEvent)
	{
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var eventId = this.getView().byId("eventId").getSelectedItem().getText();
		var userIdEventValue = this.getView().byId("userIdEventValue").getValue();
		var userSeq = this.getView().byId("userSeq").getValue();		
		var sDate = this.getView().byId("dateEffEvent").getValue();
		
		if(eventId == "" || userIdEventValue == "" || userSeq  == "" || sDate ==""){
			sap.m.MessageToast.show(i18n.getText('document_Value_not_empty'));
		}
		else{
			
			var oparameters = 
			{
				eventId : eventId, 
				userIdEventValue:userIdEventValue, 
				dateEffEvent:sDate,
				userSeq:userSeq
			};
			
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.create("/admin/generateEventList/",oparameters,null, 
					function(oData, response) 
					{
						sap.m.MessageToast.show(response.body);
					}
			);	
		}
			
	},
	
	onSearchAttchQuery:function(oEvent)
	{
		var i18n = this.getView().getModel("i18n").getResourceBundle();		
		var userIdEventValue = this.getView().byId("userIdEventValue").getValue();				
		var sDateInit = this.getView().byId("dateInitAttach").getValue();
		var sDateEnd = this.getView().byId("dateEndAttach").getValue();
			
		var oparameters = 
		{	 
			userIdEventValue:userIdEventValue, 
			dateInit:sDateInit,
			dateEnd:sDateEnd
		};
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.create("/admin/generateEventAttach/",oparameters,null, 
				function(oData, response) 
				{
					sap.m.MessageToast.show(response.body);
				}
		);			
	},	
	
	onVersionInfo: function(oEvent) {
		sap.m.MessageBox.information("Version 3.1.2v 19.4.3d");
	}
})


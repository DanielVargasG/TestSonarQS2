sap.ui.controller("generator.view.Administration.EventListenerCtrlAttach", {

	//-------------------------------------------------
	onInit : function() {

		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
		
		//load status ctrl event attach
		var amModelStatusCtrAttachEvent = new sap.ui.model.json.JSONModel();
		var oDataModelStatusCtrAttachEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelStatusCtrAttachEvent.read("/admin/statusCtrEventAttach", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelStatusCtrAttachEvent.setSizeLimit(json.length);
				amModelStatusCtrAttachEvent.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});		
		
		sap.ui.getCore().setModel(amModelStatusCtrAttachEvent, "modelStatusCtrAttachEvent");		
	},
	//-------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		if (evt.getParameter("name") !== "/adm/evenlisterctrattach") {
			return;
		}
		
		var varId = evt.getParameter("arguments").idCtrl;
		window.idProcess = varId;
		window.refreshview = this.refreshView();		
	},
	//-------------------------------------------------
	//Refresh view
	refreshView : function(){
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/eventsCtrlAttach/process/"+window.idProcess, {
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
	onChangeStatus:function(oEvent)
	{
		var idEvent = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());					
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectStatus = new sap.ui.core.Item({
			key : "{modelStatusCtrAttachEvent>id}",
			text : "{modelStatusCtrAttachEvent>label}"
		});	
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_Edit_status"),
			type : 'Message',
			content : [ 
			
			new sap.m.Label({text : i18n.getText("event_New_status"),labelFor : '',width : '90%'}), 
			
			new sap.m.Select('submitDialogStatus',{width : '100%'}).bindAggregation("", {
				path : "modelStatusCtrAttachEvent>/",
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

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Submit"),
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
					oDataModel2.create('/admin/eventsCtrl/uploadCtrlDocEvent/' + idEvent, oparameters, null, function(oData, response) {
						var json = JSON.parse(response.body);												
						sap.m.MessageToast.show(i18n.getText("event_Updated"));	

					}, function() {
						sap.m.MessageToast.show(i18n.getText("event_Updated_error"));
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
		this._oRouter.myNavBack("/adm/evenlisterctrprocess");
	},
	showInfo : function(oEvent) {
		this.refreshView();
	}	
	
	//-------------------------------------------------	
	//Delete event listener	
	//-------------------------------------------------
	//Edit Event Listener		
	//-------------------------------------------------
	//Add Event listener	
	//-------------------------------------------------
})
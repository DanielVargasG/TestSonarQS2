jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.view.Administration.synch.SynchHisto", {

	//-------------------------------------------------
	onInit : function() {

		window.keysAttachSelect = new Map();
		window.idAttachSelect = new Array();
		
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		var amModel = new sap.ui.model.json.JSONModel();		
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	//-------------------------------------------------
	_handleRouteMatched : function(evt) {
		// Check whether is the detail page is matched.
		console.log(evt.getParameter("name"));
		if (evt.getParameter("name") !== "/adm/synchhisto") {
			return;
		}
		
		var varId = evt.getParameter("arguments").idCtrlH;
		window.idEventHisto = varId;
		window.refreshview = this.refreshView();		
	},
	//-------------------------------------------------
	//Refresh view
	refreshView : function(){
		console.log("ok");
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/attachmentHistory", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				console.log(json);
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

		console.log('on est de retour');
		
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

	//-------------------------------------------------
	//Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administrationAttachment");
	},
	
	onDeleteAll:function(oEvent)
	{
		var idValue = this.getView().getModel().getProperty("id", oEvent.getSource().getBindingContext());
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/attachmentsHistoDeleteAll/", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
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
	
	//-------------------------------------------------
	onPressCheckHisto : function(evt){
		
		var bindingContext = evt.getSource().getBindingContext();
		var res = bindingContext.sPath.split("/");
		var oEventBus = sap.ui.getCore().getEventBus();
		var value = evt.getParameter('selected');
		var clave=bindingContext.oModel.oData[res[1]].id;
		window.idAttachSelect.push(evt.getParameter('id'));		
		window.keysAttachSelect.set(clave,value);
		console.log(clave+" "+value);
	
	},
	
	onPressProcessAgain:function(oEvent)
	{						
		var i18n = this.getView().getModel("i18n").getResourceBundle();		
		var view = this.getView();
		
		var obj = [];
		var isTrue=false;
		window.keysAttachSelect.forEach(function(valor, clave) {
			  var tmp = new Object();
			  tmp.id = clave;
			  tmp.check = valor;
			  obj.push(tmp);
			  if(valor) {isTrue=true;}
			});
		
		if(isTrue)
		{		
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
						dialog.close();
						var amModel = new sap.ui.model.json.JSONModel();
						
											
						var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
						oDataModel2.create('/admin/eventsCtrlHistoProcessAgain/SYNC', obj, null, 
						function(oData, response) 
						{
							var json = JSON.parse(response.body);
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
						
						window.keysAttachSelect=new Map();
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
			
			for (var i = 0; i < window.idAttachSelect.length; i++) { 
				console.log(window.idAttachSelect[i]);
				sap.ui.getCore().byId(window.idAttachSelect[i]).setSelected(false);
			}
			window.idAttachSelect = new Array();
		}
		else
		{
			var msg = sap.m.MessageBox.alert(i18n.getText("global_select_alert"), {
			    title: "Alert",
			    onClose: null,  
			    styleClass: ""  , 
			    initialFocus: null , 
			    textDirection: sap.ui.core.TextDirection.Inherit 
			    });
		}

		dialog.open();
		
	},
	
	//-------------------------------------------------	
	//Delete event listener	
	//-------------------------------------------------
	//Edit Event Listener		
	//-------------------------------------------------
	//Add Event listener	
	//-------------------------------------------------
})
jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.controller.administration.MassiveLoadUserDoc", {
	
	
	onInit : function() {
		console.log("IN");
		this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
		//load status ctrl event
		var amModelStatusCtr = new sap.ui.model.json.JSONModel();
		var oDataModelStatusCtr = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModelStatusCtr.read("/admin/statusCtrEvent", {
			sync : false,
			success : function(oData, response) {
				var json = JSON.parse(response.body);
				amModelStatusCtr.setSizeLimit(json.length);
				amModelStatusCtr.setData(json);
			},
			error : function(oData, response) {
				// items = oData.results.length;
			}
		});		
		
		sap.ui.getCore().setModel(amModelStatusCtr, "modelStatusCtr");	
		
		window._this['ctrlMassiveLoadDoc'] = this;
		window.refreshviewMassLoadDet = function() {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/massive/attach/"+window.idUser+"/"+window.idCtrlHisto, {
				sync : false,
				success : function(oData, response) {
					var json = JSON.parse(response.body);
					console.log(json);
					amModel.setSizeLimit(json.length);
					amModel.setData(json);
					
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlMassiveLoadDoc'].getView().setModel(amModel, "userdet");
			window._this['ctrlMassiveLoadDoc'].getView().byId("idPrincipalTable").setModel(amModel);
		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	
	_handleRouteMatched : function(evt) {
		window.idUser = evt.getParameter("arguments").idUser;
		window.idMass = evt.getParameter("arguments").idMass;
		window.idCtrlHisto = evt.getParameter("arguments").idCtrlHisto;
		
		console.log(evt.getParameter("arguments"));
		console.log(window.idUser+" "+window.idMass+" "+window.idCtrlHisto);
		window.refreshviewMassLoadDet ();
		
	},
	
	//
	handleRefreshData : function(oEvent){
		window.refreshviewMassLoadDet();
	},
	
	// Back menu
	onNavPressed : function(oEvent) {
		console.log("aquio que tiene "+window.idMass)
		this._oRouter.myNavBack("massiveLoadUserDet",{idMass:window.idMass, idUser:window.idUser,idCtrlHisto : window.idCtrlHisto});		
	},
	
	onChangeMassDoc : function(oEvent){
		var idEvent = this.getView().getModel("userdet").getProperty("id", oEvent.getSource().getBindingContext());
		var idStatus = this.getView().getModel("userdet").getProperty("status", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oItemSelectStatus = new sap.ui.core.Item({
			key : "{modelStatusCtr>id}",
			text : "{modelStatusCtr>label}"
		});
		
		var dialog = new sap.m.Dialog({
			title : i18n.getText("event_Change_status"),
			type : 'Message',
			content : [ 
			
			new sap.m.Label({text : i18n.getText("event_New_status"),labelFor : '',width : '90%'}), 
			
			new sap.m.Select('submitDialogStatus',{width : '100%'}).bindAggregation("", {
				path : "modelStatusCtr>/",
				template : oItemSelectStatus
			}).setSelectedKey(idStatus)

			],
			beginButton : new sap.m.Button({
				text : i18n.getText("global_Edit"),
				enabled : true,
				press : function() {
					var sStatus = sap.ui.getCore().byId('submitDialogStatus').getSelectedItem().getKey();
					dialog.close();

					var amModel = new sap.ui.model.json.JSONModel();
					
					var oDataModel2 = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
					oDataModel2.read('/admin/massiveuserdetupdate/' + idEvent+"/"+sStatus, {
						sync : false,
						success : function(oData, response) {
							var json = JSON.parse(response.body);
							sap.m.MessageToast.show(i18n.getText("loadUser_Update_massive"));
							window.refreshviewMassLoadDet();
						},
						error : function(oData, response) {
							sap.m.MessageToast.show(i18n.getText("loadUser_Update_error"));
						}
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
	
	onObservationPress:function(oEvent){
		var observation =this.getView().getModel("userdet").getProperty("observations", oEvent.getSource().getBindingContext());
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var message = sap.m.MessageBox.show(observation, {
			icon: sap.m.MessageBox.Icon.NONE,
		    title: i18n.getText("global_Observations"),
		    onClose: null,
		    styleClass: "",
		    initialFocus: null ,
		    textDirection: sap.ui.core.TextDirection.Inherit
		    });
	}
	
})


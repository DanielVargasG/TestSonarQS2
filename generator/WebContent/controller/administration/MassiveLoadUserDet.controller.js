jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.controller.administration.MassiveLoadUserDet", {
	
	
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
		
		window._this['ctrlMassiveLoadDet'] = this;
				
		window.refreshviewMassLoadPage = function(page) {
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/massiveuserdetpage/"+window.idMass+"/"+window.pageuser, {
				sync : false,
				success : function(oData, response) {
					if(response.body == ""){
						var json = {};
						console.log(json);
						amModel.setSizeLimit(json.length);
						amModel.setData(json);
						window._this['ctrlMassiveLoadDet'].getView().byId("btnNext").setEnabled(false);
					}else{
						var json = JSON.parse(response.body);
						console.log(json);
						amModel.setSizeLimit(json.length);
						amModel.setData(json);
						window._this['ctrlMassiveLoadDet'].getView().byId("btnNext").setEnabled(true);
					}
				},
				error : function(oData, response) {
					// items = oData.results.length;
				}
			});
			window._this['ctrlMassiveLoadDet'].getView().setModel(amModel, "userdet");
			window._this['ctrlMassiveLoadDet'].getView().byId("idPrincipalTable").setModel(amModel);
			if(window.pageuser == 1){
				window._this['ctrlMassiveLoadDet'].getView().byId("btnPrev").setEnabled(false);
			}else{
				window._this['ctrlMassiveLoadDet'].getView().byId("btnPrev").setEnabled(true);
			}
		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	
	_handleRouteMatched : function(evt) {
		window.idMass = evt.getParameter("arguments").idMass;
		console.log("--- id massive "+window.idMass);
		window.pageuser = 1
		window.refreshviewMassLoadPage (window.pageuser);
		
	},
	
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("massiveLoadUser");
	},
	
	onChangeUser : function(oEvent){
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
		var ObservationValue =this.getView().getModel("userdet").getProperty("observations", oEvent.getSource().getBindingContext());
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
		
		var find = 'quot;';
		var re = new RegExp(find, 'g');
		res = res.replace(re, " ");
		
		var find = '&amp;';
		var re = new RegExp(find, 'g');
		res = res.replace(re, " ");
		
		var find = '{';
		var re = new RegExp(find, 'g');
		res = res.replace(re, "(");
		
		var find = '}';
		var re = new RegExp(find, 'g');
		res = res.replace(re, ")");
		
		
		var message = sap.m.MessageBox.show(res, 
		{
			icon: sap.m.MessageBox.Icon.NONE,
		    title: i18n.getText("global_Observations"),
		    onClose: null,
		    styleClass: "",
		    initialFocus: null ,
		    textDirection: sap.ui.core.TextDirection.Inherit
		    });
	},
	
	onUserPress : function(oEvent){
		window.idUser = this.getView().getModel("userdet").getProperty("id", oEvent.getSource().getBindingContext());
		window.idCtrlHisto = this.getView().getModel("userdet").getProperty("eventLCtrlHistoryId", oEvent.getSource().getBindingContext());
		
		if(window.idCtrlHisto==null)
			window.idCtrlHisto = 0;
		
		console.log("---------- doc of "+window.idUser+" -- masive "+window.idMass+" --histo "+window.idCtrlHisto);
		this._oRouter.navTo("massiveLoadUserDoc", {idMass : window.idMass,
												   idUser : window.idUser, 
												   idCtrlHisto : window.idCtrlHisto});
	},
	
	onPagePrev :function(){
		console.log("previous");
		if(window.pageuser >1){
			window.pageuser = window.pageuser -1;
			window.refreshviewMassLoadPage(window.pageuser);
		}
		else if(window.pageuser == 1){
			window.refreshviewMassLoadPage(window.pageuser);
		}
	},
	
	onPageNext : function(){
		window.pageuser = window.pageuser +1;
		window.refreshviewMassLoadPage(window.pageuser);
	},
	
	handleRefreshData : function(){
		window.refreshviewMassLoadPage(window.pageuser);
	}
	
})


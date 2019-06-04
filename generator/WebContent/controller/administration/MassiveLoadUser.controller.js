jQuery.sap.require("sap.m.MessageBox");
sap.ui.controller("generator.controller.administration.MassiveLoadUser", {
	
	onInit : function() 
	{		
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
		window._this['ctrlMassiveLoad'] = this;
		
		window.refreshAdmParamById = function() 
		{		
			oDataModelStatusCtr.read("/admin/admParameterByName/massive_download_url", {
				sync : false,
				success : function(oData, response) 
				{
					var json = JSON.parse(response.body);
					window.massive_url_download = json.value;
					
				},
				error : function(oData, response) {
					window.massive_url_download = "";		
				}
			});
		}
		
		window.refreshviewMassLoad = function() 
		{	
			//load status ctrl event
			var amModelLastExecution = new sap.ui.model.json.JSONModel();
			var oDataModelLastCtrEvent = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModelLastCtrEvent.read("/admin/massive/last_executions", {
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
			window._this['ctrlMassiveLoad'].getView().byId("idTableLastProcess").setModel(amModelLastExecution);
			
			var amModel = new sap.ui.model.json.JSONModel();
			var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
			oDataModel.read("/admin/massiveuserall", {
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
			window._this['ctrlMassiveLoad'].getView().setModel(amModel, "userdet");
			window._this['ctrlMassiveLoad'].getView().byId("idPrincipalTable").setModel(amModel);
		}
		
		this._oRouter.attachRoutePatternMatched(this._handleRouteMatched, this);
	},
	
	_handleRouteMatched : function(evt) {
		
		window.refreshviewMassLoad();
		window.refreshAdmParamById();
		
	},
	
	// Back menu
	onNavPressed : function(oEvent) {
		this._oRouter.myNavBack("administration");
	},
	
	handleUploadComplete : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var sResponse = oEvent.getParameter("response");
		if (oEvent.getParameter("status")== 1001){
			sap.m.MessageToast.show(i18n.getText("loadUser_error_1001"));
			return;
		}
		else if (oEvent.getParameter("status")== 1002){
			sap.m.MessageToast.show(i18n.getText("loadUser_error_1002"));
			return;
		}
		
		
		else if (oEvent.getParameter("status")== 500){
			sap.m.MessageToast.show(i18n.getText("eventexpimp_Upload_error"));
			return;
		}
		
		if (sResponse) {
			var sMsg = "";
			var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
			if (sResponse == "200") {
				sMsg = i18n.getText("eventexpimp_Upload_success");
				oEvent.getSource().setValue("");
				window.refreshviewMassLoad();
				window.refreshAdmParamById();
				
			} else {
				sMsg = i18n.getText("eventexpimp_Upload_error");
			}
			sap.m.MessageToast.show(sMsg);
		} else {
			sMsg = i18n.getText("eventexpimp_Upload_success");
			oEvent.getSource().setValue("");
			sap.m.MessageToast.show(sMsg );
			
		}
		window.refreshviewMassLoad();
		window.refreshAdmParamById();
		
	},

	// -------------------------------------------------
	handleTypeMissmatch: function(oEvent) {
		var aFileTypes = oEvent.getSource().getFileType();
		jQuery.each(aFileTypes, function(key, value) {
			aFileTypes[key] = "*." + value
		});
		var sSupportedFileTypes = aFileTypes.join(", ");
		sap.m.MessageToast.show(i18n.getText("templatedetails_File_type") + oEvent.getParameter("fileType") + i18n.getText("templatedetails_Not_supported_choose_types") + sSupportedFileTypes);
	},

	// -------------------------------------------------
	onPressImport : function(oEvent) {
		var i18n = this.getView().getModel("i18n").getResourceBundle(); 
		var oFileUploader = this.getView().byId("loadUser");
		if (!oFileUploader.getValue()) {
			sap.m.MessageToast.show(i18n.getText("templatedetails_Choose_file_upload"));
			return;
		}
		
		//oFileUploader.setSendXHR("true");
		var nameLoad = this.getView().byId("nameLoad").getValue();
		var onbAttach = this.getView().byId("onbAttach").getSelected();
		var recAttach = this.getView().byId("recAttach").getSelected();
		var ecAttach = this.getView().byId("ecAttach").getSelected();
		var tmpAttach = this.getView().byId("tmpAttach").getSelected();
		
		var attachment = "NA";
		
		if(onbAttach)
			attachment += "ONB@@";
		
		if(recAttach)
			attachment += "REC@@";
		
		if(ecAttach)
			attachment += "EMC@@";		
		
		if(tmpAttach)
			attachment += "TMP";		
		
		if(nameLoad=="")
			nameLoad = "-1";
		
		oFileUploader.setUploadUrl("/generator/rst/json/admin/massive/users/"+nameLoad+"/"+attachment);
		oFileUploader.upload();
		
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
			title : i18n.getText("global_Edit"),
			type : 'Message',
			content : [ 
			
			new sap.m.Label({text : i18n.getText("event_New_status"),labelFor : '',width : '90%'}), 
			
			new sap.m.Select('submitDialogStatus',{width : '100%'}).bindAggregation("", {
				path : "modelStatusCtr>/",
				template : oItemSelectStatus
			}).setSelectedKey(idStatus),
			
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
				text : i18n.getText("global_Edit"),
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
					oDataModel2.create('/admin/massiveuserupdate/' + idEvent, oparameters, null,
						function(oData, response) 
						{
							var json = JSON.parse(response.body);
							sap.m.MessageToast.show(i18n.getText("global_Update"));
							window.refreshviewMassLoad();
						},
						function() {
							sap.m.MessageToast.show(i18n.getText("global_Error"));
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
	
	onObservationPress : function(oEvent){
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
		
		var message = sap.m.MessageBox.show(res, {
			icon: sap.m.MessageBox.Icon.NONE,
		    title: i18n.getText("global_Observations"),
		    onClose: null,
		    styleClass: "",
		    initialFocus: null ,
		    textDirection: sap.ui.core.TextDirection.Inherit
		    });
	},
	
	onUserPress : function(oEvent){
		window.idMass = this.getView().getModel("userdet").getProperty("id", oEvent.getSource().getBindingContext());
		this._oRouter.navTo("massiveLoadUserDet", {
			idMass : window.idMass
		});
	},
	
	onPressStatistics: function(oEvent)
	{
		var amModelStatis = new sap.ui.model.json.JSONModel();
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		var id = this.getView().getModel("userdet").getProperty("id", oEvent.getSource().getBindingContext());
		var nameMassLoad = this.getView().getModel("userdet").getProperty("nameLoad", oEvent.getSource().getBindingContext());

		
		oDataModel.read("/admin/massivedownload/"+id+"/statistics", {
			sync : false,
			success : function(oData, response) 
			{				
				json = JSON.parse(response.body);
				amModelStatis.setData(json);
			},
			error : function(oData, response) {			
				sap.m.MessageToast.show(i18n.getText("loadUser_Error_upload"));
			}
		});

		
		if (!this.resizableDialog) 
		{
			this.resizableDialog = new sap.m.Dialog(
			{
				title : i18n.getText("global_statistics")+" ( "+nameMassLoad+")",
				contentWidth : "550px",
				contentHeight : "300px",
				resizable : true,
				content : [new sap.m.List("listUser").setModel(amModelStatis).bindItems("/statisticsList/", new sap.m.StandardListItem({
					title : "{label}",
					description: "{value}",
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
			this.getView().addDependent(this.resizableDialog);
		}

		this.resizableDialog.open();		

	},
	
	onPressCurrent : function(oEvent){
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/massiveusertime/"+false, {
			sync : false,
			success : function(oData, response) {
				var json;
				if(response.body == null || response.body == ""){
					json = {};
				}else{
					json = JSON.parse(response.body);
				}
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				
			},
			error : function(oData, response) {
				// items = oData.results.length;
				sap.m.MessageToast.show(i18n.getText("loadUser_Error_upload"));
			}
		});
		window._this['ctrlMassiveLoad'].getView().setModel(amModel, "userdet");
		window._this['ctrlMassiveLoad'].getView().byId("idPrincipalTable").setModel(amModel);
		
	},
	
	onPressFuture : function(oEvent){
		
		var amModel = new sap.ui.model.json.JSONModel();
		var oDataModel = new sap.ui.model.odata.ODataModel("/generator/rst/json", true);
		oDataModel.read("/admin/massiveusertime/"+true, {
			sync : false,
			success : function(oData, response) {
				var json;
				if(response.body == null){
					json = null;
				}else{
					json = JSON.parse(response.body);
				}
				amModel.setSizeLimit(json.length);
				amModel.setData(json);
				
			},
			error : function(oData, response) {
				// items = oData.results.length;
				sap.m.MessageToast.show(i18n.getText("loadUser_Error_upload"));
			}
		});
		window._this['ctrlMassiveLoad'].getView().setModel(amModel, "userdet");
		window._this['ctrlMassiveLoad'].getView().byId("idPrincipalTable").setModel(amModel);
	},
	
	handleRefreshData : function(oEvent){
		window.refreshviewMassLoad();
		window.refreshAdmParamById();
	},
	
	onDownloadError : function(oEvent)
	{
		var i18n = this.getView().getModel("i18n").getResourceBundle();
		var status = ["TimeOut","ErrorPpd","ErrorFields","Error","NoExist","Terminate","Pending","Loading","Processing","TerminateByUser"];
		var id = this.getView().getModel("userdet").getProperty("id", oEvent.getSource().getBindingContext());
		var title = this.getView().getModel("userdet").getProperty("nameLoad", oEvent.getSource().getBindingContext());
		
		if(window.massive_url_download!=null && window.massive_url_download!="")
		{
			var dialog = new sap.m.Dialog({title : i18n.getText("status_log_generate"),type : 'Message',
				content : 
				[
							
					new sap.m.Label({text :i18n.getText("status_log_select_status"),labelFor : '',width : '90%'}),
					new sap.m.Label({text :"",labelFor : '',width : '100%'}),
					
					new sap.m.CheckBox('Errors', {width : '20%',selected : true}),
					new sap.m.Label({text :i18n.getText("status_log_data_error"),labelFor : '',width : '90%'}),
					
					new sap.m.CheckBox('ErrorPpd', {width : '20%',selected : true}),				
					new sap.m.Label({text :i18n.getText("status_log_conection_error_ppd"),labelFor : '',width : '90%'}),
					
					new sap.m.CheckBox('Pending', {width : '20%',selected : true}),				
					new sap.m.Label({text :i18n.getText("status_log_pending_error"),labelFor : '',width : '90%'}),
					
					new sap.m.CheckBox('Successful', {width : '20%',selected : true}),
					new sap.m.Label({text :i18n.getText("status_log_successful"),labelFor : '',width : '90%'})

				],
				beginButton : new sap.m.Button({
					text : i18n.getText("global_download"),
					enabled : true,
					press : function() 
					{					
						var errors = sap.ui.getCore().byId('Errors').getSelected();
						var errorPpd = sap.ui.getCore().byId("ErrorPpd").getSelected();
						var errorFields = sap.ui.getCore().byId("Pending").getSelected();
						var successful = sap.ui.getCore().byId("Successful").getSelected();
											
						var status = "";
						var statusFilter = "";
						
						if(errors)
							status += "Errors@@";
						
						if(errorPpd)
							status += "ErrorPpd@@";
						
						if(errorFields)
							status += "Pending@@";
						
						if(successful)
							status += "Successful@@";				
						
						if(status!=""){
							statusFilter = status.substring(0,status.length-2);
						}
						else
							statusFilter = "NA";
						
						//window.open(window.massive_url_download+"/"+id+"/"+statusFilter, '_blank');
						var xhr = new XMLHttpRequest();
						var type = XMLHttpRequest.responseType;
						
						xhr.open('GET',window.massive_url_download+"/"+id+"/"+statusFilter, true);
						xhr.timeout = 180000; // time in milliseconds
						
						xhr.responseType  = "text";
						
						sap.ui.core.BusyIndicator.show(0);
						xhr.onreadystatechange = function() 
						{
						    if (xhr.readyState == 4 && xhr.status == 200) 
						    {   
						        var exportFilename = title+".csv"; 
						    	var csvData = new Blob([xhr.responseText], {type: 'text/csv;charset=utf-8;'});
						    	//IE11 & Edge
						    	if (navigator.msSaveBlob) 
						    	{
						    	    navigator.msSaveBlob(csvData, exportFilename);
						    	    sap.ui.core.BusyIndicator.hide();
						    	} else 
						    	{
						    	    //In FF link must be added to DOM to be clicked
						    	    var link = document.createElement('a');
						    	    link.href = window.URL.createObjectURL(csvData);
						    	    link.setAttribute('download', exportFilename);
						    	    document.body.appendChild(link);    
						    	    link.click();
						    	    document.body.removeChild(link);
						    	    sap.ui.core.BusyIndicator.hide();
						    	}
						        
						    }
						};
						
						xhr.onload = function () {
							sap.ui.core.BusyIndicator.hide();
							console.log("return file");
						};

						xhr.ontimeout = function (e) {
						  // XMLHttpRequest timed out. Do something here.
							sap.ui.core.BusyIndicator.hide();
							console.log("time ouyt");
						};

						xhr.send();
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
		}
		else
		{
			var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			sap.m.MessageBox.error(i18n.getText("adminparameter_error_null")+" massive_download_url", {styleClass: bCompact ? "sapUiSizeCompact" : ""}
			);
		}		
	},	
	
})


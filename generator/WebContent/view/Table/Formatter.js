sap.ui.define(function() {
	"use strict";

	var Formatter = {

		weightState : function(fValue) {
			try {

				if (fValue == "Pending") {
					return "Warning";
				} else if (fValue == "Validated") {
					return "Success";
				} else if (fValue < 2000) {
					return "Warning";
				} else {
					return "Error";
				}
			} catch (err) {
				return "None";
			}
		},

		status : function(fValue) {
			try {
				if (fValue === "Error") {
					new sap.ui.core.IconPool.getIconURI("accept");
				}
			} catch (err) {
				return null;
			}
		}

	};

	return Formatter;

}, /* bExport= */true);
/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
if(navigator.userAgent.toLowerCase().indexOf('firefox') > -1){
	if (jsObject && jsObject.forEach) {
		jsObject.forEach(function(e) {
			Object.keys(e).filter(
				function(k) {
					return e.hasOwnProperty(k)
							&& (k.indexOf('_') == 0 || k == '$' || k == 'X') && e[k];
			}).forEach(function(key) {
					e[key].toJSON = function() {
						return;
					}
			});
		});
	};
}


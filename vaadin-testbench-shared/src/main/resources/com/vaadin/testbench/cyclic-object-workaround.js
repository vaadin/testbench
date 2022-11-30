/*
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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


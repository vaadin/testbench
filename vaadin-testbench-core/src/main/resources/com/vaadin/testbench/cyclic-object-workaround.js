/*
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
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


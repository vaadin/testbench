if (jsObject.forEach) {
	jsObject.forEach(function(e) {
		Object.keys(e).filter(
			function(k) {
				return e.hasOwnProperty(k)
						&& (k.indexOf('__') == 0 || k == '$') && e[k];
		}).forEach(function(key) {
				e[key].toJSON = function() {
					return;
				}
		});
	});
};

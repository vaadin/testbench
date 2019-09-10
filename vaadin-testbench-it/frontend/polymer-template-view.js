import {PolymerElement, html} from '@polymer/polymer/polymer-element.js';

class PolymerTemplateView extends PolymerElement {
    static get is() {
	return 'polymer-template-view';
    }
    static get template() {
	return html`
	    <div>
			<button class="button button-shadow-1" id="shadow-button-1">Shadow Button 1</button>
	    </div>
	    <slot>XXX</slot>
	    <div>
			<button class="button button-shadow-2" id="shadow-button-2">Shadow Button 2</button>
	    </div>
	    <slot name="special-slot"></slot>
	    <div>
			<button class="button button-shadow-special" id="special-button">Special Button (in Shadow DOM)</button>
	    </div>
	    <button class="button" id="foo'*+bar'">Button with special id</button>`;
    }
}

window.customElements.define(PolymerTemplateView.is, PolymerTemplateView);


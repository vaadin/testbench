// Import the PolymerElement base class and html helper
import {PolymerElement, html} from '@polymer/polymer';

// Define an element class
class PolymerTemplateView extends PolymerElement {

    // Define public API properties
    static get properties() {
        return {liked: Boolean}
    }

    // Define the element's template
    static get template() {
        return html`
            <div>
                <button class="button button-shadow-1" id="shadow-button-1">
                    Shadow Button 1
                </button>
            </div>
            <slot></slot>
            <div>
                <button class="button button-shadow-2" id="shadow-button-2">
                    Shadow Button 2
                </button>
            </div>
            <slot name="special-slot"></slot>
            <div>
                <button class="button button-shadow-special"
                        id="special-button">Special Button (in Shadow DOM)
                </button>
            </div>
            <button class="button" id="foo'*+bar'">Button with special id
            </button>
        `;
    }
}

// Register the element with the browser
customElements.define('polymer-template-view', PolymerTemplateView);

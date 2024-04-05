import { html, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('caption-component')
class CaptionComponent extends LitElement {
    @property({ type: String })
    label!: string;

    @property({ type: String })
    placeholder!: string;

    render() {
        return html`
            <div>
                <div id="label">${this.label}</div>
                <div id="placeholder">${this.placeholder}</div>
            </div>
        `;
    }
}

// customElements.define('caption-component', CaptionComponent);

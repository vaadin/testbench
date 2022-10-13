import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators';

@customElement("template-view")
export class TemplateView extends LitElement {

    render() {
        return html`        <div>
        <button class="button button-shadow-1" id="shadow-button-1">Shadow Button 1</button>
    </div>
    <slot></slot>
    <div>
        <button class="button button-shadow-2" id="shadow-button-2">Shadow Button 2</button>
    </div>
    <slot name="special-slot"></slot>
    <div>
        <button class="button button-shadow-special" id="special-button">Special Button (in Shadow DOM)</button>
    </div>
    <button class="button" id="foo'*+bar'">Button with special id</button>

`;
    }
}
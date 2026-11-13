// An external file, so the page also runs under script-src 'self'.
class XInner extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'}).innerHTML =
            `<span class="x" data-name="${this.getAttribute('name')}-shadow">deep</span>`;
    }
}
customElements.define('x-inner', XInner);

class XHost extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'}).innerHTML =
            '<span class="x" data-name="shadow1">shadow1</span><x-inner name="inner"></x-inner><slot></slot>';
    }
}
customElements.define('x-host', XHost);

class XClosed extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'closed'}).innerHTML = '<span class="x" data-name="closed">closed</span>';
    }
}
customElements.define('x-closed', XClosed);

class XText extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'}).innerHTML = '<b>S[</b><slot></slot><b>]</b>';
    }
}
customElements.define('x-text', XText);

// The control for the CSP probe: what the page's own code may do
try {
    window.pageEval = String(eval('1'));
} catch (e) {
    window.pageEval = e.name;
}

window.keyLog = [];
document.getElementById('keys').addEventListener('keydown', e => window.keyLog.push(
    {key: e.key, code: e.code, location: e.location,
     mods: (e.shiftKey ? 'S' : '') + (e.ctrlKey ? 'C' : '') + (e.altKey ? 'A' : '') + (e.metaKey ? 'M' : '')}));

// The page's own mouse: what a drag or an Actions click sends
window.mouseLog = [];
window.moves = 0;
['mousedown', 'mouseup', 'click', 'dblclick', 'contextmenu'].forEach(type =>
    document.addEventListener(type, e => window.mouseLog.push(type + ' ' + e.button
        + ' ' + e.detail + ' ' + (e.target.id || e.target.tagName) + ' '
        + e.clientX + ',' + e.clientY + (e.shiftKey ? ' shift' : ''))));
document.addEventListener('mousemove', () => window.moves++);
document.addEventListener('contextmenu', e => e.preventDefault());
['dragstart', 'drop'].forEach(type => document.addEventListener(type,
    e => window.mouseLog.push(type + ' ' + (e.target.id || e.target.tagName))));
document.getElementById('drop').addEventListener('dragover', e => e.preventDefault());

import { css, html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

/**
 * A minimal web component that mimics vaadin-grid's DOM structure to reproduce
 * MoveTargetOutOfBoundsException (#2156).
 *
 * The real Grid causes the exception because:
 * 1. The table has overflow:auto and the host/scroller have overflow:visible
 * 2. The tbody has position:sticky, so it stays in place when the table scrolls
 * 3. When scrollIntoView is called on a cell, the table scrolls but the sticky
 *    tbody prevents the cell from moving visually
 * 4. The Grid virtualizer updates tbody.transform on requestAnimationFrame to
 *    compensate for the scroll offset, but this happens AFTER Chrome's
 *    Actions.moveToElement reads the element position
 * 5. Chrome sees the cell still at x>viewport and throws
 *    MoveTargetOutOfBoundsException
 *
 * This component reproduces that exact behavior: tbody is sticky, and a scroll
 * listener applies the compensating transform asynchronously via rAF.
 */
@customElement("grid-like-container")
export class GridLikeContainer extends LitElement {

    static styles = css`
        :host {
            display: flex;
            position: relative;
            overflow: visible;
            height: 50px;
        }
        #scroller {
            display: flex;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            overflow: visible;
        }
        table {
            display: flex;
            flex-direction: column;
            width: 100%;
            overflow: auto;
            position: relative;
        }
        tbody {
            position: sticky;
            left: 0;
            display: block;
        }
        tr {
            display: flex;
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
        }
        td {
            position: relative;
            box-sizing: border-box;
            border: 1px solid #ccc;
            padding: 8px;
        }
        .col-first {
            min-width: 300px;
            width: 300px;
        }
        .col-spacer {
            min-width: 3000px;
            width: 3000px;
        }
        .col-target {
            min-width: 100px;
            width: 100px;
        }
    `;

    render() {
        return html`
            <div id="scroller">
                <table>
                    <tbody>
                        <tr>
                            <td class="col-first">
                                <slot name="first"></slot>
                            </td>
                            <td class="col-spacer">
                                <slot name="spacer"></slot>
                            </td>
                            <td class="col-target">
                                <slot name="target"></slot>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        `;
    }

    firstUpdated() {
        const table = this.shadowRoot!.querySelector('table')!;
        const tbody = this.shadowRoot!.querySelector('tbody')!;

        // Mimic Grid's virtualizer: on scroll, apply a compensating transform
        // to tbody on the next animation frame. The sticky positioning keeps
        // the tbody in place during scroll, and the transform adjusts its
        // position to match the scroll offset.
        //
        // A guard flag prevents a feedback loop: applying the transform can
        // cause the browser to adjust scrollLeft (sticky + transform
        // interaction), which would fire another scroll event and overwrite
        // the transform. The flag ensures only the first scroll is handled.
        let rafPending = false;
        table.addEventListener('scroll', () => {
            if (rafPending) return;
            const sl = table.scrollLeft;
            rafPending = true;
            requestAnimationFrame(() => {
                tbody.style.transform = `translateX(${-sl}px)`;
            });
        });
    }
}

/**
 * Vaadin k6 Helper Functions
 *
 * Reusable utility functions for load testing Vaadin applications with k6.
 * Import these functions in your k6 test scripts to interact with Vaadin's
 * UIDL (UI Description Language) protocol.
 *
 * Usage:
 *   import { initPageLoad, vaadinRequest, vaadinUnloadRequest } from './vaadin-k6-helpers.js';
 *
 *   const BASE_URL = "http://localhost:8080";
 *   const vaadinInfo = initPageLoad(BASE_URL);
 *   vaadinRequest(BASE_URL, vaadinInfo, `[{...}]`, 0, "");
 */

import http from "k6/http";
import { check, fail } from "k6";

/**
 * Creates HTTP request parameters with standard headers for Vaadin UIDL requests.
 * @param {string} baseUrl - The base URL of the application
 * @param {string} route - The current route path for the Referer header
 * @returns {object} HTTP request parameters with headers and cookies
 */
export function createBaseParams(baseUrl, route) {
    return {
        headers: {
            "Proxy-Connection": `keep-alive`,
            "Content-type": `application/json; charset=UTF-8`,
            Accept: `*/*`,
            Origin: baseUrl,
            Referer: `${baseUrl}/${route}`,
            "Accept-Encoding": `gzip, deflate`,
            "Accept-Language": `en-GB,en-US;q=0.9,en;q=0.8`,
        },
        cookies: {},
    };
}

/**
 * Extracts the JSESSIONID from a response.
 * @param {Response} response - response
 * @returns {string|null} The JSESSIONID value or null if not found
 */
export function extractJSessionId(response) {
    const cookieString = response.headers["Set-Cookie"];

    if (!cookieString) return null;
    const match = cookieString.match(/JSESSIONID=([^;]+)/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin CSRF security token from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {string|null} The CSRF token or null if not found
 */
export function getVaadinSecurityKey(html) {
    const match = html.match(/["']Vaadin-Security-Key["']\s*:\s*["']([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})["']/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin Push ID from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {string|null} The Push ID or null if not found
 */
export function getVaadinPushId(html) {
    const match = html.match(/["']Vaadin-Push-ID["']\s*:\s*["']([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})["']/);
    return match ? match[1] : null;
}

/**
 * Extracts the Vaadin UI ID from the HTML response.
 * @param {string} html - The HTML response body
 * @returns {number|null} The UI ID or null if not found
 */
export function getVaadinUiId(html) {
    const match = html.match(/["']v-uiId["']\s*:\s*(\d+)/);
    return match ? Number(match[1]) : null;
}

/**
 * Extracts the Spring Security CSRF token from the HTML response.
 *
 * Hilla endpoints (/connect/*) are secured by Spring Security and expect the
 * session's current CSRF token echoed back in the X-CSRF-TOKEN header. The
 * token is rendered into every HTML page as {@code <meta name="_csrf"
 * content="...">} — this works for both {@code HttpSessionCsrfTokenRepository}
 * (session-stored, no cookie) and {@code CookieCsrfTokenRepository}. The
 * recorded value is tied to the original session and won't match a fresh VU.
 * @param {string} html - The HTML response body
 * @returns {string} The current CSRF token, or an empty string if not found
 */
export function getHillaCsrfToken(html) {
    if (!html) return "";
    const match = html.match(/<meta\s+name=["']_csrf["']\s+content=["']([^"']+)["']/);
    return match ? match[1] : "";
}

/**
 * Initializes the Vaadin application by loading the page and extracting session info.
 * Performs two requests:
 * 1. GET / - Load the initial HTML page
 * 2. GET /?v-r=init - Initialize Vaadin with browser details (screen size, timezone, etc.)
 * @param {string} baseUrl - The base URL of the application
 * @returns {object} Object containing csrfToken and uiID for subsequent requests
 */
export function initPageLoad(baseUrl) {

    // Request 1: Load initial HTML page
    let params = {
        headers: {
            "Proxy-Connection": `keep-alive`,
            "Upgrade-Insecure-Requests": `1`,
            Accept: `text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7`,
            "Accept-Encoding": `gzip, deflate`,
            "Accept-Language": `en-GB,en-US;q=0.9,en;q=0.8`,
        },
        cookies: {},
    };

    let url = http.url`${baseUrl}/`;
    let resp = http.request("GET", url, null, params);

    if (!check(resp, { "page load status equals 200": (r) => r.status === 200 })) {
        fail(`Page load failed with status: ${resp.status}`);
    }

    // Request 2: Initialize Vaadin session with browser details
    params = {
        headers: {
            "Proxy-Connection": `keep-alive`,
            Accept: `*/*`,
            Referer: `${baseUrl}/`,
            "Accept-Encoding": `gzip, deflate`,
            "Accept-Language": `en-GB,en-US;q=0.9,en;q=0.8`,
        },
        cookies: {},
    };

    url = http.url`${baseUrl}/?v-r=init&location=&query=&v-browserDetails=%7B%22v-sh%22%3A%222160%22%2C%22v-sw%22%3A%223840%22%2C%22v-wh%22%3A%221934%22%2C%22v-ww%22%3A%221200%22%2C%22v-bh%22%3A%221934%22%2C%22v-bw%22%3A%221200%22%2C%22v-curdate%22%3A%221768550330146%22%2C%22v-tzo%22%3A%22-60%22%2C%22v-dstd%22%3A%2260%22%2C%22v-rtzo%22%3A%22-60%22%2C%22v-dston%22%3A%22false%22%2C%22v-tzid%22%3A%22Europe%2FBerlin%22%2C%22v-wn%22%3A%22v-0.8318380938909788%22%2C%22v-td%22%3A%22false%22%2C%22v-pr%22%3A%221%22%2C%22v-np%22%3A%22MacIntel%22%2C%22v-cs%22%3A%22light%22%2C%22v-tn%22%3A%22lumo%22%7D`;
    resp = http.request("GET", url, null, params);

    if (!check(resp, { "vaadin init status equals 200": (r) => r.status === 200 })) {
        fail(`Vaadin init failed with status: ${resp.status}`);
    }

    // Extract session info for subsequent requests
    const csrfToken = getVaadinSecurityKey(resp.body);
    const uiID = getVaadinUiId(resp.body);

    if (!csrfToken || uiID === null) {
        fail(`Failed to extract Vaadin session values (csrfToken=${csrfToken}, uiID=${uiID})`);
    }

    return { csrfToken, uiID };
}

/**
 * Sends a Vaadin UIDL (UI Description Language) request with RPC payload.
 * @param {string} baseUrl - The base URL of the application
 * @param {object} vaadinInfo - Object containing csrfToken and uiID
 * @param {string} rpcPayload - JSON array string of RPC calls to execute
 * @param {number} idCounter - Sync/client ID counter for request ordering
 * @param {string} route - Current route path for the Referer header
 * @returns {object} HTTP response object
 */
export function vaadinRequest(baseUrl, vaadinInfo, rpcPayload, idCounter, route) {
    let url = http.url`${baseUrl}/?v-r=uidl&v-uiId=${vaadinInfo.uiID}`;

    const resp = http.request(
        "POST",
        url,
        `{
        "csrfToken":"${vaadinInfo.csrfToken}",
        "rpc":${rpcPayload},
        "syncId":${idCounter},
        "clientId":${idCounter}
        }`,
        createBaseParams(baseUrl, route),
    );

    if (!check(resp, {
        'UIDL request succeeded': (r) => r.status === 200,
        'no server error': (r) => !r.body.includes('"appError"'),
        'session is valid': (r) => !r.body.includes('Your session needs to be refreshed'),
        'security key valid': (r) => !r.body.includes('Invalid security key'),
        'valid UIDL response': (r) => r.body.includes('"syncId"'),
    })) {
        fail(`UIDL request failed (status ${resp.status}): ${resp.body.substring(0, 200)}`);
    }

    return resp;
}

/**
 * Sends a Vaadin session unload request when user leaves the page.
 * This properly terminates the server-side session.
 * @param {string} baseUrl - The base URL of the application
 * @param {object} vaadinInfo - Object containing csrfToken and uiID
 * @param {string} route - Current route path for the Referer header
 * @returns {object} HTTP response object
 */
export function vaadinUnloadRequest(baseUrl, vaadinInfo, route) {
    let url = http.url`${baseUrl}/?v-r=uidl&v-uiId=${vaadinInfo.uiID}`;

    return http.request(
        "POST",
        url,
        `{
        "csrfToken":"${vaadinInfo.csrfToken}",
        "rpc":[],
        "UNLOAD":true
        }`,
        createBaseParams(baseUrl, route),
    );
}

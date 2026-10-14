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

    // Guard r.body / resp.body with (... || '') — if the server returns an
    // error response, k6 may surface body as undefined/null, and the raw
    // .includes()/.substring() calls would throw a TypeError before the
    // status check can fail-fast on the actual problem.
    if (!check(resp, {
        'UIDL request succeeded': (r) => r.status === 200,
        'no server error': (r) => !(r.body || '').includes('"appError"'),
        'session is valid': (r) => !(r.body || '').includes('Your session needs to be refreshed'),
        'security key valid': (r) => !(r.body || '').includes('Invalid security key'),
        'valid UIDL response': (r) => (r.body || '').includes('"syncId"'),
    })) {
        fail(`UIDL request failed (status ${resp.status}): ${(resp.body || '').substring(0, 200)}`);
    }

    return resp;
}

/**
 * Refreshes the runtime node-ID map from a Vaadin UIDL response body.
 *
 * Vaadin allocates server-side node IDs sequentially per session, so the
 * numbers recorded during HAR capture drift as soon as the UI is reordered,
 * conditionally rendered, or the Flow framework bumps its allocation order.
 * The converter rewrites RPC payloads to reference `nodeMap['stable-key']`
 * instead of literal numbers; this function keeps that map in sync with the
 * live server by walking each response's `changes` array and deriving the
 * same stable key the converter picked:
 *
 *   1. The element's `id` attribute when present (e.g. `'login-button'`).
 *   2. Otherwise `tag#ordinal` — the Nth node of that tag to attach in the
 *      session (e.g. `'vaadin-button#3'`). Per-tag ordinals persist across
 *      responses via a non-enumerable-ish `__ordinals` sub-object.
 *
 * The function mutates and returns `nodeMap` so callers can chain
 * `nodeMap = updateNodeMap(nodeMap, response.body)` without re-allocating.
 * Malformed JSON, missing `changes`, and unexpected shapes are tolerated
 * silently — this is hot-path code inside every VU iteration.
 *
 * @param {object} nodeMap - The current map, mutated in place. Pass `{}` to
 *     start fresh.
 * @param {string} responseBody - The raw response body, with or without the
 *     `for(;;);` prefix Vaadin adds for script-tag-injection defence.
 * @returns {object} The same `nodeMap` reference, for chaining.
 */
export function updateNodeMap(nodeMap, responseBody) {
    if (!nodeMap || !responseBody || typeof responseBody !== "string") {
        return nodeMap || {};
    }
    let body = responseBody;
    if (body.startsWith("for(;;);")) {
        body = body.substring(8);
    }
    let parsed;
    try {
        parsed = JSON.parse(body);
    } catch (e) {
        return nodeMap;
    }
    const ordinals = nodeMap.__ordinals || (nodeMap.__ordinals = {});
    const entries = Array.isArray(parsed) ? parsed : [parsed];
    for (const entry of entries) {
        if (!entry || typeof entry !== "object") continue;
        // Init responses occasionally wrap the payload in a top-level
        // `uidl` string — descend into it if present.
        if (typeof entry.uidl === "string") {
            updateNodeMap(nodeMap, entry.uidl);
        }
        const changes = entry.changes;
        if (!Array.isArray(changes)) continue;
        for (const change of changes) {
            if (!change || typeof change !== "object") continue;
            const nodeId = change.node;
            if (typeof nodeId !== "number" || nodeId <= 0) continue;
            if (change.key === "id" && typeof change.value === "string") {
                // id attributes take priority over any tag#N fallback.
                nodeMap[change.value] = nodeId;
                continue;
            }
            let tag = null;
            if (change.key === "tag" && typeof change.value === "string") {
                tag = change.value;
            } else if (typeof change.tag === "string") {
                tag = change.tag;
            }
            if (tag) {
                ordinals[tag] = (ordinals[tag] || 0) + 1;
                const fallbackKey = tag + "#" + ordinals[tag];
                // Don't clobber an id-based mapping for the same node.
                if (nodeMap[fallbackKey] === undefined) {
                    nodeMap[fallbackKey] = nodeId;
                }
            }
        }
    }
    return nodeMap;
}

/**
 * Returns the live node ID for a stable key or throws.
 *
 * Intentionally loud: a silent `undefined` would be substituted into the RPC
 * payload as the literal string `"undefined"`, causing the server to reject
 * the request with a confusing error downstream. Failing fast at the
 * call site makes the broken recording obvious.
 *
 * @param {object} nodeMap - The runtime map populated by `updateNodeMap`.
 * @param {string} key - The stable key the converter recorded (id attribute
 *     or `tag#ordinal` fallback).
 * @returns {number} The current server-assigned node ID.
 * @throws {Error} If the key is not present in the map.
 */
export function resolveNode(nodeMap, key) {
    const id = nodeMap && nodeMap[key];
    if (id === undefined) {
        throw new Error("unresolved node: " + key);
    }
    return id;
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

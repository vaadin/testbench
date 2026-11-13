(function () {
var __seleniumAtom = (() => {
  var __defProp = Object.defineProperty;
  var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __hasOwnProp = Object.prototype.hasOwnProperty;
  var __export = (target, all) => {
    for (var name in all)
      __defProp(target, name, { get: all[name], enumerable: true });
  };
  var __copyProps = (to, from, except, desc) => {
    if (from && typeof from === "object" || typeof from === "function") {
      for (let key of __getOwnPropNames(from))
        if (!__hasOwnProp.call(to, key) && key !== except)
          __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
    }
    return to;
  };
  var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);

  // javascript/atoms/typescript/get-attribute.ts
  var get_attribute_exports = {};
  __export(get_attribute_exports, {
    default: () => get_attribute_default
  });
  function get(element, attribute) {
    const PROPERTY_ALIASES = {
      "class": "className",
      "readonly": "readOnly"
    };
    const BOOLEAN_PROPERTIES = /* @__PURE__ */ new Set([
      "allowfullscreen",
      "allowpaymentrequest",
      "allowusermedia",
      "async",
      "autofocus",
      "autoplay",
      "checked",
      "compact",
      "complete",
      "controls",
      "declare",
      "default",
      "defaultchecked",
      "defaultselected",
      "defer",
      "disabled",
      "ended",
      "formnovalidate",
      "hidden",
      "indeterminate",
      "iscontenteditable",
      "ismap",
      "itemscope",
      "loop",
      "multiple",
      "muted",
      "nohref",
      "nomodule",
      "noresize",
      "noshade",
      "novalidate",
      "nowrap",
      "open",
      "paused",
      "playsinline",
      "pubdate",
      "readonly",
      "required",
      "reversed",
      "scoped",
      "seamless",
      "seeking",
      "selected",
      "truespeed",
      "typemustmatch",
      "willvalidate"
    ]);
    function getAttribute(element2, attributeName) {
      return element2.getAttribute(attributeName.toLowerCase());
    }
    function getProperty(element2, propertyName) {
      return element2[propertyName];
    }
    function isElement(node, tagName) {
      const elem = node;
      if (!elem || elem.nodeType !== 1) {
        return false;
      }
      if (!tagName) {
        return true;
      }
      const normalizedTag = tagName.toUpperCase();
      if (node instanceof HTMLFormElement) {
        return normalizedTag === "FORM";
      }
      return typeof elem.tagName === "string" && elem.tagName.toUpperCase() === normalizedTag;
    }
    function isSelectable(element2) {
      if (isElement(element2, "OPTION")) {
        return true;
      }
      if (isElement(element2, "INPUT")) {
        const type = element2.type.toLowerCase();
        return type === "checkbox" || type === "radio";
      }
      return false;
    }
    function isSelected(element2) {
      var _a;
      if (isElement(element2, "OPTION")) {
        return element2.selected;
      }
      const type = (_a = element2.type) == null ? void 0 : _a.toLowerCase();
      if (type === "checkbox" || type === "radio") {
        return element2.checked;
      }
      return false;
    }
    function isObject(value) {
      return value !== null && (typeof value === "object" || typeof value === "function");
    }
    const name = attribute.toLowerCase();
    if (name === "style") {
      const style = element.style;
      if (!style) {
        return null;
      }
      return typeof style === "string" ? style : style.cssText;
    }
    if ((name === "selected" || name === "checked") && isSelectable(element)) {
      return isSelected(element) ? "true" : null;
    }
    const isLink = isElement(element, "A");
    const isImg = isElement(element, "IMG");
    if (isImg && name === "src" || isLink && name === "href") {
      const attrValue = getAttribute(element, name);
      if (attrValue) {
        return String(getProperty(element, name));
      }
      return attrValue;
    }
    if (name === "spellcheck") {
      const attrValue = getAttribute(element, name);
      if (attrValue !== null) {
        const lower = attrValue.toLowerCase();
        if (lower === "false") {
          return "false";
        }
        if (lower === "true") {
          return "true";
        }
      }
      return String(getProperty(element, name));
    }
    const propName = PROPERTY_ALIASES[name] || attribute;
    if (BOOLEAN_PROPERTIES.has(name)) {
      const hasAttr = element.getAttribute(name) !== null;
      const propValue = getProperty(element, propName);
      return hasAttr || !!propValue ? "true" : null;
    }
    if (name === "value" && isElement(element, "LI")) {
      const attrValue = element.getAttribute(name);
      return attrValue != null ? attrValue : null;
    }
    let property;
    try {
      property = getProperty(element, propName);
    } catch (_e) {
    }
    if (property == null || isObject(property)) {
      const attrValue = element.getAttribute(name);
      return attrValue != null ? attrValue : null;
    }
    return property != null ? String(property) : null;
  }
  var get_attribute_default = get;
  return __toCommonJS(get_attribute_exports);
})();
return __seleniumAtom.default;
})()

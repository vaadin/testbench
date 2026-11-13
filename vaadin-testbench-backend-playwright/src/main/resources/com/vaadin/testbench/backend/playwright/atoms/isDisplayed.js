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

  // javascript/atoms/typescript/is-displayed.ts
  var is_displayed_exports = {};
  __export(is_displayed_exports, {
    default: () => shown_default
  });

  // javascript/atoms/typescript/core/attribute.ts
  var SPLIT_STYLE_ATTRIBUTE_ON_SEMICOLONS = new RegExp(
    `[;]+(?=(?:(?:[^"]*"){2})*[^"]*$)(?=(?:(?:[^']*'){2})*[^']*$)(?=(?:[^()]*\\([^()]*\\))*[^()]*$)`
  );

  // javascript/atoms/typescript/core/element.ts
  var tagNameDescriptor = null;
  function getTagNameDescriptor() {
    if (tagNameDescriptor === null) {
      tagNameDescriptor = Object.getOwnPropertyDescriptor(Element.prototype, "tagName");
    }
    return tagNameDescriptor;
  }
  function toUpperCaseTag(tagName) {
    return tagName ? tagName.toUpperCase() : void 0;
  }
  function isElement(node, tagName) {
    if (!node || node.nodeType !== 1) {
      return false;
    }
    var asElement = node;
    var descriptor = getTagNameDescriptor();
    var rawTagName = descriptor && typeof descriptor.get === "function" ? descriptor.get.call(asElement) : asElement.tagName;
    var upperTagName = typeof rawTagName === "string" ? rawTagName.toUpperCase() : "";
    var normalizedTagName = toUpperCaseTag(tagName);
    if (upperTagName === "FORM") {
      return !normalizedTagName || normalizedTagName === "FORM";
    }
    return !!upperTagName && (!normalizedTagName || upperTagName === normalizedTagName);
  }
  function getParentElement(node) {
    var current = node.parentNode;
    while (current && current.nodeType !== Node.ELEMENT_NODE && current.nodeType !== Node.DOCUMENT_NODE && current.nodeType !== Node.DOCUMENT_FRAGMENT_NODE) {
      current = current.parentNode;
    }
    return current && current.nodeType === 1 ? current : null;
  }
  function getParentNodeInComposedDom(node) {
    var parent = node.parentNode;
    var slottable = node;
    var parentWithShadow = parent;
    if (parentWithShadow && parentWithShadow.shadowRoot && slottable.assignedSlot !== void 0) {
      return slottable.assignedSlot ? slottable.assignedSlot.parentNode : null;
    }
    return parent;
  }

  // javascript/atoms/typescript/core/geometry.ts
  function createRect(left, top, width, height) {
    return {
      left,
      top,
      right: left + width,
      bottom: top + height,
      width,
      height
    };
  }
  function clamp(value, min, max) {
    return Math.min(Math.max(value, min), max);
  }
  function getAreaRelativeRect(area) {
    var shape = area.shape.toLowerCase();
    var coords = area.coords.split(",").map(function(value) {
      return Number(value.trim());
    });
    if (shape === "rect" && coords.length === 4) {
      return createRect(coords[0], coords[1], coords[2] - coords[0], coords[3] - coords[1]);
    }
    if (shape === "circle" && coords.length === 3) {
      return createRect(coords[0] - coords[2], coords[1] - coords[2], coords[2] * 2, coords[2] * 2);
    }
    if (shape === "poly" && coords.length > 2) {
      var minX = coords[0];
      var minY = coords[1];
      var maxX = minX;
      var maxY = minY;
      for (var index = 2; index + 1 < coords.length; index += 2) {
        minX = Math.min(minX, coords[index]);
        maxX = Math.max(maxX, coords[index]);
        minY = Math.min(minY, coords[index + 1]);
        maxY = Math.max(maxY, coords[index + 1]);
      }
      return createRect(minX, minY, maxX - minX, maxY - minY);
    }
    return createRect(0, 0, 0, 0);
  }
  function findImageUsingMap(mapName, doc) {
    return doc.querySelector('[usemap="#' + mapName.replace(/\\/g, "\\\\").replace(/"/g, '\\"') + '"]');
  }
  function maybeFindImageMap(elem, getRect = getClientRect) {
    var isMap = isElement(elem, "MAP");
    if (!isMap && !isElement(elem, "AREA")) {
      return null;
    }
    var map = isMap ? elem : isElement(elem.parentNode, "MAP") ? elem.parentNode : null;
    var image = null;
    var rect = createRect(0, 0, 0, 0);
    if (isElement(map, "MAP") && map.name) {
      image = findImageUsingMap(map.name, map.ownerDocument);
      if (image) {
        rect = getRect(image);
        if (!isMap && isElement(elem, "AREA") && elem.shape.toLowerCase() !== "default") {
          var relativeRect = getAreaRelativeRect(elem);
          var relativeX = Math.min(Math.max(relativeRect.left, 0), rect.width);
          var relativeY = Math.min(Math.max(relativeRect.top, 0), rect.height);
          var width = Math.min(relativeRect.width, rect.width - relativeX);
          var height = Math.min(relativeRect.height, rect.height - relativeY);
          rect = createRect(relativeX + rect.left, relativeY + rect.top, width, height);
        }
      }
    }
    return { image, rect };
  }
  function getClientRect(elem, getRect) {
    var imageMap = maybeFindImageMap(elem, getRect || getClientRect);
    if (imageMap) {
      return imageMap.rect;
    }
    var elemTagName = typeof elem.tagName === "string" ? elem.tagName : "";
    if (elemTagName.toUpperCase() === "HTML") {
      var doc = elem.ownerDocument;
      var sizeElem = doc.compatMode === "CSS1Compat" ? doc.documentElement : doc.body || doc.documentElement;
      return createRect(0, 0, sizeElem.clientWidth, sizeElem.clientHeight);
    }
    try {
      var nativeRect = elem.getBoundingClientRect();
      return {
        left: nativeRect.left,
        top: nativeRect.top,
        right: nativeRect.right,
        bottom: nativeRect.bottom,
        width: nativeRect.right - nativeRect.left,
        height: nativeRect.bottom - nativeRect.top
      };
    } catch (_error) {
      return createRect(0, 0, 0, 0);
    }
  }
  function getClientRegion(elem, optRegion, getRect = getClientRect) {
    var rect = getRect(elem);
    if (!optRegion) {
      return rect;
    }
    var region = "left" in optRegion ? optRegion : { left: optRegion.x, top: optRegion.y, width: 1, height: 1 };
    var left = clamp(rect.left + region.left, rect.left, rect.right);
    var top = clamp(rect.top + region.top, rect.top, rect.bottom);
    var right = clamp(left + region.width, left, rect.right);
    var bottom = clamp(top + region.height, top, rect.bottom);
    return createRect(left, top, right - left, bottom - top);
  }

  // javascript/atoms/typescript/core/style.ts
  function getEffectiveStyle(elem, propertyName) {
    const win = elem.ownerDocument.defaultView;
    const computed = win ? win.getComputedStyle(elem) : null;
    if (!computed) {
      return null;
    }
    return computed.getPropertyValue(propertyName) || null;
  }

  // javascript/atoms/typescript/core/overflow.ts
  function getOverflowState(elem, optRegion, getStyle = getEffectiveStyle, getRect = getClientRect) {
    var region = getClientRegion(elem, optRegion, getRect);
    var ownerDoc = elem.ownerDocument;
    var htmlElem = ownerDoc.documentElement;
    var bodyElem = ownerDoc.body;
    var htmlOverflowStyle = getStyle(htmlElem, "overflow") || "visible";
    var treatAsFixedPosition = false;
    function canBeOverflowed(container2, position) {
      if (container2 === htmlElem) {
        return true;
      }
      var display = getStyle(container2, "display") || "";
      if (display.indexOf("inline") === 0 || display === "contents") {
        return false;
      }
      if (position === "absolute" && getStyle(container2, "position") === "static") {
        return false;
      }
      return true;
    }
    function getOverflowParent(current) {
      var position = getStyle(current, "position");
      if (position === "fixed") {
        treatAsFixedPosition = true;
        return current === htmlElem ? null : htmlElem;
      }
      var parent = getParentElement(current);
      while (parent && !canBeOverflowed(parent, position)) {
        parent = getParentElement(parent);
      }
      return parent;
    }
    function getOverflowStyles(current) {
      var overflowElem = current;
      if (htmlOverflowStyle === "visible") {
        if (current === htmlElem && bodyElem) {
          overflowElem = bodyElem;
        } else if (current === bodyElem) {
          return { x: "visible", y: "visible" };
        }
      }
      var overflow = {
        x: getStyle(overflowElem, "overflow-x") || "visible",
        y: getStyle(overflowElem, "overflow-y") || "visible"
      };
      if (current === htmlElem) {
        overflow.x = overflow.x === "visible" ? "auto" : overflow.x;
        overflow.y = overflow.y === "visible" ? "auto" : overflow.y;
      }
      return overflow;
    }
    function getScroll(current) {
      if (current === htmlElem) {
        return {
          x: ownerDoc.defaultView ? ownerDoc.defaultView.pageXOffset : 0,
          y: ownerDoc.defaultView ? ownerDoc.defaultView.pageYOffset : 0
        };
      }
      return { x: current.scrollLeft, y: current.scrollTop };
    }
    function overflowStateOf(container2) {
      return getOverflowState(container2, void 0, getStyle, getRect);
    }
    for (var container = getOverflowParent(elem); container; container = getOverflowParent(container)) {
      var containerOverflow = getOverflowStyles(container);
      if (containerOverflow.x === "visible" && containerOverflow.y === "visible") {
        continue;
      }
      var containerRect = getRect(container);
      if (containerRect.width === 0 || containerRect.height === 0) {
        return "hidden";
      }
      var underflowsX = region.right < containerRect.left;
      var underflowsY = region.bottom < containerRect.top;
      if (underflowsX && containerOverflow.x === "hidden" || underflowsY && containerOverflow.y === "hidden") {
        return "hidden";
      }
      if (underflowsX && containerOverflow.x !== "visible" || underflowsY && containerOverflow.y !== "visible") {
        var containerScroll = getScroll(container);
        var unscrollableX = region.right < containerRect.left - containerScroll.x;
        var unscrollableY = region.bottom < containerRect.top - containerScroll.y;
        if (unscrollableX && containerOverflow.x !== "visible" || unscrollableY && containerOverflow.y !== "visible") {
          return "hidden";
        }
        return overflowStateOf(container) === "hidden" ? "hidden" : "scroll";
      }
      var overflowsX = region.left >= containerRect.left + containerRect.width;
      var overflowsY = region.top >= containerRect.top + containerRect.height;
      if (overflowsX && containerOverflow.x === "hidden" || overflowsY && containerOverflow.y === "hidden") {
        return "hidden";
      }
      if (overflowsX && containerOverflow.x !== "visible" || overflowsY && containerOverflow.y !== "visible") {
        if (treatAsFixedPosition) {
          var docScroll = getScroll(container);
          if (region.left >= htmlElem.scrollWidth - docScroll.x || region.right >= htmlElem.scrollHeight - docScroll.y) {
            return "hidden";
          }
        }
        return overflowStateOf(container) === "hidden" ? "hidden" : "scroll";
      }
    }
    return "none";
  }

  // javascript/atoms/typescript/core/shown.ts
  function isShownElement(elem, optIgnoreOpacity) {
    var computedStyleCache = /* @__PURE__ */ new Map();
    var clientRectCache = /* @__PURE__ */ new Map();
    var displayedCache = /* @__PURE__ */ new Map();
    function getEffectiveStyle2(elem2, propertyName) {
      var computed = computedStyleCache.get(elem2);
      if (computed === void 0) {
        var win = elem2.ownerDocument.defaultView;
        computed = win ? win.getComputedStyle(elem2) || null : null;
        computedStyleCache.set(elem2, computed);
      }
      if (!computed) {
        return null;
      }
      var value = computed.getPropertyValue(propertyName);
      return value || null;
    }
    function getOpacity(elem2) {
      var opacityStyle = getEffectiveStyle2(elem2, "opacity");
      var opacity = opacityStyle ? Number(opacityStyle) : 1;
      var parent = getParentElement(elem2);
      return parent ? opacity * getOpacity(parent) : opacity;
    }
    function getClientRect2(elem2) {
      var cachedRect = clientRectCache.get(elem2);
      if (cachedRect) {
        return cachedRect;
      }
      var rect = getClientRect(elem2, getClientRect2);
      clientRectCache.set(elem2, rect);
      return rect;
    }
    function maybeFindImageMap2(elem2) {
      return maybeFindImageMap(elem2, getClientRect2);
    }
    function getClientRegion2(elem2) {
      return getClientRegion(elem2, void 0, getClientRect2);
    }
    function getOverflowState2(elem2) {
      return getOverflowState(elem2, void 0, getEffectiveStyle2, getClientRect2);
    }
    function isShownInternal(elem2, ignoreOpacity, displayedFn) {
      if (!isElement(elem2)) {
        throw new Error("Argument to isShown must be of type Element");
      }
      if (isElement(elem2, "BODY")) {
        return true;
      }
      if (isElement(elem2, "OPTION") || isElement(elem2, "OPTGROUP")) {
        var select = elem2.closest("select");
        return !!select && isShownInternal(select, true, displayedFn);
      }
      var imageMap = maybeFindImageMap2(elem2);
      if (imageMap) {
        return !!imageMap.image && imageMap.rect.width > 0 && imageMap.rect.height > 0 && isShownInternal(imageMap.image, ignoreOpacity, displayedFn);
      }
      if (isElement(elem2, "INPUT") && elem2.type.toLowerCase() === "hidden") {
        return false;
      }
      if (isElement(elem2, "NOSCRIPT")) {
        return false;
      }
      var visibility = getEffectiveStyle2(elem2, "visibility");
      if (visibility === "collapse" || visibility === "hidden") {
        return false;
      }
      if (!displayedFn(elem2)) {
        return false;
      }
      if (!ignoreOpacity && getOpacity(elem2) === 0) {
        return false;
      }
      function positiveSize(element) {
        var rect = getClientRect2(element);
        if (rect.height > 0 && rect.width > 0) {
          return true;
        }
        if (isElement(element, "PATH") && (rect.height > 0 || rect.width > 0)) {
          var strokeWidth = getEffectiveStyle2(element, "stroke-width");
          return !!strokeWidth && parseInt(strokeWidth, 10) > 0;
        }
        var elementVisibility = getEffectiveStyle2(element, "visibility");
        if (elementVisibility === "collapse" || elementVisibility === "hidden") {
          return false;
        }
        if (!displayedFn(element)) {
          return false;
        }
        if (getEffectiveStyle2(element, "overflow") === "hidden") {
          return false;
        }
        for (var index = 0; index < element.childNodes.length; index += 1) {
          var child = element.childNodes[index];
          if (child.nodeType === Node.TEXT_NODE) {
            var text = child.nodeValue || "";
            if (/^[\s]*$/.test(text) && /[\n\r\t]/.test(text)) {
              continue;
            }
            return true;
          }
          if (isElement(child) && positiveSize(child)) {
            return true;
          }
        }
        return false;
      }
      if (!positiveSize(elem2)) {
        return false;
      }
      function hiddenByOverflow(element) {
        if (getOverflowState2(element) !== "hidden") {
          return false;
        }
        for (var index = 0; index < element.childNodes.length; index += 1) {
          var child = element.childNodes[index];
          if (isElement(child) && !hiddenByOverflow(child) && positiveSize(child)) {
            return false;
          }
        }
        return true;
      }
      return !hiddenByOverflow(elem2);
    }
    function displayed(node) {
      var cached = displayedCache.get(node);
      if (cached !== void 0) {
        return cached;
      }
      if (isElement(node)) {
        var display = getEffectiveStyle2(node, "display");
        var contentVisibility = getEffectiveStyle2(node, "content-visibility");
        if (display === "none" || contentVisibility === "hidden") {
          displayedCache.set(node, false);
          return false;
        }
      }
      var parent = getParentNodeInComposedDom(node);
      if (typeof ShadowRoot === "function" && parent instanceof ShadowRoot) {
        if (parent.host.shadowRoot && parent.host.shadowRoot !== parent) {
          displayedCache.set(node, false);
          return false;
        }
        parent = parent.host;
      }
      if (parent && (parent.nodeType === Node.DOCUMENT_NODE || parent.nodeType === Node.DOCUMENT_FRAGMENT_NODE)) {
        displayedCache.set(node, true);
        return true;
      }
      if (isElement(parent, "DETAILS") && !parent.open && !isElement(node, "SUMMARY")) {
        displayedCache.set(node, false);
        return false;
      }
      var result = !!parent && displayed(parent);
      displayedCache.set(node, result);
      return result;
    }
    return isShownInternal(elem, !!optIgnoreOpacity, displayed);
  }
  var shown_default = isShownElement;
  return __toCommonJS(is_displayed_exports);
})();
return __seleniumAtom.default;
})()

import { autoUpdate, computePosition, flip, shift, offset, arrow } from "@floating-ui/dom";

export function initTooltip(selector) {
  const containerEls = document.querySelectorAll(selector);
  containerEls.forEach((containerEl) => {
    let cleanup = null;

    function update(refEl, labelEl, arrowEl, placement) {
      cleanup = autoUpdate(refEl, labelEl, () => {
        computePosition(refEl, labelEl, {
          placement,
          middleware: [
            offset(6),
            flip(),
            shift({ padding: 5 }),
            arrow({element: arrowEl}),
          ],
        }).then(({x, y, placement: side, middlewareData}) => {
          const { x: arrowX, y: arrowY } = middlewareData.arrow;
          const staticSide = {
            top: "bottom",
            right: "left",
            bottom: "top",
            left: "right",
          }[side.split("-")[0]];
          Object.assign(labelEl.style, {
            left: `${x}px`,
            top: `${y}px`,
          });
          Object.assign(arrowEl.style, {
            left: arrowX != null ? `${arrowX}px` : "",
            top: arrowY != null ? `${arrowY}px` : "",
            right: "",
            bottom: "",
            [staticSide]: "-4px",
          });
        });
      });
    }

    function showTooltip() {
      if (typeof cleanup === 'function') {
        cleanup();
      }
      const refEl = containerEl.querySelector('.fui-tooltip-ref');
      const labelEl = containerEl.querySelector('.fui-tooltip-text');
      const arrowEl = containerEl.querySelector('.fui-tooltip-arrow');
      const placement = containerEl.dataset.placement || 'top';
      if (!refEl || !labelEl) {
        return;
      }
      labelEl.style.display = 'block';
      update(refEl, labelEl, arrowEl, placement);
    }

    function hideTooltip() {
      if (typeof cleanup === 'function') {
        cleanup();
      }
      const labelEl = containerEl.querySelector('.fui-tooltip-text');
      if (!labelEl) {
        return;
      }
      labelEl.style.display = '';
      cleanup = null;
    }

    [
      ['mouseenter', showTooltip],
      ['mouseleave', hideTooltip],
      ['focus', showTooltip],
      ['blur', hideTooltip],
    ].forEach(([event, listener]) => {
      containerEl.addEventListener(event, listener);
    });
  });
}

export default initTooltip;

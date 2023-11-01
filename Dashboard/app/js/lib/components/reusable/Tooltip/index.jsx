import React, { cloneElement, useState, useRef } from 'react';
import PropTypes from 'prop-types';
import {
  offset,
  flip,
  shift,
  arrow,
  autoUpdate,
  useFloating,
  useInteractions,
  useHover,
  useFocus,
  useRole,
  useDismiss,
  useMergeRefs,
  FloatingArrow,
} from "@floating-ui/react";

export default function Tooltip({children, label = null, placement = "top"}) {
  const arrowRef = useRef(null);
  const [open, setOpen] = useState(false);
  const { refs, floatingStyles, context } = useFloating({
    placement,
    open,
    onOpenChange: setOpen,
    whileElementsMounted: autoUpdate,
    middleware: [
      offset(10),
      flip(),
      shift({ padding: 8 }),
      arrow({ element: arrowRef }),
    ],
  });
  const { getReferenceProps, getFloatingProps } = useInteractions([
    useHover(context),
    useFocus(context),
    useRole(context, { role: "tooltip" }),
    useDismiss(context),
  ]);
  const ref = useMergeRefs([refs.setReference, children.ref]);

  return (
    <>
      {cloneElement(children, getReferenceProps({ ref, ...children.props }))}
      {label && open && (
        <div
          ref={refs.setFloating}
          style={{
            backgroundColor: "white",
            border: "1px solid #ccc",
            borderRadius: "5px",
            maxWidth: "40%",
            padding: "0.4em 0.8em",
            textAlign: "center",
            width: "max-content",
            ...floatingStyles,
          }}
          {...getFloatingProps()}
        >
          {label}
          <FloatingArrow
            ref={arrowRef}
            context={context}
            fill="white"
            stroke="#ccc"
            strokeWidth={1}
          />
        </div>
      )}
    </>
  );
};

Tooltip.defaultProps = {
  label: null,
  placement: "top",
}

Tooltip.propTypes = {
  children: PropTypes.any.isRequired,
  label: PropTypes.any,
  placement: PropTypes.string,
}

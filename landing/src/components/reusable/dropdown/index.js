import React, { useState, useRef, useEffect } from "react";
import "./index.scss";

const Dropdown = ({ selectData, className, Icon, textToDisplay = "En" }) => {
  const toggleRef = useRef(null);
  const [toggleDropdown, setToggleDropdown] = useState(false);
  const select = selectData.filter(
    (option) => option.value.toUpperCase() !== textToDisplay?.toUpperCase()
  );

  const customSelectTriggerClass = toggleDropdown
    ? "custom-select-trigger open-trigger "
    : "custom-select-trigger closed-trigger";

  const selectedOptionClass = toggleDropdown
    ? "custom-select open-select"
    : "custom-select ";

  const onCloseDropdown = (e) => {
    if (
      toggleRef.current &&
      toggleDropdown &&
      !toggleRef.current.contains(e.target)
    ) {
      setToggleDropdown(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", onCloseDropdown);
  }, [toggleDropdown]);
console.log('toggleDropdown::::::', toggleDropdown);
  return (
    <div className={`custom-select-container ${className}`}>
      <div className={selectedOptionClass} ref={toggleRef}>
        <div
          className={customSelectTriggerClass}
          onClick={() => setToggleDropdown(!toggleDropdown)}
        >
          {!toggleDropdown ? (
            <div className="trigger-closed-text">
              {textToDisplay ? textToDisplay : ""}
            </div>
          ) : (
            <div className="trigger-open-text" value="">
              {textToDisplay}
            </div>
          )}
          <Icon />
        </div>
        <ul>
          {select.map((option) => (
            <li key={option.id} className="custom-select-option">
              <div
                key={option?.id}
                className="option-button"
                value={option.value}
                onClick={() => setToggleDropdown(false)}
              >
                {option.label || option.name}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Dropdown;

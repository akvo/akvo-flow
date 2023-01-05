import React from "react";
import "./index.scss";

const Checkbox = ({ name = "test", label = "label" }) => {
  return (
    <div className="checkbox">
      <input type="checkbox" name={name} id={name} />
      <label htmlFor={name}>{label}</label>
    </div>
  );
};

export default Checkbox;

import React from "react";
import "./index.scss";

const Input = ({ label, placeholder, name }) => {
  return (
    <div className="input-wrapper">
      <input
        id={name}
        name={name}
        type="text"
        placeholder={placeholder}
        required
      />
      <label htmlFor={name}>{label}</label>
    </div>
  );
};

export default Input;

import React from "react";
import "./index.scss";
import { Link } from "react-router-dom";

const Button = ({ text, type, linkTo = "#", action }) => {
  return (
    <Link to={linkTo}>
      <button
        onClick={action}
        className={type === "outlined" ? "outlined" : "filled"}
      >
        {text}
      </button>
    </Link>
  );
};
export default Button;

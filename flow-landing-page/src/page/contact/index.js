import React from "react";
import "./style.scss";
import Button from "../../components/reusable/button";

const Contact = () => {
  return (
    <div className="contact">
      <h3 className="title">Get in touch</h3>
      <ul className="contact-list">
        <li className="list-item address">
          <b>Address</b>
          <span>'s-Gravenhekje 1-A, 1011 TG Amsterdam, The Netherlands</span>
        </li>
        <li className="list-item phone">
          <b>Phone</b>
          <span>+31 20 820 0175</span>
        </li>
      </ul>
      <div className="card">
        <h4 className="card-title">
          Start collecting data <span>today</span>
        </h4>
        <Button type="outlined" text="Get started" linkTo="/signup" />
      </div>
    </div>
  );
};

export default Contact;

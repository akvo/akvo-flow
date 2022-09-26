import React from "react";
import "./index.scss";
import { ReactComponent as RightArrow } from "../../images/right-arrow.svg";
import Input from "../reusable/input";
import Checkbox from "../reusable/checkbox";

const Subscribe = () => {
  return (
    <div className="subscribe">
      <div>
        <div className="subscribe-header">
          <h3 className="heading">Subscribe to our newsletter</h3>
          <p className="paragraph">Sign up to receive product updates</p>
        </div>
        <div className="form">
          <div className="container">
            <Input placeholder="Your name" name="name" label="Your name" />
            <Input
              placeholder="Your surname"
              name="surname"
              label="Your surname"
            />
            <Input
              placeholder="Your organisation"
              name="organisation"
              label="Your organisation"
            />
            <div className="input-wrapper">
              <input
                id="email"
                name="email"
                type="text"
                placeholder="Your email"
                required
              />
              <label htmlFor="email">Your email</label>
              <button className="submit-button">
                Subscribe <RightArrow />
              </button>
            </div>
          </div>
          <Checkbox
            label=" By checking this box you agree to our Terms of Service"
            name="agree"
          />
        </div>
      </div>
    </div>
  );
};
export default Subscribe;

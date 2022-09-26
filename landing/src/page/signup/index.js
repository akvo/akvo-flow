import React, { useState } from "react";
import "./index.scss";

import { ReactComponent as DownArrow } from "../../images/large-arrow.svg";
import { ReactComponent as Check } from "../../images/circled-check.svg";

import Input from "../../components/reusable/input";
import Dropdown from "../../components/reusable/dropdown/index";
import Button from "../../components/reusable/button/index";
import Checkbox from "../../components/reusable/checkbox/index";

const Signup = () => {
  const [step, setStep] = useState(1);
  const { innerWidth } = window;
  const countries = [
    { id: 1, name: "Africa", value: "africa" },
    { id: 2, name: "Government", value: "government" },
    { id: 3, name: "Private sector", value: "private-sector" },
    { id: 4, name: "Knowledge Institute", value: "knowledge-institute" },
    {
      id: 5,
      name: "Multilateral Partnership",
      value: "multilateral-partnership",
    },
    { id: 6, name: "Other", value: "other" },
  ];

  const countryToDisplay = countries.find(
    (country) => country.value === "africa"
  );

  const organisationType = [
    { id: 1, name: "NGO", value: "ngo" },
    { id: 2, name: "Government", value: "government" },
    { id: 3, name: "Private sector", value: "private-sector" },
    { id: 4, name: "Knowledge Institute", value: "knowledge-institute" },
    {
      id: 5,
      name: "Multilateral Partnership",
      value: "multilateral-partnership",
    },
    { id: 6, name: "Other", value: "other" },
  ];

  return (
    <div className="signup">
      <div className="container">
        {step === 1 && (
          <div className="tab step-1">
            <h3 className="heading">
              Your organisation is a few steps away from becoming data-driven.
            </h3>
            <div className="signup-form">
              <div className="wrapper">
                <p className="title">Email</p>
                <Input
                  label="Your email"
                  placeholder="Your email"
                  name="email"
                />
              </div>
              <div className="wrapper">
                <p className="title">Full name</p>
                <Input
                  label="Your full name"
                  placeholder="Your full name"
                  name="name"
                />
              </div>
              <div className="wrapper">
                <p className="title">Country</p>
                <div className="select-wrapper">
                  <Dropdown
                    selectData={countries}
                    Icon={DownArrow}
                    textToDisplay={countryToDisplay.name}
                    className="select"
                  />
                </div>
              </div>

              <div className="wrapper">
                <p className="title">Type of organisation</p>
                <div className="select-wrapper">
                  <Dropdown
                    selectData={organisationType}
                    Icon={DownArrow}
                    textToDisplay={"NGO"}
                    className="select"
                  />
                </div>
              </div>

              <div className="wrapper">
                <p className="title">Role</p>
                <div className="select-wrapper">
                  <Dropdown
                    selectData={organisationType}
                    Icon={DownArrow}
                    textToDisplay={"NGO"}
                    className="select"
                  />
                </div>
              </div>
              <div className="wrapper">
                <p className="title">Sector</p>
                <div className="select-wrapper">
                  <Dropdown
                    selectData={organisationType}
                    Icon={DownArrow}
                    textToDisplay={"NGO"}
                    className="select"
                  />
                </div>
              </div>
              <Checkbox
                label="By signing in you agree to our Terms of Service"
                name="agree"
              />
              <Button
                action={() => setStep(2)}
                linkTo="#"
                text="Sign up"
                type="outlined"
              />
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="tab step-2">
            <h3 className="heading">
              One last thing before you’re set, we promise. This information
              helps us provide you with a better service.
            </h3>
            <div className="use-of-data-wrapper">
              <p className="question">What is your main use for data?</p>
              <ul className="data-use-list">
                <li className="list-item">
                  <Checkbox label="Reporting " name="report" />
                </li>
                <li className="list-item">
                  <Checkbox
                    label="Monitoring and evaluation"
                    name="evaluation"
                  />
                </li>
                <li className="list-item">
                  <Checkbox label="Decision making" name="decision" />
                </li>
                <li className="list-item">
                  <Checkbox label="All of the above" name="all" />
                </li>
                <li className="list-item">
                  <Checkbox label="Other" name="other" />
                </li>
              </ul>
            </div>
            <div className="other-input">
              <p className="label"> Please specify</p>
              <input label="" disabled placeholder="" name="other" />
            </div>
            <div className="button-wrapper">
              <Button
                action={() => setStep(3)}
                type="outlined"
                text={innerWidth < 1024 ? "Signup" : "Finish sing-up"}
                linkTo="#"
              />
              <Button
                action={() => setStep(3)}
                type="outlined"
                text={innerWidth < 1024 ? "Skip" : "Skip the questions"}
                linkTo="#"
              />
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="tab step-3">
            <Check />
            <h3 className="heading">¡Congratulations!</h3>
            <p className="paragraph">
              In a few minutes, you’ll receive an email confirmation with the
              instructions to start using Akvo Flow.
            </p>
            <Button
              linkTo="/"
              type="outlined"
              text="Go back to the main page"
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default Signup;

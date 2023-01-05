import React, { useEffect, useState } from "react";
import "./index.scss";
import Button from "../../reusable/button";
import { planData } from "../data";
import { ReactComponent as Check } from "../../../images/check.svg";
import { ReactComponent as Cross } from "../../../images/cross.svg";

const PricingTab = () => {
  const [selected, setSelected] = useState("flow-basic");
  const selectedPlan = planData.find((plan) => plan.id === selected);

  const isSelected = (plan) => selectedPlan.id === plan;

  return (
    <div className="pricing-mobile">
      <ul className="tab-list">
        <li
          className={`list-item ${isSelected("flow-basic") && "selected"}`}
          onClick={() => setSelected("flow-basic")}
        >
          Flow Basic
        </li>
        <li
          className={`list-item ${isSelected("flow-pro") && "selected"}`}
          onClick={() => setSelected("flow-pro")}
        >
          Flow Pro
        </li>
        <li
          className={`list-item ${isSelected("flow-pro-plus") && "selected"}`}
          onClick={() => setSelected("flow-pro-plus")}
        >
          Flow Custom
        </li>
      </ul>

      <div className="plan">
        {selected === "flow-pro" && (
          <div className="most-popular">Most popular</div>
        )}
        <h3 className="plan-name">{selectedPlan.name}</h3>
        <div className="plan-price">{selectedPlan.price}</div>
        <small className="plan-note">{selectedPlan.note}</small>
        <Button
          type="filled"
          text={
            selectedPlan.name.toLowerCase() === "flow pro +"
              ? "Contact sales"
              : "Get started"
          }
          linkTo="/signup"
        />
      </div>

      <table className="features-table">
        <tbody>
          <tr className="features-row">
            <td className="feature">
              <b>Features</b>
            </td>
            <td className="empty-row"></td>
          </tr>
          {selectedPlan.features.map((feature, index) => {
            return (
              <tr key={index} className="features-row">
                <td className="feature-name">{feature.name}</td>
                <td className="feature-value">
                  {feature.value === "yes" ? (
                    <div className="check">
                      <Check />
                    </div>
                  ) : feature.value === "no" ? (
                    <div className="cross">
                      <Cross />
                    </div>
                  ) : (
                    feature.value
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      <small className="note">
        *PowerBI integration will be gauged based on familiarity with the tool
        by partner
      </small>
    </div>
  );
};

export default PricingTab;

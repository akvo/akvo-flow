import React from "react";
import "./index.scss";
import { ReactComponent as Check } from "../../../images/check.svg";
import { ReactComponent as Cross } from "../../../images/cross.svg";
import Button from "../../reusable/button/index";

const PricingTable = () => {
  return (
    <div className="pricing">
      <h3>Choose your plan</h3>
      <table>
        <tbody>
          <tr className="most-popular">
            <td></td>
            <td></td>
            <td>
              <div>Most popular</div>
            </td>
            <td></td>
          </tr>

          <tr className="plan-type">
            <th></th>
            <th>Flow Basic</th>
            <th>Flow Pro</th>
            <th>Flow Pro +</th>
          </tr>
          <tr className="plan-price">
            <td></td>
            <td>
              <div className="wrapper">
                <div>£0/m</div>
                <span>*Only for new clients</span>
              </div>
            </td>
            <td>
              <div className="wrapper">
                <div>£350/m</div>
                <span>*Only for new clients</span>
              </div>
            </td>
            <td>
              <div className="wrapper">
                <div>Custom</div>
                <span>Get more from your plan</span>
              </div>
            </td>
          </tr>
          <tr className="features">
            <td>Features</td>
            <td>
              <Button type="filled" text="Get started" linkTo="/signup" />
            </td>
            <td>
              <Button type="filled" text="Get started" linkTo="/signup" />
            </td>
            <td>
              <Button type="filled" text="Contact sales" />
            </td>
          </tr>
          <tr className="languages">
            <td>Languages</td>
            <td>EN/FR/ES</td>
            <td>EN/FR/ES</td>
            <td>EN/FR/ES</td>
          </tr>
          <tr className="configuration">
            <td>Configuration</td>
            <td>Self service</td>
            <td>Self service</td>
            <td>Configuration service</td>
          </tr>
          <tr className="editor">
            <td>Survey editor</td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
          </tr>
          <tr className="data-submission">
            <td>Data point submissions</td>
            <td>300 Max.</td>
            <td>1000 Max.</td>
            <td>Based on org. needs</td>
          </tr>
          <tr className="export-format">
            <td>Data export formats</td>
            <td>1</td>
            <td>2</td>
            <td>4</td>
          </tr>
          <tr className="monitoring">
            <td>Data monitoring (multiple forms within a survey)</td>
            <td>
              <Cross />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
          </tr>
          <tr className="user-management">
            <td>User management</td>
            <td>1 user</td>
            <td>4 users</td>
            <td>Unlimited users</td>
          </tr>
          <tr className="device-link">
            <td>Link to mobile data collection app (*Android)</td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
          </tr>
          <tr className="form">
            <td>Webforms</td>
            <td>
              <Cross />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
          </tr>
          <tr className="integration">
            <td>PowerBI integration*</td>
            <td>
              <Cross />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
            <td className="check" aria-label="yes">
              <Check />
            </td>
          </tr>
          <tr className="support">
            <td>Help desk and support (in english)</td>
            <td>
              <Cross />
            </td>
            <td>Service package inc.</td>
            <td>Service package inc.</td>
          </tr>
        </tbody>
      </table>
      <small className="note">
        *PowerBI integration will be gauged based on familiarity with the tool
        by the partner
      </small>
    </div>
  );
};

export default PricingTable;

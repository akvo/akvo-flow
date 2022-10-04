import React from "react";
import "./index.scss";
import Button from "../reusable/button/index";

import { SIGNUP } from "../../paths";

// Organisations icons
import planetInfographic from "../../images/key-features/planet-infographic.png";
import gSierraLeonne from "../../images/organisations/g-sierra-leonne-logo.png";
import simavi from "../../images/organisations/simavi-logo.png";
import fairTrade from "../../images/organisations/fairtrade-logo.png";
import snv from "../../images/organisations/snv-logo.png";
import lifewater from "../../images/organisations/lifewater-logo.png";
import unicef from "../../images/organisations/unicef-logo.png";
import oneDrop from "../../images/organisations/one-drop-logo.png";
import idh from "../../images/organisations/idh-logo.png";
import nuffic from "../../images/organisations/nuffic-logo.png";
// Map
import map from "../../images/map.svg";

const Overview = () => {
  const organisationLogos = [
    { name: "Government of Sierra Leonne", logo: gSierraLeonne },
    { name: "Simavi", logo: simavi },
    { name: "Fairtrade", logo: fairTrade },
    { name: "snv", logo: snv },
    { name: "Lifewater", logo: lifewater },
    { name: "Unicef", logo: unicef },
    { name: "One Drop", logo: oneDrop },
    { name: "idh", logo: idh },
    { name: "Nuffic", logo: nuffic },
  ];

  const statistics = [
    { name: "Data points", count: "1.5M" },
    { name: "Organisations", count: "200+" },
    { name: "Countries ", count: "70+" },
  ];

  return (
    <div className="overview">
      <section className="introduction">
        <article className="wrapper">
          <h2 className="heading">
            Design your survey and <span>start collecting data</span> today.
          </h2>
          <p className="paragraph">
            Akvo Flow can easily adapt to your data needs. Our partners collect
            and use data to drive change all over the world.
          </p>
          <Button type="filled" text="Start for free now" linkTo={SIGNUP} />
          <img src={planetInfographic} alt="3d model planet infographic" />
        </article>
      </section>

      <section className="partners">
        <h3 className="heading">
          Trusted by hundreds of businesses, NGOs & governments
        </h3>
        <div>
          {organisationLogos?.map((organisation, index) => (
            <img
              key={index}
              src={organisation?.logo}
              alt={organisation?.name}
              className="logo"
            />
          ))}
        </div>
      </section>
      <section className="impact">
        <article className="wrapper">
          <h3 className="heading">
            Development organisations <span>all over the world</span> use Akvo
            Flow to drive impact
          </h3>
          <p className="paragraph">
            The development sector has been progressively moving towards
            data-driven decision making and impact. With Akvo Flow, NGOs,
            governments, knowledge institutions and the private sector support
            millions of beneficiaries all over the world.{" "}
          </p>
          <Button type="outlined" text="Get started" linkTo={SIGNUP} />
        </article>
        <div>
          <img src={map} alt="map" />
        </div>
        <div className="statistic">
          {statistics.map((stat, index) => (
            <div key={index}>
              <b>{stat.count}</b>
              <span>{stat.name}</span>
            </div>
          ))}
        </div>
        <div className="banner">
          <h3 className="heading">
            Take your development programme to the next level
          </h3>
          <div className="paragraph">Capture reliable and timely data</div>
        </div>
      </section>
    </div>
  );
};

export default Overview;

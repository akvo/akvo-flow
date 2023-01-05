import React, { useState } from "react";
import "./index.scss";

import surveys from "../../images/survey-editor.png";
import monitoringForm from "../../images/monitoring-forms.png";
import assignments from "../../images/assignments.png";
import dataExport from "../../images/data-exports-image.png";
import dataPointSubmission from "../../images/data-point-submissions.png";

const KeyFeatures = () => {
  const [activeIndex, setActiveIndex] = useState(0);
  const featuresData = [
    {
      id: 1,
      title: "Survey editor",
      description:
        "Create, edit, copy, and store one or multiple surveys within your own personal online space.",
      image: surveys,
    },
    {
      id: 2,
      title: "Monitoring forms",
      description:
        "Does your organisation carry out baseline to endline surveys? Use this feature to track change over time for specific data points.",
      image: monitoringForm,
    },
    {
      id: 3,
      title: "Assignments",
      description:
        "Connect your enumerators’ devices within your online workspace and create groups and assignments for specific surveys and within a specific timeframe.",
      image: assignments,
    },
    {
      id: 4,
      title: "Data exports",
      description:
        "Export your data in various formats to conduct data cleaning, data analysis, geoshape, survey viewing, and more.",
      image: dataExport,
    },
    {
      id: 5,
      title: "Data point submissions",
      description:
        "Once you have created and assigned your surveys, get enumerators out in the field to collect data and submit the data points. Data points will be stored in your instance and can be accessed, edited, and exported directly from your instance.",
      image: dataPointSubmission,
    },
  ];

  return (
    <div className="key-features">
      <div className="wrapper">
        <h3 className="heading">Akvo Flow’s key features</h3>
        <div className="accordion">
          {featuresData.map((data, index) => {
            const isActive = index === activeIndex;
            return (
              <div
                id={data.id}
                className={`container ${isActive ? "active" : ""}`}
                key={data.id}
                onClick={() => setActiveIndex(index)}
              >
                <h4 className="title">{data.title}</h4>
                <div className="paragraph-wrapper">
                  <p className="paragraph">{data.description}</p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
      <img
        src={featuresData[activeIndex].image}
        alt={featuresData[activeIndex].title}
        className="key-feature-image"
      />
    </div>
  );
};
export default KeyFeatures;

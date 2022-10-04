import React, { useState } from "react";
import "./index.scss";
import { Link } from "react-router-dom";
import { ReactComponent as RoundedArrow } from "../../images/rounded-arrow.svg";
import Dropdown from "../reusable/dropdown";

const Footer = ({ setSelected }) => {
  const [selectedLanguage, setSelectedLanguage] = useState("en");
  const languages = [
    { id: 1, value: "en", label: "English" },
    { id: 2, value: "fr", label: "French" },
    { id: 3, value: "es", label: "Espagnol" },
  ];

  const language = languages.find((lang) => lang.value === selectedLanguage);

  const languageOptions = languages.filter(
    (lang) => lang.value !== selectedLanguage
  );

  return (
    <footer className="footer">
      <div>
        <div className="wrapper">
          <h1 className="flow-logo">
            <Link onClick={() => setSelected("/")} to="/">
              Akvoflow
            </Link>
          </h1>

          <div className="navigation-container">
            <div className="navigation-section">
              <div className="title">Pages</div>
              <ul className="navigation-list">
                <li className="list-item">
                  <Link
                    onClick={() => setSelected("/key-features")}
                    to="/key-features"
                  >
                    Key features
                  </Link>
                </li>
                <li className="list-item">
                  <Link onClick={() => setSelected("/pricing")} to="/pricing">
                    Pricing
                  </Link>
                </li>
                <li className="list-item">
                  <Link onClick={() => setSelected("/contact")} to="/contact">
                    Contact
                  </Link>
                </li>
              </ul>
            </div>

            <div className="navigation-section">
              <div className="title">General</div>
              <ul className="navigation-list">
                <li className="list-item">
                  <a href="https://flowsupport.akvo.org/" name="support">
                    Support
                  </a>
                </li>
                <li className="list-item">
                  <a
                    href="/akvo-saas-terms-of-service-final-september 2018.pdf"
                    target="_self"
                    name="term-of-service"
                  >
                    Terms of Service
                  </a>
                </li>
              </ul>
            </div>

            <div className="navigation-section">
              <div className="title">Useful info</div>
              <ul className="navigation-list">
                <li className="list-item">
                  Chamber of Commerce (KvK) number: 27327087
                </li>
                <li className="list-item">VAT number: NL 819794727 B01</li>
              </ul>
            </div>
          </div>
        </div>
        <Dropdown
          selectData={languageOptions}
          className="language-select"
          Icon={RoundedArrow}
          textToDisplay={language.label}
        />
        <div className="copyright-wrapper">
          <small>© 2022 AkvoFlow</small>
          <small>
            Powered by{" "}
            <a href="https://akvo.org/" name="akvo">
              Akvo.org
            </a>
          </small>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

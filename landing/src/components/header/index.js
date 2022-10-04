import React, { useState, useEffect } from "react";
import "./index.scss";
import { Link } from "react-router-dom";
import { ReactComponent as DownArrow } from "../../images/down-arrow.svg";

import Button from "../reusable/button";
import Dropdown from "../reusable/dropdown";

const DISABLE_SCROLLING_CLASS = "disabled-scroll";

const Header = ({ selected, setSelected }) => {
  const [isShownMenu, setIsShownMenu] = useState(false);
  const { innerWidth } = window;
  const body = document.querySelector("body");
  const languages = [
    { id: 1, value: "En", label: "En" },
    { id: 2, value: "Fr", label: "Fr" },
    { id: 3, value: "Es", label: "Es" },
  ];

  useEffect(() => {
    if (isShownMenu && innerWidth < 601) {
      body.classList.add(DISABLE_SCROLLING_CLASS);
    } else {
      body.classList.remove(DISABLE_SCROLLING_CLASS);
    }
  }, [isShownMenu]);

  return (
    <div className="header-wrapper">
      <header className="header">
        <h1 className="flow-logo">
          <Link onClick={() => setSelected("/")} to="/">
            Akvoflow
          </Link>
        </h1>
        <nav className="navigation">
          <ul className="navigation-list">
            <li className="list-item">
              <Link
                to="/key-features"
                onClick={() => setSelected("/key-features")}
                className={"/key-features" === selected ? `selected` : ""}
              >
                Key features
              </Link>
            </li>
            <li className="list-item">
              <Link
                to="/pricing"
                onClick={() => setSelected("/pricing")}
                className={"/pricing" === selected ? `selected` : ""}
              >
                Pricing
              </Link>
            </li>
            <li className="list-item">
              <Link
                to="/contact"
                onClick={() => setSelected("/contact")}
                className={"/contact" === selected ? `selected` : ""}
              >
                Contact
              </Link>
            </li>
          </ul>
          <div className="extra-navigation">
            <Dropdown
              selectData={languages}
              Icon={DownArrow}
              className="nav-item language-select"
            />
            <a
              href="http://akvoflowsandbox.appspot.com/"
              className="nav-item login"
            >
              Log in
            </a>
            <Button type="outlined" text="Free trial" linkTo="/signup" />
          </div>
        </nav>
        <button
          className={
            isShownMenu
              ? `toggle-menu-button closed-menu`
              : `toggle-menu-button opened-menu`
          }
          onClick={() => setIsShownMenu(!isShownMenu)}
        >
          {isShownMenu ? "close" : "open"}
        </button>
      </header>
      <div
        className={isShownMenu ? `menu-wrapper visible` : `menu-wrapper hide`}
      >
        <nav className="menu-navigation">
          <ul className="menu-navigation-list">
            <li className="menu-list-item">
              <Link onClick={() => setIsShownMenu(false)} to="/key-features">
                Key features
              </Link>
            </li>
            <li className="menu-list-item">
              <Link onClick={() => setIsShownMenu(false)} to="/pricing">
                Pricing
              </Link>
            </li>
            <li className="menu-list-item">
              <Link onClick={() => setIsShownMenu(false)} to="/contact">
                Contact
              </Link>
            </li>
          </ul>
          <div className="menu-extra-navigation">
            <Button
              action={() => {
                setIsShownMenu(false);
              }}
              type="outlined"
              text="Free trial"
              linkTo="/signup"
            />
            <a
              onClick={() => setIsShownMenu(false)}
              href="http://akvoflowsandbox.appspot.com/"
              className="menu-nav-item login"
            >
              Log in
            </a>
          </div>
        </nav>
      </div>
    </div>
  );
};

export default Header;

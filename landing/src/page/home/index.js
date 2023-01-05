import React from "react";
import "./style.scss";
import Overview from "../../components/overview/index";
import KeyFeatures from "../../components/key-features/index";
import PricingTable from "../../components/pricing/pricing-table";
import Subscribe from '../../components/subscribe/index';
import PricingTab from '../../components/pricing/pricing-tab/index';
import Testimonial from "../../components/testimonial";

const Home = () => {
  return (
    <div className="home">
      <Overview />
      <KeyFeatures />
      <PricingTab />
      <PricingTable />
      <Testimonial/>
      <Subscribe />
    </div>
  );
};

export default Home;

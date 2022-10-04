import { useState, useEffect } from "react";
import { Switch, Route, useHistory } from "react-router-dom";

import { HOME, CONTACT, KEY_FEATURES, PRICING, SIGNUP } from "./paths";

import Footer from "./components/footer";
import Header from "./components/header";
import PricingTab from "./components/pricing/pricing-tab/index";
import PricingTable from "./components/pricing/pricing-table";

import KeyFeaturesPage from "./page/key-features/index";
import Contact from "./page/contact/index";
import Home from "./page/home";
import Signup from "./page/signup";

function App() {
  const history = useHistory();
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    setSelected(history.location.pathname);
  }, [selected]);

  return (
    <div className="App">
      <Header selected={selected} setSelected={setSelected} />
      <Switch>
        <Route exact path={HOME}>
          <Home />
        </Route>
        <Route exact path={KEY_FEATURES}>
          <KeyFeaturesPage />
        </Route>
        <Route exact path={CONTACT}>
          <Contact />
        </Route>
        <Route exact path={PRICING}>
          <PricingTable />
          <PricingTab />
        </Route>
        <Route exact path={SIGNUP}>
          <Signup setSelected={setSelected} />
        </Route>
      </Switch>
      <Footer setSelected={setSelected} />
    </div>
  );
}

export default App;

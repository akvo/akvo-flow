import { Switch, Route, useHistory } from "react-router-dom";
import Footer from "./components/footer";
import Header from "./components/header";
import KeyFeaturesPage from "./page/key-features/index";
import Contact from "./page/contact/index";
import Home from "./page/home";
import Signup from "./page/signup";
import PricingTab from "./components/pricing/pricing-tab/index";
import PricingTable from "./components/pricing/pricing-table";
import { useState, useEffect } from "react";

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
        <Route exact path="/">
          <Home />
        </Route>
        <Route exact path="/key-features">
          <KeyFeaturesPage />
        </Route>
        <Route exact path="/contact">
          <Contact />
        </Route>
        <Route exact path="/pricing">
          <PricingTable />
          <PricingTab />
        </Route>
        <Route exact path="/signup">
          <Signup setSelected={setSelected}/>
        </Route>
      </Switch>
      <Footer setSelected={setSelected} />
    </div>
  );
}

export default App;

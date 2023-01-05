import { Switch, Route } from "react-router-dom";
import Footer from "./components/footer";
import Header from "./components/header";
import KeyFeaturesPage from "./page/key-features/index";
import Contact from "./page/contact/index";
import Home from "./page/home";
import Signup from "./page/signup";
import PricingTab from "./components/pricing/pricing-tab/index";
import PricingTable from "./components/pricing/pricing-table";

function App() {
  return (
    <div className="App">
      <Header />
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
          <Signup />
        </Route>
      </Switch>
      <Footer />
    </div>
  );
}

export default App;

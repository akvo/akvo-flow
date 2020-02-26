// custom router for assignment tabs
import React from 'react';
import types from 'prop-types';

const { Provider, Consumer } = React.createContext();

// Uses Context.Provider to pass value to all children
export class TabRouter extends React.Component {
  static propTypes = {
    children: types.any.isRequired,
  };

  state = {
    currentPath: '',
    pathData: null,
  };

  changeTab = (path, data = null) => {
    this.setState({ currentPath: path, pathData: data });
  };

  render() {
    const contextValue = {
      currentPath: this.state.currentPath,
      pathData: this.state.pathData,
      changeTab: this.changeTab,
    };

    return <Provider value={contextValue}>{this.props.children}</Provider>;
  }
}

export function TabRoute({ Component, path }) {
  return (
    <Consumer>
      {value =>
        path === value.currentPath && (
          <Component routeData={value.pathData} goTo={value.changeTab} />
        )
      }
    </Consumer>
  );
}

export function withTabRouter(Component) {
  return function(props) {
    return <Consumer>{value => <Component routerContext={value} {...props} />}</Consumer>;
  };
}

TabRouter.propTypes = {
  children: types.any.isRequired,
};

TabRoute.propTypes = {
  Component: types.any.isRequired,
  path: types.string.isRequired,
};

export default {};

import React from 'react';

export function contextConnect(Context, mapProvider) {
  return Component => class WrappedComponent extends React.Component {
    static displayName = `withContext(${Component.displayName || Component.name})`

    render() {
      return (
        <Context.Consumer>
          {(context) => {
            const props = mapProvider(context);
            return <Component {...this.props} {...props} />;
          }}
        </Context.Consumer>
      );
    }
  };
}

export default {};
